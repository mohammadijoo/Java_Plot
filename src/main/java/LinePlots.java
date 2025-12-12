// LinePlots.java
// Java/XChart for line plotting examples.
// All plots are shown in a single JFrame with 6 tabs.

import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.style.markers.SeriesMarkers;
import org.knowm.xchart.style.lines.SeriesLines;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

public class LinePlots {

    public static void main(String[] args) {

        // If anything blows up on the Swing thread, print it to the console
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            System.err.println("Uncaught exception on thread " + t.getName());
            e.printStackTrace();
        });

        // Build all charts first (pure math, no Swing yet)
        XYChart chart1 = createMultipleLineChart();
        XYChart chart2 = createSetOfVectorsChart();
        XYChart chart3 = createSinLinesChart();
        XYChart chart4 = createSinLinesWithMarkersChart();
        List<XYChart> tiledCharts = createTiledCharts();          // Example 5 (2×1)
        List<XYChart> subplots3x2Charts = createSubplots3x2();    // Example 6 (3×2)

        // Build and show the UI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> buildAndShowUI(
                chart1, chart2, chart3, chart4, tiledCharts, subplots3x2Charts
        ));
    }

    // -------------------------------------------------------------------------
    //  Build main UI: one window, 6 tabs
    // -------------------------------------------------------------------------

    private static void buildAndShowUI(XYChart chart1,
                                       XYChart chart2,
                                       XYChart chart3,
                                       XYChart chart4,
                                       List<XYChart> tiled,
                                       List<XYChart> subplots3x2) {

        JFrame frame = new JFrame("Java_Plot – XChart demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();

        // Tab 1: Multiple line plots
        tabs.addTab("1) Multiple lines", wrapSingleChart(chart1));

        // Tab 2: Set of vectors
        tabs.addTab("2) Set of vectors", wrapSingleChart(chart2));

        // Tab 3: sin() line plots
        tabs.addTab("3) sin(x) shifts", wrapSingleChart(chart3));

        // Tab 4: sin() with markers
        tabs.addTab("4) sin + markers", wrapSingleChart(chart4));

        // Tab 5: 2×1 grid (top & bottom)
        tabs.addTab("5) 2×1 grid", wrapChartGrid(tiled, 2, 1));

        // Tab 6: 3×2 grid (6 charts)
        tabs.addTab("6) 3×2 grid", wrapChartGrid(subplots3x2, 3, 2));

        frame.add(tabs, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null); // center on screen
        frame.setVisible(true);
    }

    // -------------------------------------------------------------------------
    //  Helper UI methods
    // -------------------------------------------------------------------------

    private static JPanel wrapSingleChart(XYChart chart) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new XChartPanel<>(chart), BorderLayout.CENTER);
        return panel;
    }

    private static JPanel wrapChartGrid(List<XYChart> charts, int rows, int cols) {
        JPanel gridPanel = new JPanel(new GridLayout(rows, cols));
        for (XYChart chart : charts) {
            gridPanel.add(new XChartPanel<>(chart));
        }
        JPanel outer = new JPanel(new BorderLayout());
        outer.add(gridPanel, BorderLayout.CENTER);
        return outer;
    }

    /**
     * Base chart factory.
     * By default, limits axis labels to 2 decimal places ("0.00").
     */
    private static XYChart createBaseChart(String title, String xLabel, String yLabel) {
        return createBaseChart(title, xLabel, yLabel, "0.00", "0.00");
    }

    /**
     * Base chart factory with explicit decimal patterns.
     * xPattern / yPattern are standard Java DecimalFormat patterns, e.g.:
     *  - "0.00" → 2 decimals
     *  - "0"    → integer values
     */
    private static XYChart createBaseChart(String title,
                                           String xLabel,
                                           String yLabel,
                                           String xPattern,
                                           String yPattern) {

        XYChart chart = new XYChartBuilder()
                .width(600)
                .height(400)
                .title(title)
                .xAxisTitle(xLabel)
                .yAxisTitle(yLabel)
                .build();

        chart.getStyler().setLegendVisible(true);
        chart.getStyler().setToolTipsEnabled(true);
        chart.getStyler().setPlotContentSize(0.9);
        chart.getStyler().setPlotMargin(10);

        chart.getStyler().setXAxisDecimalPattern(xPattern);
        chart.getStyler().setYAxisDecimalPattern(yPattern);

        return chart;
    }

    // -------------------------------------------------------------------------
    //  Numeric helpers (linspace + apply)
    // -------------------------------------------------------------------------

    private static double[] linspace(double start, double end, int num) {
        double[] result = new double[num];
        if (num == 1) {
            result[0] = start;
            return result;
        }
        double step = (end - start) / (num - 1);
        for (int i = 0; i < num; i++) {
            result[i] = start + step * i;
        }
        return result;
    }

    private static double[] apply(double[] x, DoubleUnaryOperator op) {
        double[] y = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            y[i] = op.applyAsDouble(x[i]);
        }
        return y;
    }

    // -------------------------------------------------------------------------
    //  1) Multiple line plots on same axes
    // -------------------------------------------------------------------------

    private static XYChart createMultipleLineChart() {
        double[] x = linspace(0.0, 2.0 * Math.PI, 200);
        double[] y1 = apply(x, Math::sin);

        double[] y2 = new double[x.length];
        double[] y3 = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            y2[i] = -y1[i];
            y3[i] = x[i] / Math.PI - 1.0;
        }

        // Manual points (like your C++ example)
        double[] x4 = {0, 1, 2, 3, 4, 5, 6};
        double[] y4 = {1.0, 0.7, 0.4, 0.0, -0.4, -0.7, -1.0};

        // Use default 2-decimal axis formatting
        XYChart chart = createBaseChart("Multiple line plots", "x", "y");

        XYSeries s1 = chart.addSeries("sin(x)", x, y1);
        s1.setMarker(SeriesMarkers.CIRCLE);
        s1.setLineStyle(SeriesLines.SOLID);

        XYSeries s2 = chart.addSeries("-sin(x)", x, y2);
        s2.setMarker(SeriesMarkers.DIAMOND);
        s2.setLineStyle(SeriesLines.DASH_DASH);

        XYSeries s3 = chart.addSeries("x/π - 1", x, y3);
        s3.setMarker(SeriesMarkers.SQUARE);
        s3.setLineStyle(SeriesLines.DOT_DOT);

        XYSeries s4 = chart.addSeries("manual points", x4, y4);
        s4.setMarker(SeriesMarkers.CROSS);
        s4.setLineStyle(SeriesLines.NONE);

        return chart;
    }

    // -------------------------------------------------------------------------
    //  2) Plot from “set of vectors”
    // -------------------------------------------------------------------------

    private static XYChart createSetOfVectorsChart() {
        // Same values as your C++ set<vector<double>>
        double[] row1 = {16, 5, 9, 4};
        double[] row2 = {2, 11, 7, 14};
        double[] row3 = {3, 10, 6, 15};
        double[] row4 = {13, 8, 12, 1};

        // X-axis is 1..4
        double[] x = {1, 2, 3, 4};

        // For this one, make both axes integer labels
        XYChart chart = createBaseChart(
                "Multiple line plots (set of vectors)",
                "x", "y",
                "0", "0"
        );

        XYSeries s1 = chart.addSeries("row 1", x, row1);
        s1.setMarker(SeriesMarkers.CIRCLE);

        XYSeries s2 = chart.addSeries("row 2", x, row2);
        s2.setMarker(SeriesMarkers.DIAMOND);

        XYSeries s3 = chart.addSeries("row 3", x, row3);
        s3.setMarker(SeriesMarkers.SQUARE);

        XYSeries s4 = chart.addSeries("row 4", x, row4);
        s4.setMarker(SeriesMarkers.TRIANGLE_UP);

        return chart;
    }

    // -------------------------------------------------------------------------
    //  3) Sin function line plots (3 phase-shifted sinusoids)
    // -------------------------------------------------------------------------

    private static XYChart createSinLinesChart() {
        double[] x = linspace(0.0, 2.0 * Math.PI, 200);
        double[] y1 = apply(x, Math::sin);
        double[] y2 = apply(x, v -> Math.sin(v - 0.25));
        double[] y3 = apply(x, v -> Math.sin(v - 0.5));

        XYChart chart = createBaseChart("Sin() function line plots", "x", "y");

        XYSeries s1 = chart.addSeries("sin(x)", x, y1);
        s1.setMarker(SeriesMarkers.NONE);
        s1.setLineStyle(SeriesLines.SOLID);

        XYSeries s2 = chart.addSeries("sin(x - 0.25)", x, y2);
        s2.setMarker(SeriesMarkers.NONE);
        s2.setLineStyle(SeriesLines.DASH_DASH);

        XYSeries s3 = chart.addSeries("sin(x - 0.5)", x, y3);
        s3.setMarker(SeriesMarkers.NONE);
        s3.setLineStyle(SeriesLines.DOT_DOT);

        return chart;
    }

    // -------------------------------------------------------------------------
    //  4) Sin function line plots with markers
    // -------------------------------------------------------------------------

    private static XYChart createSinLinesWithMarkersChart() {
        double[] x = linspace(0.0, 2.0 * Math.PI, 40);
        double[] y1 = apply(x, Math::sin);
        double[] y2 = apply(x, v -> Math.sin(v - 0.25));
        double[] y3 = apply(x, v -> Math.sin(v - 0.5));

        XYChart chart = createBaseChart("Sin() function line plots with markers", "x", "y");

        XYSeries s1 = chart.addSeries("sin(x)", x, y1);
        s1.setMarker(SeriesMarkers.CIRCLE);
        s1.setLineStyle(SeriesLines.SOLID);

        XYSeries s2 = chart.addSeries("sin(x - 0.25)", x, y2);
        s2.setMarker(SeriesMarkers.DIAMOND);
        s2.setLineStyle(SeriesLines.DASH_DASH);

        XYSeries s3 = chart.addSeries("sin(x - 0.5)", x, y3);
        s3.setMarker(SeriesMarkers.CROSS);
        s3.setLineStyle(SeriesLines.NONE);

        return chart;
    }

    // -------------------------------------------------------------------------
    //  5) Simple “tiledlayout” example: 2×1 (top + bottom)
    // -------------------------------------------------------------------------

    private static List<XYChart> createTiledCharts() {
        double[] x = linspace(0.0, 3.0, 200);
        double[] y1 = apply(x, v -> Math.sin(5.0 * v));
        double[] y2 = apply(x, v -> Math.sin(15.0 * v));

        XYChart top = createBaseChart("Top Plot", "x", "sin(5x)");
        top.addSeries("sin(5x)", x, y1).setMarker(SeriesMarkers.NONE);
        top.getStyler().setLegendVisible(false);

        XYChart bottom = createBaseChart("Bottom Plot", "x", "sin(15x)");
        bottom.addSeries("sin(15x)", x, y2).setMarker(SeriesMarkers.NONE);
        bottom.getStyler().setLegendVisible(false);

        List<XYChart> list = new ArrayList<>();
        list.add(top);
        list.add(bottom);
        return list;
    }

    // -------------------------------------------------------------------------
    //  6) 3×2 “subplots” — 6 separate charts assembled in a GridLayout
    // -------------------------------------------------------------------------

    private static List<XYChart> createSubplots3x2() {
        List<XYChart> charts = new ArrayList<>();

        // (row 0, col 0): sin(x)
        double[] x1 = linspace(0.0, 10.0, 100);
        double[] y1 = apply(x1, Math::sin);
        XYChart c1 = createBaseChart("sin(x)", "x", "sin(x)");
        c1.addSeries("sin(x)", x1, y1).setMarker(SeriesMarkers.CIRCLE);
        c1.getStyler().setLegendVisible(false);
        charts.add(c1);

        // (row 0, col 1): tan(sin(x)) - sin(tan(x))
        double[] x2 = linspace(-Math.PI, Math.PI, 40);
        double[] y2 = apply(x2, v -> Math.tan(Math.sin(v)) - Math.sin(Math.tan(v)));
        XYChart c2 = createBaseChart("tan(sin x) - sin(tan x)", "x", "y");
        XYSeries s2 = c2.addSeries("f(x)", x2, y2);
        s2.setMarker(SeriesMarkers.DIAMOND);
        s2.setLineStyle(SeriesLines.DASH_DASH);
        c2.getStyler().setLegendVisible(false);
        charts.add(c2);

        // (row 1, col 0): cos(5x)
        double[] x3 = linspace(0.0, 10.0, 150);
        double[] y3 = apply(x3, v -> Math.cos(5.0 * v));
        XYChart c3 = createBaseChart("2-D Line Plot", "x", "cos(5x)");
        c3.addSeries("cos(5x)", x3, y3).setMarker(SeriesMarkers.NONE);
        c3.getStyler().setLegendVisible(false);
        charts.add(c3);

        // (row 1, col 1): time-like plot
        double[] x4 = {0, 30, 60, 90, 120, 150, 180};
        double[] y4 = {0.8, 0.9, 0.1, 0.9, 0.6, 0.1, 0.3};
        // Integer X labels, decimal Y labels
        XYChart c4 = createBaseChart("Time Plot", "Time (s)", "Value", "0", "0.00");
        c4.addSeries("time series", x4, y4).setMarker(SeriesMarkers.CIRCLE);
        c4.getStyler().setLegendVisible(false);
        charts.add(c4);

        // (row 2, col 0): sin(5x)
        double[] x5 = linspace(0.0, 3.0, 100);
        double[] y5 = apply(x5, v -> Math.sin(5.0 * v));
        XYChart c5 = createBaseChart("sin(5x)", "x", "y");
        c5.addSeries("sin(5x)", x5, y5).setMarker(SeriesMarkers.NONE);
        c5.getStyler().setLegendVisible(false);
        charts.add(c5);

        // (row 2, col 1): circle
        double r = 2.0;
        double xc = 4.0;
        double yc = 3.0;
        double[] theta = linspace(0.0, 2.0 * Math.PI, 200);
        double[] x6 = new double[theta.length];
        double[] y6 = new double[theta.length];
        for (int i = 0; i < theta.length; i++) {
            x6[i] = r * Math.cos(theta[i]) + xc;
            y6[i] = r * Math.sin(theta[i]) + yc;
        }
        XYChart c6 = createBaseChart("Circle", "x", "y");
        c6.addSeries("circle", x6, y6).setMarker(SeriesMarkers.NONE);
        c6.getStyler().setLegendVisible(false);
        charts.add(c6);

        return charts;
    }
}
