
/*Answer to Part A

//         50
//       /    \
//     25      75
//    /  \     /  \
//  13   33  60   88
// /  \        /  \
//7   19     80   101
//
//
//

//
The tree can be described as inefficient because some branches are deeper 
 (e.g 50 - 75 - 88 - 101) making search more difficult and time consuming to reach a node.
 It also makes insertion and deletion much slower than usual.*/
 /*Part B
Advantages of generic data over non-generics
1. A generic data is reusable and flexible in multiple contexts.
   This helps in saving time and avoiding code duplication
2.A generic data structure can also be used for multiple data types, which gives the flexibilty to build more general purpose codes without , 
Knowing you can pass in different types of objects.
3. We can also say generics reduces the likelihood of 
errors in your code because theres is less repitition of codes as compared to non generics.
Generics are also safe to use as we get compile error and not runtime error in non generics
 */


import java.util.ArrayList;

public class EchoServer extends AbstractServer {
    //Class variables *************************************************

    /**
     * The default port to listen on.
     */
    final public static int DEFAULT_PORT = 5555;

    //Constructors ****************************************************
    /**
     * Constructs an instance of the echo server.
     *
     * @param port The port number to connect on.
     */
    public EchoServer(int port) {

        super(port);

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
        if (msg instanceof Envelope) {
            handleClientCommand((Envelope) msg, client);
        } else {
            String userId = (String) client.getInfo("userId");

            if (userId != null) {
                System.out.println("Message received: " + msg + " from " + client);
                String messageWithId = userId + ": " + msg;
                sendToAllClientsInRoom(messageWithId, client.getInfo("room").toString());
            } else {
                System.out.println("Error: userId is null for client " + client);
            }
        }
    }

    public void handleClientCommand(Envelope env, ConnectionToClient client) {
        if (env.getName().equals("login")) {
            client.setInfo("userId", env.getMsg());
            client.setInfo("room", "commons");

            System.out.println(client.getInfo("userId") + " has joined and has been placed in room " + client.getInfo("room"));
        }

        if (env.getName().equals("join")) {
            client.setInfo("room", env.getMsg());
            System.out.println(client.getInfo("userId") + " has moved to room " + client.getInfo("room"));
        }

        if (env.getName().equals("pm")) {
            String target = env.getArg();
            sendToClientByUserId(env.getMsg(), target);
        }

        if (env.getName().equals("who")) {
            Envelope returnEnv = new Envelope("who", null, null);
            ArrayList clientsInRoom = getAllClientsInRoom(client.getInfo("room").toString());
            returnEnv.setMsg(clientsInRoom);

            try {
                client.sendToClient(returnEnv);
            } catch (Exception ex) {
                System.out.println("Error sending 'who' response.");
            }
        }

// Modify the userstatus command
        if (env.getName().equals("userstatus")) {
            StringBuilder statusList = new StringBuilder();
            ArrayList<String> clientsInRoom = getAllClientsInRoom(client.getInfo("room").toString());

            for (String user : clientsInRoom) {
                ConnectionToClient targetClient = findClientByUserId(user);
                if (targetClient != null) {
                    statusList.append(user)
                            .append(" - ")
                            .append(targetClient.getInfo("room"))
                            .append("\n");
                }
            }

            String response = statusList.toString();
            try {
                client.sendToClient(response);
            } catch (Exception ex) {
                System.out.println("Error sending user status to client");
            }
        }

        // Function for Join Room
        if (env.getName().equals("joinroom")) {
            // room1 from the command
            String room1 = (String) env.getMsg();
            // room2 from the command
            String room2 = (String) env.getArg();

            // Retrieve the userId of the client that issued the command (this is the client sending the command)
            String userId = (String) client.getInfo("userId");

            // Log the action
            System.out.println(userId + " requested to move users from " + room1 + " to " + room2);

            // If the client is already in room1, move them to room2
            if (client.getInfo("room").equals(room1)) {
                // Move the client to room2
                client.setInfo("room", room2);
                sendToClientByUserId(userId, "You have been moved from room " + room1 + " to room " + room2);
            }

            // Send confirmation message back to the client (confirming action)
            String message = "You moved users from room " + room1 + " to room " + room2;
            try {
                Envelope response = new Envelope("joinroom", null, message);
                // Ensure the client can receive this
                client.sendToClient(response);
            } catch (Exception ex) {
                System.out.println("Error sending confirmation to client.");
            }
        }
    }
    // Find client by userId function

    public ConnectionToClient findClientByUserId(String userId) {
        // Ensure that getClientConnections() returns an array of ConnectionToClient objects
        Thread[] clientThreadList = getClientConnections(); // Assuming this returns Thread or ConnectionToClient objects

        for (int i = 0; i < clientThreadList.length; i++) {
            // Cast thread to ConnectionToClient
            ConnectionToClient currentClient = (ConnectionToClient) clientThreadList[i];

            // Check if the userId is set in the currentClient info
            Object userIdFromClient = currentClient.getInfo("userId");

            // Ensure userIdFromClient is not null and then compare it with the userId
            if (userIdFromClient != null && userIdFromClient instanceof String) {
                if (userId.equals(userIdFromClient)) {
                    return currentClient; // Return the client if userId matches
                }
            }
        }

        // Return null if no client found with the given userId
        return null;
    }

    public void sendToAllClientsInRoom(Object msg, String room) {
        Thread[] clientThreadList = getClientConnections();

        for (int i = 0; i < clientThreadList.length; i++) {
            ConnectionToClient currentClient = (ConnectionToClient) clientThreadList[i];
            if (room.equals(currentClient.getInfo("room"))) {
                try {
                    currentClient.sendToClient(msg);
                } catch (Exception ex) {
                    System.out.println("Error sending message to client");
                }
            }
        }
    }

    public void sendToClientByUserId(Object msg, String userId) {
        Thread[] clientThreadList = getClientConnections();

        for (int i = 0; i < clientThreadList.length; i++) {
            ConnectionToClient currentClient = (ConnectionToClient) clientThreadList[i];
            if (userId.equals(currentClient.getInfo("userId"))) {
                try {
                    currentClient.sendToClient(msg);
                } catch (Exception ex) {
                    System.out.println("Error sending message to client");
                }
            }
        }
    }

    public ArrayList<String> getAllClientsInRoom(String room) {
        Thread[] clientThreadList = getClientConnections();
        ArrayList<String> results = new ArrayList<String>();
        for (int i = 0; i < clientThreadList.length; i++) {
            ConnectionToClient currentClient = (ConnectionToClient) clientThreadList[i];
            if (room.equals(currentClient.getInfo("room"))) {
                results.add(currentClient.getInfo("userId").toString());
            }
        }
        return results;
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
