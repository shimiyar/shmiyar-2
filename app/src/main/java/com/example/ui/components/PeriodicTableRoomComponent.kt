package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AppDatabase
import com.example.data.local.DataLoader
import com.example.data.local.ElementDao
import com.example.data.local.ElementEntity
import com.example.ui.theme.*
import com.example.util.PersianUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * کامپوننت جامع و تعاملی نمایش جدول تناوبی با پشتیبانی از دیتابیس Room
 * - اسکرول دو‌بعدی روان (افقی و عمودی) برای نمایش کامل تمام ۱۱۸ عنصر بدون برش یا پنهان ماندن بخش‌های پایینی
 * - قابلیت تغییر حالت به «جدول ۱۸ ستونه استاندارد» و «فهرست تفکیک‌شده دوره‌ها»
 * - شماره‌گذاری استاندارد گروه‌ها (۱ تا ۱۸) و دوره‌ها (۱ تا ۷)
 * - کلیک روی هر عنصر و واکشی مستقیم اطلاعات از دیتابیس Room (عدد اتمی، جرم و دسته)
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PeriodicTableRoomComponent(
    modifier: Modifier = Modifier,
    customElementDao: ElementDao? = null,
    onElementSelected: ((ElementEntity) -> Unit)? = null
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // دسترسی مستقیم به DAO دیتابیس Room
    val elementDao = remember(customElementDao) {
        customElementDao ?: AppDatabase.getDatabase(context).elementDao()
    }

    // فهرست عناصر از جدول Room
    val elementsFromRoom by elementDao.getAllElements().collectAsState(initial = emptyList())

    // اطمینان از مقداردهی کامل پایگاه داده Room برای تمام ۱۱۸ عنصر با نکات کنکوری
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val count = elementDao.getCount()
            val firstEl = elementDao.getElementByNumber(1)
            if (count < 118 || firstEl?.konkurTipsAndUses?.contains("فرآیند هابر") != true) {
                val entities = DataLoader.loadElementEntities(context)
                elementDao.insertElements(entities)
            }
        }
    }

    // حالت‌های جستجو و فیلتر
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf<String?>(null) }
    var viewMode by remember { mutableStateOf(0) } // 0: جدول کامل 18 ستونه, 1: فهرست هوشمند دوره‌ها
    var isFitMode by remember { mutableStateOf(false) } // false: نمای بزرگ با اسکرول، true: نمای کامل جاگیری در صفحه
    var zoomScale by remember { mutableFloatStateOf(1.0f) } // مقیاس زوم جدول بزرگ

    // وضعیت عنصر انتخاب شده واکشی شده از Room
    var selectedElement by remember { mutableStateOf<ElementEntity?>(null) }
    var isLoadingElement by remember { mutableStateOf(false) }

    // نگاشت سریع بر اساس موقعیت در جدول (سطر و ستون)
    val gridMap = remember(elementsFromRoom) {
        elementsFromRoom.associateBy { Pair(it.gridY, it.gridX) }
    }

    // فیلتر عناصر
    val filteredList = remember(elementsFromRoom, searchQuery, selectedCategoryFilter) {
        elementsFromRoom.filter { el ->
            val matchSearch = if (searchQuery.isBlank()) true else {
                el.persianName.contains(searchQuery, ignoreCase = true) ||
                el.englishName.contains(searchQuery, ignoreCase = true) ||
                el.symbol.contains(searchQuery, ignoreCase = true) ||
                el.atomicNumber.toString().contains(searchQuery)
            }
            val matchCategory = if (selectedCategoryFilter == null) true else {
                el.category.contains(selectedCategoryFilter!!) ||
                selectedCategoryFilter!!.contains(el.category)
            }
            matchSearch && matchCategory
        }
    }

    val categories = remember {
        listOf(
            "همه",
            "فلز قلیایی",
            "فلز قلیایی خاکی",
            "فلز واسطه",
            "فلز پس‌واسطه",
            "شبه‌فلز",
            "نافلز واکنش‌پذیر",
            "هالوژن",
            "گاز نجیب",
            "لانتانید",
            "آکتینید"
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("periodic_table_component")
    ) {
        // نوار بالایی: وضعیت دیتابیس Room + سوئیچر حالت نمایش
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = DarkSurface2,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, DarkSeparator)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(MetallicCyan)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${PersianUtils.toPersianDigits(elementsFromRoom.size)} عنصر شیمیایی",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }

            // سوئیچر دو حالته: جدول ۱۸ ستونه / فهرست دوره‌ها
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.height(34.dp)
            ) {
                SegmentedButton(
                    selected = viewMode == 0,
                    onClick = { viewMode = 0 },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = AppleBlue,
                        activeContentColor = Color.White,
                        inactiveContainerColor = DarkSurface1,
                        inactiveContentColor = TextSecondary
                    )
                ) {
                    Text(text = "جدول ۱۸ ستونه", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                SegmentedButton(
                    selected = viewMode == 1,
                    onClick = { viewMode = 1 },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = AppleBlue,
                        activeContentColor = Color.White,
                        inactiveContainerColor = DarkSurface1,
                        inactiveContentColor = TextSecondary
                    )
                ) {
                    Text(text = "فهرست دوره‌ها", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // فیلد جستجوی سریع
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("جستجو در دیتابیس Room: نام، نماد یا عدد اتمی...", color = TextTertiary, fontSize = 12.sp) },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "جستجو", tint = AppleBlue, modifier = Modifier.size(20.dp)) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "پاک کردن", tint = TextSecondary, modifier = Modifier.size(18.dp))
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = DarkSurface1,
                unfocusedContainerColor = DarkSurface1,
                focusedBorderColor = AppleBlue,
                unfocusedBorderColor = DarkSeparator,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 2.dp)
                .height(50.dp)
                .testTag("room_element_search_input")
        )

        // ردیف چیپ‌های دسته‌بندی
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            contentPadding = PaddingValues(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(categories) { cat ->
                val isSelected = (cat == "همه" && selectedCategoryFilter == null) || (cat == selectedCategoryFilter)
                val catColor = if (cat == "همه") AppleBlue else getCategoryColor(cat)

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) catColor.copy(alpha = 0.25f) else DarkSurface1,
                    border = BorderStroke(1.dp, if (isSelected) catColor else DarkSeparator),
                    modifier = Modifier
                        .clickable {
                            selectedCategoryFilter = if (cat == "همه") null else cat
                        }
                        .testTag("category_filter_chip_$cat")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (cat != "همه") {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(catColor)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                        }
                        Text(
                            text = cat,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) catColor else TextSecondary
                        )
                    }
                }
            }
        }

        // بخش اصلی جدول یا فهرست با قابلیت اسکرول و زوم
        if (viewMode == 0) {
            // نوار تنظیمات نمایش جدول (نمای کامل بدون برش / نمای زوم بزرگ)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // سوئیچر نمای فشرده (تمام صفحه) در برابر نمای بزرگ (زوم)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(DarkSurface1)
                        .border(1.dp, DarkSeparator, RoundedCornerShape(10.dp))
                        .padding(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Surface(
                        onClick = { isFitMode = true },
                        color = if (isFitMode) AppleBlue else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "📱 نمای کامل (تمام صفحه)",
                            fontSize = 10.5.sp,
                            fontWeight = if (isFitMode) FontWeight.Bold else FontWeight.Normal,
                            color = if (isFitMode) Color.White else TextSecondary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        onClick = { isFitMode = false },
                        color = if (!isFitMode) AppleBlue else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "🔎 نمای بزرگ (زوم)",
                            fontSize = 10.5.sp,
                            fontWeight = if (!isFitMode) FontWeight.Bold else FontWeight.Normal,
                            color = if (!isFitMode) Color.White else TextSecondary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // دکمه‌های کنترل زوم در نمای بزرگ
                if (!isFitMode) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Surface(
                            onClick = { zoomScale = (zoomScale - 0.15f).coerceAtLeast(0.75f) },
                            color = DarkSurface2,
                            shape = CircleShape,
                            border = BorderStroke(1.dp, DarkSeparator),
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = "−", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                        }

                        Text(
                            text = "${(zoomScale * 100).toInt()}%",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )

                        Surface(
                            onClick = { zoomScale = (zoomScale + 0.15f).coerceAtMost(1.35f) },
                            color = DarkSurface2,
                            shape = CircleShape,
                            border = BorderStroke(1.dp, DarkSeparator),
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = "+", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                        }
                    }
                }
            }

            val hScroll = rememberScrollState(initial = 0)
            val vScroll = rememberScrollState(initial = 0)

            // در نمای بزرگ: ردیف پرش سریع به گروه‌های جدول
            if (!isFitMode) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val jumpSections = listOf(
                        Triple("گروه ۱ و ۲ (قلیایی)", 0, 0),
                        Triple("واسطه (۳ تا ۱۲)", 150, 0),
                        Triple("اصلی (۱۳ تا ۱۷)", 650, 0),
                        Triple("گازهای نجیب (۱۸)", 1000, 0),
                        Triple("عناصر f (لانتانیدها)", 150, 480)
                    )

                    items(jumpSections) { (label, hTarget, vTarget) ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = DarkSurface2,
                            border = BorderStroke(1.dp, AppleBlue.copy(alpha = 0.35f)),
                            modifier = Modifier.clickable {
                                coroutineScope.launch {
                                    hScroll.animateScrollTo(hTarget)
                                    if (vTarget > 0) vScroll.animateScrollTo(vTarget)
                                }
                            }
                        ) {
                            Text(
                                text = "⚡ $label",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppleBlue,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (isFitMode) {
                    // حالت فشرده تمام صفحه: تمام ۱۸ ستون بدون اسکرول افقی روی صفحه جا می‌گیرند
                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 6.dp)
                    ) {
                        val availableW = maxWidth - 18.dp
                        val cellW = ((availableW - 36.dp) / 18f).coerceIn(15.dp, 21.dp)
                        val cellH = (cellW * 1.35f).coerceIn(21.dp, 28.dp)

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(vScroll)
                                .padding(bottom = 96.dp)
                        ) {
                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    // سرستون شماره گروه‌ها (۱ تا ۱۸)
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(modifier = Modifier.size(width = 16.dp, height = 16.dp))
                                        for (col in 1..18) {
                                            Box(
                                                modifier = Modifier.size(width = cellW, height = 16.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = col.toString(),
                                                    fontSize = 7.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = TextTertiary
                                                )
                                            }
                                        }
                                    }

                                    // سطرهای ۱ تا ۱۰
                                    for (row in 1..10) {
                                        if (row == 8) {
                                            Row(
                                                modifier = Modifier.padding(vertical = 3.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Box(modifier = Modifier.size(width = 16.dp, height = 14.dp))
                                                Text(
                                                    text = "⬇ عناصر f (لانتانیدها و آکتینیدها)",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = AppleYellow
                                                )
                                            }
                                        } else {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                val periodLabel = when (row) {
                                                    in 1..7 -> row.toString()
                                                    9 -> "La"
                                                    10 -> "Ac"
                                                    else -> ""
                                                }
                                                Box(
                                                    modifier = Modifier.size(width = 16.dp, height = cellH),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = periodLabel,
                                                        fontSize = 8.5.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (row in 9..10) AppleYellow else AppleBlue
                                                    )
                                                }

                                                for (col in 1..18) {
                                                    val element = gridMap[Pair(row, col)]
                                                    if (element != null) {
                                                        val isMatch = searchQuery.isBlank() && selectedCategoryFilter == null || filteredList.contains(element)
                                                        RoomElementCompactCell(
                                                            element = element,
                                                            isDimmed = !isMatch,
                                                            isFitMode = true,
                                                            cellWidth = cellW,
                                                            cellHeight = cellH,
                                                            onClick = {
                                                                coroutineScope.launch {
                                                                    isLoadingElement = true
                                                                    val fetched = elementDao.getElementByNumber(element.atomicNumber)
                                                                    selectedElement = fetched ?: element
                                                                    isLoadingElement = false
                                                                    onElementSelected?.invoke(selectedElement!!)
                                                                }
                                                            }
                                                        )
                                                    } else {
                                                        Box(modifier = Modifier.size(width = cellW, height = cellH))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // حالت بزرگ با اسکرول کامل دو‌بعدی (افقی + عمودی)
                    val baseCellW = 54.dp * zoomScale
                    val baseCellH = 62.dp * zoomScale

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(vScroll)
                            .padding(bottom = 96.dp)
                    ) {
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(hScroll)
                                    .padding(horizontal = 12.dp, vertical = 2.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    // سرستون شماره گروه‌ها (۱ تا ۱۸)
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(modifier = Modifier.size(width = 24.dp, height = 20.dp))
                                        for (col in 1..18) {
                                            Box(
                                                modifier = Modifier.size(width = baseCellW, height = 20.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = PersianUtils.toPersianDigits(col),
                                                    fontSize = (10 * zoomScale).sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = TextTertiary
                                                )
                                            }
                                        }
                                    }

                                    // سطرهای ۱ تا ۱۰
                                    for (row in 1..10) {
                                        if (row == 8) {
                                            Row(
                                                modifier = Modifier.padding(vertical = 5.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Box(modifier = Modifier.size(width = 24.dp, height = 16.dp))
                                                Text(
                                                    text = "⬇ عناصر f (لانتانیدها ۵۷-۷۱ و آکتینیدها ۸۹-۱۰۳)",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = AppleYellow
                                                )
                                            }
                                        } else {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                val periodLabel = when (row) {
                                                    in 1..7 -> PersianUtils.toPersianDigits(row)
                                                    9 -> "La"
                                                    10 -> "Ac"
                                                    else -> ""
                                                }
                                                Box(
                                                    modifier = Modifier.size(width = 24.dp, height = baseCellH),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = periodLabel,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (row in 9..10) AppleYellow else AppleBlue
                                                    )
                                                }

                                                for (col in 1..18) {
                                                    val element = gridMap[Pair(row, col)]
                                                    if (element != null) {
                                                        val isMatch = searchQuery.isBlank() && selectedCategoryFilter == null || filteredList.contains(element)
                                                        RoomElementCompactCell(
                                                            element = element,
                                                            isDimmed = !isMatch,
                                                            isFitMode = false,
                                                            cellWidth = baseCellW,
                                                            cellHeight = baseCellH,
                                                            onClick = {
                                                                coroutineScope.launch {
                                                                    isLoadingElement = true
                                                                    val fetched = elementDao.getElementByNumber(element.atomicNumber)
                                                                    selectedElement = fetched ?: element
                                                                    isLoadingElement = false
                                                                    onElementSelected?.invoke(selectedElement!!)
                                                                }
                                                            }
                                                        )
                                                    } else {
                                                        Box(modifier = Modifier.size(width = baseCellW, height = baseCellH))
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
            }
        } else {
            // حالت ۲: فهرست تفکیک‌شده دوره‌ها (مخصوص اسکرول عمودی سریع روی گوشی)
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(start = 14.dp, top = 6.dp, end = 14.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // گروه‌بندی عناصر بر اساس دوره
                val periodGroups = (1..7).map { p ->
                    Pair("دوره $p", filteredList.filter { it.period == p && it.atomicNumber !in 57..71 && it.atomicNumber !in 89..103 })
                } + listOf(
                    Pair("لانتانیدها (۵۷ تا ۷۱)", filteredList.filter { it.atomicNumber in 57..71 }),
                    Pair("آکتینیدها (۸۹ تا ۱۰۳)", filteredList.filter { it.atomicNumber in 89..103 })
                )

                items(periodGroups.filter { it.second.isNotEmpty() }) { (periodTitle, elementsInGroup) ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                        border = BorderStroke(1.dp, DarkSeparator)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = periodTitle,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = AppleBlue
                                )
                                Text(
                                    text = "${PersianUtils.toPersianDigits(elementsInGroup.size)} عنصر",
                                    fontSize = 11.sp,
                                    color = TextTertiary
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // شبکه منعطف عناصر این دوره
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                elementsInGroup.forEach { el ->
                                    RoomElementCompactCell(
                                        element = el,
                                        isDimmed = false,
                                        onClick = {
                                            coroutineScope.launch {
                                                isLoadingElement = true
                                                val fetched = elementDao.getElementByNumber(el.atomicNumber)
                                                selectedElement = fetched ?: el
                                                isLoadingElement = false
                                                onElementSelected?.invoke(selectedElement!!)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // نشانگر لودینگ واکشی از دیتابیس Room
    if (isLoadingElement) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                border = BorderStroke(1.dp, AppleBlue)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = AppleBlue)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("در حال بارگذاری اطلاعات...", color = TextPrimary, fontSize = 13.sp)
                }
            }
        }
    }

    // شیت نمایش جزئیات واکشی شده از دیتابیس Room
    selectedElement?.let { element ->
        RoomElementDetailBottomSheet(
            element = element,
            onDismiss = { selectedElement = null }
        )
    }
}

/**
 * سلول فشرده و باکیفیت برای جدول تناوبی
 */
@Composable
fun RoomElementCompactCell(
    element: ElementEntity,
    isDimmed: Boolean,
    isFitMode: Boolean = false,
    cellWidth: androidx.compose.ui.unit.Dp = 54.dp,
    cellHeight: androidx.compose.ui.unit.Dp = 62.dp,
    onClick: () -> Unit
) {
    val categoryColor = getCategoryColor(element.category)

    if (isFitMode) {
        // سلول بسیار فشرده برای نمایش تمام صفحه کل جدول مندلیف
        Box(
            modifier = Modifier
                .size(width = cellWidth, height = cellHeight)
                .clip(RoundedCornerShape(3.dp))
                .background(if (isDimmed) DarkSurface2.copy(alpha = 0.35f) else DarkSurface2)
                .border(
                    width = 1.dp,
                    color = if (isDimmed) DarkSeparator.copy(alpha = 0.2f) else categoryColor.copy(alpha = 0.75f),
                    shape = RoundedCornerShape(3.dp)
                )
                .clickable { onClick() }
                .testTag("element_cell_${element.symbol}"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 1.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // عدد اتمی ریز
                Text(
                    text = element.atomicNumber.toString(),
                    fontSize = 6.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDimmed) TextTertiary else categoryColor,
                    maxLines = 1
                )

                // نماد عنصر
                Text(
                    text = element.symbol,
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isDimmed) TextTertiary else TextPrimary,
                    maxLines = 1
                )

                // خط رنگی دسته
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(1.5.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(if (isDimmed) Color.Transparent else categoryColor)
                )
            }
        }
    } else {
        // سلول استاندارد با اندازه بزرگ، نام فارسی و جزئیات کامل
        Box(
            modifier = Modifier
                .size(width = cellWidth, height = cellHeight)
                .clip(RoundedCornerShape(10.dp))
                .background(DarkSurface2)
                .border(
                    width = 1.dp,
                    color = if (isDimmed) DarkSeparator.copy(alpha = 0.25f) else categoryColor.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable { onClick() }
                .testTag("element_cell_${element.symbol}"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(3.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // ردیف بالا: عدد اتمی
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = PersianUtils.toPersianDigits(element.atomicNumber),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDimmed) TextTertiary else categoryColor
                    )

                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(if (isDimmed) Color.Transparent else categoryColor)
                    )
                }

                // مرکز: نماد شیمیایی
                Text(
                    text = element.symbol,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isDimmed) TextTertiary else TextPrimary
                )

                // پایین: نام فارسی
                Text(
                    text = element.persianName,
                    fontSize = 8.5.sp,
                    maxLines = 1,
                    fontWeight = FontWeight.Medium,
                    color = if (isDimmed) TextTertiary else TextSecondary
                )
            }
        }
    }
}

/**
 * شیت اختصاصی نمایش جزئیات عنصر واکشی شده از دیتابیس Room
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomElementDetailBottomSheet(
    element: ElementEntity,
    onDismiss: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    var copiedToClipboard by remember { mutableStateOf(false) }
    val categoryColor = getCategoryColor(element.category)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface1,
        dragHandle = { BottomSheetDefaults.DragHandle(color = DarkSeparator) },
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = Modifier.testTag("room_element_detail_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 36.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // هدر عنصر: نماد، نام فارسی، نام انگلیسی و دسته
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // نماد بزرگ
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(categoryColor.copy(alpha = 0.35f), DarkSurface2)
                                )
                            )
                            .border(2.dp, categoryColor, RoundedCornerShape(18.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = PersianUtils.toPersianDigits(element.atomicNumber),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = categoryColor
                            )
                            Text(
                                text = element.symbol,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = element.persianName,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                        Text(
                            text = element.englishName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // تگ دسته عنصر
                        Surface(
                            color = categoryColor.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, categoryColor.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = element.category,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = categoryColor,
                                modifier = Modifier
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                                    .testTag("element_category_label")
                            )
                        }
                    }
                }

                // دکمه کپی مشخصات
                IconButton(
                    onClick = {
                        val text = """
                            عنصر: ${element.persianName} (${element.symbol})
                            عدد اتمی: ${element.atomicNumber}
                            جرم اتمی: ${element.atomicMass}
                            دسته: ${element.category}
                            دوره: ${element.period} | گروه: ${element.group}
                            آرایش الکترونی: ${element.electronConfiguration}
                        """.trimIndent()
                        clipboardManager.setText(AnnotatedString(text))
                        copiedToClipboard = true
                    }
                ) {
                    Icon(
                        imageVector = if (copiedToClipboard) Icons.Default.Check else Icons.Default.Share,
                        contentDescription = "کپی یا اشتراک",
                        tint = if (copiedToClipboard) AppleGreen else TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // ۳ کارت آماری اصلی: عدد اتمی، جرم اتمی و دسته (الزامات صریح کاربر)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // عدد اتمی
                RoomStatCard(
                    modifier = Modifier.weight(1f),
                    title = "عدد اتمی (Z)",
                    value = PersianUtils.toPersianDigits(element.atomicNumber),
                    color = AppleBlue,
                    testTag = "atomic_number_label"
                )

                // جرم اتمی
                RoomStatCard(
                    modifier = Modifier.weight(1f),
                    title = "جرم اتمی",
                    value = "${PersianUtils.toPersianDigits(element.atomicMass)}",
                    subtitle = "g/mol",
                    color = AppleOrange,
                    testTag = "atomic_mass_label"
                )

                // دسته
                RoomStatCard(
                    modifier = Modifier.weight(1.2f),
                    title = "دسته عنصر",
                    value = element.category,
                    color = categoryColor,
                    testTag = "element_category_card"
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // سایر مشخصات علمی واکشی شده از Room
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface2),
                border = BorderStroke(1.dp, DarkSeparator),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "🔬 ویژگی‌های ساختاری و موقعیت",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    RoomDetailRow(title = "آرایش الکترونی", value = element.electronConfiguration)
                    RoomDetailRow(title = "دوره (تناوب)", value = PersianUtils.toPersianDigits(element.period))
                    RoomDetailRow(title = "گروه", value = PersianUtils.toPersianDigits(element.group))
                    RoomDetailRow(title = "بلوک جدول", value = "زیرلایه ${element.block}")
                    element.electronegativity?.let {
                        RoomDetailRow(title = "الکترونگاتیوی (مقیاس پاولینگ)", value = PersianUtils.toPersianDigits(it))
                    }
                    element.atomicRadiusPm?.let {
                        RoomDetailRow(title = "شعاع اتمی", value = "${PersianUtils.toPersianDigits(it)} pm")
                    }
                    RoomDetailRow(title = "حالت در ۲۹۸ کلوین", value = element.stateAt298K)
                    if (element.oxidationStates.isNotBlank()) {
                        RoomDetailRow(title = "اعداد اکسایش متداول", value = PersianUtils.toPersianDigits(element.oxidationStates))
                    }
                }
            }

            // مشخصات ویژه کنکوری: آزمون شعله یا استثنای آرایش
            val flameColor = when (element.atomicNumber) {
                3 -> "سرخ کارمینی (زرشکی)"
                11 -> "زرد طلایی درخشان"
                19 -> "بنفش کم‌رنگ (ارغوانی)"
                20 -> "سرخ آجری"
                29 -> "سبز زمردی / آبی"
                38 -> "سرخ درخشان آتش‌بازی"
                56 -> "سبز سیبی شاخص"
                else -> null
            }

            val configException = when (element.atomicNumber) {
                24 -> "[Ar] 3d⁵ 4s¹ (نیمه‌پر با ۶ الکترون تک، حداکثر الکترون تک دوره ۴)"
                29 -> "[Ar] 3d¹⁰ 4s¹ (لایه d پر و پایدار با ۱ الکترون تک در 4s)"
                42 -> "[Kr] 4d⁵ 5s¹ (نیمه‌پر)"
                46 -> "[Kr] 4d¹⁰ (زیرلایه 5s کاملاً خالی)"
                47 -> "[Kr] 4d¹⁰ 5s¹ (لایه d پر)"
                79 -> "[Xe] 4f¹⁴ 5d¹⁰ 6s¹ (لایه d پر)"
                else -> null
            }

            if (flameColor != null || configException != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (flameColor != null) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = AppleOrange.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, AppleOrange.copy(alpha = 0.5f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(text = "🔥 آزمون شعله کنکور", style = MaterialTheme.typography.labelSmall, color = AppleOrange, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = flameColor, style = MaterialTheme.typography.bodySmall, color = TextPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    if (configException != null) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MetallicGold.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, MetallicGold.copy(alpha = 0.5f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(text = "⚡ استثنای آرایش الکترونی", style = MaterialTheme.typography.labelSmall, color = MetallicGold, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = configException, style = MaterialTheme.typography.bodySmall, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            // نکات تستی، حفظیات و کاربردهای مهم کنکور (طبق جزوات کنکور)
            if (element.konkurTipsAndUses.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface2),
                    border = BorderStroke(1.dp, AppleYellow.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🎯", fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "نکات طلایی، حفظیات و دام‌های تستی کنکور",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = AppleYellow
                                )
                            }
                            Surface(
                                color = AppleYellow.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "نکات تستی کنکور",
                                    color = AppleYellow,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = element.konkurTipsAndUses,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary,
                            lineHeight = 24.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // دکمه تایید و بستن
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppleBlue),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("close_room_element_detail_button")
            ) {
                Text(
                    text = "متوجه شدم و بستن",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun RoomStatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String? = null,
    color: Color,
    testTag: String
) {
    Card(
        modifier = modifier.testTag(testTag),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface2),
        border = BorderStroke(1.dp, color.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                fontSize = 10.sp,
                color = TextSecondary,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 9.sp,
                    color = TextTertiary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun RoomDetailRow(
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
    }
    HorizontalDivider(color = DarkSeparator.copy(alpha = 0.5f), thickness = 0.5.dp)
}

/**
 * رنگ‌بندی اختصاصی دسته‌های جدول تناوبی
 */
fun getCategoryColor(category: String): Color {
    return when {
        category.contains("قلیایی خاکی") -> CatAlkalineEarth
        category.contains("قلیایی") -> CatAlkaliMetal
        category.contains("واسطه") && !category.contains("پس") -> CatTransitionMetal
        category.contains("پس‌واسطه") -> CatPostTransitionMetal
        category.contains("شبه‌فلز") -> CatMetalloid
        category.contains("هالوژن") -> CatHalogen
        category.contains("گاز نجیب") || category.contains("نجیب") -> CatNobleGas
        category.contains("لانتانید") -> CatLanthanide
        category.contains("آکتینید") -> CatActinide
        category.contains("نافلز") -> CatReactiveNonmetal
        else -> CatTransitionMetal
    }
}
