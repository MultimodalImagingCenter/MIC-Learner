package fr.curie.miclearner.structure;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class to download files from a Zenodo record using the REST API
 */

public class ZenodoDownloader {
    private static final String ZENODO_API_URL = "https://zenodo.org/api/records/";
    public static void main(String[] args) throws IOException {
        String recordId = "18243980";
        String outputDir = "/home/noemie/Downloads/zenodoTest";
        ZenodoDownloader downloader = new ZenodoDownloader();
        List<String> fileSelection = Arrays.asList("datapackage.json", "Notaufnahmesurveillance_Standorte.tsv");
        downloader.download(recordId, outputDir, fileSelection);
    }



    /**
     * Orchestrates the download process for a specific Zenodo record
     *
     * @param recordId      the zenodo record identifier
     * @param outputDirPath local directory path to save files
     * @param filesToMatch  list of specific filenames to download. If null or empty, all files are downloaded
     */
    public void download(String recordId, String outputDirPath, List<String> filesToMatch) {
        try {
            // 1. fetch metadata and map filenames to their download URLs (map <FileName, URL>)
            Map<String, String> zenodoFiles = fetchMetadataAsMap(recordId);
            Path outputDir = Paths.get(outputDirPath);
            Files.createDirectories(outputDir);

            // 2. validate user selection against available files in the record
            if (filesToMatch != null && !filesToMatch.isEmpty()) {
                for (String requestedFile : filesToMatch) {
                    if (!zenodoFiles.containsKey(requestedFile)) {
                        System.err.println("WARNING: Requested file '" + requestedFile
                                + "' not found in Zenodo record #" + recordId);
                    }
                }
            }

            // 3. iterate through available files and download based on user selection
            for (Map.Entry<String, String> entry : zenodoFiles.entrySet()) {
                String fileName = entry.getKey();
                String downloadUrl = entry.getValue();

                if (filesToMatch == null || filesToMatch.isEmpty() || filesToMatch.contains(fileName)) {
                    downloadSingleFile(downloadUrl, fileName, outputDir);
                }
            }
            System.out.println("Process completed.");

        } catch (Exception e) {
            System.err.println("Error with record " + recordId + " : " + e.getMessage());
        }
    }


    private static JsonArray fetchMetadata(String recordId) throws IOException {
        URL url = new URL(ZENODO_API_URL + recordId);
        System.out.println("url: " + url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Accept", "application/json"); // we need a json format
        conn.setRequestMethod("GET");

        if (conn.getResponseCode() != 200) {
            throw new IOException("Zenodo respond with error " + conn.getResponseCode()
                    + " (please check that the ID record is valid)");
        }

        try (InputStreamReader reader = new InputStreamReader(conn.getInputStream())) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            return root.getAsJsonArray("files"); // only get info about file
        }
    }

    /**
     * Connects to Zenodo API to retrieve the list of files + their URLs
     *
     * @param recordId the Zenodo record ID
     * @return a Map where keys are filenames and values are download URLs
     * @throws IOException if network or parsing error occurs
     */
    private Map<String, String> fetchMetadataAsMap(String recordId) throws IOException {
        Map<String, String> fileMap = new HashMap<>();
        URL url = new URL(ZENODO_API_URL + recordId);
        System.out.println("url: " + url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json"); // we need a json format

        // timeouts to handle potential proxy delays
        conn.setConnectTimeout(40000);
        conn.setReadTimeout(40000);

        if (conn.getResponseCode() != 200) {
            throw new IOException("Zenodo API Error " + conn.getResponseCode());
        }

        try (InputStreamReader reader = new InputStreamReader(conn.getInputStream())) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray filesArray = root.getAsJsonArray("files"); // only get info about file
            for (JsonElement element : filesArray) {
                JsonObject f = element.getAsJsonObject();
                fileMap.put(f.get("key").getAsString(), f.getAsJsonObject("links").get("self").getAsString());
            }
        }
        return fileMap;
    }

    /**
     * Downloads a single file from a URL to the target directory
     *
     * @param urlStr    the direct download URL
     * @param fileName  the name of the file to be created
     * @param outputDir the destination directory
     */
    private void downloadSingleFile(String urlStr, String fileName, Path outputDir) {

        Path targetPath = outputDir.resolve(fileName);

        if (Files.exists(targetPath)) {
            System.out.println("Skipping: '" + fileName + "' already exists in " + outputDir);
            return;
        }

        try {
            // Create sub folder
            if (targetPath.getParent() != null) {
                Files.createDirectories(targetPath.getParent());
            }

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "*/*");

            // handle slow proxy
            conn.setConnectTimeout(60000); // 60 sec for connexion
            conn.setReadTimeout(300000);  // 5 min to read data

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new IOException("Server returned HTTP " + conn.getResponseCode());
            }

            System.out.println("Downloading " + fileName + "...");
            try (InputStream in = conn.getInputStream()) {
                Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }
            System.out.println("  Success: " + fileName + " save to " + outputDir);

        } catch (IOException e) {
            System.err.println("Download failed for file: " + fileName + " : " + e.getMessage());
        }
    }


}
