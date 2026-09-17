package com.agentguard.service;

import java.text.Normalizer;
import java.util.Locale;

import org.springframework.stereotype.Service;

@Service
public class PromptNormalizer {

    public String normalize(String prompt) {
        return Normalizer.normalize(prompt, Normalizer.Form.NFKC)
                .trim()
                .replaceAll("\\s+", " ")
                .toLowerCase(Locale.ROOT);
    }
}
