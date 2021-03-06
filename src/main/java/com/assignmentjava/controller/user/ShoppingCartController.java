package com.assignmentjava.controller.user;

import com.assignmentjava.dto.OrderDTO;
import com.assignmentjava.model.*;
import com.assignmentjava.repository.AccountRepository;
import com.assignmentjava.repository.OrderDetailRepository;
import com.assignmentjava.repository.OrderRepository;
import com.assignmentjava.repository.ProductRepository;
import com.assignmentjava.services.ShoppingCartServices;
import com.assignmentjava.services.SupportServices;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/shopping-cart")
public class ShoppingCartController {

    @Autowired
    ShoppingCartServices shoppingCartServices;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderDetailRepository orderDetailRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    SupportServices supportServices;

    @GetMapping("")
    public String getCart(@ModelAttribute("message") String message,
                          Model model) {
        if (message.length() > 0) {
            model.addAttribute("message", message);
        }
        int countItem = 0;
        double costItem = 0;
        costItem = shoppingCartServices.getAmount();
        countItem = shoppingCartServices.getCount();
        Collection<Item> cartItems = shoppingCartServices.getItems();
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("costItem", costItem);
        model.addAttribute("countItem", countItem);
        return "user/shopping-cart";
    }

    @GetMapping("/add/{id}")
    public String addToCart(@PathVariable("id") Optional<Integer> id,
                            RedirectAttributes redirect) {
        if (id.isEmpty()) {
            redirect.addFlashAttribute("message", "L???i th??m s???n ph???m v??o gi??? h??ng");
            return "redirect:/shop";
        }
        Optional<Product> productCheck = productRepository.findById(id.get());
        if (productCheck.isPresent() && productCheck.get().getQuantity() <= 0){
            redirect.addFlashAttribute("message", "S???n ph???m b???n ch???n hi???n ??ang h???t h??ng. vui l??ng ch???n s???n ph???m kh??c");
            return "redirect:/shop";
        }

        try {
            shoppingCartServices.addItem(id.get());
        } catch (Exception e) {
            redirect.addFlashAttribute("message", e);
            return "redirect:/shop";
        }
        redirect.addFlashAttribute("message", "Th??m s???n ph???m v??o gi??? h??ng th??nh c??ng");
        return "redirect:/shopping-cart";

    }

    @PostMapping("update")
    public String update(@RequestParam("id") Optional<Integer> id,
                         @RequestParam("quantity") Optional<Integer> quantity,
                         RedirectAttributes redirect) {
        if (id.isEmpty() || quantity.isEmpty()) {
            redirect.addFlashAttribute("message", "L???i c???p nh???t s???n ph???m trong gi??? h??ng");
            return "redirect:/shopping-cart";
        }
        Optional<Product> product = productRepository.findById(id.get());
        if (product.get().getQuantity() < quantity.get()) {
            redirect.addFlashAttribute("message", "M???t h??ng n??y hi???n t???i ch??? c??n " + product.get().getQuantity() + ". N???u b???n mu???n mua th??m vui l??ng quay l???i sau");
            shoppingCartServices.updateItem(id.get(), product.get().getQuantity());
            return "redirect:/shopping-cart";

        }
        shoppingCartServices.updateItem(id.get(), quantity.get());
        return "redirect:/shopping-cart";
    }

    @GetMapping("/delete/{id}")
    public String deleteItem(@PathVariable("id") Optional<Integer> id, RedirectAttributes redirect) {
        if (id.isEmpty()) {
            redirect.addFlashAttribute("message", "L???i x??a s???n ph???m trong gi??? h??ng");
            return "redirect:/shopping-cart";
        }
        shoppingCartServices.removeItem(id.get());
        redirect.addFlashAttribute("message", "X??a s???n ph???m trong gi??? h??ng th??nh c??ng");
        return "redirect:/shopping-cart";
    }

    @GetMapping("/clear")
    public String clearShoppingCart(RedirectAttributes redirect) {
        shoppingCartServices.clear();
        redirect.addFlashAttribute("message", "X??a gi??? h??ng th??nh c??ng");
        return "redirect:/shop";
    }

