
package de.lystx.hytoracloud.driver.service.util.minecraft.ping;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.lystx.hytoracloud.driver.service.util.Utils;
import org.xbill.DNS.*;
import org.xbill.DNS.Record;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ServerPinger {

    private static final Gson GSON = new Gson();
    private static final String SRV_QUERY_PREFIX = "_minecraft._tcp.%s";

    /**
     * Fetches a {@link ServerInfo} for the supplied hostname.
     * <b>Assumed timeout of 2s and port of 25565.</b>
     *
     * @param address - a valid String hostname
     * @return {@link ServerInfo}
     * @throws IOException
     */
    public static ServerInfo getPing(final String address) throws IOException {
        return getPing(ServerInfoOptions.builder().hostname(address).build());
    }

    /**
     * Fetches a {@link ServerInfo} for the supplied options.
     *
     * @param options - a filled instance of {@link ServerInfoOptions}
     * @return {@link ServerInfo}
     * @throws IOException
     */
    public static ServerInfo getPing(final ServerInfoOptions options) throws IOException {

        Preconditions.checkNotNull(options.getHostname(), "Hostname cannot be null.");

        String hostname = options.getHostname();
        int port = options.getPort();

        try {

            Record[] records = new Lookup(String.format(SRV_QUERY_PREFIX, hostname), Type.SRV).run();

            if (records != null) {

                for (Record record : records) {
                    SRVRecord srv = (SRVRecord) record;

                    hostname = srv.getTarget().toString().replaceFirst("\\.$", "");
                    port = srv.getPort();
                }

            }
        } catch (TextParseException e) {
            e.printStackTrace();
        }

        String json;
        long ping = -1;

        try (final Socket socket = new Socket()) {

            long start = System.currentTimeMillis();
            socket.connect(new InetSocketAddress(hostname, port), options.getTimeout());
            ping = System.currentTimeMillis() - start;

            try (DataInputStream in = new DataInputStream(socket.getInputStream());
                 DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                 //> Handshake
                 ByteArrayOutputStream handshake_bytes = new ByteArrayOutputStream();
                 DataOutputStream handshake = new DataOutputStream(handshake_bytes)) {

                handshake.writeByte(Utils.PACKET_HANDSHAKE);
                Utils.writeVarInt(handshake, Utils.PROTOCOL_VERSION);
                Utils.writeVarInt(handshake, options.getHostname().length());
                handshake.writeBytes(options.getHostname());
                handshake.writeShort(options.getPort());
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
                json = new String(data, options.getCharset());

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

            // For those versions that work with TextComponent MOTDs

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

        ServerInfo output = GSON.fromJson(jsonObject, ServerInfo.class);
        output.setPing(ping);

        return output;
    }

}
