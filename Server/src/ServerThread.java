import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Artur Guimarães
 */
public class ServerThread extends Thread {

    public String name;
    private long startTime, endTime;
    
    public void run() {
    }

    public ServerThread(String name) {
        setStartTime();
        this.name = name;
        this.start();
    }
    
    public void multiplyMatrix(int[][] m1, int[][] m2) {
        System.out.println("\n" + name + " está gerando a matriz resultande MA1 X MA2 ...");
        int[][] result = getMultiplyMatrix(m1, m2);
        generateMatrixFile("multiplica.txt", result);
        setEndTime();
    }
    
    private int[][] getMultiplyMatrix(int[][] m1, int[][] m2) {
        if (m1.length == m2.length) {
             int dimension = m1.length;
            int[][] result = new int[dimension][dimension];
            for (int i = 0; i < dimension; i++)
                for (int j = 0; j < dimension; j++) 
                    result[i][j] = m1[i][j] * m2[i][j];
            return result;
        }
        else
            return null;
    }
    
    public void sumMatrix(int[][] m1, int[][] m2) {
        System.out.println("\n" + name + " está gerando a matriz resultande MA1 + MA2 ...");
        int[][] sumResult = getSumMatrix(m1, m2);
        generateMatrixFile("soma.txt", sumResult);
        setEndTime();
    }
    
    private int[][] getSumMatrix(int[][] m1, int[][] m2) {
        if (m1.length == m2.length) {
             int dimension = m1.length;
            int[][] result = new int[dimension][dimension];
            for (int i = 0; i < dimension; i++)
                for (int j = 0; j < dimension; j++) 
                    result[i][j] = m1[i][j] + m2[i][j];
            return result;
        }
        else
            return null;
    }
    
    private void generateMatrixFile(String newFileName, int[][] matrix) {
        try {
            System.out.println(String.format("Gerando arquivo %s ...",newFileName));
            Formatter file = new Formatter(new File(newFileName));
            
            //Escrevendo a dimensao
            file.format("%s\n%d\n", "DIMENSAO", matrix[0].length);
            
            //Escrevendo matriz no arquivo
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix.length; j++)
                    file.format("%d ", matrix[i][j]);
                file.format("%s\n","");
            }
            System.out.println(String.format("%s gerado com sucesso!",newFileName));
            file.close();
        }
        catch (FileNotFoundException ex) {
            System.out.println("Arquivo não pode ser criado.");
        }

    }
    
    public void setStartTime() {
        startTime = System.nanoTime();
    }
    
    public String getStartTime() {
        return getFormattedTime(startTime);
    }
    
    public void setEndTime() {
        endTime = System.nanoTime();
    }
    
    public String getEndTime() {
        return getFormattedTime(endTime);
    }
    
    public String getExecutionTime() {
        return getFormattedTime(endTime - startTime);
    }
    
    public String getFormattedTime(long time) {
        long h = TimeUnit.HOURS.convert(time, TimeUnit.NANOSECONDS);
        long m = TimeUnit.MINUTES.convert(time, TimeUnit.NANOSECONDS);
        long s = TimeUnit.SECONDS.convert(time, TimeUnit.NANOSECONDS);
        long ms = TimeUnit.MILLISECONDS.convert(time, TimeUnit.NANOSECONDS);
        long us = TimeUnit.MICROSECONDS.convert(time, TimeUnit.NANOSECONDS);        
        return String.format("%dh%dm%ds%dms%dus",h,m,s,ms,us);
    }
    
}