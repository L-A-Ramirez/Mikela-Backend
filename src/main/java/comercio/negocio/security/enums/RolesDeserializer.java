package comercio.negocio.security.enums;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;



public class RolesDeserializer extends JsonDeserializer<Set<RolNombre>> {
    @Override
    public Set<RolNombre> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        Set<RolNombre> roles = new HashSet<>();
        for (JsonNode roleNode : node) {
            roles.add(RolNombre.valueOf(roleNode.asText()));
        }
        return roles;
    }
}
