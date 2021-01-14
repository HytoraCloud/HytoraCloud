package de.lystx.cloudsystem.library.service.network.connection.channel.base;

import lombok.Getter;

@Getter
public class NetworkChannel {

    private final Identifier identifier;
    private final Provider provider;

    public NetworkChannel(Identifier identifyer, Provider provider) {
        this.identifier = identifyer;
        this.provider = provider;
    }

    public NetworkChannel(String identifyer, String provider) {
        this(new Identifier(identifyer), new Provider(provider));
    }

    public String getChannelID() {
        return this.provider.getName() + "::" + this.identifier.getId();
    }

}
