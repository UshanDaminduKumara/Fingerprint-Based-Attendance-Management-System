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
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import net.proteanit.sql.DbUtils;


/**
 *
 * @author DAMIDU
 */
public class students extends javax.swing.JFrame {
public students(String logname) {
     try {
            // Set FlatLaf Look and Feel (light theme) globally for all components
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    initComponents();
    
        conn = DBconnect.connect();
        empty();
         jLabel17.setText("youer finger print inserted");
        detail_load_SD2();
       std_Reg_tableload_SD2();
        
        this.logname=logname;//this use for set valeu after log
        loadStd (logname);
         roomtable_load(logname);
       jPanel10.setVisible(false);  
     

        
        

}
    
    public students() {
         try {
            // Set FlatLaf Look and Feel (light theme) globally for all components
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        initComponents();
        
        conn = DBconnect.connect();
        empty();
          
          detail_load_SD2();
          std_Reg_tableload_SD2();
          jPanel10.setVisible(false);  
          jLabel20.setVisible(false);  
          
            openSerialPort();    
// Data for the chart
 
   
    }
    String logname;
    Connection conn=null;
    PreparedStatement pst=null;
    ResultSet rs=null;
    String Login_std_id=null;//need tochange only for now
    String Login_sem=null;

////1 page
  public void empty(){
        try {
            txtregno.setText("");
           txtname.setText("");
           txtfac.setSelectedIndex(-1);
           txtdeg.setSelectedIndex(-1);
           txtname.setText("");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,e);
        }
        
    }  
      //insert data in to database
      public void insert(){
          
          String std_reg;
          String std_name;
          String std_fac;
          String std_deg;
          int std_bach;
          
          //befor entering data into data base chack whether data is valid or not
         if(txtregno.getText().isEmpty() || txtname.getText().isEmpty()|| txtbach.getText().isEmpty() ||txtfac.getSelectedItem()==null ||txtdeg.getSelectedItem()==null ){
              JOptionPane.showMessageDialog(null,"Check again some filed are empty");}
         
         else{
          std_reg =txtregno.getText();
          std_name =txtname.getText();
          std_fac=txtfac.getSelectedItem().toString();
          std_deg=txtdeg.getSelectedItem().toString();
         
          try{std_bach=Integer.parseInt(txtbach.getText());}//this code get error when user enter string value so that case using this we can stop errors
           catch(Exception e){JOptionPane.showMessageDialog(null,e);
                 return;//this use end method, if eny error in txtbach data
                }
                try {
                            //String x = jLabel17.getText();
                            //String numeric = x.replaceAll("[^0-9]", ""); // Removes all non-numeric characters
                            //int number = Integer.parseInt(numeric);
                            //String fingerprintid = String.valueOf(number);
                    
                    //String fingerprintid=null;
                    //String fingerprintid =jLabel17.getText();
                      String sql="insert into std (std_id,std_name,bg_no,fac,deg) values('"+std_reg+"','"+std_name+"','"+std_bach+"','"+std_fac+"','"+std_deg+"')";//
                      pst =conn.prepareStatement(sql);
                      pst.execute();
                      
                      JOptionPane.showMessageDialog(null,"INSERTED");
                      jLabel17.setText("youer finger print inserted");
                  } catch (Exception e) {
                      JOptionPane.showMessageDialog(null,e);
                  }
         }
         
         
      }
      public void fingerprintInsert(){
                
                String std_reg =txtregno.getText(); 
                String room_id=txtroom.getText();
                    //need to implement new method for add finger print
              try {
                            
                  
                            String x =jLabel17.getText();
                            String numeric = x.replaceAll("[^0-9]", ""); // Removes all non-numeric characters
                            int number = Integer.parseInt(numeric);
                            String fingerprintid = String.valueOf(number);
                    
                    
                   
                      String sql="INSERT INTO fingerprint (std_id, room_id, fin_id) VALUES (?, ?, ?)";
                      pst =conn.prepareStatement(sql);
                 pst.setString(1, std_reg);
                 pst.setString(2, room_id);
                 pst.setString(3,fingerprintid );
                 pst.executeUpdate();
                      
                      JOptionPane.showMessageDialog(null,"INSERTED");
                      jLabel17.setText("youer finger print inserted");
                  } catch (Exception e) {
                      JOptionPane.showMessageDialog(null,e);
                  }       
      }
      
      //
       public void roomtable_load(String stdid){
         
                try {
                 String sql="SELECT room_id as Room_Id FROM `fingerprint` WHERE std_id =?";
                 pst =conn.prepareStatement(sql);
                 pst.setString(1,stdid);
           
            rs= pst.executeQuery();
            jTable1.setModel(DbUtils.resultSetToTableModel(rs));
                  
              } catch (Exception e) {
                  JOptionPane.showMessageDialog(null, e);
              }
                
          }
      //fingerprint
       SerialPort comPort;
      public void openSerialPort() {
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
                        jLabel17.setText(receivedData);
                    });
                }
            });
        } else {
            JOptionPane.showMessageDialog(this, "Failed to open port.");
        }
    }
      public void closeSerialPort() {
        if (comPort != null && comPort.isOpen()) {
            comPort.closePort();
            System.out.println("Port closed.");
        }
    }
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
     public void  loadStd (String std_id){
        txtregno.setText(std_id);
         System.out.println(std_id);
  
    try {
            String sql="SELECT * FROM std WHERE std_id=?";
            pst =conn.prepareStatement(sql);
          
            pst.setString(1, std_id);
            rs= pst.executeQuery();
            
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {


                String stdName = rs.getString("std_name");
                txtname.setText(stdName);

                int bg_no = rs.getInt("bg_no");
                txtbach.setText(String.valueOf(bg_no));

                String fac = rs.getString("fac");
                for (int i = 0; i < txtfac.getItemCount(); i++) {
                        if(txtfac.getItemAt(i).equals(fac)){
                                txtfac.setSelectedIndex(i);
                        }
                }
                String deg = rs.getString("deg");
                for (int i = 0; i < txtdeg.getItemCount(); i++) {
                        if(txtdeg.getItemAt(i).equals(deg)){
                                txtdeg.setSelectedIndex(i);
                        }
                }
               
            }
        } catch (SQLException e) {JOptionPane.showMessageDialog(null, e);
        } 
     }
      //for finger print insert

 
      /////////////////////////////////////////////
      ///page2
      public void tableload_SD2(){
         int bach=Integer.parseInt(txtbach.getText());
         String fac=(String)txtfac.getSelectedItem();
                 
         
                  
       try {
            String sql="SELECT onging_sem from bg where bg_no='"+bach+"' and fac='"+fac+"';";
            pst =conn.prepareStatement(sql);
            rs= pst.executeQuery();
          while(rs.next()){
             
             txtsem_SD2.setText(rs.getString("onging_sem"));
            }
        } catch (Exception e) {JOptionPane.showMessageDialog(null, e);
        }           
        Login_sem=txtsem_SD2.getText();        
          try {
            String sql="SELECT co_id as course_code,co_name as course_name FROM course INNER JOIN bg ON course.sem = bg.onging_sem WHERE bg.onging_sem='"+Login_sem+"';";
            pst =conn.prepareStatement(sql);
            rs= pst.executeQuery();
            txttable2_SD2.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {JOptionPane.showMessageDialog(null, e);
        }
    }
      public void std_Reg_tableload_SD2(){
         Login_std_id= txtregno.getText();
         txtsid_SD2.setText(Login_std_id);
       try {
            String sql="SELECT co_id as course_code FROM std_co_reg WHERE std_id='"+Login_std_id+"';";
            pst =conn.prepareStatement(sql);
            rs= pst.executeQuery();
            txttable_SD2.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {JOptionPane.showMessageDialog(null, e);
        }
    }
      
      public void insert_std_co_reg_SD2(){
         
          String coid=txtseleco_SD2.getText();
          String stdid=txtsid_SD2.getText();
         
       
         
                try {
                 String sql = "INSERT INTO std_co_reg (std_id, co_id) VALUES (?, ?)";
                  
                 pst =conn.prepareStatement(sql);
                 pst.setString(1, stdid);
                 pst.setString(2, coid);
                 pst.executeUpdate();

                  std_Reg_tableload_SD2();
                  JOptionPane.showMessageDialog(null, "successfully added");
              } catch (Exception e) {
                  JOptionPane.showMessageDialog(null, e);
              }
                
          }
      
      
       //fac_load use for loading data to combobox
      public void detail_load_SD2(){
             txtsid_SD2.setText(Login_std_id);
             txtsem_SD2.setText(Login_sem);
    }
      
       public void chartDataget(){
         List<String> courses =  new ArrayList<>();
         List<Double> attendance=new ArrayList<>();

       
         txtsid_SD2.setText(Login_std_id);
       try {
            //String sql="SELECT co_id as course_code FROM std_co_reg WHERE std_id='"+Login_std_id+"';";
           // String sql2="SELECT co_id, COUNT(*) AS total_occurrences, SUM(CASE WHEN std_status = 1 THEN 1 ELSE 0 END) AS count_status_1, ROUND(SUM(CASE WHEN std_status = 1 THEN 1 ELSE 0 END) * 100.0 / COUNT(*)) AS percentage_status_1 FROM attendance WHERE std_id = '"+Login_std_id+"' GROUP BY co_id";
            String sql23="SELECT a.co_id, COUNT(*) AS total_occurrences, SUM(CASE WHEN a.std_status = 1 THEN 1 ELSE 0 END) AS count_status_1, (SUM(CASE WHEN a.std_status = 1 THEN 1 ELSE 0 END) * 100.0 / COUNT(*)) AS percentage_status_1 FROM attendance a JOIN course c ON a.co_id = c.co_id JOIN bg b ON c.sem = b.onging_sem WHERE a.std_id ='"+Login_std_id+"'GROUP BY a.co_id LIMIT 0, 15";
            pst =conn.prepareStatement(sql23);
            
            rs= pst.executeQuery();
            while (rs.next()) {
                courses.add(rs.getString("co_id"));
                attendance.add(rs.getDouble("percentage_status_1"));
                
                
            }
        } catch (Exception e) {JOptionPane.showMessageDialog(null, e);
        }
       
       
           

    
        //List<Double> attendance = Arrays.asList( 64.0, 92.0, 76.8, 88.2);
        //
        //SELECT co_id, COUNT(*) AS total_occurrences, SUM(CASE WHEN std_status = 1 THEN 1 ELSE 0 END) AS count_status_1 FROM attendance WHERE std_id = 'ss' GROUP BY co_id LIMIT 25;
        // Use the ChartGenerator class to add the chart to jPanel6
        ChartGenerator chartGenerator = new ChartGenerator();
        chartGenerator.addAttendanceChart(Login_std_id,jPanel6, courses, attendance);
        
        
//chartGenerator.addAttendanceChart("ss",jPanel6, courses, attendance);
       
       
       }
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
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtname = new javax.swing.JTextField();
        txtregno = new javax.swing.JTextField();
        txtbach = new javax.swing.JTextField();
        txtfac = new javax.swing.JComboBox<>();
        txtdeg = new javax.swing.JComboBox<>();
        jPanel10 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel21 = new javax.swing.JLabel();
        txtroom = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton2 = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        ad2 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jPanel5_SD2 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txttable2_SD2 = new javax.swing.JTable();
        jButton4_SD2 = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        txttable_SD2 = new javax.swing.JTable();
        jLabel14 = new javax.swing.JLabel();
        txtsid_SD2 = new javax.swing.JTextField();
        txtsem_SD2 = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        txtseleco_SD2 = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        student = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();

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
        stdbtn.setForeground(new java.awt.Color(255, 255, 255));
        stdbtn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        stdbtn.setText("Students");
        stdbtn.setToolTipText("");
        stdbtn.addMouseListener(new java.awt.event.MouseAdapter() {
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
        jLabel9.setForeground(new java.awt.Color(0, 153, 153));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel9.setText("Attendance");
        jLabel9.setToolTipText("");
        jLabel9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel9MouseClicked(evt);
            }
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
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 102), 3), "Students Section", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 13), new java.awt.Color(0, 102, 102))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 102, 102));
        jLabel1.setText("Student Details");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 102, 102));
        jLabel2.setText("Student name");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 102, 102));
        jLabel3.setText("Student Reg.No");

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 102, 102));
        jLabel5.setText("Faculty");

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 102, 102));
        jLabel6.setText("Types of undergraduate degrees");

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 102, 102));
        jLabel7.setText("Batch No");

        txtfac.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Applied Sciences", "Arts & Culture", "Technology", "Management & Commerce", "Engineering", "Islamic Studies & Arabic" }));
        txtfac.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtfacActionPerformed(evt);
            }
        });

        txtdeg.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "General Degree", "Honors Degree" }));
        txtdeg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtdegActionPerformed(evt);
            }
        });

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/delet.png"))); // NOI18N
        jLabel10.setToolTipText("Delete");
        jLabel10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel10MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel10MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabel10MouseExited(evt);
            }
        });

        jLabel47.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/edit.png"))); // NOI18N
        jLabel47.setToolTipText("Update");
        jLabel47.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel47MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel47MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabel47MouseExited(evt);
            }
        });

        jLabel29.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(0, 102, 102));
        jLabel29.setText("Delect All Student Deatiles ");

        jLabel30.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(0, 102, 102));
        jLabel30.setText("Update Deatiles");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(55, 55, 55)
                .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel30)
                .addGap(211, 211, 211)
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel29)
                .addContainerGap(19, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel47, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel10Layout.createSequentialGroup()
                            .addGap(8, 8, 8)
                            .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel30)
                                .addComponent(jLabel29))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel10))
                .addGap(25, 25, 25))
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 102, 102), 5, true));
        jPanel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jPanel3MouseEntered(evt);
            }
        });

        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/fingerprint icon std.gif"))); // NOI18N

        jLabel26.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(0, 102, 102));
        jLabel26.setText(" FingerPrint Scanning");

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(0, 102, 102));
        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/ok1.png"))); // NOI18N
        jLabel11.setText("Add");
        jLabel11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel11MouseClicked(evt);
            }
        });

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(0, 102, 102));
        jLabel17.setText("Click Start");

        jButton1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(0, 102, 102));
        jButton1.setText("start");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel21.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(0, 102, 102));
        jLabel21.setText("Room No");

        jTable1.setForeground(new java.awt.Color(0, 102, 102));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                "Room NO"
            }
        ));
        jTable1.setSelectionBackground(new java.awt.Color(0, 153, 153));
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(111, 111, 111)
                .addComponent(jLabel26)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtroom, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel26)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel21)
                            .addComponent(txtroom, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(24, 24, 24)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );

        jButton2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jButton2.setForeground(new java.awt.Color(0, 102, 102));
        jButton2.setText("ADD");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(191, 191, 191)
                        .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(363, 363, 363)
                        .addComponent(jLabel1)))
                .addContainerGap(196, Short.MAX_VALUE))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(71, 71, 71)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                        .addComponent(txtdeg, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtname, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtregno, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtfac, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtbach, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 443, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtregno, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addGap(57, 57, 57)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtname, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addGap(61, 61, 61)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtfac, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addGap(75, 75, 75)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(txtdeg, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(51, 51, 51)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtbach, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7))
                        .addGap(41, 41, 41)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(56, 56, 56))
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
        txtdeg.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                java.awt.Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    c.setBackground(new java.awt.Color(0, 153, 153)); // #009999 color
                }
                return c;
            }
        });

        ad1.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 70, 1120, 770));

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(0, 102, 102));
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/nextbtn.png"))); // NOI18N
        jLabel20.setText("Next");
        jLabel20.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLabel20.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel20MouseClicked(evt);
            }
        });
        ad1.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(1080, 860, -1, -1));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 102, 102));
        jLabel4.setText("1");
        ad1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 880, -1, -1));

        jTabbedPane1.addTab("1", ad1);

        ad2.setBackground(new java.awt.Color(255, 255, 255));
        ad2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(0, 102, 102));
        jLabel15.setText("Setup");
        ad2.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 50, -1, -1));

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
        });
        ad2.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 860, -1, -1));

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(0, 102, 102));
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel23.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/nextbtn.png"))); // NOI18N
        jLabel23.setText("Next");
        jLabel23.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jLabel23.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel23MouseClicked(evt);
            }
        });
        ad2.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(1080, 860, -1, -1));

        jPanel5_SD2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5_SD2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 102), 3), "Student Course Registation", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 13), new java.awt.Color(0, 102, 102))); // NOI18N

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 102, 102));
        jLabel8.setText("Course Id");

        txttable2_SD2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txttable2_SD2.setForeground(new java.awt.Color(0, 102, 102));
        txttable2_SD2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null}
            },
            new String [] {
                "Course Id", "Course_name"
            }
        ));
        txttable2_SD2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        txttable2_SD2.setRowHeight(20);
        txttable2_SD2.setSelectionBackground(new java.awt.Color(0, 153, 153));
        txttable2_SD2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txttable2_SD2MouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(txttable2_SD2);

        jButton4_SD2.setBackground(new java.awt.Color(255, 255, 255));
        jButton4_SD2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButton4_SD2.setForeground(new java.awt.Color(0, 102, 102));
        jButton4_SD2.setText("ADD IN TO Registed courses Table");
        jButton4_SD2.setActionCommand("ADD IN TO Registed Courses Table");
        jButton4_SD2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4_SD2ActionPerformed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(0, 102, 102));
        jLabel12.setText("Semester");

        txttable_SD2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txttable_SD2.setForeground(new java.awt.Color(0, 102, 102));
        txttable_SD2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null}
            },
            new String [] {
                "Course Id"
            }
        ));
        txttable_SD2.setRowHeight(20);
        txttable_SD2.setSelectionBackground(new java.awt.Color(0, 153, 153));
        jScrollPane4.setViewportView(txttable_SD2);

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(0, 102, 102));
        jLabel14.setText("Student Id");

        txtsid_SD2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtsid_SD2ActionPerformed(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(0, 102, 102));
        jLabel19.setText("Rgistaed Courses");

        jLabel32.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel32.setForeground(new java.awt.Color(0, 102, 102));
        jLabel32.setText("Available Courses For you");

        javax.swing.GroupLayout jPanel5_SD2Layout = new javax.swing.GroupLayout(jPanel5_SD2);
        jPanel5_SD2.setLayout(jPanel5_SD2Layout);
        jPanel5_SD2Layout.setHorizontalGroup(
            jPanel5_SD2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5_SD2Layout.createSequentialGroup()
                .addContainerGap(66, Short.MAX_VALUE)
                .addGroup(jPanel5_SD2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5_SD2Layout.createSequentialGroup()
                        .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(84, 84, 84)))
                .addGroup(jPanel5_SD2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5_SD2Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5_SD2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12)
                            .addComponent(jLabel8)
                            .addComponent(jLabel14))
                        .addGap(22, 22, 22)
                        .addGroup(jPanel5_SD2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtsid_SD2, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtsem_SD2, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtseleco_SD2, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(61, 61, 61))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5_SD2Layout.createSequentialGroup()
                        .addComponent(jButton4_SD2, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)))
                .addGroup(jPanel5_SD2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 289, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(59, 59, 59))
        );
        jPanel5_SD2Layout.setVerticalGroup(
            jPanel5_SD2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5_SD2Layout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addGroup(jPanel5_SD2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel5_SD2Layout.createSequentialGroup()
                        .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3))
                    .addGroup(jPanel5_SD2Layout.createSequentialGroup()
                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5_SD2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 544, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel5_SD2Layout.createSequentialGroup()
                                .addGroup(jPanel5_SD2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtsid_SD2, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel14))
                                .addGap(34, 34, 34)
                                .addGroup(jPanel5_SD2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtsem_SD2, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel12))
                                .addGap(42, 42, 42)
                                .addGroup(jPanel5_SD2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtseleco_SD2, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel8))
                                .addGap(45, 45, 45)
                                .addComponent(jButton4_SD2, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(58, Short.MAX_VALUE))
        );

        ad2.add(jPanel5_SD2, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 120, 1120, 720));

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
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/nextbtn.png"))); // NOI18N
        jLabel25.setText("Next");
        jLabel25.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jLabel25.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel25MouseClicked(evt);
            }
        });
        student.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(1080, 860, -1, -1));

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 102), 3), "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 13), new java.awt.Color(0, 102, 102))); // NOI18N

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1104, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 744, Short.MAX_VALUE)
        );

        student.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 80, 1110, 750));

        jTabbedPane1.addTab("3", student);

        jPanel1.add(jTabbedPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -40, 1600, 1040));

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
closeSerialPort();
System.exit(0);        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel13MouseClicked

    private void jLabel20MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel20MouseClicked
        
        tableload_SD2();
        std_Reg_tableload_SD2();
        jTabbedPane1.setSelectedIndex(1);         // TODO add your handling code here:
    }//GEN-LAST:event_jLabel20MouseClicked

    private void jLabel22MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel22MouseClicked
