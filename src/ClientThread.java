
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.StringTokenizer;

/**
 * The class implements the Runnable interface for running multiple threads. It
 * contains a constructor for initialising a client socket and a method run()
 * that runs each request in a separate thread
 *
 * @170024836
 * @version 1
 * @since 10.11.2017
 */
public class ClientThread implements Runnable {

    //instance variables
    private Socket clientSocket;
    private BufferedReader br;
    private PrintWriter pw;
    private BufferedOutputStream bos;

    /**
     * Constructor that initialises the client socket.
     *
     * @param socket the socket that accepts the connection of the server
     */
    public ClientThread(Socket socket) {
        this.clientSocket = socket;
    }

    /**
     * The method runs each request in a separate thread. It reads client's
     * request and gives an appropriate response for the GET, HEAD and DELETE
     * requests. It also implements responses when service or request do not
     * exist.
     */
    public void run() {
        boolean afterFirstLine = false;
        String resourceFile = "";

        try {
            //input stream for reading the request from client
            br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            //output stream to respond to client for header information
            pw = new PrintWriter(clientSocket.getOutputStream());
            //binary data output stream to respond to client
            bos = new BufferedOutputStream(
                    clientSocket.getOutputStream());

            while (true) {
                //flag to read only the first line from client's message
                if (afterFirstLine) {
                    break;
                }

                //reads first line of client's message
                String clientLine = br.readLine();
                System.out.println("Clinet thread message: " + clientLine);

                //creates StringTokenizer to parse client's request
                StringTokenizer parse = new StringTokenizer(clientLine);
                String method = parse.nextToken().toUpperCase(); //parse the client's method/command
                resourceFile = parse.nextToken().toLowerCase(); //parse the requested file
                String subDir = getSubDirectory(resourceFile); //extracts a subdirectory if present
                resourceFile = getResourceName(resourceFile); //extracts the resourceFile from a subdirectory
                String response = ""; //response to be written in the logging file

                //logic for the corresponding methods GET, HEAD, DELETE and methods that are not implemented
                if (method.equals("HEAD") || method.equals("GET")) {
                    afterFirstLine = true; // change the flag if the first line of the client's request is written

                    //creates a file instance of the client's requested file in the user's directory with the
                    //appended subdirectory, if the file is located in one
                    File file = new File(Config.getUserDir() + subDir, resourceFile);
                    int length = (int) file.length(); // length of the file
                    String content = getContentType(resourceFile); //MIME content type of the file

                    FileInputStream fis = null;
                    //creates byte array (with size the file length) for the data to be stored
                    byte[] arrData = new byte[length];
                    try {
                        fis = new FileInputStream(file);
                        fis.read(arrData);
                        fis.close();

                        //header information
                        response = "HTTP/1.1 200 OK"; //response for the logging file
                        //setting header response to the client
                        setHeaderResponse(content, length);

                        //if the client's method is GET, puts the byte array in an output
                        //stream as a response to the client
                        if (method.equals("GET")) {
                            bos.write(arrData, 0, length);
                            bos.flush();
                        }
                    } catch (FileNotFoundException fnfe) {
                        response = "HTTP/1.1 404 Not Found"; //resource Not Found response for the logging file
                        //setting resource not found response to the client
                        setResourceNotFoundResponse(resourceFile);
                    }
                } else if (method.equals("DELETE")) {
                    afterFirstLine = true; // change the flag if the first line of the client's request is written
                    System.out.println("In delete method");
                    //deleting a resource and sending a response to the client
                    response = deleteResource(subDir, resourceFile);
                } else {
                    afterFirstLine = true;
                    // change the flag if the first line of the client's request is written
                    response = "HTTP/1.1 501 Not Implemented";
                    //when method is not implemented send a response to the client
                    setMethodNotImplementedResponse();
                }

                //get date and time now
                LocalDateTime now = LocalDateTime.now();

                //writes current date and time, method and header response information in a log file
                writeToLogFile(now + "   " + method + "   " + response + "\r\n");
            }
        } catch (Exception ex) {
            System.out.println("Exception while running the thread occured.");
            ex.printStackTrace();
        } finally {
            //closes all streams and the client socket connection
            closeConnAndStreams();
        }
    }

    /**
     * Extracts the subdirectory of a given resourceFile, if it exist, otherwise
     * returns an empty string.
     *
     * @param resourceFile The full name of the resourceFile, with backslash /
     * in the front
     * @return the subdirectory or an empty string
     */
    public String getSubDirectory(String resourceFile) {
        String subDir = "";
        int idxLastBackslash = resourceFile.lastIndexOf("/");
        subDir = resourceFile.substring(0, idxLastBackslash);
        return subDir;
    }

