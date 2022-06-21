package com.assignmentjava.controller.admin;

import com.assignmentjava.dto.AccountDTO;
import com.assignmentjava.repository.AccountRepository;
import com.assignmentjava.model.Account;
import com.assignmentjava.services.FileSystemStorageServices;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/admin/user-manager")
public class UserManagerController {
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    HttpServletRequest req;

    @Autowired
    FileSystemStorageServices systemStorageServices;


    @Autowired
    PasswordEncoder passwordEncoder;

    boolean sortIndex = true;

    @GetMapping("/images/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> getImage(@PathVariable String filename) throws Exception {
        Resource file = systemStorageServices.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment: filename=\"" + file.getFilename() + "\"").body(file);
    }

    @GetMapping("")
    public String getView(@ModelAttribute("message") String message) {
        //truy van va sap xep
        Pageable pageable = PageRequest.of(0, 10, Sort.by((Sort.Direction.DESC), "username"));
        Page<Account> resultPage = accountRepository.findAll(pageable);
        if (resultPage.getTotalPages() > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, resultPage.getTotalPages())
                    .boxed()
                    .collect(Collectors.toList());
            req.setAttribute("pageNumbers", pageNumbers);
        }
        req.setAttribute("accountPage", resultPage);
        if (message.length() > 0) {
            req.setAttribute("message", message);
        }
        return "admin/user/user-manager";
    }

    @GetMapping("/sort")
    public String sortAccount(@RequestParam("page") Optional<Integer> page,
                              @RequestParam("size") Optional<Integer> size,
                              @RequestParam("sortby") Optional<String> sortType,
                              @RequestParam("sort") Optional<Boolean> sort,
                              @ModelAttribute("message") String message) {
        int curentPage = page.orElse(1);
        int pageSize = size.orElse(10);
        String sortName = sortType.orElse("username");
        Page<Account> resultPage = null;
        //truy van va sap xep
        Pageable pageable = PageRequest.of(curentPage - 1, pageSize, Sort.by((sortIndex ? Sort.Direction.DESC : Sort.Direction.ASC), sortName));
        resultPage = accountRepository.findAll(pageable);
        boolean sortPage = sort.orElse(false);
        if (sortPage) sortIndex = !sortIndex;
        if (resultPage.getTotalPages() > 0) {
            //gender số trang ra các trang nhỏ
            List<Integer> pageNumbers = IntStream.rangeClosed(1, resultPage.getTotalPages())
                    .boxed()
                    .collect(Collectors.toList());
            req.setAttribute("pageNumbers", pageNumbers);
        }
        req.setAttribute("accountPage", resultPage);
        if (message.length() > 0) {
            req.setAttribute("message", message);
        }
        return "admin/user/user-manager";
    }

    @GetMapping("/lock/{username}")
    public String deleteAccount(@PathVariable("username") String username, RedirectAttributes redirect, Principal principal) {
        Account account = accountRepository.getById(username);
        if (account.getUsername().equalsIgnoreCase(principal.getName())) {
            redirect.addFlashAttribute("message", "Bạn không thể khóa tài khoản bạn đang đăng nhập");
            return "redirect:/admin/user-manager";
        }
        account.setActivated(!account.getActivated());
        Account accountUpdate = accountRepository.save(account);
        if (accountUpdate.getActivated()) {
            redirect.addFlashAttribute("message", "Mở khóa tài khoản người dùng \"" + username + "\" thành công!");
        } else {
            redirect.addFlashAttribute("message", "Khóa tài khoản người dùng \"" + username + "\" thành công!");
        }
        return "redirect:/admin/user-manager";
    }

    @GetMapping("add")
    public String add(@ModelAttribute("message") String message, Model model) {
        if (message.length() > 0) {
            model.addAttribute("message", message);
        }
        AccountDTO dto = new AccountDTO();
        dto.setEdit(false);
        model.addAttribute("account", dto);
        return "/admin/user/add-account";
    }

    @GetMapping("edit/{username}")
    public String edit(@ModelAttribute("message") String message, Model model, @PathVariable("username") String username) {
        if (message.length() > 0) {
            System.out.println("message " + message + " / " + message.length());
            model.addAttribute("message", message);
        }
        Account account = accountRepository.getById(username);
        AccountDTO dto = new AccountDTO();
        dto.setEdit(true);
        BeanUtils.copyProperties(account, dto);
        model.addAttribute("account", dto);
        return "/admin/user/add-account";
    }

    @PostMapping("addOrEdit")
    public String addOrEdit(@Valid @ModelAttribute("account") AccountDTO dto,
                            RedirectAttributes redirect) {
        Optional<Account> checkAccount = accountRepository.findById(dto.getUsername());
        Account account = new Account();
        BeanUtils.copyProperties(dto, account);
        if (!dto.isEdit()) {
            if (checkAccount.isPresent()) {
                redirect.addFlashAttribute("message", "Tài khoản đã tồn tại vui lòng chọn tài khoản khác");
                return "redirect:/admin/user-manager/add";
            }
            account.setPassword(passwordEncoder.encode(dto.getPassword()));
        }else {
            account.setPassword(checkAccount.get().getPassword());
        }

        if (!dto.getImageFile().isEmpty()) {
            String uuid = UUID.randomUUID().toString();
            account.setPhoto(systemStorageServices.getStoredFileName(dto.getImageFile(), uuid));
            try {
                systemStorageServices.store(dto.getImageFile(), account.getPhoto());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (dto.getImageFile().isEmpty() && dto.getPhoto() != null) {
            account.setPhoto(dto.getPhoto());
        } else {
            redirect.addFlashAttribute("message", "Bạn chưa chọn ảnh đại diện");
            return "redirect:/admin/user-manager/adđ";
        }
        accountRepository.save(account);
        redirect.addFlashAttribute("message", "Thêm người dùng " + dto.getUsername() + " thành công!");
        return "redirect:/admin/user-manager";
    }
}
