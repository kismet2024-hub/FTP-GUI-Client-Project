
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EchoServer extends AbstractServer {
    //Class variables *************************************************

    /**
     * The default port to listen on.
     */
    final public static int DEFAULT_PORT = 5555;
    private static final String UPLOADS_DIR = "uploads/";

    //Constructors ****************************************************
    /**
     * Constructs an instance of the echo server.
     *
     * @param port The port number to connect on.
     */
    public EchoServer(int port) {

        super(port);
        //Ensure uploads directory exist
        new File(UPLOADS_DIR).mkdirs();

        try {
            this.listen(); //Start listening for connections
        } catch (Exception ex) {
            System.out.println("ERROR - Could not listen for clients!");
        }

    }

    //Instance methods ************************************************
    /**
     * This method handles any messages received from the client.
     *
     * @param msg The message received from the client.
     * @param client The connection from which the message originated.
     */
    public void handleMessageFromClient(Object msg, ConnectionToClient client) {
        if (msg instanceof Envelope2) {
            try {
                handleClientCommand((Envelope2) msg, client);
            } catch (IOException ex) {
                Logger.getLogger(EchoServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            String userId = (String) client.getInfo("userId");

            if (userId != null) {
                System.out.println("Message received: " + msg + " from " + client);
                String messageWithId = userId + ": " + msg;
         //       sendToAllClientsInRoom(messageWithId, client.getInfo("room").toString());
            } else {
                System.out.println("Error: userId is null for client " + client);
            }
        }
        
    }
    //Function to handle message from client *Receive and Send File to client
    private void handleClientCommand(Envelope2 envelope, ConnectionToClient client) throws IOException {
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
    //Function to receive files from client
    private void receiveFileFromClient(Envelope2 envelope, ConnectionToClient client) throws IOException {
        try {
            File file = new File(UPLOADS_DIR + envelope.getFileName());
            Files.write(file.toPath(), envelope.getFileData());
            System.out.println("File uploaded: " + envelope.getFileName());
            client.sendToClient("File uploaded successfully: " + envelope.getFileName());
        } catch (Exception ex) {
            System.out.println("Error receiving file from client.");
            client.sendToClient("Error receiving file.");
        }
    }
    
    
    //Function to send file to client
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
    
    //Function to send a filelist
    private void sendFileList(ConnectionToClient client) {
        File folder = new File(UPLOADS_DIR);
        File[] files = folder.listFiles();
        List<String> fileList = new ArrayList<>();
        if (files != null) {
            for (File file : files) fileList.add(file.getName());
        }
        try {
            client.sendToClient(fileList);
        } catch (IOException ex) {
            System.out.println("Error sending file list to client.");
        }
    }

    
    

//    public void handleClientCommand(Envelope env, ConnectionToClient client) {
//        
//        
//        if (env.getName().equals("login")) {
//            client.setInfo("userId", env.getMsg());
//            client.setInfo("room", "commons");
//
//            System.out.println(client.getInfo("userId") + " has joined and has been placed in room " + client.getInfo("room"));
//        }
//
//        if (env.getName().equals("join")) {
//            client.setInfo("room", env.getMsg());
//            System.out.println(client.getInfo("userId") + " has moved to room " + client.getInfo("room"));
//        }
//
//        if (env.getName().equals("pm")) {
//            String target = env.getArg();
//            sendToClientByUserId(env.getMsg(), target);
//        }
//
//        if (env.getName().equals("who")) {
//            Envelope returnEnv = new Envelope("who", null, null);
//            ArrayList clientsInRoom = getAllClientsInRoom(client.getInfo("room").toString());
//            returnEnv.setMsg(clientsInRoom);
//
//            try {
//                client.sendToClient(returnEnv);
//            } catch (Exception ex) {
//                System.out.println("Error sending 'who' response.");
//            }
//        }
//
//// Modify the userstatus command
//        if (env.getName().equals("userstatus")) {
//            StringBuilder statusList = new StringBuilder();
//            ArrayList<String> clientsInRoom = getAllClientsInRoom(client.getInfo("room").toString());
//
//            for (String user : clientsInRoom) {
//                ConnectionToClient targetClient = findClientByUserId(user);
//                if (targetClient != null) {
//                    statusList.append(user)
//                            .append(" - ")
//                            .append(targetClient.getInfo("room"))
//                            .append("\n");
//                }
//            }
//
//            String response = statusList.toString();
//            try {
//                client.sendToClient(response);
//            } catch (Exception ex) {
//                System.out.println("Error sending user status to client");
//            }
//        }
//
//        // Function for Join Room
//        if (env.getName().equals("joinroom")) {
//            // room1 from the command
//            String room1 = (String) env.getMsg();
//            // room2 from the command
//            String room2 = (String) env.getArg();
//
//            // Retrieve the userId of the client that issued the command (this is the client sending the command)
//            String userId = (String) client.getInfo("userId");
//
//            // Log the action
//            System.out.println(userId + " requested to move users from " + room1 + " to " + room2);
//
//            // If the client is already in room1, move them to room2
//            if (client.getInfo("room").equals(room1)) {
//                // Move the client to room2
//                client.setInfo("room", room2);
//                sendToClientByUserId(userId, "You have been moved from room " + room1 + " to room " + room2);
//            }
//
//            // Send confirmation message back to the client (confirming action)
//            String message = "You moved users from room " + room1 + " to room " + room2;
//            try {
//                Envelope response = new Envelope("joinroom", null, message);
//                // Ensure the client can receive this
//                client.sendToClient(response);
//            } catch (Exception ex) {
//                System.out.println("Error sending confirmation to client.");
//            }
//        }
//    }
//    // Find client by userId function
//
//    public ConnectionToClient findClientByUserId(String userId) {
//        // Ensure that getClientConnections() returns an array of ConnectionToClient objects
//        Thread[] clientThreadList = getClientConnections(); // Assuming this returns Thread or ConnectionToClient objects
//
//        for (int i = 0; i < clientThreadList.length; i++) {
//            // Cast thread to ConnectionToClient
//            ConnectionToClient currentClient = (ConnectionToClient) clientThreadList[i];
//
//            // Check if the userId is set in the currentClient info
//            Object userIdFromClient = currentClient.getInfo("userId");
//
//            // Ensure userIdFromClient is not null and then compare it with the userId
//            if (userIdFromClient != null && userIdFromClient instanceof String) {
//                if (userId.equals(userIdFromClient)) {
//                    return currentClient; // Return the client if userId matches
//                }
//            }
//        }
//
//        // Return null if no client found with the given userId
//        return null;
//    }
//
//    public void sendToAllClientsInRoom(Object msg, String room) {
//        Thread[] clientThreadList = getClientConnections();
//
//        for (int i = 0; i < clientThreadList.length; i++) {
//            ConnectionToClient currentClient = (ConnectionToClient) clientThreadList[i];
//            if (room.equals(currentClient.getInfo("room"))) {
//                try {
//                    currentClient.sendToClient(msg);
//                } catch (Exception ex) {
//                    System.out.println("Error sending message to client");
//                }
//            }
//        }
//    }

//    public void sendToClientByUserId(Object msg, String userId) {
//        Thread[] clientThreadList = getClientConnections();
//
//        for (int i = 0; i < clientThreadList.length; i++) {
//            ConnectionToClient currentClient = (ConnectionToClient) clientThreadList[i];
//            if (userId.equals(currentClient.getInfo("userId"))) {
//                try {
//                    currentClient.sendToClient(msg);
//                } catch (Exception ex) {
//                    System.out.println("Error sending message to client");
//                }
//            }
//        }
//    }

//    public ArrayList<String> getAllClientsInRoom(String room) {
//        Thread[] clientThreadList = getClientConnections();
//        ArrayList<String> results = new ArrayList<String>();
//        for (int i = 0; i < clientThreadList.length; i++) {
//            ConnectionToClient currentClient = (ConnectionToClient) clientThreadList[i];
//            if (room.equals(currentClient.getInfo("room"))) {
//                results.add(currentClient.getInfo("userId").toString());
//            }
//        }
//        return results;
//    }

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
