class Slot {
    private final int id;
    private final SlotType type;
    private final Floor floor;
    private SlotStatus status;
    private Vehicle vehicle;

    public Slot(int id, SlotType type, Floor floor) {
        this.id = id;
        this.type = type;
        this.floor = floor;
        this.status = SlotStatus.AVAILABLE;
    }

    public int getId() { return id; }
    public SlotType getType() { return type; }
    public Floor getFloor() { return floor; }
    public SlotStatus getStatus() { return status; }

    public void assignVehicle(Vehicle v) {
        this.vehicle = v;
        this.status = SlotStatus.OCCUPIED;
    }

    public void removeVehicle() {
        this.vehicle = null;
        this.status = SlotStatus.AVAILABLE;
    }
}