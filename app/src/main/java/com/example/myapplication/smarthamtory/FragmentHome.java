package com.example.myapplication.smarthamtory;

import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class FragmentHome extends Fragment {
    private TextView close_machine_name, close_machine_data, mugae, unit;
    private Context context;
    private ArrayList<ListViewItem> items;
    BLE_mesh ble_mesh;
    BLE_scanner ble_scanner;
    double tempRSSI[];

    int max = -100;
    String closest = "";
    int cnt = 1;
    String data2 = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //ble 주변 장치 스캔 시작
        ble_scanner = new BLE_scanner(context, scanCallback, new String[]{"B8:27:EB:A5:63:57", "B8:27:EB:41:45:A5", "B8:27:EB:BE:1E:08", "B8:27:EB:95:9F:A8"});
        ble_scanner.startScan();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        context = container.getContext();
        items = new ArrayList<>();

        close_machine_name = (TextView)view.findViewById(R.id.close_machine_name_2);
        close_machine_data = (TextView)view.findViewById(R.id.close_machine_data_2);
        mugae = (TextView)view.findViewById(R.id.close_machine_data);
        unit = (TextView) view.findViewById(R.id.unit);


        String phone_id = "aa:aa:aa:aa";
        HashMap<String,String> rpi_hashMap = new HashMap<>();
        rpi_hashMap.put("11","B8:27:EB:A5:63:57");
        rpi_hashMap.put("22","B8:27:EB:41:45:A5");
        rpi_hashMap.put("33","B8:27:EB:BE:1E:08");
        rpi_hashMap.put("45","B8:27:EB:95:9F:A8");
        //        rpi_hashMap.put("55","B8:27:EB:C0:11:A5");

        ble_mesh = new BLE_mesh(context,phone_id,rpi_hashMap);

        return view;

    }
    @Override
    public void onPause() {
        super.onPause();
        Toast.makeText(getContext(),"end",Toast.LENGTH_SHORT).show();
        ble_scanner.stopScan();
    }


    private final ScanCallback scanCallback = new ScanCallback() {
        // 장치 하나 발견할때마다 호출됨
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            cnt++;
            if(cnt % 5 != 0){
                if(result.getRssi() > max){
                    max = result.getRssi();
                    closest = result.getDevice().toString();
                }
                Log.d("QQcnt", String.valueOf(cnt));
                Log.d("QQmax22", String.valueOf(max));
                Log.d("QQclosest", closest);

            }
            else{
                viewSetting(closest);
                ble_scanner.stopScan();
                Log.d("QQble", "blebleble");

                ble_mesh.start_ble_scan(meshScanCallback);  // thread 로 계속 요청
                ble_mesh.MSG_send((byte) Integer.parseInt("33",16), 1000);

            }

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

    private final MeshScanCallback meshScanCallback = new MeshScanCallback() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onMeshScanResult(int callbackType, ScanResult result,byte source, byte[] data) {
            Log.d("mesh", "mesh");
            List<Integer> test = new ArrayList<>();
            for(byte b : data) {
                test.add(Byte.toUnsignedInt(b));
            }
            data2 = Arrays.toString(test.toArray());
            Log.d("mesh", data2);
            close_machine_data.setText(data2);
            ble_mesh.stop_ble_scan();
            ble_scanner.startScan();
        }
    };

    public void viewSetting(String closest){
        String name ="";
        String data1 = ""; //거리, 무게 등
        String data2 = ""; //진짜 데이터
        String tempunit = "";

        name = getName(closest);
        switch(name) {
            case "프레스":
                data1 = "거리 : ";
                tempunit = "cm";
                break;
            case "차체조립":
                data1 = "실링온도 : ";
                tempunit = "℃";
                break;
            case "의장":
                data1 = "압력 : ";
                tempunit = "N";
                break;
            case "도장":
                data1 = "무게 : ";
                tempunit = "kg";
                break;
        }

        close_machine_name.setText(name);
        mugae.setText(data1);
        unit.setText(tempunit);
    }


    public String getName(String device){
        if(device.equals("B8:27:EB:BE:1E:08"))
            return "프레스";
        else if(device.equals("B8:27:EB:41:45:A5"))
            return "차체조립";
        else if(device.equals("B8:27:EB:A5:63:57"))
            return "의장";
        else if(device.equals("B8:27:EB:95:9F:A8"))
            return "도장";
        return "";
    }

}