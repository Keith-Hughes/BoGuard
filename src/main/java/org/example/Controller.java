package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.javalin.http.Context;
import io.javalin.http.Handler;

import java.io.FileWriter;

import java.io.IOException;
import java.util.*;

import java.util.*;


public class Controller {

    static List<JsonNode> productsData = new ArrayList<>();
    public static final Handler add_product = context -> {
        Map<String, List<JsonNode>> finData = Map.of("products", productsData);
        context.render("/addproduct.html", finData );
    };
    public  static  final Handler signupComplete= ctx -> {
        ctx.render("/signupComplete.html" );
    };
    public  static  final Handler dashboard= ctx -> {
        ctx.render("/dashboard.html" );
    };

    public static final Handler product_action = context -> {
        String manufacturer = context.formParamAsClass("manufacturer", String.class)
                .check(Objects::nonNull, "Manufacturer is required")
                .get();
        String manufactureDateTime = context.formParamAsClass("manufactureDateTime", String.class)
                .check(Objects::nonNull, "manufactureDateTime is required")
                .get();
        String expiryDate = context.formParamAsClass("expiryDate", String.class)
                .check(Objects::nonNull, "Manufacturer is required")
                .get();
        String productDescription = context.formParamAsClass("productDescription", String.class)
                .check(Objects::nonNull, "manufactureDateTime is required")
                .get();
        String locationManufactured = context.formParamAsClass("locationManufactured", String.class)
                .check(Objects::nonNull, "Manufacturer is required")
                .get();
        String batchNumber = context.formParamAsClass("batchNumber", String.class)
                .check(Objects::nonNull, "manufactureDateTime is required")
                .get();



        //get all params from form
        String hashMap=Web3Engine.addProduct(Web3Engine.web3j, Web3Engine.credentials, Web3Engine.contractAddress, manufacturer, manufactureDateTime, expiryDate, productDescription,locationManufactured,batchNumber );
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.createObjectNode()
                .put("manufacturer", manufacturer)
                .put("manufactureDateTime", manufactureDateTime)
                .put("expiryDate", expiryDate)
                .put("productDescription", productDescription)
                .put("locationManufactured", locationManufactured)
                .put("batchNumber", batchNumber)
                .put("hash", hashMap);
        productsData.add(jsonNode);
        QRCodeGenerator.generateQRCode(hashMap);
        System.out.println();
        context.result("Product set successfully!");


//        Map<String, Object> viewModel = Map.of("town", locationManufactured, "province", productDescription, "schedule", Schedule);

        //interact with API to get the schedules needed
        context.redirect(Routes.ADD_PRODUCT);
    };

    public static final Handler verfification = context ->{

        String hash = context.pathParam("hashcode");
        System.out.println(hash);
        Map<String, Object> viewModel;
        boolean verified = false;
        try{
        if (Web3Engine.confirmTransactionHash(hash, productsData)){

            for(JsonNode i: productsData){
                System.out.println(i.get("hash").asText());
                if(i.get("hash").asText().toLowerCase().strip().equalsIgnoreCase(hash.strip())){
                    System.out.println("True");
                    verified= true;
                    viewModel = Map.of("manufacturer", i.get("manufacturer"),
                                    "manufactureDateTime", i.get("manufactureDateTime"),
                                    "expiryDate", i.get("expiryDate"),
                                    "productDescription", i.get("productDescription"),
                                    "locationManufactured", i.get("locationManufactured"),
                                    "batchNumber", i.get("batchNumber"),
                                    "hash", i.get("hash"),
                                    "verified", verified);

                                    context.render("/verification.html", viewModel);

                                    break;

                                    }

            }

        }

        context.render("/verification.html", Map.of("verified", verified));
        }catch (NoSuchElementException e){
            viewModel = Map.of("verified", verified);
            context.render("/verification.html", viewModel);
        }
    };




    public static final Handler trendAnalysisHandler = context ->{
        BusinessData businessData = generateSampleData();

        context.render("/TrendAnalysis.html", Map.of("businessData", businessData));
    };

    private static BusinessData generateSampleData() throws IOException {
        // Generate or retrieve sample business data
        // ...
        BusinessData data = new BusinessData();
        List<Map<String, Map<String, Object>>> sampleData = Database.readJsonFile("src/database.json");

        data.setProductInfo(sampleData);
        data.setExpenses();
        data.setRevenue();
        return data;
    }
}
