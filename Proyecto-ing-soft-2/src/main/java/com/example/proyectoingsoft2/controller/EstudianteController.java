package com.example.proyectoingsoft2.controller;

import com.example.proyectoingsoft2.model.personas.Estudiante;
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

public class EstudianteController {

    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtDocumento;
    @FXML
    private ComboBox<String> cbGrado;
    @FXML
    private ComboBox<Acudiente> cbAcudiente;
    @FXML
    private TableView<Estudiante> tablaEstudiantes;
    @FXML
    private TableColumn<Estudiante, Long> colId;
    @FXML
    private TableColumn<Estudiante, String> colNombre;
    @FXML
    private TableColumn<Estudiante, String> colDocumento;
    @FXML
    private TableColumn<Estudiante, String> colGrado;
    @FXML
    private TableColumn<Estudiante, String> colAcudiente;
    @FXML
    private TableColumn<Estudiante, String> colEstado;
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

    private ObservableList<Estudiante> datosEstudiantes;
    private ObservableList<Acudiente> acudientes;

    @FXML
    public void initialize() {
        datosEstudiantes = FXCollections.observableArrayList();
        acudientes = FXCollections.observableArrayList();

        // Configurar columnas de tabla
        colId.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleLongProperty(cellData.getValue().getId()).asObject());
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDocumento.setCellValueFactory(new PropertyValueFactory<>("documento"));
        colGrado.setCellValueFactory(new PropertyValueFactory<>("grado"));

        colAcudiente.setCellValueFactory(cellData -> {
            Estudiante e = cellData.getValue();
            String acudiente = e.getAcudiente() != null ? e.getAcudiente().getNombre() : "Sin asignar";
            return new javafx.beans.property.SimpleStringProperty(acudiente);
        });

        colEstado.setCellValueFactory(new PropertyValueFactory<>("estadoMatricula"));

        // Configurar ComboBox Grado
        cbGrado.setItems(FXCollections.observableArrayList(
                "Pre-jardín", "Jardín", "Transición", "Primero", "Segundo",
                "Tercero", "Cuarto", "Quinto"
        ));
        cbGrado.setValue("Primero");

        // Cargar acudientes
        cargarAcudientes();

        // Configurar eventos de botones
        btnAgregar.setOnAction(event -> agregarEstudiante());
        btnLimpiar.setOnAction(event -> limpiarFormulario());
        btnEditar.setOnAction(event -> editarEstudianteSeleccionado());
        btnEliminar.setOnAction(event -> eliminarEstudianteSeleccionado());
        btnVolver.setOnAction(event -> volverAlMenu());

        cargarDatosEstudiantes();
    }

    private void cargarAcudientes() {
        acudientes.clear();
        acudientes.addAll(FactoryPersonas.listAcudientes());
        cbAcudiente.setItems(acudientes);
    }

    private void agregarEstudiante() {
        try {
            if (txtNombre.getText().isEmpty() || txtDocumento.getText().isEmpty() || cbAcudiente.getValue() == null) {
                mostrarAlerta("Error", "Complete todos los campos obligatorios", Alert.AlertType.WARNING);
                return;
            }

            Estudiante estudiante = FactoryPersonas.crearEstudiante(
                    txtNombre.getText(),
                    txtDocumento.getText(),
                    cbGrado.getValue(),
                    cbAcudiente.getValue()
            );

            cargarDatosEstudiantes();
            limpiarFormulario();
            mostrarAlerta("Éxito", "Estudiante registrado correctamente", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al registrar estudiante: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void editarEstudianteSeleccionado() {
        Estudiante seleccionado = tablaEstudiantes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Advertencia", "Seleccione un estudiante para editar", Alert.AlertType.WARNING);
            return;
        }
        ventanaEdicion(seleccionado);
    }

    private void ventanaEdicion(Estudiante estudiante) {
        Stage dialog = new Stage();
        dialog.setTitle("Editar Estudiante");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));
        vbox.setStyle("-fx-background-color: #f0f0f0;");

        Label lblEditar = new Label("Editar Estudiante #" + estudiante.getId());
        lblEditar.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        TextField edtNombre = new TextField(estudiante.getNombre());
        TextField edtDocumento = new TextField(estudiante.getDocumento());

        ComboBox<String> edtGrado = new ComboBox<>();
        edtGrado.setItems(FXCollections.observableArrayList(
                "Pre-jardín", "Jardín", "Transición", "Primero", "Segundo",
                "Tercero", "Cuarto", "Quinto"
        ));
        edtGrado.setValue(estudiante.getGrado());

        ComboBox<Acudiente> edtAcudiente = new ComboBox<>();
        edtAcudiente.setItems(acudientes);
        edtAcudiente.setValue(estudiante.getAcudiente());

        Button btnGuardar = new Button("GUARDAR");
        btnGuardar.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 10 20; -fx-font-weight: bold;");
        btnGuardar.setOnAction(e -> {
            estudiante.setNombre(edtNombre.getText());
            estudiante.setDocumento(edtDocumento.getText());
            estudiante.setGrado(edtGrado.getValue());
            estudiante.setAcudiente(edtAcudiente.getValue());
            FactoryPersonas.updateEstudiante(estudiante);
            cargarDatosEstudiantes();
            dialog.close();
            mostrarAlerta("Éxito", "Estudiante actualizado correctamente", Alert.AlertType.INFORMATION);
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
                new Label("Documento:"), edtDocumento,
                new Label("Grado:"), edtGrado,
                new Label("Acudiente:"), edtAcudiente,
                new Separator(), botones
        );

        Scene scene = new Scene(vbox, 400, 400);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void eliminarEstudianteSeleccionado() {
        Estudiante seleccionado = tablaEstudiantes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Advertencia", "Seleccione un estudiante para eliminar", Alert.AlertType.WARNING);
            return;
        }

        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmar Eliminación");
        alerta.setHeaderText("¿Está seguro?");
        alerta.setContentText("Se eliminará: " + seleccionado.getNombre());

        if (alerta.showAndWait().get() == ButtonType.OK) {
            FactoryPersonas.deleteEstudiante(seleccionado.getId());
            cargarDatosEstudiantes();
            mostrarAlerta("Éxito", "Estudiante eliminado correctamente", Alert.AlertType.INFORMATION);
        }
    }

    private void cargarDatosEstudiantes() {
        datosEstudiantes.clear();
        datosEstudiantes.addAll(FactoryPersonas.listEstudiantes());
        tablaEstudiantes.setItems(datosEstudiantes);
        lblTotal.setText("Total Estudiantes: " + datosEstudiantes.size());
    }

    private void limpiarFormulario() {
        txtNombre.clear();
        txtDocumento.clear();
        cbGrado.setValue("Primero");
        cbAcudiente.setValue(null);
        tablaEstudiantes.getSelectionModel().clearSelection();
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