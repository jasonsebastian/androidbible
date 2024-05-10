package yuku.alkitab.tracking

import yuku.alkitab.base.util.AppLog

object Tracker {
    private const val TAG = "Tracker"

    @JvmStatic
    fun trackEvent(category: String, vararg paramPairs: Any) {
        AppLog.d(TAG, "$category: $paramPairs")
    }
}
