import java.util.*;

class SlotManager {

    private final Map<Gate, Map<SlotType, TreeSet<Slot>>> available = new HashMap<>();

    public void addSlot(Slot slot, List<Gate> gates) {

        for (Gate gate : gates) {

            available
                .computeIfAbsent(gate, g -> new HashMap<>())
                .computeIfAbsent(slot.getType(), t ->
                    new TreeSet<>(Comparator
                        .comparingInt((Slot s) -> calculateDistance(s, gate))
                        .thenComparingInt(Slot::getId)
                    )
                )
                .add(slot);
        }
    }

    public TreeSet<Slot> getAvailableSlots(Gate gate, SlotType type) {
        return available
                .getOrDefault(gate, Collections.emptyMap())
                .get(type);
    }

    public void occupy(Slot slot, List<Gate> gates) {

        for (Gate gate : gates) {
            TreeSet<Slot> set = available.get(gate).get(slot.getType());
            if (set != null) {
                set.remove(slot);
            }
        }
    }

    public void free(Slot slot, List<Gate> gates) {
        addSlot(slot, gates);
    }

    private int calculateDistance(Slot slot, Gate gate) {

        int floorDiff = Math.abs(
            slot.getFloor().getId() - gate.getFloor().getId()
        );

        int FLOOR_PENALTY = 50;

        return floorDiff * FLOOR_PENALTY + slot.getId();
    }

    public void displayAvailability(Gate gate) {

        Map<SlotType, TreeSet<Slot>> map = available.get(gate);

        if (map == null) return;

        System.out.println("From Gate " + gate.getId());

        for (var entry : map.entrySet()) {
            System.out.print(entry.getKey() + ": ");

            for (Slot s : entry.getValue()) {
                System.out.print(
                    "[S" + s.getId() +
                    ",F" + s.getFloor().getId() + "] "
                );
            }

            System.out.println();
        }
    }
}