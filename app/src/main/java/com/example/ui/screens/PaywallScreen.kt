package com.example.ui.screens

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
import com.example.data.repository.BillingRepository
import com.example.ui.theme.OrangeAccent
import com.example.ui.theme.TealPrimary
import com.example.ui.viewmodel.MainViewModel
import com.example.util.PersianUtils

@Composable
fun PaywallScreen(
    viewModel: MainViewModel,
    onClose: () -> Unit
) {
    var selectedPlanId by remember { mutableStateOf(BillingRepository.PRODUCT_ID_ANNUAL) } // Anchoring: Annual default!

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onClose) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "بستن")
                }

                TextButton(onClick = { viewModel.restorePurchases() }) {
                    Text("بازیابی خرید", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = { viewModel.purchasePlan(selectedPlanId) },
                        colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("paywall_cta_button")
                    ) {
                        Text(
                            text = "ارتقا و فعال‌سازی اشتراک آنی",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(onClick = { viewModel.startFreeTrial() }) {
                        Text(
                            text = "یا شروع ۳ روز آزمایش رایگان (بدون پرداخت)",
                            style = MaterialTheme.typography.bodySmall,
                            color = TealPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        modifier = Modifier.testTag("paywall_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Sparkle Icon
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(OrangeAccent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.WorkspacePremium,
                    contentDescription = null,
                    tint = OrangeAccent,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "با شیمی‌یار پرو، شیمی کنکور و نهایی را مسلط شو!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Honest Subtitle
            Surface(
                color = TealPrimary.copy(alpha = 0.1f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = "🎯 پوشش کامل و خط‌به‌خط کتاب‌های درسی دهم، یازدهم و دوازدهم",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = TealPrimary,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Free vs Pro Comparison
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "امکانات اختصاصی اشتراک پرو",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val features = listOf(
                        Triple("مرور فلش‌کارت‌های لایتنر", "محدود", "نامحدود در تمام پایه‌ها"),
                        Triple("تست‌های کنکور سراسری", "محدود", "بانک سوالات + پاسخنامه تشریحی"),
                        Triple("نکات طلایی و دام‌های تستی", "فقط فصل اول", "تمام فصول دهم، یازدهم و دوازدهم"),
                        Triple("تحلیل تستی جدول تناوبی", "محدود", "تحلیل تستی کامل ۱۱۸ عنصر")
                    )

                    features.forEach { (feat, free, pro) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = feat, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1.5f))
                            Text(text = free, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                            Text(text = pro, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = TealPrimary, modifier = Modifier.weight(1.5f), textAlign = TextAlign.End)
                        }
                        Divider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 2.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Plans Selector
            Text(
                text = "طرح‌های اشتراک شیمی‌یار",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 1) Annual Plan (Best Choice)
            PlanSelectionCard(
                title = "اشتراک ۱ ساله (طلایی کنکور)",
                price = "۴۸۵,۰۰۰ تومان",
                originalPrice = null,
                discount = null,
                monthlyEquiv = "دسترسی کامل برای سال تحصیلی",
                isBestChoice = true,
                isSelected = selectedPlanId == BillingRepository.PRODUCT_ID_ANNUAL,
                onClick = { selectedPlanId = BillingRepository.PRODUCT_ID_ANNUAL }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 2) 6-Month Plan
            PlanSelectionCard(
                title = "اشتراک ۶ ماهه",
                price = "۳۸۵,۰۰۰ تومان",
                originalPrice = null,
                discount = null,
                monthlyEquiv = "مناسب برای یک نیم‌سال تحصیلی",
                isBestChoice = false,
                isSelected = selectedPlanId == BillingRepository.PRODUCT_ID_SEMIANNUAL,
                onClick = { selectedPlanId = BillingRepository.PRODUCT_ID_SEMIANNUAL }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 3) Monthly Plan
            PlanSelectionCard(
                title = "اشتراک ۱ ماهه",
                price = "۹۹,۰۰۰ تومان",
                originalPrice = null,
                discount = null,
                monthlyEquiv = "دسترسی ۳۰ روزه",
                isBestChoice = false,
                isSelected = selectedPlanId == BillingRepository.PRODUCT_ID_MONTHLY,
                onClick = { selectedPlanId = BillingRepository.PRODUCT_ID_MONTHLY }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Guarantee & Trust
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(imageVector = Icons.Default.VerifiedUser, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "گارانتی ۱۰۰٪ بازگشت وجه تا ۷ روز در صورت عدم رضایت",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PlanSelectionCard(
    title: String,
    price: String,
    originalPrice: String?,
    discount: String?,
    monthlyEquiv: String?,
    isBestChoice: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) TealPrimary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) OrangeAccent else MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = isSelected,
                        onClick = onClick,
                        colors = RadioButtonDefaults.colors(selectedColor = OrangeAccent)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (isBestChoice) {
                    Surface(
                        color = OrangeAccent,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "بهترین انتخاب",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 40.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = price,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = OrangeAccent
                    )
                    if (originalPrice != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = originalPrice,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textDecoration = TextDecoration.LineThrough
                        )
                    }
                }

                if (discount != null) {
                    Surface(
                        color = Color(0xFFFFEBEE),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = discount,
                            color = Color(0xFFC62828),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            if (monthlyEquiv != null) {
                Text(
                    text = "معادل $monthlyEquiv",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 40.dp, top = 2.dp)
                )
            }
        }
    }
}
