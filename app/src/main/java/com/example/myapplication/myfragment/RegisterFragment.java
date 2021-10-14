package com.example.myapplication.myfragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.myapplication.Code;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;

public class RegisterFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_register, container, false);
        ImageView imageView = view.findViewById(R.id.iv_registeractivity_showCode);
        imageView.setImageBitmap(Code.getInstance().createBitmap());
        MainActivity.realCode = Code.getInstance().getCode().toLowerCase();
        return view;
    }
}
