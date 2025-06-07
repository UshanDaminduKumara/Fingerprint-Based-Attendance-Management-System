/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package newUi;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import static javax.swing.SwingConstants.CENTER;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.LineBorder;
import net.proteanit.sql.DbUtils;

/**
 *
 * @author DAMIDU
 */
public final class admin extends javax.swing.JFrame {

    public admin() {
         try {
            // Set FlatLaf Look and Feel (light theme) globally for all components
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        initComponents();
        styleTable(txttable);
        styleTable(txttable2);
        styleTable(txttable4);
        styleTable(txttablead3);
        styleTable(ad3);
        styleTable(txttablead4);
        styleTable(txttable2ad4);
       
        conn = DBconnect.connect();
        tableload();
        fac_load();
        tableload_AD2();
        tableload_AD3();
        detail_load_AD3();
        tableload_AD4();
        chart();
    }
    Connection conn = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
///style

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
                    cell.setBackground(new java.awt.Color(0, 0, 201)); // Light Green Selection
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
        table.setGridColor(new java.awt.Color(200, 0, 200)); // Light Gray Grid
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);

        // **Fix Gray Background Issue**
        table.setOpaque(false);
        table.setBackground(Color.WHITE); // Set Table Background to White
        ((javax.swing.JScrollPane) table.getParent().getParent()).getViewport().setBackground(Color.WHITE); // Set ScrollPane Background to White
    }



