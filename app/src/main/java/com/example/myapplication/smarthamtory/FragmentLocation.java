package com.example.myapplication.smarthamtory;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

public class FragmentLocation extends Fragment {
    protected static final String TAG = "MonitoringActivity";
    private BeaconManager beaconManager;
    private static final int PERMISSION_REQUEST_FINE_LOCATION = 1;
    private static final int PERMISSION_REQUEST_BACKGROUND_LOCATION = 2;
    private ConstraintLayout constlayout;
    private ConstraintSet applyConstraintSet = new ConstraintSet();
    TextView location_user;
    private String RSSI;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loaction, container, false);

        TextView distance = (TextView)view.findViewById(R.id.textView3);



        // ##### 유저 위치 변경시키기
        location_user = view.findViewById(R.id.location_user);

        ConstraintLayout.LayoutParams newLayoutParams = (ConstraintLayout.LayoutParams) location_user.getLayoutParams();

        newLayoutParams.topMargin = 100;
        newLayoutParams.leftMargin = 200;
        newLayoutParams.rightMargin = 50;
        location_user.setLayoutParams(newLayoutParams);
        // ##### 유저 위치 변경시키기

        return view;
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}