module br.com.ifce.so.projectdeadlockdetection {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires eu.hansolo.tilesfx;

    opens br.com.ifce.so.projectdeadlockdetection to javafx.fxml;
    exports br.com.ifce.so.projectdeadlockdetection;
    exports br.com.ifce.so.projectdeadlockdetection.ui.controller;
    opens br.com.ifce.so.projectdeadlockdetection.ui.controller to javafx.fxml;
}