/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package newUi;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import static javax.swing.SwingConstants.CENTER;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import net.proteanit.sql.DbUtils;

/**
 *
 * @author DAMIDU
 */
public class lacturers extends javax.swing.JFrame {

    
    public lacturers() {
       try {
            // Set FlatLaf Look and Feel (light theme) globally for all components
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        initComponents();
        styleTable(Table1);
        styleTable(att_dataTable);
        conn = DBconnect.connect();
      lec_reg_load();
             
    } public lacturers(String LoglecID) {
        try {
            // Set FlatLaf Look and Feel (light theme) globally for all components
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        initComponents();
        styleTable(Table1);
        styleTable(att_dataTable);
        conn = DBconnect.connect();
      
       this.LoglecID=LoglecID;//this use for set valeu after log
        txtLid.setText(LoglecID);
        lec_reg_load();
             
    }
    String LoglecID;
    Connection conn=null;
    PreparedStatement pst=null;
    ResultSet rs=null;
    //need tochange only for now
    public void styleTable(javax.swing.JTable table) {
    // Set Row Height for Better Visibility
    table.setRowHeight(30);
    
    // Set Default Font for Table Cells
    table.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));

    // Change Header Style with Proper Custom Renderer
    javax.swing.table.JTableHeader tableHeader = table.getTableHeader();
    tableHeader.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
    tableHeader.setForeground(Color.WHITE);
    
    // Fix Header Background Issue
    tableHeader.setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
        @Override
        public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            java.awt.Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            cell.setBackground(new java.awt.Color(0, 102, 102)); // Dark Teal Header
            cell.setForeground(Color.WHITE);
            setHorizontalAlignment(CENTER);
            return cell;
        }
    });

    // Customize Row Selection Color (Now Green)
    javax.swing.table.DefaultTableCellRenderer cellRenderer = new javax.swing.table.DefaultTableCellRenderer() {
        @Override
        public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            java.awt.Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (isSelected) {
                cell.setBackground(new java.awt.Color(200, 230, 201)); // Light Green Selection
                cell.setForeground(Color.BLACK); // Keep Text Readable
            } else {
                cell.setBackground(Color.WHITE); // Default White Background
                cell.setForeground(Color.BLACK);
            }
            
            return cell;
        }
    };

    // Apply Renderer to All Columns
    for (int i = 0; i < table.getColumnCount(); i++) {
        table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
    }

    // Ensure the Grid is Visible but Subtle
    table.setGridColor(new java.awt.Color(200, 200, 200)); // Light Gray Grid
    table.setShowVerticalLines(true);
    table.setShowHorizontalLines(true);

    // **Fix Gray Background Issue**
    table.setOpaque(false);
    table.setBackground(Color.WHITE); // Set Table Background to White
    ((javax.swing.JScrollPane) table.getParent().getParent()).getViewport().setBackground(Color.WHITE); // Set ScrollPane Background to White
}
    public void lec_reg_load(){
    String lec_id=txtLid.getText();
     try {
            String sql="SELECT course.co_id, course.co_name FROM course INNER JOIN lec_co_reg ON course.co_id =lec_co_reg.co_id WHERE lec_co_reg.lec_id='"+lec_id+"'";
            pst =conn.prepareStatement(sql);
            rs= pst.executeQuery();
            Table1.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,e);
        }
      try {
            String sql="SELECT lec_name FROM lec WHERE lec_id='"+lec_id+"'";
            pst =conn.prepareStatement(sql);
            rs= pst.executeQuery();
             if (rs.next()) { // Ensure there is a result
                 String lecName = rs.getString("lec_name"); // Get the value of the 'lec_name' column
                  txtLname1.setText(lecName); // Set the text in the JTextField
                } else {
                 txtLname1.setText(""); // Clear the field if no results are found
    }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,e);
        }


}
    private String SelectedRow() {
        int selectedRowIndex = Table1.getSelectedRow();
        String selectrowDetail;

            if (selectedRowIndex != -1) {
                
                selectrowDetail= (String) Table1.getValueAt(selectedRowIndex,0);
                
                
            } else {
                selectrowDetail= null;
                JOptionPane.showMessageDialog(null,"No row is selected");
                return null;
            }
        return selectrowDetail;   
    }
    //2nd page//////////////////////////////////////////////////
    public void tableselectloadnextpage(String selectrowDetail){
         String coCode=selectrowDetail;
         int attLNo = Integer.parseInt(txtLno.getSelectedItem().toString());
 try {
            
            jLabel11.setText(coCode);
            
           // jLabel28.setText(coCode);
         
          try {
            String sql="SELECT bg_no,sem FROM bg INNER JOIN course ON bg.onging_sem =course.sem WHERE course.co_id='"+coCode+"'";
            pst =conn.prepareStatement(sql);
            rs= pst.executeQuery();
             if (rs.next()) { 
                  BatchNo.removeAllItems();

                   BatchNo.addItem(rs.getString("bg_no"));
                   jLabel26.setText(rs.getString("sem"));
                } else {
                 System.out.println("no attendance"); //
    }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,e);
        }
          
          int bgNo = Integer.parseInt(BatchNo.getSelectedItem().toString());
          
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
        
 //present count
  try {
           
      
         int bgNo = Integer.parseInt(BatchNo.getSelectedItem().toString());
          
           // String sql="SELECT COUNT(SELECT std_status FROM attendance where co_id=? and att_l_no=? and bg_no=?) FROM attendance";
            String sql="SELECT COUNT(std_status) AS count FROM attendance WHERE co_id = ? AND att_l_no = ? AND bg_no = ? AND std_status=1";
            pst =conn.prepareStatement(sql);
            pst.setString(1, coCode);
            pst.setInt(2, attLNo);
            pst.setInt(3, bgNo);
         
            rs= pst.executeQuery();
            while(rs.next()){
                jLabel29.setText(rs.getString("count"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,e);
        }
  try{
      int bgNo = Integer.parseInt(BatchNo.getSelectedItem().toString());
  String sql="SELECT COUNT(std_status) AS count FROM attendance WHERE co_id = ? AND att_l_no = ? AND bg_no = ?";
            pst =conn.prepareStatement(sql);
            pst.setString(1, coCode);
            pst.setInt(2, attLNo);
            pst.setInt(3, bgNo);
         
            rs= pst.executeQuery();
            while(rs.next()){
                jLabel28.setText(rs.getString("count"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,e);
        }
 
        
 
}
     //3nd page//////////////////////////////////////////////////
    public void drowchart(){                                          
    int totalStudents=0;
    
    int bgno = Integer.parseInt(BatchNo.getSelectedItem().toString());
       
    Map<Integer, Integer> attendanceData = new HashMap<>();
    try {
            String sql="SELECT att_l_no, COUNT(*) AS status_count FROM attendance WHERE std_status = 1 AND co_id ='"+jLabel11.getText()+"'AND bg_no ='"+bgno+"' GROUP BY att_l_no;";
            pst =conn.prepareStatement(sql);
            
            rs= pst.executeQuery();
            while (rs.next()) {
                attendanceData.put(rs.getInt("att_l_no"),rs.getInt("status_count"));
            }
        } catch (Exception e) {JOptionPane.showMessageDialog(null, e);
        }
      try {
            String sql="SELECT COUNT(*) AS max_status_count FROM attendance WHERE  co_id = '"+jLabel11.getText()+"' AND bg_no ='"+bgno+"' GROUP BY att_l_no ORDER BY max_status_count DESC LIMIT 1;";
            pst =conn.prepareStatement(sql);
            
            rs= pst.executeQuery();
            while (rs.next()) {
                totalStudents=rs.getInt("max_status_count");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }        
      ChartGenerator chartGenerator = new ChartGenerator();
      chartGenerator.lacAttendanceChart(jPanel7, jLabel11.getText(), attendanceData, totalStudents);        // TODO add your handling code here:
}
    /////4th page//////////////////////////////////////////////////
public void stddataload(String std){
    
    String coid=jLabel11.getText();
    jLabel31.setText(coid);
    txtLid1.setText(std);
    
    try {
            String sql="SELECT * FROM std WHERE std_id=?";
            pst =conn.prepareStatement(sql);
            pst.setString(1, std);
            rs= pst.executeQuery();
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String stdName = rs.getString("std_name");
                jLabel17.setText(stdName);
            }
        } catch (SQLException e) {JOptionPane.showMessageDialog(null, e);
        } 
    //
    try {
            String sql="SELECT att_l_no as Lession_No,std_status as Status FROM `attendance` WHERE std_id=? and co_id=? ORDER BY att_l_no;";
            pst =conn.prepareStatement(sql);
            pst.setString(1, std);
            pst.setString(2, coid);
            rs= pst.executeQuery();
            jTable1.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {JOptionPane.showMessageDialog(null, e);
        }
    //chart generate
    
      Map<String, Integer> studentData = new HashMap<>();
       
            try {
            String sql= "SELECT SUM(CASE WHEN std_status = 1 THEN 1 ELSE 0 END) AS Total_Present, SUM(CASE WHEN std_status = 0 THEN 1 ELSE 0 END) AS Total_Absent FROM attendance WHERE std_id=? and co_id=?;";
            pst =conn.prepareStatement(sql);
            pst.setString(1, std);
            pst.setString(2, coid);
            rs= pst.executeQuery();
            while (rs.next()) {
        
        int Total_Present= rs.getInt("Total_Present");// Get the formatted date
        int Total_Absent= rs.getInt("Total_Absent");// Get the formatted date
        studentData.put("Present", Total_Present);
        studentData.put("Absent", Total_Absent);
        
        // Add to the map
    }
        } catch (Exception e) {JOptionPane.showMessageDialog(null, e);
        }
       
      ChartGenerator chartGenerator = new ChartGenerator();
      chartGenerator.generate_pi_chart2(jPanel3,studentData);
    
    
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
        lecLabel7 = new javax.swing.JLabel();
        stdbtn = new javax.swing.JLabel();
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
        txtLname1 = new javax.swing.JTextField();
        txtLid = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        Table1 = new javax.swing.JTable();
        seeAttbtn = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        ad2 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jPanel5_SD2 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        BatchNo = new javax.swing.JComboBox<>();
        txtLno = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        att_dataTable = new javax.swing.JTable();
        jButton3 = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jLabel19 = new javax.swing.JLabel();
        student = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        student1 = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        txtLid1 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel27 = new javax.swing.JLabel();

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

        lecLabel7.setBackground(new java.awt.Color(0, 102, 102));
        lecLabel7.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        lecLabel7.setForeground(new java.awt.Color(255, 255, 255));
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

        stdbtn.setBackground(new java.awt.Color(255, 255, 255));
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
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 102), 3), "Lacturers Section", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 13), new java.awt.Color(0, 102, 102))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 102, 102));
        jLabel1.setText("Lacturers Details");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 102, 102));
        jLabel2.setText("Lacturers Name");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 102, 102));
        jLabel3.setText("Lacturers No");

        txtLid.setToolTipText("Enter");
        txtLid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtLidActionPerformed(evt);
            }
        });

        Table1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Course ID", "Course Name"
            }
        ));
        Table1.setGridColor(new java.awt.Color(255, 255, 255));
        Table1.setRowHeight(25);
        Table1.setRowMargin(0);
        Table1.setSelectionBackground(new java.awt.Color(0, 153, 153));
        Table1.setShowVerticalLines(false);
        jScrollPane1.setViewportView(Table1);

        seeAttbtn.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        seeAttbtn.setForeground(new java.awt.Color(0, 102, 102));
        seeAttbtn.setText("See Attendance Selected Row");
        seeAttbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seeAttbtnActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 102, 102));
        jLabel5.setText("Courses");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap(316, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 482, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41)
                .addComponent(seeAttbtn)
                .addGap(28, 28, 28))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(328, 328, 328)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(153, 153, 153)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2)
                    .addComponent(jLabel5))
                .addGap(41, 41, 41)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtLid)
                    .addComponent(txtLname1))
                .addGap(308, 308, 308))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(71, 71, 71)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(seeAttbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(58, 58, 58)
                                .addComponent(jLabel3))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtLid, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtLname1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(51, Short.MAX_VALUE))
        );

        ad1.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 80, 1120, 780));

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
        jPanel5_SD2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 102), 3));

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(0, 102, 102));
        jLabel15.setText("Course Code");

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

        jLabel26.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(0, 102, 102));
        jLabel26.setText("Semester");

        jLabel28.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(0, 102, 102));
        jLabel28.setText("Total Student count");

        jLabel29.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(0, 102, 102));
        jLabel29.setText("0");

        jLabel33.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel33.setForeground(new java.awt.Color(0, 102, 102));
        jLabel33.setText("Lesion No");

        BatchNo.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BatchNo.setForeground(new java.awt.Color(0, 102, 102));
        BatchNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BatchNoActionPerformed(evt);
            }
        });
        BatchNo.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                java.awt.Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    c.setBackground(new java.awt.Color(0, 153, 153)); // #009999 color
                }
                return c;
            }
        });

        txtLno.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtLno.setForeground(new java.awt.Color(0, 102, 102));
        txtLno.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", " " }));
        txtLno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtLnoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(BatchNo, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtLno, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap(50, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BatchNo))
                .addGap(35, 35, 35)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtLno, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(169, 169, 169))
        );

        txtLno.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                java.awt.Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    c.setBackground(new java.awt.Color(0, 153, 153)); // #009999 color
                }
                return c;
            }
        });

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

        jButton3.setBackground(new java.awt.Color(255, 255, 255));
        jButton3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jButton3.setForeground(new java.awt.Color(0, 102, 102));
        jButton3.setText("Print Attendance");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(0, 102, 102));
        jLabel11.setText("Course Code");

        jButton4.setBackground(new java.awt.Color(255, 255, 255));
        jButton4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jButton4.setForeground(new java.awt.Color(0, 102, 102));
        jButton4.setText("Selected Student Attendance");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5_SD2Layout = new javax.swing.GroupLayout(jPanel5_SD2);
        jPanel5_SD2.setLayout(jPanel5_SD2Layout);
        jPanel5_SD2Layout.setHorizontalGroup(
            jPanel5_SD2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5_SD2Layout.createSequentialGroup()
                .addGap(348, 348, 348)
                .addComponent(jLabel15)
                .addGap(32, 32, 32)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(449, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5_SD2Layout.createSequentialGroup()
                .addContainerGap(92, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(39, 39, 39)
                .addGroup(jPanel5_SD2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 436, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5_SD2Layout.createSequentialGroup()
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton4)))
                .addGap(118, 118, 118))
        );
        jPanel5_SD2Layout.setVerticalGroup(
            jPanel5_SD2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5_SD2Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel5_SD2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23)
                .addGroup(jPanel5_SD2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel5_SD2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        ad2.add(jPanel5_SD2, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 120, 1110, 720));

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(0, 102, 102));
        jLabel19.setText("Attendance Recode");
        ad2.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 50, 470, -1));

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

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 102), 3));

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1094, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 754, Short.MAX_VALUE)
        );

        student.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 70, 1100, 760));

        jTabbedPane1.addTab("3", student);

        student1.setBackground(new java.awt.Color(255, 255, 255));
        student1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(0, 102, 102));
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/backbtn.png"))); // NOI18N
        jLabel25.setText("Back");
        jLabel25.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jLabel25.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel25MouseClicked(evt);
            }
        });
        student1.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 860, -1, -1));

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(0, 102, 102));
        jLabel20.setText("4");
        student1.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 880, -1, -1));

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 102), 3));

        jPanel3.setBackground(new java.awt.Color(242, 250, 230));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 466, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 480, Short.MAX_VALUE)
        );

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 102, 102));
        jLabel7.setText("Student Id");

        txtLid1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtLid1.setForeground(new java.awt.Color(0, 102, 102));
        txtLid1.setToolTipText("Enter");
        txtLid1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtLid1ActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 102, 102));
        jLabel10.setText("Student Name");

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(0, 102, 102));

        jLabel21.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(0, 102, 102));
        jLabel21.setText("Couse Code");

        jLabel31.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(0, 102, 102));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Leson_NO", "Attendance"
            }
        ));
        jTable1.setSelectionBackground(new java.awt.Color(0, 153, 153));
        jScrollPane3.setViewportView(jTable1);

        jLabel27.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(0, 102, 102));
        jLabel27.setText("Selected Student Attendance Data");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(jLabel21)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtLid1, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(102, 102, 102))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 422, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(33, 33, 33)))
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(53, 53, 53))
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(138, 138, 138)
                .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 927, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(29, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(jLabel27)
                .addGap(38, 38, 38)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtLid1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addGap(24, 24, 24)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(20, 20, 20)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel21)
                            .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 404, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7))
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(68, Short.MAX_VALUE))
        );

        student1.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 70, 1100, 760));

        jTabbedPane1.addTab("3", student1);

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
        System.exit(0);        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel13MouseClicked

    private void jLabel22MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel22MouseClicked
