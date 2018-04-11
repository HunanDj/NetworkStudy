package com.example.fudj.networkstudy;

import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by fudj on 2018/4/10.
 */

public class OKHttpEngine {


    private volatile static OKHttpEngine mInstance;

    public static OKHttpEngine getInstance() {
        if (mInstance == null) {
            synchronized (OKHttpEngine.class) {
                if (mInstance == null) {
                    mInstance = new OKHttpEngine();
                }
            }
        }
        return mInstance;
    }

    public interface ResultCallBack<T> {

        void onResponse(Call call, T result);

        void onError(Call call, Exception e);
    }

    private OkHttpClient mClient;
    private Handler mHandler;


    public OKHttpEngine() {
        mClient = new OkHttpClient.Builder().
                readTimeout(5, TimeUnit.SECONDS).
                writeTimeout(5, TimeUnit.SECONDS).addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                return chain.proceed(chain.request());
            }
        }).
                connectTimeout(5, TimeUnit.SECONDS).build();

        mHandler = new Handler(Looper.getMainLooper());
    }

    public void doAsyncHttpRequest(String url, ResultCallBack callBack) {
        final Request request = new Request.Builder().url(url).build();

        Call call = mClient.newCall(request);

        dealResult(call, callBack);

    }


    private void dealResult(Call call, final ResultCallBack callBack) {
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendFailedCallback(call, e, callBack);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                sendSuccessCallBack(call, response.body().string(), callBack);
            }
        });

    }


    private void sendFailedCallback(final Call call, final Exception e, final ResultCallBack callBack) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callBack.onError(call, e);
            }
        });
    }

    private void sendSuccessCallBack(final Call call, final Object object, final ResultCallBack callBack) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callBack.onResponse(call, object);
            }
        });
    }

}
