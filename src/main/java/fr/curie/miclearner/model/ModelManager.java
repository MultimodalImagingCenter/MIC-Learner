package fr.curie.miclearner.model;

import ij.IJ;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ModelManager {
    private final ModelRegistry registry;
    private final FileSystemManager fsManager;
    private final ModelDownloader downloader;
    private final Path baseModelsDir;

    public ModelManager(ModelRegistry registry, FileSystemManager fsManager, ModelDownloader downloader) {
        this.registry = registry;
        this.fsManager = fsManager;
        this.downloader = downloader;
        baseModelsDir = fsManager.getMicModelDirectory();
    }

    public ModelRegistry getRegistry() {
        return registry;
    }


    /**
     * Checks if a model is fully installed and all its files are present.
     */
    public boolean isModelComplete(String modelId) {
        ModelDefinition def = registry.getModel(modelId);
        if (def == null) return false;

        for (FileEntry entry : def.getFiles()) {
            if (FileEntry.ACTION_EXTRACT.equals(entry.getAction())) {
                // Check every file that is supposed to be extracted
                for (String relativeDestPath : entry.getExtractMap().values()) {
                    if (fileAbsentOrCorrupted(relativeDestPath, null)) return false;
                }
            } else {
                if (fileAbsentOrCorrupted(entry.getDestinationPath(), entry.getSha256())) return false;
            }
        }
        return true;
    }

    private boolean fileAbsentOrCorrupted(String relativeFilePath, String expectedHash) {
        if (!fsManager.fileExists(relativeFilePath)) return true;

        // If no hash is expected, just return true (it exists)
        if (expectedHash == null || expectedHash.isEmpty()) return false;

        try {
            String actualHash = HashUtils.calculateSHA256(fsManager.resolveDestination(relativeFilePath));
            return !actualHash.equalsIgnoreCase(expectedHash);
        } catch (IOException e) {
            // could not calculate hash
            return true;
        }
    }

    public void installModel(String modelId) throws IOException {
        ModelDefinition def = registry.getModel(modelId);
        if (def == null) {
            throw new IllegalArgumentException("Unknown model ID: " + modelId);
        }

        for (FileEntry entry : def.getFiles()) {
            try {

                Path target = fsManager.resolveDestination(entry.getDestinationPath());
                fsManager.ensureParentDirectoriesExist(target);
                downloader.processEntry(entry, baseModelsDir);
                ij.IJ.log("      " + Paths.get(entry.getDestinationPath()).getFileName() + " successfully downloaded");

            } catch (IOException | RuntimeException e) {

                ij.IJ.log("      Failed to process " + Paths.get(entry.getDestinationPath()).getFileName() + ": " + e.getMessage());
            }
        }
    }
}