package com.refeved.monitor.ui;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.InputSource;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.refeved.monitor.AppContext;
import com.refeved.monitor.R;
import com.refeved.monitor.adapter.DevDealLogListViewAdapter;
import com.refeved.monitor.net.WebClient;
import com.refeved.monitor.struct.DeviceAbnormalLog;
import com.refeved.monitor.struct.DeviceDealLog;
import com.refeved.monitor.util.LogPrint;
import com.refeved.monitor.util.UiHelper;
import com.refeved.monitor.view.LogListView;
import com.refeved.monitor.view.component.pull2refresh.OnRefreshListener;
import com.refeved.monitor.view.component.pull2refresh.VerticalPullToRefreshLayout;

@SuppressLint({ "HandlerLeak", "ResourceAsColor" })
public class LogSectionFragment extends SherlockFragment implements
		OnClickListener, OnScrollListener {
	private AppContext appContext;
	ImageButton mHeaderLogRefresh;
	View mLogFragmentView;
	Button m_log;
	LogListView mLogDealListView;
	Boolean visiable = false;
	ListView mListViewLogs; // 存放两种日志的里列表
	DevDealLogListViewAdapter mDealLogListViewAdapter; // 里面有两种数据类型，切换时，可以随时切换
	int count = 5;
	private List<DeviceAbnormalLog> lvAbnormalLogData = new ArrayList<DeviceAbnormalLog>();
	private List<DeviceDealLog> lvDealLogData = new ArrayList<DeviceDealLog>();
	public static final String ARG_SECTION_NUMBER = "section_number";
	private static final int CLICK_LOGBUTTON = 1; // 日志切换按钮
	private static final int CLICK_LOGREFRESH = 2; // 日志刷新按钮
	private static final int EVERY_PAGE_NUM = 20; // 每页日志个数

	private final static int HANDLER_WEB = 0; // 发送WEB请求
	private final static int HANDLER_REFRESH = 1; // 刷新列表

	RefreshThread mRefreshThread;
	LogDate c_dealDate = new LogDate();
	LogDate c_abnormalDate = new LogDate();
	private boolean mHasMore = false;
	final Semaphore sempDeal = new Semaphore(0);
	final Semaphore sempAbnormal = new Semaphore(0);
	private static int  mInitRefreshStatus = 0;
	/*
	 * 日志信息类 记录当前日志总数、当前页和总页数
	 */
	public class LogDate {
		int pageNO;
		int pageTotal;
		int LogTotal;
	};

	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLER_WEB: {
				Log.d("huwei---"+LogPrint.ML(), "handler web to get data!");
				beforeDataLoad();
				if (appContext.SwitchLog == AppContext.ABNORMAL) {
					onDevLogListupdate(WebClient.Method_getExceptionLogs,
							c_abnormalDate.pageNO);
				} else {
					onDevLogListupdate(WebClient.Method_getMachineHandleLogs,
							c_dealDate.pageNO);
				}

			}
				break;
			case HANDLER_REFRESH: {
				Log.d("huwei---"+LogPrint.ML(), "handler web to refresh data!");
				afterDataLoad(msg.obj);
			}
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	/*
	 * 异常日志的解析方法 功能：把DOM解析的格式转化为数组列表格式
	 */
	public void parseGetByAidXml_Abnormal(Element element,
			List<DeviceAbnormalLog> node) {
		Element e = null;
		if ((e = element.getChild("total")) != null) {
			c_abnormalDate.LogTotal = Integer.parseInt(e.getValue());
			c_abnormalDate.pageTotal = (c_abnormalDate.LogTotal % EVERY_PAGE_NUM) != 0 ? (c_abnormalDate.LogTotal / EVERY_PAGE_NUM) + 1
					: (c_abnormalDate.LogTotal % EVERY_PAGE_NUM);
			element.removeChild("total");
		}
		while ((e = element.getChild("exceptionLog")) != null) {
			String mAcid = e.getChild("maid") != null ? e.getChild("maid")
					.getValue() : "null";
			String mDescription = e.getChild("macDes") != null ? e.getChild(
					"macDes").getValue() : "null";
			String mExceptionType = e.getChild("exceptionDes") != null ? e
					.getChild("exceptionDes").getValue() : "null";
			String mStarttime = e.getChild("starttime") != null ? e.getChild(
					"starttime").getValue() : "null";
			String mEndtime = e.getChild("endtime") != null ? e.getChild(
					"endtime").getValue() : "null";
			DeviceAbnormalLog n = new DeviceAbnormalLog(mAcid, mDescription,
					mExceptionType, mStarttime, mEndtime);
			node.add(n);
			element.removeChild("exceptionLog");
		}
		if ((e = element.getChild("list")) != null) {
			parseGetByAidXml_Abnormal(e, node);
			element.removeChild("list");
		}

	}

	/*
	 * 处理日志的解析方法 功能：把DOM解析的格式转化为数组列表格式
	 */
	public void parseGetByAidXml_Deal(Element element, List<DeviceDealLog> node) {
		Element e = null;
		if ((e = element.getChild("total")) != null) {
			c_dealDate.LogTotal = Integer.parseInt(e.getValue());
			c_dealDate.pageTotal = (c_dealDate.LogTotal % EVERY_PAGE_NUM) != 0 ? (c_dealDate.LogTotal / EVERY_PAGE_NUM) + 1
					: (c_dealDate.LogTotal % EVERY_PAGE_NUM);
			element.removeChild("total");
		}
		while ((e = element.getChild("machine")) != null) {
			String mAcid = e.getChild("macid") != null ? e.getChild("macid")
					.getValue() : "null";
			String mDescription = e.getChild("macdes") != null ? e.getChild(
					"macdes").getValue() : "null";
			String mStarttime = e.getChild("startTime") != null ? e.getChild(
					"startTime").getValue() : "null";
			String mStartUserName = e.getChild("startUserName") != null ? e
					.getChild("startUserName").getValue() : "null";
			String mStartDes = e.getChild("startDes") != null ? e.getChild(
					"startDes").getValue() : "null";
			String mEndUserName = e.getChild("endUserName") != null ? e
					.getChild("endUserName").getValue() : "null";
			String mEndtime = e.getChild("endTime") != null ? e.getChild(
					"endTime").getValue() : "null";
			String mEndDes = e.getChild("endDes") != null ? e
					.getChild("endDes").getValue() : "null";
			DeviceDealLog n = new DeviceDealLog(mAcid, mDescription,
					mStarttime, mStartUserName, mStartDes, mEndUserName,
					mEndtime, mEndDes);
			node.add(n);
			element.removeChild("machine");
		}
		if ((e = element.getChild("list")) != null) {
			parseGetByAidXml_Deal(e, node);
			element.removeChild("list");
		}

	}

	public BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String resXml = intent.getStringExtra(WebClient.Param_resXml);

			// if(resXml == null) return;
			// 判断是否接收到异常日志
			if (intent.getAction().equals(
					WebClient.INTERNAL_ACTION_GETABNORMALLOGS)) {
				// resXml =
				// "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><page><total>1249</total><list><exceptionLog><macid>39</macid><macDes>00124b0003966227</macDes><exceptionDes>温度采集器网络异常</exceptionDes><starttime>2014-01-03 12:08:53</starttime><endtime></endtime><time></time><typeName></typeName></exceptionLog><exceptionLog><macid>57</macid><macDes>00124b00039675aa</macDes><exceptionDes>温度采集器网络异常</exceptionDes><starttime>2014-01-03 12:08:51</starttime><endtime></endtime><time></time><typeName></typeName></exceptionLog><exceptionLog><macid>41</macid><macDes>00124b000396617a</macDes><exceptionDes>温度采集器网络异常</exceptionDes><starttime>2014-01-03 12:08:47</starttime><endtime></endtime><time></time><typeName></typeName></exceptionLog><exceptionLog><macid>7</macid><macDes>冰箱7</macDes><exceptionDes>温度采集器网络异常</exceptionDes><starttime>2014-01-03 11:43:27</starttime><endtime></endtime><time></time><typeName></typeName></exceptionLog><exceptionLog><macid>5</macid><macDes>冰箱5</macDes><exceptionDes>温度采集器网络异常</exceptionDes><starttime>2014-01-03 11:28:49</starttime><endtime></endtime><time></time><typeName></typeName></exceptionLog><exceptionLog><macid>5</macid><macDes>冰箱5</macDes><exceptionDes>设备报警</exceptionDes><starttime>2014-01-03 11:28:20</starttime><endtime></endtime><time></time><typeName></typeName></exceptionLog><exceptionLog><macid>5</macid><macDes>冰箱5</macDes><exceptionDes>设备报警</exceptionDes><starttime>2014-01-03 11:28:02</starttime><endtime>2014-01-03 11:28:18</endtime><time></time><typeName></typeName></exceptionLog><exceptionLog><macid>5</macid><macDes>冰箱5</macDes><exceptionDes>温度采集器网络异常</exceptionDes><starttime>2014-01-03 11:22:27</starttime><endtime>2014-01-03 11:25:20</endtime><time></time><typeName></typeName></exceptionLog><exceptionLog><macid>4</macid><macDes>冰箱4</macDes><exceptionDes>温度采集器网络异常</exceptionDes><starttime>2014-01-03 11:22:26</starttime><endtime></endtime><time></time><typeName></typeName></exceptionLog><exceptionLog><macid>7</macid><macDes>冰箱7</macDes><exceptionDes>温度采集器网络异常</exceptionDes><starttime>2014-01-03 11:21:42</starttime><endtime>2014-01-03 11:24:49</endtime><time></time><typeName></typeName></exceptionLog><exceptionLog><macid>1</macid><macDes>冰箱1</macDes><exceptionDes>温度采集器网络异常</exceptionDes><starttime>2014-01-03 11:18:51</starttime><endtime></endtime><time></time><typeName></typeName></exceptionLog><exceptionLog><macid>1</macid><macDes>冰箱1</macDes><exceptionDes>设备报警</exceptionDes><starttime>2014-01-03 11:18:21</starttime><endtime></endtime><time></time><typeName></typeName></exceptionLog><exceptionLog><macid>1</macid><macDes>冰箱1</macDes><exceptionDes>温度采集器网络异常</exceptionDes><starttime>2014-01-03 11:13:29</starttime><endtime>2014-01-03 11:18:21</endtime><time></time><typeName></typeName></exceptionLog><exceptionLog><macid>4</macid><macDes>冰箱4</macDes><exceptionDes>温度采集器网络异常</exceptionDes><starttime>2014-01-03 11:13:17</starttime><endtime>2014-01-03 11:15:59</endtime><time></time><typeName></typeName></exceptionLog><exceptionLog><macid>5</macid><macDes>冰箱5</macDes><exceptionDes>温度采集器网络异常</exceptionDes><starttime>2014-01-03 11:13:14</starttime><endtime>2014-01-03 11:15:55</endtime><time></time><typeName></typeName></exceptionLog><exceptionLog><macid>40</macid><macDes>00124b0003967596</macDes><exceptionDes>温度采集器网络异常</exceptionDes><starttime>2014-01-03 10:08:44</starttime><endtime></endtime><time></time><typeName></typeName></exceptionLog><exceptionLog><macid>37</macid><macDes>00124b0003966146</macDes><exceptionDes>温度采集器网络异常</exceptionDes><starttime>2014-01-03 09:53:07</starttime><endtime></endtime><time></time><typeName></typeName></exceptionLog><exceptionLog><macid>18</macid><macDes>00124b000396603b</macDes><exceptionDes>温度采集器网络异常</exceptionDes><starttime>2014-01-03 09:47:07</starttime><endtime></endtime><time></time><typeName></typeName></exceptionLog><exceptionLog><macid>55</macid><macDes>00124b00039679a6</macDes><exceptionDes>温度采集器网络异常</exceptionDes><starttime>2014-01-03 09:46:58</starttime><endtime></endtime><time></time><typeName></typeName></exceptionLog><exceptionLog><macid>54</macid><macDes>00124b00039675a2</macDes><exceptionDes>温度采集器网络异常</exceptionDes><starttime>2014-01-03 09:35:19</starttime><endtime></endtime><time></time><typeName></typeName></exceptionLog></list></page>";
				List<DeviceAbnormalLog> list = new ArrayList<DeviceAbnormalLog>();

				if (resXml.equals("error") || resXml.equals("null")) {
					// UIHealper.DisplayToast(appContext,"获取日志信息失败！");
					Log.w("huwei", "abnormal log fail");
					list = null;
				} else {
					try {
						SAXBuilder builder = new SAXBuilder();
						StringReader sr = new StringReader(resXml);
						InputSource is = new InputSource(sr);
						Document Doc = builder.build(is);
						Element rootElement = Doc.getRootElement();
						parseGetByAidXml_Abnormal(rootElement, list);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				Message message = new Message();
				message.what = HANDLER_REFRESH;
				message.obj = list;
				mHandler.sendMessage(message);
			}// 判断是否接收到处理日志
			else if (intent.getAction().equals(
					WebClient.INTERNAL_ACTION_GETDEALLOGS)) {
				// resXml =
				// "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><page><total>15</total><list><machine><macid>50</macid><macdes>00124b00039679a9</macdes><startUserName>test</startUserName><startTime>2014-01-02 16:52:00</startTime><startDes>null</startDes><endUserName></endUserName><endTime></endTime><endDes></endDes></machine><machine><macid>48</macid><macdes>00124b000396613e</macdes><startUserName>test</startUserName><startTime>2014-01-02 16:51:43</startTime><startDes>null</startDes><endUserName></endUserName><endTime></endTime><endDes></endDes></machine><machine><macid>2</macid><macdes>冰箱2</macdes><startUserName>张鑫2</startUserName><startTime>2013-12-23 14:05:02</startTime><startDes></startDes><endUserName>张鑫2</endUserName><endTime>2013-12-23 14:05:29</endTime><endDes></endDes></machine><machine><macid>2</macid><macdes>冰箱2</macdes><startUserName>张鑫2</startUserName><startTime>2013-12-23 13:50:03</startTime><startDes>null</startDes><endUserName>张鑫2</endUserName><endTime>2013-12-23 13:50:13</endTime><endDes>null</endDes></machine><machine><macid>2</macid><macdes>冰箱2</macdes><startUserName>张鑫2</startUserName><startTime>2013-12-23 13:37:37</startTime><startDes>asdsd撒地方</startDes><endUserName>张鑫2</endUserName><endTime>2013-12-23 13:37:58</endTime><endDes>阿斯顿撒</endDes></machine><machine><macid>2</macid><macdes>冰箱2</macdes><startUserName>张鑫2</startUserName><startTime>2013-12-23 13:35:37</startTime><startDes>null</startDes><endUserName>张鑫2</endUserName><endTime>2013-12-23 13:36:48</endTime><endDes>null</endDes></machine><machine><macid>9</macid><macdes>测试1</macdes><startUserName>张鑫2</startUserName><startTime>2013-12-20 10:44:24</startTime><startDes>null</startDes><endUserName>张鑫2</endUserName><endTime>2013-12-20 10:44:30</endTime><endDes>null</endDes></machine><machine><macid>6</macid><macdes>冰箱6</macdes><startUserName>张鑫2</startUserName><startTime>2013-12-19 17:07:34</startTime><startDes>null</startDes><endUserName>张鑫2</endUserName><endTime>2013-12-19 17:07:38</endTime><endDes>null</endDes></machine><machine><macid>6</macid><macdes>冰箱6</macdes><startUserName>张鑫2</startUserName><startTime>2013-12-19 17:07:18</startTime><startDes>null</startDes><endUserName>张鑫2</endUserName><endTime>2013-12-19 17:07:23</endTime><endDes>null</endDes></machine><machine><macid>21</macid><macdes>test1</macdes><startUserName>张鑫2</startUserName><startTime>2013-12-19 10:55:58</startTime><startDes>test2</startDes><endUserName>张鑫2</endUserName><endTime>2013-12-19 10:56:04</endTime><endDes>test2</endDes></machine><machine><macid>20</macid><macdes>测试1</macdes><startUserName>张鑫2</startUserName><startTime>2013-12-19 10:55:40</startTime><startDes>test1</startDes><endUserName>张鑫2</endUserName><endTime>2013-12-19 10:55:50</endTime><endDes>test1</endDes></machine><machine><macid>19</macid><macdes>dabingxiang</macdes><startUserName>张鑫2</startUserName><startTime>2013-12-19 10:54:40</startTime><startDes>test</startDes><endUserName>张鑫2</endUserName><endTime>2013-12-19 10:54:54</endTime><endDes>test</endDes></machine><machine><macid>19</macid><macdes>dabingxiang</macdes><startUserName>张鑫2</startUserName><startTime>2013-12-18 21:33:15</startTime><startDes>null</startDes><endUserName>张鑫2</endUserName><endTime>2013-12-18 21:33:28</endTime><endDes>null</endDes></machine><machine><macid>3</macid><macdes>冰箱3</macdes><startUserName>张鑫2</startUserName><startTime>2013-12-05 17:00:00</startTime><startDes>null</startDes><endUserName>张鑫2</endUserName><endTime>2013-12-05 17:30:01</endTime><endDes>null</endDes></machine><machine><macid>2</macid><macdes>冰箱2</macdes><startUserName>张鑫2</startUserName><startTime>2013-12-05 15:37:51</startTime><startDes>null</startDes><endUserName>张鑫2</endUserName><endTime>2013-12-05 15:37:58</endTime><endDes>null</endDes></machine></list></page>";
				List<DeviceDealLog> list = new ArrayList<DeviceDealLog>();
				if (resXml.equals("error")) {
					// UIHealper.DisplayToast(appContext,"获取日志信息失败！");
					Log.w("huwei", "deal log fail");
					list = null;
				} else {
					try {
						SAXBuilder builder = new SAXBuilder();
						StringReader sr = new StringReader(resXml);
						InputSource is = new InputSource(sr);
						Document Doc = builder.build(is);
						Element rootElement = Doc.getRootElement();
						parseGetByAidXml_Deal(rootElement, list);
						lvDealLogData.addAll(list);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				Message message = new Message();
				message.what = HANDLER_REFRESH;
				message.obj = list;
				mHandler.sendMessage(message);
			}
			// if(mInit == -1 || mInit == 0){
			// mInit++;
			// Message message = new Message();
			// message.what = HANDLER_SETSELECTION;
			// mHandler.sendMessage(message);
			// }
		}
	};
	private boolean mLoading;
	private View mFooterMore;
	private View mFooterNoData;
	private View mFooterNoNet;
	private View loadingText;
	private VerticalPullToRefreshLayout mRefreshLayout;
	private View menu_refresh_loading;

	/*
	 * 自动刷新日志，可以设置刷新间隔时间，例如appContext.SettingsRefreshFrequency值为x，刷新间隔时间就是x秒。
	 */
	public class RefreshThread extends Thread {

		@Override
		public void run() {
			while (appContext.SettingsRefreshFrequency > 0 && visiable) {
				int count = appContext.SettingsRefreshFrequency * 100;
				while (count-- > 0 && visiable) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if (visiable) {
					Message message = new Message();
					message.what = HANDLER_WEB;
					mHandler.sendMessage(message);
				}
			}
		}
	}

	public LogSectionFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		appContext = (AppContext) getSherlockActivity().getApplication();
		c_dealDate.pageNO = 1;
		c_abnormalDate.pageNO = 1;
		IntentFilter filter = new IntentFilter();
		filter.addAction(WebClient.INTERNAL_ACTION_GETABNORMALLOGS);
		filter.addAction(WebClient.INTERNAL_ACTION_GETDEALLOGS);
		appContext.registerReceiver(receiver, filter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mLogFragmentView = inflater.inflate(R.layout.refresh_fragment_log,
				container, false);
		mRefreshLayout = (VerticalPullToRefreshLayout) mLogFragmentView
				.findViewById(R.id.infomation_tab_list);
		// mRefreshLayout.addView(mLogDealListView); // 动态给下拉列表里添加一个listview
		mListViewLogs = (ListView) mLogFragmentView
				.findViewById(R.id.list_exception_log);
		initListView(mListViewLogs, this.getActivity(), 0);
		// initListView(mListViewHandlerLogs, this.getActivity(), 1);

		// 初始化进度条和文本显示
		loadingText = mLogFragmentView.findViewById(R.id.init_tv);
		menu_refresh_loading = mLogFragmentView
				.findViewById(R.id.menu_refresh_loading);

		mFooterMore = getActivity().getLayoutInflater().inflate(
				R.layout.footer_view_more, null);
		mFooterMore.setVisibility(View.GONE);
		// mListViewExceptionLogs.addFooterView(mFooterMore,null,false);

		mFooterNoData = getActivity().getLayoutInflater().inflate(
				R.layout.footer_view_no_data, null);
		mFooterNoData.setVisibility(View.GONE);
		// mListViewExceptionLogs.addFooterView(mFooterNoData,null,false);

		mFooterNoNet = getActivity().getLayoutInflater().inflate(
				R.layout.footer_view_no_net, null);
		mFooterNoNet.setVisibility(View.GONE);

		// ************************************************************************************
		// 下拉刷新动作监听器，第一次界面加载时就会调用
		mRefreshLayout
				.setOnPullDownRefreshListener(new OnRefreshListener.Standard() {
					@Override
					public void onBeforeRefresh() {
						Log.d("huwei---"+LogPrint.ML(),
								"setOnPullDownRefreshListener-->onBeforeRefresh");
						beforeDataLoad();
						mHasMore = true;
					}

					@Override
					public void refreshInBackground() {
						Log.d("huwei---"+LogPrint.ML(),
								"setOnPullDownRefreshListener-->refreshInBackground");
						mInitRefreshStatus = 1;
						if (appContext.SwitchLog == AppContext.ABNORMAL) {
							c_abnormalDate.pageNO = 1;
//							lvAbnormalLogData.clear();
						} else {
							c_dealDate.pageNO = 1;
//							lvDealLogData.clear();
						}
						// 这里第一次获取数据
						mHandler.sendEmptyMessage(HANDLER_WEB);
					}

					@Override
					public void onAfterRefresh() {
						Log.d("huwei---"+LogPrint.ML(),
								"setOnPullDownRefreshListener-->onAfterRefresh");
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// afterDataLoad(null);
					}
				});
		// ************************************************************************************
		return mLogFragmentView;
	}

	private void initListView(ListView mListView, Context context, int idx) {
		Log.i("huwei---"+LogPrint.CML(), "initListView");
		mListView.setFadingEdgeLength(0);
		mListView.setHeaderDividersEnabled(false);
		mListView.setOnScrollListener(this);
		mListView.setCacheColorHint(0);
		mListView.setScrollingCacheEnabled(false);
		mListView.setFooterDividersEnabled(false);
		mHandler.sendEmptyMessage(HANDLER_WEB);
		mDealLogListViewAdapter = new DevDealLogListViewAdapter(context,
				new ArrayList<DeviceDealLog>(),
				new ArrayList<DeviceAbnormalLog>(),
				R.layout.dev_deal_log_listitem);
		mListView.setAdapter(mDealLogListViewAdapter);

	}

	private void beforeDataLoad() {
		mLoading = true;
		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Log.d("huwei", "satart_beforeDataLoad()");
				// TODO Auto-generated method stub
				mFooterNoData.setVisibility(View.GONE);
				// mListView.removeFooterView(mFooterNoData);
				mFooterNoNet.setVisibility(View.GONE);
				// mListView.removeFooterView(mFooterNoNet);
				// mListView.removeFooterView(mFooterMore);
				if (mDealLogListViewAdapter != null
						&& mDealLogListViewAdapter.getCount() >= 10) {
					mListViewLogs.addFooterView(mFooterMore);
					mFooterMore.setVisibility(View.VISIBLE);
				} else {
					mFooterMore.setVisibility(View.GONE);
				}
			}
		});
	}

	/**
	 * @param list
	 *            要显示的数据
	 */
	@SuppressWarnings("unchecked")
	private void afterDataLoad(final Object xLogList) {

		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {

				List<DeviceAbnormalLog> AbnormalList = null;
				List<DeviceDealLog> DealList = null;
				if (appContext.SwitchLog == AppContext.ABNORMAL) {
					AbnormalList = (List<DeviceAbnormalLog>) xLogList;
					mHasMore = (AbnormalList != null)
							&& (AbnormalList.size() > 0);
				} else {
					DealList = (List<DeviceDealLog>) xLogList;
					mHasMore = (DealList != null) && (DealList.size() > 0);
				}

				loadingText.setVisibility(View.GONE);

				mRefreshLayout.setVisibility(View.VISIBLE);
				//
				menu_refresh_loading.setVisibility(View.GONE);
				mRefreshLayout.setVisibility(View.VISIBLE);
				if (!mHasMore) {// 无数据
					Log.i("huwei", "afterDataLoad-->not Data");
					if (mDealLogListViewAdapter != null
							&& mDealLogListViewAdapter.getCount() >= 10) {
						mListViewLogs.removeFooterView(mFooterMore);
					}
					if (appContext.SwitchLog == AppContext.ABNORMAL) {
						c_abnormalDate.pageNO--;
					} else {
						c_dealDate.pageNO--;
					}
					if (UiHelper.showNoNetToast(getActivity())) {// 无网络
						if (mDealLogListViewAdapter != null
								&& mDealLogListViewAdapter.getCount() == 0) {// 无网络，无数据显示引导下拉刷新
							mFooterNoNet.setVisibility(View.VISIBLE);
							// mListView.removeFooterView(mFooterNoNet);
							mListViewLogs.addFooterView(mFooterNoNet);
						}
					} else {// 有网络，无更多数据
						// mListView.removeFooterView(mFooterNoData);
						mListViewLogs.addFooterView(mFooterNoData);
					}
				}
				// 有数据
				else {
					Log.i("huwei", "afterDataLoad-->have Data");
					// mListView.removeFooterView(mFooterMore);
					if(mInitRefreshStatus == 1){
						mInitRefreshStatus = 0;
						//如果是下拉刷新，日志则从头开始显示
						if (appContext.SwitchLog == AppContext.ABNORMAL) {
							lvAbnormalLogData.clear();
						} else {
							lvDealLogData.clear();
						}
					}
					
					if (appContext.SwitchLog == AppContext.ABNORMAL) {
						lvAbnormalLogData.addAll(AbnormalList);
						mDealLogListViewAdapter.setListItems(null,
								lvAbnormalLogData, AppContext.ABNORMAL);
						mDealLogListViewAdapter.notifyDataSetChanged();
					} else {
						lvDealLogData.addAll(DealList);
						mDealLogListViewAdapter.setListItems(lvDealLogData,
								null, AppContext.DEAL);
						mDealLogListViewAdapter.notifyDataSetChanged();
					}

					mListViewLogs.addFooterView(mFooterMore);
				}
				mFooterNoData
						.setVisibility(mHasMore ? View.GONE : View.VISIBLE);
				mFooterMore.setVisibility(mDealLogListViewAdapter != null
						&& mDealLogListViewAdapter.getCount() >= 10 && mHasMore ? View.VISIBLE
						: View.GONE);
				mLoading = false;
			}
		});
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		final ActionBar actionBar = getSherlockActivity().getSupportActionBar();

		m_log = (Button) actionBar.getCustomView().findViewById(
				R.id.main_head_log_switch);
		m_log.setOnClickListener(this);
		m_log.setTag(CLICK_LOGBUTTON);
		if (appContext.SwitchLog == AppContext.ABNORMAL) {
			m_log.setText(R.string.Abnormal_log_Text);
		} else {
			m_log.setText(R.string.Deal_log_Text);
		}
		mHeaderLogRefresh = (ImageButton) actionBar.getCustomView()
				.findViewById(R.id.main_head_log_refresh);
		mHeaderLogRefresh.setOnClickListener(this);
		mHeaderLogRefresh.setTag(CLICK_LOGREFRESH);

	}

	@Override
	public void onClick(View v) {
		int tag = (Integer) v.getTag();

		switch (tag) {
		// 日志类型转换按钮
		case CLICK_LOGBUTTON: {
			if (appContext.SwitchLog == AppContext.ABNORMAL) {
				m_log.setText(R.string.Deal_log_Text);
				appContext.SwitchLog = AppContext.DEAL;
				mDealLogListViewAdapter.setListItems(lvDealLogData, null,
						AppContext.DEAL);
				mDealLogListViewAdapter.notifyDataSetChanged();
			} else {
				m_log.setText(R.string.Abnormal_log_Text);
				appContext.SwitchLog = AppContext.ABNORMAL;
				mDealLogListViewAdapter.setListItems(null, lvAbnormalLogData,
						AppContext.ABNORMAL);
				mDealLogListViewAdapter.notifyDataSetChanged();
			}

			SharedPreferences settings = appContext
					.getSharedPreferences(
							getString(R.string.settings_filename),
							Context.MODE_PRIVATE);
			Editor editor = settings.edit();
			// 添加要保存的数据
			editor.putInt(getString(R.string.settings_log_button_status),
					appContext.SwitchLog);
			// 确认保存
			editor.commit();
			// mListView.addFootView();
		}
			break;
		// 日志刷新按钮（此按钮已被隐藏）
		case CLICK_LOGREFRESH: {
			if (appContext.SwitchLog == AppContext.ABNORMAL) {
				c_abnormalDate.pageNO = 1;
				lvAbnormalLogData.clear();
				onDevLogListupdate(WebClient.Method_getExceptionLogs,
						c_abnormalDate.pageNO);
			} else {
				c_dealDate.pageNO = 1;
				lvDealLogData.clear();
				onDevLogListupdate(WebClient.Method_getMachineHandleLogs,
						c_dealDate.pageNO);
			}
			// mListView.addFootView();
		}

			break;
		default:
			break;
		}
	}

	/*
	 * 原功能：该方法用于告诉用户该Fragment对象的UI是否是对用户可见的 调用条件：每次Fragment的启动和停止都会调用此方法
	 * 重写功能：该方法用于决定是否启用刷新线程
	 */
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);
		// visiable = isVisibleToUser;
		// if (isVisibleToUser == true) {
		// if (mRefreshThread == null) {
		// mRefreshThread = new RefreshThread();
		// try {
		// mRefreshThread.start();
		// } catch (IllegalThreadStateException e) {
		//
		// e.printStackTrace();
		// }
		// }
		// } else {
		// if (mRefreshThread != null) {
		// try {
		// mRefreshThread.join(15);
		// mRefreshThread = null;
		// } catch (InterruptedException e) {
		//
		// e.printStackTrace();
		// }
		//
		// }
		// }
	}

	@Override
	public void onStop() {
		super.onStop();
		visiable = false;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		appContext.unregisterReceiver(receiver);
	}


	private void onDevLogListupdate(String logMethod, int pageNO) {
		WebClient client = WebClient.getInstance();
		Map<String, String> param = new HashMap<String, String>();
		param.put("pageNO", "" + pageNO);
		param.put("pageSIZE", "" + EVERY_PAGE_NUM);
		client.sendMessage(appContext, logMethod, param);
	}


	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		Log.d("tom", "onScrollStateChanged");
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// 加载更多
		if ((totalItemCount - mListViewLogs.getHeaderViewsCount() - mListViewLogs
				.getFooterViewsCount()) > 0 // 总数据还有下一页,继续加载数据
				&& (firstVisibleItem + visibleItemCount + 1 > totalItemCount)
				&& !mLoading) {
			Log.d("huwei---"+LogPrint.ML(), "ready to load data..");
			if (appContext.SwitchLog == AppContext.ABNORMAL) {
				c_abnormalDate.pageNO++;
			} else {
				c_dealDate.pageNO++;
			}

			mHandler.sendEmptyMessage(HANDLER_WEB);
		}
	}

}