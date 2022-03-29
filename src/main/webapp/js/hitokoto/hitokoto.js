let myHitokotoMaxId = -1;
let myHitokotoScale = -1;

getMyHitokotoMaxId();
setTimeout(function () {
    myHitokotoScale = myHitokotoMaxId / (54.0 + myHitokotoMaxId);
}, 200);

function getHitokoto() {
    let result = {Status: "Success"};
    $.ajax({
        url: "https://v1.hitokoto.cn/",
        type: "post",
        data: JSON.stringify({
            encode: "json",
            charset: "utf8"
        }),
        dataType: "json",
        success: function (response) {
            if(response["from_who"] === "null")
                response["from_who"] = null;
            if(response["from"] === "null")
                response["from"] = null;
            $.extend(result, response);
        },
        error: function () {
            result = {Status: "Error"};
        }
    });
    return result;
}

function getMyHitokoto(id) {
    let result = {"Status": "Success"};
    $.ajax({
        url: rootUrl + "/function/hitokoto/get",
        type: "post",
        data: JSON.stringify({
            mode: "getHitokoto",
            id: id
        }),
        dataType: "json",
        success: function (response) {
            $.extend(result, response);
        },
        error: function () {
            result = {Status: "Error", ErrorType: "Server Internal Error"};
        }
    });
    return result;
}

function getMyHitokotoMaxId() {
    $.ajax({
        url: rootUrl + "/function/hitokoto/get",
        type: "post",
        data: JSON.stringify({
            mode: "getMaxId"
        }),
        dataType: "json",
        success: function (response) {
            myHitokotoMaxId = response["data"];
        },
        error: function () {
            myHitokotoMaxId = -1;
        }
    });
}