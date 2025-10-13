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

    // ===== INYECCIONES FXML =====
    @FXML
    private TextField txtGrado;

    @FXML
    private TextField txtValor;

    @FXML
    private ComboBox<String> cbEstado;

    @FXML
    private TableView<Cupo> tablaCupos;

    @FXML
    private TableColumn<Cupo, Long> colId;

    @FXML
    private TableColumn<Cupo, String> colGrado;

    @FXML
    private TableColumn<Cupo, Double> colValor;

    @FXML
    private TableColumn<Cupo, String> colEstado;

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

    // ===== VARIABLES INTERNAS =====
    private FactoryGestionCupos factory;
    private ObservableList<Cupo> datosCupos;

    @FXML
    public void initialize() {
        factory = new FactoryGestionCupos();
        datosCupos = FXCollections.observableArrayList();

        // Configurar tabla
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colGrado.setCellValueFactory(new PropertyValueFactory<>("grado"));
        colValor.setCellValueFactory(new PropertyValueFactory<>("valor"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        // Configurar ComboBox
        cbEstado.setItems(FXCollections.observableArrayList("DISPONIBLE", "RESERVADO", "OCUPADO"));
        cbEstado.setValue("DISPONIBLE");

        // Configurar eventos
        btnAgregar.setOnAction(event -> agregarCupo());
        btnLimpiar.setOnAction(event -> limpiarFormulario());
        btnEditar.setOnAction(event -> editarCupoSeleccionado());
        btnEliminar.setOnAction(event -> eliminarCupoSeleccionado());
        btnVolver.setOnAction(event -> volverAlMenu());

        // Cargar datos iniciales
        cargarDatosCupos();
    }

    // ===== CRUD OPERATIONS =====

    private void agregarCupo() {
        try {
            if (txtGrado.getText().isEmpty() || txtValor.getText().isEmpty()) {
                mostrarAlerta("Error", "Por favor complete todos los campos", Alert.AlertType.WARNING);
                return;
            }

            String grado = txtGrado.getText();
            double valor = Double.parseDouble(txtValor.getText());
            String estado = cbEstado.getValue();

            factory.createCupo(estado, valor, grado);
            cargarDatosCupos();
            limpiarFormulario();

            mostrarAlerta("Éxito", "Cupo agregado correctamente", Alert.AlertType.INFORMATION);
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Ingrese un valor numérico válido", Alert.AlertType.ERROR);
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

        Label lbl1 = new Label("Grado:");
        lbl1.setStyle("-fx-font-weight: bold;");
        TextField edtGrado = new TextField(cupo.getGrado());
        edtGrado.setStyle("-fx-padding: 8; -fx-border-color: #3498db; -fx-border-radius: 3;");

        Label lbl2 = new Label("Valor ($):");
        lbl2.setStyle("-fx-font-weight: bold;");
        TextField edtValor = new TextField(String.valueOf(cupo.getValor()));
        edtValor.setStyle("-fx-padding: 8; -fx-border-color: #3498db; -fx-border-radius: 3;");

        Label lbl3 = new Label("Estado:");
        lbl3.setStyle("-fx-font-weight: bold;");
        ComboBox<String> edtEstado = new ComboBox<>();
        edtEstado.setItems(FXCollections.observableArrayList("DISPONIBLE", "RESERVADO", "OCUPADO"));
        edtEstado.setValue(cupo.getEstado());
        edtEstado.setStyle("-fx-padding: 8; -fx-border-color: #3498db; -fx-border-radius: 3;");

        // Botones
        Button btnGuardar = new Button("GUARDAR");
        btnGuardar.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 10 20; -fx-font-weight: bold;");
        btnGuardar.setOnAction(e -> {
            try {
                cupo.setGrado(edtGrado.getText());
                cupo.setValor(Double.parseDouble(edtValor.getText()));
                cupo.setEstado(edtEstado.getValue());
                factory.updateCupo(cupo);
                cargarDatosCupos();
                dialog.close();
                mostrarAlerta("Éxito", "Cupo actualizado correctamente", Alert.AlertType.INFORMATION);
            } catch (NumberFormatException ex) {
                mostrarAlerta("Error", "Ingrese un valor numérico válido", Alert.AlertType.ERROR);
            }
        });

        Button btnCancelar = new Button("CANCELAR");
        btnCancelar.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-padding: 10 20; -fx-font-weight: bold;");
        btnCancelar.setOnAction(e -> dialog.close());

        vbox.getChildren().addAll(
                lblEditar,
                new Separator(),
                lbl1, edtGrado,
                lbl2, edtValor,
                lbl3, edtEstado,
                new Separator(),
                new HBox(10, btnGuardar, btnCancelar)
        );

        Scene scene = new Scene(vbox, 350, 350);
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
        alerta.setContentText("ID: " + seleccionado.getId() + " - Grado: " + seleccionado.getGrado());

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
    }

    private void limpiarFormulario() {
        txtGrado.clear();
        txtValor.clear();
        cbEstado.setValue("DISPONIBLE");
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


            // Cargar la vista del menú principal
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/proyectoingsoft2/view/main-view.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setMaximized(true);

            stage.setTitle("Menú Principal");
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}