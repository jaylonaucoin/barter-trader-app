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
            double itemValue;

            try {
                // according to the product format
                if (parts.length == 2) {
                    // if upload in valuationSer, so will be product - value
                    itemValue = Double.parseDouble(parts[1]);
                } else if (parts.length > 3) {
                    // if upload in the postGoods, will be product product - description - preference - value
                    itemValue = Double.parseDouble(parts[3]);
                } else {
                    // print nothing
                    return;
                }
            } catch (NumberFormatException e) {
                // print nothing
                return;
            }
            // calculate the value and remove from original position
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
            double itemValue;

            if (parts.length == 2 || parts.length > 3) {
                //only the number
                String priceString = parts[parts.length - 1].replaceAll("[^\\d.]", "");
                try {
                    itemValue = Double.parseDouble(priceString);
                } catch (NumberFormatException e) {
                    return false;
                }

                return totalValue >= itemValue;
            }
        }
       return false;
    }
}
