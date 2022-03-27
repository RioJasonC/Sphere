// PARALLAX
let scene = document.getElementById("scene");
let parallaxInstance = new Parallax(scene);

//LOGIN
let loginPresentStatus = true; //"true" refers to login form, "false" refers to signup form
let loginSwitchIsDone = false; //whether mouse up
let tempLoginBtnStyleProps;
let publicKey;
let rootUrl = "/Sphere";

//Link to Remote Server and get the session
$.ajax({
    url: rootUrl + "/lts",
    type: "post",
    success: function (response) {
        if(response["Status"] === "Success") {
            if (response["ExistStatus"] === "New") {
                $.cookie("PublicKey", response["PublicKey"], {path: "/"});
                publicKey = response["PublicKey"];
            }
            else {
                publicKey = $.cookie("PublicKey");
            }
        }
        else {
            $(window).attr("location", rootUrl + "/function/text/output?text=" + response["ErrorType"]);
        }
    },
    error: function () {
        $(window).attr("location", rootUrl + "/function/text/output?text=Internal Server Error");
    }
});

//Login Status Check
$.ajax({
    url: rootUrl + "/lsc",
    type: "post",
    data: JSON.stringify({
        id: $.cookie("id")
    }),
    dataType: "json",
    success: function (response) {
        if(response["Status"] === "Success") {
            $(window).attr("location", rootUrl + "/home.html");
        }
        else {
            $.removeCookie("id");
        }
    },
    error: function () {
        $(window).attr("location", rootUrl + "/errorLog?" + "500");
    }
});

