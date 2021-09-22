package com.mq.testblescanner;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.AdvertisingSet;
import android.bluetooth.le.AdvertisingSetCallback;
import android.bluetooth.le.AdvertisingSetParameters;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ThreadPoolExecutor;

public class MainActivity extends AppCompatActivity {
    private final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
//    private BluetoothAdapter bluetoothAdapter;
//    private BluetoothLeScanner leScanner;
//    private List<ScanFilter> scanFilters;

    private ArrayList<ListViewItem> items;
    private CustomBaseAdapter customBaseAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get permission
        String[] permission_list = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        ActivityCompat.requestPermissions(MainActivity.this, permission_list,  1);

        /*
        //ble 검색을위한것
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bleCheck(bluetoothAdapter);
        leScanner = bluetoothAdapter.getBluetoothLeScanner();
        scanFilters = new ArrayList<>();
         */

        //list 보여줌
        items = new ArrayList<>();
        customBaseAdapter = new CustomBaseAdapter(this,items);
        ListView listView =  findViewById(R.id.listviews);
        listView.setAdapter(customBaseAdapter);

        BLE_scanner ble_scanner = new BLE_scanner(this,scanCallback,new String[]{ "B8:27:EB:41:45:A5","B8:27:EB:C0:11:A5","B8:27:EB:95:9F:A8","B8:27:EB:A5:63:57","B8:27:EB:BE:1E:08"});
//        findViewById(R.id.scan).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                customBaseAdapter.notifyDataSetChanged();
//                ble_scanner.startScan();
//                /*
//                //ble 주변 장치 스캔 시작
//                ScanFilter scanFilter = new ScanFilter.Builder().setDeviceAddress("B8:27:EB:C0:11:A5").build();
////                ScanSettings scanSettings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_POWER).build();
////                ScanSettings scanSettings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_OPPORTUNISTIC).build();
//                ScanSettings scanSettings = new ScanSettings.Builder().setScanMode(
//                        ScanSettings.SCAN_MODE_LOW_POWER).setReportDelay(0).build();
//                ScanSettings settings = new ScanSettings.Builder()
//                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
//                        .setReportDelay(0)
//                        .build();
//
//                scanFilters.add(scanFilter);
//                scanFilters.add(new ScanFilter.Builder().setDeviceAddress("B8:27:EB:95:9F:A8").build());
////                leScanner.startScan(scanCallback);
//                leScanner.startScan(scanFilters,settings,scanCallback);
//                 */
//            }
//        });
//        findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //ble 검색끝 삭제
////                leScanner.stopScan(scanCallback);
//                ble_scanner.stopScan();
//                items.clear();
//                customBaseAdapter.notifyDataSetChanged();
//            }
//        });


        //********************** advertising Test*************************************
        BLE_advert ble_advert = new BLE_advert(this);
        findViewById(R.id.ad_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ByteBuffer ttt = ByteBuffer.allocate(5);
                ttt.put((byte) 0x04);
                ttt.put((byte) 0xFF);
                ttt.put(2,(byte) 0x01);
                ttt.put(3,(byte) 0x02);
                ttt.put(4,(byte) 0x00);
                AdvertiseCallback advertiseCallback=new AdvertiseCallback() {
                    @Override
                    public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                        super.onStartSuccess(settingsInEffect);
                        Toast.makeText(getApplicationContext(), "success"+settingsInEffect.toString()+" : "+ttt.get(0) ,Toast.LENGTH_SHORT).show();
//                    Log.i(TAG, "onStartSuccess: ");
                    }

                    @Override
                    public void onStartFailure(int errorCode) {
                        super.onStartFailure(errorCode);
                        Toast.makeText(getApplicationContext(), "a:"+errorCode ,Toast.LENGTH_SHORT).show();
//                    Log.e(TAG, "onStartFailure: "+errorCode );
                    }
                };
                ble_advert.startAdvertising(ttt,advertiseCallback,5000);
            }
        });
        findViewById(R.id.ad_stop).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                ble_advert.stopAdvertising();
            }
        });
        //******************************************************************************
        //"11":"B8:27:EB:A5:63:57",
        //"22":"B8:27:EB:41:45:A5",
        //"33":"B8:27:EB:BE:1E:08",
        //"45":"B8:27:EB:95:9F:A8",
        //"55":"B8:27:EB:C0:11:A5"



        // ble_mesh start
        HashMap<String,String> rpi_hashMap = new HashMap<>();
        rpi_hashMap.put("11","B8:27:EB:A5:63:57");
        rpi_hashMap.put("22","B8:27:EB:41:45:A5");
        rpi_hashMap.put("33","B8:27:EB:BE:1E:08");
        rpi_hashMap.put("45","B8:27:EB:95:9F:A8");
        rpi_hashMap.put("55","B8:27:EB:C0:11:A5");

