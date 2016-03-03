package com.github.onsdigital.helpers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class Json {

    public static String format(Object o) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(o);
    }
}
