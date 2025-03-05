package jsoneditor;

import utils.FileUtil;
import utils.JsonProcessor;
import com.google.gson.*;

import java.io.FileReader;
import java.util.*;

public class JsonEditor {
    public static void main(String[] args) {
        FileReader currentFileReader = null;
        String currentFilePath = "";
        boolean continueProgram = true;

        while (continueProgram) {
            try {
                if (currentFileReader == null) {
                    currentFileReader = FileUtil.readFile();
                    currentFilePath = FileUtil.getLastFilePath();
                }

                System.out.println("Please select an option:");
                System.out.println("1- Show Field Frequency Analysis");
                System.out.println("2- Field Remover");

                Scanner scanner = new Scanner(System.in);
                int option = scanner.nextInt();
                scanner.nextLine(); // Buffer temizleme

                JsonParser parser = new JsonParser();
                JsonObject jsonObject = parser.parse(currentFileReader).getAsJsonObject();

                if (option == 1) {
                    analyzeJsonStructure(jsonObject);
                } else if (option == 2) {
                    removeFields(jsonObject);
                } else {
                    System.out.println("Invalid option selected.");
                }

                System.out.println("\nWould you like to perform another operation? (yes/no):");
                String continueAnswer = scanner.nextLine().toLowerCase();

                if (continueAnswer.equals("yes")) {
                    System.out.println("Would you like to continue with the original file? (yes/no):");
                    System.out.println("Selected file: " + currentFilePath);

                    String useOriginalFile = scanner.nextLine().toLowerCase();
                    if (useOriginalFile.equals("no")) {
                        currentFileReader = FileUtil.readFile();
                        currentFilePath = FileUtil.getLastFilePath();
                    } else {
                        currentFileReader = new FileReader(currentFilePath);
                    }
                } else {
                    continueProgram = false;
                    System.out.println("Program terminated.");
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                Scanner scanner = new Scanner(System.in);
                System.out.println("\nWould you like to perform another operation? (yes/no):");
                String continueAnswer = scanner.nextLine().toLowerCase();
                if (continueAnswer.equals("yes")) {
                    continueProgram = true;
                } else {
                    continueProgram = false;
                    System.out.println("Program terminated.");
                }
            }
        }
    }

    private static void analyzeJsonStructure(JsonObject jsonObject) {
        Map<String, Integer> fieldCounts = new HashMap<>();
        countFields(jsonObject, "", fieldCounts);

        List<String> fields = new ArrayList<>(fieldCounts.keySet());
        for (int i = 0; i < fields.size(); i++) {
            String field = fields.get(i);
            System.out.printf("%d- %s (%d pieces)%n", i + 1, field, fieldCounts.get(field));
        }
    }

    private static void countFields(JsonObject jsonObject, String prefix, Map<String, Integer> fieldCounts) {
        // JSON nesnesindeki her bir anahtar için döngü
        for (String key : jsonObject.keySet()) {
            String fullPath = prefix.isEmpty() ? key : prefix + "." + key;
            // keyin sayısını artır
            fieldCounts.putIfAbsent(fullPath, 0);
            fieldCounts.put(fullPath, fieldCounts.get(fullPath) + 1);

            JsonElement element = jsonObject.get(key);
            // Eğer eleman bir JSON nesnesi ise, rekürsif olarak çağır
            if (element.isJsonObject()) {
                countFields(element.getAsJsonObject(), fullPath, fieldCounts);
            } else if (element.isJsonArray()) {
                JsonArray array = element.getAsJsonArray();
                // Eğer eleman bir JSON dizisi ise, her bir eleman için döngü
                for (JsonElement arrayElement : array) {
                    if (arrayElement.isJsonObject()) {
                        JsonObject arrayObj = arrayElement.getAsJsonObject();
                        // Dizideki her bir nesne için anahtarları say
                        for (String arrayKey : arrayObj.keySet()) {
                            String arrayPath = fullPath + "[]." + arrayKey;
                            fieldCounts.putIfAbsent(arrayPath, 0);
                            fieldCounts.put(arrayPath, fieldCounts.get(arrayPath) + 1);
                        }
                        // Dizideki nesneler için rekürsif olarak çağır
                        countFields(arrayObj, fullPath + "[]", fieldCounts);
                    }
                }
            }
        }
    }

    private static void removeFields(JsonObject jsonObject) {
        List<String> fields = new ArrayList<>();
        printJsonStructure(jsonObject, "", fields);

        Scanner scanner = new Scanner(System.in);
        Set<String> fieldsToRemove = new HashSet<>();
        System.out.println("Enter field numbers to remove (type 'done' to finish):");

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
                    System.out.println("Invalid number, please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input, please enter a number.");
            }
        }

        JsonObject newJsonObject = JsonProcessor.deleteJsonFieldsRecursive(jsonObject, fieldsToRemove);
        try {
            FileUtil.writeJsonToFile(newJsonObject);
        } catch (Exception e) {
            System.err.println("Error writing file: " + e.getMessage());
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