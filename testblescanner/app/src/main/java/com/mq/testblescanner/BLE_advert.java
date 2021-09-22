package com.mq.testblescanner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.AdvertisingSet;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.nio.ByteBuffer;

public class BLE_advert {
    private final Context context;
    private BluetoothLeAdvertiser advertiser=null;
    private BluetoothAdapter bluetoothAdapter;
    private AdvertiseData.Builder dataBuilder;
    private AdvertiseSettings.Builder settingsBuilder;
    private AdvertiseCallback mAdvertiseCallback;
    private AdvertiseCallback mAdvertisingClientCallback;
    private boolean mStarted;

    public BLE_advert(Context context){
        this.context = context;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bleCheck(bluetoothAdapter);
        if(bluetoothAdapter != null)
            advertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
        else
            Toast.makeText(context, "블루투스를 지원하지 않는 장치입니다.", Toast.LENGTH_SHORT).show();

        mAdvertiseCallback = null;
        mStarted = false;
    }

    // 광고 시작
    public void startAdvertising(ByteBuffer data, AdvertiseCallback callback,int milliTime){
        if(mStarted){
            stopAdvertising();
//            Toast.makeText(context,"이미 광고중",Toast.LENGTH_SHORT).show();
        }
        mAdvertisingClientCallback = callback;
        setData(data);
        advertSetting(milliTime);

        startAdvertising();
    }
    public void startAdvertising(ByteBuffer data, AdvertiseCallback callback,int milliTime,int id){
        if(mStarted){
            stopAdvertising();
//            Toast.makeText(context,"이미 광고중",Toast.LENGTH_SHORT).show();
        }
        mAdvertisingClientCallback = callback;
        setData(data,id);
        advertSetting(milliTime);

        startAdvertising();
    }
    private void startAdvertising(){
        advertiser.startAdvertising(settingsBuilder.build(),dataBuilder.build(),getAdvertiseCallback());
    }
    public void stopAdvertising(){
        mStarted = false;
        advertiser.stopAdvertising(getAdvertiseCallback());
    }
    //id 까지 변경 set data
    private void setData(ByteBuffer data,int id){
        dataBuilder = new AdvertiseData.Builder();
        dataBuilder.setIncludeDeviceName(false);
        if(data == null){
            data = ByteBuffer.allocate(1);
            data.put(0,(byte) 0x0);
        }
        dataBuilder.addManufacturerData(id,data.array());  // id
    }
    // advert data
    private void setData(ByteBuffer data){
        dataBuilder = new AdvertiseData.Builder();
        dataBuilder.setIncludeDeviceName(false);
        if(data == null){
            data = ByteBuffer.allocate(1);
            data.put(0,(byte) 0x0);
        }
        dataBuilder.addManufacturerData(255,data.array());  // id
    }
    // advert setting
    private void advertSetting(int milliTime){
        settingsBuilder = new AdvertiseSettings.Builder();
        settingsBuilder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY);
        settingsBuilder.setConnectable(false);
        settingsBuilder.setTimeout(milliTime);
        //Use the connectable flag if you intend on opening a Gatt Server
        //to allow remote connections to your device.
        settingsBuilder.setConnectable(false);
    }
    //callback
    private AdvertiseCallback getAdvertiseCallback() {
        if (mAdvertiseCallback == null) {
            mAdvertiseCallback = new AdvertiseCallback() {
                @Override
                public void onStartFailure(int errorCode) {
//                    LogManager.e(TAG,"Advertisement start failed, code: %s", errorCode);
                    if (mAdvertisingClientCallback != null) {
                        mAdvertisingClientCallback.onStartFailure(errorCode);
                    }

                }
                @Override
                public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                    mStarted = true;
                    if (mAdvertisingClientCallback != null) {
                        mAdvertisingClientCallback.onStartSuccess(settingsInEffect);
                    }

                }
            };
        }
        return mAdvertiseCallback;
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
