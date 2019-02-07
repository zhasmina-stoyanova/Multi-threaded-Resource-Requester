# Multi-threaded-Resource-Requester
A multi-threaded Java application with implemented client and server communication. Server handles GET, HEAD and DELETE client requests.

### Task 1. Returning of binary images (GIF, JPEG and PNG).

The return of binary images is done through byte array and sending the response to the client as an output stream.
### Task 2. The Multithreading can support multiple concurrent client connection requests up to a specified limit.

The multithreading is done through implementing the Runnable interface for each thread and putting it into a pool of connections with the help of the class Executors and its static method newFixedThreadPool(Config.MAX_CONNECTIONS_ALLOWED) by setting maximum number of connections allowed (in the Config.java class). The example is for at most 10 connections.
### Task 3. Logging each request in a  file, indicating  date/time,  request  type, response code etc.
The file log.txt is created in the directory where the WebServerMain.java file is. In this case, it is in the src subdirectory of the main project directory.
The file contains information about the date and time of the response, the requested method and information, containing the code of the response with some additional data, such as: length of file, content type, server name.
### Task 4. Support of other methods in addition to GET and HEAD.
The DELETE method is implemented. It removes a file and gives a response to the client with an appropriate code, depending on the result. 200 OK if the file is successfully deleted.
