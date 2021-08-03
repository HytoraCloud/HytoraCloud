
package de.lystx.hytoracloud.driver.connection.cloudflare.elements.user.invite;

import de.lystx.hytoracloud.driver.connection.cloudflare.elements.CloudFlareable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;



@AllArgsConstructor @Getter @Setter
public class CloudFlareInvite implements CloudFlareable {

    /**
     * The id of this invite
     */
    private String id;

    /**
     * The id of the invited member
     */
    private String invitedMemberId;

    /**
     * The email of the invited member
     */
    private String invitedMemberEmail;

    /**
     * The id of the organization
     */
    private String organizationId;

    /**
     * The name of the organization
     */
    private String organizationName;

    /**
     * The roles of this invite
     */
    private List<CloudFlareRole> cloudFlareRoles;

    /**
     * Invitator
     */
    private String invitedBy;

    /**
     * The date when invitation created
     */
    private String invitedOn;

    /**
     * When invitation expires
     */
    private String expiresOn;

    /**
     * The status pf this invitation
     */
    private String status;
    
    @Override
    public String toString( ) {
        return new ToStringBuilder( this ).append( "id", id ).append( "invitedMemberId", invitedMemberId ).append( "invitedMemberEmail", invitedMemberEmail ).append( "organizationId", organizationId ).append( "organizationName", organizationName ).append( "roles", cloudFlareRoles).append( "invitedBy", invitedBy ).append( "invitedOn", invitedOn ).append( "expiresOn", expiresOn ).append( "Status", status ).toString();
    }
    
}
