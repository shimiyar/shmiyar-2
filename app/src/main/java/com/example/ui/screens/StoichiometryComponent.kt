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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.util.PersianUtils

/**
 * ماشین‌حساب جامع و هوشمند استوکیومتری کنکور
 * شامل دو بخش:
 * ۱. حل مسائل واکنش‌های استوکیومتری (معلوم به مجهول A -> B با ضرایب، درصد خلوص و بازده درصدی)
 * ۲. تبدیل جامع یکاها و شرایط STP
 */
enum class StoichiometryCalculatorTab(val title: String, val icon: String) {
    REACTION_SOLVER("حل مسئله واکنش (A ➔ B)", "⚗️"),
    UNIT_CONVERTER("تبدیل سریع یکاها و STP", "📐")
}

enum class StoichUnit(val label: String, val shortUnit: String) {
    GRAM("گرم (g)", "g"),
    MOLE("مول (mol)", "mol"),
    LITER_STP("لیتر STP", "L"),
    ML_STP("میلی‌لیتر STP", "mL"),
    PARTICLES("تعداد ذره", "×۱۰²³")
}

data class KonkurReactionPreset(
    val title: String,
    val equation: String,
    val reactantName: String,
    val reactantMolarMass: Double,
    val reactantCoeff: Double,
    val targetName: String,
    val targetMolarMass: Double,
    val targetCoeff: Double,
    val defaultKnownUnit: StoichUnit = StoichUnit.GRAM,
    val defaultTargetUnit: StoichUnit = StoichUnit.LITER_STP
)

data class QuickCompound(val name: String, val formula: String, val molarMass: Double)

@Composable
fun StoichiometryComponent(modifier: Modifier = Modifier) {
    var activeTab by remember { mutableStateOf(StoichiometryCalculatorTab.REACTION_SOLVER) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("stoichiometry_component")
    ) {
        // Tab Selector Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(DarkSurface2)
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            StoichiometryCalculatorTab.values().forEach { tab ->
                val isSelected = activeTab == tab
                Surface(
                    onClick = { activeTab = tab },
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) AppleOrange else Color.Transparent,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = tab.icon, fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = tab.title,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else TextSecondary
                        )
                    }
                }
            }
        }

        when (activeTab) {
            StoichiometryCalculatorTab.REACTION_SOLVER -> ReactionStoichiometrySolverView()
            StoichiometryCalculatorTab.UNIT_CONVERTER -> SingleSubstanceStoichiometryView()
        }
    }
}

/**
 * ۱. حل‌کننده پیشرفته مسائل واکنش‌های استوکیومتری
 */
