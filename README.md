# JSON Editor Project

This project is a simple JSON editor that allows users to remove specific fields from a JSON file. The project is built using Java and Maven.

## Project Structure

## How to Run
1. Build the project using Maven:
    ```sh
    mvn clean install
    ```
2. Run the `JsonEditor` class:

3. When prompted, enter the path to the JSON file (e.g., `src/main/resources/exampleWordList.json`).

4. The program will display the structure of the JSON file with numbered fields. Enter the numbers of the fields you want to remove, one by one. Type `done` when you are finished.

5. The edited JSON file will be saved in the `output` directory with a timestamped filename.

Example Program Output:
```
Lütfen dosya yolunu girin:
src/main/resources/exampleWordList.json
1- words
2- words[].id
3- words[].ENG
4- words[].TR
Kaldırmak istediğiniz alanların numaralarını girin (bitirmek için 'done' yazın):
2
done
JSON dosyası başarıyla düzenlendi: output/json-20250304221419.json
```