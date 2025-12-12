import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.CategorySeries;
import org.knowm.xchart.CategorySeries.CategorySeriesRenderStyle;
import org.knowm.xchart.XChartPanel;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

/**
 *
 * 1) Basic histogram of N(0,1)
 * 2) Compare binning algorithms (automatic, Scott, FD, integers, Sturges, sqrt)
 * 3) Histogram with fixed number of bins (50)
 * 4) Histogram with custom bin edges + count-density normalization
 * 5) Categorical histogram (string categories)
 * 6) Overlaid normalized histograms (probability)
 * 7) Histogram normalized to PDF + theoretical normal PDF
 *
 * GUI:
 *   - Single JFrame
 *   - JTabbedPane with one tab per "example"
 *   - Example 2 tab contains a 2x3 grid of small charts (subplots)
 *
 * Notes:
 *   - To save a plot, right-click on it and choose "Save As..."
 */
public class Histograms {

    // Global RNG with fixed seed for reproducibility
    private static final Random RNG = new Random(0L);

    // ----- Small container for histogram data -----

    private static final class HistogramData {
        final double[] binCenters;
        final double[] binCounts;
        final double[] binWidths;
        final double totalCount;

        HistogramData(double[] centers, double[] counts, double[] widths) {
            this.binCenters = centers;
            this.binCounts = counts;
            this.binWidths = widths;
            double sum = 0.0;
            for (double c : counts) {
                sum += c;
            }
            this.totalCount = sum;
        }
    }

    // ----- MAIN -----

