package com.charan.setupBox.presentation.home

sealed class HomeEffect {
    data object NavigateToLoginScreen : HomeEffect()
}
