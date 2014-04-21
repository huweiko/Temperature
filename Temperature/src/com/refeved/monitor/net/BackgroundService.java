package com.refeved.monitor.net;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.InputSource;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import com.refeved.monitor.AppContext;
import com.refeved.monitor.R;
import com.refeved.monitor.UIHealper;
import com.refeved.monitor.struct.AreaTreeNode;
import com.refeved.monitor.struct.DevFrige;
import com.refeved.monitor.struct.DevHumidity;
import com.refeved.monitor.struct.Device;
import com.refeved.monitor.struct.ErrorDev;
import com.refeved.monitor.struct.TreeNode;
import com.refeved.monitor.ui.MainActivity;

@SuppressLint("HandlerLeak")
public class BackgroundService extends Service{
	AppContext appContext;
	int notificationID = 0;
	boolean ServiceRunning = false;
	private List<ErrorDev> lvDevData = new ArrayList<ErrorDev>();
	
	public Handler mHandler=new Handler()  
    {  
        public void handleMessage(Message msg)  
        {  
            switch(msg.what)  
            {  
            case 1:  
            	WebClient client = WebClient.getInstance();
    			client.sendMessage(appContext, WebClient.Method_getMonitorInfosForAllExceptionWidthUserID, null);
                break;  
            default:  
                break;            
            }  
            super.handleMessage(msg);  
        }  
    }; 
  public class RefreshThread  extends Thread{
    	
    	@Override
		public void run(){
    		if(ServiceRunning && appContext.SettingsNotification){
    			Message message=new Message();  
    			message.what=1;  
    			mHandler.sendMessage(message);  
    		}
    		
    		while(ServiceRunning && appContext.SettingsNotification)
        	{
    			while(ServiceRunning && appContext.SettingsCheckFrequency > 0 && appContext.SettingsNotification)
            	{
            		int count = appContext.SettingsCheckFrequency * 100;
            		while(ServiceRunning && count-- > 0)
            		{
                		try {  
                            Thread.sleep(10);  
                        } catch (InterruptedException e) {  
                            e.printStackTrace();  
                        }  
            		}
            		
            		if(ServiceRunning && appContext.SettingsNotification){
            			Message message=new Message();  
            			message.what=1;  
            			mHandler.sendMessage(message);  
            		}
                    
            	}
        	}
    		
    		stopSelf();
    	}
    }
    
    RefreshThread mRefreshThread = null;
    

    
    
   public void parseGetErrorByAidXml(Element element,AreaTreeNode node, String parentLocation){
	   Element e = null;
	   String location = null;
	   if(element.getAttributeValue("des") != null)
	   {
		   location = parentLocation+element.getAttributeValue("des") + " ";
	   }
	   else
	   {
		   location = parentLocation + " ";
	   }
	   while( (e =element.getChild("humidity")) != null)
	   {
		   String type = Device.Type_Humidity;
		   String id = e.getAttributeValue("macid");
		   String status = e.getAttributeValue("state");
		   String descprition = e.getAttributeValue("des");
		   String low = e.getAttributeValue("low");
		   String high = e.getAttributeValue("high");
		   String hum = e.getAttributeValue("humidity");
		   DevHumidity devHum = new DevHumidity(type, id, location, status, descprition,low,high, hum, null);
		   node.addDevice(devHum);
		   element.removeChild("humidity");
	   }
	   
	   while( (e =element.getChild("temperature")) != null)
	   {
		   String type = Device.Type_Frige;
		   String id = e.getAttributeValue("macid");
		   String status = e.getAttributeValue("state");
		   String descprition = e.getAttributeValue("des");
		   String low = e.getAttributeValue("low");
		   String high = e.getAttributeValue("high");
		   String temp = e.getAttributeValue("temperature");
		   DevFrige devHum = new DevFrige(type, id, location, status, descprition,low,high, temp, "");
		   node.addDevice(devHum);
		   element.removeChild("temperature");
	   }
	   
	   while( (e =element.getChild("address")) != null)
	   {
		   String id = e.getAttributeValue("aid");
		   String name = e.getAttributeValue("des");
		   AreaTreeNode n = new AreaTreeNode(node, id, name, false, new ArrayList<TreeNode>(), new ArrayList<Device>());
		   node.getmChildren().add(n);
		   
		   parseGetErrorByAidXml(e,n,location);
		   element.removeChild("address");
	   }
   }
	
 
    
public BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			
			String resXml = intent.getStringExtra(WebClient.Param_resXml);
	
