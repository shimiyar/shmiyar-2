package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*
import com.example.util.PersianUtils
import kotlinx.coroutines.delay

@Composable
fun DailyOfferModal(
    onDismiss: () -> Unit,
    onPurchase: (variant: String) -> Unit,
    onActivateTrial: () -> Unit
) {
    var selectedPlan by remember { mutableStateOf("1_year") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface1),
            border = androidx.compose.foundation.BorderStroke(2.dp, MetallicGold),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("daily_offer_modal")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with Close and Tag
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MetallicRose.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MetallicRose.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = "⚡ پیشنهاد شگفت‌انگیز روز",
                            color = MetallicRose,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "بستن",
                            tint = TextTertiary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Hero Badge Icon
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(MetallicGold.copy(alpha = 0.35f), Color.Transparent)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🔥", fontSize = 38.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "پلن ویژه عضویت شیمی‌یار طلایی",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "دسترسی ۱ ساله به تمام بانک تست، درسنامه‌ها، جدول عناصر و لایتنر",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))


                // ۱. سالانه (۴۸۵,۰۰۰ تومان)
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedPlan == "1_year") DarkSurface3 else DarkSurface2
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (selectedPlan == "1_year") 2.dp else 1.dp,
                        color = if (selectedPlan == "1_year") MetallicGold else DarkSeparator
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedPlan = "1_year" }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedPlan == "1_year",
                                onClick = { selectedPlan = "1_year" },
                                colors = RadioButtonDefaults.colors(selectedColor = MetallicGold)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "اشتراک ۱ ساله (طلایی)",
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = MetallicGold,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "پیشنهادی",
                                            color = Color.Black,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "دسترسی کامل برای سال تحصیلی و کنکور",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Text(
                            text = "۴۸۵,۰۰۰ تومان",
                            fontWeight = FontWeight.ExtraBold,
                            color = MetallicGold,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // ۲. شش ماهه (۳۸۵,۰۰۰ تومان)
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedPlan == "6_months") DarkSurface3 else DarkSurface2
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (selectedPlan == "6_months") 2.dp else 1.dp,
                        color = if (selectedPlan == "6_months") MetallicCyan else DarkSeparator
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedPlan = "6_months" }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedPlan == "6_months",
                                onClick = { selectedPlan = "6_months" },
                                colors = RadioButtonDefaults.colors(selectedColor = MetallicCyan)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = "اشتراک ۶ ماهه",
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "مناسب برای یک نیم‌سال تحصیلی",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Text(
                            text = "۳۸۵,۰۰۰ تومان",
                            fontWeight = FontWeight.ExtraBold,
                            color = MetallicCyan,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // ۳. ماهانه (۹۹,۰۰۰ تومان)
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedPlan == "1_month") DarkSurface3 else DarkSurface2
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (selectedPlan == "1_month") 2.dp else 1.dp,
                        color = if (selectedPlan == "1_month") MetallicCopper else DarkSeparator
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedPlan = "1_month" }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedPlan == "1_month",
                                onClick = { selectedPlan = "1_month" },
                                colors = RadioButtonDefaults.colors(selectedColor = MetallicCopper)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = "اشتراک ۱ ماهه",
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "دسترسی ۳۰ روزه",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Text(
                            text = "۹۹,۰۰۰ تومان",
                            fontWeight = FontWeight.ExtraBold,
                            color = MetallicCopper,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Real Value Bullets
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurface3.copy(alpha = 0.5f))
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(
                        "✅ باز شدن ۱۰۰٪ تمامی ۴۱۰+ فلش‌کارت و درسنامه ۳ پایه دهم، یازدهم و دوازدهم",
                        "✅ بانک فرمول‌های طلایی و میانبرهای سرعتی تستی زیر ۴۰ ثانیه",
                        "✅ شبیه‌ساز آزمون‌های جامع کنکور با کارنامه تخمین تراز و درصد",
                        "✅ محاسبه‌گر هوشمند جرم مولی، استوکیومتری، pH و سری پتانسیل E°",
                        "✅ تایمر تمرکز ۳۵ ثانیه‌ای تست سرعتی و حالت پومودورو با پاداش XP",
                        "✅ ضمانت ۱۰۰٪ بازگشت وجه تا ۷ روز در صورت عدم رضایت"
                    ).forEach { item ->
                        Text(
                            text = item,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Primary CTA Button
                Button(
                    onClick = { onPurchase(selectedPlan) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = when (selectedPlan) {
                            "1_year" -> MetallicGold
                            "6_months" -> MetallicCyan
                            else -> MetallicCopper
                        }
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("accept_daily_offer_button")
                ) {
                    val priceLabel = when (selectedPlan) {
                        "1_year" -> "۴۸۵,۰۰۰ تومان"
                        "6_months" -> "۳۸۵,۰۰۰ تومان"
                        else -> "۹۹,۰۰۰ تومان"
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "👑 خرید اشتراک و فعال‌سازی ($priceLabel)",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Free trial fallback or maybe later
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onActivateTrial) {
                        Text(
                            text = "یا تست ۳ روزه رایگان کافه‌بازار",
                            color = MetallicEmerald,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    TextButton(onClick = onDismiss) {
                        Text(
                            text = "شاید بعداً",
                            color = TextTertiary,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Security and Guarantee
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = MetallicEmerald,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "۱۰۰٪ گارانتی بازگشت وجه در صورت عدم رضایت تا ۷ روز",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary,
                        fontSize = 10.sp
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}