//admin page 1
    public void tableload() {
        try {
            String sql = "select fac as Faculty from fac ";
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            txttable.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
        }
        try {
            String sql = "select bg_no as Batch_No,fac as Faculty,bg_status as Batch_Status,onging_sem as Ongoing_sem from bg ";
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            txttable2.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public void insert() {

        String fac;

        if (txtfac.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "cannot add null value");
            return;
        } else {
            fac = txtfac.getText();

            try {

                String sql = "insert into fac(fac) values('" + fac + "')";
                pst = conn.prepareStatement(sql);
                pst.execute();
                tableload();
                JOptionPane.showMessageDialog(null, "successfully added");

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }

        }

    }

    public void facselecteditemload() {
        String fac = txttable.getValueAt(txttable.getSelectedRow(), 0).toString();
        txtfac.setText(fac);
    }

    public void facdelete() {
        try {
            int selectedRow = txttable.getSelectedRow();
            if (selectedRow != -1) { // Check if a row is selected

                String fac = txttable.getValueAt(selectedRow, 0).toString();

                String sql = "DELETE FROM fac WHERE fac = ?";
                pst = conn.prepareStatement(sql);
                pst.setString(1, fac);

                pst.executeUpdate(); // Use executeUpdate() for DELETE

                pst.close(); // Close PreparedStatement to free resources
                tableload();
                fac_load();
                txtfac.setText("");
                JOptionPane.showMessageDialog(null, "successfully Delete");
            } else {
                JOptionPane.showMessageDialog(null, "No row selected.");

            }
        } catch (SQLException e) {
            e.printStackTrace(); // Print error details
        }
    }

    public void facupdate() {

//need to wrrite qari for update
//UPDATE `fac` SET `fac` = 'Applied Science' WHERE `fac`.`fac` = 'Applied Sciences'
        try {
            int selectedRow = txttable.getSelectedRow();
            if (selectedRow != -1) {
                String fac = txttable.getValueAt(txttable.getSelectedRow(), 0).toString();
                String sql = "UPDATE fac SET fac = ? WHERE fac = ?";
                pst = conn.prepareStatement(sql);
                pst.setString(1, txtfac.getText());
                pst.setString(2, fac);
                pst.executeUpdate();
                tableload();
                fac_load();
                txtfac.setText("");
                JOptionPane.showMessageDialog(null, "successfully Updated");
            } else {
                JOptionPane.showMessageDialog(null, "No row selected.");

            }
        } catch (SQLException e) {
            e.printStackTrace(); // Print error details
        }
    }
//////////////////////////////////////////////////////////////////////////

    public void insert2() {
        int bgno;
        String fac;
        String bg_sta;
        String onging_sem;

        bgno = Integer.parseInt(txtfac1.getText());
        fac = (String) txtselectfac.getSelectedItem();
        bg_sta = (String) txtselectfac2.getSelectedItem();
        onging_sem = (String) txtselectfac1.getSelectedItem();
        try {
            //INSERT INTO `bg` (`bg_no`, `fac`, `bg_status`, `onging_sem`) VALUES ('20', 'Applied Sciences', 'Active', '1st Year 1st Sem');
            String sql = "insert into bg(bg_no,fac,bg_status,onging_sem) values('" + bgno + "','" + fac + "','" + bg_sta + "','" + onging_sem + "')";
            pst = conn.prepareStatement(sql);
            pst.execute();
            tableload();
            JOptionPane.showMessageDialog(null, "successfully added");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }

    public void bachselecteditemload() {
        int selectedRow = txttable2.getSelectedRow();
        if (selectedRow != -1) {
            String fac = txttable2.getValueAt(txttable2.getSelectedRow(), 1).toString();

            for (int i = 0; i < txtselectfac.getItemCount(); i++) {
                if (txtselectfac.getItemAt(i).equals(fac)) {
                    txtselectfac.setSelectedIndex(i);

                }
            }

            String bach = txttable2.getValueAt(txttable2.getSelectedRow(), 0).toString();
            txtfac1.setText(bach);

            String bachStatus = txttable2.getValueAt(txttable2.getSelectedRow(), 2).toString();

            if (bachStatus.equals("Active")) {
                txtselectfac2.setSelectedIndex(0);
            } else if (bachStatus.equals("Deactive")) {
                txtselectfac2.setSelectedIndex(1);
            } else {
                txtselectfac.setSelectedIndex(-1);
            }

            String sem = txttable2.getValueAt(txttable2.getSelectedRow(), 3).toString();

            switch (sem) {
                case "1st Year 1st Semester":
                    txtselectfac1.setSelectedIndex(0);
                    break;
                case "1st Year 2nd Semester":
                    txtselectfac1.setSelectedIndex(1);
                    break;
                case "2nd Year 1st Semester":
                    txtselectfac1.setSelectedIndex(2);
                    break;
                case "2nd Year 2nd Semester":
                    txtselectfac1.setSelectedIndex(3);
                    break;
                case "3rd Year 1st Semester":
                    txtselectfac1.setSelectedIndex(4);
                    break;
                case "3rd Year 2nd Semester":
                    txtselectfac1.setSelectedIndex(5);
                    break;
                case "4th Year 1st Semester":
                    txtselectfac1.setSelectedIndex(6);
                    break;
                case "4th Year 2nd Semester":
                    txtselectfac1.setSelectedIndex(7);
                    break;
                default:
                    txtselectfac1.setSelectedIndex(8);
                    break;
            }

        }
    }

    public void batchelete() {
        try {
            //bg_no,fac,bg_status,onging_sem
            int selectedRow = txttable2.getSelectedRow();
            if (selectedRow != -1) { // Check if a row is selected

                int bg_no = Integer.parseInt(txttable2.getValueAt(selectedRow, 0).toString());
                String fac = txttable2.getValueAt(selectedRow, 1).toString();
                String sql = "DELETE FROM bg WHERE  bg_no=? and fac = ?";
                pst = conn.prepareStatement(sql);

                pst.setInt(1, bg_no);
                pst.setString(2, fac);

                pst.executeUpdate(); // Use executeUpdate() for DELETE

                pst.close(); // Close PreparedStatement to free resources
                tableload();
                txtselectfac.setSelectedIndex(-1);
                txtfac1.setText("");
                txtselectfac2.setSelectedIndex(-1);
                txtselectfac1.setSelectedIndex(-1);

                JOptionPane.showMessageDialog(null, "successfully Delete");
            } else {
                JOptionPane.showMessageDialog(null, "No row selected.");

            }
        } catch (SQLException e) {
            e.printStackTrace(); // Print error details
        }
    }

    public void batchupdate() {

        try {
            int selectedRow = txttable2.getSelectedRow();
            if (selectedRow != -1) {
                int bg_no = Integer.parseInt(txttable2.getValueAt(selectedRow, 0).toString());
                String fac = txttable2.getValueAt(selectedRow, 1).toString();
                String onging_sem = (String) txtselectfac1.getSelectedItem();
                String bg_status = (String) txtselectfac2.getSelectedItem();

                int bg_noNew = Integer.parseInt(txtfac1.getText());
                String facNew = (String) txtselectfac.getSelectedItem();
                String sql = "UPDATE bg SET bg_status = ?,onging_sem=?,bg_no=?,fac = ? WHERE bg_no=? and fac = ?";
                pst = conn.prepareStatement(sql);

                pst.setString(1, bg_status);
                pst.setString(2, onging_sem);
                pst.setInt(3, bg_noNew);

                pst.setString(4, facNew);
                pst.setInt(5, bg_no);
                pst.setString(6, fac);
                pst.executeUpdate();
                tableload();

                txtselectfac.setSelectedIndex(-1);
                txtfac1.setText("");
                txtselectfac2.setSelectedIndex(-1);
                txtselectfac1.setSelectedIndex(-1);

                JOptionPane.showMessageDialog(null, "successfully Updated");
            } else {
                JOptionPane.showMessageDialog(null, "No row selected.");

            }
        } catch (SQLException e) {
            e.printStackTrace(); // Print error details
        }
    }
    //fac_load use for loading data to combobox

    public void fac_load() {
        txtselectfac.removeAllItems();
        try {
            String sql = "select fac from fac";
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                txtselectfac.addItem(rs.getString("fac"));
            }
            txtselectfac.setSelectedIndex(-1);
        } catch (SQLException ex) {
            System.out.println(ex);
        }

    }
    ///////////////////////////////////////////
//admin page 2

    public void tableload_AD2() {

        try {
            String sql = "select co_id as course,co_name as Course_Name,sem as Semester,co_mode as Course_Mode,co_type as Course_Type,co_credit as Course_Credit,co_hours as Course_Hours,no_of_lesion as No_of_Lesion,day as Day,time as Time  from course ";
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            txttable4.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public void insert2_AD2() {
        String co_id;
        String co_name;
        String sem;
        String co_mode;
        String co_type;
        int co_credit;
        int co_hours;
        int no_of_lesion;
        String day=null;
        int time = 0;

        //need to add data verification using if eles 
        co_id = txtcoid.getText();
        co_name = txtname.getText();
        sem = (String) txtselectsem.getSelectedItem();
        co_mode = (String) txtselectmode.getSelectedItem();
        co_type = (String) txtselecttype.getSelectedItem();
        co_credit = Integer.parseInt((String) txtselectcre.getSelectedItem());
        co_hours = Integer.parseInt((String) txtselecthours.getSelectedItem());
        no_of_lesion = Integer.parseInt((String) txtselectLeNO.getSelectedItem());
        
        
        //time geting
        try {   day=(String) datetxt.getSelectedItem();
                String x=(String) timetxt.getSelectedItem();
                     // Extract the numeric part
                String[] parts = x.split(" ");
                String hourPart = parts[0].split("\\.")[0]; // Get the hour before "."
                time = Integer.parseInt(hourPart);

                // Convert to 24-hour format if it's PM
                if (parts[1].equals("P.M") && time != 12) {
                    time += 12;
                }
              
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "you can update Date & time later");
        }
        try {

            String sql = "insert into course(co_id,co_name,sem,co_mode,co_type,co_credit,co_hours,no_of_lesion,day,time) values('" + co_id + "','" + co_name + "','" + sem + "','" + co_mode + "','" + co_type + "','" + co_credit + "','" + co_hours + "','" + no_of_lesion + "','" + day + "','" + time + "')";
            pst = conn.prepareStatement(sql);
            pst.execute();
            tableload_AD2();
            JOptionPane.showMessageDialog(null, "successfully added");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }

    public void Courseselecteditemload() {
        int selectedRow = txttable4.getSelectedRow();
        if (selectedRow != -1) {
            //co_id,co_name,sem as Semester,co_mode,co_type,co_credit,co_hours,no_of_lesion 
            String co_id = txttable4.getValueAt(txttable4.getSelectedRow(), 0).toString();
            String co_name = txttable4.getValueAt(txttable4.getSelectedRow(), 1).toString();
            String sem = txttable4.getValueAt(txttable4.getSelectedRow(), 2).toString();
            String co_mode = txttable4.getValueAt(txttable4.getSelectedRow(), 3).toString();
            String co_type = txttable4.getValueAt(txttable4.getSelectedRow(), 4).toString();
            int co_credit = Integer.parseInt(txttable4.getValueAt(txttable4.getSelectedRow(), 5).toString());
            int co_hours = Integer.parseInt(txttable4.getValueAt(txttable4.getSelectedRow(), 6).toString());
            int no_of_lesion = Integer.parseInt(txttable4.getValueAt(txttable4.getSelectedRow(), 7).toString());
            String date = txttable4.getValueAt(txttable4.getSelectedRow(), 8).toString();
            int time=Integer.parseInt(txttable4.getValueAt(txttable4.getSelectedRow(),9).toString());
            txtcoid.setText(co_id);
            txtname.setText(co_name);
            switch (sem) {
                case "1st Year 1st Semester":
                    txtselectsem.setSelectedIndex(0);
                    break;
                case "1st Year 2nd Semester":
                    txtselectsem.setSelectedIndex(1);
                    break;
                case "2nd Year 1st Semester":
                    txtselectsem.setSelectedIndex(2);
                    break;
                case "2nd Year 2nd Semester":
                    txtselectsem.setSelectedIndex(3);
                    break;
                case "3rd Year 1st Semester":
                    txtselectsem.setSelectedIndex(4);
                    break;
                case "3rd Year 2nd Semester":
                    txtselectsem.setSelectedIndex(5);
                    break;
                case "4th Year 1st Semester":
                    txtselectsem.setSelectedIndex(6);
                    break;
                case "4th Year 2nd Semester":
                    txtselectsem.setSelectedIndex(7);
                    break;
                default:
                    txtselectsem.setSelectedIndex(8);
                    break;
            }
            switch (co_mode) {
                case "Physical":
                    txtselectmode.setSelectedIndex(0);
                    break;
                case "Online":
                    txtselectmode.setSelectedIndex(1);
                    break;
                case "Hybrid":
                    txtselectmode.setSelectedIndex(2);
                    break;

                default:
                    txtselectmode.setSelectedIndex(-1);
                    break;
            }
            switch (co_type) {
                case "Theory":
                    txtselecttype.setSelectedIndex(0);
                    break;
                case "Practical":
                    txtselecttype.setSelectedIndex(1);
                    break;
                case "Hybrid":
                    txtselecttype.setSelectedIndex(2);
                    break;

                default:
                    txtselecttype.setSelectedIndex(-1);
                    break;
            }

            for (int i = 0; i < 7; i++) {
                if (co_credit == i) {
                    txtselectcre.setSelectedIndex(i);
                }
            }

            for (int i = 0; i < 7; i++) {
                if (co_hours == i) {
                    txtselecthours.setSelectedIndex(i);
                }
            }

            for (int i = 10; i < 16; i++) {
                if (no_of_lesion == i) {
                    txtselectLeNO.setSelectedIndex(i - 10);
                }
            }
            switch (date) {
                case "Monday":
                    datetxt.setSelectedIndex(0);
                    break;
                case "Tuesday":
                    datetxt.setSelectedIndex(1);
                    break;
                case "Wednesday":
                    datetxt.setSelectedIndex(2);
                    break;
                case "Thursday":
                    datetxt.setSelectedIndex(3);
                    break;
                case "Friday":
                    datetxt.setSelectedIndex(4);
                    break;
                default:
                    datetxt.setSelectedIndex(-1);
                    break;
            }switch (time) {
                case 8:
                    timetxt.setSelectedIndex(0);
                    break;
                case 9:
                    timetxt.setSelectedIndex(1);
                    break;
                case 10:
                    timetxt.setSelectedIndex(2);
                    break;
                case 11:
                    timetxt.setSelectedIndex(3);
                    break;
                case 13:
                    timetxt.setSelectedIndex(4);
                    break;
                case 14:
                    timetxt.setSelectedIndex(5);
                    break;
                case 15:
                    timetxt.setSelectedIndex(6);
                    break;
                case 16:
                    timetxt.setSelectedIndex(7);
                    break;
                default:
                    timetxt.setSelectedIndex(-1);
                    break;
            }
        }
    }

    public void Coursedelete() {
        try {
            //bg_no,fac,bg_status,onging_sem
            int selectedRow = txttable4.getSelectedRow();
            if (selectedRow != -1) { // Check if a row is selected

                String co_id = txttable4.getValueAt(selectedRow, 0).toString();
                String sql = "DELETE FROM course WHERE co_id = ?";
                pst = conn.prepareStatement(sql);
                pst.setString(1, co_id);
                pst.executeUpdate(); // Use executeUpdate() for DELETE
                pst.close(); // Close PreparedStatement to free resources
                tableload_AD2();

                txtcoid.setText("");
                txtname.setText("");
                txtselectsem.setSelectedIndex(-1);
                txtselectmode.setSelectedIndex(-1);
                txtselecttype.setSelectedIndex(-1);
                txtselectcre.setSelectedIndex(-1);
                txtselecthours.setSelectedIndex(-1);
                txtselectLeNO.setSelectedIndex(-1);
                datetxt.setSelectedIndex(-1);
                timetxt.setSelectedIndex(-1);

                JOptionPane.showMessageDialog(null, "successfully Delete");
            } else {
                JOptionPane.showMessageDialog(null, "No row selected.");

            }
        } catch (SQLException e) {
            e.printStackTrace(); // Print error details
        }
    }

    public void Courseupdate() {
        try {
            int selectedRow = txttable4.getSelectedRow();
            if (selectedRow != -1) {
                String co_id;
                String co_idnew;
                String co_name;
                String sem;
                String co_mode;
                String co_type;
                int co_credit;
                int co_hours;
                int no_of_lesion;
                String day=null;
                int time = 0;

                //need to add data verification using if eles 
                co_id = txttable4.getValueAt(selectedRow, 0).toString();
                co_idnew = txtcoid.getText();
                co_name = txtname.getText();
                sem = (String) txtselectsem.getSelectedItem();
                co_mode = (String) txtselectmode.getSelectedItem();
                co_type = (String) txtselecttype.getSelectedItem();
                co_credit = Integer.parseInt((String) txtselectcre.getSelectedItem());
                co_hours = Integer.parseInt((String) txtselecthours.getSelectedItem());
                no_of_lesion = Integer.parseInt((String) txtselectLeNO.getSelectedItem());
                
                try {   day=(String) datetxt.getSelectedItem();
                String x=(String) timetxt.getSelectedItem();
                     // Extract the numeric part
                String[] parts = x.split(" ");
                String hourPart = parts[0].split("\\.")[0]; // Get the hour before "."
                time = Integer.parseInt(hourPart);

                // Convert to 24-hour format if it's PM
                if (parts[1].equals("P.M") && time != 12) {
                    time += 12;
                }
              
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "you can update Date or time later");
        }
                String sql = "UPDATE course SET co_id = ?, co_name = ?, sem = ?, co_mode = ?, co_type = ?, co_credit = ?, co_hours = ?, no_of_lesion = ?,day=?,time=? WHERE co_id = ?";

                //String sql = "UPDATE bg SET bg_status = ?,onging_sem=?,bg_no=?,fac = ? WHERE bg_no=? and fac = ?";
                pst = conn.prepareStatement(sql);

                pst.setString(1, co_idnew);
                pst.setString(2, co_name);
                pst.setString(3, sem);
                pst.setString(4, co_mode);
                pst.setString(5, co_type);
                pst.setInt(6, co_credit);
                pst.setInt(7, co_hours);
                pst.setInt(8, no_of_lesion);
                pst.setString(9, day);
                pst.setInt(10, time);
                pst.setString(11, co_id);
                pst.executeUpdate();
                tableload_AD2();

                txtcoid.setText("");
                txtname.setText("");
                txtselectsem.setSelectedIndex(-1);
                txtselectmode.setSelectedIndex(-1);
                txtselecttype.setSelectedIndex(-1);
                txtselectcre.setSelectedIndex(-1);
                txtselecthours.setSelectedIndex(-1);
                txtselectLeNO.setSelectedIndex(-1);
                datetxt.setSelectedIndex(-1);
                timetxt.setSelectedIndex(-1);
                JOptionPane.showMessageDialog(null, "successfully Updated");
            } else {
                JOptionPane.showMessageDialog(null, "No row selected.");

            }
        } catch (SQLException e) {
            e.printStackTrace(); // Print error details
        }
    }
    /////////////////////////////////////////////////////////////
//admin page 3 

    public void tableload_AD3() {
        try {
            String sql = "select room_id as Room_ID,room_name as Room_Name from room ";
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            txttablead3.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
        }
        try {
            String sql = "select co_id as Course_Id,room_id as Room_Id,bg_no as Batch_NO from room_allocation";
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            ad3.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    //

    public void insert_AD3() {

        String roomid;
        String roomname;

        roomid = txtroomid.getText();
        roomname = txtroomname.getText();
        if (roomid.trim().isEmpty() || roomname.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Room ID and Room Name cannot be empty.");
            return; // Stop execution if fields are empty
        } else {
            try {
                String sql = "insert into room(room_id,room_name) values('" + roomid + "','" + roomname + "')";
                pst = conn.prepareStatement(sql);
                pst.execute();
                tableload_AD3();
                JOptionPane.showMessageDialog(null, "successfully added");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }

    public void roomselecteditemload() {
        int selectedRow = txttablead3.getSelectedRow();
        if (selectedRow != -1) {
            //co_id,co_name,sem as Semester,co_mode,co_type,co_credit,co_hours,no_of_lesion 
            String R_id = txttablead3.getValueAt(txttablead3.getSelectedRow(), 0).toString();
            String R_name = txttablead3.getValueAt(txttablead3.getSelectedRow(), 1).toString();

            txtroomid.setText(R_id);
            txtroomname.setText(R_name);

        }
    }

    public void roomdelete() {
        try {

            int selectedRow = txttablead3.getSelectedRow();
            if (selectedRow != -1) { // Check if a row is selected

                String R_id = txttablead3.getValueAt(selectedRow, 0).toString();
                String sql = "DELETE FROM room WHERE room_id = ?";
                pst = conn.prepareStatement(sql);
                pst.setString(1, R_id);
                pst.executeUpdate(); // Use executeUpdate() for DELETE
                pst.close(); // Close PreparedStatement to free resources
                tableload_AD3();
                txtroomid.setText("");
                txtroomname.setText("");
                JOptionPane.showMessageDialog(null, "successfully Delete");
            } else {
                JOptionPane.showMessageDialog(null, "No row selected.");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Print error details
        }
    }

    public void roomupdate() {
        try {
            int selectedRow = txttablead3.getSelectedRow();
            if (selectedRow != -1) {
                String R_id;
                String R_idnew;
                String R_name;

                //need to add data verification using if eles 
                R_id = txttablead3.getValueAt(selectedRow, 0).toString();
                R_idnew = txtroomid.getText();
                R_name = txtroomname.getText();

                String sql = "UPDATE room SET room_id = ?, room_name = ? WHERE room_id = ?";

                pst = conn.prepareStatement(sql);

                pst.setString(1, R_idnew);
                pst.setString(2, R_name);
                pst.setString(3, R_id);
                pst.executeUpdate();

                tableload_AD3();

                txtroomid.setText("");
                txtroomname.setText("");
                JOptionPane.showMessageDialog(null, "successfully Updated");
            } else {
                JOptionPane.showMessageDialog(null, "No row selected.");

            }
        } catch (SQLException e) {
            e.printStackTrace(); // Print error details
        }
    }

    public void search_coAD3() {
        String se_code = txtroomname1.getText();
        txtselectcoid3.removeAllItems();
        try {
            String sql = "SELECT * FROM `course` WHERE co_id LIKE ?";
            pst = conn.prepareStatement(sql);
            pst.setString(1, se_code + "%");
            rs = pst.executeQuery();
            while (rs.next()) {
                txtselectcoid3.addItem(rs.getString("co_id"));
            }
            txtselectcoid3.setSelectedIndex(0);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }

    public void search_roomAD3() {
        String se_id = txtroomId.getText();
        txtselectroomid.removeAllItems();
        try {
            String sql = "SELECT * FROM `room` WHERE room_id LIKE ?";
            pst = conn.prepareStatement(sql);
            pst.setString(1, se_id + "%");
            rs = pst.executeQuery();
            while (rs.next()) {
                txtselectroomid.addItem(rs.getString("room_id"));
            }
            txtselectroomid.setSelectedIndex(0);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }

    public void detail_load_AD3() {

        try {

            String sql = "select DISTINCT bg_no from bg";//methana where xxx.xxx=yyyy.yyy kiyala enna oni
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                txtselectbatchno.addItem(rs.getString("bg_no"));
            }
            txtselectbatchno.setSelectedIndex(-1);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }

    }
//

    public void insert2_AD3() {

        String co_id;
        String room_id;
        int bg_no;

        co_id = (String) txtselectcoid3.getSelectedItem();
        room_id = (String) txtselectroomid.getSelectedItem();
        bg_no = Integer.parseInt((String) txtselectbatchno.getSelectedItem());

        try {
            String sql = "insert into room_allocation(co_id,room_id,bg_no) values('" + co_id + "','" + room_id + "','" + bg_no + "')";
            pst = conn.prepareStatement(sql);
            pst.execute();
            tableload_AD3();
            JOptionPane.showMessageDialog(null, "successfully added");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }

    public void roomAlloselecteditemload() {
        int selectedRow = ad3.getSelectedRow();
        if (selectedRow != -1) {

            String R_id = ad3.getValueAt(selectedRow, 0).toString();
            String co_id = ad3.getValueAt(selectedRow, 1).toString();
            int bg_no = Integer.parseInt(ad3.getValueAt(selectedRow, 2).toString());

            txtroomname1.setText(R_id);

            txtroomId.setText(co_id);
            for (int i = 0; i < txtselectbatchno.getItemCount(); i++) {

                if (Integer.parseInt(txtselectbatchno.getItemAt(i)) == bg_no) {

                    txtselectbatchno.setSelectedIndex(i);
                }

            }
        }
    }

    public void roomAllodelete() {
        try {

            int selectedRow = ad3.getSelectedRow();
            if (selectedRow != -1) { // Check if a row is selected

                String co_id = ad3.getValueAt(selectedRow, 0).toString();
                String R_id = ad3.getValueAt(selectedRow, 1).toString();
                int bg_no = Integer.parseInt(ad3.getValueAt(selectedRow, 2).toString());

                String sql = "DELETE FROM room_allocation WHERE room_id = ? AND co_id = ? AND bg_no = ?";

                pst = conn.prepareStatement(sql);
                pst.setString(1, R_id);
                pst.setString(2, co_id);
                pst.setInt(3, bg_no);
                pst.executeUpdate(); // Use executeUpdate() for DELETE
                pst.close(); // Close PreparedStatement to free resources
                tableload_AD3();

                txtselectroomid.removeAllItems();
                txtselectcoid3.removeAllItems();
                txtroomname1.setText("");
                txtroomId.setText("");
                txtselectbatchno.setSelectedIndex(-1);
                JOptionPane.showMessageDialog(null, "successfully Delete");
            } else {
                JOptionPane.showMessageDialog(null, "No row selected.");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Print error details
        }
    }

    public void roomAlloupdate() {
        try {
            int selectedRow = ad3.getSelectedRow();
            if (selectedRow != -1) {

                String co_id = ad3.getValueAt(selectedRow, 0).toString();
                String R_id = ad3.getValueAt(selectedRow, 1).toString();
                int bg_no = Integer.parseInt(ad3.getValueAt(selectedRow, 2).toString());

                String R_idnew = txtroomId.getText();
                String co_idnew = txtroomname1.getText();
                int bg_nonew = Integer.parseInt((String) txtselectbatchno.getSelectedItem());

                String sql = "UPDATE room_allocation SET room_id = ?, co_id = ?, bg_no = ? WHERE room_id = ? AND co_id = ? AND bg_no = ?";

                pst = conn.prepareStatement(sql);

                pst.setString(1, R_idnew);
                pst.setString(2, co_idnew);
                pst.setInt(3, bg_nonew);
                pst.setString(4, R_id);
                pst.setString(5, co_id);
                pst.setInt(6, bg_no);
                pst.executeUpdate();

                tableload_AD3();
                txtselectroomid.removeAllItems();
                txtselectcoid3.removeAllItems();
                txtroomname1.setText("");
                txtroomId.setText("");
                txtselectbatchno.setSelectedIndex(-1);

                JOptionPane.showMessageDialog(null, "successfully Updated");
            } else {
                JOptionPane.showMessageDialog(null, "No row selected.");

            }
        } catch (SQLException e) {
            e.printStackTrace(); // Print error details
        }
    }

////////////////////////////////////////////////////////////////
//admin page 4 
    public void tableload_AD4() {
        try {
            String sql = "select lec_id as Lectures_Id,lec_name as Lectures_Name from lec ";
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            txttablead4.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
        }
        try {
            String sql = "select co_id as Course_Id,lec_id as Lectures_Id from lec_co_reg";
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            txttable2ad4.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    //

    //fac_load use for loading data to combobox
    public void detail_load_AD4() {

        try {
            String sql = "select lec_id from lec";
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                txtselectlel_idad4.addItem(rs.getString("lec_id"));
            }
            txtselectlel_idad4.setSelectedIndex(-1);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }

    }
//

    public void insertlec() {

        String lec_id;
        String lec_name;

        lec_id = txtlecid.getText();
        lec_name = txtlecname.getText();
        if (lec_id == null || lec_id.isEmpty() || lec_name == null || lec_name.isEmpty()) {
            JOptionPane.showMessageDialog(null, "fill the empty field");

            return;
        }

        try {
            String sql = "insert into lec(lec_id,lec_name) values('" + lec_id + "','" + lec_name + "')";
            pst = conn.prepareStatement(sql);
            pst.execute();
            tableload_AD4();
            JOptionPane.showMessageDialog(null, "successfully added");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }

    public void lecSelecteditemload() {
        int selectedRow = txttablead4.getSelectedRow();
        if (selectedRow != -1) {

            String lec_id = txttablead4.getValueAt(selectedRow, 0).toString();
            String lec_name = txttablead4.getValueAt(selectedRow, 1).toString();
            txtlecid.setText(lec_id);
            txtlecname.setText(lec_name);
        }
    }

    public void lecdelete() {
        try {

            int selectedRow = txttablead4.getSelectedRow();
            if (selectedRow != -1) { // Check if a row is selected

                String lec_id = txttablead4.getValueAt(selectedRow, 0).toString();

                String sql = "DELETE FROM lec WHERE lec_id = ?";

                pst = conn.prepareStatement(sql);
                pst.setString(1, lec_id);

                pst.executeUpdate(); // Use executeUpdate() for DELETE
                pst.close(); // Close PreparedStatement to free resources
                tableload_AD4();

                txtlecid.setText("");
                txtlecname.setText("");
                JOptionPane.showMessageDialog(null, "successfully Delete");
            } else {
                JOptionPane.showMessageDialog(null, "No row selected.");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Print error details
        }
    }

    public void lecupdate() {
        try {
            int selectedRow = txttablead4.getSelectedRow();
            if (selectedRow != -1) {
                String lec_id = txttablead4.getValueAt(selectedRow, 0).toString();
                String lec_idNew = txtlecid.getText();
                String lec_nameNew = txtlecname.getText();
                String sql = "UPDATE lec SET lec_id = ?, lec_name = ? WHERE lec_id = ?";
                pst = conn.prepareStatement(sql);
                pst.setString(1, lec_idNew);
                pst.setString(2, lec_nameNew);
                pst.setString(3, lec_id);
                pst.executeUpdate();
                tableload_AD4();
                txtlecid.setText("");
                txtlecname.setText("");
                JOptionPane.showMessageDialog(null, "successfully Updated");
            } else {
                JOptionPane.showMessageDialog(null, "No row selected.");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Print error details
        }
    }

    ////////////////////////////////////////////////
    public void search_coAD4() {
        String se_code = txtroomname2.getText();
        txtselectcoid4.removeAllItems();
        try {
            String sql = "SELECT * FROM `course` WHERE co_id LIKE ?";
            pst = conn.prepareStatement(sql);
            pst.setString(1, se_code + "%");
            rs = pst.executeQuery();
            while (rs.next()) {
                txtselectcoid4.addItem(rs.getString("co_id"));
            }
            txtselectcoid4.setSelectedIndex(0);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public void search_lecAD4() {
        String lec_id = txtroomname3.getText();
        txtselectlel_idad4.removeAllItems();
        try {
            String sql = "SELECT * FROM lec WHERE lec_id LIKE ?";
            pst = conn.prepareStatement(sql);
            pst.setString(1, lec_id + "%");
            rs = pst.executeQuery();
            while (rs.next()) {
                txtselectlel_idad4.addItem(rs.getString("lec_id"));
            }
            txtselectlel_idad4.setSelectedIndex(0);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public void insert_lec_co_reg() {
        String coid;
        String lecid;
        coid = txtroomname2.getText();
        lecid = (String) txtselectlel_idad4.getSelectedItem();
        try {
            String sql = "insert into lec_co_reg(co_id,lec_id) values('" + coid + "','" + lecid + "')";
            pst = conn.prepareStatement(sql);
            pst.execute();
            tableload_AD4();
            JOptionPane.showMessageDialog(null, "successfully added");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public void lec_co_regSelecteditemload() {
        int selectedRow = txttable2ad4.getSelectedRow();
        if (selectedRow != -1) {

            String co_id = txttable2ad4.getValueAt(selectedRow, 0).toString();
            String lec_id = txttable2ad4.getValueAt(selectedRow, 1).toString();
            txtroomname2.setText(co_id);
            txtroomname3.setText(lec_id);
        }
    }

    public void lec_co_regdelete() {
        try {

            int selectedRow = txttable2ad4.getSelectedRow();
            if (selectedRow != -1) { // Check if a row is selected

                String co_id = txttable2ad4.getValueAt(selectedRow, 0).toString();
                String lec_id = txttable2ad4.getValueAt(selectedRow, 1).toString();

                String sql = "DELETE FROM lec_co_reg WHERE lec_id = ? and co_id=?";

                pst = conn.prepareStatement(sql);
                pst.setString(1, lec_id);
                pst.setString(2, co_id);

                pst.executeUpdate(); // Use executeUpdate() for DELETE
                pst.close(); // Close PreparedStatement to free resources
                tableload_AD4();
                txtselectcoid4.removeAllItems();
                txtselectlel_idad4.removeAllItems();
                txtroomname2.setText("");
                txtroomname3.setText("");
                JOptionPane.showMessageDialog(null, "successfully Delete");
            } else {
                JOptionPane.showMessageDialog(null, "No row selected.");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Print error details
        }
    }

    public void lec_co_regupdate() {
        try {
            int selectedRow = txttable2ad4.getSelectedRow();
            if (selectedRow != -1) {
                String co_id = txttable2ad4.getValueAt(selectedRow, 0).toString();
                String lec_id = txttable2ad4.getValueAt(selectedRow, 1).toString();
                String co_idNew = txtroomname2.getText();
                String lec_idNew = txtroomname3.getText();

                String sql = "UPDATE lec_co_reg SET lec_id = ?, co_id = ? WHERE lec_id = ? AND co_id = ?";
                pst = conn.prepareStatement(sql);
                pst.setString(1, lec_idNew);
                pst.setString(2, co_idNew);
                pst.setString(3, lec_id);
                pst.setString(4, co_id);
                pst.executeUpdate();
                tableload_AD4();
                txtroomname2.setText("");
                txtroomname3.setText("");
                JOptionPane.showMessageDialog(null, "successfully Updated");
            } else {
                JOptionPane.showMessageDialog(null, "No row selected.");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Print error details
        }
    }
/////////////////////////////////////////////////////////////////////////////////
    //55555555555555555555555555555555555555555555555////////
    public void chart(){
        Map<String, Integer> attendanceData = new LinkedHashMap<>();

     try {
            String sql= "SELECT DATE_FORMAT(att_l_date, '%b-%d') AS Attendance_Date, CAST((SUM(CASE WHEN std_status = 1 THEN 1 ELSE 0 END) / COUNT(*)) * 100 AS UNSIGNED) AS Attendance_Percentage FROM attendance GROUP BY att_l_date ORDER BY att_l_date LIMIT 20;";
            pst =conn.prepareStatement(sql);
            rs= pst.executeQuery();
            while (rs.next()) {
        String attendanceDate = rs.getString("Attendance_Date"); // Get the formatted date
                
        int attendancePercentage = rs.getInt("Attendance_Percentage");
        attendanceData.put(attendanceDate, attendancePercentage);
        // Add to the map
    }
        } catch (Exception e) {JOptionPane.showMessageDialog(null, e);
        }
    
       ChartGenerator chartGenerator = new ChartGenerator();
      chartGenerator.generate_chart_for_admin(jPanel17,attendanceData,0);
      
      /////////////////////////////////
       Map<String, Integer> studentData = new HashMap<>();
       
            try {
            String sql= "SELECT SUM(CASE WHEN std_status = 1 THEN 1 ELSE 0 END) AS Total_Present, SUM(CASE WHEN std_status = 0 THEN 1 ELSE 0 END) AS Total_Absent FROM attendance WHERE att_l_date = CURDATE();";
            pst =conn.prepareStatement(sql);
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
       
       
      //chartGenerator.generate_chart_for_admin(jPanel17,attendanceData);
      chartGenerator.generate_pi_chart(jPanel18,studentData);
      
      ////////////////////////////////////////////////////////////
    
           Map<String, Integer> CourseData = new LinkedHashMap<>();
       
            try {
            String sql= "SELECT a.co_id AS Course_ID, CAST((SUM(CASE WHEN a.std_status = 1 THEN 1 ELSE 0 END) / COUNT(*)) * 100 AS UNSIGNED) AS Average_Attendance_Percentage, MAX(a.att_l_date) AS Latest_Attendance_Date FROM attendance a GROUP BY a.co_id ORDER BY Latest_Attendance_Date DESC LIMIT 15;";
            pst =conn.prepareStatement(sql);
            rs= pst.executeQuery();
            while (rs.next()) {
        String co=rs.getString("Course_ID");
        
        int avg= rs.getInt("Average_Attendance_Percentage");// Get the formatted date
        
        CourseData.put(co, avg);

    }
        } catch (Exception e) {JOptionPane.showMessageDialog(null, e);
        }
            chartGenerator.generate_chart_for_admin(jPanel23,CourseData,1); 
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
        welcome = new javax.swing.JPanel();
        jPanel24 = new javax.swing.JPanel();
        jButton9 = new javax.swing.JButton();
        jLabel85 = new javax.swing.JLabel();
        jPanel29 = new javax.swing.JPanel();
        jLabel90 = new javax.swing.JLabel();
        jLabel91 = new javax.swing.JLabel();
        jLabel92 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jPanel25 = new javax.swing.JPanel();
        jLabel73 = new javax.swing.JLabel();
        jPanel27 = new javax.swing.JPanel();
        jLabel38 = new javax.swing.JLabel();
        jLabel75 = new javax.swing.JLabel();
        jLabel74 = new javax.swing.JLabel();
        jLabel79 = new javax.swing.JLabel();
        jButton10 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jPanel26 = new javax.swing.JPanel();
        jButton8 = new javax.swing.JButton();
        jLabel84 = new javax.swing.JLabel();
        jPanel28 = new javax.swing.JPanel();
        jLabel69 = new javax.swing.JLabel();
        jLabel86 = new javax.swing.JLabel();
        jLabel87 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        ad1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtfac = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        txttable = new javax.swing.JTable();
        jPanel10 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txttable2 = new javax.swing.JTable();
        txtselectfac = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        txtfac1 = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        txtselectfac1 = new javax.swing.JComboBox<>();
        txtselectfac2 = new javax.swing.JComboBox<>();
        jPanel11 = new javax.swing.JPanel();
        jLabel48 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        ad4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        ad2 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel26 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        txttable4 = new javax.swing.JTable();
        jLabel27 = new javax.swing.JLabel();
        txtname = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        txtselectsem = new javax.swing.JComboBox<>();
        jLabel29 = new javax.swing.JLabel();
        txtselectmode = new javax.swing.JComboBox<>();
        txtcoid = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        txtselecttype = new javax.swing.JComboBox<>();
        jLabel31 = new javax.swing.JLabel();
        txtselectcre = new javax.swing.JComboBox<>();
        jLabel32 = new javax.swing.JLabel();
        txtselecthours = new javax.swing.JComboBox<>();
        jLabel33 = new javax.swing.JLabel();
        txtselectLeNO = new javax.swing.JComboBox<>();
        jPanel12 = new javax.swing.JPanel();
        jLabel50 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        datetxt = new javax.swing.JComboBox<>();
        jLabel45 = new javax.swing.JLabel();
        timetxt = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        student = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        txtroomid = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        txttablead3 = new javax.swing.JTable();
        jLabel34 = new javax.swing.JLabel();
        txtroomname = new javax.swing.JTextField();
        jPanel14 = new javax.swing.JPanel();
        jLabel53 = new javax.swing.JLabel();
        jLabel58 = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel35 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        ad3 = new javax.swing.JTable();
        txtselectroomid = new javax.swing.JComboBox<>();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        txtselectbatchno = new javax.swing.JComboBox<>();
        txtselectcoid3 = new javax.swing.JComboBox<>();
        txtroomname1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        txtroomId = new javax.swing.JTextField();
        jPanel16 = new javax.swing.JPanel();
        jLabel63 = new javax.swing.JLabel();
        jLabel64 = new javax.swing.JLabel();
        jLabel65 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        admin = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel39 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        txttable2ad4 = new javax.swing.JTable();
        txtselectlel_idad4 = new javax.swing.JComboBox<>();
        jLabel40 = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        jLabel60 = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        txtselectcoid4 = new javax.swing.JComboBox<>();
        jButton3 = new javax.swing.JButton();
        txtroomname2 = new javax.swing.JTextField();
        jButton4 = new javax.swing.JButton();
        txtroomname3 = new javax.swing.JTextField();
        jPanel9 = new javax.swing.JPanel();
        jLabel41 = new javax.swing.JLabel();
        txtlecid = new javax.swing.JTextField();
        jScrollPane7 = new javax.swing.JScrollPane();
        txttablead4 = new javax.swing.JTable();
        jLabel42 = new javax.swing.JLabel();
        txtlecname = new javax.swing.JTextField();
        jPanel13 = new javax.swing.JPanel();
        jLabel52 = new javax.swing.JLabel();
        jLabel56 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel71 = new javax.swing.JLabel();
        datavisual = new javax.swing.JPanel();
        jLabel76 = new javax.swing.JLabel();
        jPanel21 = new javax.swing.JPanel();
        jPanel17 = new javax.swing.JPanel();
        jPanel18 = new javax.swing.JPanel();
        jPanel23 = new javax.swing.JPanel();
        jLabel88 = new javax.swing.JLabel();
        report = new javax.swing.JPanel();
        jLabel80 = new javax.swing.JLabel();
        jLabel81 = new javax.swing.JLabel();
        jLabel83 = new javax.swing.JLabel();
        jPanel22 = new javax.swing.JPanel();
        jPanel19 = new javax.swing.JPanel();
        jLabel66 = new javax.swing.JLabel();
        txtlecid1 = new javax.swing.JTextField();
        jLabel67 = new javax.swing.JLabel();
        jLabel68 = new javax.swing.JLabel();
        txtLno11 = new javax.swing.JComboBox<>();
        jButton5 = new javax.swing.JButton();
        txtlecid2 = new javax.swing.JTextField();
        jPanel20 = new javax.swing.JPanel();
        jLabel70 = new javax.swing.JLabel();
        sem11 = new javax.swing.JComboBox<>();
        jLabel20 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        txtlecid3 = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setResizable(false);

        jPanel2.setBackground(new java.awt.Color(0, 102, 102));
        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(0, 255, 0), null, null));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        adminlable.setBackground(new java.awt.Color(255, 255, 255));
        adminlable.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        adminlable.setForeground(new java.awt.Color(255, 255, 255));
        adminlable.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        adminlable.setText("Admin");
        adminlable.setToolTipText("");
        adminlable.addMouseListener(new java.awt.event.MouseAdapter() {
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
        stdbtn.setText("Lecturer");
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
        jPanel2.add(stdbtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 390, 200, 70));

        lecLabel7.setBackground(new java.awt.Color(0, 102, 102));
        lecLabel7.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        lecLabel7.setForeground(new java.awt.Color(0, 153, 153));
        lecLabel7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lecLabel7.setText("Students");
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
        jPanel2.add(lecLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 630, 200, 70));

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

        welcome.setBackground(new java.awt.Color(255, 255, 255));
        welcome.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel24.setBackground(new java.awt.Color(255, 255, 255));
        jPanel24.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 102), 5));
        jPanel24.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jPanel24MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jPanel24MouseExited(evt);
            }
        });
        jPanel24.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton9.setBackground(new java.awt.Color(255, 255, 255));
        jButton9.setText("Click");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });
        jPanel24.add(jButton9, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 350, 220, 40));

        jLabel85.setBackground(new java.awt.Color(255, 255, 255));
        jLabel85.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel85.setForeground(new java.awt.Color(0, 102, 102));
        jLabel85.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel85.setText("Attendance Report ");
        jLabel85.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel24.add(jLabel85, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, 330, 72));

        jPanel29.setBackground(new java.awt.Color(255, 255, 255));

        jLabel90.setBackground(new java.awt.Color(0, 102, 102));
        jLabel90.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel90.setForeground(new java.awt.Color(0, 102, 102));
        jLabel90.setText("Semester-Wise Batch Report");
        jLabel90.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel91.setBackground(new java.awt.Color(0, 102, 102));
        jLabel91.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel91.setForeground(new java.awt.Color(0, 102, 102));
        jLabel91.setText("Course-wise Report");
        jLabel91.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel92.setBackground(new java.awt.Color(0, 102, 102));
        jLabel92.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel92.setForeground(new java.awt.Color(0, 102, 102));
        jLabel92.setText("Lesson-wise Report");

        javax.swing.GroupLayout jPanel29Layout = new javax.swing.GroupLayout(jPanel29);
        jPanel29.setLayout(jPanel29Layout);
        jPanel29Layout.setHorizontalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel29Layout.createSequentialGroup()
                .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel29Layout.createSequentialGroup()
                        .addGap(51, 51, 51)
                        .addComponent(jLabel91))
                    .addGroup(jPanel29Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel90))
                    .addGroup(jPanel29Layout.createSequentialGroup()
                        .addGap(55, 55, 55)
                        .addComponent(jLabel92)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel29Layout.setVerticalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel29Layout.createSequentialGroup()
                .addContainerGap(24, Short.MAX_VALUE)
                .addComponent(jLabel91)
                .addGap(38, 38, 38)
                .addComponent(jLabel90)
                .addGap(40, 40, 40)
                .addComponent(jLabel92)
                .addGap(29, 29, 29))
        );

        jPanel24.add(jPanel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 110, -1, -1));

        jLabel17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/re.png"))); // NOI18N
        jPanel24.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 390, 200, 200));

        welcome.add(jPanel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(910, 160, 350, 650));

        jPanel25.setBackground(new java.awt.Color(255, 255, 255));
        jPanel25.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 102), 5));
        jPanel25.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jPanel25MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jPanel25MouseExited(evt);
            }
        });

        jLabel73.setBackground(new java.awt.Color(255, 255, 255));
        jLabel73.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel73.setForeground(new java.awt.Color(0, 102, 102));
        jLabel73.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel73.setText("Academic Admin Operations");
        jLabel73.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jPanel27.setBackground(new java.awt.Color(255, 255, 255));

        jLabel38.setBackground(new java.awt.Color(0, 102, 102));
        jLabel38.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel38.setForeground(new java.awt.Color(0, 102, 102));
        jLabel38.setText("Room Allocation");
        jLabel38.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel75.setBackground(new java.awt.Color(0, 102, 102));
        jLabel75.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel75.setForeground(new java.awt.Color(0, 102, 102));
        jLabel75.setText("Course Management");
        jLabel75.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel74.setBackground(new java.awt.Color(0, 102, 102));
        jLabel74.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel74.setForeground(new java.awt.Color(0, 102, 102));
        jLabel74.setText("Batch & Faculty Management ");
        jLabel74.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel79.setBackground(new java.awt.Color(0, 102, 102));
        jLabel79.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel79.setForeground(new java.awt.Color(0, 102, 102));
        jLabel79.setText("Lecture Scheduling ");

        javax.swing.GroupLayout jPanel27Layout = new javax.swing.GroupLayout(jPanel27);
        jPanel27.setLayout(jPanel27Layout);
        jPanel27Layout.setHorizontalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel27Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel74, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel27Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel79)
                            .addComponent(jLabel75)
                            .addGroup(jPanel27Layout.createSequentialGroup()
                                .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(9, 9, 9)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel27Layout.setVerticalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel27Layout.createSequentialGroup()
                .addContainerGap(24, Short.MAX_VALUE)
                .addComponent(jLabel74)
                .addGap(30, 30, 30)
                .addComponent(jLabel75)
                .addGap(26, 26, 26)
                .addComponent(jLabel38)
                .addGap(29, 29, 29)
                .addComponent(jLabel79))
        );

        jButton10.setBackground(new java.awt.Color(255, 255, 255));
        jButton10.setText("Click");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/logmini.png"))); // NOI18N

        javax.swing.GroupLayout jPanel25Layout = new javax.swing.GroupLayout(jPanel25);
        jPanel25.setLayout(jPanel25Layout);
        jPanel25Layout.setHorizontalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel73, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel25Layout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addComponent(jPanel27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel25Layout.createSequentialGroup()
                        .addGap(64, 64, 64)
                        .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(20, 20, 20))
        );
        jPanel25Layout.setVerticalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel73, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(52, 52, 52)
                .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        welcome.add(jPanel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 160, 360, 650));

        jPanel26.setBackground(new java.awt.Color(255, 255, 255));
        jPanel26.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 102), 5));
        jPanel26.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jPanel26MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jPanel26MouseExited(evt);
            }
        });
        jPanel26.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton8.setBackground(new java.awt.Color(255, 255, 255));
        jButton8.setText("Click");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });
        jPanel26.add(jButton8, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 350, 220, 40));

        jLabel84.setBackground(new java.awt.Color(255, 255, 255));
        jLabel84.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel84.setForeground(new java.awt.Color(0, 102, 102));
        jLabel84.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel84.setText("Attendance Overview");
        jLabel84.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel26.add(jLabel84, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 30, 350, 72));

        jPanel28.setBackground(new java.awt.Color(255, 255, 255));

        jLabel69.setBackground(new java.awt.Color(0, 102, 102));
        jLabel69.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel69.setForeground(new java.awt.Color(0, 102, 102));
        jLabel69.setText("Courses with the Lowest Attendance ");
        jLabel69.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel86.setBackground(new java.awt.Color(0, 102, 102));
        jLabel86.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel86.setForeground(new java.awt.Color(0, 102, 102));
        jLabel86.setText("Today's Attendance");
        jLabel86.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel87.setBackground(new java.awt.Color(0, 102, 102));
        jLabel87.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel87.setForeground(new java.awt.Color(0, 102, 102));
        jLabel87.setText("Attendance Overview");
        jLabel87.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel28Layout = new javax.swing.GroupLayout(jPanel28);
        jPanel28.setLayout(jPanel28Layout);
        jPanel28Layout.setHorizontalGroup(
            jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel28Layout.createSequentialGroup()
                .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel28Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel69, javax.swing.GroupLayout.PREFERRED_SIZE, 303, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel28Layout.createSequentialGroup()
                        .addGap(59, 59, 59)
                        .addComponent(jLabel87))
                    .addGroup(jPanel28Layout.createSequentialGroup()
                        .addGap(64, 64, 64)
                        .addComponent(jLabel86)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel28Layout.setVerticalGroup(
            jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel28Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabel87)
                .addGap(32, 32, 32)
                .addComponent(jLabel69)
                .addGap(31, 31, 31)
                .addComponent(jLabel86)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel26.add(jPanel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 110, 299, -1));

        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/graph.png"))); // NOI18N
        jPanel26.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 390, 300, 200));

        welcome.add(jPanel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 160, 350, 650));

        jTabbedPane1.addTab("0", welcome);

        ad1.setBackground(new java.awt.Color(255, 255, 255));
        ad1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 102), 3), "Faculty Section", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 13), new java.awt.Color(0, 102, 102))); // NOI18N
        jPanel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel3MouseClicked(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 102, 102));
        jLabel3.setText("Add Faculty");

        txtfac.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        txtfac.setForeground(new java.awt.Color(0, 102, 102));

        txttable.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txttable.setForeground(new java.awt.Color(0, 102, 102));
        txttable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null}
            },
            new String [] {
                "Faculty"
            }
        ));
        txttable.setFocusable(false);
        txttable.setRowHeight(25);
        txttable.setSelectionBackground(new java.awt.Color(0, 153, 153));
        txttable.setShowVerticalLines(false);
        txttable.addContainerListener(new java.awt.event.ContainerAdapter() {
            public void componentRemoved(java.awt.event.ContainerEvent evt) {
                txttableComponentRemoved(evt);
            }
        });
        txttable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txttableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(txttable);

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));

        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/add.png"))); // NOI18N
        jLabel12.setToolTipText("Add");
        jLabel12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel12MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel12MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabel12MouseExited(evt);
            }
        });

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/delet.png"))); // NOI18N
        jLabel2.setToolTipText("Delete");
        jLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel2MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel2MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabel2MouseExited(evt);
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

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(47, 47, 47)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(25, 25, 25))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(69, 69, 69)
                .addComponent(jLabel3)
                .addGap(43, 43, 43)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtfac, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE))
                .addGap(151, 151, 151)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtfac, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addGap(28, 28, 28)
                        .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        ad1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 130, 1110, 230));

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 102), 3), "Batch Section", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 13), new java.awt.Color(0, 102, 102))); // NOI18N
        jPanel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel5MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jPanel5MouseEntered(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 102, 102));
        jLabel5.setText("Select Faculty");

        txttable2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txttable2.setForeground(new java.awt.Color(0, 102, 102));
        txttable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Faculty", "Batch No", "Batch Status", "Onging Semester"
            }
        ));
        txttable2.setRowHeight(25);
        txttable2.setSelectionBackground(new java.awt.Color(0, 153, 153));
        txttable2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txttable2MouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(txttable2);

        txtselectfac.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        txtselectfac.setForeground(new java.awt.Color(0, 102, 102));
        txtselectfac.setFocusable(false);

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 102, 102));
        jLabel6.setText("Batch No");

        txtfac1.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        txtfac1.setForeground(new java.awt.Color(0, 102, 102));
        txtfac1.setAlignmentX(0.8F);

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(0, 102, 102));
        jLabel11.setText("Batch Status");

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(0, 102, 102));
        jLabel14.setText("Onging Sem");

        txtselectfac1.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        txtselectfac1.setForeground(new java.awt.Color(0, 102, 102));
        txtselectfac1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1st Year 1st Semester", "1st Year 2nd Semester", "2nd Year 1st Semester", "2nd Year 2nd Semester", "3rd Year 1st Semester", "3rd Year 2nd Semester", "4th Year 1st Semester", "4th Year 2nd Semester", "None" }));
        txtselectfac1.setSelectedIndex(-1);
        txtselectfac1.setToolTipText("in this Onging Sem you need to up date when new semester start");
        txtselectfac1.setFocusable(false);

        txtselectfac2.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        txtselectfac2.setForeground(new java.awt.Color(0, 102, 102));
        txtselectfac2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Active", "Deactive" }));
        txtselectfac2.setSelectedIndex(-1);
        txtselectfac2.setFocusable(false);

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));

        jLabel48.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/add.png"))); // NOI18N
        jLabel48.setToolTipText("Add");
        jLabel48.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel48MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel48MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabel48MouseExited(evt);
            }
        });

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/delet.png"))); // NOI18N
        jLabel8.setToolTipText("Delete");
        jLabel8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel8MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel8MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabel8MouseExited(evt);
            }
        });

        jLabel49.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/edit.png"))); // NOI18N
        jLabel49.setToolTipText("Update");
        jLabel49.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel49MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel49MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabel49MouseExited(evt);
            }
        });

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 49, Short.MAX_VALUE)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48)
                .addComponent(jLabel49, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel49, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel11Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel48))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(204, 204, 204)
                        .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(79, 79, 79)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14)
                            .addComponent(jLabel6)
                            .addComponent(jLabel11)
                            .addComponent(jLabel5))
                        .addGap(29, 29, 29)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtselectfac, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtfac1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtselectfac2, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtselectfac1, javax.swing.GroupLayout.Alignment.TRAILING, 0, 236, Short.MAX_VALUE))))
                .addGap(139, 139, 139)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 496, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(66, 66, 66)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtselectfac, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addGap(18, 18, Short.MAX_VALUE)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtfac1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtselectfac2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11))
                        .addGap(31, 31, 31)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtselectfac1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14))
                        .addGap(18, 18, 18)
                        .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(29, 29, 29))
        );

        txtselectfac.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                java.awt.Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    c.setBackground(new java.awt.Color(0, 153, 153)); // #009999 color
                }
                return c;
            }
        });
        txtselectfac1.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                java.awt.Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    c.setBackground(new java.awt.Color(0, 153, 153)); // #009999 color
                }
                return c;
            }
        });
        txtselectfac2.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                java.awt.Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    c.setBackground(new java.awt.Color(0, 153, 153)); // #009999 color
                }
                return c;
            }
        });

        ad1.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 410, 1110, 400));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 102, 102));
        jLabel4.setText("1");
        ad1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 890, -1, -1));

        ad4.setBackground(new java.awt.Color(255, 255, 255));
        ad4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 102, 102));
        jLabel1.setText("Academic Admin Operations");
        ad4.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 50, -1, -1));

        ad1.add(ad4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1210, -1));

        jLabel51.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel51.setForeground(new java.awt.Color(0, 102, 102));
        jLabel51.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel51.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/nextbtn.png"))); // NOI18N
        jLabel51.setText("Next");
        jLabel51.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLabel51.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel51MouseClicked(evt);
            }
        });
        ad1.add(jLabel51, new org.netbeans.lib.awtextra.AbsoluteConstraints(1080, 860, -1, -1));

        jLabel46.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel46.setForeground(new java.awt.Color(0, 102, 102));
        jLabel46.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel46.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/backbtn.png"))); // NOI18N
        jLabel46.setText("Back");
        jLabel46.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jLabel46.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel46MouseClicked(evt);
            }
        });
        ad1.add(jLabel46, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 860, -1, -1));

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

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 102), 3), "Course Section", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 13), new java.awt.Color(0, 102, 102))); // NOI18N
        jPanel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel7MouseClicked(evt);
            }
        });

        jLabel26.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(0, 102, 102));
        jLabel26.setText("Course ID");

        txttable4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txttable4.setForeground(new java.awt.Color(0, 102, 102));
        txttable4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {}
            },
            new String [] {

            }
        ));
        txttable4.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        txttable4.setRowHeight(25);
        txttable4.setSelectionBackground(new java.awt.Color(0, 153, 153));
        txttable4.setShowVerticalLines(false);
        txttable4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txttable4MouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(txttable4);

        jLabel27.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(0, 102, 102));
        jLabel27.setText("Course Name");

        txtname.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtname.setForeground(new java.awt.Color(0, 102, 102));

        jLabel28.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(0, 102, 102));
        jLabel28.setText("Related Sem");

        txtselectsem.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtselectsem.setForeground(new java.awt.Color(0, 102, 102));
        txtselectsem.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1st Year 1st Semester", "1st Year 2nd Semester", "2nd Year 1st Semester", "2nd Year 2nd Semester", "3rd Year 1st Semester", "3rd Year 2nd Semester", "4th Year 1st Semester", "4th Year 2nd Semester", "None" }));
        txtselectsem.setSelectedIndex(-1);
        txtselectsem.setToolTipText("Select");

        jLabel29.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(0, 102, 102));
        jLabel29.setText("Course Mode");

        txtselectmode.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtselectmode.setForeground(new java.awt.Color(0, 102, 102));
        txtselectmode.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Physical", "Online", "Hybrid" }));
        txtselectmode.setSelectedIndex(-1);
        txtselectmode.setToolTipText("Select");

        txtcoid.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtcoid.setForeground(new java.awt.Color(0, 102, 102));

        jLabel30.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(0, 102, 102));
        jLabel30.setText("Course Type");

        txtselecttype.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtselecttype.setForeground(new java.awt.Color(0, 102, 102));
        txtselecttype.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Theory", "Practical", "Hybrid" }));
        txtselecttype.setSelectedIndex(-1);
        txtselecttype.setToolTipText("Select");

        jLabel31.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(0, 102, 102));
        jLabel31.setText("Credit Value");

        txtselectcre.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtselectcre.setForeground(new java.awt.Color(0, 102, 102));
        txtselectcre.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0", "1", "2", "3", "4", "5", "6", "" }));
        txtselectcre.setSelectedIndex(-1);
        txtselectcre.setToolTipText("Select");

        jLabel32.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel32.setForeground(new java.awt.Color(0, 102, 102));
        jLabel32.setText("Hours");

        txtselecthours.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtselecthours.setForeground(new java.awt.Color(0, 102, 102));
        txtselecthours.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0", "1", "2", "3", "4", "5", "6" }));
        txtselecthours.setSelectedIndex(-1);
        txtselecthours.setToolTipText("Select");

        jLabel33.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel33.setForeground(new java.awt.Color(0, 102, 102));
        jLabel33.setText("NO of Lesson");

        txtselectLeNO.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtselectLeNO.setForeground(new java.awt.Color(0, 102, 102));
        txtselectLeNO.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "10", "11", "12", "13", "14", "15" }));
        txtselectLeNO.setSelectedIndex(-1);
        txtselectLeNO.setToolTipText("Select");

        jPanel12.setBackground(new java.awt.Color(255, 255, 255));

        jLabel50.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/add.png"))); // NOI18N
        jLabel50.setToolTipText("Add");
        jLabel50.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel50MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel50MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabel50MouseExited(evt);
            }
        });

        jLabel54.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/delet.png"))); // NOI18N
        jLabel54.setToolTipText("Delete");
        jLabel54.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel54MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel54MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabel54MouseExited(evt);
            }
        });

        jLabel55.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/edit.png"))); // NOI18N
        jLabel55.setToolTipText("Update");
        jLabel55.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel55MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel55MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabel55MouseExited(evt);
            }
        });

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addComponent(jLabel50, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37)
                .addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 56, Short.MAX_VALUE)
                .addComponent(jLabel55, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel55, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel12Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel50))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        datetxt.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        datetxt.setForeground(new java.awt.Color(0, 102, 102));
        datetxt.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday" }));
        datetxt.setSelectedIndex(-1);
        datetxt.setToolTipText("Select");

        jLabel45.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel45.setForeground(new java.awt.Color(0, 102, 102));
        jLabel45.setText("DATE & TIME");

        timetxt.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        timetxt.setForeground(new java.awt.Color(0, 102, 102));
        timetxt.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "08.00 A.M", "09.00 A.M", "10.00 A.M", "11.00 A.M", "1.00 P.M", "2.00 P.M", "3.00 P.M", "4.00 P.M" }));
        timetxt.setSelectedIndex(-1);
        timetxt.setToolTipText("Select");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel7Layout.createSequentialGroup()
                            .addComponent(jLabel26)
                            .addGap(6, 6, 6))
                        .addComponent(jLabel27)
                        .addComponent(jLabel28)
                        .addComponent(jLabel29)
                        .addComponent(jLabel31)
                        .addComponent(jLabel32)
                        .addComponent(jLabel30)
                        .addComponent(jLabel33))
                    .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 67, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(txtcoid, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
                        .addComponent(txtname, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
                        .addComponent(txtselectmode, javax.swing.GroupLayout.Alignment.TRAILING, 0, 222, Short.MAX_VALUE)
                        .addComponent(txtselectcre, javax.swing.GroupLayout.Alignment.TRAILING, 0, 222, Short.MAX_VALUE)
                        .addComponent(txtselecthours, javax.swing.GroupLayout.Alignment.TRAILING, 0, 222, Short.MAX_VALUE)
                        .addComponent(txtselecttype, javax.swing.GroupLayout.Alignment.TRAILING, 0, 222, Short.MAX_VALUE)
                        .addComponent(txtselectLeNO, javax.swing.GroupLayout.Alignment.TRAILING, 0, 222, Short.MAX_VALUE)
                        .addComponent(txtselectsem, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel7Layout.createSequentialGroup()
                            .addComponent(datetxt, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(timetxt, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(120, 120, 120)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 553, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(txtcoid, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtname, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtselectsem, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtselectmode, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtselecttype, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtselectcre, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtselecthours, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel32))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtselectLeNO, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(datetxt, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(timetxt, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(35, Short.MAX_VALUE)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 612, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 48, Short.MAX_VALUE))
        );

        txtselectsem.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                java.awt.Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    c.setBackground(new java.awt.Color(0, 153, 153)); // #009999 color
                }
                return c;
            }
        });
        txtselectmode.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                java.awt.Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    c.setBackground(new java.awt.Color(0, 153, 153)); // #009999 color
                }
                return c;
            }
        });
        txtselecttype.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                java.awt.Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    c.setBackground(new java.awt.Color(0, 153, 153)); // #009999 color
                }
                return c;
            }
        });
        txtselectcre.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                java.awt.Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    c.setBackground(new java.awt.Color(0, 153, 153)); // #009999 color
                }
                return c;
            }
        });
        txtselecthours.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                java.awt.Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    c.setBackground(new java.awt.Color(0, 153, 153)); // #009999 color
                }
                return c;
            }
        });
        txtselectLeNO.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                java.awt.Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    c.setBackground(new java.awt.Color(0, 153, 153)); // #009999 color
                }
                return c;
            }
        });
        datetxt.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                java.awt.Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    c.setBackground(new java.awt.Color(0, 153, 153)); // #009999 color
                }
                return c;
            }
        });
        timetxt.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                java.awt.Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    c.setBackground(new java.awt.Color(0, 153, 153)); // #009999 color
                }
                return c;
            }
        });

        ad2.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 110, 1120, 720));

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 102, 102));
        jLabel10.setText("Academic Admin Operations");
        ad2.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 50, -1, -1));

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
        jLabel25.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLabel25.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel25MouseClicked(evt);
            }
        });
        student.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(1080, 860, -1, -1));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 102), 3), "Room Section", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 13), new java.awt.Color(0, 102, 102))); // NOI18N
        jPanel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel4MouseClicked(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(0, 102, 102));
        jLabel19.setText("Add Room Id");

        txtroomid.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtroomid.setForeground(new java.awt.Color(0, 102, 102));

        txttablead3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txttablead3.setForeground(new java.awt.Color(0, 102, 102));
        txttablead3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null}
            },
            new String [] {
                "Room Id", "Room Name"
            }
        ));
        txttablead3.setRowHeight(20);
        txttablead3.setSelectionBackground(new java.awt.Color(0, 153, 153));
        txttablead3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txttablead3MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(txttablead3);

        jLabel34.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel34.setForeground(new java.awt.Color(0, 102, 102));
        jLabel34.setText("Add Room Name");

        txtroomname.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtroomname.setForeground(new java.awt.Color(0, 102, 102));

        jPanel14.setBackground(new java.awt.Color(255, 255, 255));

        jLabel53.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/add.png"))); // NOI18N
        jLabel53.setToolTipText("Add");
        jLabel53.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel53MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel53MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabel53MouseExited(evt);
            }
        });

        jLabel58.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/delet.png"))); // NOI18N
        jLabel58.setToolTipText("Delete");
        jLabel58.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel58MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel58MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabel58MouseExited(evt);
            }
        });

        jLabel59.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/edit.png"))); // NOI18N
        jLabel59.setToolTipText("Update");
        jLabel59.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel59MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel59MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabel59MouseExited(evt);
            }
        });

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel53, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                .addComponent(jLabel58, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48)
                .addComponent(jLabel59, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel59, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel14Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel58, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel53))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(58, 58, 58)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtroomid, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(txtroomname, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(77, 77, 77)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 368, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel19)
                                    .addComponent(txtroomid, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel34)
                                    .addComponent(txtroomname, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(25, Short.MAX_VALUE))
        );

        student.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 150, 1160, 240));

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 102), 3), "Room Allocation Section", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 13), new java.awt.Color(0, 102, 102))); // NOI18N
        jPanel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel6MouseClicked(evt);
            }
        });

        jLabel35.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel35.setForeground(new java.awt.Color(0, 102, 102));
        jLabel35.setText("Room Id");

        ad3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        ad3.setForeground(new java.awt.Color(0, 102, 102));
        ad3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null}
            },
            new String [] {
                "Room Id", "Course Id", "Batch No"
            }
        ));
        ad3.setGridColor(new java.awt.Color(255, 255, 255));
        ad3.setRowHeight(20);
        ad3.setSelectionBackground(new java.awt.Color(0, 153, 153));
        ad3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ad3MouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(ad3);

        txtselectroomid.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtselectroomid.setForeground(new java.awt.Color(0, 102, 102));
        txtselectroomid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtselectroomidActionPerformed(evt);
            }
        });

        jLabel36.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel36.setForeground(new java.awt.Color(0, 102, 102));
        jLabel36.setText("Course Id");

        jLabel37.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel37.setForeground(new java.awt.Color(0, 102, 102));
        jLabel37.setText("Batch No");

        txtselectbatchno.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtselectbatchno.setForeground(new java.awt.Color(0, 102, 102));

        txtselectcoid3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtselectcoid3.setForeground(new java.awt.Color(0, 102, 102));
        txtselectcoid3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtselectcoid3ActionPerformed(evt);
            }
        });

        txtroomname1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtroomname1.setForeground(new java.awt.Color(0, 102, 102));

        jButton1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(0, 102, 102));
        jButton1.setText("search");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButton2.setForeground(new java.awt.Color(0, 102, 102));
        jButton2.setText("search");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        txtroomId.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtroomId.setForeground(new java.awt.Color(0, 102, 102));

        jPanel16.setBackground(new java.awt.Color(255, 255, 255));

        jLabel63.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/add.png"))); // NOI18N
        jLabel63.setToolTipText("Add");
        jLabel63.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel63MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel63MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabel63MouseExited(evt);
            }
        });

        jLabel64.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/delet.png"))); // NOI18N
        jLabel64.setToolTipText("Delete");
        jLabel64.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel64MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel64MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabel64MouseExited(evt);
            }
        });

        jLabel65.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/edit.png"))); // NOI18N
        jLabel65.setToolTipText("Update");
        jLabel65.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel65MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel65MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabel65MouseExited(evt);
            }
        });

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel63, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel64, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48)
                .addComponent(jLabel65, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel65, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel16Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel64, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel63))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addGap(127, 127, 127)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel36)
                    .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel37))
                .addGap(10, 10, 10)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtroomname1, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtroomId, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(3, 3, 3)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButton1)
                            .addComponent(jButton2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtselectcoid3, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtselectroomid, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 5, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(txtselectbatchno, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(39, 39, 39)
                        .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(56, 56, 56)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 368, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 322, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(58, 58, 58)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel36)
                                    .addComponent(txtroomname1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtselectcoid3, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(30, 30, 30)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtselectroomid, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel35)
                                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtroomId, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(44, 44, 44)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(txtselectbatchno, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel37, javax.swing.GroupLayout.Alignment.LEADING)))
                            .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(31, Short.MAX_VALUE))
        );

        txtselectroomid.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                java.awt.Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    c.setBackground(new java.awt.Color(0, 153, 153)); // #009999 color
                }
                return c;
            }
        });
        txtselectbatchno.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                java.awt.Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    c.setBackground(new java.awt.Color(0, 153, 153)); // #009999 color
                }
                return c;
            }
        });
        txtselectcoid3.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                java.awt.Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    c.setBackground(new java.awt.Color(0, 153, 153)); // #009999 color
                }
                return c;
            }
        });

        student.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 430, 1160, 400));

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(0, 102, 102));
        jLabel21.setText("Academic Admin Operations");
        student.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 50, -1, -1));

        jTabbedPane1.addTab("3", student);

        admin.setBackground(new java.awt.Color(255, 255, 255));
        admin.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 102), 3), "Lectures Course Registation", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 13), new java.awt.Color(0, 102, 102))); // NOI18N
        jPanel8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel8MouseClicked(evt);
            }
        });

        jLabel39.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel39.setForeground(new java.awt.Color(0, 102, 102));
        jLabel39.setText("Lecture Id");

        txttable2ad4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txttable2ad4.setForeground(new java.awt.Color(0, 153, 153));
        txttable2ad4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null}
            },
            new String [] {
                "Course Id", "Lecture Id"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                true, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        txttable2ad4.setRowHeight(20);
        txttable2ad4.setSelectionBackground(new java.awt.Color(0, 153, 153));
        txttable2ad4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txttable2ad4MouseClicked(evt);
            }
        });
        jScrollPane6.setViewportView(txttable2ad4);

        txtselectlel_idad4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtselectlel_idad4.setForeground(new java.awt.Color(0, 102, 102));
        txtselectlel_idad4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtselectlel_idad4ActionPerformed(evt);
            }
        });

        jLabel40.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel40.setForeground(new java.awt.Color(0, 102, 102));
        jLabel40.setText("Course Id");

        jPanel15.setBackground(new java.awt.Color(255, 255, 255));

        jLabel60.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/add.png"))); // NOI18N
        jLabel60.setToolTipText("Add");
        jLabel60.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel60MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel60MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabel60MouseExited(evt);
            }
        });

        jLabel61.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/delet.png"))); // NOI18N
        jLabel61.setToolTipText("Delete");
        jLabel61.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel61MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel61MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabel61MouseExited(evt);
            }
        });

        jLabel62.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/edit.png"))); // NOI18N
        jLabel62.setToolTipText("Update");
        jLabel62.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel62MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel62MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabel62MouseExited(evt);
            }
        });

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel60, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                .addComponent(jLabel61, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48)
                .addComponent(jLabel62, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel62, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel15Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel61, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel60))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        txtselectcoid4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtselectcoid4.setForeground(new java.awt.Color(0, 102, 102));
        txtselectcoid4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtselectcoid4ActionPerformed(evt);
            }
        });

        jButton3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jButton3.setForeground(new java.awt.Color(0, 102, 102));
        jButton3.setText("search");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        txtroomname2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtroomname2.setForeground(new java.awt.Color(0, 102, 102));

        jButton4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jButton4.setForeground(new java.awt.Color(0, 102, 102));
        jButton4.setText("search");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        txtroomname3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtroomname3.setForeground(new java.awt.Color(0, 102, 102));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(114, 114, 114)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel40)
                        .addGap(5, 5, 5)
                        .addComponent(txtroomname2, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtselectcoid4, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel39)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(txtroomname3, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtselectlel_idad4, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(13, 13, 13)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 56, Short.MAX_VALUE)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 368, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap(63, Short.MAX_VALUE)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel40)
                        .addGap(78, 78, 78))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtroomname2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtselectcoid4, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(64, 64, 64)))
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtroomname3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtselectlel_idad4, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel39))
                .addGap(18, 18, 18)
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        txtselectlel_idad4.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                java.awt.Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    c.setBackground(new java.awt.Color(0, 153, 153)); // #009999 color
                }
                return c;
            }
        });
        txtselectcoid4.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                java.awt.Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    c.setBackground(new java.awt.Color(0, 153, 153)); // #009999 color
                }
                return c;
            }
        });

        admin.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 450, 1130, 360));

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));
        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 102), 3), "Lectures Section", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 13), new java.awt.Color(0, 102, 102))); // NOI18N
        jPanel9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel9MouseClicked(evt);
            }
        });

        jLabel41.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel41.setForeground(new java.awt.Color(0, 102, 102));
        jLabel41.setText("Add Lecturer Id");

        txtlecid.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtlecid.setForeground(new java.awt.Color(0, 102, 102));

        txttablead4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txttablead4.setForeground(new java.awt.Color(0, 102, 102));
        txttablead4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null}
            },
            new String [] {
                "Lectures Id", "Lectures Name"
            }
        ));
        txttablead4.setRowHeight(20);
        txttablead4.setSelectionBackground(new java.awt.Color(0, 153, 153));
        txttablead4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txttablead4MouseClicked(evt);
            }
        });
        jScrollPane7.setViewportView(txttablead4);

        jLabel42.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel42.setForeground(new java.awt.Color(0, 102, 102));
        jLabel42.setText("Add Lecturer Name");

        txtlecname.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtlecname.setForeground(new java.awt.Color(0, 102, 102));

        jPanel13.setBackground(new java.awt.Color(255, 255, 255));

        jLabel52.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/add.png"))); // NOI18N
        jLabel52.setToolTipText("Add");
        jLabel52.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel52MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel52MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabel52MouseExited(evt);
            }
        });

        jLabel56.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/delet.png"))); // NOI18N
        jLabel56.setToolTipText("Delete");
        jLabel56.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel56MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel56MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabel56MouseExited(evt);
            }
        });

        jLabel57.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/edit.png"))); // NOI18N
        jLabel57.setToolTipText("Update");
        jLabel57.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel57MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel57MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabel57MouseExited(evt);
            }
        });

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel52, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                .addComponent(jLabel56, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48)
                .addComponent(jLabel57, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel57, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel13Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel56, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel52))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel41)
                    .addComponent(jLabel42))
                .addGap(18, 18, 18)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtlecid, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(txtlecname, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(70, 70, 70)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 368, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel41)
                            .addComponent(txtlecid, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(txtlecname)
                                .addGap(2, 2, 2))
                            .addComponent(jPanel13, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        admin.add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 140, 1130, 270));

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

        jLabel71.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        jLabel71.setForeground(new java.awt.Color(0, 102, 102));
        jLabel71.setText("Academic Admin Operations");
        admin.add(jLabel71, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 50, -1, -1));

        jTabbedPane1.addTab("4", admin);

        datavisual.setBackground(new java.awt.Color(255, 255, 255));
        datavisual.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel76.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel76.setForeground(new java.awt.Color(0, 102, 102));
        jLabel76.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel76.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/backbtn.png"))); // NOI18N
        jLabel76.setText("Back");
        jLabel76.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jLabel76.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel76MouseClicked(evt);
            }
        });
        datavisual.add(jLabel76, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 860, -1, -1));

        jPanel21.setBackground(new java.awt.Color(255, 255, 255));
        jPanel21.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 102), 3));

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 369, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 295, Short.MAX_VALUE)
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 271, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 701, Short.MAX_VALUE)
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(jPanel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 131, Short.MAX_VALUE))
                    .addComponent(jPanel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        datavisual.add(jPanel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 30, 1080, 810));

        jLabel88.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel88.setForeground(new java.awt.Color(0, 102, 102));
        jLabel88.setText("1");
        datavisual.add(jLabel88, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 880, -1, -1));

        jTabbedPane1.addTab("5", datavisual);

        report.setBackground(new java.awt.Color(255, 255, 255));
        report.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel80.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel80.setForeground(new java.awt.Color(0, 102, 102));
        jLabel80.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel80.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/backbtn.png"))); // NOI18N
        jLabel80.setText("Back");
        jLabel80.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jLabel80.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel80MouseClicked(evt);
            }
        });
        report.add(jLabel80, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 860, -1, -1));

        jLabel81.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel81.setForeground(new java.awt.Color(0, 102, 102));
        jLabel81.setText("1");
        report.add(jLabel81, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 880, -1, -1));

        jLabel83.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        jLabel83.setForeground(new java.awt.Color(0, 102, 102));
        jLabel83.setText("Attendance Report Generator");
        report.add(jLabel83, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 120, -1, -1));

        jPanel22.setBackground(new java.awt.Color(255, 255, 255));
        jPanel22.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 102), 3));

        jPanel19.setBackground(new java.awt.Color(255, 255, 255));
        jPanel19.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 102), 3), "Print information for a specific course", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 13), new java.awt.Color(0, 102, 102))); // NOI18N

        jLabel66.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel66.setForeground(new java.awt.Color(0, 102, 102));
        jLabel66.setText("Course ID");

        txtlecid1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtlecid1.setForeground(new java.awt.Color(0, 102, 102));
        txtlecid1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtlecid1ActionPerformed(evt);
            }
        });

        jLabel67.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel67.setForeground(new java.awt.Color(0, 102, 102));
        jLabel67.setText("Bg No");

        jLabel68.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel68.setForeground(new java.awt.Color(0, 102, 102));
        jLabel68.setText("Lecture NO");

        txtLno11.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtLno11.setForeground(new java.awt.Color(0, 102, 102));
        txtLno11.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", " " }));
        txtLno11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtLno11ActionPerformed(evt);
            }
        });

        jButton5.setBackground(new java.awt.Color(255, 255, 255));
        jButton5.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jButton5.setForeground(new java.awt.Color(0, 102, 102));
        jButton5.setText("Print Attendance");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        txtlecid2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtlecid2.setForeground(new java.awt.Color(0, 102, 102));

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addGap(60, 60, 60)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel67)
                    .addComponent(jLabel68)
                    .addComponent(jLabel66))
                .addGap(48, 48, 48)
                .addComponent(txtlecid2, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(42, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel19Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel19Layout.createSequentialGroup()
                        .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtLno11, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtlecid1, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(41, 41, 41))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel19Layout.createSequentialGroup()
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 364, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26))))
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel19Layout.createSequentialGroup()
                .addGap(54, 54, 54)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel66)
                    .addComponent(txtlecid1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(34, 34, 34)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel67)
                    .addComponent(txtlecid2, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtLno11, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel68))
                .addGap(29, 29, 29)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(62, Short.MAX_VALUE))
        );

        // Code adding the component to the parent container - not shown here
        txtLno11.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                java.awt.Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    c.setBackground(new java.awt.Color(0, 153, 153)); // #009999 color
                }
                return c;
            }
        });

        jPanel20.setBackground(new java.awt.Color(255, 255, 255));
        jPanel20.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 102), 3), "Print information for all course one by one", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 13), new java.awt.Color(0, 102, 102))); // NOI18N

        jLabel70.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel70.setForeground(new java.awt.Color(0, 102, 102));
        jLabel70.setText("Bg No");

        sem11.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        sem11.setForeground(new java.awt.Color(0, 102, 102));
        sem11.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1st Year 1st Semester", "1st Year 2nd Semester", "2nd Year 1st Semester", "2nd Year 2nd Semester", "3rd Year 1st Semester", "3rd Year 2nd Semester", "4th Year 1st Semester", "4th Year 2nd Semester", "None" }));
        sem11.setToolTipText("in this Onging Sem you need to up date when new semester start");
        sem11.setFocusable(false);
        sem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sem11ActionPerformed(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(0, 102, 102));
        jLabel20.setText("Semester");

        jButton6.setBackground(new java.awt.Color(255, 255, 255));
        jButton6.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jButton6.setForeground(new java.awt.Color(0, 102, 102));
        jButton6.setText("Print Attendance");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        txtlecid3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtlecid3.setForeground(new java.awt.Color(0, 102, 102));

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addContainerGap(35, Short.MAX_VALUE)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel20Layout.createSequentialGroup()
                        .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel70, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(42, 42, 42)
                        .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(sem11, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtlecid3, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(39, 39, 39))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel20Layout.createSequentialGroup()
                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 357, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(24, 24, 24))))
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addGap(61, 61, 61)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel70)
                    .addComponent(txtlecid3, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(45, 45, 45)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sem11, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 64, Short.MAX_VALUE)
                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(59, 59, 59))
        );

        sem11.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                java.awt.Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    c.setBackground(new java.awt.Color(0, 153, 153)); // #009999 color
                }
                return c;
            }
        });

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addContainerGap(75, Short.MAX_VALUE)
                .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(78, 78, 78)
                .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(61, 61, 61))
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addContainerGap(50, Short.MAX_VALUE)
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(51, Short.MAX_VALUE))
        );

        report.add(jPanel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 230, 1080, 470));

        jTabbedPane1.addTab("6", report);

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

    private void adminlableMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_adminlableMouseEntered
        adminlable.setForeground(Color.decode("#CCFFFF"));
        adminlable.setFont(new Font(adminlable.getFont().getName(), adminlable.getFont().getStyle(), 50));// TODO add your handling code here:
    }//GEN-LAST:event_adminlableMouseEntered

    private void adminlableMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_adminlableMouseExited
        adminlable.setForeground(Color.decode("#FFFFFF"));
        adminlable.setFont(new Font(adminlable.getFont().getName(), adminlable.getFont().getStyle(), 36));// TODO add your handling code here:
    }//GEN-LAST:event_adminlableMouseExited

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

    private void jLabel9MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel9MouseClicked
        Attendance atd = new Attendance();
        this.hide();
        atd.setVisible(true);
    }//GEN-LAST:event_jLabel9MouseClicked

    private void lecLabel7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lecLabel7MouseClicked
        students std = new students();
        this.hide();
        std.setVisible(true);
        
    }//GEN-LAST:event_lecLabel7MouseClicked

    private void jLabel43MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel43MouseClicked
        jTabbedPane1.setSelectedIndex(3);        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel43MouseClicked

    private void jLabel25MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel25MouseClicked
        jTabbedPane1.setSelectedIndex(4);        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel25MouseClicked

    private void jLabel24MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel24MouseClicked
        jTabbedPane1.setSelectedIndex(2);       // TODO add your handling code here:
    }//GEN-LAST:event_jLabel24MouseClicked

    private void jLabel23MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel23MouseClicked
