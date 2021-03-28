package de.lystx.serverselector.spigot.manager.npc.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class SkinFetcher {

	private static Map<String, String> values = Maps.newConcurrentMap();
	private static Map<String, String> signatures = Maps.newConcurrentMap();
	private static List<String> skins = Lists.newLinkedList();

	public String getSkinValue(String uuid) {
		return data(uuid)[0];
	}

	public String getSkinSignature(String uuid) {
		return data(uuid)[1];
	}

	public String[] data(String uuid) {
		String[] data = new String[2];
		if (!skins.contains(uuid)) {
			skins.add(uuid);
			try {
				URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
				URLConnection uc = url.openConnection();
				uc.setUseCaches(false);
				uc.setDefaultUseCaches(false);
				uc.addRequestProperty("User-Agent", "Mozilla/5.0");
				uc.addRequestProperty("Cache-Control", "no-cache, no-store, must-revalidate");
				uc.addRequestProperty("Pragma", "no-cache");
				String json = new Scanner(uc.getInputStream(), "UTF-8").useDelimiter("\\A").next();
				JSONParser parser = new JSONParser();
				Object obj = parser.parse(json);
				JSONArray properties = (JSONArray) ((JSONObject) obj).get("properties");
				for (int i = 0; i < properties.size(); i++) {
					try {
						JSONObject property = (JSONObject) properties.get(i);
						String name = (String) property.get("name");
						String value = (String) property.get("value");
						String signature = property.containsKey("signature") ? (String) property.get("signature") : null;
						values.put(uuid, value);
						signatures.put(uuid, signature);
						data[0] = value;
						data[1] = signature;
					} catch (Exception e) {}
				}
			} catch (Exception e) {}
		} else {
			data[0] = values.get(uuid);
			data[1] = signatures.get(uuid);
		}
		return data;
	}

}