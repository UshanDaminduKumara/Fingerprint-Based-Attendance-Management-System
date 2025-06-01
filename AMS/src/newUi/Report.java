/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package newUi;

import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import net.proteanit.sql.DbUtils;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.column.Columns;
import net.sf.dynamicreports.report.builder.component.Components;
import net.sf.dynamicreports.report.builder.style.Styles;
import net.sf.dynamicreports.report.constant.HorizontalAlignment;
import net.sf.dynamicreports.report.constant.VerticalAlignment;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.log4j.BasicConfigurator;

/**
 *
 * @author DAMIDU
 */
public class Report {
    
    public Report(){
     conn= DBconnect.connect();
    }
    Connection conn=null;
    PreparedStatement pst=null;
    ResultSet rs=null;
    
    
  
 public void allcoforsem(int bg,String sem){
     
  try {    
 String sql="SELECT DISTINCT a.co_id FROM attendance a JOIN course c ON a.co_id = c.co_id WHERE a.bg_no = ? AND c.sem = ?;";
  pst = conn.prepareStatement(sql);
    pst.setInt(1, bg);
    pst.setString(2, sem);
   rs = pst.executeQuery();


boolean hasData = false;

    while (rs.next()) {
     hasData = true;   
    rs.getString("co_id");
    Report report= new Report();
    report.ReportForCourse(rs.getString("co_id"),bg);
    }

if (!hasData) {
    JOptionPane.showMessageDialog(null, "No data available.");
}
 } catch (Exception e) {
    JOptionPane.showMessageDialog(null, e);
}
 }
    
