package com.nalamala.bardog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.List;

public class FaqFragment extends Fragment {

    ExpandableListView questionList;
    HashMap<String, String> questionsAnswers;
    QuestionsListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_faq, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // initialize list data
        questionList = getView().findViewById(R.id.questions_listview);
        questionsAnswers = new HashMap<>();
        initListData();

        // set adapter
        adapter = new QuestionsListAdapter(getContext(), questionsAnswers, questionList);
        questionList.setAdapter(adapter);
    }

    private void initListData() {
        questionsAnswers.put(getString(R.string.q1), getString(R.string.a1));
        questionsAnswers.put(getString(R.string.q2), getString(R.string.a2));
        questionsAnswers.put(getString(R.string.q3), getString(R.string.a3));
        questionsAnswers.put(getString(R.string.q4), getString(R.string.a4));
        questionsAnswers.put(getString(R.string.q5), getString(R.string.a5));
        questionsAnswers.put(getString(R.string.q6), getString(R.string.a6));
        questionsAnswers.put(getString(R.string.q7), getString(R.string.a7));
        questionsAnswers.put(getString(R.string.q8), getString(R.string.a8));
        questionsAnswers.put(getString(R.string.q9), getString(R.string.a9));
    }
}
