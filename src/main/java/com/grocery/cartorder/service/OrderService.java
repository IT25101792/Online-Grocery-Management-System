package com.grocery.cartorder.service;

import com.grocery.cartorder.model.CartItem;
import com.grocery.cartorder.model.Order;
import com.grocery.cartorder.model.OrderItem;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final Path ordersFile =
            Paths.get(
                    "data",
                    "orders.txt"
            );


    private final CartService
            cartService;


    private final ProductStockService
            productStockService;


    public OrderService(
            CartService cartService,
            ProductStockService productStockService
    ) {

        this.cartService =
                cartService;


        this.productStockService =
                productStockService;


        initializeFile();
    }


    /* ==================================================
       CREATE ORDER FILE
    ================================================== */

    private void initializeFile() {

        try {

            Files.createDirectories(
                    ordersFile.getParent()
            );


            if (!Files.exists(ordersFile)) {

                Files.createFile(
                        ordersFile
                );
            }

        }
        catch (IOException e) {

            throw new RuntimeException(
                    "Unable to initialize orders file.",
                    e
            );
        }
    }


    /* ==================================================
       CREATE ORDER
    ================================================== */

    public synchronized Order createOrder(
            String customerId
    ) {

        List<CartItem> cartItems =
                cartService.getCartItems();


        if (cartItems.isEmpty()) {

            throw new IllegalArgumentException(
                    "Your cart is empty."
            );
        }


        /*
         * STEP 1
         * CHECK STOCK
         */

        productStockService
                .validateOrderStock(
                        cartItems
                );


        /*
         * STEP 2
         * CONVERT CART ITEMS
         * TO ORDER ITEMS
         */

        List<OrderItem> orderItems =
                new ArrayList<>();


        for (CartItem cartItem : cartItems) {

            OrderItem orderItem =
                    new OrderItem(

                            cartItem.getProductId(),

                            cartItem.getProductName(),

                            cartItem.getPrice(),

                            cartItem.getQuantity()
                    );


            orderItems.add(
                    orderItem
            );
        }


        /*
         * STEP 3
         * CREATE ORDER ID
         */

        String orderId =
                "ORD-"
                        + UUID
                        .randomUUID()
                        .toString()
                        .substring(0, 8);


        /*
         * STEP 4
         * CALCULATE TOTAL
         */

        double total =
                cartService.calculateTotal();


        /*
         * STEP 5
         * CREATE ORDER OBJECT
         */

        Order order =
                new Order(

                        orderId,

                        customerId,

                        orderItems,

                        total,

                        LocalDateTime.now(),

                        "PENDING"
                );


        /*
         * STEP 6
         * REDUCE STOCK
         *
         * This is the actual purchase.
         */

        productStockService
                .reduceStockForOrder(
                        cartItems
                );


        /*
         * STEP 7
         * SAVE ORDER
         */

        try {

            saveOrder(
                    order
            );

        }
        catch (RuntimeException e) {

            /*
             * If saving order fails,
             * return stock back.
             */

            productStockService
                    .restoreStockForOrder(
                            cartItems
                    );


            throw e;
        }


        /*
         * STEP 8
         * CLEAR CART
         */

        cartService.clearCart();


        return order;
    }


    /* ==================================================
       READ
       GET ALL ORDERS
    ================================================== */

    public synchronized List<Order>
    getAllOrders() {

        List<Order> orders =
                new ArrayList<>();


        try {

            List<String> lines =
                    Files.readAllLines(
                            ordersFile,
                            StandardCharsets.UTF_8
                    );


            for (String line : lines) {

                if (line.isBlank()) {
                    continue;
                }


                Order order =
                        parseOrder(
                                line
                        );


                if (order != null) {

                    orders.add(
                            order
                    );
                }
            }

        }
        catch (IOException e) {

            throw new RuntimeException(
                    "Unable to read orders.",
                    e
            );
        }


        return orders;
    }


    /* ==================================================
       READ
       GET ORDER BY ID
    ================================================== */

    public Order getOrderById(
            String orderId
    ) {

        List<Order> orders =
                getAllOrders();


        for (Order order : orders) {

            if (
                    order
                            .getOrderId()
                            .equals(orderId)
            ) {

                return order;
            }
        }


        return null;
    }


    /* ==================================================
       UPDATE
       ORDER STATUS
    ================================================== */

    public synchronized void updateOrderStatus(
            String orderId,
            String newStatus
    ) {

        if (
                newStatus == null
                        ||
                        newStatus.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "Order status is required."
            );
        }


        List<Order> orders =
                getAllOrders();


        boolean found = false;


        for (Order order : orders) {

            if (
                    order
                            .getOrderId()
                            .equals(orderId)
            ) {

                order.setStatus(
                        newStatus.toUpperCase()
                );


                found = true;

                break;
            }
        }


        if (!found) {

            throw new IllegalArgumentException(
                    "Order not found."
            );
        }


        saveAllOrders(
                orders
        );
    }


    /* ==================================================
       UPDATE
       CANCEL ORDER
    ================================================== */

    public void cancelOrder(
            String orderId
    ) {

        updateOrderStatus(
                orderId,
                "CANCELLED"
        );
    }


    /* ==================================================
       DELETE
       DELETE ORDER
    ================================================== */

    public synchronized void deleteOrder(
            String orderId
    ) {

        List<Order> orders =
                getAllOrders();


        orders.removeIf(

                order ->

                        order
                                .getOrderId()
                                .equals(orderId)
        );


        saveAllOrders(
                orders
        );
    }


    /* ==================================================
       SAVE ONE ORDER
    ================================================== */

    private void saveOrder(
            Order order
    ) {

        try {

            Files.write(
                    ordersFile,

                    List.of(
                            convertOrderToLine(
                                    order
                            )
                    ),

                    StandardCharsets.UTF_8,

                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );

        }
        catch (IOException e) {

            throw new RuntimeException(
                    "Unable to save order.",
                    e
            );
        }
    }


    /* ==================================================
       SAVE ALL ORDERS
    ================================================== */

    private void saveAllOrders(
            List<Order> orders
    ) {

        try {

            List<String> lines =
                    new ArrayList<>();


            for (Order order : orders) {

                lines.add(
                        convertOrderToLine(
                                order
                        )
                );
            }


            Files.write(
                    ordersFile,
                    lines,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );

        }
        catch (IOException e) {

            throw new RuntimeException(
                    "Unable to save orders.",
                    e
            );
        }
    }


    /* ==================================================
       CONVERT ORDER TO TEXT
    ================================================== */

    private String convertOrderToLine(
            Order order
    ) {

        List<String> itemStrings =
                new ArrayList<>();


        for (OrderItem item : order.getItems()) {

            String itemText =

                    safe(
                            item.getProductId()
                    )

                            + "~"

                            + safe(
                            item.getProductName()
                    )

                            + "~"

                            + item.getPrice()

                            + "~"

                            + item.getQuantity();


            itemStrings.add(
                    itemText
            );
        }


        String itemsText =
                String.join(
                        ";",
                        itemStrings
                );


        return

                safe(
                        order.getOrderId()
                )

                        + "|"

                        + safe(
                        order.getCustomerId()
                )

                        + "|"

                        + order.getOrderDate()

                        + "|"

                        + safe(
                        order.getStatus()
                )

                        + "|"

                        + order.getTotalAmount()

                        + "|"

                        + itemsText;
    }


    /* ==================================================
       CONVERT TEXT TO ORDER
    ================================================== */

    private Order parseOrder(
            String line
    ) {

        try {

            String[] parts =
                    line.split(
                            "\\|",
                            -1
                    );


            if (parts.length != 6) {

                return null;
            }


            String orderId =
                    parts[0];


            String customerId =
                    parts[1];


            LocalDateTime orderDate =
                    LocalDateTime.parse(
                            parts[2]
                    );


            String status =
                    parts[3];


            double total =
                    Double.parseDouble(
                            parts[4]
                    );


            List<OrderItem> orderItems =
                    new ArrayList<>();


            String itemsText =
                    parts[5];


            if (!itemsText.isBlank()) {

                String[] itemParts =
                        itemsText.split(
                                ";"
                        );


                for (String itemPart : itemParts) {

                    String[] fields =
                            itemPart.split(
                                    "~",
                                    -1
                            );


                    if (fields.length == 4) {

                        OrderItem item =
                                new OrderItem(

                                        fields[0],

                                        fields[1],

                                        Double.parseDouble(
                                                fields[2]
                                        ),

                                        Integer.parseInt(
                                                fields[3]
                                        )
                                );


                        orderItems.add(
                                item
                        );
                    }
                }
            }


            return new Order(

                    orderId,

                    customerId,

                    orderItems,

                    total,

                    orderDate,

                    status
            );


        }
        catch (Exception e) {

            return null;
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


        return value

                .replace(
                        "|",
                        "/"
                )

                .replace(
                        ";",
                        ","
                )

                .replace(
                        "~",
                        "-"
                );
    }
}