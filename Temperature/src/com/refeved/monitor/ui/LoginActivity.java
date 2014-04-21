/*
 * �ļ�����LoginActivity.java
 * ���ܣ���½����
 * ���ߣ�huwei
 * ����ʱ�䣺2013-10-17
 * 
 * 
 * 
 * */
package com.refeved.monitor.ui;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.InputSource;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.refeved.monitor.AppContext;
import com.refeved.monitor.R;
import com.refeved.monitor.UIHealper;
import com.refeved.monitor.net.WebClient;
import com.refeved.monitor.struct.UserInfo;


public class LoginActivity extends BaseActivity
{
	Button button_land;
	Button button_setting;
	private AppContext appContext;
	ProgressDialog m_pDialog;
	private EditText unameEt,upassEt;
	private static final String FILE_NAME="saveUserNamePwd";
	private UserInfo mUserInfo;
	private RadioGroup      m_RadioGroupNetSelect;  
	private RadioButton     m_RadioButtonInwardNet,m_RadioButtonExternalNet; 
	public static final String ExternalServerIP = "net.serverip.External";
	public static final String InwardServerIP = "net.serverip.Inward";
	private String mRadioGroupStatus;//����ip��ѡ��״̬
	int m_count = 0;
	//private CustomImageView image = null;
	class DialogThread extends Thread {
		@Override
		public void run() {
			int count = 10;
			while (count-- > 0) {
				try {
					
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if(m_pDialog.isShowing())
			{
				m_pDialog.cancel();
				Looper.prepare();     
				UIHealper.DisplayToast(appContext,"��½��ʱ��");
				Looper.loop(); 	
			}

		}
	}
	DialogThread startThread;
	
	public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        startThread = new DialogThread();
        appContext = (AppContext) getApplication();
        //�ر��Զ����������뷨
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.login_activity);
        
        IntentFilter filter = new IntentFilter();
		filter.addAction(WebClient.INTERNAL_ACTION_FINDBYLOGIN);
		appContext.registerReceiver(receiver, filter);
		
		TextView setting_button = (TextView)findViewById(R.id.login_setting_button);
		
		//���絥��ѡ���
		m_RadioGroupNetSelect = (RadioGroup) findViewById(R.id.RadioGroupNetSelect);  
		m_RadioButtonInwardNet = (RadioButton) findViewById(R.id.RadioButtonInwardNet);  
		m_RadioButtonExternalNet = (RadioButton) findViewById(R.id.RadioButtonExternalNet); 
		
