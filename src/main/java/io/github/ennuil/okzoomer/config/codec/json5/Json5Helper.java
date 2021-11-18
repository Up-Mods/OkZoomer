package io.github.ennuil.okzoomer.config.codec.json5;

import java.io.IOException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.LazilyParsedNumber;

import org.quiltmc.json5.JsonReader;

public class Json5Helper {
    public static final JsonElement parseJson5Reader(JsonReader reader) throws IOException {
        return switch (reader.peek()) {
            case STRING -> new JsonPrimitive(reader.nextString());
            case NUMBER -> new JsonPrimitive(new LazilyParsedNumber(reader.nextString()));
            case BOOLEAN -> new JsonPrimitive(reader.nextBoolean());
            case NULL -> {
                reader.nextNull();
                yield JsonNull.INSTANCE;
            }
            case BEGIN_ARRAY -> {
                JsonArray array = new JsonArray();
                reader.beginArray();
                while (reader.hasNext()) {
                    array.add(parseJson5Reader(reader));
                }
                reader.endArray();
                yield array;
            }
            case BEGIN_OBJECT -> {
                JsonObject object = new JsonObject();
                reader.beginObject();
                while (reader.hasNext()) {
                    object.add(reader.nextName(), parseJson5Reader(reader));
                }
                reader.endObject();
                yield object;
            }
            default -> throw new IllegalArgumentException();
        };
    };
}
