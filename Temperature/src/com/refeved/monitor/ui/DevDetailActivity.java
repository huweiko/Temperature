/*
 * 文件名：DevDetailActivity.java
 * 功能：设备细节活动框，主要显示单个设备的ID、类型、地址、log等。
 * 作者：huwei
 * 修改时间：2013-10-25
 * 
 * 
 * 
 * */
package com.refeved.monitor.ui;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.InputSource;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.refeved.monitor.AppContext;
import com.refeved.monitor.R;
import com.refeved.monitor.UIHealper;
import com.refeved.monitor.net.WebClient;
import com.refeved.monitor.struct.AreaTreeNode;
import com.refeved.monitor.struct.DevFrige;
import com.refeved.monitor.struct.DevHumidity;
import com.refeved.monitor.struct.Device;
import com.refeved.monitor.struct.TreeNode;

public class DevDetailActivity extends BaseActivity implements OnClickListener {
	private static final int CLICK_HISTORY_CURVE = 0;
	private static final int CLICK_HISTORY_DATE = 1;
	AppContext appContext;
	View mCustomView;
	int count = 5;
	Boolean visiable = false;
	static String DevDetailMacID;
	public static String mDeviceType = null;
	ImageButton mHeaderRefresh;
	TextView mHeaderTittle;
	ProgressBar mHeaderprogressBarRefresh;
	ImageButton mHeaderHome;
	Button mHistoryCurveMenu;
	Button mHistoryDateMenu;
	ViewPager mViewPager;//ViewPager用于实现多页面的切换效果（也就是左右滑动效果）
	DevDetailPagerAdapter mSectionsPagerAdapter;

//	DevLogListViewAdapter mDevLogListViewAdapter;

	@SuppressLint("HandlerLeak")
	public Handler mHandler=new Handler()  
	{  
		public void handleMessage(Message msg)  
		{  
			switch(msg.what)  
			{  
			case 1:  
				onDevInfoUpdate();
				break;  
			default:  
				break;            
			}  
			super.handleMessage(msg);  
		}  
	}; 

	private class RefreshThread extends Thread{
		@Override  
		public void run()  
		{  
			while(appContext.SettingsRefreshFrequency > 0 && visiable)
			{
				int count = appContext.SettingsRefreshFrequency;
				Log.i("huwei", "DevDetailThread-->count = "+count);
				while(count-- > 0 && visiable)
				{
					try {  
						Thread.sleep(1000);  
					} catch (InterruptedException e) {  
						e.printStackTrace();  
					}  
				}
				if(visiable){
					Message message=new Message();  
					message.what=1;  
					mHandler.sendMessage(message);  
				}
			}
		}  
	}
	RefreshThread mRefreshThread= null;

	public void parseGetDevInfoXml(Element element,AreaTreeNode node){
		Element e = element;

		String location = e.getChild("aName") == null? "" : e.getChild("aName").getValue();
		String type = e.getChild("type") == null? "" : e.getChild("type").getValue();
		String aid = e.getChild("aid") == null? "" : e.getChild("aid").getValue();
		String descprition = e.getChild("des") == null? "" : e.getChild("des").getValue();
		String low = e.getChild("clow") == null? "" : e.getChild("clow").getValue();
		String high = e.getChild("chigh") == null? "" : e.getChild("chigh").getValue();
		String sn = e.getChild("sn") == null? "" : e.getChild("sn").getValue();
		//			String hum = e.getText();
		if(type.equals("6")){
			DevFrige devFrige = new DevFrige(Device.Type_Frige, aid, location, "", descprition, low, high, "", sn);
			node.addDevice(devFrige);
		}
		else if(type.equals("7")){
			DevHumidity devHum = new DevHumidity(Device.Type_Humidity, aid, location, "", descprition, low, high, "", sn);
			node.addDevice(devHum);
		}
	}

	public BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub

