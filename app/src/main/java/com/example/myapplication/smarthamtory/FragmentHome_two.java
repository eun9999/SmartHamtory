package com.example.myapplication.smarthamtory;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentHome_two extends Fragment {

    TextView data1, data2, data3, data4;
    EditText error_content1, error_content2, error_content3, error_content4;
    CheckBox cb1, cb2, cb3, cb4;
    Button sendErrorBtn;
    String _url;
    String user_id, user_pwd, error_content, equipment_name;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_two, container, false);

        cb1 = view.findViewById(R.id.checkBox1);    // 설비 1의 체크박스
        cb2 = view.findViewById(R.id.checkBox2);
        cb3 = view.findViewById(R.id.checkBox3);
        cb4 = view.findViewById(R.id.checkBox4);

        sendErrorBtn = view.findViewById(R.id.sendError);   // 에러 전송 버튼

        data1 = view.findViewById(R.id.machine_data1_2);    // 설비 1의 센서값
        data2 = view.findViewById(R.id.machine_data2_2);
        data3 = view.findViewById(R.id.machine_data3_2);
        data4 = view.findViewById(R.id.machine_data4_2);

        error_content1 = view.findViewById(R.id.error_content1);    // 설비 1의 에러 내용
        error_content2 = view.findViewById(R.id.error_content2);
        error_content3 = view.findViewById(R.id.error_content3);
        error_content4 = view.findViewById(R.id.error_content4);


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
        sendErrorBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                _url = "mqhome.ipdisk.co.kr/apps/errorreceive/";

                if (cb1.isChecked()){
                    equipment_name = "프레스";
                    error_content = error_content1.getText().toString();    // 설비 1의 에러 내용
                    putData(user_id, user_pwd, equipment_name, error_content);
                    cb1.setChecked(false);
                }
                if (cb2.isChecked()){
                    equipment_name = "차체조립";
                    error_content = error_content2.getText().toString();    // 설비 2의 에러 내용
                    putData(user_id, user_pwd, equipment_name, error_content);
                    cb2.setChecked(false);
                }
                if (cb3.isChecked()){
                    equipment_name = "도장";
                    error_content = error_content3.getText().toString();    // 설비 3의 에러 내용
                    putData(user_id, user_pwd, equipment_name, error_content);
                    cb3.setChecked(false);
                }
                if (cb4.isChecked()){
                    equipment_name = "의장";
                    error_content = error_content4.getText().toString();    // 설비 4의 에러 내용
                    putData(user_id, user_pwd, equipment_name, error_content);
                    cb4.setChecked(false);
                }


            }
        });

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
}
