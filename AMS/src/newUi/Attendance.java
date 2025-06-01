/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package newUi;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.formdev.flatlaf.FlatLightLaf;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import java.awt.Color;
import java.awt.Font;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import net.proteanit.sql.DbUtils;

import java.sql.DriverManager;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;



import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFileChooser;

/**
 *
 * @author DAMIDU
 */
public class Attendance extends javax.swing.JFrame {

    
    public Attendance() {
        try {
            // Set FlatLaf Look and Feel (light theme) globally for all components
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        initComponents();
        conn= DBconnect.connect();
       load_attData();
      openSerialPort();
      
             
    }
    
    Connection conn=null;
    PreparedStatement pst=null;
    ResultSet rs=null;
    private SerialPort comPort;
public void load_attData(){
 try {
            String sql= "select fac from fac";
            pst =conn.prepareStatement(sql);
            rs= pst.executeQuery();
            while(rs.next()){
             txtfac.addItem(rs.getString("fac"));
            }
            txtfac.setSelectedIndex(-1);
        } catch (SQLException ex) {
           
        }



}

public  void search_co(){
String se_code=txtcoCode.getText();
   searchResult.removeAllItems();
   

try {
    
            String sql= "SELECT * FROM `course` WHERE co_id LIKE ?";
            pst =conn.prepareStatement(sql);
            pst.setString(1, se_code + "%");
            rs= pst.executeQuery();
            while(rs.next()){
             searchResult.addItem(rs.getString("co_id"));
            }
            searchResult.setSelectedIndex(0);
        } catch (Exception e) {
                      JOptionPane.showMessageDialog(null,e);
                  }



}
//after search we get course id then we can enter data auto
public void aftersearch(){
String co_code=txtcoCode.getText();
    
try {
  
            String sql= "SELECT bg_no FROM room_allocation INNER JOIN course ON room_allocation.co_id = course.co_id WHERE room_allocation.co_id=?";
            pst =conn.prepareStatement(sql);
            pst.setString(1, co_code);
            rs= pst.executeQuery();
            while(rs.next()){
             txtautobg.addItem(rs.getString("bg_no"));
            }
            
        } catch (Exception e) {
                      JOptionPane.showMessageDialog(null,e);
                  }

 DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyy/MM/dd");
 LocalDateTime now=LocalDateTime.now();
 String date=dtf.format(now);
 txtdatenow.setText(date);
 
 //SELECT max(att_l_no) FROM attendance WHERE co_id="01" and bg_no="01";
 //need to impliment this also automate ger att_l_no
}


public void At_table_1stsave(){
    String coCode=txtcoCode.getText();
    int attLNo = Integer.parseInt(txtregno1.getText());
    int bgNo = Integer.parseInt((String)txtautobg.getSelectedItem());
    String attLDate = txtdatenow.getText();
    try {
         
         int stdStatus = 0;
         
             //SELECT std.std_id FROM std INNER JOIN std_co_reg ON std.std_id=std_co_reg.std_id WHERE std_co_reg.co_id="CSH321222" and std.bg_no=20;
             String sql = "INSERT INTO attendance(co_id,std_id,att_l_no,bg_no,std_status,att_l_date) SELECT ?,std.std_id,?,?,?,? FROM std INNER JOIN std_co_reg ON std.std_id=std_co_reg.std_id WHERE std_co_reg.co_id=?and std.bg_no=?";
             pst =conn.prepareStatement(sql);
           
            
            pst.setString(1, coCode);
            pst.setInt(2, attLNo);
            pst.setInt(3, bgNo);
            pst.setInt(4, stdStatus);
            pst.setString(5, attLDate);
            pst.setString(6, coCode);
            pst.setInt(7, bgNo);
            
            pst.executeUpdate();
           
          
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,e);
            JOptionPane.showMessageDialog(null,"you need to create new one");
             jTabbedPane1.setSelectedIndex(0);
             
            return;
        }
    jTabbedPane1.setSelectedIndex(1);
       tablelRefesh();
       
//load 3rd page details

jLabel11.setText(coCode);
jLabel17.setText(String.valueOf(bgNo));
jLabel21.setText(attLDate);
//semester set
try {
          
             //SELECT std.std_id FROM std INNER JOIN std_co_reg ON std.std_id=std_co_reg.std_id WHERE std_co_reg.co_id="CSH321222" and std.bg_no=20;
            String sql="SELECT sem FROM course where co_id=? ";
            pst =conn.prepareStatement(sql);
            pst.setString(1, coCode);
           
         
            rs= pst.executeQuery();
            while(rs.next()){
                jLabel15.setText(rs.getString("sem"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,e);
        }
//l_no
jLabel29.setText(String.valueOf(attLNo));
//total student in course
int rowCount = att_dataTable.getRowCount();
jLabel19.setText(String.valueOf(rowCount));

}

public void presentcount(){

 try {
            String coCode=txtcoCode.getText();
         int attLNo = Integer.parseInt(txtregno1.getText());
         int bgNo = Integer.parseInt((String)txtautobg.getSelectedItem());
          
           // String sql="SELECT COUNT(SELECT std_status FROM attendance where co_id=? and att_l_no=? and bg_no=?) FROM attendance";
            String sql="SELECT COUNT(std_status) AS count FROM attendance WHERE co_id = ? AND att_l_no = ? AND bg_no = ? AND std_status=1";
            pst =conn.prepareStatement(sql);
            pst.setString(1, coCode);
            pst.setInt(2, attLNo);
            pst.setInt(3, bgNo);
         
            rs= pst.executeQuery();
            while(rs.next()){
                jLabel20.setText(rs.getString("count"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,e);
        }
}
public void tablelRefesh(){
 try {
            String coCode=txtcoCode.getText();
         int attLNo = Integer.parseInt(txtregno1.getText());
         int bgNo = Integer.parseInt((String)txtautobg.getSelectedItem());
          
            String sql="SELECT std_id,std_status FROM attendance where co_id=? and att_l_no=? and bg_no=?;";
            pst =conn.prepareStatement(sql);
            pst.setString(1, coCode);
            pst.setInt(2, attLNo);
            pst.setInt(3, bgNo);
         
            rs= pst.executeQuery();
            att_dataTable.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,e);
        }
}

public void present(){
    //UPDATE attendance SET std_status = '1' WHERE attendance.co_id =? AND attendance.std_id =? AND attendance.att_l_no = ? AND attendance.bg_no = ?;
int selectedRow = att_dataTable.getSelectedRow();
if (selectedRow != -1) {
    String std_id = (String) att_dataTable.getValueAt(selectedRow, 0);
   
    
    try {
            String coCode=txtcoCode.getText();
         int attLNo = Integer.parseInt(txtregno1.getText());
         int bgNo = Integer.parseInt((String)txtautobg.getSelectedItem());
          
            String sql="UPDATE attendance SET std_status = '1' WHERE attendance.co_id =? AND attendance.std_id =? AND attendance.att_l_no = ? AND attendance.bg_no = ?";
            pst =conn.prepareStatement(sql);
            pst.setString(1, coCode);
            pst.setString(2, std_id);
            pst.setInt(3, attLNo);
            pst.setInt(4, bgNo);
         
            pst.executeUpdate();
            tablelRefesh();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,e);
        }
    
    
}
 else { JOptionPane.showMessageDialog(null,"Not Student selected, plz selecte");; }
}
public void absent(){
int selectedRow = att_dataTable.getSelectedRow();
if (selectedRow != -1) {
    String std_id = (String) att_dataTable.getValueAt(selectedRow, 0);
   
    
    try {
            String coCode=txtcoCode.getText();
         int attLNo = Integer.parseInt(txtregno1.getText());
         int bgNo = Integer.parseInt((String)txtautobg.getSelectedItem());
          
            String sql="UPDATE attendance SET std_status = '0' WHERE attendance.co_id =? AND attendance.std_id =? AND attendance.att_l_no = ? AND attendance.bg_no = ?";
            pst =conn.prepareStatement(sql);
            pst.setString(1, coCode);
            pst.setString(2, std_id);
            pst.setInt(3, attLNo);
            pst.setInt(4, bgNo);
         
            pst.executeUpdate();
            tablelRefesh();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,e);
        }
    
    
}
 else { JOptionPane.showMessageDialog(null,"Not Student selected, plz selecte");; }
}
public void show_studentId(){
    int selectedRow = att_dataTable.getSelectedRow();
    if (selectedRow != -1) {
    String std_id = (String) att_dataTable.getValueAt(selectedRow, 0);
    Select_S_id.setText(std_id);
    }
}
//************************************************************************************************
    //Qr scanner part
//************************************************************************************************

////////////////////////////////////////////////////////////////////////////////////////
//finger print/////////////////////////////////////////////////////////////////////////



public void fingPrint_present(String i){
 String std_id = i;
    try {
            String coCode=txtcoCode.getText();
         int attLNo = Integer.parseInt(txtregno1.getText());
         int bgNo = Integer.parseInt((String)txtautobg.getSelectedItem());
        
            String sql="UPDATE attendance SET std_status = '1' WHERE attendance.co_id =? AND attendance.std_id =? AND attendance.att_l_no = ? AND attendance.bg_no = ?";
            pst =conn.prepareStatement(sql);
            pst.setString(1, coCode);
            pst.setString(2, std_id);
            pst.setInt(3, attLNo);
            pst.setInt(4, bgNo);
         
            pst.executeUpdate();
            tablelRefesh();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,e);
        }
}

//----------------------------------------------------------------------------------------------------------------------------------------
//--Ardoino----------------------------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------
    private void openSerialPort() {
        // Adjust to your COM port (e.g., "COM11") and baud rate (e.g., 9600)
        comPort = SerialPort.getCommPort("COM7");
        comPort.setBaudRate(9600);
        
        if (comPort.openPort()) {
            System.out.println("Port opened successfully!");
            // Add a data listener for asynchronous reading
            comPort.addDataListener(new SerialPortDataListener() {
                @Override
                public int getListeningEvents() {
                    // Listen for data available event
                    return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
                }
                @Override
                public void serialEvent(SerialPortEvent event) {
                    if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
                        return;
                    
                    byte[] newData = new byte[comPort.bytesAvailable()];
                    int numRead = comPort.readBytes(newData, newData.length);
                    String receivedData = new String(newData, 0, numRead);
                    System.out.println(receivedData);
                    
                    // Update the UI safely on the Event Dispatch Thread
                    java.awt.EventQueue.invokeLater(() -> {
                        jLabel10.setText(receivedData);
                    });
                   
                }
            });
        } else {
            JOptionPane.showMessageDialog(this, "Failed to open port.");
        }
    }
 



private void checkmysqldb() {
      try {

        String x = jLabel10.getText();
String numeric = x.replaceAll("[^0-9]", ""); // Removes all non-numeric characters in fingerpint id
int number = Integer.parseInt(numeric);
String result = String.valueOf(number);
String room=ro_id.getText();
try {
                    
        String sql = "SELECT std_id FROM fingerprint WHERE fin_id = ? and room_id=?";
        pst = conn.prepareStatement(sql);
        pst.setString(1, result);
        pst.setString(2, room);
} catch (Exception e) {
            e.printStackTrace();
           JOptionPane.showMessageDialog(this,e.getMessage());
        }
        rs = pst.executeQuery();

        if (rs.next()) { // If a std_id exists then check attendance table there are recode exist or not
                      String coCode = txtcoCode.getText();
                      int attLNo =Integer.parseInt(txtregno1.getText());
                      int bgNo = Integer.parseInt((String)txtautobg.getSelectedItem());
                      String stdID =  rs.getString("std_id");
                                try {
                            String sql = "SELECT * FROM attendance where co_id=? and att_l_no=? and bg_no=? and std_id=?;";
                            pst = conn.prepareStatement(sql);
                            pst.setString(1, coCode);
                            pst.setInt(2, attLNo);
                            pst.setInt(3, bgNo);
                            pst.setString(4, stdID);
                            } catch (Exception e) {
                                e.printStackTrace();
                                JOptionPane.showMessageDialog(this,e.getMessage());
                            }
                            rs = pst.executeQuery();
                            if (rs.next()) {//if that value exit in attendance table you can update
                                             try {
                     
                                            String sql = "UPDATE attendance SET std_status = 1 WHERE co_id = ? AND att_l_no = ? AND bg_no = ? AND std_id = ? AND std_status = 0;";
                                            pst = conn.prepareStatement(sql);
                                            pst.setString(1, coCode);
                                            pst.setInt(2, attLNo);
                                            pst.setInt(3, bgNo);
                                            pst.setString(4, stdID);
                                            //Thread.sleep(2000); 
                                            // Use executeUpdate() for update queries
                                            int rowsAffected = pst.executeUpdate();
                                            if (rowsAffected > 0) {
                                                
                                                jLabel32.setText(stdID + " Marked.");
                                                 
                                                //JOptionPane.showMessageDialog(null, stdID + " marked.");
                                            } else {
                                                jLabel32.setText(stdID +" Marked");
                                            }

                                            } catch (Exception e) {
                                                JOptionPane.showMessageDialog(null, "ERROR IN Marking DB");
                                            }
                            }else{
                               jLabel32.setText("Not registered for this course");
                            }
            
            
            
            
            
                           
            
            
            
            
          
            System.out.println("finger id is_"+result+"_Student Name: " + rs.getString("std_id"));
             // Stop the Timer (ensure it's implemented correctly)
        } else {
            System.out.println("finger id is_"+result+"_No record found for the given fingerprint.");
        }
    } catch (SQLException e) {
        System.err.println("Database error: " + e.getMessage());
    } catch (Exception e) {
        
    }
}


