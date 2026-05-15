package fr.curie.miclearner.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileSystemManager {

    private final Path micModelDirectory;

    public FileSystemManager(String baseIJPath) {
        micModelDirectory = Paths.get(baseIJPath, "models","MicLearningModels");
    }

     public Path resolveDestination(String destinationPath) {
        return micModelDirectory.resolve(destinationPath);
    }

    /**
     * Ensures that the parent directory for a specific file exists.
     * Used before downloading or extracting a file.
     */
    public void ensureParentDirectoriesExist(Path filePath) throws IOException {
        Path parent = filePath.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }
    }

    /**
     * Checks if a file is present on disk.
     */
    public boolean fileExists(String relativePath) {
        return Files.exists(resolveDestination(relativePath));
    }

    /**
     * Returns the root directory of the MicLearningModels folder.
     */
    public Path getMicModelDirectory() {
        return micModelDirectory;
    }

}