txtselectbatchno.removeAllItems();
detail_load_AD3();
        jTabbedPane1.setSelectedIndex(3);
    }//GEN-LAST:event_jLabel23MouseClicked

    private void jLabel22MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel22MouseClicked
        jTabbedPane1.setSelectedIndex(1);        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel22MouseClicked

    private void jLabel12MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel12MouseClicked
        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/addlightA.gif")));
        insert();
        fac_load();        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel12MouseClicked

    private void jLabel2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseClicked
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/deletlightA.gif")));

        facdelete();        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel2MouseClicked

    private void txttableComponentRemoved(java.awt.event.ContainerEvent evt) {//GEN-FIRST:event_txttableComponentRemoved

    }//GEN-LAST:event_txttableComponentRemoved

    private void jLabel47MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel47MouseClicked
        jLabel47.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/editlightA.gif")));
        facupdate();        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel47MouseClicked

    private void txttableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txttableMouseClicked
        facselecteditemload();     // TODO add your handling code here:
    }//GEN-LAST:event_txttableMouseClicked

    private void jLabel48MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel48MouseClicked
        jLabel48.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/addlightA.gif")));
        insert2();          // TODO add your handling code here:
    }//GEN-LAST:event_jLabel48MouseClicked

    private void jLabel8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel8MouseClicked
        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/deletlightA.gif")));
        batchelete();        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel8MouseClicked

    private void jLabel49MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel49MouseClicked
        jLabel49.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/editlightA.gif")));
        batchupdate();        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel49MouseClicked

    private void txttable2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txttable2MouseClicked
        bachselecteditemload();        // TODO add your handling code here:
    }//GEN-LAST:event_txttable2MouseClicked

    private void jLabel12MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel12MouseEntered
        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/addlight.png")));          // TODO add your handling code here:
    }//GEN-LAST:event_jLabel12MouseEntered

    private void jLabel12MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel12MouseExited
        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/add.png")));        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel12MouseExited

    private void jLabel2MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseEntered
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/deletlight.png")));        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel2MouseEntered

    private void jLabel2MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseExited
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/delet.png")));        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel2MouseExited

    private void jLabel47MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel47MouseEntered
        jLabel47.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/editlight.png")));        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel47MouseEntered

    private void jLabel47MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel47MouseExited
        jLabel47.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/edit.png")));         // TODO add your handling code here:
    }//GEN-LAST:event_jLabel47MouseExited

    private void jLabel49MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel49MouseEntered
        jLabel49.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/editlight.png")));         // TODO add your handling code here:
    }//GEN-LAST:event_jLabel49MouseEntered

    private void jLabel49MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel49MouseExited
        jLabel49.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/edit.png")));         // TODO add your handling code here:
    }//GEN-LAST:event_jLabel49MouseExited

    private void jLabel8MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel8MouseExited
        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/delet.png")));         // TODO add your handling code here:
    }//GEN-LAST:event_jLabel8MouseExited

    private void jLabel8MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel8MouseEntered
        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/deletlight.png")));         // TODO add your handling code here:
    }//GEN-LAST:event_jLabel8MouseEntered

    private void jLabel48MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel48MouseExited
        jLabel48.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/add.png")));        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel48MouseExited

    private void jLabel48MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel48MouseEntered
        jLabel48.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/addlight.png")));          // TODO add your handling code here:
    }//GEN-LAST:event_jLabel48MouseEntered

    private void jLabel50MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel50MouseClicked
        jLabel50.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/addlightA.gif")));
        insert2_AD2();
        tableload_AD2();      // TODO add your handling code here:
    }//GEN-LAST:event_jLabel50MouseClicked

    private void jLabel50MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel50MouseEntered
        jLabel50.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/addlight.png")));        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel50MouseEntered

    private void jLabel50MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel50MouseExited
        jLabel50.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/add.png")));         // TODO add your handling code here:
    }//GEN-LAST:event_jLabel50MouseExited

    private void jLabel54MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel54MouseClicked
        jLabel54.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/deletlightA.gif")));
        Coursedelete();        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel54MouseClicked

    private void jLabel54MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel54MouseEntered
        jLabel54.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/deletlight.png")));           // TODO add your handling code here:
    }//GEN-LAST:event_jLabel54MouseEntered

    private void jLabel54MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel54MouseExited
        jLabel54.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/delet.png")));        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel54MouseExited

    private void jLabel55MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel55MouseClicked
        jLabel55.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/editlightA.gif")));
        Courseupdate();        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel55MouseClicked

    private void jLabel55MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel55MouseEntered
        jLabel55.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/editlight.png")));          // TODO add your handling code here:
    }//GEN-LAST:event_jLabel55MouseEntered

    private void jLabel55MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel55MouseExited
        jLabel55.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/edit.png")));         // TODO add your handling code here:
    }//GEN-LAST:event_jLabel55MouseExited

    private void jLabel51MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel51MouseClicked
        jTabbedPane1.setSelectedIndex(2);        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel51MouseClicked

    private void txttable4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txttable4MouseClicked
        Courseselecteditemload();        // TODO add your handling code here:
    }//GEN-LAST:event_txttable4MouseClicked

    private void jLabel52MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel52MouseClicked

        jLabel52.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/addlightA.gif")));
        insertlec();
        detail_load_AD4();        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel52MouseClicked

    private void jLabel52MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel52MouseEntered
        jLabel52.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/addlight.png")));        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel52MouseEntered

    private void jLabel52MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel52MouseExited
        jLabel52.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/add.png")));         // TODO add your handling code here:
    }//GEN-LAST:event_jLabel52MouseExited

    private void jLabel56MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel56MouseClicked
        jLabel56.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/deletlightA.gif")));        // TODO add your handling code here:
        lecdelete();
    }//GEN-LAST:event_jLabel56MouseClicked

    private void jLabel56MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel56MouseEntered
        jLabel56.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/deletlight.png")));          // TODO add your handling code here:
    }//GEN-LAST:event_jLabel56MouseEntered

    private void jLabel56MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel56MouseExited
        jLabel56.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/delet.png")));         // TODO add your handling code here:
    }//GEN-LAST:event_jLabel56MouseExited

    private void jLabel57MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel57MouseClicked
        jLabel57.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/editlightA.gif")));         // TODO add your handling code here:
        lecupdate();
    }//GEN-LAST:event_jLabel57MouseClicked

    private void jLabel57MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel57MouseEntered
        jLabel57.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/editlight.png")));         // TODO add your handling code here:
    }//GEN-LAST:event_jLabel57MouseEntered

    private void jLabel57MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel57MouseExited
        jLabel57.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/edit.png")));         // TODO add your handling code here:
    }//GEN-LAST:event_jLabel57MouseExited

    private void jLabel53MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel53MouseClicked
        jLabel53.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/addlightA.gif")));
        insert_AD3();        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel53MouseClicked

    private void jLabel53MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel53MouseEntered
        jLabel53.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/addlight.png")));       // TODO add your handling code here:
    }//GEN-LAST:event_jLabel53MouseEntered

    private void jLabel53MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel53MouseExited
        jLabel53.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/add.png")));        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel53MouseExited

    private void jLabel58MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel58MouseClicked
        jLabel58.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/deletlightA.gif")));
        roomdelete();// TODO add your handling code here:
    }//GEN-LAST:event_jLabel58MouseClicked

    private void jLabel58MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel58MouseEntered
        jLabel58.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/deletlight.png")));          // TODO add your handling code here:
    }//GEN-LAST:event_jLabel58MouseEntered

    private void jLabel58MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel58MouseExited
        jLabel58.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/delet.png")));        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel58MouseExited

    private void jLabel59MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel59MouseClicked
        jLabel59.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/editlightA.gif")));
        roomupdate();// TODO add your handling code here:
    }//GEN-LAST:event_jLabel59MouseClicked

    private void jLabel59MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel59MouseEntered
        jLabel59.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/editlight.png")));       // TODO add your handling code here:
    }//GEN-LAST:event_jLabel59MouseEntered

    private void jLabel59MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel59MouseExited
        jLabel59.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/edit.png")));        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel59MouseExited

    private void txttablead3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txttablead3MouseClicked
        roomselecteditemload();        // TODO add your handling code here:
    }//GEN-LAST:event_txttablead3MouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        search_coAD3();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        search_roomAD3();       // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void txtselectcoid3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtselectcoid3ActionPerformed
        txtroomname1.setText((String) txtselectcoid3.getSelectedItem());        // TODO add your handling code here:
    }//GEN-LAST:event_txtselectcoid3ActionPerformed

    private void txtselectroomidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtselectroomidActionPerformed
        txtroomId.setText((String) txtselectroomid.getSelectedItem());         // TODO add your handling code here:
    }//GEN-LAST:event_txtselectroomidActionPerformed

    private void jLabel63MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel63MouseClicked
        jLabel63.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/addlightA.gif")));
        insert2_AD3();// TODO add your handling code here:
    }//GEN-LAST:event_jLabel63MouseClicked

    private void jLabel63MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel63MouseEntered
        jLabel63.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/addlight.png")));
    }//GEN-LAST:event_jLabel63MouseEntered

    private void jLabel63MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel63MouseExited
        jLabel63.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/add.png")));        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel63MouseExited

    private void jLabel64MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel64MouseClicked
        jLabel64.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/deletlightA.gif")));
        roomAllodelete();// TODO add your handling code here:
    }//GEN-LAST:event_jLabel64MouseClicked

    private void jLabel64MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel64MouseEntered
        jLabel64.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/deletlight.png")));           // TODO add your handling code here:
    }//GEN-LAST:event_jLabel64MouseEntered

    private void jLabel64MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel64MouseExited
        jLabel64.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/delet.png")));        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel64MouseExited

    private void jLabel65MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel65MouseClicked
        jLabel65.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/editlightA.gif")));
        roomAlloupdate();        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel65MouseClicked

    private void jLabel65MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel65MouseEntered
        jLabel65.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/editlight.png")));
    }//GEN-LAST:event_jLabel65MouseEntered

    private void jLabel65MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel65MouseExited
        jLabel65.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/edit.png")));   // TODO add your handling code here:
    }//GEN-LAST:event_jLabel65MouseExited

    private void stdbtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_stdbtnMouseExited
        stdbtn.setForeground(Color.decode("#009999"));
        stdbtn.setFont(new Font(stdbtn.getFont().getName(), stdbtn.getFont().getStyle(), 36));   // TODO add your handling code here:
    }//GEN-LAST:event_stdbtnMouseExited

    private void stdbtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_stdbtnMouseEntered
        stdbtn.setForeground(Color.decode("#CCFFFF"));
        stdbtn.setFont(new Font(stdbtn.getFont().getName(), stdbtn.getFont().getStyle(), 42));

        // TODO add your handling code here:
    }//GEN-LAST:event_stdbtnMouseEntered

    private void stdbtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_stdbtnMouseClicked

