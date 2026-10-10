package com.grocery.cartorder.service;

import com.grocery.cartorder.model.CartItem;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CartService {

    private final Path cartFile =
            Paths.get(
                    "data",
                    "cart.txt"
            );


    private final ProductStockService
            productStockService;


    public CartService(
            ProductStockService productStockService
    ) {

        this.productStockService =
                productStockService;


        initializeFile();
    }


    /* ==================================================
       CREATE CART FILE
    ================================================== */

    private void initializeFile() {

        try {

            Files.createDirectories(
                    cartFile.getParent()
            );


            if (!Files.exists(cartFile)) {

                Files.createFile(
                        cartFile
                );
            }

        }
        catch (IOException e) {

            throw new RuntimeException(
                    "Unable to initialize cart file.",
                    e
            );
        }
    }


    /* ==================================================
       CREATE
       ADD PRODUCT TO CART
    ================================================== */

    public synchronized void addToCart(
            String productId,
            String productName,
            double price,
            int quantity
    ) {

        if (quantity <= 0) {

            throw new IllegalArgumentException(
                    "Quantity must be greater than zero."
            );
        }


        if (price < 0) {

            throw new IllegalArgumentException(
                    "Price cannot be negative."
            );
        }


        int availableStock =
                productStockService.getStock(
                        productId
                );


        if (availableStock <= 0) {

            throw new IllegalArgumentException(

                    productName
                            + " is out of stock."
            );
        }


        List<CartItem> cartItems =
                getCartItems();


        /*
         * CHECK WHETHER PRODUCT
         * IS ALREADY IN CART
         */

        for (CartItem item : cartItems) {

            if (
                    item.getProductId()
                            .equals(productId)
            ) {

                int newQuantity =
                        item.getQuantity()
                                + quantity;


                if (
                        newQuantity
                                > availableStock
                ) {

                    throw new IllegalArgumentException(

                            "Only "
                                    + availableStock
                                    + " "
                                    + productName
                                    + " items are available."
                    );
                }


                item.setQuantity(
                        newQuantity
                );


                saveCart(
                        cartItems
                );


                return;
            }
        }


        /*
         * NEW PRODUCT
         */

        if (
                quantity
                        > availableStock
        ) {

            throw new IllegalArgumentException(

                    "Only "
                            + availableStock
                            + " "
                            + productName
                            + " items are available."
            );
        }


        String cartItemId =
                "CART-"
                        + UUID
                        .randomUUID()
                        .toString()
                        .substring(0, 8);


        CartItem cartItem =
                new CartItem(

                        cartItemId,
                        productId,
                        productName,
                        price,
                        quantity
                );


        cartItems.add(
                cartItem
        );


        saveCart(
                cartItems
        );
    }


    /* ==================================================
       READ
       GET CART ITEMS
    ================================================== */

    public synchronized List<CartItem>
    getCartItems() {

        List<CartItem> cartItems =
                new ArrayList<>();


        try {

            List<String> lines =
                    Files.readAllLines(
                            cartFile,
                            StandardCharsets.UTF_8
                    );


            for (String line : lines) {

                if (line.isBlank()) {
                    continue;
                }


                String[] parts =
                        line.split(
                                "\\|",
                                -1
                        );


                if (parts.length == 5) {

                    String cartItemId =
                            parts[0];

                    String productId =
                            parts[1];

                    String productName =
                            parts[2];

                    double price =
                            Double.parseDouble(
                                    parts[3]
                            );

                    int quantity =
                            Integer.parseInt(
                                    parts[4]
                            );


                    CartItem item =
                            new CartItem(

                                    cartItemId,
                                    productId,
                                    productName,
                                    price,
                                    quantity
                            );


                    cartItems.add(
                            item
                    );
                }
            }

        }
        catch (IOException e) {

            throw new RuntimeException(
                    "Unable to read cart.",
                    e
            );
        }


        return cartItems;
    }


    /* ==================================================
       UPDATE
       CHANGE QUANTITY
    ================================================== */

    public synchronized void updateQuantity(
            String cartItemId,
            int quantity
    ) {

        if (quantity <= 0) {

            throw new IllegalArgumentException(
                    "Quantity must be greater than zero."
            );
        }


        List<CartItem> cartItems =
                getCartItems();


        boolean found = false;


        for (CartItem item : cartItems) {

            if (
                    item.getCartItemId()
                            .equals(cartItemId)
            ) {

                int availableStock =
                        productStockService.getStock(
                                item.getProductId()
                        );


                if (
                        quantity
                                > availableStock
                ) {

                    throw new IllegalArgumentException(

                            "Only "
                                    + availableStock
                                    + " "
                                    + item.getProductName()
                                    + " items are available."
                    );
                }


                item.setQuantity(
                        quantity
                );


                found = true;

                break;
            }
        }


        if (!found) {

            throw new IllegalArgumentException(
                    "Cart item not found."
            );
        }


        saveCart(
                cartItems
        );
    }


    /* ==================================================
       DELETE
       REMOVE PRODUCT
    ================================================== */

    public synchronized void removeFromCart(
            String cartItemId
    ) {

        List<CartItem> cartItems =
                getCartItems();


        cartItems.removeIf(

                item ->

                        item
                                .getCartItemId()
                                .equals(cartItemId)
        );


        saveCart(
                cartItems
        );
    }


    /* ==================================================
       CALCULATE TOTAL
    ================================================== */

    public double calculateTotal() {

        double total = 0;


        List<CartItem> cartItems =
                getCartItems();


        for (CartItem item : cartItems) {

            total +=
                    item.getSubtotal();
        }


        return total;
    }


    /* ==================================================
       CLEAR CART
    ================================================== */

    public synchronized void clearCart() {

        saveCart(
                new ArrayList<>()
        );
    }


    /* ==================================================
       SAVE CART
    ================================================== */

    private void saveCart(
            List<CartItem> cartItems
    ) {

        try {

            List<String> lines =
                    new ArrayList<>();


            for (CartItem item : cartItems) {

                String line =

                        item.getCartItemId()
                                + "|"

                                + item.getProductId()
                                + "|"

                                + safe(
                                item.getProductName()
                        )
                                + "|"

                                + item.getPrice()
                                + "|"

                                + item.getQuantity();


                lines.add(
                        line
                );
            }


            Files.write(
                    cartFile,
                    lines,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );

        }
        catch (IOException e) {

            throw new RuntimeException(
                    "Unable to save cart.",
                    e
            );
        }
    }


    /* ==================================================
       SAFE TEXT
    ================================================== */

    private String safe(
            String value
    ) {

        if (value == null) {

            return "";
        }


        return value.replace(
                "|",
                "/"
        );
    }
}