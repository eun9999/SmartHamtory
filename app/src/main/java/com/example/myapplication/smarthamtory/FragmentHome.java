package com.example.myapplication.smarthamtory;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentHome extends Fragment {
    private TextView machine_name, machine_data1, machine_data2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home,
                container,
                false);
        machine_name = (TextView)view.findViewById(R.id.machine_name);
        machine_data1 = (TextView)view.findViewById(R.id.machine_data1);
        machine_data2 = (TextView)view.findViewById(R.id.machine_data2);

        return view;

    }

/*    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
*/



}