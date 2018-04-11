package com.example.fudj.networkstudy;

import android.content.ContentValues;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.Call;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public static final Executor EXECUTOR = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        EXECUTOR.execute(new Runnable() {
//            @Override
//            public void run() {
////                useHttpUrlConnectionPost("https://www.baidu.com");
//                userJsonVolley();
//            }
//
//        });

        OKHttpEngine.getInstance().doAsyncHttpRequest("https://www.baidu.com", new OKHttpEngine.ResultCallBack() {
            @Override
            public void onResponse(Call call, Object result) {
                    Log.e(TAG, "onResponse" + result.toString());
            }

            @Override
            public void onError(Call call, Exception e) {
                Log.e(TAG, "onError" + e.getMessage());
            }
        });


    }

    private void useVolley() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest mStringRequest = new StringRequest(Request.Method.GET, "https://www.baidu.com", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.getMessage());
            }
        });
        requestQueue.add(mStringRequest);
    }

    private void userJsonVolley() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, "https://www.baidu.com",
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.getMessage());
            }
        });
        requestQueue.add(objectRequest);
    }

    private void useHttpUrlConnectionPost(String url) {
        InputStream inputStream;
        HttpURLConnection urlConnection = getHttpURLConnection(url);

        HashMap<String, String> paramsList = new HashMap<>();
        paramsList.put("userName", "mrfu");
        paramsList.put("password", "123456");

        try {
            postParams(urlConnection.getOutputStream(), paramsList);
            inputStream = urlConnection.getInputStream();

            int resultCode = urlConnection.getResponseCode();
            String response = convertStreamToString(inputStream);

            Log.e(TAG, "请求状态码:" + resultCode + "\n请求结果:\n" + response);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private HttpURLConnection getHttpURLConnection(String url) {
        HttpURLConnection mHttpURLConnection = null;
        try {
            URL mURL = new URL(url);
            mHttpURLConnection = (HttpURLConnection) mURL.openConnection();
            mHttpURLConnection.setConnectTimeout(5000);
            mHttpURLConnection.setReadTimeout(5000);
            mHttpURLConnection.setRequestMethod("POST");
            mHttpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            mHttpURLConnection.setDoInput(true);
            mHttpURLConnection.setDoOutput(true);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mHttpURLConnection;
    }

    private void postParams(OutputStream output, HashMap<String, String> paramsList) throws IOException {
        StringBuilder mStringBuilder = new StringBuilder();
        for (String key : paramsList.keySet()) {
            if (!TextUtils.isEmpty(mStringBuilder)) {
                mStringBuilder.append("&");
            }
            mStringBuilder.append(URLEncoder.encode(key, "UTF-8"));
            mStringBuilder.append("=");
            mStringBuilder.append(URLEncoder.encode(paramsList.get(key), "UTF-8"));
        }

        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(output, "UTF-8"));
        bufferedWriter.write(mStringBuilder.toString());
        bufferedWriter.flush();
        bufferedWriter.close();
    }

    private String convertStreamToString(InputStream in) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        StringBuffer sb = new StringBuffer();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }
}
