<div align="center" style="font-family: system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif; line-height: 1.6;">

  <h1 style="margin-bottom: 0.2em;">Java Plotting Playground</h1>
  <p style="font-size: 0.95rem; max-width: 720px; margin: 0 auto;">
    A minimal, extensible <strong>Java</strong> project showcasing plotting with
    the <strong>XChart</strong> library, starting with
    <strong>line plots</strong> and <strong>histograms</strong> implemented in
    <code>LinePlots.java</code> and <code>Histograms.java</code>.
    Future extensions can include additional plot types such as scatter plots, pie charts, and more.
  </p>

  <p style="font-size: 0.85rem; color: #666; margin-top: 0.5em;">
    Built with Java, Maven, XChart, and Swing.
  </p>

</div>

<hr />

<!-- ========================================================= -->
<!-- Table of Contents                                        -->
<!-- ========================================================= -->

<ul style="list-style: none; padding-left: 0; font-size: 0.95rem;">
  <li>• <a href="#about-this-repository">About this repository</a></li>
  <li>• <a href="#what-is-xchart-and-how-do-we-plot-in-java">What is XChart and how do we plot in Java?</a></li>
  <li>• <a href="#installing-prerequisites-java-and-maven">Installing prerequisites: Java and Maven</a></li>
  <li>• <a href="#installing-xchart-library">Installing XChart library</a></li>
  <li>• <a href="#project-structure">Project structure</a></li>
  <li>• <a href="#building-and-running-with-maven">Building and running with Maven</a></li>
  <li>• <a href="#building-and-running-without-maven">Building and running without Maven</a></li>
  <li>• <a href="#mvn-compile-vs-mvn-clean-compile">mvn compile vs mvn clean compile</a></li>
  <li>• <a href="#lineplotsjava-module-line-plots">LinePlots.java module (line plots)</a></li>
  <li>• <a href="#histogramsjava-module-histograms">Histograms.java module (histograms)</a></li>
  <li>• <a href="#execjava-and-java2d-rendering-flags">exec:java and Java2D rendering flags</a></li>
  <li>• <a href="#running-different-main-classes">Running different main classes</a></li>
  <li>• <a href="#implementation-tutorial-video">Implementation tutorial video</a></li>
</ul>

---

## About this repository

This repository is a small but extensible playground for plotting in **Java** using the
<a href="https://knowm.org/open-source/xchart/" target="_blank">XChart</a> library.

Current examples:

- `LinePlots.java` — multiple line-plot demonstrations (multi-series plots, markers, subplots via grid layouts, etc.).
- `Histograms.java` — various histogram use-cases (different binning rules, normalization modes, categorical histograms, overlays, etc.).

The layout is intentionally **modular**, so you can:

- Add new plot types (e.g., scatter, pie, bar) as separate `*.java` classes.
- Extend this README by adding new sections using the same structure as the **LinePlots** and **Histograms** sections.

---

## What is XChart and how do we plot in Java?

