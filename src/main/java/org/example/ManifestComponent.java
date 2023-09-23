package org.example;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ManifestComponent {
    SEPARATOR("SEPARATOR", ".+"), CHECK_MARK("CHECK MARK", ".{1}"),
    BLANK_MARK("BLANK MARK", ".{1}");

    private final String manifestName;
    private final String regex;

    @Override
    public String toString() {
        return manifestName;
    }
}

