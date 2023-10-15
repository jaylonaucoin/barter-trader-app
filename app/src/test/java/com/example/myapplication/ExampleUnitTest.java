package com.example.myapplication;

import org.junit.Before;
<<<<<<< Updated upstream



import org.junit.BeforeClass;


import org.junit.BeforeClass;

=======
import org.junit.BeforeClass;
>>>>>>> Stashed changes
import org.junit.Test;

import static org.junit.Assert.*;

<<<<<<< Updated upstream

import androidx.annotation.Nullable;

import java.util.List;



=======
>>>>>>> Stashed changes
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;

import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

<<<<<<< Updated upstream

=======
>>>>>>> Stashed changes
/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {



    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

<<<<<<< Updated upstream

=======
>>>>>>> Stashed changes
    @Test
    public void testStringLength() {
        assertTrue(MainActivity.checkStringLength("I like a car", 5));
        assertFalse(MainActivity.checkStringLength("a car", 5));
    }

<<<<<<< Updated upstream

=======
>>>>>>> Stashed changes
}