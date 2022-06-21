package com.assignmentjava.controller.admin;

import com.assignmentjava.dto.ProductDTO;
import com.assignmentjava.model.Category;
import com.assignmentjava.model.Product;
import com.assignmentjava.repository.CategoryRepository;
import com.assignmentjava.repository.ProductRepository;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("admin/products-manager")
public class ProductController {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    FileSystemStorageServices systemStorageServices;

    @Autowired
    CategoryRepository categoryRepository;



    boolean sortIndex = true;

    @GetMapping("/images/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> getImage(@PathVariable String filename) throws Exception {
        Resource file = systemStorageServices.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment: filename=\"" + file.getFilename() + "\"").body(file);
    }

    @GetMapping("")
    public String getView(@ModelAttribute("message") String message, Model model) {
        //truy van va sap xep
        Pageable pageable = PageRequest.of(0, 10, Sort.by((Sort.Direction.ASC), "id"));
        Page<Product> resultPage = productRepository.findAll(pageable);
        if (resultPage.getTotalPages() > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, resultPage.getTotalPages())
                    .boxed()
                    .collect(Collectors.toList());

            model.addAttribute("pageNumbers", pageNumbers);
        }
        model.addAttribute("productPage", resultPage);
        if (message.length() > 0) {
            model.addAttribute("message", message);
        }
        return "admin/product/product-manager";
    }

    @GetMapping("/sort")
    public String sortAccount(@RequestParam("page") Optional<Integer> page,
                              @RequestParam("size") Optional<Integer> size,
                              @RequestParam("sortby") Optional<String> sortType,
                              @RequestParam("sort") Optional<Boolean> sort,
                              Model model,
                              @ModelAttribute("message") String message) {
        int curentPage = page.orElse(1);
        int pageSize = size.orElse(10);
        String sortName = sortType.orElse("id");
        Page<Product> resultPage = null;
        //truy van va sap xep
        Pageable pageable = PageRequest.of(curentPage - 1, pageSize, Sort.by((sortIndex ? Sort.Direction.DESC : Sort.Direction.ASC), sortName));
        resultPage = productRepository.findAll(pageable);
        boolean sortPage = sort.orElse(true);
        if (sortPage) sortIndex = !sortIndex;
        if (resultPage.getTotalPages() > 0) {
            //gender số trang ra các trang nhỏ
            List<Integer> pageNumbers = IntStream.rangeClosed(1, resultPage.getTotalPages())
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        model.addAttribute("productPage", resultPage);
        if (message.length() > 0) {
            model.addAttribute("message", message);
        }
        return "admin/product/product-manager";
    }

    @GetMapping("/lock/{id}")
    public String deleteAccount(@PathVariable("id") Integer id, RedirectAttributes redirect) {
        Product product = productRepository.getById(id);

        product.setAvailable(!product.getAvailable());
        Product productUpdate = productRepository.save(product);
        if (productUpdate.getAvailable()) {
            redirect.addFlashAttribute("message", "Mở khóa sản phẩm \"" + productUpdate.getName() + "\" thành công!");
        } else {
            redirect.addFlashAttribute("message", "Khóa sản phẩm \"" + productUpdate.getName() + "\" thành công!");
        }
        return "redirect:/admin/products-manager";
    }

    @GetMapping("add")
    public String add(@ModelAttribute("message") String message, Model model) {
        if (message.length() > 0) {
            System.out.println("message " + message + " / " + message.length());
            model.addAttribute("message", message);
        }
        ProductDTO dto = new ProductDTO();
        dto.setEdit(false);
        model.addAttribute("product", dto);
        return "/admin/product/add-product";
    }

    @GetMapping("edit/{id}")
    public String edit(@ModelAttribute("message") String message, Model model, @PathVariable("id") Integer id) {
        if (message.length() > 0) {
            model.addAttribute("message", message);
        }
        Product product = productRepository.getById(id);
        ProductDTO dto = new ProductDTO();
        dto.setEdit(true);
        BeanUtils.copyProperties(product, dto);
        model.addAttribute("product", dto);
        return "/admin/product/add-product";
    }

    @PostMapping("addOrEdit")
    public String addOrEdit(@Valid @ModelAttribute("product") ProductDTO dto,
                            RedirectAttributes redirect) {
        Product product = new Product();
        BeanUtils.copyProperties(dto, product);
        product.setCreateDate(LocalDate.now());
        product.setCategoryID(categoryRepository.getById(dto.getCategoryID()));
        if (!dto.getImageFile().isEmpty()) {
            String uuid = UUID.randomUUID().toString();
            product.setImage(systemStorageServices.getStoredFileName(dto.getImageFile(), uuid));
            try {
                systemStorageServices.store(dto.getImageFile(), product.getImage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (dto.getImageFile().isEmpty() && dto.getImage() != null) {
            product.setImage(dto.getImage());
        } else {
            redirect.addFlashAttribute("message", "Bạn chưa chọn ảnh cho sản phẩm");
            return "redirect:/admin/products-manager/adđ";
        }
        productRepository.save(product);
        redirect.addFlashAttribute("message", "Thêm sản phẩm " + dto.getName() + " thành công!");
        return "redirect:/admin/products-manager";
    }

    @ModelAttribute("categories")
    public List<Category> getALlCategories(){
        return categoryRepository.findAll();
    }
}
