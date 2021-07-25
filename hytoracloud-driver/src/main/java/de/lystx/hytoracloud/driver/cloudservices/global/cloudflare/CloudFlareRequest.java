
package de.lystx.hytoracloud.driver.cloudservices.global.cloudflare;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.constants.CloudFlareAction;
import de.lystx.hytoracloud.driver.cloudservices.global.cloudflare.manage.CloudFlareAuth;
import de.lystx.hytoracloud.driver.commons.http.utils.HttpRequestType;
import de.lystx.hytoracloud.driver.commons.http.utils.HttpResponse;
import de.lystx.hytoracloud.driver.commons.storage.JsonDocument;
import de.lystx.hytoracloud.driver.utils.Utils;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkNotNull;

@Getter
public class CloudFlareRequest {
    
    /**
     * The auth login
     */
    private CloudFlareAuth cloudFlareAuth;

    /**
     * The type of this request
     */
    private HttpRequestType httpMethod;

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
     * Sets the additional path which will be appended on
     * Not usable when {@link CloudFlareAction} was given.
     *
     * @param additionalPath the path
     * @return current request
     */
    public CloudFlareRequest additionalPath(String additionalPath) {

        if (additionalPath.startsWith( "/" )) {
            additionalPath = additionalPath.substring(1);
        }

        this.additionalPath = additionalPath;
        return this;
    }

    /**
     * Sets object getting to async
     * @return current request
     */
    public CloudFlareRequest async() {
        this.async = true;
        return this;
    }

    public CloudFlareRequest queryString(String parameter, Object value ) {
        queryStrings.put(checkNotNull( parameter, "invalid query string" ), checkNotNull( value, "invalid query string" ) );
        return this;
    }

    public CloudFlareRequest identifiers(String... orderedIdentifiers) {
        Collections.addAll(this.orderedIdentifiers, orderedIdentifiers);
        return this;
    }
    /**
     * Sets the body of this request
     *
     * @param body the body
     * @return request
     */
    public CloudFlareRequest body(JsonObject body) {
        this.body = body;
        return this;
    }

    public CloudFlareRequest body(Object obj) {
        return this.body(new JsonDocument().append(obj).build());
    }

    public CloudFlareRequest body(String s) {
        return this.body(new JsonDocument(s).build());
    }


    /**
     * Sends the request
     *
     * @return response
     */
    private HttpResponse<String> sendRequest() {
        switch (httpMethod) {
            case GET:
                return cloudFlareAuth.getHttpRestClient().get(categoryPath()).queryString(queryStrings).asString();
            case POST:
                return cloudFlareAuth.getHttpRestClient().post(categoryPath()).queryString(queryStrings).body(body.toString()).asString();
            case DELETE:
                return cloudFlareAuth.getHttpRestClient().delete(categoryPath()).queryString(queryStrings).body(body.toString()).asString();
            case PUT:
                return cloudFlareAuth.getHttpRestClient().put(categoryPath()).queryString(queryStrings).body(body.toString()).asString();
            case PATCH:
                return cloudFlareAuth.getHttpRestClient().patch(categoryPath()).queryString(queryStrings).body(body.toString()).asString();
            default:
                throw new IllegalStateException("Should never happen because other http methods are blocked.");
        }
    }

