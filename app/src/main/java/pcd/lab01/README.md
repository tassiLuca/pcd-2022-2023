# Lab01

## Multithreaded programming in Java

- Java has been the first mainstream programming language to provide native support for concurrent programming
  - “conservative approach”: everything is still an object
  - mechanisms for concurrency
- Extended with the `java.util.concurrent` library (JSR 166 and JSR 236) to provide a higher level of support to concurrent programming
  - semaphores, locks, synchronizers, etc
  - task frameworks

- Java provides a basic API for defining new types of threads, and for dynamically creating and (partially) managing thread execution
  - **Platform threads**: are typically mapped 1:1 to kernel threads scheduled by the operating system.
    - usually have a large stack and other resources that are maintained by the operating system
    - are suitable for executing all types of tasks but may be a limited resource
  - **Virtual threads (from Java19)**: are typically user-mode threads scheduled by the Java runtime rather than the operating system
    - require few resources and a single Java virtual machine may support millions of virtual threads
    - <ins>are suitable for executing tasks that spend most of the time blocked, often waiting for I/O operations to complete</ins>
    - <ins>are not intended for long-running CPU-intensive operations</ins>
- A thread is represented by the abstract class [`Thread`](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/Thread.html#stop())
  - a concrete thread can be defined by extending `Thread` class and implementing the `run` method, which defines the behavior of the thread
  - an alternative approach is provided to define a thread, based on `Runnable` interface, useful when the class used to implement the thread belongs to some class hierarchy already extending some class, which is not `Thread` (Java does not support multiple inheritance)
  - to start thread asynchronous execution, the method `start` is provided: it returns immediately, and a new activity executing what is specified in the `run` method is launched
- The thread terminates as soon as the execution of the method run

![thread-api](http://www.plantuml.com/plantuml/svg/bLNRKjim47ttLwX-0eFz0NuYaBIJAQGT2CDBPpgAlOb5PCcZ2o73yEzTonVPO4hhbqIxPrVdhBJQ2sseChRan1GAQ1xJYWM11aC4BK5ND8CxgAYYHYeYf80WGDkUxe0yqYUQS2fsv2KW-35XG6qnWDnQ8UY6G-kelpDbnScfMxu6xaT80uJ3rX2vxIDIyjMAp30fA2VBFg4fg26wa9RdP0Fabs1bnQ441A3XIAWuaenJhScXaDe110eJ6MX25JXg8aUiaKqyF4Dck-1MUjBmQlAidOdq--X9KGsgrJMqO55tOD8-w7F4kIjuIkwCOcBt7Z_jxV-ye5p3ibToHh5yJt0bhJ0dfsajVGAKMQLGLSS_RVLE87DRLdS6Ztoe4falNHYNOkVDU6rMIFCDiZ5rt4Veduf9nSpnHC2X--yeZpFqi3TwQXzoC-0L96xj4UvIAbkP0IalhlU2KxEHaWCLWsYz_1GnurZk3tnp2gKKayueUfGCNL7qpbOopfbkYZlo-nvg2iW1gZ5wcCjrzdY3EDP6Ld4yp88sgojiZxTc1TgMq5kdshQfUPg6ZbdOHqVHNDP8Oh4PiUSSbG6HA385T6qtijzZG1YsrCEGr7qy025Dn-0lLBn6psSEbO8CBqR-nRVCkc2Qb60AYNsklPs90khcu4TYXQPRmESbz-wI7d5JwjuI9Z8fD4EAmdX4VRtgXsSOHTM66KNL4T-VWZunQLNo7uhy2Ks9mgum7qd8CGUNsJwk5BxUN_vzJt_yqrl2C-hMHEEzncrwo1RdH7tg1sQA-YGRvkuiUyoXO5b12ggn622GOfTrpAvTsNBKsuFtTxLuEEyNj_UhrUpoUk4jbzS_hhulvjxmC5kkbwjlth1UtYpclz-R5xStozLilPWFRtux4c9RnUF71ltDqBWvFaYr1uMrgUTDDqLwxvhg_TH82LfNaYGaR0P3sCK65vW9v_CV "thread-api")


```java
public class MyWorker extends Thread {

  public MyWorker(String name) {
    super(name);
  }

  public void run() { 
    // ...
    // <active behavior>
    // ...
  }
}
```

```java
public class MyTask implements Runnable {
  public void run() {
    // ...
    // <active behavior>
    // ...
  }
}

Thread th = new Thread(new MyTask());
th.start();
```
- :warning: Some important notes:
  - the method to execute the thread object is `start()`, **not** `run()`
    - If `run()` method is called directly instead of `start()`, `run()` method will be treated as a normal overridden method of the thread class (or runnable interface), just like a passive object. This run method will be executed within the context of the current thread, not in a new thread.
    - It’s the `start()` method that spawns a new thread and schedules the thread with the JVM. The JVM will let the newly spawned thread execute `run()` method when the resources and CPU are ready.
  - All the public methods to asynchronously act on the control flow of the thread have been deprecated (see [Java Thread Primitive Deprecation](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/doc-files/threadPrimitiveDeprecation.html) -- the reasons described here will be completely clear in a couple of labs...). The same functionality is achieved through proper patterns, we will see in the following.
- When the Java virtual machine starts up, there is usually one non-daemon thread (the thread that typically calls the application's main method). The Java virtual machine terminates when all started non-daemon threads have terminated.
  - and a lot of other daemon threads: Java RMI, garbage collector, ...

### Implicit synchronization

- By applying the keyword `synchronized` as a qualifier to any code block within any method, only one thread at a time can obtain access to the object where `synchronized` is defined
  - prevents arbitrary interleaving of the actions in the method bodies
  - prevents unintended interactions among threads accessing the same objects

```java
synchronized (<object reference expression>) {     
    // <code block>
}  
```

- Suggestion: **to be used in passive objects that are shared and concurrently accessed (for updates) by multiple threads**.

### Joining Threads

- The `join()` method allows for a thread to synchronize its execution with the termination of another thread
  - in particular: `t.join()` suspends the current thread until the thread `t` has completed its execution
  - see `step03`
  
    `MyWorkerA`:
    ```java
    public void run() {
        println("a1");
        sleepFor(5000);
        println("a2");
    }
    ```

    `MyWorkerB`:
    ```java
    public void run() {
        println("b1");
        println("b2");
        try {
            workerA.join();
        } catch (InterruptedException ex){
            ex.printStackTrace();
        }
        println("b3");
    }
    ```

    Ordine delle stampe: `b3` è garantito sia stampato dopo `a2`.

## Programming Discipline

- Strong conceptual separation between **active** and **passive** entities:
  - active entities as agents that are responsible for accomplishing some tasks $\Rightarrow$ no interfaces
  - passive entities as the objects shared and manipulated by such agents to accomplish such tasks, described by a contract, i.e. their interface
- Viewing threads as <ins>**active objects encapsulating state, behavior and the control of the behavior**</ins>
  - the object's methods should be called only by the thread represented by the object
  - **the use of public methods should be minimized**, ideally no public methods; protected ones are allowed for extendibility's sake
- **Promoting interaction using shared (passive) objects, not by calling public methods of their interface**: this would violate encapsulation of the control flow

## Performances and profiling

### Monitoring

- JConsole is the Java Monitoring and Management Console, a graphical tool shipped in J2SE JDK 5.0 (and later versions)
  - it uses the instrumentation of the Java virtual machine to provide information on the performance and resource consumption of applications running on the Java platform
  - Useful (also) to monitor the thread spawned by running Java programs, including VM threads, such as the one used for garbage collecting
  - simply, `jconsole` on a shell
- Similar to JConsole, VisualVm is a full-fledged profiler that allows for measuring and visualizing the performances of Java programs
  - like JConsole, it uses the instrumentation of the Java virtual machine to provide information on the performance and resource consumption of applications running on the Java platform
  - More fine-grained monitoring than JConsole:
    - monitoring % CPU used by methods, threads
    - monitoring how long a thread is blocked or running
    - ...
- https://stackoverflow.com/questions/27406200/visualvm-thread-states
- TO FINISH!