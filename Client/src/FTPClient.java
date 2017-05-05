import java.io.*;
import java.net.*;
import java.util.*;

/**
 *
 * @author Artur Guimarães
 */
public class FTPClient {
    private Socket socket;
    private DataInputStream dataIn;
    private DataOutputStream dataOut;
    private BufferedReader bufferedReader;
    private int[][] MA1, MA2;
    private int dimension = -1;
    private String fileName = "matriz.txt",
            multFileName = "multiplica.txt",
            sumFileName = "soma.txt";
    
    public FTPClient(Socket port) {
        System.out.println("Bem-vindo!\nTentando estabelecer conexão ...");
        try {
            socket = port;
            dataIn = new DataInputStream(socket.getInputStream());
            dataOut = new DataOutputStream(socket.getOutputStream());
            bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        }
        catch(Exception ex) {
        }        
    }
    
    public void run() throws Exception {
        System.out.println(String.format("Conexão estabelecida na porta %d.\n",socket.getPort()));
        int max = 100;//números aleatórios serão de 0 à 99
        
        //Obtendo dimensao do usuário
        while (dimension <= 0) {
            System.out.println("Digite a dimensão M para MA1 e MA2 (M x M):");
            dimension = Integer.parseInt(bufferedReader.readLine());
            if (dimension <= 0)
                System.out.println("\nNúmero inválido!");
        }
        System.out.println(String.format("\nDimensao escolhida: %d x %d", dimension, dimension));
        
        //Gerando MA1
        System.out.println("Gerando MA1...");
        MA1 = new int[dimension][dimension];
        for (int i = 0; i < dimension; i++)
            for (int j = 0; j < dimension; j++)
                MA1[i][j] = (int)(Math.random()*max);
        printMatrix(MA1);
        
        //Gerando MA2
        System.out.println("\nGerando MA2...");
        MA2 = new int[dimension][dimension];
        for (int i = 0; i < dimension; i++)
            for (int j = 0; j < dimension; j++)
                MA2[i][j] = (int)(Math.random()*max);
        printMatrix(MA2);
        
        generateMatrixFile();
        displayMenu();
    }
    
    private void generateMatrixFile() {
        try {
            System.out.println(String.format("\nGerando arquivo %s ...",fileName));
            Formatter file = new Formatter(new File(fileName));
            
            //Escrevendo a dimensao
            file.format("%s\n%d\n", "DIMENSAO", dimension);
            
            //Escrevendo MA1 no arquivo
            file.format("%s\n", "MA1");
            for (int i = 0; i < MA1.length; i++) {
                for (int j = 0; j < MA1.length; j++)
                    file.format("%d ", MA1[i][j]);
                file.format("%s\n","");
            }
            
            //Escrevendo MA2 no arquivo
            file.format("%s\n", "MA2");
            for (int i = 0; i < MA2.length; i++) {
                for (int j = 0; j < MA2.length; j++)
                    file.format("%d ", MA2[i][j]);
                file.format("%s\n","");
            }
            System.out.println(String.format("%s gerado com sucesso!",fileName));
            file.close();
        }
        catch (FileNotFoundException ex) {
            System.out.println("Arquivo não pode ser criado.");
        }

    }
    
    private void displayMenu() throws Exception {
        while(true) {  
            String menu = "\n[ MENU ]\n" + 
                          "1. Mostrar matriz.txt na tela\n" +
                          "2. Enviar matriz.txt para o servidor\n" +
                          "3. Receber multiplica.txt e soma.txt do servidor\n" +
                          "4. Mostrar multiplica.txt na tela\n" +
                          "5. Mostrar soma.txt na tela\n" +
                          "6. Sair\n\n" +
                          "Digite sua opção:";
            System.out.println(menu);
            
            int choice;
            choice = Integer.parseInt(bufferedReader.readLine());
            switch(choice) {
                case 1:
                    System.out.println("\nMA1:");
                    printMatrix(MA1);
                    System.out.println("\nMA2:");
                    printMatrix(MA2);
                    break;
                case 2:
                    dataOut.writeUTF("SEND");
                    SendFile();
                    break;
                case 3:
                    dataOut.writeUTF("GET");
                    ReceiveFile();
                    break;
                case 4:
                    System.out.println("\nMatriz MA1 X MA2:");
                    printMatrix(ReadMatrixFromFile(multFileName));
                    break;
                case 5: 
                    System.out.println("\nMatriz MA1 + MA2:");
                    printMatrix(ReadMatrixFromFile(sumFileName));
                    break;
                default:
                    dataOut.writeUTF("DISCONNECT");
                    System.exit(1);
                    break;
            }
        }
    }
    
