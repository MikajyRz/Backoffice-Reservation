package com.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class FileStorage {
    private static String uploadDir;

    public static void init(String basePath) {
        uploadDir = basePath;
    }

    public static void save(byte[] data, String fileName) throws IOException {
        if (uploadDir == null) {
            uploadDir = System.getProperty("user.dir") + java.io.File.separator + "uploads";
        }

        Files.createDirectories(Paths.get(uploadDir));

        String filename = UUID.randomUUID() + "_" + fileName;
        Path target = Paths.get(uploadDir, filename);
        Files.write(target, data);
    }
}
