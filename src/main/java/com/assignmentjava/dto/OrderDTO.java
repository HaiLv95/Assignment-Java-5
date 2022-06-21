package com.assignmentjava.dto;

public class OrderDTO {
    private Integer id;
    private String username;
    private Integer productID;
    private String Address;
    private String nameReceived;
    private String phoneNumber;
    private String status;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNameReceived() {
        return nameReceived;
    }

    public void setNameReceived(String nameReceived) {
        this.nameReceived = nameReceived;
    }

    public OrderDTO(Integer id, String username, Integer productID, String address, String nameReceived, String phoneNumber, String status) {
        this.id = id;
        this.username = username;
        this.productID = productID;
        this.phoneNumber = phoneNumber;
        this.status = status;
        Address = address;
        this.nameReceived = nameReceived;
    }

    public OrderDTO() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getProductID() {
        return productID;
    }

    public void setProductID(Integer productID) {
        this.productID = productID;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }
}