@Composable
fun ReactionStoichiometrySolverView() {
    val presets = remember {
        listOf(
            KonkurReactionPreset(
                title = "تولید گاز H₂ از آلومینیم",
                equation = "2Al + 6HCl ➔ 2AlCl₃ + 3H₂",
                reactantName = "آلومینیم (Al)",
                reactantMolarMass = 27.0,
                reactantCoeff = 2.0,
                targetName = "گاز هیدروژن (H₂)",
                targetMolarMass = 2.0,
                targetCoeff = 3.0,
                defaultKnownUnit = StoichUnit.GRAM,
                defaultTargetUnit = StoichUnit.LITER_STP
            ),
            KonkurReactionPreset(
                title = "تجزیه سنگ آهک (تولید CO₂)",
                equation = "CaCO₃ ➔ CaO + CO₂",
                reactantName = "کلسیم کربنات (CaCO₃)",
                reactantMolarMass = 100.0,
                reactantCoeff = 1.0,
                targetName = "کربن دی‌اکسید (CO₂)",
                targetMolarMass = 44.0,
                targetCoeff = 1.0,
                defaultKnownUnit = StoichUnit.GRAM,
                defaultTargetUnit = StoichUnit.LITER_STP
            ),
            KonkurReactionPreset(
                title = "تولید آهن در کوره بلند",
                equation = "Fe₂O₃ + 3CO ➔ 2Fe + 3CO₂",
                reactantName = "آهن(III) اکسید (Fe₂O₃)",
                reactantMolarMass = 160.0,
                reactantCoeff = 1.0,
                targetName = "آهن خالص (Fe)",
                targetMolarMass = 56.0,
                targetCoeff = 2.0,
                defaultKnownUnit = StoichUnit.GRAM,
                defaultTargetUnit = StoichUnit.GRAM
            ),
            KonkurReactionPreset(
                title = "تجزیه پتاسیم کلرات (تولید O₂)",
                equation = "2KClO₃ ➔ 2KCl + 3O₂",
                reactantName = "پتاسیم کلرات (KClO₃)",
                reactantMolarMass = 122.5,
                reactantCoeff = 2.0,
                targetName = "گاز اکسیژن (O₂)",
                targetMolarMass = 32.0,
                targetCoeff = 3.0,
                defaultKnownUnit = StoichUnit.GRAM,
                defaultTargetUnit = StoichUnit.LITER_STP
            ),
            KonkurReactionPreset(
                title = "فرایند هابر (سنتز آمونیاک)",
                equation = "N₂ + 3H₂ ➔ 2NH₃",
                reactantName = "گاز نیتروژن (N₂)",
                reactantMolarMass = 28.0,
                reactantCoeff = 1.0,
                targetName = "گاز آمونیاک (NH₃)",
                targetMolarMass = 17.0,
                targetCoeff = 2.0,
                defaultKnownUnit = StoichUnit.LITER_STP,
                defaultTargetUnit = StoichUnit.GRAM
            ),
            KonkurReactionPreset(
                title = "سوختن گاز متان",
                equation = "CH₄ + 2O₂ ➔ CO₂ + 2H₂O",
                reactantName = "گاز متان (CH₄)",
                reactantMolarMass = 16.0,
                reactantCoeff = 1.0,
                targetName = "کربن دی‌اکسید (CO₂)",
                targetMolarMass = 44.0,
                targetCoeff = 1.0,
                defaultKnownUnit = StoichUnit.GRAM,
                defaultTargetUnit = StoichUnit.LITER_STP
            ),
            KonkurReactionPreset(
                title = "واکنش سدیم با آب",
                equation = "2Na + 2H₂O ➔ 2NaOH + H₂",
                reactantName = "فلز سدیم (Na)",
                reactantMolarMass = 23.0,
                reactantCoeff = 2.0,
                targetName = "گاز هیدروژن (H₂)",
                targetMolarMass = 2.0,
                targetCoeff = 1.0,
                defaultKnownUnit = StoichUnit.GRAM,
                defaultTargetUnit = StoichUnit.LITER_STP
            )
        )
    }

    // Input States
    var selectedPresetTitle by remember { mutableStateOf(presets[0].title) }
    var currentEquation by remember { mutableStateOf(presets[0].equation) }

    // Known Substance (A)
    var knownName by remember { mutableStateOf(presets[0].reactantName) }
    var knownAmountInput by remember { mutableStateOf("54") }
    var knownUnit by remember { mutableStateOf(presets[0].defaultKnownUnit) }
    var knownMolarMassInput by remember { mutableStateOf(presets[0].reactantMolarMass.toString()) }
    var knownCoeffInput by remember { mutableStateOf(presets[0].reactantCoeff.toInt().toString()) }
    var purityInput by remember { mutableStateOf("100") } // درصد خلوص

    // Target Substance (B)
    var targetName by remember { mutableStateOf(presets[0].targetName) }
    var targetUnit by remember { mutableStateOf(presets[0].defaultTargetUnit) }
    var targetMolarMassInput by remember { mutableStateOf(presets[0].targetMolarMass.toString()) }
    var targetCoeffInput by remember { mutableStateOf(presets[0].targetCoeff.toInt().toString()) }
    var yieldInput by remember { mutableStateOf("100") } // بازده درصدی واکنش

    // Safe robust numeric parsing
    fun safeParse(text: String, fallback: Double = 1.0): Double {
        if (text.isBlank()) return fallback
        val cleaned = text.trim()
            .replace('۰', '0').replace('۱', '1').replace('۲', '2').replace('۳', '3').replace('۴', '4')
            .replace('۵', '5').replace('۶', '6').replace('۷', '7').replace('۸', '8').replace('۹', '9')
            .replace('٠', '0').replace('١', '1').replace('٢', '2').replace('٣', '3').replace('٤', '4')
            .replace('٥', '5').replace('٦', '6').replace('٧', '7').replace('٨', '8').replace('٩', '9')
            .replace('٫', '.').replace('/', '.').replace(',', '.')
        return cleaned.toDoubleOrNull() ?: fallback
    }

    val knownAmount = safeParse(knownAmountInput, 0.0)
    val knownMolarMass = safeParse(knownMolarMassInput, 1.0).coerceAtLeast(0.001)
    val knownCoeff = safeParse(knownCoeffInput, 1.0).coerceAtLeast(0.001)
    val purityPercent = safeParse(purityInput, 100.0).coerceIn(0.0, 100.0)

    val targetMolarMass = safeParse(targetMolarMassInput, 1.0).coerceAtLeast(0.001)
    val targetCoeff = safeParse(targetCoeffInput, 1.0).coerceAtLeast(0.001)
    val yieldPercent = safeParse(yieldInput, 100.0).coerceIn(0.0, 100.0)

    // Calculation logic
    // 1. Calculate pure moles of A reacted
    val pureMolesA = when (knownUnit) {
        StoichUnit.GRAM -> (knownAmount * (purityPercent / 100.0)) / knownMolarMass
        StoichUnit.MOLE -> knownAmount * (purityPercent / 100.0)
        StoichUnit.LITER_STP -> (knownAmount * (purityPercent / 100.0)) / 22.4
        StoichUnit.ML_STP -> (knownAmount * (purityPercent / 100.0)) / 22400.0
        StoichUnit.PARTICLES -> (knownAmount * (purityPercent / 100.0)) / 6.022 // in 10^23
    }

    // 2. Moles of B produced taking stoichiometric ratio & yield into account
    val molesBProduced = pureMolesA * (targetCoeff / knownCoeff) * (yieldPercent / 100.0)

    // 3. Convert moles of B to the target unit
    val resultValue = when (targetUnit) {
        StoichUnit.GRAM -> molesBProduced * targetMolarMass
        StoichUnit.MOLE -> molesBProduced
        StoichUnit.LITER_STP -> molesBProduced * 22.4
        StoichUnit.ML_STP -> molesBProduced * 22400.0
        StoichUnit.PARTICLES -> molesBProduced * 6.022 // coefficient of 10^23
    }

    val formattedResult = if (resultValue < 0.0001 && resultValue > 0) {
        String.format("%.6f", resultValue)
    } else if (resultValue % 1.0 == 0.0) {
        String.format("%.0f", resultValue)
    } else {
        String.format("%.3f", resultValue)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 4.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // ۱. انتخاب سریع واکنش‌های پرتکرار کنکور
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                border = BorderStroke(1.dp, AppleOrange.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⚡ انتخاب واکنش پرتکرار کنکور (یک‌کلیک برای حل):",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppleOrange
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(presets) { preset ->
                            val isSelected = selectedPresetTitle == preset.title
                            Surface(
                                onClick = {
                                    selectedPresetTitle = preset.title
                                    currentEquation = preset.equation
                                    knownName = preset.reactantName
                                    knownMolarMassInput = if (preset.reactantMolarMass % 1.0 == 0.0) preset.reactantMolarMass.toInt().toString() else preset.reactantMolarMass.toString()
                                    knownCoeffInput = preset.reactantCoeff.toInt().toString()
                                    knownUnit = preset.defaultKnownUnit

                                    targetName = preset.targetName
                                    targetMolarMassInput = if (preset.targetMolarMass % 1.0 == 0.0) preset.targetMolarMass.toInt().toString() else preset.targetMolarMass.toString()
                                    targetCoeffInput = preset.targetCoeff.toInt().toString()
                                    targetUnit = preset.defaultTargetUnit
                                },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) AppleOrange else DarkSurface2,
                                border = BorderStroke(1.dp, if (isSelected) AppleOrange else DarkSeparator)
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)) {
                                    Text(
                                        text = preset.title,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else TextPrimary
                                    )
                                    Text(
                                        text = preset.equation,
                                        fontSize = 9.sp,
                                        color = if (isSelected) Color.White.copy(alpha = 0.85f) else TextTertiary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = DarkSurface2,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "معادله واکنش: ",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = currentEquation,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MetallicCyan
                            )
                        }
                    }
                }
            }
        }

        // ۲. کارت ماده معلوم مسئله (Substance A)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                border = BorderStroke(1.dp, AppleBlue.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = AppleBlue.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.size(24.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = "A", fontWeight = FontWeight.Bold, color = AppleBlue, fontSize = 12.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ماده معلوم مسئله ($knownName)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = AppleBlue
                            )
                        }

                        // چیپ‌های سریع درصد خلوص
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "خلوص:", fontSize = 10.sp, color = TextTertiary)
                            Spacer(modifier = Modifier.width(4.dp))
                            listOf("100", "90", "80").forEach { p ->
                                val isSel = purityInput == p
                                Surface(
                                    onClick = { purityInput = p },
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (isSel) AppleBlue else DarkSurface2,
                                    modifier = Modifier.padding(horizontal = 2.dp)
                                ) {
                                    Text(
                                        text = "${PersianUtils.toPersianDigits(p)}٪",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) Color.White else TextSecondary,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // ردیف فیلدهای مقدار، ضریب، جرم مولی و خلوص
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedTextField(
                            value = knownAmountInput,
                            onValueChange = { knownAmountInput = it },
                            label = { Text("مقدار ماده", fontSize = 10.sp) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = DarkSurface2,
                                unfocusedContainerColor = DarkSurface2,
                                focusedBorderColor = AppleBlue,
                                unfocusedBorderColor = DarkSeparator,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier.weight(1.2f)
                        )

                        OutlinedTextField(
                            value = knownMolarMassInput,
                            onValueChange = { knownMolarMassInput = it },
                            label = { Text("جرم مولی (M)", fontSize = 10.sp) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = DarkSurface2,
                                unfocusedContainerColor = DarkSurface2,
                                focusedBorderColor = AppleBlue,
                                unfocusedBorderColor = DarkSeparator,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier.weight(1.1f)
                        )

                        OutlinedTextField(
                            value = knownCoeffInput,
                            onValueChange = { knownCoeffInput = it },
                            label = { Text("ضریب (a)", fontSize = 10.sp) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = DarkSurface2,
                                unfocusedContainerColor = DarkSurface2,
                                focusedBorderColor = AppleBlue,
                                unfocusedBorderColor = DarkSeparator,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier.weight(0.7f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // انتخاب یکای ماده معلوم
                    Text(text = "یکای مقدار وارد شده معلوم:", fontSize = 10.sp, color = TextTertiary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkSurface2)
                            .padding(2.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        StoichUnit.values().forEach { unit ->
                            val isSel = knownUnit == unit
                            Surface(
                                onClick = { knownUnit = unit },
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSel) AppleBlue else Color.Transparent,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = unit.label,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSel) Color.White else TextSecondary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 5.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // ۳. کارت ماده مجهول مسئله (Substance B) و بازده واکنش
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                border = BorderStroke(1.dp, AppleGreen.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = AppleGreen.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.size(24.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = "B", fontWeight = FontWeight.Bold, color = AppleGreen, fontSize = 12.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ماده مجهول خواسته شده ($targetName)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = AppleGreen
                            )
                        }

                        // چیپ‌های سریع بازده درصدی
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "بازده:", fontSize = 10.sp, color = TextTertiary)
                            Spacer(modifier = Modifier.width(4.dp))
                            listOf("100", "80", "75").forEach { y ->
                                val isSel = yieldInput == y
                                Surface(
                                    onClick = { yieldInput = y },
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (isSel) AppleGreen else DarkSurface2,
                                    modifier = Modifier.padding(horizontal = 2.dp)
                                ) {
                                    Text(
                                        text = "${PersianUtils.toPersianDigits(y)}٪",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) Color.White else TextSecondary,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedTextField(
                            value = targetMolarMassInput,
                            onValueChange = { targetMolarMassInput = it },
                            label = { Text("جرم مولی مجهول (M)", fontSize = 10.sp) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = DarkSurface2,
                                unfocusedContainerColor = DarkSurface2,
                                focusedBorderColor = AppleGreen,
                                unfocusedBorderColor = DarkSeparator,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier.weight(1.2f)
                        )

                        OutlinedTextField(
                            value = targetCoeffInput,
                            onValueChange = { targetCoeffInput = it },
                            label = { Text("ضریب مجهول (b)", fontSize = 10.sp) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = DarkSurface2,
                                unfocusedContainerColor = DarkSurface2,
                                focusedBorderColor = AppleGreen,
                                unfocusedBorderColor = DarkSeparator,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier.weight(0.8f)
                        )

                        OutlinedTextField(
                            value = yieldInput,
                            onValueChange = { yieldInput = it },
                            label = { Text("بازده R٪", fontSize = 10.sp) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = DarkSurface2,
                                unfocusedContainerColor = DarkSurface2,
                                focusedBorderColor = AppleGreen,
                                unfocusedBorderColor = DarkSeparator,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier.weight(0.9f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // انتخاب یکای خواسته شده برای مجهول
                    Text(text = "یکای هدف خواسته شده در سوال:", fontSize = 10.sp, color = TextTertiary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkSurface2)
                            .padding(2.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        StoichUnit.values().forEach { unit ->
                            val isSel = targetUnit == unit
                            Surface(
                                onClick = { targetUnit = unit },
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSel) AppleGreen else Color.Transparent,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = unit.label,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSel) Color.White else TextSecondary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 5.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // ۴. کارت پاسخ نهایی طلایی و فرمول کسر تبدیل کنکور
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface2),
                border = BorderStroke(1.5.dp, AppleYellow),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🎯 پاسخ نهایی مسئله استوکیومتری:",
                            fontWeight = FontWeight.Bold,
                            color = AppleYellow,
                            fontSize = 13.sp
                        )
                        Surface(
                            color = AppleGreen.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "محاسبه دقیق کنکور",
                                color = AppleGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // باکس بزرگ مقدار خروجی
                    Surface(
                        color = DarkSurface1,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, AppleYellow.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "مقدار حاصل از $targetName:",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${PersianUtils.toPersianDigits(formattedResult)} ${targetUnit.label}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = AppleYellow
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "معادل ${PersianUtils.toPersianDigits(String.format("%.4f", molesBProduced))} مول ماده $targetName",
                                fontSize = 11.sp,
                                color = MetallicCyan
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // فرمول طلایی کسر تستی کنکور با جایگذاری دقیق عددی
                    Text(
                        text = "💡 جایگذاری عددی در کسر تبدیل طلایی کنکور:",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 11.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Surface(
                        color = DarkSurface3,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            val knownDenom = when (knownUnit) {
                                StoichUnit.GRAM -> "${PersianUtils.toPersianDigits(knownCoeff.toInt())} × ${PersianUtils.toPersianDigits(knownMolarMass)}"
                                StoichUnit.MOLE -> "${PersianUtils.toPersianDigits(knownCoeff.toInt())} × ۱"
                                StoichUnit.LITER_STP -> "${PersianUtils.toPersianDigits(knownCoeff.toInt())} × ۲۲.۴"
                                StoichUnit.ML_STP -> "${PersianUtils.toPersianDigits(knownCoeff.toInt())} × ۲۲۴۰۰"
                                StoichUnit.PARTICLES -> "${PersianUtils.toPersianDigits(knownCoeff.toInt())} × ۶.۰۲۲×۱۰²³"
                            }
                            val targetDenom = when (targetUnit) {
                                StoichUnit.GRAM -> "${PersianUtils.toPersianDigits(targetCoeff.toInt())} × ${PersianUtils.toPersianDigits(targetMolarMass)}"
                                StoichUnit.MOLE -> "${PersianUtils.toPersianDigits(targetCoeff.toInt())} × ۱"
                                StoichUnit.LITER_STP -> "${PersianUtils.toPersianDigits(targetCoeff.toInt())} × ۲۲.۴"
                                StoichUnit.ML_STP -> "${PersianUtils.toPersianDigits(targetCoeff.toInt())} × ۲۲۴۰۰"
                                StoichUnit.PARTICLES -> "${PersianUtils.toPersianDigits(targetCoeff.toInt())} × ۶.۰۲۲×۱۰²³"
                            }

                            Text(
                                text = "کسر معلوم: (${PersianUtils.toPersianDigits(knownAmount)} × ${PersianUtils.toPersianDigits(purityPercent)}٪ خلوص) ÷ ($knownDenom)",
                                fontSize = 11.sp,
                                color = AppleBlue
                            )
                            if (yieldPercent < 100.0) {
                                Text(
                                    text = "ضریب بازده: × (${PersianUtils.toPersianDigits(yieldPercent)} ÷ ۱۰۰)",
                                    fontSize = 11.sp,
                                    color = AppleOrange
                                )
                            }
                            Text(
                                text = "کسر مجهول: x ÷ ($targetDenom)",
                                fontSize = 11.sp,
                                color = AppleGreen
                            )
                            Text(
                                text = "➔ مقدار مجهول x = ${PersianUtils.toPersianDigits(formattedResult)} ${targetUnit.shortUnit}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppleYellow
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * ۲. تبدیل جامع یکاها و شرایط STP برای یک ماده
 */
@Composable
fun SingleSubstanceStoichiometryView() {
    var inputMode by remember { mutableStateOf(StoichUnit.MOLE) }
    var quantityInput by remember { mutableStateOf("1") }
    var molarMassInput by remember { mutableStateOf("98") }
    var coefficientInput by remember { mutableStateOf("1") }
    var selectedCompoundName by remember { mutableStateOf<String?>("H2SO4") }

    val quickCompounds = remember {
        listOf(
            QuickCompound("سولفوریک اسید", "H2SO4", 98.0),
            QuickCompound("کلسیم کربنات", "CaCO3", 100.0),
            QuickCompound("سدیم کلرید", "NaCl", 58.5),
            QuickCompound("کربن دی‌اکسید", "CO2", 44.0),
            QuickCompound("آب", "H2O", 18.0),
            QuickCompound("گاز اکسیژن", "O2", 32.0),
            QuickCompound("آلومینیم", "Al", 27.0),
            QuickCompound("آهن (III) اکسید", "Fe2O3", 160.0),
            QuickCompound("سدیم هیدروکسید", "NaOH", 40.0),
            QuickCompound("گلوکز", "C6H12O6", 180.0),
            QuickCompound("متان", "CH4", 16.0)
        )
    }

    fun safeParse(text: String, fallback: Double = 1.0): Double {
        if (text.isBlank()) return fallback
        val cleaned = text.trim()
            .replace('۰', '0').replace('۱', '1').replace('۲', '2').replace('۳', '3').replace('۴', '4')
            .replace('۵', '5').replace('۶', '6').replace('۷', '7').replace('۸', '8').replace('۹', '9')
            .replace('٠', '0').replace('١', '1').replace('٢', '2').replace('٣', '3').replace('٤', '4')
            .replace('٥', '5').replace('٦', '6').replace('٧', '7').replace('٨', '8').replace('٩', '9')
            .replace('٫', '.').replace('/', '.').replace(',', '.')
        return cleaned.toDoubleOrNull() ?: fallback
    }

    val rawQty = safeParse(quantityInput, 0.0)
    val mm = safeParse(molarMassInput, 1.0).coerceAtLeast(0.001)
    val coeff = safeParse(coefficientInput, 1.0).coerceAtLeast(0.001)

    // Calculate moles according to selected input mode
    val moles = when (inputMode) {
        StoichUnit.MOLE -> rawQty
        StoichUnit.GRAM -> rawQty / mm
        StoichUnit.LITER_STP -> rawQty / 22.4
        StoichUnit.ML_STP -> rawQty / 22400.0
        StoichUnit.PARTICLES -> (rawQty * 6.022e23) / 6.022e23
    }

    val grams = moles * mm
    val milligrams = grams * 1000.0
    val gasLitersStp = moles * 22.4
    val gasMillilitersStp = gasLitersStp * 1000.0
    val particlesSci = moles * 6.022 // coefficient of 10^23
    val stoichiometricFraction = moles / coeff

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 4.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // ۱. کارت انتخاب حالت و مقادیر ورودی
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                border = BorderStroke(1.dp, AppleOrange.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "📐 تبدیل یکاهای مول، جرم، حجم STP و تعداد ذرات",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = AppleOrange
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "یکای معلوم را انتخاب کرده و مقدار آن را وارد کنید:",
                        fontSize = 11.5.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // تب‌های انتخاب نوع ورودی
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkSurface2)
                            .padding(3.dp),
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        StoichUnit.values().forEach { mode ->
                            val isSelected = inputMode == mode
                            Surface(
                                onClick = {
                                    inputMode = mode
                                    if (quantityInput.isBlank() || quantityInput == "0") {
                                        quantityInput = "1"
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) AppleOrange else Color.Transparent,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = mode.label,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else TextSecondary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // ردیف فیلدهای ورودی
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = quantityInput,
                            onValueChange = { quantityInput = it },
                            label = { Text(inputMode.label, fontSize = 11.sp) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = DarkSurface2,
                                unfocusedContainerColor = DarkSurface2,
                                focusedBorderColor = AppleOrange,
                                unfocusedBorderColor = DarkSeparator,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier.weight(1.2f)
                        )

                        OutlinedTextField(
                            value = molarMassInput,
                            onValueChange = {
                                molarMassInput = it
                                selectedCompoundName = null
                            },
                            label = { Text("جرم مولی M", fontSize = 11.sp) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = DarkSurface2,
                                unfocusedContainerColor = DarkSurface2,
                                focusedBorderColor = AppleOrange,
                                unfocusedBorderColor = DarkSeparator,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier.weight(1.1f)
                        )

                        OutlinedTextField(
                            value = coefficientInput,
                            onValueChange = { coefficientInput = it },
                            label = { Text("ضریب a", fontSize = 11.sp) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = DarkSurface2,
                                unfocusedContainerColor = DarkSurface2,
                                focusedBorderColor = AppleOrange,
                                unfocusedBorderColor = DarkSeparator,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier.weight(0.7f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(text = "📌 انتخاب ترکیب‌های پرتکرار کنکور:", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(quickCompounds) { qc ->
                            val isSelected = selectedCompoundName == qc.formula
                            Surface(
                                onClick = {
                                    selectedCompoundName = qc.formula
                                    molarMassInput = if (qc.molarMass % 1.0 == 0.0) qc.molarMass.toInt().toString() else qc.molarMass.toString()
                                },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) AppleOrange else DarkSurface2,
                                border = BorderStroke(1.dp, if (isSelected) AppleOrange else DarkSeparator)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = qc.formula,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else TextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "(${PersianUtils.toPersianDigits(qc.molarMass.toInt())})",
                                        fontSize = 9.5.sp,
                                        color = if (isSelected) Color.White.copy(alpha = 0.8f) else TextTertiary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // ۲. نتایج جامع و بی‌درنگ استوکیومتری
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                border = BorderStroke(1.dp, DarkSeparator),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "📊 مقادیر محاسبه‌شده ماده:",
                        fontWeight = FontWeight.Bold,
                        color = MetallicCyan,
                        fontSize = 12.5.sp
                    )

                    StoichiometryResultRow(
                        label = "مقدار مول ماده (n)",
                        value = "${PersianUtils.toPersianDigits(String.format("%.4f", moles))} mol",
                        color = AppleOrange
                    )
                    HorizontalDivider(color = DarkSeparator)

                    StoichiometryResultRow(
                        label = "جرم کل ماده (m = n × M)",
                        value = "${PersianUtils.toPersianDigits(String.format("%.2f", grams))} گرم (${PersianUtils.toPersianDigits(String.format("%.0f", milligrams))} mg)",
                        color = AppleGreen
                    )
                    HorizontalDivider(color = DarkSeparator)

                    StoichiometryResultRow(
                        label = "حجم گاز در شرایط STP (V = n × ۲۲.۴)",
                        value = "${PersianUtils.toPersianDigits(String.format("%.2f", gasLitersStp))} لیتر",
                        color = MetallicCyan
                    )
                    HorizontalDivider(color = DarkSeparator)

                    StoichiometryResultRow(
                        label = "حجم گاز در STP برحسب میلی‌لیتر (mL)",
                        value = "${PersianUtils.toPersianDigits(String.format("%.0f", gasMillilitersStp))} میلی‌لیتر",
                        color = AppleBlue
                    )
                    HorizontalDivider(color = DarkSeparator)

                    StoichiometryResultRow(
                        label = "تعداد ذرات (با عدد آووگادرو)",
                        value = "${PersianUtils.toPersianDigits(String.format("%.3f", particlesSci))} × ۱۰²³ ذره",
                        color = AppleYellow
                    )
                    HorizontalDivider(color = DarkSeparator)

                    StoichiometryResultRow(
                        label = "ارزش کسر تستی کنکور (n / a)",
                        value = "${PersianUtils.toPersianDigits(String.format("%.4f", stoichiometricFraction))}",
                        color = MetallicEmerald
                    )
                }
            }
        }

        // ۳. کسر پیش‌ساخته تستی و راهنما
        item {
            Surface(
                color = DarkSurface2,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, AppleYellow.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "💡 فرمول طلایی کسر تبدیل استوکیومتری در کنکور:", fontWeight = FontWeight.Bold, color = AppleYellow, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "کسر واکنش‌دهنده = کسر فرآورده:\n(جرم ÷ a × M) = (حجم گاز STP ÷ a × ۲۲.۴) = (تعداد ذره ÷ a × ۶.۰۲۲×۱۰²³) = (مول ÷ a)",
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
fun StoichiometryResultRow(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 12.sp, color = TextSecondary)
        Text(text = value, fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = color)
    }
}
