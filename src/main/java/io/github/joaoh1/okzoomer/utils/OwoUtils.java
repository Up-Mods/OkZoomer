package io.github.joaoh1.okzoomer.utils;

import java.util.Random;

import io.github.joaoh1.okzoomer.config.OkZoomerConfigPojo;

//The most humorous part of Ok Zoomer. ^w^
public class OwoUtils {
    public static String[] OWO_ARRAY = new String[]{
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
        "VwV"
    };

    public static void printOwo() {
        if (OkZoomerConfigPojo.tweaks.printOwoOnStart) {
            Random random = new Random();
		    ZoomUtils.modLogger.info("[Ok Zoomer] " + OWO_ARRAY[random.nextInt(OWO_ARRAY.length)] + " what's this");
        }
    }
}