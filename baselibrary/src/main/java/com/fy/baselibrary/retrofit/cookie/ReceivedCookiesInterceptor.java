package com.fy.baselibrary.retrofit.cookie;

import com.fy.baselibrary.utils.LogUtils;
import com.fy.baselibrary.utils.SpfUtils;

import java.io.IOException;

import io.reactivex.Observable;
import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * 实现 拦截器，将 Http返回的 cookie 存储到本地
 * Created by fangs on 2018/3/30.
 */
public class ReceivedCookiesInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (null == chain) LogUtils.d("http", "Receivedchain == null");

        Response response = chain.proceed(chain.request());
        LogUtils.d("http", "originalResponse" + response.toString());

        if (!response.headers("set-cookie").isEmpty()) {

            StringBuffer cookieBuffer = new StringBuffer();

            Object[] strArray = response.headers("set-cookie").toArray();

            Observable.fromArray(strArray)
                    .map(s -> s.toString().split(";")[0])
                    .subscribe(s -> cookieBuffer.append(s).append(";"));

            SpfUtils.saveStrToSpf("cookie", cookieBuffer.toString());

            LogUtils.d("http", "ReceivedCookiesInterceptor" + cookieBuffer.toString());
        }

        return response;
    }
}
