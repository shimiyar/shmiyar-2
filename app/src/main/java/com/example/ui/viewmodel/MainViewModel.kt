package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.*
import com.example.data.model.*
import com.example.data.repository.BillingRepository
import com.example.data.repository.ChemistryRepository
import com.example.domain.EquationBalancer
import com.example.domain.MolarMassCalculator
import com.example.domain.UnitConverter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppTab(val title: String) {
    HOME("خانه"),
    PERIODIC_TABLE("جدول تناوبی"),
    FLASHCARDS("فلش‌کارت"),
    QUIZZES("آزمون"),
    TOOLS("ابزارها")
}

enum class PeriodicColorMode(val title: String) {
    CATEGORY("دسته عناصر"),
    ELECTRONEGATIVITY("الکترونگاتیوی"),
    ATOMIC_RADIUS("شعاع اتمی"),
    STATE_OF_MATTER("حالت ماده")
}

data class ExamSessionState(
    val isActive: Boolean = false,
    val isExamMode: Boolean = true, // true = timed exam, false = practice
    val questions: List<QuestionEntity> = emptyList(),
    val currentIndex: Int = 0,
    val userAnswers: Map<Int, Int> = emptyMap(), // questionId -> option (1..4)
    val remainingSeconds: Int = 0,
    val isFinished: Boolean = false,
    val totalTimeSeconds: Int = 0
)

