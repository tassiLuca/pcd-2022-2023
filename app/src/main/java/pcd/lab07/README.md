# Lab07

## Asynchronous Programming in Java with Event loops: Vert.x framework

- The entry point into the Vert.x Core API is the [`Vert.X`](https://vertx.io/docs/apidocs/io/vertx/core/Vertx.html) interface:
    ```java
    Vertx vertx = Vertx.vertx();
    ```
  - it’s the control center of Vert.x and is how you do pretty much everything, including creating clients and servers, getting a reference to the event bus, setting timers, as well as many other things

- **Event-driven approach** with [**Futures**](https://vertx.io/docs/apidocs/io/vertx/core/Future.html), which are equivalent to JS Promises
  - **Each Vertx instance maintains several event loops** (by default based on the number of available cores on the machine, but this can be overridden). **This means a single Vertx process can scale across your server.** Even though a Vertx instance maintains multiple event loops, **any particular handler will never be executed concurrently**, and in most cases (with the exception of worker verticles, see below) will always be called using the exact same event loop.

  - :point_right: <ins>***The Golden Rule - Don’t Block the Event Loop***</ins>
    - If your application is not responsive it might be a sign that you are blocking an event loop somewhere. To help you diagnose such issues, Vert.x will automatically log warnings if it detects an event loop hasn’t returned for some time:

      ```
      Thread vertx-eventloop-thread-3 has been blocked for 20458 ms
      ```

  - see `Step1_Basic.java`
    - code inside `main()` is executed by the main thread `Thread[#1,main,5,main]` 
    - handler code passed in `onComplete()` method is executed by the event-loop thread: `Thread[#24,vert.x-eventloop-thread-0,5,main]`
    - consequence: the handler is executed before the log of the message `"done"` (which is intentionally delayed) -- in general the order is not deterministic.
    - note in Vert.x a [`FileSystem`](https://vertx.io/docs/apidocs/io/vertx/core/file/FileSystem.html) interface exists which contains a broad set of operations for manipulating files on the file system with **non-blocking** behaviour, taking a handler to be called when the operation completes or an error occurs
      - I/O operation in the Java framework are synchronous, blocking behaviour :expressionless:

- :star: The suggested way to create active components: using **Verticles**.
  - actor-like model (that we will see in the following labs)
  - [Verticles](https://vertx.io/docs/apidocs/io/vertx/core/Verticle.html) are chunks of code that get deployed and run by Vert.x. A Vert.x instance maintains N event loop threads (where N by default is core $*$ 2).
    - Implemented extending [`AbstractVerticle`](https://vertx.io/docs/apidocs/io/vertx/core/AbstractVerticle.html) class

      ![verticle](http://www.plantuml.com/plantuml/svg/hLB1QiCm3BtxAmIz9BHV44f8wIuhWnK6kJkE6ZGSErZgo1ZxzyLn2AKm66K-2Fdf-Kbwk6BHGQwzXGyCGiQYKkG4mqKRX7h1u4l1TBOTIeJ6B2_csfXWSYHPV3yky0wCdqH6AU2OaZmnvsxP7C_1zyHOm9BiQqzjkVByaCNm6-jRATsm16q4ZDNCS5YkwypK3nPxTCBtUspkfqlexGPfZ8bteIvkUsBlNfjtD8cxRN6m_ZEgFp_5-fi4Sjq_kJG4Rv3v43gE6Y_4iayDobiR7_7qp_w2KXMwTlePFm00 "verticle")

      - Normally you would override the `start` method. When Vert.x deploys the verticle it will call it, and when the method has completed the verticle will be considered started.
      You can also optionally override the `stop` method.

      - If you have to do something in your verticle start-up which takes some time and you don’t want the verticle to be considered deployed until that happens (e.g. start HTTP server) you have to implement the asynchronous `start` method (see `Step7_SimpleServer` e `Step8_WebService`). This version of the method takes a `Future` as a parameter. When the method returns the verticle will not be considered deployed. Some time later, after you’ve done everything you need to do you can call complete on the Future (or fail) to signal that you’re done.

      - <ins>They are assigned an event loop thread when they are created and the start method is called with that event loop. When you call any other methods that takes a handler on a core API from an event loop then Vert.x will guarantee that those handlers, when called, will be executed on the same event loop.</ins>

    - see `Step2_WithVerticle.java`
      - unlike previous example everything inside the verticle is executed by event-loop thread: `Thread[#23,vert.x-eventloop-thread-0,5,main]`
      - consequence: the handlers are **always** executed **after** the message `"done"` has been printed on console
      - :point_right: **Pros**: 
        - clarifies, making explicit, who executes what since using Verticles it is guaranted that all the code in your verticle instance is always executed on the same event loop
        - write all the code in your application as single threaded and let Vert.x worry about the threading and scaling: no more worrying about synchronized and volatile any more, ...

  - An application would typically be composed of many verticle instances running in the same Vert.x instance at the same time. 

- `Step3_Chaining.java`: `compose` can be used for chaining futures
  - `Future` offers more: `map`, `recover`, `otherwise`, `andThen` and even a `flatMap` which is an alias of `compose`

- `Step4_Promises.java`

  ```java
  Promise<String> promise = Promise.promise();
  legacyGreetAsync(promise);
  Future<String> greeting = promise.future();
  ```

  - [`Future`s](https://vertx.io/docs/apidocs/io/vertx/core/Future.html) represent the **"read-side" of an asynchronous result**
  - [`Promise`s](https://vertx.io/docs/apidocs/io/vertx/core/Promise.html) are the **"write-side"**, **allowing you to defer the action of providing a result**.

- `Step5_Composition.java`
  - `CompositeFuture.all` takes several futures arguments (up to 6) and returns a future that is succeeded when all the futures are succeeded and failed when at least one of the futures is failed
  - `CompositeFuture.any` takes several futures arguments (up to 6) and returns a future that is succeeded when one of the futures is, and failed when all the futures are failed
  - `CompositeFuture.join` takes several futures arguments (up to 6) and returns a future that is succeeded when all the futures are succeeded, and failed when all the futures are completed and at least one of them is failed

- `Step6_WithBlocking.java`
  - in an idyllic world all APIs will be written asynchronously but in the real world, at least in the JVM standard library (but also in lot of other libraries), there are synchronous APIs with blocking behavior (see JDBC API)
  - But we cannot call blocking operations directly from an event loop...
    - (and we cannot rewrite every single library, of course)
  - The way to deal with this problem is by calling `executeBlocking` specifying both the blocking code to execute and a result handler to be called back asynchronous when the blocking code has been executed

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
  - An alternative way to run blocking code is to use a worker verticle: like a standard verticle, but it’s executed using a thread from the Vert.x worker thread pool, rather than using an event loop.
    - see [documentation](https://vertx.io/docs/apidocs/io/vertx/core/WorkerExecutor.html)

- [**Event Bus**](https://vertx.io/docs/apidocs/io/vertx/core/eventbus/EventBus.html)
  - There is a single event bus instance for every Vert.x instance 
    - it is obtained using the method `getVertx().eventBus()`
  - The event bus allows different parts of your application to communicate with each other, irrespective of what language they are written in, and whether they’re in the same Vert.x instance, or in a different Vert.x instance.
  - The event bus supports publish/subscribe, point-to-point, and request-response messaging.
  - **publish/subscribe messaging pattern** with best-effort delivery.
    - JSON as common standard for messages
  - to register an handler:

    ```java
    eventBus.consumer("news.uk.sport", message -> { ... });
    ```

  - to publish messages:

    ```java
    eventBus.publish("news.uk.sport", "Yay! Someone kicked a ball");
    ```

    - `send` is a particular case of `publish` exploiting P2P messaging pattern: only one handler registered at the address receives the message

      ```java
      eventBus.send("news.uk.sport", "Yay! Someone kicked a ball");
      ```

  - Messages can contain headers to be specified with `addHeader` methods.
  - Vert.x will deliver messages to any particular handler in the same order they were sent from any particular sender.
  - ACK: to acknowledge that the message has been processed, the consumer can reply to the message by calling `reply`.
    The receiver:

    ```java
    MessageConsumer<String> consumer = eventBus.consumer("news.uk.sport");
    consumer.handler(message -> {
        System.out.println("I have received a message: " + message.body());
        message.reply("how interesting!");
    });
    ```

    The sender:

    ```java
    eventBus
        .request("news.uk.sport", "Yay! Someone kicked a ball across a patch of grass")
        .onComplete(ar -> {
            if (ar.succeeded()) {
                System.out.println("Received reply: " + ar.result().body());
            }
        });
    ```

    Uses examples:
    - A simple message consumer which implements a service which returns the time of the day would acknowledge with a message containing the time of day in the reply body
    - A message consumer which implements a persistent queue, might acknowledge with true if the message was successfully persisted in storage, or false if not.
    - A message consumer which processes an order might acknowledge with true when the order has been successfully processed so it can be deleted from the database
  - When sending a message with a reply handler, you can specify a timeout in the `DeliveryOptions`
  - The event bus supports sending arbitrary objects over the event bus. You can do this by defining a `codec` for the objects you want to send.
    - [`MessageCoded`](https://vertx.io/docs/apidocs/io/vertx/core/eventbus/MessageCodec.html): interface allowing a custom message type to be (un)marshalled across the event bus.
    - see [documentation](https://vertx.io/docs/vertx-core/java/#_message_codecs)

### References

- [Main documentation](https://vertx.io/docs/vertx-core/java/)
- [JavaDoc](https://vertx.io/docs/apidocs/overview-summary.html)
