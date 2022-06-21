package com.assignmentjava.controller.user;

import com.assignmentjava.model.Account;
import com.assignmentjava.model.Order;
import com.assignmentjava.model.Orderdetail;
import com.assignmentjava.repository.AccountRepository;
import com.assignmentjava.repository.OrderDetailRepository;
import com.assignmentjava.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/orders")
public class OrdersController {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    OrderDetailRepository orderDetailRepository;

    @GetMapping("")
    public String getOrrders(Model model, Principal principal, @ModelAttribute("message")String message){
        if (message.length() > 0) {
            model.addAttribute("message", message);
        }
        Account account = accountRepository.getById(principal.getName());
        List<Order> orders = orderRepository.getAllByUsername(account);
        model.addAttribute("orderList", orders);
        return "user/order";
    }

    @GetMapping("success/{id}")
    public String receivedProduct(@PathVariable("id")Optional<Integer> id, RedirectAttributes redirect){
        Order orderCheck = orderRepository.getById(id.get());
        if (orderCheck.getStatus().equalsIgnoreCase("delivering")){
            orderCheck.setStatus("success");
            orderRepository.save(orderCheck);
            redirect.addFlashAttribute("message", "Cảm ơn bạn đã tin tưởng shop");
        }else{
            redirect.addFlashAttribute("message", "Có lỗi xảy ra với đơn hàng. Vui lòng thử lại sau");
        }
        return "redirect:/orders";
    }
    @GetMapping("cancel/{id}")
    public String cancelOrders(@PathVariable("id")Optional<Integer> id, RedirectAttributes redirect){
        Order orderCheck = orderRepository.getById(id.get());
        if (orderCheck.getStatus().equalsIgnoreCase("pending")){
            orderCheck.setStatus("cancel");
            orderRepository.save(orderCheck);
            redirect.addFlashAttribute("message", "Hủy đơn hàng thành công");
        }else{
            redirect.addFlashAttribute("message", "Có lỗi xảy ra với đơn hàng. Vui lòng thử lại sau");
        }
        return "redirect:/orders";
    }
    @GetMapping("orderdetail/{id}")
    public String getOrderDetail(@PathVariable("id") Optional<Integer> id, Model model){
        Order order = orderRepository.getById(id.get());
         List<Orderdetail> orderList = orderDetailRepository.findAllByOrderID(order);
         model.addAttribute("orderList", orderList);
         return "user/order-detail";
    }
}
