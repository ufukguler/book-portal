package com.bookportal.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProfanityService {
    private final ResourceLoader resourceLoader;
    private static final String RESOURCE_FILENAME = "classpath:profanity.txt";

    public boolean isProfanity(String text) {
        Map<String, Boolean> file = getPronityMap();
        String[] split = text.split(" ");
        for (String s : split) {
            if (file.containsKey(s)) {
                return true;
            }
        }
        return false;
    }

    @Cacheable("profanity")
    public Map<String, Boolean> getPronityMap() {
        Map<String, Boolean> map = new HashMap<>();
        Resource resource = resourceLoader.getResource(RESOURCE_FILENAME);
        try {
            File file = resource.getFile();
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                map.put(line, true);
            }
        } catch (IOException ignored) {
        }
        return map;
    }
}
