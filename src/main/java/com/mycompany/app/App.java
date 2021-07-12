package com.mycompany.app;

import java.util.concurrent.*;

public class App
{
    static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(); // create a single thread because we are using scheduler

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println(Thread.currentThread().getId()); // not needed, but gives an idea about the working thread
        // pollForCompletion returns a CompletableFuture object. Once this is complete only then thenApply will be called
        //pollForCompletion is Task T1 ,and the logic inside thenApply acts as a task T2 which will only execute once task T1 is complete
        final Object jobResult = pollForCompletion("2").thenApply(jId -> {
            System.out.println("Your next operation "+jId); // this block acts as Task T2, you can write your own logic here
            return jId;
        }).get();
        // Completable Future makes asynchronous calls, but we want the async logic to commplete before executing next staement, so we are using get()
        System.out.println(Thread.currentThread().getId());
        executor.shutdown();
        //when shutdown() method is called on an executor service, it stops accepting new tasks, waits for previously submitted tasks to execute, and then terminates the executor.
    }

    private static CompletableFuture<String> pollForCompletion(String jobId) {
        Long time = System.currentTimeMillis(); //
        Long timeToReach = time + 10000; // a variable to decide when polling should be complete

        RemoteServer remoteServer = new RemoteServer(); // has the main polling logic
        CompletableFuture<String> completionFuture = new CompletableFuture<>(); // we will return this object once polling is complete
        final ScheduledFuture<Void> checkFuture = (ScheduledFuture<Void>) executor.scheduleAtFixedRate(() -> {
            try {
                // we are calling the polling logic. If it returns true, we complete the Task T1 and return a completed future
                if (remoteServer.isJobDone(jobId, timeToReach)) {
                    completionFuture.complete(jobId);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, 0, 2, TimeUnit.SECONDS);
        // scheduler is scheduled to be executed every 2 seconds, till it return true.
        completionFuture.whenComplete((result, thrown) -> {
            checkFuture.cancel(true);
        });
        return completionFuture;
    }
}

class RemoteServer {
     boolean isJobDone(String jobId, Long timeToReach) throws InterruptedException {
        System.out.println(Thread.currentThread().getId());
        if(System.currentTimeMillis() > timeToReach) // this will be true after 10 seconds according to the logic
            return true;
        else
            return false;

    }
}

//OUTPUT :
//
//        1
//        12
//        12
//        12
//        12
//        12
//        12
//        Your next operation 2
//        1