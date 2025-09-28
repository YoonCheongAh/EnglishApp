package org.example;

import com.google.gson.*;
import com.opencsv.CSVWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Crawling {
    private static final String BUILD_ID = "SlxmNzSynPdOc0lfdm5FA";

    public static void main(String[] args) {
        int startId = 5336;
        int endId = 5373;

        try (FileOutputStream fos = new FileOutputStream("flashcards.csv");
             OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
             CSVWriter writer = new CSVWriter(osw)) {

            // Thêm BOM để Excel nhận diện UTF-8
            fos.write(0xEF);
            fos.write(0xBB);
            fos.write(0xBF);

            // Header với cột audio_url
            String[] header = {"word", "word_type", "topic", "phonetic", "meaning_en", "image_url", "audio_url"};
            writer.writeNext(header);

            int totalCards = 0;
            int successfulCategories = 0;

            for (int categoryId = startId; categoryId <= endId; categoryId++) {
                try {
                    int cardsInCategory = processCategory(categoryId, writer);
                    if (cardsInCategory > 0) {
                        totalCards += cardsInCategory;
                        successfulCategories++;
                        System.out.println("Category " + categoryId + ": " + cardsInCategory + " cards");
                    }

                    // Nghỉ giữa các request
                    Thread.sleep(1000);

                } catch (Exception e) {
                    System.err.println("Error processing category " + categoryId + ": " + e.getMessage());
                }
            }

            System.out.println("\n==========================================");
            System.out.println("Crawl completed!");
            System.out.println("Successful categories: " + successfulCategories);
            System.out.println("Total cards: " + totalCards);
            System.out.println("Data saved to: flashcards.csv");

        } catch (IOException e) {
            System.err.println("IO Error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static int processCategory(int categoryId, CSVWriter writer) {
        String apiUrl = "https://langeek.co/_next/data/" + BUILD_ID + "/en/vocab/subcategory/" + categoryId + "/learn.json?locale=en&id=" + categoryId;
        int cardCount = 0;

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                return 0;
            }

            StringBuilder inline = new StringBuilder();
            try (Scanner scanner = new Scanner(conn.getInputStream(), StandardCharsets.UTF_8.name())) {
                while (scanner.hasNext()) {
                    inline.append(scanner.nextLine());
                }
            }

            JsonObject jsonObject = JsonParser.parseString(inline.toString()).getAsJsonObject();

            if (!jsonObject.has("pageProps")) {
                return 0;
            }

            JsonObject pageProps = jsonObject.getAsJsonObject("pageProps");
            JsonObject initialState = pageProps.getAsJsonObject("initialState");
            JsonObject staticData = initialState.getAsJsonObject("static");
            JsonObject subcategory = staticData.getAsJsonObject("subcategory");

            // Lấy tên topic từ subcategory
            String topic = "";
            if (subcategory.has("title")) {
                topic = subcategory.get("title").getAsString();
            } else if (subcategory.has("originalTitle")) {
                topic = subcategory.get("originalTitle").getAsString();
            }

            if (!subcategory.has("cards")) {
                return 0;
            }

            JsonArray cards = subcategory.getAsJsonArray("cards");

            for (JsonElement cardElem : cards) {
                try {
                    JsonObject card = cardElem.getAsJsonObject();
                    JsonObject main = card.getAsJsonObject("mainTranslation");

                    String word = main.get("title").getAsString();

                    String wordType = main.getAsJsonObject("partOfSpeech").get("partOfSpeechType").getAsString();

                    String phonetic = main.has("pronunciation") ? main.get("pronunciation").getAsString() : "";
                    String meaningEn = main.get("translation").getAsString();

                    String imageUrl = main.getAsJsonObject("wordPhoto").get("photo").getAsString();

                    String audioUrl = main.get("titleVoice").getAsString();

                    String[] row = {
                            word,           // word
                            wordType,       // word_type
                            topic,          // topic
                            phonetic,       // phonetic
                            meaningEn,      // meaning_en
                            imageUrl,       // image_url
                            audioUrl        // audio_url
                    };

                    writer.writeNext(row);
                    cardCount++;

                } catch (Exception e) {
                    System.err.println("Error processing card in category " + categoryId + ": " + e.getMessage());
                    // Tiếp tục xử lý card tiếp theo, không dừng
                }
            }

            return cardCount;

        } catch (Exception e) {
            System.err.println("Error with category " + categoryId + ": " + e.getMessage());
            return 0;
        }
    }
}