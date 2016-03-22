package com.greentech.wnd.android;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.greentech.wnd.android.bean.AgriProduct;
import com.greentech.wnd.android.bean.DiseaseFeature;
import com.greentech.wnd.android.bean.DiseasePosition;
import com.greentech.wnd.android.constant.Constant;
import com.greentech.wnd.android.util.CheckedNet;
import com.greentech.wnd.android.util.CustomDialog;
import com.greentech.wnd.android.util.GsonUtil;
import com.greentech.wnd.android.util.NetUtil;

/**
 * 根据构造函数中传入的需要填充的Layout资源Id，将相应的Layout填充到指定的容器中。
 */
public class SelectedDiseaseFragment extends Fragment {
	private Handler mHandler = new Handler();
	private static List<AgriProduct> mAgriProductList;

	private List<DiseasePosition> mDiseasePositionList;
	private List<String> list_type = new ArrayList<String>();

	private int mAgriProductId = 0;
	// 用来判断是否为第一次加载
	public static boolean isFirstLoad = true;
	private ExpandableListView mNameExpandable;// 左边的产品类别expandableListView
	private ExpandableListView mExpandableListView;// 右边的产品部位expandableListView

	private MyExpandableListAdapter mExpandableListAdapter;
	private NameExpandable mNameExpandableAdapter;
	public Map<Integer, Integer> featureId = new HashMap<Integer, Integer>();

	private Button mSubmit;
	private Dialog dialog;
	private String diseaseFeatures;
	private boolean fromWhere;// 判断是不是主界面的按钮调用了这个fragment

	// 记录已选择的产品信息
	private Map<Integer, String> selectKV = new HashMap<Integer, String>();
	private Map<Integer, TextView> selectedTextView = new HashMap<Integer, TextView>();// 记录选中的textView
	private LinkedList<Integer> listInteger = new LinkedList<Integer>();// 记录上次选中的textView的ｉｄ
	// -----------------------------------
	// private ListView listView;
	
	private ProductListAdapter adapter;

	public SelectedDiseaseFragment(boolean fromWhere) {
		super();
		this.fromWhere = fromWhere;
	}