this.hide();
new LOGIN_TO_SYSTEM("lecturer").setVisible(true);
    }//GEN-LAST:event_stdbtnMouseClicked

    private void ad3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ad3MouseClicked
        roomAlloselecteditemload();
        search_roomAD3();
        search_coAD3();       // TODO add your handling code here:
    }//GEN-LAST:event_ad3MouseClicked

    private void jLabel60MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel60MouseClicked
        insert_lec_co_reg();
        jLabel60.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/addlightA.gif"))); // TODO add your handling code here:
    }//GEN-LAST:event_jLabel60MouseClicked

    private void jLabel60MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel60MouseEntered
        jLabel60.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/addlight.png")));       // TODO add your handling code here:
    }//GEN-LAST:event_jLabel60MouseEntered

    private void jLabel60MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel60MouseExited
        jLabel60.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/add.png")));        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel60MouseExited

    private void jLabel61MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel61MouseClicked
        jLabel61.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/deletlightA.gif")));        // TODO add your handling code here:
        lec_co_regdelete();
    }//GEN-LAST:event_jLabel61MouseClicked

    private void jLabel61MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel61MouseEntered
        jLabel61.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/deletlight.png")));       // TODO add your handling code here:
    }//GEN-LAST:event_jLabel61MouseEntered

    private void jLabel61MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel61MouseExited
        jLabel61.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/delet.png")));        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel61MouseExited

    private void jLabel62MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel62MouseClicked
        jLabel62.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/editlightA.gif")));
        lec_co_regupdate();// TODO add your handling code here:
    }//GEN-LAST:event_jLabel62MouseClicked

    private void jLabel62MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel62MouseEntered
        jLabel62.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/editlight.png")));          // TODO add your handling code here:
    }//GEN-LAST:event_jLabel62MouseEntered

    private void jLabel62MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel62MouseExited
        jLabel62.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/edit.png")));          // TODO add your handling code here:
    }//GEN-LAST:event_jLabel62MouseExited

    private void txtselectcoid4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtselectcoid4ActionPerformed
        txtroomname2.setText((String) txtselectcoid4.getSelectedItem());         // TODO add your handling code here:
    }//GEN-LAST:event_txtselectcoid4ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        search_coAD4();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        search_lecAD4();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton4ActionPerformed

    private void txttablead4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txttablead4MouseClicked
        lecSelecteditemload();        // TODO add your handling code here:
    }//GEN-LAST:event_txttablead4MouseClicked

    private void txtselectlel_idad4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtselectlel_idad4ActionPerformed
        txtroomname3.setText((String) txtselectlel_idad4.getSelectedItem());           // TODO add your handling code here:
    }//GEN-LAST:event_txtselectlel_idad4ActionPerformed

    private void txttable2ad4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txttable2ad4MouseClicked
        lec_co_regSelecteditemload();
        search_lecAD4();
        search_coAD4();         // TODO add your handling code here:
    }//GEN-LAST:event_txttable2ad4MouseClicked

    private void jLabel76MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel76MouseClicked
