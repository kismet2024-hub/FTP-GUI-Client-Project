import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.logging.Level;
import java.util.logging.Logger;



public class GUIConsole extends JFrame implements ChatIF {

    // Class variables
    final public static int DEFAULT_PORT = 5555;

    // Instance variables
    ChatClient client;
    private JButton browseB = new JButton("Browse");
    private JButton saveB = new JButton("Save");
    private JButton downloadB = new JButton("Download");
    private JButton logoffB = new JButton("Logoff");
    private JButton loginB = new JButton("Login");
    private JButton sendB = new JButton("Send");
    private JButton quitB = new JButton("Quit");
    private JLabel userIdLB = new JLabel("User Id:", JLabel.RIGHT);
    private JTextField userIdTxF = new JTextField("");
    private JTextField portTxF = new JTextField("5555");
    private JTextField hostTxF = new JTextField("127.0.0.1");
    private JTextField messageTxF = new JTextField("");
    private JComboBox<String> fileListCombo = new JComboBox<>();
    private JTextArea messageList = new JTextArea();
    private File selectedFile;
    private JLabel portLB = new JLabel("Port: ", JLabel.RIGHT);
    private JLabel hostLB = new JLabel("Host: ", JLabel.RIGHT);
    private JLabel messageLB = new JLabel("Message: ", JLabel.RIGHT);
    
    private final String FILE_DIRECTORY = "C:\\Users\\COMPUTER\\Downloads\\outstanding"; // Update this path

    // Main method
    public static void main(String[] args) {
        new GUIConsole("localhost", 5555);
    }

    // Constructor to set up the GUI and connection
    public GUIConsole(String host, int port) {
        super("Simple Chat GUI");
        setSize(500, 600);
        setLayout(new BorderLayout(5, 5));
        JPanel bottom = new JPanel();
        add("Center", new JScrollPane(messageList));
        add("South", bottom);

        bottom.setLayout(new GridLayout(9, 2, 7, 10));
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

        // Action listener for login
        loginB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String userId = userIdTxF.getText();
                String host = hostTxF.getText();
                int port = Integer.parseInt(portTxF.getText());
                try {
                    client = new ChatClient(host, port, GUIConsole.this);
                    send("#login " + userId);
                    messageList.append("Logged in as " + userId + "\n");
                } catch (IOException ex) {
                    messageList.append("Error: Unable to connect to server.\n");
                }
            }
        });

        // Action listener for logoff
        logoffB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                send("#logoff");
                messageList.append("Logged off.\n");
                if (client != null) {
                    try {
                        client.closeConnection();
                    } catch (IOException ex) {
                        Logger.getLogger(GUIConsole.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });

        // Action listener for sending message
        sendB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String message = messageTxF.getText();
                messageTxF.setText(""); // clear the input field
                if (!message.trim().isEmpty()) {
                    send(message);
                    messageList.append("You: " + message + "\n");
                }
            }
        });

        // Action listener for quit
        quitB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                send("#quit");
                if (client != null) {
                    try {
                        client.closeConnection();
                    } catch (IOException ex) {
                        Logger.getLogger(GUIConsole.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                System.exit(0); // close the application
            }
        });

        // Action listener for browsing file
        browseB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                chooseFile();
            }
        });

        // Action listener for saving file
        saveB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                uploadFile();
            }
        });

        // Action listener for downloading file
        downloadB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String fileName = (String) fileListCombo.getSelectedItem();
                if (fileName != null && !fileName.isEmpty()) {
                    Envelope envelope = new Envelope("#ftpget", fileName, null);
                    send(envelope.toString());
                    messageList.append("Downloading file: " + fileName + "\n");
                } else {
                    messageList.append("No file selected for download.\n");
                }
            }
        });

        setVisible(true);
    }

    // Sends the message to the server
    public void send(String message) {
        if (client != null) {
            client.handleMessageFromClientUI(message);
        }
    }

    // This method takes a string and displays it on the GUI interface.
    @Override
    public void display(String message) {
        messageList.append(message + "\n");
    }

    // Method to choose a file for upload
    private void chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            messageList.append("Selected file: " + selectedFile.getName() + "\n");
        }
    }

    // Method to upload the selected file
    private void uploadFile() {
        if (selectedFile != null) {
            try {
                // Read the selected file into a byte array
                byte[] fileData = Files.readAllBytes(selectedFile.toPath());

                // Send the file data to the server for upload
                Envelope envelope = new Envelope("#ftpUpload", selectedFile.getName(), fileData);
                send(envelope.toString());

                // Append message to indicate the file is uploaded
                messageList.append("File uploaded: " + selectedFile.getName() + "\n");

                // Update the file list in the combo box after the upload
                updateFileList();
            } catch (IOException ex) {
                messageList.append("Error uploading file.\n");
            }
        }
    }

    // Method to handle file data received from the server and save it locally
    public void saveFile(byte[] fileData, String fileName) {
        try (FileOutputStream fos = new FileOutputStream(FILE_DIRECTORY + File.separator + fileName)) {
            fos.write(fileData);
            messageList.append("File saved: " + fileName + "\n");
        } catch (IOException ex) {
            messageList.append("Error saving file.\n");
        }
    }

    // Method to update the file list in the combo box
    private void updateFileList() {
        // Clear the existing items in the combo box
        fileListCombo.removeAllItems();

        // Check if the directory exists and is indeed a directory
        File dir = new File(FILE_DIRECTORY);

        if (!dir.exists() || !dir.isDirectory()) {
            messageList.append("Directory not found or invalid.\n");
            return;  // Exit the method if the directory doesn't exist
        }

        // Get the list of files in the directory
        File[] files = dir.listFiles();

        // If no files are found, show a message
        if (files == null || files.length == 0) {
            messageList.append("No files found in the directory.\n");
        } else {
            // If files are found, add them to the combo box
            for (File file : files) {
                if (file.isFile()) { // Add only files (not directories)
                    fileListCombo.addItem(file.getName());
                }
            }
        }
    }
}
