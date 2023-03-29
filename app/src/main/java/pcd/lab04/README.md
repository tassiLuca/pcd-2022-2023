# Lab04

## Implementing monitors in Java

Two basic approaches to developing monitors in Java:

- exploiting low-level Java mechanisms (synchronized, wait, notify, notifyAll)
- exploiting high-level `java.util.concurrent` support

### Explicit synchronization

- `wait()` method: any synchronized method in any object can contain a `wait()` call, which suspend the current thread
  - If the current thread has been interrupted, then the method exits immediately, throwing an `InterruptedException`, otherwise the current thread is blocked
  - The JVM places the thread in the internal and otherwise inaccessible wait set associated with the target object
  - **The synchronization lock for the target object is released, but all other locks held by the thread are retained**
    - A full release is obtained even if the lock is re-entrantly held due to nested synchronized calls on the target object
    - upon later resumption, the lock status is fully restored
  - The timed versions of the wait method, `wait(long msecs)` and `wait(long msecs, int nanosecs)`, take arguments specifying the desired maximum time to remain in the wait set, after which the lock is automatically released.
 
- `notify()` method: one (arbitrarily chosen) thread waiting on the target object is resumed upon invocation of this method (must be contained in a synchronized method or block)
  - If one exists, an arbitrarily chosen thread, say `T`, is removed by the JVM from the internal wait set associated with the target object. <ins>There is no guarantee about which waiting thread will be selected when the wait set contains more than one thread.</ins>
  - `T` must re-obtain the synchronization lock for the target object, which will always cause it to block at least until the thread calling `notify()` releases the lock.
    - It will continue to block if some other thread obtains the lock first.
  - `T` is then resumed from the point of its wait

- `notifyAll()` method: all threads waiting on the target object are resumed upon the invocation of this method on the target object (must be contained in a synchronized method or block)
  - :warning: because they must acquire the lock, threads continue one at a time

:warning: If `Thread.interrupt()` is invoked for a thread suspended in a wait, the same notify mechanics apply, except that after re-acquiring the lock, the method throws an `InterruptedException` and the thread's interruption status is set to false.
If an interrupt and a notify occur at about the same time, there is no guarantee about which action has precedence, so either result is possible (future revisions of JLS may introduce deterministic guarantees about these outcomes).

> An object following the monitor pattern encapsulates all its mutable state and guards it with object’s own intrinsic lock.
>
> Rules:
>
> - every public method must be implemented as synchronized
> - no public fields
> - monitor code must access / use only objects completely confined inside the monitor
> - a single condition variable is available, which is the object itself
>   -  `wait`, `notify`, `notifyAll` as `waitC` and `signalC`




## Exploiting high-level support