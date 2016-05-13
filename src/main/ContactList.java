
package main; /**
 * Created by Feng on 5/5/2016.
 */

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;

public class ContactList extends Application {
    public static final File contactListFile = new File("ContactList.ser");
    private int index;
    private ArrayList<Contact> contacts = new ArrayList<>();
    private TextField tfName = new TextField();
    private TextField tfPhone = new TextField();
    private TextField tfEmail = new TextField();
    private Button btnPrev = new Button("<< Previous");
    private Button btnSrch = new Button("Search ?");
    private Button btnNext = new Button("Next >>");
    private Button btnAdd = new Button("Add +");
    private Button btnEdit = new Button("Edit *");
    private Button btnRmv = new Button("Remove -");
    private Button btnClr = new Button("Clear 0");
    private Button btnQuit = new Button("Quit X");

    public ContactList() throws IOException, ClassNotFoundException {
        if (contactListFile.exists()) {
            ObjectInputStream input = new ObjectInputStream(new FileInputStream(contactListFile));
            contacts = (ArrayList<Contact>) input.readObject();
            input.close();

            this.index = 0;
            if (!contacts.isEmpty())
                this.setText(index);
        }
        else {
            ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(contactListFile));
            output.close();

            this.index = 0;
        }
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Creates a visual for contacts
        ObservableList<Contact> contactSelection = FXCollections.observableArrayList(contacts);
        ListView<Contact> listView = new ListView<>(contactSelection);
        ListProperty<Contact> listProperty = new SimpleListProperty<>();            // Allows for list refresh
        listView.itemsProperty().bind(listProperty);                                // through binding otherwise
        listProperty.set(FXCollections.observableArrayList(contacts));              // the list never updates
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        listView.setMouseTransparent(true);
        listView.setFocusTraversable(false);

        // Panes
        GridPane searchPane = new GridPane();
            searchPane.addRow(0, new Label("Name: "), tfName);
            searchPane.addRow(1, new Label("Phone: "), tfPhone);
            searchPane.addRow(2, new Label("Email: "), tfEmail);
            searchPane.setAlignment(Pos.BASELINE_LEFT);
            searchPane.setPadding(new Insets(10));

        // Gets rid of repetitive code
        ButtonRowPane btRow1 = new ButtonRowPane(btnPrev, btnSrch, btnNext);
        ButtonRowPane btRow2 = new ButtonRowPane(btnAdd, btnEdit, btnRmv);
        ButtonRowPane btRow3 = new ButtonRowPane(btnClr, btnQuit, null);

        VBox buttonVBox = new VBox();
            buttonVBox.getChildren().addAll(btRow1, btRow2, btRow3);
            buttonVBox.setPadding(new Insets(10));
            buttonVBox.setSpacing(8);

        VBox buttonSearchVBox = new VBox();
            buttonSearchVBox.getChildren().addAll(searchPane, buttonVBox);
            buttonSearchVBox.setSpacing(50);

        StackPane selectionStack = new StackPane();
            selectionStack.getChildren().add(listView);

        SplitPane splitPane = new SplitPane();
            splitPane.setDividerPositions(.55);
            splitPane.getItems().addAll(selectionStack, buttonSearchVBox);

        // Button Actions
        btnPrev.setOnAction(e -> previousContact());
        btnSrch.setOnAction(e -> searchContact());
        btnNext.setOnAction(e -> nextContact());
        btnAdd.setOnAction(e -> {
            addContact();
            listProperty.set(FXCollections.observableArrayList(contacts));      // Updates selection view when pressed
        });
        btnEdit.setOnAction(e -> {
            editContact();
            listProperty.set(FXCollections.observableArrayList(contacts));
        });
        btnRmv.setOnAction(e -> {
            removeContact();
            listProperty.set(FXCollections.observableArrayList(contacts));
        });
        btnClr.setOnAction((e -> clearText()));
        btnQuit.setOnAction(e -> {
            try {
                quitApp();
            } catch (IOException ex) {
                System.out.println("File not found");
                System.exit(0);
            }
        });

        Scene scene = new Scene(splitPane, 550, 250);
        primaryStage.setTitle("Contact List");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    // Button Functions-------------------------------------------------------------------------
    public void previousContact() {
        if (!tfName.getText().isEmpty()) {
            index--;
            try {
                this.setText(index);
            } catch (ArrayIndexOutOfBoundsException ex) {
                System.out.println(ex.getMessage());
            }
        }
        else {
            index = 0;
            this.setText(index);
        }
    }

    public void nextContact() {
        if (!tfName.getText().isEmpty()) {
            index++;
            try {
                this.setText(index);
            } catch (IndexOutOfBoundsException ex) {
                System.out.println(ex.getMessage());
                index = contacts.size() - 1;
            }
        }
        else {
            index = 0;
            this.setText(index);
        }
    }

    public void searchContact() {
        contacts.stream().filter(person -> person.getName().equalsIgnoreCase(tfName.getText())).forEach(person -> {
            index = contacts.indexOf(person);
            this.setText(index);
        });
    }

    public void addContact() {
        boolean contactExists = false;

        Contact tempContact = new Contact(tfName.getText(), tfPhone.getText(), tfEmail.getText());
        for (Contact person : contacts) {
            contactExists = person.equals(tempContact);
            if (contactExists) {
                index = contacts.indexOf(person);
                break;
            }
        }
        if (!contactExists && !tfName.getText().isEmpty()) {
            this.contacts.add(tempContact);
            index = contacts.size();
        }
    }

    public void editContact() {
        for (Contact person : contacts) {
            if (person.getName().equals(tfName.getText())) {
                index = contacts.indexOf(person);
                contacts.get(index).setPhone(tfPhone.getText());
                contacts.get(index).setEmail(tfEmail.getText());
                break;
            }
            else
                index = 0;
        }
    }

    public void removeContact() {
        Contact contact = new Contact(tfName.getText());
        for (Contact person : contacts) {
            if (person.equals(contact) == true) {
                contacts.remove(person);
                this.clearText();
                index = 0;
                break;
            }
        }
    }

    public void clearText() {
        index = 0;
        tfName.setText("");
        tfPhone.setText("");
        tfEmail.setText("");
    }

    public void quitApp() throws IOException {
        ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(contactListFile));
        output.writeObject(this.contacts);
        output.close();

        Platform.exit();
    }

    // Having to replace the text constantly required its own method
    public void setText(int i) {
        tfName.setText(contacts.get(i).getName());
        tfPhone.setText(contacts.get(i).getPhone());
        tfEmail.setText(contacts.get(i).getEmail());
    }

    public static void main(String[] args) {
        launch(args);
    }
}

// Each Button Flowpane created had the same settings which caused redundant code in start
class ButtonRowPane extends FlowPane {
    public ButtonRowPane(Button bt1, Button bt2, Button bt3) {
        if (bt3 == null)
            getChildren().addAll(bt1, bt2);
        else
            getChildren().addAll(bt1, bt2, bt3);

        this.setHgap(5);
        this.setAlignment(Pos.BASELINE_LEFT);
    }

}


