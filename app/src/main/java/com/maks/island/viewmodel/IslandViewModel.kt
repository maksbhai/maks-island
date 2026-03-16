package com.maks.island.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.maks.island.MaksIslandApp
import com.maks.island.domain.models.CallState
import com.maks.island.domain.models.IslandSettings
import com.maks.island.domain.models.IslandVisualState
import com.maks.island.domain.models.MediaState
import com.maks.island.domain.models.NotificationItem
import com.maks.island.domain.models.PreviewScenario
import com.maks.island.domain.models.TimerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.PriorityQueue

class IslandViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = (application as MaksIslandApp).settingsRepository

    val settings: StateFlow<IslandSettings> = repo.settings.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        IslandSettings(),
    )
    val onboardingSeen = repo.onboardingSeen.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val appAllowList = repo.appAllowList.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    private val queue = PriorityQueue<QueuedState>(compareByDescending { it.priority })
    private val _islandState = MutableStateFlow<IslandVisualState>(IslandVisualState.Idle)
    val islandState = _islandState.asStateFlow()

    private val _previewScenario = MutableStateFlow(PreviewScenario.Idle)
    val previewScenario = _previewScenario.asStateFlow()

    val homeSummary = combine(settings, islandState) { s, state ->
        "Enabled: ${s.enabled} • Live state: ${state::class.simpleName ?: "Idle"}"
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Ready")

    fun completeOnboarding() = viewModelScope.launch { repo.setOnboardingSeen() }
    fun setBool(key: String, value: Boolean) = viewModelScope.launch { repo.updateBool(key, value) }
    fun setFloat(key: String, value: Float) = viewModelScope.launch { repo.updateFloat(key, value) }
    fun setInt(key: String, value: Int) = viewModelScope.launch { repo.updateInt(key, value) }
    fun setEnum(key: String, value: String) = viewModelScope.launch { repo.updateEnum(key, value) }
    fun setAppAllowed(packageName: String, allowed: Boolean) = viewModelScope.launch { repo.setAppAllowed(packageName, allowed) }

    fun setPreviewScenario(scenario: PreviewScenario) {
        _previewScenario.value = scenario
    }

    fun triggerPreviewScenario(scenario: PreviewScenario) {
        setPreviewScenario(scenario)
        when (scenario) {
            PreviewScenario.Idle -> clearToIdle()
            PreviewScenario.Notification -> pushNotification(NotificationItem("com.chat", "Messages", "Maks", "Meet at 8:30 PM?"))
            PreviewScenario.Media -> setMediaDemo()
            PreviewScenario.Charging -> setChargingDemo()
            PreviewScenario.Timer -> setTimerDemo()
            PreviewScenario.UrgentCall -> setCallDemo()
        }
    }

    fun pushNotification(item: NotificationItem) = enqueue(1, IslandVisualState.Notification(item))
    fun setMediaDemo() = enqueue(2, IslandVisualState.Media(MediaState("Neon Sky", "Maks", true, 0.56f)))
    fun setChargingDemo() = enqueue(3, IslandVisualState.Charging(78, false))
    fun setTimerDemo() = enqueue(4, IslandVisualState.Timer(TimerState(900, 510, false)))
    fun setCallDemo() = enqueue(5, IslandVisualState.Call(CallState("Pixel Support", 0, true)))

    fun clearToIdle() {
        queue.clear()
        _islandState.value = IslandVisualState.Idle
    }

    private fun enqueue(priority: Int, state: IslandVisualState) {
        queue.add(QueuedState(priority, state))
        _islandState.value = queue.peek()?.state ?: IslandVisualState.Idle
        if (state is IslandVisualState.Notification) {
            viewModelScope.launch {
                delay(settings.value.autoCollapseMillis.toLong())
                queue.removeIf { it.state == state }
                _islandState.value = queue.peek()?.state ?: IslandVisualState.Idle
            }
        }
    }
}

data class QueuedState(val priority: Int, val state: IslandVisualState)
