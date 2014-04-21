package com.refeved.monitor.util;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.XMLOutputter;

import com.refeved.monitor.struct.DeviceScanning;

public class CreatXmlString
{

//    public static void main(String[] args)
//    {
//        MyXmlTest test = new MyXmlTest();
//        System.out.println(test.createStringFromXmlDoc());
//
//    }

    public static String createStringFromXmlDoc(List<DeviceScanning> node)
    {
        Element root = new Element("root");
        Document doc = new Document(root);
        
        // Element injectionReq = new Element("InjectionRequestCommand");

        for (int i = 0; i < node.size(); i++)
        {
            // 创建节点 user;
            Element elements = new Element("machine");
            // 给 user 节点添加属性 id;
//            elements.setAttribute("id", "" + i);
            // 给 user 节点添加子节点并赋值；
            // new Element("name")中的 "name" 替换成表中相应字段，setText("xuehui")中 "xuehui
            // 替换成表中记录值；
            elements.addContent(new Element("des").setText(node.get(i).mDescribe));
            elements.addContent(new Element("aid").setText(node.get(i).mAid));
            elements.addContent(new Element("type").setText(node.get(i).mType));
            elements.addContent(new Element("clow").setText(node.get(i).mClow));
            elements.addContent(new Element("chigh").setText(node.get(i).mChigh));
            elements.addContent(new Element("cdif").setText(node.get(i).mCdif));
            elements.addContent(new Element("sn").setText(node.get(i).mSN));

            // 给父节点list添加user子节点;
            root.addContent(elements);
        }
        ByteArrayOutputStream byteRep = new ByteArrayOutputStream();
        XMLOutputter docWriter = new XMLOutputter();
        // XMLOutputter docWriter2=new XMLOutputter();
        try
        {
            docWriter.output(doc, byteRep);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        String strFromXml = byteRep.toString();
        return strFromXml;
    }

}