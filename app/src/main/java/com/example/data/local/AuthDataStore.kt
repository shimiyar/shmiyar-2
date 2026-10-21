package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.userAuthDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_auth_settings")

/**
 * ذخیره و مدیریت وضعیت ورود و کاربر مهمان با استفاده از Jetpack DataStore Preferences
 */
class AuthDataStore(private val context: Context) {

    companion object {
        val IS_GUEST_USER_KEY = booleanPreferencesKey("is_guest_user")
        val IS_BAZAAR_USER_KEY = booleanPreferencesKey("is_bazaar_user")
        val BAZAAR_ACCESS_TOKEN_KEY = androidx.datastore.preferences.core.stringPreferencesKey("bazaar_access_token")
        val BAZAAR_ACCOUNT_ID_KEY = androidx.datastore.preferences.core.stringPreferencesKey("bazaar_account_id")
    }

    /**
     * جریان واکشی وضعیت ورود کاربر مهمان
     */
    val isGuestUserFlow: Flow<Boolean> = context.userAuthDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[IS_GUEST_USER_KEY] ?: false
        }

    /**
     * جریان وضعیت ورود با حساب رسمی کافه‌بازار
     */
    val isBazaarUserFlow: Flow<Boolean> = context.userAuthDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[IS_BAZAAR_USER_KEY] ?: false
        }

    /**
     * جریان توکن دسترسی کافه‌بازار
     */
    val bazaarTokenFlow: Flow<String?> = context.userAuthDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[BAZAAR_ACCESS_TOKEN_KEY]
        }

    /**
     * جریان کلی احراز هویت (کاربر مهمان یا کاربر وارد شده با کافه‌بازار)
     */
    val isAuthenticatedFlow: Flow<Boolean> = context.userAuthDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            (preferences[IS_GUEST_USER_KEY] == true) || (preferences[IS_BAZAAR_USER_KEY] == true)
        }

    /**
     * ذخیره وضعیت ورود به عنوان کاربر مهمان
     */
    suspend fun setGuestUser(isGuest: Boolean) {
        context.userAuthDataStore.edit { preferences ->
            preferences[IS_GUEST_USER_KEY] = isGuest
        }
    }

    /**
     * ذخیره وضعیت کاربر لاگین شده با کافه‌بازار به همراه توکن و شناسه حساب
     */
    suspend fun setBazaarLoggedIn(isLoggedIn: Boolean, token: String? = null, accountId: String? = null) {
        context.userAuthDataStore.edit { preferences ->
            preferences[IS_BAZAAR_USER_KEY] = isLoggedIn
            preferences[IS_GUEST_USER_KEY] = isLoggedIn // کاربر به عنوان احراز شده در کل برنامه شناخته می‌شود
            if (token != null) {
                preferences[BAZAAR_ACCESS_TOKEN_KEY] = token
            }
            if (accountId != null) {
                preferences[BAZAAR_ACCOUNT_ID_KEY] = accountId
            }
        }
    }

    /**
     * خروج از حساب و بازگشت به صفحه ورود
     */
    suspend fun logout() {
        context.userAuthDataStore.edit { preferences ->
            preferences[IS_GUEST_USER_KEY] = false
            preferences[IS_BAZAAR_USER_KEY] = false
            preferences.remove(BAZAAR_ACCESS_TOKEN_KEY)
            preferences.remove(BAZAAR_ACCOUNT_ID_KEY)
        }
    }
}
