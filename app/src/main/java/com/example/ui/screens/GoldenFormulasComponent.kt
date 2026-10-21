package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.util.PersianUtils

data class GoldenFormula(
    val id: String,
    val title: String,
    val category: String,
    val formula: String,
    val description: String,
    val konkurTrap: String,
    val example: String,
    val isVipOnly: Boolean = false
)

@Composable
fun GoldenFormulasComponent(
    onLockedClick: () -> Unit,
    isPro: Boolean
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("همه") }
    var copiedFormulaId by remember { mutableStateOf<String?>(null) }

    val categories = listOf(
        "همه",
        "استوکیومتری",
        "محلول‌ها و غلظت",
        "گازها و STP",
        "گرماشیمی",
        "اسید و باز",
        "الکتروشیمی",
        "شیمی آلی"
    )

    val allFormulas = remember {
        listOf(
            GoldenFormula(
                id = "gf_1",
                title = "کسرهای پیش‌ساخته استوکیومتری سرعتی",
                category = "استوکیومتری",
                formula = "mol / a = m / (a × M) = V(STP) / (a × ۲۲.۴) = (M' × V) / a = N / (a × N_A)",
                description = "تبدیل تمام کمیت‌های استوکیومتری (گرم، مول، تعداد ذره، حجم گاز در STP و حجم محلول) بدون نیاز به محاسبات سنتی چندمرحله‌ای.",
                konkurTrap = "ضریب موازنه (a) در مخرج همه کسرها ضرب می‌شود! در کسر محلول (M'×V)، حجم باید برحسب لیتر باشد.",
                example = "از واکنش ۵.۴g آلومینیم با هیدروکلریک اسید، چند لیتر H2 در STP تولید می‌شود؟ کسر Al را با کسر H2 مساوی قرار دهید."
            ),
            GoldenFormula(
                id = "gf_2",
                title = "فرمول طلایی درصد خلوص و بازده درصدی",
                category = "استوکیومتری",
                formula = "مقدار خالص = نمونه × (R / ۱۰۰) | مقدار فرآورده = مقدار نظری × (P / ۱۰۰)",
                description = "درصد خلوص (R) در صورت کسر ماده واکنش‌دهنده، و بازده درصدی (P) در صورت کسر فرآورده ضرب می‌شود.",
                konkurTrap = "ناخالصی‌ها در واکنش شرکت نمی‌کنند؛ بازده درصدی کمتر از ۱۰۰٪ به معنای اتلاف یا واکنش‌های جانبی است.",
                example = "برای یک نمونه سنگ معدن با خلوص ۸۰٪، مستقیماً ضریب ۰.۸ را در جرم واکنش‌دهنده اعمال کنید."
            ),
            GoldenFormula(
                id = "gf_3",
                title = "رابطه سریع غلظت مولی و درصد جرمی (فرمول ۱۰ad/M)",
                category = "محلول‌ها و غلظت",
                formula = "M = (۱۰ × a × d) / M_w",
                description = "محاسبه مستقیم غلظت مولی (M) با داشتن درصد جرمی (a)، چگالی محلول (d برحسب g/mL) و جرم مولی حل‌شونده (M_w).",
                konkurTrap = "چگالی باید برحسب g/mL یا g/cm³ باشد. اگر چگالی برحسب kg/m³ یا g/L بود، بر ۱۰۰۰ تقسیم کنید!",
                example = "اسید سولفوریک ۹۸٪ با چگالی ۱.۸۴g/mL: M = (10 × 98 × 1.84) / 98 = ۱۸.۴ مولار."
            ),
            GoldenFormula(
                id = "gf_4",
                title = "فرمول غلظت برحسب ppm (قسمت در میلیون)",
                category = "محلول‌ها و غلظت",
                formula = "ppm = (جرم حل‌شونده / جرم کل محلول) × ۱۰⁶ = (mg حل‌شونده / kg محلول)",
                description = "برای محلول‌های بسیار رقیق آبی (چگالی ≈ ۱g/mL)، هر ppm معادل ۱ میلی‌گرم در لیتر محلول است.",
                konkurTrap = "واحد جرم حل‌شونده و جرم محلول باید یکسان باشد (هر دو گرم یا هر دو میلی‌گرم).",
                example = "وجود ۳.۵mg یون کلسیم در ۵۰۰g آب: ppm = (3.5 × 10⁻³ / 500) × 10⁶ = ۷ ppm."
            ),
            GoldenFormula(
                id = "gf_5",
                title = "چگالی گازها و قانون عمومی گاز ایده‌آل",
                category = "گازها و STP",
                formula = "در STP: d = M_w / ۲۲.۴ | در هر شرایط: PM = dRT یا (P₁V₁)/T₁ = (P₂V₂)/T₂",
                description = "چگالی گاز در STP تنها به جرم مولی آن وابسته است. سنگین‌ترین گاز در هوا، گازی با بیشترین جرم مولی است.",
                konkurTrap = "دما در تمامی محاسبات گازها باید برحسب کلوین (K = °C + ۲۷۳) باشد نه سلسیوس!",
                example = "چگالی گاز اکسیژن O2 در شرایط STP: d = 32 / 22.4 ≈ ۱.۴۳ g/L."
            ),
            GoldenFormula(
                id = "gf_6",
                title = "محاسبه گرما و ظرفیت گرمایی مولی و ویژه",
                category = "گرماشیمی",
                formula = "Q = m × c × ΔT = C × ΔT = n × C_m × ΔT",
                description = "ارتباط گرمای مبادله شده با جرم ماده (m)، ظرفیت گرمایی ویژه (c برحسب J/g.°C) و تغییر دما (ΔT).",
                konkurTrap = "آب بیشترین ظرفیت گرمایی ویژه را میان مایعات دارد (c = ۴.۱۸۴ J/g.°C). تغییر دما برحسب سلسیوس با کلوین برابر است (ΔT_C = ΔT_K).",
                example = "گرمای لازم برای گرم کردن ۲۰۰g آب از ۲۵ به ۷۵ درجه: Q = 200 × 4.18 × 50 = ۴۱,۸۰۰ ژول = ۴۱.۸ کیلوژول."
            ),
            GoldenFormula(
                id = "gf_7",
                title = "محاسبه آنتالپی واکنش از انرژی پیوند",
                category = "گرماشیمی",
                formula = "ΔH = مجموع D(پیوندهای شکسته شده) - مجموع D(پیوندهای تشکیل شده)",
                description = "شکستن پیوند گرماگیر (+) و تشکیل پیوند گرماده (-) است. فرمول انرژی پیوند برعکس آنتالپی تشکیل است!",
                konkurTrap = "تعداد پیوندها را در ضریب استوکیومتری ماده ضرب کنید! مثلاً در متان CH4 چهار پیوند C-H وجود دارد.",
                example = "برای واکنش CH4 + 2O2 -> CO2 + 2H2O تمام پیوندهای دوطرف را بشمارید."
            ),
            GoldenFormula(
                id = "gf_8",
                title = "فرمول سریع غلظت یون هیدرونیوم و درجه یونش",
                category = "اسید و باز",
                formula = "[H⁺] = M × α | K_a = (M × α²) / (۱ - α) ≈ M × α² | [H⁺] ≈ √(K_a × M)",
                description = "برای اسیدهای ضعیف با درجه یونش کوچک‌تر از ۰.۰۵ (یا کمتر از ۵٪)، می‌توان از تقریب ۱ - α ≈ ۱ استفاده کرد.",
                konkurTrap = "تقریب فوق برای اسیدهایی با Ka بزرگ یا محلول‌های بسیار رقیق معتبر نیست!",
                example = "اگر اسید استیک ۰.۱ مولار با Ka = 10⁻⁵ باشد: [H⁺] = √(10⁻⁵ × 0.1) = 10⁻³ مولار و pH = ۳."
            ),
            GoldenFormula(
                id = "gf_9",
                title = "پتانسیل استاندارد سلول گالوانی (emf) و خودبه‌خودی بودن",
                category = "الکتروشیمی",
                formula = "E°_cell = E°_cathode - E°_anode | اگر E°_cell > ۰ باشد: واکنش خودبه‌خودی است",
                description = "گونه با E° مثبت‌تر در نقش کاتد کاهش می‌یابد؛ گونه با E° منفی‌تر در نقش آند اکسید می‌شود.",
                konkurTrap = "پتانسیل کاهش یک ویژگی شدتی است؛ با ضرب کردن ضرایب معادله، مقدار E° هرگز تغییر نمی‌کند و ضرب نمی‌شود!",
                example = "سلول روی-مس: E°_cell = +0.34 - (-0.76) = +۱.۱۰ V."
            ),
            GoldenFormula(
                id = "gf_10",
                title = "فرمول عمومی هیدروکربن‌ها و مشتقات اکسیژن‌دار",
                category = "شیمی آلی",
                formula = "آلکان: CₙH₂ₙ₊₂ | آلکن: CₙH₂ₙ | آلکین: CₙH₂ₙ₋₂ | الکل/اتر: CₙH₂ₙ₊₂O | کربوکسیلیک اسید/استر: CₙH₂ₙO₂",
                description = "تشخیص فوری نوع ترکیب و تعداد پیوندهای دوگانه یا حلقه‌ها از روی تعداد کربن و هیدروژن در تست‌های کنکور.",
                konkurTrap = "آلکن‌ها با سیکلوآلکان‌ها و الکل‌ها با اترها همپار (ایزومر) هستند و فرمول مولکولی یکسانی دارند.",
                example = "فرمول C4H8 می‌تواند بوتن یا سیکلوبوتان باشد و سیرنشده با هیدروژن اضافه است."
            )
        )
    }

    val filteredFormulas = remember(searchQuery, selectedCategory) {
        allFormulas.filter { f ->
            val matchCat = if (selectedCategory == "همه") true else f.category == selectedCategory
            val matchQuery = if (searchQuery.isBlank()) true else {
                f.title.contains(searchQuery, ignoreCase = true) ||
                f.formula.contains(searchQuery, ignoreCase = true) ||
                f.description.contains(searchQuery, ignoreCase = true) ||
                f.konkurTrap.contains(searchQuery, ignoreCase = true)
            }
            matchCat && matchQuery
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("golden_formulas_component"),
        contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // ۱. جستجو و فیلتر دسته‌بندی
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                border = BorderStroke(1.dp, DarkSeparator),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "⚡ بانک فرمول‌های طلایی و میانبرهای کنکور",
                        fontWeight = FontWeight.Bold,
                        color = AppleYellow,
                        fontSize = 13.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "روابط تستی برای حل مسائل زیر ۴۰ ثانیه همراه با دام‌های طراحان:",
                        fontSize = 11.5.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("جستجو در فرمول‌ها، مباحث و دام‌های تستی...", fontSize = 11.5.sp, color = TextTertiary) },
                        leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = AppleYellow, modifier = Modifier.size(18.dp)) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(imageVector = Icons.Default.Clear, contentDescription = "پاک کردن", tint = TextSecondary, modifier = Modifier.size(16.dp))
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DarkSurface2,
                            unfocusedContainerColor = DarkSurface2,
                            focusedBorderColor = AppleYellow,
                            unfocusedBorderColor = DarkSeparator,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(categories) { cat ->
                            val isSelected = selectedCategory == cat
                            Surface(
                                onClick = { selectedCategory = cat },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) AppleYellow else DarkSurface2,
                                border = BorderStroke(1.dp, if (isSelected) AppleYellow else DarkSeparator)
                            ) {
                                Text(
                                    text = cat,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.Black else TextSecondary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // ۲. لیست کارت‌های فرمول طلایی
        items(filteredFormulas) { item ->
            val isCopied = copiedFormulaId == item.id
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                border = BorderStroke(1.dp, MetallicBorder),
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
                                color = AppleYellow.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = item.category,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AppleYellow,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = item.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = TextPrimary
                            )
                        }

                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("formula", "${item.title}: ${item.formula}")
                                clipboard.setPrimaryClip(clip)
                                copiedFormulaId = item.id
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "کپی فرمول",
                                tint = if (isCopied) AppleGreen else TextTertiary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // فرمول برجسته با استایل اختصاصی
                    Surface(
                        color = DarkSurface2,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, AppleYellow.copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = item.formula,
                            color = AppleYellow,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp,
                            lineHeight = 22.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = item.description,
                        fontSize = 11.5.sp,
                        color = TextSecondary,
                        lineHeight = 19.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // بخش دام طراح کنکور (Trap)
                    Surface(
                        color = AppleOrange.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, AppleOrange.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = AppleOrange,
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = "دام پرتکرار طراح کنکور:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AppleOrange
                                )
                                Text(
                                    text = item.konkurTrap,
                                    fontSize = 11.sp,
                                    color = TextPrimary,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // مثال تستی کوتاه
                    Surface(
                        color = DarkSurface2,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "💡 کاربرد: ${item.example}",
                            fontSize = 10.5.sp,
                            color = TextTertiary,
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}
