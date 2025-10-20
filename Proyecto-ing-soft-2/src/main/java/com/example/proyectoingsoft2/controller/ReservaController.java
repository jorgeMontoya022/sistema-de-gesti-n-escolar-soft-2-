package com.example.proyectoingsoft2.controller;

import com.example.proyectoingsoft2.model.reservas.*;
import com.example.proyectoingsoft2.model.personas.*;
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
import javafx.print.*;

import java.util.Map;
import java.util.Optional;

public class ReservaController {

    @FXML
    private ComboBox<Estudiante> cbEstudiante;
    @FXML
    private ComboBox<String> cbGrado;
    @FXML
    private ComboBox<String> cbJornada;
    @FXML
    private Label lblDisponibilidadCupo;
    @FXML
    private TextArea txtObservaciones;
    @FXML
    private TableView<Reserva> tablaReservas;
    @FXML
    private TableColumn<Reserva, Long> colId;
    @FXML
    private TableColumn<Reserva, String> colNumero;
    @FXML
    private TableColumn<Reserva, String> colEstudiante;
    @FXML
    private TableColumn<Reserva, String> colCupo;
    @FXML
    private TableColumn<Reserva, String> colEstado;
    @FXML
    private TableColumn<Reserva, String> colFechaReserva;
    @FXML
    private TableColumn<Reserva, Long> colDiasRestantes;
    @FXML
    private Label lblTotal;
    @FXML
    private Label lblActivas;
    @FXML
    private Button btnReservar;
    @FXML
    private Button btnLimpiar;
    @FXML
    private Button btnConfirmar;
    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnImprimir;
    @FXML
    private Button btnVolver;

    private FactoryGestionReservas factoryReservas;
    private ObservableList<Reserva> datosReservas;
    private ObservableList<Estudiante> estudiantes;

    @FXML
    public void initialize() {
        factoryReservas = FactoryGestionReservas.getInstance();
        datosReservas = FXCollections.observableArrayList();
        estudiantes = FXCollections.observableArrayList();

        // Configurar columnas
        colId.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleLongProperty(cellData.getValue().getIdReserva()).asObject());

        colNumero.setCellValueFactory(new PropertyValueFactory<>("numeroReserva"));

        colEstudiante.setCellValueFactory(cellData -> {
            Reserva r = cellData.getValue();
            String nombre = r.getEstudiante() != null ? r.getEstudiante().getNombre() : "Sin asignar";
            return new javafx.beans.property.SimpleStringProperty(nombre);
        });

        colCupo.setCellValueFactory(cellData -> {
            Reserva r = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(r.getCupoInfo());
        });

        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        colFechaReserva.setCellValueFactory(cellData -> {
            Reserva r = cellData.getValue();
            String fecha = r.getFechaReserva() != null ?
                    r.getFechaReserva().toLocalDate().toString() : "";
            return new javafx.beans.property.SimpleStringProperty(fecha);
        });

        colDiasRestantes.setCellValueFactory(cellData -> {
            Reserva r = cellData.getValue();
            return new javafx.beans.property.SimpleLongProperty(r.getDiasRestantes()).asObject();
        });

