package com.example.myapplication.smarthamtory;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class QRActivity extends AppCompatActivity {
    private Button goMainBtn;
    private Button scanQRBtn;
    private Button deleteBtn;
    TextView textViewName;
    String user_id, user_pwd;
    DBHelper helper;
    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);
        deleteBtn = (Button) findViewById(R.id.delete);
        goMainBtn = (Button) findViewById(R.id.goMain);
        scanQRBtn = (Button) findViewById(R.id.scanQR);
        helper = new DBHelper(QRActivity.this, "equipDB.db", null, 1);
        db = helper.getWritableDatabase();
        helper.onCreate(db);
        // LoginActivity로부터 user_id 전송받음
        Intent intent = getIntent();
        user_id = intent.getExtras().getString("user_id");
        user_pwd = intent.getExtras().getString("user_pwd");
        deleteBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //db 전체 데이터 삭제
                String delete = "DELETE FROM equipment";
                db.execSQL(delete);
            }
        });
        goMainBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(QRActivity.this, MainActivity.class);
                intent.putExtra("user_id", user_id);
                intent.putExtra("user_pwd", user_pwd);
                startActivity(intent);
            }
        });

        scanQRBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(QRActivity.this, ScanQR.class);
                intent.putExtra("user_id", user_id);
                intent.putExtra("user_pwd", user_pwd);
                startActivity(intent);
            }
        });


    }

}