jTabbedPane1.setSelectedIndex(0);         // TODO add your handling code here:
    }//GEN-LAST:event_jLabel76MouseClicked

    private void jLabel80MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel80MouseClicked
jTabbedPane1.setSelectedIndex(0);         // TODO add your handling code here:
    }//GEN-LAST:event_jLabel80MouseClicked

    private void txtLno11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtLno11ActionPerformed
        //tableselectloadnextpage(SelectedRow());        // TODO add your handling code here:
    }//GEN-LAST:event_txtLno11ActionPerformed

    private void sem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sem11ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_sem11ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed

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
                r.generateAttendanceReport(txtlecid1.getText(),Integer.parseInt(txtLno11.getSelectedItem().toString()),Integer.parseInt(txtlecid2.getText())); 
                break;
            case 1: // "Print All"
                r.ReportForCourse(txtlecid1.getText(),Integer.parseInt(txtlecid2.getText()));        // TODO add your handling code here:

                break;
            default:
              
                break;
        }
    

//r.ReportForCourse(jLabel11.getText(),Integer.parseInt(BatchNo.getSelectedItem().toString()));        // TODO add your handling code here:
// TODO add your handling code here:
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
    Report report= new Report();
    report.allcoforsem(Integer.parseInt(txtlecid3.getText()),sem11.getSelectedItem().toString());        // TODO add your handling code here:
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
jTabbedPane1.setSelectedIndex(5);         // TODO add your handling code here:
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
jTabbedPane1.setSelectedIndex(6);         // TODO add your handling code here:
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
jTabbedPane1.setSelectedIndex(1);       // TODO add your handling code here:
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jLabel46MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel46MouseClicked
 jTabbedPane1.setSelectedIndex(0);        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel46MouseClicked

    private void jPanel5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel5MouseClicked

                tableload();
                txtselectfac.setSelectedIndex(-1);
                txtfac1.setText("");
                txtselectfac2.setSelectedIndex(-1);
                txtselectfac1.setSelectedIndex(-1);// TODO add your handling code here:
    }//GEN-LAST:event_jPanel5MouseClicked

    private void jPanel5MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel5MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel5MouseEntered

    private void jPanel3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel3MouseClicked
