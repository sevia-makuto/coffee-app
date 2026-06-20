package com.example.ui

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// High fidelity in-app active notification class
data class CoffeeNotification(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val orderId: Int? = null
)

class CoffeeViewModel(application: Application) : AndroidViewModel(application) {

    private val database = CoffeeDatabase.getDatabase(application)
    private val repository = CoffeeRepository(database.coffeeDao())

    // 1. App Styling State (Dark Mode night override)
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    // 2. Navigation State (Current View)
    // "home" | "map" | "rewards" | "favorites" | "history" | "configurer" | "checkout_confirm"
    private val _currentScreen = MutableStateFlow("home")
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    fun navigateTo(screen: String) {
        _currentScreen.value = screen
    }

    // 3. Database Flows (Room Persistence)
    val favoriteOrders: StateFlow<List<FavoriteOrder>> = repository.favorites
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val orderHistory: StateFlow<List<PastOrder>> = repository.pastOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userRewards: StateFlow<UserRewards> = repository.userRewards
        .map { it ?: UserRewards() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserRewards())

    // 4. Cafe and Menu Selections
    val participatingCafes = repository.participatingCafes
    
    private val _selectedCafe = MutableStateFlow<Cafe>(participatingCafes[0])
    val selectedCafe: StateFlow<Cafe> = _selectedCafe.asStateFlow()

    fun selectCafe(cafe: Cafe) {
        _selectedCafe.value = cafe
    }

    private val _selectedMenuItem = MutableStateFlow<MenuItem?>(null)
    val selectedMenuItem: StateFlow<MenuItem?> = _selectedMenuItem.asStateFlow()

    fun selectMenuItem(item: MenuItem) {
        _selectedMenuItem.value = item
        // Initialize customizer with defaults
        resetCustomizer()
        navigateTo("configurer")
    }

    // 5. Active Coffee Customizer States
    var selectedSize = MutableStateFlow("Standard") // "Standard" | "Grande" (+$0.60) | "Alchemist" (+$1.20)
    var selectedMilk = MutableStateFlow("Whole Milk") // "Whole Milk" | "Oat Milk" (+$0.50) | "Almond Milk" (+$0.50) | "Pistachio Milk" (+$0.80)
    var selectedSweetness = MutableStateFlow("Semi Sweet") // "Unsweetened" | "Quarter Sweet" | "Semi Sweet" | "Fully Indulgent"
    var selectedExtraShots = MutableStateFlow(0) // Extra espresso shots (+$0.75 per shot)

    private fun resetCustomizer() {
        selectedSize.value = "Standard"
        selectedMilk.value = "Whole Milk"
        selectedSweetness.value = "Semi Sweet"
        selectedExtraShots.value = 0
    }

    fun calculateItemPrice(): Double {
        val base = selectedMenuItem.value?.price ?: 0.0
        var addon = 0.0
        if (selectedSize.value == "Grande") addon += 0.60
        if (selectedSize.value == "Alchemist") addon += 1.20
        if (selectedMilk.value != "Whole Milk" && selectedMilk.value != "None") {
            addon += if (selectedMilk.value == "Pistachio Milk") 0.80 else 0.50
        }
        addon += selectedExtraShots.value * 0.75
        return base + addon
    }

    fun getCustomizationSummary(): String {
        val sizeStr = selectedSize.value
        val milkStr = selectedMilk.value
        val sweetStr = selectedSweetness.value
        val shotStr = if (selectedExtraShots.value > 0) "+${selectedExtraShots.value} Shot(s)" else "Standard Shots"
        return "$sizeStr, $milkStr, $sweetStr, $shotStr"
    }

    // 6. Commuter Favorites Management
    fun saveCurrentToFavorites() {
        val item = selectedMenuItem.value ?: return
        val cafe = selectedCafe.value
        val customizations = getCustomizationSummary()
        val price = calculateItemPrice()
        val points = item.pointsReward

        viewModelScope.launch {
            repository.saveToFavorites(
                FavoriteOrder(
                    cafeId = cafe.id,
                    cafeName = cafe.name,
                    itemName = item.name,
                    customizations = customizations,
                    totalPrice = price,
                    pointsToEarn = points
                )
            )
        }
    }

    fun removeFavorite(fav: FavoriteOrder) {
        viewModelScope.launch {
            repository.removeFromFavorites(fav)
        }
    }

    // Quick Reorder for commuting speed
    fun quickReorder(fav: FavoriteOrder) {
        // Build simulated item
        _selectedCafe.value = participatingCafes.firstOrNull { it.id == fav.cafeId } ?: participatingCafes[0]
        _selectedMenuItem.value = MenuItem(
            id = "fav_temp",
            name = fav.itemName,
            description = fav.customizations,
            price = fav.totalPrice,
            category = "Lattes",
            pointsReward = fav.pointsToEarn
        )
        // Set customize options based on string parsing for exact user selection
        if (fav.customizations.contains("Grande")) selectedSize.value = "Grande"
        if (fav.customizations.contains("Alchemist")) selectedSize.value = "Alchemist"
        if (fav.customizations.contains("Oat Milk")) selectedMilk.value = "Oat Milk"
        if (fav.customizations.contains("Almond Milk")) selectedMilk.value = "Almond Milk"
        if (fav.customizations.contains("Pistachio Milk")) selectedMilk.value = "Pistachio Milk"
        if (fav.customizations.contains("Fully Indulgent")) selectedSweetness.value = "Fully Indulgent"
        if (fav.customizations.contains("Quarter Sweet")) selectedSweetness.value = "Quarter Sweet"
        
        navigateTo("checkout_confirm")
    }

