package top.mrys.maskimg;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainController implements ControllerInit {

  @FXML
  public ScrollPane logPane;
  @FXML
  public Spinner<Integer> threadNum;
  @FXML
  private Label welcomeText;

  @FXML
  private Label maskImgPath;

  @FXML
  private Label srcImgPath;

  @FXML
  private Button scanButton;

  @FXML
  private TextField imgType;
  @FXML
  private ProgressBar progressBar;

  @FXML
  private ScrollPane scrollPane;

  private String fileType = "jpg;png;jpeg;bmp;gif";

  FileChooser fileChooser = new FileChooser();
  DirectoryChooser directoryChooser = new DirectoryChooser();

  private String srcImgPathStr;

  private Long count = 0L;

  @Override
  public void init() {
    imgType.setText(fileType);
//    logPane.setDisable(true);


    logPane.setFitToWidth(true);
    Pane pane = new VBox();
    logPane.setContent(pane);
    //logPane 追加日志
    Log.setConsumer(msg -> {
      Label label = new Label(msg);
//      Pane content = (Pane) logPane.getContent();
      Platform.runLater(()-> {
        pane.getChildren().add(label);
        logPane.setVvalue(1.0);
      });
    });
    Log.info("日志初始化成功");
//    textArea.prefHeightProperty().bind(scrollPane.heightProperty());
//    scrollPane.setContent(textArea);
//    Log.setConsumer(logPane::appendText);
    //设置线程数
    SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 8, 1);
    threadNum.setValueFactory(valueFactory);
    threadNum.setPromptText("线程数");

  }

  @FXML
  protected void onHelloButtonClick() {
    welcomeText.setText("Welcome to JavaFX Application!");
  }

  @FXML
  public void selectMaskImg(ActionEvent actionEvent) {
    fileChooser.setTitle("选择水印图片");
    fileChooser.setInitialDirectory(null);
    fileChooser.getExtensionFilters().addAll(
      new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
      new FileChooser.ExtensionFilter("All Files", "*.*"));
    File selectedFile = fileChooser.showOpenDialog(null);
    if (selectedFile != null) {
      maskImgPath.setText(selectedFile.getAbsolutePath());
    }
  }

  @FXML
  public void selectSrcImg(ActionEvent actionEvent) {
    directoryChooser.setTitle("选择源图片目录");
    directoryChooser.setInitialDirectory(null);
    File selectedDirectory = directoryChooser.showDialog(null);
    if (selectedDirectory != null) {
      srcImgPathStr = selectedDirectory.getAbsolutePath();
      srcImgPath.setText(srcImgPathStr);
      scanButton.setDisable(false);
    }
  }

  @FXML
  public void scan(ActionEvent actionEvent) throws IOException {
    count = getPathStream().count();
    Log.info("共有" + count + "个文件");
  }

  private Stream<Path> getPathStream() throws IOException {
    return Files.walk(Paths.get(srcImgPathStr))
      .filter(path -> {
        String fileName = path.getFileName().toString();
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        return Arrays.asList(imgType.getText().split(";")).contains(suffix);
      });
  }


  @FXML
  public void imgTypeChange(ActionEvent actionEvent) {
    fileType = imgType.getText();
    Log.info(fileType);
  }

  @FXML
  public void mask(ActionEvent actionEvent) {
    AtomicLong i = new AtomicLong(0);
    new Thread(() -> {
      Log.info("开始遍历文件");
      try {
        getPathStream()
          .collect(Collectors.groupingBy(path -> path.hashCode() % (Integer) threadNum.getValue()))
          .forEach((k, v) -> new Thread(() -> v.forEach(path -> {
              Log.info("开始处理" + path.toString());
              String fileName = path.getFileName().toString();
              String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
              WaterMaskImgUtils.pressImage(path.toString(), maskImgPath.getText(), 0, 0, 0.5f, 0, suffix);
              progressBar.setProgress(i.incrementAndGet() * 1.0 / count);
            })).start());
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }).start();
  }

}