package com.example.cmtProject.util;

import org.springframework.core.io.ClassPathResource;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.pdf.BaseFont;

import java.io.OutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PdfGenerator {
    public void generatePdf(String htmlContent, String outputPath) throws Exception {
        // 첫 번째: payslip.pdf
        String outputPath1 = "D:/pdfs/payslip.pdf";
        createPdf(htmlContent, outputPath1);

        // 두 번째: empPdf.pdf 추가 생성
        String outputPath2 = "D:/pdfs/empPdf.pdf";
        createPdf(htmlContent, outputPath2);
    }

    private void createPdf(String htmlContent, String outputPath) throws Exception {
        File file = new File(outputPath);
        File parentDir = file.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();  // 폴더 자동 생성
        }

        try (OutputStream os = new FileOutputStream(outputPath)) {
            ITextRenderer renderer = new ITextRenderer();

            String fontPath = new ClassPathResource("fonts/NanumGothic-Regular.ttf")
                    .getFile().getAbsolutePath();
            renderer.getFontResolver().addFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(os);
        }
    }
}

