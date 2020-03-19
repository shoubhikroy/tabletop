package models;

import java.util.ArrayList;

public class ErrorResource {
    public ErrorResource(ArrayList<String> errors) {
        this.errors = errors;
    }

    private ArrayList<String> errors;

    public ArrayList<String> getErrors() {
        return errors;
    }

    @Override
    public String toString() {
        return "ErrorResource{" +
                "errors=" + errors +
                '}';
    }

    public void setErrors(ArrayList<String> errors) {
        this.errors = errors;
    }
}
