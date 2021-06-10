package Chatting_Client;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;

import java.io.*;
import java.net.*;
import java.util.*;

public class Client extends JFrame implements ActionListener {
	static public String Player_NickName = "";
	static public String Player_IP = "";
	static public int Player_Port;
	static public String Chat_Mod = "";
	static public String Player_Whisping = "";
	
	JButton Login = new JButton("Login");
	JTextField NickName = new JTextField();
	JLabel NickName_Label = new JLabel("Nickname");
	JLabel Back_Ground = new JLabel();
	
	JTextField Port_Input = new JTextField();
	JLabel Port_Label = new JLabel("Port Number");
	
	JTextField IP_Input = new JTextField();
	JLabel IP_Label = new JLabel("IP Adress");
	
	JTextField LobbyChatInput = new JTextField();
	JTextArea List_Chat_Area = new JTextArea();
	JList Resent_List = new JList();
	
	JLabel User_List_Label = new JLabel("User List");
	JLabel User_Chat_Label = new JLabel("Chat Box");
	
	JButton Broad_Cast_Button = new JButton("All");
	JButton Uni_Cast_Button = new JButton("Selected");
	
	JScrollPane Resent_List_Scroll = new JScrollPane(Resent_List);
	JScrollPane List_Chat_AreaSP = new JScrollPane();
	JScrollBar Resent_List_ScrollBar = Resent_List_Scroll.getVerticalScrollBar();
	JScrollBar List_Chat_AreaSB = List_Chat_AreaSP.getVerticalScrollBar();
	
	Client() {
		this.setTitle("Client");
		this.setSize(200, 320);
		this.setVisible(true);
		this.setLayout(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Dimension dimen = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension dimenl = this.getSize();
		int xpos = (int) (dimen.getWidth() / 2 - dimenl.getWidth() / 2);
		int ypos = (int) (dimen.getHeight() / 2 - dimenl.getHeight() / 2);
		this.setLocation(xpos, ypos);
		
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		
		Back_Ground.setBounds(0, -15, 200, 320);
		
		NickName_Label.setBounds(20, 30, 150, 21);
		NickName_Label.setVisible((true));
		this.add(NickName_Label);
		
		NickName = new JTextField();
		NickName.setBounds(20, 60, 150, 21);
		this.add(NickName);
		NickName.setColumns(10);
		Port_Label.setBounds(20, 90, 150, 21);
		Port_Label.setVisible(true);
		this.add(Port_Label);
		
		Port_Input = new JTextField();
		Port_Input.setBounds(20, 120, 150, 21);
		this.add(Port_Input);
		Port_Input.setColumns(10);
		
		IP_Label.setBounds(20, 150, 150, 21);
		IP_Label.setVisible(true);
		this.add(IP_Label);
		
		IP_Input = new JTextField();
		IP_Input.setBounds(20, 180, 150, 21);
		this.add(IP_Input);
		IP_Input.setColumns(18);
		
		Login.setBounds(50, 230, 100, 40);
		Login.setBackground(new Color(102, 204, 204));
		Login.setVisible(true);
		Login.addActionListener(this);
		
		Broad_Cast_Button.addActionListener(this);
		Uni_Cast_Button.addActionListener(this);
		
		Port_Input.setText("9000");
		IP_Input.setText("127.0.0.1");
		this.add(Login);
		
		Back_Ground.setVisible(true);
		this.add(Back_Ground);
	}
	
	public static void main(String[] args) {
		new Client();
	}
		
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == Login) {
			if(NickName.getText().equals("") || NickName.getText().length() == 0) {}
			else if(Port_Input.getText().equals("") || Port_Input.getText().length() == 0) {}
			else if(IP_Input.getText().equals("") || IP_Input.getText().length() == 0) {}
			else {
				Player_Port = Integer.parseInt(Port_Input.getText().trim());
				Player_IP = IP_Input.getText().trim();
				Player_NickName = NickName.getText().trim();
				this.setVisible(false);
				new Login_Connect(Player_NickName, Player_IP, Player_Port);
				}
			}
		
		if(e.getSource() == Broad_Cast_Button) {
			Chat_Mod = "Broad_Cast";
			List_Chat_Area.append("Now, Talk for all.\n");
		}
		
