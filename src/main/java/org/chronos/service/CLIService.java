package org.chronos.service;

import org.chronos.model.Assignment;
import org.chronos.model.PackageType;
import org.chronos.model.Rider;
import org.chronos.model.RiderStatus;
import org.chronos.model.Package;

import java.util.List;

public class CLIService {
    private final DispatchCenter dispatchCenter;
    private int packageIdCounter;

    public CLIService(DispatchCenter dispatchCenter) {
        this.dispatchCenter = dispatchCenter;
        this.packageIdCounter = 0;
    }

    public void handle(String input) {
        String[] parts = input.trim().split("\\s+");
        if (parts.length == 0) return;

        String command = parts[0];
        switch (command.toLowerCase()) {
            case "place_order" -> handlePlaceOrder(parts);
            case "update_rider_status" -> handleUpdateRiderStatus(parts);
            case "simulate_delivery" -> handleSimulateDelivery(parts);
            case "get_status" -> handleGetStatus(parts);
            case "audit_query" -> handleAuditQuery(parts);
            case "help" -> handleHelp();
            case "current_deliveries" -> handleCurrentDeliveries();
            default -> System.out.println("Unknown command: " + command);
        }
    }

    private void handlePlaceOrder(String[] parts) {
        if (parts.length < 4) {
            System.out.println("Usage: place_order <EXPRESS|STANDARD> <delivery_time_in_minutes> <fragile>");
            return;
        }
        String id = "PKG" + packageIdCounter;
        packageIdCounter++;

        PackageType type;
        try {
            type = PackageType.valueOf(parts[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid package type. Use EXPRESS or STANDARD.");
            return;
        }

        long minutes;
        try {
            minutes = Long.parseLong(parts[2]);
        } catch (NumberFormatException e) {
            System.out.println("Invalid delivery time. It should be a number representing minutes.");
            return;
        }

        boolean fragile = Boolean.parseBoolean(parts[3]);
        long now = System.currentTimeMillis();
        long deadline = now + (minutes * 60 * 1000); // Convert minutes to milliseconds

        Package pkg = new Package(id, type, now, deadline, fragile);
        dispatchCenter.placeOrder(pkg);
        System.out.println("Order placed: " + id);
    }

    private void handleUpdateRiderStatus(String[] parts) {
        if (parts.length < 4) {
            System.out.println("Usage: update_rider_status <rider_id> <AVAILABLE|UNAVAILABLE|DELIVERING> <can_handle_fragile> <reliability_rating>");
            return;
        }
        String id = "RD" + parts[1];
        RiderStatus status = RiderStatus.valueOf(parts[2].toUpperCase());
        boolean canHandleFragile = Boolean.parseBoolean(parts[3]);
        double rating = parts.length > 4 ? Double.parseDouble(parts[4]) : 5.0;

        Rider rider = new Rider(id, status, rating, canHandleFragile);
        dispatchCenter.updateRider(rider);
        System.out.println("Rider updated: " + id);
    }

    private void handleSimulateDelivery(String[] parts) {
        if (parts.length < 2) {
            System.out.println("Usage: simulate_delivery <package_id>");
            return;
        }
        String id = parts[1];
        dispatchCenter.simulateDelivery(id);
        System.out.println("Delivery simulated for package: " + id);
    }

    private void handleGetStatus(String[] parts) {
        if (parts.length < 2) {
            System.out.println("Usage: get_status <package_id|rider_id>");
            return;
        }
        String id = parts[1];
        Object status = dispatchCenter.getStatus(id);
        System.out.println(status != null ? status.toString() : "No record found for: " + id);
    }

    private void handleAuditQuery(String[] parts) {
        if (parts.length < 2) {
            System.out.println("Usage: audit_query <rider_id|missed_express>");
            return;
        }

        if (parts[1].equalsIgnoreCase("missed_express")) {
            List<Package> missed = dispatchCenter.getMissedExpressDeliveries();
            if (missed.isEmpty()) {
                System.out.println("No missed EXPRESS deliveries found.");
            } else {
                missed.forEach(pkg -> System.out.println("Missed: " + pkg.getId()));
            }
        } else {
            String riderId = parts[1];
            List<Package> delivered = dispatchCenter.getDeliveredByRiderInLast24Hours(riderId);
            if (delivered.isEmpty()) {
                System.out.println("No deliveries found for Rider: " + riderId + " in last 24 hours.");
            } else {
                delivered.forEach(pkg -> System.out.println("Delivered: " + pkg.getId()));
            }
        }
    }

    private void handleCurrentDeliveries() {
        List<Assignment> assignments = dispatchCenter.getAssignments();
        boolean found = false;
        for (Assignment a : assignments) {
            if (a.getDeliveryTime() == 0) {
                System.out.println("Package " + a.getPackageId() + " is being delivered by Rider " + a.getRiderId());
                found = true;
            }
        }
        if (!found) {
            System.out.println("No packages are currently being delivered.");
        }
    }

    public void handleHelp() {
        System.out.println("\nAvailable Commands:");
        System.out.println("-------------------");
        System.out.println("place_order <EXPRESS|STANDARD> <delivery_time_in_minutes> <fragile>");
        System.out.println("update_rider_status <rider_id> <AVAILABLE|UNAVAILABLE|DELIVERING> <can_handle_fragile>");
        System.out.println("simulate_delivery <package_id>");
        System.out.println("get_status <package_id|rider_id>");
        System.out.println("audit_query <rider_id>");
        System.out.println("audit_query missed_express");
        System.out.println("current_deliveries");
        System.out.println("help");
        System.out.println("exit\n");
    }
}

