
package com.banking.utils;

import org.apache.poi.xwpf.usermodel.*;
import org.apache.poi.util.Units;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WordReportGenerator {

    private static XWPFDocument document;
    private static final String outputPath = "output/Parabank_Execution_Report.docx";
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");
    private static ThreadLocal<String> currentStepText = new ThreadLocal<>();

    public static synchronized void initReport() {
        if (document!= null) return;
        document = new XWPFDocument();
        new File("output").mkdirs();
        XWPFParagraph title = document.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = title.createRun();
        run.setText("Banking Automation - Parabank Execution Report");
        run.setBold(true); run.setFontSize(16); run.setColor("000080");
        XWPFParagraph datePara = document.createParagraph();
        datePara.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun dateRun = datePara.createRun();
        dateRun.setText("Execution Started: " + LocalDateTime.now().format(dtf));
        dateRun.setFontSize(10); dateRun.setItalic(true);
    }

    public static void setCurrentStep(String step) { currentStepText.set(step); }

    public static synchronized void addScenarioHeader(String feature, String scenario) {
        if (document == null) initReport();
        XWPFParagraph p = document.createParagraph();
        XWPFRun r = p.createRun();
        r.setText("\nFeature: " + feature + " | Scenario: " + scenario);
        r.setBold(true); r.setFontSize(13); r.setColor("000000");
    }

    // For Hooks @AfterStep - uses ThreadLocal and clears after
    public static synchronized void addStepResult(String feature, String scenario, int stepNum, String timestamp, byte[] screenshot) {
        if (document == null) initReport();
        String gherkin = currentStepText.get();
        if (gherkin == null || gherkin.isEmpty()) gherkin = "Step " + stepNum;
        XWPFParagraph stepPara = document.createParagraph();
        XWPFRun stepRun = stepPara.createRun();
        stepRun.setText(" " + gherkin + " | Time: " + timestamp);
        stepRun.setFontSize(10); stepRun.setBold(true); stepRun.setColor("333333");
        if (screenshot!= null) {
            try {
                XWPFParagraph imgPara = document.createParagraph();
                XWPFRun imgRun = imgPara.createRun();
                imgRun.addPicture(new ByteArrayInputStream(screenshot),
                        XWPFDocument.PICTURE_TYPE_PNG,
                        "step_" + stepNum + "_" + System.nanoTime() + ".png",
                        Units.toEMU(500), Units.toEMU(280));
            } catch (Exception e) { e.printStackTrace(); }
        }
        currentStepText.remove(); // clear only for main steps
    }

    // NEW: For intermediate screenshot inside PageObject - does NOT clear ThreadLocal
    public static synchronized void addIntermediateScreenshot(String description, String timestamp, byte[] screenshot) {
        if (document == null) initReport();
        String gherkin = currentStepText.get();
        if (gherkin == null) gherkin = description;
        XWPFParagraph stepPara = document.createParagraph();
        XWPFRun stepRun = stepPara.createRun();
        stepRun.setText(" " + gherkin + " | Time: " + timestamp + " (" + description + ")");
        stepRun.setFontSize(10); stepRun.setBold(true); stepRun.setColor("0066CC");
        if (screenshot!= null) {
            try {
                XWPFParagraph imgPara = document.createParagraph();
                XWPFRun imgRun = imgPara.createRun();
                imgRun.addPicture(new ByteArrayInputStream(screenshot),
                        XWPFDocument.PICTURE_TYPE_PNG,
                        "intermediate_" + System.nanoTime() + ".png",
                        Units.toEMU(500), Units.toEMU(280));
            } catch (Exception e) { e.printStackTrace(); }
        }
        // DO NOT remove ThreadLocal here!
    }

    public static synchronized void addScenarioFooter(String scenario, String status, String timestamp) {
        XWPFParagraph p = document.createParagraph();
        XWPFRun r = p.createRun();
        r.setText("Result: " + scenario + " - " + status + " at " + timestamp);
        r.setFontSize(11); r.setBold(true);
        if(status.equalsIgnoreCase("PASSED")) r.setColor("008000");
        else r.setColor("FF0000");
        XWPFParagraph sep = document.createParagraph();
        sep.createRun().setText("==================================================================");
    }

    public static synchronized void saveReport() {
        try (FileOutputStream out = new FileOutputStream(outputPath)) {
            document.write(out);
        } catch (Exception e) { e.printStackTrace(); }
    }
}