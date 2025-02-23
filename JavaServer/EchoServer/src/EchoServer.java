import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EchoServer extends AbstractServer {
    // Class variables *************************************************

    final public static int DEFAULT_PORT = 5555;
    private static final String UPLOADS_DIR = "uploads/";

    // Constructors ****************************************************

    public EchoServer(int port) {
        super(port);
        new File(UPLOADS_DIR).mkdirs(); // Ensure uploads directory exists
        try {
            this.listen(); // Start listening for connections
        } catch (Exception ex) {
            System.out.println("ERROR - Could not listen for clients!");
        }
    }

    // Instance methods ************************************************

    public void handleMessageFromClient(Object msg, ConnectionToClient client) {
        if (msg instanceof Envelope) {
            try {
                handleClientCommand((Envelope) msg, client);
            } catch (IOException ex) {
                Logger.getLogger(EchoServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            String userId = (String) client.getInfo("userId");
            if (userId != null) {
                System.out.println("Message received: " + msg + " from " + client);
                String messageWithId = userId + ": " + msg;
            } else {
                System.out.println("Error: userId is null for client " + client);
            }
        }
    }

    private void handleClientCommand(Envelope envelope, ConnectionToClient client) throws IOException {
        String command = envelope.getCommand();
        switch (command) {
            case "#ftpUpload":
                receiveFileFromClient(envelope, client);
                break;
            case "#ftpget":
                sendFileToClient(client, envelope.getArgument());
                break;
            case "#ftplist":
                sendFileList(client);
                break;
            default:
                System.out.println("Unknown command: " + command);
        }
    }

    private void receiveFileFromClient(Envelope envelope, ConnectionToClient client) throws IOException {
        try {
            File file = new File(UPLOADS_DIR + envelope.getFileName());
            Files.write(file.toPath(), envelope.getFileData()); // Save the file
            System.out.println("File uploaded: " + envelope.getFileName());
            client.sendToClient("File uploaded successfully: " + envelope.getFileName());
        } catch (Exception ex) {
            System.out.println("Error receiving file from client.");
            client.sendToClient("Error receiving file.");
        }
    }

    private void sendFileToClient(ConnectionToClient client, String fileName) throws IOException {
        try {
            File file = new File(UPLOADS_DIR + fileName);
            if (!file.exists()) {
                client.sendToClient("Error: File not found.");
                return;
            }
            byte[] fileData = Files.readAllBytes(file.toPath());
            client.sendToClient(new Envelope("#ftpget", fileName, fileData));
            System.out.println("File sent: " + fileName);
        } catch (Exception ex) {
            System.out.println("Error sending file to client.");
            client.sendToClient("Error sending file.");
        }
    }

    private void sendFileList(ConnectionToClient client) {
        File folder = new File(UPLOADS_DIR);
        File[] files = folder.listFiles();
        List<String> fileList = new ArrayList<>();
        if (files != null) {
            for (File file : files) fileList.add(file.getName());
        }
        try {
            client.sendToClient(fileList); // Send file list
        } catch (IOException ex) {
            System.out.println("Error sending file list to client.");
        }
    }

    protected void serverStarted() {
        System.out.println("Server listening for connections on port " + getPort());
    }

    protected void serverStopped() {
        System.out.println("Server has stopped listening for connections.");
    }

    public static void main(String[] args) {
        int port = 0;

        try {
            port = Integer.parseInt(args[0]);
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            port = DEFAULT_PORT;
        }

        EchoServer sv = new EchoServer(port);

        try {
            sv.listen();
        } catch (Exception ex) {
            System.out.println("ERROR - Could not listen for clients!");
        }
    }

    protected void clientConnected(ConnectionToClient client) {
        System.out.println("<Client Connected:" + client + ">");
    }

    protected void clientDisconnected(ConnectionToClient client) {
        System.out.println("<Client disconnected>");
    }
}
