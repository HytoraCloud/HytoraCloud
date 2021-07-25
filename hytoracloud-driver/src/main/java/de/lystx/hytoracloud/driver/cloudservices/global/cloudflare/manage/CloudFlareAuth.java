
package de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.manage;

import com.google.common.util.concurrent.MoreExecutors;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.objects.json.ZoneSettingDeserializer;
import de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.objects.zone.ZoneSetting;
import de.lystx.hytoracloud.driver.commons.http.client.HttpRestClient;
import de.lystx.hytoracloud.driver.utils.Utils;
import lombok.Getter;
import lombok.NonNull;
import org.apache.http.client.config.CookieSpecs;

import javax.annotation.Nullable;
import java.io.Closeable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;

@Getter
public class CloudFlareAuth implements Closeable {

    /**
     * The api token for email usage
     */
    private String authenticationKey;

    /**
     * The email for token usage
     */
    private String authenticationEmail;

    /**
     * The specified CloudFlare token
     */
    private String authenticationXToken;

    /**
     * The http client for info
     */
    private final HttpRestClient httpRestClient;

    /**
     * The thread pool
     */
    private final ExecutorService threadPool;

    /**
     * The final default values
     */
    public static final ExecutorService DEFAULT_THREAD_POOL = Executors.newFixedThreadPool(100);
    public static final Gson GSON = new GsonBuilder().registerTypeAdapter(ZoneSetting.class, new ZoneSettingDeserializer()).create();

    //Just xToken access
    public CloudFlareAuth(String authenticationXToken) {
        this.authenticationXToken = authenticationXToken;
        this.threadPool = DEFAULT_THREAD_POOL;
        this.httpRestClient = HttpRestClient.builder().baseUrl(Utils.CLOUDFLARE_API_BASE_URL).defaultHeader("Content-Type", "application/json").defaultHeader("Authorization", "Bearer " + this.getAuthenticationXToken()).followRedirect(false).cookieSpec(CookieSpecs.IGNORE_COOKIES).build();
    }

    //Just token and email
    public CloudFlareAuth(String authenticationKey, String authenticationEmail) {
        this.authenticationKey = authenticationKey;
        this.authenticationEmail = authenticationEmail;
        this.threadPool = DEFAULT_THREAD_POOL;
        this.httpRestClient = HttpRestClient.builder().baseUrl(Utils.CLOUDFLARE_API_BASE_URL).defaultHeader("Content-Type", "application/json").defaultHeader("X-Auth-Key", this.getAuthenticationKey()).defaultHeader("X-Auth-Email", this.getAuthenticationEmail()).followRedirect(false).cookieSpec(CookieSpecs.IGNORE_COOKIES).build();

    }

    //All values access
    public CloudFlareAuth(String authenticationKey, String authenticationEmail, ExecutorService threadPool, String apiBaseUrl) {
        this.authenticationKey = authenticationKey;
        this.authenticationEmail = authenticationEmail;
        this.threadPool = threadPool;
        this.httpRestClient = HttpRestClient.builder().baseUrl(apiBaseUrl).defaultHeader("Content-Type", "application/json").defaultHeader("X-Auth-Key", this.getAuthenticationKey()).defaultHeader("X-Auth-Email", this.getAuthenticationEmail()).followRedirect(false).cookieSpec(CookieSpecs.IGNORE_COOKIES).build();
    }

    /**
     * Gets the current thread pool
     *
     * @return executor service
     */
    public ExecutorService getThreadPool() {
        return threadPool == null ? DEFAULT_THREAD_POOL : threadPool;
    }

    /**
     * Closes this client access
     *
     * @param timeout the timeout
     * @param unit the unit
     */
    public void close(long timeout,TimeUnit unit) {
        this.httpRestClient.close();
        if (threadPool != null)
            MoreExecutors.shutdownAndAwaitTermination(threadPool, timeout, checkNotNull(unit));
    }

    @Override
    public void close() {
        close(4, TimeUnit.SECONDS);
    }
}
