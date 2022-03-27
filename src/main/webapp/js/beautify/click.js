$(document).ready(function ($) {
    let id = -1;
    let textArray = [
        "太陽の傾いたこの世界で", "空の上の森の中の", "この戦いが終わったら", "帰らぬ者と、待ち続けた者たち",
        "誰も彼もが、正義の名のもとに", "消えない過去、消えていく未来", "ただいま帰りました", "いずれその陽は落ちるとしても",
        "たとえ未来が見えなくても", "いまこの時の輝きを", "どうか、忘れないで", "世界で一番幸せな女の子"
    ];
    $("body").click(function (e) {
        id = (id + 1) % textArray.length;
        let $i = $("<span></span>").text(textArray[id]);
        let x = e.pageX;
        let y = e.pageY;
        let sum = 0;
        for(let q = 0; q < textArray[id].length; q++) {
            sum += textArray[id].charCodeAt(q) > 255 ? 2 : 1;
        }

        $i.css({
            "z-index": 999999999,
            "top": y - 16,
            "font-family": "serif",
            "font-size": "16px",
            "left": x - sum * 4.25,
            "position": "absolute",
            "font-weight": "bold",
            "color": "rgb(" + ~~(255 * Math.random()) + "," + ~~(255 * Math.random()) + "," + ~~(255 * Math.random()) + ")",
            "-webkit-user-select": "none",
            "-moz-user-select": "none",
            "-ms-user-select": "none",
            "user-select": "none"
        })
        $("body").append($i);
        $i.animate({
            "top": y - 150,
            "opacity": 0
        },
        2000,
        function () {
            $i.remove();
        });
    });
});