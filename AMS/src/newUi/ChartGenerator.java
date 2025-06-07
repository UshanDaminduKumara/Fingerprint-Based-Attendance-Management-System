package newUi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;

public class ChartGenerator {

    public void addAttendanceChart(String id,JPanel panel, List<String> courses, List<Double> attendancePercentages) {
        // Create dataset
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < courses.size(); i++) {
            dataset.addValue(attendancePercentages.get(i), "Attendance", courses.get(i));
        }

        // Create bar chart
        JFreeChart barChart = ChartFactory.createBarChart(
                id+" Student Attendance", // Chart Title
                "Courses",            // X-Axis Label
                "Attendance (%)",     // Y-Axis Label
                dataset,              // Dataset
                PlotOrientation.VERTICAL,
                false, true, false
        );

        // Get the plot
        CategoryPlot plot = barChart.getCategoryPlot();

        // Customize text colors
        java.awt.Color customRed = new java.awt.Color(0, 102, 102); // Custom red color
        barChart.getTitle().setPaint(customRed); // Chart title color
        plot.getDomainAxis().setLabelPaint(customRed); // X-axis label color
        plot.getDomainAxis().setTickLabelPaint(customRed); // X-axis tick label color
        plot.getRangeAxis().setLabelPaint(customRed); // Y-axis label color
        plot.getRangeAxis().setTickLabelPaint(customRed); // Y-axis tick label color

        // Create a custom renderer to set individual bar colors
        org.jfree.chart.renderer.category.BarRenderer customRenderer = new org.jfree.chart.renderer.category.BarRenderer() {
            @Override
            public java.awt.Paint getItemPaint(int row, int column) {
                double attendance = attendancePercentages.get(column);
                if (attendance >= 80) {
                    return new java.awt.Color(0, 102, 102); // Green for attendance >= 80
                } else {
                    return new java.awt.Color(217, 83, 79); // Red for attendance < 80
                }
            }
        };

        // Apply the custom renderer
        plot.setRenderer(customRenderer);

        // Set background color to white
        barChart.setBackgroundPaint(java.awt.Color.WHITE); // Chart background
        plot.setBackgroundPaint(java.awt.Color.WHITE); // Plot background

        // Create and add the chart panel to the specified JPanel
        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(panel.getSize()); // Set size to match the panel
        panel.setLayout(new BorderLayout()); // Set layout
        panel.add(chartPanel, BorderLayout.CENTER); // Add chart to the panel
        panel.validate(); // Refresh the panel to display the chart
    }

    public void lacAttendanceChart(JPanel jPanelName, String courseName, Map<Integer, Integer> presentData, int totalStudents) {
        // Create dataset
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<Integer, Integer> entry : presentData.entrySet()) {
            int lessonNumber = entry.getKey();
            int presentCount = entry.getValue();
            int absentCount = totalStudents - presentCount;

            dataset.addValue(presentCount, "Present", "Lesson " + lessonNumber);
            dataset.addValue(absentCount, "Absent", "Lesson " + lessonNumber);
        }

        // Create stacked bar chart
        JFreeChart stackedBarChart = ChartFactory.createStackedBarChart(
                "Attendance per Lesson - " + courseName, // Chart Title
                "Lesson Number",                         // X-Axis Label
                "Number of Students",                    // Y-Axis Label
                dataset,                                 // Dataset
                PlotOrientation.VERTICAL,
                true, true, false
        );
        // Add space above the title
stackedBarChart.setPadding(new RectangleInsets(20, 0, 0, 0));
    
// Customize text colors
java.awt.Color customColor = new java.awt.Color(0, 102, 102); // Custom teal color
stackedBarChart.getTitle().setPaint(customColor); // Chart title color

