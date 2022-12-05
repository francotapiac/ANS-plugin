package com.emcoders.ansplugin.controllers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChartControllerTest {

    @Test
    void format_number() {
        ChartController layoutController = new ChartController();
        String number = layoutController.format_number(3.13256);
        assertEquals("3.1326",number);
    }
}