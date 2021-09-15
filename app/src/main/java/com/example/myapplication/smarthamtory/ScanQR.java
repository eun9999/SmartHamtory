package com.example.myapplication.smarthamtory;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

public class ScanQR extends AppCompatActivity {
    private IntentIntegrator intentIntegrator;
    private TextView textViewName, textViewAddress, textViewResult;
    String user_id, user_pwd;
    DBHelper helper;
    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        Button mainButton = (Button) findViewById(R.id.goMain);
        Button QRButton = (Button) findViewById(R.id.scanQR);

        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewAddress = (TextView) findViewById(R.id.textViewAddress);

        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setCaptureActivity(Orientation.class);
        intentIntegrator.setPrompt("Scan something");
        intentIntegrator.setBeepEnabled(false);
        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.initiateScan();
        //new IntentIntegrator(this).initiateScan();

        Intent intent = getIntent();
        user_id = intent.getExtras().getString("user_id");
        user_pwd = intent.getExtras().getString("user_pwd");

        helper = new DBHelper(ScanQR.this, "equipDB.db", null, 1);
        db = helper.getWritableDatabase();
        helper.onCreate(db);


        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("user_id", user_id);
                intent.putExtra("user_pwd", user_pwd);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        QRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ScanQR.class);
                intent.putExtra("user_id", user_id);
                intent.putExtra("user_pwd", user_pwd);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "QR 스캔 취소", Toast.LENGTH_LONG).show();
                // todo
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                // todo


                // 결과값 알림창 띄우기
                new AlertDialog.Builder(this)
                        .setTitle(R.string.app_name)
                        .setMessage(result.getContents() + " [" + result.getFormatName() + "]")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                            }
                        })
                        .show();

                // 결과값 화면에 표시하기
                try {
                    //data를 json으로 변환
                    JSONObject obj = new JSONObject(result.getContents());
                    // "" 속에는 QR코드의 키 값
                    textViewName.setText(obj.getString("name"));
                    textViewAddress.setText(obj.getString("address"));

                    //insert
                    ContentValues values = new ContentValues();
                    values.put("name", obj.getString("name"));
                    values.put("address", obj.getString("address"));
                    db.insertWithOnConflict("equipment", null, values, SQLiteDatabase.CONFLICT_REPLACE);

                    String sql = "SELECT * FROM equipment;";
                    Cursor c = db.rawQuery(sql, null);

                    while(c.moveToNext()){
                        System.out.println("name : "+c.getString(c.getColumnIndex("name")));
                        System.out.println("address : "+c.getString(c.getColumnIndex("address")));
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    //Toast.makeText(MainActivity.this, result.getContents(), Toast.LENGTH_LONG).show();
                    textViewResult.setText(result.getContents());
                }


            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}

