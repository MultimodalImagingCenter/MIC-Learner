package fr.curie.miclearner.model;

import ij.IJ;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.*;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ModelDownloader {
    public ModelDownloader() {}

    public void processEntry(FileEntry entry, Path micModelsFolder) throws IOException {
        if (FileEntry.ACTION_EXTRACT.equals(entry.getAction())) {
            downloadAndExtract(entry, micModelsFolder);
        } else {
            // Standard DOWNLOAD
            Path target = micModelsFolder.resolve(entry.getDestinationPath());
            downloadFile(entry.getSourceUrl(), target);
        }
    }

    private void downloadFile(String sourceUrl, Path target) throws IOException {
        try (InputStream in = new URL(sourceUrl).openStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void downloadAndExtract(FileEntry entry, Path micModelsFolder) throws IOException {
        // download to temporary file
        Path tempZip = Files.createTempFile("model_download_", ".zip");
        try {
            downloadFile(entry.getSourceUrl(), tempZip);

            // open Zip and extract only requested files
            try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(tempZip.toFile().toPath()))) {
                ZipEntry zipEntry = zis.getNextEntry();

                while (zipEntry != null) {

                    // check if this file in the zip is in our extractMap
                    if (entry.getExtractMap().containsKey(zipEntry.getName())) {
                        String relativeTarget = entry.getExtractMap().get(zipEntry.getName());
                        Path targetPath = micModelsFolder.resolve(relativeTarget);

                        Files.createDirectories(targetPath.getParent());
                        Files.copy(zis, targetPath, StandardCopyOption.REPLACE_EXISTING);
                    }
                    zipEntry = zis.getNextEntry();
                }
            }
        } finally {
            Files.deleteIfExists(tempZip);
        }
    }
}