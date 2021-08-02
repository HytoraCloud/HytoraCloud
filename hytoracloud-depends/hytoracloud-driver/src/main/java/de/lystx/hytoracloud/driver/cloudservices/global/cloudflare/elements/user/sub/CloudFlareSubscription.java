
package de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.elements.user.sub;

import de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.elements.CloudFlareable;
import de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.elements.zone.CloudFlareZone;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;


@Setter @Getter @AllArgsConstructor
public class CloudFlareSubscription implements CloudFlareable {

    /**
     * The id of this subscription
     */
    private String id;

    /**
     * The state
     */
    private String state;

    /**
     * The price of this subscription
     */
    private Integer price;

    /**
     * The currency
     */
    private String currency;

    /**
     * All values
     */
    private List<CloudFlareValue> cloudFlareValues;

    /**
     * The zone
     */
    private CloudFlareZone cloudFlareZone;

    /**
     * The frequency
     */
    private String frequency;

    /**
     * The ratePlan
     */
    private CloudFlareRatePlan cloudFlareRatePlan;

    /**
     * The currentPeriodEnd
     */
    private String currentPeriodEnd;

    /**
     * The currentPeriodStart
     */
    private String currentPeriodStart;
    
    @Override
    public String toString() {
        return new ToStringBuilder( this ).append( "id", id ).append( "state", state ).append( "price", price ).append( "currency", currency ).append( "componentValues", cloudFlareValues).append( "zone", cloudFlareZone).append( "Frequency", frequency ).append( "ratePlan", cloudFlareRatePlan).append( "currentPeriodEnd", currentPeriodEnd ).append( "currentPeriodStart", currentPeriodStart ).toString();
    }
    
}
