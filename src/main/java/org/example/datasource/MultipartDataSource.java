package org.example.datasource;

import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

public class MultipartDataSource implements DataSource {

    private final MultipartFile file;
    private String cached;

    public MultipartDataSource(MultipartFile file) {
        this.file = file;
    }

    @Override
    public String read() throws Exception {
        if (cached == null) {
            cached = new String(file.getBytes(), StandardCharsets.UTF_8);
        }
        return cached;
    }

    @Override
    public String getIdentifier() {
        String name = file.getOriginalFilename();
        if (name == null || !name.contains(".")) {
            return "";
        }
        return name.substring(name.lastIndexOf('.') + 1).toLowerCase();
    }
}
