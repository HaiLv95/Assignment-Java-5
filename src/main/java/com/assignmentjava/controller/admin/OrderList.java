package com.assignmentjava.controller.admin;

import com.assignmentjava.model.Category;
import com.assignmentjava.model.Mail;
import com.assignmentjava.model.Order;
import com.assignmentjava.model.Product;
import com.assignmentjava.repository.AccountRepository;
import com.assignmentjava.repository.OrderDetailRepository;
import com.assignmentjava.repository.OrderRepository;
import com.assignmentjava.services.SupportServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/admin/orderlist")
public class OrderList {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderDetailRepository orderDetailRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    SupportServices supportServices;

    @GetMapping("")
    public String getOrderList(@ModelAttribute("message") String message,
                               @ModelAttribute("filter") Optional<String> filter,
                               Model model) {
        String sortName = filter.orElse("all");
        Page<Order> resultPage = null;
        if (sortName.equalsIgnoreCase("all")) {

            Pageable pageable = PageRequest.of(0, 20, Sort.by((Sort.Direction.ASC), "id"));
            resultPage = orderRepository.findAll(pageable);
        } else if (sortName.equalsIgnoreCase("pending")) {
            Pageable pageable = PageRequest.of(0, 20, Sort.by((Sort.Direction.ASC), "id"));
            resultPage = orderRepository.findAllByStatus(sortName, pageable);
        } else if (sortName.equalsIgnoreCase("delivering")) {
            Pageable pageable = PageRequest.of(0, 20, Sort.by((Sort.Direction.ASC), "id"));
            resultPage = orderRepository.findAllByStatus(sortName, pageable);
        } else if (sortName.equalsIgnoreCase("success")) {
            Pageable pageable = PageRequest.of(0, 20, Sort.by((Sort.Direction.ASC), "id"));
            resultPage = orderRepository.findAllByStatus(sortName, pageable);
        } else if (sortName.equalsIgnoreCase("cancel")) {
            Pageable pageable = PageRequest.of(0, 20, Sort.by((Sort.Direction.ASC), "id"));
            resultPage = orderRepository.findAllByStatus(sortName, pageable);
        } else {
            Pageable pageable = PageRequest.of(0, 20, Sort.by((Sort.Direction.ASC), "id"));
            resultPage = orderRepository.findAll(pageable);
        }
        if (resultPage.getTotalPages() > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, resultPage.getTotalPages())
                    .boxed()
                    .collect(Collectors.toList());

            model.addAttribute("pageNumbers", pageNumbers);
        }
        model.addAttribute("sortName", sortName);
        model.addAttribute("orderPage", resultPage);
        if (message.length() > 0) {
            model.addAttribute("message", message);
        }
        return "admin/order/order";
    }

    @GetMapping("/confirm/{id}")
    public String confirmOrder(@PathVariable("id") Integer id,
                               RedirectAttributes redirect) {
        Order order = orderRepository.getById(id);
        if (order.getStatus().equalsIgnoreCase("cancel")) {
            redirect.addFlashAttribute("filter", "cancel");
            redirect.addFlashAttribute("message", "????n h??ng ???? ???????c ng?????i mua h???y");
            return "redirect:/admin/orderlist";
        }
        order.setStatus("delivering");
        orderRepository.save(order);
        redirect.addFlashAttribute("filter", "delivering");
        redirect.addFlashAttribute("message", "X??c nh???n ????n h??ng th??nh c??ng");
        Mail mail = new Mail();
        mail.setSubject("X??C NH???N ????N H??NG");
        mail.setMailFrom("hai95.lv@gmail.com");
        mail.setSendTo(order.getUsername().getEmail());
        mail.setContent("<h4>????n h??ng c???a b???n ???? ???????c ng?????i b??n x??c nh???n.</h4>" +
                "<h4>????n h??ng ??ang tr??n ???????ng v???n chuy???n. B???n vui l??ng ????? ?? ??i???n tho???i</h4>" +
                "<h4>Xin c???m ??n!</h4>");
        try {
            supportServices.sendEmail(mail);
        } catch (Exception e) {
            return "redirect:/admin/orderlist";
        }
        return "redirect:/admin/orderlist";
    }

    @GetMapping("/cancel/{id}")
    public String cancelOrder(@PathVariable("id") Integer id,
                              RedirectAttributes redirect) {
        Order order = orderRepository.getById(id);
        order.setStatus("cancel");
        orderRepository.save(order);
        redirect.addFlashAttribute("filter", "cancel");
        redirect.addFlashAttribute("message", "H???y ????n h??ng th??nh c??ng");
        Mail mail = new Mail();
        mail.setSubject("H???Y ????N H??NG");
        mail.setMailFrom("hai95.lv@gmail.com");
        mail.setSendTo(order.getUsername().getEmail());
        mail.setContent("<h3>R???t xin l???i.</h3>" +
                "<h4>????n h??ng c???a b???n ???? b??? ng?????i b??n h???y do l???i c???a ng?????i b??n</h4>" +
                "<h4>Xin c???m ??n!</h4>");
        try {
            supportServices.sendEmail(mail);
        } catch (Exception e) {
            return "redirect:/admin/orderlist";
        }
        return "redirect:/admin/orderlist";
    }


}