class MainViewModel(
    private val repository: ChemistryRepository,
    private val billingRepository: BillingRepository
) : ViewModel() {

    // Tab Navigation
    private val _currentTab = MutableStateFlow(AppTab.HOME)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    // Sub-screens & Overlays
    private val _showOnboarding = MutableStateFlow(false)
    val showOnboarding: StateFlow<Boolean> = _showOnboarding.asStateFlow()

    private val _showPaywall = MutableStateFlow(false)
    val showPaywall: StateFlow<Boolean> = _showPaywall.asStateFlow()

    private val _showPaywallDialog = MutableStateFlow(false)
    val showPaywallDialog: StateFlow<Boolean> = _showPaywallDialog.asStateFlow()
    var paywallPromptReason: String = ""

    private val _selectedElement = MutableStateFlow<Element?>(null)
    val selectedElement: StateFlow<Element?> = _selectedElement.asStateFlow()

    private val _showNotesScreen = MutableStateFlow(false)
    val showNotesScreen: StateFlow<Boolean> = _showNotesScreen.asStateFlow()

    private val _showPackagesScreen = MutableStateFlow(false)
    val showPackagesScreen: StateFlow<Boolean> = _showPackagesScreen.asStateFlow()

    private val _showMistakesOnly = MutableStateFlow(false)
    val showMistakesOnly: StateFlow<Boolean> = _showMistakesOnly.asStateFlow()

    // Periodic Table State
    private val _periodicSearch = MutableStateFlow("")
    val periodicSearch: StateFlow<String> = _periodicSearch.asStateFlow()

    private val _periodicColorMode = MutableStateFlow(PeriodicColorMode.CATEGORY)
    val periodicColorMode: StateFlow<PeriodicColorMode> = _periodicColorMode.asStateFlow()

    private val _isKonkurMode = MutableStateFlow(false)
    val isKonkurMode: StateFlow<Boolean> = _isKonkurMode.asStateFlow()

    private val _selectedBlockFilter = MutableStateFlow<String?>(null) // "s", "p", "d", "f"
    val selectedBlockFilter: StateFlow<String?> = _selectedBlockFilter.asStateFlow()

    // Flashcards & Leitner State
    private val _selectedGradeFilter = MutableStateFlow<Int?>(null) // null = all, 10, 11, 12, 0
    val selectedGradeFilter: StateFlow<Int?> = _selectedGradeFilter.asStateFlow()

    private val _selectedBoxFilter = MutableStateFlow<Int?>(null) // null = all, 1..5
    val selectedBoxFilter: StateFlow<Int?> = _selectedBoxFilter.asStateFlow()

    private val _currentFlashcardIndex = MutableStateFlow(0)
    val currentFlashcardIndex: StateFlow<Int> = _currentFlashcardIndex.asStateFlow()

    private val _isFlashcardFlipped = MutableStateFlow(false)
    val isFlashcardFlipped: StateFlow<Boolean> = _isFlashcardFlipped.asStateFlow()

    // Exam Session
    private val _examSession = MutableStateFlow(ExamSessionState())
    val examSession: StateFlow<ExamSessionState> = _examSession.asStateFlow()
    private var timerJob: Job? = null

    // Tools State
    val molarMassInput = MutableStateFlow("Ca(OH)2")
    val molarMassResult = MutableStateFlow<MolarMassCalculator.CalculationResult?>(null)
    val molarMassError = MutableStateFlow<String?>(null)

    val equationInput = MutableStateFlow("Al + Fe2O3 = Al2O3 + Fe")
    val equationResult = MutableStateFlow<EquationBalancer.BalanceResult?>(null)
    val equationError = MutableStateFlow<String?>(null)

    val converterMoles = MutableStateFlow("1.5")
    val converterMolarMass = MutableStateFlow("58.5") // NaCl
    val converterVolume = MutableStateFlow("1.0")
    val converterResult = MutableStateFlow<UnitConverter.ConversionResult?>(null)

    // Data from Room
    val allFlashcards = repository.allFlashcards
    val allQuestions = repository.allQuestions
    val allNotes = repository.allNotes
    val allPackages = repository.allPackages
    val userStats = repository.userStats
    val isPro = billingRepository.isPro

    init {
        viewModelScope.launch {
            repository.initializeDatabaseIfEmpty()
            calculateMolarMass()
            balanceEquation()
            convertUnits()
        }
    }

    fun setTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun openElementDetail(element: Element) {
        _selectedElement.value = element
    }

    fun closeElementDetail() {
        _selectedElement.value = null
    }

    fun setPeriodicSearch(query: String) {
        _periodicSearch.value = query
    }

    fun setPeriodicColorMode(mode: PeriodicColorMode) {
        _periodicColorMode.value = mode
    }

    fun toggleKonkurMode() {
        _isKonkurMode.value = !_isKonkurMode.value
    }

    fun setBlockFilter(block: String?) {
        _selectedBlockFilter.value = block
    }

    fun setGradeFilter(grade: Int?) {
        _selectedGradeFilter.value = grade
    }

    fun setBoxFilter(box: Int?) {
        _selectedBoxFilter.value = box
        _currentFlashcardIndex.value = 0
        _isFlashcardFlipped.value = false
    }

    fun flipFlashcard() {
        _isFlashcardFlipped.value = !_isFlashcardFlipped.value
    }

    fun nextFlashcard(cardsList: List<FlashcardEntity>, knewIt: Boolean) {
        if (cardsList.isEmpty()) return
        val currentCard = cardsList[_currentFlashcardIndex.value % cardsList.size]

        viewModelScope.launch {
            repository.answerFlashcard(currentCard, knewIt)
            _isFlashcardFlipped.value = false
            if (_currentFlashcardIndex.value < cardsList.size - 1) {
                _currentFlashcardIndex.value += 1
            } else {
                _currentFlashcardIndex.value = 0
            }
        }
    }

    // Exam Management
    fun startExam(questions: List<QuestionEntity>, isTimedExam: Boolean, durationMinutes: Int = 15) {
        timerJob?.cancel()
        val totalSec = durationMinutes * 60
        _examSession.value = ExamSessionState(
            isActive = true,
            isExamMode = isTimedExam,
            questions = questions,
            currentIndex = 0,
            userAnswers = emptyMap(),
            remainingSeconds = totalSec,
            totalTimeSeconds = totalSec,
            isFinished = false
        )

        if (isTimedExam) {
            timerJob = viewModelScope.launch {
                while (_examSession.value.remainingSeconds > 0 && !_examSession.value.isFinished) {
                    delay(1000)
                    _examSession.value = _examSession.value.copy(
                        remainingSeconds = _examSession.value.remainingSeconds - 1
                    )
                }
                if (!_examSession.value.isFinished) {
                    finishExam()
                }
            }
        }
    }

    fun selectExamAnswer(questionId: Int, option: Int) {
        val current = _examSession.value
        val updatedAnswers = current.userAnswers.toMutableMap()
        updatedAnswers[questionId] = option
        _examSession.value = current.copy(userAnswers = updatedAnswers)

        // If in practice mode, record immediately
        if (!current.isExamMode) {
            val q = current.questions.find { it.id == questionId }
            if (q != null) {
                viewModelScope.launch {
                    repository.recordQuestionAnswer(questionId, option, option == q.correctOption)
                }
            }
        }
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

    fun finishExam() {
        timerJob?.cancel()
        val current = _examSession.value
        _examSession.value = current.copy(isFinished = true)

        viewModelScope.launch {
            for (q in current.questions) {
                val ans = current.userAnswers[q.id] ?: 0
                val isCorrect = ans == q.correctOption
                repository.recordQuestionAnswer(q.id, ans, isCorrect)
            }
        }
    }

    fun closeExam() {
        timerJob?.cancel()
        _examSession.value = ExamSessionState()
    }

    // Tools
    fun calculateMolarMass() {
        val result = MolarMassCalculator.calculate(molarMassInput.value)
        result.onSuccess {
            molarMassResult.value = it
            molarMassError.value = null
        }.onFailure {
            molarMassError.value = it.message
        }
    }

    fun balanceEquation() {
        val result = EquationBalancer.balance(equationInput.value)
        result.onSuccess {
            equationResult.value = it
            equationError.value = null
        }.onFailure {
            equationError.value = it.message
        }
    }

    fun convertUnits() {
        try {
            val moles = converterMoles.value.toDoubleOrNull() ?: 1.0
            val mw = converterMolarMass.value.toDoubleOrNull() ?: 58.5
            val vol = converterVolume.value.toDoubleOrNull() ?: 1.0
            converterResult.value = UnitConverter.convertFromMoles(moles, mw, vol)
        } catch (_: Exception) {}
    }

    // Paywall
    fun openPaywall(reason: String = "") {
        paywallPromptReason = reason
        _showPaywall.value = true
    }

    fun closePaywall() {
        _showPaywall.value = false
        _showPaywallDialog.value = false
    }

    fun showSoftPaywall(reason: String) {
        paywallPromptReason = reason
        _showPaywallDialog.value = true
    }

    fun purchasePlan(productId: String) {
        viewModelScope.launch {
            billingRepository.purchasePlan(productId)
            repository.setProUser(true)
            _showPaywall.value = false
            _showPaywallDialog.value = false
        }
    }

    fun startFreeTrial() {
        viewModelScope.launch {
            billingRepository.startFreeTrial()
            repository.setProUser(true)
            _showPaywall.value = false
            _showPaywallDialog.value = false
        }
    }

    fun restorePurchases() {
        viewModelScope.launch {
            billingRepository.restorePurchases()
            repository.setProUser(true)
            _showPaywall.value = false
        }
    }

    fun updateReminderSettings(hour: Int, minute: Int, enabled: Boolean) {
        viewModelScope.launch {
            repository.updateReminderSettings(hour, minute, enabled)
        }
    }

    // Bookmarks and Downloads
    fun toggleNoteBookmark(note: NoteEntity) {
        viewModelScope.launch {
            repository.toggleNoteBookmark(note.id, note.isBookmarked)
        }
    }

    fun toggleQuestionBookmark(question: QuestionEntity) {
        viewModelScope.launch {
            repository.toggleQuestionBookmark(question.id, question.isBookmarked)
        }
    }

    fun removeMistake(questionId: Int) {
        viewModelScope.launch {
            repository.removeMistake(questionId)
        }
    }

    fun downloadPackage(pkg: PackageEntity) {
        viewModelScope.launch {
            repository.downloadPackage(pkg.id)
        }
    }

    fun setShowNotesScreen(show: Boolean) {
        _showNotesScreen.value = show
    }

    fun setShowPackagesScreen(show: Boolean) {
        _showPackagesScreen.value = show
    }

    fun setShowMistakesOnly(show: Boolean) {
        _showMistakesOnly.value = show
    }

    fun completeOnboarding(grade: String, major: String) {
        viewModelScope.launch {
            repository.updateGradeAndMajor(grade, major)
            _showOnboarding.value = false
        }
    }

    fun setOnboardingVisible(visible: Boolean) {
        _showOnboarding.value = visible
    }

    class Factory(
        private val repository: ChemistryRepository,
        private val billingRepository: BillingRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(repository, billingRepository) as T
        }
    }
}
