package ftp;

import java.util.List;

//This interface defines the functionality available through the web service
public interface IFileServer {
    boolean login(String username);
    boolean logoff(String username) ;
    boolean upload(String fileName, byte[] fileData, String username);
    byte[] download(String fileName, String username);
    List<String> getFileList(String username);
}