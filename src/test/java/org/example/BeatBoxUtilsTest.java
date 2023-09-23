package org.example.beatBox;

import org.example.demos.sequencer.beatBox.BeatBoxUtils;
import org.example.demos.sequencer.beatBox.ManifestComponent;
import org.example.demos.sequencer.beatBox.exceptions.IllegalPropertyException;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertThrows;

public class BeatBoxUtilsTest {
    @Test
    public void testRequireNonEmptyCollection() {
        assertThrows(IllegalPropertyException.class,
                () -> BeatBoxUtils.requireNonEmpty(Collections.emptyList(), "someEmptyCollection"));
    }
    @Test
    public void testRequireNonEmptyString() {
        assertThrows(IllegalPropertyException.class,
                () -> BeatBoxUtils.requireNonEmpty("", "someEmptyString"));
    }
    @Test
    public void testRequirePositiveInt() {
        assertThrows(IllegalPropertyException.class,
                () -> BeatBoxUtils.requirePositive(-1, "minusOne"));
        assertThrows(IllegalPropertyException.class,
                () -> BeatBoxUtils.requirePositive(0, "zero"));
    }
    @Test
    public void testRequirePositiveFloat() {
        assertThrows(IllegalPropertyException.class,
                () -> BeatBoxUtils.requirePositive(-1f, "minusOne"));
        assertThrows(IllegalPropertyException.class,
                () -> BeatBoxUtils.requirePositive(0f, "zero"));
    }
    @Test
    public void testRequireNonNullObject() {
        assertThrows(IllegalPropertyException.class,
                () -> BeatBoxUtils.requireNonNull(null, "nullObject"));
    }

    @Test
    public void testRequireManifestComponentValidity() {
        assertThrows(IllegalPropertyException.class,
                () -> BeatBoxUtils.requireManifestComponentValidity("mark", ManifestComponent.CHECK_MARK));
    }
}
