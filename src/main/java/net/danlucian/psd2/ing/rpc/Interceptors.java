package net.danlucian.psd2.ing.rpc;

import okhttp3.logging.HttpLoggingInterceptor;

public interface Interceptors {

    default HttpLoggingInterceptor loggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return interceptor;
    }

}
