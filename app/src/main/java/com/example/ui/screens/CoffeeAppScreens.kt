package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.*
import com.example.ui.CoffeeViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoffeeAppMainScreen(viewModel: CoffeeViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val activeBanner by viewModel.activeBanner.collectAsStateWithLifecycle()
    val userRewards by viewModel.userRewards.collectAsStateWithLifecycle()

    // Central Theme Overlay Config
    val coffeeColorScheme = if (isDarkMode) {
        darkColorScheme(
            primary = Color(0xFFFFB74D), // warm honey gold
            onPrimary = Color(0xFF3E2723),
            primaryContainer = Color(0xFF5D4037),
            onPrimaryContainer = Color(0xFFEFEBE9),
            secondary = Color(0xFFFFCC80),
            secondaryContainer = Color(0xFF4E342E),
            onSecondaryContainer = Color(0xFFD7CCC8),
            background = Color(0xFF15100E), // obsidian brown
            surface = Color(0xFF221A18),
            onBackground = Color(0xFFEFEBE9),
            onSurface = Color(0xFFEFEBE9),
            surfaceVariant = Color(0xFF3E2724)
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF5D4037), // rich espresso brown
            onPrimary = Color.White,
            primaryContainer = Color(0xFFEFEBE9), // smooth foam milk cream
            onPrimaryContainer = Color(0xFF3E2723),
            secondary = Color(0xFF8D6E63),
            secondaryContainer = Color(0xFFF5EFEB),
            onSecondaryContainer = Color(0xFF4E342E),
            background = Color(0xFFFDFCF9), // crisp eggshell warm white
            surface = Color(0xFFF5EFEB),
            onBackground = Color(0xFF2E1C1A),
            onSurface = Color(0xFF2E1C1A),
            surfaceVariant = Color(0xFFE7DDD8)
        )
    }

    MaterialTheme(colorScheme = coffeeColorScheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Coffee,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Brewnique",
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.testTag("app_brand_title")
                                    )
                                }
                            },
                            actions = {
                                // Notification Drawer Indicator
                                Box(
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .clickable { viewModel.navigateTo("history") }
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Notifications,
                                        contentDescription = "Simulated Push Notification Panel",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    if (notifications.isNotEmpty()) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .background(Color.Red, CircleShape)
                                                .align(Alignment.TopEnd)
                                        )
                                    }
                                }

                                // Interactive Dark Mode switch toggle
                                IconButton(
                                    onClick = { viewModel.toggleDarkMode() },
                                    modifier = Modifier.testTag("dark_mode_toggle")
                                ) {
                                    Icon(
                                        imageVector = if (isDarkMode) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                                        contentDescription = "Toggle Dark Theme",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.background
                            )
                        )
                    },
                    bottomBar = {
                        // Persistent M3 bottom controller bar
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.background,
                            tonalElevation = 8.dp
                        ) {
                            NavigationBarItem(
                                selected = currentScreen == "home" || currentScreen == "configurer",
                                onClick = { viewModel.navigateTo("home") },
                                label = { Text("Order") },
                                icon = { Icon(Icons.Outlined.Coffee, "Order screen") },
                                modifier = Modifier.testTag("nav_order")
                            )
                            NavigationBarItem(
                                selected = currentScreen == "map",
                                onClick = { viewModel.navigateTo("map") },
                                label = { Text("Cafes Map") },
                                icon = { Icon(Icons.Outlined.Map, "Interactive map") },
                                modifier = Modifier.testTag("nav_map")
                            )
                            NavigationBarItem(
                                selected = currentScreen == "favorites",
                                onClick = { viewModel.navigateTo("favorites") },
                                label = { Text("Favorites") },
                                icon = { Icon(Icons.Outlined.FavoriteBorder, "Favorites list") },
                                modifier = Modifier.testTag("nav_favorites")
                            )
                            NavigationBarItem(
                                selected = currentScreen == "history" || currentScreen == "rewards",
                                onClick = { viewModel.navigateTo("history") },
                                label = { Text("Activity") },
                                icon = { Icon(Icons.Outlined.History, "Activity tracker") },
                                modifier = Modifier.testTag("nav_activity")
                            )
                        }
                    }
                ) { paddingValues ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        AnimatedContent(
                            targetState = currentScreen,
                            transitionSpec = {
                                fadeIn() togetherWith fadeOut()
                            },
                            label = "ScreenTransition"
                        ) { screen ->
                            when (screen) {
                                "home" -> DashboardHomeScreen(viewModel)
                                "map" -> CafeMapScreen(viewModel)
                                "favorites" -> FavoritesScreen(viewModel)
                                "history" -> HistoryRewardsScreen(viewModel)
                                "configurer" -> DrinkConfigurerScreen(viewModel)
                                "checkout_confirm" -> CheckoutOptionScreen(viewModel)
                                else -> DashboardHomeScreen(viewModel)
                            }
                        }
                    }
                }

                // HIGH ATTENTION floating local banner for status messages
                AnimatedVisibility(
                    visible = activeBanner != null,
                    enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                ) {
                    activeBanner?.let { notif ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.clearBanner()
                                    viewModel.navigateTo("history")
                                }
                                .testTag("simulated_push_toast")
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(
                                            MaterialTheme.colorScheme.primary,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.NotificationsActive,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = notif.title,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = notif.message,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                                        fontSize = 12.sp,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                IconButton(onClick = { viewModel.clearBanner() }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close push toast",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }

                // Interactive customer review and feedback sheet
                FeedbackBottomSheet(viewModel)
            }
        }
    }
}

