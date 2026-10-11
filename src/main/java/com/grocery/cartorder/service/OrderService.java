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

     // Create the orders file if it is not available
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


    // Create an order from the items in the cart
    public synchronized Order createOrder(
            String customerId
    ) {

        List<CartItem> cartItems =
                cartService.getCartItems();

        // Check whether the cart is empty
        if (cartItems.isEmpty()) {

            throw new IllegalArgumentException(
                    "Your cart is empty."
            );
        }


        // Check whether the products have enough stock
        productStockService
                .validateOrderStock(
                        cartItems
                );


        // Move the cart item details into the order
        List<OrderItem> orderItems =
                new ArrayList<>();

        // Move the cart item details into the order
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


        // Generate a new ID for the order
        String orderId =
                "ORD-"
                        + UUID
                        .randomUUID()
                        .toString()
                        .substring(0, 8);


        // Get the total amount from the cart
        double total =
                cartService.calculateTotal();


        // Create the order with the current date and initial status
        Order order =
                new Order(

                        orderId,

                        customerId,

                        orderItems,

                        total,

                        LocalDateTime.now(),

                        "PENDING"
                );


        // Reduce the purchased quantities from the stock
        productStockService
                .reduceStockForOrder(
                        cartItems
                );


        // Save the order into the text file
        try {

            saveOrder(
                    order
            );

        }
        catch (RuntimeException e) {

            // Give the stock back if the order cannot be saved
            productStockService
                    .restoreStockForOrder(
                            cartItems
                    );


            throw e;
        }


        // Empty the cart after placing the order
        cartService.clearCart();


        return order;
    }


    //   Read all Orders
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


    //Read get order by ID

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


    //Update order status
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


    //Update cancel Order
    public void cancelOrder(
            String orderId
    ) {

        updateOrderStatus(
                orderId,
                "CANCELLED"
        );
    }


    //Delete Order
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


    //Save one order
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


    //Save all orders
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


    //Convert order to text
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


    //Convert Text to order
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


    //Safe text
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
