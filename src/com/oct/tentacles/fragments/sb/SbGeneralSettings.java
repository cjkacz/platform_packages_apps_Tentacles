/*
 * Copyright (C) 2012 The CyanogenMod Project
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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.oct.tentacles.R;
import com.oct.tentacles.preference.SettingsPreferenceFragment;
import com.oct.tentacles.Utils;
import com.oct.tentacles.util.Helpers;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class SbGeneralSettings extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String TAG = "StatusBarGeneral";

    private static final String PREF_CUSTOM_STATUS_BAR_COLOR = "custom_status_bar_color";
    private static final String PREF_STATUS_BAR_OPAQUE_COLOR = "status_bar_opaque_color";
    private static final String STATUS_BAR_BRIGHTNESS = "statusbar_brightness_slider";

    private CheckBoxPreference mCustomBarColor;
    private CheckBoxPreference mStatusbarSliderPreference;
    private ColorPickerPreference mBarOpaqueColor;

    private boolean mCheckPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createCustomView();
    }

    private PreferenceScreen createCustomView() {
        mCheckPreferences = false;
        PreferenceScreen prefSet = getPreferenceScreen();
        if (prefSet != null) {
            prefSet.removeAll();
        }

        addPreferencesFromResource(R.xml.sb_general_settings);
        prefSet = getPreferenceScreen();

        int intColor;
        String hexColor;

        PackageManager pm = getPackageManager();
        Resources systemUiResources;
        try {
            systemUiResources = pm.getResourcesForApplication("com.android.systemui");
        } catch (Exception e) {
            Log.e(TAG, "can't access systemui resources",e);
            return null;
        }

        mCustomBarColor = (CheckBoxPreference) prefSet.findPreference(PREF_CUSTOM_STATUS_BAR_COLOR);
        mCustomBarColor.setChecked(Settings.System.getInt(mContentAppRes,
                Settings.System.CUSTOM_STATUS_BAR_COLOR, 0) == 1);

        mStatusbarSliderPreference = (CheckBoxPreference) findPreference(STATUS_BAR_BRIGHTNESS);
        mStatusbarSliderPreference.setChecked((Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.STATUSBAR_BRIGHTNESS_SLIDER, 0) == 1));

        mBarOpaqueColor = (ColorPickerPreference) findPreference(PREF_STATUS_BAR_OPAQUE_COLOR);
        mBarOpaqueColor.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_OPAQUE_COLOR, 0xff000000);
        mBarOpaqueColor.setSummary(getResources().getString(R.string.default_string));
        if (intColor == 0xff000000) {
            intColor = systemUiResources.getColor(systemUiResources.getIdentifier(
                    "com.android.systemui:color/system_bar_background_opaque", null, null));
        } else {
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mBarOpaqueColor.setSummary(hexColor);
        }
        mBarOpaqueColor.setNewPreviewColor(intColor);

        mCheckPreferences = true;
        return prefSet;
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (!mCheckPreferences) {
            return false;
        }
        if (preference == mBarOpaqueColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer
                    .valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_OPAQUE_COLOR, intHex);
            Helpers.restartSystemUI();
            return true;
        }
        return false;
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;

        if (preference == mCustomBarColor) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.CUSTOM_STATUS_BAR_COLOR,
            mCustomBarColor.isChecked() ? 1 : 0);
            Helpers.restartSystemUI();
            return true;
        } else if (preference == mStatusbarSliderPreference) {
            value = mStatusbarSliderPreference.isChecked();
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUSBAR_BRIGHTNESS_SLIDER, value ? 1 : 0);
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

}
