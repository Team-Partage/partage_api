package com.egatrap.partage.common.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class DateTimeFormatterAdapter extends TypeAdapter<DateTimeFormatter> {

    @Override
    public void write(JsonWriter out, DateTimeFormatter value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value.toString());
        }
    }

    @Override
    public DateTimeFormatter read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        } else {
            return DateTimeFormatter.ofPattern(in.nextString());
        }
    }
}