package com.mq.testblescanner;

import android.bluetooth.le.ScanResult;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Arrays;

public class ListViewItem {
    private final ScanResult scanResult;
    ListViewItem(ScanResult scanResult){
        this.scanResult = scanResult;
    }
    public String getDevice(){
        return scanResult.getDevice().toString();
    }
    public String getRSSI(){
        return ""+scanResult.getRssi();
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getDistance(){
        double rssi = scanResult.getRssi();
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
        if (scanResult.getDevice().toString().equals("B8:27:EB:A5:63:57")){
            return "의장";
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