    private void SendFile() throws Exception {        
        File file = new File(fileName);
        if(!file.exists()) {
            System.out.println("Arquivo não existe!");
            dataOut.writeUTF("File not found");
            return;
        }
        
        dataOut.writeUTF(fileName);
        
        String serverMessage = dataIn.readUTF();
        if(serverMessage.compareTo("File Already Exists") == 0) {
            System.out.println("Arquivo já existe.");
            return;
        }
        
        System.out.println("Enviando arquivo ...");
        FileInputStream fileIn = new FileInputStream(file);
        int character;
        do {
            character = fileIn.read();
            dataOut.writeUTF(String.valueOf(character));
        } while(character != -1);
        fileIn.close();
        System.out.println(dataIn.readUTF());
    }
    
    private void ReceiveFile() throws Exception {
        dataOut.writeUTF(multFileName);
        dataOut.writeUTF(sumFileName);
        String serverMessage = dataIn.readUTF();
        
        if(serverMessage.compareTo("File Not Found") == 0) {
            System.out.println("Arquivos não encontrados no servidor ...");
            return;
        }
        else if(serverMessage.compareTo("SENDINGFIRSTFILE") == 0) {
            //Recebendo multiplica.txt
            System.out.println("\nRecebendo multiplica.txt ...");
            File file = new File(multFileName);
            FileOutputStream fileOut = new FileOutputStream(file);
            int character;
            String temp;
            do {
                temp = dataIn.readUTF();
                character = Integer.parseInt(temp);
                if(character != -1) {
                    fileOut.write(character);                    
                }
            }while(character != -1);
            fileOut.close();
            System.out.println(dataIn.readUTF());
        
            //Recebendo soma.txt
            System.out.println("Recebendo soma.txt ...");
            file = new File(sumFileName);
            fileOut = new FileOutputStream(file);
            do {
                temp = dataIn.readUTF();
                character = Integer.parseInt(temp);
                if(character != -1) {
                    fileOut.write(character);                    
                }
            }while(character != -1);
            fileOut.close();
            System.out.println(dataIn.readUTF());
            String TH1StartTime = dataIn.readUTF();
            String TH2StartTime = dataIn.readUTF();
            String TH1EndTime = dataIn.readUTF();
            String TH2EndTime = dataIn.readUTF();
            String TH1ExecTime = dataIn.readUTF();
            String TH2ExecTime = dataIn.readUTF();
            System.out.println("\nMatriz MA1 X MA2:");
            printMatrix(ReadMatrixFromFile(multFileName));
            System.out.println("\nMatriz MA1 + MA2:");
            printMatrix(ReadMatrixFromFile(sumFileName));
            System.out.println("\nInício TH1: " + TH1StartTime);
            System.out.println("Término TH1: " + TH1EndTime);
            System.out.println("Tempo de execução TH1: " + TH1ExecTime);
            System.out.println("Início TH2: " + TH2StartTime);
            System.out.println("Término TH2: " + TH2EndTime);
            System.out.println("Tempo de execução TH2: " + TH2ExecTime);
        }      
    }
    
    public int[][] ReadMatrixFromFile(String newFileName) {
        try {
            Scanner file = new Scanner(new File(newFileName));
            int newDimension = 0;
            int[][] result = new int[newDimension][newDimension];
            while(file.hasNextLine()) {
                String nextLine = file.nextLine();
                if(nextLine.equals("DIMENSAO")) {
                    newDimension = Integer.parseInt(file.nextLine());
                    result = new int[newDimension][newDimension];
                }
                else {
                    for (int i = 0; i < newDimension; i++) {
                        String[] numbers = nextLine.split(" ");
                        for (int j = 0; j < newDimension; j++)
                            result[i][j] = Integer.parseInt(numbers[j]);
                        if(file.hasNextLine())
                            nextLine = file.nextLine();
                    }
                }
            }
            file.close();
            return result;
        }
        catch (FileNotFoundException ex) {
            System.out.println("Arquivo não encontrado.");
            return null;
        }
    }
    
    private void printMatrix(int[][] matrix) {
        if (matrix != null) {
            if (matrix.length > 0) {
                for (int i = 0; i < matrix.length; i++) {
                    for (int j = 0; j < matrix.length; j++) 
                        System.out.print(matrix[i][j] + " ");
                    System.out.println(" ");
                }
                return;
            }
        }
        System.out.println("Matriz vazia!");
    }
}