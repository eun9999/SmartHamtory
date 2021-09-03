package com.example.myapplication.smarthamtory;

import android.content.ContentValues;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class RequestHttpURLConnection {
    public String request(String _url, ContentValues _params, String requestMethod) {
        HttpURLConnection urlConnection = null;

        // 1. 버퍼 파라미터 보낼값 설정
        StringBuilder sbParams = new StringBuilder();
        String csrf = cookie_csrf_request();    // csrf 쿠키 값 받아오기
        if(csrf == null)
            return "cookie_not_receive_error";

        //post 에 csrf 등록
        if(_params == null)
            sbParams.append("csrfmiddlewaretoken").append("=").append(csrf);
        else{
            boolean params_isand = false;
            String key,value;

            for(Map.Entry<String,Object> parameter : _params.valueSet()){
                key = parameter.getKey();
                value = parameter.getValue().toString();

                if(params_isand)
                    sbParams.append("&");
                else if (_params.size() > 1)
                    params_isand = true;

                sbParams.append(key).append("=").append(value);
            }
            sbParams.append("&").append("csrfmiddlewaretoken").append("=").append(csrf);    //post 로 보낼때 필수
        }
        //1 end

        // 2. url 연결 및 데이터 가지고 오기
        try {
            URL url = new URL(_url);
            urlConnection = (HttpURLConnection) url.openConnection();

            //url 연결
            urlConnection.setRequestMethod(requestMethod); // POST 또는 GET
            urlConnection.setRequestProperty("Accept-Charset", "utf-8"); // Accept-Charset 설정.
            urlConnection.setRequestProperty("Context_Type", "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("Cookie","csrftoken="+csrf);   // csrf 쿠키로도 전송

            // 파라미터 전달 POST 일때만 가능
            if(!requestMethod.equals("GET")) {
                String strParams = sbParams.toString();
                OutputStream os = urlConnection.getOutputStream();
                os.write(strParams.getBytes(StandardCharsets.UTF_8));
                os.flush();
                os.close();
            }
            
            // 반응 200 인지 확인
            if(urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK)
                return "fail_in_not"+HttpURLConnection.HTTP_OK;
            
            // html 요청 페이 값 받기
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), StandardCharsets.UTF_8));
            String line;
            StringBuilder page= new StringBuilder();

            while ((line = reader.readLine()) != null){
                page.append(line);
            }
            return page.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(urlConnection != null)
                urlConnection.disconnect();
        }
        //2 end
        return "error";
    }
    private String cookie_csrf_request() {
        HttpURLConnection urlConnection = null;

        // 1 . url 연결 및 쿠키 데이터 가지고 오기
        try {
            URL url = new URL("http://mqhome.ipdisk.co.kr/sites/login/");
            urlConnection = (HttpURLConnection) url.openConnection();

            //url 연결
            urlConnection.setRequestMethod("GET"); // 무조건 GET
            urlConnection.setRequestProperty("Accept-Charset", "utf-8"); // Accept-Charset 설정.
            urlConnection.setRequestProperty("Context_Type", "application/x-www-form-urlencoded");

            // 연결확인
            if(urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK)
                return null;
            
            // 쿠키값 list 로 받기
            List<String> cookies = urlConnection.getHeaderFields().get("set-cookie");
            assert cookies != null;
            for(Object cookie : cookies){
                Log.d("cookies",""+cookie.toString().split(";\\s*")[0]);
                for(Object s : cookie.toString().split(";\\s*")){
                    if(s.toString().split("=")[0].equals("csrftoken"))  // csrf 쿠키 값만 빼오기
                        return s.toString().split("=")[1];
                }
                return null;
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(urlConnection != null)
                urlConnection.disconnect();
        }
        return null;
    }
}