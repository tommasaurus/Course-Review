package edu.virginia.cs.courseReview;

import java.util.Formatter;
import java.util.Objects;

public class Course {
    private String department;
    private int catalog;

    public Course ()
    {
        this.department= "";
        this.catalog= 0;
    }

    public Course (String department, int catalog){
        this.department= department;
        this.catalog= catalog;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getCatalog() {
        return catalog;
    }

    public void setCatalog(int catalog) {
        this.catalog = catalog;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return getCatalog() == course.getCatalog() && getDepartment().equals(course.getDepartment());
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
