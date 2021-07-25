
package de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.objects.dns;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.constants.RecordType;
import de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.objects.Identifiable;
import de.lystx.hytoracloud.driver.commons.storage.JsonDocument;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Getter
@Setter
@AllArgsConstructor
public class DNSRecord implements Identifiable {

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
