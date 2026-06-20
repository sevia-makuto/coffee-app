package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

// 1. Core Models for Cafe & Menus
data class Cafe(
    val id: String,
    val name: String,
    val address: String,
    val distanceMiles: Double,
    val specialtyName: String,
    val specialtyPrice: Double,
    val rating: Double,
    val pointsMultiplier: Int = 10, // points earned per dollar
    val mapX: Float, // custom grid coords for beautiful SVG/Canvas interactive map (0f..100f)
    val mapY: Float,
    val menuItems: List<MenuItem>
)

data class MenuItem(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val category: String, // "Espresso" | "Cold Brew" | "Lattes" | "Pastry"
    val pointsReward: Int
)

class CoffeeRepository(private val coffeeDao: CoffeeDao) {

    // Seeded cafe directory
    val participatingCafes = listOf(
        Cafe(
            id = "cafe_horizon",
            name = "Cafe Horizon",
            address = "404 Latte Lane, Espresso District",
            distanceMiles = 0.2,
            specialtyName = "Nitro Horizon Cold Brew",
            specialtyPrice = 5.25,
            rating = 4.8,
            mapX = 35f,
            mapY = 40f,
            menuItems = listOf(
                MenuItem("m1", "Nitro Horizon Cold Brew", "Infused with nitrogen for a creamy finish, notes of velvety caramel & pecan", 5.25, "Cold Brew", 50),
                MenuItem("m2", "Single-Origin Pour Over", "Ethiopian Yirgacheffe, light roast with vibrant citrus notes", 4.75, "Espresso", 45),
                MenuItem("m3", "Vanilla Macadamia Latte", "Subtly sweet, handcrafted with homemade high-grade macadamia nut blend", 5.50, "Lattes", 55),
                MenuItem("m4", "Glazed Morning Croissant", "Flaky, buttery pastry baked freshly before dawn", 3.75, "Pastry", 35)
            )
        ),
        Cafe(
            id = "barista_haven",
            name = "Barista's Haven",
            address = "720 Bourbon Blvd, Roaster Flats",
            distanceMiles = 0.5,
            specialtyName = "Bourbon Amber Miel",
            specialtyPrice = 6.00,
            rating = 4.9,
            mapX = 65f,
            mapY = 25f,
            menuItems = listOf(
                MenuItem("h1", "Bourbon Amber Miel", "Organic honey, real cinnamon, espresso extract aged in bourbon wood barrels", 6.00, "Lattes", 60),
                MenuItem("h2", "Cortado", "Balanced 1:2 ratio of strong espresso and velvety steamed whole milk", 4.25, "Espresso", 40),
                MenuItem("h3", "Cold Brew Tonic", "Bold cold brew concentrate shaken with premium fever-tree tonic and mint", 5.00, "Cold Brew", 50),
                MenuItem("h4", "Dark Chocolate Pecan Brownie", "Fudge-like decadent brownie loaded with roasted pecans", 4.00, "Pastry", 40)
            )
        ),
        Cafe(
            id = "crema_co",
            name = "Crema & Co",
            address = "95 Velvet Plaza, Pastel Hill",
            distanceMiles = 0.9,
            specialtyName = "Rose Cardamom Latte",
            specialtyPrice = 5.75,
            rating = 4.7,
            mapX = 20f,
            mapY = 75f,
            menuItems = listOf(
                MenuItem("c1", "Rose Cardamom Latte", "Floral sweet elements blended with crushed premium green cardamom", 5.75, "Lattes", 55),
                MenuItem("c2", "Matcha Cloud Latte", "Ceremonial stoneground Uji matcha served cold, topped with thick cream foam", 5.50, "Cold Brew", 55),
                MenuItem("c3", "Classic Double Macchiato", "Two dark espresso shots stained with a delicate spoonful of microfoam", 3.75, "Espresso", 35),
                MenuItem("c4", "Pistachio Almond Bun", "Rolled brioche filled with freshly crushed pistachio butter", 4.50, "Pastry", 45)
            )
        ),
        Cafe(
            id = "daily_commute_express",
            name = "Daily Commute Express",
            address = "12 Subway Junction, Underground Plaza",
            distanceMiles = 1.2,
            specialtyName = "Turbo Espresso Shot",
            specialtyPrice = 3.25,
            rating = 4.5,
            mapX = 80f,
            mapY = 82f,
            menuItems = listOf(
                MenuItem("d1", "Turbo Espresso Shot", "High-caffeine espresso blend crafted for maximum speed and morning focus", 3.25, "Espresso", 30),
                MenuItem("d2", "Quick Brew Light", "Fresh drip coffee, served steaming under 10 seconds", 2.75, "Espresso", 25),
                MenuItem("d3", "Iced Espresso Shakerato", "Double espresso, raw turbinado sugar shaken on ice until frothy", 4.50, "Cold Brew", 45),
                MenuItem("d4", "Oatmeal Apple Muffin", "Warm whole-grain muffin stuffed with baked cinnamon apple slices", 3.50, "Pastry", 30)
            )
        )
    )

    // Favorites Delegations
    val favorites: Flow<List<FavoriteOrder>> = coffeeDao.getFavorites()

    suspend fun saveToFavorites(favorite: FavoriteOrder) {
        coffeeDao.insertFavorite(favorite)
    }

    suspend fun removeFromFavorites(favorite: FavoriteOrder) {
        coffeeDao.deleteFavorite(favorite)
    }

    // Past Orders and Feedback Delegations
    val pastOrders: Flow<List<PastOrder>> = coffeeDao.getPastOrders()

    suspend fun createPastOrder(order: PastOrder): Long {
        return coffeeDao.insertPastOrder(order)
    }

    suspend fun updatePastOrder(order: PastOrder) {
        coffeeDao.updatePastOrder(order)
    }

    // User Rewards Delegations
    val userRewards: Flow<UserRewards?> = coffeeDao.getUserRewardsFlow()

    suspend fun incrementUserRewards(pointsAwarded: Int) {
        val currentRewards = coffeeDao.getUserRewardsDirect() ?: UserRewards()
        val updatedRewards = currentRewards.copy(
            totalPoints = currentRewards.totalPoints + pointsAwarded
        )
        coffeeDao.insertUserRewards(updatedRewards)
    }

    suspend fun initializeRewardsIfNeeded() {
        val direct = coffeeDao.getUserRewardsDirect()
        if (direct == null) {
            coffeeDao.insertUserRewards(UserRewards())
        }
    }
}
