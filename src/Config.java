
/**
 * The class contains variables that are used for configuring server and client
 * sockets and thread pool maximum reused number of threads.
 *
 * @170024836
 * @version 1
 * @since 10.11.2017
 */
public class Config {

    //the port on which the server listens for connections
    private static int port;
    //the directory from which the server serves the documents to clients
    private static String userDir = "";

    /**
     * A constant variable with the maximum clients connections allowed.
     */
    public static final int MAX_CONNECTIONS_ALLOWED = 10; //

    /**
     * get method for getting the port on which the server listens for
     * connections.
     *
     * @return the port on which the server listens for connections
     */
    public static int getPort() {
        return port;
    }

    /**
     * set method to set the port on which the server listens for connections.
     *
     * @param aPort the port to set on which the server listens for connections
     */
    public static void setPort(int aPort) {
        port = aPort;
    }

    /**
     * get method for getting the directory where the server serves the
     * documents to clients.
     *
     * @return the userDir where the server serves the documents to clients
     */
    public static String getUserDir() {
        return userDir;
    }

    /**
     * set method for setting the directory where the server serves the
     * documents to clients.
     *
     * @param aUserDir the userDir to set where the server serves the documents
     * to clients
     */
    public static void setUserDir(String aUserDir) {
        userDir = aUserDir;
    }
}
