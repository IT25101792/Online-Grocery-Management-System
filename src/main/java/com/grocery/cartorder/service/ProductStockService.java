package com.grocery.cartorder.service;

import com.grocery.cartorder.model.CartItem;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductStockService {

    private final Path stockFile =
            Paths.get("data", "stock.txt");


    public ProductStockService() {

        initializeStockFile();
    }


    //Create Stock file

    private void initializeStockFile() {

        try {

            Files.createDirectories(
                    stockFile.getParent()
            );


            if (!Files.exists(stockFile)) {

                List<String> defaultStock =
                        List.of(

                                // VEGETABLES
                                "V001|20",
                                "V002|15",
                                "V003|18",
                                "V004|5",

                                // FRUITS
                                "F001|12",
                                "F002|20",
                                "F003|15",
                                "F004|4",

                                // DAIRY
                                "D001|10",
                                "D002|15",
                                "D003|6",
                                "D004|9",

                                // SNACKS
                                "S001|20",
                                "S002|18",
                                "S003|12",
                                "S004|8"
                        );


                Files.write(
                        stockFile,
                        defaultStock,
                        StandardCharsets.UTF_8
                );
            }

        }
        catch (IOException e) {

            throw new RuntimeException(
                    "Unable to create stock file.",
                    e
            );
        }
    }


    //Read Stock file

    private Map<String, Integer> readStock() {

        Map<String, Integer> stock =
                new LinkedHashMap<>();


        try {

            List<String> lines =
                    Files.readAllLines(
                            stockFile,
                            StandardCharsets.UTF_8
                    );


            for (String line : lines) {

                if (line.isBlank()) {
                    continue;
                }


                String[] parts =
                        line.split("\\|");


                if (parts.length == 2) {

                    String productId =
                            parts[0];


                    int quantity =
                            Integer.parseInt(
                                    parts[1]
                            );


                    stock.put(
                            productId,
                            quantity
                    );
                }
            }


        }
        catch (IOException e) {

            throw new RuntimeException(
                    "Unable to read stock.",
                    e
            );
        }


        return stock;
    }


    //Read all stock

    public synchronized Map<String, Integer>
    getAllStock() {

        return readStock();
    }


    //Get one stock

    public synchronized int getStock(
            String productId
    ) {

        Map<String, Integer> stock =
                readStock();


        return stock.getOrDefault(
                productId,
                0
        );
    }


    //Check wheher enough stock available

    public synchronized boolean hasEnoughStock(
            String productId,
            int requestedQuantity
    ) {

        int availableStock =
                getStock(productId);


        return requestedQuantity > 0
                &&
                requestedQuantity <= availableStock;
    }


    //Validate cart before order

    public synchronized void validateOrderStock(
            List<CartItem> cartItems
    ) {

        Map<String, Integer> stock =
                readStock();


        for (CartItem item : cartItems) {

            int availableStock =
                    stock.getOrDefault(
                            item.getProductId(),
                            0
                    );


            if (availableStock <= 0) {

                throw new IllegalArgumentException(

                        item.getProductName()
                                + " is out of stock."
                );
            }


            if (
                    item.getQuantity()
                            > availableStock
            ) {

                throw new IllegalArgumentException(

                        item.getProductName()
                                + " only has "
                                + availableStock
                                + " items left."
                );
            }
        }
    }


    //Reduce stock after purchase

    public synchronized void reduceStockForOrder(
            List<CartItem> cartItems
    ) {

        Map<String, Integer> stock =
                readStock();


        /*
         * FIRST CHECK EVERY PRODUCT.
         *
         * This is done before reducing anything.
         */

        for (CartItem item : cartItems) {

            int availableStock =
                    stock.getOrDefault(
                            item.getProductId(),
                            0
                    );


            if (
                    item.getQuantity()
                            > availableStock
            ) {

                throw new IllegalArgumentException(

                        "Not enough stock for "
                                + item.getProductName()
                );
            }
        }


        /*
         * NOW REDUCE STOCK.
         */

        for (CartItem item : cartItems) {

            int currentStock =
                    stock.getOrDefault(
                            item.getProductId(),
                            0
                    );


            int newStock =
                    currentStock
                            - item.getQuantity();


            stock.put(
                    item.getProductId(),
                    newStock
            );
        }


        saveStock(stock);
    }


    //RESTORE STOCK Used if order saving fails.

    public synchronized void restoreStockForOrder(
            List<CartItem> cartItems
    ) {

        Map<String, Integer> stock =
                readStock();


        for (CartItem item : cartItems) {

            int currentStock =
                    stock.getOrDefault(
                            item.getProductId(),
                            0
                    );


            stock.put(

                    item.getProductId(),

                    currentStock
                            + item.getQuantity()
            );
        }


        saveStock(stock);
    }


    //Manually change stock

    public synchronized void updateStock(
            String productId,
            int newQuantity
    ) {

        if (newQuantity < 0) {

            throw new IllegalArgumentException(
                    "Stock cannot be negative."
            );
        }


        Map<String, Integer> stock =
                readStock();


        stock.put(
                productId,
                newQuantity
        );


        saveStock(stock);
    }


    //Save stock

    private void saveStock(
            Map<String, Integer> stock
    ) {

        try {

            List<String> lines =
                    stock
                            .entrySet()
                            .stream()
                            .map(entry ->

                                    entry.getKey()
                                            + "|"
                                            + entry.getValue()

                            )
                            .toList();


            Files.write(
                    stockFile,
                    lines,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );

        }
        catch (IOException e) {

            throw new RuntimeException(
                    "Unable to save stock.",
                    e
            );
        }
    }
}
