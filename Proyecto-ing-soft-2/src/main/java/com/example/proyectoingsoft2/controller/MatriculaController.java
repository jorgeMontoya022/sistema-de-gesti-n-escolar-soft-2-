package com.example.proyectoingsoft2.controller;

import com.example.proyectoingsoft2.model.contratos.*;
import com.example.proyectoingsoft2.model.personas.*;
import com.example.proyectoingsoft2.model.reservas.*; // NUEVO
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

    @FXML
    private ComboBox<Estudiante> cbEstudiante;
    @FXML
    private TextField txtValor;
    @FXML
    private DatePicker dpFecha;
    @FXML
    private Label lblDisponibilidadCupo; // NUEVO: Para mostrar disponibilidad
    @FXML
    private TableView<Matricula> tablaMatriculas;
    @FXML
    private TableColumn<Matricula, Long> colId;
    @FXML
    private TableColumn<Matricula, String> colEstudiante;
    @FXML
    private TableColumn<Matricula, String> colAcudiente;
    @FXML
    private TableColumn<Matricula, String> colCupo; // NUEVA COLUMNA
    @FXML
    private TableColumn<Matricula, String> colEstado;
    @FXML
    private TableColumn<Matricula, Double> colValor;
    @FXML
    private TableColumn<Matricula, String> colFecha;
    @FXML
    private Label lblTotal;
    @FXML
    private Label lblTotalValor;
    @FXML
    private Button btnAgregar;
    @FXML
    private Button btnLimpiar;
    @FXML
    private Button btnAprobar;
    @FXML
    private Button btnRechazar;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnVolver;

    private FactoryGestionContratos factoryContratos;
    private ObservableList<Matricula> datosMatriculas;
    private ObservableList<Estudiante> estudiantes;
    private Directora directoraActual;

    @FXML
    public void initialize() {
        factoryContratos = FactoryGestionContratos.getInstance();
        datosMatriculas = FXCollections.observableArrayList();
        estudiantes = FXCollections.observableArrayList();

        // Simular directora actual
        directoraActual = new Directora(9999, "Dra. María García", "directora", "1234");

        // Configurar columnas
        colId.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleLongProperty(cellData.getValue().getIdMatricula()).asObject());

        colEstudiante.setCellValueFactory(cellData -> {
            Matricula m = cellData.getValue();
            String nombre = m.getEstudiante() != null ? m.getEstudiante().getNombre() : "Sin asignar";
            return new javafx.beans.property.SimpleStringProperty(nombre);
        });

        colAcudiente.setCellValueFactory(cellData -> {
            Matricula m = cellData.getValue();
            String acudiente = m.getEstudiante() != null && m.getEstudiante().getAcudiente() != null ?
                    m.getEstudiante().getAcudiente().getNombre() : "Sin asignar";
            return new javafx.beans.property.SimpleStringProperty(acudiente);
        });

       /*
        * // NUEVA COLUMNA: Cupo Asignado
        colCupo.setCellValueFactory(cellData -> {
            Matricula m = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(m.getCupoInfo());
        });
        *
        *
        * */


        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colValor.setCellValueFactory(new PropertyValueFactory<>("valorMatricula"));

        colFecha.setCellValueFactory(cellData -> {
            Matricula m = cellData.getValue();
            String fecha = m.getFechaRegistro() != null ? m.getFechaRegistro().toString() : "";
            return new javafx.beans.property.SimpleStringProperty(fecha);
        });

        dpFecha.setValue(LocalDate.now());

        // Cargar estudiantes
        cargarEstudiantes();

        // NUEVO: Listener para mostrar disponibilidad de cupo
        cbEstudiante.setOnAction(event -> actualizarDisponibilidadCupo());

        // Configurar eventos
        btnAgregar.setOnAction(event -> crearMatricula());
        btnLimpiar.setOnAction(event -> limpiarFormulario());
        btnAprobar.setOnAction(event -> aprobarMatriculaSeleccionada());
        btnRechazar.setOnAction(event -> rechazarMatriculaSeleccionada());
        btnEliminar.setOnAction(event -> eliminarMatriculaSeleccionada());
        btnVolver.setOnAction(event -> volverAlMenu());

        cargarDatosMatriculas();
    }

    private void cargarEstudiantes() {
        estudiantes.clear();
        estudiantes.addAll(FactoryPersonas.listEstudiantes());
        cbEstudiante.setItems(estudiantes);
    }

    // NUEVO: Método para verificar y mostrar disponibilidad de cupo
    private void actualizarDisponibilidadCupo() {
        if (cbEstudiante.getValue() == null) {
            lblDisponibilidadCupo.setText("");
            lblDisponibilidadCupo.setStyle("-fx-text-fill: black;");
            return;
        }

        Estudiante est = cbEstudiante.getValue();
        String grado = est.getGrado();

        String info = factoryContratos.obtenerInfoDisponibilidad(grado);
        lblDisponibilidadCupo.setText(info);

        if (factoryContratos.verificarDisponibilidadCupo(grado)) {
            lblDisponibilidadCupo.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
        } else {
            lblDisponibilidadCupo.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
        }
    }

    private void crearMatricula() {
        try {
            if (cbEstudiante.getValue() == null || txtValor.getText().isEmpty()) {
                mostrarAlerta("Error", "Complete todos los campos obligatorios", Alert.AlertType.WARNING);
                return;
            }

            Estudiante estudiante = cbEstudiante.getValue();
            double valor = Double.parseDouble(txtValor.getText());

            // Verificar disponibilidad antes de crear
            String grado = estudiante.getGrado();


            if (!factoryContratos.verificarDisponibilidadCupo(grado)) {
                mostrarAlerta("Sin Cupos",
                        "No hay cupos disponibles para el grado" + grado +".\n\n" +
                                "Por favor, contacte a la administración o seleccione otro grado/jornada.",
                        Alert.AlertType.ERROR);
                return;
            }

            // Crear contrato con condiciones por defecto
            String condiciones = "Contrato estándar de matrícula escolar para el año lectivo 2024-2025. " +
                    "El estudiante se compromete a cumplir con el manual de convivencia institucional.";

            Contrato contrato = factoryContratos.createContrato(
                    estudiante.getAcudiente(),
                    condiciones
            );

            // Simular firmas completadas
            factoryContratos.firmarContrato(contrato.getIdContrato(), "ACUDIENTE");
            factoryContratos.firmarContrato(contrato.getIdContrato(), "COORDINADOR");
            factoryContratos.firmarContrato(contrato.getIdContrato(), "DIRECTORA");

            // Crear matrícula
            Matricula matricula = factoryContratos.createMatricula(estudiante, contrato, valor);

            cargarDatosMatriculas();
            limpiarFormulario();
            mostrarAlerta("Éxito",
                    "Matrícula creada correctamente.\n" +
                            "Estado: PENDIENTE\n" +
                            "El cupo será asignado cuando la directora la apruebe.",
                    Alert.AlertType.INFORMATION);
        } catch (IllegalArgumentException e) {
            mostrarAlerta("Error de Validación", e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al crear matrícula: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void aprobarMatriculaSeleccionada() {
        Matricula seleccionada = tablaMatriculas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Advertencia", "Seleccione una matrícula para aprobar", Alert.AlertType.WARNING);
            return;
        }

        if (!"PENDIENTE".equals(seleccionada.getEstado())) {
            mostrarAlerta("Advertencia", "Solo se pueden aprobar matrículas PENDIENTES", Alert.AlertType.WARNING);
            return;
        }

        // Verificar nuevamente disponibilidad (por si cambió)
        Estudiante est = seleccionada.getEstudiante();
        String grado = est.getGrado();


        if (!factoryContratos.verificarDisponibilidadCupo(grado)) {
            mostrarAlerta("Sin Cupos",
                    "Ya no hay cupos disponibles para " + grado + ".\n\n" +
                            "No se puede aprobar la matrícula.",
                    Alert.AlertType.ERROR);
            return;
        }

        // Confirmar aprobación
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Aprobación");
        confirmacion.setHeaderText("¿Aprobar matrícula?");
        confirmacion.setContentText(
                "Estudiante: " + est.getNombre() + "\n" +
                        "Grado: " + grado + "\n" +
                        "Se asignará automáticamente un cupo disponible."
        );

        if (confirmacion.showAndWait().get() != ButtonType.OK) {
            return;
        }

        try {
            factoryContratos.aprobarMatricula(seleccionada.getIdMatricula(), directoraActual);
            cargarDatosMatriculas();

            // Obtener la matrícula actualizada para mostrar el cupo asignado
            Matricula actualizada = factoryContratos.findMatriculaById(seleccionada.getIdMatricula()).orElse(null);
            String cupoInfo = actualizada != null ? actualizada.getCupoInfo() : "desconocido";

            mostrarAlerta("Éxito",
                    "Matrícula aprobada correctamente.\n\n" +
                            "Cupo asignado: " + cupoInfo + "\n" +
                            "El estudiante puede proceder con el pago.",
                    Alert.AlertType.INFORMATION);
        } catch (IllegalStateException e) {
            mostrarAlerta("Error", e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al aprobar matrícula: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void rechazarMatriculaSeleccionada() {
        Matricula seleccionada = tablaMatriculas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Advertencia", "Seleccione una matrícula para rechazar", Alert.AlertType.WARNING);
            return;
        }

        if (!"PENDIENTE".equals(seleccionada.getEstado())) {
            mostrarAlerta("Advertencia", "Solo se pueden rechazar matrículas PENDIENTES", Alert.AlertType.WARNING);
            return;
        }

        // Ventana de diálogo para motivo del rechazo
        Stage dialog = new Stage();
        dialog.setTitle("Rechazar Matrícula");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));
        vbox.setStyle("-fx-background-color: #f0f0f0;");

        Label lblMotivo = new Label("Motivo del rechazo:");
        lblMotivo.setStyle("-fx-font-weight: bold;");
        TextArea txtMotivo = new TextArea();
        txtMotivo.setWrapText(true);
        txtMotivo.setPrefRowCount(5);

        Button btnConfirmar = new Button("CONFIRMAR RECHAZO");
        btnConfirmar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 10 20; -fx-font-weight: bold;");
        btnConfirmar.setOnAction(e -> {
            try {
                if (txtMotivo.getText().isEmpty()) {
                    mostrarAlerta("Error", "Debe ingresar un motivo de rechazo", Alert.AlertType.WARNING);
                    return;
                }
                factoryContratos.rechazarMatricula(seleccionada.getIdMatricula(), txtMotivo.getText());
                cargarDatosMatriculas();
                dialog.close();
                mostrarAlerta("Éxito", "Matrícula rechazada correctamente.\nNo se asignó ningún cupo.",
                        Alert.AlertType.INFORMATION);
            } catch (Exception ex) {
                mostrarAlerta("Error", "Error al rechazar: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        Button btnCancelar = new Button("CANCELAR");
        btnCancelar.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-padding: 10 20; -fx-font-weight: bold;");
        btnCancelar.setOnAction(e -> dialog.close());

        HBox botones = new HBox(10);
        botones.setAlignment(javafx.geometry.Pos.CENTER);
        botones.getChildren().addAll(btnConfirmar, btnCancelar);

        vbox.getChildren().addAll(lblMotivo, txtMotivo, new Separator(), botones);

        Scene scene = new Scene(vbox, 400, 300);
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

        String mensaje = "Se eliminará la matrícula de: " +
                (seleccionada.getEstudiante() != null ? seleccionada.getEstudiante().getNombre() : "Sin asignar");

        if (seleccionada.getCupoAsignado() != null) {
            mensaje += "\n\nSe LIBERARÁ el cupo: " + seleccionada.getCupoInfo();
        }

        alerta.setContentText(mensaje);

        if (alerta.showAndWait().get() == ButtonType.OK) {
            factoryContratos.deleteMatricula(seleccionada.getIdMatricula());
            cargarDatosMatriculas();
            mostrarAlerta("Éxito", "Matrícula eliminada correctamente.\nEl cupo ha sido liberado.",
                    Alert.AlertType.INFORMATION);
        }
    }

    private void cargarDatosMatriculas() {
        datosMatriculas.clear();
        datosMatriculas.addAll(factoryContratos.listMatriculas());
        tablaMatriculas.setItems(datosMatriculas);
        lblTotal.setText("Total Matrículas: " + datosMatriculas.size());

        double total = datosMatriculas.stream()
                .mapToDouble(Matricula::getValorMatricula)
                .sum();
        lblTotalValor.setText("Valor Total: $" + String.format("%.2f", total));
    }

    private void limpiarFormulario() {
        cbEstudiante.setValue(null);
        txtValor.clear();
        dpFecha.setValue(LocalDate.now());
        lblDisponibilidadCupo.setText("");
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