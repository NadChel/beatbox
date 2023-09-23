package org.example;

import com.seryozha.commons.MidiUtil;
import com.seryozha.commons.UIUtil;
import com.seryozha.commons.Util;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.example.exceptions.IllegalPatternException;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Track;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.awt.BorderLayout.*;
import static javax.sound.midi.Sequencer.LOOP_CONTINUOUSLY;
import static javax.sound.midi.ShortMessage.*;
import static javax.swing.BoxLayout.Y_AXIS;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

@Slf4j
public class BeatBox {
    private final Sequencer sequencer;
    private final Sequence sequence = Util.wrapInTryCatchAndGet(() -> new Sequence(Sequence.PPQ, 4));
    private Track track = sequence.createTrack();
    @Getter
    private int numberOfBeats;
    private JPanel mainPanel;
    @Getter(value = AccessLevel.PACKAGE)
    private JComponent labelBox;
    @Getter(value = AccessLevel.PACKAGE)
    private JPanel checkBoxPanel;
    private final ItemListener checkBoxItemListener = new CheckBoxItemListener();
    private final MouseListener checkBoxMouseListener = new CheckBoxMouseListener();
    private JButton saveButton;
    private JButton startButton;
    private JButton stopButton;
    private JButton tempoUpButton;
    private JButton tempoDownButton;
    private JButton clearButton;
    private boolean isMousePressed;
    @Getter
    private final PressedButtonStrategy pressedButtonStrategy;
    @Getter
    private final MissingInstrumentStrategy missingInstrumentStrategy;
    @Getter
    private final MissingBeatsStrategy missingBeatsStrategy;
    @Getter
    private final ExcessBeatsStrategy excessBeatsStrategy;
    @Getter
    private final String separator;
    @Getter
    private final String checkMark;
    @Getter
    private final String blankMark;
    @Getter(value = AccessLevel.PACKAGE)
    private final Map<MidiInstrument, List<JCheckBox>> instrumentMap;
    private static final Rectangle MAXIMUM_WINDOW_BOUNDS = UIUtil.getMaximumWindowBounds();
    private static final int MINIMUM_FRAME_WIDTH = 500;
    private static final int WIDTH_PER_BEAT = 15;
    private static final int MINIMUM_FRAME_HEIGHT = 225;
    private static final int HEIGHT_PER_INSTRUMENT = 20;

    public BeatBox() {
        this(BeatBoxConfiguration.configure());
    }

    BeatBox(BeatBoxConfiguration config) {
        sequencer = Util.wrapInTryCatchAndGet(() -> {
            Sequencer s = MidiSystem.getSequencer();
            s.open();
            s.setTempoInBPM(config.getTempoInBPM());
            return s;
        });
        instrumentMap = config.getInstrumentMap();
        numberOfBeats = config.getNumberOfBeats();
        pressedButtonStrategy = config.getPressedButtonStrategy();
        missingInstrumentStrategy = config.getMissingInstrumentStrategy();
        missingBeatsStrategy = config.getMissingBeatsStrategy();
        excessBeatsStrategy = config.getExcessBeatsStrategy();
        separator = config.getSeparator();
        checkMark = config.getCheckMark();
        blankMark = config.getBlankMark();
    }

    public Collection<MidiInstrument> getInstruments() {
        return instrumentMap.keySet();
    }

    public float getTempo() {
        return sequencer.getTempoInBPM();
    }