			String resXml = intent.getStringExtra(WebClient.Param_resXml);
			if(intent.getAction().equals(WebClient.INTERNAL_ACTION_GETBYMACID))
			{
				if(resXml != null)
				{
					if(resXml.equals("error") || resXml.equals("null")){
							mHeaderprogressBarRefresh.setVisibility(View.INVISIBLE);
						UIHealper.DisplayToast(appContext,"获取设备信息失败！");
					}
					else {
						mHeaderprogressBarRefresh.setVisibility(View.INVISIBLE);
						try{
							SAXBuilder builder = new SAXBuilder();
							StringReader sr = new StringReader(resXml);   
							InputSource is = new InputSource(sr); 
							Document Doc = builder.build(is);
							Element rootElement = (Element) Doc.getRootElement();
							AreaTreeNode node = new AreaTreeNode(null,"root","0",false, new ArrayList<TreeNode>(), new ArrayList<Device>());
							parseGetDevInfoXml(rootElement, node);
							//设备详细信息头
							LoadDevInfo(node);
//							mDevLogListViewAdapter.setListItems(lvDevLogData);
//							mDevLogListViewAdapter.notifyDataSetChanged();
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
					
				}

			}		
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dev_detail);
		appContext = (AppContext) getApplication();

		mCustomView = getLayoutInflater().inflate(R.layout.custom_actionbar, null);

		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setCustomView(mCustomView,new ActionBar.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_gradient_bg));
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		mHeaderRefresh = (ImageButton) mCustomView.findViewById(R.id.main_head_refresh);
		ImageButton locationButton =  (ImageButton) mCustomView.findViewById(R.id.main_head_location);
		locationButton.setVisibility(View.GONE);
		mHeaderHome = (ImageButton) mCustomView.findViewById(R.id.main_head_home);
		mHeaderHome.setVisibility(View.VISIBLE);
		mHeaderTittle = (TextView) mCustomView.findViewById(R.id.main_head_title);
		mHeaderprogressBarRefresh = (ProgressBar) mCustomView.findViewById(R.id.main_head_progressBar_refresh);
		mHeaderTittle.setText(R.string.header_tittle_dev_detail);
		mHeaderRefresh.setVisibility(View.VISIBLE);
		
		
		mHistoryCurveMenu = (Button) findViewById(R.id.history_curve_menu);
		mHistoryCurveMenu.setOnClickListener(this);
		mHistoryCurveMenu.setTag(CLICK_HISTORY_CURVE);
		
		mHistoryDateMenu = (Button) findViewById(R.id.history_date_menu);
		mHistoryDateMenu.setOnClickListener(this);
		mHistoryDateMenu.setTag(CLICK_HISTORY_DATE);
		
		mHeaderHome.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				UIHealper.showHome(DevDetailActivity.this);
			}
		});
		
		mHeaderRefresh.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onDevInfoUpdate();
			}
		});

		//正在加载布局
		
//		mDevLogListViewAdapter =new DevLogListViewAdapter(this, lvDevLogData, R.layout.dev_detail_log_listitem);
//		mLogListView.setAdapter(mDevLogListViewAdapter);

		String id = getString(R.string.device_info_id_tittle);

		DevDetailMacID = (String) getIntent().getCharSequenceExtra(id);

		onDevInfoUpdate();

		
		mSectionsPagerAdapter = new DevDetailPagerAdapter(getSupportFragmentManager());

		mViewPager = (ViewPager) findViewById(R.id.dev_detail_pager);
		mViewPager.setOffscreenPageLimit(2);

		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.requestDisallowInterceptTouchEvent(true);
		//当页面发生改变的时候（比如翻滚）会进入此函数
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {

						switch (position) {
						case 0:
							mHistoryCurveMenu.setTextColor(Color.RED);
							mHistoryDateMenu.setTextColor(Color.BLACK);
							break;
						case 1:
							mHistoryCurveMenu.setTextColor(Color.BLACK);
							mHistoryDateMenu.setTextColor(Color.RED);
							break;

						default:
							break;
						}
					}
				});
		
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(WebClient.INTERNAL_ACTION_GETBYMACID);
		appContext.registerReceiver(receiver, filter);

