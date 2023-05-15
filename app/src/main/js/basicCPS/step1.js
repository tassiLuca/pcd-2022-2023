/** Simulation of an async function using `setTimeout`. */
function asyncFunction(x, f, callback) {
    console.log("async function")
    setTimeout(() => {
        callback(f(x));
    }, 0);
}

/*
 * Parallel execution: the following are executed concurrently
 * ^^^^^^^^^^^^^^^^^^
 */
asyncFunction(10, x => {
    console.log("Started task 1");
    return x + 1;
}, res => console.log("Result task 1 = " + res));

asyncFunction(11, x => {
    console.log("Started task 2");
    return x * 2;
}, res => console.log("Result 2 = " + res));
