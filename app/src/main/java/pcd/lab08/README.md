# Lab08

## Reactive programming (recalls)

Quintessence of **Reactive Paradigm**:
> Providing abstractions to express programs as _reactions_ to external events having the language automatically manage the flow of time (by conceptually supporting simultaneity), and data and computation dependencies.

This facilitates the declarative development of event-driven applications because developers can **express** programs in terms of **what to do and let the language automatically manage when to do it**.

:star: <ins>Key point</ins> Everything is built around the notion of **continuous time-varying values and propagation of change**: state changes are automatically and efficiently propagated across the network of dependent computations **by the underlying execution model**.

## RxJava

ReactiveX is a library for composing asynchronous and event-based programs by using observable sequences of events (new value available, error, completed) ordered in time.

It extends the observer pattern to support sequences of data and/or events and adds operators that allow you to compose sequences together declaratively while abstracting away concerns about things like low-level threading, synchronization, thread-safety, concurrent data structures, and non-blocking I/O.

<ins>Observer pattern abstractions:</ins>

- **observable**: entity emitting any number of signals/events, then terminating either by completing or due to an error
  - [**`ObservableSource<T>`**](https://reactivex.io/RxJava/3.x/javadoc/io/reactivex/rxjava3/core/ObservableSource.html) functional interface with `subscribe(Observer<? super T> observer)` method 
  - [**`Observable<T>`** : **`ObservableSource<T>`**](https://reactivex.io/RxJava/3.x/javadoc/io/reactivex/rxjava3/core/Observable.html): abstract class that offers factory methods, intermediate operators and the ability to consume synchronous and/or asynchronous reactive dataflows, **allowing one or more subscribers to react to any events in real time**
  - two kinds of observables:
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

You can think of the `Observable` class as a “push” equivalent to `Iterable`, which is a “pull”. With an `Iterable`, the consumer pulls values from the producer and the thread blocks until those values arrive. By contrast, with an `Observable` the **producer pushes values to the consumer whenever values are available**. This approach is more flexible, because values can arrive **synchronously** or **asynchronously**.

- `Test02a_Creation_Synch.java`
  - use example of a **cold** observable that start emitting values only when it has at least one subscriber
  - <ins>elements are emitted lazily</ins>, **an observable doesn’t do anything until it receives a subscription; it’s the subscription that triggers an observable to begin emitting events, up until it emits an error or completed event and is terminated: `A` and `source` prints are alternated.**
- `Test02b_Creation_Async.java`
  - _fancy_ **not recommended** way of creating a `Flowable` in an async manner: now the emission and the print of the values is in charge of a different `Thread` instance.
  - **By default, an `Observable` and the chain of operators that you apply to it will do its work, and will notify its observers, on the same thread on which its `Subscribe` method is called.**
- `Test02d_CreationHot.java`
  - use example of a **hot** observable
  - `publish` method returns a `ConnectableObservable`, which is a variety of `ObservableSource` that waits until its connect method is called before it begins emitting items to those `Observers` that have subscribed to it
    ```java
    ConnectableObservable<Integer> hotObservable = source.publish();
    ```
- ...

ReactiveX provides a [collection of operators](https://reactivex.io/documentation/operators.html) with which you can filter, select, transform, combine, and compose Observables. This allows for efficient execution and composition.

:red_circle: **Streams are immutable**

**[Scheduler]s** allow to control the execution of the cascade of `Observable` operators on threads.
Some ReactiveX Observable operators have variants that take a `Scheduler` as a parameter. These instruct the operator to do some or all of its work on a particular `Scheduler`.

The `SubscribeOn` operator specify a different `Scheduler` on which the `Observable` should operate. 
The `ObserveOn` operator specifies a different `Scheduler` that the `Observable` will use to send notifications to its observers.


Available schedulers/scheduling policies:
- `Schedulers.computation()`: run computation intensive work on a fixed number of dedicated threads in the background. Most asynchronous operator use this as their default Scheduler.
- `Scheduler.io()`: Run I/O-like or blocking operations on a dynamically changing set of threads
- `Schedulers.single()`: Run work on a single thread in a sequential and FIFO manner
- In addition, there is option to wrap an existing Executor (and its subtypes such as ExecutorService) into a Scheduler via Schedulers.from(Executor). This can be used, for example, to have a larger but still fixed pool of threads (unlike computation() and io() respectively)

... TODO: more on backpressure ...
