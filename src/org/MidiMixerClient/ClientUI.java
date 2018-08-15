package org.MidiMixerClient;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import java.util.*;
import java.io.*;
import javax.sound.midi.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;

import java.net.*;

public class ClientUI{
	JFrame uiBackboneframe;
	JPanel uiCheckBoxPanel;
	JSlider uiSeek;
	JList uiChatBoxList;
	JTextField uiMessageBox;
	ArrayList<JCheckBox> CurrCheckBoxes = new ArrayList<JCheckBox>();
	Tracks CurrTrack;
	ArrayList<Tracks> MixerTracks = new ArrayList<Tracks>();
	Vector<String> MessagesStore = new Vector<String>();
	HashMap<String, Tracks> ReceivedMessages = new HashMap<String, Tracks>();
	int MessageCount = 1;
	String userName;
	ObjectOutputStream ObjOutStream;
	ObjectInputStream ObjInStream;
	
	// Available Instruments
	String[] InstrumentNames = new String[] {"Bass Drum",
			"Closed Hi-Hat", "Open Hi-Hat","Acoustic Snare",
			"Crash Cymbal", "Hand Clap", "High Tom", "Hi Bongo", "Maracas", "Whistle",
			"Low Conga", "Cowbell", "Vibraslap", "Low-mid Tom", "High Aqogo", "Open Hi Conga"};
	int Instruments[] = new int[] {35,42,46,38,49,39,50,60,70,72,64,56,58,47,67,63};
	
	public static void main(String[] args) {
		ClientUI client = new ClientUI();
		client.go();
	}
	
	public void go() {
		userName = "HPotter";
		try {
			Socket conn = new Socket("127.0.0.1",4242);
			ObjOutStream = new ObjectOutputStream(conn.getOutputStream());
			ObjInStream = new ObjectInputStream(conn.getInputStream());
			//Start a thread to read and store incoming messages
			Thread msgReader = new Thread(new MsgReader());
			msgReader.start();
		} catch(Exception ex) {
			System.out.println("Server Connection Failed! Limited features supported");
		}
		buildGUI();
		CurrTrack = new Tracks();
	}
	
	public void changeBoth(JComponent comp, int width, int height) {
		comp.setAlignmentX(Component.CENTER_ALIGNMENT);
	    comp.setAlignmentY(Component.CENTER_ALIGNMENT);
	    Dimension dim = comp.getPreferredSize();
	    dim.width = width;
	    dim.height = height;
	    comp.setMaximumSize(dim);
	}
	public void changeBoth(JComponent comp) {
		changeBoth(comp, Integer.MAX_VALUE, Integer.MAX_VALUE);
	}
	public void changeWidth(JComponent comp) {
		changeBoth(comp, Integer.MAX_VALUE,comp.getPreferredSize().height);
	}
	public void changeHeight(JComponent comp) {
		changeBoth(comp, comp.getPreferredSize().width, Integer.MAX_VALUE);
	}
	
	public void addHSpace(JComponent comp) {
		comp.add(Box.createRigidArea(new Dimension(5,0)));
	}
	public void addVSpace(JComponent comp) {
		comp.add(Box.createRigidArea(new Dimension(0,5)));
	}
	public void addRigidArea(JComponent comp, int width, int height) {
		comp.add(Box.createRigidArea(new Dimension(width, height)));
	}
	
