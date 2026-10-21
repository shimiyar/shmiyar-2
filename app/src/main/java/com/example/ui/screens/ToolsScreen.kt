package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Search
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
import com.example.ui.components.UrgencyPromoCountdownBanner
import com.example.ui.theme.*
import com.example.ui.viewmodel.ShimiYarViewModel
import com.example.util.PersianUtils

enum class ToolSubTab(val title: String, val icon: String) {
    MOLAR_MASS("جرم مولی و درصد جرمی", "⚖️"),
    STOICHIOMETRY("استوکیومتری و STP", "📐"),
    PH_CALCULATOR("محاسبه‌گر pH و اسید/باز", "🧪"),
    GOLDEN_FORMULAS("فرمول‌های طلایی کنکور", "⚡"),
    FOCUS_TIMER("تایمر تست‌زنی ۳۵ ثانیه", "⏱️"),
    ELECTROCHEMISTRY("پتانسیل E° و اکسایش", "⚡"),
    SOLUBILITY("انحلال‌پذیری و رسوب‌ها", "💧")
}

@Composable
fun ToolsScreen(
    viewModel: ShimiYarViewModel
) {
    val isPro by viewModel.isPro.collectAsState()
    var activeSubTab by remember { mutableStateOf(ToolSubTab.MOLAR_MASS) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .testTag("tools_screen")
    ) {
        // ۱. نوار تب‌های جعبه‌ابزار
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(ToolSubTab.values()) { tab ->
                val isSelected = activeSubTab == tab
                Surface(
                    onClick = { activeSubTab = tab },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) AppleBlue else DarkSurface2,
                    border = BorderStroke(1.dp, if (isSelected) AppleBlue else DarkSeparator)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = tab.icon, fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = tab.title,
                            fontSize = 11.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else TextSecondary
                        )
                    }
                }
            }
        }

        // ۲. نوار پیشرفت دسترسی ۲۵٪ به جعبه‌ابزار (فقط برای کاربران نسخه رایگان)
        if (!isPro) {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 2.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "دسترسی ۲۵٪ نسخه رایگان به جعبه‌ابزار",
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

        // ۳. بنر ارتقا به عضویت طلایی
        if (!isPro) {
            UrgencyPromoCountdownBanner(
                title = "👑 عضویت ویژه شیمی‌یار طلایی",
                subtitle = "دسترسی نامحدود به موازنه واکنش‌ها، استوکیومتری پیشرفته و آزمون‌های جامع",
                onUpgradeClick = { viewModel.openDailyOfferModal() },
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 2.dp)
            )
        }

        // بخش اسکرول‌شونده محتوای ابزار انتخابی
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            when (activeSubTab) {
                ToolSubTab.MOLAR_MASS -> MolarMassCalculatorView(viewModel)
                ToolSubTab.STOICHIOMETRY -> StoichiometryComponent()
                ToolSubTab.PH_CALCULATOR -> PhCalculatorComponent(onLockedClick = { viewModel.openDailyOfferModal() }, isPro = isPro)
                ToolSubTab.GOLDEN_FORMULAS -> GoldenFormulasComponent(onLockedClick = { viewModel.openDailyOfferModal() }, isPro = isPro)
                ToolSubTab.FOCUS_TIMER -> KonkurTimerComponent(viewModel = viewModel, onLockedClick = { viewModel.openDailyOfferModal() }, isPro = isPro)
                ToolSubTab.ELECTROCHEMISTRY -> ElectrochemistrySeriesView()
                ToolSubTab.SOLUBILITY -> SolubilityRulesView()
            }
        }
    }
}

/**
 * ابزار محاسبه جرم مولی و درصد جرمی عناصر
 */
