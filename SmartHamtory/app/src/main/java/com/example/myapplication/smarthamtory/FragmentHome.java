package com.example.myapplication.smarthamtory;

import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Entity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Trace;
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

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

    private HashMap<String,String> rpi_hashMap2 = new HashMap<>();
    private HashMap<String,Integer> rpi_hashMap3 = new HashMap<>();
    private Thread thread;
    private String device = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        rpi_hashMap.put("55","B8:27:EB:C0:11:A5");


        rpi_hashMap2.put("B8:27:EB:A5:63:57","11");
        rpi_hashMap2.put("B8:27:EB:41:45:A5","22");
        rpi_hashMap2.put("B8:27:EB:BE:1E:08","33");
        rpi_hashMap2.put("B8:27:EB:95:9F:A8","45");
        rpi_hashMap2.put("B8:27:EB:C0:11:A5","55");

//        rpi_hashMap3.put("B8:27:EB:A5:63:57",-4);
//        rpi_hashMap3.put("B8:27:EB:41:45:A5",-1);
//        rpi_hashMap3.put("B8:27:EB:BE:1E:08",-3);
//        rpi_hashMap3.put("B8:27:EB:95:9F:A8",-2);
//        rpi_hashMap3.put("B8:27:EB:C0:11:A5",-5);
//        rpi_hashMap3.put("B8:27:EB:C0:11:A5",-6);
//
////        ble_mesh = new BLE_mesh(context,phone_id,rpi_hashMap);
//
//        List<String> keySetList = new ArrayList<>(rpi_hashMap3.keySet());
//        Collections.sort(keySetList,(o1,o2)->(rpi_hashMap3.get(o1).compareTo(rpi_hashMap3.get(o2))));
//        for(String kk : keySetList) {
//            Log.d("thread_create_view", kk+"  "+rpi_hashMap3.get(kk));
//        }
//        Log.d("thread_create_view",""+keySetList.size());

        ble_mesh = new BLE_mesh(context,phone_id,rpi_hashMap);
        ble_mesh.start_ble_scan(meshScanCallback);
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(!thread.isInterrupted()){
                    Log.d("thread_create_view", String.valueOf(rpi_hashMap3.size()));
                    try {
                        List<String> keySetList = new ArrayList<>(rpi_hashMap3.keySet());
                        Collections.sort(keySetList,(o1,o2)->(rpi_hashMap3.get(o1).compareTo(rpi_hashMap3.get(o2))));
                        if(keySetList.size() > 0) {
                            ble_mesh.MSG_send((byte) Integer.parseInt(rpi_hashMap2.get(keySetList.get(keySetList.size()-1)), 16), 1000);
                            rpi_hashMap3.put(keySetList.get(keySetList.size()-1),-100);
                        }
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        });
        thread.start();

        //ble 주변 장치 스캔 시작
        ble_scanner = new BLE_scanner(context, scanCallback, new String[]{"B8:27:EB:A5:63:57", "B8:27:EB:41:45:A5", "B8:27:EB:BE:1E:08", "B8:27:EB:95:9F:A8","B8:27:EB:C0:11:A5"});
        ble_scanner.startScan();

        Log.d("create_view","ddddddddddddddddddddddddddddddddddd");

        return view;

    }
    @Override
    public void onStop() {
        super.onStop();
        Toast.makeText(getContext(),"end",Toast.LENGTH_SHORT).show();
        ble_scanner.stopScan();
        ble_mesh.stop_ble_scan();
        max = -100;
        cnt = 1;
        thread.interrupt();
        device = null;
    }


    private final ScanCallback scanCallback = new ScanCallback() {
        // 장치 하나 발견할때마다 호출됨
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            rpi_hashMap3.put(result.getDevice().toString(), result.getRssi());
//            Log.d("QQ_ble_sssoncScanf", result.getScanRecord().toString() + " cnt : "+cnt);
//            cnt++;
//            if(cnt % 5 != 0){
//                if(result.getRssi() > max){
//                    max = result.getRssi();
//                    closest = result.getDevice().toString();
//                }
//                Log.d("QQcnt", String.valueOf(cnt));
//                Log.d("QQmax22", String.valueOf(max));
//                Log.d("QQclosest", closest);
//            }
//            else{
//                viewSetting(closest);
////                ble_scanner.stopScan();
//                Log.d("QQble", "blebleble "+cnt);
//                device =  closest;
//            }

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
            viewSetting(result.getDevice().toString());
            close_machine_data.setText(data2);
//            ble_mesh.stop_ble_scan();
//            ble_scanner.startScan();
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
            default:
                data1 = "??? : ";
                tempunit = "???";
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