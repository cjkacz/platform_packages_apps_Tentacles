/*
 * Copyright (C) 2014 Team OCtOS
 * Warning: Tentacles may sting!
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.oct.tentacles.fragments.sb;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.os.UserHandle;

import com.oct.tentacles.R;
import com.oct.tentacles.preference.SettingsPreferenceFragment;

public class NotificationDrawer extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "NotificationDrawer";

    private static final String UI_COLLAPSE_BEHAVIOUR = "notification_drawer_collapse_on_dismiss";
    private static final String PRE_COLLAPSE_PANEL = "collapse_panel";
	private static final String SWIPE_TO_SWITCH_SCREEN_DETECTION = "full_swipe_to_switch_detection";

    private ListPreference mCollapseOnDismiss;
    private CheckBoxPreference mCollapsePanel;
	private CheckBoxPreference mFullScreenDetection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.sb_notification_drawer);
        PreferenceScreen prefScreen = getPreferenceScreen();

        // Notification drawer
        int collapseBehaviour = Settings.System.getInt(getContentResolver(),
                Settings.System.STATUS_BAR_COLLAPSE_ON_DISMISS,
                Settings.System.STATUS_BAR_COLLAPSE_IF_NO_CLEARABLE);
        mCollapseOnDismiss = (ListPreference) findPreference(UI_COLLAPSE_BEHAVIOUR);
        mCollapseOnDismiss.setValue(String.valueOf(collapseBehaviour));
        mCollapseOnDismiss.setOnPreferenceChangeListener(this);
        updateCollapseBehaviourSummary(collapseBehaviour);
		
        mFullScreenDetection = (CheckBoxPreference) findPreference(SWIPE_TO_SWITCH_SCREEN_DETECTION);
        mFullScreenDetection.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.SWIPE_TO_SWITCH_SCREEN_DETECTION, 0) == 1);
        mFullScreenDetection.setOnPreferenceChangeListener(this);
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mCollapseOnDismiss) {
            int value = Integer.valueOf((String) objValue);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.STATUS_BAR_COLLAPSE_ON_DISMISS, value);
            updateCollapseBehaviourSummary(value);
            return true;            
        } else if (preference == mCollapsePanel) {
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.QS_COLLAPSE_PANEL,
                    (Boolean) objValue ? 1 : 0, UserHandle.USER_CURRENT);
            return true;
        } else if (preference == mFullScreenDetection) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(getContentResolver(),
                Settings.System.SWIPE_TO_SWITCH_SCREEN_DETECTION, value ? 1 : 0);
            return true;
        }

        return false;
    }

    private void updateCollapseBehaviourSummary(int setting) {
        String[] summaries = getResources().getStringArray(
                R.array.notification_drawer_collapse_on_dismiss_summaries);
        mCollapseOnDismiss.setSummary(summaries[setting]);
    }
}