tableload();
                fac_load();
                txtfac.setText("");        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel3MouseClicked

    private void jPanel7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel7MouseClicked
  tableload_AD2();

                txtcoid.setText("");
                txtname.setText("");
                txtselectsem.setSelectedIndex(-1);
                txtselectmode.setSelectedIndex(-1);
                txtselecttype.setSelectedIndex(-1);
                txtselectcre.setSelectedIndex(-1);
                txtselecthours.setSelectedIndex(-1);
                txtselectLeNO.setSelectedIndex(-1);
                datetxt.setSelectedIndex(-1);
                timetxt.setSelectedIndex(-1);        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel7MouseClicked

    private void jPanel9MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel9MouseClicked
tableload_AD4();

                txtlecid.setText("");
                txtlecname.setText("");        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel9MouseClicked

    private void jPanel8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel8MouseClicked
  tableload_AD4();
                txtselectcoid4.removeAllItems();
                txtselectlel_idad4.removeAllItems();
                txtroomname2.setText("");
                txtroomname3.setText("");        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel8MouseClicked

    private void jPanel4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel4MouseClicked
tableload_AD3();
                txtroomid.setText("");
                txtroomname.setText("");        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel4MouseClicked

    private void jPanel6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel6MouseClicked
 tableload_AD3();

                txtselectroomid.removeAllItems();
                txtselectcoid3.removeAllItems();
                txtroomname1.setText("");
                txtroomId.setText("");
                txtselectbatchno.setSelectedIndex(-1);        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel6MouseClicked

    private void txtlecid1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtlecid1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtlecid1ActionPerformed

    private void jPanel25MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel25MouseEntered
