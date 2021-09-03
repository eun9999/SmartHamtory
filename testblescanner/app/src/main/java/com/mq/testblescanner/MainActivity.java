package com.mq.testblescanner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner leScanner;

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
        
        //ble 검색을위한것
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bleCheck(bluetoothAdapter);
        leScanner = bluetoothAdapter.getBluetoothLeScanner();



        //list 보여줌
        items = new ArrayList<>();
        customBaseAdapter = new CustomBaseAdapter(this,items);
        ListView listView =  findViewById(R.id.listviews);
        listView.setAdapter(customBaseAdapter);


        findViewById(R.id.scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customBaseAdapter.notifyDataSetChanged();
                //ble 주변 장치 스캔 시작
                leScanner.startScan(scanCallback);
            }
        });
        findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ble 검색끝 삭제
                leScanner.stopScan(scanCallback);
                items.clear();
                customBaseAdapter.notifyDataSetChanged();
            }
        });
    }
    private final ScanCallback scanCallback = new ScanCallback() {
        // 장치 하나 발견할때마다 호출됨
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if(result.getDevice().toString().equals("B8:27:EB:C0:11:A5") || result.getDevice().toString().equals("B8:27:EB:A5:63:57") || result.getDevice().toString().equals("B8:27:EB:BE:1E:08") ){
                Log.d("MAC", "zzz");
                for(ListViewItem listViewItem:items){
                    if(result.getDevice().equals(listViewItem.getScanResult().getDevice())) {
                        items.set(items.indexOf(listViewItem),new ListViewItem(result));
                        result = null;
                        break;
                    }
                }
                if(result != null)
                    items.add(new ListViewItem(result));    //중복 나열 방지
                customBaseAdapter.notifyDataSetChanged();
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

        device.setText(listViewItem.getDevice());
        name.setText(listViewItem.getName());
        rssi.setText(listViewItem.getRSSI());
        dis.setText(listViewItem.getDistance());
        record.setText(listViewItem.getScanRecorder());
        Log.d("test", listViewItem.getScanRecorder());

        return view;
    }
}