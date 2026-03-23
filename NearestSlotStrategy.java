import java.util.*;

class NearestSlotStrategy implements ParkingStrategy {

    @Override
    public Slot findSlot(Vehicle vehicle, Gate gate, SlotManager manager) {

        List<SlotType> allowedTypes = getAllowedTypes(vehicle.getType());

        for (SlotType type : allowedTypes) {

            TreeSet<Slot> set = manager.getAvailableSlots(gate, type);

            if (set != null && !set.isEmpty()) {
                return set.first(); // nearest slot
            }
        }

        return null;
    }

    private List<SlotType> getAllowedTypes(VehicleType vt) {
        return switch (vt) {
            case BIKE -> List.of(SlotType.SMALL, SlotType.MEDIUM, SlotType.LARGE);
            case CAR -> List.of(SlotType.MEDIUM, SlotType.LARGE);
            case BUS -> List.of(SlotType.LARGE);
        };
    }
}