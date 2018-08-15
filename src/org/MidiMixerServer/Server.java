package org.MidiMixerServer;
import java.io.*;
import java.util.*;
import java.net.*;

public class Server{
	ArrayList<ObjectOutputStream> clientOutStreams;
	
	public static void main (String[] args) {
		new Server().go();
	}
	
	public void go(){
		clientOutStreams = new ArrayList<ObjectOutputStream>();
		try {
			ServerSocket serverSock = new ServerSocket(4242);
			while(true){
				Socket clientSocket = serverSock.accept();
				ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
				clientOutStreams.add(out);
				Thread t = new Thread(new ClientHandler(clientSocket));
				t. start() ;
				System.out.println("got a connection");
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
		
	public void tellEveryone(Object one , Object two) {
		Iterator it = clientOutStreams.iterator();
		while(it.hasNext()){
			try {
				ObjectOutputStream out = (ObjectOutputStream) it .next();
				out.writeObject(one) ;
				out.writeObject(two) ;
			}catch(Exception ex) {ex.printStackTrace();}
		}
	}
		
	public class ClientHandler implements Runnable{
		ObjectInputStream in;
		Socket clientSocket;
		
		public ClientHandler(Socket socket) {
			try{
				clientSocket = socket;
				in = new ObjectInputStream(clientSocket.getInputStream());
			}catch(Exception ex){ex.printStackTrace();}
		}
		
		public void run(){
			Object obj1 = null;
			Object obj2 = null;
			try{
				while((obj1 = in.readObject())!= null){
					obj2 = in .readObject();
					System.out.println("read two objects") ;
					tellEveryone(01, 02);
				}
			}catch(Exception ex){ex.printStackTrace();}
		}
	}
}
	
