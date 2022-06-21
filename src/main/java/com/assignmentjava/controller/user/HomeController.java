package com.assignmentjava.controller.user;

import com.assignmentjava.repository.AccountRepository;
import com.assignmentjava.repository.CategoryRepository;
import com.assignmentjava.repository.ProductRepository;
import com.assignmentjava.services.FileSystemStorageServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/")
public class HomeController {
    @Autowired
    HttpServletRequest req;
    @Autowired
    ProductRepository productRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    FileSystemStorageServices systemStorageServices;

    @Autowired
    CategoryRepository categoryRepository;
    @RequestMapping({"", "home"})
    public String homeView(@ModelAttribute("message")String message, Model model){
        Pageable top1ProductPageable = PageRequest.of(0, 1, Sort.Direction.DESC, "sell");
        Pageable top3ProductPageable = PageRequest.of(0, 3, Sort.Direction.DESC, "sell");
        req.setAttribute("top1Product", productRepository.findAll(top1ProductPageable).toList().get(0));
        req.setAttribute("top3Categories", productRepository.findTop3CategoryID());
        req.setAttribute("top3products", productRepository.findAll(top3ProductPageable).toList());
        if (message.length() > 0) {
            model.addAttribute("message", message);
        }
        return "user/home";
    }
    @GetMapping("/images/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> getImage(@PathVariable String filename) throws Exception {
        Resource file = systemStorageServices.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment: filename=\"" + file.getFilename() + "\"").body(file);
    }
}
