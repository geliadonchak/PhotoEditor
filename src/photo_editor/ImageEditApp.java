package photo_editor;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import photo_editor.gson.JsonItem;
import photo_editor.gson.JsonProject;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import photo_editor.nodes.DraggableNode;
import photo_editor.nodes.LinkNode;
import photo_editor.nodes.NodeTypes;
import photo_editor.nodes.SetImageNode;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;

public class ImageEditApp extends Application {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private final ImageContainer imageContainer = new ImageContainer();
    private final ImageView imageView = new ImageView();
    private final AnchorPane blueprintLayout = new AnchorPane();
    private final BorderPane imgLayout = new BorderPane();

    @Override
    public void start(Stage stage)  {
        SplitPane root = new SplitPane();
        root.setOrientation(Orientation.VERTICAL);
        root.setDividerPosition(0, 0.8);

        imgLayout.setMinSize(0, 0);

        blueprintLayout.setMinSize(0, 0);
        blueprintLayout.setStyle("-fx-background-color: #F5F5F5;");

        VBox imgLayoutVBox = new VBox();
        imgLayoutVBox.setAlignment(Pos.CENTER);

        MenuBar menuBar = new MenuBar();

        Menu addMenu = new Menu("Фильтры");
        MenuItem changeSaturation = new MenuItem("Насыщенность");
        MenuItem setNegative = new MenuItem("Негатив");
        MenuItem setSepia = new MenuItem("Сепия");
        MenuItem setSharpness = new MenuItem("Резкость");
        MenuItem changeBrightness = new MenuItem("Яркость");
        MenuItem setGray = new MenuItem("Оттенки серого");
        MenuItem setBlackWhite = new MenuItem("Нуар");
        MenuItem changeContrast = new MenuItem("Контрастность");

        Menu fileMenu = new Menu("Файл");
        MenuItem newProject = new MenuItem("Новый проект");
        MenuItem loadProject = new MenuItem("Загрузить проект");
        MenuItem saveThis = new MenuItem("Сохранить");
        MenuItem saveAs = new MenuItem("Сохранить как...");
        MenuItem saveProject = new MenuItem("Сохранить проект");

        fileMenu.getItems().addAll(newProject, saveThis, saveAs, saveProject, loadProject);
        addMenu.getItems().addAll(changeSaturation, setNegative, setSepia, setSharpness, changeBrightness, setGray);
        menuBar.getMenus().addAll(fileMenu, addMenu);

        changeSaturation.addEventHandler(ActionEvent.ACTION, actionEvent -> createNode(NodeTypes.ChangeSaturation));
        setSharpness.addEventHandler(ActionEvent.ACTION, actionEvent -> createNode(NodeTypes.SetSharpness));
        changeBrightness.addEventHandler(ActionEvent.ACTION, actionEvent -> createNode(NodeTypes.ChangeBrightness));
        setNegative.addEventHandler(ActionEvent.ACTION, actionEvent -> createNode(NodeTypes.SetNegative));
        setSepia.addEventHandler(ActionEvent.ACTION, actionEvent -> createNode(NodeTypes.SetSepia));
        setGray.addEventHandler(ActionEvent.ACTION, actionEvent -> createNode(NodeTypes.SetGray));
        setBlackWhite.addEventHandler(ActionEvent.ACTION, actionEvent -> createNode(NodeTypes.SetBlackWhite));
        changeContrast.addEventHandler(ActionEvent.ACTION, actionEvent -> createNode(NodeTypes.ChangeContrast));
        saveThis.addEventHandler(ActionEvent.ACTION, actionEvent -> imageSave());
        saveAs.addEventHandler(ActionEvent.ACTION, actionEvent -> imageSaveAs());
        newProject.addEventHandler(ActionEvent.ACTION, actionEvent -> createProject());
        saveProject.addEventHandler(ActionEvent.ACTION, actionEvent -> saveProject());
        loadProject.addEventHandler(ActionEvent.ACTION, actionEvent -> loadProject());

        ScrollPane scroller = new ScrollPane(imageView);
        scroller.setFitToWidth(true);
        imgLayoutVBox.getChildren().addAll(menuBar, scroller);
        root.getItems().addAll(imgLayoutVBox, blueprintLayout);
        imgLayout.setTop(imgLayoutVBox);

        createNode(NodeTypes.SetImage);

        stage.setTitle("Photo Editor");
        stage.setScene(new Scene(root, 1300.0, 800.0));
        stage.show();
    }

    private void imageSave() {
        if (!Imgcodecs.imwrite(StaticResourceManager.currentImagePath, imageContainer.getMainImage())) {
            System.out.println("ERROR: unable to load image");
        }
    }

