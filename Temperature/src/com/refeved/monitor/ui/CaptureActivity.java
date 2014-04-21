package com.refeved.monitor.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.refeved.monitor.AppContext;
import com.refeved.monitor.R;
import com.refeved.monitor.UIHealper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.refeved.monitor.camera.CameraManager;
import com.refeved.monitor.decoding.CaptureActivityHandler;
import com.refeved.monitor.decoding.InactivityTimer;
import com.refeved.monitor.view.ViewfinderView;
import com.refeved.monitor.net.WebClient;
import com.refeved.monitor.struct.DeviceScanning;
import com.refeved.monitor.util.CreatXmlString;
/**
 * Initial the camera
 * 
 * @author Ryan.Tang
 */
public class CaptureActivity extends Activity implements Callback, OnClickListener {

	private CaptureActivityHandler mHandler;
	private ViewfinderView mViewfinderView;
	private View mTitleView; //扫描界面的标题
	private View mScanningBehindView; //扫描后的界面
	private boolean mHasSurface;
	private Vector<BarcodeFormat> mDecodeFormats;
	private String mCharacterSet;
	private InactivityTimer mInactivityTimer;
	private MediaPlayer mMediaPlayer;
	private boolean mPlayBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean mVibrate;
	private int mDeviceDesCount = 0;
	private AppContext appContext;
	
	private String mPrevDeviceMacid = "";
	//扫描的设备列表
	private List<DeviceScanning> lvDevData = new ArrayList<DeviceScanning>();
	/*
	 * 界面控件
	 * */
	private Button mButtonBack; //返回按钮
	private Button mButtonChangeRoom;//更换房间按钮
	private Button mButtonPutStorage;//入库按钮
	private TextView mTextViewDistrict;//地址显示文本框
	private TextView mTextViewDeviceDesCount;//设备描述计数
	private EditText mEditTextDeviceDesName;//设备描述名
	private EditText mEditTextDeviceUpperLimit;//輸入值上限
	private EditText mEditTextDeviceLowerLimit;//輸入值下限
	
	private TextView mTextViewCaptureSerialNumber; //设备序列号
	private TextView mTextViewCaptureType;//设备类型
	private TextView mTextViewCaptureDes;//设备描述
	private TextView mTextViewCaptureUpperLimit;//温湿度上限
	private TextView mTextViewCaptureLowerLimit;//温湿度下限
	private TextView mTextViewCaptureOffset;//温湿度偏移量
	private Button mButtonCaptureAgain;//重新扫描按钮
	private Button mButtonCaptureHold;//保存扫描结果按钮
	private TextView mTextViewDeviceNum;//显示未入库设备个数

	private static final int CLICK_BACK = 1;  //扫描界面返回键
	private static final int CLICK_CHANGE_ROOM = 2; //切换房间按钮
	private static final int CLICK_PUT_STORAGE = 3; //入库按钮
	private static final int CLICK_CAPTURE_AGAIN = 4; //重新扫描按钮
	private static final int CLICK_CAPTURE_HOLD = 5; //保存扫描结果按钮
	
