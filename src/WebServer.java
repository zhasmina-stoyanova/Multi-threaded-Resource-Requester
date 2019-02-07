
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The class contains a constructor that creates a server socket, listening on a
 * given port, a method runWebServer() that is used to create a client socket
 * and to accept a connection from the server and create a thread object and
 * submit it in the thread pool and execute it. If an exception occurs, the
 * server stops.
 *
 * @170024836
 * @version 1
 * @since 10.11.2017
 */
public class WebServer {

    private ServerSocket serverSocket; //an instance variable of the server socket class
    //creates a thread pool that reuses a fixed number of threads
    private ExecutorService executorService = Executors.newFixedThreadPool(Config.MAX_CONNECTIONS_ALLOWED);

    /**
     * A constructor that creates an instance of a ServerSocket class that
     * listens for connections on the specified port.
     *
     * @param port on which the server is listening for connections
     */
    public WebServer(int port) {
        try {
            //creates server socket instance that listens on the specified port
            serverSocket = new ServerSocket(port);
            System.out.println("Server is listening for connections on port: " + port + "...");
        } catch (IOException ex) {
            System.out.println("Can not create server socket!");
            ex.printStackTrace();
        }
    }

    /**
     * After the client accepts server's connection, the method creates a new
     * object of the class ClientThread for every client's request and runs it
     * in a new thread. Every thread is added to the thread pool.
     *
     */
    public void runWebServer() {
        while (true) {
            Socket clientSocket;
            try {
                //client accepting a connection
                clientSocket = serverSocket.accept();
                //prints the hostname of the IP address of the client on the standard output
                System.out.println("Connection request from: " + clientSocket.getInetAddress().getHostName());
                //creating a thread
                ClientThread clientThread = new ClientThread(clientSocket);
                //submitting a task for execution
                executorService.submit(clientThread);
            } catch (IOException ex) {
                System.out.println("Server cannot accept connection from client!");
                ex.printStackTrace();
                //stops the server running
                stopServer();
            }
        }
    }

    /**
     * The method stops the running server.
     */
    public void stopServer() {
        //stops the server and cancels tasks in progress
        executorService.shutdownNow();
        try {
            serverSocket.close();
        } catch (IOException e) {
            System.out.println("Error in server shutdown");
            e.printStackTrace();
        }
        //terminates the program
        System.exit(-1);
    }
}
