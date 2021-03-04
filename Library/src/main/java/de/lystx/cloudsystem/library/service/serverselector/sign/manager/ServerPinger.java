package de.lystx.cloudsystem.library.service.serverselector.sign.manager;

import lombok.Getter;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

@Getter
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

    /**
     * Pings Server and sets fields to returned values
     * @param adress
     * @param port
     * @param timeout
     * @throws IOException
     */
    public void pingServer(final String adress, final int port, final int timeout) throws IOException {
        this.socket = new Socket();
        this.socket.setSoTimeout(timeout);
        this.socket.connect(new InetSocketAddress(adress, port), timeout);

        this.outputStream = socket.getOutputStream();
        this.dataOutputStream = new DataOutputStream(outputStream);
        this.inputStream = socket.getInputStream();
        this.inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_16BE);

        try {

            dataOutputStream.write(new byte[] { -2, 1 });
            int packetId = inputStream.read();
            if(packetId != 255) {
               this.close();
            }

            int length = inputStreamReader.read();
            if (length == -1 || length == 0) {
                this.close();
            }

            final char[] chars = new char[length];

            if (inputStreamReader.read(chars, 0, length) != length) {
                this.close();
            }

            final String string = new String(chars);

            final String[] data;
            if (string.startsWith("§")) {
                data = string.split("\u0000");
                motd = data[3];
                players = Integer.parseInt(data[4]);
                maxplayers = Integer.parseInt(data[5]);
            } else {
                data = string.split("§");
                motd = data[0];
                players = Integer.parseInt(data[1]);
                maxplayers = Integer.parseInt(data[2]);
            }
            this.online = true;
            this.close();
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            System.out.println("[ServerPinger] Timed out while pinging " + adress + ":" + port + " after " + timeout + "ms");
        }
    }

    /**
     * Stops pinging server
     * @throws IOException
     */
    private void close() throws IOException {
        dataOutputStream.close();
        outputStream.close();
        inputStreamReader.close();
        inputStream.close();
        socket.close();
    }

}
