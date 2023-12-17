function wasteTime() {
    for (let i = 0; i <= 1_000_000; i++) {
        for (let j = 0; j < 20_000; j++) {
            Math.sinh(i) * Math.acos(j);
        }
    }
}

function delay(t) {
    return new Promise(resolve => {
        setTimeout(() => resolve(), t);
    })
}

async function waitTimeAndLog() {
    console.log("Before");
    await delay(1_000);
    console.log("After");
}

waitTimeAndLog().then(res => {
    console.log("async function ended");
});
console.log("In the meanwhile...");
// Warning: again, if there is a long-term computation the console.log("After") is executed only after!
wasteTime();
console.log("Long term computation just ended");
