package com.refeved.monitor;

import com.refeved.monitor.net.BackgroundService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RunOnStartupReceiver extends BroadcastReceiver { 
		AppContext appContext;
        @Override
        public void onReceive(Context context, Intent intent) {
        	appContext = (AppContext)context.getApplicationContext();
        	//UIHealper.DisplayToast(context, "RunOnSartupReceiver");
        	if(appContext.SettingsRunOnStartup)
        	{
        		if(appContext.SettingsNotification && appContext.SettingsRunInBackGround)
        		{
        			appContext.BackgroundServiceIntent = new Intent(appContext,BackgroundService.class);
        			appContext.startService(appContext.BackgroundServiceIntent);
        		}
        	}
        }
} 
