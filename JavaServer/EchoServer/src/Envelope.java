import java.io.Serializable;

/**
 * Envelope class for wrapping messages, file data, and additional metadata
 * for communication between server and client.
 * This class can hold commands, file data, text messages, and other arguments.
 */
public class Envelope implements Serializable {
    // Command for the operation (message type or file transfer)
    private String command;   
    // Additional argument for the message (optional)
    private String argument;
    // Stores the file name for file transfers
    private String fileName;    
    // Stores the file content in bytes
    private byte[] fileData;    
    // Stores the message content (text message, object, etc.)
    private Object msg; 
    // Name for identification or sender
    private String name;       
    
    
    
    // Default constructor
    public Envelope() {}

    // Constructor for regular messages (with command, argument, and message)
    public Envelope(String command, String argument, Object msg) {
        this.command = command;
        this.argument = argument;
     // Store the message content
        this.msg = msg;          
    }

    // Constructor for file transfers (with command, fileName, and file data)
    public Envelope(String command, String fileName, byte[] fileData) {
        this.command = command;
        this.fileName = fileName;
        this.fileData = fileData;
    }

    // Getters and Setters
    public String getCommand() { return command; }
    public void setCommand(String command) { this.command = command; }

    public String getArgument() { return argument; }
    public void setArgument(String argument) { this.argument = argument; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public byte[] getFileData() { return fileData; }
    public void setFileData(byte[] fileData) { this.fileData = fileData; }

    public Object getMsg() { return msg; }
    public void setMsg(Object msg) { this.msg = msg; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public String toString() {
        return "Envelope{" + "command='" + command + '\'' + ", argument='" + argument + '\'' + ", fileName='" + fileName + '\'' + 
                ", fileSize=" + (fileData != null ? fileData.length : 0) + " bytes" +
                ", msg=" + (msg != null ? msg : "null") +
                ", name='" + name + '\'' +
                '}';
    }
}
