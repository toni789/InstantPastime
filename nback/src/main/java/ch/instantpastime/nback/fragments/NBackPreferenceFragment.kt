package ch.instantpastime.nback.fragments

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.preference.*
import ch.instantpastime.nback.R
import ch.instantpastime.nback.core.NBackBoard

class NBackPreferenceFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager?.context?.let {context ->
            preferenceScreen = buildPreferenceScreen(context)
        }
    }
    private fun buildPreferenceScreen(context: Context): PreferenceScreen? {
        val screen = preferenceManager?.createPreferenceScreen(context)?.apply {
            val gameOptionsCategory = PreferenceCategory(context).apply {
                key = NBackSettings.NBACK_OPTIONS_CAT_KEY
                title = getString(R.string.game_options_full)
            }
            addPreference(gameOptionsCategory)

            val nbackLevelPref = SeekBarPreference(context).apply {
                key = NBackSettings.NBACK_LEVEL_KEY
                title = getString(R.string.nback_level, NBackBoard.MIN_LEVEL, NBackBoard.MAX_LEVEL)
                summary = getString(R.string.nback_level_hint)
                min = NBackBoard.MIN_LEVEL
                max = NBackBoard.MAX_LEVEL
                showSeekBarValue = true
                setDefaultValue(NBackBoard.DEFAULT_LEVEL)
                onPreferenceChangeListener = Preference.OnPreferenceChangeListener { p, v ->
                    onNBackLevelPreferenceChanged(p, v)
                }
            }
            gameOptionsCategory.addPreference(nbackLevelPref)

            val timePerTrialPref = SeekBarPreference(context).apply {
                key = NBackSettings.NBACK_MILLISECONDS_KEY
                title = getString(R.string.nback_interval)
                summary = getString(R.string.nback_interval_hint)
                max = NBackBoard.MAX_MILLISEC
                min = NBackBoard.MIN_MILLISEC
                showSeekBarValue = true
                setDefaultValue(NBackBoard.DEFAULT_MILLISEC)
                onPreferenceChangeListener = Preference.OnPreferenceChangeListener { p, v ->
                    onTimePerTrialPreferenceChanged(p, v)
                }
            }
            gameOptionsCategory.addPreference(timePerTrialPref)

            // Note from https://developer.android.com/guide/topics/ui/settings/programmatic-hierarchy
            // Warning: Make sure to add the PreferenceCategory to the PreferenceScreen before adding children to it.
            // Preferences cannot be added to a PreferenceCategory that isn’t attached to the root screen.
        }
        return screen
    }

    private fun onNBackLevelPreferenceChanged(preference: Preference, newValue: Any?): Boolean {

        return if (newValue is Int && preference is SeekBarPreference) {
            val oldValue = preference.value
            if (oldValue != newValue) {
                preference.value = newValue
                Toast.makeText(
                    preference.context,
                    getString(R.string.nback_level_changed, newValue.toInt()),
                    Toast.LENGTH_SHORT
                ).show()
                true
            } else {
                Toast.makeText(preference.context, "No change: same value", Toast.LENGTH_SHORT).show()
                false
            }
        } else {
            Toast.makeText(preference.context, "No change: wrong type", Toast.LENGTH_SHORT).show()
            false
        }
    }

    private fun onTimePerTrialPreferenceChanged(preference: Preference, newValue: Any?): Boolean {
        return if (newValue is Int && preference is SeekBarPreference) {
            val oldValue = preference.value
            if (oldValue != newValue) {
                preference.value = newValue
                Toast.makeText(
                    preference.context,
                    resources.getQuantityString(R.plurals.nback_interval_changed, newValue, newValue),
                    Toast.LENGTH_SHORT
                ).show()
                true
            } else false
        } else false
    }
}