//		int taskID = this.getTaskId();
//		UIHealper.DisplayToast(appContext, "taskID = "+taskID);
	}
	
	public void onClick(View v)
	{
		int tag = (Integer) v.getTag();
		switch (tag)
		{
			case CLICK_HISTORY_CURVE:
				mViewPager.setCurrentItem(0);break;
			case CLICK_HISTORY_DATE:
				mViewPager.setCurrentItem(1);break;
			default:break;
		}
	}
	private class DevDetailPagerAdapter extends FragmentPagerAdapter {
		DevDetailHistoryCurveFragment mDevDetailHistoryCurveFragment;
		DevDetailHistoryListFragment mDevDetailHistoryListFragment;
		public DevDetailPagerAdapter(FragmentManager fm) {
			super(fm);
			mDevDetailHistoryCurveFragment = null;
			mDevDetailHistoryListFragment = null;
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				if (mDevDetailHistoryCurveFragment == null)
					mDevDetailHistoryCurveFragment = new DevDetailHistoryCurveFragment();
				return mDevDetailHistoryCurveFragment;
			case 1:
				if (mDevDetailHistoryListFragment == null)
					mDevDetailHistoryListFragment = new DevDetailHistoryListFragment();
				return mDevDetailHistoryListFragment;
			}
			return null;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 2;
		}
	}
	@Override
	protected void onStart (){
		super.onStart();
//		visiable =true;

//		if(mRefreshThread == null || !mRefreshThread.isAlive()){
//			mRefreshThread = new RefreshThread();
//			mRefreshThread.start();
//		}
	}

	@Override
	protected void onStop(){	
		super.onStop();
//		visiable = false;
//		try {
//			mRefreshThread.join(15);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		appContext.unregisterReceiver(receiver);
	}

	private void setDevInfo(Device dev){
		TextView textView;
		
		textView= (TextView)findViewById(R.id.device_info_description);
		textView.setText(getString(R.string.device_description)+dev.getmDescription());
		
		textView= (TextView)findViewById(R.id.device_info_location);
		textView.setText(getString(R.string.device_info_location_tittle)+dev.getmLocation());
		
		textView= (TextView)findViewById(R.id.device_info_boundary);
		textView.setText(getString(R.string.device_info_boundary_tittile)+dev.getmLow()+" to "+dev.getmHigh());
		
		textView= (TextView)findViewById(R.id.device_info_sn);
		textView.setText(getString(R.string.device_sn)+dev.getmSn());
		
		ImageView imageView = (ImageView)findViewById(R.id.listitem_imageview);
		if(dev.getmType().equals(Device.Type_Frige))
		{
			imageView.setBackgroundResource(R.drawable.icon_temperature);
		}
		else if(dev.getmType().equals(Device.Type_Humidity))
		{
			imageView.setBackgroundResource(R.drawable.icon_humidity);
		}
		mDeviceType = dev.getmType();
	}

	private void LoadDevInfo(AreaTreeNode node){
		if(node == null) return;
		if(node.getmDevices().size() >0)
		{
			Device dev = node.getmDevices().get(0);
			setDevInfo(dev);
		}

		if(node.getmChildren() == null){ 
			return;
		}
		else
		{
			for(int i = 0 ; i < node.getmChildren().size() ; i++  )
			{
				LoadDevInfo((AreaTreeNode) node.getmChildren().get(i));
			}
		}
	}

	private void onDevInfoUpdate(){
		if(DevDetailMacID != null)
		{
			mHeaderprogressBarRefresh.setVisibility(View.VISIBLE);
			WebClient client = WebClient.getInstance();
			Map<String,String> param = new HashMap<String, String>();
			param.put("MACID", DevDetailMacID);
			client.sendMessage(appContext, WebClient.Method_getByMACID, param);
		}
	}

	public void finishWithResult(int resultCode , Intent data)
	{
		this.setResult(resultCode, data);
		this.finish();
	}
}