		InitRadioButtonStatus();
		m_RadioGroupNetSelect.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if(checkedId == m_RadioButtonInwardNet.getId()){
					WebClient.mCurrentServerIP = appContext.InwardServerIP;
					mRadioGroupStatus = InwardServerIP;
				}
				else{
					WebClient.mCurrentServerIP = appContext.ExternalServerIP;
					mRadioGroupStatus = ExternalServerIP;
				}
				SharedPreferences sharedPreferences = appContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
				Editor editor = sharedPreferences.edit();
				//����Ҫ���������
				editor.putString("RadioNetStatus",mRadioGroupStatus);
				//ȷ�ϱ���
				editor.commit();
			}
			
		});
		
		setting_button.setOnClickListener(new Button.OnClickListener()
        {
        	public void onClick(View v)
        	{ 
        		Intent intent = new Intent();
				/* ָ��intentҪ�������� */
        		intent.setClass(LoginActivity.this, LoginSettingActivity.class);
        		/* ����һ���µ�Activity */
        		startActivityForResult(intent, 1);
               
        	}
        });
        button_land = (Button)findViewById(R.id.button1);
        unameEt=(EditText)findViewById(R.id.username);
        upassEt=(EditText)findViewById(R.id.password);
		SharedPreferences sharedPreferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		//���ļ��л�ȡ���������
		String usernameContent = sharedPreferences.getString("username", "");
		String passwordContent = sharedPreferences.getString("password", "");
		//�ж��Ƿ������ݴ��ڣ���������Ӧ����
		if(usernameContent != null && !"".equals(usernameContent))
			unameEt.setText(usernameContent);
		if(passwordContent != null && !"".equals(passwordContent))
			upassEt.setText(passwordContent);
		

        button_land.setOnClickListener(new Button.OnClickListener()
        {
        	public void onClick(View v)
        	{ 
        		String uname = unameEt.getText().toString();
				String password = upassEt.getText().toString();
				if((uname.equals("")) || (password.equals("")))
				{
					
					UIHealper.DisplayToast(appContext,"�û��������벻��Ϊ�գ�");
				}
				else 
				{
					if(mRadioGroupStatus.equals(ExternalServerIP)){
						WebClient.mCurrentServerIP = appContext.ExternalServerIP;
					}else{
						WebClient.mCurrentServerIP = appContext.InwardServerIP;
					}
					//����ProgressDialog����
					m_pDialog = new ProgressDialog(LoginActivity.this);

					// ���ý�������񣬷��ΪԲ�Σ���ת��
					m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					
					// ����ProgressDialog ��ʾ��Ϣ
					m_pDialog.setMessage("��ǰIP��"+WebClient.mCurrentServerIP+"\n������֤��Ϣ...");
					// ����ProgressDialog �Ľ������Ƿ���ȷ
					m_pDialog.setIndeterminate(false);
					
					// ����ProgressDialog �Ƿ���԰��˻ذ���ȡ��
					m_pDialog.setCancelable(true);

					// ��ProgressDialog��ʾ
					m_pDialog.show();
	
					WebClient client = WebClient.getInstance();
					Map<String,String> param = new HashMap<String, String>();
					param.put(uname, password);
					client.sendMessage(appContext, WebClient.Method_findByLogin, param);
				}
        	}   	
        });
    }
	private void InitRadioButtonStatus() {
		// TODO Auto-generated method stub
		SharedPreferences sharedPreferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		//���ļ��л�ȡ���������
		mRadioGroupStatus = sharedPreferences.getString("RadioNetStatus", InwardServerIP);
		if(mRadioGroupStatus.equals(ExternalServerIP)){
			m_RadioButtonExternalNet.setChecked(true);
		}else{
			m_RadioButtonInwardNet.setChecked(true);
		}
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode,  Intent data)  
	{   
		super.onActivityResult(requestCode, resultCode, data);

	}

	
	//�����û���������
	protected void onSaveContent() 
	{
		super.onStop();
		String usernameContent = unameEt.getText().toString();
		String passwordContent = upassEt.getText().toString();
		//��ȡSharedPreferencesʱ����Ҫ����������
		//��һ���Ǳ�����ļ������ƣ��ڶ��������Ǳ����ģʽ���Ƿ�ֻ����Ӧ��ʹ�ã�
		SharedPreferences sharedPreferences = appContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		//����Ҫ���������
		editor.putString("username", usernameContent);
		editor.putString("password", passwordContent);
		//ȷ�ϱ���
		editor.commit();
	}
	//���չ㲥
	public BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub

			String resXml = intent.getStringExtra(WebClient.Param_resXml);
			
			if(intent.getAction().equals(WebClient.INTERNAL_ACTION_FINDBYLOGIN))
			{

				if(resXml != null)
				{
					if(resXml.equals("error"))
					{
						m_pDialog.cancel();
						UIHealper.DisplayToast(appContext,"�û������������");
					}
					else if(resXml.equals("null")){
						m_pDialog.cancel();
						UIHealper.DisplayToast(appContext,"��½ʧ�ܣ����������Ƿ���ͨ��");
					}
					else
					{	
						try
						{
							onSaveContent();
							SAXBuilder builder = new SAXBuilder();
							StringReader sr = new StringReader(resXml);   
							InputSource is = new InputSource(sr); 
							Document Doc = builder.build(is);
							Element rootElement = (Element) Doc.getRootElement();
							
							mUserInfo = UserInfo.getAppManager();
							ConserveUserInfo(rootElement, mUserInfo);
							appContext.UserID = mUserInfo.mUserID;
							SharedPreferences settings = getSharedPreferences(getString(R.string.settings_filename),Context.MODE_PRIVATE);
							Editor editor = settings.edit();
							//����Ҫ���������
							editor.putString(getString(R.string.settings_userID), appContext.UserID);
							//ȷ�ϱ���
							editor.commit();
							m_pDialog.cancel();
							UIHealper.DisplayToast(appContext,"��½�ɹ�");
			        		/* ָ��intentҪ�������� */
			        		intent.setClass(LoginActivity.this, MainActivity.class);
			        		/* ����һ���µ�Activity */
			        		startActivity(intent);
			        		/* �رյ�ǰ��Activity */
			        		LoginActivity.this.finish();
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
	protected void onDestroy() 
	{
		super.onDestroy();
	}
	
	private void ConserveUserInfo(Element rootElement, UserInfo mUserInfo2) 
	{
		Element e = null;
		
		if(rootElement != null && mUserInfo2 != null)
		{
			 if((e =rootElement.getChild("USERID")) != null)
			 {
				 mUserInfo2.setUserID(e.getValue());
			 }  
			 if((e =rootElement.getChild("USERNAME")) != null)
			 {
				 mUserInfo2.setUserName(e.getValue());
			 } 
			 if((e =rootElement.getChild("USERPWD")) != null)
			 {
				 mUserInfo2.setPassWord(e.getValue());
			 } 
			 if((e =rootElement.getChild("EMAIL")) != null)
			 {
				 mUserInfo2.setEmail(e.getValue());
			 } 
			 if((e =rootElement.getChild("ALIAS")) != null)
			 {
				 mUserInfo2.setAlias(e.getValue());
			 } 
			 if((e =rootElement.getChild("PHONECODE")) != null)
			 {
				 mUserInfo2.setPhoneCode(e.getValue());
			 } 
		
		}
		
	}

}