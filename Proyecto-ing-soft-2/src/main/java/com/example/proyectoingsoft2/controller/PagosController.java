package com.example.proyectoingsoft2.controller;

import com.example.proyectoingsoft2.model.pagos.Pago;
import com.example.proyectoingsoft2.model.pagos.FactoryGestionPagos;
import com.example.proyectoingsoft2.model.contratos.Matricula;
import com.example.proyectoingsoft2.model.contratos.FactoryGestionContratos;
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

public class PagosController {

    @FXML
    private ComboBox<Matricula> cbMatricula;
    @FXML
    private TextField txtMonto;
    @FXML
    private ComboBox<String> cbMetodoPago;
    @FXML
    private TextField txtNumeroTransaccion;
    @FXML
    private TextField txtDescripcion;
    @FXML
    private TableView<Pago> tablaPagos;
    @FXML
    private TableColumn<Pago, Long> colId;
    @FXML
    private TableColumn<Pago, Long> colMatricula;
    @FXML
    private TableColumn<Pago, Double> colMonto;
    @FXML
    private TableColumn<Pago, String> colMetodo;
    @FXML
    private TableColumn<Pago, String> colEstado;
    @FXML
    private TableColumn<Pago, String> colFecha;
    @FXML
    private Label lblTotal;
    @FXML
    private Label lblTotalPagos;
    @FXML
    private Label lblPagosPendientes;
    @FXML
    private Button btnAgregar;
    @FXML
    private Button btnCompletar;
    @FXML
    private Button btnRechazar;
    @FXML
    private Button btnReembolsar;
    @FXML
    private Button btnLimpiar;
    @FXML
    private Button btnVolver;

    private FactoryGestionPagos factoryPagos;
    private FactoryGestionContratos factoryContratos;
    private ObservableList<Pago> datosPagos;
    private ObservableList<Matricula> matriculas;

