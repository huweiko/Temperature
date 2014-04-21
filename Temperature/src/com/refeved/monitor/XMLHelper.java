package com.refeved.monitor;

import java.util.ArrayList;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.XMLOutputter;

public class XMLHelper {
	public static final String XMLTITLE_ROOT = "root";
	public static final String XMLTITLE_MACHINE = "machine";
	public static final String XMLTITLE_TEMPERATURE = "temperature";
	public static final String XMLTITLE_HUMIDITY = "humidity";
	public static final String XMLTITLE_STATE = "state";
	
	public static byte[] getTestXmlBytes(){
		Element root = new Element("root"); 
        root.addContent(new Element("filename").setText("test"));
        
        Document Doc = new Document(root);
        
        XMLOutputter XMLOut = new XMLOutputter();
        
        // 输出 user.xml 文件；
         String xmlString = XMLOut.outputString(Doc);
         byte[] xmlMessage = xmlString.getBytes();
         
         return xmlMessage;
	}
	
	public static String getDevStatusXmlString(ArrayList<String> macList ){
		String xmlString = null;
		Element root = new Element(XMLTITLE_ROOT); 
		for(int i = 0 ; i < macList.size() ; i ++){
			root.addContent(new Element(XMLTITLE_MACHINE).setText(macList.get(i)));
		}
        Document Doc = new Document(root); 
        XMLOutputter XMLOut = new XMLOutputter();
        xmlString = XMLOut.outputString(Doc);
        return xmlString;
	}
	
	public static byte[] getIDInDistrictBytes(String districtID){
		Element root = new Element("root"); 
        root.addContent(new Element("address").setText(districtID));
        
        Document Doc = new Document(root);
        
        XMLOutputter XMLOut = new XMLOutputter();
        
        // 输出 user.xml 文件；
         String xmlString = XMLOut.outputString(Doc);
         byte[] xmlMessage = xmlString.getBytes();
         
         return xmlMessage;
	}
	
	public static byte[] getDevStatusBytes(String macID){
		Element root = new Element("root"); 
        root.addContent(new Element("machine").setText(macID));
        
        Document Doc = new Document(root);
        
        XMLOutputter XMLOut = new XMLOutputter();
        
        // 输出 user.xml 文件；
         String xmlString = XMLOut.outputString(Doc);
         byte[] xmlMessage = xmlString.getBytes();
         
         return xmlMessage;
	}
	
	public static byte[] getDevLogBytes(String macID,int num){
		Element root = new Element("root"); 
        root.addContent(new Element("machine").setText(macID));
        root.addContent(new Element("num").setText(String.valueOf(num)));
        
        Document Doc = new Document(root);
        
        XMLOutputter XMLOut = new XMLOutputter();
        
        // 输出 user.xml 文件；
         String xmlString = XMLOut.outputString(Doc);
         byte[] xmlMessage = xmlString.getBytes();
         
         return xmlMessage;
	}
	
	public static byte[] getAllLogBytes(int num){
		Element root = new Element("root"); 
        root.addContent(new Element("num").setText(String.valueOf(num)));
        
        Document Doc = new Document(root);
        
        XMLOutputter XMLOut = new XMLOutputter();
        
        // 输出 user.xml 文件；
         String xmlString = XMLOut.outputString(Doc);
         byte[] xmlMessage = xmlString.getBytes();
         
         return xmlMessage;
	}
	
}
