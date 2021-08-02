
package de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.elements.zone;

import de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.elements.CloudFlareable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Getter @Setter @AllArgsConstructor
public class ZonePlan implements CloudFlareable {

    /**
     * The id of this plan
     */
    private String id;

    /**
     * The name of this plan
     */
    private String name;

    /**
     * The price of this plan
     */
    private Integer price;

    /**
     * The currency of this plan
     */
    private String currency;

    /**
     * The frequency of this plan
     */
    private String frequency;

    /**
     * The legacy id of this plan
     */
    private String legacyId;

    /**
     * If subscribed
     */
    private Boolean isSubscribed;

    /**
     * If can subscribe
     */
    private Boolean canSubscribe;
    
    @Override
    public String toString() {
        return new ToStringBuilder( this ).append( "id", id ).append( "name", name ).append( "price", price ).append( "currency", currency ).append( "Frequency", frequency ).append( "legacyId", legacyId ).append( "isSubscribed", isSubscribed ).append( "canSubscribe", canSubscribe ).toString();
    }
    
}
