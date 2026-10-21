package com.example.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkTealPrimary
import com.example.ui.theme.OrangeAccent
import com.example.ui.theme.TealPrimary
import com.example.util.PersianUtils
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SplashOnboardingScreen(
    onFinish: (grade: String, major: String) -> Unit
) {
    var step by remember { mutableStateOf(0) } // 0: Splash, 1: Major, 2: Grade, 3: Features & Notification
    var selectedMajor by remember { mutableStateOf("تجربی") }
    var selectedGrade by remember { mutableStateOf("دوازدهم") }

    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ ->
        // Proceed on grant or denial
        onFinish(selectedGrade, selectedMajor)
    }

    LaunchedEffect(Unit) {
        // Splash delay
        kotlinx.coroutines.delay(1800)
        step = 1
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        when (step) {
            0 -> SplashScreenMolecule()
            1 -> OnboardingMajorStep(
                selectedMajor = selectedMajor,
                onMajorSelected = { selectedMajor = it },
                onNext = { step = 2 }
            )
            2 -> OnboardingGradeStep(
                selectedGrade = selectedGrade,
                onGradeSelected = { selectedGrade = it },
                onNext = { step = 3 },
                onBack = { step = 1 }
            )
            3 -> OnboardingFeaturesStep(
                onComplete = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        onFinish(selectedGrade, selectedMajor)
                    }
                },
                onBack = { step = 2 }
            )
        }
    }
}

@Composable
fun SplashScreenMolecule() {
    val infiniteTransition = rememberInfiniteTransition(label = "molecule")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(24.dp)
    ) {
        Box(
            modifier = Modifier
                .size(180.dp)
                .scale(scale),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2, size.height / 2)
                val primaryColor = Color(0xFF00828A)
                val accentColor = Color(0xFFF3722C)

                // Central nucleus
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(primaryColor, Color(0xFF004D54)),
                        center = center,
                        radius = 32.dp.toPx()
                    ),
                    radius = 30.dp.toPx(),
                    center = center
                )

                // Orbital ring 1
                drawOval(
                    color = primaryColor.copy(alpha = 0.4f),
                    topLeft = Offset(center.x - 70.dp.toPx(), center.y - 35.dp.toPx()),
                    size = androidx.compose.ui.geometry.Size(140.dp.toPx(), 70.dp.toPx()),
                    style = Stroke(width = 3.dp.toPx())
                )

                // Orbital ring 2 (angled)
                drawOval(
                    color = accentColor.copy(alpha = 0.4f),
                    topLeft = Offset(center.x - 35.dp.toPx(), center.y - 70.dp.toPx()),
                    size = androidx.compose.ui.geometry.Size(70.dp.toPx(), 140.dp.toPx()),
                    style = Stroke(width = 3.dp.toPx())
                )

                // Orbiting electron 1
                val rad1 = Math.toRadians(angle.toDouble())
                val e1X = center.x + 70.dp.toPx() * cos(rad1).toFloat()
                val e1Y = center.y + 35.dp.toPx() * sin(rad1).toFloat()
                drawCircle(color = primaryColor, radius = 7.dp.toPx(), center = Offset(e1X, e1Y))

                // Orbiting electron 2
                val rad2 = Math.toRadians((angle + 90).toDouble())
                val e2X = center.x + 35.dp.toPx() * cos(rad2).toFloat()
                val e2Y = center.y + 70.dp.toPx() * sin(rad2).toFloat()
                drawCircle(color = accentColor, radius = 7.dp.toPx(), center = Offset(e2X, e2Y))
            }

            // Official App Icon at the nucleus
            Image(
                painter = painterResource(id = R.drawable.app_icon_fg),
                contentDescription = "آیکون شیمی یار",
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "شیمی‌یار",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "دستیار تخصصی آموزش و کنکور شیمی",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun OnboardingMajorStep(
    selectedMajor: String,
    onMajorSelected: (String) -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "گام ۱ از ۳",
            style = MaterialTheme.typography.labelMedium,
            color = OrangeAccent,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "رشته تحصیلی خود را انتخاب کنید",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "محتوا و بودجه‌بندی تست‌ها متناسب با رشته شما تنظیم می‌شود",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        val majors = listOf(
            Pair("تجربی", "شیمی با ضریب ۹ در کنکور سراسری تجربی"),
            Pair("ریاضی", "شیمی با ضریب ۷ در کنکور سراسری ریاضی فیزیک")
        )

        majors.forEach { (major, desc) ->
            val isSelected = selectedMajor == major
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) TealPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
                ),
                border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, TealPrimary) else null,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onMajorSelected(major) }
                    .padding(vertical = 8.dp)
                    .testTag("major_$major")
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { onMajorSelected(major) },
                        colors = RadioButtonDefaults.colors(selectedColor = TealPrimary)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "علوم $major",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = desc,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onNext,
            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("next_major_button")
        ) {
            Text(text = "مرحله بعد", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
fun OnboardingGradeStep(
    selectedGrade: String,
    onGradeSelected: (String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "گام ۲ از ۳",
            style = MaterialTheme.typography.labelMedium,
            color = OrangeAccent,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "در کدام مقطع تحصیلی هستید؟",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(28.dp))

        val grades = listOf("دهم", "یازدهم", "دوازدهم", "داوطلب کنکور (فارغ‌التحصیل)")

        grades.forEach { grade ->
            val isSelected = selectedGrade == grade
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) TealPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
                ),
                border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, TealPrimary) else null,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onGradeSelected(grade) }
                    .padding(vertical = 6.dp)
                    .testTag("grade_$grade")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { onGradeSelected(grade) },
                        colors = RadioButtonDefaults.colors(selectedColor = TealPrimary)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "پایه $grade",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
            ) {
                Text(text = "قبلی")
            }
            Button(
                onClick = onNext,
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .weight(2f)
                    .height(52.dp)
                    .testTag("next_grade_button")
            ) {
                Text(text = "مرحله بعد", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun OnboardingFeaturesStep(
    onComplete: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "گام ۳ از ۳",
            style = MaterialTheme.typography.labelMedium,
            color = OrangeAccent,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "به شیمی‌یار خوش آمدید!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "ابزارهای فوق‌العاده برای تسلط مفهومی و ارتقای درصد در شیمی کنکور",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        val features = listOf(
            Triple(Icons.Default.MenuBook, "جعبه لایتنر هوشمند", "تثبیت نکات، فرمول‌ها و رنگ رسوب‌ها در حافظه بلندمدت"),
            Triple(Icons.Default.ViewModule, "جدول تناوبی تعاملی کنکوری", "بررسی تمام ۱۱۸ عنصر، شعاع اتمی و الکترونگاتیوی"),
            Triple(Icons.Default.Calculate, "موتور موازنه و محاسبات", "محاسبه دقیق جرم مولی، موازنه واکنش و تبدیل‌های استوکیومتری"),
            Triple(Icons.Default.NotificationsActive, "یادآور مطالعه روزانه", "نوتیفیکیشن سر وقت برای حفظ زنجیره مطالعه و عدم افت انگیزه")
        )

        features.forEach { (icon, title, desc) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(TealPrimary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = TealPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = desc,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
            ) {
                Text(text = "قبلی")
            }
            Button(
                onClick = onComplete,
                colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .weight(2f)
                    .height(52.dp)
                    .testTag("start_app_button")
            ) {
                Text(text = "شروع یادگیری 🚀", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
            }
        }
    }
}