$(document).ready(function() {
    console.log("欢迎访问yyx的小站，如想注册账户，可以联系本人获取CDKey。");
    let switchBtn = $(".switch-btn");

    switchBtn.hover(
        function() {
            tempLoginBtnStyleProps = $(this).css([
                "color",
                "background-color",
                "transition",
                "-moz-transition",
                "-webkit-transition",
                "-o-transition"
            ]);

            $(this).css({
                "color": "white",
                "background-color": "dodgerblue"
            });
        },
        function() {
            if(loginSwitchIsDone) {
                loginSwitchIsDone = false;
                return;
            }

            let This = $(this);
            $.each(tempLoginBtnStyleProps, function(prop, value) {
                This.css(prop, value);
            });
        });

    switchBtn.mousedown(function() {
        if(loginSwitchIsDone)
            loginSwitchIsDone = false;

        $(this).css({
            "color": "dodgerblue",
            "background-color": "rgb(200, 200, 200)",
            "transition": "all 0s",
            "-moz-transition": "all 0s",
            "-webkit-transition": "all 0s",
            "-o-transition": "all 0s"
        });
    });

    switchBtn.mouseup(function() {
        loginSwitchIsDone = true;

        if(loginPresentStatus) {
            if($(this).attr("id") === "switch-btn-left") {
                let This = $(this);
                $.each(tempLoginBtnStyleProps, function(prop, value) {
                    This.css(prop, value);
                });
                return;
            }
            else { //From login form to signup form
                loginPresentStatus = false;

                $(this).css({
                    "color": "white",
                    "background-color": "dodgerblue",
                    "transition": "all 0.5s",
                    "-moz-transition": "all 0.5s",
                    "-webkit-transition": "all 0.5s",
                    "-o-transition": "all 0.5s"
                });
                $("#switch-btn-left").css({
                    "color": "dodgerblue",
                    "background-color": "white",
                });
                $("#login-center-id").attr("placeholder", "用户名");
                let text = $("#login-center-input-id-text");
                text.empty();
                text.append("用户名");
                $(".login-center-cdkey").fadeIn(200);
                $(".login-center-input-btn").text("注册");
            }
        }
        else {
            if($(this).attr("id") === "switch-btn-right") {
                let This = $(this);
                $.each(tempLoginBtnStyleProps, function(prop, value) {
                    This.css(prop, value);
                });
                return;
            }
            else { //From signup form to log in form
                loginPresentStatus = true;

                $(this).css({
                    "color": "white",
                    "background-color": "dodgerblue",
                    "transition": "all 0.5s",
                    "-moz-transition": "all 0.5s",
                    "-webkit-transition": "all 0.5s",
                    "-o-transition": "all 0.5s"
                });
                $("#switch-btn-right").css({
                    "color": "dodgerblue",
                    "background-color": "white",
                });
                $("#login-center-id").attr("placeholder", "用户名或id");

                let text = $("#login-center-input-id-text");
                text.empty();
                text.append("用户名或id");
                $(".login-center-cdkey").fadeOut(200);
                $(".login-center-input-btn").text("登录");
            }
        }

        tempLoginBtnStyleProps = $(this).css([
            "transition",
            "-moz-transition",
            "-webkit-transition",
            "-o-transition"
        ]);
        tempLoginBtnStyleProps["color"] = "white";
        tempLoginBtnStyleProps["background-color"] = "dodgerblue";
    });

    $("#login-center-id").blur(function() {
        if(loginPresentStatus)
            $(this).attr("placeholder", "用户名或id");
        else
            $(this).attr("placeholder", "用户名");
    });

    $(".login-center-input-btn").mouseup(function() {
        /*Form data judge*/
        let loginCenterId = $("#login-center-id").val();
        let password = $("#login-center-password").val();
        let continueStatus = true;

        if(loginPresentStatus === true) { //Login
            if(loginCenterId.length === 0) { //Id Error
                $.growl.error({title: "提醒", message: "请输入用户名或id", location: "tc"});
                continueStatus = false;
            }
            if(password.length === 0) { //Password Error
                $.growl.error({title: "提醒", message: "请输入密码", location: "tc"});
                continueStatus = false;
            }
        }
        else { //Sign Up
            let cdkey = $("#login-center-cdkey");

            if(loginCenterId.length === 0) { //Id Error
                $.growl.error({title: "异常", message: "请输入用户名或id", location: "tc"});
                continueStatus = false;
            }
            if(password.length === 0) { //Password Error
                $.growl.error({title: "异常", message: "请输入密码", location: "tc"});
                continueStatus = false;
            }
            if(cdkey.val().length !== 19) { //CDKey Length Error
                $.growl.error({title: "异常", message: "CDKey长度错误（期望长度为19，正确格式为AA00-AA00-AA00-AA00）", location: "tc"});
                cdkey.val("");
                continueStatus = false;
            }
            else if(!(/^([A-Z0-9]){4}(-([A-Z0-9]){4}){3}$/.test(cdkey.val()))) { //CDKey Format Error
                $.growl.error({title: "异常", message: "无效的CDkey（正确格式为AA00-AA00-AA00-AA00）", location: "tc"});
                cdkey.val("");
                continueStatus = false;
            }
        }
        if(!continueStatus)
            return;

        /*Link to the Remote Server*/
        loginProcessAnimate();
        let AESKey = randomString(16);
        password = aesEncrypt(password, AESKey);
        let encryptor = new JSEncrypt();
        encryptor.setPublicKey(publicKey);
        AESKey = encryptor.encrypt(AESKey);

        /*Login or Signup process*/
        setTimeout(function () {
            if (loginPresentStatus) { //Login
                $.ajax({
                    url: rootUrl + "/login",
                    type: "post",
                    data: JSON.stringify({
                        "id": loginCenterId,
                        "password": password,
                        "AESKey": AESKey
                    }),
                    dataType: "json",
                    success: function (response) {
                        $("#login-center-id").val("");
                        $("#login-center-password").val("");
                        if (response["Status"] === "Success") {
                            $.cookie("id", response["id"], {path: "/"})
                            loginProcessAnimateExit();
                            $.growl.notice({title: "提醒", message: "登录成功，页面跳转中...", location: "tc"});
                            setTimeout(function () {
                                $(window).attr("location", rootUrl + "/function/text/output?text=登录成功");
                            }, 1000);
                        }
                        else {
                            loginProcessAnimateExit();
                            $.growl.error({title: "异常", message: response["ErrorType"], location: "tc"});
                        }
                    },
                    error: function () {
                        $.growl.error({title: "异常", message: "Internal Server Error - Login Check Failed", location: "tc"});
                        loginProcessAnimateExit();
                    }
                });
            }
            else { //Sign Up
                let cdkey = $("#login-center-cdkey").val();
                $.ajax({
                    url: rootUrl + "/signup",
                    type: "post",
                    data: JSON.stringify({
                        "id": loginCenterId,
                        "password": password,
                        "cdkey": cdkey,
                        "AESKey": AESKey
                    }),
                    dataType: "json",
                    success: function (response) {
                        $("#login-center-id").val("");
                        $("#login-center-password").val("");
                        $("#login-center-cdkey").val("");
                        if (response["Status"] === "Success") {
                            loginProcessAnimateExit();
                            $.growl.notice({title: "提醒", message: "注册成功，页面跳转中...", location: "tc"});
                            setTimeout(function () {
                                $(window).attr("location", rootUrl + "/function/text/output?text=注册成功");
                            }, 1000);
                        }
                        else {
                            loginProcessAnimateExit();
                            $.growl.error({title: "异常", message: response["ErrorType"], location: "tc"});
                        }
                    },
                    error: function () {
                        $.growl.error({title: "异常", message: "Internal Server Error - Sign Up Check Failed..."});
                        loginProcessAnimateExit();
                    }
                });
            }
        }, 1000);
    });
});

function loginProcessAnimate() {
    $(".login-center").hide();
    $(".login-mod-btn").hide();
    $(".login").css({
        "width": "100px",
        "height": "100px",
        "left": "78%",
        "top": "40%",
        "animation-name": "loginProcessAnimation",
        "animation-duration": "2s",
        "animation-iteration-count": "infinite",
        "-webkit-animation-name": "loginProcessAnimation",
        "-webkit-animation-duration": "2s",
        "-webkit-animation-iteration-count": "infinite"
    });
}

function loginProcessAnimateExit() {
    $(".login").css({
        "width": "",
        "height": "",
        "left": "",
        "top": "",
        "animation-name": "",
        "animation-duration": "",
        "animation-iteration-count": "",
        "-webkit-animation-name": "",
        "-webkit-animation-duration": "",
        "-webkit-animation-iteration-count": ""
    });
    $(".login-center").show();
    $(".login-mod-btn").show();
}