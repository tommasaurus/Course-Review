package edu.virginia.cs.courseReview;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class DatabaseManager {
    private Connection connection;

    public void connect() {
        if (connection != null) {

            throw new IllegalStateException("already connected");
        }
        try {
            Class.forName("org.sqlite.JDBC");
            String dbUrl = "jdbc:sqlite:Reviews.sqlite3";
            connection = DriverManager.getConnection(dbUrl);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void createTables() {
        try {
            if (connection == null) {
                throw new IllegalStateException("Manager not connected yet");
            }
            Statement statement = connection.createStatement();
            String sql = "CREATE TABLE Students (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                         "login TEXT(255) UNIQUE NOT NULL," +
                         "password TEXT(255) NOT NULL);";

            String sql1= "CREATE TABLE Courses (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                         "department TEXT(255) NOT NULL," +
                         "catalog_number INT(5) NOT NULL," +
                         "UNIQUE(department, catalog_number));";

            String sql2 = "CREATE TABLE Reviews (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                          "student_id INTEGER NOT NULL REFERENCES Students(id) ON DELETE CASCADE," +
                          "course_id INTEGER NOT NULL REFERENCES Courses(id) ON DELETE CASCADE," +
                          "message TEXT(255) NOT NULL," +
                          "rating INT(1) NOT NULL CHECK(rating BETWEEN 1 AND 5)," +
                          "UNIQUE(student_id, course_id));";

            statement.execute(sql);
            statement.execute(sql1);
            statement.execute(sql2);

        } catch (SQLException e) {
            throw new IllegalStateException("Error creating table:" + e.getMessage());
        }
    }

    public void clear() {
        try {
            if (connection == null) {
                throw new IllegalStateException("Manager not connected yet");
            }
            if (!studentsExists() && !coursesExists() && !reviewsExists()) {
                throw new IllegalStateException("Tables do not exist");
            }
            Statement statement = connection.createStatement();
            statement.execute("DELETE FROM Students;");
            statement.execute("DELETE FROM Courses;");
            statement.execute("DELETE FROM Reviews;");
            statement.close();
        } catch (SQLException e) {
            throw new IllegalStateException("could not clear " + e.getMessage());
        }
    }

    public void deleteTables() {
        if (connection == null) {
            throw new IllegalStateException("Manager is not connected");
        }
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DROP TABLE IF EXISTS Students;");
            statement.executeUpdate("DROP TABLE IF EXISTS Courses;");
            statement.executeUpdate("DROP TABLE IF EXISTS Reviews;");
            statement.close();
        } catch (SQLException e) {
            throw new IllegalStateException("could not delete " + e.getMessage());
        }
    }

    public void addStudent(Student student) {
        if (!studentsExists()) {
            throw new IllegalStateException("Students table not created yet");
        }

        try {
            if (connection == null) {
                throw new IllegalStateException("Manager not connected yet");
            }

            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO Students (login, password) VALUES (?, ?);"
            );

            statement.setString(1, student.getLogin());
            statement.setString(2, student.getPassword());
            statement.executeUpdate();

        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                throw new IllegalArgumentException("Student already exists in database");
            } else {
                throw new IllegalStateException("Error adding students:" + e.getMessage());
            }
        }
    }

    public List<Student> getAllStudents() {
        try {
            if (connection == null) {
                throw new IllegalStateException("Manager is not connected");
            }
            if (!studentsExists()) {
                throw new IllegalStateException("Stops does not exist");
            }

            Statement statement = connection.createStatement();
            String sql = "SELECT * from Students;";
            ResultSet rs = statement.executeQuery(sql);

            List<Student> ret = new ArrayList<Student>();
            if (!rs.next()) {
                return ret;
            }
            while (rs.next()) {
                ret.add(new Student(rs.getString("login"), rs.getString("password")));
            }
            return ret;
        } catch (SQLException e) {
            throw new IllegalStateException("could not select " + e.getMessage());
        }
    }

    public Student getStudentByUsername(String username) {
        if (connection == null) {
            throw new IllegalStateException("Manager not connected");
        }
        if (!studentsExists()) {
            throw new IllegalStateException("Students does not exist");
        }
        Student student1 = new Student();
        try {
            Statement statement = connection.createStatement();
            try {
                ResultSet rs = statement.executeQuery("SELECT * FROM Students");
            } catch (SQLException e) {
                throw new IllegalStateException("Table for students does not exist");
            }
            ResultSet rs2 = statement.executeQuery("SELECT * FROM Students");
            Boolean retrieve = false;
            while (rs2.next()) {
                if (rs2.getString("login").equals(username)) {
                    student1 = new Student(rs2.getString("login"), rs2.getString("password"));
                    retrieve = true;
                }
            }
            rs2.close();
            if (!retrieve) {
                throw new IllegalArgumentException("No student with username:" + username + " found");
            }
        } catch (SQLException e) {
            System.out.println("Error getting student by username");
            System.out.println(e.getMessage());
        }
        return student1;
    }

    public void addCourse(Course course) {
        if (connection == null) {
            throw new IllegalStateException("Manager not connected");
        }
        if (!reviewsExists() || !coursesExists()) {
            throw new IllegalStateException("Courses table doesn't exist");
        }
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO Courses (department, catalog_number) VALUES (?, ?)");

            statement.setString(1, course.getDepartment().toUpperCase());
            statement.setInt(2, course.getCatalog());
            statement.execute();
            statement.close();
        } catch (SQLException e) {
            if ("23000".equals(e.getSQLState()))
            {
                System.err.println("This course already exists");
            }
            else
            {
                throw new RuntimeException(e);
            }
        }
    }

    public List<Course> getAllCourses()
    {
        if (connection == null){
            throw new IllegalStateException("The Manager is not connected");
        }
        if (!coursesExists()) {
            throw new IllegalStateException("Courses does not exist");
        }

        try {
            Statement statement = connection.createStatement();
            String sql = "SELECT * from Courses;";
            ResultSet rs = statement.executeQuery(sql);
            List<Course> ret = new ArrayList<Course>();
            while (rs.next()) {
                ret.add(new Course(rs.getString("department"), rs.getInt("catalog_number")));
            }
            return ret;
        } catch (SQLException e) {
            throw new IllegalStateException("could not select " + e.getMessage());
        }
    }

    public void addReview(Review review) {
        if (!studentsExists()) {
            throw new IllegalStateException("Students table not created yet");
        }
        if (!coursesExists()) {
            throw new IllegalStateException("Courses table not created yet");
        }
        try {
            if (connection == null) {
                throw new IllegalStateException("Manager not connected yet");
            }
            Statement statement = connection.createStatement();
            ResultSet rs2 = statement.executeQuery("SELECT * FROM Students");
            Boolean retrieve = false;
            int studentid = -1;
            while (rs2.next()) {
                if (rs2.getString("login").equals(review.getStudent().getLogin())) {
                    studentid = rs2.getInt("id");
                    retrieve = true;
                }
            }

            if (!retrieve)
            {
                throw new IllegalStateException("this student does not exist");
            }


            Statement statement1 = connection.createStatement();
            ResultSet rs3 = statement1.executeQuery("SELECT * FROM Courses");
            retrieve = false;
            int courseid = -1;
            while (rs3.next()) {
                if (rs3.getString("department").toUpperCase().equals(review.getCourse().getDepartment().toUpperCase()) && (rs3.getInt("catalog_number") == (review.getCourse().getCatalog()))) {
                    courseid = rs3.getInt("id");
                    retrieve = true;
                }
            }

            if (!retrieve)
            {
                throw new IllegalStateException("this course does not exist");
            }

            PreparedStatement statement2 = connection.prepareStatement(
                    "INSERT INTO Reviews (student_id, course_id, message, rating) VALUES (?, ?, ?, ?);"
            );

            statement2.setInt(1, studentid);
            statement2.setInt(2, courseid);
            statement2.setString(3, review.getFeedback());
            statement2.setInt(4, review.getRating());
            statement2.execute();


        } catch (SQLException e) {
                throw new IllegalStateException("Error adding review:" + e.getMessage());
        }
    }

    public List<Review> getReviews(Course courseId) {
        ArrayList<Review> reviews = new ArrayList<Review>();
        if (!reviewsExists()) {
            throw new IllegalStateException("Reviews table not created yet");
        }
        if (!coursesExists()) {
            throw new IllegalStateException("Courses table not created yet");
        }
        if (!studentsExists()) {
            throw new IllegalStateException("Students table not created yet");
        }
        try {
            if (connection == null) {
                throw new IllegalStateException("Manager not connected yet");
            }
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM Reviews");
            while (rs.next())
            {
                Statement statement1 = connection.createStatement();
                ResultSet rs3 = statement1.executeQuery("SELECT * FROM Courses");
                String department = "";
                int catalog = 0;
                while (rs3.next())
                {
                    if (rs3.getInt("id") == rs.getInt(3))
                    {
                        department = rs3.getString("department");
                        catalog = rs3.getInt("catalog_number");
                        break;
                    }
                }
                Course course = new Course(department,catalog);
                if (courseId.getDepartment().equals(department) && courseId.getCatalog() == catalog) {
                    Statement statement2 = connection.createStatement();
                    ResultSet rs2 = statement2.executeQuery("SELECT * FROM Students");
                    String login = "";
                    String password = "";
                    while (rs2.next()) {
                        if (rs2.getInt("id") == rs.getInt("student_id")) {
                            login = rs2.getString("login");
                            password = rs2.getString("password");
                            break;
                        }
                    }
                    Student student = new Student(login, password);
                    reviews.add(new Review(student, course, rs.getString("message"), rs.getInt("rating")));
                }
            }
            return reviews;
        } catch (SQLException e) {
            throw new IllegalStateException("Error getting review:" + e.getMessage());
        }
    }


    public void disconnect() {
        if (connection == null) {
            throw new IllegalStateException("Manager has not connected");
        }
        try {
            if (connection != null || connection.isClosed()) {
                connection.close();
                System.out.println("Disconnected from database");
            }

        } catch (SQLException e) {
            System.out.println("Error when disconnecting");
            System.out.println(e.getMessage());
        }
    }

    public boolean studentsExists()
    {
        try
        {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='Students';");
            if (rs.next())
            {
                return true;
            }
        }
        catch (SQLException e)
        {
        }
        return false;
    }

    public boolean coursesExists()
    {
        try
        {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='Courses';");
            if (rs.next())
            {
                return true;
            }
        }
        catch (SQLException e)
        {
        }
        return false;
    }

    public boolean reviewsExists()
    {
        try
        {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='Reviews';");
            if (rs.next())
            {
                return true;
            }
        }
        catch (SQLException e)
        {
        }
        return false;
    }
    public void setup() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("INSERT INTO Students(LOGIN, PASSWORD) VALUES ('bob123', '123')");
        statement.execute("INSERT INTO Students(LOGIN, PASSWORD) VALUES ('rob123', '123')");
        statement.execute("INSERT INTO Students(LOGIN, PASSWORD) VALUES ('cob123', '123')");
        statement.execute("INSERT INTO Courses(DEPARTMENT, CATALOG_NUMBER) VALUES ('CS', '3140');");
        statement.execute("INSERT INTO Courses(DEPARTMENT, CATALOG_NUMBER) VALUES ('ENWR', '2000');");
        statement.execute("INSERT INTO Courses(DEPARTMENT, CATALOG_NUMBER) VALUES ('EMGL', '2599');");
        statement.execute("INSERT INTO Reviews(student_id, course_id, message, rating) VALUES (1, 1, 'fun', 5);");
        statement.execute("INSERT INTO Reviews(student_id, course_id, message, rating) VALUES (2, 2, 'boring', 5);");
        statement.execute("INSERT INTO Reviews(student_id, course_id, message, rating) VALUES (2, 1, 'amaZing', 4);");
        statement.execute("INSERT INTO Reviews(student_id, course_id, message, rating) VALUES (3, 3, 'trash', 1);");



    }
    public static void main (String[] args){
        DatabaseManager db = new DatabaseManager();
        db.connect();
        db.deleteTables();

    }


}


