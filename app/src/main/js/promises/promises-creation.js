/**
 * A simple function returning a promise which is resolved or rejected
 * after `delay` secs, depending on an extracted random number is greater
 * or not the given threshold.
 */
function delayWithRandom(delay, threshold) {
    return new Promise((resolve, reject) => {
        setTimeout(() => {
            let r = Math.random()
            if (r > threshold) {
                resolve(r);
            } else {
                reject(r);
            }
        }, delay)
    })
}

delayWithRandom(1000, 0.5).then((res) => {
    console.log("Successful result = " + res);
    return 2 * res;
}, (rej) => {
    console.log("Rejected due to = " + rej);
})