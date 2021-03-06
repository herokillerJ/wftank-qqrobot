package cn.wftank.qqrobot.common.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class CustomLocalDateTimeDesSerializer extends StdDeserializer<LocalDateTime> {

    public CustomLocalDateTimeDesSerializer() {
        this(null);
    }

    public CustomLocalDateTimeDesSerializer(Class<LocalDateTime> t) {
        super(t);
    }

    @Override
    public LocalDateTime deserialize(JsonParser jsonparser, DeserializationContext context)
            throws IOException {
        Long timestamp = Long.parseLong(jsonparser.getText());
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault());
    }
}