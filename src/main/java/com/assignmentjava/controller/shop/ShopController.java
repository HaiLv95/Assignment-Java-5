package com.assignmentjava.controller.shop;

import com.assignmentjava.model.Category;
import com.assignmentjava.model.Product;
import com.assignmentjava.repository.CategoryRepository;
import com.assignmentjava.repository.ProductRepository;
import com.assignmentjava.services.FileSystemStorageServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/shop")
public class ShopController {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    FileSystemStorageServices systemStorageServices;
    boolean sortIndex = true;

    @GetMapping("/images/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> getImage(@PathVariable String filename) throws Exception {
        Resource file = systemStorageServices.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment: filename=\"" + file.getFilename() + "\"").body(file);
    }

    @GetMapping("")
    public String viewShop(@ModelAttribute("message") String message, Model model) {
        Pageable pageable = PageRequest.of(0, 12, Sort.by((Sort.Direction.ASC), "id"));
        Page<Product> resultPage = productRepository.findAllByAvailable(true, pageable);
        if (resultPage.getTotalPages() > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, resultPage.getTotalPages())
                    .boxed()
                    .collect(Collectors.toList());

            model.addAttribute("pageNumbers", pageNumbers);
            model.addAttribute("sortName", "name");
        }
        model.addAttribute("productsPage", resultPage);
        if (message.length() > 0) {
            model.addAttribute("message", message);
        }
        return "user/shop";
    }

    @GetMapping("search")
    public String search(@ModelAttribute("name") Optional<String> name, Model model) {
        Pageable pageable = PageRequest.of(0, 12, Sort.by((Sort.Direction.ASC), "id"));
        Page<Product> resultPage = null;
        String nameSearch = name.orElse("all");
        System.out.println(nameSearch);
        if (!nameSearch.equalsIgnoreCase("all")) {
            resultPage = productRepository.findAllByNameContaining(nameSearch, pageable);
            if (resultPage.isEmpty()) resultPage = productRepository.findAllByAvailable(true, pageable);
        } else {
            resultPage = productRepository.findAllByAvailable(true, pageable);
        }
        if (resultPage.getTotalPages() > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, resultPage.getTotalPages())
                    .boxed()
                    .collect(Collectors.toList());

            model.addAttribute("pageNumbers", pageNumbers);
            model.addAttribute("sortName", "name");
        }
        model.addAttribute("productsPage", resultPage);
        return "user/shop";
    }

    @GetMapping("/sort")
    public String sortAccount(@RequestParam("page") Optional<Integer> page,
                              @RequestParam("size") Optional<Integer> size,
                              @RequestParam("sortby") Optional<String> sortType,
                              @RequestParam("sort") Optional<Boolean> sort,
                              Model model,
                              @ModelAttribute("message") String message) {
        int curentPage = page.orElse(1);
        int pageSize = size.orElse(12);
        String sortName = sortType.orElse("id");
        Page<Product> resultPage = null;
        //truy van va sap xep
        Pageable pageable = PageRequest.of(curentPage - 1, pageSize, Sort.by((sortIndex ? Sort.Direction.DESC : Sort.Direction.ASC), sortName));
        resultPage = productRepository.findAllByAvailable(true, pageable);
        boolean sortPage = sort.orElse(false);
        if (sortPage) sortIndex = !sortIndex;
        if (resultPage.getTotalPages() > 0) {
            //gender số trang ra các trang nhỏ
            List<Integer> pageNumbers = IntStream.rangeClosed(1, resultPage.getTotalPages())
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        model.addAttribute("sortName", sortName);
        model.addAttribute("productsPage", resultPage);
        if (message.length() > 0) {
            model.addAttribute("message", message);
        }
        return "user/shop";
    }
    @GetMapping("category/{id}")
    public String getByCategory(@PathVariable("id")Optional<Integer> id,
                                Model model,
                                @ModelAttribute("message")String message){
        Optional<Category> category = categoryRepository.findById(id.get());
        Pageable pageable = PageRequest.of(0, 12, Sort.by((Sort.Direction.ASC), "id"));
        Page<Product> resultPage = null;
        if (category.isPresent()){
            resultPage = productRepository.findAllByCategoryIDAndAvailable(category.get(), true, pageable);
        }else {
            resultPage = productRepository.findAllByAvailable(true, pageable);
        }
        if (resultPage.getTotalPages() > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, resultPage.getTotalPages())
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
            model.addAttribute("sortName", "name");
        }
        model.addAttribute("productsPage", resultPage);
        if (message.length() > 0) {
            model.addAttribute("message", message);
        }
        return "user/shop";
    }


    @GetMapping("/{id}")
    public String shopSingle(@PathVariable("id") Optional<Integer> id, Model model) {
        Product product = productRepository.getById(id.orElse(1));
        model.addAttribute("product", product);
        return "user/shop-single";
    }

    @ModelAttribute("allCategories")
    public List<Category> allCategories() {
        return categoryRepository.findAllByActivated(true);
    }

    @ModelAttribute("top3Product")
    public List<Product> top3Product() {
        Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "sell"));
        return productRepository.findAllByAvailable(true, pageable).toList();
    }
}
