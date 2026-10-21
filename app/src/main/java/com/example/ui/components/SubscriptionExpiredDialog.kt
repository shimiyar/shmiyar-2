package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*

/**
 * دیالوگ اطلاع‌رسانی پایان مهلت ۳ روزه دسترسی رایگان یا اشتراک قبلی
 */
@Composable
fun SubscriptionExpiredDialog(
    onDismiss: () -> Unit,
    onRenewPlan: (planId: String) -> Unit
) {
    var selectedPlan by remember { mutableStateOf("1_year") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface1),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, MetallicGold),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("subscription_expired_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MetallicRose.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MetallicRose.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "⚠️ پایان مهلت دسترسی",
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
                        Icon(imageVector = Icons.Default.Close, contentDescription = "بستن", tint = TextTertiary)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Icon
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(MetallicGold.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.HourglassBottom,
                        contentDescription = null,
                        tint = MetallicGold,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "مهلت ۳ روزه شیمی‌یار پرو شما به پایان رسید",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "برای حفظ تداوم یادگیری و دسترسی بدون محدودیت به بانک تست‌های تالیفی و کنکور، پاسخنامه‌های تشریحی و لایتنر، اشتراک خود را انتخاب و تمدید نمایید:",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ۱. سالانه (۴۸۵,۰۰۰ تومان)
                Card(
                    shape = RoundedCornerShape(14.dp),
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
                                    Surface(color = MetallicGold, shape = RoundedCornerShape(4.dp)) {
                                        Text(
                                            text = "پیشنهادی",
                                            color = Color.Black,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
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
                    shape = RoundedCornerShape(14.dp),
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
                    shape = RoundedCornerShape(14.dp),
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

                Spacer(modifier = Modifier.height(18.dp))

                // CTA Button
                Button(
                    onClick = { onRenewPlan(selectedPlan) },
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
                        .height(48.dp)
                        .testTag("renew_plan_button")
                ) {
                    val priceLabel = when (selectedPlan) {
                        "1_year" -> "۴۸۵,۰۰۰ تومان"
                        "6_months" -> "۳۸۵,۰۰۰ تومان"
                        else -> "۹۹,۰۰۰ تومان"
                    }
                    Text(
                        text = "تمدید و فعال‌سازی فوری ($priceLabel)",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(onClick = onDismiss) {
                    Text(
                        text = "ادامه با دسترسی محدود رایگان",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextTertiary
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))
            }
        }
    }
}
