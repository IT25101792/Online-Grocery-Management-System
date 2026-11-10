package com.grpproject.store.UserManagement.Model;




public class Admin extends User {

    private String role;

    public Admin(int userId, String name, String email,
                 String password, String role) {
        super(userId, name, email, password);
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void manageProducts() {
        System.out.println("Admin can manage products.");
    }

    public void manageOrders() {
        System.out.println("Admin can manage orders.");
    }

    @Override
    public String toFileString() {
        return super.toFileString() + "|" + role;
    }
}

