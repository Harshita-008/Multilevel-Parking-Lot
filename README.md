# Multilevel Parking Lot

Console-based multilevel parking lot simulation built with a strategy-driven slot allocation flow and slot-type-based hourly pricing.

## Features

Vehicle types (`VehicleType`)
- Supports `BIKE`, `CAR`, and `BUS`.
- Slot compatibility is determined by vehicle type using `NearestSlotStrategy`.

Slot types (`SlotType`)
- Supports `SMALL`, `MEDIUM`, and `LARGE` slots.
- Slot availability is tracked per `Gate` in `SlotManager`.

Multi-floor + multi-gate setup
- The program models multiple `Floor` objects and multiple `Gate` objects (each gate belongs to one floor).
- `Main` registers all created slots into the `SlotManager` for all gates, and the chosen slot is determined using a “distance” ordering that penalizes floor differences.

Interactive operations (menu-driven)
- View available slots for a gate.
- Park a vehicle (allocates a slot and generates a ticket).
- Exit a vehicle (frees the slot and calculates the bill).

Ticket tracking
- Every parked vehicle gets a `Ticket` with a UUID `id`, plus references to the allocated `Slot` and entry `Gate`.
- Exits are processed using the ticket id.

Pricing
- Hourly pricing is based on the allocated slot’s `SlotType`.
- Cost uses ticket duration in minutes and rounds up to hours using `ceil`.

## Design Patterns

Strategy Pattern for slot allocation
- `ParkingStrategy` defines `findSlot(Vehicle vehicle, Gate gate, SlotManager manager)`.
- `NearestSlotStrategy` implements this strategy and selects the best slot by allowed `SlotType` order and `SlotManager`’s distance-based ordering.

Strategy Pattern for pricing
- `PricingStrategy` defines `calculate(Ticket ticket)`.
- `HourlyPricing` implements pricing by slot type rate and ticket duration.

Composition for orchestration
- `ParkingManager` composes:
  - `SlotManager` (availability and slot storage)
  - `ParkingStrategy` (allocation logic)
  - `PricingStrategy` (pricing logic)
  - `List<Gate>` (system context)

## System Concepts

Gates, floors, and slots
- `Floor` is identified by `id`.
- `Gate` is identified by `id` and is associated with one `Floor`.
- `Slot` is identified by `id`, has a `SlotType`, belongs to a `Floor`, and tracks `SlotStatus` (`AVAILABLE` / `OCCUPIED`).

Slot selection (“nearest” ordering)
- For each `(Gate, SlotType)`, `SlotManager` stores available slots in a `TreeSet` ordered by a computed distance:
  - `distance = floorDiff * FLOOR_PENALTY + slotId`
  - `floorDiff = abs(slot.floor.id - gate.floor.id)`
  - `FLOOR_PENALTY = 50`
- `NearestSlotStrategy` picks the first available slot in this ordered set.

Vehicle-to-slot compatibility rules (in `NearestSlotStrategy`)
- `BIKE` -> allowed `SlotType` in order: `SMALL`, then `MEDIUM`, then `LARGE`
- `CAR` -> allowed `SlotType` in order: `MEDIUM`, then `LARGE`
- `BUS` -> allowed `SlotType` in order: `LARGE`

Ticket lifecycle
- On parking:
  - `Ticket` stores `entry = LocalDateTime.now()`, plus vehicle/slot/gate references.
- On exit:
  - `Ticket.close()` sets `exit = LocalDateTime.now()`.
  - The slot is freed, and `PricingStrategy` computes cost from ticket duration.

Important pricing detail (from `HourlyPricing`)
- Duration is calculated as `Duration.between(entry, exit).toMinutes()` (whole minutes).
- Total cost is `ceil(durationMinutes / 60.0) * rate`.

## Class Diagram

![Class Diagram](./Multilevel-Parking-Lot/Multilevel%20Parking%20Lot.png)

## Usage

