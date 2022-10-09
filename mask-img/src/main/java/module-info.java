module top.mrys.maskimg {
  requires javafx.controls;
  requires javafx.fxml;

//  requires org.controlsfx.controls;
//  requires org.kordamp.ikonli.javafx;
  requires org.kordamp.bootstrapfx.core;
  requires java.desktop;

  opens top.mrys.maskimg to javafx.fxml;
  exports top.mrys.maskimg;
}