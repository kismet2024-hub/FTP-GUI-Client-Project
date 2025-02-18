import java.io.*;
import java.util.ArrayList;

/**
 * This class overrides some of the methods defined in the abstract superclass
 * in order to give more functionality to the client.
 */
public class ChatClient extends AbstractClient {
    //Instance variables **********************************************

    /**
     * The interface type variable. It allows the implementation of the display
     * method in the client.
     */
    ChatIF clientUI;

    //Constructors ****************************************************
    /**
     * Constructs an instance of the chat client.
     *
     * @param host The server to connect to.
     * @param port The port number to connect on.
     * @param clientUI The interface type variable.
     */
    public ChatClient(String host, int port, ChatIF clientUI)
            throws IOException {
        super(host, port); //Call the superclass constructor
        this.clientUI = clientUI;
        //openConnection();
    }

    //Instance methods ************************************************
    /**
     * This method handles all data that comes in from the server.
     *
     * @param msg The message from the server.
     */
    public void handleMessageFromServer(Object msg) {
        if (msg instanceof Envelope) {
            handleServerCommand((Envelope) msg);
        } else {
            clientUI.display(msg.toString());
        }
    }

    public void handleServerCommand(Envelope env) {
        if (env.getName().equals("who")) {
            ArrayList<String> clientsInRoom = (ArrayList<String>) env.getMsg();

            clientUI.display("---Users in room---");
            for (int i = 0; i < clientsInRoom.size(); i++) {
                clientUI.display(clientsInRoom.get(i));
            }

            // Handle server command for userstatus
            if (env.getName().equals("userstatus")) {
                ArrayList<String> userStatus = (ArrayList<String>) env.getMsg(); // Get the user status list

                clientUI.display("---User Status---");
                for (int i = 0; i < userStatus.size(); i++) {
                    clientUI.display(userStatus.get(i)); // Display each user and their room
                }
            }
            //Handle server command for ison
            if (env.getName().equals("ison")) {
                String response = (String) env.getMsg(); // Get server response
                clientUI.display(response);
            }

        }
    }

    /**
     * This method handles all data coming from the UI
     *
     * @param message The message from the UI.
     */
    public void handleMessageFromClientUI(String message) {

        //if the first character in our message is a # treat it as a command
        if (message.charAt(0) == '#') {

            handleClientCommand(message);

        } else {
            try {
                sendToServer(message);
            } catch (IOException e) {
                clientUI.display("Could not send message to server.  Terminating client.......");
                quit();
            }
        }
    }

    /**
     * This method terminates the client.
     */
    public void quit() {
        try {
            closeConnection();
        } catch (IOException e) {
        }
        System.exit(0);
    }

    public void connectionClosed() {

        System.out.println("Connection closed");

    }

    protected void connectionException(Exception exception) {

        System.out.println("The server has shut down");

    }

    //compare message to premade command strings
    //if the message matches perform that command
    public void handleClientCommand(String message) {

        //disconnects from server and shuts down client
        if (message.equals("#quit")) {
            clientUI.display("Shutting Down Client");
            quit();

        }

        //disconnect from server
        if (message.equals("#logoff")) {
            clientUI.display("Disconnecting from server");
            try {
                closeConnection();
            } catch (IOException e) {
            };

        }

        //changes the host that the socket attempts to connect to
        //format: #setHost hostName
        if (message.indexOf("#setHost") >= 0) {

            if (isConnected()) {
                clientUI.display("Cannot change host while connected");
            } else {
                //#setHost localhost
                //localhost
                String newHost = message.substring(9, message.length()).trim();
                setHost(newHost);
            }

        }

        //changes the port that the socket attempts to connect to
        //format: #setPort 5555
        if (message.indexOf("#setPort") >= 0) {

            if (isConnected()) {
                clientUI.display("Cannot change port while connected");
            } else {
                //#setPort 5555
                // 5555
                int newPort = Integer.parseInt(message.substring(9, message.length()));
                setPort(newPort);
            }

        }

        //if we are not already logged in connect us to a server
        if (message.indexOf("#login") >= 0) {

            if (isConnected()) {
                clientUI.display("already connected");
            } else {

                try {

                    openConnection();
                } catch (IOException e) {
                    clientUI.display("failed to connect to server.");
                }

                String userId = message.substring(7, message.length()).trim();
                Envelope env = new Envelope("login", null, userId);

                try {
                    sendToServer(env);
                } catch (IOException e) {
                    clientUI.display("Could not send message to server.  Terminating client.......");
                    quit();
                }
            }
        }

        if (message.indexOf("#join") >= 0) {

            if (isConnected()) {
                String room = message.substring(6, message.length()).trim();
                Envelope env = new Envelope("join", null, room);

                try {
                    sendToServer(env);
                } catch (IOException e) {
                    clientUI.display("Could not send message to server.  Terminating client.......");
                    quit();
                }
            } else {
                clientUI.display("You must be connected to a server to perform this command");
            }
        }
        if (message.indexOf("#pm") >= 0) {

            if (isConnected()) {
                String userAndMessage = message.substring(4, message.length()).trim();
                String user = userAndMessage.substring(0, userAndMessage.indexOf(" ")).trim();
                String msg = userAndMessage.substring(userAndMessage.indexOf(" "), userAndMessage.length()).trim();
                Envelope env = new Envelope("pm", user, msg);

                try {
                    sendToServer(env);
                } catch (IOException e) {
                    clientUI.display("Could not send message to server.  Terminating client.......");
                    quit();
                }
            } else {
                clientUI.display("You must be connected to a server to perform this command");
            }
        }

        if (message.indexOf("#who") >= 0) {

            if (isConnected()) {

                Envelope env = new Envelope("who", null, null);

                try {
                    sendToServer(env);
                } catch (IOException e) {
                    clientUI.display("Could not send message to server.  Terminating client.......");
                    quit();
                }
            } else {
                clientUI.display("You must be connected to a server to perform this command");
            }
        }
        //Join Room Function implementation
    if (message.indexOf("#joinroom") >= 0) {

    if (isConnected()) {
        // Extract the room1 and room2 values from the message and split the #joinroom function
        String[] parts = message.substring(10).trim().split(" "); 
        
        if (parts.length == 2) {
            // First room to move users from
            String room1 = parts[0];  
            // Second room to move users to
            String room2 = parts[1];  
            
            // Create the Envelope to send the "joinroom" command
            Envelope env = new Envelope("joinroom", room1, room2);
            
            try {
                sendToServer(env);  // Send the joinroom request to the server
            } catch (IOException e) {
                clientUI.display("Could not send message to server.  Terminating client.......");
                quit();
            }
        } else {
            clientUI.display("Invalid command format. Please use: #joinroom <room1> <room2>");
        }
    } else {
        clientUI.display("You must be connected to a server to perform this command.");
    }
}


        // Handles #userstatus command
        if (message.indexOf("#userstatus") >= 0) {
            if (isConnected()) {
                // Request the user status from the server
                Envelope env = new Envelope("userstatus", null, null);

                try {
                    sendToServer(env); // Send the request to the server
                } catch (IOException e) {
                    clientUI.display("Could not send message to server. Terminating client...");
                    quit();
                }
            } else {
                clientUI.display("You must be connected to a server to perform this command");
            }
        }

    }
}
//End of ChatClient class
