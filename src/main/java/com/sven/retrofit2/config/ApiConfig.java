package com.sven.retrofit2.config;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sven.retrofit2.api.ItemApi;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Configuration
public class ApiConfig {

	@Bean
	public OkHttpClient initOkHttpClient() {

		//@formatter:off
		return new OkHttpClient().newBuilder()
				.readTimeout(0,  TimeUnit.SECONDS)
				.writeTimeout(0, TimeUnit.SECONDS)
				.connectTimeout(0, TimeUnit.SECONDS)
				.retryOnConnectionFailure(true)
				.addInterceptor(new Interceptor() {
			@Override
			public okhttp3.Response intercept(Chain chain) throws IOException {
				Request originalRequest = chain.request();

				Request.Builder builder = originalRequest.newBuilder().header("Authorization",
						Credentials.basic("aUsername", "aPassword"));

				Request newRequest = builder.build();
				return chain.proceed(newRequest);
			}
		}).build();
		//@formatter:on
	}

	@Bean
	public ItemApi getItemApi(OkHttpClient client) {
		//@formatter:off
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://127.0.0.1:8090/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        //@formatter:on
		return retrofit.create(ItemApi.class);
	}
}
