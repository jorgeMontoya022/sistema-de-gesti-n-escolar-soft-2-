package com.example.proyectoingsoft2.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class ReportesController {
    public void volverAlMenu() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/proyectoingsoft2/view/main-view.fxml"));
        Scene scene = new Scene(loader.load(), 400, 300);
        Stage stage = new Stage();
        stage.setTitle("Menú Principal");
        stage.setScene(scene);
        stage.show();
    }
}
