package server;

import client.Args;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

import static server.Constants.*;

public class JsonDataBase {
    private JsonObject response;
    Gson gson;
    private JsonObject db = new JsonObject();

    public JsonDataBase() {
        gson = new Gson();
    }

    public String get(JsonElement key) {
        response = new JsonObject();
        response.addProperty("response", OK);
        String value = "";
        try (Reader reader = Files.newBufferedReader(Paths.get(FILE_PATH));) {
            db = gson.fromJson(reader, JsonObject.class);

            if (key.isJsonPrimitive() && db.has(key.getAsString())) {
                value = db.get(key.getAsString()).getAsString();
                response.addProperty("value", value);
            } else if (key.isJsonArray()) {
                try {
                    value = findElement(key.getAsJsonArray(), false).getAsString();
                    response.addProperty("value", value);
                } catch (UnsupportedOperationException e) {
                    response.add("value", findElement(key.getAsJsonArray(), false));
                }

            }
        } catch (Exception e) {
            response.addProperty("response", ERROR);
            response.addProperty("reason", NO_SUCK_KEY);
        }

        return response.toString();
    }

    public String set(String key, String str) {
        response = new JsonObject();
        try (FileWriter file = new FileWriter(FILE_PATH);) {
            try {
                JsonObject value;
                value = gson.fromJson(str, JsonObject.class);
                db.add(key, value);
            } catch (Exception e) {
                db.addProperty(key, str.replace("\"", ""));
            }
//            db.addProperty(key, str);
            file.write(db.toString());

            response.addProperty("response", OK);
        } catch (Exception e) {
            response.addProperty("response", ERROR);
        }

        return response.toString();
    }

    public String set(JsonArray keys, String str) {
        response = new JsonObject();

        try (Reader reader = Files.newBufferedReader(Paths.get(FILE_PATH));) {
            db = gson.fromJson(reader, JsonObject.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (FileWriter file = new FileWriter(FILE_PATH);) {

        findElement(keys, true)
                .getAsJsonObject()
                .addProperty(keys.remove(keys.size() - 1)
                .getAsString(), str.replace("\"", ""));

 //           db.getAsJsonObject(key.get(0).getAsString()).getAsJsonObject(key.get(1).getAsString()).addProperty(key.get(2).getAsString(), str.replace("\"", ""));

            file.write(db.toString());

            response.addProperty("response", OK);
        } catch (Exception e) {
            response.addProperty("response", ERROR);
        }

        return response.toString();
    }

    public String delete(JsonElement key) {
        response = new JsonObject();
        try (Reader reader = Files.newBufferedReader(Paths.get(FILE_PATH));) {
            db = gson.fromJson(reader, JsonObject.class);

            if (key.isJsonPrimitive() && db.has(key.getAsString())) {
                db.remove(key.getAsString());
            } else if (key.isJsonArray()) {
                try {
                    findElement(key.getAsJsonArray(), true).getAsJsonObject().remove(key.getAsJsonArray().get(key.getAsJsonArray().size() -1).getAsString());
                } catch (UnsupportedOperationException e) {
                    findElement(key.getAsJsonArray(), true).getAsJsonObject().remove(key.getAsJsonArray().get(key.getAsJsonArray().size() -1).toString());
                } catch (Exception e) {
                    response.addProperty("response", ERROR);
                    response.addProperty("reason", NO_SUCK_KEY);
                }
            }

            response.addProperty("response", OK);

            } catch(Exception e){
                response.addProperty("response", ERROR);
                response.addProperty("reason", NO_SUCK_KEY);
            }

            try (FileWriter file = new FileWriter(FILE_PATH)) {
                file.write(db.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response.toString();
        }

    synchronized public String executeProgram(String line) {
        JsonObject newJsonObj = gson.fromJson(line, JsonObject.class);

        while (true) {
            switch (newJsonObj.get("type").getAsString()) {
                case "get": {
                    return get(newJsonObj.get("key"));
                }
                case "set": {
                    try {
                        return set(newJsonObj.get("key").getAsString(), newJsonObj.get("value").toString());
                    } catch (Exception e) {
                        return set(newJsonObj.getAsJsonArray("key"), newJsonObj.get("value").toString());
                    }

                }
                case "delete": {
                    return delete(newJsonObj.get("key"));
                }
                case "exit":
                    return "exit";
            }
        }

//        Args args = gson.fromJson(line, Args.class);

//        while (true) {
//            switch (args.getType()) {
//                case "get": {
//                    return get(args.getKey());
//                }
//                case "set": {
//                    return set(args.getKey(), args.getValue());
//                 //   return set(args.getKey(), args.jsonObject.toString());
//                }
//                case "delete": {
//                    return delete(args.getKey());
//                }
//                case "exit":
//                    return "exit";
//            }
//        }

    }

    public JsonElement findElement(JsonArray keys, boolean toEdit) {
        JsonElement tmp = db;
            for(JsonElement key: keys) {

                if (toEdit) {
                    if(!tmp.getAsJsonObject().has(key.getAsString())){
                        tmp.getAsJsonObject().add(key.getAsString(), new JsonObject());
                    }

                    if (tmp.getAsJsonObject().keySet().contains(keys.get(keys.size()-1).getAsString()))
                        break;
                }

                tmp = tmp.getAsJsonObject().get(key.getAsString());
            }


        return tmp;
    }

}
