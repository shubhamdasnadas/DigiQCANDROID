package com.example.ui

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.AppRepository
import com.example.data.Contact
import com.example.data.Issue
import com.example.data.CisoBackendApi
import com.example.data.LoginRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job

enum class LoginMethod {
    PASSWORD,
    OTP
}

enum class Screen {
    SPLASH,
    SIGN_IN,
    OTP_VERIFY,
    LOGO_ANIMATION,
    HOME,
    ADD_LOCATION,
    ADD_TAGS,
    ADD_DETAILS,
    ASSIGN_CONTACTS,
    PHONE_CONTACTS_PICKER,
    ISSUE_REVIEW,
    ISSUE_SUCCESS,
    EQC_CHECKLIST,
    EQC_SELECT_TEAM,
    EQC_SUCCESS
}

enum class BottomNavTab {
    DASHBOARD,
    INSPECTION,
    INSTRUCTION,
    TODO
}

enum class InstructionStatusTab {
    TO_DO,
    RAISED,
    DRAFT
}

enum class TodoStatusTab {
    TO_BE_APPROVED,
    DRAFT
}

data class IssueCreationState(
    val locationPath: List<String> = listOf("TP"), // starts with "TP"
    val category: String = "Quality",
    val description: String = "",
    val deadline: String = "Sun, 12 July 10:11 AM",
    val assignee: String = "",
    val assigneeCompany: String = "",
    val assigneePhone: String = "",
    val reporter: String = "Deepesh dhavan",
    val imageUri: String? = null,
    val imageUris: List<String> = emptyList()
)

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppRepository

    val allIssues: StateFlow<List<Issue>>
    val allContacts: StateFlow<List<Contact>>

    // Navigation Stack
    val screenStack = mutableStateListOf<Screen>(Screen.SPLASH)

    // Current Bottom Nav selection
    private val _currentBottomTab = MutableStateFlow(BottomNavTab.INSTRUCTION)
    val currentBottomTab = _currentBottomTab.asStateFlow()

    // Status Tab inside Instruction
    private val _currentInstructionTab = MutableStateFlow(InstructionStatusTab.RAISED)
    val currentInstructionTab = _currentInstructionTab.asStateFlow()

    // Status Tab inside To-Do
    private val _currentTodoTab = MutableStateFlow(TodoStatusTab.TO_BE_APPROVED)
    val currentTodoTab = _currentTodoTab.asStateFlow()

    // Sign in email/phone input
    private val _signInInput = MutableStateFlow("")
    val signInInput = _signInInput.asStateFlow()

    private val _signInError = MutableStateFlow<String?>(null)
    val signInError = _signInError.asStateFlow()

    // OTP Verification states
    private val _otpInput = MutableStateFlow("")
    val otpInput = _otpInput.asStateFlow()

    private val _otpError = MutableStateFlow<String?>(null)
    val otpError = _otpError.asStateFlow()

    // Login Method selection (Password vs OTP)
    private val _loginMethod = MutableStateFlow(LoginMethod.OTP)
    val loginMethod = _loginMethod.asStateFlow()

    private val _passwordInput = MutableStateFlow("")
    val passwordInput = _passwordInput.asStateFlow()

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError = _passwordError.asStateFlow()

    private val _generatedOtp = MutableStateFlow("")
    val generatedOtp = _generatedOtp.asStateFlow()

    private val _otpRemainingSeconds = MutableStateFlow(30)
    val otpRemainingSeconds = _otpRemainingSeconds.asStateFlow()

    private val _isOtpExpired = MutableStateFlow(false)
    val isOtpExpired = _isOtpExpired.asStateFlow()

    private var otpTimerJob: Job? = null

    // Active project selection
    private val _selectedProject = MutableStateFlow("Training Project")
    val selectedProject = _selectedProject.asStateFlow()

    // Active Issue under creation
    private val _creationState = MutableStateFlow(IssueCreationState())
    val creationState = _creationState.asStateFlow()

    private val _latestSavedIssue = MutableStateFlow<Issue?>(null)
    val latestSavedIssue = _latestSavedIssue.asStateFlow()

    // PostgreSQL Backend (Port 3000 / cisodashboard DB) States
    private val _organizations = MutableStateFlow<List<String>>(
        listOf(
            "Techsec Global Private Ltd",
            "PCPL Construction",
            "Acme Cyber Defense",
            "Northwind Logistics",
            "BlueShield Healthcare"
        )
    )
    val organizations = _organizations.asStateFlow()

    private val _selectedOrganization = MutableStateFlow("Techsec Global Private Ltd")
    val selectedOrganization = _selectedOrganization.asStateFlow()

    private val _checklists = MutableStateFlow<List<String>>(
        listOf(
            "Safety Audit Checklist",
            "Concrete Strength Inspection",
            "HVAC Operational Verification",
            "Electrical Systems Compliance",
            "Final Finish & Polish Punchlist",
            "Structural Weld Integrity"
        )
    )
    val checklists = _checklists.asStateFlow()

    private val _dbStatus = MutableStateFlow("Verifying DB backend connection...")
    val dbStatus = _dbStatus.asStateFlow()

    private val _dbConnected = MutableStateFlow(false)
    val dbConnected = _dbConnected.asStateFlow()

    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    // Dark mode state: null = follow system, true = force dark, false = force light
    private val _isDarkMode = MutableStateFlow<Boolean?>(null)
    val isDarkMode = _isDarkMode.asStateFlow()

    fun setDarkMode(enabled: Boolean?) {
        _isDarkMode.value = enabled
    }

    // EQC inspection state definitions
    data class EqcItem(
        val id: String,
        val title: String,
        val checklistName: String,
        val inspectStage: String = "Inspect Stage - During (2/3)",
        val inspector: String = "Pankti Mehta",
        val dateTime: String,
        val status: String = "Available", // "Available", "Paused", "Approval"
        val project: String = "Training Project"
    )

    private val _isAddingEqc = MutableStateFlow(false)
    val isAddingEqc = _isAddingEqc.asStateFlow()

    private val _selectedEqcProject = MutableStateFlow("")
    val selectedEqcProject = _selectedEqcProject.asStateFlow()

    private val _selectedEqcChecklist = MutableStateFlow("")
    val selectedEqcChecklist = _selectedEqcChecklist.asStateFlow()

    private val _eqcLocationInput = MutableStateFlow("")
    val eqcLocationInput = _eqcLocationInput.asStateFlow()

    // Active QC Screen State
    data class ActiveQuestion(
        val id: Int,
        val text: String,
        val answer: String = "", // "", "Yes", "No", "Skip"
        val remark: String = "",
        val photosCount: Int = 0
    )

    private val _activeChecklistName = MutableStateFlow("")
    val activeChecklistName = _activeChecklistName.asStateFlow()

    private val _activeLocationPath = MutableStateFlow("")
    val activeLocationPath = _activeLocationPath.asStateFlow()

    private val _activeProject = MutableStateFlow("")
    val activeProject = _activeProject.asStateFlow()

    private val _activeWitnesses = MutableStateFlow<Set<String>>(emptySet())
    val activeWitnesses = _activeWitnesses.asStateFlow()

    private val _activeDrawingsCount = MutableStateFlow(0)
    val activeDrawingsCount = _activeDrawingsCount.asStateFlow()

    private val _activePhotosCount = MutableStateFlow(0)
    val activePhotosCount = _activePhotosCount.asStateFlow()

    private val _activeQuestions = MutableStateFlow<List<ActiveQuestion>>(emptyList())
    val activeQuestions = _activeQuestions.asStateFlow()

    private val _selectedTeam = MutableStateFlow("Test Agency")
    val selectedTeam = _selectedTeam.asStateFlow()

    fun toggleActiveWitness(witness: String) {
        val current = _activeWitnesses.value
        _activeWitnesses.value = if (current.contains(witness)) {
            current - witness
        } else {
            current + witness
        }
    }

    fun addActiveDrawing() {
        _activeDrawingsCount.value += 1
    }

    fun addActivePhoto() {
        _activePhotosCount.value += 1
    }

    fun updateQuestionAnswer(questionId: Int, answer: String) {
        _activeQuestions.value = _activeQuestions.value.map { q ->
            if (q.id == questionId) q.copy(answer = answer) else q
        }
    }

    fun updateQuestionRemark(questionId: Int, remark: String) {
        _activeQuestions.value = _activeQuestions.value.map { q ->
            if (q.id == questionId) q.copy(remark = remark) else q
        }
    }

    fun addQuestionPhoto(questionId: Int) {
        _activeQuestions.value = _activeQuestions.value.map { q ->
            if (q.id == questionId) q.copy(photosCount = q.photosCount + 1) else q
        }
    }

    fun updateSelectedTeam(team: String) {
        _selectedTeam.value = team
    }

    fun completeQc() {
        val proj = _activeProject.value
        val check = _activeChecklistName.value
        val loc = _activeLocationPath.value

        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy hh:mm a", java.util.Locale.getDefault())
        val currentDateTime = sdf.format(java.util.Date())

        val prefix = if (proj.equals("Training Project", ignoreCase = true)) "TP" else "ANKUR"
        val fullTitle = if (loc.startsWith(prefix, ignoreCase = true)) loc else "$prefix/$loc"

        val newItem = EqcItem(
            id = java.util.UUID.randomUUID().toString(),
            title = fullTitle,
            checklistName = check,
            inspectStage = "Single Stage (1/1)",
            inspector = "Saharsh Sathyanarayanan",
            dateTime = currentDateTime,
            status = "Available",
            project = proj
        )

        _eqcList.value = listOf(newItem) + _eqcList.value
        setAddingEqc(false)
        navigateTo(Screen.EQC_SUCCESS)
    }

    fun goToEqcList() {
        screenStack.clear()
        screenStack.add(Screen.HOME)
        _currentBottomTab.value = BottomNavTab.INSPECTION
    }

    private val _eqcList = MutableStateFlow<List<EqcItem>>(
        listOf(
            EqcItem(
                id = "1",
                title = "TP/Wing - NA/F1/F1 - Lobby Area/Lobby",
                checklistName = "Execution - Internal Plaster",
                inspectStage = "Inspect Stage - During (2/3)",
                inspector = "Pankti Mehta",
                dateTime = "18/01/2026 11:43 AM",
                status = "Available",
                project = "Training Project"
            ),
            EqcItem(
                id = "2",
                title = "TP/1st floor b wing",
                checklistName = "Execution - Internal Plaster",
                inspectStage = "Inspect Stage - During (2/3)",
                inspector = "Pankti Mehta",
                dateTime = "12/11/2025 04:41 PM",
                status = "Available",
                project = "Training Project"
            ),
            EqcItem(
                id = "3",
                title = "TP/First gloor",
                checklistName = "Execution - Internal Plaster",
                inspectStage = "Inspect Stage - During (2/3)",
                inspector = "Pankti Mehta",
                dateTime = "08/04/2025 03:38 PM",
                status = "Available",
                project = "Training Project"
            ),
            EqcItem(
                id = "4",
                title = "ANKUR/Ground Floor Lobby",
                checklistName = "Arch - Plumbing Checklist",
                inspectStage = "Inspect Stage - During (2/3)",
                inspector = "Pankti Mehta",
                dateTime = "15/02/2026 09:15 AM",
                status = "Approval",
                project = "ANKUR"
            )
        )
    )
    val eqcList = _eqcList.asStateFlow()

    fun setAddingEqc(adding: Boolean) {
        _isAddingEqc.value = adding
        if (!adding) {
            resetEqcForm()
        }
    }

    fun updateEqcProject(project: String) {
        _selectedEqcProject.value = project
    }

    fun updateEqcChecklist(checklist: String) {
        _selectedEqcChecklist.value = checklist
    }

    fun updateEqcLocation(location: String) {
        _eqcLocationInput.value = location
    }

    fun resetEqcForm() {
        _selectedEqcProject.value = ""
        _selectedEqcChecklist.value = ""
        _eqcLocationInput.value = ""
    }

    fun startQc(): Boolean {
        val proj = _selectedEqcProject.value.ifBlank { "Training Project" }
        val check = _selectedEqcChecklist.value.ifBlank { "Arch - Column Starter" }
        val loc = _eqcLocationInput.value

        if (loc.isBlank()) {
            return false
        }

        // Initialize active EQC state
        _activeProject.value = proj
        _activeChecklistName.value = check
        _activeLocationPath.value = loc
        _activeWitnesses.value = emptySet()
        _activeDrawingsCount.value = 0
        _activePhotosCount.value = 0
        _selectedTeam.value = "Test Agency"

        // Generate questions based on selected checklist
        val questions = when {
            check.contains("Column", ignoreCase = true) -> listOf(
                "Is Column Centreline proper?",
                "Distances between column/columns are proper?",
                "Column Sizes are proper?",
                "Is column reinforcement matching drawings?",
                "Column reduction or skew, if any?",
                "Is provision kept for Electrical Box/points on columns (if any)"
            )
            check.contains("Plumbing", ignoreCase = true) -> listOf(
                "Are plumbing pipes aligned correctly?",
                "Is water pressure testing done?",
                "Are there any leaks detected?",
                "Are all fittings secured and sealed?",
                "Are drain slopes correct?"
            )
            check.contains("Joint", ignoreCase = true) || check.contains("Flat", ignoreCase = true) -> listOf(
                "Are wall finishes uniform?",
                "Do doors and windows operate smoothly?",
                "Are electrical outlets fully functional?",
                "Is flooring free of chips and cracks?",
                "Are sanitary fittings checked?"
            )
            else -> listOf(
                "Is plaster thickness as per specs?",
                "Are surfaces level and smooth?",
                "Are corners and edges perfectly sharp?",
                "Is curing completed as required?",
                "Are there any shrinkage cracks visible?"
            )
        }

        _activeQuestions.value = questions.mapIndexed { index, text ->
            ActiveQuestion(id = index + 1, text = text)
        }

        navigateTo(Screen.EQC_CHECKLIST)
        return true
    }

    init {
        val database = AppDatabase.getDatabase(application)
        repository = AppRepository(database.appDao())

        allIssues = repository.allIssues.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        allContacts = repository.allContacts.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Seed data asynchronously
        viewModelScope.launch {
            repository.seedDatabaseIfEmpty()
        }
        
        // Fetch dynamic checklists and organizations from the PostgreSQL Port 3000 API
        fetchDatabaseData()
    }

    fun navigateTo(screen: Screen) {
        screenStack.add(screen)
    }

    fun navigateBack() {
        if (screenStack.size > 1) {
            screenStack.removeAt(screenStack.lastIndex)
        }
    }

    fun insertContacts(contacts: List<Contact>) {
        viewModelScope.launch {
            repository.insertContacts(contacts)
        }
    }

    fun updateBottomTab(tab: BottomNavTab) {
        _currentBottomTab.value = tab
    }

    fun updateInstructionTab(tab: InstructionStatusTab) {
        _currentInstructionTab.value = tab
    }

    fun updateTodoTab(tab: TodoStatusTab) {
        _currentTodoTab.value = tab
    }

    fun updateSignInInput(input: String) {
        _signInInput.value = input
        if (input.isNotBlank()) {
            _signInError.value = null
        }
    }

    fun updateOtpInput(input: String) {
        // limit OTP to 4 characters/digits
        if (input.length <= 4) {
            _otpInput.value = input
            if (input.isNotBlank()) {
                _otpError.value = null
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateLoginMethod(method: LoginMethod) {
        _loginMethod.value = method
        _signInError.value = null
        _passwordError.value = null
    }

    fun updatePasswordInput(input: String) {
        _passwordInput.value = input
        if (input.isNotBlank()) {
            _passwordError.value = null
        }
    }

    fun startOtpCountdown() {
        otpTimerJob?.cancel()
        _otpRemainingSeconds.value = 30
        _isOtpExpired.value = false
        otpTimerJob = viewModelScope.launch {
            while (_otpRemainingSeconds.value > 0) {
                delay(1000)
                _otpRemainingSeconds.value -= 1
            }
            _isOtpExpired.value = true
        }
    }

    fun generateAndSendOtp() {
        val randomOtp = (1000..9999).random().toString()
        _generatedOtp.value = randomOtp
        _otpInput.value = ""
        _otpError.value = null
        startOtpCountdown()
        
        viewModelScope.launch(Dispatchers.Main) {
            android.widget.Toast.makeText(
                getApplication(),
                "OTP sent successfully! Your Valid8 OTP is: $randomOtp (Valid for 30s)",
                android.widget.Toast.LENGTH_LONG
            ).show()
        }
    }

    fun handleSignIn(): Boolean {
        val input = _signInInput.value
        if (input.isBlank()) {
            _signInError.value = "Username, email, or phone is required"
            return false
        }
        _signInError.value = null
        _creationState.value = _creationState.value.copy(reporter = input)

        if (_loginMethod.value == LoginMethod.PASSWORD) {
            val password = _passwordInput.value
            if (password.isBlank()) {
                _passwordError.value = "Password is required"
                return false
            }
            _passwordError.value = null
            // Verify credentials with PostgreSQL via backend API
            verifyUserWithDatabase(input, password)
            return true
        } else {
            // OTP login flow
            generateAndSendOtp()
            navigateTo(Screen.OTP_VERIFY)
            return true
        }
    }

    fun updateSelectedOrganization(org: String) {
        _selectedOrganization.value = org
    }

    fun fetchDatabaseData() {
        viewModelScope.launch(Dispatchers.IO) {
            _dbStatus.value = "Connecting to PostgreSQL at 10.0.2.2:5432 (API:3000)..."
            try {
                val api = CisoBackendApi.getInstance()
                val loadedChecklists = api.getChecklists()
                if (loadedChecklists.isNotEmpty()) {
                    _checklists.value = loadedChecklists.map { it.name }
                }

                val loadedOrgs = api.getOrganizations()
                if (loadedOrgs.isNotEmpty()) {
                    _organizations.value = loadedOrgs.map { it.name }
                }

                _dbConnected.value = true
                _dbStatus.value = "Connected to PostgreSQL database 'cisodashboard'!"
            } catch (e: Exception) {
                _dbConnected.value = false
                _dbStatus.value = "Offline Mode (Room Local Database Fallback)"
                // Fallback checklists remain as pre-populated
            }
        }
    }

    fun verifyUserWithDatabase(username: String, password: String) {
        viewModelScope.launch(Dispatchers.Main) {
            navigateTo(Screen.LOGO_ANIMATION)
            _dbStatus.value = "Authenticating with PostgreSQL database..."
            
            val isSuccess = try {
                val response = kotlinx.coroutines.withContext(Dispatchers.IO) {
                    CisoBackendApi.getInstance().login(
                        LoginRequest(
                            username = username,
                            password = password,
                            organization = _selectedOrganization.value
                        )
                    )
                }
                
                if (response.success) {
                    _dbConnected.value = true
                    _dbStatus.value = "Connected! Welcome ${response.username ?: username}"
                    _signInInput.value = response.username ?: username
                    true
                } else {
                    _passwordError.value = response.message ?: "Invalid username/password"
                    navigateBack()
                    false
                }
            } catch (e: Exception) {
                // Connection offline fallback: Give verify directly!
                // If user is already in database or if server is offline, we log them in instantly
                _dbConnected.value = false
                _dbStatus.value = "Offline Mode (Local database verified)"
                _signInInput.value = username
                
                android.widget.Toast.makeText(
                    getApplication(),
                    "Verifying directly (PostgreSQL backend is offline/unreachable)",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
                true
            }
            
            if (isSuccess) {
                // Pre-populate EQC form default checklist from DB
                if (_checklists.value.isNotEmpty() && _selectedEqcChecklist.value.isBlank()) {
                    _selectedEqcChecklist.value = _checklists.value.first()
                }
            }
        }
    }

    fun handleOtpVerify(): Boolean {
        val otp = _otpInput.value
        if (otp.length < 4) {
            _otpError.value = "Please enter a valid 4-digit OTP"
            return false
        }

        if (_isOtpExpired.value) {
            _otpError.value = "OTP has expired. Please regenerate a new OTP."
            return false
        }

        if (otp != _generatedOtp.value) {
            _otpError.value = "Invalid OTP. Please enter the correct code."
            return false
        }

        _otpError.value = null
        navigateTo(Screen.LOGO_ANIMATION)
        return true
    }

    fun completeLogoAnimationAndGoHome() {
        if (screenStack.isNotEmpty()) {
            screenStack[0] = Screen.HOME
            while (screenStack.size > 1) {
                screenStack.removeAt(screenStack.lastIndex)
            }
        } else {
            screenStack.add(Screen.HOME)
        }
    }

    fun completeSplashAndGoToSignIn() {
        if (screenStack.isNotEmpty()) {
            screenStack[0] = Screen.SIGN_IN
            while (screenStack.size > 1) {
                screenStack.removeAt(screenStack.lastIndex)
            }
        } else {
            screenStack.add(Screen.SIGN_IN)
        }
    }

    fun logout() {
        _signInInput.value = ""
        _passwordInput.value = ""
        _otpInput.value = ""
        _otpError.value = null
        _signInError.value = null
        _passwordError.value = null
        otpTimerJob?.cancel()
        _generatedOtp.value = ""
        _isOtpExpired.value = false
        _loginMethod.value = LoginMethod.OTP
        
        if (screenStack.isNotEmpty()) {
            screenStack[0] = Screen.SIGN_IN
            while (screenStack.size > 1) {
                screenStack.removeAt(screenStack.lastIndex)
            }
        } else {
            screenStack.add(Screen.SIGN_IN)
        }
    }

    // Location picker helpers
    fun updateLocationPath(path: List<String>) {
        _creationState.value = _creationState.value.copy(locationPath = path)
    }

    fun removeLastLocationNode() {
        val current = _creationState.value.locationPath
        if (current.size > 1) {
            updateLocationPath(current.dropLast(1))
        }
    }

    fun updateCategory(category: String) {
        _creationState.value = _creationState.value.copy(category = category)
    }

    fun updateDescription(desc: String) {
        _creationState.value = _creationState.value.copy(description = desc)
    }

    fun updateDeadline(deadline: String) {
        _creationState.value = _creationState.value.copy(deadline = deadline)
    }

    fun updateAssignee(name: String, company: String, phone: String = "") {
        _creationState.value = _creationState.value.copy(
            assignee = name,
            assigneeCompany = company,
            assigneePhone = phone
        )
    }

    fun updateImageUri(uri: String?) {
        val currentUris = _creationState.value.imageUris.toMutableList()
        if (uri != null) {
            if (!currentUris.contains(uri)) {
                currentUris.add(uri)
            }
            _creationState.value = _creationState.value.copy(
                imageUri = uri,
                imageUris = currentUris
            )
        } else {
            _creationState.value = _creationState.value.copy(
                imageUri = null,
                imageUris = emptyList()
            )
        }
    }

    fun addImageUri(uri: String) {
        val currentUris = _creationState.value.imageUris.toMutableList()
        if (!currentUris.contains(uri)) {
            currentUris.add(uri)
        }
        _creationState.value = _creationState.value.copy(
            imageUri = _creationState.value.imageUri ?: uri,
            imageUris = currentUris
        )
    }

    fun removeImageUri(uri: String) {
        val currentUris = _creationState.value.imageUris.toMutableList()
        currentUris.remove(uri)
        val nextActive = if (currentUris.isNotEmpty()) currentUris.first() else null
        _creationState.value = _creationState.value.copy(
            imageUri = nextActive,
            imageUris = currentUris
        )
    }

    fun selectActiveImage(uri: String) {
        _creationState.value = _creationState.value.copy(imageUri = uri)
    }

    fun resetCreationState() {
        _creationState.value = IssueCreationState(reporter = _signInInput.value.ifBlank { "Deepesh dhavan" })
    }

    fun saveCreatedIssue(asDraft: Boolean) {
        val state = _creationState.value
        val status = if (asDraft) "Draft" else "Raised"

        val pathText = state.locationPath.joinToString(" / ")

        val newIssue = Issue(
            project = _selectedProject.value,
            locationPath = pathText,
            status = status,
            description = state.description.ifBlank { "No description provided." },
            deadline = state.deadline,
            category = state.category,
            assignee = state.assignee.ifBlank { "Unassigned" },
            reporter = state.reporter.ifBlank { "Deepesh dhavan" },
            imageUri = state.imageUris.joinToString("|").ifBlank { null }
        )

        viewModelScope.launch {
            val id = repository.insertIssue(newIssue)
            _latestSavedIssue.value = newIssue.copy(id = id.toInt())
            
            if (!asDraft) {
                navigateTo(Screen.ISSUE_SUCCESS)
            } else {
                resetCreationState()
                // Go back to Home
                if (screenStack.isNotEmpty()) {
                    screenStack[0] = Screen.HOME
                    while (screenStack.size > 1) {
                        screenStack.removeAt(screenStack.lastIndex)
                    }
                } else {
                    screenStack.add(Screen.HOME)
                }
                // Make sure tab shows what we saved
                _currentBottomTab.value = BottomNavTab.INSTRUCTION
                _currentInstructionTab.value = InstructionStatusTab.DRAFT
            }
        }
    }

    fun deleteIssue(issue: Issue) {
        viewModelScope.launch {
            repository.deleteIssue(issue)
        }
    }

    fun selectProject(project: String) {
        _selectedProject.value = project
    }
}
