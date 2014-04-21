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

package com.android.toolbox.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import com.android.toolbox.R;
import com.android.toolbox.misc.Utils;
import com.android.toolbox.misc.CMDProcessor;

//
// I/O Scheduler Related Settings
//
public class IOScheduler extends Activity  {

    public static final String IOSCHED_PREF = "pref_io_sched";
    public static final String IOSCHED_LIST_FILE = "/sys/block/mmcblk0/queue/scheduler";

    public static final String SOB_PREF = "pref_io_sched_set_on_boot";

    private static String mIOSchedulerFormat;

    private static ListPreference mIOSchedulerPref;
    
    protected void onCreate(Bundle savedInstanceState) {   
        super.onCreate(savedInstanceState);  
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragement()).commit();  
    }
    
    public static class PrefsFragement extends PreferenceFragment implements OnPreferenceChangeListener{ 

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mIOSchedulerFormat = getString(R.string.io_sched_summary);

        String[] availableIOSchedulers = new String[0];
        String availableIOSchedulersLine;
        int bropen, brclose;
        String currentIOScheduler = null;

        addPreferencesFromResource(R.xml.ioscheduler_settings);

        PreferenceScreen prefScreen = getPreferenceScreen();

        mIOSchedulerPref = (ListPreference) prefScreen.findPreference(IOSCHED_PREF);

        /* I/O scheduler
        Some systems might not use I/O schedulers */
        if (!Utils.fileExists(IOSCHED_LIST_FILE) ||
            (availableIOSchedulersLine = Utils.fileReadOneLine(IOSCHED_LIST_FILE)) == null) {
            prefScreen.removePreference(mIOSchedulerPref);

        } else {
            availableIOSchedulers = availableIOSchedulersLine.replace("[", "").replace("]", "").split(" ");
            bropen = availableIOSchedulersLine.indexOf("[");
            brclose = availableIOSchedulersLine.lastIndexOf("]");
            if (bropen >= 0 && brclose >= 0)
                currentIOScheduler = availableIOSchedulersLine.substring(bropen + 1, brclose);

            mIOSchedulerPref.setEntryValues(availableIOSchedulers);
            mIOSchedulerPref.setEntries(availableIOSchedulers);
            if (currentIOScheduler != null)
                mIOSchedulerPref.setValue(currentIOScheduler);
            mIOSchedulerPref.setSummary(String.format(mIOSchedulerFormat, currentIOScheduler));
            mIOSchedulerPref.setOnPreferenceChangeListener(this);
        }
    }

    @Override
    public void onResume() {
        String availableIOSchedulersLine;
        int bropen, brclose;
        String currentIOScheduler;

        super.onResume();

        if (Utils.fileExists(IOSCHED_LIST_FILE) &&
            (availableIOSchedulersLine = Utils.fileReadOneLine(IOSCHED_LIST_FILE)) != null) {
            bropen = availableIOSchedulersLine.indexOf("[");
            brclose = availableIOSchedulersLine.lastIndexOf("]");
            if (bropen >= 0 && brclose >= 0) {
                currentIOScheduler = availableIOSchedulersLine.substring(bropen + 1, brclose);
                mIOSchedulerPref.setSummary(String.format(mIOSchedulerFormat, currentIOScheduler));
            }
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String fname = "";

        if (newValue != null) {
            if (preference == mIOSchedulerPref) {
                fname = IOSCHED_LIST_FILE;
            }
            new CMDProcessor().su.runWaitFor("busybox echo " + newValue + " > "
                    + fname);
                if (preference == mIOSchedulerPref) {
                    mIOSchedulerPref.setSummary(String.format(mIOSchedulerFormat, (String) newValue));
                }
                return true;
            } else {
                return false;
            }

    }
}
}
