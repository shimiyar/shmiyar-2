package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.DataLoader
import com.example.data.local.ElementEntity
import com.example.data.local.PreferencesHelper
import com.example.data.model.*
import com.example.reminder.DueLeitnerInfo
import com.example.reminder.LeitnerReminderManager
import com.example.search.SearchCategoryFilter
import com.example.search.SmartSearchEngine
import com.example.search.SmartSearchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

import com.example.domain.MolarMassCalculator

enum class ShimiTab(val title: String) {
    TABLE("جدول"),
    EDUCATION("آموزش"),
    FLASHCARDS("فلشکارت"),
    QUIZ("آزمون"),
    TOOLS("ابزارها"),
    ORGANIC("شیمی آلی"),
    GLOSSARY("واژهنامه"),
    PROFILE("پروفایل")
}

class ShimiYarViewModel(application: Application) : AndroidViewModel(application) {
    val prefs = PreferencesHelper(application)
    val database = AppDatabase.getDatabase(application)
    val elementDao = database.elementDao()

    // Room elements reactive stream
    val roomElements: StateFlow<List<ElementEntity>> = elementDao.getAllElements()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Elements, Flashcards, Education, Glossary loaded 100% offline from assets
    val elements: List<ElementItem> = DataLoader.loadElements(application)
    val flashcards: List<FlashcardItem> = DataLoader.loadFlashcards(application)
    val educationChapters: List<EducationChapter> = DataLoader.loadEducation(application)
    val glossaryTerms: List<GlossaryItem> = DataLoader.loadGlossary(application)

    // Tab State
    private val _currentTab = MutableStateFlow(ShimiTab.TABLE)
    val currentTab: StateFlow<ShimiTab> = _currentTab.asStateFlow()

    // Navigation and Modals
    private val _showOnboarding = MutableStateFlow(!prefs.onboardingDone)
    val showOnboarding: StateFlow<Boolean> = _showOnboarding.asStateFlow()

    private val _showPremiumModal = MutableStateFlow(false)
    val showPremiumModal: StateFlow<Boolean> = _showPremiumModal.asStateFlow()

    private val _showDailyOfferModal = MutableStateFlow(false)
    val showDailyOfferModal: StateFlow<Boolean> = _showDailyOfferModal.asStateFlow()

    private val _showDailyTipModal = MutableStateFlow(false)
    val showDailyTipModal: StateFlow<Boolean> = _showDailyTipModal.asStateFlow()

    private val _selectedElement = MutableStateFlow<ElementItem?>(null)
    val selectedElement: StateFlow<ElementItem?> = _selectedElement.asStateFlow()

    // Room fetched element state
    private val _selectedElementFromRoom = MutableStateFlow<ElementEntity?>(null)
    val selectedElementFromRoom: StateFlow<ElementEntity?> = _selectedElementFromRoom.asStateFlow()

    private val _isLoadingElementFromRoom = MutableStateFlow(false)
    val isLoadingElementFromRoom: StateFlow<Boolean> = _isLoadingElementFromRoom.asStateFlow()

    private val _achievementMessage = MutableStateFlow<String?>(null)
    val achievementMessage: StateFlow<String?> = _achievementMessage.asStateFlow()

    // Stats State
    private val _xp = MutableStateFlow(prefs.xp)
    val xp: StateFlow<Int> = _xp.asStateFlow()

    private val _level = MutableStateFlow(prefs.level)
    val level: StateFlow<Int> = _level.asStateFlow()

    private val _streak = MutableStateFlow(prefs.checkAndUpdateStreak())
    val streak: StateFlow<Int> = _streak.asStateFlow()

    private val _isPro = MutableStateFlow(prefs.isPro)
    val isPro: StateFlow<Boolean> = _isPro.asStateFlow()

    // Education Screen
    private val _selectedGrade = MutableStateFlow(10) // 10, 11, 12
    val selectedGrade: StateFlow<Int> = _selectedGrade.asStateFlow()

    private val _targetEducationChapterId = MutableStateFlow<String?>(null)
    val targetEducationChapterId: StateFlow<String?> = _targetEducationChapterId.asStateFlow()

    // Smart Search State
    private val _showSmartSearch = MutableStateFlow(false)
    val showSmartSearch: StateFlow<Boolean> = _showSmartSearch.asStateFlow()

    private val _smartSearchQuery = MutableStateFlow("")
    val smartSearchQuery: StateFlow<String> = _smartSearchQuery.asStateFlow()

