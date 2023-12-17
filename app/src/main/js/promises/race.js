function delayWithRand(delay) {
    return new Promise(resolve => {
        setTimeout(() => resolve(Math.random()), delay);
    });
}

console.log("Now: " + new Date().toISOString());

let myPromise1 = delayWithRand(10_000);
let myPromise2 = delayWithRand(5_000);

let myTotalPromise = Promise.race([myPromise1, myPromise2]);
myTotalPromise.then(value => {
    // expected to enter here after 5 seconds
    console.log("At: " + new Date().toISOString() + ": " + value);
})
