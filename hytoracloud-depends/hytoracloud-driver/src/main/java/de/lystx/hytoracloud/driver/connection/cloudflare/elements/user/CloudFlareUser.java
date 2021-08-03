
package de.lystx.hytoracloud.driver.connection.cloudflare.elements.user;

import de.lystx.hytoracloud.driver.connection.cloudflare.elements.CloudFlareable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Setter @AllArgsConstructor @Getter
public class CloudFlareUser implements CloudFlareable {

    /**
     * The id of this user
     */
    private String id;

    /**
     * The email of this user
     */
    private String email;

    /**
     * The first name of this user
     */
    private String firstName;

    /**
     * The last name of this user
     */
    private String lastName;

    /**
     * The username of this user
     */
    private String username;

    /**
     * The telephone number of this user
     */
    private String telephone;

    /**
     * The country of this user
     */
    private String country;

    /**
     * The zipCode of this user
     */
    private String zipCode;

    /**
     * The creation date of this user
     */
    private String createdOn;

    /**
     * The last time this user was modified
     */
    private String modifiedOn;

    /**
     * If this user has two factor authentication enabled
     */
    private boolean twoFactorAuthenticationEnabled;
    
    @Override
    public String toString() {
        return new ToStringBuilder( this ).append( "id", id ).append( "email", email ).append( "firstName", firstName ).append( "lastName", lastName ).append( "username", username ).append( "telephone", telephone ).append( "country", country ).append( "zipCode", zipCode ).append( "createdOn", createdOn ).append( "modifiedOn", modifiedOn ).append( "twoFactorAuthenticationEnabled", twoFactorAuthenticationEnabled ).toString();
    }
    
}