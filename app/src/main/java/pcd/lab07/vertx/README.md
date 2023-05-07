# Lab06

## Asynchronous Programming in Java with Event loops: Vert.x framework

- The entry point into the Vert.x Core API is the `Vert.X` interface:
  - it’s the control center of Vert.x and is how you do pretty much everything, including creating clients and servers, getting a reference to the event bus, setting timers, as well as many other things
  - singleton:

    ```java
    Vertx vertx = Vertx.vertx();
    ```

- **Event-driven approach** with **Futures**, which are equivalent to JS Promises
  - **Each Vertx instance maintains several event loops.** By default we choose the number based on the number of available cores on the machine, but this can be overridden. **This means a single Vertx process can scale across your server.** Even though a Vertx instance maintains multiple event loops, **any particular handler will never be executed concurrently**, and in most cases (with the exception of worker verticles, see below) will always be called using the exact same event loop.

  - :point_right: <ins>***The Golden Rule - Don’t Block the Event Loop***</ins>
    - If your application is not responsive it might be a sign that you are blocking an event loop somewhere. To help you diagnose such issues, Vert.x will automatically log warnings if it detects an event loop hasn’t returned for some time:
      ```
      Thread vertx-eventloop-thread-3 has been blocked for 20458 ms
      ```

  - see `Step1_Basic.java`
    - code inside `main()` is executed by the main thread `Thread[#1,main,5,main]` 
    - handler code passed in `onComplete()` method is executed by the event-loop thread: `Thread[#24,vert.x-eventloop-thread-0,5,main]`
    - consequence: the handler is executed before the log of the message `"done"` (which is intentionally delayed) -- in general the order is not deterministic.

- The suggested way to create active components: using **Verticles**.
  - actor-like model (that we will see in the following labs)
  - Verticles are chunks of code that get deployed and run by Vert.x. A Vert.x instance maintains N event loop threads (where N by default is core*2).
    - Implemented extending `AbstractVerticle` class

      ![verticle](http://www.plantuml.com/plantuml/svg/hLB1QiCm3BtxAmIz9BHV44f8wIuhWnK6kJkE6ZGSErZgo1ZxzyLn2AKm66K-2Fdf-Kbwk6BHGQwzXGyCGiQYKkG4mqKRX7h1u4l1TBOTIeJ6B2_csfXWSYHPV3yky0wCdqH6AU2OaZmnvsxP7C_1zyHOm9BiQqzjkVByaCNm6-jRATsm16q4ZDNCS5YkwypK3nPxTCBtUspkfqlexGPfZ8bteIvkUsBlNfjtD8cxRN6m_ZEgFp_5-fi4Sjq_kJG4Rv3v43gE6Y_4iayDobiR7_7qp_w2KXMwTlePFm00 "verticle")

      Normally you would override the `start` method.When Vert.x deploys the verticle it will call the it, and when the method has completed the verticle will be considered started.
      You can also optionally override the `stop` method.

      If you have to do something in your verticle start-up which takes some time and you don’t want the verticle to be considered deployed until that happens (e.g. start HTTP server) you have to implement the asynchronous `start` method (see `Step7_SimpleServer` e `Step8_WebService`). This version of the method takes a `Future` as a parameter. When the method returns the verticle will not be considered deployed. Some time later, after you’ve done everything you need to do you can call complete on the Future (or fail) to signal that you’re done.

    - see `Step2_WithVerticle.java`
      - unlike previous example everything inside the verticle is executed by event-loop thread: `Thread[#23,vert.x-eventloop-thread-0,5,main]`
      - consequence: the handlers are **always** executed **after** the message `"done"` has been printed on console.

  - An application would typically be composed of many verticle instances running in the same Vert.x instance at the same time. 

- `Step3_Chaining.java`: `compose` can be used for chaining futures
  - `Future` offers more: `map`, `recover`, `otherwise`, `andThen` and even a `flatMap` which is an alias of `compose`

- `Step4_Promises.java`
  ```java
  Promise<String> promise = Promise.promise();
  legacyGreetAsync(promise);
  Future<String> greeting = promise.future();
  ```
  - Futures represent the **"read-side" of an asynchronous result**
  - Promises are the **"write-side"**, **allowing you to defer the action of providing a result**.


- `Step5_Composition.java`
  - `CompositeFuture.all` takes several futures arguments (up to 6) and returns a future that is succeeded when all the futures are succeeded and failed when at least one of the futures is failed
  - `CompositeFuture.any` takes several futures arguments (up to 6) and returns a future that is succeeded when one of the futures is, and failed when all the futures are failed
  - `CompositeFuture.join` takes several futures arguments (up to 6) and returns a future that is succeeded when all the futures are succeeded, and failed when all the futures are completed and at least one of them is failed


- `Step6_WithBlocking.java`
  - in an idyllic world all APIs will be written asynchronously but in the real world, at least in the JVM standard library (but other in lot of other libraries), have synchronous APIs with blocking behavior (see JDBC API)
  - But we cannot call blocking operations directly from an event loop...
  - The way to do is by calling `executeBlocking` specifying both the blocking code to execute and a result handler to be called back asynchronous when the blocking code has been executed
    ```java
    vertx.executeBlocking(promise -> {
        // Call some blocking API that takes a significant amount of time to return
        String result = someAPI.blockingMethod("hello");
        promise.complete(result);
    }).onComplete(res -> {
        System.out.println("The result is: " + res.result());
    });
    ```
    - note that blocking code should block for a reasonable amount of time (i.e no more than a few seconds)
    - long blocking operations should use a dedicated thread managed by the application, which can interact with verticles using the event-bus or `runOnContext`
  - An alternative way to run blocking code is to use a worker verticle: like a standard verticle but it’s executed using a thread from the Vert.x worker thread pool, rather than using an event loop.
    - see [documentation](https://vertx.io/docs/vertx-core/java/#worker_verticles)

- **Event Bus**
  - ...