        // Colorear fila según días restantes
        tablaReservas.setRowFactory(tv -> new TableRow<Reserva>() {
            @Override
            protected void updateItem(Reserva reserva, boolean empty) {
                super.updateItem(reserva, empty);
                if (empty || reserva == null) {
                    setStyle("");
                } else {
                    if (reserva.haExpirado() || "EXPIRADA".equals(reserva.getEstado())) {
                        setStyle("-fx-background-color: #ffcccc;");
                    } else if (reserva.getDiasRestantes() <= 3 && reserva.estaVigente()) {
                        setStyle("-fx-background-color: #fff3cd;");
                    } else if ("CONFIRMADA".equals(reserva.getEstado())) {
                        setStyle("-fx-background-color: #d4edda;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });

        // Configurar ComboBox
        cbGrado.setItems(FXCollections.observableArrayList(
                "Pre-jardín", "Jardín", "Transición", "Primero", "Segundo",
                "Tercero", "Cuarto", "Quinto"
        ));
        cbGrado.setValue("Primero");

        cbJornada.setItems(FXCollections.observableArrayList("MAÑANA", "TARDE", "COMPLETA"));
        cbJornada.setValue("MAÑANA");

        // Cargar estudiantes
        cargarEstudiantes();

        // Listeners para actualizar disponibilidad
        cbEstudiante.setOnAction(event -> verificarReservaExistente());
        cbGrado.setOnAction(event -> actualizarDisponibilidadCupo());
        cbJornada.setOnAction(event -> actualizarDisponibilidadCupo());

        // Configurar eventos
        btnReservar.setOnAction(event -> crearReserva());
        btnLimpiar.setOnAction(event -> limpiarFormulario());
        btnConfirmar.setOnAction(event -> confirmarReservaSeleccionada());
        btnCancelar.setOnAction(event -> cancelarReservaSeleccionada());
        btnImprimir.setOnAction(event -> imprimirComprobanteReserva());
        btnVolver.setOnAction(event -> volverAlMenu());

        cargarDatosReservas();

        // Expirar reservas vencidas al iniciar
        factoryReservas.expirarReservasVencidas();
    }

    private void cargarEstudiantes() {
        estudiantes.clear();
        estudiantes.addAll(FactoryPersonas.listEstudiantes());
        cbEstudiante.setItems(estudiantes);
    }

    private void verificarReservaExistente() {
        if (cbEstudiante.getValue() == null) {
            lblDisponibilidadCupo.setText("");
            return;
        }

        Estudiante est = cbEstudiante.getValue();

        if (factoryReservas.tieneReservaActiva(est.getId())) {
            Optional<Reserva> reservaOpt = factoryReservas.obtenerReservaActiva(est.getId());
            if (reservaOpt.isPresent()) {
                Reserva reserva = reservaOpt.get();
                lblDisponibilidadCupo.setText(
                        "⚠️ RESERVA ACTIVA: " + reserva.getNumeroReserva() +
                                " | Expira en " + reserva.getDiasRestantes() + " días"
                );
                lblDisponibilidadCupo.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                btnReservar.setDisable(true);
                return;
            }
        }

        btnReservar.setDisable(false);
        actualizarDisponibilidadCupo();
    }

    private void actualizarDisponibilidadCupo() {
        if (cbEstudiante.getValue() == null) {
            lblDisponibilidadCupo.setText("");
            return;
        }

        String grado = cbGrado.getValue();
        String jornada = cbJornada.getValue();

        String info = factoryReservas.obtenerInfoDisponibilidad(grado, jornada);
        lblDisponibilidadCupo.setText(info);

        if (factoryReservas.verificarDisponibilidadCupo(grado, jornada)) {
            lblDisponibilidadCupo.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
        } else {
            lblDisponibilidadCupo.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
        }
    }

    private void crearReserva() {
        try {
            if (cbEstudiante.getValue() == null) {
                mostrarAlerta("Error", "Seleccione un estudiante", Alert.AlertType.WARNING);
                return;
            }

            Estudiante estudiante = cbEstudiante.getValue();
            Acudiente acudiente = estudiante.getAcudiente();

            if (acudiente == null) {
                mostrarAlerta("Error", "El estudiante no tiene un acudiente asignado",
                        Alert.AlertType.ERROR);
                return;
            }

            String grado = cbGrado.getValue();
            String jornada = cbJornada.getValue();

            // Crear reserva
            Reserva reserva = factoryReservas.createReserva(
                    estudiante, acudiente, grado, jornada
            );

            if (!txtObservaciones.getText().isEmpty()) {
                reserva.setObservaciones(txtObservaciones.getText());
                factoryReservas.updateReserva(reserva);
            }

            cargarDatosReservas();
            limpiarFormulario();

            // Mostrar comprobante
            mostrarComprobanteReserva(reserva);

        } catch (IllegalStateException e) {
            mostrarAlerta("Reserva Duplicada", e.getMessage(), Alert.AlertType.WARNING);
        } catch (IllegalArgumentException e) {
            mostrarAlerta("Sin Cupos", e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al crear reserva: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void mostrarComprobanteReserva(Reserva reserva) {
        Stage dialog = new Stage();
        dialog.setTitle("Comprobante de Reserva");

        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: white; -fx-border-color: #27ae60; -fx-border-width: 3;");

        Label lblTitulo = new Label("✅ RESERVA EXITOSA");
        lblTitulo.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #27ae60;");

        Label lblInfo = new Label(
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                        "COMPROBANTE DE RESERVA DE CUPO\n" +
                        "Colegio Montessori Quimbaya\n" +
                        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                        "Número de Reserva: " + reserva.getNumeroReserva() + "\n" +
                        "Fecha: " + reserva.getFechaReserva().toLocalDate() + "\n" +
                        "Válida hasta: " + reserva.getFechaExpiracion().toLocalDate() + "\n\n" +
                        "DATOS DEL ESTUDIANTE:\n" +
                        "Nombre: " + reserva.getEstudiante().getNombre() + "\n" +
                        "Documento: " + reserva.getEstudiante().getDocumento() + "\n\n" +
                        "DATOS DEL ACUDIENTE:\n" +
                        "Nombre: " + reserva.getAcudiente().getNombre() + "\n" +
                        "Documento: " + reserva.getAcudiente().getDocumento() + "\n" +
                        "Teléfono: " + reserva.getAcudiente().getTelefono() + "\n\n" +
                        "CUPO RESERVADO:\n" +
                        "Grado: " + reserva.getGradoSolicitado() + "\n" +
                        "Jornada: " + reserva.getJornadaSolicitada() + "\n\n" +
                        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                        "IMPORTANTE:\n" +
                        "• Esta reserva es válida por 15 días\n" +
                        "• Días restantes: " + reserva.getDiasRestantes() + "\n" +
                        "• Debe completar la matrícula antes de la fecha de expiración\n" +
                        "• Conserve este número de reserva\n" +
                        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        );
        lblInfo.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 11;");

        Button btnImprimir = new Button("🖨️ IMPRIMIR");
        btnImprimir.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 10 20; -fx-font-weight: bold;");
        btnImprimir.setOnAction(e -> {
            imprimirComprobante(lblInfo.getText());
        });

        Button btnCerrar = new Button("CERRAR");
        btnCerrar.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-padding: 10 20; -fx-font-weight: bold;");
        btnCerrar.setOnAction(e -> dialog.close());

        HBox botones = new HBox(10);
        botones.setAlignment(javafx.geometry.Pos.CENTER);
        botones.getChildren().addAll(btnImprimir, btnCerrar);

        vbox.getChildren().addAll(lblTitulo, new Separator(), lblInfo, new Separator(), botones);

        Scene scene = new Scene(vbox, 500, 650);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void imprimirComprobante(String texto) {
        try {
            PrinterJob job = PrinterJob.createPrinterJob();
            if (job != null && job.showPrintDialog(null)) {
                Label label = new Label(texto);
                label.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 10;");

                boolean success = job.printPage(label);
                if (success) {
                    job.endJob();
                    mostrarAlerta("Éxito", "Comprobante enviado a impresión", Alert.AlertType.INFORMATION);
                }
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo imprimir: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void confirmarReservaSeleccionada() {
        Reserva seleccionada = tablaReservas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Advertencia", "Seleccione una reserva para confirmar", Alert.AlertType.WARNING);
            return;
        }

        if (!"ACTIVA".equals(seleccionada.getEstado())) {
            mostrarAlerta("Advertencia", "Solo se pueden confirmar reservas ACTIVAS", Alert.AlertType.WARNING);
            return;
        }

        if (!seleccionada.estaVigente()) {
            mostrarAlerta("Advertencia", "La reserva ha expirado", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Reserva");
        confirmacion.setHeaderText("¿Confirmar reserva?");
        confirmacion.setContentText(
                "Número: " + seleccionada.getNumeroReserva() + "\n" +
                        "Estudiante: " + seleccionada.getEstudiante().getNombre() + "\n\n" +
                        "Esto indica que la matrícula ha sido completada."
        );

        if (confirmacion.showAndWait().get() == ButtonType.OK) {
            try {
                factoryReservas.confirmarReserva(seleccionada.getIdReserva());
                cargarDatosReservas();
                mostrarAlerta("Éxito",
                        "Reserva confirmada correctamente.\n" +
                                "El cupo ha sido asignado definitivamente.",
                        Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                mostrarAlerta("Error", "Error al confirmar: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void cancelarReservaSeleccionada() {
        Reserva seleccionada = tablaReservas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Advertencia", "Seleccione una reserva para cancelar", Alert.AlertType.WARNING);
            return;
        }

        if (!"ACTIVA".equals(seleccionada.getEstado())) {
            mostrarAlerta("Advertencia", "Solo se pueden cancelar reservas ACTIVAS", Alert.AlertType.WARNING);
            return;
        }

        Stage dialog = new Stage();
        dialog.setTitle("Cancelar Reserva");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));
        vbox.setStyle("-fx-background-color: #f0f0f0;");

        Label lblMotivo = new Label("Motivo de la cancelación:");
        lblMotivo.setStyle("-fx-font-weight: bold;");
        TextArea txtMotivo = new TextArea();
        txtMotivo.setWrapText(true);
        txtMotivo.setPrefRowCount(5);

        Button btnConfirmar = new Button("CONFIRMAR CANCELACIÓN");
        btnConfirmar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 10 20; -fx-font-weight: bold;");
        btnConfirmar.setOnAction(e -> {
            try {
                if (txtMotivo.getText().isEmpty()) {
                    mostrarAlerta("Error", "Debe ingresar un motivo", Alert.AlertType.WARNING);
                    return;
                }
                factoryReservas.cancelarReserva(seleccionada.getIdReserva(), txtMotivo.getText());
                cargarDatosReservas();
                dialog.close();
                mostrarAlerta("Éxito",
                        "Reserva cancelada correctamente.\n" +
                                "El cupo queda disponible para otros estudiantes.",
                        Alert.AlertType.INFORMATION);
            } catch (Exception ex) {
                mostrarAlerta("Error", "Error al cancelar: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        Button btnCancelar = new Button("VOLVER");
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

    private void imprimirComprobanteReserva() {
        Reserva seleccionada = tablaReservas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Advertencia", "Seleccione una reserva para imprimir", Alert.AlertType.WARNING);
            return;
        }

        String comprobante =
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                        "COMPROBANTE DE RESERVA DE CUPO\n" +
                        "Colegio Montessori Quimbaya\n" +
                        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                        "Número de Reserva: " + seleccionada.getNumeroReserva() + "\n" +
                        "Estado: " + seleccionada.getEstado() + "\n" +
                        "Fecha: " + seleccionada.getFechaReserva().toLocalDate() + "\n" +
                        "Válida hasta: " + seleccionada.getFechaExpiracion().toLocalDate() + "\n\n" +
                        "DATOS DEL ESTUDIANTE:\n" +
                        "Nombre: " + seleccionada.getEstudiante().getNombre() + "\n" +
                        "Documento: " + seleccionada.getEstudiante().getDocumento() + "\n\n" +
                        "CUPO RESERVADO:\n" +
                        seleccionada.getCupoInfo() + "\n\n" +
                        "Días restantes: " + seleccionada.getDiasRestantes() + "\n" +
                        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━";

        imprimirComprobante(comprobante);
    }

    private void cargarDatosReservas() {
        datosReservas.clear();

        // Expirar reservas vencidas antes de cargar
        factoryReservas.expirarReservasVencidas();

        datosReservas.addAll(factoryReservas.listReservas());
        tablaReservas.setItems(datosReservas);

        Map<String, Integer> stats = factoryReservas.obtenerEstadisticas();
        lblTotal.setText("Total Reservas: " + stats.get("Total Reservas"));
        lblActivas.setText("Activas: " + stats.get("Activas") +
                " | Confirmadas: " + stats.get("Confirmadas") +
                " | Expiradas: " + stats.get("Expiradas"));
    }

    private void limpiarFormulario() {
        cbEstudiante.setValue(null);
        cbGrado.setValue("Primero");
        cbJornada.setValue("MAÑANA");
        txtObservaciones.clear();
        lblDisponibilidadCupo.setText("");
        btnReservar.setDisable(false);
        tablaReservas.getSelectionModel().clearSelection();
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