package org.example;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Main {


    public static void main(String[] args) {
        String stringjasontest = "{\"name\":\"John Doe\"}";
        JsonObject jsonObject = new JsonParser().parse(stringjasontest).getAsJsonObject();
        System.out.println(jsonObject.get("name").getAsString());

    }
}