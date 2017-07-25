package com.arcsoft.supervisor.commons.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.apache.commons.io.output.StringBuilderWriter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Singleton pattern implementation for {@link com.fasterxml.jackson.databind.ObjectMapper}.
 *
 * @author zw.
 */
public class JsonMapper {

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    private JsonMapper() {
    }

    public static ObjectMapper getMapper() {
        return mapper;
    }

    /**
     * Construct a <code>CollectionType</code> instance with specified <code>elementClass</code>.
     *
     * @param elementClass the element class
     * @return a <code>list</code> collection type instance
     */
    public static CollectionType constructListType(Class<?> elementClass) {
        return getMapper().getTypeFactory().constructCollectionType(List.class, elementClass);
    }

    /**
     * Re-create a newly string of json with given rootNode and existed nodes.
     *
     * @param rootNode     the root node of json string
     * @param existedNodes the existed node
     * @return a newly json compose by existed nodes
     * @throws IOException
     */
    public static String composeExistedNodesAsJson(JsonNode rootNode, String... existedNodes) throws IOException {
        StringBuilderWriter writer = new StringBuilderWriter();
        try (JsonGenerator generator = mapper.getFactory().createGenerator(writer)) {
            generator.writeStartObject();
            for (String existedNode : existedNodes) {
                generator.writeObjectField(existedNode, rootNode.findPath(existedNode));
            }
            generator.writeEndObject();
        }
        return writer.toString();
    }

    /**
     * Re-create a newly string of json with given json string and existed nodes.
     * @param json the string of json
     * @param existedNodes the existed nodes
     * @return a newly json compose by existed nodes
     * @throws IOException
     */
    public static String composeExistedNodesAsJson(String json, String... existedNodes) throws IOException {
        return composeExistedNodesAsJson(getMapper().readTree(json), existedNodes);
    }

    /**
     * Converts the given {@code values} to string of json.
     *
     * @param values the key-value pair.the key will used as json key and the value will be the value of key
     * @return a string of json
     * @throws IOException
     */
    public static String toJson(Map<String, Object> values) throws IOException {
        StringBuilderWriter writer = new StringBuilderWriter();
        try (JsonGenerator generator = mapper.getFactory().createGenerator(writer)) {
            generator.writeStartObject();
            for (Map.Entry<String, Object> entry : values.entrySet()) {
                generator.writeObjectField(entry.getKey(), entry.getValue());
            }
            generator.writeEndObject();
        }
        return writer.toString();
    }

}