    private void getstatditecton(){
    startMethod();
        if (comPort != null && comPort.isOpen()) {
        try {
            OutputStream out = comPort.getOutputStream();
            String dataToSend = "d";  // For example, send the character 'e'
            out.write(dataToSend.getBytes());  // Send the data as bytes
            out.flush();  // Ensure the data is sent immediately
            System.out.println("Sent: " + dataToSend);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error sending data: " + e.getMessage());
        }
    } else {
        JOptionPane.showMessageDialog(this, "Port is not open.");
    }
    
    }
    
    

    // Call this method if you ever want to close the port
    private void closeSerialPort() {
        if (comPort != null && comPort.isOpen()) {
            comPort.closePort();
            System.out.println("Port closed.");
        }
    }
    Timer timer;
     public void startMethod() {
        if (timer == null) { // Ensure the timer runs only once
            timer = new Timer(2000, new ActionListener() {
                
                public void actionPerformed(ActionEvent e) {
                    checkmysqldb();
                   // Call the desired method repeatedly
                }
            });
            timer.start();
        }
    }
     public void stopMethod() {
         
    if (timer != null) { // Check if the timer exists
        timer.stop(); // Stop the timer
        timer = null; // Optionally set the timer to null to clean up
        System.out.println("Timer has been stopped.");
       closeSerialPort();
    }
}
  public static String readFileContent(String filePath) {
    File file = new File(filePath);

    if (file.exists()) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString().trim(); // Returning file content
    }