jTabbedPane1.setSelectedIndex(0);        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel22MouseClicked

    private void jLabel23MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel23MouseClicked
 chartDataget();
 jTabbedPane1.setSelectedIndex(2);

    }//GEN-LAST:event_jLabel23MouseClicked

    private void jLabel24MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel24MouseClicked
 jTabbedPane1.setSelectedIndex(1);       // TODO add your handling code here:
    }//GEN-LAST:event_jLabel24MouseClicked

    private void jLabel25MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel25MouseClicked
jTabbedPane1.setSelectedIndex(3);        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel25MouseClicked

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
     stdbtn.setForeground(Color.decode("#FFFFFF"));
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
    jLabel9.setForeground(Color.decode("#009999"));
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

    private void jLabel11MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel11MouseClicked
 fingerprintInsert();
 roomtable_load(txtregno.getText());
        closeSerialPort();      
    }//GEN-LAST:event_jLabel11MouseClicked

    private void txtfacActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtfacActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtfacActionPerformed

    private void jButton4_SD2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4_SD2ActionPerformed
        
         
        insert_std_co_reg_SD2();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton4_SD2ActionPerformed

    private void adminlableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_adminlableMouseClicked
closeSerialPort();
this.hide();
new LOGIN_TO_SYSTEM().setVisible(true);
// TODO add your handling code here:
    }//GEN-LAST:event_adminlableMouseClicked

    private void lecLabel7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lecLabel7MouseClicked
