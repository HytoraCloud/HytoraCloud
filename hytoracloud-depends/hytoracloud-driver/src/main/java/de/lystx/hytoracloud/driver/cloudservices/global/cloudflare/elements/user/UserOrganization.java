
package de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.elements.user;

import de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.elements.CloudFlareable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;


@Setter @Getter @AllArgsConstructor
public class UserOrganization implements CloudFlareable {

    /**
     * The id of this organization
     */
    private String id;

    /**
     * The name of this organization
     */
    private String name;

    /**
     * The status of this organization
     */
    private String status;

    /**
     * The permissions this organization has
     */
    private List<String> permissions;

    /**
     * The roles this organization has
     */
    private List<String> roles;

    @Override
    public String toString() {
        return new ToStringBuilder( this ).append( "id", id ).append( "name", name ).append( "Status", status ).append( "permissions", permissions ).append( "roles", roles ).toString();
    }

}