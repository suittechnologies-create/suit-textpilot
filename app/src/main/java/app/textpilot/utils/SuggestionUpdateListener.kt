package app.textpilot.utils

import app.textpilot.suggestions.TypingInfo

interface SuggestionUpdateListener {
    fun onSuggestionUpdated()
    fun onSuggestionError(typingInfo: TypingInfo, errorMessage: String)
}