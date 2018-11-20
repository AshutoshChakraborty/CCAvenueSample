package com.example.admin.ccavenuesample.sdk;

import android.content.Context;

import com.example.admin.ccavenuesample.R;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Sdk {
    private final Retrofit retrofit;
    private Service service;

    private Sdk(Retrofit retrofit) {
        this.retrofit = retrofit;
        createService();
    }

    /**
     * Builder for {@link Sdk}
     */
    public static class Builder {
        public Builder() {
        }

        /**
         * Create the {@link Sdk} to be used.
         *
         * @return {@link Sdk}
         */
        public Sdk build(Context context, boolean shouldUseMyJson) {
            Retrofit retrofit = null;
            String baseUrl = null;
            if (shouldUseMyJson){
                baseUrl = context.getResources().getString(R.string.base_url_myjson);
            }else {
                baseUrl = context.getResources().getString(R.string.base_url);
            }
            if (InterceptorHTTPClientCreator.getOkHttpClient() != null) {
                retrofit = new Retrofit.Builder()
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .client(InterceptorHTTPClientCreator.getOkHttpClient())
                        .baseUrl(baseUrl)
                        .build();

                return new Sdk(retrofit);
            } else {
                retrofit = new Retrofit.Builder()
                        .addConverterFactory(ScalarsConverterFactory.create())
//                        .client(InterceptorHTTPClientCreator.getOkHttpClient())
                        .baseUrl(baseUrl)
                        .build();
            }
            return new Sdk(retrofit);
        }

        public Sdk build(Context context) {
            Retrofit retrofit = null;
            String baseUrl = null;
            baseUrl = context.getResources().getString(R.string.base_url);
            if (InterceptorHTTPClientCreator.getOkHttpClient() != null) {
                retrofit = new Retrofit.Builder()
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .client(InterceptorHTTPClientCreator.getOkHttpClient())
                        .baseUrl(baseUrl)
                        .build();

                return new Sdk(retrofit);
            } else {
                retrofit = new Retrofit.Builder()
                        .addConverterFactory(ScalarsConverterFactory.create())
//                        .client(InterceptorHTTPClientCreator.getOkHttpClient())
                        .baseUrl(baseUrl)
                        .build();
            }
            return new Sdk(retrofit);
        }
    }

    private void createService() {
        service = retrofit.create(Service.class);
    }

    public Service getService(){
        return service;
    }
}
