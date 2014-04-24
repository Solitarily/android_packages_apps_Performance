/*
 * Performance Control - An Android CPU Control application Copyright (C) 2012
 * James Roberts
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.android.toolbox;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.os.SystemProperties;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.view.MenuItem;

import com.android.toolbox.R;

public class Performance extends Activity {
	
	
	
    private static final String USE_DITHERING_PREF = "pref_use_dithering";

    private static final String USE_DITHERING_PERSIST_PROP = "persist.sys.use_dithering";

    private static final String USE_DITHERING_DEFAULT = "1";

    private static final String USE_16BPP_ALPHA_PREF = "pref_use_16bpp_alpha";

    private static final String USE_16BPP_ALPHA_PROP = "persist.sys.use_16bpp_alpha";

    private static ListPreference mUseDitheringPref;

    private static CheckBoxPreference mUse16bppAlphaPref;
    
    protected void onCreate(Bundle savedInstanceState) {   
        super.onCreate(savedInstanceState);  
        
        setContentView(R.layout.toolbox); 
        ActionBar actionBar = getActionBar(); 
        actionBar.setDisplayShowHomeEnabled(false);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragement()).commit();  
    }
    
    protected void onStart() {  
        super.onStart();  
        ActionBar actionBar = this.getActionBar();  
        actionBar.setDisplayHomeAsUpEnabled(true);  
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public static class PrefsFragement extends PreferenceFragment implements OnPreferenceChangeListener{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getPreferenceManager() != null) {

            addPreferencesFromResource(R.xml.performance_settings);

            PreferenceScreen prefSet = getPreferenceScreen();

            String useDithering = SystemProperties.get(USE_DITHERING_PERSIST_PROP, USE_DITHERING_DEFAULT);
            mUseDitheringPref = (ListPreference) prefSet.findPreference(USE_DITHERING_PREF);
            mUseDitheringPref.setOnPreferenceChangeListener(this);
            mUseDitheringPref.setValue(useDithering);
            mUseDitheringPref.setSummary(mUseDitheringPref.getEntry());

            mUse16bppAlphaPref = (CheckBoxPreference) prefSet.findPreference(USE_16BPP_ALPHA_PREF);
            String use16bppAlpha = SystemProperties.get(USE_16BPP_ALPHA_PROP, "0");
            mUse16bppAlphaPref.setChecked("1".equals(use16bppAlpha));

        }
    }    

	@Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mUse16bppAlphaPref) {
            SystemProperties.set(USE_16BPP_ALPHA_PROP,
                    mUse16bppAlphaPref.isChecked() ? "1" : "0");
        } else {
            // If we didn't handle it, let preferences handle it.
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }

        return true;
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mUseDitheringPref) {
            String newVal = (String) newValue;
            int index = mUseDitheringPref.findIndexOfValue(newVal);
            SystemProperties.set(USE_DITHERING_PERSIST_PROP, newVal);
            mUseDitheringPref.setSummary(mUseDitheringPref.getEntries()[index]);
        }
        return true;
    }
    }
    }
    
    
