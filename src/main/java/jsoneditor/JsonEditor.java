package jsoneditor;

import utils.FileUtil;
import utils.JsonProcessor;
import com.google.gson.*;

import java.io.FileReader;
import java.util.*;

public class JsonEditor {
    public static void main(String[] args) {
        try {
            FileReader fileReader = FileUtil.readFile();
            JsonParser parser = new JsonParser();
            JsonObject jsonObject = parser.parse(fileReader).getAsJsonObject();

            // JSON yapısını numaralandırarak yazdır
            List<String> fields = new ArrayList<>();
            printJsonStructure(jsonObject, "", fields);

            // Kullanıcıdan kaldırılacak alanları al
            Scanner scanner = new Scanner(System.in);
            Set<String> fieldsToRemove = new HashSet<>();
            System.out.println("Kaldırmak istediğiniz alanların numaralarını girin (bitirmek için 'done' yazın):");
            while (true) {
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("done")) {
                    break;
                }
                try {
                    int index = Integer.parseInt(input) - 1;
                    if (index >= 0 && index < fields.size()) {
                        fieldsToRemove.add(fields.get(index));
                    } else {
                        System.out.println("Geçersiz numara, lütfen tekrar deneyin.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Geçersiz giriş, lütfen bir numara girin.");
                }
            }

            JsonObject newJsonObject = JsonProcessor.deleteJsonFieldsRecursive(jsonObject, fieldsToRemove);
            FileUtil.writeJsonToFile(newJsonObject);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void printJsonStructure(JsonObject jsonObject, String prefix, List<String> fields) {
        for (String key : jsonObject.keySet()) {
            String fullPath = prefix.isEmpty() ? key : prefix + "." + key;
            fields.add(fullPath);
            System.out.println(fields.size() + "- " + fullPath);

            JsonElement element = jsonObject.get(key);
            if (element.isJsonObject()) {
                printJsonStructure(element.getAsJsonObject(), fullPath, fields);
            } else if (element.isJsonArray()) {
                JsonArray array = element.getAsJsonArray();
                if (array.size() > 0 && array.get(0).isJsonObject()) {
                    printJsonStructure(array.get(0).getAsJsonObject(), fullPath + "[]", fields);
                }
            }
        }
    }
}