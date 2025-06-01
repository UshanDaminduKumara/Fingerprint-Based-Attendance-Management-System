package newUi;

import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.JOptionPane;

public class DBconnect {
    public static Connection connect() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://192.168.167.203:3306/attendance_db";
            String user = "remoteuser";        
            String password = "yourpassword";  
            
            conn = DriverManager.getConnection(url, user, password); // 🟢 This line was missing

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
        return conn;
    }
}