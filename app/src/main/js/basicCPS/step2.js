/** Simulation of an async function using `setTimeout`. */
function asyncFunction(x, f, callback) {
    setTimeout(() => {
        callback(f(x));
    }, 0);
}

/* Sequential! */
asyncFunction(1, x => x + 1, res => {
    console.log("[" + new Date() + "] res = " + res);
    asyncFunction(res, x => x * x, res2 => {
        console.log("[" + new Date() + "] res = " + res2)
    })
})