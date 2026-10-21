package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.DailyTipModal
import com.example.ui.components.DailyOfferModal
import com.example.ui.components.OnboardingModal
import com.example.ui.components.PremiumModal
import com.example.ui.components.ShimiHeader
import com.example.ui.components.SmartSearchDialog
import com.example.ui.components.SubscriptionExpiredDialog
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.LoginViewModel
import com.example.ui.viewmodel.ShimiTab
import com.example.ui.viewmodel.ShimiYarViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: ShimiYarViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        handleNotificationIntent(intent)
        handleOAuthRedirect(intent)

        setContent {
            val isGuestUser by loginViewModel.isGuestUser.collectAsState()
            val eyeCareMode by viewModel.eyeCareMode.collectAsState()

            MyApplicationTheme(isEyeCareMode = eyeCareMode) {
                // Mandatory RTL Layout Direction for full Persian experience
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    when (isGuestUser) {
                        null -> {
                            // در حال بارگذاری اولیه از DataStore
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(DarkBackground),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = AppleBlue)
                            }
                        }
                        false -> {
                            LoginScreen(
                                viewModel = loginViewModel,
                                onNavigateToMain = {
                                    // DataStore emits isGuestUser = true and transitions automatically
                                }
                            )
                        }
                        true -> {
                            ShimiYarMainApp(
                                viewModel = viewModel,
                                onLogoutToLogin = {
                                    loginViewModel.logout()
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleNotificationIntent(intent)
        handleOAuthRedirect(intent)
    }

    private fun handleOAuthRedirect(intent: android.content.Intent?) {
        val uri = intent?.data
        if (uri != null && uri.host == "shimiyar.ir" && uri.path?.startsWith("/oauth") == true) {
            loginViewModel.handleWebRedirect(uri)
        }
    }

    private fun handleNotificationIntent(intent: android.content.Intent?) {
        val openPaywall = intent?.getBooleanExtra(com.example.reminder.SubscriptionReminderManager.EXTRA_OPEN_PAYWALL, false) ?: false
        if (openPaywall) {
            viewModel.openPremiumModal()
        }

        val targetTab = intent?.getStringExtra("EXTRA_NAVIGATE_TAB")
        if (targetTab == "FLASHCARDS") {
            viewModel.setTab(ShimiTab.FLASHCARDS)
            val category = intent.getStringExtra("EXTRA_CATEGORY")
            if (!category.isNullOrBlank()) {
                viewModel.setFcCategory(category)
            }
        }
    }
}

@Composable
fun ShimiYarMainApp(
    viewModel: ShimiYarViewModel,
    onLogoutToLogin: (() -> Unit)? = null
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.refreshDueLeitnerInfo()
        }
    }

    LaunchedEffect(Unit) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            if (!hasPermission) {
                permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        // بررسی خودکار وضعیت انقضای اشتراک ۳ روزه و اطلاع‌رسانی
        viewModel.checkSubscriptionExpiry(context)
    }

    val currentTab by viewModel.currentTab.collectAsState()
    val isPro by viewModel.isPro.collectAsState()
    val showOnboarding by viewModel.showOnboarding.collectAsState()
    val showPremiumModal by viewModel.showPremiumModal.collectAsState()
    val showDailyOfferModal by viewModel.showDailyOfferModal.collectAsState()
    val showDailyTipModal by viewModel.showDailyTipModal.collectAsState()
    val showSmartSearch by viewModel.showSmartSearch.collectAsState()
    val showSubscriptionExpiredDialog by viewModel.showSubscriptionExpiredDialog.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    // BackHandler for drawer
    BackHandler(enabled = drawerState.isOpen) {
        coroutineScope.launch { drawerState.close() }
    }

    // BackHandler to return to Table tab before exiting
    BackHandler(enabled = !drawerState.isOpen && currentTab != ShimiTab.TABLE) {
        viewModel.setTab(ShimiTab.TABLE)
    }

    if (showOnboarding) {
        OnboardingModal(
            onFinish = { viewModel.finishOnboarding() }
        )
        return
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = DarkSurface1,
                drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp),
                modifier = Modifier.width(300.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                // Drawer Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkSurface2),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.app_icon_fg),
                            contentDescription = "آیکون شیمی یار",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp))
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "شیمی یار", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(text = "دستیار تخصصی کنکور", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                }

                Divider(color = DarkSeparator, modifier = Modifier.padding(vertical = 12.dp))

                // Smart Search Quick Entry in Drawer
                NavigationDrawerItem(
                    icon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = MetallicCyan) },
                    label = { Text("جستجوی هوشمند (عنصر، فرمول، مبحث)", color = MetallicCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    selected = false,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        viewModel.openSmartSearch()
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = DarkSurface2,
                        unselectedContainerColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 2.dp)
                        .testTag("drawer_smart_search_item")
                )

                // Navigation Items
                val menuItems = listOf(
                    Triple(ShimiTab.TABLE, "جدول تناوبی تعاملی (۱۱۸ عنصر)", Icons.Default.ViewModule),
                    Triple(ShimiTab.EDUCATION, "آموزش کامل دبیرستان (۱۰ فصل)", Icons.Default.School),
                    Triple(ShimiTab.FLASHCARDS, "جعبه لایتنر و فلش‌کارت (۴۱۰+)", Icons.Default.Style),
                    Triple(ShimiTab.QUIZ, "شبیه‌ساز آزمون‌های تستی کنکور", Icons.Default.FactCheck),
                    Triple(ShimiTab.TOOLS, "جعبه‌ابزار محاسباتی و جرم مولی", Icons.Default.Calculate),
                    Triple(ShimiTab.ORGANIC, "شیمی آلی کنکور (ساختارها و گروه‌ها)", Icons.Default.Science),
                    Triple(ShimiTab.GLOSSARY, "واژه‌نامه تخصصی و مفاهیم", Icons.Default.MenuBook),
                    Triple(ShimiTab.PROFILE, "کارنامه تحصیلی، تراز و نشان‌ها", Icons.Default.Person)
                )

                menuItems.forEach { (tab, label, icon) ->
                    NavigationDrawerItem(
                        icon = { Icon(imageVector = icon, contentDescription = null, tint = AppleBlue) },
                        label = { Text(label, color = TextPrimary, fontWeight = FontWeight.Medium) },
                        selected = currentTab == tab,
                        onClick = {
                            viewModel.setTab(tab)
                            coroutineScope.launch { drawerState.close() }
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = DarkSurface2,
                            unselectedContainerColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Guest Status Card in Drawer (فقط برای کاربران رایگان)
                if (!isPro) {
                    Surface(
                        color = Color(0xFF16A34A).copy(alpha = 0.15f),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, Color(0xFF16A34A).copy(alpha = 0.4f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🟢", fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "شما وارد نسخه رایگان شدید",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            if (onLogoutToLogin != null) {
                                TextButton(
                                    onClick = {
                                        coroutineScope.launch { drawerState.close() }
                                        onLogoutToLogin()
                                    },
                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "صفحه ورود",
                                        fontSize = 11.sp,
                                        color = AppleYellow,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // Pro Banner in Drawer
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isPro) AppleYellow.copy(alpha = 0.15f) else AppleOrange.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable {
                            coroutineScope.launch { drawerState.close() }
                            if (isPro) viewModel.openPremiumModal() else viewModel.openDailyOfferModal()
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = if (isPro) "👑" else "⚡", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isPro) "عضویت طلایی شما فعال است" else "ارتقای عضویت به شیمی‌یار طلایی",
                                fontWeight = FontWeight.Bold,
                                color = if (isPro) AppleYellow else AppleOrange,
                                fontSize = 13.sp
                            )
                            Text(
                                text = if (isPro) "دسترسی ۱۰۰٪ به تمام امکانات فعال است" else "دسترسی کامل به تمام درسنامه‌ها و تست‌ها",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }
    ) {
        var showFreeNotice by remember { mutableStateOf(true) }

        Scaffold(
            containerColor = DarkBackground,
            topBar = {
                Column {
                    ShimiHeader(
                        viewModel = viewModel,
                        onMenuClick = { coroutineScope.launch { drawerState.open() } }
                    )

                    // نوار دسترسی سریع افقی به تمامی ۸ بخش اصلی اپلیکیشن
                    val quickTabs = listOf(
                        Triple(ShimiTab.TABLE, "جدول", "⚛️"),
                        Triple(ShimiTab.EDUCATION, "درسنامه", "📚"),
                        Triple(ShimiTab.FLASHCARDS, "لایتنر", "🗂️"),
                        Triple(ShimiTab.QUIZ, "آزمون", "🎯"),
                        Triple(ShimiTab.TOOLS, "جعبه‌ابزار", "🧮"),
                        Triple(ShimiTab.ORGANIC, "شیمی آلی", "🧪"),
                        Triple(ShimiTab.GLOSSARY, "واژه‌نامه", "🔬"),
                        Triple(ShimiTab.PROFILE, "کارنامه", "🏆")
                    )

                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 3.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(quickTabs) { (tab, label, icon) ->
                            val isSelected = currentTab == tab
                            Surface(
                                onClick = { viewModel.setTab(tab) },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) AppleBlue else DarkSurface2,
                                border = BorderStroke(1.dp, if (isSelected) AppleBlue else DarkSeparator),
                                modifier = Modifier.testTag("quick_tab_${tab.name.lowercase()}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = icon, fontSize = 11.5.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else TextSecondary
                                    )
                                }
                            }
                        }
                    }

                    // بنر اطلاع‌رسانی نسخه رایگان (فقط برای نسخه رایگان)
                    if (!isPro && showFreeNotice) {
                        Surface(
                            color = Color(0xFF16A34A).copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, Color(0xFF16A34A).copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "🟢", fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "شما وارد نسخه رایگان شدید (ورود مهمان فعال است)",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                IconButton(
                                    onClick = { showFreeNotice = false },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "بستن",
                                        tint = Color.White.copy(alpha = 0.8f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            bottomBar = {
                ShimiBottomNavigation(
                    currentTab = currentTab,
                    onTabSelected = { viewModel.setTab(it) }
                )
            },
            modifier = Modifier.testTag("shimi_scaffold")
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentTab) {
                    ShimiTab.TABLE -> TableScreen(viewModel = viewModel)
                    ShimiTab.ORGANIC -> OrganicChemistryScreen(viewModel = viewModel)
                    ShimiTab.EDUCATION -> EducationScreen(viewModel = viewModel)
                    ShimiTab.FLASHCARDS -> FlashcardScreen(viewModel = viewModel)
                    ShimiTab.QUIZ -> QuizScreen(viewModel = viewModel)
                    ShimiTab.TOOLS -> ToolsScreen(viewModel = viewModel)
                    ShimiTab.GLOSSARY -> GlossaryScreen(viewModel = viewModel)
                    ShimiTab.PROFILE -> ProfileScreen(viewModel = viewModel)
                }
            }
        }
    }

    // Modals
    if (showPremiumModal) {
        PremiumModal(
            isPro = isPro,
            onDismiss = { viewModel.closePremiumModal() },
            onActivateTrial = { viewModel.activateFreeTrial() },
            onPurchase = { variant -> viewModel.purchaseSubscription(variant) },
            onRestorePurchases = { viewModel.restorePurchases() }
        )
    }

    if (showDailyOfferModal && !isPro) {
        DailyOfferModal(
            onDismiss = { viewModel.closeDailyOfferModal() },
            onPurchase = { variant -> viewModel.purchaseSubscription(variant) },
            onActivateTrial = { viewModel.activateFreeTrial() }
        )
    }

    if (showDailyTipModal) {
        DailyTipModal(
            tip = viewModel.todayTip,
            onDismiss = { viewModel.closeDailyTip() }
        )
    }

    if (showSmartSearch) {
        SmartSearchDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.closeSmartSearch() }
        )
    }

    if (showSubscriptionExpiredDialog) {
        SubscriptionExpiredDialog(
            onDismiss = { viewModel.closeSubscriptionExpiredDialog() },
            onRenewPlan = { planId ->
                viewModel.purchaseSubscription(planId)
            }
        )
    }
}

@Composable
fun ShimiBottomNavigation(
    currentTab: ShimiTab,
    onTabSelected: (ShimiTab) -> Unit
) {
    NavigationBar(
        containerColor = DarkSurface1,
        tonalElevation = 4.dp,
        windowInsets = NavigationBarDefaults.windowInsets,
        modifier = Modifier.testTag("shimi_bottom_nav")
    ) {
        val tabs = listOf(
            Triple(ShimiTab.TABLE, "جدول", Icons.Default.ViewModule),
            Triple(ShimiTab.EDUCATION, "درسنامه", Icons.Default.School),
            Triple(ShimiTab.FLASHCARDS, "لایتنر", Icons.Default.Style),
            Triple(ShimiTab.QUIZ, "آزمون", Icons.Default.FactCheck),
            Triple(ShimiTab.TOOLS, "جعبه‌ابزار", Icons.Default.Calculate)
        )

        tabs.forEach { (tab, title, icon) ->
            val isSelected = currentTab == tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = title,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AppleBlue,
                    selectedTextColor = AppleBlue,
                    indicatorColor = AppleBlue.copy(alpha = 0.15f),
                    unselectedIconColor = TextTertiary,
                    unselectedTextColor = TextTertiary
                ),
                modifier = Modifier.testTag("tab_${tab.name.lowercase()}")
            )
        }
    }
}
