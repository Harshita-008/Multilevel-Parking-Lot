import java.util.*;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        // Floors
        Floor f1 = new Floor(1);
        Floor f2 = new Floor(2);

        // Gates
        Gate g1 = new Gate(1, f1);
        Gate g2 = new Gate(2, f1);
        Gate g3 = new Gate(3, f1);
        Gate g4 = new Gate(4, f2);
        Gate g5 = new Gate(5, f2);

        List<Gate> gates = List.of(g1, g2, g3, g4, g5);

        // Slot Manager
        SlotManager sm = new SlotManager();

        // Floor 1
        for (int i = 1; i <= 3; i++) {
            sm.addSlot(new Slot(i, SlotType.SMALL, f1), gates);
        }
        for (int i = 4; i <= 6; i++) {
            sm.addSlot(new Slot(i, SlotType.MEDIUM, f1), gates);
        }
        for (int i = 7; i <= 8; i++) {
            sm.addSlot(new Slot(i, SlotType.LARGE, f1), gates);
        }

        // Floor 2
        for (int i = 9; i <= 11; i++) {
            sm.addSlot(new Slot(i, SlotType.SMALL, f2), gates);
        }
        for (int i = 12; i <= 14; i++) {
            sm.addSlot(new Slot(i, SlotType.MEDIUM, f2), gates);
        }
        for (int i = 15; i <= 16; i++) {
            sm.addSlot(new Slot(i, SlotType.LARGE, f2), gates);
        }

        // Parking Manager
        ParkingManager pm = new ParkingManager(
            sm,
            new NearestSlotStrategy(),
            new HourlyPricing(),
            gates
        );

        while (true) {

            System.out.println("\n1. View Available Slots");
            System.out.println("2. Park Vehicle");
            System.out.println("3. Exit Vehicle");
            System.out.println("4. Exit");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1 -> {
                    System.out.println("Enter Gate ID to view slots (1/2/3/4/5): ");
                    int gid = sc.nextInt();

                    Gate gate = gates.stream()
                        .filter(g -> g.getId() == gid)
                        .findFirst()
                        .orElse(null);

                    if (gate == null) {
                        System.out.println("Invalid gate");
                        break;
                    }

                    sm.displayAvailability(gate);
                }

                case 2 -> {

                    System.out.print("Enter Vehicle Number: ");
                    String number = sc.nextLine();

                    System.out.println("Select Vehicle Type:");
                    System.out.println("1. BIKE");
                    System.out.println("2. CAR");
                    System.out.println("3. BUS");

                    int typeChoice = sc.nextInt();

                    VehicleType vehicleType;

                    switch (typeChoice) {
                        case 1 -> vehicleType = VehicleType.BIKE;
                        case 2 -> vehicleType = VehicleType.CAR;
                        case 3 -> vehicleType = VehicleType.BUS;
                        default -> {
                            System.out.println("Invalid vehicle type");
                            continue; // go back to menu loop
                        }
                    }

                    System.out.println("Select Gate:");
                    for (Gate g : gates) {
                        System.out.println("Gate " + g.getId() +
                            " (Floor " + g.getFloor().getId() + ")");
                    }

                    int gid = sc.nextInt();

                    Gate gate = gates.stream()
                        .filter(g -> g.getId() == gid)
                        .findFirst()
                        .orElse(null);

                    if (gate == null) {
                        System.out.println("Invalid gate");
                        break;
                    }

                    Vehicle v = new Vehicle(number, vehicleType);

                    Ticket t = pm.park(v, gate);

                    if (t != null) {
                        System.out.println("Ticket ID: " + t.getId());
                        System.out.println("Allocated Slot: " +
                            t.getSlot().getId() +
                            " (Floor " + t.getSlot().getFloor().getId() + ")");
                    }
                }

                case 3 -> {
                    System.out.print("Enter Ticket ID: ");
                    String id = sc.nextLine();

                    double cost = pm.exit(id);

                    if (cost != -1) {
                        System.out.println("Total Cost: " + cost);
                    }
                }

                case 4 -> {
                    System.out.println("Exiting...");
                    return;
                }
            }
        }
    }
}