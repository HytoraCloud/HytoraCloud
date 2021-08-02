package de.lystx.hytoracloud.driver.commons.http.mapper;

import de.lystx.hytoracloud.driver.commons.http.mapper.impl.JsonMapper;
import de.lystx.hytoracloud.driver.commons.http.mapper.impl.TextPlainMapper;
import de.lystx.hytoracloud.driver.commons.http.requests.RequestType;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class ObjectMappers {

    /**
     * All registered {@link ObjectMapper}s
     */
    @Getter
    private static final Map<RequestType, ObjectMapper> REGISTERED_MAPPERS = new HashMap<>();

    /**
     * Gets an {@link ObjectMapper} from type
     *
     * @param type the type
     * @return mapper
     */
    public static ObjectMapper getMapper(RequestType type) {
        if (REGISTERED_MAPPERS.isEmpty()) {
            synchronized (REGISTERED_MAPPERS) {
                if (REGISTERED_MAPPERS.isEmpty()) {
                    ObjectMappers.REGISTERED_MAPPERS.put(RequestType.TEXT_PLAIN_TYPE, new TextPlainMapper());
                    ObjectMappers.REGISTERED_MAPPERS.put(RequestType.APPLICATION_JSON_TYPE, new JsonMapper());
                }
            }
        }
        ObjectMapper mapper = REGISTERED_MAPPERS.get(type);
        if (mapper == null) {
            mapper = REGISTERED_MAPPERS.entrySet().stream().filter(kv -> kv.getKey().isCompatible(type)).map(Map.Entry::getValue).findFirst().get();
        }
        return mapper;
    }

}
