import java.net.*;

/**
 *
 * @author Artur Guimarães
 */
public class Server {
    
    public static int port = 8888;
    
    public static void main(String args[]) throws Exception {
        ServerSocket socket = new ServerSocket(port);
        System.out.println(String.format("Server FTP iniciado na porta %d.",port));
        System.out.println("Esperando conexão ...");
        while(true) {
            FTPServer server = new FTPServer(socket.accept());
        }
    }
}