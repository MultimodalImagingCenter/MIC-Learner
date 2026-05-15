package fr.curie.miclearner.model;

import java.util.List;

public class ModelDefinition {
    private String id;
    private String displayName;
    private List<FileEntry> files;

    public ModelDefinition() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public List<FileEntry> getFiles() { return files; }
    public void setFiles(List<FileEntry> files) { this.files = files; }
}
