package com.refeved.monitor.util;

public class LogPrint{
	
	 private static String getMethodName()
	{
		return (new Exception()).getStackTrace()[2].getMethodName();
	}
	
	 private static  String getClassName()
	{
		return (new Exception()).getStackTrace()[2].getClassName();
	}
	
	 private static  int getLineNumber()
	{
		return (new Exception()).getStackTrace()[2].getLineNumber();
	}
	
	public static String CML()
	{
		return "["+getClassName()+"]"+getMethodName()+":"+getLineNumber();
	}
	
	public static String ML()
	{
		return getMethodName()+"():"+getLineNumber();
	}
	
}