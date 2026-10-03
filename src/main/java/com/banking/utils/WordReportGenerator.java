package com.banking.utils;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.Document;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Creates one Word evidence file per scenario plus one combined summary file per run.
 *
 * output/reports/run_<timestamp>/
 *     Execution_Summary.docx
 *     01_<feature>_<scenario>.docx
 *     02_<feature>_<scenario>.docx
 */
public class WordReportGenerator {

    private static final Logger LOG = Logger.getLogger(WordReportGenerator.class.getName());

    // Time zone is explicit everywhere, so durations are computed on zone-aware values
    private static final ZoneId ZONE = ZoneId.systemDefault();

    private static final DateTimeFormatter DISPLAY = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");
    private static final DateTimeFormatter FILE_STAMP = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private static final ZonedDateTime RUN_START = ZonedDateTime.now(ZONE);
    private static final Path RUN_DIR = Paths.get("output", "reports", "run_" + RUN_START.format(FILE_STAMP));
    private static final String SUMMARY_FILE = "Execution_Summary.docx";

    private static final String STATUS_PASSED = "PASSED";
    private static final String STATUS_FAILED = "FAILED";

    private static final String COLOR_BLACK = "000000";
    private static final String COLOR_NAVY = "000080";
    private static final String COLOR_GREEN = "008000";
    private static final String COLOR_RED = "FF0000";
    private static final String COLOR_DARK_RED = "CC0000";
    private static final String COLOR_BLUE = "0066CC";
    private static final String COLOR_GRAY = "555555";
    private static final String COLOR_DARK_GRAY = "333333";

    private static final int MAX_NAME_PART = 50;

    private static final AtomicInteger SEQUENCE = new AtomicInteger(0);
    private static final Object LOCK = new Object();
    private static final List<ScenarioResult> RESULTS = new ArrayList<>();

    // Per-thread state: safe for parallel execution
    private static final ThreadLocal<ScenarioReport> CURRENT = new ThreadLocal<>();
    private static final ThreadLocal<String> CURRENT_STEP = new ThreadLocal<>();

    private WordReportGenerator() {}

    // ------------------------------------------------------------------
    // Data holders
    // ------------------------------------------------------------------

    private static class ScenarioReport {
        final XWPFDocument doc = new XWPFDocument();
        final String feature;
        final String name;
        final int seq;
        final ZonedDateTime start = ZonedDateTime.now(ZONE);
        boolean failureMarked = false;

        ScenarioReport(String feature, String name, int seq) {
            this.feature = feature;
            this.name = name;
            this.seq = seq;
        }
    }

    private static class ScenarioResult {
        int seq;
        String feature;
        String name;
        String status;
        long seconds;
        String fileName;
    }

    // ------------------------------------------------------------------
    // Called by step definitions and page objects (unchanged signatures)
    // ------------------------------------------------------------------

    public static void setCurrentStep(String step) {
        CURRENT_STEP.set(step);
    }

    // Screenshot taken inside a page object. Does NOT clear the current step text.
    public static void addIntermediateScreenshot(String description, String timestamp, byte[] screenshot) {
        ScenarioReport rep = CURRENT.get();
        if (rep == null) return;
        String text = CURRENT_STEP.get();
        if (text == null || text.isEmpty()) text = description;
        addText(rep.doc, text + " | Time: " + timestamp + " (" + description + ")",
                10, true, false, COLOR_BLUE, ParagraphAlignment.LEFT);
        addPicture(rep.doc, screenshot, "intermediate");
    }

    // ------------------------------------------------------------------
    // Called by Hooks
    // ------------------------------------------------------------------

    public static void startScenario(String feature, String scenario) {
        ScenarioReport rep = new ScenarioReport(feature, scenario, SEQUENCE.incrementAndGet());
        CURRENT.set(rep);
        CURRENT_STEP.remove();

        addText(rep.doc, "Banking Automation - Scenario Execution Report",
                16, true, false, COLOR_NAVY, ParagraphAlignment.CENTER);
        addText(rep.doc, "Feature: " + feature, 12, true, false, COLOR_BLACK, ParagraphAlignment.LEFT);
        addText(rep.doc, "Scenario: " + scenario, 12, true, false, COLOR_BLACK, ParagraphAlignment.LEFT);
        addText(rep.doc, "Started: " + rep.start.format(DISPLAY),
                10, false, true, COLOR_GRAY, ParagraphAlignment.LEFT);
    }

    public static void addStepResult(int stepNum, String timestamp, byte[] screenshot, boolean scenarioFailedSoFar) {
        ScenarioReport rep = CURRENT.get();
        if (rep == null) return;

        String text = CURRENT_STEP.get();
        if (text == null || text.isEmpty()) text = "Step " + stepNum;

        // Mark only the first step at which the scenario turned to failed
        boolean failedHere = scenarioFailedSoFar && !rep.failureMarked;
        if (failedHere) rep.failureMarked = true;

        String label = (failedHere ? "[FAILED] " : "") + text + " | Time: " + timestamp;
        addText(rep.doc, label, 10, true, false, failedHere ? COLOR_DARK_RED : COLOR_DARK_GRAY,
                ParagraphAlignment.LEFT);
        addPicture(rep.doc, screenshot, "step_" + stepNum);

        CURRENT_STEP.remove();
    }