    return null; // File does not exist
}
   
     

//////////////////////////////////////////////////////////////////////////////////
     ///report generating 

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        adminlable = new javax.swing.JLabel();
        stdbtn = new javax.swing.JLabel();
        lecLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        imageside = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        ad1 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtcoCode = new javax.swing.JTextField();
        getattendance = new javax.swing.JLabel();
        txtfac = new javax.swing.JComboBox<>();
        jLabel26 = new javax.swing.JLabel();
        txtregno1 = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        txtdatenow = new javax.swing.JTextField();
        searchbtn = new javax.swing.JButton();
        searchResult = new javax.swing.JComboBox<>();
        txtautobg = new javax.swing.JComboBox<>();
        jButton4 = new javax.swing.JButton();
        ro_id = new javax.swing.JTextField();
        jLabel34 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        ad2 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jPanel5_SD2 = new javax.swing.JPanel();
        jToggleButton3 = new javax.swing.JToggleButton();
        jToggleButton4 = new javax.swing.JToggleButton();
        student = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        txtabsentbtn = new javax.swing.JToggleButton();
        txtpresentbtn = new javax.swing.JToggleButton();
        Select_S_id = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        att_dataTable = new javax.swing.JTable();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        admin = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jButton8 = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setPreferredSize(new java.awt.Dimension(1600, 1000));
        setResizable(false);

        jPanel2.setBackground(new java.awt.Color(0, 102, 102));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        adminlable.setBackground(new java.awt.Color(255, 255, 255));
        adminlable.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        adminlable.setForeground(new java.awt.Color(0, 153, 153));
        adminlable.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        adminlable.setText("Admin");
        adminlable.setToolTipText("");
        adminlable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                adminlableMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                adminlableMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                adminlableMouseExited(evt);
            }
        });
        jPanel2.add(adminlable, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 280, 200, 70));

        stdbtn.setBackground(new java.awt.Color(0, 102, 102));
        stdbtn.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        stdbtn.setForeground(new java.awt.Color(0, 153, 153));
        stdbtn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        stdbtn.setText("Students");
        stdbtn.setToolTipText("");
        stdbtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                stdbtnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                stdbtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                stdbtnMouseExited(evt);
            }
        });
        jPanel2.add(stdbtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 630, 200, 70));

        lecLabel7.setBackground(new java.awt.Color(0, 102, 102));
        lecLabel7.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        lecLabel7.setForeground(new java.awt.Color(0, 153, 153));
        lecLabel7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lecLabel7.setText("Lecturer");
        lecLabel7.setToolTipText("");
        lecLabel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lecLabel7MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lecLabel7MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lecLabel7MouseExited(evt);
            }
        });
        jPanel2.add(lecLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 390, 200, 70));

        jLabel9.setBackground(new java.awt.Color(0, 102, 102));
        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel9.setText("Attendance");
        jLabel9.setToolTipText("");
        jLabel9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel9MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabel9MouseExited(evt);
            }
        });
        jPanel2.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 510, -1, 70));

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(9, 153, 153));
        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/exit.png"))); // NOI18N
        jLabel13.setText(" Exit");
        jLabel13.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jLabel13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel13MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel13MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabel13MouseExited(evt);
            }
        });
        jPanel2.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 770, 186, 72));

        imageside.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/222.gif"))); // NOI18N
        imageside.setText("jLabel56");
        jPanel2.add(imageside, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 280, 1000));

        jPanel1.setBackground(new java.awt.Color(204, 204, 0));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        ad1.setBackground(new java.awt.Color(255, 255, 255));
        ad1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 102), 3), "Attendance Section", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 13), new java.awt.Color(0, 102, 102))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 102, 102));
        jLabel1.setText("Attendance Details");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 102, 102));
        jLabel2.setText("Batch No");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 102, 102));
        jLabel3.setText("Course Code");

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 102, 102));
        jLabel5.setText("Faculty");

        getattendance.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        getattendance.setForeground(new java.awt.Color(0, 102, 102));
        getattendance.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/ok1.png"))); // NOI18N
        getattendance.setText("   Enter Details Checked");
        getattendance.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                getattendanceMouseClicked(evt);
            }
        });

        txtfac.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtfacActionPerformed(evt);
            }
        });

        jLabel26.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(0, 102, 102));
        jLabel26.setText("Lession No");

        jLabel27.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(0, 102, 102));
        jLabel27.setText("Lession Date");

        searchbtn.setText("Search");
        searchbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchbtnActionPerformed(evt);
            }
        });

        searchResult.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                searchResultMouseExited(evt);
            }
        });
        searchResult.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchResultActionPerformed(evt);
            }
        });

        txtautobg.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                txtautobgMouseExited(evt);
            }
        });
        txtautobg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtautobgActionPerformed(evt);
            }
        });

        jButton4.setText("Auto");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        ro_id.setText("R1");
        ro_id.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ro_idActionPerformed(evt);
            }
        });

        jLabel34.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel34.setForeground(new java.awt.Color(0, 102, 102));
        jLabel34.setText("Room Id");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(322, 322, 322))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(getattendance, javax.swing.GroupLayout.PREFERRED_SIZE, 322, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(298, 298, 298))))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(294, 294, 294)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtautobg, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(180, 180, 180)
                        .addComponent(txtdatenow, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(180, 180, 180)
                        .addComponent(txtregno1, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel3))
                                .addGap(180, 180, 180))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel34)
                                .addGap(211, 211, 211)))
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtcoCode, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
                            .addComponent(txtfac, 0, 222, Short.MAX_VALUE)
                            .addComponent(ro_id))))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(searchbtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(29, 29, 29)
                .addComponent(searchResult, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(49, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addComponent(jLabel1)
                .addGap(33, 33, 33)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ro_id, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel34))
                .addGap(29, 29, 29)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtcoCode, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3)
                        .addComponent(searchbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(searchResult, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtfac, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(42, 42, 42)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtdatenow, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27))
                .addGap(34, 34, 34)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtregno1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26))
                .addGap(29, 29, 29)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtautobg, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(58, 58, 58)
                .addComponent(getattendance)
                .addGap(71, 71, 71))
        );

        txtfac.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                java.awt.Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    c.setBackground(new java.awt.Color(0, 153, 153)); // #009999 color
                }
                return c;
            }
        });
        searchResult.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                java.awt.Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    c.setBackground(new java.awt.Color(0, 153, 153)); // #009999 color
                }
                return c;
            }
        });
        txtautobg.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                java.awt.Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    c.setBackground(new java.awt.Color(0, 153, 153)); // #009999 color
                }
                return c;
            }
        });

        ad1.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 90, 1120, 770));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 102, 102));
        jLabel4.setText("1");
        ad1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 880, -1, -1));

        jTabbedPane1.addTab("1", ad1);

        ad2.setBackground(new java.awt.Color(255, 255, 255));
        ad2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(0, 102, 102));
        jLabel16.setText("2");
        ad2.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 880, -1, -1));

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(0, 102, 102));
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/backbtn.png"))); // NOI18N
        jLabel22.setText("Back");
        jLabel22.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jLabel22.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel22MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel22MouseEntered(evt);
            }
        });
        ad2.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 860, -1, -1));

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(0, 102, 102));
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel23.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/nextbtn.png"))); // NOI18N
        jLabel23.setText("Next");
        jLabel23.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLabel23.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel23MouseClicked(evt);
            }
        });
        ad2.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(1080, 860, -1, -1));

        jPanel5_SD2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5_SD2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 102), 3), "Attendance  Mode", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 13), new java.awt.Color(0, 102, 102))); // NOI18N

        jToggleButton3.setBackground(new java.awt.Color(255, 255, 255));
        jToggleButton3.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jToggleButton3.setForeground(new java.awt.Color(0, 102, 102));
        jToggleButton3.setText("Manual Marking");
        jToggleButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton3ActionPerformed(evt);
            }
        });

        jToggleButton4.setBackground(new java.awt.Color(255, 255, 255));
        jToggleButton4.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jToggleButton4.setForeground(new java.awt.Color(0, 102, 102));
        jToggleButton4.setText("Finger Print Marking");
        jToggleButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5_SD2Layout = new javax.swing.GroupLayout(jPanel5_SD2);
        jPanel5_SD2.setLayout(jPanel5_SD2Layout);
        jPanel5_SD2Layout.setHorizontalGroup(
            jPanel5_SD2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5_SD2Layout.createSequentialGroup()
                .addGap(346, 346, 346)
                .addGroup(jPanel5_SD2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jToggleButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 410, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jToggleButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 410, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(340, Short.MAX_VALUE))
        );
        jPanel5_SD2Layout.setVerticalGroup(
            jPanel5_SD2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5_SD2Layout.createSequentialGroup()
                .addContainerGap(187, Short.MAX_VALUE)
                .addComponent(jToggleButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(66, 66, 66)
                .addComponent(jToggleButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(292, 292, 292))
        );

        ad2.add(jPanel5_SD2, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 110, 1110, 720));

        jTabbedPane1.addTab("2", ad2);

        student.setBackground(new java.awt.Color(255, 255, 255));
        student.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(0, 102, 102));
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel24.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/backbtn.png"))); // NOI18N
        jLabel24.setText("Back");
        jLabel24.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jLabel24.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel24MouseClicked(evt);
            }
        });
        student.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 860, -1, -1));

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(0, 102, 102));
        jLabel18.setText("3");
        student.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 880, -1, -1));

        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(0, 102, 102));
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/nextbtn.png"))); // NOI18N
        jLabel25.setText("Next");
        jLabel25.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLabel25.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel25MouseClicked(evt);
            }
        });
        student.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(1080, 860, -1, -1));

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 102), 3), "Attendance Mark Section", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 18), new java.awt.Color(0, 102, 102))); // NOI18N

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 102, 102), 2, true));

        txtabsentbtn.setBackground(new java.awt.Color(255, 255, 255));
        txtabsentbtn.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        txtabsentbtn.setForeground(new java.awt.Color(204, 0, 0));
        txtabsentbtn.setText("Mark as Absent");
        txtabsentbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtabsentbtnActionPerformed(evt);
            }
        });

        txtpresentbtn.setBackground(new java.awt.Color(255, 255, 255));
        txtpresentbtn.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        txtpresentbtn.setForeground(new java.awt.Color(0, 153, 0));
        txtpresentbtn.setText("Mark AS Present");
        txtpresentbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtpresentbtnActionPerformed(evt);
            }
        });

        Select_S_id.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        Select_S_id.setForeground(new java.awt.Color(0, 102, 102));
        Select_S_id.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Select_S_id.setText("NOT Selecte Yet");
        Select_S_id.setToolTipText("");
        Select_S_id.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 102)));
        Select_S_id.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel31.setBackground(new java.awt.Color(255, 255, 255));
        jLabel31.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(0, 102, 102));
        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setText("Select Student And Mark");
        jLabel31.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtabsentbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtpresentbtn)
                            .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addComponent(Select_S_id, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(Select_S_id, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38)
                .addComponent(txtpresentbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(txtabsentbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(99, 99, 99))
        );

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 102, 102), 2, true));

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 102, 102));
        jLabel8.setText("Total Std Count");

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(0, 102, 102));
        jLabel12.setText("Batch No");

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(0, 102, 102));
        jLabel14.setText("Present Std count");

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 102, 102));
        jLabel6.setText("Semester");

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(0, 102, 102));
        jLabel11.setText("Course Code");

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(0, 102, 102));
        jLabel15.setText("Semester");

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 102, 102));
        jLabel7.setText("Course Code");

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(0, 102, 102));
        jLabel17.setText("Batch No");

        jLabel19.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(0, 102, 102));
        jLabel19.setText("Total Student count");

        jLabel20.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(0, 102, 102));
        jLabel20.setText("0");

        jLabel21.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(0, 102, 102));
        jLabel21.setText("Date");

        jLabel28.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(0, 102, 102));
        jLabel28.setText("Date");

        jLabel29.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(0, 102, 102));
        jLabel29.setText("Lesion No");

        jLabel30.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(0, 102, 102));
        jLabel30.setText("Lesion No");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(34, 34, 34))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addGap(73, 73, 73)
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(22, 22, 22))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        att_dataTable.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        att_dataTable.setForeground(new java.awt.Color(0, 102, 102));
        att_dataTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Stusent ID", "Status"
            }
        ));
        att_dataTable.setGridColor(new java.awt.Color(0, 102, 102));
        att_dataTable.setSelectionBackground(new java.awt.Color(0, 153, 153));
        att_dataTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                att_dataTableMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(att_dataTable);

        jButton2.setBackground(new java.awt.Color(255, 255, 255));
        jButton2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jButton2.setForeground(new java.awt.Color(0, 102, 102));
        jButton2.setText("Print Attendance");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(255, 255, 255));
        jButton3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jButton3.setForeground(new java.awt.Color(0, 102, 102));
        jButton3.setText("Get New ");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 404, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 396, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(15, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(96, 96, 96)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 304, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(47, 47, 47)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(50, 50, 50)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 572, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(41, Short.MAX_VALUE))
        );

        student.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 80, 1110, 740));

        jTabbedPane1.addTab("3", student);

        admin.setBackground(new java.awt.Color(255, 255, 255));
        admin.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 102), 3), "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 13), new java.awt.Color(0, 102, 102))); // NOI18N

        jButton5.setBackground(new java.awt.Color(255, 255, 255));
        jButton5.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jButton5.setForeground(new java.awt.Color(0, 102, 102));
        jButton5.setText("Start Marking");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setBackground(new java.awt.Color(255, 255, 255));
        jButton6.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jButton6.setForeground(new java.awt.Color(0, 102, 102));
        jButton6.setText("See Attendance Sheet");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jButton7.setForeground(new java.awt.Color(0, 102, 102));
        jButton7.setText("stop");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 153, 153));
        jLabel10.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Sensor  Display", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 10), new java.awt.Color(0, 102, 102))); // NOI18N

        jButton8.setBackground(new java.awt.Color(255, 255, 255));
        jButton8.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jButton8.setForeground(new java.awt.Color(0, 102, 102));
        jButton8.setText("Try Again");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 102), 3));

        jLabel32.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel32.setForeground(new java.awt.Color(204, 0, 0));
        jLabel32.setText("Marked id will show this place");

        jLabel33.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/R2-ezgif.com-crop.gif"))); // NOI18N

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel33)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 418, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(170, 170, 170))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(jLabel33))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 367, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, 367, Short.MAX_VALUE)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                            .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 52, Short.MAX_VALUE)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 630, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(52, 52, 52)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(62, 62, 62)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(79, 79, 79)
                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(56, Short.MAX_VALUE))
        );

        admin.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 180, 1120, 550));

        jLabel43.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel43.setForeground(new java.awt.Color(0, 102, 102));
        jLabel43.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel43.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/backbtn.png"))); // NOI18N
        jLabel43.setText("Back");
        jLabel43.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jLabel43.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel43MouseClicked(evt);
            }
        });
        admin.add(jLabel43, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 860, -1, -1));

        jLabel44.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel44.setForeground(new java.awt.Color(0, 102, 102));
        jLabel44.setText("4");
        admin.add(jLabel44, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 880, -1, -1));

        jLabel45.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel45.setForeground(new java.awt.Color(0, 102, 102));
        jLabel45.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel45.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/nextbtn.png"))); // NOI18N
        jLabel45.setText("Next");
        jLabel45.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLabel45.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel45MouseClicked(evt);
            }
        });
        admin.add(jLabel45, new org.netbeans.lib.awtextra.AbsoluteConstraints(1080, 860, -1, -1));

        jLabel46.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        jLabel46.setForeground(new java.awt.Color(0, 102, 102));
        jLabel46.setText("Finger Print Scanner");
        admin.add(jLabel46, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 70, -1, -1));

        jTabbedPane1.addTab("4", admin);

        jPanel1.add(jTabbedPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -50, 1600, 1050));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 283, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 1162, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 1000, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel13MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel13MouseClicked
