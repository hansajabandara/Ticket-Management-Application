import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TicketPool {
    private final List<Integer> tickets = Collections.synchronizedList(new ArrayList<>());
    private final int maxCapacity;

    public TicketPool(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public synchronized boolean addTicket(int ticketId) {
        if (tickets.size() < maxCapacity) {
            tickets.add(ticketId);
            System.out.println("Vendor added ticket: " + ticketId + " | Total Tickets: " + tickets.size());
            return true;
        }
        System.out.println("Vendor cannot add ticket. Pool is full.");
        return false;
    }

    public synchronized Integer removeTicket() {
        if (!tickets.isEmpty()) {
            Integer ticket = tickets.remove(0);
            System.out.println("Customer retrieved ticket: " + ticket + " | Remaining Tickets: " + tickets.size());
            return ticket;
        }
        return null;
    }

    public synchronized boolean hasTickets() {
        return !tickets.isEmpty();
    }

    public synchronized boolean isFull() {
        return tickets.size() >= maxCapacity;
    }

    public synchronized int getTotalTickets() {
        return tickets.size();
    }
}
