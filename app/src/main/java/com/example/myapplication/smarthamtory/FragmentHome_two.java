package com.example.myapplication.smarthamtory;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;

public class FragmentHome_two extends Fragment {
    TextView data1, data2, data3, data4;
    EditText error_content1, error_content2, error_content3, error_content4;
    CheckBox cb1, cb2, cb3, cb4;
    Button sendErrorBtn;
    String _url;
    String user_id, user_pwd, error_content, equipment_name;
    private Context context;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner leScanner;
    private ArrayList<ListViewItem> items;
    private CustomBaseAdapter customBaseAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ble 검색을위한것
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bleCheck(bluetoothAdapter);
        leScanner = bluetoothAdapter.getBluetoothLeScanner();

        //ble 주변 장치 스캔 시작
        leScanner.startScan(scanCallback);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_two, container, false);
        //ArrayList<ListViewItem> items = ((MainActivity)MainActivity.context_main).items;
        //cb1 = view.findViewById(R.id.checkBox1);    // 설비 1의 체크박스
        context = container.getContext();
        //list 보여줌
        items = new ArrayList<>();
        customBaseAdapter = new CustomBaseAdapter(getActivity().getApplicationContext(),items);
        ListView listView =  view.findViewById(R.id.listviews);
        listView.setAdapter(customBaseAdapter);
        customBaseAdapter.notifyDataSetChanged();

        sendErrorBtn = view.findViewById(R.id.sendError);   // 에러 전송 버튼
        error_content1 = view.findViewById(R.id.error_content1);    // 설비 1의 에러 내용

        data1 = view.findViewById(R.id.machine_data1_2);    // 설비 1의 센서값

        // 로그인 아이디, 비밀번호 받아오기
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            bundle = getArguments();
            user_id = bundle.getString("user_id");
            user_pwd = bundle.getString("user_pwd");
            Log.d("user", user_id);
            Log.d("user", user_pwd);
        }

        // 에러 전송 버튼 클릭
//        sendErrorBtn.setOnClickListener(new View.OnClickListener(){
//            public void onClick(View v){
//                _url = "mqhome.ipdisk.co.kr/apps/errorreceive/";
//
//                if (cb1.isChecked()){
//                    equipment_name = "프레스";
//                    error_content = error_content1.getText().toString();    // 설비 1의 에러 내용
//                    putData(user_id, user_pwd, equipment_name, error_content);
//                    cb1.setChecked(false);
//                }
//                if (cb2.isChecked()){
//                    equipment_name = "차체조립";
//                    error_content = error_content2.getText().toString();    // 설비 2의 에러 내용
//                    putData(user_id, user_pwd, equipment_name, error_content);
//                    cb2.setChecked(false);
//                }
//                if (cb3.isChecked()){
//                    equipment_name = "도장";
//                    error_content = error_content3.getText().toString();    // 설비 3의 에러 내용
//                    putData(user_id, user_pwd, equipment_name, error_content);
//                    cb3.setChecked(false);
//                }
//                if (cb4.isChecked()){
//                    equipment_name = "의장";
//                    error_content = error_content4.getText().toString();    // 설비 4의 에러 내용
//                    putData(user_id, user_pwd, equipment_name, error_content);
//                    cb4.setChecked(false);
//                }
//
//
//            }
//        });

        return view;
    }

    public class NetworkTest extends AsyncTask<Void,Void,String> {
        String url;
        ContentValues values;
        String requestMethod;
        NetworkTest(String url, ContentValues contentValues,String requestMethod){
            this.url = url;
            this.values = contentValues;
            this.requestMethod = requestMethod;
        }
        @Override
        protected String doInBackground(Void... voids) {
            String result;
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url,values,requestMethod);
            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result.equals("성공")){
                Toast.makeText(getActivity(),"전송 완료", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 에러 내용을 전송하는 기능
    public void putData(String user_id, String user_pwd, String equipment_name, String error_content){
        ContentValues contentValues = new ContentValues();
        contentValues.put("user_id", user_id);
        contentValues.put("user_pwd", user_pwd);
        contentValues.put("equipment_name", equipment_name);
        contentValues.put("error_content", error_content);

        NetworkTest networkTest = new NetworkTest("http://" + _url, contentValues,"POST");
        networkTest.execute();

    }
    private final ScanCallback scanCallback = new ScanCallback() {
        // 장치 하나 발견할때마다 호출됨
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if(result.getDevice().toString().equals("B8:27:EB:C0:11:A5") ||
                    result.getDevice().toString().equals("B8:27:EB:A5:63:57") ||
                    result.getDevice().toString().equals("B8:27:EB:41:45:A5") ||
                    result.getDevice().toString().equals("B8:27:EB:BE:1E:08") ){
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
            //Toast.makeText(this, "블루투스를 지원하지 않는 장치입니다.", Toast.LENGTH_SHORT).show();
            Toast.makeText(context, "블루투스를 지원하지 않는 장치입니다.", Toast.LENGTH_SHORT).show();
            //아래 3줄 finish() 대신하는거
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().remove(this).commit();
            fragmentManager.popBackStack();
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
class CustomBaseAdapter extends BaseAdapter {
    private ArrayList<ListViewItem> arrayList;
    public static Context context;
    public CustomBaseAdapter(Context context ,ArrayList<ListViewItem> arrayList){
        this.context = context;
        this.arrayList = arrayList;
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

        TextView mugae = view.findViewById(R.id.machine_data1);
        TextView record = view.findViewById(R.id.machine_data1_2);
        TextView unit = view.findViewById(R.id.machine_data1_3);
        TextView name = view.findViewById(R.id.machine_name2);

        if (listViewItem.getName().equals("프레스")){
            name.setText("프레스");
            mugae.setText("거리 : ");
            String str = listViewItem.getScanRecorder().substring(8,14);

            int idx = str.indexOf(",");
            String tempdata1 = str.substring(0,idx);
            String tempdata2 = str.substring(idx+1);
            tempdata2 = tempdata2.replace(",", "");
            if (tempdata1.length()<2 || tempdata2.length()<2) {
                if(tempdata1.length()<2)
                    tempdata1 = "0" + tempdata1; //3이면 03이렇게 표현하기위해
                if(tempdata2.length()<2)
                    tempdata2 = "0" + tempdata2; //3이면 03이렇게 표현하기위해
            }
            record.setText(tempdata1+"."+tempdata2);
            unit.setText("cm");
        }
        else if (listViewItem.getName().equals("차체조립")){
            name.setText("차체조립");
            mugae.setText("실링 온도 : ");
            String str = listViewItem.getScanRecorder().substring(8,10);
            record.setText(str);
            unit.setText("℃");
        }
        else if (listViewItem.getName().equals("의장")){
            name.setText("의장");
            mugae.setText("압력 : ");
            String str = listViewItem.getScanRecorder().substring(8,10);
            record.setText(str);
            unit.setText(" ");
        }
        else if (listViewItem.getName().equals("도장")){
            name.setText("도장");
            mugae.setText("무게 : ");
            String str = listViewItem.getScanRecorder().substring(8,10);
            record.setText(str);
            unit.setText("kg");
        }

        return view;
    }
}