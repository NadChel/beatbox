package org.example.beatBox;

import org.example.demos.sequencer.beatBox.PressedButtonStrategy;
import org.junit.Test;

import javax.swing.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PressedButtonStrategyTest {
    @Test
    public void testToggle() {
        var pressedButtonBehavior = PressedButtonStrategy.TOGGLE;
        var checkBox = new JCheckBox();
        checkBox.setSelected(false);
        pressedButtonBehavior.apply(checkBox);
        assertTrue(checkBox.isSelected());
        pressedButtonBehavior.apply(checkBox);
        assertFalse(checkBox.isSelected());
    }
    @Test
    public void testSelect() {
        var pressedButtonBehavior = PressedButtonStrategy.SELECT;
        var checkBox = new JCheckBox();
        checkBox.setSelected(false);
        pressedButtonBehavior.apply(checkBox);
        assertTrue(checkBox.isSelected());
        pressedButtonBehavior.apply(checkBox);
        assertTrue(checkBox.isSelected());
    }
    @Test
    public void testDoNothing() {
        var pressedButtonBehavior = PressedButtonStrategy.DO_NOTHING;
        var checkBox = new JCheckBox();
        checkBox.setSelected(false);
        pressedButtonBehavior.apply(checkBox);
        assertFalse(checkBox.isSelected());
        pressedButtonBehavior.apply(checkBox);
        assertFalse(checkBox.isSelected());
    }
}
