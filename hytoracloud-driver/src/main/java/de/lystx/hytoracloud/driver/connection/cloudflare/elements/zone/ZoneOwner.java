
package de.lystx.hytoracloud.driver.connection.cloudflare.elements.zone;

import de.lystx.hytoracloud.driver.connection.cloudflare.elements.CloudFlareable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;


@Setter @AllArgsConstructor @Getter
public class ZoneOwner implements CloudFlareable {

    /**
     * The id of this owner
     */
    private String id;

    /**
     * The email of this owner
     */
    private String email;

    /**
     * The type of this owner
     */
    private String ownerType;
    
    @Override
    public String toString() {
        return new ToStringBuilder( this ).append( "id", id ).append( "email", email ).append( "ownerType", ownerType ).toString();
    }
    
}
