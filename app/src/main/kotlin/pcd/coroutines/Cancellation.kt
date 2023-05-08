package pcd.coroutines

import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val job = launch {
        repeat(1_000) {
            println("[job] I'm sleeping $it...")
            delay(3_000L)
        }
    }
    delay(13_000L)
    println("[main] I'm tired of waiting :)")
    // job.cancel()
    // job.join()
    job.cancelAndJoin() // cancels the job and waits for its completion
    println("[main] Quitting!")
}
