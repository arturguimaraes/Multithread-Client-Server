import java.io.*;
import java.net.*;
import java.util.*;

/**
 *
 * @author Artur Guimarães
 */
public class FTPServer extends Thread {
    private Socket socket;
    private DataInputStream dataIn;
    private DataOutputStream dataOut;
    private int[][] MA1, MA2;
    private int dimension;
    private String fileName = "matriz.txt",
            multFileName = "multiplica.txt",
            sumFileName = "soma.txt";
    private ServerThread TH1, TH2;
    
    public FTPServer (Socket port) {
        try {
            socket = port;                        
            dataIn = new DataInputStream(socket.getInputStream());
            dataOut = new DataOutputStream(socket.getOutputStream());
            System.out.println("Cliente FTP conectado!");
            start();
        }
        catch(Exception ex) {
        }        
    }
    
    public void run() {
        System.out.println("Esperando comando ...");
        while(true) {
            try {
                String clientCommand = dataIn.readUTF();
                switch (clientCommand) {
                    case "SEND":
                        System.out.println("\nRecebendo arquivo ...");                
                        ReceiveFile();
                        break;
                    case "GET":
                        System.out.println("\nEnviando arquivos de resultado ...");
                        SendFile();
                        break;
                    case "DISCONNECT":
                        System.out.println("\nComando de desconectar recebido ...");
                        System.exit(1);
                        break;
                    default:
                        break;
                }
            }
            catch(Exception ex) {
            }
        }
    }
    
    private void ReceiveFile() throws Exception {
        fileName = dataIn.readUTF();
        if(fileName.compareTo("File not found") == 0) {
            return;
        }
        File file = new File(fileName);
        String option;
        
        if(file.exists()) {
            dataOut.writeUTF("File Already Exists");
            return;
        }
        else {
            dataOut.writeUTF("SendFile");
            option = "S";
        }
            
        if(option.compareTo("S") == 0) {
            System.out.println("Recebendo arquivo ...");
            FileOutputStream fileOut = new FileOutputStream(file);
            int character;
            String temp;
            do {
                temp = dataIn.readUTF();
                character = Integer.parseInt(temp);
                if(character != -1) {
                    fileOut.write(character);                    
                }
            } while(character != -1);
            fileOut.close();
            System.out.println("Arquivo recebido com sucesso!");
            dataOut.writeUTF("Arquivo enviado com sucesso!");
            initiateThreads();
        }
        else {
            return;
        }
    }
    
    private void initiateThreads() {
        ReadFile();
        
        TH1 = new ServerThread("TH1");
        TH1.multiplyMatrix(MA1, MA2);
        TH2 = new ServerThread("TH2");        
        TH2.sumMatrix(MA1, MA2);
    }
    
    public void ReadFile() {
        try {
            Scanner file = new Scanner(new File(fileName));
            while(file.hasNextLine()) {
                String nextLine = file.nextLine();
                switch (nextLine) {
                    case "DIMENSAO":
                        dimension = Integer.parseInt(file.nextLine());
                        MA1 = new int[dimension][dimension];
                        MA2 = new int[dimension][dimension];
                        break;
                    case "MA1":
                        for (int i = 0; i < dimension; i++) {
                            nextLine = file.nextLine();
                            String[] numbers = nextLine.split(" ");
                            for (int j = 0; j < dimension; j++)
                                MA1[i][j] = Integer.parseInt(numbers[j]);
                        }
                        break;
                    case "MA2":
                        for (int i = 0; i < dimension; i++) {
                            nextLine = file.nextLine();
                            String[] numbers = nextLine.split(" ");
                            for (int j = 0; j < dimension; j++)
                                MA2[i][j] = Integer.parseInt(numbers[j]);
                        }
                        break;
                }
            }
            file.close();
            System.out.println("Matrizes recebidas ...");
        }
        catch (FileNotFoundException ex) {
            System.out.println("Arquivo não encontrado.");
        }
    }
    
    private void SendFile() throws Exception {        
        multFileName = dataIn.readUTF();
        sumFileName = dataIn.readUTF();
        File multFile = new File(multFileName);
        File sumFile = new File(sumFileName);
        if(!multFile.exists() || !sumFile.exists()) {
            dataOut.writeUTF("File Not Found");
            return;
        }
        else {
            //Enviando multiplica.txt
            dataOut.writeUTF("SENDINGFIRSTFILE");
            FileInputStream fileIn = new FileInputStream(multFile);
            int character;
            do {
                character = fileIn.read();
                dataOut.writeUTF(String.valueOf(character));
            } while(character != -1);    
            fileIn.close();    
            System.out.println("multiplica.txt enviado com sucesso!");
            dataOut.writeUTF("multiplica.txt recebido com sucesso!");
            
            //Enviando soma.txt
            fileIn = new FileInputStream(sumFile);
            do {
                character = fileIn.read();
                dataOut.writeUTF(String.valueOf(character));
            } while(character != -1);    
            fileIn.close();    
            System.out.println("soma.txt enviado com sucesso!\nTodos os arquivos foram enviados com sucesso!");
            dataOut.writeUTF("soma.txt recebido com sucesso!\nTodos os arquivos foram recebidos com sucesso!");
            dataOut.writeUTF(TH1.getStartTime());
            dataOut.writeUTF(TH2.getStartTime());
            dataOut.writeUTF(TH1.getEndTime());
            dataOut.writeUTF(TH2.getEndTime());
            dataOut.writeUTF(TH1.getExecutionTime());
            dataOut.writeUTF(TH2.getExecutionTime());
        }
    }
}