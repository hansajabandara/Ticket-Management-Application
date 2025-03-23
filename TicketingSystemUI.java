import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class TicketingSystemUI extends Application {

    private TextField maxTicketCapacityField;
    private TextField ticketReleaseRateField;
    private TextField customerRetrievalRateField;
    private TextField totalTicketsField;
    private TextField vendorCountField;
    private TextField customerCountField;

    private Button startButton;
    private Button stopButton;
    private Button resetButton;

    @Override
    public void start(Stage primaryStage) {
        // Create input fields
        maxTicketCapacityField = new TextField();
        ticketReleaseRateField = new TextField();
        customerRetrievalRateField = new TextField();
        totalTicketsField = new TextField();
        vendorCountField = new TextField();
        customerCountField = new TextField();

        // Create buttons
        startButton = new Button("Start");
        stopButton = new Button("Stop");
        resetButton = new Button("Reset");

        // Create a welcome message label
        Label welcomeLabel = new Label("Welcome To Ticketing Application");
        welcomeLabel.setFont(new Font("Arial", 20)); // Set the font size
        welcomeLabel.setStyle("-fx-font-weight: bold;"); // Make the text bold

        // Create VBox for configuration inputs
        VBox inputVBox = createInputVBox();

        // Create an HBox for buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(startButton, stopButton, resetButton);

        // Create a VBox to hold everything vertically
        VBox vbox = new VBox(20);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));
        vbox.getChildren().addAll(welcomeLabel, inputVBox, buttonBox); // Add the welcome message

        // Configure button actions
        configureButtonActions();

        // Set the scene with a larger window size
        Scene scene = new Scene(vbox, 600, 700); // Updated dimensions for a larger window
        primaryStage.setTitle("Ticketing System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createInputVBox() {
        // Create VBox for configuration inputs
        VBox inputVBox = new VBox(10); // Spacing of 10 between elements
        inputVBox.setAlignment(Pos.CENTER_LEFT);
        inputVBox.setPadding(new Insets(20));
        inputVBox.setStyle("-fx-background-color: lightblue; -fx-border-color: #ccc; -fx-border-width: 1px;");

        // Create GridPane for labels and fields
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10));

        // Add bold labels and input fields to the grid
        Label maxTicketCapacityLabel = new Label("Max Ticket Capacity:");
        maxTicketCapacityLabel.setStyle("-fx-font-weight: bold;");
        Label ticketReleaseRateLabel = new Label("Ticket Release Rate (ms):");
        ticketReleaseRateLabel.setStyle("-fx-font-weight: bold;");
        Label customerRetrievalRateLabel = new Label("Customer Retrieval Rate (ms):");
        customerRetrievalRateLabel.setStyle("-fx-font-weight: bold;");
        Label totalTicketsLabel = new Label("Total Tickets:");
        totalTicketsLabel.setStyle("-fx-font-weight: bold;");
        Label vendorCountLabel = new Label("Vendor Count:");
        vendorCountLabel.setStyle("-fx-font-weight: bold;");
        Label customerCountLabel = new Label("Customer Count:");
        customerCountLabel.setStyle("-fx-font-weight: bold;");

        gridPane.add(maxTicketCapacityLabel, 0, 0);
        gridPane.add(maxTicketCapacityField, 1, 0);
        gridPane.add(ticketReleaseRateLabel, 0, 1);
        gridPane.add(ticketReleaseRateField, 1, 1);
        gridPane.add(customerRetrievalRateLabel, 0, 2);
        gridPane.add(customerRetrievalRateField, 1, 2);
        gridPane.add(totalTicketsLabel, 0, 3);
        gridPane.add(totalTicketsField, 1, 3);
        gridPane.add(vendorCountLabel, 0, 4);
        gridPane.add(vendorCountField, 1, 4);
        gridPane.add(customerCountLabel, 0, 5);
        gridPane.add(customerCountField, 1, 5);

        inputVBox.getChildren().add(gridPane);
        return inputVBox;
    }

    private void configureButtonActions() {
        // Set button sizes and colors
        startButton.setPrefWidth(120);
        stopButton.setPrefWidth(120);
        resetButton.setPrefWidth(120);

        // Style buttons
        startButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");
        stopButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px;");
        resetButton.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white; -fx-font-size: 14px;");

        // Start Button Action
        startButton.setOnAction(e -> {
            try {
                String configuration = String.format(
                        "maxTicketCapacity=%s&ticketReleaseRate=%s&customerRetrievalRate=%s&totalTickets=%s&vendorCount=%s&customerCount=%s",
                        maxTicketCapacityField.getText(),
                        ticketReleaseRateField.getText(),
                        customerRetrievalRateField.getText(),
                        totalTicketsField.getText(),
                        vendorCountField.getText(),
                        customerCountField.getText()
                );
                HttpClient.sendPostRequest("http://localhost:8080/configure", configuration);
                HttpClient.sendPostRequest("http://localhost:8080/start", "");
                showAlert(Alert.AlertType.INFORMATION, "Ticket Operations Started");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error: " + ex.getMessage());
            }
        });

        // Stop Button Action
        stopButton.setOnAction(e -> {
            try {
                HttpClient.sendPostRequest("http://localhost:8080/stop", "");
                showAlert(Alert.AlertType.INFORMATION, "Ticket Operations Stopped");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error: " + ex.getMessage());
            }
        });

        // Reset Button Action
        resetButton.setOnAction(e -> {
            maxTicketCapacityField.clear();
            ticketReleaseRateField.clear();
            customerRetrievalRateField.clear();
            totalTicketsField.clear();
            vendorCountField.clear();
            customerCountField.clear();
            showAlert(Alert.AlertType.INFORMATION, "Fields Reset");
        });
    }

    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setContentText(message);
        alert.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
