package io.github.ennuil.okzoomer.utils;

import java.util.Random;

// The most humorous part of Ok Zoomer ^w^
public class OwoUtils {
    public static final String[] OWO_ARRAY = {
        "owo", "OwO", "uwu", "nwn", "^w^", ">w<", "Owo", "owO",
        ";w;", "0w0", "QwQ", "TwT", "-w-", "$w$", "@w@", "*w*",
        ":w:", "°w°", "ºwº", "ówò", "òwó", "`w´", "´w`", "~w~",
        "umu", "nmn", "own", "nwo", "ùwú", "úwù", "ñwñ", "UwU",
        "NwN", "ÙwÚ", "PwP", "own", "nwo", "/w/", "\\w\\", "|w|",
        "#w#", "<>w<>", "'w'", "\"w\"", "öwö", "ôwô", "ÖwÖ", "ÔwÔ",
        ".w.", "+w+", ")w(", "]w[", "}w{", "_w_", "=w=", "!w!",
        "YwY", "vwv", "VwV", "<w>", "çwç", "ÇwÇ", ">w>", "<w<",
        "—w—", "→w→", "→w←", "←w←"
    };

    public static void printOwo() {
        Random random = new Random();
		ZoomUtils.LOGGER.info(String.format("[Ok Zoomer] %s what's this", OWO_ARRAY[random.nextInt(OWO_ARRAY.length)]));
    }
}