closeSerialPort();
this.hide();
new LOGIN_TO_SYSTEM("lecturer").setVisible(true);
               // TODO add your handling code here:
    }//GEN-LAST:event_lecLabel7MouseClicked

    private void jLabel9MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel9MouseClicked
closeSerialPort(); 
this.hide();
new LOGIN_TO_SYSTEM("Attendance").setVisible(true);      // TODO add your handling code here:
    }//GEN-LAST:event_jLabel9MouseClicked

    private void jLabel10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel10MouseClicked
        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/deletlightA.gif")));

               // TODO add your handling code here:
    }//GEN-LAST:event_jLabel10MouseClicked

    private void jLabel10MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel10MouseEntered
        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/deletlight.png")));        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel10MouseEntered

    private void jLabel10MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel10MouseExited
        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/delet.png")));        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel10MouseExited

    private void jLabel47MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel47MouseClicked
        jLabel47.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/editlightA.gif")));
              // TODO add your handling code here:
    }//GEN-LAST:event_jLabel47MouseClicked

    private void jLabel47MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel47MouseEntered
        jLabel47.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/editlight.png")));        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel47MouseEntered

    private void jLabel47MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel47MouseExited
        jLabel47.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/edit.png")));         // TODO add your handling code here:
    }//GEN-LAST:event_jLabel47MouseExited

    private void txtsid_SD2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtsid_SD2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtsid_SD2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
   
        if (comPort != null && comPort.isOpen()) {
        try {
            OutputStream out = comPort.getOutputStream();
            String dataToSend = "e";  // For example, send the character 'e'
            out.write(dataToSend.getBytes());  // Send the data as bytes
            out.flush();  // Ensure the data is sent immediately
            System.out.println("Sent: " + dataToSend);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error sending data: " + e.getMessage());
        }
    } else {
        JOptionPane.showMessageDialog(this, "Port is not open.");
    }       // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void txtdegActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtdegActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtdegActionPerformed

    private void txttable2_SD2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txttable2_SD2MouseClicked
 int selectedRow = txttable2_SD2.getSelectedRow();
        String name = (String) txttable2_SD2.getValueAt(selectedRow, 0);

        txtseleco_SD2.setText(name);         // TODO add your handling code here:
    }//GEN-LAST:event_txttable2_SD2MouseClicked

    private void jPanel3MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel3MouseEntered
         // TODO add your handling code here:
    }//GEN-LAST:event_jPanel3MouseEntered

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        insert();                 // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

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
            java.util.logging.Logger.getLogger(students.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(students.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(students.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(students.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new students().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel ad1;
    private javax.swing.JPanel ad2;
    private javax.swing.JLabel adminlable;
    private javax.swing.JLabel imageside;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton4_SD2;
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
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel5_SD2;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lecLabel7;
    private javax.swing.JLabel stdbtn;
    private javax.swing.JPanel student;
    private javax.swing.JTextField txtbach;
    private javax.swing.JComboBox<String> txtdeg;
    private javax.swing.JComboBox<String> txtfac;
    private javax.swing.JTextField txtname;
    private javax.swing.JTextField txtregno;
    private javax.swing.JTextField txtroom;
    private javax.swing.JTextField txtseleco_SD2;
    private javax.swing.JTextField txtsem_SD2;
    private javax.swing.JTextField txtsid_SD2;
    private javax.swing.JTable txttable2_SD2;
    private javax.swing.JTable txttable_SD2;
    // End of variables declaration//GEN-END:variables
}
