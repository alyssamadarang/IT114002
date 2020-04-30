import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatterBox extends JFrame implements OnReceive {
	// public SampleSocketClient client = new SampleSocketClient(); connect to Client
	static SocketClient client;
	static JTextArea chatLog;
	public static void main(String[] args) {
		//create frame
		//JFrame frame = new JFrame("Chat Room"); 
		ChatterBox frame = new ChatterBox();
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
		chatLog = chatTextArea;
		
		//don't let the user edit this directly
		chatTextArea.setEditable(false);
		chatTextArea.setText("");
		
		//create panel to hold multiple controls
		JPanel chatArea = new JPanel();
		chatArea.setPreferredSize(new Dimension(250, 400));
		chatArea.setLayout(new BorderLayout());
		
		//create panel to display connected users
		//JPanel usersArea = new JPanel();
		//usersArea.setPreferredSize(new Dimension(150, 400));
		//usersArea.setLayout(new BorderLayout());
		
		//add text area to chat area and usersArea
		chatArea.add(chatTextArea, BorderLayout.CENTER);
		chatArea.setBorder(BorderFactory.createLineBorder(Color.black));
		
		//usersArea.add(usersText, BorderLayout.CENTER);
		//usersArea.setBorder(BorderFactory.createLineBorder(Color.black));
		
		//JLabel uLabel = new JLabel();
		//uLabel.setText("Online users");
		
		// add label to users area
		//usersArea.add(uLabel, BorderLayout.NORTH);
		
		
		//add chat area and users area to panel
		chatRoom.add(chatArea, BorderLayout.CENTER);
		//chatRoom.add(usersArea, BorderLayout.EAST);
		
		//username textfield
		JTextField usernameField = new JTextField("Your username");
		usernameField.setPreferredSize(new Dimension(100, 30));
		
		
		//IP and port input fields
		JTextField ipAdd = new JTextField("127.0.0.1");
		ipAdd.setPreferredSize(new Dimension(100, 30));
		JTextField portNum = new JTextField("3001");
		portNum.setPreferredSize(new Dimension(50, 30));
		
		//connect button
		JButton connect = new JButton();
		connect.setPreferredSize(new Dimension (100, 30));
		connect.setText("Connect");
		
		
		connect.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	int _port = -1;
		    	try {
		    		_port = Integer.parseInt(portNum.getText());
		    	}
		    	catch(Exception num) {
		    		System.out.println("Port not a number");
		    	}
		    	if(_port > -1) {
			    	client = SocketClient.connect(ipAdd.getText(), _port);
			    
			    	
			    	//trigger any one-time data after client connects
			    	
			    	//register our history/message listener
			    	client.registerMessageListener(frame);
			    	client.postConnectionData();
			    	connect.setEnabled(false);
		    	}
		    }
		});
		
		
		
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
					//chatTextArea.append("\n" + username + ": " + messageField.getText());
					//reset textfield
					client.sendMessage(messageField.getText());
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
	}
	@Override
	public void onReceivedSwitch(boolean isOn) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onReceivedMessage(String msg) {
		// TODO Auto-generated method stub
		System.out.println(msg);
		chatLog.append(msg);
		chatLog.append(System.lineSeparator());
	}

}