jLabel73.setForeground(new Color(0, 153,153));
jPanel25.setBorder(new LineBorder(new Color(0, 153,153), 5));        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel25MouseEntered

    private void jPanel25MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel25MouseExited
jPanel25.setBorder(new LineBorder(new Color(0, 102,102), 5));
jLabel73.setForeground(new Color(0, 102,102));// TODO add your handling code here:
    }//GEN-LAST:event_jPanel25MouseExited

    private void jPanel26MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel26MouseEntered
jLabel84.setForeground(new Color(0, 153,153));
jPanel26.setBorder(new LineBorder(new Color(0, 153,153), 5));          // TODO add your handling code here:
    }//GEN-LAST:event_jPanel26MouseEntered

    private void jPanel26MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel26MouseExited
jLabel84.setForeground(new Color(0, 102,102)); 
jPanel26.setBorder(new LineBorder(new Color(0, 102,102), 5));     // TODO add your handling code here:
    }//GEN-LAST:event_jPanel26MouseExited

    private void jPanel24MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel24MouseEntered
jLabel85.setForeground(new Color(0, 153,153));
jPanel24.setBorder(new LineBorder(new Color(0, 153,153), 5));       // TODO add your handling code here:
    }//GEN-LAST:event_jPanel24MouseEntered

    private void jPanel24MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel24MouseExited
