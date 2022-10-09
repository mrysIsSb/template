package top.mrys.maskimg;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HelloApplication extends Application {


  @Override
  public void start(Stage stage) throws IOException {
    Log.setConsumer(System.out::print);
    Log.setConsumer(msg-> {
      try {
        Path path = Paths.get("./log.txt");
        if (Files.exists(path)) {
          Files.write(path, msg.getBytes(), StandardOpenOption.APPEND);
        } else {
          Files.createFile(path);
          Files.write(path, msg.getBytes());
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });
    FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("main-view.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 800, 600);
    scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
    stage.setTitle("图片加水印");
    stage.setScene(scene);
    MainController main = fxmlLoader.getController();
    ((ControllerInit) main).init();
    stage.show();
    Log.info("启动成功---------"+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))+"---------");
  }

  public static void main(String[] args) {
    launch();
  }
}