jTabbedPane1.setSelectedIndex(0);        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel22MouseClicked

    private void jLabel23MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel23MouseClicked
        drowchart();
        jTabbedPane1.setSelectedIndex(2);
    }//GEN-LAST:event_jLabel23MouseClicked

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
  lecLabel7.setForeground(Color.decode("#ffffff"));      
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

    private void txtLidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtLidActionPerformed
lec_reg_load();   // TODO add your handling code here:
    }//GEN-LAST:event_txtLidActionPerformed

    private void seeAttbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seeAttbtnActionPerformed
        if(SelectedRow()==null){return;}
        else{tableselectloadnextpage(SelectedRow());
        jTabbedPane1.setSelectedIndex(1);}
        // TODO add your handling code here:
    }//GEN-LAST:event_seeAttbtnActionPerformed

    private void att_dataTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_att_dataTableMouseClicked
       // show_studentId();  // TODO add your handling code here:
    }//GEN-LAST:event_att_dataTableMouseClicked

    private void txtLnoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtLnoActionPerformed
tableselectloadnextpage(SelectedRow());        // TODO add your handling code here:
    }//GEN-LAST:event_txtLnoActionPerformed

    private void BatchNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BatchNoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_BatchNoActionPerformed

    private void jLabel24MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel24MouseClicked
        jTabbedPane1.setSelectedIndex(1);       // TODO add your handling code here:
    }//GEN-LAST:event_jLabel24MouseClicked

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
Report r=new Report();
      
     String[] options = {"Print the Selected Lesson Number", "Print All Lessons", "Cancel"};
        int choice = JOptionPane.showOptionDialog(
                null,
                "Select print option:",
                "Print Options",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        switch (choice) {
            case 0: // "Print One"
                r.generateAttendanceReport(jLabel11.getText(),Integer.parseInt(txtLno.getSelectedItem().toString()),Integer.parseInt(BatchNo.getSelectedItem().toString())); 
                break;
            case 1: // "Print All"
                r.ReportForCourse(jLabel11.getText(),Integer.parseInt(BatchNo.getSelectedItem().toString()));        // TODO add your handling code here:

                break;
            default:
              
                break;
        }

        

// TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jLabel25MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel25MouseClicked
jTabbedPane1.setSelectedIndex(1);        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel25MouseClicked

    private void txtLid1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtLid1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtLid1ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        {
        int selectedRowIndex = att_dataTable.getSelectedRow();
        String selectrowDetail;
            if (selectedRowIndex != -1) {
                selectrowDetail= (String) att_dataTable.getValueAt(selectedRowIndex,0);
                
                stddataload(selectrowDetail);
                
                jTabbedPane1.setSelectedIndex(3);
                //System.out.println();  
            } else {
                selectrowDetail= null;
                JOptionPane.showMessageDialog(null,"No row is selected");
            }
        }
        
       
                  
        
// TODO add your handling code here:
    }//GEN-LAST:event_jButton4ActionPerformed
    private void jLabel9MouseClicked(java.awt.event.MouseEvent evt) {
        
    Attendance att=new Attendance();
    this.hide();
    att.setVisible(true);
    }
    private void stdbtnMouseClicked(java.awt.event.MouseEvent evt) {                                       
    students std=new students();
    this.hide();
    std.setVisible(true);
    }
    private void adminlableMouseClicked(java.awt.event.MouseEvent evt) {

    new LOGIN_TO_SYSTEM().setVisible(true);
    this.hide();
    }
    private void lecLabel7MouseClicked(java.awt.event.MouseEvent evt) {                                       
        
    }
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
            java.util.logging.Logger.getLogger(lacturers.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(lacturers.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(lacturers.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(lacturers.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new lacturers().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> BatchNo;
    private javax.swing.JTable Table1;
    private javax.swing.JPanel ad1;
    private javax.swing.JPanel ad2;
    private javax.swing.JLabel adminlable;
    private javax.swing.JTable att_dataTable;
    private javax.swing.JLabel imageside;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
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
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel4;
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
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lecLabel7;
    private javax.swing.JButton seeAttbtn;
    private javax.swing.JLabel stdbtn;
    private javax.swing.JPanel student;
    private javax.swing.JPanel student1;
    private javax.swing.JTextField txtLid;
    private javax.swing.JTextField txtLid1;
    private javax.swing.JTextField txtLname1;
    private javax.swing.JComboBox<String> txtLno;
    // End of variables declaration//GEN-END:variables


}
