import java.net.*;

/**
 *
 * @author Artur Guimarães
 */
class Client {
    
    public static String hostname = "127.0.0.1";
    public static int port = 8888;
    
    public static void main(String args[]) throws Exception {
        Socket socket = new Socket(hostname,port);
        FTPClient client = new FTPClient(socket);
        client.run();
    }
}