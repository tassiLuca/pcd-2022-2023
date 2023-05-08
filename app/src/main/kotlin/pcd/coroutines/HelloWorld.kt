package pcd.coroutines

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    // `runBlocking` runs a new coroutine and blocks the current thread until its completion
    // This function should not be used from a coroutine.
    repeat(100_000) { // launching a lot of coroutines
        launch { // launch a new coroutine concurrently with the rest of the code
            delay(1000L) // non-blocking delay for 1 second
            // NOTE: suspending a coroutine **does not** block the underlying thread, but allows
            //       other coroutines to run and use the underlying thread for their code
            print(".")
        }
    }
    println("[main] Welcome!") // main coroutine continues
}
