package com.learning.spring.kafka.avro.reflection;

import org.apache.avro.reflect.Nullable;
import org.springframework.util.StringUtils;

public class ReflectionCustomer {

    private String firstName;
    private String lastName;
    @Nullable //Nullable in avro is a union of Null and String
    private String middleName;

    public ReflectionCustomer () {}

    public ReflectionCustomer (String firstName, String lastName, String middleName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getFullName () {
        return firstName + " " +  ( (middleName != null && !middleName.isEmpty()) ? middleName + " " : "") + lastName;
    }

    @Override
    public String toString() {
        return getFullName();
    }
}
