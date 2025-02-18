
import java.io.Serializable;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Instructor
 */public class Envelope implements Serializable {
    private String command;      // Command for the operation (message type or file transfer)
    private String argument;     // Additional argument for the message (optional)
    private String fileName;     // Stores the file name for file transfers
    private byte[] fileData;     // Stores the file content in bytes
    private Object msg;          // Stores the message or data (text message, object, etc.)
    private String name;         // Name for identification or sender
    private String arg;          // Argument for the specific purpose (could be a command or other)

    // Default constructor
    public Envelope() {}

    // Constructor for regular messages (with command, argument, and message)
    public Envelope(String command, String argument, Object msg) {
        this.command = command;
        this.argument = argument;
        this.fileName = fileName;
        this.fileData = fileData;
        this.msg = msg;  // Store the message content
        this.name = name;
        this.arg = arg;
    }

    // Constructor for file transfers (with command, fileName, and file data)
    public Envelope(String command, String fileName, byte[] fileData) {
        this.command = command;
        this.argument = null;
        this.fileName = fileName;
        this.fileData = fileData;
        this.msg = msg; // No message content
        this.name = name;
        this.arg = arg;
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

    public String getArg() { return arg; }
    public void setArg(String arg) { this.arg = arg; }

    @Override
    public String toString() {
        return "Envelope{command='" + command + "', argument='" + argument + "', fileName='" + fileName + 
               "', fileSize=" + (fileData != null ? fileData.length : 0) + " bytes, msg=" + (msg != null ? msg : "null") + 
               ", name='" + name + "', arg='" + arg + "'}";
    }
}