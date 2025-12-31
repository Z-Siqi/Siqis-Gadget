package com.sqz.gadget.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.sqz.gadget.ui.NavRoute
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NavViewModel : ViewModel() {
    companion object {
        data class NavState(
            val navTo: NavRoute? = null,
            val requestBack: Boolean = false,
        )
    }

    private val _state = MutableStateFlow<NavState>(NavState())
    val state: StateFlow<NavState> = _state.asStateFlow()

    fun navigate(route: NavRoute) {
        _state.update { it.copy(navTo = route) }
    }

    private var _navBackProcess = false

    fun requestBack() {
        if (!_navBackProcess) {
            _navBackProcess = true
            _state.update { it.copy(requestBack = true) }
            viewModelScope.launch { // Fix multiple click nav to nothing issue
                delay(700)
                _navBackProcess = false
            }
        }
    }

    /**
     * Navigate controller handler
     * @param navController NavHostController, expect [rememberNavController]
     * @param state NavState, expect [collectAsState] for correct navigation state handling
     */
    fun navController(
        navController: NavHostController,
        state: State<NavState>
    ): NavHostController {
        if (state.value.navTo != null) _state.update {
            navController.navigate(it.navTo!!.name)
            it.copy(navTo = null)
        }
        if (state.value.requestBack) _state.update {
            navController.popBackStack()
            it.copy(requestBack = false)
        }
        return navController
    }
}