    public static void main(String[] args) {
        // Precompute all charts BEFORE starting Swing

        CategoryChart hist1 = createHistogram1();
        List<CategoryChart> hist2Charts = createHistogram2BinningComparisonCharts();
        CategoryChart hist3 = createHistogram3();
        CategoryChart hist4 = createHistogram4();
        CategoryChart hist5 = createHistogram5Categorical();
        CategoryChart hist6 = createHistogram6OverlaidProbability();
        CategoryChart hist7 = createHistogram7PdfOverlay();

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Java_Plot - Histograms");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            JTabbedPane tabs = new JTabbedPane();

            tabs.addTab("1: Basic N(0,1)", new XChartPanel<>(hist1));
            tabs.addTab("2: Binning rules (2x3)", createBinningGridPanel(hist2Charts));
            tabs.addTab("3: 50 bins", new XChartPanel<>(hist3));
            tabs.addTab("4: Custom edges", new XChartPanel<>(hist4));
            tabs.addTab("5: Categorical", new XChartPanel<>(hist5));
            tabs.addTab("6: Overlaid (probability)", new XChartPanel<>(hist6));
            tabs.addTab("7: Hist + Normal PDF", new XChartPanel<>(hist7));

            frame.add(tabs, BorderLayout.CENTER);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    // ----- Example 1: Simple histogram, automatic binning -----

    private static CategoryChart createHistogram1() {
        double[] x1 = randn(10_000, 0.0, 1.0);

        // Use Freedman–Diaconis as a decent "automatic" bin estimate
        int numBins = fdBinCount(x1);
        HistogramData h = uniformBinHistogram(x1, numBins);

        System.out.println("Histogram 1 with " + h.binCenters.length + " bins");

        CategoryChart chart = createEmptyHistogramChart(
                "Histogram of standard normal data",
                "Value",
                "Frequency"
        );
        chart.getStyler().setXAxisDecimalPattern("0.00");
        chart.getStyler().setYAxisDecimalPattern("0"); // counts are integers

        List<Double> x = DoubleStream.of(h.binCenters).boxed().collect(Collectors.toList());
        List<Double> y = DoubleStream.of(h.binCounts).boxed().collect(Collectors.toList());
        chart.addSeries("N(0,1)", x, y);

        return chart;
    }

    // ----- Example 2: Compare binning algorithms (2x3 grid of charts) -----

    private static List<CategoryChart> createHistogram2BinningComparisonCharts() {
        double[] x2 = randn(10_000, 0.0, 1.0);

        List<CategoryChart> charts = new ArrayList<>(6);

        // Automatic -> here we simply use the FD rule as the "automatic" choice
        charts.add(createHistogramForBins(
                x2,
                fdBinCount(x2),
                "Automatic (FD rule)"
        ));

        // Scott's rule
        charts.add(createHistogramForBins(
                x2,
                scottBinCount(x2),
                "Scott's rule"
        ));

        // Freedman–Diaconis rule explicitly
        charts.add(createHistogramForBins(
                x2,
                fdBinCount(x2),
                "Freedman–Diaconis rule"
        ));

        // Integers rule: bins on integer boundaries
        double[] intEdges = integerEdges(x2);
        HistogramData hIntegers = histogramWithCustomEdges(x2, intEdges);
        charts.add(createHistogramChartFromData(
                hIntegers,
                "Integers rule",
                "Value",
                "Frequency",
                "0" // integer ticks
        ));

        // Sturges' rule
        charts.add(createHistogramForBins(
                x2,
                sturgesBinCount(x2),
                "Sturges' rule"
        ));

        // Square-root rule
        charts.add(createHistogramForBins(
                x2,
                sqrtBinCount(x2),
                "Square root rule"
        ));

        return charts;
    }

    private static CategoryChart createHistogramForBins(double[] data, int numBins, String title) {
        HistogramData h = uniformBinHistogram(data, numBins);
        return createHistogramChartFromData(
                h,
                title,
                "Value",
                "Frequency",
                "0.0"
        );
    }

    private static JPanel createBinningGridPanel(List<CategoryChart> charts) {
        JPanel panel = new JPanel(new GridLayout(2, 3));
        for (CategoryChart chart : charts) {
            panel.add(new XChartPanel<>(chart));
        }
        return panel;
    }

    // ----- Example 3: Change number of bins (final state: 50 bins) -----

    private static CategoryChart createHistogram3() {
        double[] x3 = randn(1_000, 0.0, 1.0);

        int numBins = 50; // directly show the final state with 50 bins
        HistogramData h = uniformBinHistogram(x3, numBins);

        CategoryChart chart = createHistogramChartFromData(
                h,
                h.binCenters.length + " bins",
                "Value",
                "Frequency",
                "0.00"
        );

        return chart;
    }

    // ----- Example 4: Custom bin edges + count-density normalization -----

    private static CategoryChart createHistogram4() {
        double[] x4 = randn(10_000, 0.0, 1.0);

        double[] edges = {
                -10.0000, -2.0000, -1.7500, -1.5000, -1.2500,
                -1.0000, -0.7500, -0.5000, -0.2500, 0.0,
                 0.2500,  0.5000,  0.7500,  1.0000,  1.2500,
                 1.5000,  1.7500,  2.0000, 10.0000
        };

        HistogramData h = histogramWithCustomEdges(x4, edges);

        // count_density: count / bin_width
        double[] heights = new double[h.binCounts.length];
        for (int i = 0; i < h.binCounts.length; i++) {
            heights[i] = h.binCounts[i] / h.binWidths[i];
        }

        CategoryChart chart = new CategoryChartBuilder()
                .width(1600)
                .height(900)
                .title("Histogram with custom bin edges")
                .xAxisTitle("Value")
                .yAxisTitle("Count density")
                .build();

        chart.getStyler().setLegendVisible(false);
        chart.getStyler().setXAxisDecimalPattern("0.00");
        chart.getStyler().setYAxisDecimalPattern("0.00");

        List<Double> x = DoubleStream.of(h.binCenters).boxed().collect(Collectors.toList());
        List<Double> y = DoubleStream.of(heights).boxed().collect(Collectors.toList());
        chart.addSeries("Custom edges", x, y);

        return chart;
    }

    // ----- Example 5: Categorical histogram (string categories) -----

    private static CategoryChart createHistogram5Categorical() {
        String[] responses = {
                "no", "no", "yes", "yes", "yes", "no", "no",
                "no", "no", "undecided", "undecided", "yes", "no", "no",
                "no", "yes", "no", "yes", "no", "yes", "no",
                "no", "no", "yes", "yes", "yes", "yes"
        };

        Map<String, Integer> counts = new LinkedHashMap<>();
        for (String r : responses) {
            counts.merge(r, 1, Integer::sum);
        }

        // Fix category order: no, yes, undecided (to be explicit)
        List<String> categories = Arrays.asList("no", "yes", "undecided");
        List<Integer> y = categories.stream()
                .map(c -> counts.getOrDefault(c, 0))
                .collect(Collectors.toList());

        CategoryChart chart = new CategoryChartBuilder()
                .width(1600)
                .height(900)
                .title("Histogram of categorical responses")
                .xAxisTitle("Category")
                .yAxisTitle("Count")
                .build();

        chart.getStyler().setLegendVisible(false);

        chart.addSeries("Responses", categories, y);
        return chart;
    }

    // ----- Example 6: Overlaid normalized histograms (probability) -----

    private static CategoryChart createHistogram6OverlaidProbability() {
        double[] x5 = randn(2_000, 0.0, 1.0);
        double[] y5 = randn(5_000, 1.0, 1.0);

        double globalMin = Math.min(min(x5), min(y5));
        double globalMax = Math.max(max(x5), max(y5));

        double binWidth = 0.25;
        HistogramData hX = histogramWithBinWidth(x5, globalMin, globalMax, binWidth);
        HistogramData hY = histogramWithBinWidth(y5, globalMin, globalMax, binWidth);

        double[] probX = new double[hX.binCounts.length];
        double[] probY = new double[hY.binCounts.length];

        for (int i = 0; i < probX.length; i++) {
            probX[i] = hX.binCounts[i] / hX.totalCount;
            probY[i] = hY.binCounts[i] / hY.totalCount;
        }

        CategoryChart chart = new CategoryChartBuilder()
                .width(1200)
                .height(800)
                .title("Overlaid normalized histograms")
                .xAxisTitle("Value")
                .yAxisTitle("Probability")
                .build();

        chart.getStyler().setLegendVisible(true);
        chart.getStyler().setOverlapped(true); // overlay bars instead of side-by-side
        chart.getStyler().setXAxisDecimalPattern("0.0");
        chart.getStyler().setYAxisDecimalPattern("0.00");

        List<Double> centers = DoubleStream.of(hX.binCenters).boxed().collect(Collectors.toList());
        List<Double> yProbX = DoubleStream.of(probX).boxed().collect(Collectors.toList());
        List<Double> yProbY = DoubleStream.of(probY).boxed().collect(Collectors.toList());

        chart.addSeries("N(0,1)", centers, yProbX);
        chart.addSeries("N(1,1)", centers, yProbY);

        return chart;
    }

    // ----- Example 7: Histogram normalized to PDF + theoretical normal PDF -----

    private static CategoryChart createHistogram7PdfOverlay() {
        double mu = 5.0;
        double sigma = 2.0;

        double[] x6 = randn(5_000, mu, sigma);

        double rangeMin = -5.0;
        double rangeMax = 15.0;
        double binWidth = 0.5;

        HistogramData h = histogramWithBinWidth(x6, rangeMin, rangeMax, binWidth);

        // Empirical PDF: count / (N_used * bin_width)
        double[] pdfEmpirical = new double[h.binCounts.length];
        for (int i = 0; i < pdfEmpirical.length; i++) {
            pdfEmpirical[i] = h.binCounts[i] / (h.totalCount * h.binWidths[i]);
        }

        // Theoretical normal PDF evaluated at bin centers
        double[] pdfTheoretical = new double[h.binCenters.length];
        for (int i = 0; i < pdfTheoretical.length; i++) {
            pdfTheoretical[i] = normalPdf(h.binCenters[i], mu, sigma);
        }

        CategoryChart chart = new CategoryChartBuilder()
                .width(1200)
                .height(800)
                .title("Histogram with theoretical normal PDF")
                .xAxisTitle("Value")
                .yAxisTitle("Probability density")
                .build();

        chart.getStyler().setLegendVisible(true);
        chart.getStyler().setOverlapped(true);
        chart.getStyler().setXAxisDecimalPattern("0.0");
        chart.getStyler().setYAxisDecimalPattern("0.000");

        List<Double> centers = DoubleStream.of(h.binCenters).boxed().collect(Collectors.toList());
        List<Double> yEmp = DoubleStream.of(pdfEmpirical).boxed().collect(Collectors.toList());
        List<Double> yTh = DoubleStream.of(pdfTheoretical).boxed().collect(Collectors.toList());

        CategorySeries empSeries = chart.addSeries("Empirical PDF", centers, yEmp);
        CategorySeries pdfSeries = chart.addSeries("Normal PDF", centers, yTh);

        // Bars for empirical, line for theoretical PDF
        empSeries.setChartCategorySeriesRenderStyle(CategorySeriesRenderStyle.Bar);
        pdfSeries.setChartCategorySeriesRenderStyle(CategorySeriesRenderStyle.Line);

        return chart;
    }

    // ----- Utility: Create a basic histogram chart skeleton -----

    private static CategoryChart createEmptyHistogramChart(String title,
                                                           String xAxisTitle,
                                                           String yAxisTitle) {
        CategoryChart chart = new CategoryChartBuilder()
                .width(1200)
                .height(800)
                .title(title)
                .xAxisTitle(xAxisTitle)
                .yAxisTitle(yAxisTitle)
                .build();

        chart.getStyler().setLegendVisible(false);
        chart.getStyler().setXAxisDecimalPattern("0.00");
        chart.getStyler().setYAxisDecimalPattern("0");
        return chart;
    }

    private static CategoryChart createHistogramChartFromData(HistogramData h,
                                                              String title,
                                                              String xAxisTitle,
                                                              String yAxisTitle,
                                                              String xDecimalPattern) {
        CategoryChart chart = new CategoryChartBuilder()
                .width(1200)
                .height(800)
                .title(title)
                .xAxisTitle(xAxisTitle)
                .yAxisTitle(yAxisTitle)
                .build();

        chart.getStyler().setLegendVisible(false);
        chart.getStyler().setXAxisDecimalPattern(xDecimalPattern);
        chart.getStyler().setYAxisDecimalPattern("0"); // integer counts by default

        List<Double> x = DoubleStream.of(h.binCenters).boxed().collect(Collectors.toList());
        List<Double> y = DoubleStream.of(h.binCounts).boxed().collect(Collectors.toList());
        chart.addSeries("data", x, y);

        return chart;
    }

    // ----- Utility: random normal -----

    private static double[] randn(int n, double mean, double stdDev) {
        double[] data = new double[n];
        for (int i = 0; i < n; i++) {
            data[i] = mean + stdDev * RNG.nextGaussian();
        }
        return data;
    }

    // ----- Utility: basic stats -----

    private static double min(double[] data) {
        return Arrays.stream(data).min().orElse(0.0);
    }

    private static double max(double[] data) {
        return Arrays.stream(data).max().orElse(0.0);
    }

    private static double mean(double[] data) {
        return Arrays.stream(data).average().orElse(0.0);
    }

    private static double stdDev(double[] data) {
        double m = mean(data);
        double sumSq = 0.0;
        for (double v : data) {
            double d = v - m;
            sumSq += d * d;
        }
        return Math.sqrt(sumSq / (data.length - 1));
    }

    private static double percentile(double[] sorted, double p) {
        if (sorted.length == 0) return Double.NaN;
        double pos = p / 100.0 * (sorted.length - 1);
        int idx = (int) pos;
        double frac = pos - idx;
        if (idx + 1 < sorted.length) {
            return sorted[idx] * (1.0 - frac) + sorted[idx + 1] * frac;
        } else {
            return sorted[idx];
        }
    }

    // ----- Utility: binning algorithms -----

    /** Freedman–Diaconis bin count (used as "automatic" and explicitly). */
    private static int fdBinCount(double[] data) {
        double[] copy = data.clone();
        Arrays.sort(copy);
        double q1 = percentile(copy, 25.0);
        double q3 = percentile(copy, 75.0);
        double iqr = q3 - q1;
        if (iqr <= 0.0 || Double.isNaN(iqr)) {
            return sqrtBinCount(data);
        }
        int n = data.length;
        double h = 2.0 * iqr * Math.pow(n, -1.0 / 3.0);
        double range = copy[copy.length - 1] - copy[0];
        int bins = (int) Math.round(range / h);
        if (bins < 5) bins = 5;
        if (bins > 100) bins = 100;
        return bins;
    }

    /** Scott's rule for bin count. */
    private static int scottBinCount(double[] data) {
        double sigma = stdDev(data);
        if (sigma == 0.0 || Double.isNaN(sigma)) {
            return sqrtBinCount(data);
        }
        int n = data.length;
        double h = 3.5 * sigma * Math.pow(n, -1.0 / 3.0);
        double min = min(data);
        double max = max(data);
        double range = max - min;
        int bins = (int) Math.round(range / h);
        if (bins < 5) bins = 5;
        if (bins > 100) bins = 100;
        return bins;
    }

    /** Sturges' rule for bin count. */
    private static int sturgesBinCount(double[] data) {
        int n = data.length;
        int bins = (int) Math.ceil(Math.log(n) / Math.log(2.0) + 1.0);
        if (bins < 5) bins = 5;
        if (bins > 100) bins = 100;
        return bins;
    }

    /** Square-root rule for bin count. */
    private static int sqrtBinCount(double[] data) {
        int bins = (int) Math.round(Math.sqrt(data.length));
        if (bins < 5) bins = 5;
        if (bins > 100) bins = 100;
        return bins;
    }

    /** Integer edges for "integers" rule. */
    private static double[] integerEdges(double[] data) {
        double min = min(data);
        double max = max(data);
        int start = (int) Math.floor(min);
        int end = (int) Math.ceil(max);
        int numBins = Math.max(1, end - start);
        double[] edges = new double[numBins + 1];
        for (int i = 0; i <= numBins; i++) {
            edges[i] = start + i;
        }
        return edges;
    }

    // ----- Utility: histogram builders -----

    private static HistogramData uniformBinHistogram(double[] data, int numBins) {
        double min = min(data);
        double max = max(data);
        if (max == min) {
            max = min + 1e-9;
        }
        return uniformBinHistogramInRange(data, min, max, numBins);
    }

    private static HistogramData uniformBinHistogramInRange(double[] data,
                                                            double min,
                                                            double max,
                                                            int numBins) {
        double range = max - min;
        double binWidth = range / numBins;
        double[] counts = new double[numBins];

        for (double v : data) {
            if (Double.isNaN(v)) continue;
            int idx = (int) ((v - min) / binWidth);
            if (idx < 0 || idx >= numBins) {
                // include max in last bin
                if (v == max) {
                    idx = numBins - 1;
                } else {
                    continue;
                }
            }
            counts[idx] += 1.0;
        }

        double[] centers = new double[numBins];
        double[] widths = new double[numBins];
        for (int i = 0; i < numBins; i++) {
            double left = min + i * binWidth;
            double right = left + binWidth;
            centers[i] = (left + right) / 2.0;
            widths[i] = binWidth;
        }

        return new HistogramData(centers, counts, widths);
    }

    private static HistogramData histogramWithBinWidth(double[] data,
                                                       double min,
                                                       double max,
                                                       double binWidth) {
        int numBins = (int) Math.ceil((max - min) / binWidth);
        double extendedMax = min + numBins * binWidth;
        return uniformBinHistogramInRange(data, min, extendedMax, numBins);
    }

    private static HistogramData histogramWithCustomEdges(double[] data, double[] edges) {
        int numBins = edges.length - 1;
        double[] counts = new double[numBins];

        for (double v : data) {
            if (Double.isNaN(v)) continue;
            if (v < edges[0] || v > edges[edges.length - 1]) {
                continue;
            }
            int binIndex = -1;
            for (int i = 0; i < numBins; i++) {
                double left = edges[i];
                double right = edges[i + 1];
                boolean inBin = (v >= left && (v < right || (i == numBins - 1 && v <= right)));
                if (inBin) {
                    binIndex = i;
                    break;
                }
            }
            if (binIndex >= 0) {
                counts[binIndex] += 1.0;
            }
        }

        double[] centers = new double[numBins];
        double[] widths = new double[numBins];
        for (int i = 0; i < numBins; i++) {
            widths[i] = edges[i + 1] - edges[i];
            centers[i] = (edges[i + 1] + edges[i]) / 2.0;
        }

        return new HistogramData(centers, counts, widths);
    }

    // ----- Utility: normal PDF -----

    private static double normalPdf(double x, double mu, double sigma) {
        double z = (x - mu) / sigma;
        double factor = 1.0 / (sigma * Math.sqrt(2.0 * Math.PI));
        return factor * Math.exp(-0.5 * z * z);
    }
}
