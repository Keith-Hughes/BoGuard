package org.example;

import java.util.List;
import java.util.Map;

public class BusinessData {
    public double revenue= 0.0;
    public double expenses= 0.0;
    public  String advice="Cappy Fruit Juice Blend : Your customers seem to enjoy this product, buy more of it. ";
    public  String adviceDry="Dry lemon : Keep your supply steady.";
    public  String adviceTop="Toppers: reduce the supply";

    private List<Map<String,Map<String,Object>>> productInfo;

    
    public List<Map<String,Map<String,Object>>> getProductInfo() {
        return productInfo;
    }

    public void setProductInfo(List<Map<String, Map<String, Object>>> productInfo) {
        this.productInfo = productInfo;
    }

    public double getExpenses() {

        return expenses;
    }

    public void setExpenses() {

        for(Map<String, Map<String, Object>> entry : productInfo){
            for(Map.Entry<String, Map<String, Object>> innerEntry : entry.entrySet() ){
                Map<String, Object> innerMap = innerEntry.getValue();
                this.expenses += Integer.parseInt(String.valueOf(innerMap.get("cost price")));
            }
        }



    }

    public double getRevenue() {
        return revenue;
    }

    public void setRevenue() {

        for(Map<String, Map<String, Object>> entry : productInfo){
            for(Map.Entry<String, Map<String, Object>> innerEntry : entry.entrySet() ){
                Map<String, Object> innerMap = innerEntry.getValue();
                this.revenue += Integer.parseInt(String.valueOf(innerMap.get("amount")))- Integer.parseInt(String.valueOf(innerMap.get("cost price")));
            }
        }

    }
    // Other relevant metrics


}

