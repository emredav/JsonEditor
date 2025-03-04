package utils;

import com.google.gson.*;
import java.util.Set;

public class JsonProcessor {

    /**
     * Verilen JSON nesnesini işleyerek belirtilen alanları kaldırır.
     *
     * @param jsonObject İşlenecek JSON nesnesi
     * @param fieldsToRemove Kaldırılacak alanların tam yollarını içeren set
     * @return İşlenmiş yeni JSON nesnesi
     */
    public static JsonObject deleteJsonFieldsRecursive(JsonObject jsonObject, Set<String> fieldsToRemove) {
        JsonObject newJsonObject = new JsonObject();

        for (String key : jsonObject.keySet()) {
            String fullPath = key;
            if (fieldsToRemove.contains(fullPath)) {
                continue;
            }

            JsonElement element = jsonObject.get(key);
            if (element.isJsonArray()) {
                JsonArray newArray = new JsonArray();
                for (JsonElement arrayElement : element.getAsJsonArray()) {
                    if (arrayElement.isJsonObject()) {
                        // Recursive call for nested JSON objects within arrays
                        newArray.add(deleteProcessWithPrefix(arrayElement.getAsJsonObject(), fieldsToRemove, fullPath + "[]"));
                    } else {
                        newArray.add(arrayElement);
                    }
                }
                newJsonObject.add(key, newArray);
            } else if (element.isJsonObject()) {
                // Recursive call for nested JSON objects
                newJsonObject.add(key, deleteProcessWithPrefix(element.getAsJsonObject(), fieldsToRemove, fullPath));
            } else {
                newJsonObject.add(key, element);
            }
        }

        return newJsonObject;
    }

    /**
     * Verilen JSON nesnesini işleyerek belirtilen alanları kaldırır.
     *
     * @param jsonObject İşlenecek JSON nesnesi
     * @param fieldsToRemove Kaldırılacak alanların tam yollarını içeren set
     * @param prefix Alanların tam yolunu oluşturmak için kullanılan ön ek
     * @return İşlenmiş yeni JSON nesnesi
     */
    private static JsonObject deleteProcessWithPrefix(JsonObject jsonObject, Set<String> fieldsToRemove, String prefix) {
        JsonObject newJsonObject = new JsonObject();

        for (String key : jsonObject.keySet()) {
            String fullPath = prefix + "." + key;
            if (fieldsToRemove.contains(fullPath)) {
                continue;
            }

            JsonElement element = jsonObject.get(key);
            if (element.isJsonArray()) {
                JsonArray newArray = new JsonArray();
                for (JsonElement arrayElement : element.getAsJsonArray()) {
                    if (arrayElement.isJsonObject()) {
                        // Recursive call for nested JSON objects within arrays
                        newArray.add(deleteProcessWithPrefix(arrayElement.getAsJsonObject(), fieldsToRemove, fullPath + "[]"));
                    } else {
                        newArray.add(arrayElement);
                    }
                }
                newJsonObject.add(key, newArray);
            } else if (element.isJsonObject()) {
                // Recursive call for nested JSON objects
                newJsonObject.add(key, deleteProcessWithPrefix(element.getAsJsonObject(), fieldsToRemove, fullPath));
            } else {
                newJsonObject.add(key, element);
            }
        }

        return newJsonObject;
    }
}