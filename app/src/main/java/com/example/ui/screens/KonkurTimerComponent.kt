package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.ShimiYarViewModel
import com.example.util.PersianUtils
import kotlinx.coroutines.delay

enum class TimerMode {
    SPEED_TEST_35S,
    POMODORO_25M
}

@Composable
fun KonkurTimerComponent(
    viewModel: ShimiYarViewModel,
    onLockedClick: () -> Unit,
    isPro: Boolean
) {
    var mode by remember { mutableStateOf(TimerMode.SPEED_TEST_35S) }

    // Speed test state
    val testDuration = 35 // seconds per Konkur question
    var speedSecondsLeft by remember { mutableIntStateOf(testDuration) }
    var isSpeedRunning by remember { mutableStateOf(false) }
    var questionCounter by remember { mutableIntStateOf(1) }
    var correctCounter by remember { mutableIntStateOf(0) }
    var streakCounter by remember { mutableIntStateOf(0) }

    // Pomodoro state
    val pomodoroDuration = 25 * 60 // 25 mins
    var pomodoroSecondsLeft by remember { mutableIntStateOf(pomodoroDuration) }
    var isPomodoroRunning by remember { mutableStateOf(false) }

    // Speed timer effect
    LaunchedEffect(isSpeedRunning, speedSecondsLeft) {
        if (isSpeedRunning && speedSecondsLeft > 0) {
            delay(1000)
            speedSecondsLeft--
        } else if (isSpeedRunning && speedSecondsLeft == 0) {
            isSpeedRunning = false
            viewModel.triggerAchievement("⏰ زمان ۳۵ ثانیه تست تمام شد!")
        }
    }

    // Pomodoro timer effect
    LaunchedEffect(isPomodoroRunning, pomodoroSecondsLeft) {
        if (isPomodoroRunning && pomodoroSecondsLeft > 0) {
            delay(1000)
            pomodoroSecondsLeft--
        } else if (isPomodoroRunning && pomodoroSecondsLeft == 0) {
            isPomodoroRunning = false
            viewModel.addXp(30)
            viewModel.triggerAchievement("🏆 آفرین! یک پومودورو ۲۵ دقیقه‌ای شیمی تکمیل شد (+۳۰ XP)")
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("konkur_timer_component"),
        contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // ۱. سوئیچر حالت: تست ۳۵ ثانیه / پومودورو ۲۵ دقیقه
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                border = BorderStroke(1.dp, DarkSeparator),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkSurface2)
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Surface(
                            onClick = { mode = TimerMode.SPEED_TEST_35S },
                            shape = RoundedCornerShape(10.dp),
                            color = if (mode == TimerMode.SPEED_TEST_35S) AppleBlue else Color.Transparent,
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "⚡ تست‌زنی ۳۵ ثانیه", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = if (mode == TimerMode.SPEED_TEST_35S) Color.White else TextSecondary)
                            }
                        }

                        Surface(
                            onClick = { mode = TimerMode.POMODORO_25M },
                            shape = RoundedCornerShape(10.dp),
                            color = if (mode == TimerMode.POMODORO_25M) AppleOrange else Color.Transparent,
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "🍅 پومودورو ۲۵ دقیقه", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = if (mode == TimerMode.POMODORO_25M) Color.White else TextSecondary)
                            }
                        }
                    }
                }
            }
        }

        // ۲. محتوای هر حالت
        if (mode == TimerMode.SPEED_TEST_35S) {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                    border = BorderStroke(1.dp, AppleBlue.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "تست شماره ${PersianUtils.toPersianDigits(questionCounter)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Surface(
                                color = AppleOrange.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "🔥 زنجیره: ${PersianUtils.toPersianDigits(streakCounter)}",
                                    color = AppleOrange,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Large Circular Countdown Display
                        val progress = speedSecondsLeft / 35f
                        val circleColor = when {
                            speedSecondsLeft > 15 -> AppleGreen
                            speedSecondsLeft > 7 -> AppleOrange
                            else -> AppleRed
                        }

                        Box(
                            modifier = Modifier
                                .size(150.dp)
                                .clip(CircleShape)
                                .background(DarkSurface2),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                progress = { progress },
                                strokeWidth = 8.dp,
                                color = circleColor,
                                trackColor = DarkSurface3,
                                modifier = Modifier.fillMaxSize()
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = PersianUtils.toPersianDigits(speedSecondsLeft),
                                    fontSize = 42.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = circleColor
                                )
                                Text(text = "ثانیه", fontSize = 12.sp, color = TextSecondary)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Control Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    isSpeedRunning = !isSpeedRunning
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSpeedRunning) DarkSurface3 else AppleBlue
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = if (isSpeedRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = if (isSpeedRunning) "توقف موقت" else "شروع تست")
                            }

                            OutlinedButton(
                                onClick = {
                                    speedSecondsLeft = testDuration
                                    isSpeedRunning = false
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                            ) {
                                Icon(imageVector = Icons.Default.Refresh, contentDescription = "تنظیم مجدد")
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Feedback Buttons for this question
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    correctCounter++
                                    streakCounter++
                                    questionCounter++
                                    speedSecondsLeft = testDuration
                                    isSpeedRunning = true
                                    viewModel.addXp(10)
                                    viewModel.triggerAchievement("🎯 تست درست ثبت شد (+۱۰ XP)!")
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AppleGreen),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = "✓ درست زدم (+۱۰ XP)", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }

                            Button(
                                onClick = {
                                    streakCounter = 0
                                    questionCounter++
                                    speedSecondsLeft = testDuration
                                    isSpeedRunning = true
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AppleRed.copy(alpha = 0.8f)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = "✗ غلط یا وقت کم آمد", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        } else {
            // Pomodoro 25 Minutes Focus View
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                    border = BorderStroke(1.dp, AppleOrange.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🍅 حالت تمرکز عمیق شیمی کنکور",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AppleOrange
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "۲۵ دقیقه مطالعه پیوسته بدون وقفه همراه با پاداش +۳۰ XP",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        val mins = pomodoroSecondsLeft / 60
                        val secs = pomodoroSecondsLeft % 60
                        val timeStr = String.format("%02d:%02d", mins, secs)

                        Box(
                            modifier = Modifier
                                .size(160.dp)
                                .clip(CircleShape)
                                .background(DarkSurface2),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                progress = { pomodoroSecondsLeft / (25f * 60f) },
                                strokeWidth = 8.dp,
                                color = AppleOrange,
                                trackColor = DarkSurface3,
                                modifier = Modifier.fillMaxSize()
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = PersianUtils.toPersianDigits(timeStr),
                                    fontSize = 34.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextPrimary
                                )
                                Text(text = "دقیقه : ثانیه", fontSize = 11.sp, color = TextSecondary)
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { isPomodoroRunning = !isPomodoroRunning },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isPomodoroRunning) DarkSurface3 else AppleOrange
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = if (isPomodoroRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = if (isPomodoroRunning) "توقف موقت" else "شروع تمرکز")
                            }

                            OutlinedButton(
                                onClick = {
                                    pomodoroSecondsLeft = pomodoroDuration
                                    isPomodoroRunning = false
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                            ) {
                                Icon(imageVector = Icons.Default.Refresh, contentDescription = "تنظیم مجدد")
                            }
                        }
                    }
                }
            }
        }

        // ۳. نکات مدیریت زمان شیمی در دفترچه کنکور سراسری
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                border = BorderStroke(1.dp, DarkSeparator),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "💡 استراتژی مدیریت زمان درس شیمی در کنکور:",
                        fontWeight = FontWeight.Bold,
                        color = MetallicCyan,
                        fontSize = 12.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• دفترچه تخصصی کنکور شامل ۳۵ سوال شیمی است که زمان استاندارد کل آن ۳۵ دقیقه است (دقیقاً ۱ دقیقه به ازای هر تست).\n• سوالات حفظی و مفاهیمی را در ۳۰ الی ۴۰ ثانیه پاسخ دهید تا زمان ذخیره شده به مسائل چندمرحله‌ای استوکیومتری اختصاص یابد.\n• سوالات وقت‌گیر یا با محاسبات سنگین را علامت‌گذاری کرده و به دور دوم موکول کنید.",
                        fontSize = 11.sp,
                        color = TextPrimary,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}
