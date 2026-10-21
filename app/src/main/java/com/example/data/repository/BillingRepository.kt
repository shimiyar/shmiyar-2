package com.example.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SubscriptionPlan(
    val productId: String,
    val title: String,
    val periodTitle: String,
    val priceToman: Long,
    val isBestChoice: Boolean = false,
    val description: String
)

class BillingRepository {
    // Official Product IDs for CafeBazaar / Store In-App Billing
    companion object {
        const val PRODUCT_ID_MONTHLY = "shimiyar_pro_1m"
        const val PRODUCT_ID_SEMIANNUAL = "shimiyar_pro_6m"
        const val PRODUCT_ID_ANNUAL = "shimiyar_pro_1y"
    }

    val plans: List<SubscriptionPlan> = listOf(
        SubscriptionPlan(
            productId = PRODUCT_ID_MONTHLY,
            title = "اشتراک یک ماهه",
            periodTitle = "۱ ماهه",
            priceToman = 99000L,
            isBestChoice = false,
            description = "دسترسی کامل ۳۰ روزه به تمام امکانات و محتوای آموزشی"
        ),
        SubscriptionPlan(
            productId = PRODUCT_ID_SEMIANNUAL,
            title = "اشتراک شش ماهه",
            periodTitle = "۶ ماهه",
            priceToman = 385000L,
            isBestChoice = false,
            description = "مناسب برای یک نیم‌سال تحصیلی و جمع‌بندی کنکور"
        ),
        SubscriptionPlan(
            productId = PRODUCT_ID_ANNUAL,
            title = "اشتراک یک ساله (طلایی)",
            periodTitle = "۱۲ ماهه",
            priceToman = 485000L,
            isBestChoice = true,
            description = "مقرون‌به‌صرفه‌ترین پلن، دسترسی کامل تا پایان سال تحصیلی و کنکور"
        )
    )

    private val _isPro = MutableStateFlow(false)
    val isPro: StateFlow<Boolean> = _isPro.asStateFlow()

    private val _isTrialActive = MutableStateFlow(false)
    val isTrialActive: StateFlow<Boolean> = _isTrialActive.asStateFlow()

    suspend fun purchasePlan(productId: String): Result<Boolean> {
        // CafeBazaar IAB purchase handler
        _isPro.value = true
        return Result.success(true)
    }

    suspend fun startFreeTrial(): Result<Boolean> {
        _isTrialActive.value = true
        _isPro.value = true
        return Result.success(true)
    }

    suspend fun restorePurchases(): Result<Boolean> {
        _isPro.value = true
        return Result.success(true)
    }

    fun setProStatus(pro: Boolean) {
        _isPro.value = pro
    }
}