	public static List<AgriProduct> getmAgriProductList() {
		return mAgriProductList;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		isFirstLoad = true;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		final View view = inflater.inflate(R.layout.product_name_list,
				container, false);
		// listView = (ListView) view.findViewById(R.id.listView1);
		mNameExpandable = (ExpandableListView) view
				.findViewById(R.id.expandableListView);
		mExpandableListView = (ExpandableListView) view
				.findViewById(R.id.expandableListView1);
		/* 隐藏默认箭头显示 */
		mNameExpandable.setGroupIndicator(null);
		mExpandableListView.setGroupIndicator(null);
		/* 隐藏选择的黄色高亮 */
		ColorDrawable drawable_tranparent_ = new ColorDrawable(
				Color.TRANSPARENT);
		mExpandableListView.setSelector(drawable_tranparent_);
		adapter = new ProductListAdapter();

		mSubmit = (Button) view.findViewById(R.id.submit);
		dialog = CustomDialog.createLoadingDialog(getActivity(), "加载数据...");
		mSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				diseaseFeatures = getAllDiseaseFeature(featureId);

				if (diseaseFeatures.length() > 0) {

					Intent intent = new Intent(getActivity(),
							DiseaseListActivity.class);
					intent.putExtra("agriProductId", mAgriProductId);
					intent.putExtra("diseaseFeatures", diseaseFeatures);

					startActivity(intent);
//					getActivity().overridePendingTransition(
//							R.anim.fade_in_right, android.R.anim.fade_out);
				} else {
					Toast.makeText(getActivity(), "请选择病害信息", Toast.LENGTH_LONG)
							.show();
				}
			}
		});

		// 加载数据
		if (CheckedNet.isNetworkAviaible(getActivity())) {
//			loadDiseaseFeatures(mAgriProductId);
			dialog.show();
			LoadDataTask load=new LoadDataTask();
			load.execute(mAgriProductId);
			mNameExpandableAdapter = new NameExpandable(getActivity(),
					list_type);
			mNameExpandable.setAdapter(mNameExpandableAdapter);
			
		} else {
			CheckedNet.checkNetwork(getActivity());
		}
		// listview添加点击事件
		// listViewOnItemSelected();
		return view;
	}

	// 遍历featureId，map集合，得到类似于|865|1542|
	public static String getAllDiseaseFeature(Map<Integer, Integer> map) {
		StringBuilder sb = new StringBuilder();

		Set<Integer> key = map.keySet();
		for (Iterator<Integer> it = key.iterator(); it.hasNext();) {
			sb.append("|");
			sb.append(it.next());
			sb.append("|");
		}
		return sb.toString();
	}


	
	public class LoadDataTask extends AsyncTask<Integer, Void, Void>{

		@Override
		protected Void doInBackground(Integer... params) {
			InputStream is = NetUtil.post(Constant.SERVIER_PATH
					+ "/json/findDiseaseFeatures.action",
					"agriProductId=" + params[0]);
			String str = NetUtil.getStringFromInputStream(is);
			JsonObject json = (JsonObject) GsonUtil.parse(str);
			// 首先判断是否为初次加载
			if (isFirstLoad||fromWhere==true) {
				mAgriProductList = GsonUtil.fromJson(
						json.get("agriProductList"),
						new TypeToken<List<AgriProduct>>() {
						}.getType());
				// 第一次加载时将listview 的第一个条目的产品id赋值给mAgriProductId
				mAgriProductId = mAgriProductList.get(0).getId();
				mAgriProductList.get(0).getCategory();
				// 给所有的产品分类
				if (mAgriProductList != null) {
					Constant.setmAgriProductList(mAgriProductList);
					for (AgriProduct pro : mAgriProductList) {
						if (!list_type.contains(pro.getCategory())) {
							list_type.add(pro.getCategory());
						}
					}
				}

			}
			mDiseasePositionList = GsonUtil.fromJson(
					json.get("diseasePositionList"),
					new TypeToken<List<DiseasePosition>>() {
					}.getType());
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					if (isFirstLoad || fromWhere) {
						// listView.setAdapter(adapter);
						// 第一次加载后改变isFirstLoad的值
						isFirstLoad = false;
						fromWhere = false;
						mNameExpandable.expandGroup(0);
						
					}
					dialog.dismiss();

					mExpandableListAdapter = new MyExpandableListAdapter(
							getActivity(), mDiseasePositionList);
					mExpandableListView
							.setAdapter(mExpandableListAdapter);
					mExpandableListView.expandGroup(0);
					
					if (mExpandableListAdapter.getGroupCount() == 0) {
						// mExpandableListView.expandGroup(0);
						Toast.makeText(getActivity(), "数据暂时缺失中", 1)
								.show();

					}
				}
			});
			
			return null;
		}
		
	}
	/**
	 * listView使用的adapter
	 */
	class ProductListAdapter extends BaseAdapter {
		// 记录选中的位置
		private int selectedPosition = 0;

		public void setSelectedPosition(int selectedPosition) {
			this.selectedPosition = selectedPosition;
		}

		@Override
		public int getCount() {

			return mAgriProductList.size();
		}

		@Override
		public Object getItem(int position) {
			return mAgriProductList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView textView = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.productlist, null);

			}

			textView = (TextView) convertView.findViewById(R.id.productName);
			textView.setText(mAgriProductList.get(position).getName());
			// 判断item是否被选中，如果被选中改变颜色
			if (selectedPosition == position) {
				textView.setBackgroundResource(R.drawable.select_listview_background);
			} else {
				textView.setBackgroundColor(Color.TRANSPARENT);
			}
			return convertView;
		}

	}

	class NameExpandable extends BaseExpandableListAdapter {
		Context mContext;
		List<String> mGroup;

		public NameExpandable(Context context, List<String> group) {
			mContext = context;
			mGroup = group;
		}

		@Override
		public int getGroupCount() {
			return mGroup.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			List<String> list_name = new ArrayList<String>();
			if(mAgriProductList!=null){
				for (AgriProduct pro : mAgriProductList) {
					if (pro.getType() == (groupPosition + 1)) {
						list_name.add(pro.getName());
					}
				}
			}
			
			return list_name.size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return mGroup.get(groupPosition);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return null;
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return 0;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			TextView textView;
			RelativeLayout relative;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.select_product_type_item, null);
			}
			textView = (TextView) convertView.findViewById(R.id.text);
			relative=(RelativeLayout) convertView.findViewById(R.id.relative);
			relative.setBackground(getResources().getDrawable(R.drawable.title_color));
			textView.setText(mGroup.get(groupPosition));
			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, final int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			TextView textView;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.expert_product_name_item_select, null);
			}
			textView = (TextView) convertView.findViewById(R.id.text);
			// 给所有的产品分类
			List<String> list_name = new ArrayList<String>();
			final List<Integer> list_id = new ArrayList<Integer>();
			for (AgriProduct pro : mAgriProductList) {
				if (pro.getType() == (groupPosition + 1)) {
					list_name.add(pro.getName());
					list_id.add(pro.getId());
				}
			}
			if(listInteger.size()!=0){
				textView.setSelected(listInteger.getLast()==list_id.get(childPosition));
			}
			
			textView.setText(list_name.get(childPosition));
			textView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Log.i(Constant.TAG,
							String.valueOf(list_id.get(childPosition)));
