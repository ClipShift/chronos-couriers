package org.chronos.strategy;

import org.chronos.model.Package;
import org.chronos.model.PackageType;
import org.chronos.model.Rider;
import org.chronos.model.RiderStatus;
import org.chronos.strategy.impl.ExpressAssignmentStrategy;
import org.chronos.strategy.impl.StandardAssignmentStrategy;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class AssignmentStrategyTest {

    @Test
    public void testGetAssignmentStrategyForExpress() {
        Package expressPkg = new Package("PKG1", PackageType.EXPRESS, System.currentTimeMillis(), System.currentTimeMillis() + 60000, false);
        AssignmentStrategy strategy = AssignmentStrategy.getAssignmentStrategyForPackage(expressPkg);
        assertInstanceOf(ExpressAssignmentStrategy.class, strategy);
    }

    @Test
    public void testGetAssignmentStrategyForStandard() {
        Package stdPkg = new Package("PKG2", PackageType.STANDARD, System.currentTimeMillis(), System.currentTimeMillis() + 60000, false);
        AssignmentStrategy strategy = AssignmentStrategy.getAssignmentStrategyForPackage(stdPkg);
        assertInstanceOf(StandardAssignmentStrategy.class, strategy);
    }

    @Test
    public void testExpressStrategyHighestReliability() {
        Rider r1 = new Rider("R1", RiderStatus.AVAILABLE, 4.0, true);
        Rider r2 = new Rider("R2", RiderStatus.AVAILABLE, 5.0, true);
        Rider r3 = new Rider("R3", RiderStatus.DELIVERING, 5.0, true);

        Package expressPkg = new Package("PKG1", PackageType.EXPRESS, System.currentTimeMillis(), System.currentTimeMillis() + 60000, false);
        AssignmentStrategy strategy = new ExpressAssignmentStrategy();

        Optional<Rider> rider = strategy.findRider(expressPkg, List.of(r1, r2, r3));
        assertTrue(rider.isPresent());
        assertEquals("R2", rider.get().getId());
    }

    @Test
    public void testStandardStrategyReturnsFirstAvailable() {
        Rider r1 = new Rider("R1", RiderStatus.AVAILABLE, 3.5, true);
        Rider r2 = new Rider("R2", RiderStatus.AVAILABLE, 4.0, true);

        Package stdPkg = new Package("PKG2", PackageType.STANDARD, System.currentTimeMillis(), System.currentTimeMillis() + 60000, false);
        AssignmentStrategy strategy = new StandardAssignmentStrategy();

        Optional<Rider> rider = strategy.findRider(stdPkg, List.of(r1, r2));
        assertTrue(rider.isPresent());
        assertEquals("R1", rider.get().getId());
    }

    @Test
    public void testStrategyHandlesNoAvailableRiders() {
        Rider r1 = new Rider("R1", RiderStatus.UNAVAILABLE, 3.0, true);

        Package pkg = new Package("PKG3", PackageType.EXPRESS, System.currentTimeMillis(), System.currentTimeMillis() + 60000, false);
        AssignmentStrategy strategy = AssignmentStrategy.getAssignmentStrategyForPackage(pkg);

        Optional<Rider> rider = strategy.findRider(pkg, List.of(r1));
        assertTrue(rider.isEmpty());
    }
}
