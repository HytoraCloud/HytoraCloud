
package de.lystx.hytoracloud.driver.connection.cloudflare.elements.dns;

import de.lystx.hytoracloud.driver.connection.cloudflare.elements.enums.RecordType;
import de.lystx.hytoracloud.driver.connection.cloudflare.elements.CloudFlareable;
import de.lystx.hytoracloud.driver.utils.json.JsonDocument;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DNSRecord implements CloudFlareable {

    /**
     * The id of this record
     */
    private String id;

    /**
     * The type of this record
     */
    private String type;

    /**
     * The name of this record
     */
    private String name;

    /**
     * The ip of this record
     */
    private String content;

    /**
     * If its proxied
     */
    private boolean proxied;

    /**
     * The ttl
     */
    private int ttl;


    public DNSRecord(String id, RecordType type, String name) {
        this(id, type, "127.0.0.1", name);
    }

    public DNSRecord(String id, RecordType type, String host, String name) {
        this(id, type.name(), name, host, false, 0);
    }

    @Override
    public String toString() {
        return JsonDocument.toString(this);
    }
}
