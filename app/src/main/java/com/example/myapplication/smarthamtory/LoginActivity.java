package com.example.myapplication.smarthamtory;

import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class LoginActivity extends AppCompatActivity {

    EditText et_id, et_pass;
    String _url;
    public String user_id, user_pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_id = findViewById(R.id.et_id);
        et_pass = findViewById( R.id.et_pass );
        Button loginButton = findViewById(R.id.btn_login);

        // 로그인 버튼을 클릭하면
        loginButton.setOnClickListener(view -> {
            _url = "mqhome.ipdisk.co.kr/apps/onlyloginvalue/";

            user_id = et_id.getText().toString();    // 사용자가 입력한 아이디
            user_pwd = et_pass.getText().toString(); // 사용자가 입력한 비밀번호

            ContentValues contentValues = new ContentValues();
            contentValues.put("user_id", user_id);
            contentValues.put("user_pwd", user_pwd);

            NetworkTest networkTest = new NetworkTest("http://" + _url, contentValues,"POST");
            networkTest.execute();

        });

    }

    public class NetworkTest extends AsyncTask<Void,Void,String> {
        String url;
        ContentValues values;
        String requestMethod;
        NetworkTest(String url, ContentValues contentValues, String requestMethod){
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

        // ********************* 이부분을 이용하여 intent 실행 하면 될듯 *********************** //
        // ******* 끝난 결과 result 값이 들어옴 ********** //
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(getApplicationContext(),""+ result, Toast.LENGTH_SHORT).show();
            if(result.equals("성공")){
                Intent intent = new Intent(getApplicationContext(), QRActivity.class);
                intent.putExtra("user_id", user_id);
                intent.putExtra("user_pwd", user_pwd);
                startActivity(intent);
            }
        }
        // ******************************************** //
        // ******************************************************************************** //
    }

}