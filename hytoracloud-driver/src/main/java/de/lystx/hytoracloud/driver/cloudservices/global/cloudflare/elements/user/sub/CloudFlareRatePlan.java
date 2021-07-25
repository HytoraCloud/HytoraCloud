
package de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.elements.user.sub;

import de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.elements.CloudFlareable;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;


@Getter
@Setter
public class CloudFlareRatePlan implements CloudFlareable {

    /**
     * The id of this plan
     */
    private String id;

    /**
     * The name of this plan
     */
    private String publicName;

    /**
     * The currency
     */
    private String currency;

    /**
     * The scope
     */
    private String scope;

    /**
     * If externallyManaged
     */
    private Boolean externallyManaged;
    
    @Override
    public String toString() {
        return new ToStringBuilder( this ).append( "id", id ).append( "publicName", publicName ).append( "currency", currency ).append( "scope", scope ).append( "externallyManaged", externallyManaged ).toString();
    }
    
}
