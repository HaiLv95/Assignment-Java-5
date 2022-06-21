package com.assignmentjava.model;

import javax.persistence.*;
import java.time.LocalDate;

@Table(name = "products", indexes = {
        @Index(name = "FK_Categories_Products_idx", columnList = "CategoryID")
})
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @Column(name = "Name", nullable = false, length = 100)
    private String name;

    @Column(name = "Image", nullable = false, length = 500)
    private String image;

    @Column(name = "CreateDate", nullable = false)
    private LocalDate createDate;

    @Column(name = "Available", nullable = false)
    private Boolean available = false;

    @Column(name = "`Desc`", nullable = false, length = 2000)
    private String desc;

    @Column(name = "Size", nullable = false)
    private Integer size;

    @Column(name = "Color", nullable = false, length = 20)
    private String color;

    @Column(name = "Price", nullable = false)
    private Double price;

    @Column(name = "Sell", nullable = false)
    private Integer sell;

    @Column(name = "Quantity", nullable = false)
    private Integer quantity;

    @ManyToOne(optional = false)
    @JoinColumn(name = "CategoryID", nullable = false)
    private Category categoryID;

    public Category getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(Category categoryID) {
        this.categoryID = categoryID;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getSell() {
        return sell;
    }

    public void setSell(Integer sell) {
        this.sell = sell;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public LocalDate getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}