    /**
     * Extracts the resourceFile name from a subdirectory, if it is in one,
     * otherwise returns an empty string.
     *
     * @param resourceFile The full name of the resourceFile, with backslash /
     * in the front
     * @return the resourceFile name or an empty string
     */
    public String getResourceName(String resourceFile) {
        String resourceName = "";
        int idxLastBackslash = resourceFile.lastIndexOf("/");
        resourceName = resourceFile.substring(idxLastBackslash);
        return resourceName;
    }

    /**
     * Gives response to the client that the resourceFile is not found.
     *
     * @param resourceFile The name of the resourceFile, with backslash / in the
     * front
     */
    public void setResourceNotFoundResponse(String resourceFile) {
        pw.print("HTTP/1.1 404 Not Found\r\n");
        pw.print("Java Web Server\r\n");
        pw.print("\r\n");
        pw.print("<HTML>");
        pw.print("<HEAD><TITLE>Resource Not Found</TITLE>" + "</HEAD>");
        pw.print("<BODY>");
        pw.print("<H3>404 Resource Not Found: " + resourceFile + "</H3>");
        pw.print("</BODY>");
        pw.print("</HTML>");
        pw.flush();
    }

    /**
     * Gives response to the client that its method is not implemented by the
     * server.
     */
    public void setMethodNotImplementedResponse() {
        pw.print("HTTP/1.1 501 Not Implemented\r\n");
        pw.flush();
    }

    /**
     * Gives header as a response to the client.
     *
     * @param content The MIME content type of the resourceFile
     * @param length The length of the file
     */
    public void setHeaderResponse(String content, int length) {
        pw.print("HTTP/1.1 200 OK\r\n");
        pw.print("Java Web Server\r\n");
        pw.print("Content-Type: " + content + "\r\n");
        pw.print("Content-Length: " + length + "\r\n");
        pw.print("\r\n");
        pw.flush();
    }

    /**
     * The method writes information about current date and time, client's
     * method and response header information in a log file for each request on
     * a separate line.
     *
     * @param text The text to be written in the log file(the appended current
     * date and time, client's method and response, separated by intervals).
     */
    public static void writeToLogFile(String text) {
        try {
            //creates a log file and appends the text to the already existing(if any)
            FileWriter fw = new FileWriter("log.txt", true);
            fw.write(text);
            fw.close();
        } catch (Exception ex) {
            System.out.println("Cannot write into file: " + ex.getMessage());
        }
    }

    /**
     * Gets the MIME content type of a given file.
     *
     * @param file The file whose content must be defined.
     * @return content type of a given file
     */
    public String getContentType(String file) {
        if (file.endsWith(".htm") || file.endsWith(".html")) {
            return "text/html";
        } else if (file.endsWith(".png")) {
            return "image/png";
        } else if (file.endsWith(".gif")) {
            return "image/gif";
        } else if (file.endsWith(".jpg") || file.endsWith(".jpeg")) {
            return "image/jpeg";
        } else {
            return "text/plain";
        }
    }

    /**
     * The method deletes a given resourceFile and writes message to the client
     * if it succeeded.
     *
     * @param resource the file to be deleted.
     * @param addDir the subdirectory in which the file might be located
     * @return the response for the log file
     */
    public String deleteResource(String addDir, String resource) {
        String response = "";
        //check for file existence
        try {
            File file = new File(Config.getUserDir() + addDir + resource);
            String content = getContentType(resource);
            if (file.delete()) {
                System.out.println(file.getName() + " is deleted!");
                response = "HTTP/1.1 200 OK";
                pw.print("HTTP/1.1 200 OK\r\n");
                pw.print("Java Web Server\r\n");
                pw.print("Content-Type: " + content + "\r\n");
                pw.print("Content-Length: " + file.length() + "\r\n");
                pw.print("\r\n");
                pw.flush();
            } else {
                response = "HTTP/1.1 202 ACCEPTED";
                pw.print("HTTP/1.1 202 ACCEPTED\r\n");
                pw.print("\r\n");
                pw.flush();
                System.out.println("Delete operation did not finish successfully.");
            }
        } catch (Exception e) {
            System.out.println("Can not delete the resource!");
            e.printStackTrace();
        }
        return response;
    }

    /**
     * The method closes all streams and the client socket connection.
     */
    public void closeConnAndStreams() {
        try {
            if (br != null) {
                br.close();
            }
            if (pw != null) {
                pw.close();
            }
            if (bos != null) {
                bos.close();
            }
            if (clientSocket != null) {
                clientSocket.close();
            }
        } catch (IOException ex) {
            System.out.println("Can not close all the streams and/or client connection!");
            ex.printStackTrace();
        }
    }
}
