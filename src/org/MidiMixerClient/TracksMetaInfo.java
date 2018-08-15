package org.MidiMixerClient;

import java.io.*;
import java.util.ArrayList;
import javax.swing.JCheckBox;

public class TracksMetaInfo implements Serializable{
	float tempo;
	float trackLength;
	ArrayList<JCheckBox> CheckBoxes = new ArrayList<JCheckBox>();
	boolean playing;
}
