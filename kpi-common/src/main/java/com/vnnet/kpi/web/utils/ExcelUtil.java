/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vnnet.kpi.web.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtil {
    public static XSSFFont getFont(XSSFWorkbook wb, String fontName, Integer fontSize, boolean bold) {
        XSSFFont font = wb.createFont();
        font.setFontName(fontName);
        font.setFontHeightInPoints((short) fontSize.intValue());
        font.setBold(bold);
        return font;
    }

    public static final XSSFCellStyle getStyle(XSSFWorkbook wb, XSSFFont font, HorizontalAlignment hAlignment, VerticalAlignment vAlignment, BorderStyle borderTop, BorderStyle borderBottom, BorderStyle borderLeft, BorderStyle borderRight) {
        XSSFCellStyle style = wb.createCellStyle();
        //set border
        style.setBorderTop(borderTop);
        style.setBorderBottom(borderBottom);
        style.setBorderLeft(borderLeft);
        style.setBorderRight(borderRight);
        //style.setFillBackgroundColor(HSSFColor.BLUE.index);
        //set alignment
        style.setAlignment(hAlignment);
        style.setVerticalAlignment(vAlignment);
        //set font
        style.setFont(font);

        return style;
    }

    public static final XSSFCellStyle getStyle1(XSSFWorkbook wb, XSSFFont font, HorizontalAlignment hAlignment, VerticalAlignment vAlignment, BorderStyle borderTop, BorderStyle borderBottom, BorderStyle borderLeft, BorderStyle borderRight) {
        XSSFCellStyle style = wb.createCellStyle();
        //set border
        style.setBorderTop(borderTop);
        style.setBorderBottom(borderBottom);
        style.setBorderLeft(borderLeft);
        style.setBorderRight(borderRight);
        //style.setFillBackgroundColor(HSSFColor.BLUE.index);
        //set alignment
        style.setAlignment(hAlignment);
        style.setVerticalAlignment(vAlignment);
        //set font
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);


        return style;
    }

    public static final XSSFCellStyle getFooterStyle(XSSFWorkbook wb, XSSFFont font, HorizontalAlignment hAlignment, VerticalAlignment vAlignment, BorderStyle borderTop, BorderStyle borderBottom, BorderStyle borderLeft, BorderStyle borderRight) {
        XSSFCellStyle style = wb.createCellStyle();
        try {
            //set border
            style.setBorderTop(borderTop);
            style.setBorderBottom(borderBottom);
            style.setBorderLeft(borderLeft);
            style.setBorderRight(borderRight);
//        style.setFillForegroundColor(IndexedColors.GREEN.getIndex());
            style.setFillForegroundColor(new XSSFColor(Hex.decodeHex("c4c5c8"), null));
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            //set alignment
            style.setAlignment(hAlignment);
            style.setVerticalAlignment(vAlignment);
            //set font
            style.setFont(font);
            return style;
        } catch (DecoderException e) {
            e.printStackTrace();
        }
        return style;
    }

    public static final void createCell(Row row, Integer column, Object value, XSSFCellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellStyle(style);

        String pattern = "dd/MM/yyyy";
        SimpleDateFormat format = new SimpleDateFormat(pattern);

        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Integer) {
            if ((Integer) value != 0) {
                cell.setCellValue((Integer) value);
            }
            //cell.setCellValue(new DecimalFormat("###,###,###.###").format(value));
        } else if (value instanceof Float) {
            if ((Float) value != 0) {
                cell.setCellValue((Float) value);
            }
        } else if (value instanceof Double) {
            if ((Double) value != 0) {
                cell.setCellValue((Double) value);
            }
        } else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        } else if (value instanceof Date) {
            cell.setCellValue((String) format.format(value));
        }
        cell.setCellStyle(style);
    }

//    public static final void createCell(HSSFRow row, short column, Object value, HSSFWorkbook wb, HSSFCellStyle style)
//    {
//        HSSFCell cell = row.getCell(column);
//        if (cell == null)
//            cell = row.createCell(column);
//        //cell.setEncoding(wb.ENCODING_UTF_18);
//        String pattern = "dd/MM/yyyy";
//        SimpleDateFormat format = new SimpleDateFormat(pattern);
//
//        if (value instanceof String)
//        {
//            cell.setCellValue((String)value);
//        }
//        else if (value instanceof Integer)
//        {
//            if ((Integer)value != 0)
//            {
//                cell.setCellValue((Integer)value);
//            }
//            //cell.setCellValue(new DecimalFormat("###,###,###.###").format(value));
//        }
//        else if (value instanceof Float)
//        {
//            if ((Float)value != 0)
//            {
//                cell.setCellValue((Float)value);
//            }
//        }
//        else if (value instanceof Double)
//        {
//            if ((Double)value != 0)
//            {
//                cell.setCellValue((Double)value);
//            }
//
//        }
//        else if (value instanceof Long)
//        {
//            cell.setCellValue((Long)value);
//        }
//        else if (value instanceof Date)
//        {
//            cell.setCellValue((String)format.format(value));
//        }
//
//        cell.setCellStyle(style);
//    }

}
