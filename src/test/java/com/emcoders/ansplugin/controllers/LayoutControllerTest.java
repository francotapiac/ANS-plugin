package com.emcoders.ansplugin.controllers;

import com.emcoders.ansplugin.models.Signal;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LayoutControllerTest {

    @Test
    void initialize_pane_signal() {
    }

    @Test
    void format_number() {
        LayoutController layoutController = new LayoutController();
        String number = layoutController.format_number(3.13256);
        assertEquals("3.1326",number);
    }
}