    @GetMapping("/check-out")
    public String getCheckOut(RedirectAttributes redirect, Model model) {
        List<Item> allItems = shoppingCartServices.getItems().stream().collect(Collectors.toList());
        if (allItems.size() < 1) {
            redirect.addFlashAttribute("message", "Gi??? h??ng tr???ng! B???n vui l??ng ki???m tra l???i gi??? h??ng");
            return "redirect:/shopping-cart";
        }
        model.addAttribute("order", new OrderDTO());
        return "/user/check-out";
    }

    @PostMapping("/check-out")
    public String checkOut(RedirectAttributes redirect,
                           Principal principal,
                           @ModelAttribute("order") Optional<OrderDTO> dto) {
        List<Item> allItems = shoppingCartServices.getItems().stream().collect(Collectors.toList());
        if (allItems.size() < 1) {
            redirect.addFlashAttribute("message", "Gi??? h??ng tr???ng! B???n vui l??ng ki???m tra l???i gi??? h??ng");
            return "redirect:/shopping-cart";
        }
        Order order = new Order();
        BeanUtils.copyProperties(dto.get(), order);
        order.setStatus("Pending");
        Account account = accountRepository.getAccountByUsernameAndActivated(principal.getName(), true);
        order.setUsername(account);
        order.setOrderDate(LocalDate.now());
        Order orderSave = orderRepository.save(order);
        String itemContent="";
        for (Item item :
                allItems) {
            Product product = productRepository.getById(item.getId());
            Orderdetail orderdetail = new Orderdetail();
            orderdetail.setOrderID(orderSave);
            orderdetail.setProductID(product);
            orderdetail.setPrice(product.getPrice());
            orderdetail.setQuantity(item.getQuantity());
            orderDetailRepository.save(orderdetail);
            product.setQuantity(product.getQuantity() - item.getQuantity());
            product.setSell(product.getSell() + item.getQuantity());
            productRepository.save(product);
            itemContent +=   "<tr style=\"border: 1px solid black\">\n" +
                    "        <td style=\"border: 1px solid black\">"+ item.getName()+" </td>\n" +
                    "        <td style=\"border: 1px solid black\">"+ item.getPrice()+ "</td>\n" +
                    "        <td style=\"border: 1px solid black\">"+ item.getQuantity() + "</td>\n" +
                    "        <td style=\"border: 1px solid black\">" + (item.getPrice() * item.getQuantity()) +"</td>\n" +
                    "    </tr>\n";
        }

        Mail mail = new Mail();
        mail.setSubject("?????T H??NG TH??NH C??NG");
        mail.setMailFrom("hai95.lv@gmail.com");
        mail.setSendTo(account.getEmail());
        String headContent = "<h4> B???n ???? ?????t h??ng th??nh c??ng vui l??ng ch??? x??c nh???n t??? ng?????i b??n. </h4> <br><br>" +
                "<h5>Chi ti???t ????n h??ng c???a b???n:</h5>" +
                "<table style=\"border: 1px solid black\">\n" +
                "    <thead style=\"border: 1px solid black\">\n" +
                "    <tr style=\"border: 1px solid black\">\n" +
                "        <th style=\"border: 1px solid black\">Product Name</th>\n" +
                "        <th style=\"border: 1px solid black\">Price</th>\n" +
                "        <th style=\"border: 1px solid black\">Quantity</th>\n" +
                "        <th style=\"border: 1px solid black\">Th??nh ti???n</th>\n" +
                "    </tr>\n" +
                "    </thead>\n" +
                "    <tbody>\n";
        String endContent = "</tbody>\n" +
                "</table>\n" +
                "        <span style=\"font-weight: bold\">T???ng s??? s???n ph???m: "+ shoppingCartServices.getCount() +"  </span> <br> <br>\n" +
                "        <span style=\"font-weight: bold\">Th??nh ti???n: "+ shoppingCartServices.getAmount()+" </span>";
        mail.setContent(headContent + itemContent + endContent);

        try{
            supportServices.sendEmail(mail);
        }catch (Exception e){
            redirect.addFlashAttribute("message", "?????t h??ng th??nh c??ng");
            return "redirect:/shop";
        }

        redirect.addFlashAttribute("message", "?????t h??ng th??nh c??ng. B???n vui l??ng check email ????? bi???t th??m th??ng tin");
        return "redirect:/shop";
    }

}
