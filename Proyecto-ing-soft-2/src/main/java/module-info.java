module com.example.proyectoingsoft2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.proyectoingsoft2.controller to javafx.fxml;
    exports com.example.proyectoingsoft2;

    exports com.example.proyectoingsoft2.controller;
    opens com.example.proyectoingsoft2.model.cupos to javafx.base;
    opens com.example.proyectoingsoft2.model.contratos to javafx.base;

}
