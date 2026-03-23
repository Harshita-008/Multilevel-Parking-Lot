interface ParkingStrategy {
    Slot findSlot(Vehicle vehicle, Gate gate, SlotManager manager);
}