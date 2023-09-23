package org.example;

public class App {
    public static void main(String[] args) {
        BeatBox beatBox = BeatBoxConfiguration.configure()
//                .setPressedButtonStrategy(PressedButtonStrategy.SELECT)
//                .setNumberOfBeats(10)
//                .setCheckMark("I")
//                .setInstruments(MARACAS, ACOUSTIC_SNARE, DRUMSTICKS)
                .build();
        beatBox.launch();
    }
}
