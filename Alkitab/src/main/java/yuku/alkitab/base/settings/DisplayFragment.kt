package yuku.alkitab.base.settings

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.preference.CheckBoxPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import yuku.alkitab.base.App
import yuku.alkitab.base.IsiActivity
import yuku.alkitab.base.widget.ConfigurationWrapper
import yuku.alkitab.debug.R

class DisplayFragment : PreferenceFragmentCompat() {
    private val configurationPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, _ ->
        val handler = Handler(Looper.getMainLooper())

        // do this after this method returns true
        handler.post {
            ConfigurationWrapper.notifyConfigurationNeedsUpdate()

            // restart this activity
            activity?.recreate()
        }

        true
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings_display)

        val pref_language = findPreference<ListPreference>(getString(R.string.pref_language_key))
        if (pref_language != null) {
            pref_language.onPreferenceChangeListener = configurationPreferenceChangeListener
            SettingsActivity.autoDisplayListPreference(pref_language)
        }

        val pref_bottomToolbarOnText = findPreference<CheckBoxPreference>(getString(R.string.pref_bottomToolbarOnText_key))
        pref_bottomToolbarOnText?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, _ ->
            // do this after this method returns true
            Handler(Looper.getMainLooper()).post { App.getLbm().sendBroadcast(Intent(IsiActivity.ACTION_NEEDS_RESTART)) }
            true
        }
    }
}
