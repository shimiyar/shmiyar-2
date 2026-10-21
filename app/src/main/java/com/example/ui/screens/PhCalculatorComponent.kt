package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.util.PersianUtils
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt

data class PresetSubstance(
    val name: String,
    val formula: String,
    val typicalPh: Double,
    val category: String,
    val note: String
)

@Composable
fun PhCalculatorComponent(
    onLockedClick: () -> Unit,
    isPro: Boolean
) {
    var phInputStr by remember { mutableStateOf("3.0") }
    var selectedPreset by remember { mutableStateOf<PresetSubstance?>(null) }

    val presets = remember {
        listOf(
            PresetSubstance("شیره معده (اسید کلریدریک)", "HCl", 1.5, "اسیدی قوی", "اسید تک پروتون‌دار قوی با یونش کامل"),
            PresetSubstance("آبلیمو طبیعی", "اسید سیتریک", 2.3, "اسیدی", "حاوی اسیدهای آلی ضعیف"),
            PresetSubstance("سرکه خوراکی", "CH3COOH", 2.9, "اسیدی ضعیف", "استیک اسید، درجه یونش کوچک‌تر از ۵٪"),
            PresetSubstance("آب گوجه فرنگی", "آلی", 4.2, "اسیدی ملایم", "پرتکرار در سوالات مقایسه‌ای کنکور"),
            PresetSubstance("باران اسیدی آلوده", "H2SO4 / HNO3", 4.5, "اسیدی", "ناشی از اکسیدهای گوگرد و نیتروژن در هوا"),
            PresetSubstance("شیر تازه", "لاکتیک اسید", 6.6, "تقریباً خنثی", "اندکی اسیدی به دلیل تخمیر لاکتوز"),
            PresetSubstance("آب خالص در دمای ۲۵°C", "H2O", 7.0, "کاملاً خنثی", "Kw = 10⁻¹⁴ و [H⁺] = [OH⁻] = 10⁻⁷"),
            PresetSubstance("خون انسان", "بافر بی کربنات", 7.4, "بازی ملایم", "دام کنکور: pH خون بین ۷.۳۵ تا ۷.۴۵ ثابت است"),
            PresetSubstance("صابون دستشویی", "R-COONa", 9.5, "بازی", "نمک اسید ضعیف و باز قوی با خاصیت قلیایی"),
            PresetSubstance("محلول آمونیاک خانگی", "NH3(aq)", 11.2, "بازی ضعیف", "باز ضعیف با Kb در تعادل یونی"),
            PresetSubstance("محلول لوله‌بازکن", "NaOH", 13.5, "بازی قوی", "سدیم هیدروکسید با یونش ۱۰۰٪ و اثر خورنده")
        )
    }

    val currentPh = remember(phInputStr) {
        val parsed = phInputStr.toDoubleOrNull()
        when {
            parsed == null -> 7.0
            parsed < 0.0 -> 0.0
            parsed > 14.0 -> 14.0
            else -> parsed
        }
    }

    val pOh = remember(currentPh) {
        (14.0 - currentPh).coerceIn(0.0, 14.0)
    }

    val hConc = remember(currentPh) {
        10.0.pow(-currentPh)
    }

    val ohConc = remember(pOh) {
        10.0.pow(-pOh)
    }

    val phColor = remember(currentPh) {
        when {
            currentPh < 3.0 -> Color(0xFFE53935) // Red
            currentPh < 6.0 -> Color(0xFFFB8C00) // Orange
            currentPh in 6.0..8.0 -> Color(0xFF43A047) // Green
            currentPh <= 11.0 -> Color(0xFF1E88E5) // Blue
            else -> Color(0xFF8E24AA) // Purple
        }
    }

    val natureText = remember(currentPh) {
        when {
            currentPh < 2.0 -> "اسیدی بسیار قوی"
            currentPh < 6.0 -> "اسیدی"
            currentPh in 6.0..8.0 -> "محیط خنثی"
            currentPh <= 12.0 -> "بازی"
            else -> "بازی بسیار قوی"
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("ph_calculator_component"),
        contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // ۱. کارت ورودی pH با اسلایدر و تکست‌فیلد
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                border = BorderStroke(1.dp, phColor.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🧪 محاسبه‌گر غلظت یون‌های هیدرونیوم و هیدروکسید",
                            fontWeight = FontWeight.Bold,
                            color = phColor,
                            fontSize = 13.sp
                        )
                        Surface(
                            color = phColor.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = natureText,
                                color = phColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "مقدار pH محلول:", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            Text(
                                text = PersianUtils.toPersianDigits(String.format("%.2f", currentPh)),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary
                            )
                        }

                        // Input field
                        OutlinedTextField(
                            value = phInputStr,
                            onValueChange = {
                                phInputStr = it
                                selectedPreset = null
                            },
                            singleLine = true,
                            placeholder = { Text("0 - 14", color = TextTertiary, fontSize = 12.sp) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.width(110.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = DarkSurface2,
                                unfocusedContainerColor = DarkSurface2,
                                focusedBorderColor = phColor,
                                unfocusedBorderColor = DarkSeparator,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Slider for intuitive scrubbing
                    Slider(
                        value = currentPh.toFloat(),
                        onValueChange = {
                            val rounded = (it * 10).roundToInt() / 10.0
                            phInputStr = String.format("%.1f", rounded)
                            selectedPreset = null
                        },
                        valueRange = 0f..14f,
                        colors = SliderDefaults.colors(
                            thumbColor = phColor,
                            activeTrackColor = phColor,
                            inactiveTrackColor = DarkSurface3
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Color Spectrum Scale
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFFE53935),
                                        Color(0xFFFB8C00),
                                        Color(0xFFFDD835),
                                        Color(0xFF43A047),
                                        Color(0xFF1E88E5),
                                        Color(0xFF8E24AA)
                                    )
                                )
                            )
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "۰ (بسیار اسیدی)", fontSize = 9.sp, color = TextTertiary)
                        Text(text = "۷ (خنثی)", fontSize = 9.sp, color = TextTertiary)
                        Text(text = "۱۴ (بسیار بازی)", fontSize = 9.sp, color = TextTertiary)
                    }
                }
            }
        }

        // ۲. نتایج محاسباتی دقیق کنکور
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                border = BorderStroke(1.dp, DarkSeparator),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "📊 مقادیر تعادلی در دمای ۲۵ درجه سانتی‌گراد (۲۹۸K):",
                        fontWeight = FontWeight.Bold,
                        color = MetallicCyan,
                        fontSize = 12.5.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ResultValueBox(
                            title = "pH",
                            value = PersianUtils.toPersianDigits(String.format("%.2f", currentPh)),
                            color = phColor,
                            modifier = Modifier.weight(1f)
                        )
                        ResultValueBox(
                            title = "pOH",
                            value = PersianUtils.toPersianDigits(String.format("%.2f", pOh)),
                            color = AppleBlue,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ResultValueBox(
                            title = "[H₃O⁺] غلظت هیدرونیوم",
                            value = formatScientific(hConc) + " M",
                            color = AppleOrange,
                            modifier = Modifier.weight(1f)
                        )
                        ResultValueBox(
                            title = "[OH⁻] غلظت هیدروکسید",
                            value = formatScientific(ohConc) + " M",
                            color = AppleGreen,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // ۳. شبیه‌ساز شناساگرهای رنگی اسید و باز (سوال پرتکرار کنکور سراسری)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                border = BorderStroke(1.dp, DarkSeparator),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "🌈 تغییر رنگ شناساگرها در این pH:",
                        fontWeight = FontWeight.Bold,
                        color = AppleYellow,
                        fontSize = 12.5.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val indicators = listOf(
                        IndicatorInfo(
                            name = "کاغذ لیتموس (تورنسل)",
                            color = if (currentPh < 7.0) Color(0xFFE53935) else Color(0xFF1E88E5),
                            colorName = if (currentPh < 7.0) "قرمز (اسیدی)" else "آبی (بازی)"
                        ),
                        IndicatorInfo(
                            name = "فنل‌فتالئین (Phenolphthalein)",
                            color = if (currentPh < 8.2) Color(0xFFECEFF1) else Color(0xFFE91E63),
                            colorName = if (currentPh < 8.2) "بی‌رنگ (اسیدی / خنثی)" else "ارغوانی صورتی (بازی)"
                        ),
                        IndicatorInfo(
                            name = "برموتیمول بلو (Bromothymol Blue)",
                            color = when {
                                currentPh < 6.0 -> Color(0xFFFDD835) // yellow
                                currentPh <= 7.6 -> Color(0xFF43A047) // green
                                else -> Color(0xFF1E88E5) // blue
                            },
                            colorName = when {
                                currentPh < 6.0 -> "زرد (اسیدی)"
                                currentPh <= 7.6 -> "سبز (خنثی)"
                                else -> "آبی (بازی)"
                            }
                        ),
                        IndicatorInfo(
                            name = "متیل نارنجی (Methyl Orange)",
                            color = if (currentPh < 3.1) Color(0xFFE53935) else Color(0xFFFB8C00),
                            colorName = if (currentPh < 3.1) "قرمز (اسیدی تند)" else "زرد نارنجی (pH > 4.4)"
                        )
                    )

                    indicators.forEach { ind ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = ind.name, fontSize = 11.5.sp, color = TextPrimary)
                            Surface(
                                color = ind.color.copy(alpha = 0.25f),
                                border = BorderStroke(1.dp, ind.color),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(ind.color)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = ind.colorName,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (ind.color == Color(0xFFECEFF1)) TextPrimary else ind.color
                                    )
                                }
                            }
                        }
                        Divider(color = DarkSeparator.copy(alpha = 0.5f))
                    }
                }
            }
        }

        // ۴. مواد و محلول‌های پرتکرار کنکور (Presets)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                border = BorderStroke(1.dp, DarkSeparator),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "📌 محلول‌ها و نمونه‌های کتاب درسی کنکور:",
                        fontWeight = FontWeight.Bold,
                        color = MetallicEmerald,
                        fontSize = 12.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "برای بررسی خودکار روی هر محلول لمس کنید:",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(presets) { p ->
                            val isSelected = selectedPreset == p
                            Surface(
                                onClick = {
                                    selectedPreset = p
                                    phInputStr = p.typicalPh.toString()
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) AppleBlue else DarkSurface2,
                                border = BorderStroke(1.dp, if (isSelected) AppleBlue else DarkSeparator)
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)) {
                                    Text(
                                        text = p.name,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else TextPrimary
                                    )
                                    Text(
                                        text = "pH ≈ ${PersianUtils.toPersianDigits(p.typicalPh)}",
                                        fontSize = 10.sp,
                                        color = if (isSelected) Color.White.copy(alpha = 0.9f) else TextSecondary
                                    )
                                }
                            }
                        }
                    }

                    if (selectedPreset != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            color = DarkSurface2,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, MetallicBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = AppleYellow, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "نکته کنکوری: ${selectedPreset!!.note}",
                                    fontSize = 11.sp,
                                    color = TextPrimary,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // ۵. فرمول‌های میانبر و تستی لگاریتم در کنکور
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                border = BorderStroke(1.dp, AppleYellow.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "⚡ ترفندهای محاسباتی سریع لگاریتم در مسائل کنکور:",
                        fontWeight = FontWeight.Bold,
                        color = AppleYellow,
                        fontSize = 12.5.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• هر یک واحد تغییر در pH، غلظت یون هیدرونیوم را ۱۰ برابر می‌کند.\n• رابطه تستی اسید ضعیف: pH = ½(pKa - log M)\n• حفظی‌های لگاریتم کنکور: log 2 ≈ ۰.۳ | log 3 ≈ ۰.۴۸ | log 5 ≈ ۰.۷ | log 7 ≈ ۰.۸۵",
                        fontSize = 11.5.sp,
                        color = TextPrimary,
                        lineHeight = 22.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ResultValueBox(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = DarkSurface2,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.35f)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = title, fontSize = 10.5.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

data class IndicatorInfo(
    val name: String,
    val color: Color,
    val colorName: String
)

fun formatScientific(value: Double): String {
    if (value <= 0.0) return "۰"
    val exp = kotlin.math.floor(log10(value)).toInt()
    val base = value / 10.0.pow(exp)
    return String.format("%.2f × ۱۰^(%d)", base, exp)
}
