
package de.lystx.hytoracloud.driver.connection.cloudflare.elements.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter @Setter @AllArgsConstructor
public class CloudFlareConfig {


    /**
     * The api token for email usage
     */
    private String xAuthKey;

    /**
     * The email for token usage
     */
    private String xAuthEmail;

    /**
     * The specified CloudFlare token
     */
    private String xAuthToken;


}