    private val _searchCategoryFilter = MutableStateFlow(SearchCategoryFilter.ALL)
    val searchCategoryFilter: StateFlow<SearchCategoryFilter> = _searchCategoryFilter.asStateFlow()

    private val _searchResults = MutableStateFlow<List<SmartSearchResult>>(emptyList())
    val searchResults: StateFlow<List<SmartSearchResult>> = _searchResults.asStateFlow()

    // Flashcards Screen
    private val _selectedFcCategory = MutableStateFlow("همه")
    val selectedFcCategory: StateFlow<String> = _selectedFcCategory.asStateFlow()

    private val _currentFcIndex = MutableStateFlow(0)
    val currentFcIndex: StateFlow<Int> = _currentFcIndex.asStateFlow()

    private val _isFcFlipped = MutableStateFlow(false)
    val isFcFlipped: StateFlow<Boolean> = _isFcFlipped.asStateFlow()

    private val _fcKnownCount = MutableStateFlow(prefs.flashcardsKnownCount)
    val fcKnownCount: StateFlow<Int> = _fcKnownCount.asStateFlow()

    private val _fcLearningCount = MutableStateFlow(
        prefs.flashcardsLearningCount.takeIf { it > 0 } ?: (if (prefs.flashcardsKnownCount > 0) 8 else 0)
    )
    val fcLearningCount: StateFlow<Int> = _fcLearningCount.asStateFlow()

    private val _fcTodayCount = MutableStateFlow(prefs.flashcardsTodayCount)
    val fcTodayCount: StateFlow<Int> = _fcTodayCount.asStateFlow()

    private val _deckFinished = MutableStateFlow(false)
    val deckFinished: StateFlow<Boolean> = _deckFinished.asStateFlow()

    // Glossary Screen
    private val _glossarySearch = MutableStateFlow("")
    val glossarySearch: StateFlow<String> = _glossarySearch.asStateFlow()

    private val _selectedGlossaryCategory = MutableStateFlow("همه")
    val selectedGlossaryCategory: StateFlow<String> = _selectedGlossaryCategory.asStateFlow()

