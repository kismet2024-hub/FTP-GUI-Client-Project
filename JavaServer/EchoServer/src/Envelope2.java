
import java.io.Serializable;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author COMPUTER
 */
public class Envelope2 implements Serializable {
    
    private String command;
    private String argument;
    private String fileName;  // Stores the file name for file transfers
    private byte[] fileData;  // Stores the file content in bytes

    // Default constructor
    public Envelope2() {}

    // Constructor for messages
    public Envelope2(String command, String argument, Object msg) {
        this.command = command;
        this.argument = argument;
        this.fileName = null;
        this.fileData = null;
    }

    // Constructor for file transfers
    public Envelope2(String command, String fileName, byte[] fileData) {
        this.command = command;
        this.argument = null;
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

    @Override
    public String toString() {
        return "Envelope{command='" + command + "', fileName='" + fileName + "', fileSize=" + (fileData != null ? fileData.length : 0) + " bytes}";
    }
}
    

