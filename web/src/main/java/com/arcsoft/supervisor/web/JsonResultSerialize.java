package com.arcsoft.supervisor.web;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Customized serialize implementation to converts {@link JsonResult} to json string.
 *
 * @author zw.
 */
class JsonResultSerialize extends JsonSerializer<JsonResult> {

    @Override
    public void serialize(JsonResult value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeObject(value.getResult());
    }

}
