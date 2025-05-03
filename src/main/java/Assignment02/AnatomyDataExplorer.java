package Assignment02;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;

public class AnatomyDataExplorer extends Application {

    private Button collapseAllButton;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        // Load the tree
        ANode rootNode = null;
        try {
            rootNode = TreeLoader.load("partof_parts_list_e.txt", "partof_element_parts.txt", "partof_inclusion_relation_list.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (rootNode == null) {
            System.err.println("Failed to load anatomy data.");
            return;
        }

        // Build TreeView
        TreeItem<ANode> rootItem = createTreeItemsRec(rootNode);
        TreeView<ANode> treeView = new TreeView<>(rootItem);
        treeView.setShowRoot(true);

        // rendering
        treeView.setCellFactory(param -> new TreeCell<ANode>() {
            @Override
            protected void updateItem(ANode item, boolean empty) {
                super.updateItem(item, empty);
                setText((item == null || empty) ? null : item.name());
            }
        });

        // ListView for file IDs
        ListView<String> listView = new ListView<>();

        // Listener for selection in treeView
        treeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<ANode>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<ANode>> observable, TreeItem<ANode> oldValue, TreeItem<ANode> newValue) {
                listView.getItems().clear();
                if (newValue != null) {
                    ANode selectedNode = newValue.getValue();
                    listView.getItems().addAll(selectedNode.fileIds());
                }
            }
        });

        // Buttons
        Button expandAllButton = new Button("Expand All");
        collapseAllButton = new Button("Collapse All");
        Button byeButton = new Button("Bye");

        expandAllButton.setOnAction(e -> expandAll(treeView));
        collapseAllButton.setOnAction(e -> collapseAll(treeView));
        byeButton.setOnAction(e -> System.exit(0));

        collapseAllButton.setDisable(true); // initially disabled

        // Layout
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox topBar = new HBox(10, expandAllButton, collapseAllButton, spacer);
        HBox bottomBar = new HBox(10, spacer = new Region(), byeButton);
        HBox.setHgrow(spacer, Priority.ALWAYS); // Spacer to push "Bye" to the right

        SplitPane splitPane = new SplitPane(treeView, listView);

        BorderPane rootLayout = new BorderPane();
        rootLayout.setTop(topBar);
        rootLayout.setBottom(bottomBar);
        rootLayout.setCenter(splitPane);

        Scene scene = new Scene(rootLayout, 800, 600);
        stage.setTitle("Anatomy Data Explorer");
        stage.setScene(scene);
        stage.show();

        // Dynamic enable/disable collapse button
        treeView.expandedItemCountProperty().addListener((obs, oldCount, newCount) -> {
            if (treeView.getRoot() != null && newCount.intValue() > 1) {
                collapseAllButton.setDisable(false);
            } else {
                collapseAllButton.setDisable(true);
            }
        });
    }

    // Recursive method
    static TreeItem<ANode> createTreeItemsRec(ANode node) {
        TreeItem<ANode> item = new TreeItem<>(node);
        for (ANode child : node.children()) {
            item.getChildren().add(createTreeItemsRec(child));
        }
        return item;
    }

    private void expandAll(TreeView<ANode> treeView) {
        TreeItem<ANode> root = treeView.getRoot();
        if (root != null) expandAllRec(root);
    }

    private void expandAllRec(TreeItem<ANode> item) {
        item.setExpanded(true);
        for (TreeItem<ANode> child : item.getChildren()) {
            expandAllRec(child);
        }
    }

    private void collapseAll(TreeView<ANode> treeView) {
        TreeItem<ANode> root = treeView.getRoot();
        if (root != null) collapseAllRec(root);
    }

    private void collapseAllRec(TreeItem<ANode> item) {
        item.setExpanded(false);
        for (TreeItem<ANode> child : item.getChildren()) {
            collapseAllRec(child);
        }
    }
}
