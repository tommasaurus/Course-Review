module CourseReviews.main {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens edu.virginia.cs.gui to javafx.fxml;
    exports edu.virginia.cs.gui;
    exports edu.virginia.cs.courseReview;
    opens edu.virginia.cs.courseReview to javafx.fxml;
}