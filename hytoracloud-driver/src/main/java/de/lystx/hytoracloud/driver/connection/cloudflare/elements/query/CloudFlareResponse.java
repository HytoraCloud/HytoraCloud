
package de.lystx.hytoracloud.driver.connection.cloudflare.elements.query;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.lystx.hytoracloud.driver.connection.cloudflare.elements.CloudFlareResult;
import de.lystx.hytoracloud.driver.utils.json.JsonDocument;
import lombok.Getter;

import java.util.*;


@Getter
public class CloudFlareResponse<T> {
    
    /**
     * The whole json object.
     * Is null when failed.
     */
    private final JsonObject json;
    
    /**
     * The "result" property in the json response parsed as the given type.
     */
    private final T object;
    
    /**
     * If the request was successful.
     */
    private final boolean successful;
    
    /**
     * The returned status code
     */
    private final int status;
    
    /**
     * The returned status text
     */
    private final String message;
    
    /**
     * Messages.
     */
    private final List<String> messages;
    
    /**
     * A list of errors -> error message by error code.
     */
    private final Map<Integer, String> errors;
    
    /**
     * Some info about the returned result regarding the pagination.
     */
    private final CloudFlareResult resultInfo;

    public CloudFlareResponse(JsonObject json, T object, boolean successful, int status, String message) {
        this.json = json;
        this.successful = successful;
        this.status = status;
        this.message = message;
        this.object = object;
        this.errors = new HashMap<>();
        this.messages = new LinkedList<>();

        this.resultInfo = this.json.has("result_info") ? JsonDocument.GSON.fromJson(json.getAsJsonObject("result_info"), CloudFlareResult.class) : null;

        // Errors
        JsonObject jsonObject;
        if (this.json.has("errors")) {
            for (JsonElement element : this.json.getAsJsonArray("errors")) {
                jsonObject = element.getAsJsonObject();
                errors.put( jsonObject.get( "code" ).getAsInt(), jsonObject.get( "message" ).getAsString() );
            }
        }
    }

    /**
     * Transforms object into a list
     * @return list
     */
    public List<T> asList() {
        if (this.object instanceof List) {
            return (List<T>)this.object;
        }
        return Collections.singletonList(this.object);
    }
}
