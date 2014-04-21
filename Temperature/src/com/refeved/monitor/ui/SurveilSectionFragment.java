/*
 * 文件名：SurveilSectionFragment.java
 * 功能：监控界面，监控各个设备的温度和报警状态
 * 作者：huwei
 * 创建时间：2013-10-25
 * 
 * 
 * 
 * */
package com.refeved.monitor.ui;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.InputSource;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragment;
import com.refeved.monitor.AppContext;
import com.refeved.monitor.R;
import com.refeved.monitor.UIHealper;
import com.refeved.monitor.net.BackgroundService;
import com.refeved.monitor.net.WebClient;
import com.refeved.monitor.struct.AreaTreeNode;
import com.refeved.monitor.struct.DevFrige;
import com.refeved.monitor.struct.DevHumidity;
import com.refeved.monitor.struct.Device;
import com.refeved.monitor.struct.TreeNode;
import com.refeved.monitor.view.GridViewBase;
import com.refeved.monitor.view.ListViewBase;

public class SurveilSectionFragment extends SherlockFragment implements OnClickListener,OnItemClickListener{
	private AppContext appContext;
	private ImageButton mHeaderRefresh;
	private ImageButton mHeaderLocation;
	private TextView mHeaderTextCount;
	private ProgressBar mHeaderProgressBarRefresh;
	ListViewBase mDevListView;
	GridViewBase mDevGridView;
	GridViewBase mDevGridView_2;
	TextView emptyTextView;
	private static final int CLICK_DISTRICT_BUTTON = 1;  //地址按钮
	private static final int CLICK_FRESH_BUTTON = 2; //刷新按钮
	
	private static final int HANDLER_DEVUPDATE = 1; //设备更新
	private static final int HANDLER_SHOW_COUNT = 2; //计数显示
	private static final int HANDLER_CLOSE_COUNT = 3; //关闭计数显示
	private static final int HANDLER_CLOSE_REFRESH_BUTTON = 4; //关闭刷新按钮
	private static boolean mCountThreadStutas = false; //判断计数线程是否为等待状态
	int count = 5;
	//String AID = "32";
	Boolean visiable = false;
	private List<Device> lvDevData = new ArrayList<Device>();

	public static final String ARG_SECTION_NUMBER = "section_number";
	public static final int SELECT_DISTRICT = 1;
	
	final Semaphore sempCountThread = new Semaphore(0);
	
	private View mSurveilListEmptyOnClick;
	@SuppressLint("HandlerLeak")
	public Handler mHandler=new Handler()  
	{  
		public void handleMessage(Message msg)  
		{  
			switch(msg.what)  
			{  
			case HANDLER_DEVUPDATE:  
				onDevListUpdate();
				break;  
			case HANDLER_SHOW_COUNT:
				if(mHeaderTextCount != null){
					mHeaderTextCount.setVisibility(View.VISIBLE);
					mHeaderTextCount.setText(""+msg.arg1);		
				}
				break;
			case HANDLER_CLOSE_COUNT:
				mHeaderTextCount.setVisibility(View.INVISIBLE);
				break;
			case HANDLER_CLOSE_REFRESH_BUTTON:
				mHeaderRefresh.setVisibility(View.INVISIBLE);
			default:  
				break;            
			}  
			super.handleMessage(msg);  
		}  
	}; 

	public void parseGetByAidXml(Element element,AreaTreeNode node, String parentLocation){
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
			//		   String hum = e.getText();
			String hum = e.getAttributeValue("humidity");
			//把从XML获取的信息存入湿度对象中
			DevHumidity devHum = new DevHumidity(type, id, location, status, descprition,low,high, hum,"");
			//把湿度信息增加到节点
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
			//		   String temp = e.getText();
			String temp = e.getAttributeValue("temperature");
			DevFrige devHum = new DevFrige(type, id, location, status, descprition,low,high, temp,"");
			node.addDevice(devHum);
			element.removeChild("temperature");
		}

