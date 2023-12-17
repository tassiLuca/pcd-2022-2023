// Issue2: lost updates | breaking macro step semantic

var x = 0;

function delay(t) {
    return new Promise(resolve => {
        setTimeout(() => resolve(), t);
    })
}

async function doIncrement() {
    let c = x;
    // the control is given back to the caller
    // => the computation is chopped up!
    await delay(1_000);
    x = c + 1;
    console.log("x: " + x);
}

doIncrement();
x = 10;
doIncrement();
