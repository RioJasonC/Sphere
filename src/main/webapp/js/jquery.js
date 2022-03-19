let tempLoginBtnStyleProps;

$(document).ready(function() {
    console.log("欢迎访问yyx的小站，如想注册账户，可以联系本人获取CDKey。");

    let cookie = $.cookie;
    let cookiePath = "/";

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
        let key = "~";
        let loginCenterId = $("#login-center-id").val();
        $.ajax({
            url: "/Sphere/edcryptKeyGet",
            data: {
                id: loginCenterId
            },
            success: function (data) {
                key = data;
            },
            error: function () {
                alert("Internal Server Error");
            }
        });

        let dataValid = true;

        if(loginPresentStatus === true) { //Login
            let password = $("#login-center-password").val();

            if(loginCenterId.length === 0) { //Id error
                alert("请输入用户名或id");
                dataValid = false;
            }
            if(password.length === 0) { //Password error
                alert("请输入密码");
                dataValid = false;
            }
        }
        else { //Sign up
            let password = $("#login-center-password").val();
            let cdkey = $("#login-center-cdkey").val();

            if(loginCenterId.length === 0) { //Id error
                alert("请输入用户名或id");
                loginFormDataClear();
                dataValid = false;
            }
            if(password.length === 0) { //Password error
                alert("请输入密码");
                loginFormDataClear();
                dataValid = false;
            }
            if(cdkey.length !== 19) { //CDKey length error
                alert("CDKey长度错误（期望长度为19，例子为AA00-AA00-AA00-AA00）");
                loginFormDataClear();
                dataValid = false;
            }
            else if(!(/^([A-Z0-9]){4}(-([A-Z0-9]){4}){3}$/.test(cdkey))) { //CDKey format error
                alert("无效的CDkey（例子为AA00-AA00-AA00-AA00）");
                loginFormDataClear();
                dataValid = false;
            }
        }

        if(dataValid === true) {
            setTimeout(function() {
                if(key === "~")
                    return;

                let password = encrypt($("#login-center-password").val(), key);
                let cdkey = $("#login-center-cdkey").val();

                if(loginPresentStatus) { //Login
                    loginProcessAnimate();

                    $.ajax({
                        url: "/Sphere/login",
                        type: "post",
                        data: JSON.stringify({
                            "id": loginCenterId,
                            "password": password
                        }),
                        dataType: "json",
                        success: function (response) {
                            setTimeout(function () {
                                if(response["ResponseStatus"] === "Success" && response["Status"] === "Success") {
                                    $(window).attr("location", "/Sphere/textOutput?text=登录成功");
                                }
                                else {
                                    $("#login-center-id").val("");
                                    $("#login-center-password").val("");
                                    alert(response["ErrorType"]);
                                } //等待完善

                                setTimeout(loginProcessAnimateExit, 200);
                            }, 1000);
                        },
                        error: function () {
                            setTimeout(function () {
                                alert("Internal Server Error");

                                setTimeout(loginProcessAnimateExit, 200);
                            }, 1000);
                        }
                    });
                }
                else { //Sign up
                    loginProcessAnimate();

                    $.ajax({
                        url: "/Sphere/signup",
                        type: "post",
                        data: JSON.stringify({
                            "id": loginCenterId,
                            "password": password,
                            "cdkey": cdkey
                        }),
                        dataType: "json",
                        success: function (response) {
                            setTimeout(function () {
                                if(response["ResponseStatus"] === "Success" && response["Status"] === "Success") {
                                    $(window).attr("location", "/Sphere/textOutput?text=注册成功");
                                }
                                else {
                                    $("#login-center-id").val("");
                                    $("#login-center-password").val("");
                                    $("#login-center-cdkey").val("");
                                    alert(response["ErrorType"]);
                                } //等待完善

                                setTimeout(loginProcessAnimateExit, 200);
                            }, 1000);
                        },
                        error: function () {
                            setTimeout(function () {
                                alert("Internal Server Error");

                                setTimeout(loginProcessAnimateExit, 200);
                            }, 1000);
                        }
                    });
                }
            }, 20);
        }

    });
});

function loginFormDataClear() { //Clear all the data of login form
    $("#login-center-id").val("");
    $("#login-center-password").val("");
    $("#login-center-cdkey").val("");
}

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