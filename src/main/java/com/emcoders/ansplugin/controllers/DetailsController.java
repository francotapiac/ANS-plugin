package com.emcoders.ansplugin.controllers;
import com.emcoders.ansplugin.models.SegmentSignal;
import javafx.scene.control.TableView;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class DetailsController {
    Workbook workbook;
    Sheet spreadsheet;

    public void create_report_excel(TableView<SegmentSignal> table_detail){
        workbook = new HSSFWorkbook();
        spreadsheet = workbook.createSheet("sample");

        Row row = spreadsheet.createRow(0);
        // Agregando nombre de columnas
        for (int j = 0; j < table_detail.getColumns().size(); j++) {
            row.createCell(j).setCellValue(table_detail.getColumns().get(j).getText());
        }

        //Agregando valores de la tabla
        for (int i = 0; i < table_detail.getItems().size(); i++) {
            row = spreadsheet.createRow(i + 1);
            for (int j = 0; j < table_detail.getColumns().size(); j++) {
                if(table_detail.getColumns().get(j).getCellData(i) != null) {
                    row.createCell(j).setCellValue(table_detail.getColumns().get(j).getCellData(i).toString());
                }
                else {
                    row.createCell(j).setCellValue("");
                }
            }
        }
        try {
            FileOutputStream fileOut = new FileOutputStream("C:\\Users\\Franco\\Desktop\\Escritorio\\workbook.xls");
            workbook.write(fileOut);
            fileOut.close();
        }  catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
