# Lab06

## Executors

Recall:

- **task**: abstract, discrete, independent unit of work uncoupled from the notion of thread
- **Division of Labor pattern (aka divide-et-impera strategy)**
  - conceive tasks as independent activities, **not** depending on state/results/side-effects of other tasks
  - choose a **task execution policy** (objectives: good throughput, good responsiveness, grace-full degradation)

:arrow_right: from JDK 5.0 support for **decoupling task submission from task execution**: tasks are logic unit of work, threads the mechanism by which the task can run <ins>asynchronously</ins>.

- `Executor` interface: an object that executes submitted `Runnable` tasks, providing a way of decoupling task submission from the mechanics of how each task will be run, including details of thread use, scheduling, etc. An Executor is normally used instead of explicitly creating threads.

- TO FINISH!

:warning: **In the Java Executor framework task must be independent**

- e.g. a task cannot wait for another task
- reason: by blocking a task (e.g., on an event semaphore) we are going to block also the physical thread running the task $\rightarrow$ deadlock possibility


<ins>**Outcomes**:</ins>

- simplify program organization
- facilitates error recovery by providing natural transaction boundaries
- promotes concurrency

## Java Virtual Threads
TO FINISH!