		if(e.getSource() == Uni_Cast_Button) {
			Chat_Mod = "Uni_Cast";
			Player_Whisping = (String)Resent_List.getSelectedValue();
			List_Chat_Area.append("Now Talk for " + Player_Whisping + ".\n");
		}
	}
	
	public class Login_Connect extends JFrame {
		private String Name_;
		private String IP_;
		private int Port_;
		
		private Socket socket;
		private InputStream is;
		private OutputStream os;
		private DataInputStream dis;
		private DataOutputStream dos;
		Vector User_List = new Vector();
		
		StringTokenizer stp;
		
		Login_Connect(String Name, String IP, int Port) {
			this.Name_ = Name;
			this.IP_ = IP;
			this.Port_ = Port;
			
			Init();
			Start();
			Net_work();
		}
		
		public void Init() {
			this.setSize(400, 330);
			this.setLayout(null);
			this.setVisible(true);
			this.setResizable(false);
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.setTitle("Client");
			Dimension dimen = Toolkit.getDefaultToolkit().getScreenSize();
			Dimension dimenl = this.getSize();
			int xpos = (int) (dimen.getWidth() / 2 - dimenl.getWidth() / 2);
			int ypos = (int) (dimen.getHeight() / 2 - dimenl.getHeight() / 2);
			this.setLocation(xpos, ypos);
			
			User_List_Label.setBounds(20, 15, 150, 30);
			this.add(User_List_Label);
			
			User_Chat_Label.setBounds(200, 15, 150, 30);
			this.add(User_Chat_Label);
			
			Resent_List.setBackground(new Color(204, 255, 255));
			
			Resent_List_Scroll.setBounds(20, 55, 150, 200);
			this.add(Resent_List_Scroll);
			
			List_Chat_Area.setBounds(200, 55, 180, 170);
			List_Chat_Area.setForeground(new Color(0, 0, 0));
			List_Chat_Area.setBackground(new Color(204, 255, 255));
			List_Chat_Area.setEditable(false);
			this.add(List_Chat_Area);
			List_Chat_AreaSP.setViewportView(List_Chat_Area);
			List_Chat_AreaSP.setBounds(200, 55, 180, 170);
			List_Chat_AreaSP.setVisible(true);
			
			LobbyChatInput.setBounds(200, 235, 180, 20);
			LobbyChatInput.setColumns(40);
			
			Broad_Cast_Button.setBounds(205, 260, 80, 30);
			Broad_Cast_Button.setBackground(new Color(102, 204, 204));
			this.add(Broad_Cast_Button);
			Uni_Cast_Button.setBounds(295, 260, 80, 30);
			Uni_Cast_Button.setBackground(new Color(102, 204, 204));
			this.add(Uni_Cast_Button);
			
			this.add(LobbyChatInput);
			this.add(List_Chat_AreaSP);
			
			Chat_Mod = "Broad_Cast";
		}
		
		
		public void Start() {
			LobbyChatInput.addKeyListener(new KeyListener() {
				@Override
				public void keyReleased(KeyEvent e) {
					// TODO Auto-generated method stub
					if(e.getKeyCode() == 10) {
						if(LobbyChatInput.hasFocus() && 
						   LobbyChatInput.getText().trim().length() != 0) {
							if(Chat_Mod.equals("Broad_Cast")) {
								To_Server_Message("Chat_Message/" + Player_NickName + "/" + LobbyChatInput.getText().trim());
								LobbyChatInput.setText("");
								LobbyChatInput.requestFocus();
							}
							else if(Chat_Mod.equals("Uni_Cast")) {
								To_Server_Message("Whisping/" + Player_NickName + "/"  + Player_Whisping + "/" 
																+ LobbyChatInput.getText().trim());
								LobbyChatInput.setText("");
								LobbyChatInput.requestFocus();
							}
						}
						else {
							LobbyChatInput.setText("");
							LobbyChatInput.requestFocus();
						
						}
					}
				}
				
				public void keyPressed(KeyEvent e) {}
				public void keyTyped(KeyEvent e) {}
			});
		}
		
		public void To_Server_Message(String Str) {
			try {
				dos.writeUTF(Str);
			} catch (IOException e) {
				List_Chat_Area.append("Fail to connect server.\n");
			}
		}
		
		public void Net_work() {
			try {
				socket = new Socket(IP_, Port_);
				if(socket != null) {
					Connection();
				}
			} catch (UnknownHostException e) {}
			catch (IOException e) {
				List_Chat_Area.append("Socket connection error\n");
			}
		}
		
		public void Connection() {
			try {
				is = socket.getInputStream();
				dis = new DataInputStream(is);
				os = socket.getOutputStream();
				dos = new DataOutputStream(os);
			} catch (IOException e) {
				List_Chat_Area.append("Set stream error\n");
			}
			
			User_List.add(Name_);
			Resent_List.setListData((User_List));
			
			try {
				dos.writeUTF(Name_);
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			
			Thread Connecting_Thread = new Thread (new Runnable() {
				@Override
				public void run() {
					while(true) {
						try {
							String msg = dis.readUTF();
							In_Message(msg);
							
							List_Chat_AreaSB.setValue(List_Chat_AreaSB.getMaximum());
							System.out.println("Message : " + msg);
						} catch (IOException e) {
							List_Chat_Area.append("Server shutdown.\n");
							try {
								os.close();
								is.close();
								dos.close();
								dis.close();
								
								socket.close();
								break;
							} catch (IOException e1) {}
						}
					}
				}
			});
			
			Connecting_Thread.start();
		}
		
		public void In_Message(String Str) {
			stp = new StringTokenizer(Str, "/");
			String Protocol = stp.nextToken();
			String First_Message = stp.nextToken();
			
			if(Protocol.equals("New_User")) {
				User_List.add(First_Message);
				Resent_List.setListData(User_List);
				List_Chat_Area.append(First_Message + " is attending.\n");
			}
			else if(Protocol.equals("Old_User")) {
				User_List.add(First_Message);
				Resent_List.setListData(User_List);
			}
			else if(Protocol.equals("Chat_Message")) {
				String context = stp.nextToken();
				List_Chat_Area.append(First_Message + " : " + context + "\n");
			}
			else if(Protocol.equals("Remove_User")) {
				for(int i = 0; i < User_List.size(); i ++) {
					if(User_List.elementAt(i).equals(First_Message)) {
						User_List.remove(i);
						Resent_List.setListData(User_List);
					}
				}
				
				List_Chat_Area.append(First_Message + " is exit.\n");
			}
			else if(Protocol.equals("Whisping")) {
				String context = stp.nextToken();
				List_Chat_Area.append(First_Message + " Private Message : " + context + "\n");
			}
		}
	}
}
