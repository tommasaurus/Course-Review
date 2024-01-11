package edu.virginia.cs.courseReview;

public class Review {
    private Student student;
    private Course course;
    private String feedback;
    private int rating;

    public Review(Student student, Course course, String feedback,int rating){
        this.course= course;
        this.student= student;
        this.rating = rating;
        this.feedback= feedback;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public Student getStudent() {
        return student;
    }

    public int getRating() {

        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}