	public void buildGUI() {
		uiBackboneframe = new JFrame("Midi Player and Mixer");
		uiBackboneframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		BorderLayout borderLayout = new BorderLayout();
		borderLayout.setHgap(20);
		JPanel backgroundPanel = new JPanel(borderLayout);
		backgroundPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		CurrCheckBoxes = new ArrayList<JCheckBox>();
		
		JPanel vRightPanel = new JPanel();
		vRightPanel.setLayout(new BoxLayout(vRightPanel,BoxLayout.Y_AXIS));
		JButton btnPlay = new JButton("Play");
		btnPlay.addActionListener(new btnPlayListener());
		changeWidth(btnPlay);
		vRightPanel.add(btnPlay);
		addVSpace(vRightPanel);
		
		JPanel vRPanel1 = new JPanel();
		vRPanel1.setLayout(new BoxLayout(vRPanel1,BoxLayout.X_AXIS));
		Label lTempo = new Label("Tempo");
		vRPanel1.add(lTempo);
		JButton btnTempoUP = new JButton("Inc");
		btnTempoUP.addActionListener(new btnTempoUPListener());
		changeWidth(btnTempoUP);
		vRPanel1.add(btnTempoUP);
		addHSpace(vRPanel1);
		JButton btnTempoDown = new JButton("Dec");
		btnTempoDown.addActionListener(new btnTempoDownListener());
		changeWidth(btnTempoDown);
		vRPanel1.add(btnTempoDown);
		vRightPanel.add(vRPanel1);
		addVSpace(vRightPanel);
		
		JPanel vRPanel2 = new JPanel();
		vRPanel2.setLayout(new BoxLayout(vRPanel2,BoxLayout.X_AXIS));
		JButton btnSetAs1 = new JButton("Set As 1");
		//btnSetAs1.addActionListener(new btnSetAs1Listener());
		changeWidth(btnSetAs1);
		vRPanel2.add(btnSetAs1);
		addHSpace(vRPanel2);
		JButton btnSetAs2 = new JButton("Set As 2");
		//btnSetAs2.addActionListener(new btnSetAs2Listener());
		changeWidth(btnSetAs2);
		vRPanel2.add(btnSetAs2);
		vRightPanel.add(vRPanel2);
		addVSpace(vRightPanel);
		
		JPanel vRPanel3 = new JPanel();
		vRPanel3.setLayout(new BoxLayout(vRPanel3,BoxLayout.X_AXIS));
		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new btnSaveListener());
		changeWidth(btnSave);
		vRPanel3.add(btnSave);
		addHSpace(vRPanel3);
		JButton btnRestore = new JButton("Restore");
		btnRestore.addActionListener(new btnRestoreListener());
		changeWidth(btnRestore);
		vRPanel3.add(btnRestore);
		addHSpace(vRPanel3);
		JButton btnClear = new JButton("Clear");
		//btnClear.addActionListener(new btnClearListener());
		changeWidth(btnClear);
		vRPanel3.add(btnClear);
		vRightPanel.add(vRPanel3);
		addVSpace(vRightPanel);
		
		uiMessageBox = new JTextField();
		vRightPanel.add(uiMessageBox);
		JButton btnSendMsg = new JButton("send");
		//btnSendMsg.addActionListener(new btnSendMsgListener());
		changeBoth(btnSendMsg,100,5);
		vRightPanel.add(btnSendMsg);
		addVSpace(vRightPanel);
		
		uiChatBoxList = new JList();
		//uiChatBoxList.addListSelectionListener(new uiChatBoxListListener());
		//uiChatBoxList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane theList = new JScrollPane(uiChatBoxList);
		vRightPanel.add(theList);
		uiChatBoxList.setListData(MessagesStore);
		
		
		JPanel vLeftPanel = new JPanel();
		vLeftPanel.setLayout(new BoxLayout(vLeftPanel,BoxLayout.Y_AXIS));
		
		JPanel vLPanel1 = new JPanel(new BorderLayout());
		vLPanel1.setBorder(new TitledBorder("Midi-WorkSpace"));
		
		GridLayout instrumentsGrid = new GridLayout(16,1);
		instrumentsGrid.setVgap(1);
		instrumentsGrid.setHgap(2);
		JPanel uiInstrumentsPanel = new JPanel(instrumentsGrid);
		for(int i=0; i < 16; i++) {
			uiInstrumentsPanel.add(new Label(InstrumentNames[i]));
		}
		
		GridLayout checkBoxGrid =  new GridLayout(16,16);
		checkBoxGrid.setVgap(1);
		checkBoxGrid.setHgap(2);
		uiCheckBoxPanel = new JPanel(checkBoxGrid);
		for(int i=0; i < 256; i++) {
			JCheckBox c = new JCheckBox();
			c.setSelected(false);
			CurrCheckBoxes.add(c);
			uiCheckBoxPanel.add(c) ;
		}
		
		vLPanel1.add(uiInstrumentsPanel, BorderLayout.CENTER);
		vLPanel1.add(uiCheckBoxPanel, BorderLayout.EAST);
		vLeftPanel.add(vLPanel1);
		addVSpace(vLeftPanel);
		
