
package de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.elements.zone;

import de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.elements.CloudFlareable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

@Setter @AllArgsConstructor @Getter
public class ZoneRatePlan implements CloudFlareable {

    /**
     * The id of this rate plan
     */
    private String id;

    /**
     * The name of this rate plan
     */
    private String name;

    /**
     * The currency of this rate plan
     */
    private String currency;

    /**
     * The duration of this rate plan
     */
    private Integer duration;

    /**
     * The frequency of this plan
     */
    private String frequency;

    /**
     * The components of this plan
     */
    private List<ZoneComponent> zoneComponents;
    
    @Override
    public String toString() {
        return new ToStringBuilder( this ).append( "id", id ).append( "name", name ).append( "currency", currency ).append( "duration", duration ).append( "Frequency", frequency ).append( "components", zoneComponents).toString();
    }
    
}
