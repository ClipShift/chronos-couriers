package org.chronos.strategy;

import org.chronos.model.Rider;
import org.chronos.model.Package;
import org.chronos.strategy.impl.ExpressAssignmentStrategy;
import org.chronos.strategy.impl.StandardAssignmentStrategy;

import java.util.Collection;
import java.util.Optional;

public interface AssignmentStrategy {
    Optional<Rider> findRider(Package pkg, Collection<Rider> riders);
    static AssignmentStrategy getAssignmentStrategyForPackage(Package pkg){
        return switch (pkg.getType()) {
            case EXPRESS -> new ExpressAssignmentStrategy();
            case null, default -> new StandardAssignmentStrategy();
        };
    }
}

