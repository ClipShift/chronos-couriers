package org.chronos.service;

import org.chronos.model.Assignment;
import org.chronos.model.Package;
import org.chronos.model.PackageStatus;
import org.chronos.model.PackageType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class AuditServiceTest {

    private AuditService auditService;
    private Map<String, Package> packageStore;

    @BeforeEach
    public void setup() {
        packageStore = new HashMap<>();
        auditService = new AuditService(packageStore);
    }

    @Test
    public void testLogAdded() {
        auditService.log("Test log entry");
        List<String> logs = auditService.getAllLogs();
        assertEquals(1, logs.size());
        assertTrue(logs.get(0).contains("Test log entry"));
    }


    @Test
    public void testGetDeliveredByRider() {
        Package pkg = new Package("PKG1", PackageType.STANDARD, System.currentTimeMillis(), System.currentTimeMillis() + 60000, false);
        packageStore.put(pkg.getId(), pkg);

        Assignment assignment = new Assignment(pkg.getId(), "R1", System.currentTimeMillis() - 1000, System.currentTimeMillis());
        auditService.logAssignment(assignment);

        List<Package> result = auditService.getDeliveredByRiderInLast24Hours("R1");
        assertEquals(1, result.size());
        assertEquals(pkg, result.get(0));
    }

    @Test
    public void testGetMissedExpress() {
        long now = System.currentTimeMillis();
        Package pkg = new Package("PKG2", PackageType.EXPRESS, now - 10000, now - 5000, false);
        pkg.setStatus(PackageStatus.DELIVERED);
        packageStore.put(pkg.getId(), pkg);

        Assignment assignment = new Assignment(pkg.getId(), "R2", now - 8000, now);
        auditService.logAssignment(assignment);

        List<Package> missed = auditService.getMissedExpressDeliveries();
        assertEquals(1, missed.size());
        assertEquals(pkg, missed.get(0));
    }

    @Test
    public void testGetAllAssignments() {
        Assignment a1 = new Assignment("PKG1", "R1", System.currentTimeMillis(), 0);
        auditService.logAssignment(a1);
        assertEquals(1, auditService.getAllAssignments().size());
    }
}