closeSerialPort();          System.exit(0);        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel13MouseClicked

    private void jLabel22MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel22MouseClicked
jTabbedPane1.setSelectedIndex(0);        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel22MouseClicked

    private void jLabel23MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel23MouseClicked
jTabbedPane1.setSelectedIndex(2);
    }//GEN-LAST:event_jLabel23MouseClicked

    private void jLabel24MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel24MouseClicked
 jTabbedPane1.setSelectedIndex(1);       // TODO add your handling code here:
    }//GEN-LAST:event_jLabel24MouseClicked

    private void jLabel25MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel25MouseClicked
       // TODO add your handling code here:
    }//GEN-LAST:event_jLabel25MouseClicked

    private void jLabel43MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel43MouseClicked
jTabbedPane1.setSelectedIndex(2);        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel43MouseClicked

    private void jLabel45MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel45MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel45MouseClicked

    private void adminlableMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_adminlableMouseEntered
adminlable.setForeground(Color.decode("#CCFFFF"));
adminlable.setFont(new Font(adminlable.getFont().getName(), adminlable.getFont().getStyle(), 50));// TODO add your handling code here:
    }//GEN-LAST:event_adminlableMouseEntered

    private void adminlableMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_adminlableMouseExited
adminlable.setForeground(Color.decode("#009999")); 
adminlable.setFont(new Font(adminlable.getFont().getName(), adminlable.getFont().getStyle(), 36));// TODO add your handling code here:
    }//GEN-LAST:event_adminlableMouseExited

    private void stdbtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_stdbtnMouseEntered
  stdbtn.setForeground(Color.decode("#CCFFFF"));
  stdbtn.setFont(new Font(stdbtn.getFont().getName(), stdbtn.getFont().getStyle(), 42));
  
