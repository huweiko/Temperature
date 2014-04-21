package com.refeved.monitor.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import com.actionbarsherlock.app.SherlockActivity;
import com.refeved.monitor.AppContext;
import com.refeved.monitor.R;
import com.refeved.monitor.net.BackgroundService;

@SuppressLint("ResourceAsColor")
public class SettingsSectionFragment extends PreferenceListFragment implements
SharedPreferences.OnSharedPreferenceChangeListener,
PreferenceListFragment.OnPreferenceAttachedListener{
	public static final int SELECT_CAPTURE = 2;
	public static final int SELECT_DISTRICT_CAPTURE = 3;
	private AppContext appContext;
	String SETTINGS_RUN_ON_STARTUP ;
	String SETTINGS_RUN_IN_BACKGROUND ;
	String SETTINGS_NOTIFICATION ;
	String SETTINGS_REFRESH_FREQUENCY ;
	String SETTINGS_CHECK_FREQUENCY;
//	String SETTINGS_ONLY_MEASUREMENT;
	String SETTINGS_QR_CODE;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        
        appContext = (AppContext) getSherlockActivity().getApplication();

        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setSharedPreferencesName(getString(R.string.settings_filename));
        addPreferencesFromResource(R.xml.settings);
        
        preferenceManager.getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
        
        SETTINGS_RUN_ON_STARTUP = getString(R.string.settings_run_on_startup);
    	SETTINGS_RUN_IN_BACKGROUND = getString(R.string.settings_run_in_background);
    	SETTINGS_NOTIFICATION = getString(R.string.settings_notification);
    	SETTINGS_REFRESH_FREQUENCY = getString(R.string.settings_refresh_frequency);
    	SETTINGS_CHECK_FREQUENCY = getString(R.string.settings_check_frequency);
    	SETTINGS_QR_CODE =getString(R.string.settings_QRcode);
//    	SETTINGS_ONLY_MEASUREMENT = getString(R.string.settings_only_measurement);
    }
	@Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
            String key) {
		Preference pref = findPreference(key);
		if(key.equals(SETTINGS_RUN_ON_STARTUP))
		{ 
			appContext.SettingsRunOnStartup = sharedPreferences.getBoolean(SETTINGS_RUN_ON_STARTUP,false); 
		}
		else if(key.equals(SETTINGS_RUN_IN_BACKGROUND))
		{
			appContext.SettingsRunInBackGround = sharedPreferences.getBoolean(SETTINGS_RUN_IN_BACKGROUND,false); 	
		}
		else if(key.equals(SETTINGS_NOTIFICATION))
		{
			appContext.SettingsNotification = sharedPreferences.getBoolean(SETTINGS_NOTIFICATION, false);
			if(appContext.SettingsNotification)
			{
				if(appContext.BackgroundServiceIntent == null)
				{
					appContext.BackgroundServiceIntent = new Intent(appContext,BackgroundService.class);
					appContext.startService(appContext.BackgroundServiceIntent);
				}
			}
			else if( !appContext.SettingsNotification )
			{
				if(appContext.BackgroundServiceIntent != null)
				{
					appContext.stopService(appContext.BackgroundServiceIntent);
					appContext.BackgroundServiceIntent = null;
				}
			}
		}
/*		else if(key.equals(SETTINGS_ONLY_MEASUREMENT))
		{
			appContext.SettingsOnlyMeasurement = sharedPreferences.getBoolean(SETTINGS_ONLY_MEASUREMENT,false);
		}*/
		else if(key.equals(SETTINGS_REFRESH_FREQUENCY))
		{
			String s  = sharedPreferences.getString(SETTINGS_REFRESH_FREQUENCY, "0");
			appContext.SettingsRefreshFrequency = Integer.parseInt(s);
			pref.setSummary("当前频率："+ s +"s");
		}
		else if(key.equals(SETTINGS_CHECK_FREQUENCY))
		{
			String s  = sharedPreferences.getString(SETTINGS_CHECK_FREQUENCY, "0");
			appContext.SettingsCheckFrequency = Integer.parseInt(s);
			pref.setSummary("当前频率："+ s +"s");
		}
    }
 
    @Override
    public void onPreferenceAttached(PreferenceScreen root, int xmlId) {
        if (root == null)
            return;
        
    }
    
}
