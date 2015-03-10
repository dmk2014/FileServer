package ftp;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@WebService
public class FileServer implements IFileServer {
    List<String> connectedUsers = new ArrayList<String>();
    final String STORAGE_DIRECTORY = "C:/fileserver/"; //Default storage for uploaded files
    static Endpoint endpoint;

    //The followed are the exposed web methods
    @WebMethod
    public boolean login(String username) {
        boolean result = false;

        if(username.trim().length() > 0) {
            if (createUserFolder(username)) {
                connectedUsers.add(username);
                System.out.println(username + " successfully connected");
                result = true;
            }
        }

        System.out.println("Login returning: " + result + "\n");
        return result;
    }

    @WebMethod
    public boolean logoff(String username) {
        if(connectedUsers.remove(username))
        {
            System.out.println(username + " disconnected successfully\n");
            return true;
        }
        else{
            System.out.println("Error disconnecting " + username + "\n");
            return false;
        }
    }

    @WebMethod
    public boolean upload(String fileName, byte[] fileData, String username){
        if(connectedUsers.contains(username))
            return storeFile(fileName,fileData,username);
        else
            return false;
    }

    @WebMethod
    public byte[] download(String fileName, String username){
        if(connectedUsers.contains(username))
            if(checkFileExists(fileName,username)){
                return retrieveFile(fileName,username);
            }

        return null;
    }

    @WebMethod
    public List<String> getFileList(String username){
        List<String> fileNames = new ArrayList<>();
        File directory = new File(STORAGE_DIRECTORY + username + "/");
        File[] listOfFiles = directory.listFiles();

        for(File file : listOfFiles)
            fileNames.add(file.getName());

        return fileNames;
    }

    public static void main(String[] argv) {
        //Check for arguments (console app only) and attempt to apply them
        if(argv.length == 0) {
            start("localhost", 8080);
        }
        else if(argv.length == 1){
            start(argv[0], 8080);
        }
        else if(argv.length == 2){
            start(argv[0], Integer.parseInt(argv[1]));
        }
        else {
            System.out.println("Failed to start server...arguments incorrect");
            System.exit(0);
        }
    }

    public static boolean start(String hostname, int port){
        try {
            //Start the web service using specified host and port
            String address = "http://" + hostname + ":" + port + "/FileServer";
            endpoint = Endpoint.publish(address, new FileServer());
            System.out.println("Server Started on " + address + "\n");
            return true;
        }catch (Exception ex){
            System.out.println(ex.getMessage());
            System.out.println("Failed to start server...check arguments\n");
            return false;
        }
    }

    public static void stop(){
        //Used to shutdown the web service
        endpoint.stop();
        System.out.println("Server stopped\n");
    }

    //Exclude ensures methods are not exposed
    @WebMethod(exclude = true)
    private boolean createUserFolder(String username)
    {
        File folder = new File(STORAGE_DIRECTORY + username);
        boolean success;

        if(!folder.exists())
        {
            try {
                System.out.println("Attempting to create folder: " + folder);
                Files.createDirectories(folder.toPath());
                System.out.println("Folder created....returning success");
                success = true;
            }
            catch(Exception e)
            {
                System.out.println("Error creating folder....returning failure");
                success = false;
            }
        }
        else
        {
            System.out.println("Folder already exists....returning success");
            success = true;
        }

        return success;
    }

    @WebMethod(exclude = true)
    private boolean storeFile(String fileName, byte[] fileData, String username)
    {
        System.out.println("Storing file (" + username + "): " + fileName);
        boolean success;

        try {
            //Attempt to write file data to disk
            File fileToWrite = new File(STORAGE_DIRECTORY + username + "/" + fileName);
            Files.write(fileToWrite.toPath(),fileData);
            success = true;
            System.out.println("File stored successfully...returning success\n");

        }catch(Exception ex){
            System.out.println("Error while saving file...returning failure\n");
            success = false;
        }

        return success;
    }

    @WebMethod(exclude = true)
    private boolean checkFileExists(String fileName, String username){
        boolean exists;

        try {
            Path filePath = Paths.get(STORAGE_DIRECTORY + username + "/" + fileName);

            if (!Files.exists(filePath)) {
                System.out.println("File not found...returning does not exist\n");
                exists = false;
            } else
                exists = true;
        }catch(Exception e){
            System.out.println("File not found (exception thrown)...returning does not exist\n");
            exists = false;
        }

        return exists;
    }

    @WebMethod(exclude = true)
    private byte[] retrieveFile(String fileName, String username){
        try {
            //Attempt to read file data from disk
            Path file = Paths.get(STORAGE_DIRECTORY + username + "/" + fileName);
            byte[] data = Files.readAllBytes(file);

            System.out.println("File retrieved...returning data\n");
            return data;
        }catch(Exception e){
            System.out.println("Error retrieving file...returning failure\n");
            return null;
        }
    }
}