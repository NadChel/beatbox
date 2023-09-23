package org.example;

import org.example.demos.sequencer.beatBox.exceptions.IllegalPropertyException;
import org.example.demos.sequencer.beatBox.exceptions.IllegalPropertyException.PropertyIssue;

import java.util.Collection;
import java.util.regex.Pattern;

public class BeatBoxUtils {
    public static void requireNonEmpty(Collection<?> collection, String propertyName) {
        requireNonNull(collection, propertyName);
        if (collection.isEmpty()) {
            throw new IllegalPropertyException(propertyName, PropertyIssue.EMPTY);
        }
    }

    public static void requireNonEmpty(String property, String propertyName) {
        requireNonNull(property, propertyName);
        if (property.isEmpty()) {
            throw new IllegalPropertyException(propertyName, PropertyIssue.EMPTY);
        }
    }

    public static void requirePositive(int property, String propertyName) {
        if (property <= 0) {
            throw new IllegalPropertyException(propertyName, PropertyIssue.NON_POSITIVE);
        }
    }

    public static void requirePositive(float property, String propertyName) {
        if (property <= 0) {
            throw new IllegalPropertyException(propertyName, PropertyIssue.NON_POSITIVE);
        }
    }

    public static void requireNonNull(Object property, String propertyName) {
        if (property == null) {
            throw new IllegalPropertyException(propertyName, PropertyIssue.NULL);
        }
    }

    public static void requireManifestComponentValidity(String passedComponent, ManifestComponent componentToMatch) {
        requireNonNull(passedComponent, "passed " + componentToMatch.getManifestName());
        requireNonNull(componentToMatch, "componentToMatch");
        if (!Pattern.matches(componentToMatch.getRegex(), passedComponent)) {
            throw new IllegalPropertyException("passed " + componentToMatch.getManifestName(), PropertyIssue.NOT_MATCHING_REGEX);
        }
    }
}
