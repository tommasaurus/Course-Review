package edu.virginia.cs.gui;

import edu.virginia.cs.courseReview.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;



import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ReviewController {
    DatabaseManager manager = new DatabaseManager();
    @FXML
    private Button existingUser;
    @FXML
    private Button createUser;
    @FXML
    private Button loginButton;
    @FXML
    private Text incorrectMainInput;
    @FXML
    private Button submit;
    @FXML
    private Button addReview;
    @FXML
    private TextField username;
    @FXML
    private Text incorrectInput;
    @FXML
    private Text selecRating;
    @FXML
    private TextField password;
    @FXML
    private TextField courseName;
    @FXML
    private TextField loginError;
    @FXML
    private Button createUserButton;
    @FXML
    private TextField selectNum;
    @FXML
    private Text reviewText;
    @FXML
    private TextField newUsername;
    @FXML
    private PasswordField newPassword1;
    @FXML
    private PasswordField newPassword2;
    @FXML
    private Button submitReview;
    @FXML
    private Button exitButton;
    @FXML
    private Button logOut;
    @FXML
    private TextField reviewMessage;
    @FXML
    private TextField seeCourse;
    @FXML
    private TextField reviewCourseMessage;
    @FXML
    private TextField averageRating;
    private static ArrayList<Review> reviews;
    private static int count = 0;

    private static String studentUser;



    ReviewApplication app = new ReviewApplication();

    @FXML
    protected void onClickExistingUser() throws IOException {
        app.changeScene("login.fxml");
    }

    @FXML
    protected void onClickCreateUser() throws IOException {
        app.changeScene("createUser.fxml");
    }

    @FXML
    protected void onClickLoginButton() throws IOException {
        try {
            manager.connect();
        } catch (IllegalStateException e) {
        }

        if (manager.studentsExists()) {
            Student student = manager.getStudentByUsername(username.getText());
            if (student.getPassword().equals(password.getText())) {
                app.changeScene("MainMenu.fxml");
                studentUser = username.getText();
                manager.disconnect();
                return;
            }
            boolean passwordCorrect = false;
            while (!passwordCorrect) {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Login");
                dialog.setHeaderText("Wrong Password");
                dialog.setContentText("Please re-enter your password:");
                Optional<String> result = dialog.showAndWait();

                if (result.get().equals(student.getPassword())) {
                    passwordCorrect = true;
                    studentUser = username.getText();
                }
            }
        }
        app.changeScene("MainMenu.fxml");
        manager.disconnect();
    }

    @FXML
    protected void onClickCreateUserButton() throws IOException {

        try {
            manager.connect();
        } catch (IllegalStateException e) {
        }
        if (!newPassword1.getText().equals(newPassword2.getText())) {
            return;
        }
        Student student = new Student(newUsername.getText(), newPassword1.getText());
        try {

            student = manager.getStudentByUsername(newUsername.getText());
        } catch (IllegalArgumentException e) {
            manager.addStudent(student);
            app.changeScene("login.fxml");
            manager.disconnect();
        }
    }

    @FXML
    protected void onClickSubmitReview() throws IOException {
        app.changeScene("SubmitReview.fxml");
    }

    @FXML
    protected void onClicklogOutButton() throws IOException {
        try {
            manager.disconnect();
        } catch (IllegalStateException e) {
        }
        app.changeScene("login.fxml");
    }

    @FXML
    protected void onClickReturnButton() throws IOException {
        try {
            manager.disconnect();
        } catch (IllegalStateException e) {
        }
        app.changeScene("createUser.fxml");
    }

    @FXML
    protected void submitReview() throws IOException {
        DatabaseManager manager = new DatabaseManager();
        try {
            manager.connect();
        } catch (IllegalStateException e) {

        }

        String[] courseID = courseName.getText().split(" ");

         if (courseID.length == 2 && courseID[0].matches("^[a-zA-Z]{2,4}$") && courseID[1].matches("^\\d{4}$")) {
            for(int i = 0; i < courseID.length; i++){
                courseID[i] = courseID[i].toUpperCase();
            }
            reviewMessage.setVisible(true);
            selectNum.setVisible(true);
            reviewText.setVisible(true);
            selecRating.setVisible(true);
            Course course = new Course(courseID[0], Integer.parseInt(courseID[1]));
            List<Course> allCourses= manager.getAllCourses();
            boolean added = false;
            for (Course each: allCourses)
            {
                if (each.equals(course))
                {
                    added = true;
                }
            }
            if (!added)
            {
                manager.addCourse(course);
            }
            addReview.setVisible(true);
        } else {
            app.changeScene("MainMenu.fxml");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid class, must be 1-4 letters followed by 4 numbers");
            alert.setContentText("Please check your input and try again.");
            alert.showAndWait();
        }
        manager.disconnect();
    }


    @FXML
    protected void submitRatingandReview() throws IOException {
        DatabaseManager manager = new DatabaseManager();
        try {

            manager.connect();

        } catch (IllegalStateException e) {
        }
        String[] courseID = courseName.getText().split(" ");

        if (courseID.length == 2 && courseID[0].matches("^[a-zA-Z]{1,4}$") && courseID[1].matches("^\\d{4}$")) {
            Student studentID = manager.getStudentByUsername(studentUser);
            int rating = Integer.parseInt(selectNum.getText());
            String review = reviewMessage.getText();
            Course course = new Course(courseID[0], Integer.parseInt(courseID[1]));
            Review userReview = new Review(studentID,course, review, rating);
            try
            {
                manager.addReview(userReview);
            }
            catch (IllegalStateException e)
            {
                app.changeScene("MainMenu.fxml");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Each student can only review every class once");
                alert.setContentText("Please check your input and try again.");
                alert.showAndWait();
                manager.disconnect();
                return;
            }

            app.changeScene("MainMenu.fxml");
        }
        manager.disconnect();
    }

    @FXML
    protected void onClickSeeReviewMain() throws IOException {
        app.changeScene("seeReview.fxml");
    }
    @FXML
    protected void onClickSeeReview(){
        DatabaseManager manager = new DatabaseManager();
        try {
            manager.connect();
        }
        catch (IllegalStateException e)
        {
        }
        String[] courseID= seeCourse.getText().split(" ");
        if (courseID.length == 2 && courseID[0].matches("^[a-zA-Z]{1,4}$") && courseID[1].matches("^\\d{4}$")) {
            Course course = new Course(courseID[0].toUpperCase(), Integer.parseInt(courseID[1]));
            if(manager.getAllCourses().contains(course)) {
                reviews = (ArrayList<Review>) manager.getReviews(course);
                count = 0;
                try {
                    reviewCourseMessage.setVisible(true);
                    reviewCourseMessage.setText(reviews.get(0).getFeedback());
                }
                catch (IndexOutOfBoundsException e)
                {
                }
                double sum = 0;
                for (Review each: reviews)
                {
                    sum += each.getRating();
                }
                double average = sum/reviews.size();
                DecimalFormat decimalFormat = new DecimalFormat("#.##");
                String formatted = decimalFormat.format(average);
                averageRating.setText("Course Average " + formatted + "/5");
            }
        }
        manager.disconnect();
    }

    @FXML
    protected void onClickNext() {
        if (reviews.size() > count + 1)
        {
            count++;
            reviewCourseMessage.setText(reviews.get(count).getFeedback());
        }
    }

    @FXML
    protected void onClickPrev() {
        if (count > 0)
        {
            count--;
            reviewCourseMessage.setText(reviews.get(count).getFeedback());
        }
    }
}


