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


    public Document append(String key, Object value) {
        try {
            if (value == null) {
                return this;
            }
            this.jsonObject.add(key, gson.toJsonTree(value));
        } catch (Exception ignored){
        }
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

    public String getString(String key) {
        if (!this.jsonObject.has(key))
            return "ERROR";
        return this.jsonObject.get(key).getAsString();
    }

    public String getString(String key, String value) {
        if (!this.jsonObject.has(key)) {
            this.jsonObject.addProperty(key, value);
            return value;
        }
        return this.jsonObject.get(key).getAsString();
    }

    public int getInteger(String key) {
        if (!this.jsonObject.has(key))
            return -1;
        return this.jsonObject.get(key).getAsInt();
    }

    public int getInteger(String key, Integer value) {
        if (!this.jsonObject.has(key)) {
            this.jsonObject.addProperty(key, value);
            return value;
        }
        return this.jsonObject.get(key).getAsInt();
    }


    public boolean getBoolean(String key) {
        if (!this.jsonObject.has(key))
            return false;
        return this.jsonObject.get(key).getAsBoolean();
    }

    public boolean getBoolean(String key, Boolean value) {
        if (!this.jsonObject.has(key)) {
            this.jsonObject.addProperty(key, value);
            return value;
        }
        return this.jsonObject.get(key).getAsBoolean();
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

    public JsonObject getJsonObject(String key) {
        return this.jsonObject.get(key).getAsJsonObject();
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
