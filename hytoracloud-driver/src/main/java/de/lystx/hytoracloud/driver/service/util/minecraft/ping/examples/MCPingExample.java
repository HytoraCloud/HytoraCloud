package de.lystx.hytoracloud.driver.service.util.minecraft.ping.examples;

import de.lystx.hytoracloud.driver.service.util.minecraft.ping.ServerPinger;
import de.lystx.hytoracloud.driver.service.util.minecraft.ping.ServerInfoOptions;
import de.lystx.hytoracloud.driver.service.util.minecraft.ping.ServerInfo;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class MCPingExample {

    public static void main(String[] args) {

        ServerInfoOptions options = ServerInfoOptions.builder()
                .hostname("gommehd.net")
                .build();

        ServerInfo reply;

        try {
            reply = ServerPinger.getPing(options);
        } catch (IOException ex) {
            System.out.println(options.getHostname() + " is down or unreachable.");
            return;
        }

        System.out.println(String.format("Full response from %s:", options.getHostname()));
        System.out.println("");

        ServerInfo.Description description = reply.getDescription();

        System.out.println("Description:");
        System.out.println("    Raw: " + description.getText());
        System.out.println("    No color codes: " + description.getStrippedText());
        System.out.println("");

        ServerInfo.Players players = reply.getPlayers();

        System.out.println("Players: ");
        System.out.println("    Online count: " + players.getOnline());
        System.out.println("    Max players: " + players.getMax());
        System.out.println("");

        // Can be null depending on the server
        List<ServerInfo.Player> sample = players.getSample();

        if (sample != null) {
            System.out.println("    Players: " + players.getSample().stream()
                    .map(player -> String.format("%s@%s", player.getName(), player.getId()))
                    .collect(Collectors.joining(", "))
            );
            System.out.println("");
        }

        ServerInfo.Version version = reply.getVersion();

        System.out.println("Version: ");

        // The protocol is the version number: http://wiki.vg/Protocol_version_numbers
        System.out.println("    Protocol: " + version.getProtocol());
        System.out.println("    Name: " + version.getName());
        System.out.println("");

        System.out.println(String.format("Favicon: %s", reply.getFavicon()));

    }

}
