import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class test {
    public static void main(String[] args) {
        String inputFile = "t8.shakespeare.txt";
        String findWordsFile = "find_words.txt";
        String dictionaryFile = "french_dictionary.csv";

        String translateFile = "t8.shakespeare.translated.txt";
        String frequencyFile = "frequency.csv";
        String performanceFile = "performance.txt";

        try {
            long startTime = System.currentTimeMillis();

            // Read input files
            String inputText = readFile(inputFile);
            Set<String> findWords = readFindWords(findWordsFile);
            Map<String, String> dictionary = readDictionary(dictionaryFile);

            // Find words to replace
            Map<String, Integer> wordFrequency = new HashMap<>();
            Set<String> replacedWords = new HashSet<>();
            for (String word : findWords) {
                if (dictionary.containsKey(word)) {
                    replacedWords.add(word);
                    wordFrequency.put(word, 0);
                }
            }

            // Replace words in input text
            String translatedText = replaceWords(inputText, dictionary, wordFrequency);

            // Save the processed file
            saveToFile(translateFile, translatedText);

            // Calculate performance metrics
            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            long memoryUsed = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

            // Save performance metrics to performance.txt
            String performanceText = "Time to process: " + totalTime + " milliseconds\n"
                    + "Memory used: " + memoryUsed + " bytes";
            saveToFile(performanceFile, performanceText);

            // Save frequency information to frequency.csv
            StringBuilder frequencyText = new StringBuilder();
            frequencyText.append("English word,French word,Frequency\n");
            for (String word : replacedWords) {
                int frequency = wordFrequency.get(word);
                frequencyText.append(word).append(",").append(dictionary.get(word)).append(",").append(frequency)
                        .append("\n");
            }
            saveToFile(frequencyFile, frequencyText.toString());

            System.out.println("Translation completed successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String readFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    private static Set<String> readFindWords(String filePath) throws IOException {
        Set<String> findWords = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String word;
            while ((word = reader.readLine()) != null) {
                findWords.add(word);
            }
        }
        return findWords;
    }

    private static Map<String, String> readDictionary(String filePath) throws IOException {
        Map<String, String> dictionary = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String englishWord = parts[0];
                    String frenchWord = parts[1];
                    dictionary.put(englishWord, frenchWord);
                }
            }
        }
        return dictionary;
    }

    private static String replaceWords(String inputText, Map<String, String> dictionary,
            Map<String, Integer> wordFrequency) {
        StringBuilder translatedText = new StringBuilder();
        String[] words = inputText.split("\\b");
        for (String word : words) {
            if (dictionary.containsKey(word)) {
                wordFrequency.put(word, wordFrequency.get(word) + 1);
                translatedText.append(dictionary.get(word));
            } else {
                translatedText.append(word);
            }
        }
        return translatedText.toString();
    }

    private static void saveToFile(String filePath, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(content);
        }
    }
}