// ==========================================================
// 1. DASHBOARD HOME SCREEN
// ==========================================================
@Composable
fun DashboardHomeScreen(viewModel: CoffeeViewModel) {
    val selectedCafe by viewModel.selectedCafe.collectAsStateWithLifecycle()
    val rewards by viewModel.userRewards.collectAsStateWithLifecycle()
    val cafes = viewModel.participatingCafes

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Customized Header greeting
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Good day,",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "Commuter Champion ☕",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        // Hero Image Card Custom Component
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .shadow(4.dp, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_coffee_hero),
                        contentDescription = "Brewnique Espresso Bar",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Gradient overlay to make text pop
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Morning Boost Rewards",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Place orders ahead, beat the commuter rush, and earn points at local boutique roasters.",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Loyalty points progress component
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "LOYALTY MEMBERSHIP",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Commuter Level: Gold",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Stars,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${rewards.totalPoints} Rewards Points Available",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = 15.sp
                            )
                        }
                    }

                    // Simulated Cup Level Indicator
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(70.dp)
                    ) {
                        CircularProgressIndicator(
                            progress = { (rewards.totalPoints % 200).toFloat() / 200f },
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 6.dp,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${rewards.totalPoints % 200}/200",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Next Cup",
                                fontSize = 8.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        }

        // Shop Selection Header
        item {
            Text(
                text = "1. Choose Participating Café",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Horizontal participating cafes slider
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cafes) { cafe ->
                    val isSelected = selectedCafe.id == cafe.id
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .width(260.dp)
                            .clickable { viewModel.selectCafe(cafe) }
                            .border(
                                width = if (isSelected) 2.dp else 0.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .testTag("cafe_card_${cafe.id}")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = cafe.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    maxLines = 1,
                                    color = MaterialTheme.colorScheme.primary,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.Star,
                                        contentDescription = "Star score",
                                        tint = Color(0xFFFBC02D),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = cafe.rating.toString(),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = cafe.address,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.DirectionsWalk,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${cafe.distanceMiles} miles away",
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                SuggestionChip(
                                    onClick = { },
                                    label = {
                                        Text(
                                            text = "10x Points",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    },
                                    colors = SuggestionChipDefaults.suggestionChipColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Menu items selection Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "2. Build Specialty Order at ${selectedCafe.name}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // List of selected cafe items
        items(selectedCafe.menuItems) { menu ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.selectMenuItem(menu) }
                    .shadow(1.dp, RoundedCornerShape(12.dp))
                    .testTag("menu_item_${menu.id}")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = menu.name,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = menu.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "$${String.format(Locale.US, "%.2f", menu.price)}",
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.secondary,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Badge(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "+${menu.pointsReward} Points",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    IconButton(
                        onClick = { viewModel.selectMenuItem(menu) },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Select items customizations",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ==========================================================
// 2. INTERACTIVE COORDINATE MAP VIEW
// ==========================================================
@Composable
fun CafeMapScreen(viewModel: CoffeeViewModel) {
    val cafes = viewModel.participatingCafes
    val selectedCafe by viewModel.selectedCafe.collectAsStateWithLifecycle()
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Local Coffee Map",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Tap on map markers to locate nearby participating cafes for rapid commuter pickup.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        // Custom high-fidelity Vector Map Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                .background(if (isDarkMode) Color(0xFF1E1412) else Color(0xFFFFFDF9))
        ) {
            // Interactive map Canvas drawing commuter streets & pins
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(cafes) {
                        detectTapGestures { offset ->
                            // Translate pixels to % of width/height
                            val pctX = (offset.x / size.width) * 100f
                            val pctY = (offset.y / size.height) * 100f

                            // Find nearest cafe within a reasonable click radius
                            val limit = 15f
                            val match = cafes.minByOrNull {
                                val dx = it.mapX - pctX
                                val dy = it.mapY - pctY
                                dx * dx + dy * dy
                            }
                            if (match != null) {
                                val dx = match.mapX - pctX
                                val dy = match.mapY - pctY
                                if (dx * dx + dy * dy < limit * limit) {
                                    viewModel.selectCafe(match)
                                }
                            }
                        }
                    }
            ) {
                val gridW = size.width
                val gridH = size.height

                // Draw mock streets (Espresso Blvd & Latte Lane etc... for beautiful visual)
                val roadColor = if (isDarkMode) Color(0xFF33201D) else Color(0xFFF1E8E5)
                val roadPaintWidth = 24.dp.toPx()

                // Espresso Blvd
                drawLine(
                    color = roadColor,
                    start = Offset(0f, gridH * 0.35f),
                    end = Offset(gridW, gridH * 0.35f),
                    strokeWidth = roadPaintWidth,
                    cap = StrokeCap.Round
                )

                // Latte Lane
                drawLine(
                    color = roadColor,
                    start = Offset(gridW * 0.3f, 0f),
                    end = Offset(gridW * 0.3f, gridH),
                    strokeWidth = roadPaintWidth,
                    cap = StrokeCap.Round
                )

                // Macchiato Junction
                drawLine(
                    color = roadColor,
                    start = Offset(0f, gridH * 0.75f),
                    end = Offset(gridW, gridH * 0.85f),
                    strokeWidth = roadPaintWidth,
                    cap = StrokeCap.Round
                )

                // Draw user location marker (pulsating blue indicator)
                val userX = gridW * 0.45f
                val userY = gridH * 0.55f
                drawCircle(
                    color = Color(0xFF29B6F6).copy(alpha = 0.2f),
                    radius = 20.dp.toPx(),
                    center = Offset(userX, userY)
                )
                drawCircle(
                    color = Color(0xFF0288D1),
                    radius = 8.dp.toPx(),
                    center = Offset(userX, userY)
                )
                drawCircle(
                    color = Color.White,
                    radius = 4.dp.toPx(),
                    center = Offset(userX, userY)
                )

                // Draw Cafe Pins
                cafes.forEach { cafe ->
                    val cx = (cafe.mapX / 100f) * gridW
                    val cy = (cafe.mapY / 100f) * gridH
                    val isSelected = cafe.id == selectedCafe.id

                    // Halo effect for selection
                    if (isSelected) {
                        drawCircle(
                            color = Color(0xFFFFB74D).copy(alpha = 0.4f),
                            radius = 24.dp.toPx(),
                            center = Offset(cx, cy)
                        )
                    }

                    // Main pin structure
                    drawCircle(
                        color = if (isSelected) Color(0xFFFF9800) else Color(0xFF5D4037),
                        radius = 12.dp.toPx(),
                        center = Offset(cx, cy)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 5.dp.toPx(),
                        center = Offset(cx, cy)
                    )
                }
            }

            // Overlay street labels descriptions
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "Espresso Blvd",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkMode) Color.Gray else Color.DarkGray,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 100.dp, start = 12.dp)
                )
                Text(
                    text = "Latte Lane",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkMode) Color.Gray else Color.DarkGray,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 22.dp, end = 120.dp)
                )

                // Interactive Mini card popup showing Selected Cafe info
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(12.dp)
                        .fillMaxWidth()
                        .shadow(6.dp, RoundedCornerShape(12.dp))
                        .testTag("map_cafe_popover")
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = selectedCafe.name,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 15.sp
                            )
                            Text(
                                text = selectedCafe.address,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                fontSize = 10.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.DirectionsWalk,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "${selectedCafe.distanceMiles} Miles",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Specialty: ${selectedCafe.specialtyName}",
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.secondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Button(
                            onClick = { viewModel.navigateTo("home") },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 2.dp)
                        ) {
                            Text("Menu", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================================
// 3. DRINK CONFIGURATION & CUSTOMIZER SCREEN
// ==========================================================
@Composable
fun DrinkConfigurerScreen(viewModel: CoffeeViewModel) {
    val selectedItem by viewModel.selectedMenuItem.collectAsStateWithLifecycle()
    val size by viewModel.selectedSize.collectAsStateWithLifecycle()
    val milk by viewModel.selectedMilk.collectAsStateWithLifecycle()
    val sweet by viewModel.selectedSweetness.collectAsStateWithLifecycle()
    val extraShots by viewModel.selectedExtraShots.collectAsStateWithLifecycle()

    selectedItem?.let { menu ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.navigateTo("home") }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Return to menu")
                    }
                    Text(
                        text = "Customize Espresso Maker",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Main Core Item Information
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = menu.name,
                            fontWeight = FontWeight.ExtraBold,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = menu.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Base Price: $${String.format(Locale.US, "%.2f", menu.price)}",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Badge(containerColor = MaterialTheme.colorScheme.primary) {
                                Text(
                                    text = "+${menu.pointsReward} Rewards Points",
                                    color = Color.White,
                                    modifier = Modifier.padding(4.dp),
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            // 1. Selector Module: Sizes
            item {
                Text(
                    text = "1. Size Options",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Standard", "Grande", "Alchemist").forEach { sOption ->
                        val isSel = size == sOption
                        val addonPrice = when (sOption) {
                            "Grande" -> "+$0.60"
                            "Alchemist" -> "+$1.20"
                            else -> "Free"
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                                .clickable { viewModel.selectedSize.value = sOption }
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = sOption,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = addonPrice,
                                    fontSize = 10.sp,
                                    color = if (isSel) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            }

            // 2. Milk options selection
            item {
                Text(
                    text = "2. Milk Selection",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val milks = listOf("Whole Milk", "Oat Milk", "Almond Milk", "Pistachio Milk")
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        milks.chunked(2).forEach { pair ->
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                pair.forEach { mOption ->
                                    val isSel = milk == mOption
                                    val addon = when (mOption) {
                                        "Pistachio Milk" -> "+$0.80"
                                        "Whole Milk" -> "Free"
                                        else -> "+$0.50"
                                    }
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                                            .clickable { viewModel.selectedMilk.value = mOption }
                                            .padding(12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "$mOption ($addon)",
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 12.sp,
                                            color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 3. Selection of sweetness parameters
            item {
                Text(
                    text = "3. Sweetness Multiplier",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Unsweetened", "Quarter Sweet", "Semi Sweet", "Fully Indulgent").forEach { sweetOption ->
                        val isSel = sweet == sweetOption
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                                .clickable { viewModel.selectedSweetness.value = sweetOption }
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = sweetOption.replace(" ", "\n"),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // 4. Extra Espresso Shots Slider
            item {
                Text(
                    text = "4. Extra Espresso Shots (+$0.75 per shot)",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        IconButton(
                            onClick = { if (extraShots > 0) viewModel.selectedExtraShots.value-- },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface, CircleShape)
                        ) {
                            Icon(Icons.Default.Remove, "Less Shots")
                        }
                        Text(
                            text = "$extraShots Extra Shots",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        IconButton(
                            onClick = { if (extraShots < 4) viewModel.selectedExtraShots.value++ },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface, CircleShape)
                        ) {
                            Icon(Icons.Default.Add, "More Shots")
                        }
                    }

                    Text(
                        text = "+$${String.format(Locale.US, "%.2f", extraShots * 0.75)}",
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            // 5. Save as Commuter favorite quick selection
            item {
                Divider(modifier = Modifier.padding(vertical = 12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable {
                            viewModel.saveCurrentToFavorites()
                        }
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = Color.Red,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Save to Favorites for 1-Tap Reorder",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Bypass checkout steps on your daily commute.",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                    Text(
                        text = "SAVE",
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.testTag("save_favorite_button")
                    )
                }
            }

            // Checkout and Order Ahead Button
            item {
                Button(
                    onClick = { viewModel.navigateTo("checkout_confirm") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("submit_customization_button")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Proceed to Payment Gate",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$${String.format(Locale.US, "%.2f", viewModel.calculateItemPrice())}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// ==========================================================
// 4. GOOGLE PAY / APPLE PAY SECURE GATEWAY CHECKOUT SCREEN
// ==========================================================
@Composable
fun CheckoutOptionScreen(viewModel: CoffeeViewModel) {
    val size by viewModel.selectedSize.collectAsStateWithLifecycle()
    val milk by viewModel.selectedMilk.collectAsStateWithLifecycle()
    val sweet by viewModel.selectedSweetness.collectAsStateWithLifecycle()
    val extraShots by viewModel.selectedExtraShots.collectAsStateWithLifecycle()
    val isCheckingOut by viewModel.isCheckingOut.collectAsStateWithLifecycle()
    val item by viewModel.selectedMenuItem.collectAsStateWithLifecycle()
    val cafe by viewModel.selectedCafe.collectAsStateWithLifecycle()
    val paymentBy by viewModel.paymentMethod.collectAsStateWithLifecycle()

    item?.let { coffeeItem ->
        val price = viewModel.calculateItemPrice()
        val tax = price * 0.08
        val total = price + tax

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateTo("configurer") }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Return")
                }
                Text(
                    text = "Verify Checkout",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Order Checkout Item Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ORDERING STORE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = cafe.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "DRINK DETAILS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = coffeeItem.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "$${String.format(Locale.US, "%.2f", coffeeItem.price)}",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Individual Customization Chips
                    Spacer(modifier = Modifier.height(6.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = "• Size Profile: $size", fontSize = 11.sp)
                        Text(text = "• Creamer Choice: $milk", fontSize = 11.sp)
                        Text(text = "• Sugar Level: $sweet", fontSize = 11.sp)
                        if (extraShots > 0) {
                            Text(text = "• Incremental Addons: +$extraShots Shot(s)", fontSize = 11.sp)
                        }
                    }
                }
            }

            // Invoice pricing balance Sheet
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Subtotal", fontSize = 13.sp)
                        Text("$${String.format(Locale.US, "%.2f", price)}", fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Local Sales Tax (8%)", fontSize = 13.sp)
                        Text("$${String.format(Locale.US, "%.2f", tax)}", fontSize = 13.sp)
                    }
                    Divider(modifier = Modifier.padding(vertical = 10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Grand Total Due", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(
                            text = "$${String.format(Locale.US, "%.2f", total)}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Secure Pay Gates selection
            Text(
                text = "Secure Biometric Payment Gateways Available:",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Google Pay Selector
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (paymentBy == "Google Pay") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.setPaymentMethod("Google Pay") }
                        .border(
                            2.dp,
                            if (paymentBy == "Google Pay") MaterialTheme.colorScheme.primary else Color.Transparent,
                            RoundedCornerShape(12.dp)
                        ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Android,
                            contentDescription = null,
                            tint = if (paymentBy == "Google Pay") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Google Pay",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                // Apple Pay Selector
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (paymentBy == "Apple Pay") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.setPaymentMethod("Apple Pay") }
                        .border(
                            2.dp,
                            if (paymentBy == "Apple Pay") MaterialTheme.colorScheme.primary else Color.Transparent,
                            RoundedCornerShape(12.dp)
                        ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Laptop,
                            contentDescription = null,
                            tint = if (paymentBy == "Apple Pay") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Apple Pay",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            if (isCheckingOut) {
                // Secure biometric scanning simulation spinner
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Authenticating with $paymentBy Secure Token...",
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Hold device near your NFC terminal / Biometrics Verified",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                        )
                    }
                }
            } else {
                Button(
                    onClick = { viewModel.triggerOrderAndCheckout() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("pay_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null
                        )
                        Text(
                            text = "Authorize & Pay with $paymentBy",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}

// ==========================================================
// 5. COMMUTER FAVORITES SCREEN
// ==========================================================
@Composable
fun FavoritesScreen(viewModel: CoffeeViewModel) {
    val favorites by viewModel.favoriteOrders.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Weekly Commuter Shortcuts",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Tap any pre-customized coffee below for 1-click speed checkout. Extremely convenient for daily train commuters.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }

        if (favorites.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No saved shortcuts yet",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "Customize your morning brew and tap 'Save to Favorites'.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(favorites) { fav ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(1.dp, RoundedCornerShape(12.dp))
                            .testTag("favorite_card_${fav.id}")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = fav.itemName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "@ ${fav.cafeName}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = fav.customizations,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }

                                IconButton(onClick = { viewModel.removeFavorite(fav) }) {
                                    Icon(
                                        imageVector = Icons.Filled.DeleteOutline,
                                        contentDescription = "Remove favorite",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "$${String.format(Locale.US, "%.2f", fav.totalPrice)}",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Button(
                                    onClick = { viewModel.quickReorder(fav) },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("quick_reorder_button_${fav.id}")
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Bolt,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("1-Tap Buy", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================================
// 6. HISTORY & REWARDS TRACKER SCREEN (with Rating link trigger)
// ==========================================================
@Composable
fun HistoryRewardsScreen(viewModel: CoffeeViewModel) {
    val pastOrders by viewModel.orderHistory.collectAsStateWithLifecycle()
    val rewards by viewModel.userRewards.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Loyalty hub & Activity",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Active points catalog overview card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "COMMUTER LEDGER",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${rewards.totalPoints} pts available",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Every dollar spent unlocks 10 points. Earn and redeem premium drinks at participating shops.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
        }

        item {
            Text(
                text = "Simulated Active Order Statuses:",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Order cards displaying realtime brewing stages
        if (pastOrders.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No Orders placed yet",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        } else {
            items(pastOrders) { order ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("past_order_card_${order.id}")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = order.cafeName,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 15.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )

                            // Nice visually colored status badges
                            val statusColor = when (order.status) {
                                "Ready" -> Color(0xFF4CAF50)
                                "Brewing" -> Color(0xFFFF9800)
                                else -> MaterialTheme.colorScheme.primary
                            }
                            AssistChip(
                                onClick = { },
                                label = { Text(order.status, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = AssistChipDefaults.assistChipColors(
                                    labelColor = statusColor,
                                    leadingIconContentColor = statusColor
                                ),
                                leadingIcon = {
                                    Icon(
                                        imageVector = when (order.status) {
                                            "Ready" -> Icons.Default.CheckCircle
                                            "Brewing" -> Icons.Default.LocalCafe
                                            else -> Icons.Default.HistoryToggleOff
                                        },
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            )
                        }

                        Text(
                            text = "${order.itemName} (${order.customizations})",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$${String.format(Locale.US, "%.2f", order.totalPrice)} • +${order.pointsEarned} Points Added",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold
                            )

                            // Interactive star rating indicator summary
                            if (order.rating > 0) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    (1..5).forEach { starIndex ->
                                        Icon(
                                            imageVector = Icons.Filled.Star,
                                            contentDescription = null,
                                            tint = if (starIndex <= order.rating) Color(0xFFFFC107) else Color(0xFFBCAAA4),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            } else {
                                OutlinedButton(
                                    onClick = { viewModel.openFeedbackSheet(order) },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                                    modifier = Modifier.testTag("rate_order_button_${order.id}")
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.RateReview,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Rate Cafe", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // Output feedback comments if provided
                        if (order.feedback.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MaterialTheme.colorScheme.background)
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = "Your Comment: \"${order.feedback}\"",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    style = androidx.compose.ui.text.TextStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

// ==========================================================
// 7. EXPERIENTIAL BOTTOM FEEDBACK OVERLAY (Star feedback & comments)
// ==========================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackBottomSheet(viewModel: CoffeeViewModel) {
    val reviewOrder by viewModel.selectedFeedbackOrder.collectAsStateWithLifecycle()

    reviewOrder?.let { order ->
        var scoreStarred by remember { mutableIntStateOf(5) }
        var inputComments by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { viewModel.closeFeedbackSheet() },
            title = {
                Text(
                    text = "Rate Experience @ ${order.cafeName}",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.testTag("feedback_title")
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "We diligently monitor service quality to support small local boutique shops. Please rate your '${order.itemName}' order:",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )

                    // Interactive 5 Star Selector row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        (1..5).forEach { barIndex ->
                            Icon(
                                imageVector = if (barIndex <= scoreStarred) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                contentDescription = "Rate $barIndex stars",
                                tint = if (barIndex <= scoreStarred) Color(0xFFFBC02D) else MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clickable { scoreStarred = barIndex }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = inputComments,
                        onValueChange = { inputComments = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .testTag("feedback_comments_input"),
                        placeholder = {
                            Text(
                                "Add feedback about taste, preparation speed, or barista courtesy...",
                                fontSize = 11.sp
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    Text(
                        text = "Earn +15 Loyalty points for rating!",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.submitOrderRating(scoreStarred, inputComments)
                    },
                    modifier = Modifier.testTag("submit_feedback_button")
                ) {
                    Text("Submit & Earn Points")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.closeFeedbackSheet() }) {
                    Text("Skip")
                }
            }
        )
    }
}
