package com.refeved.monitor.net;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;

import com.refeved.monitor.R;
import com.refeved.monitor.struct.DevFrige;
import com.refeved.monitor.struct.Device;
import com.refeved.monitor.ui.DevDetailActivity;

public class NetWorkService extends IntentService {

	Boolean running = false;
	NotificationManager m_NotificationManager;

	public NetWorkService() {
		super("NetWorkService");
	}

	@Override
	public void onCreate() {
		m_NotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		super.onCreate();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onHandleIntent(Intent intent) {
		if (running) {
			return;
		}
		running = true;

		long endTime = System.currentTimeMillis() + 5 * 1000;
		while (System.currentTimeMillis() < endTime) {
			synchronized (this) {
				try {
					wait(endTime - System.currentTimeMillis());
				} catch (Exception e) {
				}
			}
		}

		DevFrige device = new DevFrige(Device.Type_Frige,
				"10", "location", "status", "description", "0","0","80","");

		Intent notificationIntent = new Intent(this, DevDetailActivity.class);
		String type = getString(R.string.device_info_type_tittle);
		String id = getString(R.string.device_info_id_tittle);
		String location = getString(R.string.device_info_location_tittle);
		String status = getResources().getString(
				R.string.device_info_status_tittile);
		String description = getResources().getString(
				R.string.device_info_description_tittle);
		notificationIntent.putExtra(type, device.getmType());
		notificationIntent.putExtra(id, device.getmId());
		notificationIntent.putExtra(location, device.getmLocation());
		notificationIntent.putExtra(status, device.getmStatus());
		notificationIntent.putExtra(description, device.getmDescription());

		if (device.getmType().equals(Device.Type_Frige)) {
			notificationIntent.putExtra(
					getResources().getString(R.string.frige_info_temp_tittle),
					((DevFrige) device).getmTemperature());
		}

		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		Notification notification = new Notification();
		notification.icon = R.drawable.device_info_icon;
		notification.tickerText = getString(R.string.notification_tittle);
		notification.defaults = Notification.DEFAULT_SOUND;
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(this,
				getString(R.string.notification_tittle),
				getString(R.string.notification_tittle), pendingIntent);
		m_NotificationManager.notify(0, notification);

		running = false;
	}

}