package com.example.myapplication.smarthamtory;


import android.bluetooth.le.ScanResult;
import android.os.Build;
import androidx.annotation.RequiresApi;

import java.util.Arrays;

public class ListViewItem {
    private final ScanResult scanResult;
    private long before_time = 0;
    private int rssi_sums = 0;
    private int rssi_cnt = 1;
    private double rssi_mean=0;
    private double alpha = 0.3;
    private double returns_value;

    ListViewItem(ScanResult scanResult){
        this.scanResult = scanResult;
    }
    ListViewItem(ScanResult scanResult,double rssi_mean,int rssi_cnt,long before_time){
        this.scanResult = scanResult;
        this.rssi_mean = rssi_mean;
        this.rssi_cnt = rssi_cnt;
        this.before_time = before_time;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getDistance(){
        double rssi = getRssiNew2();
        double txPower = -56;
        double n = 2.0;
        return "" + Math.pow(10, ((double)txPower - rssi) / (10 * n));
    }
    public String getDevice(){
        return scanResult.getDevice().toString();
    }
    public String getRSSI(){
        return ""+scanResult.getRssi();
    }
    @RequiresApi(api = Build.VERSION_CODES.O)

    public double getRssiNew2() {
        long now = System.currentTimeMillis();
        if (now - before_time > 10000) {
            before_time = now;
            returns_value = rssi_mean;
            rssi_cnt = 1;
        } else {
            returns_value = rssi_mean * (1 - alpha);
            rssi_mean = rssi_mean + (scanResult.getRssi() - rssi_mean) / (double) rssi_cnt;
            returns_value += rssi_mean * alpha;
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getName(){
        if (scanResult.getDevice().toString().equals("B8:27:EB:A5:63:57")){
            return "의장";
        }
        if (scanResult.getDevice().toString().equals("B8:27:EB:41:45:A5")){
            return "차체조립";
        }
        if (scanResult.getDevice().toString().equals("B8:27:EB:BE:1E:08")){
            return "프레스";
        }
        if (scanResult.getDevice().toString().equals("B8:27:EB:95:9F:A8")){
            return "도장";
        }
        else
        {return ""+scanResult.getDevice().getName();}
    }
    public ScanResult getScanResult(){
        return scanResult;
    }
    public String getScanRecorder(){
        return Arrays.toString(scanResult.getScanRecord().getBytes());
    }
}