jLabel85.setForeground(new Color(0, 102,102)); 
jPanel24.setBorder(new LineBorder(new Color(0, 102,102), 5));         // TODO add your handling code here:
    }//GEN-LAST:event_jPanel24MouseExited

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
            java.util.logging.Logger.getLogger(admin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(admin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(admin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(admin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new admin().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel ad1;
    private javax.swing.JPanel ad2;
    private javax.swing.JTable ad3;
    private javax.swing.JPanel ad4;
    private javax.swing.JPanel admin;
    private javax.swing.JLabel adminlable;
    private javax.swing.JPanel datavisual;
    private javax.swing.JComboBox<String> datetxt;
    private javax.swing.JLabel imageside;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
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
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel87;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel90;
    private javax.swing.JLabel jLabel91;
    private javax.swing.JLabel jLabel92;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JPanel jPanel28;
    private javax.swing.JPanel jPanel29;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lecLabel7;
    private javax.swing.JPanel report;
    private javax.swing.JComboBox<String> sem11;
    private javax.swing.JLabel stdbtn;
    private javax.swing.JPanel student;
    private javax.swing.JComboBox<String> timetxt;
    private javax.swing.JComboBox<String> txtLno11;
    private javax.swing.JTextField txtcoid;
    private javax.swing.JTextField txtfac;
    private javax.swing.JTextField txtfac1;
    private javax.swing.JTextField txtlecid;
    private javax.swing.JTextField txtlecid1;
    private javax.swing.JTextField txtlecid2;
    private javax.swing.JTextField txtlecid3;
    private javax.swing.JTextField txtlecname;
    private javax.swing.JTextField txtname;
    private javax.swing.JTextField txtroomId;
    private javax.swing.JTextField txtroomid;
    private javax.swing.JTextField txtroomname;
    private javax.swing.JTextField txtroomname1;
    private javax.swing.JTextField txtroomname2;
    private javax.swing.JTextField txtroomname3;
    private javax.swing.JComboBox<String> txtselectLeNO;
    private javax.swing.JComboBox<String> txtselectbatchno;
    private javax.swing.JComboBox<String> txtselectcoid3;
    private javax.swing.JComboBox<String> txtselectcoid4;
    private javax.swing.JComboBox<String> txtselectcre;
    private javax.swing.JComboBox<String> txtselectfac;
    private javax.swing.JComboBox<String> txtselectfac1;
    private javax.swing.JComboBox<String> txtselectfac2;
    private javax.swing.JComboBox<String> txtselecthours;
    private javax.swing.JComboBox<String> txtselectlel_idad4;
    private javax.swing.JComboBox<String> txtselectmode;
    private javax.swing.JComboBox<String> txtselectroomid;
    private javax.swing.JComboBox<String> txtselectsem;
    private javax.swing.JComboBox<String> txtselecttype;
    private javax.swing.JTable txttable;
    private javax.swing.JTable txttable2;
    private javax.swing.JTable txttable2ad4;
    private javax.swing.JTable txttable4;
    private javax.swing.JTable txttablead3;
    private javax.swing.JTable txttablead4;
    private javax.swing.JPanel welcome;
    // End of variables declaration//GEN-END:variables
}
