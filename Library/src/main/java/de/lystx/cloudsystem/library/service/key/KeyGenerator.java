package de.lystx.cloudsystem.library.service.key;

import java.io.BufferedReader;
import java.util.UUID;
import java.util.function.Consumer;

public class KeyGenerator {


    public void create(Consumer<String> callBack, Integer strength) {
        callBack.accept(this.addHashToHash(this.getUUIDafterAmount(strength), strength));
    }

    private String getUUIDafterAmount(Integer amount) {
        StringBuilder uuidKey = new StringBuilder();
        for (int i = 0; i < amount; i++) {
            uuidKey.append(UUID.randomUUID().toString());
        }
        return uuidKey.toString();
    }

    private String stringToHash(String value) {
        String hashSalt = UUID.randomUUID().toString();
        String[] alpha = "-.,#+^°§=+@".split("");
        String ret = "";
        int saltkey = 0;
        for (String saltchar : hashSalt.split("")) {
            char salty = saltchar.charAt(0);
            saltkey += salty * salty;
        }
        for (String cCurr : value.split("")) {
            char cChar = cCurr.charAt(0);
            int end = cChar * cChar * value.length() * (ret.length() + 2) * (alpha.length + 9) + saltkey;
            for (String loopChars : String.valueOf(end).split("")) {
                int index = Integer.parseInt(loopChars.replace("-", "9"));
                ret = ret + alpha[index];
            }
        }
        return ret;
    }

    private String addHashToHash(String string, Integer amount) {
        String currentHash = string;
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < amount; i++) {
            String result = stringToHash(currentHash);
            currentHash = result;
            key.append(result);
        }
        return key.toString();
    }
}


