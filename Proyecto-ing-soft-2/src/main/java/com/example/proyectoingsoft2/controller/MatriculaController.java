package com.example.proyectoingsoft2.controller;

import com.example.proyectoingsoft2.model.contratos.Matricula;
import com.example.proyectoingsoft2.model.contratos.FactoryGestionContratos;
import com.example.proyectoingsoft2.model.personas.Estudiante;
import com.example.proyectoingsoft2.model.personas.Directora;
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
import java.time.LocalDate;

public class MatriculaController {

    // ===== INYECCIONES FXML =====
    @FXML
    private ComboBox<Estudiante> cbEstudiante;

    @FXML
    private ComboBox<String> cbEstado;

    @FXML
    private TextField txtValor;

    @FXML
    private DatePicker dpFecha;

    @FXML
    private TableView<Matricula> tablaMatriculas;

    @FXML
    private TableColumn<Matricula, Long> colId;

    @FXML
    private TableColumn<Matricula, String> colEstudiante;

    @FXML
    private TableColumn<Matricula, String> colEstado;

    @FXML
    private TableColumn<Matricula, Double> colValor;

    @FXML
    private TableColumn<Matricula, LocalDate> colFecha;

    @FXML
    private Label lblTotal;

    @FXML
    private Label lblTotalValor;

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
    private FactoryGestionContratos factory;
    private ObservableList<Matricula> datosMatriculas;
    private ObservableList<Estudiante> estudiantes;

    // Datos de prueba (Reemplazar con datos reales)
    private Directora directoraPrueba = new Directora(1, "Directora", "admin", "1234");

    @FXML
    public void initialize() {
        factory = new FactoryGestionContratos();
        datosMatriculas = FXCollections.observableArrayList();
        estudiantes = FXCollections.observableArrayList();

        // Configurar tabla
        colId.setCellValueFactory(cellData -> {
            Matricula m = cellData.getValue();
            return new javafx.beans.property.SimpleLongProperty(m.getIdMatricula()).asObject();
        });

        colEstudiante.setCellValueFactory(cellData -> {
            Matricula m = cellData.getValue();
            String nombre = m.getEstudiante() != null ? m.getEstudiante().getNombre() : "Sin asignar";
            return new javafx.beans.property.SimpleStringProperty(nombre);
        });

        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        colValor.setCellValueFactory(new PropertyValueFactory<>("valorMatricula"));

        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaRegistro"));

        // Configurar ComboBox Estado
        cbEstado.setItems(FXCollections.observableArrayList("Pendiente", "Confirmada", "Cancelada"));
        cbEstado.setValue("Pendiente");

        // Configurar DatePicker
        dpFecha.setValue(LocalDate.now());

        // Cargar estudiantes de prueba
        cargarEstudiantesPrueba();

        // Configurar eventos
        btnAgregar.setOnAction(event -> agregarMatricula());
        btnLimpiar.setOnAction(event -> limpiarFormulario());
        btnEditar.setOnAction(event -> editarMatriculaSeleccionada());
        btnEliminar.setOnAction(event -> eliminarMatriculaSeleccionada());
        btnVolver.setOnAction(event -> volverAlMenu());

        // Cargar datos iniciales
        cargarDatosMatriculas();
    }

    // ===== CRUD OPERATIONS =====

    private void agregarMatricula() {
        try {
            if (cbEstudiante.getValue() == null || txtValor.getText().isEmpty()) {
                mostrarAlerta("Error", "Por favor complete todos los campos", Alert.AlertType.WARNING);
                return;
            }

            Estudiante estudiante = cbEstudiante.getValue();
            String estado = cbEstado.getValue();
            double valor = Double.parseDouble(txtValor.getText());
            LocalDate fecha = dpFecha.getValue();

            factory.createMatricula(fecha, estado, valor, estudiante, null, directoraPrueba);
            cargarDatosMatriculas();
            limpiarFormulario();

            mostrarAlerta("Éxito", "Matrícula registrada correctamente", Alert.AlertType.INFORMATION);
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Ingrese un valor numérico válido", Alert.AlertType.ERROR);
        }
    }

    private void editarMatriculaSeleccionada() {
        Matricula seleccionada = tablaMatriculas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Advertencia", "Seleccione una matrícula para editar", Alert.AlertType.WARNING);
            return;
        }