    public void launch() {
        UIUtil.getUIBuilder()
                .withTitle("Beat Box")
                .withComponent(CENTER, () -> UIUtil.getComponentBuilder(() -> {
                            mainPanel = new JPanel(new BorderLayout());
                            return mainPanel;
                        })
                        .withBorder(() -> BorderFactory.createEmptyBorder(10, 10, 10, 10))
                        .withComponent(EAST, () -> UIUtil.getComponentBuilder(() -> new Box(Y_AXIS))
                                .withComponent(() -> {
                                    startButton = new JButton("Start");
                                    startButton.setEnabled(false);
                                    startButton.addActionListener(new StartButtonListener());
                                    return startButton;
                                })
                                .withComponent(() -> {
                                    stopButton = new JButton("Stop");
                                    stopButton.setEnabled(false);
                                    stopButton.addActionListener(new StopButtonListener());
                                    return stopButton;
                                })
                                .withComponent(() -> {
                                    tempoUpButton = new JButton("Tempo Up");
                                    tempoUpButton.setEnabled(false);
                                    tempoUpButton.addActionListener(new TempoUpButtonListener());
                                    return tempoUpButton;
                                })
                                .withComponent(() -> {
                                    tempoDownButton = new JButton("Tempo Down");
                                    tempoDownButton.setEnabled(false);
                                    tempoDownButton.addActionListener(new TempoDownButtonListener());
                                    return tempoDownButton;
                                })
                                .withComponent(() -> {
                                    clearButton = new JButton("Clear");
                                    clearButton.setEnabled(false);
                                    clearButton.addActionListener(new ClearButtonListener());
                                    return clearButton;
                                })
                                .withComponent(() -> {
                                    saveButton = new JButton("Save Pattern...");
                                    saveButton.setEnabled(false);
                                    saveButton.addActionListener(new SaveButtonListener());
                                    return saveButton;
                                })
                                .withComponent(() -> {
                                    var loadButton = new JButton("Load Pattern...");
                                    loadButton.addActionListener(new LoadButtonListener());
                                    return loadButton;
                                })
                                .build())
                        .withComponent(WEST, () -> {
                            labelBox = new Box(Y_AXIS);
                            instrumentMap.keySet().forEach(k -> labelBox.add(new Label(k.getStandardName())));
                            return labelBox;
                        })
                        .withComponent(CENTER, () -> {
                            GridLayout gridLayout = new GridLayout(instrumentMap.size(), numberOfBeats);
                            gridLayout.setVgap(1);
                            gridLayout.setHgap(2);
                            checkBoxPanel = new JPanel(gridLayout);
                            checkBoxPanel.addMouseListener(new CheckBoxPanelMouseListener());
                            ensureOneBlankCheckBoxForEachBeat();
                            return checkBoxPanel;
                        })
                        .withVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED)
                        .withHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_AS_NEEDED)
                        .build())
                .withFrameSize(calculateAppropriateWidth(), calculateAppropriateHeight())
                .visualize();
    }

    private void ensureOneBlankCheckBoxForEachBeat() {
        checkBoxPanel.removeAll();
        instrumentMap.values().forEach(checkBoxList -> {
            checkBoxList.clear();
            checkBoxList.addAll(
                    Stream.generate(this::getNewCheckBox)
                            .peek(checkBox -> checkBoxPanel.add(checkBox))
                            .limit(numberOfBeats)
                            .toList()
            );
        });
        ((GridLayout) checkBoxPanel.getLayout()).setColumns(numberOfBeats);
        adaptFrameSize();
    }

    void addMoreBeats(int numberOfBeatsToAdd) {
        numberOfBeats += numberOfBeatsToAdd;
        ensureOneBlankCheckBoxForEachBeat();
    }

    void removeBeats(int numberOfBeatsToRemove) {
        numberOfBeats -= numberOfBeatsToRemove;
        ensureOneBlankCheckBoxForEachBeat();
    }

    private int calculateAppropriateWidth() {
        return Math.min(MAXIMUM_WINDOW_BOUNDS.width,
                MINIMUM_FRAME_WIDTH + numberOfBeats * WIDTH_PER_BEAT);
    }

    private int calculateAppropriateHeight() {
        return Math.min(MAXIMUM_WINDOW_BOUNDS.height,
                MINIMUM_FRAME_HEIGHT + instrumentMap.size() * HEIGHT_PER_INSTRUMENT);
    }

    private void makeTrackAndStart() {
        refreshTrack();
        addMidiEventsForSelectedCheckBoxes();
        addDummyEventAtLastBeat();
        setSequenceAndStart();
    }

    private void addMidiEventsForSelectedCheckBoxes() {
        for (Map.Entry<MidiInstrument, List<JCheckBox>> mapEntry : instrumentMap.entrySet()) {
            int instrumentFirstByte = mapEntry.getKey().getFirstByte();
            List<JCheckBox> checkBoxList = mapEntry.getValue();
            for (int i = 0; i < checkBoxList.size(); i++) {
                if (checkBoxList.get(i).isSelected()) {
                    track.add(MidiUtil.getMidiEventBuilder()
                            .withCommand(NOTE_ON)
                            .withChannel(9)
                            .withFirstByte(instrumentFirstByte)
                            .withSecondByte(100)
                            .withTick(i)
                            .build());
                    track.add(MidiUtil.getMidiEventBuilder()
                            .withCommand(NOTE_OFF)
                            .withChannel(9)
                            .withFirstByte(instrumentFirstByte)
                            .withSecondByte(100)
                            .withTick(i + 1)
                            .build());
                }
            }
        }
    }

    private void addDummyEventAtLastBeat() {
        /* adding an event at the last beat is important because the Sequencer may not
         * go all the beats otherwise */

        track.add(MidiUtil.getMidiEventBuilder()
                .withCommand(CONTROL_CHANGE) // it could be PROGRAM_CHANGE, it doesn't matter
                .withChannel(0) // any channel between 0 and 15, doesn't matter
                .withFirstByte(0) // the bytes should be between 0 and 127 inclusive
                .withSecondByte(0) // as long as within the range, the byte values don't matter
                .withTick(numberOfBeats) // the last beat
                .build());
    }

    private void refreshTrack() {
        sequence.deleteTrack(track);
        track = sequence.createTrack();
    }

    private void setSequenceAndStart() {
        Util.wrapInTryCatch(() -> {
            sequencer.setSequence(sequence);
            sequencer.setLoopCount(LOOP_CONTINUOUSLY);
            sequencer.start();
        });
    }

    void addNewInstrument(MidiInstrument instrument) {
        var checkBoxes = Stream.generate(this::getNewCheckBox).limit(numberOfBeats).toList();
        instrumentMap.put(instrument, checkBoxes);
        labelBox.add(new Label(instrument.getStandardName()));
        checkBoxes.forEach(checkBoxPanel::add);
        var checkBoxPanelLayout = (GridLayout) checkBoxPanel.getLayout();
        checkBoxPanelLayout.setRows(checkBoxPanelLayout.getRows() + 1);
    }

    private JCheckBox getNewCheckBox() {
        var checkBox = new JCheckBox();
        checkBox.addMouseListener(checkBoxMouseListener);
        checkBox.addItemListener(checkBoxItemListener);
        checkBox.setSelected(false);
        return checkBox;
    }

    private void adaptFrameSize() {
        JFrame frame = getFrame();
        if (frame != null) {
            frame.setSize(calculateAppropriateWidth(), calculateAppropriateHeight());
        }
    }

    private JFrame getFrame() {
        return (JFrame) SwingUtilities.getWindowAncestor(mainPanel);
    }

    private void togglePlaybackControls() {
        boolean isRunning = sequencer.isRunning();
        stopButton.setEnabled(isRunning);
        tempoUpButton.setEnabled(isRunning);
        tempoDownButton.setEnabled(isRunning);
    }

    private class StartButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            makeTrackAndStart();
            togglePlaybackControls();
        }
    }

    private class StopButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            sequencer.stop();
            togglePlaybackControls();
        }
    }

    private class TempoUpButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            float currentTempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor(currentTempoFactor * 1.03f);
        }
    }

    private class TempoDownButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            float currentTempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor(currentTempoFactor * 0.97f);
        }
    }

    private class ClearButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            instrumentMap.values().stream()
                    .flatMap(Collection::stream)
                    .forEach(checkBox -> checkBox.setSelected(false));
        }
    }

    private class SaveButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.showSaveDialog(mainPanel);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileChooser.getSelectedFile()))) {
                writeManifest(writer);
                String paddedSeparator = " " + separator + " ";
                for (Map.Entry<MidiInstrument, List<JCheckBox>> mapEntry : instrumentMap.entrySet()) {
                    writer.write(mapEntry.getKey().getStandardName() + paddedSeparator +
                            mapEntry.getValue().stream()
                                    .map(checkBox -> checkBox.isSelected() ? checkMark : blankMark)
                                    .collect(Collectors.joining()) + "\n");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private void writeManifest(BufferedWriter writer) throws IOException {
            writer.write("SEPARATOR " + separator + "\n");
            writer.write("CHECK MARK " + checkMark + "\n");
            writer.write("BLANK MARK " + blankMark + "\n");
        }
    }

    private class LoadButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            JFileChooser fileChooser = new JFileChooser();
            int returnState = fileChooser.showOpenDialog(mainPanel);
            if (returnState == JFileChooser.APPROVE_OPTION) {
                performPatternLoading(fileChooser);
            } else if (returnState == JFileChooser.CANCEL_OPTION) {
                log.info("No pattern is selected – loading is aborted");
            }
        }

        private void performPatternLoading(JFileChooser fileChooser) {
            boolean frameResizingRequired = false;
            try (BufferedReader reader = new BufferedReader(new FileReader(fileChooser.getSelectedFile()))) {
                PatternManifest m = ManifestParser.parseManifest(reader);
                Map<String, String[]> namedBeatRows = readNamedBeatRows(reader, m);
                ensureEqualBeatRowLengths(namedBeatRows, m.blankMark());
                int numberOfBeatsInPattern = namedBeatRows.values().stream().findAny().orElseThrow().length;
                if (numberOfBeats < numberOfBeatsInPattern) {
                    missingBeatsStrategy.handle(new BeatsMismatchContext(
                            BeatBox.this, namedBeatRows.values().stream().toList(), m.checkMark()
                    ));
                } else if (numberOfBeats > numberOfBeatsInPattern) {
                    excessBeatsStrategy.handle(new BeatsMismatchContext(
                            BeatBox.this, namedBeatRows.values().stream().toList(), m.checkMark()
                    ));
                }
                for (Map.Entry<String, String[]> entry : namedBeatRows.entrySet()) {
                    String instrumentName = entry.getKey();
                    String[] instrumentBeats = entry.getValue();
                    var optionalMatchingEntry = findMatchingEntry(instrumentName);
                    if (optionalMatchingEntry.isPresent()) {
                        copyBeatPattern(optionalMatchingEntry.get().getValue(), instrumentBeats, m);
                    } else {
                        boolean isInstrumentAdded = missingInstrumentStrategy.handle(new InstrumentMismatchContext(
                                BeatBox.this, instrumentName,
                                instrumentBeats, m.checkMark()
                        ));
                        if (isInstrumentAdded) {
                            copyBeatPattern(findMatchingEntry(instrumentName).orElseThrow().getValue(), instrumentBeats, m);
                            frameResizingRequired = true;
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (IllegalPatternException e) {
                log.error(e.getMessage());
            }
            if (frameResizingRequired) {
                BeatBox.this.adaptFrameSize();
            }
        }

        private void ensureEqualBeatRowLengths(Map<String, String[]> namedBeatRows, String blankMark) {
            int maxBeatsInPattern = getMaxBeatsInPattern(namedBeatRows);
            namedBeatRows.replaceAll((name, beatRow) -> (beatRow.length < maxBeatsInPattern) ?
                    normalizeRow(beatRow, maxBeatsInPattern, blankMark) : beatRow);
        }

        private int getMaxBeatsInPattern(Map<String, String[]> namedBeatRows) {
            return namedBeatRows.values().stream()
                    .mapToInt(row -> row.length)
                    .max().orElseThrow();
        }

        private Map<String, String[]> readNamedBeatRows(BufferedReader reader, PatternManifest m) throws IOException {
            Map<String, String[]> namedBeatRows = new HashMap<>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] nameBeatsPair = line.split(m.separator());
                String instrumentName = nameBeatsPair[0].trim();
                String[] instrumentBeats = nameBeatsPair[1].trim().split("");
                namedBeatRows.put(instrumentName, instrumentBeats);
            }
            return namedBeatRows;
        }

        private String[] normalizeRow(String[] beatRow, int requiredLength,
                                      String paddingCharacter) {
            return ArrayUtils.addAll(beatRow, paddingCharacter.repeat(requiredLength - beatRow.length).split(""));
        }

        private Optional<Map.Entry<MidiInstrument, List<JCheckBox>>> findMatchingEntry(String instrumentName) {
            return instrumentMap.entrySet().stream()
                    .filter(entry -> entry.getKey().getStandardName().equalsIgnoreCase(instrumentName))
                    .findFirst();
        }

        private void copyBeatPattern(List<JCheckBox> appBeats, String[] patternBeats, PatternManifest m) {
            for (int i = 0; i < appBeats.size(); i++) {
                var currentCheckBox = appBeats.get(i);
                if (i < patternBeats.length) {
                    if (patternBeats[i].equals(m.checkMark())) {
                        currentCheckBox.setSelected(true);
                    } else if (patternBeats[i].equals(m.blankMark())) {
                        currentCheckBox.setSelected(false);
                    }
                } else {
                    currentCheckBox.setSelected(false);
                }
            }
        }
    }

    private class CheckBoxItemListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                setPatternControlsTo(true);
            } else if ((e.getStateChange() == ItemEvent.DESELECTED) && noCheckBoxChecked()) {
                setPatternControlsTo(false);
            }
        }

        private void setPatternControlsTo(boolean value) {
            startButton.setEnabled(value);
            saveButton.setEnabled(value);
            clearButton.setEnabled(value);
        }

        private boolean noCheckBoxChecked() {
            return instrumentMap.values().stream()
                    .flatMap(Collection::stream)
                    .noneMatch(AbstractButton::isSelected);
        }
    }

    private class CheckBoxPanelMouseListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            isMousePressed = true;
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            isMousePressed = false;
        }
    }

    private class CheckBoxMouseListener extends MouseAdapter {
        private JCheckBox originallyPressedCheckBox;
        private JCheckBox lastEnteredCheckBox;

        @Override
        public void mousePressed(MouseEvent e) {
            originallyPressedCheckBox = (JCheckBox) e.getSource();
            isMousePressed = true;
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            originallyPressedCheckBox = null;
            lastEnteredCheckBox = null;
            isMousePressed = false;
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            if (isMousePressed) {
                var enteredCheckBox = (JCheckBox) e.getSource();
                lastEnteredCheckBox = enteredCheckBox;
                pressedButtonStrategy.apply(enteredCheckBox);
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            var exitedCheckBox = (JCheckBox) e.getSource();
            if (isMousePressed &&
                    exitedCheckBox == originallyPressedCheckBox &&
                    exitedCheckBox != lastEnteredCheckBox) {
                pressedButtonStrategy.apply(exitedCheckBox);
            }
        }
    }
}
