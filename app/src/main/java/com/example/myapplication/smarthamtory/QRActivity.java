package com.example.myapplication.smarthamtory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class QRActivity extends AppCompatActivity {
    private Button goMainBtn;
    private Button scanQRBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        goMainBtn = (Button) findViewById(R.id.goMain);
        scanQRBtn = (Button) findViewById(R.id.scanQR);

        goMainBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(QRActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        scanQRBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(QRActivity.this, ScanQR.class);
                startActivity(intent);
            }
        });
    }

}
