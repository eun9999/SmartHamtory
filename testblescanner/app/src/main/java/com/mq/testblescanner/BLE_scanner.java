package com.mq.testblescanner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class BLE_scanner {
    private final Context context;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner leScanner;

    private List<ScanFilter> scanFilters;
    private ScanSettings settings;
    private ScanCallback scanCallback = null;
    private ScanCallback scanCallbackClient;

    public BLE_scanner(Context context,ScanCallback scanCallbackClient,String[] macs){  //macs 가 null 이면 모든 기기 검색
        this.context = context;
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.scanCallbackClient = scanCallbackClient;
        bleCheck(bluetoothAdapter);
        leScanner = this.bluetoothAdapter.getBluetoothLeScanner();
        scanFilters = new ArrayList<>();
        if(macs != null){
            for(String mac:macs)
                addMacFilters(mac);
        }else{
            scanFilters = null;
        }
    }
    public void startScan(){
        scanSetting();
        leScanner.startScan(scanFilters,settings,getScanCallback());
    }
    public void stopScan(){
        leScanner.stopScan(getScanCallback());
    }

    private void scanSetting(){
        settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setReportDelay(0)
                .build();
    }
    private void addMacFilters(String mac){
        scanFilters.add(new ScanFilter.Builder().setDeviceAddress(mac).build());
    }
    private ScanCallback getScanCallback(){
        if(scanCallback == null){
            scanCallback = new ScanCallback() {
                // 장치 하나 발견할때마다 호출됨
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    scanCallbackClient.onScanResult(callbackType,result);
                }
                @Override
                public void onScanFailed(int errorCode) {
                    super.onScanFailed(errorCode);
                    scanCallbackClient.onScanFailed(errorCode);
                }

                @Override
                public void onBatchScanResults(List<ScanResult> results) {
                    super.onBatchScanResults(results);
                    scanCallbackClient.onBatchScanResults(results);
                }
            };
        }
        return scanCallback;
    }
    // ble 켜기
    private void bleCheck(BluetoothAdapter bluetoothAdapter) {
        if (bluetoothAdapter == null) {
            //블루투스를 지원하지 않으면 장치를 끈다
            Toast.makeText(context, "블루투스를 지원하지 않는 장치입니다.", Toast.LENGTH_SHORT).show();
        } else {
            //연결 안되었을 때
            if (!bluetoothAdapter.isEnabled()) {
                //블루투스 연결
                Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                context.startActivity(i);
            }
        }
    }
}
