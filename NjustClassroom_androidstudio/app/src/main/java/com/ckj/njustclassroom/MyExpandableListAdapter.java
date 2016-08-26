package com.ckj.njustclassroom;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

//自定义的适配器类,用于封装列表数据
public class MyExpandableListAdapter extends BaseExpandableListAdapter {
	private Context context;

	String[] groups = { "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期天" };
	String[][] children = { { "第一大节", "第二大节", "第三大节", "第四大节", "第五大节" },
			{ "第一大节", "第二大节", "第三大节", "第四大节", "第五大节" },
			{ "第一大节", "第二大节", "第三大节", "第四大节", "第五大节" },
			{ "第一大节", "第二大节", "第三大节", "第四大节", "第五大节" },
			{ "第一大节", "第二大节", "第三大节", "第四大节", "第五大节" },
			{ "第一大节", "第二大节", "第三大节", "第四大节", "第五大节" },
			{ "第一大节", "第二大节", "第三大节", "第四大节", "第五大节" } };

	public MyExpandableListAdapter(Context context) {
		this.context = context;
	}

	// 返回子选项
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return children[groupPosition][childPosition];
	}

	// 返回子选项的id
	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	// 返回子选项的外观
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		TextView textView = getTextView();
		textView.setText(getChild(groupPosition, childPosition).toString());
		return textView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return children[groupPosition].length;
	}

	// 组选项和子选项外观
	private TextView getTextView() {
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT, 64);
		TextView textView = new TextView(context);
		textView.setLayoutParams(lp);
		textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		textView.setPadding(50, 0, 0, 0);// 左上右下
		textView.setTextSize(22);
		textView.setTextColor(Color.BLACK);
		return textView;
	}

	// 返回组选项
	@Override
	public Object getGroup(int groupPosition) {
		return groups[groupPosition];
	}

	@Override
	public int getGroupCount() {
		return groups.length;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	// 返回组选项外观
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		TextView textView = getTextView();
		textView.setText(getGroup(groupPosition).toString());
		return textView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

}