// TODO add your handling code here:
    }//GEN-LAST:event_stdbtnMouseEntered

    private void stdbtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_stdbtnMouseExited
     stdbtn.setForeground(Color.decode("#009999"));
  stdbtn.setFont(new Font(stdbtn.getFont().getName(), stdbtn.getFont().getStyle(), 36));   // TODO add your handling code here:
    }//GEN-LAST:event_stdbtnMouseExited

    private void lecLabel7MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lecLabel7MouseEntered
lecLabel7.setForeground(Color.decode("#CCFFFF"));  
lecLabel7.setFont(new Font(stdbtn.getFont().getName(), stdbtn.getFont().getStyle(), 42));// TODO add your handling code here:
    }//GEN-LAST:event_lecLabel7MouseEntered

    private void lecLabel7MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lecLabel7MouseExited
  lecLabel7.setForeground(Color.decode("#009999"));      
  lecLabel7.setFont(new Font(stdbtn.getFont().getName(), stdbtn.getFont().getStyle(), 36));      
    }//GEN-LAST:event_lecLabel7MouseExited

    private void jLabel9MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel9MouseEntered
    jLabel9.setForeground(Color.decode("#CCFFFF"));
    jLabel9.setFont(new Font(stdbtn.getFont().getName(), stdbtn.getFont().getStyle(), 42));
    }//GEN-LAST:event_jLabel9MouseEntered

    private void jLabel9MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel9MouseExited
    jLabel9.setForeground(Color.decode("#FFFFFF"));
    jLabel9.setFont(new Font(stdbtn.getFont().getName(), stdbtn.getFont().getStyle(), 36)); 
    }//GEN-LAST:event_jLabel9MouseExited

    private void jLabel13MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel13MouseEntered
   jLabel13.setForeground(Color.decode("#CCFFFF"));
    jLabel13.setFont(new Font(stdbtn.getFont().getName(), stdbtn.getFont().getStyle(), 50));
    }//GEN-LAST:event_jLabel13MouseEntered

    private void jLabel13MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel13MouseExited
     jLabel13.setForeground(Color.decode("#009999"));
    jLabel13.setFont(new Font(stdbtn.getFont().getName(), stdbtn.getFont().getStyle(), 36));
    }//GEN-LAST:event_jLabel13MouseExited

    private void getattendanceMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_getattendanceMouseClicked
        At_table_1stsave();
       
 
    }//GEN-LAST:event_getattendanceMouseClicked

    private void txtfacActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtfacActionPerformed
    // TODO add your handling code here:
    }//GEN-LAST:event_txtfacActionPerformed

    private void jToggleButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton3ActionPerformed
      
        jTabbedPane1.setSelectedIndex(2);
