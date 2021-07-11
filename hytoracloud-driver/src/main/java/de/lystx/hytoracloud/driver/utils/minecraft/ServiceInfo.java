
package de.lystx.hytoracloud.driver.utils.minecraft;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.lystx.hytoracloud.driver.utils.utillity.JsonEntity;
import de.lystx.hytoracloud.driver.utils.Utils;
import lombok.*;
import org.xbill.DNS.*;

@Getter
@ToString
@RequiredArgsConstructor
public class ServiceInfo {

    //Building
    private String address;
    private int port;
    private String charset;
    private int timeout;


    private ServiceInfo(String address, int port, String charset, int timeout) {
        this.address = address;
        this.port = port;
        this.charset = charset;
        this.timeout = timeout;

        this.description = null;
        this.players = null;
        this.version = null;
        this.favicon = null;
        this.ping = -1L;
        this.reply = false;
    }

    //Replay
    private Description description;
    private Players players;
    private Version version;
    private String favicon;
    @Setter
    private long ping;

    private ServiceInfo(Description description, Players players, Version version, String favicon, long ping) {
        this.description = description;
        this.players = players;
        this.version = version;
        this.favicon = favicon;
        this.ping = ping;

        this.address = null;
        this.port = -1;
        this.charset = null;
        this.timeout = -1;
        this.reply = true;
    }

    //Declaring
    private boolean reply;
    private static final Gson GSON = new Gson();
    private static final String SRV_QUERY_PREFIX = "_minecraft._tcp.%s";


    public ServiceInfo reply() throws IOException {

        Preconditions.checkNotNull(this.address, "Hostname cannot be null.");

        try {

            Record[] records = new Lookup(String.format(SRV_QUERY_PREFIX, this.address), Type.SRV).run();

            if (records != null) {

                for (Record record : records) {
                    SRVRecord srv = (SRVRecord) record;

                    this.address = srv.getTarget().toString().replaceFirst("\\.$", "");
                    port = srv.getPort();
                }

            }
        } catch (TextParseException e) {
            e.printStackTrace();
        }

        String json;
        long ping = -1;

        try (Socket socket = new Socket()) {

            long start = System.currentTimeMillis();
            socket.connect(new InetSocketAddress(this.address, port),  timeout);
            ping = System.currentTimeMillis() - start;

            try (DataInputStream in = new DataInputStream(socket.getInputStream());
                 DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                 //> Handshake
                 ByteArrayOutputStream handshake_bytes = new ByteArrayOutputStream();
                 DataOutputStream handshake = new DataOutputStream(handshake_bytes)) {

                handshake.writeByte(Utils.PACKET_HANDSHAKE);
                Utils.writeVarInt(handshake, Utils.PROTOCOL_VERSION);
                Utils.writeVarInt(handshake, this.address.length());
                handshake.writeBytes(this.address);
                handshake.writeShort(port);
                Utils.writeVarInt(handshake, Utils.STATUS_HANDSHAKE);

                Utils.writeVarInt(out, handshake_bytes.size());
                out.write(handshake_bytes.toByteArray());

                //> Status request
                out.writeByte(0x01); // Size of packet
                out.writeByte(Utils.PACKET_STATUSREQUEST);

                //< Status response
                Utils.readVarInt(in); // Size
                int id = Utils.readVarInt(in);

                Utils.io(id == -1, "Server prematurely ended stream.");
                Utils.io(id != Utils.PACKET_STATUSREQUEST, "Server returned invalid packet.");

                int length = Utils.readVarInt(in);
                Utils.io(length == -1, "Server prematurely ended stream.");
                Utils.io(length == 0, "Server returned unexpected value.");

                byte[] data = new byte[length];
                in.readFully(data);
                json = new String(data, charset);

                //> Ping
                out.writeByte(0x09); // Size of packet
                out.writeByte(Utils.PACKET_PING);
                out.writeLong(System.currentTimeMillis());

                //< Ping
                Utils.readVarInt(in); // Size
                id = Utils.readVarInt(in);
                Utils.io(id == -1, "Server prematurely ended stream.");
                Utils.io(id != Utils.PACKET_PING, "Server returned invalid packet.");

            }

        }

        JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
        JsonElement descriptionJsonElement = jsonObject.get("description");

        if (descriptionJsonElement.isJsonObject()) {

            JsonObject descriptionJsonObject = jsonObject.get("description").getAsJsonObject();

            if (descriptionJsonObject.has("extra")) {
                //TODO: CHECK

                descriptionJsonObject.addProperty("text", descriptionJsonObject.get("extra").getAsJsonArray().toString());

                //descriptionJsonObject.addProperty("text", new TextComponent(ComponentSerializer.parse(descriptionJsonObject.get("extra").getAsJsonArray().toString())).toLegacyText());
                jsonObject.add("description", descriptionJsonObject);
            }

        } else {

            // For those versions that work with String MOTDs

            String description = descriptionJsonElement.getAsString();
            JsonObject descriptionJsonObject = new JsonObject();
            descriptionJsonObject.addProperty("text", description);
            jsonObject.add("description", descriptionJsonObject);
        }

        JsonEntity entity = new JsonEntity(jsonObject);
        JsonEntity pp = entity.getJson("players");
        List<Player> ps = new LinkedList<>();

        for (JsonElement sample : pp.getArray("sample")) {
            JsonObject object = (JsonObject) sample;
            ps.add(new Player(object.get("name").getAsString(), object.get("id").getAsString()));
        }

        this.version = new Version(entity.getJson("version").getString("name"), entity.getJson("version").getInteger("protocol"));
        this.players = new Players(pp.getInteger("max"), pp.getInteger("online"), ps);
        this.description = new Description(entity.getJson("description").getString("text"));
        this.favicon = entity.getString("favicon");

        this.setPing(ping);
        return this;
    }

    /**
     * Prepares a {@link ServiceInfo} but without any infos
     * then u have to use {@link ServiceInfo#reply()} to set all values
     *
     * @param host the host
     * @param port the port
     * @param charset the charset
     * @param timeout the timeout
     * @return serverInfo
     */
    public static ServiceInfo prepare(String host, int port, String charset, int timeout) {
        return new ServiceInfo(host, port, charset, timeout);
    }

    public static ServiceInfo prepare(String host, int port) {
        return prepare(host, port, Charsets.UTF_8.displayName(), 5000);
    }




    @Getter
    @ToString
    @AllArgsConstructor
    public static class Description {

        private String text;

        public String getStrippedText() {
            return Utils.stripColors(this.text);
        }

    }

    @Getter
    @ToString
    @AllArgsConstructor
    public static class Players {

        private final int max;

        private final int online;

        private final List<Player> sample;

    }

    @Getter
    @ToString
    @AllArgsConstructor
    public static class Player {

        private final String name;

        private final String id;

    }


    @Getter
    @ToString
    @AllArgsConstructor
    public static class Version {

        private final String name;

        private final int protocol;

    }


}
