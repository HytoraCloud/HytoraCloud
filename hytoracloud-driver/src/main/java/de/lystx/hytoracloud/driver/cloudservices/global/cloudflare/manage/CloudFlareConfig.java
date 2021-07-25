
package de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.manage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.util.concurrent.ExecutorService;


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