    private void imageSaveAs() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("newImage");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image", "*.png", "*.jpg", "*.webp"));
        File someFile = fileChooser.showSaveDialog(null);
        String newFilePath = someFile.toURI().toString().substring(6);
        if (!Imgcodecs.imwrite(newFilePath, imageContainer.getMainImage())) {
            System.out.println("ERROR: unable to save image");
        }
    }

    private void createProject() {
        blueprintLayout.getChildren().clear();
        StaticResourceManager.nodesList.clear();
        StaticResourceManager.curvesCount = 0;

        StaticResourceManager.currentNodeType = NodeTypes.SetImage;
        createNode(NodeTypes.SetImage);
        setImage("");
        refreshImage();
    }

    private void saveProject() {
        Gson gson = new Gson();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("project");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("File", "*.json"));
        File someFile = fileChooser.showSaveDialog(null);

        if (someFile == null) {
            return;
        }

        String newFilePath = someFile.toURI().toString().substring(6);

        ArrayList<JsonItem> list = new ArrayList<>();
        DraggableNode temp = StaticResourceManager.nodesList.get(0);
        do {
            list.add(new JsonItem(
                    (float) temp.getLayoutX(),
                    (float) temp.getLayoutY(),
                    temp.getNodeType().toString(),
                    StaticResourceManager.nodesList.indexOf(temp.getNextActionNode()),
                    temp.getOptions()
            ));

            temp = temp.getNextActionNode();
        } while (temp != null);

        JsonProject project = new JsonProject(list, ((SetImageNode) StaticResourceManager.nodesList.get(0)).getImagePath());
        String JSON = gson.toJson(project);

        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(newFilePath));
            bufferedWriter.write(JSON);
            bufferedWriter.close();
        } catch (Exception ignored) {
            System.out.println("ERROR: unable to write file ".concat(newFilePath));
        }
    }

    private void loadProject() {
        Gson gson = new Gson();

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Json file", "*.json"));
        File someFile = fileChooser.showOpenDialog(null);

        if (someFile == null) {
            return;
        }

        String content;
        try {
            content = String.join("\n", Files.readAllLines(Paths.get(someFile.toString())));
        } catch (Exception ignored) {
            System.out.println("ERROR: unable to load project");
            return;
        }

        JsonReader jsonReader = new JsonReader(new StringReader(content));
        JsonProject project = gson.fromJson(jsonReader, JsonProject.class);

        blueprintLayout.getChildren().clear();
        StaticResourceManager.nodesList.clear();

        for (int i = 0; i < project.items.size(); i++) {
            DraggableNode newNode = NodesFabric.createNode(project.items.get(i).nodeType);
            StaticResourceManager.currentNodeType = newNode.getNodeType();

            StaticResourceManager.nodesList.add(newNode);
            blueprintLayout.getChildren().add(newNode);
            newNode.setLayoutY(project.items.get(i).layoutY);
            newNode.setLayoutX(project.items.get(i).layoutX);
            newNode.setOptions(project.items.get(i).filterOptions);
        }

        StaticResourceManager.curvesCount = 0;
        for (int i = 0; i < StaticResourceManager.nodesList.size(); i++) {
            if (project.items.get(i).connectedWith != -1) {
                StaticResourceManager.nodesList.get(i).setNextActionNode(StaticResourceManager.nodesList.get(project.items.get(i).connectedWith));
                StaticResourceManager.nodesList.get(project.items.get(i).connectedWith).setPrevActionNode(StaticResourceManager.nodesList.get(i));

                LinkNode link = new LinkNode();
                link.bindStartEnd(StaticResourceManager.nodesList.get(i), StaticResourceManager.nodesList.get(project.items.get(i).connectedWith));
                StaticResourceManager.curvesCount++;

                Objects.requireNonNull(StaticResourceManager.nodesList.get(0).getSuperParent()).getChildren().add(0, link);
            }
        }

        ((SetImageNode) StaticResourceManager.nodesList.get(0)).setImagePath(project.imagePath);
        refreshImage();
    }

    private void setImage(String path) {
        Mat newImage = Imgcodecs.imread(path);

        if (newImage.empty()) {
            newImage = new Mat(1080, 1920, CvType.CV_8UC3, new Scalar(255, 255, 255));

            if (StaticResourceManager.nodesList.size() != 0) {
                System.out.println("ERROR: unable to load image");
            }
        }

        imageContainer.setMainImage(newImage);
        imgLayout.setMaxHeight(imageContainer.getMainImage().rows());
        imageView.setImage(imageContainer.getWritableImage());
    }

    public void refreshImage() {
        DraggableNode current = StaticResourceManager.nodesList.get(0);

        while (current != null) {
            NodeTypes nodeType = current.getNodeType();

            if (nodeType == NodeTypes.SetImage) {
                String imagePath = ((SetImageNode) current).getImagePath();

                if (!imagePath.isEmpty()) {
                    setImage(imagePath);
                }

                current = current.getNextActionNode();
                continue;
            }

            imageContainer.filter(nodeType, current.getOptions());
            imageView.setImage(imageContainer.getWritableImage());

            current = current.getNextActionNode();
        }
    }

    private void createNode(NodeTypes nodeType) {
        DraggableNode node = NodesFabric.createNode(nodeType);

        node.isChangedProperty().addListener((a, b, c) -> {
            refreshImage();
            node.isChangedProperty().set(false);
        });

        StaticResourceManager.nodesList.add(node);
        blueprintLayout.getChildren().add(node);
        imageView.setImage(imageContainer.getWritableImage());

        refreshImage();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
