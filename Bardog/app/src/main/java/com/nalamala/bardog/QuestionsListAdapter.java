package com.nalamala.bardog;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class QuestionsListAdapter implements ExpandableListAdapter {

    Context context;
    HashMap<String, String> questionsAnswers;
    LayoutInflater layoutInflater;
    ExpandableListView questionsListView;
    int lastExpandedGroupPosition;

    public QuestionsListAdapter(Context con, HashMap<String, String> qaa, ExpandableListView questionsListView) {
        context = con;
        questionsAnswers = qaa;
        layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.questionsListView = questionsListView;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getGroupCount() {
        return questionsAnswers.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        int i = 0;
        for (String key : questionsAnswers.keySet()) {
            if (i == groupPosition) return key;
            i += 1;
        }
        return "";
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        int i = 0;
        for (String value : questionsAnswers.values()) {
            if (i == groupPosition) return value;
            i += 1;
        }
        return "";
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String group = (String) getGroup(groupPosition);
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_group, null);
        }

        TextView parentTextView = convertView.findViewById(R.id.list_parent);
        parentTextView.setText(group);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String child = (String) getChild(groupPosition, childPosition);
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_item, null);
        }

        TextView parentTextView = convertView.findViewById(R.id.list_item);
        parentTextView.setText(child);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        if(groupPosition != lastExpandedGroupPosition){
            questionsListView.collapseGroup(lastExpandedGroupPosition);
        }

        lastExpandedGroupPosition = groupPosition;
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {

    }

    @Override
    public long getCombinedChildId(long groupId, long childId) {
        return 0;
    }

    @Override
    public long getCombinedGroupId(long groupId) {
        return 0;
    }
}