	private String deviceAid = "0";//地址aid
	private String [] line = new String[10];//用于保存临时设备信息
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.capture_activity);
		// ViewUtil.addTopView(getApplicationContext(), this,
		// R.string.scan_card);
		CameraManager.init(getApplication());
		appContext = (AppContext) getApplication();
		mViewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		mTitleView =  findViewById(R.id.capture_title);
		mScanningBehindView =  findViewById(R.id.include1);
		mButtonBack = (Button) findViewById(R.id.button_back);
		mTextViewDistrict = (TextView) findViewById(R.id.textview_title);
		mButtonChangeRoom = (Button) findViewById(R.id.capture_change_room_button);
		mButtonPutStorage = (Button) findViewById(R.id.capture_put_storage_button);
		
		mEditTextDeviceDesName = (EditText) findViewById(R.id.EditText_device_info);
		mTextViewDeviceDesCount = (TextView) findViewById(R.id.device_info_count);
		mEditTextDeviceUpperLimit = (EditText) findViewById(R.id.EditText_device_upper_limit);
		mEditTextDeviceLowerLimit = (EditText) findViewById(R.id.EditText_device_lower_limit);
		
		mTextViewCaptureSerialNumber = (TextView) findViewById(R.id.textview_capture_serial_number);
		mTextViewCaptureType = (TextView) findViewById(R.id.textview_capture_type);
		mTextViewCaptureDes = (TextView) findViewById(R.id.textview_capture_des);
		mTextViewCaptureUpperLimit = (TextView) findViewById(R.id.textview_capture_upper_limit);
		mTextViewCaptureLowerLimit = (TextView) findViewById(R.id.textview_capture_lower_limit);
		mTextViewCaptureOffset = (TextView) findViewById(R.id.textview_capture_offset);
		mButtonCaptureAgain = (Button) findViewById(R.id.button_capture_again);
		mButtonCaptureHold = (Button) findViewById(R.id.button_capture_hold);
		mTextViewDeviceNum = (TextView) findViewById(R.id.TextView_device_num);
		mTextViewDeviceNum.setText("0");
		
		//获取intent附加值
		Bundle extras = getIntent().getExtras();
		if(extras != null){
			mTextViewDistrict.setText(extras.getString("mName"));//在标题栏显示地址
			deviceAid = extras.getString("AID");	
		}else{
			mTextViewDistrict.setText(R.string.Capture_room_hint);//在标题栏显示地址
			deviceAid = "";	
		}
		mTextViewDeviceDesCount.setText(Integer.toString(mDeviceDesCount));
		
		mButtonBack.setOnClickListener(this); 
		mButtonBack.setTag(CLICK_BACK); 
		mButtonChangeRoom.setOnClickListener(this); 
		mButtonChangeRoom.setTag(CLICK_CHANGE_ROOM); 
		mButtonPutStorage.setOnClickListener(this); 
		mButtonPutStorage.setTag(CLICK_PUT_STORAGE); 
		mButtonCaptureAgain.setOnClickListener(this); 
		mButtonCaptureAgain.setTag(CLICK_CAPTURE_AGAIN); 
		mButtonCaptureHold.setOnClickListener(this); 
		mButtonCaptureHold.setTag(CLICK_CAPTURE_HOLD); 

		mHasSurface = false;
		mInactivityTimer = new InactivityTimer(this);
		IntentFilter filter = new IntentFilter();
		filter.addAction(WebClient.INTERNAL_ACTION_UPDATEBYSN);
		appContext.registerReceiver(receiver, filter);
	}
	
	private void onDevCaptureListupdate(String CaptureMethod,String MachineList) {
		WebClient client = WebClient.getInstance();
		Map<String,String> param = new HashMap<String, String>();
		param.put("MachineList", MachineList);
		client.sendMessage(appContext, CaptureMethod, param);
		
	}
	
	public BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String resXml = intent.getStringExtra(WebClient.Param_resXml);
			if(resXml == null) return;
			//判断是否接收到异常日志
			if(intent.getAction().equals(WebClient.INTERNAL_ACTION_UPDATEBYSN)){
				if(resXml.equals("error") || resXml.equals("null")){
					UIHealper.DisplayToast(appContext,"入库失败！");
				}
				else{
					if(lvDevData.size() > 0){
						UIHealper.DisplayToast(appContext,"入库成功！");
						lvDevData.clear();
					}
				}
				//显示未入库的设备个数
				mTextViewDeviceNum.setText(""+lvDevData.size());
			}
		}
	};
	public void onClick(View v)
	{
		int tag = (Integer) v.getTag();
		
		switch (tag)
		{
			case CLICK_BACK:
			{
				CaptureActivity.this.finish();
					
			}
			break;
			//更换按钮
			case CLICK_CHANGE_ROOM:
			{
				Intent intent = new Intent();
				intent.setClass(CaptureActivity.this, DistrictListViewActivity.class);
				intent.putExtra("TitleName", R.string.header_tittle_scanning_district);
				startActivityForResult(intent, SurveilSectionFragment.SELECT_DISTRICT);
			}
			break;
			//入库
			case CLICK_PUT_STORAGE:
			{
				if(lvDevData.size() > 0){
					String CaptureXml = CreatXmlString.createStringFromXmlDoc(lvDevData);
					onDevCaptureListupdate(WebClient.Method_updateBySN,CaptureXml); 
				}
				else{
					UIHealper.DisplayToast(appContext,"没有库存！");
				}
			}
			break;
			//重新扫描
			case CLICK_CAPTURE_AGAIN:
			{
				showScanningView();
			}
			break;
			//单个设备入库
			case CLICK_CAPTURE_HOLD:
			{
				String Describe = mTextViewCaptureDes.getText().toString();//设备描述
				String Aid = deviceAid;//设备地址aid
				String SN = line[1];//设备序列号
				String Type = line[2];//设备类型 6：代表温度设备  7：代表湿度设备
				String Clow = line[3];//温湿度上限
				String Chigh = line[4];//温湿度下限
				String Cdif = line[5];//温度偏差
				mPrevDeviceMacid = line[1];//记录已入库的当前设备
				//把从XML获取的信息存入湿度对象中
				DeviceScanning devInfo = new DeviceScanning(Describe, Aid, Type, Clow, Chigh, Cdif, SN);
				//把湿度信息增加到节点
				lvDevData.add(devInfo);
				//如果入库50个了，就自动入库
				if(lvDevData.size() >= 50){
					UIHealper.DisplayToast(appContext,"扫描达到设备达到50个，自动入库");
					String CaptureXml = CreatXmlString.createStringFromXmlDoc(lvDevData);
					onDevCaptureListupdate(WebClient.Method_updateBySN,CaptureXml); 
				}
				mDeviceDesCount++;//描述符后数字自动加一
				mTextViewDeviceDesCount.setText(Integer.toString(mDeviceDesCount));
				showScanningView();
				
			}
			break;
			default :break;
		}
	}

	
	@Override
	public void onActivityResult(int requestCode, int resultCode,  Intent data)  
	{ 
		super.onActivityResult(requestCode, resultCode, data);
		if(data != null){
			mTextViewDistrict.setText(data.getStringExtra("mName"));
			deviceAid = data.getStringExtra("AID");
		}
			
	}
	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (mHasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		mDecodeFormats = null;
		mCharacterSet = null;

		mPlayBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			mPlayBeep = false;
		}
		initBeepSound();
		mVibrate = true;

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mHandler != null) {
			mHandler.quitSynchronously();
			mHandler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		mInactivityTimer.shutdown();
		super.onDestroy();
	}

	/**
	 * 处理扫描结果
	 * 
	 * @param result
	 * @param barcode
	 */
	public void handleDecode(Result result, Bitmap barcode) {
		String resultString = result.getText();
		playBeepSoundAndVibrate();
		String DeviceDesName = mEditTextDeviceDesName.getText().toString();
		String DeviceLowerLimit = mEditTextDeviceLowerLimit.getText().toString();
		String DeviceUpperLimit = mEditTextDeviceUpperLimit.getText().toString();
		if(deviceAid.equals("")){
			UIHealper.DisplayToast(appContext,"请选择房间");
			if (mHandler != null) {
		        mHandler.sendEmptyMessage(R.id.restart_preview);
		        }
			return;
		}
		if(DeviceDesName.equals(""))
		{
			UIHealper.DisplayToast(appContext,"设备描述名不能为空，请输入后重新扫描！");
			if (mHandler != null) {
		        mHandler.sendEmptyMessage(R.id.restart_preview);
		        }
			return;
		}
		//DES解密
/*		try {
			
			String aaString = DesUtil.encrypt("huwei", "huweijje");
			 String teString = "huwei";
			 String key = "huweikey";
			 byte[] encryptresult = DesUtil.encrypt(teString.getBytes(),key.getBytes());
			key
			String key = "BGI@2014";
			 byte[] decryptresult = DesUtil.decrypt(resultString.getBytes(),key.getBytes());
			 String t = new String(decryptresult);
			 System.out.print(t);

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
		if(resultString.indexOf("BGI") == 0){
			int nStart = resultString.indexOf("#");
			String Start = resultString.substring(nStart+1);
			int nEnd = Start.indexOf("#");
			line[0] = "BGI";
			line[1] = Start.substring(0,nEnd);//设备序列号
			line[2] = Start.substring(nEnd+1);//设备类型
			line[3] = DeviceLowerLimit;//温度下限
			line[4] = DeviceUpperLimit;//温度上限
			line[5] = "5";//温度偏差
		}        
	    else{
			UIHealper.DisplayToast(appContext,"非正常设备，请重新扫描！");
			if (mHandler != null) {
		        mHandler.sendEmptyMessage(R.id.restart_preview);
		        }
			return;
	    }
 

//        BufferedReader reader = new BufferedReader(new StringReader(resultString));
//        
//        int k = 0;
//        try {
//			while ((line[k] = reader.readLine()) != null){
//				Log.i(LogPrint.CML(),line[k]);
//				k++;		
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		//监测此设备是否是已入库的上一个设备
		if(mPrevDeviceMacid.equals(line[1]) == true){
			UIHealper.DisplayToast(appContext,"设备已扫描过!");
			showScanningView();
			return;
		}
		
    	mTextViewCaptureSerialNumber.setText(line[1]);
    	mTextViewCaptureType.setText(line[2]);
    	mTextViewCaptureUpperLimit.setText(line[3]);
    	mTextViewCaptureLowerLimit.setText(line[4]);
    	mTextViewCaptureOffset.setText(line[5]);
        mTextViewCaptureDes.setText(mEditTextDeviceDesName.getText().toString() + mDeviceDesCount);
		mInactivityTimer.onActivity();
//		mViewfinderView.drawResultBitmap(barcode);
		
		showScanningBehindView();
	}
	/*显示扫描界面*/
	private void showScanningView(){
		mTitleView.setVisibility(View.VISIBLE);
		mViewfinderView.setVisibility(View.VISIBLE);
		mScanningBehindView.setVisibility(View.GONE);
		if (mHandler != null) {
			mHandler.sendEmptyMessage(R.id.restart_preview);
		}
		//显示未入库的设备个数
		mTextViewDeviceNum.setText(""+lvDevData.size());
	}
	/*显示扫描后的界面*/
	private void showScanningBehindView(){
		mTitleView.setVisibility(View.GONE);
		mViewfinderView.setVisibility(View.GONE);
		mScanningBehindView.setVisibility(View.VISIBLE);
	}
	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (mHandler == null) {
			mHandler = new CaptureActivityHandler(this, mDecodeFormats,
					mCharacterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!mHasSurface) {
			mHasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mHasSurface = false;

	}

	public ViewfinderView getViewfinderView() {
		return mViewfinderView;
	}

	public Handler getHandler() {
		return mHandler;
	}

	public void drawViewfinder() {
		mViewfinderView.drawViewfinder();

	}

	private void initBeepSound() {
		if (mPlayBeep && mMediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mMediaPlayer = new MediaPlayer();
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mMediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				mMediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mMediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mMediaPlayer.prepare();
			} catch (IOException e) {
				mMediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (mPlayBeep && mMediaPlayer != null) {
			mMediaPlayer.start();
		}
		if (mVibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

}