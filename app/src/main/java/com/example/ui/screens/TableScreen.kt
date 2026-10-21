package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ElementItem
import com.example.ui.components.SmartUpgradeBanner
import com.example.ui.theme.*
import com.example.ui.viewmodel.ShimiYarViewModel
import com.example.util.PersianUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableScreen(
    viewModel: ShimiYarViewModel
) {
    val elements = viewModel.elements
    val streak by viewModel.streak.collectAsState()
    val isPro by viewModel.isPro.collectAsState()
    val selectedElement by viewModel.selectedElement.collectAsState()

    var searchQuery by remember { mutableStateOf("") }

    val filteredElements = remember(elements, searchQuery) {
        if (searchQuery.isBlank()) elements else {
            elements.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.symbol.contains(searchQuery, ignoreCase = true) ||
                it.number.toString().contains(searchQuery)
            }
        }
    }

    // Grid mapping: (period/row, group/column) -> element
    val gridMap = remember(elements) {
        elements.associateBy { Pair(it.y, it.x) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .testTag("table_screen")
    ) {
        // 1. Sleek Compact Streak & Tip Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🔥", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${PersianUtils.toPersianDigits(streak)} روز مطالعه پیاپی",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = AppleOrange
                )
            }

            Surface(
                onClick = { viewModel.openDailyTip() },
                color = AppleYellow.copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, AppleYellow.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "💡 نکته روز", color = AppleYellow, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // 2. Periodic Table Room Component (Connected to SQLite / Room Database)
        com.example.ui.components.PeriodicTableRoomComponent(
            modifier = Modifier.fillMaxSize(),
            customElementDao = viewModel.elementDao
        )
    }

    // Element Detail Bottom Sheet
    selectedElement?.let { el ->
        ElementBottomSheet(
            element = el,
            isPro = isPro,
            savedNote = viewModel.getElementNote(el.number),
            onSaveNote = { viewModel.saveElementNote(el.number, it) },
            onUnlockPro = { viewModel.openPremiumModal() },
            onDismiss = { viewModel.closeElementDetail() }
        )
    }
}

@Composable
fun ElementCard(
    element: ElementItem,
    dimmed: Boolean,
    onClick: () -> Unit
) {
    val catColor = getCategoryColor(element.category)

    Box(
        modifier = Modifier
            .size(width = 62.dp, height = 72.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurface1)
            .border(
                width = 1.dp,
                color = if (dimmed) DarkSeparator.copy(alpha = 0.3f) else DarkSeparator,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .testTag("element_cell_${element.symbol}"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Atomic number
            Text(
                text = PersianUtils.toPersianDigits(element.number),
                fontSize = 10.sp,
                color = if (dimmed) TextTertiary.copy(alpha = 0.4f) else TextTertiary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            // Symbol
            Text(
                text = element.symbol,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = if (dimmed) TextPrimary.copy(alpha = 0.4f) else TextPrimary
            )

            // Persian Name
            Text(
                text = element.name,
                fontSize = 10.sp,
                maxLines = 1,
                color = if (dimmed) TextSecondary.copy(alpha = 0.4f) else TextSecondary
            )

            // Bottom border 3dp colored by category
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(1.5.dp))
                    .background(if (dimmed) catColor.copy(alpha = 0.3f) else catColor)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElementBottomSheet(
    element: ElementItem,
    isPro: Boolean,
    savedNote: String,
    onSaveNote: (String) -> Unit,
    onUnlockPro: () -> Unit,
    onDismiss: () -> Unit
) {
    var noteText by remember { mutableStateOf(savedNote) }
    var isEditingNote by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = DarkSurface1,
        dragHandle = { BottomSheetDefaults.DragHandle(color = DarkSeparator) },
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        modifier = Modifier.testTag("element_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Large Symbol with Blue-Indigo gradient text
            Text(
                text = element.symbol,
                style = MaterialTheme.typography.displayMedium.copy(
                    brush = Brush.linearGradient(colors = listOf(AppleBlue, AppleIndigo))
                ),
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = element.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Surface(
                color = getCategoryColor(element.category).copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = getCategoryPersian(element.category),
                    color = getCategoryColor(element.category),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2-Column Grid: atomic number, mass, category, group, period, electron config
            val specs = listOf(
                Pair("عدد اتمی", PersianUtils.toPersianDigits(element.number)),
                Pair("جرم اتمی", "${PersianUtils.toPersianDigits(element.mass)} amu"),
                Pair("دسته عنصر", getCategoryPersian(element.category)),
                Pair("گروه", PersianUtils.toPersianDigits(element.group)),
                Pair("تناوب (دوره)", PersianUtils.toPersianDigits(element.period)),
                Pair("آرایش الکترونی", PersianUtils.toSubscript(element.electron_config))
            )

            specs.chunked(2).forEach { rowSpecs ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowSpecs.forEach { (label, value) ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface2),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = value,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Personal Note Section
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface2),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "📝 یادداشت شخصی من", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
                        TextButton(onClick = {
                            if (isEditingNote) {
                                onSaveNote(noteText)
                                isEditingNote = false
                            } else {
                                isEditingNote = true
                            }
                        }) {
                            Text(text = if (isEditingNote) "ذخیره" else "ویرایش", color = AppleBlue)
                        }
                    }

                    if (isEditingNote) {
                        OutlinedTextField(
                            value = noteText,
                            onValueChange = { noteText = it },
                            placeholder = { Text("نکته شخصی خود درباره این عنصر را اینجا بنویسید...", color = TextTertiary) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = AppleBlue,
                                unfocusedBorderColor = DarkSeparator
                            )
                        )
                    } else {
                        Text(
                            text = if (noteText.isBlank()) "هنوز یادداشتی برای این عنصر ننوشته‌اید." else noteText,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (noteText.isBlank()) TextTertiary else TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Konkur Analysis & Notes Section (طبق جزوات و کتب درسی کنکور)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface2),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    AppleOrange.copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🎯 نکات طلایی، حفظیات و دام‌های تستی کنکور",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = AppleOrange
                        )
                        Surface(
                            color = AppleOrange.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "نکات تستی کنکور",
                                color = AppleOrange,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val tipText = element.konkur_tip.takeIf { it.isNotBlank() }
                        ?: "نکات طلایی کنکور:\n• عنصر شماره ${element.number} (${element.name}) در دوره ${element.period} و گروه ${element.group} قرار دارد.\n• آرایش الکترونی: ${element.electron_config}."

                    Text(
                        text = tipText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        lineHeight = 23.sp
                    )
                }
            }
        }
    }
}
