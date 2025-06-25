# Chronos Delivery System

Clone the repo

create jar using mvn clean package

Run the app:
java -jar target/cronos-courier-1.0-SNAPSHOT.jar

Interact using command line as per the CLI interface above

Available Commands:
-------------------
- place_order <EXPRESS|STANDARD> <delivery_time_in_minutes> <fragile(boolean)> : Used to place new package for delivery, takes DeliveryType and Delivery Deadline in minutes and if package is fragile or not; return packageId.
- update_rider_status <rider_id> <AVAILABLE|UNAVAILABLE|DELIVERING> <can_handle_fragile>: Used to create new riders or update riders status, takes rider_id, rider status and if rider can handle fragile packages.
- simulate_delivery <package_id> : used to mark package as delivered with current timestamp
- get_status <package_id|rider_id> : used to get status about any package or rider based on id
- audit_query <rider_id> : used to get deliveries done in last 24 hours.
- audit_query missed_express : used to get deliveries that were missed and were express deliveries.
- help : used to print available commands
- exit : to exit application

Features:
---------
- Used a priority queue to assign Express packages before Standard packages.
- For express packages, highest reliability available drivers are selected (Created different assignment strategy for different type of packages).
- If a driver misses a delivery their reliability is decreased by 1, they can regain .1 reliability for each successful delivery.
- Logs are stores for every information.
- evaluates packages in priority order using a custom comparator:
EXPRESS > STANDARD then Sooner deadline > Later deadline then Earlier order time > Later order time

Improvements to make:
- Can apply factory design pattern to create type of packages if they have multiple default properties differing on basis on package type.
 or maybe can use decorator pattern to simply calculation of cost of package, for example Fragile(Express(Light Weight(Package))) or Express(Heavy Weight(Package)) based on different properties.