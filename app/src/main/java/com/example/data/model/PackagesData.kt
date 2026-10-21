package com.example.data.model

import com.example.data.local.PackageEntity

object PackagesData {
    val initialPackages = listOf(
        PackageEntity(
            id = "pkg_konkur_1403",
            title = "دفترچه جامع سوالات کنکور ۱۴۰۳ (نوبت اول و دوم)",
            description = "تحلیل خط‌به‌خط تمامی تست‌های شیمی کنکور سراسری تجربی و ریاضی ۱۴۰۳ همراه با دام‌های گزینه‌ای و تکنیک‌های رد گزینه سریع.",
            itemCount = 70,
            sizeMb = 4.2f,
            isPro = true,
            isDownloaded = false
        ),
        PackageEntity(
            id = "pkg_olympiad_stage1",
            title = "بانک سوالات مرحله اول المپیاد شیمی ایران",
            description = "مجموعه ۱۲۰ تست گزینش‌شده از دوره‌های ۲۵ تا ۳۳ المپیاد شیمی مناسب دانش‌آموزان مدارس سمپاد و متقاضیان درصد ۱۰۰.",
            itemCount = 120,
            sizeMb = 8.5f,
            isPro = true,
            isDownloaded = false
        ),
        PackageEntity(
            id = "pkg_stoichiometry_master",
            title = "پکیج تخصصی بمب استوکیومتری و مسائل عددی",
            description = "آموزش روش تناسب و کسر تبدیل، تکنیک‌های تقریب‌زدن کسرها بدون ماشین‌حساب، ۵۰ تست حل‌شده از غلظت و درصد خلوص.",
            itemCount = 50,
            sizeMb = 3.8f,
            isPro = false,
            isDownloaded = true,
            downloadProgress = 100
        ),
        PackageEntity(
            id = "pkg_reactions_colors",
            title = "اطلس تصویری رنگ رسوب‌ها و واکنش‌های کتاب",
            description = "تمام تغییر رنگ‌ها، اسامی کاتالیزورها و ترکیبات آلی پایه دهم تا دوازدهم به صورت فلش‌کارت‌های سریع مرور شب امتحان.",
            itemCount = 65,
            sizeMb = 6.1f,
            isPro = false,
            isDownloaded = false
        )
    )
}
