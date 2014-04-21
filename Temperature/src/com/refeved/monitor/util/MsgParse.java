package com.refeved.monitor.util;

import com.refeved.monitor.struct.MSGHEAD;

public class MsgParse{
	
	final int XPID = 0xE6;
	final int PROTOCAL_ID_BYTE =1 ;
	final int PACKET_LENGTH_BYTE =4 ;
	final int MESSAGE_ID_BYTE = 4;
	final int SERVER_ID_BYTE = 4;
	final int BUSINESS_CLASS_ID_BYTE =1;
	final int COMMAND_ID_BYTE = 4;
	final int RESPONESE_CODE_BYTE =4;
	final int MSGHEAD_SIZE = 22;
	final int DATASIZE_BYTE = 4;
	
	public MSGHEAD mMsgHead = new MSGHEAD();
	public int mDataSize = 0;
	public byte[] mData = null;
	public byte[] mPack = null;
	
	public MsgParse(){
		
	}
	
	public MsgParse(byte[] data){
		DataParse(data);
	}
	
	public boolean DataParse(byte[] data){
		if(data.length < MSGHEAD_SIZE )
			return false;
		
		mPack = new byte[data.length];
		System.arraycopy(data, 0, mPack, 0,data.length);
		
		byte[] protocalID = new byte[PROTOCAL_ID_BYTE];
		byte[] packetLength = new byte[PACKET_LENGTH_BYTE];
		byte[] messageID = new byte[MESSAGE_ID_BYTE];
		byte[] serverID = new byte[SERVER_ID_BYTE];
		byte[] businessID = new byte[BUSINESS_CLASS_ID_BYTE];
		byte[] commandID = new byte[COMMAND_ID_BYTE];
		byte[] responeCode = new byte[RESPONESE_CODE_BYTE];
		
		int pos = 0 ;
		System.arraycopy(data, pos, protocalID, 0,PROTOCAL_ID_BYTE);
		pos = pos + PROTOCAL_ID_BYTE;
		System.arraycopy( data, pos, packetLength, 0,PACKET_LENGTH_BYTE);
		pos = pos + PACKET_LENGTH_BYTE;
		System.arraycopy( data, pos, messageID, 0,MESSAGE_ID_BYTE);
		pos = pos + MESSAGE_ID_BYTE;
		System.arraycopy( data, pos, serverID, 0, SERVER_ID_BYTE);
		pos = pos + SERVER_ID_BYTE;
		System.arraycopy( data, pos, businessID, 0,BUSINESS_CLASS_ID_BYTE);
		pos = pos + BUSINESS_CLASS_ID_BYTE;
		System.arraycopy(data, pos, commandID, 0, COMMAND_ID_BYTE);
		pos = pos + COMMAND_ID_BYTE;
		System.arraycopy(data, pos, responeCode, 0, RESPONESE_CODE_BYTE);
		pos = pos + RESPONESE_CODE_BYTE;
		
		mMsgHead.PID = ByteConvert.bytesToInt(protocalID);
		mMsgHead.PackLen = ByteConvert.bytesToInt(packetLength);
		mMsgHead.MsgID = ByteConvert.bytesToInt(messageID);
		mMsgHead.SrvID = ByteConvert.bytesToInt(serverID);
		mMsgHead.ClassID = ByteConvert.bytesToInt(businessID);
		mMsgHead.CommandID = ByteConvert.bytesToInt(commandID);
		mMsgHead.ResCode = ByteConvert.bytesToInt(responeCode);
		
		if(data.length > MSGHEAD_SIZE+DATASIZE_BYTE)
		{
			byte[] dataSize = new byte[DATASIZE_BYTE];
			System.arraycopy(data, pos, dataSize, 0, DATASIZE_BYTE);
			mDataSize = ByteConvert.bytesToInt(dataSize);
			pos = pos + DATASIZE_BYTE;
			mData = new byte[ data.length - pos ];
			System.arraycopy(data, pos, mData, 0, data.length - pos);
		}
		
		return true;
	}
	
