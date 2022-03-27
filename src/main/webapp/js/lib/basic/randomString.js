function randomString(length) {
    let result = "";
    let chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    for(let i = 0; i < length; i++)
        result += chars[Math.floor(Math.random() * chars.length)];
    return result;
}