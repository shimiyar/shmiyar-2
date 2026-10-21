package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PackageEntity
import com.example.ui.components.ProBadge
import com.example.ui.theme.OrangeAccent
import com.example.ui.theme.TealPrimary
import com.example.ui.viewmodel.MainViewModel
import com.example.util.PersianUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackagesScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val packages by viewModel.allPackages.collectAsState(initial = emptyList())
    val isPro by viewModel.isPro.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("پکیج‌های آموزشی دانلودی", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "بازگشت")
                    }
                }
            )
        },
        modifier = Modifier.testTag("packages_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = TealPrimary.copy(alpha = 0.1f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.CloudDownload, contentDescription = null, tint = TealPrimary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "پکیج‌ها به طور خودکار در پس‌زمینه با WorkManager همگام‌سازی و بروزرسانی می‌شوند و پس از دانلود کاملاً آفلاین در دسترس هستند.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            items(packages) { pkg ->
                PackageCardItem(
                    pkg = pkg,
                    isProUser = isPro,
                    onDownloadClick = {
                        if (pkg.isPro && !isPro) {
                            viewModel.openPaywall("دسترسی به پکیج‌های پیشرفته و المپیاد")
                        } else {
                            viewModel.downloadPackage(pkg)
                        }
                    }
                )
            }

            // Developer / Backend API JSON schema note
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "مستندات ساختار JSON دریافت محتوا از سرور (API Schema):",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = """{
  "package_id": "pkg_konkur_1403",
  "version": 1.2,
  "download_url": "https://api.shimiyar.ir/packages/...",
  "flashcards": [ ... ],
  "questions": [ ... ]
}""",
                            fontSize = 11.sp,
                            color = TealPrimary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PackageCardItem(
    pkg: PackageEntity,
    isProUser: Boolean,
    onDownloadClick: () -> Unit
) {
    val isLocked = pkg.isPro && !isProUser

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = pkg.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                if (pkg.isPro) {
                    ProBadge()
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = pkg.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${PersianUtils.toPersianDigits(pkg.itemCount)} آیتم • ${PersianUtils.toPersianDigits(pkg.sizeMb)} مگابایت",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (pkg.isDownloaded) {
                    Surface(
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "آماده و نصب‌شده", color = Color(0xFF2E7D32), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                } else if (pkg.downloadProgress in 1..99) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${PersianUtils.toPersianDigits(pkg.downloadProgress)}٪",
                            style = MaterialTheme.typography.labelSmall,
                            color = TealPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { pkg.downloadProgress / 100f },
                            modifier = Modifier
                                .width(100.dp)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = TealPrimary
                        )
                    }
                } else {
                    Button(
                        onClick = onDownloadClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isLocked) OrangeAccent else TealPrimary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        if (isLocked) {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("آنلاک با پرو", fontSize = 12.sp)
                        } else {
                            Icon(imageVector = Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("دانلود پکیج", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
