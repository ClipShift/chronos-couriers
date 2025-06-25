package org.chronos.service;

import org.chronos.model.Assignment;
import org.chronos.model.PackageStatus;
import org.chronos.model.PackageType;
import org.chronos.model.Package;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class AuditService {

    private final List<String> logs = new ArrayList<>();
    private final List<Assignment> assignments = new ArrayList<>();
    private final Map<String, Package> packageStore;

    public AuditService(Map<String, Package> packageStore) {
        this.packageStore = packageStore;
    }

    public void log(String message) {
        logs.add(System.currentTimeMillis() + ": " + message);
    }

    public void logAssignment(Assignment assignment) {
        assignments.add(assignment);
    }

    public List<String> getAllLogs() {
        return Collections.unmodifiableList(logs);
    }

    public List<String> getLogsInLastNMillis(long millis) {
        long now = System.currentTimeMillis();
        return logs.stream()
                .filter(entry -> {
                    int idx = entry.indexOf(":");
                    if (idx <= 0) return false;
                    try {
                        long timestamp = Long.parseLong(entry.substring(0, idx));
                        return now - timestamp <= millis;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                })
                .toList();
    }

    public List<String> getLogsByKeyword(String keyword) {
        return logs.stream()
                .filter(log -> log.contains(keyword))
                .toList();
    }

    public List<Package> getDeliveredByRiderInLast24Hours(String riderId) {
        long now = System.currentTimeMillis();
        long cutoff = now - 24 * 60 * 60 * 1000;

        return assignments.stream()
                .filter(a -> a.getRiderId().equals(riderId))
                .filter(a -> a.getDeliveryTime() >= cutoff)
                .map(a -> packageStore.get(a.getPackageId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<Package> getMissedExpressDeliveries() {
        return packageStore.values().stream()
                .filter(pkg -> pkg.getType() == PackageType.EXPRESS)
                .filter(pkg -> pkg.getStatus() == PackageStatus.DELIVERED)
                .filter(pkg -> {
                    Assignment assignment = assignments.stream()
                            .filter(a -> a.getPackageId().equals(pkg.getId()))
                            .findFirst()
                            .orElse(null);
                    return assignment != null && assignment.getDeliveryTime() > pkg.getDeliveryDeadline();
                })
                .collect(Collectors.toList());
    }

    public List<Assignment> getAllAssignments() {
        return assignments;
    }
}
