package com.ek.app.scheduler;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ek.app.service.FlipkartClient;
import com.ek.app.service.NotificationService;
import com.ek.app.service.PrinterService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class LabelAgentScheduler {

    private final FlipkartClient flipkartClient;
    private final PrinterService printerService;
    private final NotificationService notificationService;

    @Scheduled(cron = "${agent.poll.cron}")
    public void processPendingShipments() {
        log.info("Agent run - fetching pending label shipments");
        try {
            List<Shipment> shipments = flipkartClient.fetchPendingLabelShipments();
            for (Shipment s : shipments) {
                try {
                    byte[] pdf = flipkartClient.generateLabelPdf(s.getShipmentId());
                    if (pdf == null) {
                        notificationService.notifyAdmin("Label generation failed for: " + s.getShipmentId());
                        continue;
                    }
                    boolean printed = printerService.printPdf(pdf);
                    if (printed) {
                        boolean updated = flipkartClient.markShipmentRTD(s.getShipmentId());
                        if (!updated) {
                            notificationService.notifyAdmin("Printed but failed to mark RTD for: " + s.getShipmentId());
                        }
                    } else {
                        notificationService.notifyAdmin("Printing failed for: " + s.getShipmentId());
                    }
                } catch (Exception ex) {
                    log.error("Processing failed for shipment " + s.getShipmentId(), ex);
                    notificationService.notifyAdmin("Processing failed for: " + s.getShipmentId() + " - " + ex.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Agent failed", e);
            notificationService.notifyAdmin("Agent failed: " + e.getMessage());
        }
    }
}
