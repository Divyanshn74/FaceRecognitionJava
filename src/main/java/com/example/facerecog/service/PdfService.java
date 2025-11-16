package com.example.facerecog.service;

import com.example.facerecog.model.Attendance;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PdfService {

    public ByteArrayInputStream generateAttendancePdf(List<Attendance> attendanceList, LocalDate startDate, LocalDate endDate) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, out);

        document.open();

        // Title
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.DARK_GRAY);
        Paragraph title = new Paragraph("Attendance Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Subtitle
        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.GRAY);
        Paragraph subtitle = new Paragraph("From: " + startDate + " To: " + endDate, subtitleFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(30);
        document.add(subtitle);

        // Group by student
        Map<String, List<Attendance>> groupedByStudent = attendanceList.stream()
                .collect(Collectors.groupingBy(att -> att.getStudent().getName()));

        Font studentNameFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
        Font tableCellFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

        for (Map.Entry<String, List<Attendance>> entry : groupedByStudent.entrySet()) {
            Paragraph studentName = new Paragraph(entry.getKey(), studentNameFont);
            studentName.setSpacingAfter(10);
            document.add(studentName);

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3f, 1f});

            // Table Header
            PdfPCell dateHeader = new PdfPCell(new Phrase("Date", tableHeaderFont));
            dateHeader.setBackgroundColor(new Color(230, 230, 230));
            dateHeader.setBorderWidth(1);
            dateHeader.setPadding(5);
            table.addCell(dateHeader);

            PdfPCell statusHeader = new PdfPCell(new Phrase("Status", tableHeaderFont));
            statusHeader.setBackgroundColor(new Color(230, 230, 230));
            statusHeader.setBorderWidth(1);
            statusHeader.setPadding(5);
            table.addCell(statusHeader);

            // Table Body
            for (Attendance att : entry.getValue()) {
                PdfPCell dateCell = new PdfPCell(new Phrase(att.getDate().toString(), tableCellFont));
                dateCell.setPadding(5);
                table.addCell(dateCell);

                PdfPCell statusCell = new PdfPCell(new Phrase(att.getStatus(), tableCellFont));
                if ("PRESENT".equals(att.getStatus())) {
                    statusCell.getPhrase().getFont().setColor(new Color(34, 139, 34));
                } else {
                    statusCell.getPhrase().getFont().setColor(Color.RED);
                }
                statusCell.setPadding(5);
                table.addCell(statusCell);
            }
            document.add(table);
        }

        document.close();
        return new ByteArrayInputStream(out.toByteArray());
    }
}
