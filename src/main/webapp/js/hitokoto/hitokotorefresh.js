let hitokotoCanvas;
let hitokotoTime = 0;
let hitokotoTimeInterval = 12000;

function hitokotoRefresh() {
    if(hitokotoTime !== 0) {
        clearInterval(intervalData["hitokotoStatus"]);
    }
    let mode = Math.random();
    let hitokotoData = {Status: "Success"};

    // Personal hitokoto
    if(mode <= myHitokotoScale && myHitokotoMaxId > 0) {
        let temp = getMyHitokoto(Math.floor(Math.random() * myHitokotoMaxId + 1));
        setTimeout(function () {
            if(temp["Status"] === "Error") {
                console.log("getMyHitokoto: " + temp["ErrorType"]);
                hitokotoData = {Status: "Error"};
            }
            else {
                $.extend(hitokotoData, temp);
            }
        }, 300);
    }
    // Hitokoto hitokoto
    else {
        hitokotoData = getHitokoto();
    }

    setTimeout(function() {
        // Personal hitokoto
        if(hitokotoData["Status"] === "Error" || hitokotoData["hitokoto"] === null) {
            let temp = getMyHitokoto(Math.floor(Math.random() * myHitokotoMaxId + 1));
            if(temp["Status"] === "Error") {
                console.log("getMyHitokoto: " + temp["ErrorType"]);
                hitokotoData = {Status: "Error"};
            }
            else {
                $.extend(hitokotoData, temp);
            }
        }

        setTimeout(function () {
            if(hitokotoData["hitokoto"] !== null && (hitokotoData["from_who"] !== undefined || hitokotoData["from"] !== undefined)) {
                $(".hitokoto-word").html(hitokotoData["hitokoto"]);
                $(".hitokoto-author").html("<div style='user-select: none; float: left'>—— </div>" + (hitokotoData["from_who"] === null ? "" : hitokotoData["from_who"])+ (hitokotoData["from"] === null ? "" : ("「" + hitokotoData["from"]) + "」"));
            }
            else {
                $(".hitokoto-word").html("お可愛いこと");
                $(".hitokoto-author").html("<div style='user-select: none; float: left'>—— </div>" + "しのみや かぐや"); // + "「" + "" + "」"
            }
            hitokotoStatusInit();
            hitokotoTime = 0;
            hitokotoStatusRefreshStart();
        }, 200);
    }, 300);
}

function hitokotoStatusInit() {
    hitokotoCanvas = document.getElementById("hitokoto-refresh-canvas");
    let context = hitokotoCanvas.getContext("2d");
    context.clearRect(0, 0, hitokotoCanvas.width, hitokotoCanvas.height);
    context.beginPath();
    context.strokeStyle = "dodgerblue";
    context.lineWidth = 5;
    context.arc(15, 15, 9, 0, 2 * Math.PI);
    context.stroke();
    context.closePath();
}

function hitokotoStatusRefreshStart() {
    intervalData["hitokotoStatus"] = setInterval(function () {
        let context = hitokotoCanvas.getContext("2d");
        context.beginPath();
        context.strokeStyle = "white";
        context.lineWidth = 5;
        hitokotoTime += 100;
        context.arc(15, 15, 9, 0, 2 * Math.PI * hitokotoTime / hitokotoTimeInterval);
        context.stroke();
        context.closePath();

        if(hitokotoTime === hitokotoTimeInterval) {
            hitokotoRefresh();
        }
    }, 100);
}