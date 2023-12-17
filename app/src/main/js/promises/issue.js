function delay(t) {
    return new Promise(resolve => {
        setTimeout(() => resolve(), t);
    })
}

for (let i = 0; i < 3; i++) {
    delay(5_000).then(() => { // we are configuring what to do next on completion, we not stop!
        let currentTime = new Date().toISOString();
        console.log("i = " + i + " at " + currentTime);
    })
}