			if(intent.getAction().equals(WebClient.INTERNAL_ACTION_GETERRORBYAID_DONE))
			{
				if(resXml != null)
				{
					try{
//						resXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"+
//									"<root>"+
//									"<address aid='1' des='深圳' pid='0' rightIndex='48'>" +
//									    "<address aid='13' des='深圳1楼' pid='1' rightIndex='60'>"+
//									       "<humidity macid='1' state='1' low='0.0' high='100.0' des='1"+
//									        "楼湿度（1）' humidity='51.20'/>"+
//									        "<temperature macid='2' state='2' low='-90.0' high='-70.0' " +
//									        "des='1楼温度（1）' temperature='-74.90'/>"+
//									        "<temperature macid='16' state='3' low='-90.0' high='-70.0' "+
//									        "des='1楼温度（2）' temperature='-84.40'/>"+
//									    "</address>"+
//									    "<address aid='15' des='深圳8楼' pid='1' rightIndex='62'>"+
//									        "<humidity macid='31' state='8' low='-100.0' high='100.0' " +
//									        "des='8楼湿度（1）' humidity='69.60'/>"+
//									        "<temperature macid='32' state='4' low='0.0' high='100.0' " +
//									        "des='8楼温度（1）' temperature='28.00'/>"+
//									    "</address>"+
//									    "<address aid='17' des='test地址33' pid='1' rightIndex='64'>" +
//									    "</address>"+
//									"</address>"+
//									"</root>";
						SAXBuilder builder = new SAXBuilder();
						StringReader sr = new StringReader(resXml);   
						InputSource is = new InputSource(sr); 
						Document Doc = builder.build(is);
						Element rootElement = (Element) Doc.getRootElement();
						AreaTreeNode node = new AreaTreeNode(null,"root","0",false, new ArrayList<TreeNode>(), new ArrayList<Device>());
						parseGetErrorByAidXml(rootElement, node, "");
						
						for(int i = 0 ; i < lvDevData.size() ; i++)
						{
							lvDevData.get(i).setmChecked(false);
						}
						
						CheckErrorList(node,lvDevData);
						//UIHealper.DisplayToast(appContext, "Get Error Devices "+ lvDevData.size());
						
						Iterator<ErrorDev> it = lvDevData.iterator();
						while(it.hasNext())
						{
							if(it.next().getmChecked() == false)
							{
								it.remove();
							}
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
			}
			
			}		
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void onCreate(){
		super.onCreate();
	
		appContext = (AppContext) getApplication();
		IntentFilter filter = new IntentFilter();
	    filter.addAction(WebClient.INTERNAL_ACTION_GETERRORBYAID_DONE);
	    appContext.registerReceiver(receiver, filter);
		ServiceRunning = true;
		

	}

	public int onStartCommand(Intent intent , int flags , int startId){
		ServiceRunning = true;
		
		if(mRefreshThread == null){
    		mRefreshThread = new RefreshThread();
    		try {
    			mRefreshThread.start(); 
			} catch (IllegalThreadStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}

		
		return Service.START_STICKY;
	}
	public void onDestroy(){
		super.onDestroy();
		
		ServiceRunning = false;
		appContext.unregisterReceiver(receiver);
		
		if(mRefreshThread != null){
    		try {
				mRefreshThread.join(15);
				mRefreshThread = null;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
	}
	
	private void CheckErrorList(AreaTreeNode node,List<ErrorDev> list){
		Calendar currentCal = Calendar.getInstance();
		
		if(node == null || list == null) return;
		
		List<Device> devices = node.getmDevices();
		for(int i = 0 ; i < devices.size() ; i++)
		{
			Device dev= devices.get(i);
			boolean isMacth = false;
			boolean isNotification = false;
			for(int j = 0 ; j < list.size() ; j ++)
			{
				if(dev.getmId().equals(list.get(j).getmId()))
				{
					isMacth = true;
					list.get(j).setmChecked(true);
					
					long s = currentCal.getTimeInMillis() - list.get(j).getmCal().getTimeInMillis();
					if(s > 3600 * 1000)//一个小时通知一次
					{
						isNotification = true;
						list.get(j).setmCal(currentCal);
					}
					
					break;
				}
			}
			
			if(!isMacth)
			{
				ErrorDev errorDev = new ErrorDev(dev.getmType(), dev.getmId(), 
						dev.getmLocation(), dev.getmStatus(), dev.getmDescription(), 
						dev.getmLow(), dev.getmHigh(), currentCal, "");
				list.add(errorDev);
				
				isNotification = true;
			}
			
			if(isNotification)
			{
//				if(appContext.SettingsOnlyMeasurement){
//					if(dev.getmStatus().equals("0")){
//						showNotification(dev);
//					}
//				}
//				else
//				{
					showNotification(dev);
//				}
//				UIHealper.DisplayToast(appContext, "Show Notification of "+ dev.getmId());
			}
				
		}
		
		if(node.getmChildren() == null)
		{ 
			return;
		}
		else
		{
			for(int i = 0 ; i < node.getmChildren().size() ; i++  )
			{
				CheckErrorList((AreaTreeNode) node.getmChildren().get(i),list);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	private void showNotification(Device device){
		Intent notificationIntent = new Intent(this, MainActivity.class);
		String id = getString(R.string.device_info_id_tittle);
		notificationIntent.putExtra(id, device.getmId());
		
		//notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
		
		Notification notification = new Notification();
		notification.icon = R.drawable.device_info_icon;
		notification.tickerText = getString(R.string.notification_tittle);
		notification.defaults = Notification.DEFAULT_ALL;

		notification.flags = Notification.FLAG_AUTO_CANCEL;
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				notificationIntent,PendingIntent.FLAG_ONE_SHOT);
		notification.setLatestEventInfo(this,device.getmDescription(),getString(R.string.device_info_status_tittile) + UIHealper.parseStatus(device.getmStatus()), pendingIntent);
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.notify(notificationID++, notification);
	}

	
	/**
	 * 检测是否有活动网络
	 */
	public static boolean contactNet(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);// 获取系统的连接服务
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();// 获取网络的连接情况
		if (activeNetInfo != null && activeNetInfo.isConnected()
				&& activeNetInfo.isAvailable()) {
			return true;
		} else {
			return false;
		}
	}
}
