package com.refeved.monitor.ui;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.InputSource;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.refeved.monitor.AppContext;
import com.refeved.monitor.R;
import com.refeved.monitor.UIHealper;
import com.refeved.monitor.adapter.DistrictListViewAdapter;
import com.refeved.monitor.net.WebClient;
import com.refeved.monitor.struct.TreeNode;

@SuppressLint("NewApi")
public class DistrictListViewActivity extends BaseActivity implements
		RepeatingImageButton.RepeatListener, View.OnClickListener {
	AppContext appContext;
	View mCustomView;
	private TreeNode rootTreeNode;

	TextView mHeaderTittle;
	ImageButton mHeaderHome;

	ListView mDistrictListView;
	View mDistrictEmptyLoading;
	View mDistrictListEmptyOnClick;
	DistrictListViewAdapter mDistrictListViewAdapter;
	private List<TreeNode> lvTreeNodeData = new ArrayList<TreeNode>();
	
	public void parseGetAreaMapXml(Element element,TreeNode node, String parentLocation){
		   Element e = null;
		   String location = null;
		   if(element.getAttributeValue("des") != null)
		   {
			   location = parentLocation+element.getAttributeValue("des") + " ";
		   }
		   else
		   {
			   location = parentLocation + " ";
		   }
		   
		   while( (e =element.getChild("address")) != null)
		   {
			   String id = e.getAttributeValue("aid");
			   String name = e.getAttributeValue("des");
			   TreeNode n = new TreeNode(node, id, name, false, new ArrayList<TreeNode>());
			   node.getmChildren().add(n);
			   
			   parseGetAreaMapXml(e,n,location);
			   element.removeChild("address");
		   }
	   }
	
	public BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
		
			String resXml = intent.getStringExtra(WebClient.Param_resXml);
			if(intent.getAction().equals(WebClient.INTERNAL_ACTION_GETAREAMAPDONE))
			{
				if(resXml != null)
				{
					if(resXml.equals("error") || resXml.equals("null")){
						mDistrictEmptyLoading.setVisibility(View.GONE);
						if(mDistrictListView.getAdapter().getCount() == 0){
							mDistrictListEmptyOnClick.setVisibility(View.VISIBLE);
						}
						UIHealper.DisplayToast(appContext,"获取地址信息失败！");
					}
					else {
						if((mDistrictEmptyLoading != null) && (mDistrictListEmptyOnClick != null)){
							mDistrictEmptyLoading.setVisibility(View.GONE);
							mDistrictListEmptyOnClick.setVisibility(View.GONE);
						}
						try{
							SAXBuilder builder = new SAXBuilder();
							StringReader sr = new StringReader(resXml);   
							InputSource is = new InputSource(sr); 
							Document Doc = builder.build(is);
							Element rootElement = (Element) Doc.getRootElement();
							rootTreeNode = new TreeNode(null,"root",getString(R.string.district_map_tittle),false, new ArrayList<TreeNode>());
							parseGetAreaMapXml(rootElement, rootTreeNode, "");
							
							lvTreeNodeData.clear();
							lvTreeNodeData.add(rootTreeNode);
							if(lvTreeNodeData.size() == 0){
								mDistrictListEmptyOnClick.setVisibility(View.VISIBLE);
							}
							mDistrictListViewAdapter.notifyDataSetChanged();
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_district_list_view);
		appContext = (AppContext) getApplication();

		mCustomView = getLayoutInflater().inflate(R.layout.custom_actionbar,
				null);

		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setCustomView(mCustomView, new ActionBar.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_gradient_bg));
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);

		mHeaderHome = (ImageButton) mCustomView
				.findViewById(R.id.main_head_home);
		mHeaderHome.setVisibility(View.VISIBLE);
		mHeaderTittle = (TextView) mCustomView
				.findViewById(R.id.main_head_title);
		ImageButton refreshButton =  (ImageButton) mCustomView
				.findViewById(R.id.main_head_refresh);
		refreshButton.setVisibility(View.GONE);
		ImageButton locationButton =  (ImageButton) mCustomView
				.findViewById(R.id.main_head_location);
		locationButton.setVisibility(View.GONE);
		
		//获取intent附加值
		Bundle extras = getIntent().getExtras();
		if(extras != null){
			mHeaderTittle.setText(extras.getInt("TitleName"));	
		}
		rootTreeNode = new TreeNode(null, "root", "Node_0", false,
				new ArrayList<TreeNode>());
		//initTree();
		//lvTreeNodeData.add(rootTreeNode);

		mHeaderHome.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				UIHealper.showHome(DistrictListViewActivity.this);
			}

		});

		mDistrictListViewAdapter = new DistrictListViewAdapter(this,
				lvTreeNodeData);
		mDistrictListView = (ListView) findViewById(R.id.district_listview);
		
		//正在加载布局
		mDistrictEmptyLoading = findViewById(R.id.DistrictListEmpty);
//		mDistrictListView.setEmptyView(mDistrictEmptyLoading);
		//空白列表点击加载
		mDistrictListEmptyOnClick = findViewById(R.id.DistrictListOnClick);
		mDistrictListEmptyOnClick.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mDistrictEmptyLoading.setVisibility(View.VISIBLE);
				mDistrictListEmptyOnClick.setVisibility(View.GONE);
				WebClient client = WebClient.getInstance();
				client.sendMessage(appContext, WebClient.Method_getAddressTree, null);
			}

		});
		
		mDistrictListView.setAdapter(mDistrictListViewAdapter);
		
		IntentFilter filter = new IntentFilter();
	    filter.addAction(WebClient.INTERNAL_ACTION_GETAREAMAPDONE);
	    appContext.registerReceiver(receiver, filter);
	    onDevListUpdate();
//	    int taskID = this.getTaskId();
//		UIHealper.DisplayToast(appContext, "taskID = "+taskID);
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		appContext.unregisterReceiver(receiver);
	}

	@Override
	public void onRepeat(View v, long duration, int repeatcount) {
		// TODO Auto-generated method stub
		if (repeatcount == 0) {
//			String id = v.getTag(R.id.button_tag_index1).toString();
			TreeNode node = (TreeNode) v.getTag();
//			UIHealper.DisplayToast(appContext, " double click = " + id
//					+ node.getmName());

            Intent intent = new Intent();
            intent.putExtra("AID", node.getmId());
            intent.putExtra("mName", node.getmName());
            this.setResult(RESULT_OK, intent);
            this.finish();
		}

		return;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		TreeNode node = (TreeNode) v.getTag();
		if (node.getmChildren() == null)
			return;

		List<TreeNode> list = new ArrayList<TreeNode>();
		for (int pos = 0; pos < lvTreeNodeData.size(); pos++) {
			TreeNode item = lvTreeNodeData.get(pos);
			list.add(item);
			if (node.getmParent() == null) {
				break;
			} else if (node.getmParent().getmId().equals(item.getmId())) {
				break;
			}
		}

		lvTreeNodeData = list;
		lvTreeNodeData.add(node);
		mDistrictListViewAdapter.setListItems(lvTreeNodeData);
		mDistrictListViewAdapter.notifyDataSetChanged();
//		UIHealper.DisplayToast(appContext, node.getmName());

	}
	 //更新设备列表
		private void onDevListUpdate(){
			if((mDistrictEmptyLoading != null) && (mDistrictListEmptyOnClick != null)){
				mDistrictEmptyLoading.setVisibility(View.VISIBLE);
				mDistrictListEmptyOnClick.setVisibility(View.GONE);
			}
			WebClient client = WebClient.getInstance();
			client.sendMessage(appContext, WebClient.Method_getAddressTree, null);
		}
	
}