package org.MidiMixerClient;

import java.awt.*;
import java.io.*;
import java.util.*;

import javax.sound.midi.*;
import javax.swing.*;


public class Tracks extends TracksMetaInfo implements Serializable{
	Sequencer sequencer;
	Sequence seq;
	Track track;

	public Tracks() {
		try {
			sequencer = MidiSystem.getSequencer();
			sequencer.open();
			seq = new Sequence(Sequence.PPQ, 4);
			track = seq.createTrack();
			tempo = 120;
			sequencer.setTempoInBPM(tempo);
			playing = false;
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public Tracks(TracksMetaInfo pMeta) {
		
	}
}