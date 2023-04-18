package courseudem.demo.client;

import courseudem.demo.server.models.Course;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;


public class CourseFx implements Observable {


    private SimpleStringProperty code;
    private SimpleStringProperty name;

    private String session;


    public CourseFx(Course course) {
        this.name = new SimpleStringProperty(course.getName());
        this.code = new SimpleStringProperty(course.getCode());
        session = course.getSession();
    }

    public Course toCourse() {
        return new Course(getName(), getCode(), session);
    }

    public CourseFx(String code, String name) {
        this.code = new SimpleStringProperty(code);
        this.name = new SimpleStringProperty(name);
    }

    public String getCode() {
        return code.get();
    }

    public void setCode(String code) {
        this.code.set(code);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }


    @Override
    public String toString() {
        return "Course{" +
                "name=" + name +
                ", code=" + code +
                '}';
    }

    @Override
    public void addListener(InvalidationListener invalidationListener) {
        code.addListener(invalidationListener);
        name.addListener(invalidationListener);
    }

    @Override
    public void removeListener(InvalidationListener invalidationListener) {
        code.removeListener(invalidationListener);
        name.removeListener(invalidationListener);

    }
}
