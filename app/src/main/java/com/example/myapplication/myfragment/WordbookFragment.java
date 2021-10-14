package com.example.myapplication.myfragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class WordbookFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_word_book, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (MainActivity.mUser != null) {
            String wordBookFile = MainActivity.mUser.getName();
            Log.d("接口", wordBookFile + " ");
            loadwords();
        }
    }

    private void loadwords() {
        Log.d("测试", "加载单词");
        FileInputStream in = null;
        BufferedReader reader = null;
        try {
            in = getActivity().openFileInput(MainActivity.mUser.getName());
            reader = new BufferedReader(new InputStreamReader(in));
            LinearLayout wordArea = getActivity().findViewById(R.id.word_area);
            String line;
            while ((line = reader.readLine()) != null) {
                TextView wordItem = new TextView(getActivity());
                //wordItem.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;   // 有问题
                wordItem.setTextAppearance(getActivity(), R.style.TextAppearance_AppCompat_Large);
                wordItem.setText(line);
                wordArea.addView(wordItem);
            }
            Log.d("读文件", "完成");
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (reader != null) reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