//					loadDiseaseFeatures(list_id.get(childPosition));
					LoadDataTask load=new LoadDataTask();
					load.execute(list_id.get(childPosition));
					mAgriProductId = list_id.get(childPosition);
					// 清除childView中的选中标记
					selectKV.clear();
					// 清除病虫害的id
					featureId.clear();
					if(listInteger.size()!=0){
						selectedTextView.get(listInteger.getLast())
						.setSelected(false);
					}
					
					selectedTextView.put(list_id.get(childPosition),
							(TextView) v);
					listInteger.addLast(list_id.get(childPosition));

					v.setSelected(true);

				}
			});
			return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

	}

	/**
	 * 简单的ExpandableListView适配器，child中包含文字和选择框
	 * 
	 * @author wlj
	 * @param <T>
	 * 
	 */
	class MyExpandableListAdapter extends BaseExpandableListAdapter {

		Context mContext;
		List<DiseasePosition> mGroup;

		public MyExpandableListAdapter(Context context,
				List<DiseasePosition> group) {
			mContext = context;
			mGroup = group;
		}

		@Override
		public int getGroupCount() {
			return mGroup.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			List<DiseaseFeature> diseaseFeatureList = mGroup.get(groupPosition)
					.getDiseaseFeatureList();
			return diseaseFeatureList == null || diseaseFeatureList.isEmpty() ? 0
					: diseaseFeatureList.size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return null;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return null;
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			TextView text;
			ImageView iv;
			if (convertView == null) {
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.group_item, null);
			}
			/* 判断是否group张开，来分别设置背景图 */
			if (isExpanded) {
				((ImageView) convertView.findViewById(R.id.e)).setImageDrawable(getResources().getDrawable(R.drawable.group_e));
			} else {
				((ImageView) convertView.findViewById(R.id.e)).setImageDrawable(getResources().getDrawable(R.drawable.group));
			}

			text = (TextView) convertView.findViewById(R.id.id_group_txt);
			// setText必须写在这里，否则会产生重复
			text.setText(mGroup.get(groupPosition).getName());
			return convertView;
		}

		@Override
		public View getChildView(final int groupPosition,
				final int childPosition, boolean isLastChild, View convertView,
				ViewGroup parent) {
			CheckedTextView text;

			if (convertView == null) {
				text = getCheckedTextView();

				text.setChecked(false);
			} else {
				text = (CheckedTextView) convertView;
			}
			text.setFocusable(false);
			text.setText(mGroup.get(groupPosition).getDiseaseFeatureList()
					.get(childPosition).getName());
			// 将id设置进text的tag
			text.setTag(mGroup.get(groupPosition).getDiseaseFeatureList()
					.get(childPosition).getId());

			// 判断即将消失的convertView是否被选中，如果被选中就将tag记录到selectKV中。以后再次加载的时候就将其标记为选中状态
			// text.getTag()一定要在text.setTag之后
			text.setChecked(selectKV.containsKey(text.getTag()));

			text.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					boolean flag = ((CheckedTextView) v).isChecked();
					Integer key = (Integer) v.getTag();
					/**
					 * 当点击CheckedTextView的时候获得当前点击的id,
					 * ExpandableListView也有点击事件OnChildClickListener
					 * 但是这个点击事件被CheckedTextView的点击事件覆盖了
					 */

					String value = ((CheckedTextView) v).getText().toString();
					// 思路：使用map来记录选中的id,key是id,value也是id

					if (flag) {
						if (selectKV.containsKey(v.getTag())) {
							selectKV.remove(key);
							featureId.remove(mDiseasePositionList
									.get(groupPosition).getDiseaseFeatureList()
									.get(childPosition).getId());
						}
					} else {
						selectKV.put(key, value);
						featureId.put(
								mDiseasePositionList.get(groupPosition)
										.getDiseaseFeatureList()
										.get(childPosition).getId(),
								mDiseasePositionList.get(groupPosition)
										.getDiseaseFeatureList()
										.get(childPosition).getId());
					}
					((CheckedTextView) v).setChecked(!flag);

				}
			});
			return text;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

		private CheckedTextView getCheckedTextView() {
			AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
					LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);
			CheckedTextView textView = new CheckedTextView(mContext);
			textView.setLayoutParams(lp);
			textView.setGravity(Gravity.CENTER_VERTICAL);
			textView.setPadding(48, 0, 0, 0);
			textView.setTextSize(20);
			textView.setTextColor(Color.BLACK);
			textView.setBackgroundResource(R.drawable.list_item_bgcolor);
			textView.setChecked(true);
			return textView;
		}

		public Map<Integer, String> getSelectKV() {
			return selectKV;
		}
	}
	
	public interface loadData{
		public void onDataLoadListener();
	}

}
