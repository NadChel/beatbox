package org.example.exceptions;

import org.example.demos.sequencer.beatBox.ManifestComponent;

public class IllegalPatternException extends Exception {
    private static final String FORMAT =
            "The pattern file doesn't specify a valid %s or does it in an illegal format";

    public IllegalPatternException(ManifestComponent problematicManifestComponent) {
        super(String.format(FORMAT, problematicManifestComponent));
    }
}
