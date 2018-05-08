package com.sven.retrofit2.controller;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sven.retrofit2.api.ItemApi;
import com.sven.retrofit2.model.Item;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.swagger.annotations.Api;
import reactor.core.publisher.Flux;

@Api
@RestController
@RequestMapping("/api/items")
public class ItemController {

	private ItemApi itemApi;

	public ItemController(ItemApi itemApi) {
		this.itemApi = itemApi;
	}

	@GetMapping()
	public Observable<Item> findAll() {

		return itemApi.findAll().flatMap(Observable::fromIterable);
	}

	@PostMapping()
	public Single<Item> add(@RequestBody Item item) {

		return itemApi.add(item);
	}

	@DeleteMapping("/{id}")
	public Completable delete(@PathVariable int id) {

		return itemApi.delete(id);
	}

	@PutMapping("compute")
	public Observable<Item> compute() {

		Item newItem = Item.builder().id(99).name("new added item").build();

		//@formatter:off
		return this.findAll()
				.concatWith(this.add(newItem))
				.concatWith(this.delete(newItem.getId()))
				.filter(s -> newItem.getId() != s.getId())
				.doOnNext(System.out::println);
		//@formatter:on

	}

	Observable<Long> tick = Observable.interval(500, TimeUnit.MILLISECONDS);

	@GetMapping("/mock")
	public Observable<Item> mock() {

		List<Item> staticData = IntStream.range(0, 10)
				.mapToObj(s -> Item.builder().id(s).name("item" + s).group("static").build())
				.collect(Collectors.toList());

		Observable<Item> data = Observable.fromIterable(staticData).zipWith(tick, (obs, timer) -> obs)
				.doOnNext(s -> System.out.println(s.getName() + " requested"));

		return data;

	}

	@GetMapping(path = "/mock2", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_STREAM_JSON_VALUE,
			MediaType.ALL_VALUE })
	public Flux<Item> mock2() {

		List<Item> staticData = IntStream.range(0, 100)
				.mapToObj(s -> Item.builder().id(s).name("item" + s).group("static").build())
				.collect(Collectors.toList());

		Flux<Item> data = Flux.interval(Duration.ofMillis(500)).zipWithIterable(staticData).map(s -> s.getT2())
				.doOnNext(s -> System.out.println(s.getName() + " requested"));

		return data;

	}
}
