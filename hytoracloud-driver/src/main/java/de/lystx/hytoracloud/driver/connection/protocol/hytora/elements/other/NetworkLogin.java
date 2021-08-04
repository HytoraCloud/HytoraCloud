package de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.other;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public class NetworkLogin {

    /**
     * The username of the login
     */
    private final String name;

    /**
     * The password of the login
     */
    private final String credentials;

    public NetworkLogin(String name) {
        this.name = name;
        this.credentials = "no_credentials";
    }
}