//        String[] sss = rpi_hashMap.values().toArray(new String[0]);
//        Log.d("rpi_hashMap",rpi_hashMap.values().toString()+" "+rpi_hashMap.keySet().toString()+"\nkeySet"+ Arrays.toString(sss));
        String phone_id = "aa:aa:aa:aa";
        BLE_mesh ble_mesh = new BLE_mesh(this,phone_id,rpi_hashMap);   // id 는 폰에 부여된 id
        EditText mesh_dest = findViewById(R.id.mesh_dest);
        findViewById(R.id.mesh_start).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try {
                    int iii = Integer.parseInt(mesh_dest.getText().toString(), 16);
                    byte bb = (byte) iii;
                    ble_mesh.MSG_send(iii, 5000); //dest 에게 메세지 보냄
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        });
        findViewById(R.id.scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customBaseAdapter.notifyDataSetChanged();
                ble_mesh.start_ble_scan(meshScanCallback);
            }
        });
        findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ble 검색끝 삭제
                ble_mesh.stop_ble_scan();
                items.clear();
                customBaseAdapter.notifyDataSetChanged();
            }
        });
        // ble_mesh end
    }
    /*
    //advert call back
    private BluetoothLeAdvertiser advert_start(){
        BluetoothLeAdvertiser advertiser = null;
        if (bluetoothAdapter.isMultipleAdvertisementSupported())
        {
            try {
                advertiser = bluetoothAdapter.getBluetoothLeAdvertiser();

                AdvertiseData.Builder dataBuilder = new AdvertiseData.Builder();
                //Define a service UUID according to your needs

//            ParcelUuid.fromString("");
                ByteBuffer ttt = ByteBuffer.allocate(5);
                ttt.put(0,(byte) 0x04);
                ttt.put(1,(byte) 0xFF);
                ttt.put(2,(byte) 0x01);
                ttt.put(3,(byte) 0x02);
                ttt.put(4,(byte) 0x00);

                dataBuilder.addManufacturerData(1,ttt.array());
                dataBuilder.setIncludeDeviceName(false);//*

                AdvertiseSettings.Builder settingsBuilder = new AdvertiseSettings.Builder();
                settingsBuilder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER);
                settingsBuilder.setTimeout(0);
                //Use the connectable flag if you intend on opening a Gatt Server
                //to allow remote connections to your device.
                settingsBuilder.setConnectable(false);

                AdvertiseCallback advertiseCallback=new AdvertiseCallback() {
                    @Override
                    public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                        super.onStartSuccess(settingsInEffect);
                        Toast.makeText(getApplicationContext(), "success"+settingsInEffect.toString() ,Toast.LENGTH_SHORT).show();
//                    Log.i(TAG, "onStartSuccess: ");
                    }

                    @Override
                    public void onStartFailure(int errorCode) {
                        super.onStartFailure(errorCode);
                        Toast.makeText(getApplicationContext(), "a:"+errorCode ,Toast.LENGTH_SHORT).show();
//                    Log.e(TAG, "onStartFailure: "+errorCode );
                    }
                };
                advertiser.startAdvertising(settingsBuilder.build(),dataBuilder.build(),advertiseCallback);
            }catch (Exception e){
                Toast.makeText(this,e.toString(), Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(this,"advertt 실패", Toast.LENGTH_SHORT).show();
        }

        return advertiser;
    }*/

    //mesh scan call back
    private final MeshScanCallback meshScanCallback = new MeshScanCallback() {
        @Override
        public void onMeshScanResult(int callbackType, ScanResult result, byte[] data) {
            Log.d("result_data", Arrays.toString(data));
            processResult(result);
        }
        private void processResult(final ScanResult scanResult){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ScanResult result = scanResult;
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
                    if(result != null) {
                        items.add(new ListViewItem(result));    //중복 나열 방지
                    }
                    customBaseAdapter.notifyDataSetChanged();
                }
            });
        }
    };
    //scan call back
    private final ScanCallback scanCallback = new ScanCallback() {
        // 장치 하나 발견할때마다 호출됨
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
//            if(!result.getDevice().toString().equals("B8:27:EB:C0:11:A5"))
//                return;
            processResult(result);
        }
        private void processResult(final ScanResult scanResult){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ScanResult result = scanResult;
                    for(ListViewItem listViewItem:items){
                        if(result.getDevice().equals(listViewItem.getScanResult().getDevice())) {
//                    double before_rssi = result.getRssi();       //이전 rssi
//                    double after_rssi = listViewItem.getScanResult().getRssi(); //현재 rssi

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
                    if(result != null) {
                        items.add(new ListViewItem(result));    //중복 나열 방지
                    }
                    customBaseAdapter.notifyDataSetChanged();
                }
            });
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
    private void bleCheck(BluetoothAdapter bluetoothAdapter) {
        if (bluetoothAdapter == null) {
            //블루투스를 지원하지 않으면 장치를 끈다
            Toast.makeText(this, "블루투스를 지원하지 않는 장치입니다.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            //연결 안되었을 때
            if (!bluetoothAdapter.isEnabled()) {
                //블루투스 연결
                Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivity(i);
            }
        }
    }
}

// list 에 ble 장치 정보 저장
class CustomBaseAdapter extends BaseAdapter{
    private ArrayList<ListViewItem> arrayList;
    private Context context;
    public CustomBaseAdapter(Context context ,ArrayList<ListViewItem> arrayList){
        this.arrayList = arrayList;
        this.context = context;
    }
    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return arrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("ViewHolder")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(context).inflate(R.layout.listview_item,viewGroup,false);

        ListViewItem listViewItem = (ListViewItem) getItem(i);

        TextView device = view.findViewById(R.id.ble_device);
        TextView name = view.findViewById(R.id.ble_name);
        TextView rssi = view.findViewById(R.id.rssi_value);
        TextView dis = view.findViewById(R.id.calc_distance);
        TextView record = view.findViewById(R.id.ble_record);
        TextView rssi2 = view.findViewById(R.id.rssi_value2);

        device.setText(listViewItem.getDevice());
        name.setText(listViewItem.getName());
        rssi.setText(listViewItem.getRSSI());
        dis.setText(listViewItem.getDistance());
        record.setText(listViewItem.getScanRecorder());
        rssi2.setText(String.format("%s", listViewItem.getRssiNew2()));
        return view;
    }
}