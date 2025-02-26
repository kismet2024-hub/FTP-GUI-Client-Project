import java.io.Serializable;
import java.io.File;
import java.nio.file.Files;

/**
 * Envelope class for wrapping messages, file data, and additional metadata
 * for communication between server and client in a GUI-based console.
 */
public class Envelope implements Serializable {
    private String command;   // Command for the operation (message type or file transfer)
    private String argument;  // Additional argument for the message (optional)
    private String fileName;  // Stores the file name for file transfers
    private byte[] fileData;  // Stores the file content in bytes
    private Object msg;       // Stores the message content (text message, object, etc.)
    private String name;      // Name for identification or sender
    
    // Default constructor
    public Envelope() {}

    // Constructor for regular messages (with command, argument, and message)
    public Envelope(String command, String argument, Object msg) {
        this.command = command;
        this.argument = argument;
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

    // Utility method to create an Envelope for text messages
    public static Envelope createMessageEnvelope(String message) {
        return new Envelope("#message", null, message);
    }

    // Utility method to create an Envelope for file uploads
    public static Envelope createFileEnvelope(String filePath) throws Exception {
        File file = new File(filePath);
        byte[] fileData = Files.readAllBytes(file.toPath());
        return new Envelope("#ftpUpload", file.getName(), fileData);
    }
}
