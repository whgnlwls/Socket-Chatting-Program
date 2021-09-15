package Chatting_Server;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class Server extends JFrame implements ActionListener{
	Vector Connecting_User = new Vector();
	
	ServerSocket Server;
	Socket Socket;
	
	JLabel Server_State = new JLabel("Server Status : ");
	JLabel Server_Port = new JLabel("Port Number : ");
	JButton Server_Start = new JButton("Start");
	JButton Server_End = new JButton("Stop");
	
	JTextArea Connect_List = new JTextArea();
	JScrollPane Connect_ListSP = new JScrollPane();
	JTextField Input_Port_Number = new JTextField();
	
	JScrollBar Connect_ListSB = Connect_ListSP.getVerticalScrollBar();
	StringTokenizer stp;
	
	Server() {
		init();
		start();
	}
	
	public void init() {
		this.setTitle("Server");
		this.setSize(200, 300);
		this.setResizable(false);
		
		Dimension dimen = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension dimenl = this.getSize();
		int xpos = (int)(dimen.getWidth() / 2 - dimenl.getWidth() / 2);
		int ypos = (int)(dimen.getHeight() / 2 - dimenl.getHeight() / 2);
		
		this.setLocation(xpos, ypos);
		this.setVisible(true);
		this.setLayout(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Server_State.setBounds(40, 5, 230, 30);
		Server_State.setVisible(true);
		this.add(Server_State);
		
		Server_Port.setBounds(35, 230, 200, 20);
		Server_Port.setVisible(true);
		this.add(Server_Port);
		
		Input_Port_Number.setBounds(110, 230, 50, 20);
		Input_Port_Number.setVisible(true);
		this.add(Input_Port_Number);
		
		Server_Start.setBounds(15, 180, 80, 30);
		Server_Start.setVisible(true);
		this.add(Server_Start);
		
		Server_End.setBounds(100, 180, 80, 30);
		Server_End.setVisible(true);
		this.add(Server_End);
		
		Connect_List.setBounds(15, 45, 165, 120);
		Connect_List.setForeground(new Color(0, 0, 0));
		Connect_List.setBackground(new Color(204, 255, 255));
		Connect_List.setVisible(true);
		Connect_List.setEditable(false);
		this.add(Connect_List);
		
		Connect_ListSP.setViewportView(Connect_List);;
		Connect_ListSP.setVisible(true);
		Connect_ListSP.setBounds(15, 45, 165, 120);
		this.add(Connect_ListSP);
		
		Server_End.setEnabled(false);
		Input_Port_Number.setText("9000");
	}
	
	public void start() {
		Server_Start.addActionListener(this);
		Server_End.addActionListener(this);
	}
	
	public static void main(String[] args) {
		new Server();
	}
	
	@Override
	public void actionPerformed(ActionEvent Click) {
		if(Click.getSource() == Server_Start) {
			if(Input_Port_Number.getText().equals("") ||
			   Input_Port_Number.getText().length() == 0 ||
			   Input_Port_Number.getText().length() < 4) {
				Connect_List.append("Please set port number.\n");
				Input_Port_Number.requestFocus();
			}
			else {
				Connect_List.setText("");
				Connect_List.append("Server is start.\n");
				Server_State.setText("Server Status : Working");
				Server_End.setEnabled(true);
				Input_Port_Number.setEnabled(false);
				
				int Port_Number = Integer.parseInt(Input_Port_Number.getText());
				Server_Start(Port_Number);
			}
		}
		
		if(Click.getSource() == Server_End) {
			try {
				for(int i = 0; i < Connecting_User.size(); i++) {
					User_Info u = (User_Info) Connecting_User.elementAt(i);
					u.User_Socket.close();
				}
				
				Server.close();
			} catch(IOException e) {
				Connect_List.append("Server isn't working.\n");
				e.printStackTrace();
			}
			
			Connect_List.setText("");
			Connect_List.append("Server is stop\n");
			Server_State.setText("Server Status : Not Working\n");
			
			Server_Start.setEnabled(true);
			Server_End.setEnabled(false);
			Input_Port_Number.setEnabled(true);
		}
	}
	
	public void Server_Start (int Port_Number) {
		try {
			Server = new ServerSocket(Port_Number);
			Server_Start.setEnabled(false);
			
			if(Server != null) {
				Connect_List.append("Server is listening.\n");
				Connection_Server();
			}
		} catch(IOException e) {
			Connect_List.append("Already socket is using.\n");
			e.printStackTrace();
		}
	}
	
	public void Connection_Server() {
		Thread Connectting_Thread = new Thread (new Runnable() {
			@Override
			public void run() {
				int User_Count = 0;
				String isStop = "false";
				
				Connect_List.append("Listen client connection...\n");
				while(isStop != "true") {
					try {
						Socket = Server.accept();
						Connect_List.append("Client connect complete\n");
						
						User_Count++;
						User_Info User = new User_Info(Socket, User_Count);
						
						User.start();
					} catch (IOException e) {
						Connect_List.append("Accept error\n");
						isStop = "true";
					}
				}
			}
		});
		Connectting_Thread.start();
	}
	
	public class User_Info extends Thread {
		private InputStream is;
		private OutputStream os;
		private DataInputStream dis;
		private DataOutputStream dos;
		
		private String Nick_Name;
		private int User_ID;
		private Socket User_Socket;
		
		User_Info(Socket S, int ID) {
			this.User_Socket = S;
			this.User_ID = ID;
			
			User_Socket_NetWork();
		}
		
		public void User_Socket_NetWork() {
			try {
				is = User_Socket.getInputStream();
				dis = new DataInputStream(is);
				os = User_Socket.getOutputStream();
				dos = new DataOutputStream(os);
				
				Nick_Name = dis.readUTF();
				Connect_List.append("User ID : " + Nick_Name + " " + User_ID + "\n");
				Broad_Cast("New_User/" + Nick_Name);
				
				for(int i =0; i < Connecting_User.size(); i++) {
					User_Info u = (User_Info) Connecting_User.elementAt(i);
					To_Client_Message("Old_User/" + u.Nick_Name);
				}
				Connecting_User.add(this);
			} catch (IOException e) {
				Connect_List.append("Set stream error\n");
			}
		}
		
		public void To_Client_Message(String Str) {
			try {
				dos.writeUTF(Str);
			} catch (IOException e) {
				Connect_List.append("Message sending error\n");
			}
		}
		
		public void Broad_Cast(String Str) {
			for(int i = 0; i < Connecting_User.size(); i++) {
				User_Info u = (User_Info) Connecting_User.elementAt(i);
				u.To_Client_Message(Str);
			}
		}
		
		public void Uni_Cast(String Str, String StrWho) {
			for(int i = 0; i < Connecting_User.size(); i++) {
				User_Info u = (User_Info) Connecting_User.elementAt(i);
				
				if(StrWho.equals(u.Nick_Name)) {
					u.To_Client_Message(Str);
				}
			}
		}
		
		public void In_Message(String Str) {
			stp = new StringTokenizer(Str, "/");
			String Protocol = stp.nextToken();
			String First_Message = stp.nextToken();
			
			if(Protocol.equals("Chat_Message")) {
				String context = stp.nextToken();
				Broad_Cast("Chat_Message/" + First_Message + "/" + context);
			}
			else if(Protocol.equals("Whisping")) {
				String context = stp.nextToken();
				String context2 = stp.nextToken();
				Uni_Cast("Whisping/" + First_Message + "/" + context2, First_Message);
				Uni_Cast("Whisping/" + First_Message + "/" + context2, context);
			}
		}
		
		public void run() {
			while(true) {
				try {
					String msg = dis.readUTF();
					System.out.println("Message for Client : " + msg);
					In_Message(msg);
					
					Connect_ListSB.setValue(Connect_ListSB.getMaximum());
				} catch (IOException e) {
					try {
						Broad_Cast("Remove_User/" + Nick_Name);
						dos.close();
						dis.close();
						User_Socket.close();
						Connecting_User.removeElement(this);
						Connect_List.append(Connecting_User.size() + " : Now connecting user\n");
						Connect_List.append("Client is exit, return source\n");
						break;
					} catch (IOException a) {}
				}
			}
		}
	}
}
