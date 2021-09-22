package com.example.myapplication.smarthamtory;

import android.util.Log;
import android.webkit.CookieSyncManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class LoginRequest extends StringRequest {
    private final static String URL = "http://mqhome.ipdisk.co.kr/sites/login/";
    private Map<String,String> map;

    public LoginRequest(String user_id, String user_pwd, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("user_id", user_id);
        map.put("user_pwd", user_pwd);

    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
