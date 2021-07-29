
package de.lystx.hytoracloud.driver.cloudservices.global.cloudflare;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.elements.enums.CloudFlareAction;
import de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.elements.config.CloudFlareAuth;
import de.lystx.hytoracloud.driver.commons.http.utils.HttpRequestType;
import de.lystx.hytoracloud.driver.commons.http.utils.HttpResponse;
import de.lystx.hytoracloud.driver.commons.storage.JsonDocument;
import de.lystx.hytoracloud.driver.utils.Utils;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static com.google.common.base.Preconditions.checkNotNull;

@Getter
public class CloudFlareRequest<T> {
    
    /**
     * The auth login
     */
    private CloudFlareAuth cloudFlareAuth;

    /**
     * The type of this request
     */
    private final HttpRequestType httpMethod;

    /**
     * Any additional url path
     */
    private String additionalPath;

    /**
     * The identifiers for this request
     */
    private final List<String> orderedIdentifiers = new LinkedList<>();

    /**
     * All queries for this request
     */
    private final Map<String, Object> queryStrings = new HashMap<>();

    /**
     * The body object
     */
    private JsonObject body = new JsonObject();

    /**
     * If object should get async
     */
    private boolean async;

    /**
     * The response
     */
    private Pair<HttpResponse<String>, JsonObject> response;

    /**
     * The type class of this request
     */
    private Class<T> typeClass;

    /**
     * Creates a request for a given category
     * @param cloudFlareAction the category
     */
    public CloudFlareRequest(CloudFlareAction cloudFlareAction) {
        this.httpMethod = cloudFlareAction.getHttpMethod();
        this.additionalPath(cloudFlareAction.getAdditionalPath());

    }

    public CloudFlareRequest(CloudFlareAction cloudFlareAction, CloudFlareAuth auth) {
        this(cloudFlareAction);
        this.cloudFlareAuth = auth;

    }

    /**
     * Sets the type class for this request
     * @param typeClass the class
     * @return current request
     */
    public CloudFlareRequest<T> typeClass(Class<T> typeClass) {
        this.typeClass = typeClass;
        return this;
    }

    /**
     * Sets the additional path which will be appended on
     * Not usable when {@link CloudFlareAction} was given.
     *
     * @param additionalPath the path
     * @return current request
     */
    public CloudFlareRequest<T> additionalPath(String additionalPath) {

        if (additionalPath.startsWith( "/" )) {
            additionalPath = additionalPath.substring(1);
        }

        this.additionalPath = additionalPath;
        return this;
    }

    /**
     * Sets object getting to async
     *
     * @return current request
     */
    public CloudFlareRequest<T> async() {
        this.async = true;
        return this;
    }

    /**
     * Puts a query string into this request
     *
     * @param parameter the parameter
     * @param value the value
     * @return current request
     */
    public CloudFlareRequest<T> queryString(String parameter, Object value ) {
        queryStrings.put(parameter, value);
        return this;
    }

    /**
     * Adds identifiers to this request
     *
     * @param orderedIdentifiers all identifiers
     * @return current request
     */
    public CloudFlareRequest<T> identifiers(String... orderedIdentifiers) {
        Collections.addAll(this.orderedIdentifiers, orderedIdentifiers);
        return this;
    }

    /**
     * Sets the body of this request
     *
     * @param body the body
     * @return request
     */
    public CloudFlareRequest<T> body(JsonObject body) {
        this.body = body;
        return this;
    }

    public CloudFlareRequest<T> body(T object) {
        return this.body(new JsonDocument().append(object).toString());
    }

    public CloudFlareRequest<T> body(String s) {
        return this.body(new JsonDocument(s).getJsonObject());
    }

    /**
     * Loads the response of this request
     *
     * @return pair of response and json data
     */
    private Pair<HttpResponse<String>, JsonObject> response() {
        if (response == null) {
            HttpResponse<String> httpResponse = null;

            if (httpMethod == HttpRequestType.GET) {
                httpResponse = cloudFlareAuth.getHttpRestClient().get(categoryPath()).queryString(queryStrings).asString();
            } else if (httpMethod == HttpRequestType.POST) {
                httpResponse = cloudFlareAuth.getHttpRestClient().post(categoryPath()).queryString(queryStrings).body(body.toString()).asString();
            } else if (httpMethod == HttpRequestType.DELETE) {
                httpResponse = cloudFlareAuth.getHttpRestClient().delete(categoryPath()).queryString(queryStrings).body(body.toString()).asString();
            } else if (httpMethod == HttpRequestType.PUT) {
                httpResponse = cloudFlareAuth.getHttpRestClient().put(categoryPath()).queryString(queryStrings).body(body.toString()).asString();
            } else if (httpMethod == HttpRequestType.PATCH) {
                httpResponse = cloudFlareAuth.getHttpRestClient().patch(categoryPath()).queryString(queryStrings).body(body.toString()).asString();
            }

            if (httpResponse == null) {
                throw new IllegalStateException("Could not parse HttpRequestType!");
            }
            JsonElement parsed = new JsonParser().parse(httpResponse.getBody());
            if (parsed.isJsonNull()) {
                throw new IllegalStateException("Could not parse returned text as json.");
            }
            response = Pair.of(httpResponse, parsed.getAsJsonObject());
        }
        return response;
    }

