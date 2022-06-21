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
            redirect.addFlashAttribute("message", "Lỗi thêm sản phẩm vào giỏ hàng");
            return "redirect:/shop";
        }
        Optional<Product> productCheck = productRepository.findById(id.get());
        if (productCheck.isPresent() && productCheck.get().getQuantity() <= 0){
            redirect.addFlashAttribute("message", "Sản phẩm bạn chọn hiện đang hết hàng. vui lòng chọn sản phẩm khác");
            return "redirect:/shop";
        }

        try {
            shoppingCartServices.addItem(id.get());
        } catch (Exception e) {
            redirect.addFlashAttribute("message", e);
            return "redirect:/shop";
        }
        redirect.addFlashAttribute("message", "Thêm sản phẩm vào giỏ hàng thành công");
        return "redirect:/shopping-cart";

    }

    @PostMapping("update")
    public String update(@RequestParam("id") Optional<Integer> id,
                         @RequestParam("quantity") Optional<Integer> quantity,
                         RedirectAttributes redirect) {
        if (id.isEmpty() || quantity.isEmpty()) {
            redirect.addFlashAttribute("message", "Lỗi cập nhật sản phẩm trong giỏ hàng");
            return "redirect:/shopping-cart";
        }
        Optional<Product> product = productRepository.findById(id.get());
        if (product.get().getQuantity() < quantity.get()) {
            redirect.addFlashAttribute("message", "Mặt hàng này hiện tại chỉ còn " + product.get().getQuantity() + ". Nếu bạn muốn mua thêm vui lòng quay lại sau");
            shoppingCartServices.updateItem(id.get(), product.get().getQuantity());
            return "redirect:/shopping-cart";

        }
        shoppingCartServices.updateItem(id.get(), quantity.get());
        return "redirect:/shopping-cart";
    }

    @GetMapping("/delete/{id}")
    public String deleteItem(@PathVariable("id") Optional<Integer> id, RedirectAttributes redirect) {
        if (id.isEmpty()) {
            redirect.addFlashAttribute("message", "Lỗi xóa sản phẩm trong giỏ hàng");
            return "redirect:/shopping-cart";
        }
        shoppingCartServices.removeItem(id.get());
        redirect.addFlashAttribute("message", "Xóa sản phẩm trong giỏ hàng thành công");
        return "redirect:/shopping-cart";
    }

    @GetMapping("/clear")
    public String clearShoppingCart(RedirectAttributes redirect) {
        shoppingCartServices.clear();
        redirect.addFlashAttribute("message", "Xóa giỏ hàng thành công");
        return "redirect:/shop";
    }

    @GetMapping("/check-out")
    public String getCheckOut(RedirectAttributes redirect, Model model) {
        List<Item> allItems = shoppingCartServices.getItems().stream().collect(Collectors.toList());
        if (allItems.size() < 1) {
            redirect.addFlashAttribute("message", "Giỏ hàng trống! Bạn vui lòng kiểm tra lại giỏ hàng");
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
            redirect.addFlashAttribute("message", "Giỏ hàng trống! Bạn vui lòng kiểm tra lại giỏ hàng");
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
        mail.setSubject("ĐẶT HÀNG THÀNH CÔNG");
        mail.setMailFrom("hai95.lv@gmail.com");
        mail.setSendTo(account.getEmail());
        String headContent = "<h4> Bạn đã đặt hàng thành công vui lòng chờ xác nhận từ người bán. </h4> <br><br>" +
                "<h5>Chi tiết đơn hàng của bạn:</h5>" +
                "<table style=\"border: 1px solid black\">\n" +
                "    <thead style=\"border: 1px solid black\">\n" +
                "    <tr style=\"border: 1px solid black\">\n" +
                "        <th style=\"border: 1px solid black\">Product Name</th>\n" +
                "        <th style=\"border: 1px solid black\">Price</th>\n" +
                "        <th style=\"border: 1px solid black\">Quantity</th>\n" +
                "        <th style=\"border: 1px solid black\">Thành tiền</th>\n" +
                "    </tr>\n" +
                "    </thead>\n" +
                "    <tbody>\n";
        String endContent = "</tbody>\n" +
                "</table>\n" +
                "        <span style=\"font-weight: bold\">Tổng số sản phẩm: "+ shoppingCartServices.getCount() +"  </span> <br> <br>\n" +
                "        <span style=\"font-weight: bold\">Thành tiền: "+ shoppingCartServices.getAmount()+" </span>";
        mail.setContent(headContent + itemContent + endContent);

        try{
            supportServices.sendEmail(mail);
        }catch (Exception e){
            redirect.addFlashAttribute("message", "Đặt hàng thành công");
            return "redirect:/shop";
        }

        redirect.addFlashAttribute("message", "Đặt hàng thành công. Bạn vui lòng check email để biết thêm thông tin");
        return "redirect:/shop";
    }

}