		while( (e =element.getChild("address")) != null)
		{
			String id = e.getAttributeValue("aid");
			String name = e.getAttributeValue("des");
			AreaTreeNode n = new AreaTreeNode(node, id, name, false, new ArrayList<TreeNode>(), new ArrayList<Device>());
			node.getmChildren().add(n);

			parseGetByAidXml(e,n,location);
			element.removeChild("address");
		}
	}

	public BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub

			String resXml = intent.getStringExtra(WebClient.Param_resXml);
//			String resXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><root><address aid='1' des='深圳' pid='0' rightIndex='48'><temperature macid='19' state='9' low='-50.0' high='-30.0' des='dabingxiang' temperature='00000'/><temperature macid='20' state='9' low='-60.0' high='-90.0' des='测试1' temperature='00000'/><address aid='13' des='深圳1楼' pid='1' rightIndex='60'></address><address aid='15' des='深圳8楼' pid='1' rightIndex='62'><humidity macid='15' state='9' low='-90.0' high='70.0' des='湿度1' humidity='16.00'/><humidity macid='16' state='9' low='-500.0' high='500.0' des='11' humidity='00000'/><temperature macid='1' state='25' low='-95.0' high='-60.0' des='冰箱1' temperature='-29.40'/><temperature macid='2' state='9' low='-90.0' high='-70.0' des='冰箱2' temperature='-65.00'/><temperature macid='3' state='9' low='-90.0' high='-70.0' des='冰箱3' temperature='00000'/><temperature macid='4' state='9' low='-90.0' high='-70.0' des='冰箱4' temperature='-87.60'/><temperature macid='5' state='25' low='-90.0' high='-50.0' des='冰箱5' temperature='51.00'/><temperature macid='6' state='9' low='-90.0' high='-70.0' des='冰箱6' temperature='00000'/><temperature macid='7' state='9' low='-90.0' high='-70.0' des='冰箱7' temperature='-82.50'/><temperature macid='8' state='9' low='-90.0' high='-70.0' des='小探头测试' temperature='-73.70'/><temperature macid='9' state='9' low='-90.0' high='-70.0' des='测试1' temperature='-78.80'/></address><address aid='17' des='test地址33' pid='1' rightIndex='64'></address><address aid='21' des='test' pid='1' rightIndex='69'></address><address aid='29' des='100台测试' pid='1' rightIndex='77'><humidity macid='43' state='10' low='-200.0' high='200.0' des='00124b0003966150' humidity='-0.10'/><humidity macid='44' state='10' low='-200.0' high='200.0' des='00124b0003966234' humidity='-0.10'/><humidity macid='45' state='10' low='-200.0' high='200.0' des='00124b0003966155' humidity='-0.10'/><humidity macid='46' state='10' low='-200.0' high='200.0' des='00124b0003966140' humidity='-0.10'/><humidity macid='48' state='14' low='-200.0' high='200.0' des='00124b000396613e' humidity='-0.10'/><temperature macid='18' state='10' low='-100.0' high='200.0' des='00124b000396603b' temperature='101.00'/><temperature macid='35' state='10' low='-100.0' high='200.0' des='00124b00039679e9' temperature='100.30'/><temperature macid='36' state='10' low='-100.0' high='200.0' des='00124b00039675e6' temperature='101.00'/><temperature macid='37' state='11' low='-100.0' high='200.0' des='00124b0003966146' temperature='-32.30'/><temperature macid='38' state='10' low='-100.0' high='200.0' des='00124b0003966173' temperature='101.00'/><temperature macid='39' state='11' low='-100.0' high='200.0' des='00124b0003966227' temperature='101.00'/><temperature macid='40' state='10' low='-500.0' high='500.0' des='00124b0003967596' temperature='100.30'/><temperature macid='41' state='11' low='-200.0' high='200.0' des='00124b000396617a' temperature='101.00'/><temperature macid='42' state='10' low='-200.0' high='200.0' des='00124b0003966230' temperature='101.00'/><temperature macid='47' state='10' low='-200.0' high='200.0' des='00124b0003965f1f' temperature='101.00'/><temperature macid='49' state='10' low='-200.0' high='200.0' des='00124b00039678f5' temperature='101.00'/><temperature macid='50' state='14' low='-200.0' high='200.0' des='00124b00039679a9' temperature='101.00'/><temperature macid='51' state='10' low='-200.0' high='200.0' des='00124b00039679a8' temperature='101.00'/><temperature macid='52' state='10' low='-200.0' high='200.0' des='00124b0003967999' temperature='47.70'/><temperature macid='53' state='10' low='-200.0' high='200.0' des='00124b00039678aa' temperature='47.60'/><temperature macid='54' state='10' low='-200.0' high='200.0' des='00124b00039675a2' temperature='101.00'/><temperature macid='55' state='10' low='-200.0' high='200.0' des='00124b00039679a6' temperature='101.00'/><temperature macid='56' state='10' low='-200.0' high='200.0' des='00124b00039679c7' temperature='100.30'/><temperature macid='57' state='11' low='-200.0' high='200.0' des='00124b00039675aa' temperature='101.00'/><temperature macid='58' state='10' low='-200.0' high='200.0' des='00124b000396759e' temperature='101.00'/><temperature macid='59' state='10' low='-200.0' high='200.0' des='00124b0003967887' temperature='101.00'/></address></address><address aid='20' des='已删除设备' pid='0' rightIndex='68'></address></root>";
			if(intent.getAction().equals(WebClient.INTERNAL_ACTION_GETBYAID_DONE))
			{
				if(resXml != null)
				{
					if(resXml.equals("error") || resXml.equals("null"))
					{
						mHeaderProgressBarRefresh.setVisibility(View.INVISIBLE);
						if(mDevGridView.getAdapter().getCount() == 0){
							mSurveilListEmptyOnClick.setVisibility(View.VISIBLE);
						}
						if(mCountThreadStutas)
							sempCountThread.release();
						UIHealper.DisplayToast(appContext,"获取设备信息失败");
					}
					else {
						mSurveilListEmptyOnClick.setVisibility(View.GONE);
						mHeaderProgressBarRefresh.setVisibility(View.INVISIBLE);
						try{
							//SAXBuilder是一个JDOM解析器 能将路径中的XML文件解析为Document对象
							SAXBuilder builder = new SAXBuilder();
							StringReader sr = new StringReader(resXml);   
							InputSource is = new InputSource(sr); 
							Document Doc = builder.build(is);
							Element rootElement = (Element) Doc.getRootElement();
							AreaTreeNode node = new AreaTreeNode(null,"root","0",false, new ArrayList<TreeNode>(), new ArrayList<Device>());
							parseGetByAidXml(rootElement, node, "");
							lvDevData.clear();
							//接收到设备列表后，进行筛选
							filterDevice(node,lvDevData);
							if(lvDevData.size() == 0){
								mSurveilListEmptyOnClick.setVisibility(View.VISIBLE);
							}
							mDevGridView.updateListView(lvDevData);
							if(mCountThreadStutas)
								sempCountThread.release();
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
	private void HandlerSendMessage(int xWhat,int xArg1){
		Message message=new Message();  
		message.what = xWhat;  
		message.arg1 = xArg1;
		mHandler.sendMessage(message);  	
	}
	public class RefreshThread  extends Thread{

		@Override
		public void run(){
			while(appContext.SettingsRefreshFrequency > 0 && visiable)
			{
				HandlerSendMessage(HANDLER_CLOSE_REFRESH_BUTTON, -1);
				int count = appContext.SettingsRefreshFrequency;
				Log.i("huwei", "SurveilThread-->count = "+count);

				while(count-- > 0)
				{
					try {  
						//显示计数
						HandlerSendMessage(HANDLER_SHOW_COUNT,count+1);
						Thread.sleep(1000);  
					} catch (InterruptedException e) {  
						e.printStackTrace();  
						break;
					}  
				}
				if(visiable){
					//更新设备信息
					HandlerSendMessage(HANDLER_CLOSE_COUNT,-1);
					HandlerSendMessage(HANDLER_DEVUPDATE,-1);
					try {
						mCountThreadStutas = true;
						sempCountThread.acquire();
						mCountThreadStutas = false;
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			//线程退出时关闭计数显示
			HandlerSendMessage(HANDLER_CLOSE_COUNT,-1);
		}
	}
	
	RefreshThread mRefreshThread;

	public SurveilSectionFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//在onCreate()方法中调用setHasOptionsMenu()，否则系统不会调用Fragment的onCreateOptionsMenu()方法
		setHasOptionsMenu(true);
		appContext = (AppContext) getSherlockActivity().getApplication();        
		IntentFilter filter = new IntentFilter();
		filter.addAction(WebClient.INTERNAL_ACTION_GETBYAID_DONE);
		appContext.registerReceiver(receiver, filter);
	}
	//onCreateView是创建该fragment对应的视图
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.surveil_fragment_gridview, container,false);

	}
	//当activity的onCreate()方法返回时调用。
	@Override
	public void onActivityCreated (Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);

		mDevGridView = (GridViewBase)getView().findViewById(R.id.grid);
		//在监控界面如果有设备被点击，就会显示设备细节
		mDevGridView.setOnItemClickListener(this);
		
		
		final ActionBar actionBar = getSherlockActivity().getSupportActionBar();
		mHeaderRefresh =  (ImageButton)actionBar.getCustomView().findViewById(R.id.main_head_refresh);
		//监听刷新按钮
		mHeaderRefresh.setOnClickListener(this);
		mHeaderRefresh.setTag(CLICK_FRESH_BUTTON);

		mHeaderLocation = (ImageButton)actionBar.getCustomView().findViewById(R.id.main_head_location);
		mHeaderLocation.setOnClickListener(this);
		mHeaderLocation.setTag(CLICK_DISTRICT_BUTTON);
		
		mHeaderTextCount = (TextView) actionBar.getCustomView()
				.findViewById(R.id.main_head_textview_count);
		mHeaderProgressBarRefresh = (ProgressBar) actionBar.getCustomView()
				.findViewById(R.id.main_head_progressBar_refresh);
		//正在加载布局
//		mDevGridView.setEmptyView(mSurveilEmptyLoading);
		//空白列表点击加载
		mSurveilListEmptyOnClick = getView().findViewById(R.id.SurveilListOnClick);
		mSurveilListEmptyOnClick.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mHeaderProgressBarRefresh.setVisibility(View.VISIBLE);
				mSurveilListEmptyOnClick.setVisibility(View.GONE);
				onDevListUpdate();
			}

		});
		//向服务器索要设备信息
		onDevListUpdate();
	}
	
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if(mDevGridView.getItems().isEmpty()) return;
		showDevDetail((Device)mDevGridView.getItems().get(position));
	}
	
	public void onClick(View v)
	{
		int tag = (Integer) v.getTag();
		
		switch (tag)
		{
			
			case CLICK_DISTRICT_BUTTON:
			{
				// TODO Auto-generated method stub
				Intent intent = new Intent(getSherlockActivity(),DistrictListViewActivity.class);
				intent.putExtra("TitleName", R.string.header_tittle_dev_district);
				startActivityForResult(intent, SELECT_DISTRICT);

				if( !(appContext.SettingsNotification && appContext.SettingsRunInBackGround))
				{
					if(appContext.BackgroundServiceIntent != null)
					{
						getSherlockActivity().stopService(appContext.BackgroundServiceIntent);
						appContext.BackgroundServiceIntent = null;
					}
				}
			}
			break;
			case CLICK_FRESH_BUTTON:
			{
				onDevListUpdate();
			}
				
			break;
			default:break;
		}
	}
	
