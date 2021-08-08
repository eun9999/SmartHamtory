package com.example.myapplication.smarthamtory;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpRetryException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class LoginActivity extends AppCompatActivity {

    private EditText et_id, et_pass;
    TextView textView;
    String urlStr;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_id = findViewById(R.id.et_id);
        et_pass = findViewById( R.id.et_pass );
        Button imageButton = (Button) findViewById(R.id.btn_login);

        textView = findViewById(R.id.textView); // 쿠키값 표시할 곳


        // 로그인 버튼을 클릭하면
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                urlStr = "https://plato.pusan.ac.kr";
                // http가 안돼
                //urlStr = "http://mqhome.ipdisk.co.kr/sites/login/";

                String user_id = et_id.getText().toString();    // 사용자가 입력한 아이디
                String user_pwd = et_pass.getText().toString(); // 사용자가 입력한 비밀번호

                // 쿠키 얻는 쓰레드 실행
                RequestThread thread = new RequestThread();
                thread.start();

                Response.Listener<String> responseListener = new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success){
                                String userID = jsonObject.getString("user_id");
                                String userPass = jsonObject.getString("user_pwd");
                                Toast.makeText(getApplicationContext(),"로그인에 성공하였습니다.",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, QRActivity.class);
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                /* 로그인 요청
                LoginRequest loginRequest = new LoginRequest(user_id, user_pwd, responseListener);
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);
                */

                // QR 선택 화면으로 이동
                Intent intent = new Intent(getApplicationContext(), QRActivity.class);
                startActivity(intent);

            }
        });

    }


    class RequestThread extends Thread {
        @Override
        public void run() {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(urlStr);
                conn = (HttpURLConnection) url.openConnection();
                if (conn != null) {
                    conn.setConnectTimeout(10000);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

                    int resCode = conn.getResponseCode();
                    if (resCode == HttpURLConnection.HTTP_OK) {
                        Reader reader = new InputStreamReader(new BufferedInputStream(conn.getInputStream()));
                        Map m = conn.getHeaderFields();
                        String cookie = null;

                        if(m.containsKey("Set-Cookie")) {
                            Collection c =(Collection)m.get("Set-Cookie");
                            for(Iterator i = c.iterator(); i.hasNext(); ) {
                                cookie = (String)i.next();
                            }
                        }
                        String[] array = cookie.split("="); // '='으로 분리
                        String[] arraytwo = array[1].split(";");    // ';'으로 분리
                        Log.d("cookie", arraytwo[0]);   // arraytwo[0]-> 쿠키값
                        println(arraytwo[0]);
                        //conn.setRequestProperty("csrftoken", arraytwo[0]);    // 서버에 세팅
                        reader.close();
                    }
                    conn.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void println(final String data) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    textView.setText(data);
                }
            });
        }

    }
}