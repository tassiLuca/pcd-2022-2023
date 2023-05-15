let x = 1;
setTimeout(() => {
    console.log("x = " + x);
}, 0);
// wasting time computation
let noSenseResultComputation = 0;
for (let i = 0; i <= 100_000; i++) {
    for (let j = 0; j < 10_000; j++) {
        noSenseResultComputation += Math.sinh(i) * Math.acos(j);
    }
}
console.log("The callback has not yet been executed! Indeed... x = " + x)
x = 2;
// here the event loop come up again and the handler is executed