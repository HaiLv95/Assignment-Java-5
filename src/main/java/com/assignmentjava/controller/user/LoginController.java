package com.assignmentjava.controller.user;

import com.assignmentjava.model.Account;
import com.assignmentjava.model.Mail;
import com.assignmentjava.repository.AccountRepository;
import com.assignmentjava.services.SupportServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Controller
public class LoginController {
    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    SupportServices supportServices;

    @GetMapping("/login")
    public String getLogin(){
        return "login";
    }

    @GetMapping("/logoff/success")
    public String logout(){
        return "redirect:/login";
    }
    @GetMapping("/login/fail")
    public String logìnFail(){
        return "redirect:/login";
    }
    @GetMapping("/403")
    @ResponseBody
    public String urlNotAccess(){
        return "<h1>Tài khoản không có quyền truy cập</h1>";
    }

    @PostMapping("/user/change-password")
    public String changePassword(@ModelAttribute("oldPass")String oldPass,
                                 @ModelAttribute("newPass")String newPass,
                                 Principal principal,
                                 RedirectAttributes redirect){
        Account account = accountRepository.getById(principal.getName());
        System.out.println("pass csdl: "+account.getPassword());
        if (passwordEncoder.matches(oldPass, account.getPassword())){
            account.setPassword(passwordEncoder.encode(newPass));
            accountRepository.save(account);
            redirect.addFlashAttribute("message", "Đổi mật khẩu thành công");
        }else {
            redirect.addFlashAttribute("message", "Mật khẩu cũ không đúng");
        }
        return "redirect:/home";
    }

    @PostMapping("/forgot-password")
    public String findPassword(@ModelAttribute("username")String username,
                               @ModelAttribute("email")String email,
                               RedirectAttributes redirect){
        Optional<Account> account = accountRepository.findById(username);
        if (account.isEmpty()){
            redirect.addFlashAttribute("message", "Username không tồn tại");
        }else {
            if (!email.equalsIgnoreCase(account.get().getEmail())){
                redirect.addFlashAttribute("message", "Email không đúng. Bạn vui lòng nhập đúng thông tin");
            }else {
                String newPass = ThreadLocalRandom.current().nextInt(1000,9999) + "";
                account.get().setPassword(passwordEncoder.encode(newPass));
                accountRepository.save(account.get());
                Mail mail = new Mail();
                mail.setSubject("ĐẶT LẠI MẬT KHẨU");
                mail.setContent("Mật khẩu hiện tại của bạn là: <p style=\"font-weight: bold\">" + newPass + " </p>." +
                        " Vui lòng đặt lại mật khẩu để bảo mật. Xin cảm ơn!");
                mail.setMailFrom("ADMIN");
                mail.setSendTo(account.get().getEmail());
                try{
                    supportServices.sendEmail(mail);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return "redirect:/home";
    }
}
