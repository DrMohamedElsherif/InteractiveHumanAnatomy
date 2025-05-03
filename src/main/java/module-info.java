
/*module com.example.assignments {
    requires javafx.controls;
    requires javafx.fxml;


    //opens com.example.assignments to javafx.fxml;
    //exports com.example.assignments;
    opens Assignment01 to javafx.fxml, javafx.graphics;
    exports Assignment01;

}*/
module com.example.assignments {
    requires javafx.controls;
    requires javafx.fxml;

    // Export all assignment packages
    exports Assignment01;
    exports Assignment02;  // Future package
    //exports Assignment03;  // Future package

    // Open all assignment packages to JavaFX
    opens Assignment01 to javafx.graphics;
    opens Assignment02 to javafx.graphics;
    //opens Assignment03 to javafx.graphics;  // Future package
}