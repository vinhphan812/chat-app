package com.example.chatapp.Models;

public class User {
    public String email;
    public String firstname;
    public String lastname;
    public String phone;
    public String address;
    public String userID;

    public String Fullname(){
        return String.format("%s %s", firstname, lastname);
    }

}

//public class User {
//    String email;
//    String firstname;
//    String lastname;
//    String phone;
//    String address;
//    String userID;
//
//    public User(){}
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public String getFirstName() {
//        return firstname;
//    }
//
//    public void setFirstName(String firstName) {
//        this.firstname = firstName;
//    }
//
//    public String getLastName() {
//        return lastname;
//    }
//
//    public void setLastName(String lastName) {
//        this.lastname = lastName;
//    }
//
//    public String getPhone() {
//        return phone;
//    }
//
//    public void setPhone(String phone) {
//        this.phone = phone;
//    }
//
//    public String getAddress() {
//        return address;
//    }
//
//    public void setAddress(String address) {
//        this.address = address;
//    }
//
//    public String getUserID() {
//        return userID;
//    }
//
//    public void setUserID(String userID) {
//        this.userID = userID;
//    }
//
//    public User(String email, String firstname, String lastname, String phone, String address, String userID) {
//        this.email = email;
//        this.firstname = firstname;
//        this.lastname = lastname;
//        this.phone = phone;
//        this.address = address;
//        this.userID = userID;
//    }
//}
