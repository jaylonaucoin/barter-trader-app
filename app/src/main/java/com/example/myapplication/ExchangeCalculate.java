package com.example.myapplication;

import java.util.ArrayList;

public class ExchangeCalculate {
    //set the start value to 0
    private double totalValue = 0;
    //build array
    private ArrayList<String> items;

    public ExchangeCalculate(ArrayList<String> initialItems, double initialTotalValue) {
        this.items = initialItems;
        this.totalValue = initialTotalValue;
    }

    public void sellItem(String itemName, double itemValue) {
        //calculate the value
        totalValue += itemValue;
        items.add(itemName + " - " + itemValue);
    }

    public void buyItem(int position) {
        if (position != -1 && position < items.size()) {
            String selectedItem = items.get(position);
            String[] parts = selectedItem.split(" - ");
            double itemValue = Double.parseDouble(parts[1]);
            //calculate the value and remove from original position
            totalValue -= itemValue;
            items.remove(position);
        }
    }

    public double getTotalValue() {
        return totalValue;
    }
    //check is able to buy 
     public boolean AbleToBuy(int position) {
        if (position != -1 && position < items.size()) {
            String selectedItem = items.get(position);
            String[] parts = selectedItem.split(" - ");
            double itemValue = Double.parseDouble(parts[1]);
            return totalValue >= itemValue;
        }
        return false;
    }
}
