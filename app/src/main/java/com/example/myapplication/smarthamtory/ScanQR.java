package com.example.myapplication.smarthamtory;

import android.content.DialogInterface;
import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        Button mainButton = (Button) findViewById(R.id.goMain);
        Button QRButton = (Button) findViewById(R.id.scanQR);

        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewAddress = (TextView) findViewById(R.id.textViewAddress);
        textViewResult = (TextView)  findViewById(R.id.textViewResult);

        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setCaptureActivity(Orientation.class);
        intentIntegrator.setPrompt("Scan something");
        intentIntegrator.setBeepEnabled(false);
        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.initiateScan();
        //new IntentIntegrator(this).initiateScan();

        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        QRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ScanQR.class);
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
                    textViewResult.setText(obj.getString("information"));


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
