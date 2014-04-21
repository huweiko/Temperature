/*
 * 文件名：TreeNode.java
 * 功能：设备地址树
 * 作者：huwei
 * 创建时间：2013-10-25
 * 
 * 
 * 
 * */
package com.refeved.monitor.struct;

import java.util.List;

public class TreeNode {
	
	private List<TreeNode> mChildren= null;
	
	private TreeNode mParent;
	
	private String mId;
	
	private String mName;
	
	private boolean mIsOpen=true;
	
	public List<TreeNode> getmChildren() {
		return mChildren;
	}

	public void setmChildren(List<TreeNode> mChildren) {
		this.mChildren = mChildren;
	}

	public TreeNode getmParent() {
		return mParent;
	}

	public void setmParent(TreeNode mParent) {
		this.mParent = mParent;
	}

	public String getmId() {
		return mId;
	}

	public void setmId(String mId) {
		this.mId = mId;
	}

	public String getmName() {
		return mName;
	}

	public void setmName(String mName) {
		this.mName = mName;
	}

	public boolean ismIsOpen() {
		return mIsOpen;
	}

	public void setmIsOpen(boolean mIsOpen) {
		this.mIsOpen = mIsOpen;
	}

	public TreeNode(TreeNode parent ,String id , String name , boolean isOpen , List<TreeNode> children){
		mParent = parent;
		mChildren = children;
		mId = id;
		mName = name;
		mIsOpen = isOpen;
	}

}
