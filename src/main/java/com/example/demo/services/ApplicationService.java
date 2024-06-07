package com.example.demo.services;

import java.util.ArrayList;
import java.util.List;

public abstract class ApplicationService {
    public ApplicationService() {}

    // TODO factory pattern as static method ?

    protected static class ResponseService {
        private boolean isSuccess;
        private List<String> errors;

        public ResponseService() {
            this.isSuccess = false;
            this.errors = new ArrayList<>();
        }

        public List<String> getErrors() {
            return this.errors;
        }

        public void setErrors(List<String> errors) {
            this.errors = errors;
        }

        public void addErrors(String error) {
            this.errors.add(error);
        }

        public boolean getIsSuccess() {
            return this.isSuccess;
        }

        public void setSuccess() {
            this.isSuccess = true;
        }
    }

}
