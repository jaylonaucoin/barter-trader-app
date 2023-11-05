package com.example.myapplication;

import org.junit.Before;

import org.junit.Test;

import java.util.ArrayList;


import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ValueSerUnitTest {
    private ExchangeCalculate calculator;
    private ArrayList<String> items;

    @Before
    public void setUp() {
        items = new ArrayList<>();
        calculator = new ExchangeCalculate(items, 0.0);
    }

    @Test
    public void testSellItem() {
        calculator.sellItem("Apple", 10.0);
        assertEquals(10.0, calculator.getTotalValue(), 0.001);
        assertEquals(1, items.size());
        assertEquals("Apple - 10.0", items.get(0));
    }
    @Test
    public void testBuyItem() {
        calculator.sellItem("Apple", 10.0);
        calculator.buyItem(0);
        assertEquals(0.0, calculator.getTotalValue(), 0.001);
        assertEquals(0, items.size());
    }

}