// TODO add your handling code here:
    }//GEN-LAST:event_jToggleButton3ActionPerformed

    private void txtabsentbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtabsentbtnActionPerformed
absent();
presentcount();// TODO add your handling code here:
    }//GEN-LAST:event_txtabsentbtnActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
txtfac.setSelectedIndex(-1);
txtautobg.removeAllItems();
searchResult.removeAllItems();



        txtcoCode.setText("");
        txtdatenow.setText("");
        txtregno1.setText("");
        jTabbedPane1.setSelectedIndex(0);
 // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void searchbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchbtnActionPerformed
search_co();        // TODO add your handling code here:
    }//GEN-LAST:event_searchbtnActionPerformed

    private void searchResultMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchResultMouseExited
     // TODO add your handling code here:
    }//GEN-LAST:event_searchResultMouseExited

    private void searchResultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchResultActionPerformed
 txtcoCode.setText((String)searchResult.getSelectedItem());
 // TODO add your handling code here:
    }//GEN-LAST:event_searchResultActionPerformed

    private void txtautobgMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtautobgMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_txtautobgMouseExited

    private void txtautobgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtautobgActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtautobgActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
aftersearch();     // TODO add your handling code here:
    }//GEN-LAST:event_jButton4ActionPerformed

    private void txtpresentbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtpresentbtnActionPerformed
