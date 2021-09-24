package com.example.myapplication.smarthamtory;

import android.Manifest;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.Toast;

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

    ImageView imageView;
    TextView rssitext;
    double tempRSSI[];
    int newX, newY;
    String user_id, user_pwd;
    private int cnt = 0; //좌표 너무 빨리 바껴서 텀 주기
    private BLE_scanner ble_scanner;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //ble 주변 장치 스캔 시작
        ble_scanner = new BLE_scanner(context, scanCallback, new String[]{"B8:27:EB:A5:63:57", "B8:27:EB:41:45:A5", "B8:27:EB:BE:1E:08", "B8:27:EB:95:9F:A8"});
        ble_scanner.startScan();
    }
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loaction, container, false);
        context = container.getContext();
        items = new ArrayList<>();

        rssitext = view.findViewById(R.id.rssitext);

        imageView = view.findViewById(R.id.location_press);

        //내 위치 나타내는 이미지 색 변경
        imageView = (ImageView)view.findViewById(R.id.location_press);

        return view;
    }


    @Override
    public void onPause() {
        Toast.makeText(getContext(),"end",Toast.LENGTH_SHORT).show();
        ble_scanner.stopScan();
        super.onPause();
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
                                    listViewItem.getBefore_time(),
                                    (byte)0,
                                    new byte[]{}
                            )
                    );    // 정보 수정
                    result = null;
                    break;
                }
            }
            if(result != null){
                items.add(new ListViewItem(result, (byte)0, new byte[]{}));    //중복 나열 방지
            }
            tempRSSI = new double[]{-100,-100,-100,-100};
            for (int i = 0; i < items.size(); i++){
                double rssi = items.get(i).getRssiNew2();
                switch(items.get(i).getName2()) {
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
            rssitext.setText("rssi : " + tempRSSI[0]+" "+tempRSSI[1]+" "+tempRSSI[2]+" "+tempRSSI[3]);
            //Log.d("caserssi", tempRSSI[0]+" "+tempRSSI[1]+" "+tempRSSI[2]+" "+tempRSSI[3]);

            //더 많은 zone 분할을 위해 sort 할 배열 추가 (deep copy)
            double[] sortArray = new double[tempRSSI.length];
            for(int i = 0; i < tempRSSI.length; i++){
                sortArray[i] = tempRSSI[i];
            }
            Arrays.sort(sortArray); //오름차순 sort

            if(cnt == 5){
                ConstraintLayout.LayoutParams newLayoutParams = (ConstraintLayout.LayoutParams) imageView.getLayoutParams();

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
                if(max == tempRSSI[0] && tempRSSI[0] > -75) { //프레스
                    newX = 100;
                    newY = 148;
                }
                else if(max == tempRSSI[1] && tempRSSI[1] > -78) { //차체조립
                    newX = 100;
                    newY = 0;
                }
                else if(max == tempRSSI[2] && tempRSSI[2] > -80) { //의장
                    newX = 220;
                    newY = 148;
                }
                else if(max == tempRSSI[3] && tempRSSI[3] > -75) { //도장
                    newX = 220;
                    newY = 0;
                }
                else if(tempRSSI[1] == -100 && tempRSSI[3] == -100){ //밖
                    newX = 160;
                    newY = 400;
                }
                Log.d("newX ", Integer.toString(newX));
                Log.d("newY ", Integer.toString(newY));

                DisplayMetrics dm = getResources().getDisplayMetrics();
                newLayoutParams.leftMargin = (int) (newX * dm.density); //dp단위로 만들기
                newLayoutParams.topMargin = (int) (newY * dm.density);
                imageView.setLayoutParams(newLayoutParams);
                int color = ContextCompat.getColor(getActivity(), R.color.red);
                imageView.setColorFilter(color);

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