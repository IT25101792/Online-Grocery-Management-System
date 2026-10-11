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

    // Services used to manage the cart, orders and product stock
    private final CartService
            cartService;


    private final OrderService
            orderService;


    private final ProductStockService
            productStockService;

// Constructor to connect the required services
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

  // Calculate the total number of items in the cart
    // This count can be displayed on the pages
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

    
// Open the main products page
    @GetMapping("/")
    public String products() {

        return "products";
    }


// Get the available stock of all products
    @GetMapping("/api/stock")
    @ResponseBody
    public Map<String, Integer>
    getStock() {

        return productStockService
                .getAllStock();
    }

 // Display the cart items and calculate the total price
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


// Add a selected product to the shopping cart
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

 // Update the quantity of a product already in the cart
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


// Remove a selected item from the shopping cart
    @PostMapping("/cart/remove")
    public String removeCartItem(

            @RequestParam String cartItemId
    ) {

        cartService.removeFromCart(
                cartItemId
        );


        return "redirect:/cart";
    }


    // Open the checkout page if the cart has items
    @GetMapping("/checkout")
    public String checkout(
            Model model
    ) {
// Do not allow checkout when the cart is empty
        if (
                cartService
                        .getCartItems()
                        .isEmpty()
        ) {

            return "redirect:/cart";
        }

// Pass cart details and total amount to the checkout page
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


 // Create a new order using the customer's ID
    @PostMapping("/orders/create")
    public String createOrder(

            @RequestParam String customerId
    ) {

        orderService.createOrder(
                customerId
        );


        return "redirect:/orders";
    }

 // Display all orders on the orders page
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

   // Change the status of an existing order
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

// Cancel an order using its order ID
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



// Delete an order from the system
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
