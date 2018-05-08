package com.sven.retrofit2.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.function.client.WebClient;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;

import com.sven.retrofit2.api.ItemApi;
import com.sven.retrofit2.model.Item;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import reactor.core.publisher.Flux;

@Controller
@RequestMapping("/items/stream")
public class ItemStreamController {

	@Autowired
	private ItemApi itemApi;

	Observable<Long> tick = Observable.interval(500, TimeUnit.MILLISECONDS);

	private Flux<Item> mock() {
		List<Item> staticData = IntStream.range(0, 100)
				.mapToObj(s -> Item.builder().id(s).name("item" + s).group("static").build())
				.collect(Collectors.toList());

		Flux<Item> data = Flux.interval(Duration.ofMillis(500)).zipWithIterable(staticData).map(s -> s.getT2())
				.doOnNext(s -> System.out.println(s.getName() + " requested"));
		return data;
	}

	@GetMapping()
	public String findAll(final Model model) {

		//@formatter:off
		Flux<Item> data = WebClient.create().get().uri("http://127.0.0.1:8090/api/items/mock2")
				// must be stream type
				//.accept(MediaType.APPLICATION_STREAM_JSON)
				.accept(MediaType.TEXT_EVENT_STREAM).exchange()
				.flatMapMany(s -> s.bodyToFlux(Item.class));
		//@formatter:on
		data.doOnNext(s -> System.out.println(s.getName() + " requested"));

		model.addAttribute("items", new ReactiveDataDriverContextVariable(data, 1));
		// model.addAttribute("items", new ReactiveDataDriverContextVariable(mock(),
		// 1));
		return "items";
	}

	@GetMapping("retrofit2")
	public String findAllWithRetrofit2(final Model model) {

		//@formatter:off
		Observable<ResponseBody> data = itemApi.stream();
		
		data.subscribe(s -> {
			System.out.println("entering onnext()");
            InputStream inputStream = s.byteStream();
            System.out.println("read byte");
            BufferedReader br = null;
            StringBuilder sb = new StringBuilder();

            String line;
            try {

                br = new BufferedReader(new InputStreamReader(inputStream));
                while (br.ready()) {
                    line = br.readLine();
                    sb.append(line);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            
            System.out.println(sb);
			
		});
//		call.enqueue(new Callback<ResponseBody>() {
//
//			@Override
//			public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//				if (response.isSuccessful()) {
//					try {
//						Flux<DataBuffer>
//						
//						System.out.println(new String(response.body().byteStream()));d
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//				
//			}
//
//			@Override
//			public void onFailure(Call<ResponseBody> call, Throwable t) {
//				// TODO Auto-generated method stub
//				
//			}});
		
		//Flux<Item> data = raw.flatMap(Flux::fromIterable);
		//@formatter:on

		model.addAttribute("items", new ReactiveDataDriverContextVariable(data, 1));
		// model.addAttribute("items", new ReactiveDataDriverContextVariable(mock(),
		// 1));
		return "items";
	}
}
