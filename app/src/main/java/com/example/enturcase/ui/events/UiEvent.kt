package com.example.enturcase.ui.events

sealed class UiEvent {
    data object ReloadData : UiEvent()
}
