package de.lystx.cloudsystem.library.elements.other;

import com.google.gson.*;
import io.vson.elements.object.VsonMember;
import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonSettings;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Getter @Setter
public class Document {

    private final Gson gson;
    private final File file;
    private final JsonParser parser;

    private JsonObject jsonObject;

    public Document() {
        this(new JsonObject(), null);
    }

    public Document(File file) {
        this(new JsonObject(), file, null);
    }

    public Document(JsonObject object) {
        this(object, null, null);
    }

    public Document(String input) {
        this(new JsonObject(), null, input);
    }

    public Document(JsonObject object, File file) {
        this(object, file, null);
    }

    public Document(JsonObject object, File file, String input) {
        this.jsonObject = object;
        this.parser = new JsonParser();
        this.gson = (new GsonBuilder()).serializeNulls().setPrettyPrinting().disableHtmlEscaping().create();
        this.file = file;
        if (file != null) {
            try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
                jsonObject = parser.parse(new BufferedReader(reader)).getAsJsonObject();
            } catch (Exception ignored) {

            }
        }
        if (input != null) {
            JsonElement jsonElement;
            try {
                jsonElement = parser.parse(input);
            } catch (Exception e) {
                e.printStackTrace();
                jsonElement = new JsonObject();
            }
            this.jsonObject = jsonElement.getAsJsonObject();
        }
    }


    public Document append(String key, String value) {
        this.jsonObject.addProperty(key, value);
        return this;
    }

    public Document append(String key, Number value) {
        this.jsonObject.addProperty(key, value);
        return this;
    }

    public Document append(String key, Boolean value) {
        this.jsonObject.addProperty(key, value);
        return this;
    }

    public Document append(String key, JsonElement value) {
        this.jsonObject.add(key, value);
        return this;
    }

    public Document append(String key, Object value) {
        try {
            if (value == null) {
                return this;
            }
            this.jsonObject.add(key, gson.toJsonTree(value));
        } catch (NullPointerException e){}

        return this;
    }

    public Document append(Object value) {
        if (value == null) {
            return this;
        }
        this.jsonObject = gson.toJsonTree(value).getAsJsonObject();
        return this;
    }

    public Document remove(String key) {
        this.jsonObject.remove(key);
        return this;
    }

    public List<String> keys() {
        List<String> c = new LinkedList<>();
        for (Map.Entry<String, JsonElement> x : this.jsonObject.entrySet())
            c.add(x.getKey());
        return c;
    }

    public Set<Object> values() {
        Set<Object> v = new HashSet<>();
        for (Map.Entry<String, JsonElement> x : this.jsonObject.entrySet())
            v.add(x.getValue());
        return v;
    }

    public String getString(String key) {
        if (!this.jsonObject.has(key))
            return "ERROR";
        return this.jsonObject.get(key).getAsString();
    }

    public String getString(String key, String value) {
        if (!this.jsonObject.has(key)) {
            this.append(key, value);
            return value;
        }
        return this.jsonObject.get(key).getAsString();
    }

    public JsonElement getElement(String key) {
        return this.jsonObject.get(key);
    }

    public int getInteger(String key) {
        if (!this.jsonObject.has(key))
            return -1;
        return this.jsonObject.get(key).getAsInt();
    }

    public int getInteger(String key, Integer value) {
        if (!this.jsonObject.has(key)) {
            this.append(key, value);
            return value;
        }
        return this.jsonObject.get(key).getAsInt();
    }

    public long getLong(String key) {
        if (!this.jsonObject.has(key))
            return -1L;
        return this.jsonObject.get(key).getAsLong();
    }

    public long getLong(String key, Long value) {
        if (!this.jsonObject.has(key)) {
            this.append(key, value);
            return value;
        }
        return this.jsonObject.get(key).getAsLong();
    }

    public double getDouble(String key) {
        if (!this.jsonObject.has(key))
            return -1D;
        return this.jsonObject.get(key).getAsDouble();
    }

    public double getDouble(String key, java.lang.Double value) {
        if (!this.jsonObject.has(key)) {
            this.append(key, value);
            return value;
        }
        return this.jsonObject.get(key).getAsDouble();
    }

    public boolean getBoolean(String key) {
        if (!this.jsonObject.has(key))
            return false;
        return this.jsonObject.get(key).getAsBoolean();
    }

    public boolean getBoolean(String key, Boolean value) {
        if (!this.jsonObject.has(key)) {
            this.append(key, value);
            return value;
        }
        return this.jsonObject.get(key).getAsBoolean();
    }

    public float getFloat(String key) {
        if (!this.jsonObject.has(key))
            return 0.0F;
        return this.jsonObject.get(key).getAsFloat();
    }

    public float getFloat(String key, Float value) {
        if (!this.jsonObject.has(key)) {
            this.append(key, value);
            return value;
        }
        return this.jsonObject.get(key).getAsFloat();
    }

    public short getShort(String key) {
        if (!this.jsonObject.has(key))
            return (short)-1;
        return this.jsonObject.get(key).getAsShort();
    }

    public short getShort(String key, short value) {
        if (!this.jsonObject.has(key)) {
            this.append(key, value);
            return value;
        }
        return this.jsonObject.get(key).getAsShort();
    }

    public Document getDocument(String key, Document def) {
        if (this.has(key)) {
            return new Document(this.jsonObject.get(key).getAsJsonObject());
        }
        this.append(key, def.getJsonObject());
        return def;
    }

    public Document append(String key, Document value) {
        this.jsonObject.add(key, value.getJsonObject());
        return this;
    }

    public boolean has(String key) {
        return this.jsonObject.has(key);
    }

    public Boolean isEmpty() {
        return this.keys().isEmpty();
    }

    public void clear() {
        for (String key : this.keys()) {
            this.remove(key);
        }
    }

    public JsonArray getJsonArray(String key) {
        if (!this.jsonObject.has(key)) {
            return new JsonArray();
        }
        return this.jsonObject.get(key).getAsJsonArray();
    }

    public JsonObject getJsonObject(String key) {
        return this.jsonObject.get(key).getAsJsonObject();
    }

    public List<String> getList(String key) {
        List<String> result = new LinkedList<>();
        for (JsonElement element : this.getJsonArray(key)) {
            if (element instanceof JsonPrimitive && ((JsonPrimitive) element).isString()) {
                result.add(element.getAsString());
            }
        }
        return result;
    }

    public <T> List<T> getList(Class<T> tClass) {
        List<T> result = new LinkedList<>();
        for (JsonElement element : this.getAs(JsonArray.class)) {
            if (element instanceof JsonObject) {
                result.add((T)new Document().getObject(element.getAsJsonObject(), tClass));
            }
        }
        return result;
    }

    public <T> List<T> getList(String key, Class<T> getAs) {
        List<T> result = new LinkedList<>();
        for (JsonElement element : this.getJsonArray(key)) {
            if (element instanceof JsonObject) {
                result.add((T)new Document().getObject(element.getAsJsonObject(), getAs));
            }
        }
        return result;
    }

    public Document getDocument(String key) {
        try {
            return new Document(this.jsonObject.get(key).getAsJsonObject());
        } catch (NullPointerException ex) {
            this.append(key, new Document());
            return new Document(this.jsonObject.get(key).getAsJsonObject());
        }
    }

    public <T> T getObject(JsonObject jsonObject, Class<T> tClass) {
        if (jsonObject == null) {
            return null;
        }
        return gson.fromJson(jsonObject, tClass);
    }

    public <T> T getAs(Class<T> tClass) {
        return this.getObject(this.getJsonObject(), tClass);
    }

    public <T> T getObject(String key, Class<T> tClass) {
        return this.getObject(this.getJsonObject(key), tClass);
    }

    public <T> T getObject(String key, Type type) {
        return this.gson.fromJson(this.jsonObject.get(key), type);
    }

    public <T> T getObject(String key, T def) {
        if (!this.jsonObject.has(key)) {
            this.append(key, def);
        }
        return this.getObject(key, (Class<T>) def.getClass());
    }

    public static Document fromFile(File file) {
        return new Document(file);
    }

    public VsonObject toVson() {
        VsonObject vsonObject = new VsonObject(VsonSettings.CREATE_FILE_IF_NOT_EXIST);
        for (String key : this.keys()) {
            vsonObject.append(key, this.get(key));
        }
        return vsonObject;
    }

    public void save() {
        this.save(this.file);
    }

    public void save(File file) {
        try (PrintWriter w = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8), true)) {
            w.print(gson.toJson(this.getJsonObject()));
            w.flush();
        } catch (Exception ignored) {

        }
    }

    public String toString() {
        return gson.toJson(this.getJsonObject());
    }

    public Object get(String s) {
        return this.jsonObject.get(s);
    }
}