    public static void endScenario(String status, String timestamp) {
        ScenarioReport rep = CURRENT.get();
        if (rep == null) return;

        try {
            addText(rep.doc, "Result: " + status + " at " + timestamp,
                    11, true, false, STATUS_PASSED.equalsIgnoreCase(status) ? COLOR_GREEN : COLOR_RED,
                    ParagraphAlignment.LEFT);

            String fileName = String.format("%02d_%s_%s.docx", rep.seq, safe(rep.feature), safe(rep.name));
            Files.createDirectories(RUN_DIR);
            try (OutputStream out = Files.newOutputStream(RUN_DIR.resolve(fileName))) {
                rep.doc.write(out);
            }
            rep.doc.close();

            ScenarioResult result = new ScenarioResult();
            result.seq = rep.seq;
            result.feature = rep.feature;
            result.name = rep.name;
            result.status = status;
            result.seconds = Duration.between(rep.start, ZonedDateTime.now(ZONE)).getSeconds();
            result.fileName = fileName;

            synchronized (LOCK) {
                RESULTS.add(result);
                writeSummary();
            }
        } catch (IOException e) {
            LOG.log(Level.WARNING, "Could not save scenario report", e);
        } finally {
            CURRENT.remove();
            CURRENT_STEP.remove();
        }
    }

    // ------------------------------------------------------------------
    // Combined summary (rebuilt after every scenario, so it is always up to date)
    // ------------------------------------------------------------------

    private static void writeSummary() {
        try (XWPFDocument doc = new XWPFDocument()) {
            List<ScenarioResult> sorted = new ArrayList<>(RESULTS);
            sorted.sort(Comparator.comparingInt(r -> r.seq));

            long passed = sorted.stream().filter(r -> STATUS_PASSED.equalsIgnoreCase(r.status)).count();
            long failed = sorted.stream().filter(r -> STATUS_FAILED.equalsIgnoreCase(r.status)).count();
            long other = sorted.size() - passed - failed;

            addText(doc, "Banking Automation - Execution Summary",
                    16, true, false, COLOR_NAVY, ParagraphAlignment.CENTER);
            addText(doc, "Run started: " + RUN_START.format(DISPLAY) + "   |   Last updated: "
                    + ZonedDateTime.now(ZONE).format(DISPLAY), 10, false, true, COLOR_GRAY, ParagraphAlignment.CENTER);
            addText(doc, "Total: " + sorted.size() + "   Passed: " + passed + "   Failed: " + failed
                    + "   Other: " + other, 12, true, false, COLOR_BLACK, ParagraphAlignment.LEFT);

            String[] headers = {"#", "Feature", "Scenario", "Status", "Time (s)", "Report file"};
            XWPFTable table = doc.createTable(sorted.size() + 1, headers.length);
            for (int c = 0; c < headers.length; c++) {
                XWPFTableCell cell = table.getRow(0).getCell(c);
                cell.setColor("D9E2F3");
                setCell(cell, headers[c], true, COLOR_BLACK);
            }
            for (int i = 0; i < sorted.size(); i++) {
                ScenarioResult r = sorted.get(i);
                XWPFTableRow row = table.getRow(i + 1);
                String statusColor = STATUS_PASSED.equalsIgnoreCase(r.status) ? COLOR_GREEN : COLOR_RED;
                setCell(row.getCell(0), String.valueOf(r.seq), false, COLOR_BLACK);
                setCell(row.getCell(1), r.feature, false, COLOR_BLACK);
                setCell(row.getCell(2), r.name, false, COLOR_BLACK);
                setCell(row.getCell(3), r.status, true, statusColor);
                setCell(row.getCell(4), String.valueOf(r.seconds), false, COLOR_BLACK);
                setCell(row.getCell(5), r.fileName, false, COLOR_BLACK);
            }

            Files.createDirectories(RUN_DIR);
            try (OutputStream out = Files.newOutputStream(RUN_DIR.resolve(SUMMARY_FILE))) {
                doc.write(out);
            }
        } catch (IOException e) {
            // e.g. the summary is open in Word and locked on Windows
            LOG.log(Level.WARNING, "Could not write summary report", e);
        }
    }

    // ------------------------------------------------------------------
    // Small helpers
    // ------------------------------------------------------------------

    private static void setCell(XWPFTableCell cell, String text, boolean bold, String color) {
        XWPFRun run = cell.getParagraphArray(0).createRun();
        run.setText(text);
        run.setFontSize(10);
        run.setBold(bold);
        run.setColor(color);
    }

    private static XWPFRun addText(XWPFDocument doc, String text, int size, boolean bold, boolean italic,
                                   String color, ParagraphAlignment align) {
        XWPFParagraph p = doc.createParagraph();
        p.setAlignment(align);
        XWPFRun run = p.createRun();
        run.setText(text);
        run.setFontSize(size);
        run.setBold(bold);
        run.setItalic(italic);
        run.setColor(color);
        return run;
    }

    private static void addPicture(XWPFDocument doc, byte[] png, String name) {
        if (png == null) return;
        try {
            XWPFRun run = doc.createParagraph().createRun();
            run.addPicture(new ByteArrayInputStream(png), Document.PICTURE_TYPE_PNG,
                    name + "_" + System.nanoTime() + ".png", Units.toEMU(500), Units.toEMU(280));
        } catch (InvalidFormatException | IOException e) {
            LOG.log(Level.WARNING, "Could not add screenshot", e);
        }
    }

    // Turns a feature or scenario name into a safe, short file name part (no regex needed)
    private static String safe(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length() && sb.length() < MAX_NAME_PART; i++) {
            char ch = s.charAt(i);
            boolean alphanumeric = (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9');
            if (alphanumeric) {
                sb.append(ch);
            } else if (!sb.isEmpty() && sb.charAt(sb.length() - 1) != '_') {
                sb.append('_');
            }
        }
        int end = sb.length();
        while (end > 0 && sb.charAt(end - 1) == '_') {
            end--;
        }
        sb.setLength(end);
        return sb.isEmpty() ? "unnamed" : sb.toString();
    }
}