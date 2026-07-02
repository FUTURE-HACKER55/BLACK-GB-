package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.ChatRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class Screen {
    object Auth : Screen()
    object Main : Screen()
    data class ChatDetails(val conversationId: String) : Screen()
    object NewGroup : Screen()
    data class StatusViewer(val statusId: String) : Screen()
    object NewStatus : Screen()
}

enum class MainTab {
    CHATS, STATUS, CONTACTS, PROFILE, SETTINGS
}

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ChatRepository(application)

    // --- Navigation State ---
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Auth)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _currentTab = MutableStateFlow(MainTab.CHATS)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

    // --- Auth State ---
    private val _currentUser = MutableStateFlow<ProfileEntity?>(null)
    val currentUser: StateFlow<ProfileEntity?> = _currentUser.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _authSuccessMessage = MutableStateFlow<String?>(null)
    val authSuccessMessage: StateFlow<String?> = _authSuccessMessage.asStateFlow()

    // --- Chats / Conversations State ---
    private val _conversations = MutableStateFlow<List<ConversationEntity>>(emptyList())
    val conversations: StateFlow<List<ConversationEntity>> = _conversations.asStateFlow()

    private val _activeConversation = MutableStateFlow<ConversationEntity?>(null)
    val activeConversation: StateFlow<ConversationEntity?> = _activeConversation.asStateFlow()

    private val _activeConversationMessages = MutableStateFlow<List<MessageEntity>>(emptyList())
    val activeConversationMessages: StateFlow<List<MessageEntity>> = _activeConversationMessages.asStateFlow()

    private val _activeConversationMembers = MutableStateFlow<List<ProfileEntity>>(emptyList())
    val activeConversationMembers: StateFlow<List<ProfileEntity>> = _activeConversationMembers.asStateFlow()

    // --- Search & Contacts State ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _contacts = MutableStateFlow<List<ProfileEntity>>(emptyList())
    val contacts: StateFlow<List<ProfileEntity>> = _contacts.asStateFlow()

    // --- Statuses State ---
    private val _activeStatuses = MutableStateFlow<List<StatusEntity>>(emptyList())
    val activeStatuses: StateFlow<List<StatusEntity>> = _activeStatuses.asStateFlow()

    private val _myStatuses = MutableStateFlow<List<StatusEntity>>(emptyList())
    val myStatuses: StateFlow<List<StatusEntity>> = _myStatuses.asStateFlow()

    // Active status views count
    private val _activeStatusViews = MutableStateFlow<List<StatusViewEntity>>(emptyList())
    val activeStatusViews: StateFlow<List<StatusViewEntity>> = _activeStatusViews.asStateFlow()

    init {
        // Recover previous user session if exists
        val savedUserId = repository.getCurrentUserId()
        if (savedUserId != null) {
            viewModelScope.launch {
                val profile = repository.getProfile(savedUserId)
                if (profile != null) {
                    _currentUser.value = profile
                    _currentScreen.value = Screen.Main
                    observeUserData(profile.id)
                } else {
                    repository.logout()
                }
            }
        }

        // Live search contacts
        viewModelScope.launch {
            _searchQuery
                .debounce(300)
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    repository.searchContacts(query)
                }
                .collect { results ->
                    _contacts.value = results
                }
        }
    }

    private fun observeUserData(userId: String) {
        // Collect conversations in real time
        viewModelScope.launch {
            repository.getConversations(userId).collect { list ->
                _conversations.value = list
            }
        }

        // Collect active statuses in real time
        viewModelScope.launch {
            repository.getActiveStatuses().collect { statuses ->
                _activeStatuses.value = statuses
            }
        }

        // Collect personal statuses
        viewModelScope.launch {
            repository.getMyStatuses(userId).collect { statuses ->
                _myStatuses.value = statuses
            }
        }
    }

    // --- Navigation Actions ---
    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun selectTab(tab: MainTab) {
        _currentTab.value = tab
    }

    // --- Auth Actions ---
    fun registerUser(email: String, username: String, displayName: String, psw: String) {
        _authError.value = null
        _authSuccessMessage.value = null
        if (psw.length < 8) {
            _authError.value = "Le mot de passe doit contenir au moins 8 caractères."
            return
        }
        viewModelScope.launch {
            repository.register(email, username, displayName, psw)
                .onSuccess { profile ->
                    _currentUser.value = profile
                    _currentScreen.value = Screen.Main
                    observeUserData(profile.id)
                }
                .onFailure { exc ->
                    _authError.value = exc.localizedMessage ?: "Une erreur est survenue lors de l'inscription."
                }
        }
    }

    fun loginUser(email: String, psw: String) {
        _authError.value = null
        _authSuccessMessage.value = null
        viewModelScope.launch {
            repository.login(email, psw)
                .onSuccess { profile ->
                    _currentUser.value = profile
                    _currentScreen.value = Screen.Main
                    observeUserData(profile.id)
                }
                .onFailure { exc ->
                    _authError.value = exc.localizedMessage ?: "Email ou mot de passe incorrect."
                }
        }
    }

    fun logout() {
        repository.logout()
        _currentUser.value = null
        _currentScreen.value = Screen.Auth
        _currentTab.value = MainTab.CHATS
    }

    fun requestPasswordReset(email: String) {
        _authError.value = null
        _authSuccessMessage.value = null
        viewModelScope.launch {
            val resultMessage = repository.simulatePasswordReset(email)
            _authSuccessMessage.value = resultMessage
        }
    }

    fun verifyForgotPasswordToken(email: String, token: String, newPsw: String) {
        _authError.value = null
        _authSuccessMessage.value = null
        if (newPsw.length < 8) {
            _authError.value = "Le mot de passe doit contenir au moins 8 caractères."
            return
        }
        viewModelScope.launch {
            repository.confirmPasswordReset(email, token, newPsw)
                .onSuccess {
                    _authSuccessMessage.value = "Mot de passe réinitialisé avec succès ! Connectez-vous."
                }
                .onFailure { exc ->
                    _authError.value = exc.localizedMessage ?: "Échec de la réinitialisation."
                }
        }
    }

    fun verifyEmailSimulated() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.verifyEmail(user.id)
            val updatedProfile = repository.getProfile(user.id)
            _currentUser.value = updatedProfile
        }
    }

    fun toggleBlueVerified() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val updated = user.copy(isBlueVerified = !user.isBlueVerified)
            repository.updateProfile(updated)
            _currentUser.value = updated
        }
    }

    fun updateProfileInfo(displayName: String, bio: String, avatarUrl: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val updated = user.copy(
                displayName = displayName,
                bio = bio,
                avatarUrl = avatarUrl
            )
            repository.updateProfile(updated)
            _currentUser.value = updated
        }
    }

    // --- Messaging Actions ---
    fun openConversation(convoId: String) {
        viewModelScope.launch {
            val convo = repository.getOrCreateDirectConversation(
                _currentUser.value?.id ?: "", 
                convoId // Here convoId can be recipient user id
            )
            val conversation = repository.getConversations(_currentUser.value?.id ?: "").firstOrNull()?.find { it.id == convo }
                ?: ConversationEntity(convo, false, "Chat", "", _currentUser.value?.id ?: "", System.currentTimeMillis())

            _activeConversation.value = conversation
            _currentScreen.value = Screen.ChatDetails(convo)

            // Collect active messages
            repository.getMessages(convo).collect { msgs ->
                _activeConversationMessages.value = msgs
            }
        }

        viewModelScope.launch {
            val convo = repository.getOrCreateDirectConversation(_currentUser.value?.id ?: "", convoId)
            _activeConversationMembers.value = repository.getMembersForConversation(convo)
        }
    }

    fun openDirectConversationById(convoId: String) {
        viewModelScope.launch {
            val convoObj = repository.getConversations(_currentUser.value?.id ?: "").firstOrNull()?.find { it.id == convoId }
            _activeConversation.value = convoObj
            _currentScreen.value = Screen.ChatDetails(convoId)

            repository.getMessages(convoId).collect { msgs ->
                _activeConversationMessages.value = msgs
            }
        }

        viewModelScope.launch {
            _activeConversationMembers.value = repository.getMembersForConversation(convoId)
        }
    }

    fun postMessage(content: String, mediaUrl: String? = null, mediaType: String? = null) {
        val user = _currentUser.value ?: return
        val convo = _activeConversation.value ?: return
        viewModelScope.launch {
            repository.sendMessage(convo.id, user.id, content, mediaUrl, mediaType)
        }
    }

    fun createGroup(name: String, memberIds: List<String>) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val list = memberIds.toMutableList()
            list.add(user.id)
            val convoId = repository.createGroupConversation(name, list, user.id)
            _currentScreen.value = Screen.Main
            _currentTab.value = MainTab.CHATS
            openDirectConversationById(convoId)
        }
    }

    // --- Search Queries ---
    fun updateSearchQuery(q: String) {
        _searchQuery.value = q
    }

    // --- Status Actions ---
    fun postTextStatus(content: String, colorHex: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.postStatus(user.id, content, null, colorHex)
            _currentScreen.value = Screen.Main
            _currentTab.value = MainTab.STATUS
        }
    }

    fun postMediaStatus(content: String, mediaUrl: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.postStatus(user.id, content, mediaUrl, null)
            _currentScreen.value = Screen.Main
            _currentTab.value = MainTab.STATUS
        }
    }

    fun deleteStatusById(statusId: String) {
        viewModelScope.launch {
            repository.deleteStatus(statusId)
        }
    }

    fun viewStatusItem(statusId: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.viewStatus(statusId, user.id)
        }

        viewModelScope.launch {
            repository.getViewsForStatus(statusId).collect { views ->
                _activeStatusViews.value = views
            }
        }
    }

    private val _registeredProfiles = MutableStateFlow<List<ProfileEntity>>(emptyList())
    val registeredProfiles: StateFlow<List<ProfileEntity>> = _registeredProfiles.asStateFlow()

    fun loadRegisteredProfiles() {
        viewModelScope.launch {
            _registeredProfiles.value = repository.getAllRegisteredProfiles()
        }
    }
}
