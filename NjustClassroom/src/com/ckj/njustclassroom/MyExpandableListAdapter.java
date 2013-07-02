package com.ckj.njustclassroom;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

//�Զ������������,���ڷ�װ�б�����
public class MyExpandableListAdapter extends BaseExpandableListAdapter {
	private Context context;

	String[] groups = { "����һ", "���ڶ�", "������", "������", "������", "������", "������" };
	String[][] children = { { "��һ���", "�ڶ����", "�������", "���Ĵ��", "������" },
			{ "��һ���", "�ڶ����", "�������", "���Ĵ��", "������" },
			{ "��һ���", "�ڶ����", "�������", "���Ĵ��", "������" },
			{ "��һ���", "�ڶ����", "�������", "���Ĵ��", "������" },
			{ "��һ���", "�ڶ����", "�������", "���Ĵ��", "������" },
			{ "��һ���", "�ڶ����", "�������", "���Ĵ��", "������" },
			{ "��һ���", "�ڶ����", "�������", "���Ĵ��", "������" } };

	public MyExpandableListAdapter(Context context) {
		this.context = context;
	}

	// ������ѡ��
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return children[groupPosition][childPosition];
	}

	// ������ѡ���id
	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	// ������ѡ������
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

	// ��ѡ�����ѡ�����
	private TextView getTextView() {
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT, 64);
		TextView textView = new TextView(context);
		textView.setLayoutParams(lp);
		textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		textView.setPadding(50, 0, 0, 0);// ��������
		textView.setTextSize(22);
		textView.setTextColor(Color.BLACK);
		return textView;
	}

	// ������ѡ��
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

	// ������ѡ�����
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
