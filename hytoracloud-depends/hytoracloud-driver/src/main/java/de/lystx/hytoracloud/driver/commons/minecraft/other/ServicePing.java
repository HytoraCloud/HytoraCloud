package de.lystx.hytoracloud.driver.commons.minecraft.other;

import lombok.Getter;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

/**
 * This class is for getting values
 * for a specific server.
 * You can even use it to ping other servers
 * like "GommeHD.net" or "Hypixel.net" to get information
 * of them like OnlinePlayers, MaxPlayers and the Motd
 */
@Getter
public class ServicePing {

    /**
     * The motd of this ping
     */
    private String motd;

    /**
     * The amount of players
     */
    private int players;

    /**
     * The max players
     */
    private int maxplayers;

    /**
     * If online
     */
    private boolean online;

    /**
     * The socket for data
     */
    private Socket socket;

    /**
     * The output of the socket
     */
    private OutputStream outputStream;

    /**
     * The data reader of the socket
     */
    private DataOutputStream dataOutputStream;

    /**
     * The input of the socket
     */
    private InputStream inputStream;

    /**
     * The data reader of the socket
     */
    private InputStreamReader inputStreamReader = null;

    /**
     * The connected tries
     */
    private int tries = 0;

    /**
     * Pings Server and sets fields to returned values
     *
     * @param address the address
     * @param port the port
     * @param timeout the timeout
     * @throws IOException if something goes wrong
     */
    public void pingServer(String address, int port, int timeout) throws IOException {
        this.socket = new Socket();
        this.socket.setSoTimeout(timeout);
        this.socket.connect(new InetSocketAddress(address, port), timeout);

        this.outputStream = socket.getOutputStream();
        this.dataOutputStream = new DataOutputStream(outputStream);
        this.inputStream = socket.getInputStream();
        this.inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_16BE);

        try {

            this.dataOutputStream.write(new byte[] { -2, 1 });
            int packetId = inputStream.read();
            if (packetId != 255) {
               this.close();
            }

            int length = inputStreamReader.read();
            if (length == -1 || length == 0) {
                this.close();
            }

            char[] chars = new char[length];

            if (inputStreamReader.read(chars, 0, length) != length) {
                this.close();
            }

            String string = new String(chars);
            String[] data;

            try {
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
            } catch (ArrayIndexOutOfBoundsException e) {
                //e.printStackTrace(); --> Ignoring because it just re-pings
            }
            this.close();
        } catch (SocketTimeoutException e) {
            if (this.tries < 5) {
                this.tries++;
                this.pingServer(address, port, timeout);
                return;
            }
            this.tries = 0;
        }
    }

    /**
     * Closes the socket connection
     * and stops the ping-request
     *
     * @throws IOException if something goes wrong
     */
    private void close() throws IOException {
        this.dataOutputStream.close();
        this.outputStream.close();
        this.inputStreamReader.close();
        this.inputStream.close();
        this.socket.close();
    }

}
