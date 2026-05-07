package com.charan.setupBox.presentation.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charan.setupBox.data.local.entity.SetupBoxContent
import com.charan.setupBox.data.repository.SupabaseRepo
import com.charan.setupBox.repository.SetUpBoxContentRepository
import com.charan.setupBox.utils.ProcessState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val supabaseRepo: SupabaseRepo,
    private val setUpBoxContentRepository: SetUpBoxContentRepository
) : ViewModel() {

    data class UiState(
        val allData: List<SetupBoxContent> = emptyList(),
        val openModalSheet: Boolean = false,
        val logoutState: ProcessState? = null
    )

    sealed interface Intent {
        data object Refresh : Intent
        data object ToggleModalSheet : Intent
        data object Logout : Intent
        data object ConsumeLogoutState : Intent
    }

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            setUpBoxContentRepository.getAllData().collectLatest {
                _uiState.update { state -> state.copy(allData = it) }
            }
        }
        refresh()
    }

    fun onIntent(intent: Intent) {
        when (intent) {
            Intent.Refresh -> refresh()
            Intent.ToggleModalSheet -> _uiState.update { it.copy(openModalSheet = !it.openModalSheet) }
            Intent.Logout -> logout()
            Intent.ConsumeLogoutState -> _uiState.update { it.copy(logoutState = null) }
        }
    }

    private fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            supabaseRepo.getDataFromSupabase()
        }
    }

    private fun logout() {
        _uiState.update { it.copy(logoutState = ProcessState.Loading) }
        viewModelScope.launch(Dispatchers.IO) {
            supabaseRepo.logout().collectLatest {
                _uiState.update { state -> state.copy(logoutState = it) }
            }
        }
    }
}
