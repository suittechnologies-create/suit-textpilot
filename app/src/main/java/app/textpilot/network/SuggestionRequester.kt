package app.textpilot.network

import app.textpilot.data.PreferencesManager
import app.textpilot.suggestions.TypingInfo

interface SuggestionRequester {
    suspend fun requestSuggestionsFromServer(typingInfo: TypingInfo, preferencesManager: PreferencesManager): String
}