@Composable
fun MolarMassCalculatorView(viewModel: ShimiYarViewModel) {
    val formulaInput by viewModel.molarMassInput.collectAsState()
    val result by viewModel.molarMassResult.collectAsState()
    val error by viewModel.molarMassError.collectAsState()

    val quickFormulas = listOf(
        "H2SO4", "Ca(OH)2", "Al2(SO4)3", "C6H12O6",
        "KMnO4", "CuSO4.5H2O", "Fe2O3", "HNO3",
        "Ba(OH)2", "C12H22O11", "CH3COOH", "NaCl"
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                border = BorderStroke(1.dp, DarkSeparator),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "⚖️ محاسبه‌گر هوشمند جرم مولی (M) و درصد جرمی",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MetallicCyan
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "فرمول شیمیایی ترکیب مورد نظر را وارد کنید (پشتیبانی از پرانتز و هیدرات‌ها):",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 11.5.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = formulaInput,
                        onValueChange = { viewModel.setMolarMassInput(it) },
                        placeholder = { Text("مثال: H2SO4 یا Al2(SO4)3", color = TextTertiary, fontSize = 12.sp) },
                        trailingIcon = {
                            if (formulaInput.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setMolarMassInput("") }) {
                                    Icon(imageVector = Icons.Default.Clear, contentDescription = "پاک کردن", tint = TextSecondary, modifier = Modifier.size(18.dp))
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DarkSurface2,
                            unfocusedContainerColor = DarkSurface2,
                            focusedBorderColor = AppleBlue,
                            unfocusedBorderColor = DarkSeparator,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "📌 ترکیبات پرتکرار کنکور سراسری:", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(quickFormulas) { f ->
                            Surface(
                                onClick = { viewModel.setMolarMassInput(f) },
                                shape = RoundedCornerShape(8.dp),
                                color = DarkSurface2,
                                border = BorderStroke(1.dp, if (formulaInput == f) AppleBlue else DarkSeparator)
                            ) {
                                Text(
                                    text = f,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (formulaInput == f) AppleBlue else TextSecondary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        if (error != null) {
            item {
                Surface(
                    color = AppleRed.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, AppleRed.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "⚠️ $error",
                        color = AppleRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        result?.let { res ->
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                    border = BorderStroke(1.5.dp, AppleGreen.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "جرم مولی کل ترکیب", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = PersianUtils.toPersianDigits(String.format("%.2f", res.molarMass)),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = AppleGreen
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "گرم بر مول (g/mol)", fontSize = 13.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item {
                Text(
                    text = "📊 تفکیک و درصد جرمی عناصر سازنده:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            items(res.compositions) { comp ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                    border = BorderStroke(1.dp, DarkSeparator),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = AppleBlue.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(text = comp.symbol, fontWeight = FontWeight.Bold, color = AppleBlue, fontSize = 13.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(text = comp.persianName, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 13.sp)
                                    Text(
                                        text = "${PersianUtils.toPersianDigits(comp.count)} اتم × ${PersianUtils.toPersianDigits(String.format("%.2f", comp.atomicMass))} = ${PersianUtils.toPersianDigits(String.format("%.2f", comp.totalMass))} گرم",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }

                            Text(
                                text = "${PersianUtils.toPersianDigits(String.format("%.1f", comp.percentage))}٪",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = AppleYellow
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { (comp.percentage / 100f).toFloat() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = AppleYellow,
                            trackColor = DarkSurface3
                        )
                    }
                }
            }
        }
    }
}

// ماشین‌حساب استوکیومتری و حل‌کننده واکنش‌ها در StoichiometryComponent.kt پیاده‌سازی شده است.

/**
 * جدول سری الکتروشیمیایی و پتانسیل کاهش استاندارد
 */
@Composable
fun ElectrochemistrySeriesView() {
    val series = listOf(
        Triple("F₂ + 2e⁻ ⇌ 2F⁻", "+۲.۸۷ V", "قوی‌ترین اکسنده کنکور (بالاترین تمایل کاهش)"),
        Triple("S₂O₈²⁻ + 2e⁻ ⇌ 2SO₄²⁻", "+۲.۰۱ V", "اکسنده بسیار پرقدرت"),
        Triple("MnO₄⁻ + 8H⁺ + 5e⁻ ⇌ Mn²⁺ + 4H₂O", "+۱.۵۱ V", "پرمنگنات در محیط اسیدی (رنگ بنفش به بی‌رنگ)"),
        Triple("Au³⁺ + 3e⁻ ⇌ Au", "+۱.۵۰ V", "طلا (فلز بسیار کم‌واکنش)"),
        Triple("Cl₂ + 2e⁻ ⇌ 2Cl⁻", "+۱.۳۶ V", "گاز کلر (اکسنده قوی)"),
        Triple("Cr₂O₇²⁻ + 14H⁺ + 6e⁻ ⇌ 2Cr³⁺ + 7H₂O", "+۱.۳۳ V", "دی‌کرومات در محیط اسیدی (نارنجی به سبز)"),
        Triple("O₂ + 4H⁺ + 4e⁻ ⇌ 2H₂O", "+۱.۲۳ V", "نیم‌واکنش کاهش اکسیژن در زنگ زدن آهن"),
        Triple("Br₂ + 2e⁻ ⇌ 2Br⁻", "+۱.۰۷ V", "مایع قهوه‌ای برم"),
        Triple("Ag⁺ + e⁻ ⇌ Ag", "+۰.۸۰ V", "یون نقره"),
        Triple("Fe³⁺ + e⁻ ⇌ Fe²⁺", "+۰.۷۷ V", "کاهش آهن سه به دو"),
        Triple("I₂ + 2e⁻ ⇌ 2I⁻", "+۰.۵۴ V", "ید بنفش"),
        Triple("Cu²⁺ + 2e⁻ ⇌ Cu", "+۰.۳۴ V", "مس (کاتد در سلول دانیل)"),
        Triple("2H⁺ + 2e⁻ ⇌ H₂", "۰.۰۰ V", "الکترود استاندارد هیدروژن (SHE) مبنای مقایسه"),
        Triple("Pb²⁺ + 2e⁻ ⇌ Pb", "-۰.۱۳ V", "سرب"),
        Triple("Sn²⁺ + 2e⁻ ⇌ Sn", "-۰.۱۴ V", "قلع (روکش قوطی حلبی)"),
        Triple("Ni²⁺ + 2e⁻ ⇌ Ni", "-۰.۲۵ V", "نیکل"),
        Triple("Fe²⁺ + 2e⁻ ⇌ Fe", "-۰.۴۴ V", "آهن (خوردگی و اکسایش)"),
        Triple("Zn²⁺ + 2e⁻ ⇌ Zn", "-۰.۷۶ V", "روی (آند در سلول دانیل و حفاظت کاتدی)"),
        Triple("Al³⁺ + 3e⁻ ⇌ Al", "-۱.۶۶ V", "آلومینیم (روش هال)"),
        Triple("Mg²⁺ + 2e⁻ ⇌ Mg", "-۲.۳۷ V", "منیزیم"),
        Triple("Na⁺ + e⁻ ⇌ Na", "-۲.۷۱ V", "سدیم (فلز قلیایی فعال)"),
        Triple("K⁺ + e⁻ ⇌ K", "-۲.۹۳ V", "پتاسیم"),
        Triple("Li⁺ + e⁻ ⇌ Li", "-۳.۰۴ V", "قوی‌ترین کاهنده کنکور (پایین‌ترین پتانسیل)")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "⚡ سری پتانسیل کاهش استاندارد (E°) کنکور", fontWeight = FontWeight.Bold, color = MetallicCyan, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "هر چه E° مثبت‌تر باشد، تمایل به کاهش (اکسندگی گونه) بیشتر است. گونه با E° پایین‌تر نقش آند و گونه با E° بالاتر نقش کاتد را ایفا می‌کند.",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        items(series) { (reaction, potential, note) ->
            val isPositive = potential.startsWith("+")
            val isZero = potential.startsWith("۰")
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                border = BorderStroke(1.dp, if (isZero) AppleYellow else DarkSeparator),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 9.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = reaction, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 12.sp)
                        Text(text = note, color = TextTertiary, fontSize = 10.5.sp)
                    }
                    Surface(
                        color = when {
                            isZero -> AppleYellow.copy(alpha = 0.2f)
                            isPositive -> AppleGreen.copy(alpha = 0.2f)
                            else -> AppleOrange.copy(alpha = 0.2f)
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = potential,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                isZero -> AppleYellow
                                isPositive -> AppleGreen
                                else -> AppleOrange
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * اطلس انحلال‌پذیری و رسوب‌ها
 */
@Composable
fun SolubilityRulesView() {
    val rules = listOf(
        Triple("همیشه محلول در آب (بدون استثنا)", "نیترات‌ها (NO₃⁻)، استات‌ها (CH₃COO⁻)، یون‌های فلزات قلیایی (Li⁺, Na⁺, K⁺) و آمونیوم (NH₄⁺)", AppleGreen),
        Triple("کلریدها، برمیدها و یدیدها (Cl⁻, Br⁻, I⁻)", "اکثراً محلول هستند مگر در کنار یون‌های Ag⁺, Pb²⁺, Hg₂²⁺ که رسوب می‌دهند (مانند AgCl سفید و PbI₂ زرد).", AppleBlue),
        Triple("سولفات‌ها (SO₄²⁻)", "اکثراً محلول هستند مگر با کاتیون‌های Ba²⁺, Ca²⁺, Pb²⁺ که رسوب می‌دهند (مانند BaSO₄ سفید).", MetallicCyan),
        Triple("هیدروکسیدها (OH⁻)", "اکثراً نامحلول هستند مگر در کنار فلزات قلیایی و Ba²⁺ و Ca²⁺ (کم‌محلول). هیدروکسید آهن (II) سبز و آهن (III) قهوه‌ای است.", AppleOrange),
        Triple("کربنات‌ها و فسفات‌ها (CO₃²⁻, PO₄³⁻)", "اکثراً نامحلول و رسوب هستند مگر با فلزات قلیایی و آمونیوم.", MetallicRose),
        Triple("سولفیدها (S²⁻)", "اکثر فلزات واسطه رسوب‌های سولفیدی تیره می‌دهند (مانند CuS, FeS, PbS رسوب‌های سیاه).", AppleYellow)
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(rules) { (category, description, color) ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                border = BorderStroke(1.dp, color.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(color)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = category, fontWeight = FontWeight.Bold, color = color, fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = description, color = TextPrimary, fontSize = 11.5.sp, lineHeight = 20.sp)
                }
            }
        }
    }
}
