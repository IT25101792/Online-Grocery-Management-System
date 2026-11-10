package com.grpproject.store.product.controller;

import com.grpproject.store.product.model.NonPerishableProduct;
import com.grpproject.store.product.model.PerishableProduct;
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
    public String listProducts(@RequestParam(required = false) String keyword, Model model) {
        model.addAttribute("products", service.searchProducts(keyword));
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
    public String addProduct(@RequestParam String type,
                             @RequestParam String name,
                             @RequestParam String category,
                             @RequestParam(defaultValue = "0") double price,
                             @RequestParam(defaultValue = "0") int stockQty,
                             @RequestParam(defaultValue = "0") int reorderLevel,
                             @RequestParam(required = false)
                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expiryDate,
                             @RequestParam(required = false) String packagingType,
                             Model model, RedirectAttributes redirect) {
        try {
            // id 0 is a placeholder, the service generates the real one
            Product p = buildProduct(type, 0, name, category, price, stockQty,
                    reorderLevel, expiryDate, packagingType);
            service.addProduct(p);
            redirect.addFlashAttribute("success", "Product " + p.getProductId() + " added successfully");
            return "redirect:/products";
        } catch (IllegalArgumentException e) {
            // show the form again with what the user typed
            model.addAttribute("error", e.getMessage());
            model.addAttribute("type", type);
            model.addAttribute("name", name);
            model.addAttribute("category", category);
            model.addAttribute("price", price);
            model.addAttribute("stockQty", stockQty);
            model.addAttribute("reorderLevel", reorderLevel);
            model.addAttribute("expiryDate", expiryDate);
            model.addAttribute("packagingType", packagingType);
            return "product/add";
        }
    }

    // UPDATE: show the form
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable int id, Model model, RedirectAttributes redirect) {
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
    public String editProduct(@PathVariable int id,
                              @RequestParam String name,
                              @RequestParam String category,
                              @RequestParam(defaultValue = "0") double price,
                              @RequestParam(defaultValue = "0") int stockQty,
                              @RequestParam(defaultValue = "0") int reorderLevel,
                              @RequestParam(required = false)
                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expiryDate,
                              @RequestParam(required = false) String packagingType,
                              Model model, RedirectAttributes redirect) {
        Product existing = service.getProduct(id);
        if (existing == null) {
            redirect.addFlashAttribute("error", "Product not found: " + id);
            return "redirect:/products";
        }
        try {
            // the type can't change when editing, so reuse the existing one
            Product updated = buildProduct(existing.getProductType(), id, name, category,
                    price, stockQty, reorderLevel, expiryDate, packagingType);
            service.updateProduct(updated);
            redirect.addFlashAttribute("success", "Product " + id + " updated successfully");
            return "redirect:/products";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("product", existing);
            return "product/edit";
        }
    }

    // DELETE
    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable int id, RedirectAttributes redirect) {
        try {
            service.deleteProduct(id);
            redirect.addFlashAttribute("success", "Product " + id + " deleted");
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/products";
    }

    // Builds the right subclass depending on the type (polymorphism)
    private Product buildProduct(String type, int id, String name, String category,
                                 double price, int stockQty, int reorderLevel,
                                 LocalDate expiryDate, String packagingType) {
        if ("PERISHABLE".equals(type)) {
            return new PerishableProduct(id, name, category, price, stockQty, reorderLevel, expiryDate);
        } else if ("NON_PERISHABLE".equals(type)) {
            return new NonPerishableProduct(id, name, category, price, stockQty, reorderLevel, packagingType);
        }
        throw new IllegalArgumentException("Please choose a product type");
    }
}