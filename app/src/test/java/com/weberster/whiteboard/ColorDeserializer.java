package com.weberster.whiteboard;

import android.support.annotation.ColorInt;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class ColorDeserializer implements JsonDeserializer<Integer> {
    public Integer deserialize(JsonElement colorString, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        @ColorInt Integer colorInt = Integer.parseUnsignedInt(colorString.getAsString(), 16);
        return colorInt;
    }
}
