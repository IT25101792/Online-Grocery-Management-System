package com.grpproject.store.product.controller;

import com.grpproject.store.product.model.Product;
import com.grpproject.store.product.service.ProductService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    // READ: list + search
    @GetMapping
    public String list(@RequestParam(required = false) String keyword, Model model) {
        model.addAttribute("products", service.search(keyword));
        model.addAttribute("keyword", keyword);
        return "product/list";
    }

    // CREATE: show the form
    @GetMapping("/add")
    public String showAddForm() {
        return "product/add";
    }

    // CREATE: handle the form
    @PostMapping("/add")
    public String add(@RequestParam String type,
                      @RequestParam String name,
                      @RequestParam String category,
                      @RequestParam(defaultValue = "0") double price,
                      @RequestParam(defaultValue = "0") int stock,
                      @RequestParam(required = false)
                      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expiryDate,
                      @RequestParam(required = false) Integer shelfLifeMonths,
                      Model model, RedirectAttributes redirect) {
        try {
            Product p = service.addProduct(type, name, category, price, stock, expiryDate, shelfLifeMonths);
            redirect.addFlashAttribute("success", "Product " + p.getId() + " added successfully");
            return "redirect:/products";
        } catch (IllegalArgumentException e) {
            // show the form again with what the user typed
            model.addAttribute("error", e.getMessage());
            model.addAttribute("type", type);
            model.addAttribute("name", name);
            model.addAttribute("category", category);
            model.addAttribute("price", price);
            model.addAttribute("stock", stock);
            model.addAttribute("expiryDate", expiryDate);
            model.addAttribute("shelfLifeMonths", shelfLifeMonths);
            return "product/add";
        }
    }

    // UPDATE: show the form
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable String id, Model model, RedirectAttributes redirect) {
        Product product = service.getProduct(id);
        if (product == null) {
            redirect.addFlashAttribute("error", "Product not found: " + id);
            return "redirect:/products";
        }
        model.addAttribute("product", product);
        return "product/edit";
    }

    // UPDATE: handle the form
    @PostMapping("/edit/{id}")
    public String update(@PathVariable String id,
                         @RequestParam String name,
                         @RequestParam String category,
                         @RequestParam(defaultValue = "0") double price,
                         @RequestParam(defaultValue = "0") int stock,
                         @RequestParam(required = false)
                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expiryDate,
                         @RequestParam(required = false) Integer shelfLifeMonths,
                         Model model, RedirectAttributes redirect) {
        try {
            service.updateProduct(id, name, category, price, stock, expiryDate, shelfLifeMonths);
            redirect.addFlashAttribute("success", "Product " + id + " updated successfully");
            return "redirect:/products";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("product", service.getProduct(id));
            return "product/edit";
        }
    }

    // DELETE
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable String id, RedirectAttributes redirect) {
        try {
            service.deleteProduct(id);
            redirect.addFlashAttribute("success", "Product " + id + " deleted");
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/products";
    }
}