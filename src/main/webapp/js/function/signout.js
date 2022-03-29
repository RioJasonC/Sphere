function signout() {
    $.removeCookie("id", {path: "/"});
    setTimeout(function () {
        location.replace(rootUrl);
    }, 500);
}