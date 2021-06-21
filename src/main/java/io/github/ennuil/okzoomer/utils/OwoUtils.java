package io.github.ennuil.okzoomer.utils;

import java.util.Random;

// The most humorous part of Ok Zoomer. ^w^
public class OwoUtils {
    public static String[] OWO_ARRAY = {
        "owo",
        "OwO",
        "uwu",
        "nwn",
        "^w^",
        ">w<",
        "Owo",
        "owO",
        ";w;",
        "0w0",
        "QwQ",
        "TwT",
        "-w-",
        "$w$",
        "@w@",
        "*w*",
        ":w:",
        "°w°",
        "ºwº",
        "ówò",
        "òwó",
        "`w´",
        "´w`",
        "~w~",
        "umu",
        "nmn",
        "own",
        "nwo",
        "ùwú",
        "úwù",
        "ñwñ",
        "UwU",
        "NwN",
        "ÙwÚ",
        "PwP",
        "own",
        "nwo",
        "/w/",
        "\\w\\",
        "|w|",
        "#w#",
        "<>w<>",
        "'w'",
        "\"w\"",
        "öwö",
        "ôwô",
        "ÖwÖ",
        "ÔwÔ",
        ".w.",
        "+w+",
        ")w(",
        "]w[",
        "}w{",
        "_w_",
        "=w=",
        "!w!",
        "YwY",
        "vwv",
        "VwV",
        "<w>",
        "çwç",
        "ÇwÇ"
    };

    public static void printOwo() {
        Random random = new Random();
		ZoomUtils.modLogger.info("[Ok Zoomer] " + OWO_ARRAY[random.nextInt(OWO_ARRAY.length)] + " what's this");
    }
}