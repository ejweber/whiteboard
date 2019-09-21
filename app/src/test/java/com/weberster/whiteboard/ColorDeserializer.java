package com.weberster.whiteboard;

import android.graphics.Color;
import android.support.annotation.ColorInt;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Android uses ColorInts of the form 0xAARRGGBB to represent colors as 32-bit integers. Java is
 * happy to represent even a large literal of this form as signed integers (i.e. 0xffffffff as -1).
 * However, in most other contexts (i.e. our Golang API and our MySQL database), an 0xffffffff
 * literal either overflows an integer or is represented as 4294967295. To limit confusion and
 * allow us to use 0xAARRGGBB form literals, we use unsigned integers to represent colors in all
 * other contexts. ColorDeserializer interprets a JSON encoded integer as a long (to avoid overflow)
 * and uses bit shifting and masking to convert this long representation to an int representation
 * that preserves bit order, NOT value.
 */
public class ColorDeserializer implements JsonDeserializer<Integer> {
    @Override
    public Integer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        long unsignedLong = json.getAsLong();
        int a = (int) (unsignedLong >> 24) & 0xff;
        int r = (int) (unsignedLong >> 16) & 0xff;
        int g = (int) (unsignedLong >> 8) & 0xff;
        int b = (int) unsignedLong & 0xff;
        @ColorInt int color = (a << 24) | (r  << 16) | (g  << 8) | b;
        return color;
    }
}
