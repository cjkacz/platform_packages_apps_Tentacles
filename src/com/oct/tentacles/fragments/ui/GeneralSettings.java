/*
 * Copyright (C) 2014 The CarbonRom project
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

package com.oct.tentacles.fragments.ui;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.RemoteException;
import android.preference.CheckBoxPreference;
import android.content.ContentResolver;
import android.content.Intent;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManagerGlobal;

import com.oct.tentacles.R;
import com.oct.tentacles.Utils;
import com.oct.tentacles.preference.SettingsPreferenceFragment;

public class GeneralSettings extends SettingsPreferenceFragment {
    private static final String TAG = "GeneralSettings";

    private static final String KEY_EXPANDED_DESKTOP = "expanded_desktop";
    private static final String KEY_EXPANDED_DESKTOP_NO_NAVBAR = "expanded_desktop_no_navbar";
    private static final String CATEGORY_NAVBAR = "navigation_bar";
	
    private static final String KERNELTWEAKER_START = "kerneltweaker_start";
	
    // Package name of the kernel tweaker app
    public static final String KERNELTWEAKER_PACKAGE_NAME = "com.dsht.kerneltweaker";
    // Intent for launching the kernel tweaker main actvity
    public static Intent INTENT_KERNELTWEAKER = new Intent(Intent.ACTION_MAIN)
            .setClassName(KERNELTWEAKER_PACKAGE_NAME, KERNELTWEAKER_PACKAGE_NAME + ".MainActivity");

    private Preference mKernelTweaker;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.ui_general_settings);
		
        PreferenceScreen prefSet = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();

        // Expanded desktop
         mExpandedDesktopPref = (ListPreference) findPreference(KEY_EXPANDED_DESKTOP);
         mExpandedDesktopNoNavbarPref =
                 (CheckBoxPreference) findPreference(KEY_EXPANDED_DESKTOP_NO_NAVBAR);
 
         int expandedDesktopValue = Settings.System.getInt(getContentResolver(),
                 Settings.System.EXPANDED_DESKTOP_STYLE, 0);
 
         try {
             boolean hasNavBar = WindowManagerGlobal.getWindowManagerService().hasNavigationBar();
 
             if (hasNavBar) {
                 mExpandedDesktopPref.setOnPreferenceChangeListener(this);
                 mExpandedDesktopPref.setValue(String.valueOf(expandedDesktopValue));
                 updateExpandedDesktop(expandedDesktopValue);
                 prefScreen.removePreference(mExpandedDesktopNoNavbarPref);
             } else {
                 // Hide no-op "Status bar visible" expanded desktop mode
                 mExpandedDesktopNoNavbarPref.setOnPreferenceChangeListener(this);
                 mExpandedDesktopNoNavbarPref.setChecked(expandedDesktopValue > 0);
                 prefScreen.removePreference(mExpandedDesktopPref);
                 // Hide navigation bar category
                 prefScreen.removePreference(findPreference(CATEGORY_NAVBAR));
             }
         } catch (RemoteException e) {
             Log.e(TAG, "Error getting navigation bar status");
         }
 

        mKernelTweaker = (Preference)
                prefSet.findPreference(KERNELTWEAKER_START);

        } else if (preference == mExpandedDesktopPref) {
             int expandedDesktopValue = Integer.valueOf((String) objValue);
             updateExpandedDesktop(expandedDesktopValue);
             return true;
         } else if (preference == mExpandedDesktopNoNavbarPref) {
             boolean value = (Boolean) objValue;
             updateExpandedDesktop(value ? 2 : 0);
             return true;
    }
	

    @Override
    public void onResume() {
        super.onResume();
    }
	
    @Override
    public void onPause() {
        super.onPause();
    }
	
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mKernelTweaker){
            startActivity(INTENT_KERNELTWEAKER);
            return true;
        }

private void updateExpandedDesktop(int value) {
         ContentResolver cr = getContentResolver();
         Resources res = getResources();
         int summary = -1;
 
         Settings.System.putInt(cr, Settings.System.EXPANDED_DESKTOP_STYLE, value);
 
         if (value == 0) {
             // Expanded desktop deactivated
             Settings.System.putInt(cr, Settings.System.POWER_MENU_EXPANDED_DESKTOP_ENABLED, 0);
             Settings.System.putInt(cr, Settings.System.EXPANDED_DESKTOP_STATE, 0);
             summary = R.string.expanded_desktop_disabled;
         } else if (value == 1) {
             Settings.System.putInt(cr, Settings.System.POWER_MENU_EXPANDED_DESKTOP_ENABLED, 1);
             summary = R.string.expanded_desktop_status_bar;
         } else if (value == 2) {
             Settings.System.putInt(cr, Settings.System.POWER_MENU_EXPANDED_DESKTOP_ENABLED, 1);
             summary = R.string.expanded_desktop_no_status_bar;
         }
 
         if (mExpandedDesktopPref != null && summary != -1) {
             mExpandedDesktopPref.setSummary(res.getString(summary));
         }
     }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