CategoryPlot plot = stackedBarChart.getCategoryPlot();
plot.getDomainAxis().setLabelPaint(customColor); // X-axis label color
plot.getDomainAxis().setTickLabelPaint(customColor); // X-axis tick label color
plot.getRangeAxis().setLabelPaint(customColor); // Y-axis label color
plot.getRangeAxis().setTickLabelPaint(customColor); // Y-axis tick label color



        // Customize chart appearance
       
        StackedBarRenderer renderer = new StackedBarRenderer();
        renderer.setSeriesPaint(0, new Color(76, 175, 80));  // Green for Present
        renderer.setSeriesPaint(1, new Color(244, 67, 54));  // Red for Absent
        plot.setRenderer(renderer);
        // Ensure the Y-axis shows only integer values
NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
plot.setBackgroundPaint(Color.WHITE);

        // Create Chart Panel
        ChartPanel chartPanel = new ChartPanel(stackedBarChart);
        chartPanel.setPreferredSize(new Dimension(jPanelName.getWidth(), jPanelName.getHeight()));

        // Remove existing components from JPanel and add new chart
        jPanelName.removeAll();
        jPanelName.setLayout(new java.awt.BorderLayout());
        jPanelName.add(chartPanel, java.awt.BorderLayout.CENTER);
        jPanelName.revalidate();
        jPanelName.repaint();
    }
    
    public void generate_chart_for_admin(JPanel jPanelName, Map<String, Integer> presentData,int x) {
      String Chart_Title;
        String X_Axis_Label;
        if(x==0){
        Chart_Title="Overall Attendance During The Past Few Days";
        X_Axis_Label=" ";
        
        }else{
        Chart_Title="Courses with the Lowest Attendance Percentage";
        X_Axis_Label=" ";
        }
        
// Create dataset
    DefaultCategoryDataset dataset = new DefaultCategoryDataset(); // Only declare once
    presentData.forEach((key, value) -> dataset.addValue(value, "Attendance",key));

    // Create bar chart
    JFreeChart barChart = ChartFactory.createBarChart(
            Chart_Title, // Chart_Title
            X_Axis_Label,                 // X-Axis Label
            "Attendance (%)",          // Y-Axis Label
            dataset,                   // Dataset
            PlotOrientation.VERTICAL,
            false, true, false
    );
    // Customize the chart
    CategoryPlot plot = barChart.getCategoryPlot();
            // Change text color to (0, 102, 102)
        java.awt.Color customColor = new java.awt.Color(0, 102, 102);
        barChart.getTitle().setPaint(customColor); // Title text color
        plot.getDomainAxis().setLabelPaint(customColor); // X-axis label color
        plot.getDomainAxis().setTickLabelPaint(customColor); // X-axis tick label color
        plot.getRangeAxis().setLabelPaint(customColor); // Y-axis label color
        plot.getRangeAxis().setTickLabelPaint(customColor); // Y-axis tick label color
    // Add custom renderer for tooltips
    BarRenderer customRenderer = new BarRenderer() {
@Override
public java.awt.Paint getItemPaint(int row, int column) {
    String key = dataset.getColumnKey(column).toString().replace("Course ", ""); // Remove "Course " prefix
    int attendance = presentData.getOrDefault(key, 0); // Fetch attendance percentage
    //System.out.println("Modified Key: " + key + ", Attendance: " + attendance);
    if (attendance >= 80) {
        return new java.awt.Color(0, 104, 0); // Green for attendance >= 80
    } else {
        return new java.awt.Color(144, 238, 144); // Light green for attendance < 80
    }
}
    };
// Set tooltips using StandardCategoryToolTipGenerator
customRenderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator() {
    public String generateToolTip(DefaultCategoryDataset dataset, int row, int column) {
        int attendance = presentData.get(column + 1); // Get the attendance value from the map
        return "Course " + (column + 1) + ": Attendance = " + attendance + "%"; // Tooltip content
    }
});
if(x==0){
LineAndShapeRenderer lineRenderer = new LineAndShapeRenderer();
        lineRenderer.setSeriesPaint(0, Color.RED); 
        plot.setDataset(1, dataset);
        plot.setRenderer(1, lineRenderer);
    }else{}
