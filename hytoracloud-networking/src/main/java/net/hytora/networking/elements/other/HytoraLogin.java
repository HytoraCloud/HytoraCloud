package net.hytora.networking.elements.other;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public class HytoraLogin {

    /**
     * The username of the login
     */
    private final String name;

    /**
     * The password of the login
     */
    private final String credentials;

    public HytoraLogin(String name) {
        this.name = name;
        this.credentials = "no_credentials";
    }
}
