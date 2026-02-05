package com.ek.app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Service
@Slf4j
public class PrinterService {

    @Value("${printer.name:TSC}")
    private String printerName;

    /**
     * Prints raw PDF bytes to the configured local printer.
     * For TSC printers, if they accept image or TSPL, you might need to convert PDF to image or send TSPL.
     */
    public boolean printPdf(byte[] pdfBytes) {
        try {
            DocFlavor flavor = DocFlavor.INPUT_STREAM.PDF;
            PrintService service = findPrintService(printerName, flavor);
            if (service == null) {
                log.error("Printer not found: " + printerName);
                return false;
            }
            DocPrintJob job = service.createPrintJob();
            try (InputStream is = new ByteArrayInputStream(pdfBytes)) {
                Doc doc = new SimpleDoc(is, flavor, null);
                job.print(doc, new HashPrintRequestAttributeSet());
            }
            return true;
        } catch (Exception e) {
            log.error("Printing failed", e);
            return false;
        }
    }

    private PrintService findPrintService(String name, DocFlavor flavor) {
        PrintService[] services = PrintServiceLookup.lookupPrintServices(flavor, null);
        for (PrintService s : services) {
            if (s.getName().toLowerCase().contains(name.toLowerCase())) {
                return s;
            }
        }
        // fallback: try any service
        PrintService[] all = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService s : all) {
            if (s.getName().toLowerCase().contains(name.toLowerCase())) {
                return s;
            }
        }
        return null;
    }
}
