import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.Popup;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import javafx.scene.layout.VBox;
import com.mongodb.client.FindIterable;
import java.util.Objects;

public class BlackjackGUI extends Application {

    private Deck deck;
    private Player dealer;
    private Player user;

    private Label dealerLabel;
    private Label userLabel;

    private boolean gameOver;

    private Popup gameOutcomePopup;


    @Override
    public void start(Stage primaryStage) {

        gameOver = false;
        gameOutcomePopup = new Popup();
        deck = new Deck();
        dealer = new Player("Dealer");
        user = new Player("You");

        dealerLabel = new Label("Dealer: ");
        dealerLabel.setFont(Font.font("Impact",50));
        dealerLabel.setTextFill(Color.WHITESMOKE);

        userLabel = new Label("You: ");
        userLabel.setFont(Font.font("Impact",50));
        userLabel.setTextFill(Color.WHITESMOKE);


        Button viewDataButton = new Button("Game Log");
        viewDataButton.setStyle("-fx-font: bold italic 20pt Arial; -fx-effect: dropshadow( one-pass-box , black , 8 , 0.0 , 2 , 0 )");
        viewDataButton.setOnAction(event -> {
            showDataWindow(primaryStage);
        });

        Button clearDataButton = new Button("Clear Game Log");
        clearDataButton.setStyle("-fx-font: bold italic 20pt Arial; -fx-effect: dropshadow( one-pass-box , black , 8 , 0.0 , 2 , 0 )");
        clearDataButton.setOnAction(actionEvent -> {
            clearDatabase();
            showPopup(primaryStage,"Game log cleared");
        });

        Label titleLabel = new Label("   Blackjack!");
        titleLabel.setFont(Font.font("Impact",50));
        titleLabel.setTextFill(Color.WHITESMOKE);


        VBox labelsBox = new VBox(10, dealerLabel, userLabel);
        labelsBox.setAlignment(Pos.CENTER);

        Image image = new Image("desktop-wallpaper-blackjack-casino-game.jpeg");
        BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, false));
        Background background = new Background(backgroundImage);

        Button hitButton = new Button("Hit!");
        hitButton.setStyle("-fx-font: bold italic 20pt Arial; -fx-effect: dropshadow( one-pass-box , black , 8 , 0.0 , 2 , 0 )");
        hitButton.setOnAction(event -> {
            if (user.getHandValue() > 30) {
                gameOver = true;
            }
            if (gameOver) {
                return;
            }
            dealCard(user);
            updateLabels();

            if (user.getHandValue() > 21) {
                showPopup(primaryStage,"Bust! You lose!");
                updateOutcomeCount("Losses");
                gameOver = true;
                hitButton.setDisable(true);
            }

            if (user.getHandValue() == 21) {
                showPopup(primaryStage,"You Win!");
                updateOutcomeCount("Wins");
                gameOver = true;
                hitButton.setDisable(true);
            }
        });

        Button standButton = new Button("Stand");
        standButton.setStyle("-fx-font: bold italic 20pt Arial; -fx-effect: dropshadow( one-pass-box , black , 8 , 0.0 , 2 , 0 )");
        standButton.setOnAction(event -> {
            if (gameOver) {
                return;
            }
            playDealer();
            updateLabels();
            determineWinner(primaryStage);
            gameOver = true;

            if (user.getHandValue() > 21) {
                standButton.setDisable(true);
            }
        });

        Button newGameButton = new Button("New Game");
        newGameButton.setStyle("-fx-font: bold italic 20pt Arial; -fx-effect: dropshadow( one-pass-box , black , 8 , 0.0 , 2 , 0 )");
        newGameButton.setOnAction(event -> {
            hitButton.setDisable(false);
            standButton.setDisable(false);
            gameOver = false;
            startNewGame();
            updateLabels();
            gameOutcomePopup.hide();
        });

        HBox buttonBox = new HBox(10, hitButton, standButton, newGameButton,viewDataButton,clearDataButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(15));

        BorderPane root = new BorderPane();
        root.setCenter(labelsBox);
        root.setBottom(buttonBox);
        root.setBackground(background);
        root.setTop(titleLabel);


        Scene gameScene = new Scene(root, 400, 300);
        primaryStage.setFullScreen(true);
        primaryStage.setTitle("Blackjack");
        primaryStage.setScene(gameScene);
        primaryStage.show();


        startNewGame();
        updateLabels();
    }

    private void startNewGame() {
        deck.shuffle();
        dealer.discardCards();
        user.discardCards();
        dealCard(dealer);
        dealCard(user);
    }

    private void dealCard(Player player) {
        if (deck.isEmpty()) {
            deck.reset();
            deck.shuffle();
        }
        Card card = deck.dealCard();
        player.addCard(card);
    }

    private void playDealer() {
        while (dealer.getHandValue() < 17) {
            dealCard(dealer);
        }
    }

    private void updateLabels() {
        dealerLabel.setText("Dealer: " + dealer.getHandValue());
        userLabel.setText("You: " + user.getHandValue());
    }

    private void showPopup(Stage primaryStage, String message) {
        Label outcomeLabel = new Label(message);
        outcomeLabel.setFont(Font.font("Impact", 25));
        outcomeLabel.setTextFill(Color.WHITE);

        VBox popupContent = new VBox(outcomeLabel);
        popupContent.setAlignment(Pos.CENTER);
        popupContent.setStyle("-fx-background-color: rgba(0,0,0,0.5); -fx-padding: 25px");

        gameOutcomePopup.getContent().clear();
        gameOutcomePopup.getContent().add(popupContent);
        gameOutcomePopup.setAutoHide(true);
        gameOutcomePopup.show(primaryStage);
    }

    private void determineWinner(Stage primaryStage) {

        int dealerValue = dealer.getHandValue();
        int userValue = user.getHandValue();

        if (userValue == 21) {
            showPopup(primaryStage,"You win!");
            updateOutcomeCount("Wins");
        }
        if (userValue > 21) {
            showPopup(primaryStage,"Bust! You lose!");
            updateOutcomeCount("Losses");
        } else if (dealerValue > 21 || userValue > dealerValue) {
            showPopup(primaryStage, "You win!");
            updateOutcomeCount("Wins");
        } else if (userValue == dealerValue) {
            showPopup(primaryStage, "Push!");
            updateOutcomeCount("Pushes");
        } else {
            showPopup(primaryStage, "You lose!");
            updateOutcomeCount("Losses");
        }
    }
    private void updateOutcomeCount(String outcome) {
        try (MongoClient client = MongoClients.create("mongodb://localhost:27017")) {
            MongoDatabase database = client.getDatabase("blackjack");
            MongoCollection<Document> collection = database.getCollection("outcomes");

            Document filter = new Document("outcome", outcome);
            Document document = collection.find(filter).first();

            if (document != null) {
                int count = document.getInteger("count");
                document.put("count", count + 1);
                collection.replaceOne(filter, document);
            } else {
                Document newDocument = new Document("outcome", outcome)
                        .append("count", 1);
                collection.insertOne(newDocument);
            }
            if (document != null) {
                String json = document.toJson();
                System.out.println(json);
            }
        }
    }

    private void showDataWindow(Stage primaryStage) {

        try (MongoClient client = MongoClients.create("mongodb://localhost:27017")) {
            MongoDatabase database = client.getDatabase("blackjack");
            MongoCollection<Document> collection = database.getCollection("outcomes");

            FindIterable<Document> documents = collection.find();

            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis yAxis = new NumberAxis(0,50,5);
            BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);

            chart.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm());
            chart.setTitle("Game Statistics");
            chart.setStyle("-fx-font-family: 'Impact'; -fx-font-size: 21pt;");
            xAxis.setLabel("Outcomes");
            xAxis.setStyle("-fx-font-family: 'Impact'; -fx-font-size: 21pt;");
            yAxis.setLabel("Count");
            yAxis.setStyle("-fx-font-family: 'Impact'; -fx-font-size: 25;");
            chart.autosize();
            BackgroundFill backgroundFill = new BackgroundFill(Color.WHITESMOKE,null, null);
            chart.setLegendVisible(false);
            Background background = new Background(backgroundFill);
            chart.setBackground(background);


            XYChart.Series<String, Number> series = new XYChart.Series<>();
            for (Document document : documents) {
                String outcome = document.getString("outcome");
                Integer count = document.getInteger("count");

                if (outcome != null && count != null) {
                    series.getData().add(new XYChart.Data<>(outcome, count));
                }
            }

            ObservableList<XYChart.Series<String, Number>> data = FXCollections.observableArrayList(series);
            chart.setData(data);

            Stage stage = new Stage();
            stage.setScene(new Scene(chart, 800, 600));
            stage.show();

        }
    }
    private void clearDatabase() {
        try (MongoClient client = MongoClients.create("mongodb://localhost:27017")) {
            MongoDatabase database = client.getDatabase("blackjack");
            MongoCollection<Document> collection = database.getCollection("outcomes");

            collection.deleteMany(new Document());
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}