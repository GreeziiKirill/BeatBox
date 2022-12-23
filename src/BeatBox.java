
import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class BeatBox {
    JFrame frame;
    JPanel mainPanel;
    ArrayList<JCheckBox> checkBoxesList;
    Sequencer sequencer;
    Sequence sequence;
    Track track;

    String[] instrumentNames = {"Bass Drum", "Closed Hi-Hat", "Pent Hi-Hat",
            "Acoustic", "Crash Cymal", "Hand Clap",
            "High Tom", "Hi Bongo", "Maracas",
            "whistle", "Low Conga", "cowbell",
            "Vibraslap", "Low-mid Tom", "High Agogo",
            "Open Hi Conga"};

    int[] instument = {35,42,46,38,49,39,50,60,70,72,64,56,58,47,67,63};

    public static void main(String[] args) {
        BeatBox beatBox = new BeatBox();
        beatBox.buildGUI();
    }

    public void buildGUI(){
        frame = new JFrame("Cyber BeatBox");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        BorderLayout borderLayout = new BorderLayout();

        JPanel background = new JPanel();
        background.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        checkBoxesList = new ArrayList<JCheckBox>();

        Box buttonBox = new Box(BoxLayout.Y_AXIS);
        Box nameBox = new Box(BoxLayout.Y_AXIS);

        JButton start = new JButton("Start");
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buildTrackAndStart();
            }
        });

        JButton stop = new JButton("stop");
        stop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sequencer.stop();
            }
        });

        JButton upTempo = new JButton("upTempo");
        upTempo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                float tempofactor = sequencer.getTempoFactor();
                sequencer.setTempoFactor((float) (tempofactor * 1.03));
            }
        });

        JButton downTempo = new JButton("downTempo");
        downTempo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                float tempofactor = sequencer.getTempoFactor();
                sequencer.setTempoFactor((float) (tempofactor * 0.97));
            }
        });

        JButton clearCheckBox = new JButton("clearCheckBox");
        clearCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (JCheckBox jCheckBox:checkBoxesList) {
                    jCheckBox.setSelected(false);
                }
            }
        });

        buttonBox.add(start);
        buttonBox.add(stop);
        buttonBox.add(upTempo);
        buttonBox.add(downTempo);
        buttonBox.add(clearCheckBox);

        for(int i = 0; i < 16; i++){
            nameBox.add(new Label(instrumentNames[i]));
        }

        GridLayout gridLayout = new GridLayout(16,16);
        gridLayout.setVgap(1);
        gridLayout.setVgap(2);

        mainPanel = new JPanel(gridLayout);
        for(int i = 0; i < 256; i++){
            JCheckBox c = new JCheckBox();
            c.setSelected(false);
            checkBoxesList.add(c);
            mainPanel.add(c);
        }

        background.add(BorderLayout.EAST, nameBox);
        background.add(BorderLayout.CENTER,mainPanel);
        background.add(BorderLayout.WEST,buttonBox);

        setUpMidi();

        frame.getContentPane().add(background);
        frame.setBounds(50,50,300,300);
        frame.pack();
        frame.setVisible(true);
    }

    public void setUpMidi(){

        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();

            sequence = new Sequence(Sequence.PPQ, 4);
            Track track = sequence.createTrack();
            sequencer.setTempoInBPM(120);

        }catch (Exception ex){};

    }

    public void buildTrackAndStart(){
        int[] trackList = null;
        sequence.deleteTrack(track);
        track = sequence.createTrack();

        for(int i = 0; i < 16;i++){
            trackList = new int[16];

            int key = instument[i];

            for (int j = 0; j < 16; j++){
                JCheckBox jc = checkBoxesList.get(j+(16*i));
                if (jc.isSelected()){
                    trackList[j] = key;
                }else{
                    trackList[j] = 0;
                }
            }
            makeTracks(trackList);
            track.add(makeEvent(176,1,127,0,16));

        }
        track.add(makeEvent(192,9,1,0,15));
        try {
            sequencer.setSequence(sequence);
            sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);
            sequencer.start();
        }catch (Exception ex){

        }

    }

    public void makeTracks(int[] list){
        for (int i = 0; i < 16; i++){
            int key = list[i];
            if(key !=0){
                track.add(makeEvent(144,9,key,100,i));
                track.add(makeEvent(128,9,key,100,i+1));
            }
        }
    }
    public MidiEvent makeEvent(int comd, int chan, int one, int two, int tick){
        MidiEvent event = null;
        try {
            ShortMessage a = new ShortMessage();
            a.setMessage(comd,chan,one,two);
            event = new MidiEvent(a,tick);
        }catch (Exception e){
        }
        return event;
    }
}