	public byte[] CreatePack(int msgid,int srvid,int classid,int cmd, int rescod,byte[] data,int datalen){
		mMsgHead.PID = XPID;
		mMsgHead.MsgID = msgid;
		mMsgHead.SrvID = srvid;
		mMsgHead.ClassID = classid;
		mMsgHead.CommandID = cmd;
		mMsgHead.ResCode = rescod;
		if( datalen > 0 )
			mMsgHead.PackLen = MSGHEAD_SIZE+DATASIZE_BYTE+datalen;
		else
			mMsgHead.PackLen = MSGHEAD_SIZE;
		
		mDataSize = datalen;
		mData = data;
		
		byte[] MsgHead = new byte[MSGHEAD_SIZE];
		byte[] protocalID = ByteConvert.intToBytes(mMsgHead.PID);
		byte[] packetLength = ByteConvert.intToBytes(mMsgHead.PackLen);
		byte[] messageID = ByteConvert.intToBytes(mMsgHead.MsgID);
		byte[] serverID = ByteConvert.intToBytes(mMsgHead.SrvID);
		byte[] businessID = ByteConvert.intToBytes(mMsgHead.ClassID);
		byte[] commandID = ByteConvert.intToBytes(mMsgHead.CommandID);
		byte[] responeCode = ByteConvert.intToBytes(mMsgHead.ResCode);
		
		
		byte[] dataSize = ByteConvert.intToBytes(mDataSize);
		
		int pos = 0 ;
		System.arraycopy(protocalID, protocalID.length-PROTOCAL_ID_BYTE, MsgHead, pos, PROTOCAL_ID_BYTE);
		pos = pos + PROTOCAL_ID_BYTE;
		System.arraycopy(packetLength, packetLength.length-PACKET_LENGTH_BYTE, MsgHead, pos, PACKET_LENGTH_BYTE);
		pos = pos + PACKET_LENGTH_BYTE;
		System.arraycopy(messageID, messageID.length-MESSAGE_ID_BYTE, MsgHead, pos, MESSAGE_ID_BYTE);
		pos = pos + MESSAGE_ID_BYTE;
		System.arraycopy(serverID, serverID.length-SERVER_ID_BYTE, MsgHead, pos, SERVER_ID_BYTE);
		pos = pos + SERVER_ID_BYTE;
		System.arraycopy(businessID, businessID.length-BUSINESS_CLASS_ID_BYTE, MsgHead, pos, BUSINESS_CLASS_ID_BYTE);
		pos = pos + BUSINESS_CLASS_ID_BYTE;
		System.arraycopy(commandID, commandID.length-COMMAND_ID_BYTE, MsgHead, pos, COMMAND_ID_BYTE);
		pos = pos + COMMAND_ID_BYTE;
		System.arraycopy(responeCode, responeCode.length-RESPONESE_CODE_BYTE, MsgHead, pos, RESPONESE_CODE_BYTE);
		pos = pos + RESPONESE_CODE_BYTE;
		
		if(datalen > 0)
		{
			byte[] pack = new byte[MSGHEAD_SIZE+DATASIZE_BYTE+datalen];
			int p = 0 ;
			System.arraycopy(MsgHead, 0, pack, p, MSGHEAD_SIZE);
			p = p + MSGHEAD_SIZE;
			System.arraycopy(dataSize, 0, pack, p, DATASIZE_BYTE);
			p = p + DATASIZE_BYTE;
			System.arraycopy(mData, 0, pack, p, mDataSize);
			p = p + mDataSize;
			
			mPack = pack;
		}
		else
		{
			byte[] pack = new byte[MSGHEAD_SIZE];
			System.arraycopy(MsgHead, 0, pack, 0, MSGHEAD_SIZE);
			
			mPack = pack;
		}
		return mPack;
	}
	
}

