import java.time.*;
import java.util.UUID;

class Ticket {
    private final String id;
    private final Vehicle vehicle;
    private final Slot slot;
    private final Gate gate;
    private final LocalDateTime entry;
    private LocalDateTime exit;

    public Ticket(Vehicle v, Slot s, Gate g) {
        this.id = UUID.randomUUID().toString();
        this.vehicle = v;
        this.slot = s;
        this.gate = g;
        this.entry = LocalDateTime.now();
    }

    public void close() {
        this.exit = LocalDateTime.now();
    }

    public long durationMinutes() {
        return Duration.between(entry, exit).toMinutes();
    }

    public String getId() {
        return id;
    }

    public Slot getSlot() {
        return slot;
    }

    public Gate getGate() {
        return gate;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }
}