    // 7. Cart & Checkout Simulation
    private val _paymentMethod = MutableStateFlow("Google Pay") // "Google Pay" | "Apple Pay"
    val paymentMethod: StateFlow<String> = _paymentMethod.asStateFlow()

    fun setPaymentMethod(method: String) {
        _paymentMethod.value = method
    }

    // Checkout processing state
    private val _isCheckingOut = MutableStateFlow(false)
    val isCheckingOut: StateFlow<Boolean> = _isCheckingOut.asStateFlow()

    // 8. Simulated Push Notification Queue & History
    private val _notifications = MutableStateFlow<List<CoffeeNotification>>(emptyList())
    val notifications: StateFlow<List<CoffeeNotification>> = _notifications.asStateFlow()

    private val _activeBanner = MutableStateFlow<CoffeeNotification?>(null)
    val activeBanner: StateFlow<CoffeeNotification?> = _activeBanner.asStateFlow()

    fun clearBanner() {
        _activeBanner.value = null
    }

    fun dismissNotification(id: String) {
        _notifications.value = _notifications.value.filter { it.id != id }
    }

    private fun postSimulatedPushNotification(title: String, message: String, orderId: Int? = null) {
        val newNotification = CoffeeNotification(
            id = "notif_${System.currentTimeMillis()}",
            title = title,
            message = message,
            orderId = orderId
        )
        _notifications.value = listOf(newNotification) + _notifications.value
        _activeBanner.value = newNotification // triggers visual overlay/toast
    }

    // 9. Interactive Local Order Status update simulation
    fun triggerOrderAndCheckout() {
        val item = selectedMenuItem.value ?: return
        val cafe = selectedCafe.value
        val customizations = getCustomizationSummary()
        val price = calculateItemPrice()
        val rewardsAdd = item.pointsReward

        viewModelScope.launch {
            _isCheckingOut.value = true
            delay(1500) // simulated biometric / secure validation delay
            _isCheckingOut.value = false

            // Create Room database entry for this past order
            val newOrderId = repository.createPastOrder(
                PastOrder(
                    cafeId = cafe.id,
                    cafeName = cafe.name,
                    itemName = item.name,
                    customizations = customizations,
                    totalPrice = price,
                    pointsEarned = rewardsAdd,
                    status = "Placed & Paid"
                )
            ).toInt()

            // Update user points ledger
            repository.incrementUserRewards(rewardsAdd)

            // Order complete, proceed to order confirmation history
            navigateTo("history")

            // Begin real-time state simulator with push alerts
            simulateOrderLifeCycle(newOrderId, item.name, cafe.name)
        }
    }

    private fun simulateOrderLifeCycle(orderId: Int, coffeeName: String, cafeName: String) {
        viewModelScope.launch {
            // Step 1: Placed Confirmation
            postSimulatedPushNotification(
                title = "Order Confirmed! ☕",
                message = "The Baristas at $cafeName accepted your order for '$coffeeName'.",
                orderId = orderId
            )

            // Step 2: Brewing Transition (8 seconds later)
            delay(8000)
            updateOrderStatusInDb(orderId, "Brewing")
            postSimulatedPushNotification(
                title = "Order is Brewing ♨️",
                message = "Your customized '$coffeeName' is steaming up at $cafeName.",
                orderId = orderId
            )

            // Step 3: Ready for Pickup (12 seconds later)
            delay(12000)
            updateOrderStatusInDb(orderId, "Ready")
            postSimulatedPushNotification(
                title = "Ready for Pick Up! 🚀",
                message = "Grab your hot '$coffeeName' at the express commuter counter at $cafeName.",
                orderId = orderId
            )
        }
    }

    private suspend fun updateOrderStatusInDb(orderId: Int, newStatus: String) {
        val currentOrders = repository.pastOrders.first()
        val target = currentOrders.find { it.id == orderId }
        if (target != null) {
            repository.updatePastOrder(target.copy(status = newStatus))
        }
    }

    // 10. Feedback Review Integration
    private val _selectedFeedbackOrder = MutableStateFlow<PastOrder?>(null)
    val selectedFeedbackOrder: StateFlow<PastOrder?> = _selectedFeedbackOrder.asStateFlow()

    fun openFeedbackSheet(order: PastOrder) {
        _selectedFeedbackOrder.value = order
    }

    fun closeFeedbackSheet() {
        _selectedFeedbackOrder.value = null
    }

    fun submitOrderRating(ratingStars: Int, feedbackText: String) {
        val targetOrder = _selectedFeedbackOrder.value ?: return
        viewModelScope.launch {
            // Update PastOrder review in Room Database
            val updated = targetOrder.copy(
                rating = ratingStars,
                feedback = feedbackText
            )
            repository.updatePastOrder(updated)
            
            // Give user 10 bonus feedback rewards points for participating!
            repository.incrementUserRewards(15) // rating bonus points

            postSimulatedPushNotification(
                title = "+15 Loyalty Points! 🎉",
                message = "We appreciate your feedback! 15 bonus points credited to your account."
            )
            
            closeFeedbackSheet()
        }
    }

    init {
        // Initialize records on start
        viewModelScope.launch {
            repository.initializeRewardsIfNeeded()
        }
    }
}
