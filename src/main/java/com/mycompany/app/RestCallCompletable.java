package com.mycompany.app;

import org.omg.CORBA.TIMEOUT;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * This program will call 5 endpoints parallelly using Completable Future, and store the responses from all endpoints in an object
 */
public class RestCallCompletable {

    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
        List<Object> list = new ArrayList<>(); // list object to store and aggregate responses from all endpoints

        // list of 5 different dummy URLS which are to be executed parallely
        String url1 = "https://jsonplaceholder.typicode.com/todos/1";
        String url2 = "https://jsonplaceholder.typicode.com/todos/2";
        String url3 = "https://jsonplaceholder.typicode.com/todos/3";
        String url4 = "https://jsonplaceholder.typicode.com/todos/4";
        String url5 = "https://jsonplaceholder.typicode.com/todos/5";

        RestTemplate restTemplate = new RestTemplate(); // this is a springframework dependency to make Rest Endpoint calls.

        // allOf makes sure to call all endpoints parallelly
        // supplyAsync will return a CompletableFuture object which will consist of the response from endpoints
        // thenApply will only be executed once supplyAsync() is complete
        // in thenAccept we just append the response from endpoints to the list object
        // error handling : https://www.baeldung.com/java-completablefuture
        CompletableFuture.allOf(
                CompletableFuture.supplyAsync(() -> restTemplate.getForObject(url1, String.class))
                        .thenApply(x -> {
                            list.add(x);
                            System.out.println("called url1");
                            return null;
                        }).handle((s, t) -> s != null ? s : "Hello, Stranger!"),
                CompletableFuture.supplyAsync(() -> restTemplate.getForObject(url2, String.class))
                        .thenApply(x -> {
                            list.add(x);
                            System.out.println("called url2");
                            return null;
                        }).handle((s, t) -> s != null ? s : "Hello, Stranger!"),
                CompletableFuture.supplyAsync(() -> restTemplate.getForObject(url3, String.class))
                        .thenApply(x -> {
                            list.add(x);
                            System.out.println("called url3");
                            return null;
                        }).handle((s, t) -> s != null ? s : "Hello, Stranger!"),
                CompletableFuture.supplyAsync(() -> restTemplate.getForObject(url4, String.class))
                        .thenApply(x -> {
                            list.add(x);
                            System.out.println("called url4");
                            return null;
                        }).handle((s, t) -> s != null ? s : "Hello, Stranger!"),
                CompletableFuture.supplyAsync(() -> restTemplate.getForObject(url5, String.class))
                        .thenApply(x -> {
                            list.add(x);
                            System.out.println("called url5");
                            return null;
                        }).handle((s, t) -> {
                            if (s != null) {
                            } else {
                               System.out.println("Hello, Stranger!");
                            }
                    return s;
                })
                ).get();
        System.out.println(list);
    }

//  Output :

//[{
//        "userId": 1,
//        "id": 4,
//        "title": "et porro tempora",
//        "completed": true
//    }, {
//        "userId": 1,
//        "id": 2,
//        "title": "quis ut nam facilis et officia qui",
//        "completed": false
//    }, {
//        "userId": 1,
//        "id": 5,
//        "title": "laboriosam mollitia et enim quasi adipisci quia provident illum",
//        "completed": false
//    }, {
//        "userId": 1,
//        "id": 1,
//        "title": "delectus aut autem",
//        "completed": false
//    }, {
//        "userId": 1,
//        "id": 3,
//        "title": "fugiat veniam minus",
//        "completed": false
//    }]
//
}
