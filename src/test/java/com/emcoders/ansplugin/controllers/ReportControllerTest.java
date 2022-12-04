package com.emcoders.ansplugin.controllers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReportControllerTest {

    @Test
    void create_report_excel() {
    }

    @Test
    void read_xls() {
        ReportController reportController = new ReportController();
        assertEquals(null, reportController.read_xls("",true,1));
    }
}