package com.grpproject.store.UserManagement.Model;



public class Customer extends User {

    private String address;
    private String phone;

    public Customer(int userId, String name, String email,
                    String password, String address, String phone) {
        super(userId, name, email, password);
        this.address = address;
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void register() {
        System.out.println("Customer registration details created.");
    }

    public void updateProfile() {
        System.out.println("Customer profile updated.");
    }

    @Override
    public String toFileString() {
        return super.toFileString()
                + "|" + address
                + "|" + phone;
    }
}

