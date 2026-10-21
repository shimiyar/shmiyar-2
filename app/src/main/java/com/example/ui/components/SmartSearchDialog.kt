package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.ElementItem
import com.example.search.SearchCategoryFilter
import com.example.search.SmartSearchEngine
import com.example.search.SmartSearchResult
import com.example.ui.theme.*
import com.example.ui.viewmodel.ShimiYarViewModel
import com.example.util.PersianUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartSearchDialog(
    viewModel: ShimiYarViewModel,
    onDismiss: () -> Unit
) {
    val query by viewModel.smartSearchQuery.collectAsState()
    val filter by viewModel.searchCategoryFilter.collectAsState()
    val results by viewModel.searchResults.collectAsState()

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        // Auto-request focus for immediate typing
        try {
            focusRequester.requestFocus()
        } catch (_: Exception) {}
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .testTag("smart_search_dialog"),
            color = DarkBackground
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // Top Search Bar Bar Header
                Surface(
                    color = DarkSurface1,
                    shadowElevation = 6.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        // Title & Close Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = MetallicCyan.copy(alpha = 0.15f),
                                    shape = CircleShape,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(text = "🔍", fontSize = 18.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "جستجوی هوشمند شیمی‌یار",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "عناصر، فرمول‌ها و روابط، و مباحث درسی کنکور",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondary
                                    )
                                }
                            }

                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier.testTag("close_smart_search_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "بستن",
                                    tint = TextSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Search Input Field
                        OutlinedTextField(
                            value = query,
                            onValueChange = { viewModel.setSmartSearchQuery(it) },
                            placeholder = {
                                Text(
                                    text = "نام عنصر (طلا، Fe)، فرمول (H2O، PV=nRT) یا مبحث...",
                                    color = TextTertiary,
                                    fontSize = 13.sp
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = MetallicCyan
                                )
                            },
                            trailingIcon = {
                                if (query.isNotBlank()) {
                                    IconButton(onClick = { viewModel.setSmartSearchQuery("") }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "پاک کردن",
                                            tint = TextSecondary
                                        )
                                    }
                                }
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = DarkSurface2,
                                unfocusedContainerColor = DarkSurface2,
                                focusedBorderColor = MetallicCyan,
                                unfocusedBorderColor = DarkSeparator,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester)
                                .testTag("smart_search_input_field")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Category Filter Tabs
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(SearchCategoryFilter.values()) { cat ->
                                val isSelected = filter == cat
                                Surface(
                                    color = if (isSelected) MetallicCyan.copy(alpha = 0.2f) else DarkSurface2,
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(
                                        width = 1.dp,
                                        color = if (isSelected) MetallicCyan else DarkSeparator
                                    ),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { viewModel.setSearchFilter(cat) }
                                        .testTag("filter_${cat.name.lowercase()}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = cat.iconEmoji, fontSize = 14.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = cat.title,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) MetallicCyan else TextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Results or Suggestions Content
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    if (query.isBlank()) {
                        // Empty Search State: Popular searches & tips
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                Text(
                                    text = "🔥 جستجوهای پرتکرار و کنکوری:",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = AppleOrange
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                FlowSuggestionChips(
                                    suggestions = SmartSearchEngine.popularSuggestions,
                                    onSuggestionClick = { suggestion ->
                                        val cleanText = suggestion.substringBefore(" (").trim()
                                        viewModel.setSmartSearchQuery(cleanText)
                                    }
                                )
                            }

                            item {
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkSeparator),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            text = "💡 راهنمای جستجوی هوشمند",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MetallicCyan
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        SearchTipRow(
                                            icon = "⚛️",
                                            title = "نام یا نماد عنصر شیمیایی",
                                            desc = "مانند «طلا»، «Fe»، «کربن» یا عدد اتمی «26» — انتقال مستقیم به شناسنامه عنصر در جدول تناوبی"
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        SearchTipRow(
                                            icon = "🧪",
                                            title = "فرمول یا رابطه کنکوری",
                                            desc = "مانند «H2O»، «NaCl»، «PV=nRT»، «pH» یا «M1V1=M2V2» — مشاهده مشخصات ترکیب و ارجاع به فصل مربوطه"
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        SearchTipRow(
                                            icon = "📚",
                                            title = "مبحث یا واژه کنکوری",
                                            desc = "مانند «استوکیومتری»، «آرایش الکترونی»، «اسید و باز»، «قاعده آفبا» یا «لایتنر» — باز شدن مستقیم جزوه و نکات طلایی"
                                        )
                                    }
                                }
                            }
                        }
                    } else if (results.isEmpty()) {
                        // No Results State
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = "🧪🔍", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "نتیجه‌ای برای «$query» پیدا نشد",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "املای کلمه را بررسی کنید یا از واژه‌های پیشنهادی زیر استفاده نمایید:",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(18.dp))
                            FlowSuggestionChips(
                                suggestions = listOf("H2O", "طلا", "PV=nRT", "اسید و باز", "NaCl", "آهن", "استوکیومتری"),
                                onSuggestionClick = { suggestion ->
                                    val cleanText = suggestion.substringBefore(" (").trim()
                                    viewModel.setSmartSearchQuery(cleanText)
                                }
                            )
                        }
                    } else {
                        // Results List
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 4.dp, vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${PersianUtils.toPersianDigits(results.size)} نتیجه پیدا شد",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MetallicCyan
                                    )
                                    Text(
                                        text = "کلیک برای هدایت مستقیم",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextTertiary
                                    )
                                }
                            }

                            items(results, key = { it.id }) { item ->
                                when (item) {
                                    is SmartSearchResult.ElementResult -> {
                                        SearchResultElementCard(
                                            result = item,
                                            onClick = { viewModel.navigateToElementFromSearch(item.element) }
                                        )
                                    }
                                    is SmartSearchResult.FormulaResult -> {
                                        SearchResultFormulaCard(
                                            result = item,
                                            onClick = { viewModel.navigateToFormulaFromSearch(item) },
                                            onElementClick = { symbol ->
                                                val elem = viewModel.elements.firstOrNull {
                                                    it.symbol.equals(symbol, ignoreCase = true)
                                                }
                                                if (elem != null) {
                                                    viewModel.navigateToElementFromSearch(elem)
                                                }
                                            }
                                        )
                                    }
                                    is SmartSearchResult.ChapterResult -> {
                                        SearchResultChapterCard(
                                            result = item,
                                            onClick = {
                                                viewModel.navigateToEducationFromSearch(
                                                    grade = item.chapter.grade,
                                                    chapterId = item.chapter.id
                                                )
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
    }
}

@Composable
fun SearchResultElementCard(
    result: SmartSearchResult.ElementResult,
    onClick: () -> Unit
) {
    val element = result.element

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface1),
        border = androidx.compose.foundation.BorderStroke(1.dp, AppleBlue.copy(alpha = 0.4f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("result_elem_${element.number}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Atomic Number & Symbol Box
            Surface(
                color = DarkSurface3,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AppleBlue.copy(alpha = 0.6f)),
                modifier = Modifier.size(54.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = PersianUtils.toPersianDigits(element.number),
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = element.symbol,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = AppleBlue
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = element.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Surface(
                        color = AppleBlue.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = element.category,
                            color = AppleBlue,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "جرم: ${element.mass} | گروه ${PersianUtils.toPersianDigits(element.group)}، تناوب ${PersianUtils.toPersianDigits(element.period)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontSize = 11.sp
                )

                if (result.matchDescription.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "🎯 ${result.matchDescription}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MetallicCyan,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Jump arrow
            Icon(
                imageVector = Icons.Default.ArrowBack, // Persian RTL: points to left/forward
                contentDescription = "مشاهده در جدول",
                tint = AppleBlue,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun SearchResultFormulaCard(
    result: SmartSearchResult.FormulaResult,
    onClick: () -> Unit,
    onElementClick: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface1),
        border = androidx.compose.foundation.BorderStroke(1.dp, MetallicCyan.copy(alpha = 0.4f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("result_formula_${result.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Formula Text
                Text(
                    text = result.formula,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MetallicCyan,
                    letterSpacing = 0.5.sp
                )

                Surface(
                    color = MetallicCyan.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = result.categoryBadge,
                        color = MetallicCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = result.chemicalName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = result.contextInfo,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom action row: related elements or jump to chapter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (result.relatedElementSymbols.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "عناصر سازنده:",
                            fontSize = 10.sp,
                            color = TextTertiary
                        )
                        result.relatedElementSymbols.forEach { sym ->
                            Surface(
                                color = DarkSurface3,
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.clickable { onElementClick(sym) }
                            ) {
                                Text(
                                    text = "$sym ⚛️",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AppleBlue,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.width(4.dp))
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable(onClick = onClick)
                ) {
                    Text(
                        text = if (result.relatedChapterId != null) "مشاهده در جزوه پایه ${PersianUtils.toPersianDigits(result.grade ?: 10)} ↗" else "مشاهده در جدول ↗",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MetallicCyan
                    )
                }
            }
        }
    }
}

@Composable
fun SearchResultChapterCard(
    result: SmartSearchResult.ChapterResult,
    onClick: () -> Unit
) {
    val chapter = result.chapter

    val gradeColor = when (chapter.grade) {
        10 -> AppleGreen
        11 -> AppleOrange
        12 -> ApplePurple
        else -> AppleBlue
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface1),
        border = androidx.compose.foundation.BorderStroke(1.dp, gradeColor.copy(alpha = 0.4f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("result_chap_${chapter.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = chapter.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f)
                )

                Surface(
                    color = gradeColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "پایه ${chapter.gradeTitle}",
                        color = gradeColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                color = DarkSurface2,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "🔍 بخش منطبق: ${result.matchedField}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = gradeColor
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = result.matchedSnippet,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "رفتن به جزوه و مطالعه مبحث 📖",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = gradeColor
                )
            }
        }
    }
}

@Composable
fun FlowSuggestionChips(
    suggestions: List<String>,
    onSuggestionClick: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Group into chunks of 3 for tidy wrapping
        suggestions.chunked(3).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { item ->
                    Surface(
                        color = DarkSurface2,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSeparator),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onSuggestionClick(item) }
                    ) {
                        Text(
                            text = item,
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp)
                        )
                    }
                }
                // Fill remainder if row has less than 3
                for (i in rowItems.size until 3) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun SearchTipRow(
    icon: String,
    title: String,
    desc: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(text = icon, fontSize = 16.sp)
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                lineHeight = 16.sp
            )
        }
    }
}
