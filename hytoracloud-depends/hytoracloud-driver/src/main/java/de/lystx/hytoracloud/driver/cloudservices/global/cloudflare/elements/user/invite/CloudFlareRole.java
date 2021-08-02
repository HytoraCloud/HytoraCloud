
package de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.elements.user.invite;

import de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.elements.CloudFlareable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;


@Setter @AllArgsConstructor @Getter
public class CloudFlareRole implements CloudFlareable {

    /**
     * The id of this role
     */
    private String id;

    /**
     * The name of this role
     */
    private String name;

    /**
     * The description
     */
    private String description;

    /**
     * The permissions
     */
    private List<String> permissions;
    
    
    @Override
    public String toString( ) {
        return new ToStringBuilder( this ).append( "id", id ).append( "name", name ).append( "description", description ).append( "permissions", permissions ).toString();
    }
    
}
