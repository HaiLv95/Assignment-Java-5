package com.assignmentjava.model;

import javax.persistence.*;
import java.time.LocalDate;

@Table(name = "orders", indexes = {
        @Index(name = "FK_Account_orders_idx", columnList = "Username")
})
@Entity
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "Username", nullable = false)
    private Account username;

    @Column(name = "NameReceived", nullable = false, length = 45)
    private String nameReceived;

    @Column(name = "PhoneNumber", nullable = false, length = 10)
    private String phoneNumber;


    @Column(name = "OrderDate", nullable = false)
    private LocalDate orderDate;

    @Column(name = "Address", nullable = false, length = 100)
    private String address;

    @Column(name = "Status", nullable = false, length = 45)
    private String status;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getNameReceived() {
        return nameReceived;
    }

    public void setNameReceived(String nameReceived) {
        this.nameReceived = nameReceived;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public Account getUsername() {
        return username;
    }

    public void setUsername(Account username) {
        this.username = username;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}