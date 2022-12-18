package com.emcoders.ansplugin.controllers;
import com.emcoders.ansplugin.models.Alert;
import com.emcoders.ansplugin.models.Feature;
import com.emcoders.ansplugin.models.SegmentSignal;
import com.emcoders.ansplugin.models.Signal;
import javafx.scene.control.TableView;
import javafx.util.Pair;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ReportController {
    Workbook workbook;
    Sheet spreadsheet;
    Sheet signal;
    Sheet event;

    public void create_time_line_excel(List<Pair<Alert,Feature>> original_time_line, String path_dir, int type){
        Workbook workbook = new HSSFWorkbook();
        spreadsheet = workbook.createSheet("Particiones Señal Procesada");
        Row row = spreadsheet.createRow(0);
        row.createCell(0).setCellValue("Segmento");
        row.createCell(1).setCellValue("Tiempo Inicial (s)");
        row.createCell(2).setCellValue("Tiempo Final (s)");
        row.createCell(3).setCellValue("Emoción");
        row.createCell(4).setCellValue("Coherencia Cardíaca");
        row.createCell(5).setCellValue("Alerta");
        row.createCell(6).setCellValue("Valor Coherencia");
        row.createCell(7).setCellValue("Muy Baja Frecuencia (VLF)");
        row.createCell(8).setCellValue("Baja Frecuencia (LF)");
        row.createCell(9).setCellValue("Alta Frecuencia (HF)");
        row.createCell(10).setCellValue("LF/HF");
        row.createCell(11).setCellValue("FF Total");
        row.createCell(12).setCellValue("hr_mean");
        row.createCell(13).setCellValue("hr_min");
        row.createCell(14).setCellValue("hr_max");
        row.createCell(15).setCellValue("sdnn");
        row.createCell(16).setCellValue("rmssd");
        row.createCell(17).setCellValue("sdsd");
        int cont = 0;

        for (Pair<Alert, Feature> element: original_time_line) {
            row = spreadsheet.createRow(cont + 1);
            row.createCell(0).setCellValue(cont + 1);
            row.createCell(1).setCellValue(element.getKey().getStart_time());
            row.createCell(2).setCellValue(element.getKey().getEnd_time());
            row.createCell(3).setCellValue(element.getKey().getEmotion().getName());
            row.createCell(4).setCellValue(element.getKey().getDescription());
            row.createCell(5).setCellValue(element.getKey().getText_alert());
            row.createCell(6).setCellValue(element.getKey().getRatio_coherence());
            for(Pair<String, Float> freq: element.getValue().getFrequency_feature()){
                if(freq.getKey().equals("vlf"))
                    row.createCell(7).setCellValue(freq.getValue());
                else if (freq.getKey().equals("lf")) {  row.createCell(8).setCellValue(freq.getValue());}
                else if (freq.getKey().equals("hf")) {  row.createCell(9).setCellValue(freq.getValue());}
                else if (freq.getKey().equals("lf-hf")) {  row.createCell(10).setCellValue(freq.getValue());}
                else if (freq.getKey().equals("fft_total")) {  row.createCell(11).setCellValue(freq.getValue());}
            }
            for(Pair<String, Float> time: element.getValue().getTimes_feature()) {
                if(time.getKey().equals("hr_mean"))
                    row.createCell(12).setCellValue(time.getValue());
                else if (time.getKey().equals("hr_min")) {row.createCell(13).setCellValue(time.getValue());}
                else if (time.getKey().equals("hr_max")) {row.createCell(14).setCellValue(time.getValue());}
                else if (time.getKey().equals("sdnn")) {row.createCell(15).setCellValue(time.getValue());}
                else if (time.getKey().equals("rmssd")) {row.createCell(16).setCellValue(time.getValue());}
                else if (time.getKey().equals("sdsd")) {row.createCell(17).setCellValue(time.getValue());}
            }
            cont++;
        }
        String name_document = "senalAnalisis";
        if(type == 1)
            name_document = "senalParticiones";

        try {
            FileOutputStream fileOut = new FileOutputStream(path_dir + "/" + name_document + ".xls");
            workbook.write(fileOut);
            fileOut.close();
        }  catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void create_signal_excel(List<Double> fci, List<Double> times_fci, String path_dir){
        Workbook workbook = new HSSFWorkbook();
        signal = workbook.createSheet("Señal");
        Row row_signal = signal.createRow(0);
        //Agregando valores para la señal fci y sus tiempos
        row_signal.createCell(0).setCellValue("FCI");
        row_signal.createCell(1).setCellValue("Time");
        for(int k = 0; k < fci.size(); k++ ){
            row_signal = signal.createRow(k + 1);
            row_signal.createCell(0).setCellValue(fci.get(k).toString());
            row_signal.createCell(1).setCellValue(times_fci.get(k).toString());
        }
        try {
            FileOutputStream fileOut = new FileOutputStream(path_dir + "/" + "fciPoints" + ".xls");
            workbook.write(fileOut);
            fileOut.close();
        }  catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void create_alert_excel(List<Pair<Alert,Feature>> original_time_line, Signal signal_model, String path_dir){
        Workbook workbook = new HSSFWorkbook();
        signal = workbook.createSheet("Alertas");
        Row row_event = signal.createRow(0);
        //Agregando valores para los eventos
        row_event.createCell(0).setCellValue("Tiempo Inicial (s)");
        row_event.createCell(1).setCellValue("Tiempo Final (s)");
        row_event.createCell(2).setCellValue("Coherencia Cardíaca");
        row_event.createCell(3).setCellValue("Emoción");
        row_event.createCell(4).setCellValue("Tipo de Evento");
        int count_event_signal = 0;
        for(int m = 0; m < signal_model.getTime_line().size(); m++){
            if(signal_model.getTime_line().get(m).getKey().getType_alert() == 1 || signal_model.getTime_line().get(m).getKey().getType_alert() == 2 ){
                row_event = event.createRow(count_event_signal + 1);
                row_event.createCell(0).setCellValue(signal_model.getTime_line().get(m).getValue().getStart_time());
                row_event.createCell(1).setCellValue(signal_model.getTime_line().get(m).getValue().getEnd_time());
                row_event.createCell(2).setCellValue(signal_model.getTime_line().get(m).getKey().getDescription());
                row_event.createCell(3).setCellValue(signal_model.getTime_line().get(m).getKey().getEmotion().getName());
                row_event.createCell(4).setCellValue(signal_model.getTime_line().get(m).getKey().getText_alert());
                count_event_signal++;
            }
        }
        try {
            FileOutputStream fileOut = new FileOutputStream(path_dir + "/" + "alerts" + ".xls");
            workbook.write(fileOut);
            fileOut.close();
        }  catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
    public void create_report_excel(TableView<SegmentSignal> table_detail, List<Double> fci, List<Double> times_fci, Signal signal_model, String path_dir, String name_path_signal){
        workbook = new HSSFWorkbook();
        spreadsheet = workbook.createSheet("Resumen de analisis");
        signal = workbook.createSheet("Señal");
        event = workbook.createSheet("Event");

        Row row = spreadsheet.createRow(0);
        Row row_signal = signal.createRow(0);
        Row row_event = event.createRow(0);

        // Agregando nombre de columnas
        for (int j = 0; j < table_detail.getColumns().size(); j++) {
            row.createCell(j).setCellValue(table_detail.getColumns().get(j).getText());
        }


        //Agregando valores de la tabla para las características principal
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
        //Agregando valores para la señal fci y sus tiempos
        row_signal.createCell(0).setCellValue("FCI");
        row_signal.createCell(1).setCellValue("Time");
        for(int k = 0; k < fci.size(); k++ ){
            row_signal = signal.createRow(k + 1);
            row_signal.createCell(0).setCellValue(fci.get(k).toString());
            row_signal.createCell(1).setCellValue(times_fci.get(k).toString());
        }

        //Agregando valores para los eventos
        row_event.createCell(0).setCellValue("Tiempo Inicial (s)");
        row_event.createCell(1).setCellValue("Tiempo Final (s)");
        row_event.createCell(2).setCellValue("Coherencia Cardíaca");
        row_event.createCell(3).setCellValue("Emoción");
        row_event.createCell(4).setCellValue("Tipo de Evento");
        int count_event_signal = 0;
        for(int m = 0; m < signal_model.getTime_line().size(); m++){
            if(signal_model.getTime_line().get(m).getKey().getType_alert() == 1 || signal_model.getTime_line().get(m).getKey().getType_alert() == 2 ){
                row_event = event.createRow(count_event_signal + 1);
                row_event.createCell(0).setCellValue(signal_model.getTime_line().get(m).getValue().getStart_time());
                row_event.createCell(1).setCellValue(signal_model.getTime_line().get(m).getValue().getEnd_time());
                row_event.createCell(2).setCellValue(signal_model.getTime_line().get(m).getKey().getDescription());
                row_event.createCell(3).setCellValue(signal_model.getTime_line().get(m).getKey().getEmotion().getName());
                row_event.createCell(4).setCellValue(signal_model.getTime_line().get(m).getKey().getText_alert());
                count_event_signal++;
            }
        }

        try {
            FileOutputStream fileOut = new FileOutputStream(path_dir + "/" + name_path_signal + ".xls");
            workbook.write(fileOut);
            fileOut.close();
        }  catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public  List<List<String>> read_xls(String path, boolean allCellHaveData, int type_read){
        if(path == "")
            return null;

        List<List<String>> sheetData = new ArrayList();
        int headerCount = 0;

        try (FileInputStream fis = new FileInputStream(path);
             Workbook wb = WorkbookFactory.create(fis)) {
            Sheet sheet;
            if(type_read == 1){
                sheet = wb.getSheetAt(0);
            }
            else{
                sheet = wb.getSheetAt(1);
            }
            for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
                Row row = sheet.getRow(i);
                String value;

                List<String> rowData = new ArrayList();
                //System.out.println("\nROW " + row.getRowNum() + " has " + row.getPhysicalNumberOfCells() + " cell(s).");
                //System.out.println("last cell number: " + row.getLastCellNum());
                if (i == 0) {
                    headerCount = row.getLastCellNum();
                }

                for (int c = 0; c < row.getLastCellNum(); c++) {

                    Cell cell = row.getCell(c);

                    if (cell != null) {
                        System.out.println(cell);
                        switch (cell.getCellType()) {
                            case FORMULA:
                                value = "FORMULA value=" + cell.getCellFormula();
                                rowData.add(cell.getCellFormula());
                                break;

                            case NUMERIC:
                                value = "NUMERIC value=" + cell.getNumericCellValue();
                                rowData.add(Double.toString(cell.getNumericCellValue()));
                                break;

                            case STRING:
                                value = "STRING value=" + cell.getStringCellValue();
                                rowData.add(cell.getStringCellValue());
                                break;

                            case BLANK:
                                value = "<BLANK>";
                                rowData.add("");
                                break;

                            case BOOLEAN:
                                value = "BOOLEAN value-" + cell.getBooleanCellValue();
                                rowData.add(cell.getBooleanCellValue() ? "true" : "false");
                                break;

                            case ERROR:
                                value = "ERROR value=" + cell.getErrorCellValue();
                                rowData.add(Byte.toString(cell.getErrorCellValue()));
                                break;

                            default:
                                value = "UNKNOWN value of type " + cell.getCellType();
                                rowData.add(cell.getCellType().toString());
                        }
                        //System.out.println("CELL col=" + cell.getColumnIndex() + " VALUE=" + value);
                    }
                    else {
                        //System.out.println("CELL col=" + " VALUE=" + "<My Blank>");
                        rowData.add("<My Blank>");
                    }
                }

                if (!allCellHaveData) {
                    int currentRowCount = row.getLastCellNum();
                    while (currentRowCount < headerCount) {
                        rowData.add("<Blank end column>");
                        currentRowCount++;
                    }
                }
                sheetData.add(rowData);
            }
        }
        catch (FileNotFoundException ex) {
            System.out.println(ex);
        }
        catch (IOException ex) {
            System.out.println(ex);
        }

        return sheetData;
    }


}
