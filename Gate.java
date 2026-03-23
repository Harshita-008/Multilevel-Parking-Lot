class Gate {
    private final int id;
    private final Floor floor;

    public Gate(int id, Floor floor) {
        this.id = id;
        this.floor = floor;
    }

    public int getId() {
        return id;
    }

    public Floor getFloor() {
        return floor;
    }
}