        ventanaEdicion(seleccionada);
    }

    private void ventanaEdicion(Matricula matricula) {
        Stage dialog = new Stage();
        dialog.setTitle("Editar Matrícula");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));
        vbox.setStyle("-fx-background-color: #f0f0f0;");

        Label lblEditar = new Label("Editar Matrícula #" + matricula.getIdMatricula());
        lblEditar.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label lbl1 = new Label("Estado:");
        lbl1.setStyle("-fx-font-weight: bold;");
        ComboBox<String> edtEstado = new ComboBox<>();
        edtEstado.setItems(FXCollections.observableArrayList("Pendiente", "Confirmada", "Cancelada"));
        edtEstado.setValue(matricula.getEstado());
        edtEstado.setStyle("-fx-padding: 8; -fx-border-color: #27ae60; -fx-border-radius: 3;");

        Label lbl2 = new Label("Valor ($):");
        lbl2.setStyle("-fx-font-weight: bold;");
        TextField edtValor = new TextField(String.valueOf(matricula.getValorMatricula()));
        edtValor.setStyle("-fx-padding: 8; -fx-border-color: #27ae60; -fx-border-radius: 3;");

        Label lbl3 = new Label("Fecha:");
        lbl3.setStyle("-fx-font-weight: bold;");
        DatePicker edtFecha = new DatePicker(matricula.getFechaRegistro());
        edtFecha.setStyle("-fx-padding: 8; -fx-border-color: #27ae60; -fx-border-radius: 3;");

        HBox botones = new HBox(10);
        botones.setAlignment(javafx.geometry.Pos.CENTER);

        Button btnGuardar = new Button("GUARDAR");
        btnGuardar.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 10 20; -fx-font-weight: bold;");
        btnGuardar.setOnAction(e -> {
            try {
                matricula.setEstado(edtEstado.getValue());
                matricula.setValorMatricula(Double.parseDouble(edtValor.getText()));
                matricula.setFechaRegistro(edtFecha.getValue());
                factory.updateMatricula(matricula);
                cargarDatosMatriculas();
                dialog.close();
                mostrarAlerta("Éxito", "Matrícula actualizada correctamente", Alert.AlertType.INFORMATION);
            } catch (NumberFormatException ex) {
                mostrarAlerta("Error", "Ingrese un valor numérico válido", Alert.AlertType.ERROR);
            }
        });

        Button btnCancelar = new Button("CANCELAR");
        btnCancelar.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-padding: 10 20; -fx-font-weight: bold;");
        btnCancelar.setOnAction(e -> dialog.close());

        botones.getChildren().addAll(btnGuardar, btnCancelar);

        vbox.getChildren().addAll(
                lblEditar,
                new Separator(),
                lbl1, edtEstado,
                lbl2, edtValor,
                lbl3, edtFecha,
                new Separator(),
                botones
        );

        Scene scene = new Scene(vbox, 350, 350);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void eliminarMatriculaSeleccionada() {
        Matricula seleccionada = tablaMatriculas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Advertencia", "Seleccione una matrícula para eliminar", Alert.AlertType.WARNING);
            return;
        }

        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmar Eliminación");
        alerta.setHeaderText("¿Está seguro?");
        alerta.setContentText("ID: " + seleccionada.getIdMatricula() + " - Estudiante: " +
                (seleccionada.getEstudiante() != null ? seleccionada.getEstudiante().getNombre() : "Sin asignar"));

        if (alerta.showAndWait().get() == ButtonType.OK) {
            factory.deleteMatricula(seleccionada.getIdMatricula());
            cargarDatosMatriculas();
            mostrarAlerta("Éxito", "Matrícula eliminada correctamente", Alert.AlertType.INFORMATION);
        }
    }

    private void cargarDatosMatriculas() {
        datosMatriculas.clear();
        datosMatriculas.addAll(factory.listMatriculas());
        tablaMatriculas.setItems(datosMatriculas);
        lblTotal.setText("Total Matrículas: " + datosMatriculas.size());

        // Calcular total de valores
        double total = datosMatriculas.stream().mapToDouble(Matricula::getValorMatricula).sum();
        lblTotalValor.setText("Valor Total: $" + String.format("%.2f", total));
    }

    private void cargarEstudiantesPrueba() {
        // Crear estudiantes de prueba
        Estudiante e1 = new Estudiante(1, "Juan García López", "1234567", "Primero");
        Estudiante e2 = new Estudiante(2, "María Rodríguez Pérez", "1234568", "Segundo");
        Estudiante e3 = new Estudiante(3, "Carlos Martínez Silva", "1234569", "Tercero");
        Estudiante e4 = new Estudiante(4, "Ana Sánchez Torres", "1234570", "Cuarto");

        estudiantes.addAll(e1, e2, e3, e4);
        cbEstudiante.setItems(estudiantes);
    }

    private void limpiarFormulario() {
        cbEstudiante.setValue(null);
        cbEstado.setValue("Pendiente");
        txtValor.clear();
        dpFecha.setValue(LocalDate.now());
        tablaMatriculas.getSelectionModel().clearSelection();
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