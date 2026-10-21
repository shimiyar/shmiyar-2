package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GlossaryItem
import com.example.ui.theme.*
import com.example.ui.viewmodel.ShimiYarViewModel
import com.example.util.PersianUtils

@Composable
fun GlossaryScreen(
    viewModel: ShimiYarViewModel
) {
    val allTerms = viewModel.glossaryTerms
    val searchQuery by viewModel.glossarySearch.collectAsState()
    val selectedCategory by viewModel.selectedGlossaryCategory.collectAsState()

    val categories = listOf(
        "همه",
        "مفاهیم پایه",
        "خواص دوره‌ای",
        "پیوند",
        "واکنش",
        "الکتروشیمی",
        "شیمی آلی"
    )

    val filteredTerms = remember(allTerms, searchQuery, selectedCategory) {
        allTerms.filter { item ->
            val matchCat = if (selectedCategory == "همه") true else item.category == selectedCategory
            val matchSearch = if (searchQuery.isBlank()) true else {
                item.term.contains(searchQuery, ignoreCase = true) ||
                item.definition.contains(searchQuery, ignoreCase = true)
            }
            matchCat && matchSearch
        }
    }

    val isPro by viewModel.isPro.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .testTag("glossary_screen")
    ) {
        // Unlock Progress Bar (دسترسی ۲۵٪ فقط برای نسخه رایگان)
        if (!isPro) {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "دسترسی ۲۵٪ نسخه رایگان به واژه‌نامه",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = AppleOrange
                        )
                        Text(
                            text = "۲۵٪",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = AppleOrange
                        )
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    LinearProgressIndicator(
                        progress = { 0.25f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = AppleOrange,
                        trackColor = DarkSurface3
                    )
                }
            }
        }

        // Upgrade Banner for Free Users
        if (!isPro) {
            com.example.ui.components.UrgencyPromoCountdownBanner(
                title = "👑 عضویت ویژه شیمی‌یار طلایی",
                subtitle = "دسترسی کامل به تعاریف کنکور، فرمول‌ها و مفاهیم شیمی ۳ پایه",
                onUpgradeClick = { viewModel.openDailyOfferModal() },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
            )
        }

        // Terms List - scrollable area containing search and category filters
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Search Field inside scrollable content
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setGlossarySearch(it) },
                    placeholder = { Text("جستجوی واژه یا مفهوم شیمی...", color = TextTertiary, fontSize = 13.sp) },
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = AppleBlue, modifier = Modifier.size(20.dp)) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setGlossarySearch("") }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
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
                        .testTag("glossary_search_field")
                )
            }

            // Category Filter Pills inside scrollable content
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { cat ->
                        val isSelected = selectedCategory == cat
                        Surface(
                            color = if (isSelected) AppleIndigo else DarkSurface2,
                            shape = RoundedCornerShape(20.dp),
                            border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, DarkSeparator),
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { viewModel.setGlossaryCategory(cat) }
                        ) {
                            Text(
                                text = cat,
                                color = if (isSelected) Color.White else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Glossary Terms
            items(filteredTerms) { item ->
                GlossaryCardItem(item)
            }
        }
    }
}

@Composable
fun GlossaryCardItem(item: GlossaryItem) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface1),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSeparator),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.term,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AppleTeal
                )

                Surface(
                    color = AppleIndigo.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = item.category,
                        color = AppleIndigo,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = PersianUtils.formatFormula(item.definition),
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                lineHeight = 22.sp
            )
        }
    }
}
