package fr.curie.miclearner.model;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

public class HashUtils {
    public static String calculateSHA256(Path file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file.toFile())) {
            return DigestUtils.sha256Hex(fis);
        }
    }
}
