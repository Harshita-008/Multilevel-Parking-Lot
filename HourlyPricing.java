class HourlyPricing implements PricingStrategy {

    @Override
    public double calculate(Ticket t) {

        double rate = switch (t.getSlot().getType()) {
            case SMALL -> 20;
            case MEDIUM -> 50;
            case LARGE -> 100;
        };

        long minutes = t.durationMinutes();

        return Math.ceil(minutes / 60.0) * rate;
    }
}