    @FXML
    public void initialize() {
        factoryPagos = new FactoryGestionPagos();
        factoryContratos = FactoryGestionContratos.getInstance();
        datosPagos = FXCollections.observableArrayList();
        matriculas = FXCollections.observableArrayList();

        // Configurar columnas
        colId.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleLongProperty(cellData.getValue().getIdPago()).asObject());
        colMatricula.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleLongProperty(cellData.getValue().getIdMatricula()).asObject());
        colMonto.setCellValueFactory(new PropertyValueFactory<>("monto"));
        colMetodo.setCellValueFactory(new PropertyValueFactory<>("metodoPago"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        colFecha.setCellValueFactory(cellData -> {
            Pago p = cellData.getValue();
            String fecha = p.getFechaPago() != null ? p.getFechaPago().toString() : "";
            return new javafx.beans.property.SimpleStringProperty(fecha);
        });

        // Configurar ComboBox Método de Pago
        cbMetodoPago.setItems(FXCollections.observableArrayList(
                "EFECTIVO", "TRANSFERENCIA", "TARJETA", "PSE", "CUOTAS"
        ));
        cbMetodoPago.setValue("EFECTIVO");

        // Cargar matrículas aprobadas
        cargarMatriculas();

        // Configurar eventos
        btnAgregar.setOnAction(event -> crearPago());
        btnCompletar.setOnAction(event -> completarPagoSeleccionado());
        btnRechazar.setOnAction(event -> rechazarPagoSeleccionado());
        btnReembolsar.setOnAction(event -> procesarReembolsoSeleccionado());
        btnLimpiar.setOnAction(event -> limpiarFormulario());
        btnVolver.setOnAction(event -> volverAlMenu());

        cargarDatosPagos();
    }

    private void cargarMatriculas() {
        matriculas.clear();
        matriculas.addAll(factoryContratos.listMatriculas().stream()
                .filter(m -> "APROBADA".equals(m.getEstado()) || "ACTIVA".equals(m.getEstado()))
                .toList());
        cbMatricula.setItems(matriculas);
    }

    private void crearPago() {
        try {
            if (cbMatricula.getValue() == null || txtMonto.getText().isEmpty()) {
                mostrarAlerta("Error", "Complete los campos obligatorios", Alert.AlertType.WARNING);
                return;
            }

            Matricula matricula = cbMatricula.getValue();
            double monto = Double.parseDouble(txtMonto.getText());

            if (monto <= 0) {
                mostrarAlerta("Error", "El monto debe ser mayor a cero", Alert.AlertType.WARNING);
                return;
            }

            Pago pago = factoryPagos.createPago(
                    matricula.getIdMatricula(),
                    monto,
                    cbMetodoPago.getValue(),
                    txtDescripcion.getText()
            );

            cargarDatosPagos();
            limpiarFormulario();
            mostrarAlerta("Éxito", "Pago registrado como PENDIENTE. Número: " + pago.getNumeroTransaccion(),
                    Alert.AlertType.INFORMATION);
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Ingrese un valor numérico válido", Alert.AlertType.ERROR);
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al crear pago: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void completarPagoSeleccionado() {
        Pago seleccionado = tablaPagos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Advertencia", "Seleccione un pago para completar", Alert.AlertType.WARNING);
            return;
        }

        if (!"PENDIENTE".equals(seleccionado.getEstado())) {
            mostrarAlerta("Advertencia", "Solo se pueden completar pagos PENDIENTES", Alert.AlertType.WARNING);
            return;
        }

        Stage dialog = new Stage();
        dialog.setTitle("Completar Pago");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));
        vbox.setStyle("-fx-background-color: #f0f0f0;");

        Label lblMonto = new Label("Monto: $" + seleccionado.getMonto());
        lblMonto.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

        Label lblTransaccion = new Label("Número de Transacción:");
        TextField txtTransaccion = new TextField();
        txtTransaccion.setPromptText("Ej: TRX123456789");

        Button btnConfirmar = new Button("CONFIRMAR PAGO");
        btnConfirmar.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 10 20; -fx-font-weight: bold;");
        btnConfirmar.setOnAction(e -> {
            try {
                if (txtTransaccion.getText().isEmpty()) {
                    mostrarAlerta("Error", "Ingrese el número de transacción", Alert.AlertType.WARNING);
                    return;
                }

                factoryPagos.completarPago(seleccionado.getIdPago(), txtTransaccion.getText());

                // Actualizar estado de matrícula a ACTIVA
                Matricula matricula = factoryContratos.findMatriculaById(seleccionado.getIdMatricula()).orElse(null);
                if (matricula != null && "APROBADA".equals(matricula.getEstado())) {
                    factoryContratos.confirmarPagoMatricula(matricula.getIdMatricula());
                }

                cargarDatosPagos();
                cargarMatriculas();
                dialog.close();
                mostrarAlerta("Éxito", "Pago completado correctamente. Matrícula activada.",
                        Alert.AlertType.INFORMATION);
            } catch (Exception ex) {
                mostrarAlerta("Error", "Error al completar pago: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        Button btnCancelar = new Button("CANCELAR");
        btnCancelar.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-padding: 10 20; -fx-font-weight: bold;");
        btnCancelar.setOnAction(e -> dialog.close());

        HBox botones = new HBox(10);
        botones.setAlignment(javafx.geometry.Pos.CENTER);
        botones.getChildren().addAll(btnConfirmar, btnCancelar);

        vbox.getChildren().addAll(lblMonto, new Separator(), lblTransaccion, txtTransaccion, new Separator(), botones);

        Scene scene = new Scene(vbox, 400, 250);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void rechazarPagoSeleccionado() {
        Pago seleccionado = tablaPagos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Advertencia", "Seleccione un pago para rechazar", Alert.AlertType.WARNING);
            return;
        }

        if (!"PENDIENTE".equals(seleccionado.getEstado())) {
            mostrarAlerta("Advertencia", "Solo se pueden rechazar pagos PENDIENTES", Alert.AlertType.WARNING);
            return;
        }

        Stage dialog = new Stage();
        dialog.setTitle("Rechazar Pago");

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
                factoryPagos.rechazarPago(seleccionado.getIdPago(), txtMotivo.getText());
                cargarDatosPagos();
                dialog.close();
                mostrarAlerta("Éxito", "Pago rechazado correctamente", Alert.AlertType.INFORMATION);
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

    private void procesarReembolsoSeleccionado() {
        Pago seleccionado = tablaPagos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Advertencia", "Seleccione un pago para reembolsar", Alert.AlertType.WARNING);
            return;
        }

        if (!"COMPLETADO".equals(seleccionado.getEstado())) {
            mostrarAlerta("Advertencia", "Solo se pueden reembolsar pagos COMPLETADOS", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Reembolso");
        confirmacion.setHeaderText("¿Procesar reembolso?");
        confirmacion.setContentText("Se reembolsará: $" + seleccionado.getMonto() +
                "\nMétodo: " + seleccionado.getMetodoPago());

        if (confirmacion.showAndWait().get() == ButtonType.OK) {
            try {
                factoryPagos.procesarReembolso(seleccionado.getIdPago());
                cargarDatosPagos();
                mostrarAlerta("Éxito", "Reembolso procesado correctamente",
                        Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                mostrarAlerta("Error", "Error al procesar reembolso: " + e.getMessage(),
                        Alert.AlertType.ERROR);
            }
        }
    }

    private void cargarDatosPagos() {
        datosPagos.clear();
        datosPagos.addAll(factoryPagos.listPagos());
        tablaPagos.setItems(datosPagos);
        lblTotal.setText("Total Pagos: " + datosPagos.size());

        double totalCompletados = factoryPagos.calcularTotalPagosCompletados();
        lblTotalPagos.setText("Pagos Completados: $" + String.format("%.2f", totalCompletados));

        double totalPendientes = factoryPagos.calcularTotalPagosPendientes();
        lblPagosPendientes.setText("Pagos Pendientes: $" + String.format("%.2f", totalPendientes));
    }

    private void limpiarFormulario() {
        cbMatricula.setValue(null);
        txtMonto.clear();
        txtNumeroTransaccion.clear();
        txtDescripcion.clear();
        cbMetodoPago.setValue("EFECTIVO");
        tablaPagos.getSelectionModel().clearSelection();
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