package io.github.joaoh1.okzoomer.client;

import java.util.Random;

import io.github.joaoh1.okzoomer.client.events.LoadConfigEvent;
import io.github.joaoh1.okzoomer.client.events.ManageExtraKeysEvent;
import io.github.joaoh1.okzoomer.client.events.ManageZoomEvent;
import io.github.joaoh1.okzoomer.client.packets.ZoomPackets;
import io.github.joaoh1.okzoomer.client.utils.ZoomUtils;
import net.fabricmc.api.ClientModInitializer;

//This class is responsible for registering the events and packets.
public class OkZoomerClientMod implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		//TODO - Actually do zoom stuff, remove when everything's done.
		Random random = new Random();
		String[] owo = new String[]{"owo", "OwO", "uwu", "nwn", "^w^", ">w<", "Owo", "owO", ";w;", "0w0", "QwQ", "TwT", "-w-", "$w$", "@w@", "*w*", ":w:", "°w°", "ºwº", "ówò", "òwó", "`w´", "´w`", "~w~", "umu", "nmn", "own", "nwo", "ùwú", "úwù", "ñwñ", "UwU", "NwN", "ÙwÚ", "PwP", "own", "nwo", "/w/", "\\w\\", "|w|", "#w#", "<>w<>", "'w'", "\"w\"", "öwö", "ôwô", "ÖwÖ", "ÔwÔ", ".w.", "+w+", ")w(", "]w[", "}w{", "_w_"};
		ZoomUtils.modLogger.info("[Ok Zoomer] " + owo[random.nextInt(owo.length)] + " what's this");

		//Load the events.
		LoadConfigEvent.registerEvent();
		ManageZoomEvent.registerEvent();
		ManageExtraKeysEvent.registerEvent();
		
		//Register the zoom-controlling packets.
		ZoomPackets.registerPackets();
	}
}
