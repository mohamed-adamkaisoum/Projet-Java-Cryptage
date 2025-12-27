package model;

import java.io.Serializable;

public class SecureFile implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String fileName;
    private String encryptedFileName;
    private String ownerUsername;
    private long fileSize;
    private long timestamp;
    
    public SecureFile(String fileName, String encryptedFileName, String ownerUsername, long fileSize) {
        this.fileName = fileName;
        this.encryptedFileName = encryptedFileName;
        this.ownerUsername = ownerUsername;
        this.fileSize = fileSize;
        this.timestamp = System.currentTimeMillis();
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getEncryptedFileName() {
        return encryptedFileName;
    }
    
    public void setEncryptedFileName(String encryptedFileName) {
        this.encryptedFileName = encryptedFileName;
    }
    
    public String getOwnerUsername() {
        return ownerUsername;
    }
    
    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }
    
    public long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        return fileName + " (" + (fileSize / 1024) + " KB)";
    }
}
