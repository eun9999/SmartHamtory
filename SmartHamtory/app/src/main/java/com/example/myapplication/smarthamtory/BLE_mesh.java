package com.example.myapplication.smarthamtory;

import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class BLE_mesh {
    private Context context;
    private BLE_advert ble_advert;
    private BLE_scanner ble_scanner;
    private Random random;
    private HashMap<String,String> rpi_mac_lists;
    private String phone_id;
    private MeshScanCallback meshClientScanCallback = null;
    private ScanCallback scanCallback=null;
    private int dest;   //목적지
    private byte[] random_numbers = {0,0,0,0,0};
    private String random_number_string ="";
    ByteBuffer data;
    public BLE_mesh(Context context, String phone_id, HashMap<String,String> rpi_lists){  // phone_id= "aa:aa:aa:aa" 임시로 할당 받은것 중복 안되게 할당 받음
        this.context = context;
        // 광고
        this.data = ByteBuffer.allocate(27);
        // 0 ~ 6 저장
        // |목적지|경유지|출발지|xx|xx|xx|xx|data
        data.put((byte) 0x00);// 목적지  0
        data.put((byte) 0xff);// 경유지 처음에는 폰에서 전송 해서 ff "고정" 1
        data.put((byte) 0xff);// 출발지 저장 "고정" 2

        this.phone_id = phone_id;
        for(String s : this.phone_id.split(":"))    // 전송한 폰 id 저장 3~6
            data.put((byte) Integer.parseInt(s,16));    // 16 진수로 data 에 저장

        random = new Random(System.currentTimeMillis());// 랜덤 시드 정하기
        
        ble_advert = new BLE_advert(this.context);  // 광고를 위한것
        // 광고 end

        // 스캔
        this.rpi_mac_lists = (HashMap<String, String>) rpi_lists.clone();   // rpi {"id":"mac주소",~~} 복사함
        String[] macs = this.rpi_mac_lists.values().toArray(new String[0]);
        ble_scanner = new BLE_scanner(this.context,getScanCallback(),macs);
        // 스캔 end
    }
//  *********  MSG_send(advert)  **********
    public void MSG_send(int dest,int milliTime){   // milliTime 동안만 광고(라즈베리파이에게 데이터)를 달라고 요청한다.
        // 목적지 저장
        if(dest > 255){
            Toast.makeText(context, "요청 실패 목적지 255이하 0 이상",Toast.LENGTH_SHORT).show();
            return;
        }
        this.dest = dest;
        data.put(0,(byte) dest);//목적지 저장 0~254 까지  4

        //11~15 실제 packet 데이터
        //7 ~ 11 은 데이터
        data.put(7,(byte)0);    //11
        data.put(8,(byte)0);    //12
        data.put(9,(byte)0);    //13
        data.put(10,(byte)0);   //14
        data.put(11,(byte)0);   //15

        //중복 방지 랜덤 추가
        data_add_random();

        ble_advert.startAdvertising(data,null,milliTime,255);
    }
    public void MSG_stop(){
        ble_advert.stopAdvertising();
    }
    private void data_add_random(){
        //16 17 18 19 20 bit -> 실제 방송 packet에서 위치
        //12 13 14 15 16 bit
        random_number_string = "";
        for(int i: new int[]{12, 13, 14, 15}) {
            random_numbers[i-12] = (byte) random.nextInt(255);
            data.put(i, random_numbers[i-12]);
            random_number_string += String.format("%x", random_numbers[i - 12]);
        }
        random_numbers[4] = (byte)(random.nextInt(254)+1);
        data.put(16,random_numbers[4]);
        random_number_string += String.format("%x", random_numbers[4]);
    }
//  *********  MSG_send end  **********

    //  *********  MSG_receive(Scanner) *********
    public void start_ble_scan(MeshScanCallback meshScanCallback){
        this.meshClientScanCallback = meshScanCallback;
        ble_scanner.startScan();
    }
    public void stop_ble_scan(){
        ble_scanner.stopScan();
    }
    private ScanCallback getScanCallback(){
        if(scanCallback == null){
            scanCallback = new ScanCallback() {
                // 장치 하나 발견할때마다 호출됨
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    //|갯수|FF|status id |--        |목적지|경유지|출발지|xx|xx|xx|xx|00|00|00|00|00|r1|r1|r1|r1|r1|r2|r2|r2|r2|r2|00|00|00|00|hop|
                    // 0    1  2          3         4     5     6     7  8  9  10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30
                    byte[] raw_packet = result.getScanRecord().getBytes();   // 수신 받은 byte   (0 ~ 30) => 주의 부호 있음

                    //**------------------------------ 폰 데이터 유효성검사 -----------------------------
                    if(raw_packet[0] != (byte)0x1e) // 갯수 0x1e (30) 인지 체크
                        return;
                    
                    byte status_id = raw_packet[2]; // 0x00 이 아니면 모두 무시
                    if(status_id != (byte)0x00)
                        return;
                    
                    byte destination = raw_packet[4];   // 목적지
                    if(destination != (byte)0xff) // rpi -> 폰 으로 전송 하는 것임으로 0xff(목적지는 폰이라는 의미) 가 아니면 return
                         return;

                    byte source = raw_packet[6];   // 출발지
                    if(rpi_mac_lists.get(String.format("%x",source)) == null)   // 출발지 rpi mac lists 에 존재 안하면 return
                        return;
                    
                    byte waypoint = raw_packet[5];  // 경유지
                    String waypoint_mac = rpi_mac_lists.get(String.format("%x",waypoint));
                    if(waypoint_mac == null)    // 경유지 rpi mac lists 에 존재 안하면 return
                        return;
                    if(!result.getDevice().toString().equals(waypoint_mac)) // 경유지 mac 주소와 현재 탐지된 광고 mac 주소 일치하는지 확인
                        return;

                    String detect_phone_id = String.format("%x:%x:%x:%x",raw_packet[7],raw_packet[8],raw_packet[9],raw_packet[10]);
                    if(!phone_id.equals(detect_phone_id))   // phone id 일치 확인(내가 요청한 데이터의 응답인지 확인)
                        return;

                    String detect_random_number_string = String.format("%x%x%x%x%x",raw_packet[21],raw_packet[22],raw_packet[23],raw_packet[24],raw_packet[25]);
                    if(!random_number_string.equals(detect_random_number_string))   // 저장된 random num 이랑 random2(요청 할때 생성한 랜덤값) 일치안하면 return
                        return;
                    //------------------------------ 폰 데이터 유효성검사 end -----------------------------**/
                    
                    // data 추출
                    byte[] data = Arrays.copyOfRange(raw_packet,11,16);

                    // rpi_mac_lists.get(key) 없으면 null 이다
                    // Log.d("raw_packet_to_str_len",""+raw_packet.length+"  "+ Arrays.toString(data));
                    meshClientScanCallback.onMeshScanResult(callbackType,result,source,data);
                }
                @Override
                public void onScanFailed(int errorCode) {
                    super.onScanFailed(errorCode);
                    Log.d("errorCode", String.valueOf(errorCode));
                }

                @Override
                public void onBatchScanResults(List<ScanResult> results) {
                    super.onBatchScanResults(results);
                    Log.d("results",results.toString());
                }
            };
        }
        return scanCallback;
    }
    //  *********  MSG_receive end **********
}
abstract class MeshScanCallback{    // mesh scan call back 을 위한것
    public abstract void onMeshScanResult(int callbackType, ScanResult result,byte source, byte[] data);   // source 어디에서 온 데이터인지 보여줌
}