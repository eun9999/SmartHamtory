package com.example.myapplication.smarthamtory;

import android.Manifest;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FragmentLocation extends Fragment {
    protected static final String TAG = "MonitoringActivity";
    private ConstraintSet applyConstraintSet = new ConstraintSet();

    private Context context;
    private ArrayList<ListViewItem> items;

    TextView location_user, location;
    ImageView imageView;
    double tempRSSI[];
    int newX, newY;
    String user_id, user_pwd;
    private int cnt = 0; //좌표 너무 빨리 바껴서 텀 주기

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //ble 주변 장치 스캔 시작
        BLE_scanner ble_scanner = new BLE_scanner(context, scanCallback, new String[]{"B8:27:EB:A5:63:57", "B8:27:EB:41:45:A5", "B8:27:EB:BE:1E:08", "B8:27:EB:95:9F:A8"});
        ble_scanner.startScan();
    }
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loaction, container, false);
        context = container.getContext();
        items = new ArrayList<>();
        
        //내 위치 나타내는 이미지 색 변경
        imageView = (ImageView)view.findViewById(R.id.location_user);
        int color = ContextCompat.getColor(getActivity(), R.color.red);
        imageView.setColorFilter(color);

        // 로그인 아이디, 비밀번호 받아오기
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            bundle = getArguments();
            user_id = bundle.getString("user_id");
            user_pwd = bundle.getString("user_pwd");
            Log.d("user", user_id);
            Log.d("user", user_pwd);
        }


        return view;
    }

    private final ScanCallback scanCallback = new ScanCallback() {
        // 장치 하나 발견할때마다 호출됨
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            //등록된 공장의 설비만 검색
            for(ListViewItem listViewItem:items){
                if(result.getDevice().equals(listViewItem.getScanResult().getDevice())) {
                    items.set(
                            items.indexOf(listViewItem),
                            new ListViewItem(
                                    result,
                                    listViewItem.getRssi_mean(),
                                    listViewItem.getRssi_cnt(),
                                    listViewItem.getBefore_time()
                            )
                    );    // 정보 수정
                    result = null;
                    break;
                }
            }
            if(result != null){
                items.add(new ListViewItem(result));    //중복 나열 방지
            }
            tempRSSI = new double[]{0,0,0,0};
            for (int i = 0; i < items.size(); i++){
                double rssi = items.get(i).getRssiNew2();
                switch(items.get(i).getName()) {
                    case "프레스":
                        tempRSSI[0] = rssi;
                        break;
                    case "차체조립":
                        tempRSSI[1] = rssi;
                        break;
                    case "의장":
                        tempRSSI[2] = rssi;
                        break;
                    case "도장":
                        tempRSSI[3] = rssi;
                        break;
                }
            }
            Log.d("caserssi", tempRSSI[0]+" "+tempRSSI[1]+" "+tempRSSI[2]+" "+tempRSSI[3]);
            if(cnt == 50){
                ConstraintLayout.LayoutParams newLayoutParams = (ConstraintLayout.LayoutParams) location_user.getLayoutParams();

                double max = tempRSSI[0];
                for(int i = 0; i < tempRSSI.length; i++){
                    if(max < tempRSSI[i])
                        max = tempRSSI[i];
                }
                //일단 6+1개 구역으로 나눔 안 6, 밖 1
                //밖 : Top : 400, X : 180
                //가운데 : Top : 75, X : 180
                //차체에 가까울 때 : Top : 10, X : 110
                //도장에 가까울 때 : Top : 10, X : 250
                //의장에 가까울 때 : Top : 140, X : 250
                //프레스에 가까울 때 : Top : 140, X : 110
                if(max == tempRSSI[0]) {
                    newX = 110;
                    newY = 140;
                }
                else if(max == tempRSSI[1]) {
                    newX = 110;
                    newY = 10;
                }
                else if(max == tempRSSI[2]) {
                    newX = 250;
                    newY = 140;
                }
                else if(max == tempRSSI[3]) {
                    newX = 250;
                    newY = 10;
                }
                else{
                    newX = 180;
                    newY = 75;
                }

                DisplayMetrics dm = getResources().getDisplayMetrics();
                newLayoutParams.editorAbsoluteX = (int) (newX * dm.density); //dp단위로 만들기
                newLayoutParams.topMargin = (int) (newY * dm.density);
                location_user.setLayoutParams(newLayoutParams);

                cnt = 0;
            }
            cnt++;
        }
        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }
    };
}