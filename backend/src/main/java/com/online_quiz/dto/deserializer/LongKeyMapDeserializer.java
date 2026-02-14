package com.online_quiz.dto.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LongKeyMapDeserializer extends JsonDeserializer<Map<Long, Integer>> {

    @Override
    public Map<Long, Integer> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        Map<Long, Integer> map = new HashMap<>();
        JsonNode node = p.getCodec().readTree(p);
        
        if (node.isObject()) {
            ObjectNode objectNode = (ObjectNode) node;
            Iterator<Map.Entry<String, JsonNode>> fields = objectNode.fields();
            
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                try {
                    Long key = Long.parseLong(entry.getKey());
                    Integer value = entry.getValue().asInt();
                    map.put(key, value);
                } catch (NumberFormatException e) {
                    // Skip invalid keys
                    continue;
                }
            }
        }
        
        return map;
    }
}
