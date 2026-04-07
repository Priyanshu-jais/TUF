package com.example.tuf.presentation.screens.onboarding

import com.example.tuf.core.base.BaseViewModel
import com.example.tuf.data.local.DataStoreManager
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

/** ViewModel for the Onboarding flow. Handles completion and navigation. */
class OnboardingViewModel(private val dataStoreManager: DataStoreManager) : BaseViewModel() {

    private val _navigationEvent = Channel<Unit>(Channel.BUFFERED)
    val navigationEvent = _navigationEvent.receiveAsFlow()

    fun onGetStarted() {
        safeLaunch {
            dataStoreManager.setOnboardingCompleted(true)
            _navigationEvent.send(Unit)
        }
    }

    fun onSkip() {
        safeLaunch {
            dataStoreManager.setOnboardingCompleted(true)
            _navigationEvent.send(Unit)
        }
    }
}
