package com.grocery.cartorder.controller;

import com.grocery.cartorder.model.CartItem;
import com.grocery.cartorder.service.CartService;
import com.grocery.cartorder.service.OrderService;
import com.grocery.cartorder.service.ProductStockService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class CartOrderController {

    private final CartService
            cartService;


    private final OrderService
            orderService;


    private final ProductStockService
            productStockService;


    public CartOrderController(
            CartService cartService,
            OrderService orderService,
            ProductStockService productStockService
    ) {

        this.cartService =
                cartService;


        this.orderService =
                orderService;


        this.productStockService =
                productStockService;
    }


    @ModelAttribute("cartCount")
    public int cartCount() {

        return cartService
                .getCartItems()
                .stream()
                .mapToInt(
                        CartItem::getQuantity
                )
                .sum();
    }

    

    @GetMapping("/")
    public String products() {

        return "products";
    }



    @GetMapping("/api/stock")
    @ResponseBody
    public Map<String, Integer>
    getStock() {

        return productStockService
                .getAllStock();
    }


    @GetMapping("/cart")
    public String cart(
            Model model
    ) {

        model.addAttribute(
                "cartItems",
                cartService.getCartItems()
        );


        model.addAttribute(
                "total",
                cartService.calculateTotal()
        );


        return "cart";
    }



    @PostMapping("/cart/add")
    public String addToCart(

            @RequestParam String productId,

            @RequestParam String productName,

            @RequestParam double price,

            @RequestParam int quantity
    ) {

        cartService.addToCart(

                productId,

                productName,

                price,

                quantity
        );


        return "redirect:/cart";
    }


    @PostMapping("/cart/update")
    public String updateCart(

            @RequestParam String cartItemId,

            @RequestParam int quantity
    ) {

        cartService.updateQuantity(

                cartItemId,

                quantity
        );


        return "redirect:/cart";
    }



    @PostMapping("/cart/remove")
    public String removeCartItem(

            @RequestParam String cartItemId
    ) {

        cartService.removeFromCart(
                cartItemId
        );


        return "redirect:/cart";
    }



    @GetMapping("/checkout")
    public String checkout(
            Model model
    ) {

        if (
                cartService
                        .getCartItems()
                        .isEmpty()
        ) {

            return "redirect:/cart";
        }


        model.addAttribute(
                "cartItems",
                cartService.getCartItems()
        );


        model.addAttribute(
                "total",
                cartService.calculateTotal()
        );


        return "checkout";
    }



    @PostMapping("/orders/create")
    public String createOrder(

            @RequestParam String customerId
    ) {

        orderService.createOrder(
                customerId
        );


        return "redirect:/orders";
    }


    @GetMapping("/orders")
    public String orders(
            Model model
    ) {

        model.addAttribute(
                "orders",
                orderService.getAllOrders()
        );


        return "orders";
    }


    @PostMapping(
            "/orders/{orderId}/status"
    )
    public String updateOrderStatus(

            @PathVariable
            String orderId,

            @RequestParam
            String status
    ) {

        orderService.updateOrderStatus(

                orderId,

                status
        );


        return "redirect:/orders";
    }


    @PostMapping(
            "/orders/{orderId}/cancel"
    )
    public String cancelOrder(

            @PathVariable
            String orderId
    ) {

        orderService.cancelOrder(
                orderId
        );


        return "redirect:/orders";
    }




    @PostMapping(
            "/orders/{orderId}/delete"
    )
    public String deleteOrder(

            @PathVariable
            String orderId
    ) {

        orderService.deleteOrder(
                orderId
        );


        return "redirect:/orders";
    }
}
