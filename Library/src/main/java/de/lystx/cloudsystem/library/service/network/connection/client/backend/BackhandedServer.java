package de.lystx.cloudsystem.library.service.network.connection.client.backend;

import de.lystx.cloudsystem.library.service.network.connection.PacketRunner;
import de.lystx.cloudsystem.library.service.network.connection.channel.base.Channel;

import javax.net.ssl.SSLServerSocketFactory;
import java.io.*;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.channels.IllegalBlockingModeException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public abstract class BackhandedServer {

    protected HashMap<String, PacketRunner> idMethods = new HashMap<>();
    protected ServerSocket server;
    protected int port;
    protected ArrayList<RemoteClient> clients;
    protected ArrayList<RemoteClient> toBeDeleted;
    protected Thread listeningThread;
    protected boolean autoRegisterEveryClient;
    protected boolean secureMode;
    protected boolean stopped;
    protected boolean muted;
    protected long pingInterval = 30000L;
    protected static final String INTERNAL_LOGIN_ID = "_INTERNAL_LOGIN_";

    @Deprecated
    public BackhandedServer(int port) {
        this(port, true, true, false);
    }

    @Deprecated
    public BackhandedServer(int port, boolean autoRegisterEveryClient, boolean keepConnectionAlive, boolean useSSL) {
        this.clients = new ArrayList<>();
        this.port = port;
        this.autoRegisterEveryClient = autoRegisterEveryClient;
        this.muted = false;
        this.secureMode = useSSL;
        if (this.secureMode) {
            System.setProperty("javax.net.ssl.keyStore", "ssc.store");
            System.setProperty("javax.net.ssl.keyStorePassword", "SimpleServerClient");
        }
        if (autoRegisterEveryClient)
            registerLoginMethod();
        preStart();
        start();
        if (keepConnectionAlive)
            startPingThread();
    }

    protected void startPingThread() {
        (new Thread(() -> {
            while (BackhandedServer.this.server != null) {
                try {
                    Thread.sleep(BackhandedServer.this.pingInterval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                BackhandedServer.this.broadcastMessage(new Channel("_INTERNAL_PING_", "OK"));
            }
        })).start();
    }

    protected void startListening() {
        if (this.listeningThread == null && this.server != null) {
            this.listeningThread = new Thread(new Runnable() {
                public void run() {
                    while (!Thread.interrupted() && !BackhandedServer.this.stopped && BackhandedServer.this.server != null) {
                        try {
                            final Socket tempSocket = BackhandedServer.this.server.accept();
                            ObjectInputStream ois;
                            try {
                                ois = new ObjectInputStream(new BufferedInputStream(tempSocket.getInputStream()));
                            } catch (EOFException e) {
                                return;
                            }
                            Object raw = null;
                            try {
                                raw = ois.readObject();
                            } catch (ClassCastException e) {
                                System.out.println("[CONNECTION] Couldn't read incomming object (might not implement Serializable)");
                                return;
                            }
                            if (raw instanceof Channel) {
                                final Channel msg = (Channel) raw;
                                for (String current : BackhandedServer.this.idMethods.keySet()) {
                                    if (msg.id().equalsIgnoreCase(current))
                                        (new Thread(() -> {
                                            (BackhandedServer.this.idMethods.get(current)).run(msg, tempSocket);
                                            if (!msg.id().equals("_INTERNAL_LOGIN_"))
                                                try {
                                                    tempSocket.close();
                                                } catch (IOException e) {
                                                    System.exit(0);
                                                }
                                        })).start();
                                }
                            }
                        } catch (SocketException e) {
                            System.exit(0);
                        } catch (IllegalBlockingModeException|IOException|ClassNotFoundException e) {
                            e.printStackTrace();
                            System.exit(0);
                        }
                    }
                }
            });
            this.listeningThread.start();
        }
    }

    public synchronized void sendMessage(String remoteClientId, Channel message) {
        for (RemoteClient current : this.clients) {
            if (current.getId().equals(remoteClientId))
                sendMessage(current, message);
        }
    }

    public synchronized void sendMessage(RemoteClient remoteClient, Channel message) {
        try {
            if (!remoteClient.getSocket().isConnected())
                throw new ConnectException("Socket not connected.");
            ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(remoteClient.getSocket().getOutputStream()));
            out.writeObject(message);
            out.flush();
        } catch (IOException iOException) {}
    }

    public synchronized int broadcastMessage(Channel message) {
        this.toBeDeleted = new ArrayList<>();
        int txCounter = 0;
        for (RemoteClient current : this.clients) {
            sendMessage(current, message);
            txCounter++;
        }
        txCounter -= this.toBeDeleted.size();
        for (RemoteClient current : this.toBeDeleted)
            this.clients.remove(current);
        this.toBeDeleted = null;
        return txCounter;
    }

    public void registerMethod(String identifier, PacketRunner executable) {
        if (identifier.equalsIgnoreCase("_INTERNAL_LOGIN_") && this.autoRegisterEveryClient)
            System.exit(0);
        this.idMethods.put(identifier, executable);
    }

    protected void registerLoginMethod() {
        this.idMethods.put("_INTERNAL_LOGIN_", (msg, socket) -> {
            if (msg.size() == 3) {
                BackhandedServer.this.registerClient((String)msg.get(1), (String)msg.get(2), socket);
            } else if (msg.size() == 2) {
                BackhandedServer.this.registerClient((String)msg.get(1), socket);
            } else {
                BackhandedServer.this.registerClient(UUID.randomUUID().toString(), socket);
            }
        });
    }

    protected synchronized void registerClient(String id, Socket newClientSocket) {
        this.clients.add(new RemoteClient(id, newClientSocket));
    }

    protected synchronized void registerClient(String id, String group, Socket newClientSocket) {
        this.clients.add(new RemoteClient(id, group, newClientSocket));
    }

    protected void start() {
        this.stopped = false;
        this.server = null;
        try {
            if (this.secureMode) {
                this.server = SSLServerSocketFactory.getDefault().createServerSocket(this.port);
            } else {
                this.server = new ServerSocket(this.port);
            }
        } catch (IOException e) {
            System.exit(0);
        }
        startListening();
    }

    public void stop() throws IOException {
        this.stopped = true;
        if (this.listeningThread.isAlive())
            this.listeningThread.interrupt();
        if (this.server != null)
            this.server.close();
    }

    public abstract void preStart();

    protected static class RemoteClient {

        private String id;
        private String group;
        private Socket socket;

        public RemoteClient(String id, Socket socket) {
            this.id = id;
            this.group = "_DEFAULT_GROUP_";
            this.socket = socket;
        }

        public RemoteClient(String id, String group, Socket socket) {
            this.id = id;
            this.group = group;
            this.socket = socket;
        }

        public String getId() {
            return this.id;
        }

        public String getGroup() {
            return this.group;
        }

        public Socket getSocket() {
            return this.socket;
        }

        public String toString() {
            return "[RemoteClient: " + this.id + " (" + this.group + ") @ " + this.socket.getRemoteSocketAddress() + "]";
        }
    }
}