		JPanel vLPanel2 = new JPanel();
		vLPanel2.setLayout(new BoxLayout(vLPanel2,BoxLayout.X_AXIS));
		JSlider sliderSeek = new JSlider(0,16,0);
		//sliderSeek.addChangeListener(new sliderSeekListener()); // implement ChangeListener
		changeWidth(sliderSeek);
		vLPanel2.add(sliderSeek);
		addHSpace(vLPanel2);
		JLabel lTime = new JLabel("0");
		changeWidth(lTime);
		vLPanel2.add(lTime);
		addHSpace(vLPanel2);
		JButton btnOpenMixer = new JButton("Open Mixer");
		//btnOpenMixer.addActionListener(new btnOpenMixerListener());
		changeWidth(btnOpenMixer);
		vLPanel2.add(btnOpenMixer);
		vLeftPanel.add(vLPanel2);
		
		backgroundPanel.add(BorderLayout.CENTER, vLeftPanel);
		backgroundPanel.add(BorderLayout.EAST , vRightPanel);
		//backgroundPanel.add(BorderLayout.WEST , bxInstruments);
		//vLeftPanel.add(BorderLayout.SOUTH, vSouthPanel);
		
		uiBackboneframe.getContentPane().add(backgroundPanel) ;
		uiBackboneframe.setBounds(50,50,300,300);
		uiBackboneframe.pack () ;
		uiBackboneframe.setVisible(true);
	}
	
	public void buildTrackAndStart() {
		ArrayList<Integer> trackList = null;
		CurrTrack.seq.deleteTrack(CurrTrack.track);
		CurrTrack.track = CurrTrack.seq.createTrack();
		CurrTrack.CheckBoxes = CurrCheckBoxes;
		
		for(int i=0; i<16; i++) {
			trackList = new ArrayList<Integer>();
			for(int j=0; j<16; j++) {
				JCheckBox c = CurrCheckBoxes.get(j+(i*16));
				if(c.isSelected()) {
					int key = Instruments[i];
					trackList.add(new Integer(key));
				}
				else trackList.add(null);
			}
			makeTracks(trackList);
		}
		CurrTrack.track.add(makeEvent(192,9,1,0,15));
		try {
			CurrTrack.sequencer.setSequence(CurrTrack.seq);
			CurrTrack.sequencer.setLoopCount(CurrTrack.sequencer.LOOP_CONTINUOUSLY);
			CurrTrack.sequencer.start();
			//CurrTrack.sequencer.setTempoInBPM(CurrTrack.tempo);
			CurrTrack.playing = true;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public class MsgReader implements Runnable{
		public void run() {
			try {
				Object obj = null;
				while((obj = ObjInStream.readObject()) != null) {
					System.out.println("Got an object: "+ obj.getClass());
					String vMessage = (String) obj;
					Tracks vTrack = (Tracks) ObjInStream.readObject();
					ReceivedMessages.put(vMessage, vTrack);
					MessagesStore.add(vMessage);
					uiChatBoxList.setListData(MessagesStore);
				}
			}catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public class btnPlayListener implements ActionListener{
		public void actionPerformed(ActionEvent a){
			JButton vbtn = (JButton) a.getSource();
			if(!CurrTrack.playing) {
				vbtn.setText("Pause");
				buildTrackAndStart();
			}
			else {
				CurrTrack.sequencer.stop();
				CurrTrack.playing = false;
				vbtn.setText("Play");
			}
		}
	}
	
	public class btnTempoUPListener implements ActionListener{
		public void actionPerformed(ActionEvent a) {
			float tempo = CurrTrack.tempo;
			CurrTrack.sequencer.setTempoInBPM((float) (tempo * 1.03));
			CurrTrack.tempo = (float) (tempo * 1.03);
		}
	}
	
	public class btnTempoDownListener implements ActionListener{
		public void actionPerformed(ActionEvent a) {
			float tempo = CurrTrack.tempo;
			CurrTrack.sequencer.setTempoInBPM((float) (tempo * 0.97));
			CurrTrack.tempo = (float) (tempo * 0.97);
		}
	}
	
	public class btnSaveListener implements ActionListener{
		public void actionPerformed(ActionEvent a) {
			JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			btnSaveFileFilter filter = new btnSaveFileFilter();
			fc.setFileFilter(filter);
			
			int returnVal = fc.showSaveDialog(null);
		    if (returnVal == JFileChooser.APPROVE_OPTION) {
		      File file = fc.getSelectedFile();
		      try{
		    	String filePath = file.getAbsolutePath();
				if(!filePath.endsWith(".mixer")) {
				  file = new File(filePath + ".mixer");
				}
				FileOutputStream outFS = new FileOutputStream(file);
				ObjectOutputStream outOS = new ObjectOutputStream(outFS);
				outOS.writeObject(CurrTrack.CheckBoxes);
				outOS.close();
				JOptionPane.showMessageDialog(null, "Successfully Saved");
		      }catch(IOException e){e.printStackTrace();}
		    }
		}
	}
	
	public class btnSaveFileFilter extends javax.swing.filechooser.FileFilter {
	    public boolean accept(File f) {
	        return f.isDirectory() || f.getName().toLowerCase().endsWith(".mixer");
	    }
	    public String getDescription() {
	        return "*.mixer";
	    } 
	}
	
	public class btnRestoreListener implements ActionListener{
		public void actionPerformed(ActionEvent a) {
			JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			FileNameExtensionFilter filter = new FileNameExtensionFilter(".mixer", "mixer");
			fc.setFileFilter(filter);
			
			int returnVal = fc.showOpenDialog(null);
		    if (returnVal == JFileChooser.APPROVE_OPTION) {
		      File file = fc.getSelectedFile();
		      try{
		    	String filePath = file.getAbsolutePath();
				if(!filePath.endsWith(".mixer")) {
					JOptionPane.showMessageDialog(null, "Only .mixer file format is supported");
					return;
				}
				FileInputStream inFS = new FileInputStream(file);
				ObjectInputStream inOS = new ObjectInputStream(inFS);
				ArrayList<JCheckBox> vCheckBoxes = (ArrayList<JCheckBox>) inOS.readObject();
				//paintCanvas(vCheckBoxes);
				//System.out.println(vCheckBoxes.get(0).isSelected());
				inOS.close();
				JOptionPane.showMessageDialog(null, "File Sucessfully Loaded");
		      }catch(Exception e){e.printStackTrace();}
		    }
		}
	}
	
	public class MySendListener implements ActionListener{
		public void actionPerformed(ActionEvent a) {
			boolean[] checkboxState = new boolean[256] ;
			for (int i = 0; i < 256; i++) {
				JCheckBox c = CurrCheckBoxes.get(i);
				if(c.isSelected()) {
					checkboxState[i] = true;
				}
			}
			String messageToSend = null;
			try {
				ObjOutStream.writeObject(userName + MessageCount++ + ". " + uiMessageBox.getText());
				ObjOutStream.writeObject(checkboxState);
			}catch(Exception ex) {
				System.out.println("Could not send it to the server.") ;
			}
			uiMessageBox.setText("");
		}
	}
	
	public class MyListSelectionListener implements ListSelectionListener{
		public void valueChanged(ListSelectionEvent le){
			if(!le.getValueIsAdjusting()){
				String selected = (String) uiChatBoxList.getSelectedValue();
				if (selected != null){
					Tracks selectedTrack = (Tracks) ReceivedMessages.get(selected);
					changeTrack(selectedTrack);
					CurrTrack.sequencer.stop();
					buildTrackAndStart();
				}
			}
		}
	}
	
	public void changeTrack(Tracks track) {
		CurrCheckBoxes = track.CheckBoxes;
		return;
	}
	
	public void makeTracks(ArrayList list) {
		Iterator it = list.iterator();
		for(int i = 0; i < 16 ; i++) {
			Integer num = (Integer) it.next();
			if(num != null){
				int numKey = num.intValue() ;
				CurrTrack.track.add(makeEvent(144 ,9 ,numKey, 100, i));
				CurrTrack.track.add(makeEvent(128 ,9 ,numKey, 100, i+1));
			}
		}
	}
	
	public MidiEvent makeEvent(int comd, int chan, int one, int two, int tick) {
		MidiEvent event = null;
		try{
		ShortMessage a = new ShortMessage();
		a. setMessage (comd, chan, one, two);
		event = new MidiEvent(a, tick);
		}catch (Exception e) { }
		return event;
	}
	
}