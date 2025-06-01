
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package newUi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static newUi.Attendance.readFileContent;

/**
 *
 * @author DAMIDU
 */
public class test {
    public static void createFile(String filePath){
        try {
            File file = new File(filePath);
            file.createNewFile(); // Creates the file
        } catch (IOException ex) {
            Logger.getLogger(test.class.getName()).log(Level.SEVERE, null, ex);
        }
}


      public static void main(String[] args) {
 createFile("/example.txt");

    }
}
