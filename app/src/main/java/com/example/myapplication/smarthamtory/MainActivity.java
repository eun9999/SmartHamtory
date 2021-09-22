package com.example.myapplication.smarthamtory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.altbeacon.beacon.BeaconManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MainActivity extends AppCompatActivity {
    protected static final String TAG = "MainActivity";
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentHome fragmentHome = new FragmentHome();
    private FragmentHome_two fragmentHome_two = new FragmentHome_two();
    private FragmentLocation fragmentLocation = new FragmentLocation();
    String user_id, user_pwd;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner leScanner;
    //public static Context context_main;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //context_main = this;
        Intent intent = getIntent();
        user_id = intent.getExtras().getString("user_id");
        user_pwd = intent.getExtras().getString("user_pwd");

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, fragmentHome).commitAllowingStateLoss();

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelectedListener());
        String[] permission_list = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };
        ActivityCompat.requestPermissions(MainActivity.this, permission_list,  1);
    }

    // 하단 내비게이션 바 이동
    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener{
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            // 각 버튼을 클릭했을 때 fragment 띄우기
            switch(menuItem.getItemId())
            {
                case R.id.home:
                    transaction.replace(R.id.frameLayout, fragmentHome).commitAllowingStateLoss();
                    break;
                case R.id.home_two:
                    Bundle bundle = new Bundle();
                    bundle.putString("user_id", user_id);
                    bundle.putString("user_pwd", user_pwd);
                    FragmentHome_two fragment = new FragmentHome_two();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).commit();
                    fragment.setArguments(bundle);
                    break;
                case R.id.location:
                    Bundle bundle2 = new Bundle();
                    bundle2.putString("user_id", user_id);
                    bundle2.putString("user_pwd", user_pwd);
                    FragmentLocation fragment2 = new FragmentLocation();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment2).commit();
                    fragment2.setArguments(bundle2);
                    break;
            }
            return true;
        }
    }



}