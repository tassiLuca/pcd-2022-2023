function wasteTime() {
    for (let i = 0; i <= 1_000_000; i++) {
        for (let j = 0; j < 10_000; j++) {
            Math.sinh(i) * Math.acos(j);
        }
    }
}

/** A simulation async function */
function asyncFunction(x, f, callback) {
    console.log("async function")
    wasteTime();
    setTimeout(() => {
        callback(f(x));
    }, 0);
}

asyncFunction(10, x => {
    console.log("Started task 1");
    x += 1;
}, res => console.log("Result task 1 = " + res));

asyncFunction(11, x => {
    console.log("Started task 2");
    x += 1;
}, res => console.log("Result 2 = " + res));
