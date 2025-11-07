package com.example.lab4;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

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

    private final ObservableList<Node> nodes = FXCollections.observableArrayList();
    private final AVLTree avlTree = new AVLTree();
    private Node root = null;

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cellData -> cellData.getValue().getIdProperty().asObject());
        dataColumn.setCellValueFactory(cellData -> cellData.getValue().getDataProperty());
        table.setItems(nodes);

        loadFromFile();

        table.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showNodeDetails(newValue)
        );
    }

    private void refreshTableFromTree() {
        List<Node> allNodes = avlTree.getAllNodes(root);
        nodes.setAll(allNodes);
        table.refresh();
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
        try {
            int id = Integer.parseInt(idField.getText());
            String data = dataField.getText();

            // Перевіряємо наявність ID в ДЕРЕВІ
            if (avlTree.search(root, id) != null) {
                showAlert("Error", "Record with this ID already exists!");
                return;
            }

            root = avlTree.insert(root, id, data);

            refreshTableFromTree();
            clearFields();
        } catch (NumberFormatException e) {
            showAlert("Error", "ID must be an integer!");
        }
    }

    @FXML
    private void onEdit() {
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
        } catch (NumberFormatException e) {
            showAlert("Error", "ID must be an integer!");
        }
    }

    @FXML
    private void onDelete() {
        Node selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            root = avlTree.delete(root, selected.getKey());
            refreshTableFromTree();
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

            Node foundNode = avlTree.search(root, idToSearch);

            ObservableList<Node> searchResultList = FXCollections.observableArrayList();

            if (foundNode != null) {
                searchResultList.add(foundNode);
            }

            table.setItems(searchResultList);

        } catch (NumberFormatException e) {
            showAlert("Error", "Search must be by a numeric ID.");
            table.setItems(FXCollections.observableArrayList());
        }
    }

    // podunaty nad vydalennyam
    @FXML
    private void onShowAll() {
        table.setItems(nodes);
    }
    //-------------------------

    public void saveToFile() {
        try (java.io.PrintWriter writer = new java.io.PrintWriter("database.txt")) {
            for (Node node : nodes) {
                writer.println(node.getKey() + ";" + node.getData());
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to save data: " + e.getMessage());
        }
    }

    public void loadFromFile() {
        java.io.File file = new java.io.File("database.txt");
        if (!file.exists()) return;

        try (java.util.Scanner scanner = new java.util.Scanner(file)) {
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
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
