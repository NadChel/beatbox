package org.example.exceptions;

import lombok.RequiredArgsConstructor;

public class IllegalPropertyException extends RuntimeException {
    private static final String FORMAT = "%s value is invalid: should be %s";

    public IllegalPropertyException(String propertyName, PropertyIssue propertyIssue) {
        super(String.format(FORMAT, propertyName, propertyIssue.whatItShouldBeInstead()));
    }

    @RequiredArgsConstructor
    public enum PropertyIssue {
        NULL("not null"), EMPTY("not empty"),
        NON_POSITIVE("greater than zero"),
        NOT_MATCHING_REGEX("matching the regular expression");

        private final String whatItShouldBeInstead;

        String whatItShouldBeInstead() {
            return whatItShouldBeInstead;
        }
    }
}