plot.setRenderer(customRenderer);
    // Set background colors
    plot.setBackgroundPaint(Color.WHITE);
    barChart.setBackgroundPaint(Color.WHITE);
    // Add chart to JPanel
    ChartPanel chartPanel = new ChartPanel(barChart);
    chartPanel.setPreferredSize(jPanelName.getSize());
    jPanelName.removeAll(); // Clear previous content from the panel
    jPanelName.setLayout(new BorderLayout());
    jPanelName.add(chartPanel, BorderLayout.CENTER);
    jPanelName.validate(); // Refresh the panel to display the chart
}

   public void generate_pi_chart(JPanel jPanelName, Map<String, Integer> studentData) {
        // Create dataset
        DefaultPieDataset dataset = new DefaultPieDataset();

        // Populate the dataset
        for (Map.Entry<String, Integer> entry : studentData.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        // Create pie chart (styled as a donut chart)
        JFreeChart pieChart = ChartFactory.createPieChart(
                "Today Attendance", // Chart title
                dataset, 
                true,  // Show legend
                true,  // Show tooltips
                false  // No URLs
        );

        // Customize the pie plot
        PiePlot plot = (PiePlot) pieChart.getPlot();
        plot.setCircular(true); // Enforce a circular shape
        plot.setLabelGenerator(null); // Remove section labels
        plot.setBackgroundPaint(Color.WHITE); // Set background color

        // Apply Dark Green & Light Green colors
        plot.setSectionPaint((Comparable) studentData.keySet().toArray()[0], new Color(0, 100, 0));  // Dark Green
        plot.setSectionPaint((Comparable) studentData.keySet().toArray()[1], new Color(144, 238, 144)); // Light Green

        // Remove the black border
        plot.setOutlineVisible(false);

        // Move legend to the left
        pieChart.getLegend().setPosition(RectangleEdge.RIGHT);
        
        //title color
        pieChart.getTitle().setPaint(new Color(0, 100, 0));
        // Add chart to panel
        ChartPanel chartPanel = new ChartPanel(pieChart);
        chartPanel.setPreferredSize(new Dimension(jPanelName.getWidth(), jPanelName.getHeight()));
        jPanelName.removeAll();
        jPanelName.setLayout(new BorderLayout());
        jPanelName.add(chartPanel, BorderLayout.CENTER);
        jPanelName.validate();
    }
   public void generate_pi_chart2(JPanel jPanelName, Map<String, Integer> studentData) {
        // Create dataset
        DefaultPieDataset dataset = new DefaultPieDataset();

        // Populate the dataset
        for (Map.Entry<String, Integer> entry : studentData.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        // Create pie chart (styled as a donut chart)
        JFreeChart pieChart = ChartFactory.createPieChart(
                "Overall Attendance", // Chart title
                dataset, 
                true,  // Show legend
                true,  // Show tooltips
                false  // No URLs
        );

        // Customize the pie plot
        PiePlot plot = (PiePlot) pieChart.getPlot();
        plot.setCircular(true); // Enforce a circular shape
        plot.setLabelGenerator(null); // Remove section labels
        plot.setBackgroundPaint(Color.WHITE); //f2fae6// Set background color

        // Apply Dark Green & Light Green colors
        plot.setSectionPaint((Comparable) studentData.keySet().toArray()[0], new Color(0, 100, 0));  // Dark Green
        plot.setSectionPaint((Comparable) studentData.keySet().toArray()[1], new Color(144, 238, 144)); // Light Green

        // Remove the black border
        plot.setOutlineVisible(false);

        // Move legend to the left
        pieChart.getLegend().setPosition(RectangleEdge.RIGHT);
        
        //title color
        pieChart.getTitle().setPaint(new Color(0, 100, 0));
        // Add chart to panel
        ChartPanel chartPanel = new ChartPanel(pieChart);
        chartPanel.setPreferredSize(new Dimension(jPanelName.getWidth(), jPanelName.getHeight()));
        jPanelName.removeAll();
        jPanelName.setLayout(new BorderLayout());
        jPanelName.add(chartPanel, BorderLayout.CENTER);
        jPanelName.validate();
    }
}