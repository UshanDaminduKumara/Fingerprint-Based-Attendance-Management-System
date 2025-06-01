package newUi;

import java.io.*;
import java.sql.*;
import javax.swing.JFileChooser;

public class InsertFingerprint {
       private static String selectImageFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Fingerprint Image");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image Files", "jpg", "png", "bmp"));
        
        int result = fileChooser.showOpenDialog(null);
       
        return (result == JFileChooser.APPROVE_OPTION) ? fileChooser.getSelectedFile().getAbsolutePath() : "";
      
       }
               
               
    public static void main(String[] args) {
        String imagePath =selectImageFile();
                //"C:\\Users\\DAMIDU\\Documents\\NetBeansProjects\\fingerPrint\\src\\fingerprint1.jpg"; // Fingerprint Image
        String std_id = "SEU/IS/20/PS/101";

        String url = "jdbc:mysql://localhost:3306/attendance_db";
        String user = "root";
        String password = "";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO std (`std_id`, `std_name`, `bg_no`, `fac`, `deg`, `fingerprint`) VALUES (?, ?, ?, ?, ?, ?)")) {

            File imageFile = new File(imagePath);
            FileInputStream fis = new FileInputStream(imageFile);

            stmt.setString(1, std_id);
            stmt.setString(2, "ushan");
            stmt.setString(3, "20");
            stmt.setString(4, "Applied Sciences");
            stmt.setString(5, "Genaral");
            stmt.setBinaryStream(6, fis, (int) imageFile.length());
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Fingerprint inserted successfully.");
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
