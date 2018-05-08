package com.sven.retrofit2.api;

import java.util.List;

import com.sven.retrofit2.model.Item;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

public interface ItemApi {

	// @Headers({"Content-Type: application/json;charset=UTF-8"})
	@GET("items")
	public Observable<List<Item>> findAll();

	@POST("items")
	public Single<Item> add(@Body Item item);

	@DELETE("items/{id}")
	public Completable delete(@Path("id") int id);

	@Streaming
	@GET("api/items/mock2")
	public Observable<ResponseBody> stream();
}
