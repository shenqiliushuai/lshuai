package com.les.ls.utils;

import com.spire.pdf.PdfDocument;
import com.spire.pdf.utilities.PdfTable;
import com.spire.pdf.utilities.PdfTableExtractor;

import java.util.ArrayList;
import java.util.List;

/**
 * PDF解析
 *
 * @author lshuai
 */
public class PDFParser {
    public static String pdfFileName;

    public static List<String> readPdfTableToList() {
        List<String> list = new ArrayList<>();
        PdfDocument pdf = new PdfDocument(pdfFileName);
        PdfTableExtractor extractor = new PdfTableExtractor(pdf);
        StringBuilder builder = new StringBuilder();
        for (int pageIndex = 0; pageIndex < pdf.getPages().getCount(); pageIndex++) {
            PdfTable[] tableLists = extractor.extractTable(pageIndex);
            if (tableLists != null && tableLists.length > 0) {
                for (PdfTable table : tableLists) {
                    for (int i = 0; i < table.getRowCount(); i++) {
                        for (int j = 0; j < table.getColumnCount(); j++) {
                            String text = table.getText(i, j);
                            text = text.trim();
                            text = text.replaceAll("\\n", "");
                            builder.append(text).append("|");
                        }
                        list.add(builder.toString());
                        builder.setLength(0);
                    }
                }
            }
        }
        return list;
    }

    public static void main(String[] args) {
        pdfFileName = "C:\\Users\\lenovo\\Desktop\\33333101401210826100001.pdf";
        List<String> list = readPdfTableToList();
        list.forEach(System.out::println);
    }
}
