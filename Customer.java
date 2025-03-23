public class Customer extends Thread {
    private final TicketPool ticketPool;
    private final int customerRetrievalRate; // in milliseconds

    public Customer(TicketPool ticketPool, int customerRetrievalRate) {
        this.ticketPool = ticketPool;
        this.customerRetrievalRate = customerRetrievalRate;
    }

    @Override
    public void run() {
        while (ticketPool.hasTickets()) {
            Integer ticket = ticketPool.removeTicket();
            if (ticket == null) {
                break;
            }
            try {
                Thread.sleep(customerRetrievalRate);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("Customer finished retrieving tickets.");
    }
}
