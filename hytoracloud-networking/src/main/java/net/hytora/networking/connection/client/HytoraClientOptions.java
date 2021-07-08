package net.hytora.networking.connection.client;

import lombok.Getter;
import lombok.Setter;

@Getter
public class HytoraClientOptions {

    /**
     * The delay to retry the connection
     */
    private int retryDelay = 100;

    /**
     * The amount of maximum retries
     */
    private int maxRetry = 20;

    /**
     * If debug is enabled
     */
    private boolean debug = false;

    /**
     * Sets the retry delay for connections
     *
     * @param retryDelay the delay
     * @return current options
     */
    public HytoraClientOptions setRetryDelay(int retryDelay) {
        this.retryDelay = retryDelay;
        return this;
    }

    /**
     * Enables or disables debug mode
     *
     * @param debug the debug
     * @return current options
     */
    public HytoraClientOptions setDebug(boolean debug) {
        this.debug = debug;
        return this;
    }

    /**
     * Sets the maximum retries for connections
     *
     * @param maxRetry the amount
     * @return current options
     */
    public HytoraClientOptions setMaxRetry(int maxRetry) {
        this.maxRetry = maxRetry;
        return this;
    }
}
