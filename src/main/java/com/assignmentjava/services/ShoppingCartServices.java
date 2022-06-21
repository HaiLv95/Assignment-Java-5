package com.assignmentjava.services;

import com.assignmentjava.model.Item;

import java.util.Collection;

public interface ShoppingCartServices {
    //thêm item
    Item addItem(Integer id) throws Exception;
    //xóa item
    void removeItem(Integer id);
    //cập nhật giỏ hàng
    Item updateItem(Integer id, int quantity);
    //xóa tất cả item
    void  clear();
    //lấy tất cả item
    Collection<Item> getItems();
    //lấy tổng tiền thanh toán
    double getAmount();
    //lấy tổng số lượng
    int getCount();
}
