package fr.curie.miclearner.model;

import java.util.HashMap;
import java.util.Map;

public class FileEntry {

    public static final String ACTION_DOWNLOAD = "DOWNLOAD";
    public static final String ACTION_EXTRACT = "EXTRACT";

    private String sourceUrl;
    private String destinationPath;
    private String action = ACTION_DOWNLOAD; // Default to download
    private Map<String, String> extractMap = new HashMap<>();
    private String sha256;

    public FileEntry() {}

    // Getters and Setters
    public String getSourceUrl() { return sourceUrl; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }

    public String getDestinationPath() { return destinationPath; }
    public void setDestinationPath(String destinationPath) { this.destinationPath = destinationPath; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public Map<String, String> getExtractMap() { return extractMap; }
    public void setExtractMap(Map<String, String> extractMap) { this.extractMap = extractMap; }

    public String getSha256() { return sha256; }
    public void setSha256(String sha256) { this.sha256 = sha256; }
}
