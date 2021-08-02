
package de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.elements.zone;

import de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.elements.CloudFlareable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

@Setter @Getter @AllArgsConstructor
public class CloudFlareZone implements CloudFlareable {

    /**
     * The id of this zone
     */
    private String id;

    /**
     * The name of this zone
     */
    private String name;

    /**
     * The development mode
     */
    private Integer developmentMode;

    /**
     * The original name servers
     */
    private List<String> originalNameServers;

    /**
     * The original registrar
     */
    private String originalRegistrar;

    /**
     * The original dns host
     */
    private String originalDnshost;

    /**
     * The creation date
     */
    private String createdOn;

    /**
     * The last time its modified
     */
    private String modifiedOn;

    /**
     * The name servers
     */
    private List<String> nameServers;

    /**
     * The owner of this zone
     */
    private ZoneOwner zoneOwner;

    /**
     * The permissions of this zone
     */
    private List<String> permissions;

    /**
     * The plan of this zone
     */
    private ZonePlan zonePlan;

    /**
     * The pending plan of this zone
     */
    private ZonePlan zonePendingPlan;

    /**
     * The status of this zone
     */
    private String status;

    /**
     * If this zone is paused
     */
    private Boolean paused;

    /**
     * The type of this zone
     */
    private String type;
    
    @Override
    public String toString() {
        return new ToStringBuilder( this ).append( "id", id ).append( "name", name ).append( "developmentMode", developmentMode ).append( "originalNameServers", originalNameServers ).append( "originalRegistrar", originalRegistrar ).append( "originalDnshost", originalDnshost ).append( "createdOn", createdOn ).append( "modifiedOn", modifiedOn ).append( "nameServers", nameServers ).append( "owner", zoneOwner).append( "permissions", permissions ).append( "plan", zonePlan).append( "planPending", zonePendingPlan).append( "Status", status ).append( "paused", paused ).append( "type", type ).toString();
    }
    
}