    // Daily Tip selection based on day of month
    val todayTip: DailyTip = run {
        val day = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH)
        val idx = (day - 1) % DataLoader.dailyTips.size
        DataLoader.dailyTips[idx]
    }

    private val _eyeCareMode = MutableStateFlow(prefs.eyeCareMode)
    val eyeCareMode: StateFlow<Boolean> = _eyeCareMode.asStateFlow()

    // Leitner Notification State
    private val _leitnerNotifEnabled = MutableStateFlow(prefs.leitnerNotificationEnabled)
    val leitnerNotifEnabled: StateFlow<Boolean> = _leitnerNotifEnabled.asStateFlow()

    private val _leitnerNotifHour = MutableStateFlow(prefs.leitnerNotificationHour)
    val leitnerNotifHour: StateFlow<Int> = _leitnerNotifHour.asStateFlow()

    private val _leitnerNotifMinute = MutableStateFlow(prefs.leitnerNotificationMinute)
    val leitnerNotifMinute: StateFlow<Int> = _leitnerNotifMinute.asStateFlow()

    private val _dueLeitnerInfo = MutableStateFlow(LeitnerReminderManager.calculateDueCards(application))
    val dueLeitnerInfo: StateFlow<DueLeitnerInfo> = _dueLeitnerInfo.asStateFlow()

    init {
        LeitnerReminderManager.createNotificationChannel(application)
        if (prefs.leitnerNotificationEnabled) {
            LeitnerReminderManager.scheduleDailyReminder(
                application,
                prefs.leitnerNotificationHour,
                prefs.leitnerNotificationMinute
            )
        }
        // Seed Room Database with 118 elements if empty or outdated
        viewModelScope.launch(Dispatchers.IO) {
            val count = elementDao.getCount()
            val firstEl = elementDao.getElementByNumber(1)
            if (count < 118 || firstEl?.konkurTipsAndUses?.contains("فرآیند هابر") != true) {
                val entities = DataLoader.loadElementEntities(application)
                elementDao.insertElements(entities)
            }
        }
    }

    fun toggleEyeCareMode() {
        val next = !_eyeCareMode.value
        _eyeCareMode.value = next
        prefs.eyeCareMode = next
        triggerAchievement(if (next) "🌙 حالت مطالعه بدون خستگی چشم فعال شد" else "⚡ تم تیره متالیک آزمایشگاهی فعال شد")
    }

    fun setTab(tab: ShimiTab) {
        _currentTab.value = tab
    }

    private val _showSubscriptionExpiredDialog = MutableStateFlow(false)
    val showSubscriptionExpiredDialog: StateFlow<Boolean> = _showSubscriptionExpiredDialog.asStateFlow()

    fun finishOnboarding() {
        prefs.onboardingDone = true
        _showOnboarding.value = false
        _isPro.value = prefs.isPro
    }

    /**
     * بررسی هوشمند پایان مهلت اشتراک ۳ روزه و اطلاع‌رسانی خودکار به کاربر
     */
    fun checkSubscriptionExpiry(context: android.content.Context) {
        val isExpired = prefs.isSubscriptionExpired
        if (isExpired && !prefs.hasNotifiedExpired) {
            _showSubscriptionExpiredDialog.value = true
            com.example.reminder.SubscriptionReminderManager.checkAndNotifyIfExpired(context)
        }
        _isPro.value = prefs.isPro
    }

    fun closeSubscriptionExpiredDialog() {
        _showSubscriptionExpiredDialog.value = false
    }

    fun openPremiumModal() {
        _showPremiumModal.value = true
    }

    fun closePremiumModal() {
        _showPremiumModal.value = false
    }

    fun openDailyOfferModal() {
        if (_isPro.value) {
            openPremiumModal()
            return
        }
        _showDailyOfferModal.value = true
    }

    fun closeDailyOfferModal() {
        _showDailyOfferModal.value = false
    }

    fun getRemainingTrialDays(): Int? {
        if (prefs.isTrialActive) {
            val remainingMs = prefs.trialEndTime - System.currentTimeMillis()
            if (remainingMs > 0) {
                return ((remainingMs / (24 * 60 * 60 * 1000L)) + 1).toInt()
            }
        }
        return null
    }

    fun activateFreeTrial() {
        // فعال‌سازی ۳ روز دسترسی رایگان ویژه ورود با کافه‌بازار
        prefs.startThreeDayTrialForBazaar()
        _isPro.value = true
        closePremiumModal()
        closeDailyOfferModal()
        triggerAchievement("🎁 دسترسی رایگان ۳ روزه شیمی‌یار پرو فعال گردید")
    }

    fun purchaseSubscription(variant: String) {
        prefs.isPro = true
        _isPro.value = true
        closePremiumModal()
        closeDailyOfferModal()
        closeSubscriptionExpiredDialog()
        triggerAchievement("👑 عضویت شیمی‌یار پرو با موفقیت تمدید و فعال شد")
    }

    fun restorePurchases() {
        prefs.isPro = true
        _isPro.value = true
        closePremiumModal()
        closeDailyOfferModal()
        closeSubscriptionExpiredDialog()
        triggerAchievement("✅ اشتراک طلایی کافه‌بازار با موفقیت بازیابی و فعال گردید")
    }

    fun openElementDetail(el: ElementItem) {
        _selectedElement.value = el
        // Fetch from Room DB asynchronously to guarantee Room backing
        fetchElementFromRoom(el.number)
        // Gamification: 5 XP per element viewed
        addXp(5)
        // Smart Paywall: check if 5 elements viewed
        prefs.elementsViewedCount += 1
        if (prefs.elementsViewedCount == 5 && !_isPro.value) {
            openPremiumModal()
        }
    }

    fun fetchElementFromRoom(atomicNumber: Int) {
        viewModelScope.launch {
            _isLoadingElementFromRoom.value = true
            val entity = elementDao.getElementByNumber(atomicNumber)
            _selectedElementFromRoom.value = entity
            _isLoadingElementFromRoom.value = false
            addXp(5)
        }
    }

    fun closeRoomElementDetail() {
        _selectedElementFromRoom.value = null
        _selectedElement.value = null
    }

    fun closeElementDetail() {
        _selectedElement.value = null
        _selectedElementFromRoom.value = null
    }

    // Smart Search Methods
    fun openSmartSearch(initialQuery: String = "") {
        _smartSearchQuery.value = initialQuery
        performSearch(initialQuery, _searchCategoryFilter.value)
        _showSmartSearch.value = true
    }

    fun closeSmartSearch() {
        _showSmartSearch.value = false
    }

    fun setSmartSearchQuery(query: String) {
        _smartSearchQuery.value = query
        performSearch(query, _searchCategoryFilter.value)
    }

    fun setSearchFilter(filter: SearchCategoryFilter) {
        _searchCategoryFilter.value = filter
        performSearch(_smartSearchQuery.value, filter)
    }

    private fun performSearch(query: String, filter: SearchCategoryFilter) {
        _searchResults.value = SmartSearchEngine.search(
            query = query,
            elements = elements,
            chapters = educationChapters,
            filter = filter
        )
    }

    fun navigateToElementFromSearch(element: ElementItem) {
        _selectedElement.value = element
        _currentTab.value = ShimiTab.TABLE
        _showSmartSearch.value = false
        addXp(5)
        triggerAchievement("⚛️ ${element.name} (${element.symbol}) در جدول تناوبی")
    }

    fun navigateToEducationFromSearch(grade: Int, chapterId: String) {
        _selectedGrade.value = grade
        _targetEducationChapterId.value = chapterId
        _currentTab.value = ShimiTab.EDUCATION
        _showSmartSearch.value = false
        addXp(5)
        triggerAchievement("📚 مبحث درسی در جزوات آموزشی باز شد")
    }

    fun navigateToFormulaFromSearch(formula: SmartSearchResult.FormulaResult) {
        if (formula.relatedChapterId != null && formula.grade != null) {
            navigateToEducationFromSearch(formula.grade, formula.relatedChapterId)
        } else if (formula.relatedElementSymbols.isNotEmpty()) {
            val symbol = formula.relatedElementSymbols.first()
            val elem = elements.firstOrNull { it.symbol.equals(symbol, ignoreCase = true) }
            if (elem != null) {
                navigateToElementFromSearch(elem)
            } else {
                _currentTab.value = ShimiTab.TABLE
                _showSmartSearch.value = false
            }
        } else {
            _currentTab.value = ShimiTab.EDUCATION
            _showSmartSearch.value = false
        }
    }

    fun clearTargetEducationChapterId() {
        _targetEducationChapterId.value = null
    }

    fun saveElementNote(elementNumber: Int, note: String) {
        prefs.saveElementNote(elementNumber, note)
        addXp(10)
        triggerAchievement("📝 یادداشت ذخیره شد (+۱۰ XP)")
    }

    fun getElementNote(elementNumber: Int): String {
        return prefs.getElementNote(elementNumber)
    }

    fun onLockedContentClicked() {
        prefs.lockedContentClicks += 1
        if (!_isPro.value) {
            if (prefs.lockedContentClicks % 2 == 1) {
                openDailyOfferModal()
            } else {
                openPremiumModal()
            }
        }
    }

    fun setGrade(grade: Int) {
        _selectedGrade.value = grade
    }

    // Flashcards
    fun navigateToFlashcardsForLesson(grade: Int, lessonCategory: String) {
        _selectedGrade.value = grade
        _selectedFcCategory.value = lessonCategory
        _currentFcIndex.value = 0
        _isFcFlipped.value = false
        _deckFinished.value = false
        _currentTab.value = ShimiTab.FLASHCARDS
        triggerAchievement("🎴 فلش‌کارت‌های درس: $lessonCategory")
    }

    fun setFcCategory(cat: String) {
        _selectedFcCategory.value = cat
        _currentFcIndex.value = 0
        _isFcFlipped.value = false
        _deckFinished.value = false
    }

    fun flipCard() {
        _isFcFlipped.value = !_isFcFlipped.value
        addXp(5)
    }

    fun onCardRepeat(card: FlashcardItem? = null, cardsListSize: Int) {
        _isFcFlipped.value = false
        if (card != null) {
            prefs.setCardBox(card.id, 1)
            prefs.setCardLastReview(card.id, System.currentTimeMillis())
        }
        val currentLearning = _fcLearningCount.value + 1
        _fcLearningCount.value = currentLearning
        prefs.flashcardsLearningCount = currentLearning

        val newToday = _fcTodayCount.value + 1
        _fcTodayCount.value = newToday
        prefs.flashcardsTodayCount = newToday
        addXp(5)
        refreshDueLeitnerInfo()

        if (_currentFcIndex.value < cardsListSize - 1) {
            _currentFcIndex.value += 1
        } else {
            _deckFinished.value = true
        }
    }

    fun onCardKnown(card: FlashcardItem? = null, cardsListSize: Int) {
        _isFcFlipped.value = false
        if (card != null) {
            val currentBox = prefs.getCardBox(card.id)
            val newBox = (currentBox + 1).coerceAtMost(5)
            prefs.setCardBox(card.id, newBox)
            prefs.setCardLastReview(card.id, System.currentTimeMillis())
        }

        val newKnown = _fcKnownCount.value + 1
        _fcKnownCount.value = newKnown
        prefs.flashcardsKnownCount = newKnown

        val newToday = _fcTodayCount.value + 1
        _fcTodayCount.value = newToday
        prefs.flashcardsTodayCount = newToday

        addXp(10)
        refreshDueLeitnerInfo()

        if (_currentFcIndex.value < cardsListSize - 1) {
            _currentFcIndex.value += 1
        } else {
            _deckFinished.value = true
        }
    }

    fun restartDeck() {
        _currentFcIndex.value = 0
        _isFcFlipped.value = false
        _deckFinished.value = false
    }

    // Leitner Notification Methods
    fun toggleLeitnerNotification(enabled: Boolean) {
        _leitnerNotifEnabled.value = enabled
        prefs.leitnerNotificationEnabled = enabled
        val app = getApplication<Application>()
        if (enabled) {
            LeitnerReminderManager.scheduleDailyReminder(
                app,
                _leitnerNotifHour.value,
                _leitnerNotifMinute.value
            )
            val timeStr = String.format("%02d:%02d", _leitnerNotifHour.value, _leitnerNotifMinute.value)
            triggerAchievement("🔔 یادآوری روزانه لایتنر برای ساعت $timeStr فعال شد")
        } else {
            LeitnerReminderManager.cancelDailyReminder(app)
            triggerAchievement("🔕 اعلان روزانه لایتنر غیرفعال شد")
        }
    }

    fun setLeitnerReminderTime(hour: Int, minute: Int) {
        _leitnerNotifHour.value = hour
        _leitnerNotifMinute.value = minute
        prefs.leitnerNotificationHour = hour
        prefs.leitnerNotificationMinute = minute
        val app = getApplication<Application>()
        if (_leitnerNotifEnabled.value) {
            LeitnerReminderManager.scheduleDailyReminder(app, hour, minute)
        }
        val timeStr = String.format("%02d:%02d", hour, minute)
        triggerAchievement("⏰ زمان یادآوری به $timeStr تغییر یافت")
    }

    fun sendTestLeitnerNotification() {
        val app = getApplication<Application>()
        LeitnerReminderManager.sendNotification(app, isTest = true)
        triggerAchievement("⚡ اعلان آزمایشی لایتنر ارسال شد! نوار اعلان‌ها را ببینید")
    }

    fun refreshDueLeitnerInfo() {
        val app = getApplication<Application>()
        _dueLeitnerInfo.value = LeitnerReminderManager.calculateDueCards(app)
    }

    // Glossary
    fun setGlossarySearch(q: String) {
        _glossarySearch.value = q
    }

    fun setGlossaryCategory(cat: String) {
        _selectedGlossaryCategory.value = cat
    }

    // Daily Tip
    fun openDailyTip() {
        _showDailyTipModal.value = true
        addXp(10)
        triggerAchievement("💡 نکته روز خوانده شد (+۱۰ XP)")
    }

    fun closeDailyTip() {
        _showDailyTipModal.value = false
    }

    fun addXp(amount: Int) {
        val newXp = _xp.value + amount
        _xp.value = newXp
        prefs.xp = newXp
        _level.value = prefs.level
    }

    fun triggerAchievement(msg: String) {
        viewModelScope.launch {
            _achievementMessage.value = msg
            delay(3000)
            _achievementMessage.value = null
        }
    }

    // --- Molar Mass & Chemistry Tools State ---
    private val _molarMassInput = MutableStateFlow("H2SO4")
    val molarMassInput: StateFlow<String> = _molarMassInput.asStateFlow()

    private val _molarMassResult = MutableStateFlow<MolarMassCalculator.CalculationResult?>(null)
    val molarMassResult: StateFlow<MolarMassCalculator.CalculationResult?> = _molarMassResult.asStateFlow()

    private val _molarMassError = MutableStateFlow<String?>(null)
    val molarMassError: StateFlow<String?> = _molarMassError.asStateFlow()

    init {
        calculateMolarMass("H2SO4")
    }

    fun setMolarMassInput(input: String) {
        _molarMassInput.value = input
        calculateMolarMass(input)
    }

    fun calculateMolarMass(formula: String = _molarMassInput.value) {
        if (formula.isBlank()) {
            _molarMassResult.value = null
            _molarMassError.value = null
            return
        }
        val res = MolarMassCalculator.calculate(formula.trim())
        if (res.isSuccess) {
            _molarMassResult.value = res.getOrNull()
            _molarMassError.value = null
        } else {
            _molarMassResult.value = null
            _molarMassError.value = res.exceptionOrNull()?.message ?: "فرمول شیمیایی نامعتبر است"
        }
    }

    // --- Konkur Mock Exam Simulator State ---
    private val _examSession = MutableStateFlow(ExamSession())
    val examSession: StateFlow<ExamSession> = _examSession.asStateFlow()

    private var examTimerJob: Job? = null

    private val _mistakeIds = MutableStateFlow<Set<Int>>(emptySet())
    val mistakeIds: StateFlow<Set<Int>> = _mistakeIds.asStateFlow()

    fun startExam(title: String, questions: List<ExamQuestion>, timeMinutes: Int) {
        examTimerJob?.cancel()
        val totalSec = timeMinutes * 60
        _examSession.value = ExamSession(
            isActive = true,
            title = title,
            questions = questions,
            currentIndex = 0,
            answers = emptyMap(),
            bookmarkedIds = emptySet(),
            remainingSeconds = totalSec,
            totalSeconds = totalSec,
            isFinished = false
        )

        examTimerJob = viewModelScope.launch {
            while (_examSession.value.remainingSeconds > 0 && !_examSession.value.isFinished) {
                delay(1000)
                val current = _examSession.value
                if (current.remainingSeconds > 0) {
                    _examSession.value = current.copy(remainingSeconds = current.remainingSeconds - 1)
                } else {
                    finishExam()
                    break
                }
            }
        }
    }

    fun answerExamQuestion(questionId: Int, optionIndex: Int) {
        val current = _examSession.value
        val updatedAnswers = current.answers.toMutableMap()
        if (updatedAnswers[questionId] == optionIndex) {
            updatedAnswers.remove(questionId) // deselect
        } else {
            updatedAnswers[questionId] = optionIndex
        }
        _examSession.value = current.copy(answers = updatedAnswers)
    }

    fun toggleExamBookmark(questionId: Int) {
        val current = _examSession.value
        val updated = current.bookmarkedIds.toMutableSet()
        if (updated.contains(questionId)) {
            updated.remove(questionId)
        } else {
            updated.add(questionId)
        }
        _examSession.value = current.copy(bookmarkedIds = updated)
    }

    fun nextExamQuestion() {
        val current = _examSession.value
        if (current.currentIndex < current.questions.size - 1) {
            _examSession.value = current.copy(currentIndex = current.currentIndex + 1)
        }
    }

    fun prevExamQuestion() {
        val current = _examSession.value
        if (current.currentIndex > 0) {
            _examSession.value = current.copy(currentIndex = current.currentIndex - 1)
        }
    }

    fun jumpToExamQuestion(index: Int) {
        val current = _examSession.value
        if (index in current.questions.indices) {
            _examSession.value = current.copy(currentIndex = index)
        }
    }

    fun finishExam() {
        examTimerJob?.cancel()
        val current = _examSession.value
        var correct = 0
        var wrong = 0
        val newMistakes = _mistakeIds.value.toMutableSet()

        current.questions.forEach { q ->
            val userAns = current.answers[q.id]
            if (userAns != null) {
                if (userAns == q.correctOptionIndex) {
                    correct++
                } else {
                    wrong++
                    newMistakes.add(q.id)
                }
            }
        }

        _mistakeIds.value = newMistakes
        val total = current.questions.size
        val unanswered = total - correct - wrong

        // Konkur formula with negative marking: (3*R - W) / (3*T) * 100
        val rawPercent = if (total > 0) {
            ((3.0 * correct - wrong) / (3.0 * total)) * 100.0
        } else 0.0

        val finalPercent = kotlin.math.round(rawPercent * 10) / 10.0
        val taraz = (4000 + (finalPercent * 45).toInt()).coerceIn(3000, 9500)

        _examSession.value = current.copy(
            isFinished = true,
            scorePercent = finalPercent,
            correctCount = correct,
            wrongCount = wrong,
            unansweredCount = unanswered,
            estimatedTaraz = taraz
        )

        // Award XP based on participation and correct answers
        val xpGain = (correct * 15 + 20).coerceAtLeast(20)
        addXp(xpGain)
        triggerAchievement("🎉 آزمون به پایان رسید! تراز: $taraz (+${xpGain} XP)")
    }

    fun closeExam() {
        examTimerJob?.cancel()
        _examSession.value = ExamSession(isActive = false)
    }
}
