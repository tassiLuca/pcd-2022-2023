function delay(t) {
    return new Promise(resolve => {
        setTimeout(() => resolve(), t);
    })
}

async function f() {
    console.log("Started f()");
    await delay(2_000);
    console.log("done f()");
}

// no async!
function g() {
    console.log("Started g()");
    f();    // await!
    console.log("Done g()");
}

g();
