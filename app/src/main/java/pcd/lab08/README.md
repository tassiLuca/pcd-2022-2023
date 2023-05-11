# Lab08

## Reactive programming (recalls)

Quintessence of **Reactive Paradigm**:
> Providing abstractions to express programs as _reactions_ to external events having the language automatically manage the flow of time (by conceptually supporting simultaneity), and data and computation dependencies.

This facilitates the declarative development of event-driven applications because developers can **express** programs in terms of **what to do and let the language automatically manage when to do it**.

:star: <ins>Key point</ins> Everything is built around the notion of **continuous time-varying values and propagation of change**: state changes are automatically and efficiently propagated across the network of dependent computations **by the underlying execution model**.

## RxJava

ReactiveX is a library for composing asynchronous and event-based programs by using observable sequences of events (new value available, error, completed) ordered in time.

It extends the observer pattern to support sequences of data and/or events and adds operators that allow you to compose sequences together declaratively while abstracting away concerns about things like low-level threading, synchronization, thread-safety, concurrent data structures, and non-blocking I/O.

- [**Observable**](https://reactivex.io/RxJava/3.x/javadoc/io/reactivex/rxjava3/core/Observable.html): abstract class offers factory methods, intermediate operators and the ability to consume synchronous and/or asynchronous reactive dataflows
- [**Flowable**](https://reactivex.io/RxJava/3.x/javadoc/io/reactivex/rxjava3/core/Flowable.html): "advanced" observable (see below)

<ins>Observer pattern abstractions:</ins>

- **observable**: entity emitting any number of signals, then terminating either by completing or due to an error
  - 🥶 **cold** observables emit signals only when it has at least one subscriber, providing items in a lazy way.
     
  - 🥵 **hot** emits signals continuously, despite the subscribers.
- **subscriber**: entity consuming signals
  - (un)subscription abstraction used to explicitly represent the (un)link between an observer and a subscriber
  - `subscribe()` + `unsubscribe()` methods
- adds two missing semantics to the GoF's Observer pattern:
  - the ability for the producer to **signal to the consumer that there is no more data available** (`onCompleted` method).
  - the ability for the producer to **signal to the consumer that an error** has occurred (`onError` method). Operators don't have to handle exceptions, only subscribers do! Generate exceptions ar propagated to subscriber:

```java
Flowable.just("Hello, World!")
  .map(s -> potentiallyException(s))
  .map(s -> anotherPotentialException(s))
  .subscribe(new Subscriber<String>(){
      public void onNext(String s) { System.out.println(s) }
      public void onCompleted() { System.out.println("Completed!") }
      public void onError(Throwable e) { System.out.println(Ouch!) }
  })
```

ReactiveX provides a [collection of operators](https://reactivex.io/documentation/operators.html) with which you can filter, select, transform, combine, and compose Observables. This allows for efficient execution and composition.

You can think of the `Observable` class as a “push” equivalent to `Iterable`, which is a “pull”. With an `Iterable`, the consumer pulls values from the producer and the thread blocks until those values arrive. By contrast, with an `Observable` the **producer pushes values to the consumer whenever values are available**. This approach is more flexible, because values can arrive **synchronously** or **asynchronously**.

:red_circle: **Streams are immutable**




