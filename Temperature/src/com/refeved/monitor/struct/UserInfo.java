/*
 * 文件名：UserInfo.java
 * 功能：保存用户信息
 * 作者：huwei
 * 创建时间：2013-11-08
 * 
 * 
 * 
 * */
package com.refeved.monitor.struct;


public class UserInfo {
	
	private static UserInfo instance;
	private UserInfo(){}
	
	public static UserInfo getAppManager(){
		if(instance==null){
			instance=new UserInfo();
		}
		return instance;
	}
	
	
	public String mUserID;
	
	public String mUserName;
	
	public String mPassWord;
	
	public String mEmail;
	
	public String mAlias;
	
	public String mPhoneCode;
	
	public String getUserID() {
		return mUserID;
	}

	public void setUserID(String mUserID) {
		this.mUserID = mUserID;
	}

	public String getUserName() {
		return mUserName;
	}

	public void setUserName(String mUserName) {
		this.mUserName = mUserName;
	}

	public String getPassWord() {
		return mPassWord;
	}

	public void setPassWord(String mPassWord) {
		this.mPassWord = mPassWord;
	}

	public String getEmail() {
		return mEmail;
	}

	public void setEmail(String mEmail) {
		this.mEmail = mEmail;
	}

	public String getAlias() {
		return mAlias;
	}

	public void setAlias(String mAlias) {
		this.mAlias = mAlias;
	}
	
	public String getPhoneCode() {
		return mPhoneCode;
	}

	public void setPhoneCode(String mPhoneCode) {
		this.mPhoneCode = mPhoneCode;
	}
	
	public UserInfo(String UserID, String UserName,String PassWord,String Email,String Alias,String PhoneCode)
	{
		mUserID = UserID;
		
		mUserName = UserName;
		
		mPassWord = PassWord;
		
		mEmail = Email;
		
		mAlias = Alias;
		
		mPhoneCode = PhoneCode;
	}

}
