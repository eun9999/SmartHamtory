package com.example.myapplication.smarthamtory;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

public class FragmentHome extends Fragment {
    private TextView close_machine_name, close_machine_data;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        close_machine_name = (TextView)view.findViewById(R.id.close_machine_name_2);
        close_machine_data = (TextView)view.findViewById(R.id.close_machine_data_2);

        close_machine_name.setText("이름");
        close_machine_data.setText("데이터");

        return view;

    }



}