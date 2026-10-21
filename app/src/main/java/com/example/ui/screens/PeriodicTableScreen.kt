package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.AppHeader
import com.example.ui.theme.OrangeAccent
import com.example.ui.theme.TealPrimary
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.PeriodicColorMode
import com.example.util.PersianUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodicTableScreen(
    viewModel: MainViewModel
) {
    val search by viewModel.periodicSearch.collectAsState()
    val colorMode by viewModel.periodicColorMode.collectAsState()
    val isKonkurMode by viewModel.isKonkurMode.collectAsState()
    val blockFilter by viewModel.selectedBlockFilter.collectAsState()
    val selectedElement by viewModel.selectedElement.collectAsState()

    val allElements = PeriodicTableData.elements

    val filteredElements = remember(search, blockFilter) {
        allElements.filter { el ->
            val matchesSearch = if (search.isBlank()) true else {
                el.persianName.contains(search, ignoreCase = true) ||
                el.englishName.contains(search, ignoreCase = true) ||
                el.symbol.contains(search, ignoreCase = true) ||
                el.atomicNumber.toString().contains(search)
            }
            val matchesBlock = if (blockFilter == null) true else el.block == blockFilter
            matchesSearch && matchesBlock
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "جدول تناوبی تعاملی",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    // Konkur Mode Switch
                    FilterChip(
                        selected = isKonkurMode,
                        onClick = { viewModel.toggleKonkurMode() },
                        label = { Text("حالت کنکوری") },
                        leadingIcon = {
                            Icon(
                                imageVector = if (isKonkurMode) Icons.Default.Check else Icons.Default.School,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = OrangeAccent,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("konkur_mode_chip")
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Search Bar
                OutlinedTextField(
                    value = search,
                    onValueChange = { viewModel.setPeriodicSearch(it) },
                    placeholder = { Text("جستجوی نام عنصر، نماد (Fe) یا عدد اتمی (۲۶)...") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (search.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setPeriodicSearch("") }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "پاک کردن")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("periodic_search_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Color Coding Mode Switcher
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(PeriodicColorMode.values()) { mode ->
                        val isSelected = colorMode == mode
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setPeriodicColorMode(mode) },
                            label = { Text(mode.title, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TealPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                // If Konkur mode is active, show block selector
                AnimatedVisibility(visible = isKonkurMode) {
                    Column(modifier = Modifier.padding(top = 8.dp)) {
                        Text(
                            text = "فیلتر بلوک الکترونی کنکوری:",
                            style = MaterialTheme.typography.labelSmall,
                            color = OrangeAccent,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            val blocks = listOf(null to "همه", "s" to "بلوک s", "p" to "بلوک p", "d" to "بلوک d", "f" to "بلوک f")
                            blocks.forEach { (blk, label) ->
                                val selected = blockFilter == blk
                                SuggestionChip(
                                    onClick = { viewModel.setBlockFilter(blk) },
                                    label = { Text(label, fontSize = 11.sp) },
                                    colors = SuggestionChipDefaults.suggestionChipColors(
                                        containerColor = if (selected) TealPrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
                                    ),
                                    border = if (selected) androidx.compose.foundation.BorderStroke(1.5.dp, TealPrimary) else null
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Periodic Grid with Horizontal Scroll
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "👈 جدول را به صورت افقی بکشید تا تمام ۱۸ گروه را ببینید",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        val hScroll = rememberScrollState()
                        Row(
                            modifier = Modifier
                                .horizontalScroll(hScroll)
                                .padding(bottom = 8.dp)
                        ) {
                            PeriodicTableGrid(
                                elements = allElements,
                                colorMode = colorMode,
                                onElementClick = { viewModel.openElementDetail(it) }
                            )
                        }
                    }
                }
            }

            // High Yield Konkur Facts if in Konkur mode
            if (isKonkurMode) {
                item {
                    KonkurSummaryCards()
                }
            }

            // Category Legend
            item {
                Text(
                    text = "راهنمای دسته‌بندی عناصر",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                CategoryLegendFlow()
            }
        }
    }

    selectedElement?.let { element ->
        ElementDetailSheet(
            element = element,
            onDismiss = { viewModel.closeElementDetail() }
        )
    }
}

@Composable
fun PeriodicTableGrid(
    elements: List<Element>,
    colorMode: PeriodicColorMode,
    onElementClick: (Element) -> Unit
) {
    // 7 periods (rows), 18 groups (columns)
    val elementMap = remember(elements) {
        elements.associateBy { Pair(it.period, it.group) }
    }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        // Group Numbers Header
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            // Period label placeholder
            Box(modifier = Modifier.size(24.dp))
            for (g in 1..18) {
                Box(
                    modifier = Modifier.size(width = 56.dp, height = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = PersianUtils.toPersianDigits(g),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        for (p in 1..7) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Period number label
                Box(
                    modifier = Modifier.size(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = PersianUtils.toPersianDigits(p),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                for (g in 1..18) {
                    val el = elementMap[Pair(p, g)]
                    if (el != null) {
                        ElementCell(
                            element = el,
                            colorMode = colorMode,
                            onClick = { onElementClick(el) }
                        )
                    } else {
                        // Empty slot
                        Box(modifier = Modifier.size(width = 56.dp, height = 64.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ElementCell(
    element: Element,
    colorMode: PeriodicColorMode,
    onClick: () -> Unit
) {
    val cellColor = when (colorMode) {
        PeriodicColorMode.CATEGORY -> element.category.color
        PeriodicColorMode.ELECTRONEGATIVITY -> {
            val en = element.electronegativity ?: 0.0
            val fraction = ((en - 0.7) / (4.0 - 0.7)).coerceIn(0.0, 1.0).toFloat()
            // Gradient from Cyan (low) to Orange/Red (high)
            androidx.compose.ui.graphics.lerp(Color(0xFF80DEEA), Color(0xFFFF5722), fraction)
        }
        PeriodicColorMode.ATOMIC_RADIUS -> {
            val r = element.atomicRadiusPm ?: 100
            val fraction = ((r - 30) / (300.0 - 30.0)).coerceIn(0.0, 1.0).toFloat()
            androidx.compose.ui.graphics.lerp(Color(0xFFE1BEE7), Color(0xFF26A69A), fraction)
        }
        PeriodicColorMode.STATE_OF_MATTER -> {
            when (element.stateAt298K) {
                ElementState.Solid -> Color(0xFF90CAF9)
                ElementState.Liquid -> Color(0xFFFFCC80)
                ElementState.Gas -> Color(0xFFA5D6A7)
                ElementState.Synthetic -> Color(0xFFCE93D8)
            }
        }
    }

    Box(
        modifier = Modifier
            .size(width = 56.dp, height = 64.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(cellColor.copy(alpha = 0.28f))
            .border(1.dp, cellColor, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .testTag("element_${element.symbol}"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(2.dp)
        ) {
            Text(
                text = PersianUtils.toPersianDigits(element.atomicNumber),
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = element.symbol,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = element.persianName,
                fontSize = 9.sp,
                maxLines = 1,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun KonkurSummaryCards() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Text(
            text = "نکات تستی پرتکرار جدول تناوبی در کنکور",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = OrangeAccent
        )
        Spacer(modifier = Modifier.height(8.dp))

        val tips = listOf(
            "گروه ۱ (فلزات قلیایی): در آب به شدت واکنش می‌دهند و H2 آزاد می‌کنند. واکنش‌پذیری از بالا به پایین افزایش می‌یابد.",
            "گروه ۱۷ (هالوژن‌ها): تنها گروهی که هر سه حالت فیزیکی ماده (F2 و Cl2 گاز، Br2 مایع، I2 جامد) را در دمای اتاق دارد.",
            "روندهای دوره‌ای: در یک دوره از چپ به راست، شعاع اتمی کاهش و الکترونگاتیوی افزایش می‌یابد؛ در یک گروه از بالا به پایین برعکس است.",
            "استثناهای آفبا در دوره ۴: کروم ۲۴ با 3d⁵ 4s¹ و مس ۲۹ با 3d¹⁰ 4s¹ به دلیل پایداری بیشتر آرایش‌های نیمه‌پر و پر."
        )

        tips.forEach { tip ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(text = "📌", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = tip,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryLegendFlow() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        ElementCategory.values().toList().chunked(2).forEach { rowCats ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowCats.forEach { cat ->
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(cat.color)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = cat.persianTitle,
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
