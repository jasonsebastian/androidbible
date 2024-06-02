package yuku.alkitab.base.ac.base

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.view.KeyEvent
import android.view.ViewGroup
import androidx.appcompat.widget.SwitchCompat
import yuku.afw.storage.Preferences
import yuku.alkitab.base.App
import yuku.alkitab.base.S
import yuku.alkitab.base.storage.Prefkey
import yuku.alkitab.base.util.RequestCodes
import yuku.alkitab.base.widget.LeftDrawer
import yuku.alkitab.base.widget.TextAppearancePanel
import yuku.alkitab.tracking.Tracker


abstract class BaseLeftDrawerActivity : BaseActivity(), LeftDrawer.Listener {
    protected abstract val overlayContainer: ViewGroup?
    private lateinit var mPrevConfig: Configuration
    protected var textAppearancePanel: TextAppearancePanel? = null
    protected abstract val leftDrawer: LeftDrawer
    abstract val root: ViewGroup

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        mPrevConfig = Configuration(resources.configuration)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            openOrCloseLeftDrawer()
            return true
        }
        return super.onKeyUp(keyCode, event)
    }

    private fun openOrCloseLeftDrawer() {
        leftDrawer.toggleDrawer()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.diff(mPrevConfig) and ActivityInfo.CONFIG_UI_MODE != 0) {
            leftDrawer.checkSystemTheme()
            mPrevConfig = Configuration(newConfig)
        }
    }

    open fun applyPreferences() {
        // make sure S applied variables are set first
        S.recalculateAppliedValuesBasedOnPreferences()

        // apply background color, and clear window background to prevent overdraw
        window.setBackgroundDrawableResource(android.R.color.transparent)
        val backgroundColor = S.applied().backgroundColor
        root.setBackgroundColor(backgroundColor)
    }

    override fun cNightMode_checkedChange(isChecked: Boolean) {
        Tracker.trackEvent("left_drawer_night_mode_click")
        setNightMode(isChecked)
    }

    override fun cFollowSystemTheme_checkedChange(isChecked: Boolean, cNightMode: SwitchCompat) {
        Tracker.trackEvent("left_drawer_follow_system_theme_click")
        cNightMode.setEnabled(!isChecked)
        setFollowSystemTheme(isChecked, cNightMode)
    }

    private fun setNightMode(yes: Boolean) {
        val previousValue = Preferences.getBoolean(Prefkey.is_night_mode, false)
        if (previousValue == yes) return

        Preferences.setBoolean(Prefkey.is_night_mode, yes)

        applyPreferences()
        applyNightModeColors()

        textAppearancePanel?.displayValues()

        App.getLbm().sendBroadcast(Intent(ACTION_NIGHT_MODE_CHANGED))
    }

    private fun setFollowSystemTheme(yes: Boolean, cNightMode: SwitchCompat) {
        Preferences.setBoolean(Prefkey.follow_system_theme, yes)

        if (yes) {
            val systemTheme = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            cNightMode.isChecked = systemTheme == Configuration.UI_MODE_NIGHT_YES
            setNightMode(systemTheme == Configuration.UI_MODE_NIGHT_YES)
        }
    }

    private fun setShowTextAppearancePanel(yes: Boolean) {
        if (!yes) {
            textAppearancePanel?.hide()
            textAppearancePanel = null
            return
        }

        if (textAppearancePanel == null) { // not showing yet
            textAppearancePanel = createTextAppearancePanel()
            textAppearancePanel?.show()
        }
    }

    protected open fun createTextAppearancePanel(): TextAppearancePanel? {
        if (overlayContainer == null) return null
        return TextAppearancePanel(
            this,
            overlayContainer,
            object : TextAppearancePanel.Listener {
                override fun onValueChanged() {
                    applyPreferences()
                }

                override fun onCloseButtonClick() {
                    textAppearancePanel?.hide()
                    textAppearancePanel = null
                }
            },
            RequestCodes.FromActivity.TextAppearanceGetFonts,
            RequestCodes.FromActivity.TextAppearanceCustomColors
        )
    }

    override fun bDisplay_click() {
        Tracker.trackEvent("left_drawer_display_click")
        setShowTextAppearancePanel(textAppearancePanel == null)
    }

    companion object {
        const val ACTION_NIGHT_MODE_CHANGED = "yuku.alkitab.base.IsiActivity.action.NIGHT_MODE_CHANGED"
    }
}
