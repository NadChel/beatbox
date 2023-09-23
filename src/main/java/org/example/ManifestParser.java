package org.example;


import org.example.exceptions.IllegalPatternException;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static org.example.ManifestComponent.*;

public class ManifestParser {
    private static BufferedReader reader;

    static PatternManifest parseManifest(BufferedReader reader) throws IllegalPatternException, IOException {
        ManifestParser.reader = reader;
        String separator = parse(SEPARATOR);
        String checkMark = parse(CHECK_MARK);
        String blankMark = parse(BLANK_MARK);
        return new PatternManifest(separator, checkMark, blankMark);
    }

    private static String parse(ManifestComponent mc) throws IllegalPatternException, IOException {
        Matcher matcher = Pattern.compile(format("(?<=%s )%s", mc.getManifestName(), mc.getRegex()))
                .matcher(reader.readLine());
        if (matcher.find()) {
            return matcher.group();
        } else {
            throw new IllegalPatternException(mc);
        }
    }
}
