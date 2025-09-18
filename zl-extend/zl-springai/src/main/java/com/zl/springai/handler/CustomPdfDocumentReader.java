package com.zl.springai.handler;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.ai.document.Document;
import java.util.List;
import java.io.InputStream;
import java.util.Collections;

 /**
 * 文档处理器
 * @Author: GuihaoLv
 */
public class CustomPdfDocumentReader {

    public List<Document> read(InputStream inputStream) {
        try (PDDocument document = PDDocument.load(inputStream)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            return Collections.singletonList(new Document(text));
        } catch (Exception e) {
            throw new RuntimeException("Failed to read PDF", e);
        }
    }
}