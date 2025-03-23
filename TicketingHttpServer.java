import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TicketingHttpServer {
    private static TicketPool ticketPool;
    private static int maxTicketCapacity = 10;
    private static int ticketReleaseRate = 1000; // in milliseconds
    private static int customerRetrievalRate = 1000; // in milliseconds
    private static int totalTickets = 0;
    private static int vendorCount = 1;
    private static int customerCount = 1;
    private static boolean isRunning = false;

    private static final List<String> transactionLog = new ArrayList<>();
    private static ExecutorService executorService;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/configure", TicketingHttpServer::handleConfigure);
        server.createContext("/start", TicketingHttpServer::handleStart);
        server.createContext("/stop", TicketingHttpServer::handleStop);
        server.createContext("/log", TicketingHttpServer::handleLog);

        server.setExecutor(null);
        System.out.println("Server started on port 8080...");
        server.start();
    }

    private static void handleConfigure(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            sendResponse(exchange, 405, "Method Not Allowed");
            return;
        }

        String requestBody = new String(exchange.getRequestBody().readAllBytes());
        String[] params = requestBody.split("&");

        for (String param : params) {
            String[] keyValue = param.split("=");
            switch (keyValue[0]) {
                case "maxTicketCapacity" -> maxTicketCapacity = Integer.parseInt(keyValue[1]);
                case "ticketReleaseRate" -> ticketReleaseRate = Integer.parseInt(keyValue[1]);
                case "customerRetrievalRate" -> customerRetrievalRate = Integer.parseInt(keyValue[1]);
                case "totalTickets" -> totalTickets = Integer.parseInt(keyValue[1]);
                case "vendorCount" -> vendorCount = Integer.parseInt(keyValue[1]);
                case "customerCount" -> customerCount = Integer.parseInt(keyValue[1]);
            }
        }

        // Validate the ticket counts
        if (maxTicketCapacity < totalTickets) {
            sendResponse(exchange, 400, "maxTicketCapacity must be greater than or equal to initialTickets.");
            return;
        }

        ticketPool = new TicketPool(maxTicketCapacity);
        // Add initial tickets to the system
        for (int i = 1; i <= totalTickets; i++) {
            ticketPool.addTicket(i);
        }

        // Log the initial ticket count in the response
        String response = "Configuration updated. Total Tickets: " + totalTickets;
        sendResponse(exchange, 200, response);
    }


    private static void handleStart(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            sendResponse(exchange, 405, "Method Not Allowed");
            return;
        }

        if (isRunning) {
            sendResponse(exchange, 200, "Ticket operations are already running.");
            return;
        }

        isRunning = true;
        executorService = Executors.newFixedThreadPool(vendorCount + customerCount);

        // Log the initial number of tickets in the system
        String initialTicketsMessage = "Total Tickets in System: " + ticketPool.getTotalTickets();
        System.out.println(initialTicketsMessage); // You can also log this to the response if needed.

        for (int i = 0; i < vendorCount; i++) {
            executorService.execute(new Vendor(ticketPool, ticketReleaseRate, maxTicketCapacity - ticketPool.getTotalTickets()));
        }

        for (int i = 0; i < customerCount; i++) {
            executorService.execute(new Customer(ticketPool, customerRetrievalRate));
        }

        sendResponse(exchange, 200, "Ticket operations started.");
    }


    private static void handleStop(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            sendResponse(exchange, 405, "Method Not Allowed");
            return;
        }

        if (!isRunning) {
            sendResponse(exchange, 200, "Ticket operations are not running.");
            return;
        }

        isRunning = false;
        if (executorService != null) {
            executorService.shutdownNow();
        }

        sendResponse(exchange, 200, "Ticket operations stopped.");
    }

    private static void handleLog(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            sendResponse(exchange, 405, "Method Not Allowed");
            return;
        }

        StringBuilder logResponse = new StringBuilder();
        synchronized (transactionLog) {
            transactionLog.forEach(logResponse::append);
        }

        sendResponse(exchange, 200, logResponse.toString());
    }

    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
