package animatedtablerow ;

import java.util.Arrays;

import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
 
public class AnimatedTableRow extends Application {
 
   
    public static void main(String[] args) {
        launch(args);
    }
 
    @Override
    public void start(Stage stage) {
        
        stage.setTitle("Table View Sample");
        stage.setWidth(900);
        stage.setHeight(500);
 
        final ObservableList<Person> data =
            FXCollections.observableArrayList(
                new Person("Jacob", "Smith", "jacob.smith@example.com"),
                new Person("Isabella", "Johnson", "isabella.johnson@example.com"),
                new Person("Ethan", "Williams", "ethan.williams@example.com"),
                new Person("Emma", "Jones", "emma.jones@example.com"),
                new Person("Michael", "Brown", "michael.brown@example.com")
            );
        
        final TableView<Person> contactTable = createTable();
        contactTable.setPlaceholder(new Label("No more contacts to select"));
        contactTable.setItems(data);
 
        final Node contactContainer = createTableContainer("Address Book", contactTable);
        
        final TableView<Person> toTable = createTable();
        toTable.setPlaceholder(new Label("No contacts selected"));
        
        final Node toContainer = createTableContainer("Selected Contacts: ", toTable);

        final BorderPane root = new BorderPane();
        final SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(contactContainer, toContainer);
        root.setCenter(splitPane);
        final Scene scene = new Scene(root);

        contactTable.setRowFactory(new Callback<TableView<Person>, TableRow<Person>>() {  
            @Override  
            public TableRow<Person> call(TableView<Person> tableView) {  
                final TableRow<Person> row = new TableRow<>();  
                row.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (event.getClickCount() == 2 && row.getItem() != null) {
                            // Create imageview to display snapshot of row:
                            final Image image = row.snapshot(null, null);
                            final ImageView imageView = new ImageView(image);
                            // Start animation at current row:
                            final Point2D rowLocation = row.localToScene(new Point2D(0, 0));
                            final double startX = rowLocation.getX();
                            final double startY = rowLocation.getY();
                            // End animation at first row (approximately: just do 30 px below top of table) 
                            final Point2D toTableLocation = toTable.localToScene(new Point2D(0,0));
                            final double endX = toTableLocation.getX();
                            final double endY = toTableLocation.getY() + 30 ;
                            // Manage image location ourselves (don't let layout manage it)
                            imageView.setManaged(false);
                            // Set start location
                            imageView.relocate(startX, startY);
                            // Create animation
                            final TranslateTransition transition = new TranslateTransition(Duration.seconds(0.4), imageView);
                            // At end of animation, actually move data, and remove animated image
                            transition.setOnFinished(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    // Remove from first table
                                    contactTable.getItems().remove(row.getItem());
                                    // Add to first row of second table, looks better given the animation:
                                    toTable.getItems().add(0, row.getItem());
                                    // Remove animated image
                                    root.getChildren().remove(imageView);
                                }
                            });
                            // configure transition
                            transition.setByX(endX - startX);
                            transition.setByY(endY - startY);
                            // add animated image to display
                            root.getChildren().add(imageView);
                            // start animation
                            transition.play();
                        }
                    }
                });
                return row ;  
            }  
        });  
       
        stage.setScene(scene);
        stage.show();
    }

    private Node createTableContainer(final String labelText, final TableView<Person> table) {
        final VBox contactContainer = new VBox();
        contactContainer.setSpacing(5);
        contactContainer.setPadding(new Insets(10, 0, 0, 10));
        final Label label = new Label(labelText);
        label.setFont(new Font("Arial", 20));
        contactContainer.getChildren().addAll(label, table);
        final HBox container = new HBox();
        container.getChildren().add(contactContainer);
        return container;
    }
    
    private TableView<Person> createTable() {
        final TableView<Person> table = new TableView<Person>();
        table.setEditable(true);
 
        TableColumn<Person, String> firstNameCol = new TableColumn<>("First Name");
        firstNameCol.setMinWidth(100);
        firstNameCol.setCellValueFactory(
                new PropertyValueFactory<Person, String>("firstName"));
 
        TableColumn<Person, String> lastNameCol = new TableColumn<>("Last Name");
        lastNameCol.setMinWidth(100);
        lastNameCol.setCellValueFactory(
                new PropertyValueFactory<Person, String>("lastName"));
 
        TableColumn<Person, String> emailCol = new TableColumn<>("Email");
        emailCol.setMinWidth(200);
        emailCol.setCellValueFactory(
                new PropertyValueFactory<Person, String>("email"));
 
        table.getColumns().addAll(Arrays.asList(firstNameCol, lastNameCol, emailCol));
        return table;
    }
 
    public static class Person {
 
        private final SimpleStringProperty firstName;
        private final SimpleStringProperty lastName;
        private final SimpleStringProperty email;
 
        private Person(String fName, String lName, String email) {
            this.firstName = new SimpleStringProperty(fName);
            this.lastName = new SimpleStringProperty(lName);
            this.email = new SimpleStringProperty(email);
        }
 
        public String getFirstName() {
            return firstName.get();
        }
 
        public void setFirstName(String fName) {
            firstName.set(fName);
        }
 
        public String getLastName() {
            return lastName.get();
        }
 
        public void setLastName(String fName) {
            lastName.set(fName);
        }
 
        public String getEmail() {
            return email.get();
        }
 
        public void setEmail(String fName) {
            email.set(fName);
        }
    }
} 