### Compile and run
From the `Multilevel Parking Lot` folder:

```bash
javac *.java
java Main
```

### Menu options
`1. View Available Slots`
- Enter a gate id (`1` to `5` in the current `Main` setup).

`2. Park Vehicle`
- Enter a vehicle number (string).
- Select vehicle type:
  - `1` -> `BIKE`
  - `2` -> `CAR`
  - `3` -> `BUS`
- Select gate id (`1` to `5`).
- On success, you receive a printed `Ticket ID` and allocated `Slot` info.

`3. Exit Vehicle`
- Enter `Ticket ID` (the UUID printed during parking).
- The system calculates and prints `Total Cost`.

## Running (what the program does)

When the program starts, `Main` creates:
- Floors: `Floor(1)` and `Floor(2)`
- Gates: `Gate(1..3)` on `Floor(1)`, and `Gate(4..5)` on `Floor(2)`
- Slots:
  - Floor 1:
    - `SMALL`: slot ids `1..3`
    - `MEDIUM`: slot ids `4..6`
    - `LARGE`: slot ids `7..8`
  - Floor 2:
    - `SMALL`: slot ids `9..11`
    - `MEDIUM`: slot ids `12..14`
    - `LARGE`: slot ids `15..16`

Then it enters an infinite menu loop until you choose `4`.

## Example Output

Example session (illustrative; ticket ids are UUIDs and will differ per run):

```text
1. View Available Slots
2. Park Vehicle
3. Exit Vehicle
4. Exit
2
Enter Vehicle Number: B1
Select Vehicle Type:
1. BIKE
2. CAR
3. BUS
1
Select Gate:
Gate 1 (Floor 1)
Gate 2 (Floor 1)
Gate 3 (Floor 1)
Gate 4 (Floor 2)
Gate 5 (Floor 2)
1
Ticket ID: 3f1c0c52-8c5f-4c9c-9f1b-7d4a6c0b2e0a
Allocated Slot: 1 (Floor 1)

1. View Available Slots
2. Park Vehicle
3. Exit Vehicle
4. Exit
3
Enter Ticket ID: 3f1c0c52-8c5f-4c9c-9f1b-7d4a6c0b2e0a
Total Cost: 40.0
```

Assumption used for the pricing example above: the vehicle was kept for about `61` minutes, so `ceil(61/60.0) = 2`. For slot type `SMALL`, `HourlyPricing` uses rate `20`, hence `2 * 20 = 40.0`.

Viewing availability for `Gate 1` (illustrative ordering):

```text
Enter Gate ID to view slots (1/2/3/4/5):
1
From Gate 1
SMALL: [S1,F1] [S2,F1] [S3,F1] [S9,F2] [S10,F2] [S11,F2]
MEDIUM: [S4,F1] [S5,F1] [S6,F1] [S12,F2] [S13,F2] [S14,F2]
LARGE: [S7,F1] [S8,F1] [S15,F2] [S16,F2]
```

## Key Design Principles

Single Responsibility
- Each class focuses on one job: slot storage (`SlotManager`), orchestration (`ParkingManager`), domain entities (`Vehicle`, `Slot`, `Ticket`), and algorithms (`NearestSlotStrategy`, `HourlyPricing`).

Open/Closed Principle
- New slot allocation logic can be added by implementing `ParkingStrategy` without changing `ParkingManager`.
- New pricing logic can be added by implementing `PricingStrategy` without changing `ParkingManager`.

Strategy for runtime algorithm selection
- `ParkingManager` uses injected `ParkingStrategy` and `PricingStrategy` instances.

Encapsulation + clear state transitions
- `Ticket.close()` captures exit time.
- `Slot.assignVehicle()` and `Slot.removeVehicle()` manage `SlotStatus` transitions.

Consistency via centralized orchestration
- Parking/exit logic (including ticket bookkeeping) is centralized in `ParkingManager`, keeping `Main` focused on setup and user interaction.