package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.util.PersianUtils
import kotlin.math.roundToInt

/**
 * Recharts-inspired Native Compose Donut/Circular Pie Chart for Leitner Learning Progress.
 * Displays comparison of Mastered (آموخته شده) vs In-Learning (در حال یادگیری) vs Unseen cards.
 */
@Composable
fun LeitnerCircularChart(
    masteredCount: Int,
    learningCount: Int,
    unseenCount: Int,
    totalCards: Int,
    modifier: Modifier = Modifier,
    initiallyExpanded: Boolean = false
) {
    var isExpanded by remember { mutableStateOf(initiallyExpanded) }

    val safeTotal = totalCards.coerceAtLeast(1)
    val safeMastered = masteredCount.coerceIn(0, safeTotal)
    val safeLearning = learningCount.coerceIn(0, safeTotal)
    val safeUnseen = (safeTotal - safeMastered - safeLearning).coerceAtLeast(0)

    val masteredRatio = safeMastered.toFloat() / safeTotal
    val learningRatio = safeLearning.toFloat() / safeTotal

    val animatedMasteredSweep by animateFloatAsState(
        targetValue = masteredRatio * 360f,
        animationSpec = tween(durationMillis = 900),
        label = "masteredSweep"
    )

    val animatedLearningSweep by animateFloatAsState(
        targetValue = learningRatio * 360f,
        animationSpec = tween(durationMillis = 900),
        label = "learningSweep"
    )

    val masteryPercent = ((safeMastered.toFloat() / safeTotal) * 100).roundToInt()

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface1),
        border = androidx.compose.foundation.BorderStroke(1.dp, AppleBlue.copy(alpha = 0.25f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("leitner_circular_chart_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row with Title and Expand Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AppleBlue.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "📊", fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "نمودار یادگیری جعبه لایتنر",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "وضعیت کارت‌های آموخته‌شده در برابر در حال یادگیری",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }

                IconButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "تغییر وضعیت نمایش",
                        tint = AppleBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Visual: Donut Chart + Right Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Circular Canvas Donut Chart
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val colorTrack = DarkSurface3
                    val colorLearning = AppleOrange
                    val colorMastered = AppleGreen

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidth = 14.dp.toPx()
                        val diameter = size.minDimension - strokeWidth
                        val topLeft = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
                        val arcSize = Size(diameter, diameter)

                        // 1. Background Ring (Unseen Cards / Track)
                        drawArc(
                            color = colorTrack,
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )

                        // 2. Learning Arc (Orange/Amber)
                        if (animatedLearningSweep > 0.5f) {
                            drawArc(
                                color = colorLearning,
                                startAngle = -90f + animatedMasteredSweep,
                                sweepAngle = animatedLearningSweep,
                                useCenter = false,
                                topLeft = topLeft,
                                size = arcSize,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                        }

                        // 3. Mastered Arc (Neon Green)
                        if (animatedMasteredSweep > 0.5f) {
                            drawArc(
                                color = colorMastered,
                                startAngle = -90f,
                                sweepAngle = animatedMasteredSweep,
                                useCenter = false,
                                topLeft = topLeft,
                                size = arcSize,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                        }
                    }

                    // Inside Donut: Mastery Percentage and Title
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "${PersianUtils.toPersianDigits(masteryPercent)}٪",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (masteryPercent > 50) AppleGreen else TextPrimary
                        )
                        Text(
                            text = "تسلط کنکور",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            color = TextTertiary
                        )
                    }
                }

                // Legend Column
                Column(
                    modifier = Modifier.padding(start = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ChartLegendItem(
                        bulletColor = AppleGreen,
                        label = "آموخته شده (تسلط کامل)",
                        count = safeMastered,
                        total = safeTotal
                    )

                    ChartLegendItem(
                        bulletColor = AppleOrange,
                        label = "در حال یادگیری (مرور فعال)",
                        count = safeLearning,
                        total = safeTotal
                    )

                    ChartLegendItem(
                        bulletColor = TextTertiary,
                        label = "شروع نشده (کارت‌های جدید)",
                        count = safeUnseen,
                        total = safeTotal
                    )
                }
            }

            // Expandable Detailed Leitner Breakdown
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Divider(color = DarkSeparator, modifier = Modifier.padding(bottom = 12.dp))

                    Text(
                        text = "🧠 مراحل ۵ گانه جعبه لایتنر در شیمی‌یار:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = AppleBlue
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        LeitnerBoxBadge(
                            boxNumber = 1,
                            title = "جعبه ۱",
                            interval = "هر روز",
                            color = AppleRed,
                            modifier = Modifier.weight(1f)
                        )
                        LeitnerBoxBadge(
                            boxNumber = 2,
                            title = "جعبه ۲",
                            interval = "۳ روزه",
                            color = AppleOrange,
                            modifier = Modifier.weight(1f)
                        )
                        LeitnerBoxBadge(
                            boxNumber = 3,
                            title = "جعبه ۳",
                            interval = "هفتگی",
                            color = AppleYellow,
                            modifier = Modifier.weight(1f)
                        )
                        LeitnerBoxBadge(
                            boxNumber = 4,
                            title = "جعبه ۴",
                            interval = "۱۵ روزه",
                            color = AppleIndigo,
                            modifier = Modifier.weight(1f)
                        )
                        LeitnerBoxBadge(
                            boxNumber = 5,
                            title = "جعبه ۵",
                            interval = "تثبیت دائم",
                            color = AppleGreen,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        color = DarkSurface2,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "💡", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "با زدن «بلد شدم»، کارت یک جعبه جلو می‌رود و با زدن «مرور مجدد»، به جعبه ۱ بازمی‌گردد تا به تسلط ۱۰۰٪ کنکور برسید.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChartLegendItem(
    bulletColor: Color,
    label: String,
    count: Int,
    total: Int
) {
    val percent = ((count.toFloat() / total.coerceAtLeast(1)) * 100).roundToInt()

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(bulletColor)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                fontSize = 11.sp
            )
            Text(
                text = "${PersianUtils.toPersianDigits(count)} کارت (${PersianUtils.toPersianDigits(percent)}٪)",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun LeitnerBoxBadge(
    boxNumber: Int,
    title: String,
    interval: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.35f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = interval,
                fontSize = 9.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}
