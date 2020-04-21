import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class SocketClient {
	Socket server;
	
	public void connect(String address, int port) {
		try {
			server = new Socket(address, port);
			System.out.println("Client connected");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void start() throws IOException {
		if(server == null) {
			return;
		}
		System.out.println("Client Started");
		//listen to console, server in, and write to server out
		try(Scanner si = new Scanner(System.in);
				ObjectOutputStream out = new ObjectOutputStream(server.getOutputStream());
				ObjectInputStream in = new ObjectInputStream(server.getInputStream());){
			//let's block the thread for a sec to gather a username
			String name ="";
			do {
				name = "";
			}
			while(!server.isClosed() && name != null && name.length() == 0);
			//we should have a name, let's tell our server
			Payload p = new Payload();
			//we can also default payloadtype in payload
			//to a desired value, though it's good to be clear
			//what we're sending
			p.setPayloadType(PayloadType.CONNECT);
			p.setMessage(name);
			out.writeObject(p);
			
			
			//Thread to listen for keyboard input so main thread isn't blocked
			Thread inputThread = new Thread() {
				@Override
				public void run() {
					try {
						while(!server.isClosed()) {
							System.out.println("Waiting for input");
							String line = si.nextLine();
							if(!"quit".equalsIgnoreCase(line) && line != null) {
								//grab line and throw it into a payload object
								Payload p = new Payload();
								//we can also default payloadtype in payload
								//to a desired value, though it's good to be clear
								//what we're sending
								p.setPayloadType(PayloadType.MESSAGE);
								p.setMessage(line);
								out.writeObject(p);
							}
							else {
								System.out.println("Stopping input thread");
								//we're quitting so tell server we disconnected so it can broadcast
								Payload p = new Payload();
								p.setPayloadType(PayloadType.DISCONNECT);
								p.setMessage("bye");
								out.writeObject(p);
								break;
							}
						}
					}
					catch(Exception e) {
						System.out.println("Client shutdown");
					}
					finally {
						close();
					}
				}
			};
			inputThread.start();//start the thread
			
			//Thread to listen for responses from server so it doesn't block main thread
			Thread fromServerThread = new Thread() {
				@Override
				public void run() {
					try {
						Payload fromServer;
						//while we're connected, listen for payloads from server
						while(!server.isClosed() && (fromServer = (Payload)in.readObject()) != null) {
							//System.out.println(fromServer);
							processPayload(fromServer);
						}
						System.out.println("Stopping server listen thread");
					}
					catch (Exception e) {
						if(!server.isClosed()) {
							e.printStackTrace();
							System.out.println("Server closed connection");
						}
						else {
							System.out.println("Connection closed");
						}
					}
					finally {
						close();
					}
				}
			};
			fromServerThread.start();//start the thread
			
			//Keep main thread alive until the socket is closed
			//initialize/do everything before this line
			while(!server.isClosed()) {
				Thread.sleep(50);
			}
			System.out.println("Exited loop");
			System.exit(0);//force close
			//TODO implement cleaner closure when server stops
			//without this, it still waits for input before terminating
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			close();
		}
	}
	private void processPayload(Payload payload) {
		System.out.println(payload);
		switch(payload.getPayloadType()) {
		case CONNECT:
			System.out.println(
					String.format("Client \"%s\" connected", payload.getMessage())
			);
			break;
		case DISCONNECT:
			System.out.println(
					String.format("Client \"%s\" disconnected", payload.getMessage())
			);
			break;
		case MESSAGE:
			System.out.println(
					String.format("%s", payload.getMessage())
			);
			break;
		default:
			System.out.println("Unhandled payload type: " + payload.getPayloadType().toString());
			break;
		}
	}
	private void close() {
		if(server != null) {
			try {
				server.close();
				System.out.println("Closed socket");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public static void main(String[] args) {
		SocketClient client = new SocketClient();
		
		//create frame
		JFrame frame = new JFrame("Chat Room"); 
		frame.setLayout(new BorderLayout());
		
		//create panel
		JPanel chatRoom = new JPanel();
		chatRoom.setPreferredSize(new Dimension (400,400));
		chatRoom.setLayout(new BorderLayout());
		
		//create top panel
		JPanel topConnect = new JPanel();
		
		
		//create text area for messages and displaying users
		JTextArea chatTextArea = new JTextArea();
		JTextArea usersText = new JTextArea();
		
		//don't let the user edit this directly
		chatTextArea.setEditable(false);
		chatTextArea.setText("");
		
		//create panel to hold multiple controls
		JPanel chatArea = new JPanel();
		chatArea.setPreferredSize(new Dimension(250, 400));
		chatArea.setLayout(new BorderLayout());
		
		//create panel to display connected users
		JPanel usersArea = new JPanel();
		usersArea.setPreferredSize(new Dimension(150, 400));
		usersArea.setLayout(new BorderLayout());
		
		//add text area to chat area and usersArea
		chatArea.add(chatTextArea, BorderLayout.CENTER);
		chatArea.setBorder(BorderFactory.createLineBorder(Color.black));
		
		usersArea.add(usersText, BorderLayout.CENTER);
		usersArea.setBorder(BorderFactory.createLineBorder(Color.black));
		
		JLabel uLabel = new JLabel();
		uLabel.setText("Online users");
		
		// add label to users area
		usersArea.add(uLabel, BorderLayout.NORTH);
		
		
		//add chat area and users area to panel
		chatRoom.add(chatArea, BorderLayout.WEST);
		chatRoom.add(usersArea, BorderLayout.EAST);
		
		//username textfield
		JTextField usernameField = new JTextField("Your username");
		usernameField.setPreferredSize(new Dimension(100, 30));
		
		
		//IP and port input fields
		JTextField ipAdd = new JTextField("127.0.0.1");
		ipAdd.setPreferredSize(new Dimension(100, 30));
		JTextField portNum = new JTextField("3002");
		portNum.setPreferredSize(new Dimension(50, 30));
		
		//connect button
		JButton connect = new JButton();
		connect.setPreferredSize(new Dimension (100, 30));
		connect.setText("Connect");
		connect.addActionListener(new ActionListener() {
		
				
				@Override
				public void actionPerformed(ActionEvent e) {
					String username = usernameField.getText();
					usersText.append("\n" + username);
					
					connect.setText("Reconnect");
				
				}
			}
		);
		
		
		
		//create panel to hold multiple controls
		JPanel userInput = new JPanel();
		
		//setup textfield
		JTextField messageField = new JTextField();
		messageField.setPreferredSize(new Dimension(100, 30));
		
		
		//setup submit button
		JButton send = new JButton();
		send.setPreferredSize(new Dimension(100, 30));
		send.setText("Send");
		send.addActionListener(new ActionListener () {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String message = messageField.getText();
				String username = usernameField.getText();
		
				
				if(message.length()> 0) {
					//append a new line & text from textfield to textarea
					chatTextArea.append("\n" + username + ": " + messageField.getText());
					//reset textfield
					messageField.setText("");
				}
			}
			
			
		});
		
		//add username input and connect input 
		topConnect.add(usernameField);
		topConnect.add(ipAdd);
		topConnect.add(portNum);
		topConnect.add(connect);
		
		//add textfield and button to panel
		userInput.add(messageField);
		userInput.add(send);
		
		//add panels to chatRoom panel
		chatRoom.add(userInput, BorderLayout.SOUTH);
		chatRoom.add(topConnect, BorderLayout.NORTH);
		
		
		//add chatRoom panel to frame
		frame.add(chatRoom, BorderLayout.CENTER);
		
		frame.pack();
		frame.setVisible(true);
		
		
		client.connect(ipAdd.getText(), Integer.parseInt(portNum.getText()));
		try {
			//if start is private, it's valid here since this main is part of the class
			client.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}