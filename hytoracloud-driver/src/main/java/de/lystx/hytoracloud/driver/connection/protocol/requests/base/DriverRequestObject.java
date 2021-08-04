package de.lystx.hytoracloud.driver.connection.protocol.requests.base;


import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.utils.json.JsonObject;
import de.lystx.hytoracloud.driver.utils.json.PropertyObject;
import de.lystx.hytoracloud.driver.utils.other.RandomString;
import lombok.Getter;
import lombok.SneakyThrows;

@Getter
public class DriverRequestObject<T> implements DriverRequest<T> {


    private static final long serialVersionUID = -7487054913515679386L;
    /**
     * The key of the request
     */
    private final String key;

    /**
     * The id of the request
     */
    private final String id;

    /**
     * The target
     */
    private String target;

    /**
     * The type class
     */
    private String typeClass;

    /**
     * The data of this document
     */
    private PropertyObject document;

    DriverRequestObject(String key) {
        this(key, new RandomString(5).next(), null, JsonObject.serializable());
    }

    DriverRequestObject(String key, String id, String target, JsonObject<?> document) {
        this.key = key;
        this.target = target;
        this.id = id;
        this.document = (PropertyObject) document;
    }

    DriverRequestObject(String key, JsonObject<?> document) {
        this(key, new RandomString(5).next(), null, document);
    }

    DriverRequestObject(String key, String target, JsonObject<?> document) {
        this(key, new RandomString(5).next(), target, document);
    }

    public JsonObject<?> getDocument() {
        return document;
    }

    /**
     * Creates a response for this request
     *
     * @param vClass the class type
     * @param <V> the gener c
     * @return response
     */
    @Override
    public <V> DriverResponse<V> createResponse(Class<V> vClass) {
        DriverResponseObject<V> response = new DriverResponseObject<>();
        response.id(this.id);
        response.success(true);
        response.typeClass(vClass);
        return response;
    }

    @Override
    public <V> DriverResponse<V> createResponse() {
        return (DriverResponse<V>) this.createResponse(this.typeClass());
    }

    @Override
    public DriverRequest<T> append(String key, Object value) {
        this.document.append(key, value);
        return this;
    }

    @Override
    public DriverRequest<T> json(JsonObject<?> jsonData) {
        this.document = new PropertyObject(jsonData.toString());
        return this;
    }

    @Override
    public DriverRequest<T> target(String target) {
        this.target = target;
        return this;
    }

    @Override @SneakyThrows
    public Class<T> typeClass() {
        return (Class<T>) Class.forName(typeClass);
    }

    @Override
    public DriverRequest<T> typeClass(Class<?> typeClass) {
        this.typeClass = typeClass.getName();
        return this;
    }

    /**
     * Submits and sends this request
     *
     * @return future
     */
    @Override
    public DriverQuery<T> execute() {
        DriverQuery<T> simpleQuery = new DriverQueryObject<>(this);

        CloudDriver.getInstance().getMessageManager().sendChannelMessage(CloudDriver.getInstance().getRequestManager().toMessage(this));
        CloudDriver.getInstance().getRequestManager().addRequest(this.id, simpleQuery);
        return simpleQuery;
    }

}
