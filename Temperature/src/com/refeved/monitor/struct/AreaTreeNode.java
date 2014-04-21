/*
 * 文件名：AreaTreeNode.java
 * 功能：设备信息的队列
 * 作者：huwei
 * 创建时间：2013-10-25
 * 
 * 
 * 
 * */
package com.refeved.monitor.struct;

import java.util.List;

public class AreaTreeNode extends TreeNode {
	private List<Device> mDevices = null;

	public AreaTreeNode(TreeNode parent, String id, String name,
			boolean isOpen, List<TreeNode> children, List<Device> devices) {
		super(parent, id, name, isOpen, children);
		
		mDevices = devices;
	}

	public List<Device> getmDevices() {
		return mDevices;
	}

	public void setmDevices(List<Device> mDevices) {
		this.mDevices = mDevices;
	}
	
	public void addDevice(Device device){
		this.mDevices.add(device);
	}

}
