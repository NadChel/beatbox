package org.example;

import javax.swing.*;

public enum PressedButtonStrategy {
    SELECT {
        @Override
        public void apply(JCheckBox checkBox) {
            checkBox.setSelected(true);
        }
    }, TOGGLE {
        @Override
        public void apply(JCheckBox checkBox) {
            checkBox.setSelected(!checkBox.isSelected());
        }
    }, DO_NOTHING {
        @Override
        public void apply(JCheckBox checkBox) {
        }
    };

    public abstract void apply(JCheckBox checkBox);
}