present();
presentcount();// TODO add your handling code here:
    }//GEN-LAST:event_txtpresentbtnActionPerformed

    private void att_dataTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_att_dataTableMouseClicked
 show_studentId();  // TODO add your handling code here:
    }//GEN-LAST:event_att_dataTableMouseClicked

    private void jLabel22MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel22MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel22MouseEntered

    private void adminlableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_adminlableMouseClicked
closeSerialPort();          this.hide();
        new LOGIN_TO_SYSTEM().setVisible(true);        // TODO add your handling code here:
    }//GEN-LAST:event_adminlableMouseClicked

    private void lecLabel7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lecLabel7MouseClicked
closeSerialPort();          this.hide();
        new LOGIN_TO_SYSTEM("lecturer").setVisible(true);        // TODO add your handling code here:
    }//GEN-LAST:event_lecLabel7MouseClicked

    private void stdbtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_stdbtnMouseClicked
closeSerialPort(); 
this.hide();
        new LOGIN_TO_SYSTEM("student").setVisible(true);        // TODO add your handling code here:
    }//GEN-LAST:event_stdbtnMouseClicked

    private void jToggleButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton4ActionPerformed
//At_table_1stsave();
jTabbedPane1.setSelectedIndex(3);          // TODO add your handling code here:
    }//GEN-LAST:event_jToggleButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        
        getstatditecton();  // openSerialPort();   
         // TODO add your handling code here:
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        tablelRefesh();
        presentcount();
        jTabbedPane1.setSelectedIndex(2);         // TODO add your handling code here:
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
stopMethod();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
jLabel10.setText("wait 15 second");
        openSerialPort();

