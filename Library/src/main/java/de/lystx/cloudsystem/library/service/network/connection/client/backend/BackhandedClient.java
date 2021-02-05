package de.lystx.cloudsystem.library.service.network.connection.client.backend;

import de.lystx.cloudsystem.library.service.network.connection.PacketRunner;
import de.lystx.cloudsystem.library.service.network.connection.channel.base.Channel;

import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.channels.AlreadyConnectedException;
import java.util.HashMap;
import java.util.UUID;

public class BackhandedClient {

    protected String id;
    protected String group;
    protected Socket loginSocket;
    protected InetSocketAddress address;
    protected int timeout;
    protected Thread listeningThread;
    protected HashMap<String, PacketRunner> idMethods = new HashMap<>();
    protected int errorCount;
    protected boolean secureMode;
    protected boolean muted;
    protected boolean stopped;
    public static final String DEFAULT_USER_ID = UUID.randomUUID().toString();
    public static final String DEFAULT_GROUP_ID = "_DEFAULT_GROUP_";

    public BackhandedClient(String hostname, int port, int timeout) {
        this(hostname, port, timeout, false, DEFAULT_USER_ID, "_DEFAULT_GROUP_");
    }

    public BackhandedClient(String hostname, int port, int timeout, boolean useSSL, String id, String group) {
        this.id = id;
        this.group = group;
        this.errorCount = 0;
        this.address = new InetSocketAddress(hostname, port);
        this.timeout = timeout;
        this.secureMode = useSSL;
        if (this.secureMode) {
            System.setProperty("javax.net.ssl.trustStore", "ssc.store");
            System.setProperty("javax.net.ssl.keyStorePassword", "SimpleServerClient");
        }
    }

    public void start() throws IOException, ConnectException {
        this.stopped = false;
        login();
        startListening();
    }

    public void stop() {
        this.stopped = true;
    }

    protected void repairConnection() {
        System.exit(0);
    }

    protected void login() throws IOException, ConnectException {
        if (this.stopped)
            return;
        if (this.loginSocket != null && this.loginSocket.isConnected())
            throw new AlreadyConnectedException();
        if (this.secureMode) {
            this.loginSocket = SSLSocketFactory.getDefault().createSocket(this.address.getAddress(), this.address.getPort());
        } else {
            this.loginSocket = new Socket();
            this.loginSocket.connect(this.address, this.timeout);
        }
        ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(this.loginSocket.getOutputStream()));
        Channel loginPackage = new Channel("_INTERNAL_LOGIN_", this.id, this.group);
        loginPackage.sign(this.id, this.group);
        out.writeObject(loginPackage);
        out.flush();

    }

    protected void startListening() {
        if (this.listeningThread != null && this.listeningThread.isAlive())
            return;
        this.listeningThread = new Thread(new Runnable() {
            public void run() {
                while (!BackhandedClient.this.stopped) {
                    try {
                        if (BackhandedClient.this.loginSocket != null && !BackhandedClient.this.loginSocket.isConnected())
                            while (!BackhandedClient.this.loginSocket.isConnected()) {
                                BackhandedClient.this.repairConnection();
                                if (BackhandedClient.this.loginSocket.isConnected())
                                    break;
                                Thread.sleep(5000L);
                                BackhandedClient.this.repairConnection();
                            }
                        ObjectInputStream ois;
                        try {
                            ois = new ObjectInputStream(new BufferedInputStream(loginSocket.getInputStream()));
                        } catch (EOFException | StreamCorruptedException e) {
                            return;
                        }
                        Object raw = ois.readObject();
                        if (BackhandedClient.this.stopped)
                            return;
                        if (raw instanceof Channel) {
                            final Channel msg = (Channel)raw;
                            for (String current : BackhandedClient.this.idMethods.keySet()) {
                                if (current.equalsIgnoreCase(msg.id())) {
                                    (new Thread(() -> (BackhandedClient.this.idMethods.get(current)).run(msg, BackhandedClient.this.loginSocket))).start();
                                    break;
                                }
                            }
                        }
                    } catch (SocketException e) {
                        System.exit(0);
                    } catch (ClassNotFoundException|IOException|InterruptedException ex) {
                        ex.printStackTrace();
                        System.exit(0);
                    }
                    BackhandedClient.this.errorCount = 0;
                }
            }
        });
        this.listeningThread.start();
    }

    public Channel sendMessage(Channel message, int timeout) {
        try {
            Socket tempSocket;
            if (this.secureMode) {
                tempSocket = SSLSocketFactory.getDefault().createSocket(this.address.getAddress(), this.address.getPort());
            } else {
                tempSocket = new Socket();
                try {
                    tempSocket.connect(this.address, timeout);
                } catch (Exception exception) {}
            }
            ObjectOutputStream tempOOS = new ObjectOutputStream(new BufferedOutputStream(tempSocket.getOutputStream()));
            message.sign(this.id, this.group);
            tempOOS.writeObject(message);
            tempOOS.flush();
            ObjectInputStream tempOIS = new ObjectInputStream(new BufferedInputStream(tempSocket.getInputStream()));
            Object raw = tempOIS.readObject();
            tempOOS.close();
            tempOIS.close();
            tempSocket.close();
            if (raw instanceof Channel)
                return (Channel)raw;
        } catch (IOException|ClassNotFoundException iOException) {}
        return null;
    }

    public void sendMessage(Channel message) {
        sendMessage(message, this.timeout);
    }

    public void registerMethod(String identifier, PacketRunner executable) {
        this.idMethods.put(identifier, executable);
    }


}
