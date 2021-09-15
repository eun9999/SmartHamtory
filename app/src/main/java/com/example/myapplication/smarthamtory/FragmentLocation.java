package com.example.myapplication.smarthamtory;

import android.Manifest;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FragmentLocation extends Fragment {
    protected static final String TAG = "MonitoringActivity";
    private ConstraintSet applyConstraintSet = new ConstraintSet();

    private Context context;
    private ArrayList<ListViewItem> items;

    TextView location_user, location;
    String tempRSSI[];
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


        // 로그인 아이디, 비밀번호 받아오기
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            bundle = getArguments();
            user_id = bundle.getString("user_id");
            user_pwd = bundle.getString("user_pwd");
            Log.d("user", user_id);
            Log.d("user", user_pwd);
        }

        // ##### 유저 위치 변경시키기
        location_user = view.findViewById(R.id.location_user);
        location = view.findViewById(R.id.location);

        ConstraintLayout.LayoutParams newLayoutParams = (ConstraintLayout.LayoutParams) location_user.getLayoutParams();

        newLayoutParams.leftMargin = 100;
        newLayoutParams.topMargin = 200;

        location_user.setLayoutParams(newLayoutParams);

        // ##### 유저 위치 변경시키기

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
            tempRSSI = new String[]{"", "", "", ""};
            for (int i = 0; i < items.size(); i++){
                String rssi = items.get(i).getDistance();
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
                putData(tempRSSI[0], tempRSSI[1], tempRSSI[2], tempRSSI[3]);
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

    public class NetworkTest extends AsyncTask<Void,Void,String> {
        String url;
        String result;
        ContentValues values;
        String requestMethod;
        NetworkTest(String url, ContentValues contentValues,String requestMethod){
            this.url = url;
            this.values = contentValues;
            this.requestMethod = requestMethod;
        }
        @Override
        protected String doInBackground(Void... voids) {
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url,values,requestMethod);

            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("좌표", result);
            getActivity().runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            location.setText(result);
                        }
                    }
            );
            String[] position;
            position = result.split(", ");
            double positionX, positionY;
            positionX = Double.parseDouble(position[0]);
            positionY = Double.parseDouble(position[1]);

            int newX, newY;
            newX = (int) (positionX * 100);
            newY = (int) ((2 - positionY) * 100);

            if(newX > 230) newX = 230;
            else if(newX < 100) newX = 100;

            if(newY > 230) newY = 230;
            else if (newY < 70) newY = 70;

            ConstraintLayout.LayoutParams newLayoutParams = (ConstraintLayout.LayoutParams) location_user.getLayoutParams();

            newLayoutParams.leftMargin = newX;
            newLayoutParams.topMargin = newY;
            location_user.setLayoutParams(newLayoutParams);

        }
    }

    // RSSI 전송하는 기능
    public void putData(String d1, String d2, String d3, String d4){
        ContentValues contentValues = new ContentValues();

        contentValues.put("user_id", user_id);
        contentValues.put("user_pwd", user_pwd);
        contentValues.put("d1", d1);
        contentValues.put("d2", d2);
        contentValues.put("d3", d3);
        contentValues.put("d4", d4);

        NetworkTest networkTest = new NetworkTest("http://mqhome.ipdisk.co.kr/apps/trilateration_rssi/", contentValues,"POST");
        networkTest.execute();
    }
}