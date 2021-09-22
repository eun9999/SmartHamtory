package com.mq.testblescanner;

import android.bluetooth.le.ScanResult;
import android.icu.text.SymbolTable;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListViewItem {
    private final ScanResult scanResult;

    //new1
    private long before_time = 0;
    private int rssi_sums = 0;
    private int rssi_cnt = 1;
    private double rssi_mean=0;
    private double alpha = 0.3;
    //

    //new2
    //
//    private double before_rssi = 0;
//    private int rssi_cnt = 0;
    ListViewItem(ScanResult scanResult){
        this.scanResult = scanResult;
        rssi_cnt = 1;
        rssi_mean = scanResult.getRssi();
        before_time = System.currentTimeMillis();
    }
    ListViewItem(ScanResult scanResult,double rssi_mean,int rssi_cnt,long before_time){
        this.scanResult = scanResult;
        this.rssi_mean = rssi_mean;
        this.rssi_cnt = rssi_cnt;
        this.before_time = before_time;
    }
    public String getDevice(){
        return scanResult.getDevice().toString();
    }
    public String getRSSI(){
        return ""+scanResult.getRssi();
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getDistance(){
//        double rssi = scanResult.getRssi();
        double rssi = getRssiNew2();
//        double a = 0.3; // 계수
//        if(before_rssi == 0){
//            before_rssi = rssi;
//        }
//        rssi = a*rssi +
        double txPower = -56;
        if(rssi == 0)
            return "-1";
        double ratio = rssi / txPower;
        if (ratio < 1.0) {
            return ""+Math.pow(ratio, 10);
        } else {
            double accuracy = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
            return ""+accuracy;
        }
//        return ""+scanResult.getRssi();
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getName(){
        return ""+scanResult.getDevice().getName();
    }
    public ScanResult getScanResult(){
        return scanResult;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getScanRecorder(){
//        scanResult.getScanRecord().getBytes();
//        (int[])(scanResult.getScanRecord().getBytes());
        List<Integer> test = new ArrayList<>();
        for(byte b : scanResult.getScanRecord().getBytes()) {
            test.add(Byte.toUnsignedInt(b));
        }
//        return Arrays.toString((scanResult.getScanRecord().getBytes()));
        return Arrays.toString(test.toArray());
    }
//    public double getRssiNew(){
//        long now = System.currentTimeMillis();
//        long diff = now - before_time;
//        rssi_sums += scanResult.getRssi();
//        rssi_cnt++;
//        if(diff > 500){
//            rssi_mean = (double) rssi_sums/rssi_cnt;d
//            rssi_cnt = 1;
//            rssi_sums = scanResult.getRssi();
//        }
//        before_time = now;
//        return rssi_mean;
//    }
    public double getRssiNew2(){
        long now = System.currentTimeMillis();
        double returns_value;
        if(now - before_time > 5000){
            before_time = now;
            returns_value = rssi_mean;
            rssi_cnt = 1;
        }else {
            returns_value = rssi_mean*(1-alpha);
            rssi_mean = rssi_mean + (scanResult.getRssi() - rssi_mean) / (double) rssi_cnt;
            returns_value += rssi_mean*alpha;
            rssi_cnt++;
        }
        return returns_value;
    }
    public int getRssi_cnt() {
        return rssi_cnt;
    }
    public double getRssi_mean() {
        return rssi_mean;
    }
    public long getBefore_time() {
        return before_time;
    }
}
