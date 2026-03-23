import java.util.*;

class ParkingManager {

    private final SlotManager sm;
    private final ParkingStrategy ps;
    private final PricingStrategy pr;
    private final List<Gate> gates;

    private final Map<String, Ticket> activeTickets = new HashMap<>();
    private final Map<String, Ticket> vehicleToTicket = new HashMap<>();

    public ParkingManager(SlotManager sm,
                          ParkingStrategy ps,
                          PricingStrategy pr,
                          List<Gate> gates) {
        this.sm = sm;
        this.ps = ps;
        this.pr = pr;
        this.gates = gates;
    }

    public void status() {
        for (Gate g : gates) {
            sm.displayAvailability(g);
        }
    }

    public Ticket park(Vehicle v, Gate g) {

        if (vehicleToTicket.containsKey(v.getNumber())) {
            System.out.println("Vehicle already parked!");
            return null;
        }

        Slot slot = ps.findSlot(v, g, sm);

        if (slot == null) {
            System.out.println("No slot available");
            return null;
        }

        slot.assignVehicle(v);
        sm.occupy(slot, gates);

        Ticket t = new Ticket(v, slot, g);

        activeTickets.put(t.getId(), t);
        vehicleToTicket.put(v.getNumber(), t);

        return t;
    }

    public double exit(String ticketId) {

        Ticket t = activeTickets.get(ticketId);

        if (t == null) {
            System.out.println("Invalid ticket");
            return -1;
        }

        t.close();

        Slot s = t.getSlot();
        s.removeVehicle();

        sm.free(s, gates);

        activeTickets.remove(ticketId);
        vehicleToTicket.remove(t.getVehicle().getNumber());

        return pr.calculate(t);
    }
}