package pcd.coroutines

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
//    launch { doWork() }
    doAnotherWork()
    println("[main] Done")
}

// Regular function that can use suspending functions (like delay)
// to suspend the execution of a coroutine
suspend fun doWork() {
    delay(1_000L)
    println("Hello World :)")
}

// Structured concurrency: new coroutines can be only launched in a specific
// CoroutineScope which delimits the lifetime of the coroutine.
// `coroutineScope` is a way of creating a coroutine scope and does not complete
// until all launched children complete (also `runBlocking` establishes the
// corresponding scope)
suspend fun doAnotherWork() = coroutineScope {
    launch { // executed concurrently
        delay(2_000L)
        println("Hello World!")
    }
    val lifeJob = launch { // executed concurrently
        delay(1_000L)
        println("Life is funny :)")
    }
    println("Welcome")
    lifeJob.join() // wait until child coroutine completes
    println("Life Job completed")
}
