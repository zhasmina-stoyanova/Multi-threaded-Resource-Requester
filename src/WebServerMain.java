
/**
 * The class contains one main method that reads user arguments and creates
 * an instance of the WebServer class that listens on a specified port and runs
 * runWebserver method.
 *
 * @170024836
 * @version 1
 * @since 10.11.2017
 */
public class WebServerMain {

    /**
     * Reads the user arguments, creates a WebServer instance, that listens on a
     * specified port.
     *
     * @param args first argument: the directory from which the server serves
     * the documents second argument; the port on which the server listens for
     * connections
     * @throws InterruptedException if there is a problem reading the arguments
     */
    public static void main(String[] args) throws InterruptedException {
        //reads user input argusments
        if (args.length == 2) {
            try {
                Config.setUserDir(args[0]); //set the variable userDir value of the first argument
                System.setProperty("user.dir", Config.getUserDir()); //set the user current directory
                Config.setPort(Integer.parseInt(args[1])); //set the variable port value of the second argument
            } catch (Exception e) {
                System.out.println("Reading arguments exception message: " + e.getMessage());
            }
        }
        if (args.length == 0) {
            System.out.println("Usage: java WebServerMain <document_root> <port>");
            return;
        }
        //creates an instance of the WebServer class and runs the method to submit tasks for execution
        WebServer server = new WebServer(Config.getPort());
        server.runWebServer();
    }
}