    /**
     * Sends request. No object mapping and/or object parsing will be handled.
     *
     * @return CloudflareResponse<Void>
     */
    @SneakyThrows
    public CloudFlareResponse<Void> asVoid() {
        if (this.async) {
            this.async = false;
            return CompletableFuture.supplyAsync(this::asVoid, getCloudFlareAuth().getThreadPool()).get();
        }
        HttpResponse<String> httpResponse = response().getLeft();
        JsonObject json = response().getRight();
        return new CloudFlareResponse<>(json, null, httpResponse.isSuccessful(), httpResponse.getStatus(), httpResponse.getMessage());
    }

    /**
     * Sends request. Parses the json result as the object type.
     *
     * @return CloudflareResponse<T>
     */
    @SneakyThrows
    public CloudFlareResponse<T> asObject() {
        if (async) {
            async = false;
            return CompletableFuture.supplyAsync(this::asObject, getCloudFlareAuth().getThreadPool()).get();
        }
        HttpResponse<String> httpResponse = response().getLeft();
        JsonObject json = response().getRight();
        if (json.get("result").isJsonObject() ) {
            return new CloudFlareResponse<>(json, CloudFlareAuth.GSON.fromJson(json.getAsJsonObject("result"), typeClass), httpResponse.isSuccessful(), httpResponse.getStatus(), httpResponse.getMessage());
        } else if (json.get("result").isJsonArray()) {
            throw new IllegalStateException("Property 'result' is not a json object, because it is a json array use asObjectList() instead of asObject().");
        }
        return new CloudFlareResponse<>(json, null, httpResponse.isSuccessful(), httpResponse.getStatus(), httpResponse.getMessage());
    }

    /**
     * Sends the request and it might return
     * a list of objects or just one object of the given type
     * @return response
     */
    @SneakyThrows
    public CloudFlareResponse<T> singleTonOrList() {
        if (async) {
            async = false;
            return CompletableFuture.supplyAsync(this::singleTonOrList, getCloudFlareAuth().getThreadPool()).get();
        }
        JsonObject json = response().getRight();
        HttpResponse<String> httpResponse = response().getLeft();

        T object;
        // Check if result is json array.
        if (json.get("result").isJsonArray() ) {
            // Map object from json array to object list.
            object = (T) toListOfObjects(json.getAsJsonArray( "result" ), typeClass);
        } else if (json.get("result").isJsonObject() )
            // json is a json object and the object is not mapped in a List
            object = CloudFlareAuth.GSON.fromJson(json.getAsJsonObject("result"), typeClass);
        else object = null;

        // Return the response
        return new CloudFlareResponse<>(json, object, httpResponse.isSuccessful(), httpResponse.getStatus(), httpResponse.getMessage());
    }

    //Internal method to util
    public static  <T> List<T> toListOfObjects(JsonArray jsonArray, Class<T> objectType) {
        return CloudFlareAuth.GSON.fromJson(jsonArray, new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[]{objectType};
            }

            @Override
            public Type getRawType() {
                return List.class;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        });
    }

    /**
     * Sends request. Parses and maps all entries in the json array result as a List<object type>.
     *
     * @return CloudflareResponse
     */
    public CloudFlareResponse<List<T>> asList() {
        JsonObject json = response().getRight();
        HttpResponse<String> httpResponse = response().getLeft();
        
        if (json.get("result").isJsonArray()) {
            return new CloudFlareResponse<>(json, toListOfObjects(json.getAsJsonArray("result"), typeClass), httpResponse.isSuccessful(), httpResponse.getStatus(), httpResponse.getMessage());
        } else if (json.get("result").isJsonObject()) {
            throw new IllegalStateException("Property 'result' is not a json array, because it is a json object use asObject() instead of asObjectList().");
        }
        return new CloudFlareResponse<>(json, null, httpResponse.isSuccessful(), httpResponse.getStatus(), httpResponse.getMessage());
    }

    //Gets the formatted path for url
    private String categoryPath() {
        String additionalCategoryPath = checkNotNull( additionalPath, "you have to specify the additional path" );
        
        // pattern is like 'foo/{id-1}/bar/{id-2}'
        for ( int place = 1; place <= orderedIdentifiers.size(); place++ ) {
            additionalCategoryPath = additionalCategoryPath.replace("{id-" + place + "}", orderedIdentifiers.get(place - 1));
        }
        
        return additionalCategoryPath;
    }

}