//	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//		if(mDevGridView.getItems().isEmpty()) return;
//		showDevDetail((Device)mDevGridView.getItems().get(position));
//	}
	//Set a hint to the system about whether this fragment's UI is currently visible to the user.
	@Override
	public void setUserVisibleHint (boolean isVisibleToUser){
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);
		visiable = isVisibleToUser;
		if (isVisibleToUser == true) {
			if(mRefreshThread == null){
				mRefreshThread = new RefreshThread();
				try {
					mRefreshThread.start(); 
				} catch (IllegalThreadStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		else
		{
			if(mRefreshThread != null){
				mRefreshThread.interrupt();
				mRefreshThread = null;
			}
		}
	}
    //Ativity之间的传值 其实就是onActivityResult
	@SuppressLint("CommitPrefEdits")
	@Override
	public void onActivityResult(int requestCode, int resultCode,  Intent data)  
	{   
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == SELECT_DISTRICT)
		{
			if(resultCode == SherlockActivity.RESULT_OK)
			{
				if(data != null)
				{
					appContext.AID = data.getStringExtra("AID");
					//2个activity 之间的数据传递可以使用SharedPreferences来共享数据
					SharedPreferences settings = appContext.getSharedPreferences(getString(R.string.settings_filename),Context.MODE_PRIVATE); 
					SharedPreferences.Editor editor = settings.edit();   
					editor.putString(getString(R.string.settings_aid), appContext.AID); 
					editor.commit();
					onDevListUpdate();

					if(appContext.SettingsNotification)
					{
						if(appContext.BackgroundServiceIntent == null)
						{
							appContext.BackgroundServiceIntent = new Intent(appContext,BackgroundService.class);
							appContext.startService(appContext.BackgroundServiceIntent);
						}
					}
				}
			}
		}
	}

	@Override
	public void onStop(){
		super.onStop();
		
	}
	@Override
	public void onStart(){
		super.onStart();
	}
	@Override
	public void onDestroy(){
		super.onDestroy();
		appContext.unregisterReceiver(receiver);
	}

	private void loadListViewData(AreaTreeNode node,List<Device> list){
		if(node == null || list == null) return;
		if(node.getmDevices().size() >0)
		{
			Device devTag = new Device(Device.Type_Tag,"", node.getmDevices().get(0).getmLocation(), "", "","","","");
			list.add(devTag);
			list.addAll(node.getmDevices());
		}

		if(node.getmChildren() == null){ 
			return;
		}
		else
		{
			for(int i = 0 ; i < node.getmChildren().size() ; i++  )
			{
				loadListViewData((AreaTreeNode) node.getmChildren().get(i),list);
			}
		}
	}
	//显示设备细节
	public void showDevDetail(Device device)
	{
		Intent intent = new Intent(getSherlockActivity(), DevDetailActivity.class);
		String id = getString(R.string.device_info_id_tittle);
		intent.putExtra(id, device.getmId());
		startActivity(intent);
	}
 //更新设备列表
	private void onDevListUpdate(){
		mSurveilListEmptyOnClick.setVisibility(View.GONE);
		mHeaderProgressBarRefresh.setVisibility(View.VISIBLE);
		WebClient client = WebClient.getInstance();
		client.sendMessage(appContext, WebClient.Method_getMonitorInfosForAll, null);
	}
	     
	/*
	 * 功能：遍历设备列表，筛选出需要显示的区域
	 * 参数 node：传入的设备树
	 * 参数 list：筛选出来的设备
	 * */
     public void filterDevice(AreaTreeNode node, List<Device> list){  
 		if(node == null || list == null) return ;
 		
 		if(node.getmChildren() == null){ 
 			return;
 		}
 		else
 		{
 			for(int i = 0 ; i < node.getmChildren().size() ; i++  )
 			{
 				//判断是否和所选择的地址区域的节点相同，如果相同就把树型列表转化为可显示设备列表的形式
 				if(node.getmChildren().get(i).getmId().equals(appContext.AID))
 				{
 					loadListViewData((AreaTreeNode) node.getmChildren().get(i),list);
 					return ;
 				}
 				filterDevice((AreaTreeNode) node.getmChildren().get(i),list);
 			}
 		}
     }
}
     