 public void generateAttendanceReport(String coCode, int attLNo, int bgNo) {
      BasicConfigurator.configure();               
    List<Map<String, Object>> data = new ArrayList<>();
    String Date=null;
    try {
    String sql = "SELECT std_id, std_status,att_l_date FROM attendance WHERE co_id=? AND att_l_no=? AND bg_no=?";
    pst = conn.prepareStatement(sql);
    pst.setString(1, coCode);
    pst.setInt(2, attLNo);
    pst.setInt(3, bgNo);

    rs = pst.executeQuery();
    int i=1;
    if(rs.next()){while (rs.next()) {
        Map<String, Object> row = new HashMap<>();
        row.put("no", i);
        row.put("studentName", rs.getString("std_id"));
        int status = rs.getInt("std_status");
        String statusText = (status == 1) ? "Present" : "Absent";
        
        row.put("status", statusText);
        data.add(row);
        i++;
      Date=rs.getString("att_l_date");
    }}else{
        JOptionPane.showMessageDialog(null, "Data Is Not Exist IN Database");
    }
    
} catch (Exception e) {
    JOptionPane.showMessageDialog(null, e);
}
        try {
            DynamicReports.report()
                .title(
                    Components.image(getClass().getResource("/png/OSK.jpg"))
                        .setHorizontalAlignment(HorizontalAlignment.CENTER),
                    Components.text("South Eastern University of Sri Lanka")
                        .setHorizontalAlignment(HorizontalAlignment.CENTER)
                        .setStyle(DynamicReports.stl.style().setFontSize(18).bold()),
                    Components.text("Course Code: "+coCode)
                        .setHorizontalAlignment(HorizontalAlignment.CENTER)
                        .setStyle(DynamicReports.stl.style().setFontSize(12)),
                    Components.text("Lesson No: "+attLNo)
                        .setHorizontalAlignment(HorizontalAlignment.CENTER),
                  Components.text("Date: "+Date)
                        .setHorizontalAlignment(HorizontalAlignment.CENTER),
                    Components.text(" ")
                        .setHorizontalAlignment(HorizontalAlignment.CENTER)
                )
                .columns(
                    Columns.column("No", "no", Integer.class)
                        .setHorizontalAlignment(HorizontalAlignment.CENTER)
                        .setStyle(DynamicReports.stl.style()
                            .setBorder(DynamicReports.stl.penThin())
                            .setBackgroundColor(Color.WHITE)
                            .bold()),
                    Columns.column("Student ID", "studentName", String.class)
                        .setHorizontalAlignment(HorizontalAlignment.CENTER)
                        .setStyle(DynamicReports.stl.style()
                            .setBorder(DynamicReports.stl.penThin())
                            .setBackgroundColor(Color.WHITE)
                            .bold()),
                    Columns.column("Status", "status", String.class)
                        .setHorizontalAlignment(HorizontalAlignment.CENTER)
                        .setStyle(DynamicReports.stl.style()
                            .setBorder(DynamicReports.stl.penThin())
                            .setBackgroundColor(Color.WHITE)
                            .bold())
                )
                .setColumnTitleStyle(
                    DynamicReports.stl.style()
                        .setHorizontalAlignment(HorizontalAlignment.CENTER)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
                        .setBorder(DynamicReports.stl.penThin())
                        .setBackgroundColor(Color.LIGHT_GRAY)
                        .bold())
                .setDataSource(new JRBeanCollectionDataSource(data))
                .pageFooter(Components.pageXofY())
                .print();

        } catch (DRException ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
        }
        
    } 
public void generateAttendanceReport(String coCode,JTable jTable1) {
               BasicConfigurator.configure(); // Fix Log4j warning
 LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Customize format if needed
        String Date = today.format(formatter);


        List<Map<String, Object>> data = new ArrayList<>();
        TableModel model = jTable1.getModel();

        // Step 1: Get Column Headers Dynamically
        int columnCount = model.getColumnCount();
        List<String> columnNames = new ArrayList<>();
        for (int col = 0; col < columnCount; col++) {
            columnNames.add(model.getColumnName(col)); // Extract column headers dynamically
        }

        // Step 2: Extract Row Data Dynamically
        for (int row = 0; row < model.getRowCount(); row++) {
            Map<String, Object> rowData = new HashMap<>();
            for (int col = 0; col < columnCount; col++) {
                // Convert all values to String to prevent type mismatch errors
                rowData.put(columnNames.get(col), String.valueOf(model.getValueAt(row, col)));
            }
            data.add(rowData);
        }

             // Step 3: Dynamically Build Columns for Report with Borders
        try {
            DynamicReports.report()
                .title(
                   Components.image(getClass().getResource("/png/OSK.jpg"))
                        .setHorizontalAlignment(HorizontalAlignment.CENTER),
                    Components.text("South Eastern University of Sri Lanka")
                        .setHorizontalAlignment(HorizontalAlignment.CENTER)
                        .setStyle(DynamicReports.stl.style().setFontSize(18).bold()),
                    Components.text("Course Code: "+coCode)
                        .setHorizontalAlignment(HorizontalAlignment.CENTER)
                        .setStyle(DynamicReports.stl.style().setFontSize(12)),
                   
                  Components.text("Date: "+Date)
                        .setHorizontalAlignment(HorizontalAlignment.CENTER),
                    Components.text(" ")
                        .setHorizontalAlignment(HorizontalAlignment.CENTER)      
                )
                .columns(columnNames.stream()
                    .map(column -> Columns.column(column, column, String.class)
                        .setStyle(Styles.style()
                            .setBorder(Styles.penThin()) // Adds borders to table cells
                            .setHorizontalAlignment(HorizontalAlignment.CENTER)
                            .setVerticalAlignment(VerticalAlignment.MIDDLE)))
                    .toArray(net.sf.dynamicreports.report.builder.column.TextColumnBuilder[]::new)
                )
                .setColumnTitleStyle(
                    Styles.style()
                        .setBorder(Styles.penThin()) // Adds border to column headers
                        .setBackgroundColor(java.awt.Color.LIGHT_GRAY)
                        .bold()
                )
                .setDataSource(new JRBeanCollectionDataSource(data))
                .pageFooter(Components.pageXofY())
                .print();


        } catch (DRException ex) {
            JOptionPane.showMessageDialog(null, "Error generating report: " + ex.getMessage());
            ex.printStackTrace(); // Print full error to console
        }
    }


    public void ReportForCourse(String coCode,int bgNo){
      JTable jTable1 = new JTable(null);

        try {
         Statement stmt = conn.createStatement();

// Step 1: Get Attendance Session Column Names
String dynamicSql = "";
ResultSet rs = stmt.executeQuery("SELECT GROUP_CONCAT(CONCAT('MAX(CASE WHEN a.att_l_no = ', att_l_no, ' THEN a.std_status END) AS ` ', att_l_no, '`')) FROM (SELECT DISTINCT att_l_no FROM attendance WHERE co_id = '"+coCode+"') AS subquery;");
if (rs.next()) {
    dynamicSql = rs.getString(1);
}

// Step 2: Construct Final Query in Java
String finalSql = "SELECT a.std_id, " + dynamicSql + ", "
    + "(SUM(CASE WHEN a.std_status = 1 THEN 1 ELSE 0 END) / COUNT(*)) * 100 AS Attendance_Percentage "
    + "FROM attendance a "
    + "WHERE a.co_id = '"+coCode+"' AND a.bg_no = '"+bgNo+"' "
    + "GROUP BY a.std_id;";

// Step 3: Execute Query
rs = stmt.executeQuery(finalSql);
jTable1.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            System.out.println(e);
        }
        generateAttendanceReport(coCode,jTable1);
    
    }

}