    /**
     * Loads the response of this request
     *
     * @return pair of response and json data
     */
    private Pair<HttpResponse<String>, JsonObject> response() {
        if (response == null) {
            HttpResponse<String> httpResponse = this.sendRequest();
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

    public <T> T get(Class<T> tClass) {
        return (T) this.asObject(tClass);
    }

    /**
     * Sends request. Parses the json result as the object type.
     *
     * @param objectType class of object
     * @param <T>        type of object
     * @return CloudflareResponse<T>
     */
    @SneakyThrows
    public <T> CloudFlareResponse<T> asObject(Class<T> objectType) {
        if (async) {
            async = false;
            return CompletableFuture.supplyAsync(() -> this.asObject(objectType), getCloudFlareAuth().getThreadPool()).get();
        }
        HttpResponse<String> httpResponse = response().getLeft();
        JsonObject json = response().getRight();
        if (json.get("result").isJsonObject() ) {
            return new CloudFlareResponse<>(json, CloudFlareAuth.GSON.fromJson(json.getAsJsonObject("result"), objectType), httpResponse.isSuccessful(), httpResponse.getStatus(), httpResponse.getMessage());
        } else if (json.get("result").isJsonArray()) {
            throw new IllegalStateException("Property 'result' is not a json object, because it is a json array use asObjectList() instead of asObject().");
        }
        return new CloudFlareResponse<>(json, null, httpResponse.isSuccessful(), httpResponse.getStatus(), httpResponse.getMessage());
    }

    /**
     * Sends the request and it might return
     * a list of objects or just one object of the given type
     *
     * @param objectType the type
     * @param <T> the generic
     * @return response
     */
    @SneakyThrows
    public <T> CloudFlareResponse<T> asCollection(Class<T> objectType ) {
        if (async) {
            async = false;
            return CompletableFuture.supplyAsync(() -> this.asCollection(objectType), getCloudFlareAuth().getThreadPool()).get();
        }
        JsonObject json = response().getRight();
        HttpResponse<String> httpResponse = response().getLeft();

        T object;
        // Check if result is json array.
        if (json.get("result").isJsonArray() ) {
            // Map object from json array to object list.
            object = (T) Utils.toListOfObjects(json.getAsJsonArray( "result" ), objectType);
        } else if (json.get("result").isJsonObject() )
            // json is a json object and the object is not mapped in a List
            object = CloudFlareAuth.GSON.fromJson(json.getAsJsonObject("result"), objectType);
        else object = null;

        // Return the response
        return new CloudFlareResponse<>(json, object, httpResponse.isSuccessful(), httpResponse.getStatus(), httpResponse.getMessage());
    }

    /**
     * Sends request. Parses and maps all entries in the json array result as a List<object type>.
     *
     * @param objectType class of object
     * @param <T>        type of object
     * @return CloudflareResponse
     */
    public <T> CloudFlareResponse<List<T>> asList(Class<T> objectType ) {
        JsonObject json = response().getRight();
        HttpResponse<String> httpResponse = response().getLeft();
        
        if (json.get("result").isJsonArray()) {
            return new CloudFlareResponse<>(json, Utils.toListOfObjects(json.getAsJsonArray("result"), objectType), httpResponse.isSuccessful(), httpResponse.getStatus(), httpResponse.getMessage());
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

    //Forwarding to other
    private <T> void syncCallback( CloudflareCallback<CloudFlareResponse<T>> callback, Callable<CloudFlareResponse<T>> getResponse) {
        runCallback(callback, getResponse);
    }

    //Async forwarding
    private <T> void asyncCallback(CloudflareCallback<CloudFlareResponse<T>> callback, Callable<CloudFlareResponse<T>> getResponse) {
        ListenableFuture<CloudFlareResponse<T>> future = MoreExecutors.listeningDecorator( cloudFlareAuth.getThreadPool() ).submit(getResponse);
        future.addListener( ( ) -> runCallback( callback, future::get ), cloudFlareAuth.getThreadPool());
    }

    /**
     * INTERNAL HELPER METHOD!
     *
     * @param callback    user'S callback
     * @param getResponse .call() is returning the CloudflareResponse<T>
     * @param <T>         type of "object" in CloudflareResponse
     */
    public <T> void runCallback( CloudflareCallback<CloudFlareResponse<T>> callback, Callable<CloudFlareResponse<T>> getResponse ) {
        HttpResponse<String> httpResponse = response().getLeft();
        JsonObject json = response().getRight();

        Throwable throwable;
        try {
            CloudFlareResponse<T> response = getResponse.call();
            // http request successful

            // check "success" state in json -> cloudflare couldn't find the result
            if ( !response.isSuccessful() ) {
                throw new IllegalStateException( "" );
            }

            try { // Don't run onFailure when onSuccess throws an exception.
                callback.onSuccess( response );
            } catch ( Exception | Error e ) {
                e.printStackTrace();
            }
            return;
        } catch ( ExecutionException e ) {
            throwable = e.getCause();
        } catch ( Exception | Error e ) {
            throwable = e;
        }
        // Errors passed by Cloudflare
        Map<Integer, String> errors = new HashMap<>();

        JsonObject o;
        for ( JsonElement e : json.getAsJsonArray( "errors" ) ) {
            o = e.getAsJsonObject();
            errors.put( o.get( "code" ).getAsInt(), o.get( "message" ).getAsString() );
        }

        callback.onFailure( throwable, httpResponse.getStatus(), httpResponse.getMessage(), errors );
    }

    public void send(Consumer<CloudFlareResponse<?>> consumer) {
        HttpResponse<String> httpResponse = response().getLeft();
        JsonObject json = response().getRight();



    }
}
