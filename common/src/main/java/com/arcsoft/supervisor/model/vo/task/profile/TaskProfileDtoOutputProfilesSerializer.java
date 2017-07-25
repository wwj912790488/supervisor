package com.arcsoft.supervisor.model.vo.task.profile;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * A {@link JsonSerializer} implementation for filter field of {@link OutputProfileDto} of {@link TaskProfileDto}.
 *
 * @author zw.
 */
public class TaskProfileDtoOutputProfilesSerializer extends JsonSerializer<OutputProfileDto> {
    @Override
    public void serialize(OutputProfileDto value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        gen.writeStartObject();
        gen.writeObjectField(OutputProfileDto.NODE_NAME_VIDEOPROFILES, value.getVideoprofiles());
        gen.writeObjectField(OutputProfileDto.NODE_NAME_AUDIOPROFILES, value.getAudioprofiles());
        gen.writeEndObject();
    }
}
