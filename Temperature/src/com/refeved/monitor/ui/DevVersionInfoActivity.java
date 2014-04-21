package com.refeved.monitor.ui;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.refeved.monitor.R;
import com.refeved.monitor.util.DownLoadManager;
import com.refeved.monitor.util.UpdataInfo;
import com.refeved.monitor.util.UpdataInfoParser;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;


public class DevVersionInfoActivity extends BaseActivity{
	
	private UpdataInfo info;
	public String TAG = "huwei";
	public final int UPDATA_CLIENT = 1;
	public final int GET_UNDATAINFO_ERROR = 2;
	public final int DOWN_ERROR = 3;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dev_version_info);
	}
	protected void onDestroy(){
		super.onDestroy();
	}


	/*  
	 * ��ȡ��ǰ����İ汾��   
	 */   
	private String getVersionName() throws Exception{   
	    //��ȡpackagemanager��ʵ��     
	    PackageManager packageManager = getPackageManager();   
	    //getPackageName()���㵱ǰ��İ�����0�����ǻ�ȡ�汾��Ϣ    
	    PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);   
	    return packInfo.versionName;    
	}  
	/*  
	 * �ӷ�������ȡxml���������бȶ԰汾��   
	 */    
	public class CheckVersionTask implements Runnable{    
	    
		public void run() {    
	        try {    
	        	String versionname = getVersionName();
	        	//����Դ�ļ���ȡ������ ��ַ       
	            String path = getResources().getString(R.string.Abnormal_log_Text);    
	            //��װ��url�Ķ���       
	            URL url = new URL(path);    
	            HttpURLConnection conn =  (HttpURLConnection) url.openConnection();     
	            conn.setConnectTimeout(5000);    
	            InputStream is =conn.getInputStream();     
	            info =  UpdataInfoParser.getUpdataInfo(is);    
	                
	            if(info.getVersion().equals(versionname)){    
	                Log.i(TAG,"�汾����ͬ��������");    
	                LoginMain();    
	            }else{    
	                Log.i(TAG,"�汾�Ų�ͬ ,��ʾ�û����� ");    
	                Message msg = new Message();    
	                msg.what = UPDATA_CLIENT;    
	                handler.sendMessage(msg);    
	            }    
	        } catch (Exception e) {    
	            // ������       
	            Message msg = new Message();    
	            msg.what = GET_UNDATAINFO_ERROR;    
	            handler.sendMessage(msg);    
	            e.printStackTrace();    
	        }     
	    }    
	}
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler(){      
	    @Override  
	    public void handleMessage(Message msg) {  
	        // TODO Auto-generated method stub   
	        super.handleMessage(msg);  
	        switch (msg.what) {  
	        case UPDATA_CLIENT:  
	            //�Ի���֪ͨ�û���������    
	            showUpdataDialog();  
	            break;  
	            case GET_UNDATAINFO_ERROR:  
	                //��������ʱ    
	                Toast.makeText(getApplicationContext(), "��ȡ������������Ϣʧ��", 1).show();  
	                LoginMain();  
	            break;    
	            case DOWN_ERROR:  
	                //����apkʧ��   
	                Toast.makeText(getApplicationContext(), "�����°汾ʧ��", 1).show();  
	                LoginMain();  
	            break;    
	            }  
	    }  
	}; 
	/* 
	 *  
	 * �����Ի���֪ͨ�û����³���  
	 *  
	 * �����Ի���Ĳ��裺 
	 *  1.����alertDialog��builder.   
	 *  2.Ҫ��builder��������, �Ի��������,��ʽ,��ť 
	 *  3.ͨ��builder ����һ���Ի��� 
	 *  4.�Ի���show()����   
	 */  
	protected void showUpdataDialog() {  
	    AlertDialog.Builder builer = new Builder(this) ;   
	    builer.setTitle("�汾����");  
	    builer.setMessage(info.getDescription());  
	    //����ȷ����ťʱ�ӷ����������� �µ�apk Ȼ��װ    
	    builer.setPositiveButton("ȷ��", new OnClickListener() {  
	    public void onClick(DialogInterface dialog, int which) {  
	            Log.i(TAG,"����apk,����");  
	            downLoadApk();  
	        }     
	    });  
	    //����ȡ����ťʱ���е�¼   
	    builer.setNegativeButton("ȡ��", new OnClickListener() {  
	        public void onClick(DialogInterface dialog, int which) {  
	            // TODO Auto-generated method stub   
	            LoginMain();  
	        }  
	    });  
	    AlertDialog dialog = builer.create();  
	    dialog.show();  
	} 
	/* 
	 * �ӷ�����������APK 
	 */  
	protected void downLoadApk() {  
	    final ProgressDialog pd;    //�������Ի���   
	    pd = new  ProgressDialog(this);  
	    pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);  
	    pd.setMessage("�������ظ���");  
	    pd.show();  
	    new Thread(){  
	        @Override  
	        public void run() {  
	            try {  
	                File file = DownLoadManager.getFileFromServer(info.getUrl(), pd);  
	                sleep(3000);  
	                installApk(file);  
	                pd.dismiss(); //�������������Ի���   
	            } catch (Exception e) {  
	                Message msg = new Message();  
	                msg.what = DOWN_ERROR;  
	                handler.sendMessage(msg);  
	                e.printStackTrace();  
	            }  
	        }}.start();  
	}
	//��װapk    
	protected void installApk(File file) {  
	    Intent intent = new Intent();  
	    //ִ�ж���   
	    intent.setAction(Intent.ACTION_VIEW);  
	    //ִ�е���������   
	    intent.setDataAndType(Uri.fromFile(file), "application/vnd.Android.package-archive");//���߰����˴�AndroidӦΪandroid��������ɰ�װ����    
	    startActivity(intent);  
	}  
	/* 
	 * �������������� 
	 */  
	private void LoginMain(){  
	    Intent intent = new Intent(this,MainActivity.class);  
	    startActivity(intent);  
	    //��������ǰ��activity    
	    this.finish();  
	}
}