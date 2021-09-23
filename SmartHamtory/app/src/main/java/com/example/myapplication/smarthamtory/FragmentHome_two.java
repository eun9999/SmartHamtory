package com.example.myapplication.smarthamtory;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class FragmentHome_two extends Fragment {
    TextView data1;
    EditText error_content1;
    Button sendErrorBtn;
    String _url;
    String user_id, user_pwd, error_content, equipment_name;
    DBHelper helper;
    SQLiteDatabase db;
    List<String> list_temp = new ArrayList<String>();
    String[] equipment_list;
    private Context context;
    private ArrayList<ListViewItem> items;
    private CustomBaseAdapter customBaseAdapter;



    private BLE_mesh ble_mesh;
    private Thread thread;
    private boolean tf = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        helper = new DBHelper(context, "equipDB.db", null, 1);
        db = helper.getWritableDatabase();

        helper.onCreate(db);

        //QR코드 입력으로 내부 DB에 등록된 설비만 볼 수 있도록 필터링
        String sql = "SELECT * FROM equipment;";
        Cursor c = db.rawQuery(sql, null);

        while(c.moveToNext()){
//            System.out.println("name : "+c.getString(c.getColumnIndex("name")));
//            System.out.println("address : "+c.getString(c.getColumnIndex("address")));
            list_temp.add(c.getString(c.getColumnIndex("address")));
        }

        equipment_list = new String[list_temp.size()];
        list_temp.toArray(equipment_list);
        
        //ble 주변 장치 스캔 시작
//        BLE_scanner ble_scanner = new BLE_scanner(context, scanCallback, equipment_list);
//        ble_scanner.startScan();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_two, container, false);
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

        //spinner 사용하기
        final Spinner mSpinner = view.findViewById(R.id.spinner);
        String[] equipment = getResources().getStringArray(R.array.equipment);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, equipment);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);

        // 로그인 아이디, 비밀번호 받아오기
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            bundle = getArguments();
            user_id = bundle.getString("user_id");
            user_pwd = bundle.getString("user_pwd");
            Log.d("user", user_id);
            Log.d("user", user_pwd);
        }

        //에러 전송 버튼 클릭
        sendErrorBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
               //선택된 값 가져오기
                Log.d("spinner", mSpinner.getSelectedItem().toString());

                _url = "mqhome.ipdisk.co.kr/apps/errorreceive/";

                equipment_name = mSpinner.getSelectedItem().toString();
                error_content = error_content1.getText().toString();    // 설비 1의 에러 내용
                putData(user_id, user_pwd, equipment_name, error_content);
                error_content1.setText("");
            }
        });

        String phone_id = "aa:aa:aa:aa";
        HashMap<String,String> rpi_hashMap = new HashMap<>();
        rpi_hashMap.put("11","B8:27:EB:A5:63:57");
        rpi_hashMap.put("22","B8:27:EB:41:45:A5");
        rpi_hashMap.put("33","B8:27:EB:BE:1E:08");
        rpi_hashMap.put("45","B8:27:EB:95:9F:A8");
//        rpi_hashMap.put("55","B8:27:EB:C0:11:A5");


        ble_mesh = new BLE_mesh(context,phone_id,rpi_hashMap);
        ble_mesh.start_ble_scan(meshScanCallback);  // thread 로 계속 요청

        tf = true;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Random random = new Random(System.currentTimeMillis());
                int i = 0;
                int r = 0;
                while (tf) {
                    i = 0;
                    for(String rpi_id:rpi_hashMap.keySet()) {
                        if(i++ == r)
                            continue;
                        ble_mesh.MSG_send((byte) Integer.parseInt(rpi_id,16), 1000);
                        Log.d("dddddd", ""+(byte) Integer.parseInt(rpi_id,16));
                        try {
                            Thread.sleep(900);
                        } catch (InterruptedException e) {
                            Log.d("error", "" + e.toString());
                        }
                        if(!tf)
                            break;
                    }
                    r+=1;
                    if(r%3 == 0)
                        r = 0;
                }
            }
        });
        thread.start();
//        thread.interrupt();
        return view;
    }
    @Override
    public void onPause() {
        super.onPause();
        ble_mesh.stop_ble_scan();
        tf = false;
        thread.interrupt();
        Log.d("dddddd","interrupt");
    }

    private final MeshScanCallback meshScanCallback = new MeshScanCallback() {
        @Override
        public void onMeshScanResult(int callbackType, ScanResult result,byte source, byte[] data) {
            //등록된 공장의 설비만 검색
            for(ListViewItem listViewItem:items){
//                if(result.getDevice().equals(listViewItem.getScanResult().getDevice())) {
                if(source == listViewItem.getSource()){
                    items.set(items.indexOf(listViewItem),new ListViewItem(
                                                        result,
                                                        listViewItem.getRssi_mean(),
                                                        listViewItem.getRssi_cnt(),
                                                        listViewItem.getBefore_time(),
                                                        source,
                                                        data)); //이미 찾았던 설비면 거기다 값 업데이트
                    Log.d("dddddd",Arrays.toString(data));
                    result = null;
                    break;
                }
            }
            if(result != null){
                items.add(new ListViewItem(result,source,data));    //중복 나열 방지
            }
            customBaseAdapter.notifyDataSetChanged();
        }
    };
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

//        mugae.setText(listViewItem.getDevice());
//        record.setText(listViewItem.getData());
//        unit.setText("test");
//        name.setText(listViewItem.getDevice());

        if (listViewItem.getName().equals("프레스")){
            name.setText("프레스");
            mugae.setText("거리 : ");
            String str = listViewItem.getData();
            str = str.replace("[","");
            str = str.replace("]","");

            String[] str2 = str.split(", ");
            Log.d("str2", str2[0]+""+str2[1]);
            String tempdata1 = str2[0];
            String tempdata2 = str2[1];

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
            String str = listViewItem.getData();

            str = str.replace("[","");
            str = str.replace("]","");


            String[] str2 = str.split(", ");
            Log.d("str2", str2[0]);
            record.setText(str2[0]);
            unit.setText("℃");
        }
        else if (listViewItem.getName().equals("의장")){
            name.setText("의장");
            mugae.setText("압력 : ");

            String str = listViewItem.getData();

            str = str.replace("[","");
            str = str.replace("]","");


            String[] str2 = str.split(", ");
            Log.d("str3", str2[0]);
            record.setText(str2[0]);;

            unit.setText("N");
        }
        else if (listViewItem.getName().equals("도장")){
            name.setText("도장");
            mugae.setText("무게 : ");
            String str = listViewItem.getData();

            str = str.replace("[","");
            str = str.replace("]","");


            String[] str2 = str.split(", ");
            Log.d("str2", str2[0]);
            record.setText(str2[0]);
            unit.setText("kg");
        }
        return view;
    }
}