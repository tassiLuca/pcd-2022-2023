function delayWithRand(delay) {
    return new Promise(resolve => {
        setTimeout(() => resolve(Math.random()), delay);
    });
}

console.log("Now: " + new Date().toISOString());

let myPromise1 = delayWithRand(10_000);
let myPromise2 = delayWithRand(5_000);

let myTotalPromise = Promise.all([myPromise1, myPromise2]);
myTotalPromise.then(values => {
    // expected to enter here after 10 seconds: myPromise1 and myPromise2 are concurrent!
    console.log("At: " + new Date().toISOString() + ": " + values[0] + " - " + values[1]);
})
