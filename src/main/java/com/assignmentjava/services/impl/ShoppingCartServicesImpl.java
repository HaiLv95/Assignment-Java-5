package com.assignmentjava.services.impl;

import com.assignmentjava.repository.ProductRepository;
import com.assignmentjava.model.Item;
import com.assignmentjava.model.Product;
import com.assignmentjava.services.ShoppingCartServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
@SessionScope
@Service
public class ShoppingCartServicesImpl implements ShoppingCartServices {
    Map<Integer, Item> map = new HashMap<>();
    @Autowired
    ProductRepository productDAO;
    @Override
    public Item addItem(Integer id) throws Exception {
        if (map.get(id) == null){
            Product product = productDAO.getById(id);
            if (product != null){
                Item item = new Item(product.getId(), product.getName(), product.getPrice(), Integer.valueOf(1));
                map.put(product.getId(), item);
                return item;
            }else {
                throw new Exception("Add item to cart fail");
            }
        }else {
            Item item = map.get(id);
            item.setQuantity(item.getQuantity() + 1);
            return item;
        }

    }

    @Override
    public void removeItem(Integer id) {
        map.remove(id);
    }

    @Override
    public Item updateItem(Integer id, int quantity) {
        Item item = map.get(id);
        item.setQuantity(quantity);
        return item;
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Collection<Item> getItems() {
        return map.values();
    }

    @Override
    public double getAmount() {
        double amount = 0;
        Collection<Item> items = map.values();
        for (Item item: items) {
            amount += item.getPrice() * item.getQuantity();
        }
        return amount;
    }

    @Override
    public int getCount() {
        int count = 0;
        Collection<Item> items = map.values();
        for (Item item: items) {
            count += item.getQuantity();
        }
        return count;
    }
}
