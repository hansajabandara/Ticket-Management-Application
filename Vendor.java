public class Vendor extends Thread {
    private final TicketPool ticketPool;
    private final int ticketReleaseRate; // in milliseconds
    private int totalTicketsToAdd;
    private int ticketCounter = 0;

    public Vendor(TicketPool ticketPool, int ticketReleaseRate, int totalTicketsToAdd) {
        this.ticketPool = ticketPool;
        this.ticketReleaseRate = ticketReleaseRate;
        this.totalTicketsToAdd = totalTicketsToAdd;
    }

    @Override
    public void run() {
        while (totalTicketsToAdd > 0 && !ticketPool.isFull()) {
            boolean added = ticketPool.addTicket(++ticketCounter);
            if (added) {
                totalTicketsToAdd--;
            }
            try {
                Thread.sleep(ticketReleaseRate);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        // Display the total tickets in the system at the end
        System.out.println("Total Tickets in System: " + ticketPool.getTotalTickets());
    }
}
