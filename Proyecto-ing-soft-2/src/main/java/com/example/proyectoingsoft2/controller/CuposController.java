package com.example.proyectoingsoft2.controller;

import com.example.proyectoingsoft2.model.cupos.Cupo;
import com.example.proyectoingsoft2.model.cupos.FactoryGestionCupos;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CuposController {

    @FXML
    private ComboBox<String> cbGrado;
    @FXML
    private ComboBox<String> cbJornada;
    @FXML
    private TextField txtValor;
    @FXML
    private TextField txtCuposDisponibles;
    @FXML
    private TableView<Cupo> tablaCupos;
    @FXML
    private TableColumn<Cupo, Long> colId;
    @FXML
    private TableColumn<Cupo, String> colGrado;
    @FXML
    private TableColumn<Cupo, String> colJornada;
    @FXML
    private TableColumn<Cupo, Double> colValor;
    @FXML
    private TableColumn<Cupo, Integer> colDisponibles;
    @FXML
    private TableColumn<Cupo, Integer> colOcupados;
    @FXML
    private TableColumn<Cupo, String> colEstado;
    @FXML
    private Label lblTotal;
    @FXML
    private Label lblTotalCupos;
    @FXML
    private Button btnAgregar;
    @FXML
    private Button btnLimpiar;
    @FXML
    private Button btnEditar;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnVolver;

    private FactoryGestionCupos factory;
    private ObservableList<Cupo> datosCupos;

    @FXML
    public void initialize() {
        factory = FactoryGestionCupos.getInstance();
        datosCupos = FXCollections.observableArrayList();

        // Configurar columnas
        colId.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleLongProperty(cellData.getValue().getId()).asObject());
        colGrado.setCellValueFactory(new PropertyValueFactory<>("grado"));
        colJornada.setCellValueFactory(new PropertyValueFactory<>("jornada"));
        colValor.setCellValueFactory(new PropertyValueFactory<>("valor"));
        colDisponibles.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getCuposDisponibles()).asObject());
        colOcupados.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getCuposOcupados()).asObject());
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        // Configurar ComboBox
        cbGrado.setItems(FXCollections.observableArrayList(
                "Pre-jardín", "Jardín", "Transición", "Primero", "Segundo",
                "Tercero", "Cuarto", "Quinto"
        ));
        cbGrado.setValue("Primero");

        cbJornada.setItems(FXCollections.observableArrayList("MAÑANA", "TARDE", "COMPLETA"));
        cbJornada.setValue("MAÑANA");

        // Configurar eventos
        btnAgregar.setOnAction(event -> agregarCupo());
        btnLimpiar.setOnAction(event -> limpiarFormulario());
        btnEditar.setOnAction(event -> editarCupoSeleccionado());
        btnEliminar.setOnAction(event -> eliminarCupoSeleccionado());
        btnVolver.setOnAction(event -> volverAlMenu());

        cargarDatosCupos();
    }

    private void agregarCupo() {
        try {
            if (txtValor.getText().isEmpty() || txtCuposDisponibles.getText().isEmpty()) {
                mostrarAlerta("Error", "Complete todos los campos", Alert.AlertType.WARNING);
                return;
            }

            double valor = Double.parseDouble(txtValor.getText());
            int disponibles = Integer.parseInt(txtCuposDisponibles.getText());

            Cupo cupo = factory.createCupo(
                    cbGrado.getValue(),
                    valor,
                    cbJornada.getValue(),
                    disponibles
            );

            cargarDatosCupos();
            limpiarFormulario();
            mostrarAlerta("Éxito", "Cupo agregado correctamente", Alert.AlertType.INFORMATION);
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Ingrese valores numéricos válidos", Alert.AlertType.ERROR);
        }
    }

    private void editarCupoSeleccionado() {
        Cupo seleccionado = tablaCupos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Advertencia", "Seleccione un cupo para editar", Alert.AlertType.WARNING);
            return;
        }
        ventanaEdicion(seleccionado);
    }

    private void ventanaEdicion(Cupo cupo) {
        Stage dialog = new Stage();
        dialog.setTitle("Editar Cupo");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));
        vbox.setStyle("-fx-background-color: #f0f0f0;");

        Label lblEditar = new Label("Editar Cupo #" + cupo.getId());
        lblEditar.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        ComboBox<String> edtGrado = new ComboBox<>();
        edtGrado.setItems(FXCollections.observableArrayList(
                "Pre-jardín", "Jardín", "Transición", "Primero", "Segundo",
                "Tercero", "Cuarto", "Quinto"
        ));
        edtGrado.setValue(cupo.getGrado());

        ComboBox<String> edtJornada = new ComboBox<>();
        edtJornada.setItems(FXCollections.observableArrayList("MAÑANA", "TARDE", "COMPLETA"));
        edtJornada.setValue(cupo.getJornada());

        TextField edtValor = new TextField(String.valueOf(cupo.getValor()));
        TextField edtDisponibles = new TextField(String.valueOf(cupo.getCuposDisponibles()));

        Button btnGuardar = new Button("GUARDAR");
        btnGuardar.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 10 20; -fx-font-weight: bold;");
        btnGuardar.setOnAction(e -> {
            try {
                cupo.setGrado(edtGrado.getValue());
                cupo.setJornada(edtJornada.getValue());
                cupo.setValor(Double.parseDouble(edtValor.getText()));
                cupo.setCuposDisponibles(Integer.parseInt(edtDisponibles.getText()));
                factory.updateCupo(cupo);
                cargarDatosCupos();
                dialog.close();
                mostrarAlerta("Éxito", "Cupo actualizado correctamente", Alert.AlertType.INFORMATION);
            } catch (NumberFormatException ex) {
                mostrarAlerta("Error", "Ingrese valores numéricos válidos", Alert.AlertType.ERROR);
            }
        });

        Button btnCancelar = new Button("CANCELAR");
        btnCancelar.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-padding: 10 20; -fx-font-weight: bold;");
        btnCancelar.setOnAction(e -> dialog.close());

        HBox botones = new HBox(10);
        botones.setAlignment(javafx.geometry.Pos.CENTER);
        botones.getChildren().addAll(btnGuardar, btnCancelar);

        vbox.getChildren().addAll(
                lblEditar, new Separator(),
                new Label("Grado:"), edtGrado,
                new Label("Jornada:"), edtJornada,
                new Label("Valor ($):"), edtValor,
                new Label("Cupos Disponibles:"), edtDisponibles,
                new Separator(), botones
        );

        Scene scene = new Scene(vbox, 400, 380);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void eliminarCupoSeleccionado() {
        Cupo seleccionado = tablaCupos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Advertencia", "Seleccione un cupo para eliminar", Alert.AlertType.WARNING);
            return;
        }

        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmar Eliminación");
        alerta.setHeaderText("¿Está seguro?");
        alerta.setContentText("Se eliminará el cupo de " + seleccionado.getGrado() + " (" + seleccionado.getJornada() + ")");

        if (alerta.showAndWait().get() == ButtonType.OK) {
            factory.deleteCupo(seleccionado.getId());
            cargarDatosCupos();
            mostrarAlerta("Éxito", "Cupo eliminado correctamente", Alert.AlertType.INFORMATION);
        }
    }

    private void cargarDatosCupos() {
        datosCupos.clear();
        datosCupos.addAll(factory.listCupos());
        tablaCupos.setItems(datosCupos);
        lblTotal.setText("Total Cupos: " + datosCupos.size());

        long cuposDisponiblesCount = datosCupos.stream()
                .filter(Cupo::hayDisponibilidad)
                .count();
        lblTotalCupos.setText("Cupos Disponibles: " + cuposDisponiblesCount);
    }

    private void limpiarFormulario() {
        txtValor.clear();
        txtCuposDisponibles.clear();
        cbGrado.setValue("Primero");
        cbJornada.setValue("MAÑANA");
        tablaCupos.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @FXML
    private void volverAlMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/proyectoingsoft2/view/main-view.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Menú Principal");
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();

            Stage currentStage = (Stage) btnVolver.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}