try {
    Thread.sleep(15000); // Wait for 15 seconds (15000 milliseconds)
} catch (InterruptedException e) {
    e.printStackTrace();
}


        getstatditecton();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        Report r=new Report();
        r.generateAttendanceReport(jLabel11.getText(),Integer.parseInt(jLabel29.getText()),Integer.parseInt(jLabel17.getText())); 
// TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void ro_idActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ro_idActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ro_idActionPerformed
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Attendance.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Attendance.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Attendance.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Attendance.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Attendance().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Select_S_id;
    private javax.swing.JPanel ad1;
    private javax.swing.JPanel ad2;
    private javax.swing.JPanel admin;
    private javax.swing.JLabel adminlable;
    private javax.swing.JTable att_dataTable;
    private javax.swing.JLabel getattendance;
    private javax.swing.JLabel imageside;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel5_SD2;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JToggleButton jToggleButton3;
    private javax.swing.JToggleButton jToggleButton4;
    private javax.swing.JLabel lecLabel7;
    private javax.swing.JTextField ro_id;
    private javax.swing.JComboBox<String> searchResult;
    private javax.swing.JButton searchbtn;
    private javax.swing.JLabel stdbtn;
    private javax.swing.JPanel student;
    private javax.swing.JToggleButton txtabsentbtn;
    private javax.swing.JComboBox<String> txtautobg;
    private javax.swing.JTextField txtcoCode;
    private javax.swing.JTextField txtdatenow;
    private javax.swing.JComboBox<String> txtfac;
    private javax.swing.JToggleButton txtpresentbtn;
    private javax.swing.JTextField txtregno1;
    // End of variables declaration//GEN-END:variables
}
