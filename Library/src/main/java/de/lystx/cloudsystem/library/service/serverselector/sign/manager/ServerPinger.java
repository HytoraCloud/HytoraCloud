package de.lystx.cloudsystem.library.service.serverselector.sign.manager;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


public class ServerPinger {

    private String motd;
    private int players;
    private int maxplayers;
    private boolean online;

    private Socket socket = null;
    private OutputStream outputStream = null;
    private DataOutputStream dataOutputStream = null;
    private InputStream inputStream = null;
    private InputStreamReader inputStreamReader = null;

    public void pingServer(final String adress, final int port, final int timeout) throws IOException {
        socket = new Socket();
        socket.setSoTimeout(timeout);
        socket.connect(new InetSocketAddress(adress, port), timeout);

        outputStream = socket.getOutputStream();
        dataOutputStream = new DataOutputStream(outputStream);
        inputStream = socket.getInputStream();
        inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_16BE);

        dataOutputStream.write(new byte[] { -2, 1 });

        int packetId = inputStream.read();
        if(packetId != 255)
            close();

        int length = inputStreamReader.read();
        if (length == -1 || length == 0)
            close();

        final char[] chars = new char[length];

        if (inputStreamReader.read(chars, 0, length) != length)
            close();

        final String string = new String(chars);

        if (string.startsWith("§")) {
            final String[] data = string.split("\u0000");
            motd = data[3];
            players = Integer.parseInt(data[4]);
            maxplayers = Integer.parseInt(data[5]);
            online = true;
            close();
        } else {
            final String[] data = string.split("§");
            motd = data[0];
            players = Integer.parseInt(data[1]);
            maxplayers = Integer.parseInt(data[2]);
            online = true;
            close();
        }
    }

    private void close() throws IOException {
        dataOutputStream.close();
        outputStream.close();
        inputStreamReader.close();
        inputStream.close();
        socket.close();
    }

    public String getMotd() {
        return motd;
    }

    public int getPlayers() {
        return players;
    }

    public int getMaxplayers() {
        return maxplayers;
    }

    public boolean isOnline() {
        return online;
    }
}
