package com.example.proyectoingsoft2.controller;

import com.example.proyectoingsoft2.model.personas.Acudiente;
import com.example.proyectoingsoft2.model.personas.FactoryPersonas;
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

public class AcudienteController {

    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtDocumento;
    @FXML
    private TextField txtTelefono;
    @FXML
    private TextField txtCorreo;
    @FXML
    private TextField txtParentesco;
    @FXML
    private TextField txtDireccion;
    @FXML
    private TableView<Acudiente> tablaAcudientes;
    @FXML
    private TableColumn<Acudiente, Long> colId;
    @FXML
    private TableColumn<Acudiente, String> colNombre;
    @FXML
    private TableColumn<Acudiente, String> colDocumento;
    @FXML
    private TableColumn<Acudiente, String> colTelefono;
    @FXML
    private TableColumn<Acudiente, String> colCorreo;
    @FXML
    private Label lblTotal;
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

    private ObservableList<Acudiente> datosAcudientes;

    @FXML
    public void initialize() {
        datosAcudientes = FXCollections.observableArrayList();

        colId.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleLongProperty(cellData.getValue().getId()).asObject());
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDocumento.setCellValueFactory(new PropertyValueFactory<>("documento"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));

        btnAgregar.setOnAction(event -> agregarAcudiente());
        btnLimpiar.setOnAction(event -> limpiarFormulario());
        btnEditar.setOnAction(event -> editarAcudienteSeleccionado());
        btnEliminar.setOnAction(event -> eliminarAcudienteSeleccionado());
        btnVolver.setOnAction(event -> volverAlMenu());

        cargarDatosAcudientes();
    }

    private void agregarAcudiente() {
        try {
            if (txtNombre.getText().isEmpty() || txtDocumento.getText().isEmpty() ||
                    txtTelefono.getText().isEmpty() || txtCorreo.getText().isEmpty()) {
                mostrarAlerta("Error", "Complete todos los campos obligatorios", Alert.AlertType.WARNING);
                return;
            }

            Acudiente acudiente = FactoryPersonas.crearAcudiente(
                    txtNombre.getText(),
                    txtDocumento.getText(),
                    txtTelefono.getText(),
                    txtCorreo.getText(),
                    txtParentesco.getText().isEmpty() ? "Padre/Madre" : txtParentesco.getText(),
                    txtDireccion.getText()
            );

            cargarDatosAcudientes();
            limpiarFormulario();
            mostrarAlerta("Éxito", "Acudiente registrado correctamente", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al registrar acudiente: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void editarAcudienteSeleccionado() {
        Acudiente seleccionado = tablaAcudientes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Advertencia", "Seleccione un acudiente para editar", Alert.AlertType.WARNING);
            return;
        }
        ventanaEdicion(seleccionado);
    }

    private void ventanaEdicion(Acudiente acudiente) {
        Stage dialog = new Stage();
        dialog.setTitle("Editar Acudiente");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));
        vbox.setStyle("-fx-background-color: #f0f0f0;");

        Label lblEditar = new Label("Editar Acudiente #" + acudiente.getId());
        lblEditar.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        TextField edtNombre = new TextField(acudiente.getNombre());
        TextField edtTelefono = new TextField(acudiente.getTelefono());
        TextField edtCorreo = new TextField(acudiente.getCorreo());
        TextField edtParentesco = new TextField(acudiente.getParentesco());
        TextField edtDireccion = new TextField(acudiente.getDireccion() != null ? acudiente.getDireccion() : "");

        Button btnGuardar = new Button("GUARDAR");
        btnGuardar.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 10 20; -fx-font-weight: bold;");
        btnGuardar.setOnAction(e -> {
            acudiente.setNombre(edtNombre.getText());
            acudiente.setTelefono(edtTelefono.getText());
            acudiente.setCorreo(edtCorreo.getText());
            acudiente.setParentesco(edtParentesco.getText());
            acudiente.setDireccion(edtDireccion.getText());
            FactoryPersonas.updateAcudiente(acudiente);
            cargarDatosAcudientes();
            dialog.close();
            mostrarAlerta("Éxito", "Acudiente actualizado correctamente", Alert.AlertType.INFORMATION);
        });

        Button btnCancelar = new Button("CANCELAR");
        btnCancelar.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-padding: 10 20; -fx-font-weight: bold;");
        btnCancelar.setOnAction(e -> dialog.close());

        HBox botones = new HBox(10);
        botones.setAlignment(javafx.geometry.Pos.CENTER);
        botones.getChildren().addAll(btnGuardar, btnCancelar);

        vbox.getChildren().addAll(
                lblEditar, new Separator(),
                new Label("Nombre:"), edtNombre,
                new Label("Teléfono:"), edtTelefono,
                new Label("Correo:"), edtCorreo,
                new Label("Parentesco:"), edtParentesco,
                new Label("Dirección:"), edtDireccion,
                new Separator(), botones
        );

        Scene scene = new Scene(vbox, 400, 450);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void eliminarAcudienteSeleccionado() {
        Acudiente seleccionado = tablaAcudientes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Advertencia", "Seleccione un acudiente para eliminar", Alert.AlertType.WARNING);
            return;
        }

        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmar Eliminación");
        alerta.setHeaderText("¿Está seguro?");
        alerta.setContentText("Se eliminará: " + seleccionado.getNombre());

        if (alerta.showAndWait().get() == ButtonType.OK) {
            FactoryPersonas.deleteAcudiente(seleccionado.getId());
            cargarDatosAcudientes();
            mostrarAlerta("Éxito", "Acudiente eliminado correctamente", Alert.AlertType.INFORMATION);
        }
    }

    private void cargarDatosAcudientes() {
        datosAcudientes.clear();
        datosAcudientes.addAll(FactoryPersonas.listAcudientes());
        tablaAcudientes.setItems(datosAcudientes);
        lblTotal.setText("Total Acudientes: " + datosAcudientes.size());
    }

    private void limpiarFormulario() {
        txtNombre.clear();
        txtDocumento.clear();
        txtTelefono.clear();
        txtCorreo.clear();
        txtParentesco.clear();
        txtDireccion.clear();
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