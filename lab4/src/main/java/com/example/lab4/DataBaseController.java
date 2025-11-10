package com.example.lab4;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class DataBaseController {
    @FXML
    private TableView<Node> table;
    @FXML
    private TableColumn<Node, Integer> idColumn;
    @FXML
    private TableColumn<Node, String> dataColumn;
    @FXML
    private TextField idField;
    @FXML
    private TextField dataField;
    @FXML
    private TextField searchField;
    @FXML
    private TextField generationField;
    @FXML
    private Pane treeViewPane;
    @FXML
    private ComboBox<String> dbSelector;

    private TreeVisualizer treeVisualizer;

    private final ObservableList<Node> nodes = FXCollections.observableArrayList();
    private final AVLTree avlTree = new AVLTree();

    private final Generator g = new Generator();

    private Node root = null;

    private String currentDbFile = null;
    private final ObservableList<String> dbFiles = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cellData -> cellData.getValue().getIdProperty().asObject());
        dataColumn.setCellValueFactory(cellData -> cellData.getValue().getDataProperty());
        table.setItems(nodes);

        treeVisualizer = new TreeVisualizer(treeViewPane);
        setupZoom();

        dbSelector.setItems(dbFiles);
        dbSelector.valueProperty().addListener(
                (obs, oldVal, newVal) -> onDbSelected(newVal)
        );
        populateDbList();

        table.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showNodeDetails(newValue)
        );
    }

    private void setupZoom() {
        treeViewPane.setOnScroll((ScrollEvent event) -> {
            event.consume();
            double zoomFactor = 1.05;
            double deltaY = event.getDeltaY();
            if (deltaY < 0) {
                zoomFactor = 1 / zoomFactor;
            }
            double newScale = treeViewPane.getScaleX() * zoomFactor;
            if (newScale < 0.1) newScale = 0.1;
            if (newScale > 10.0) newScale = 10.0;
            treeViewPane.setScaleX(newScale);
            treeViewPane.setScaleY(newScale);
        });
    }

    private void clearAllData() {
        root = null;
        nodes.clear();
        table.refresh();
        treeVisualizer.draw(root, avlTree);
        clearFields();
    }
    
    private void populateDbList() {
        dbFiles.clear();
        File dir = new File("."); // Поточна директорія
        File[] matchingFiles = dir.listFiles((d, name) -> name.endsWith(".txt"));

        if (matchingFiles != null) {
            for (File f : matchingFiles) {
                dbFiles.add(f.getName());
            }
        }

        if (!dbFiles.isEmpty()) {
            dbSelector.setValue(dbFiles.get(0));
        } else {
            dbSelector.setValue(null);
            clearAllData();
        }
    }

    private void onDbSelected(String dbName) {
        if (dbName == null) {
            currentDbFile = null;
            clearAllData();
        } else {
            currentDbFile = dbName;
            loadFromFile();
        }
    }

    private void refreshTableFromTree() {
        List<Node> allNodes = avlTree.getAllNodes(root);
        nodes.setAll(allNodes);
        table.refresh();

        treeVisualizer.draw(root, avlTree);
    }

    private void showNodeDetails(Node node) {
        if (node != null) {
            idField.setText(String.valueOf(node.getKey()));
            dataField.setText(node.getData());
        } else {
            clearFields();
        }
    }

    @FXML
    private void onAdd() {
        if (currentDbFile == null) {
            showAlert("Error", "Please select or create a database first.");
            return;
        }
        try {
            int id = Integer.parseInt(idField.getText());
            String data = dataField.getText();

            if (avlTree.search(root, id) != null) {
                showAlert("Error", "Record with this ID already exists!");
                return;
            }

            root = avlTree.insert(root, id, data);

            refreshTableFromTree();
            clearFields();
            saveToFile();
        } catch (NumberFormatException e) {
            showAlert("Error", "ID must be an integer!");
        }
    }

    @FXML
    private void onEdit() {
        if (currentDbFile == null) {
            showAlert("Error", "Please select or create a database first.");
            return;
        }
        Node selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "No record selected!");
            return;
        }

        try {
            int newId = Integer.parseInt(idField.getText());
            String data = dataField.getText();
            int oldId = selected.getKey();

            if (newId != oldId) {
                if (avlTree.search(root, newId) != null) {
                    showAlert("Error", "Record with this ID already exists!");
                    return;
                }
                root = avlTree.delete(root, oldId);
                root = avlTree.insert(root, newId, data);
            } else {
                root = avlTree.update(root, oldId, data);
            }

            refreshTableFromTree();
            clearFields();
            saveToFile();
        } catch (NumberFormatException e) {
            showAlert("Error", "ID must be an integer!");
        }
    }

    @FXML
    private void onDelete() {
        if (currentDbFile == null) {
            showAlert("Error", "Please select or create a database first.");
            return;
        }
        Node selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            root = avlTree.delete(root, selected.getKey());
            refreshTableFromTree();
            saveToFile();
        } else {
            showAlert("Error", "No record selected!");
        }
    }

    @FXML
    private void onSearch() {
        String query = searchField.getText();
        if (query.isEmpty()) {
            onShowAll();
            return;
        }

        try {
            int idToSearch = Integer.parseInt(query);

            avlTree.resetComparisonCount();
            Node foundNode = avlTree.search(root, idToSearch);
            int comp = avlTree.getComparisonCount();

            ObservableList<Node> searchResultList = FXCollections.observableArrayList();

            if (foundNode != null) {
                searchResultList.add(foundNode);
                table.setItems(searchResultList);

                showAlert("Search Result",
                        "Record found!\nID: " + foundNode.getKey() +
                                "\nData: " + foundNode.getData() +
                                "\n\nComparisons: " + comp);
            } else {
                table.setItems(FXCollections.observableArrayList());
                showAlert("Search Result",
                        "Record not found.\nComparisons: " + comp);
            }

        } catch (NumberFormatException e) {
            showAlert("Error", "Search must be by a numeric ID.");
            table.setItems(FXCollections.observableArrayList());
        }
    }

    @FXML
    private void onShowAll() {
        table.setItems(nodes);
    }

    @FXML
    private void onGenerate() {
        String query = generationField.getText();
        if (query.isEmpty()) {
            showAlert("Error", "Please enter the number of records to generate.");
            return;
        }
        if (currentDbFile == null) {
            showAlert("Error", "Please select or create a database first.");
            return;
        }

        try {
            int n = Integer.parseInt(query);
            if (n <= 0) {
                showAlert("Error", "Amount must be a positive number.");
                return;
            }

            g.Generate(n, currentDbFile);
            loadFromFile();

            refreshTableFromTree();

            showAlert("Success", "Generated and saved " + n + " new records to " + currentDbFile);

        } catch (NumberFormatException e) {
            showAlert("Error", "Amount of records to generate should be a natural number.");
        }
    }

    @FXML
    private void onCreateDb() {
        TextInputDialog dialog = new TextInputDialog("new_db.txt");
        dialog.setTitle("Create New Database");
        dialog.setHeaderText("Enter a filename for the new database.");
        dialog.setContentText("Name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            if (name.isEmpty() || !name.endsWith(".txt")) {
                showAlert("Error", "Invalid filename. Must end with .txt");
                return;
            }
            if (dbFiles.contains(name)) {
                showAlert("Error", "A database with this name already exists.");
                return;
            }

            try {
                new File(name).createNewFile();

                dbFiles.add(name);
                dbSelector.setValue(name); // автоматичний виклик onDbSelected

            } catch (IOException e) {
                showAlert("Error", "Could not create file: " + e.getMessage());
            }
        });
    }

    @FXML
    private void onDeleteDb() {
        if (currentDbFile == null) {
            showAlert("Error", "No database selected to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Database");
        confirm.setHeaderText("Delete " + currentDbFile + "?");
        confirm.setContentText("This action is permanent and cannot be undone.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                new File(currentDbFile).delete();
                dbFiles.remove(currentDbFile);

                if (dbFiles.isEmpty()) {
                    dbSelector.setValue(null);
                } else {
                    dbSelector.setValue(dbFiles.get(0));
                }

            } catch (Exception e) {
                showAlert("Error", "Could not delete file: " + e.getMessage());
            }
        }
    }

    public void saveToFile() {
        if (currentDbFile == null) {
            return;
        }
        try (PrintWriter writer = new PrintWriter(currentDbFile)) {
            for (Node node : nodes) {
                writer.println(node.getKey() + ";" + node.getData());
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to save data: " + e.getMessage());
        }
    }

    public void loadFromFile() {
        clearAllData();

        if (currentDbFile == null) return;

        File file = new File(currentDbFile);
        if (!file.exists()) return;

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(";", 2);
                if (parts.length == 2) {
                    int key = Integer.parseInt(parts[0]);
                    String data = parts[1];
                    root = avlTree.insert(root, key, data);
                }
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to load data: " + e.getMessage());
        }

        refreshTableFromTree();
    }

    private void clearFields() {
        idField.clear();
        dataField.clear();
        table.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}