[XChart](https://knowm.org/open-source/xchart/) is a light-weight Java plotting library that uses **Swing** for interactive windows and supports:

- Line charts, scatter plots, histograms, bar charts, pie charts, etc.
- Simple API for building charts and embedding them in Swing applications.
- Export to image formats (PNG, JPG, etc.) and vector formats (SVG, EPS) via additional modules.

This repository uses:

- `XYChart` for line plots (`LinePlots.java`).
- `CategoryChart` for histograms (`Histograms.java`).
- `XChartPanel` to embed charts inside Swing containers (`JFrame` + `JTabbedPane`).

### Minimal example: a simple line chart with XChart

Below is a minimal code snippet showing how to build and display a line chart using XChart and Swing:

```java
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XChartPanel;

import javax.swing.*;
import java.util.Arrays;

public class SimpleLineExample {

    public static void main(String[] args) {
        double[] x = {0.0, 1.0, 2.0, 3.0};
        double[] y = {0.0, 1.0, 4.0, 9.0};

        XYChart chart = new XYChartBuilder()
                .width(600)
                .height(400)
                .title("y = x^2")
                .xAxisTitle("x")
                .yAxisTitle("y")
                .build();

        chart.addSeries("x^2", x, y);

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("XChart example");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new XChartPanel<>(chart));
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
```

In this repo, we generalize that pattern to **multiple charts**, all shown in a single window with **tabs** (for line plots and histograms).

> To save a chart visually, right-click on the chart area and choose **“Save As…”** or use the keyboard shortcut (usually **Ctrl+S**) while the chart has focus.

---

## Installing prerequisites: Java and Maven

You will need:

- A Java Development Kit (JDK) 8 or later (JDK 17+ recommended).
- Apache Maven (build system and dependency manager).
- Git Bash (for running the example commands on Windows, as shown here).

### Install Java (JDK)

1. Download and install a JDK (e.g., from Adoptium, Oracle, etc.).  
2. Ensure `java` and `javac` are on your PATH:

   ```bash
   java -version
   javac -version
   ```

   Both commands should print a version and not “command not found”.

### Install Maven

1. Download Maven from the official Apache Maven site.
2. Unzip/Install it and add the `bin` folder to your PATH.
3. Verify installation:

   ```bash
   mvn -version
   ```

   You should see Maven’s version and the Java version it uses.

Once Java and Maven are installed, you can clone this repository and build/run the examples directly.

---

## Installing XChart library

There are two common ways to use XChart:

1. **Via Maven (recommended)**  
   Maven downloads and manages the XChart JAR automatically.

2. **Manual JAR download (non-Maven projects)**  
   You download the JAR and add it to your classpath manually.

### 1. XChart via Maven (used in this repo)

In `pom.xml` we declare XChart as a dependency:

```xml
<dependencies>
    <dependency>
        <groupId>org.knowm.xchart</groupId>
        <artifactId>xchart</artifactId>
        <version>3.8.8</version>
    </dependency>
</dependencies>
```

When you run `mvn compile`, Maven will automatically download XChart (and its transitive dependencies) into your local repository (`~/.m2/repository`) and place it on the classpath for compilation and execution.

### 2. XChart without Maven (manual JAR)

If you are not using Maven, you can:

1. Download the `xchart-3.8.8.jar` file from Maven Central or the XChart website.
2. Compile and run with `-cp` (class path), for example on Windows:

   ```bash
   javac -cp .;path\to\xchart-3.8.8.jar LinePlots.java
   java  -cp .;path\to\xchart-3.8.8.jar LinePlots
   ```

   On Linux/macOS, the classpath separator is `:` instead of `;`:

   ```bash
   javac -cp .:path/to/xchart-3.8.8.jar LinePlots.java
   java  -cp .:path/to/xchart-3.8.8.jar LinePlots
   ```

In this repository we **strongly recommend Maven**, as it is already configured.

---

## Project structure

A minimal view of the project tree:

```text
Java_Plot/
├── pom.xml
└── src
    └── main
        └── java
            ├── LinePlots.java
            └── Histograms.java
```

- `pom.xml`  
  Maven configuration file. Declares the project coordinates (`groupId`, `artifactId`, `version`), dependencies (XChart), and the `exec-maven-plugin` to run Java `main` classes from the command line.

- `src/main/java/LinePlots.java`  
  Contains all **line plot** examples.  
  It builds multiple `XYChart` objects and shows them in a single `JFrame` with a `JTabbedPane` (tabs 1–6).

- `src/main/java/Histograms.java`  
  Contains all **histogram** examples.  
  It builds multiple `CategoryChart` objects, including a `2×3` grid of charts for comparing binning rules, again shown in a tabbed window.

You can add more plot examples by creating new Java files in `src/main/java/` and wiring them via Maven or by running them directly.

---

## Building and running with Maven

All commands below assume you are in the project root (`Java_Plot`), e.g., in Git Bash:

```bash
cd ~/Desktop/Java_Plot
```

### Compile the project

```bash
mvn compile
```

This:

- Compiles all Java sources in `src/main/java/` into `target/classes/`.
- Downloads dependencies (XChart) if not already present.

### Run the line plots (LinePlots.java)

`pom.xml` is configured with an `exec-maven-plugin` that defines a **named execution** for line plots:

```xml
<execution>
    <id>line-plots</id>
    <goals>
        <goal>java</goal>
    </goals>
    <configuration>
        <mainClass>LinePlots</mainClass>
    </configuration>
</execution>
```

To run it:

```bash
mvn exec:java@line-plots
```

This launches the line-plot GUI (`LinePlots.main`) with one window and 6 tabs.

### Run the histograms (Histograms.java)

Similarly, `pom.xml` contains:

```xml
<execution>
    <id>histograms</id>
    <goals>
        <goal>java</goal>
    </goals>
    <configuration>
        <mainClass>Histograms</mainClass>
    </configuration>
</execution>
```

To run it:

```bash
mvn exec:java@histograms
```

This launches the histogram GUI (`Histograms.main`) with one window and 7 tabs (one of them is a 2×3 grid).

---

## Building and running without Maven

Although Maven is recommended, you can still compile and run manually with `javac` and `java`, provided that XChart is on the classpath.

Assume that:

- The project root is `Java_Plot/`.
- The XChart JAR is located at `lib/xchart-3.8.8.jar` (you may choose a different path).

### Step 1: Compile

From `Java_Plot`:

```bash
javac -cp .;lib\xchart-3.8.8.jar src\main\java\LinePlots.java src\main\java\Histograms.java
```

On Linux/macOS, use `:` as the separator:

```bash
javac -cp .:lib/xchart-3.8.8.jar src/main/java/LinePlots.java src/main/java/Histograms.java
```

This will place `.class` files alongside the `.java` files unless you specify `-d` for a custom output directory.

### Step 2: Run

On Windows:

```bash
java -cp .;lib\xchart-3.8.8.jar;src\main\java LinePlots
java -cp .;lib\xchart-3.8.8.jar;src\main\java Histograms
```

On Linux/macOS:

```bash
java -cp .:lib/xchart-3.8.8.jar:src/main/java LinePlots
java -cp .:lib/xchart-3.8.8.jar:src/main/java Histograms
```

> Note: Using Maven avoids all of this manual classpath management, which is why the project is Maven-based.

---

## mvn compile vs mvn clean compile

Two commonly used Maven commands:

### `mvn compile`

- Compiles the source code in `src/main/java` into the `target/classes` directory.
- Recompiles only what changed (incremental compilation).
- Does **not** delete the `target` directory.
- Ideal for normal, iterative development.

### `mvn clean compile`

- `mvn clean` deletes the `target` directory completely.
- Maven then runs `compile` from a **fresh state**.
- Helpful when:
  - You change dependencies or plugin versions in `pom.xml`.
  - You update Java versions or compiler flags.
  - You suspect there might be stale `.class` files causing odd behavior.

As a rule of thumb:

- For everyday modifications → `mvn compile` and then `mvn exec:java@...`
- After big configuration changes or mysterious build issues → `mvn clean compile`

You do **not** need to manually delete the `target` folder; Maven’s `clean` goal takes care of that for you.

---

## LinePlots.java module (line plots)

`LinePlots.java` is the Java code for making line plots in Java. It showcases multiple line-plot patterns using **XChart** and a Swing **tabbed window**.

### High-level structure

- `main(String[] args)`
  - Creates all charts (pure computation, no Swing yet):
    - `createMultipleLineChart()`
    - `createSetOfVectorsChart()`
    - `createSinLinesChart()`
    - `createSinLinesWithMarkersChart()`
    - `createTiledCharts()` (2×1 layout of two charts)
    - `createSubplots3x2()` (6 charts assembled in a 3×2 grid)
  - Calls `buildAndShowUI(...)` via `SwingUtilities.invokeLater(...)` to ensure Swing runs on the Event Dispatch Thread.

- `buildAndShowUI(...)`
  - Creates a `JFrame` (`"Java_Plot – XChart demo"`).
  - Creates a `JTabbedPane`.
  - Adds 6 tabs:
    1. Multiple line plots
    2. Set-of-vectors plots
    3. sin(x) function line plots
    4. sin(x) function line plots with markers
    5. 2×1 layout (top/bottom)
    6. 3×2 grid (6 charts)
  - Each tab contains one or more `XChartPanel<?>` instances.
  - Packs and displays the frame.

- Helper UI methods:
  - `wrapSingleChart(XYChart chart)` – wraps a single chart in a `JPanel` with `BorderLayout`.
  - `wrapChartGrid(List<XYChart> charts, int rows, int cols)` – builds a `GridLayout` with multiple charts, used for examples 5 and 6.
  - `createBaseChart(...)` – factory for `XYChart` objects with common styling (legend, tooltips, decimal patterns for axes).

- Numeric helpers:
  - `linspace(double start, double end, int num)`.
  - `apply(double[] x, DoubleUnaryOperator op)` – applies a function to all elements of `x`.

### Example 1: Multiple line plots

`createMultipleLineChart()`:

- Generates `x` in `[0, 2π]` using `linspace`.
- Computes:
  - `sin(x)`
  - `-sin(x)`
  - `x/π - 1`
- Adds an additional manual sequence of values:
  - x: 0,1,2,3,4,5,6
  - y: 1.0, 0.7, 0.4, 0.0, -0.4, -0.7, -1.0
- Shows how to:
  - Use different markers (`CIRCLE`, `DIAMOND`, `SQUARE`, `CROSS`).
  - Use different line styles (`SOLID`, `DASH_DASH`, `DOT_DOT`).
  - Format axes to 2 decimal places.


### Example 2: Plot from “set of vectors”

`createSetOfVectorsChart()`:

- Hard-codes four numeric rows (analog of `std::set<std::vector<double>>`).
- Uses a simple x-axis of 1, 2, 3, 4.
- Plots each row as a separate series with distinct markers.
- Sets both axes to integer labels (`"0"` decimal pattern).

### Example 3: sin() function line plots

`createSinLinesChart()`:

- `sin(x)`, `sin(x - 0.25)`, `sin(x - 0.5)` for `x` in `[0, 2π]`.
- Each series has a different line style and no markers.
- Demonstrates multi-series line charts sharing the same x-axis.

### Example 4: sin() function line plots with markers

`createSinLinesWithMarkersChart()`:

- Same general functions as example 3 but:
  - Fewer sample points for clearer markers.
  - Different markers and colors for each series.
- Good reference for customizing markers and styles per series.

### Example 5: 2×1 tiled layout (top / bottom)

`createTiledCharts()`:

- Creates two charts:
  - Top: `sin(5x)`
  - Bottom: `sin(15x)`
- These charts are assembled in a `GridLayout(2, 1)` by `wrapChartGrid(...)` and displayed on tab 5.


### Example 6: 3×2 grid of subplots

`createSubplots3x2()`:

Builds a list of 6 charts:

1. `sin(x)`
2. `tan(sin(x)) - sin(tan(x))`
3. `cos(5x)`
4. Time-like data (0 to 180) with sample values
5. `sin(5x)`
6. A circle drawn via parametric equations (`x = r cos θ + x_c`, `y = r sin θ + y_c`)

These six charts are placed in a `GridLayout(3, 2)` panel and shown as tab 6.

---

## Histograms.java module (histograms)

`Histograms.java` is the Java code for making histogram charts in Java. It uses `CategoryChart` to represent histograms and a small helper class `HistogramData` to store bin centers, counts, and widths.

### High-level structure

- `main(String[] args)`:
  - Generates charts for:
    1. Basic histogram of N(0,1).
    2. Six sub-histograms comparing binning algorithms.
    3. Histogram with a fixed number of bins (50).
    4. Histogram with custom bin edges and count-density normalization.
    5. Categorical histogram (string responses).
    6. Overlaid normalized histograms (probability).
    7. Histogram normalized to PDF with theoretical normal PDF overlay.
  - Displays them in a `JFrame` with a `JTabbedPane`, one tab per “example”. Example 2 uses a nested `2×3` grid of charts in one tab.

- `HistogramData` (inner static class):
  - Holds:
    - `double[] binCenters`
    - `double[] binCounts`
    - `double[] binWidths`
    - `double totalCount`
  - Constructed by helper functions that compute histograms from raw samples.

- Random generator:
  - `randn(int n, double mean, double stdDev)` – uses `Random.nextGaussian()` to generate normal samples.

- Basic statistics and binning utilities:
  - `min`, `max`, `mean`, `stdDev`, `percentile`
  - Binning rules:
    - `fdBinCount(...)` — Freedman–Diaconis rule.
    - `scottBinCount(...)` — Scott’s rule.
    - `sturgesBinCount(...)` — Sturges’ rule.
    - `sqrtBinCount(...)` — square-root rule.
    - `integerEdges(...)` — integer-bin edges for the “integers” rule.

- Histogram building utilities:
  - `uniformBinHistogram(...)` and `uniformBinHistogramInRange(...)` — build histograms with a fixed number of equally spaced bins.
  - `histogramWithBinWidth(...)` — builds bins of fixed width over a given range.
  - `histogramWithCustomEdges(...)` — uses explicit bin edges.

- Chart construction helpers:
  - `createEmptyHistogramChart(...)` — base `CategoryChart` skeleton with axes titles, legend, decimal patterns.
  - `createHistogramChartFromData(...)` — builds a `CategoryChart` from `HistogramData` plus chart labels.

### Example 1: Simple histogram of standard normal data

`createHistogram1()`:

- Generates 10,000 samples from N(0,1).
- Uses Freedman–Diaconis (`fdBinCount`) as an “automatic” bin-count estimate.
- Builds a histogram with integer counts and 2-decimal x-axis labels.
- Logs the number of bins to the console.

### Example 2: Compare different binning algorithms (2×3)

`createHistogram2BinningComparisonCharts()`:

- Generates 10,000 samples from N(0,1).
- Builds six histograms, each using a different binning strategy:
  1. “Automatic” (here chosen as FD).
  2. Scott’s rule.
  3. Freedman–Diaconis rule explicitly.
  4. Integers rule (integer bin edges).
  5. Sturges’ rule.
  6. Square-root rule.
- Each histogram is a separate `CategoryChart` with its own title.
- `createBinningGridPanel(...)` assembles them into a `2×3` `GridLayout` and places that panel in tab 2.

### Example 3: Changing the number of bins

`createHistogram3()`:

- Generates 1,000 samples from N(0,1).
- Uses a fixed bin count of 50 directly.
- Sets the chart title to `"<numBins> bins"`.

Dynamic rebinning over time (with sleep) is not reproduced here to keep the Java GUI responsive and simpler; instead, the Java version shows the **final 50-bin configuration**.

### Example 4: Custom bin edges + count-density

`createHistogram4()`:

- Generates 10,000 samples from N(0,1).
- Uses the explicit custom bin edges.
- Computes `count_density = count / bin_width` for each bin.
- Plots count density vs. bin center as a `CategoryChart`.
- Y-axis is formatted with `0.00` decimal pattern.

### Example 5: Categorical histogram (string categories)

`createHistogram5Categorical()`:

- Uses an array of `"yes"`, `"no"`, and `"undecided"` strings.
- Builds a frequency map (`Map<String, Integer>`).
- Plots a bar chart where categories are `"no"`, `"yes"`, `"undecided"` in a fixed order.

### Example 6: Overlaid normalized histograms (probability)

`createHistogram6OverlaidProbability()`:

- Generates two normal samples:
  - 2,000 from N(0,1)
  - 5,000 from N(1,1)
- Determines a global `[min, max]` and uses a common bin width (0.25) for both datasets.
- Computes **probability** for each bin: `count_i / total_count`.
- Uses `CategoryChart` with `setOverlapped(true)` so bars from both distributions overlay each other.
- Legend indicates which distribution is which.

### Example 7: Histogram normalized to PDF + theoretical normal PDF

`createHistogram7PdfOverlay()`:

- Generates 5,000 samples from N(μ=5, σ=2).
- Builds a histogram over the range [-5, 15] with fixed bin width (0.5).
- Computes an **empirical PDF**: `count_i / (N * bin_width_i)`.
- Computes the **theoretical normal PDF** at bin centers using:
  - `normalPdf(x, mu, sigma)`.
- Plots the empirical PDF as bars and the theoretical PDF as a line (`CategorySeriesRenderStyle.Line`).

---

## exec:java and Java2D rendering flags

You may see two forms of the `exec:java` command:

```bash
mvn exec:java@histograms
```

and

```bash
mvn exec:java@histograms -Dexec.jvmArgs="-Dsun.java2d.d3d=false -Dsun.java2d.opengl=false"
```

### 1. `mvn exec:java@...` (default)

This:

- Uses the JVM with **default** Java2D settings.
- On most machines, hardware acceleration (Direct3D / OpenGL) is enabled by default.
- Is usually what you want.

### 2. `mvn exec:java` with `-Dexec.jvmArgs="..."` (disabling D3D/OpenGL)

Sometimes, due to graphics driver or JDK issues, Swing windows that use Java2D can render as **blank/white windows** even though the program is running. To work around such issues, you can disable hardware acceleration:

```bash
mvn exec:java@histograms   -Dexec.jvmArgs="-Dsun.java2d.d3d=false -Dsun.java2d.opengl=false"
```

The JVM arguments:

- `-Dsun.java2d.d3d=false` — disables Direct3D-based acceleration (Windows).
- `-Dsun.java2d.opengl=false` — disables OpenGL-based acceleration.

Use this variant **only if** you see rendering glitches (white windows, flickering, etc.). For most users, the default `mvn exec:java@...` is sufficient.

---

## Running different main classes

There are two primary ways to choose which `main` class to run.

### 1. Using named executions in `pom.xml` (recommended)

With the `exec-maven-plugin` configured as:

```xml
<execution>
    <id>line-plots</id>
    <goals><goal>java</goal></goals>
    <configuration>
        <mainClass>LinePlots</mainClass>
    </configuration>
</execution>

<execution>
    <id>histograms</id>
    <goals><goal>java</goal></goals>
    <configuration>
        <mainClass>Histograms</mainClass>
    </configuration>
</execution>
```

you can run each one explicitly:

```bash
mvn exec:java@line-plots
mvn exec:java@histograms
```

This is the approach used by this repository and is usually the cleanest.

### 2. Overriding the main class from the command line

You can also use the generic `exec:java` goal and override the `mainClass` via `-Dexec.mainClass=...`:

```bash
mvn exec:java -Dexec.mainClass=LinePlots
mvn exec:java -Dexec.mainClass=Histograms
```

This can be handy when experimenting or when you haven’t defined named executions yet. However, once you have multiple examples, the **named executions** (`@line-plots`, `@histograms`) make your workflow clearer and easier to document.

---

## Implementation tutorial video

A video tutorial about how to compile this repository and making plots with Java.

<a href="" target="_blank">
  <img
    src=""
    alt="Java Plotting with XChart - Implementation Tutorial"
    style="max-width: 100%; border-radius: 8px; box-shadow: 0 4px 16px rgba(0,0,0,0.15); margin-top: 0.5rem;"
  />
</a>
