package org.chronos.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AuditService {
    private final List<String> logs = new ArrayList<>();

    public void log(String message) {
        logs.add(System.currentTimeMillis() + ": " + message);
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
}
