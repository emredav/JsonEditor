package utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class FileUtil {
    private static String lastFilePath;

    public static FileReader readFile() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Lütfen dosya yolunu girin:");
        lastFilePath = scanner.nextLine();
        return new FileReader(lastFilePath);
    }

    public static String getLastFilePath() {
        return lastFilePath;
    }

    /**
     * Verilen JSON nesnesini belirtilen dosya yoluna yazar.
     * output klasörü altına json-YYYYMMDDHHmmss.json adıyla kaydeder.
     * @throws IOException Dosya yazma hatası
     */
    public static void writeJsonToFile(JsonObject jsonObject) throws IOException {
        String outputPath = "output/json-" +
                new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date()) + ".json";
        File outputDir = new File("output");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        try (FileWriter writer = new FileWriter(outputPath)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(jsonObject, writer);
            System.out.println("JSON dosyası başarıyla düzenlendi: " + outputPath);
        }
    }
}
