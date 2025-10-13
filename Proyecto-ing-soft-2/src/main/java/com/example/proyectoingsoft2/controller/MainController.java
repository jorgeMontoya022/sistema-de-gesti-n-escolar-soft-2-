package com.example.proyectoingsoft2.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainController {

    @FXML
    private void irACupos(ActionEvent event) {
        cambiarEscena(event, "/com/example/proyectoingsoft2/view/cupos-view.fxml", "Gestión de Cupos");
    }

    @FXML
    private void irAMatriculas(ActionEvent event) {
        cambiarEscena(event, "/com/example/proyectoingsoft2/view/matricula-view.fxml", "Gestión de Matrículas");
    }

    @FXML
    private void irAContratos(ActionEvent event) {
        cambiarEscena(event, "/com/example/proyectoingsoft2/view/contratos-view.fxml", "Gestión de Contratos");
    }

    @FXML
    private void salir() {
        System.exit(0);
    }

    /**
     * Cambia la escena actual sin crear nuevas ventanas
     */
    private void cambiarEscena(ActionEvent actionEvent,String nameFileFxml, String titleWindow) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(nameFileFxml));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle(titleWindow);
            stage.setMaximized(true);

            stage.show();

            closeWindow(actionEvent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeWindow(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }

}