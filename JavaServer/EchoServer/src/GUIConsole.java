/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 *
 * @author COMPUTER
 */
//Class can also be reffered to as Window
public class GUIConsole extends JFrame implements ChatIF {

    //Class variables *************************************************
    /**
     * The default port to connect on.
     */
    final public static int DEFAULT_PORT = 5555;

    //Instance variables **********************************************
    /**
     * The instance of the client that created this ConsoleChat.
     */
    ChatClient client;

    //GUI variables 
    private JButton browseB = new JButton("Browse");
    private JButton saveB = new JButton("Save");
    private JButton downloadB = new JButton("Download");
    private JButton logoffB = new JButton("Logoff");
    private JButton loginB = new JButton("login");
    private JButton sendB = new JButton("Send");
    private JButton quitB = new JButton("Quit");
    private JLabel userIdLB = new JLabel("User Id:", JLabel.RIGHT);
    //Make JtexFields
    private JTextField userIdTxF = new JTextField("");
    private JTextField portTxF = new JTextField("5555");
    private JTextField hostTxF = new JTextField("127.0.0.1");
    private JTextField messageTxF = new JTextField("");
    private JComboBox<String> fileListCombo = new JComboBox<>();
    private JTextArea messageList = new JTextArea();
    private File selectedFile; 
    //Make labels for port, message and host
    private JLabel portLB = new JLabel("Port: ", JLabel.RIGHT);
    private JLabel hostLB = new JLabel("Host: ", JLabel.RIGHT);
    private JLabel messageLB = new JLabel("Message: ", JLabel.RIGHT);
  

    //main method
    public static void main(String[] args) {
        GUIConsole test = new GUIConsole("localhost", 5555);
    }

    //set the host and the port
    //from command line?
    //create instance of class with host and port 
    //call method to listen for inputs?
    //constructor with host and port 
    public GUIConsole(String host, int port) {
        //set the name of the window
        super("Simple Chat GUI");
        //set the size
        setSize(300, 400);

        setLayout(new BorderLayout(5, 5));
        JPanel bottom = new JPanel();
        add("Center", messageList);
        add("South", bottom);

        //make the bottom part of the window a grid with 6 rows, 2 columns adn 5 pixels of vertical and horizontal space
        bottom.setLayout(new GridLayout(9, 4, 7, 10));
        bottom.add(userIdLB);
        bottom.add(userIdTxF);
        bottom.add(hostLB);
        bottom.add(hostTxF);
        bottom.add(portLB);
        bottom.add(portTxF);
        bottom.add(messageLB);
        bottom.add(messageTxF);
        bottom.add(loginB);
        bottom.add(logoffB);
        bottom.add(sendB);
        bottom.add(quitB);
        bottom.add(browseB);
        bottom.add(saveB);
        bottom.add(fileListCombo);
        bottom.add(downloadB);
        bottom.add(messageTxF);
        bottom.add(new JButton("Send"));

        loginB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                //update the host
                send("#setHost " + hostTxF.getText());
                //update the port 
                send("#setPort " + portTxF.getText());
                //get the userId
                String userId = userIdTxF.getText();
                //send the login Command
                send("#login" + userId);
//                System.out.println("Send button pressed");
//                
//                //Making something go into the messagelist console
//                messageList.append("Send button pressed\n");

                // Code for handling chat client connection
                // For example: 
                // ChatClient chatClient = new ChatClient(host, port);
                // chatClient.sendMessage(message);
            }
        });

        logoffB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //get the userId

                //send the login Command
                send("logoff");
//                System.out.println("Send button pressed");
//                
//                //Making something go into the messagelist console
//                messageList.append("Send button pressed\n");

                // Code for handling chat client connection
                // For example: 
                // ChatClient chatClient = new ChatClient(host, port);
                // chatClient.sendMessage(message);
            }
        });

        sendB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //
                String message = messageTxF.getText();
                //Calling the function display
                messageTxF.setText("");
                send(message);
//                System.out.println("Send button pressed");
//                
//                //Making something go into the messagelist console
//                messageList.append("Send button pressed\n");

                // Code for handling chat client connection
                // For example: 
                // ChatClient chatClient = new ChatClient(host, port);
                // chatClient.sendMessage(message);
            }
        });

        quitB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //

                send("quit");
//                System.out.println("Send button pressed");
//                
//                //Making something go into the messagelist console
//                messageList.append("Send button pressed\n");

                // Code for handling chat client connection
                // For example: 
                // ChatClient chatClient = new ChatClient(host, port);
                // chatClient.sendMessage(message);
            }
        });

        try {
            client = new ChatClient(host, port, this);
        } catch (IOException exception) {
            System.out.println("Error: Can't setup connection!!!!"
                    + " Terminating client.");
            System.exit(1);
        }
        
        
        add("Center", new JScrollPane(messageList));
        add("South", bottom);

        browseB.addActionListener(e -> chooseFile());
        saveB.addActionListener(e -> uploadFile());
        downloadB.addActionListener(e -> downloadFile());

        try {
            client = new ChatClient(host, port, this);
            requestFileList();
        } catch (IOException ex) {
            System.out.println("Error: Can't setup connection! Terminating.");
            System.exit(1);
        }

        //Do all constructor codes before showing the window
        // Setting visibility of the frame
        setVisible(true);

    }

    public void send(String message) {
        client.handleMessageFromClientUI(message);
    }

    /**
     * This method takes a string and displays it on the GUI interface.
     * CURRENTLY prints the message to the console
     *
     * @param message the message to be displayed
     */
    private void chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            messageList.append("Selected file: " + selectedFile.getName() + "\n");
        }
    }

    private void uploadFile() {
        if (selectedFile != null) {
            try {
                byte[] fileData = Files.readAllBytes(selectedFile.toPath());
                Envelope envelope = new Envelope("#ftpUpload", selectedFile.getName(), fileData);
                client.handleMessageFromClientUI(envelope);
                requestFileList();
            } catch (IOException ex) {
                messageList.append("Error uploading file.\n");
            }
        }
    }

    private void requestFileList() {
        client.handleMessageFromClientUI(new Envelope("#ftplist", null, null));
    }

    private void downloadFile() {
        String fileName = (String) fileListCombo.getSelectedItem();
        client.handleMessageFromClientUI(new Envelope("#ftpget", fileName, null));
    }
    
    
    public void display(String message) {

        messageList.append(message + "\n");
        // Display message in the GUI component (e.g., JTextArea, JLabel, etc.)
        // Example: 
        // chatArea.append(message + "\n");

        System.out.println(message);  // For now, prints the message to the console
    }
}
