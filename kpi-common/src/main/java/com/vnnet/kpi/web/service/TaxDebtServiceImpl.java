package com.vnnet.kpi.web.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.SimpleTextExtractionStrategy;
import com.vnnet.kpi.web.bean.HttpResult;
import com.vnnet.kpi.web.bean.HttpStatus;
import com.vnnet.kpi.web.config.Constants;
import com.vnnet.kpi.web.model.*;
import com.vnnet.kpi.web.persistence.SysMessagDtMapper;
import com.vnnet.kpi.web.persistence.SysMessagHdMapper;
import com.vnnet.kpi.web.utils.DateTimeUtils;
import com.vnnet.kpi.web.utils.JwtTokenForPdfFile;
import com.vnnet.kpi.web.utils.StringUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.awt.Color;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Transactional
public class TaxDebtServiceImpl implements TaxDebtService {

    private static final Logger logger = LoggerFactory.getLogger(TaxDebtServiceImpl.class);

    private static int THONG_BAO_NO_THUE = 1;
    //Tax debt notification
    private static int THONG_BAO_CUONG_CHE = 6;

    //Enforcement notice
    private static int THONG_BAO_MIEN_GIAM = 13;
    //Reduction notification
    private static int QUYET_DINH_CUONG_CHE = 15;
    //Enforcement decision

    private static int IMAGE_DPI = 76; //dpi: 300, 150, 76

    @Autowired
    private SysMessagHdMapper sysMessagHdMapper;
    @Autowired
    private SysMessagDtMapper sysMessagDtMapper;


    @Override
    public HttpResult save(MultipartFile file1, MultipartFile file2, MultipartFile[] file3, SysMessagHd sysMessagHd) {
          if(file3 != null && file3.length > 0)
              return saveWithPdf(file1, file2, file3, sysMessagHd);
          else
              return saveWithOutPdf(file1, file2, sysMessagHd);
    }

    public HttpResult saveWithPdf(MultipartFile file1, MultipartFile file2, MultipartFile[] file3, SysMessagHd sysMessagHd) {
        try {
            List<BaseThongBaoNoThue> validList1 = new ArrayList<>();
            List<BaseThongBaoNoThue> validList2 = new ArrayList<>();
            List<BaseThongBaoNoThue> invalidList1 = new ArrayList<>();
            List<BaseThongBaoNoThue> invalidList2 = new ArrayList<>();

            //Đọc file excel
            String str1 = "";
            if (sysMessagHd.getProgramId() == THONG_BAO_NO_THUE)
                str1 = loadDataFromExcelTaxDebt(file1, validList1, invalidList1);
            else if (sysMessagHd.getProgramId() == THONG_BAO_CUONG_CHE)
                str1 = loadDataFromExcelCoerciveTaxDebt1(file1, validList1, invalidList1);
            else if (sysMessagHd.getProgramId() == QUYET_DINH_CUONG_CHE)
                str1 = loadDataFromExcelCoerciveTaxDebt2(file1, validList1, invalidList1);
            else if (sysMessagHd.getProgramId() == THONG_BAO_MIEN_GIAM)
                str1 = loadDataFromExcelExemptionTaxDebt(file1, validList1, invalidList1);
            else
                str1 = "No valid program found";
            String str2 = loadDataFromExcel2(file2, validList2, invalidList2);

            //Đọc file pdf
            Map<String, String> pdfList = readPdfFile(file3);

            if (!StringUtils.isBlank(str1))
                return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, str1);
            if (!StringUtils.isBlank(str2))
                return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, str2);
            if (pdfList.isEmpty())
                return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Error reading PDF file debt notification");

            if (invalidList1.size() > 0)
                return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Error in the debt notification file (" + file1.getOriginalFilename() + "). Vui lòng xem bảng dưới để biết thêm chi tiết", invalidList1);
            if (invalidList2.size() > 0)
                return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Address book file error (" + file2.getOriginalFilename() + "). Vui lòng xem bảng dưới để biết thêm chi tiết", invalidList2);

            //Lưu file excel và pdf
            String fileName1 = storeFile(file1, "no_thue_chi_tiet");
            String fileName2 = storeFile(file2, "no_thue_danh_ba");
            String fileName3 = zipMultipleFiles(file3, "no_thue_thong_bao");

            if (fileName1.equals("") || fileName2.equals("") || fileName3.equals(""))
                return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Error saving uploaded file. Please try again later");

            sysMessagHd.setDetailFile(fileName1);
            sysMessagHd.setContactFile(fileName2);
            sysMessagHd.setPdfFile(fileName3);

            sysMessagHd.setDelFlag((byte) 0);
            int result = sysMessagHdMapper.insertSelective(sysMessagHd);
            if (result >= 1) {
                List<BaseThongBaoNoThue> invalidList3 = new ArrayList<>();
                List<SysMessagDt> sysMessagDts = loadMessages(validList1, validList2, sysMessagHd.getMessHdId(), sysMessagHd.getSmsTemplate(), invalidList3, pdfList, sysMessagHd.getCreateShortLink());
//                if(invalidList3.size() > 0) {
//                    sysMessagHdMapper.deleteByPrimaryKey(sysMessagHd.getMessHdId());
//                    return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Lỗi lấy thông tin SĐT corporation. Vui lòng xem bảng dưới để biết thêm chi tiết", invalidList3);
//                }
                for (SysMessagDt messagDt : sysMessagDts) {
                    sysMessagDtMapper.insertSelective(messagDt);
                }
                return HttpResult.ok("Message schedule created successfully. Total " + sysMessagDts.size() + " message", invalidList3);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
        }
        return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "An error occurred. Please try again later");
    }

    public HttpResult saveWithOutPdf(MultipartFile file1, MultipartFile file2, SysMessagHd sysMessagHd) {
        try {
            List<BaseThongBaoNoThue> validList1 = new ArrayList<>();
            List<BaseThongBaoNoThue> validList2 = new ArrayList<>();
            List<BaseThongBaoNoThue> invalidList1 = new ArrayList<>();
            List<BaseThongBaoNoThue> invalidList2 = new ArrayList<>();

            //Đọc file excel
            String str1 = "";
            if (sysMessagHd.getProgramId() == THONG_BAO_NO_THUE)
                str1 = loadDataFromExcelTaxDebt(file1, validList1, invalidList1);
            else if (sysMessagHd.getProgramId() == THONG_BAO_CUONG_CHE)
                str1 = loadDataFromExcelCoerciveTaxDebt1(file1, validList1, invalidList1);
            else if (sysMessagHd.getProgramId() == QUYET_DINH_CUONG_CHE)
                str1 = loadDataFromExcelCoerciveTaxDebt2(file1, validList1, invalidList1);
            else if (sysMessagHd.getProgramId() == THONG_BAO_MIEN_GIAM)
                str1 = loadDataFromExcelExemptionTaxDebt(file1, validList1, invalidList1);
            else
                str1 = "No valid program found";
            String str2 = loadDataFromExcel2(file2, validList2, invalidList2);

            if (!StringUtils.isBlank(str1))
                return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, str1);
            if (!StringUtils.isBlank(str2))
                return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, str2);

            if (invalidList1.size() > 0)
                return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Error in the debt notification file (" + file1.getOriginalFilename() + "). Vui lòng xem bảng dưới để biết thêm chi tiết", invalidList1);
            if (invalidList2.size() > 0)
                return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Address book file error (" + file2.getOriginalFilename() + "). Vui lòng xem bảng dưới để biết thêm chi tiết", invalidList2);

            //Lưu file excel và pdf
            String fileName1 = storeFile(file1, "no_thue_chi_tiet");
            String fileName2 = storeFile(file2, "no_thue_danh_ba");

            if (fileName1.equals("") || fileName2.equals(""))
                return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Error saving uploaded file. Please try again later");

            sysMessagHd.setDetailFile(fileName1);
            sysMessagHd.setContactFile(fileName2);

            sysMessagHd.setDelFlag((byte) 0);
            int result = sysMessagHdMapper.insertSelective(sysMessagHd);
            if (result >= 1) {
                List<BaseThongBaoNoThue> invalidList3 = new ArrayList<>();
                List<SysMessagDt> sysMessagDts = loadMessages(validList1, validList2, sysMessagHd.getMessHdId(), sysMessagHd.getSmsTemplate(), invalidList3, sysMessagHd.getCreateShortLink());
//                if(invalidList3.size() > 0) {
//                    sysMessagHdMapper.deleteByPrimaryKey(sysMessagHd.getMessHdId());
//                    return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Lỗi lấy thông tin SĐT corporation. Vui lòng xem bảng dưới để biết thêm chi tiết", invalidList3);
//                }
                for (SysMessagDt messagDt : sysMessagDts) {
                    sysMessagDtMapper.insertSelective(messagDt);
                }
                return HttpResult.ok("Message schedule created successfully. Total " + sysMessagDts.size() + " message", invalidList3);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
        }
        return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "An error occurred. Please try again later");
    }


    private String loadDataFromExcelTaxDebt(MultipartFile file, List<BaseThongBaoNoThue> validList, List<BaseThongBaoNoThue> invalidList) {
        try {
            InputStream inputStream = file.getInputStream();
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            inputStream.close();
            Sheet sheet = workbook.getSheetAt(0);
            workbook.close();
            Iterator<Row> rowIterator = sheet.iterator();

            int _row = 0;
            int _col = 0;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                if (_row == 8) {
                    Iterator<Cell> cellIterator = row.cellIterator();
                    String title = "";
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        title = checkHeaderExcelTaxDebt(_col, cell);
                        if (!title.equals(""))
                            break;
                        _col++;
                    }
                    if (!title.equals(""))
                        return "Detail file (" + file.getOriginalFilename() + ") missing column \"" + title + "\". Please select the file again";
                } else if (_row >= 12) {
                    Iterator<Cell> cellIterator = row.cellIterator();
                    _col = 1;
                    Cell index = cellIterator.next();
                    if (StringUtils.isBlank(getCellValue(index).toString())) {
                        _row++;
                        break;
                    }
                    BaseThongBaoNoThue hd = new BaseThongBaoNoThue();
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        Object cellValue = getCellValue(cell);
                        switch (_col) {
                            case 1:
                                if (StringUtils.isBlank(String.valueOf(cellValue)))
                                    hd.setError("Missing tax number");
                                else
                                    hd.setMaSoThue(String.valueOf(cellValue));
                                break;
                            case 2:
                                if (StringUtils.isBlank(String.valueOf(cellValue)))
                                    hd.setError("Missing taxpayer");
                                else
                                    hd.setNguoiNopThue(String.valueOf(cellValue));
                                break;
//                            case 3:
//                                if (StringUtils.isBlank(String.valueOf(cellValue)))
//                                    hd.setError("Thiếu thông tin số thông báo nợ");
//                                else
//                                    hd.setSoThongBao(String.valueOf(cellValue));
//                                break;
//                            case 4:
//                                if (StringUtils.isBlank(String.valueOf(cellValue)))
//                                    hd.setError("Thiếu thông tin ngày ban hành");
//                                else {
//                                    if (cellValue instanceof Long && DateUtil.isCellDateFormatted(cell))
//                                        hd.setNgayBanHanh(DateTimeUtils.convertDateToString(cell.getDateCellValue(), "dd/MM/yyyy"));
//                                    else if (DateTimeUtils.isValid(String.valueOf(cellValue), "dd/MM/yyyy"))
//                                        hd.setNgayBanHanh(String.valueOf(cellValue));
//                                    else
//                                        hd.setError("The issuance date information is invalid or does not use the format DD/MM/YYYY");
//                                }
//                                break;
                            case 3:
                                try {
                                    if (StringUtils.isBlank(String.valueOf(cellValue)))
                                        hd.setError("Missing information about unpaid amount");
                                    else
                                        hd.setSoTienChuaNop((long) cell.getNumericCellValue());
                                } catch (Exception ex) {
                                    hd.setError("The amount value is not a numerical");
                                }
                                break;
//                            case 6:
//                                try {
//                                    if (StringUtils.isBlank(String.valueOf(cellValue)))
//                                        hd.setError("Thiếu thông tin số tiền quá hạn");
//                                    else
//                                        hd.setSoTienQuaHan((long) cell.getNumericCellValue());
//                                } catch (Exception ex) {
//                                    hd.setError("Cột Số tiền quá hạn không phải là số");
//                                }
//                                break;
                            default:
                                break;
                        }
                        _col++;
                    }
                    hd.setRowIndex(_row + 1);
                    if (!StringUtils.isBlank(hd.getError()))
                        invalidList.add(hd);
                    else
                        validList.add(hd);
                }
                _row++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return "";
    }

    private String loadDataFromExcelCoerciveTaxDebt1(MultipartFile file, List<BaseThongBaoNoThue> validList, List<BaseThongBaoNoThue> invalidList) {
        try {
            InputStream inputStream = file.getInputStream();
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            inputStream.close();
            Sheet sheet = workbook.getSheetAt(0);
            workbook.close();
            Iterator<Row> rowIterator = sheet.iterator();

            int _row = 0;
            int _col = 0;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                if (_row == 8) {
                    Iterator<Cell> cellIterator = row.cellIterator();
                    String title = "";
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        title = checkHeaderExcelCoerciveTaxDebt1(_col, cell);
                        if (!title.equals(""))
                            break;
                        _col++;
                    }
                    if (!title.equals(""))
                        return "Detail file (" + file.getOriginalFilename() + ") missing column \"" + title + "\". Please select the file again";
                } else if (_row >= 12) {
                    Iterator<Cell> cellIterator = row.cellIterator();
                    _col = 1;
                    Cell index = cellIterator.next();
                    if (StringUtils.isBlank(getCellValue(index).toString())) {
                        _row++;
                        break;
                    }
                    BaseThongBaoNoThue hd = new BaseThongBaoNoThue();
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        Object cellValue = getCellValue(cell);
                        switch (_col) {
                            case 1:
                                if (StringUtils.isBlank(String.valueOf(cellValue)))
                                    hd.setError("Missing tax number");
                                else
                                    hd.setMaSoThue(String.valueOf(cellValue));
                                break;
                            case 2:
                                if (StringUtils.isBlank(String.valueOf(cellValue)))
                                    hd.setError("Missing taxpayer");
                                else
                                    hd.setNguoiNopThue(String.valueOf(cellValue));
                                break;
                            case 3:
                                try {
                                    if (StringUtils.isBlank(String.valueOf(cellValue)))
                                        hd.setError("Missing information about unpaid amount");
                                    else
                                        hd.setSoTienQuaHan((long) cell.getNumericCellValue());
                                } catch (Exception ex) {
                                    hd.setError("The amount value is not a numerical");
                                }
                                break;
                            default:
                                break;
                        }
                        _col++;
                    }
                    hd.setRowIndex(_row + 1);
                    if (!StringUtils.isBlank(hd.getError()))
                        invalidList.add(hd);
                    else
                        validList.add(hd);
                }
                _row++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return "";
    }

    private String loadDataFromExcelCoerciveTaxDebt2(MultipartFile file, List<BaseThongBaoNoThue> validList, List<BaseThongBaoNoThue> invalidList) {
        try {
            InputStream inputStream = file.getInputStream();
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            inputStream.close();
            Sheet sheet = workbook.getSheetAt(0);
            workbook.close();
            Iterator<Row> rowIterator = sheet.iterator();

            int _row = 0;
            int _col = 0;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                if (_row == 8) {
                    Iterator<Cell> cellIterator = row.cellIterator();
                    String title = "";
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        title = checkHeaderExcelCoerciveTaxDebt2(_col, cell);
                        if (!title.equals(""))
                            break;
                        _col++;
                    }
                    if (!title.equals(""))
                        return "Detail file (" + file.getOriginalFilename() + ") missing column \"" + title + "\". Please select the file again";
                } else if (_row >= 12) {
                    Iterator<Cell> cellIterator = row.cellIterator();
                    _col = 1;
                    Cell index = cellIterator.next();
                    if (StringUtils.isBlank(getCellValue(index).toString())) {
                        _row++;
                        break;
                    }
                    BaseThongBaoNoThue hd = new BaseThongBaoNoThue();
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        Object cellValue = getCellValue(cell);
                        switch (_col) {
                            case 1:
                                if (StringUtils.isBlank(String.valueOf(cellValue)))
                                    hd.setError("Missing tax number");
                                else
                                    hd.setMaSoThue(String.valueOf(cellValue));
                                break;
                            case 2:
                                if (StringUtils.isBlank(String.valueOf(cellValue)))
                                    hd.setError("Missing taxpayer");
                                else
                                    hd.setNguoiNopThue(String.valueOf(cellValue));
                                break;
                            case 3:
                                if (StringUtils.isBlank(String.valueOf(cellValue)))
                                    hd.setError("Missing decision number");
                                else
                                    hd.setSoQuyetDinh(String.valueOf(cellValue));
                                break;
                            case 4:
                                if (StringUtils.isBlank(String.valueOf(cellValue)))
                                    hd.setError("Missing decision approve date");
                                else {
                                    if (cellValue instanceof Long && DateUtil.isCellDateFormatted(cell))
                                        hd.setNgayBanHanh(DateTimeUtils.convertDateToString(cell.getDateCellValue(), "dd/MM/yyyy"));
                                    else if (DateTimeUtils.isValid(String.valueOf(cellValue), "dd/MM/yyyy"))
                                        hd.setNgayBanHanh(String.valueOf(cellValue));
                                    else
                                        hd.setError("The issuance date information is invalid or does not use the format DD/MM/YYYY");
                                }
                                break;
                            case 6:
                                try {
                                    if (StringUtils.isBlank(String.valueOf(cellValue)))
                                        hd.setError("Missing amount information");
                                    else
                                        hd.setSoTienQuaHan((long) cell.getNumericCellValue());
                                } catch (Exception ex) {
                                    hd.setError("The amount value is not a numerical");
                                }
                                break;
                            case 7:
                                if (StringUtils.isBlank(String.valueOf(cellValue)))
                                    hd.setError("Missing effective date information");
                                else {
                                    if (cellValue instanceof Long && DateUtil.isCellDateFormatted(cell))
                                        hd.setTuNgay(DateTimeUtils.convertDateToString(cell.getDateCellValue(), "dd/MM/yyyy"));
                                    else if (DateTimeUtils.isValid(String.valueOf(cellValue), "dd/MM/yyyy"))
                                        hd.setTuNgay(String.valueOf(cellValue));
                                    else
                                        hd.setError("The effective date information is invalid or does not use the format DD/MM/YYYY");
                                }
                                break;
                            case 8:
                                if (StringUtils.isBlank(String.valueOf(cellValue)))
                                    hd.setError("Missing expiration date information");
                                else {
                                    if (cellValue instanceof Long && DateUtil.isCellDateFormatted(cell))
                                        hd.setDenNgay(DateTimeUtils.convertDateToString(cell.getDateCellValue(), "dd/MM/yyyy"));
                                    else if (DateTimeUtils.isValid(String.valueOf(cellValue), "dd/MM/yyyy"))
                                        hd.setDenNgay(String.valueOf(cellValue));
                                    else
                                        hd.setError("The expiration date information is invalid or does not use the format DD/MM/YYYY");
                                }
                                break;
                            default:
                                break;
                        }
                        _col++;
                    }
                    hd.setRowIndex(_row + 1);
                    if (!StringUtils.isBlank(hd.getError()))
                        invalidList.add(hd);
                    else
                        validList.add(hd);
                }
                _row++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return "";
    }

    private String loadDataFromExcelExemptionTaxDebt(MultipartFile file, List<BaseThongBaoNoThue> validList, List<BaseThongBaoNoThue> invalidList) {
        try {
            InputStream inputStream = file.getInputStream();
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            inputStream.close();
            Sheet sheet = workbook.getSheetAt(0);
            workbook.close();
            Iterator<Row> rowIterator = sheet.iterator();

            int _row = 0;
            int _col = 0;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                if (_row == 8) {
                    Iterator<Cell> cellIterator = row.cellIterator();
                    String title = "";
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        title = checkHeaderExcelExemptionTaxDebt(_col, cell);
                        if (!title.equals(""))
                            break;
                        _col++;
                    }
                    if (!title.equals(""))
                        return "Detail file (" + file.getOriginalFilename() + ") missing column \"" + title + "\". Please select the file again";
                } else if (_row >= 12) {
                    Iterator<Cell> cellIterator = row.cellIterator();
                    _col = 1;
                    Cell index = cellIterator.next();
                    if (StringUtils.isBlank(getCellValue(index).toString())) {
                        _row++;
                        break;
                    }
                    BaseThongBaoNoThue hd = new BaseThongBaoNoThue();
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        Object cellValue = getCellValue(cell);
                        switch (_col) {
                            case 1:
                                if (StringUtils.isBlank(String.valueOf(cellValue)))
                                    hd.setError("Missing tax number");
                                else
                                    hd.setMaSoThue(String.valueOf(cellValue));
                                break;
                            case 2:
                                if (StringUtils.isBlank(String.valueOf(cellValue)))
                                    hd.setError("Missing taxpayer");
                                else
                                    hd.setNguoiNopThue(String.valueOf(cellValue));
                                break;
//                            case 6:
//                                if(StringUtils.isBlank(String.valueOf(cellValue)))
//                                    hd.setError("Thiếu thông tin Số quyết định");
//                                else
//                                    hd.setSoQuyetDinh(String.valueOf(cellValue));
//                                break;
//                            case 7:
//                                if(StringUtils.isBlank(String.valueOf(cellValue)))
//                                    hd.setError("Thiếu thông tin ngày quyết định");
//                                else
//                                    hd.setNgayBanHanh(String.valueOf(cellValue));
//                                break;
//                            case 8:
//                                try{
//                                    if(StringUtils.isBlank(String.valueOf(cellValue)))
//                                        hd.setError("Thiếu thông tin số tiền chậm nộp được miễn giảm");
//                                    else
//                                        hd.setSoTienChuaNop((long) cell.getNumericCellValue());
//                                }
//                                catch (Exception ex){
//                                    hd.setError("Cột Số tiền chậm nộp được miễn giảm không phải là số");
//                                }
//                                break;
                            default:
                                break;
                        }
                        _col++;
                    }
                    hd.setRowIndex(_row + 1);
                    if (!StringUtils.isBlank(hd.getError()))
                        invalidList.add(hd);
                    else
                        validList.add(hd);
                }
                _row++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return "";
    }

    private String loadDataFromExcel2(MultipartFile file, List<BaseThongBaoNoThue> validList, List<BaseThongBaoNoThue> invalidList) {
        try {
            InputStream inputStream = file.getInputStream();
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            inputStream.close();
            Sheet sheet = workbook.getSheetAt(0);
            workbook.close();
            Iterator<Row> rowIterator = sheet.iterator();

            int _row = 0;
            int _col = 0;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();

                if (_row == 0) {
                    String title = "";
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        title = checkHeaderExcel2(_col, cell);
                        if (!title.equals(""))
                            break;
                        _col++;
                    }
                    if (!title.equals(""))
                        return "Contact file (" + file.getOriginalFilename() + ") missing column \"" + title + "\". Please select the file again";
                    _row++;
                } else if (_row == 1) {
                    //Bỏ qua dòng đầu tiên dữ liệu mẫu
                    _row++;
                    continue;
                } else {
                    _col = 0;
                    BaseThongBaoNoThue hd = new BaseThongBaoNoThue();
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        switch (_col) {
                            case 1:
                                hd.setMaSoThue(String.valueOf(getCellValue(cell)));
                                break;
                            case 2:
                                hd.setNguoiNopThue(String.valueOf(getCellValue(cell)));
                                break;
                            case 3:
                                hd.setSoGiamDoc(String.valueOf(getCellValue(cell)));
                                break;
                            case 4:
                                hd.setSoKeToan(String.valueOf(getCellValue(cell)));
                                break;
                            default:
                                break;
                        }
                        _col++;
                    }

                    hd.setRowIndex(_row);
                    if (!StringUtils.isBlank(hd.getError()))
                        invalidList.add(hd);
                    else
                        validList.add(hd);

                    _row++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return "";
    }


    private String checkHeaderExcelTaxDebt(int col, Cell cell) {
        if (col == 0 && !getCellValue(cell).toString().equals("STT"))
            return "STT";
        else if (col == 1 && !getCellValue(cell).toString().equals("Mã số thuế"))
            return "Mã số thuế";
        else if (col == 2 && !getCellValue(cell).toString().equals("Tên người nộp thuế"))
            return "Tên người nộp thuế";
        else if (col == 3 && !getCellValue(cell).toString().equals("Số tiền chưa nộp NSNN đến trước tháng đôn đốc"))
            return "Số tiền chưa nộp NSNN đến trước tháng đôn đốc";

        return "";
    }

    private String checkHeaderExcelCoerciveTaxDebt1(int col, Cell cell) {
        if (col == 0 && !getCellValue(cell).toString().equals("STT"))
            return "STT";
        else if (col == 1 && !getCellValue(cell).toString().equals("Mã số thuế"))
            return "Mã số thuế";
        else if (col == 2 && !getCellValue(cell).toString().equals("Tên người nộp thuế"))
            return "Tên người nộp thuế";
        else if (col == 3 && !getCellValue(cell).toString().contains("Số tiền quá hạn phải thực hiện CCNT"))
            return "Số tiền quá hạn phải thực hiện CCNT";

        return "";
    }

    private String checkHeaderExcelCoerciveTaxDebt2(int col, Cell cell) {
        if (col == 0 && !getCellValue(cell).toString().equals("STT"))
            return "STT";
        else if (col == 1 && !getCellValue(cell).toString().equals("Mã số thuế"))
            return "Mã số thuế";
        else if (col == 2 && !getCellValue(cell).toString().equals("Tên người nộp thuế"))
            return "Tên người nộp thuế";
        else if (col == 3 && !getCellValue(cell).toString().contains("Số Quyết định"))
            return "Số Quyết định";
        else if (col == 4 && !getCellValue(cell).toString().contains("ban hành"))
            return "Ngày ban hành Quyết định";
        else if (col == 5 && !getCellValue(cell).toString().equals("Hình thức Cưỡng chế"))
            return "Hình thức Cưỡng chế";
        else if (col == 6 && !getCellValue(cell).toString().equals("Số tiền phải thực hiện CCNT"))
            return "Số tiền phải thực hiện CCNT";
        else if (col == 7 && !getCellValue(cell).toString().equals("Hiệu lực từ"))
            return "Hiệu lực từ";
        else if (col == 8 && !getCellValue(cell).toString().equals("Hiệu lực đến"))
            return "Hiệu lực đến";

        return "";
    }

    private String checkHeaderExcelExemptionTaxDebt(int col, Cell cell) {
        if (col == 0 && !getCellValue(cell).toString().equals("STT"))
            return "STT";
        else if (col == 1 && !getCellValue(cell).toString().equals("Mã số thuế"))
            return "Mã số thuế";
        else if (col == 2 && !getCellValue(cell).toString().equals("Tên người nộp thuế"))
            return "Tên người nộp thuế";
        else if (col == 3 && !getCellValue(cell).toString().contains("Ngày nhận hồ sơ"))
            return "Ngày nhận hồ sơ";
        else if (col == 4 && !getCellValue(cell).toString().contains("Số Văn bản đề nghị"))
            return "Số Văn bản đề nghị";
        else if (col == 5 && !getCellValue(cell).toString().equals("Số Quyết định"))
            return "Số Quyết định";
        else if (col == 6 && !getCellValue(cell).toString().equals("Ngày Quyết định"))
            return "Ngày Quyết định";
        else if (col == 7 && !getCellValue(cell).toString().contains("Số tiền chậm nộp được miễn giảm"))
            return "Số tiền chậm nộp được miễn giảm";
        else if (col == 8 && !getCellValue(cell).toString().contains("Số Thông báo từ chối miễn giảm"))
            return "Số Thông báo từ chối miễn giảm";
        else if (col == 9 && !getCellValue(cell).toString().equals("Ngày Thông báo"))
            return "Ngày Thông báo";
        else if (col == 10 && !getCellValue(cell).toString().equals("Lý do từ chối"))
            return "Lý do từ chối";

        return "";
    }

    private String checkHeaderExcel2(int col, Cell cell) {
        if (col == 1 && !getCellValue(cell).toString().equals("Mã số thuế"))
            return "Mã số thuế";
        else if (col == 2 && !getCellValue(cell).toString().equals("Tên NNT"))
            return "Tên NNT";
        else if (col == 3 && !getCellValue(cell).toString().equals("Điện thoại giám đốc"))
            return "Điện thoại giám đốc";
        else if (col == 4 && !getCellValue(cell).toString().equals("Điện thoại kế toán trưởng"))
            return "Điện thoại kế toán trưởng";

        return "";
    }


    private Object getCellValue(Cell cell) {
        if (cell != null) {
            CellType cellType = cell.getCellType();
            if (CellType.STRING.equals(cellType))
                return cell.getStringCellValue();
            else if (CellType.NUMERIC.equals(cellType)) {
                return (long) cell.getNumericCellValue();
            }
        }
        return "";
    }

    private List<SysMessagDt> loadMessages(List<BaseThongBaoNoThue> smsList, List<BaseThongBaoNoThue> phoneList, long id, String smsTemp, List<BaseThongBaoNoThue> invalidList, Map<String, String> pdfList, boolean createShortLink) {
        List<SysMessagDt> messagDts = new ArrayList<>();
        int rowIndex = 13;
        for (BaseThongBaoNoThue noThue : smsList) {
            List<BaseThongBaoNoThue> list = phoneList.stream().filter(it -> it.getMaSoThue() != null && it.getMaSoThue().equals(noThue.getMaSoThue())).collect(Collectors.toList());
            String pdfFile = pdfList.get(noThue.getMaSoThue());
            if (list != null && list.size() > 0) {
                if (StringUtils.isBlank(list.get(0).getSoGiamDoc()) && StringUtils.isBlank(list.get(0).getSoKeToan())) {
                    BaseThongBaoNoThue obj = new BaseThongBaoNoThue();
                    obj.setError("Corporation \"" + noThue.getNguoiNopThue() + "\" does not have contact number");
                    obj.setRowIndex(rowIndex);
                    invalidList.add(obj);
                } else if (StringUtils.isBlank(pdfFile)) {
                    BaseThongBaoNoThue obj = new BaseThongBaoNoThue();
                    obj.setError("Corporation \"" + noThue.getNguoiNopThue() + "\" there is no pdf file for debt notification");
                    obj.setRowIndex(rowIndex);
                    invalidList.add(obj);
                } else {
                    if (!StringUtils.isBlank(list.get(0).getSoGiamDoc()))
                        messagDts.add(addMessageDt(noThue, list.get(0).getSoGiamDoc(), id, smsTemp, pdfFile, createShortLink));
                    if (!StringUtils.isBlank(list.get(0).getSoKeToan()))
                        messagDts.add(addMessageDt(noThue, list.get(0).getSoKeToan(), id, smsTemp, pdfFile, createShortLink));
                }
            } else {
                BaseThongBaoNoThue obj = new BaseThongBaoNoThue();
                obj.setError("Corporation \"" + noThue.getNguoiNopThue() + "\" does not have contact number");
                obj.setRowIndex(rowIndex);
                invalidList.add(obj);
            }
            rowIndex++;
        }
        return messagDts;
    }

    private List<SysMessagDt> loadMessages(List<BaseThongBaoNoThue> smsList, List<BaseThongBaoNoThue> phoneList, long id, String smsTemp, List<BaseThongBaoNoThue> invalidList, boolean createShortLink) {
        List<SysMessagDt> messagDts = new ArrayList<>();
        int rowIndex = 13;
        for (BaseThongBaoNoThue noThue : smsList) {
            List<BaseThongBaoNoThue> list = phoneList.stream().filter(it -> it.getMaSoThue() != null && it.getMaSoThue().equals(noThue.getMaSoThue())).collect(Collectors.toList());
            if (list != null && list.size() > 0) {
                if (StringUtils.isBlank(list.get(0).getSoGiamDoc()) && StringUtils.isBlank(list.get(0).getSoKeToan())) {
                    BaseThongBaoNoThue obj = new BaseThongBaoNoThue();
                    obj.setError("Corporation \"" + noThue.getNguoiNopThue() + "\" does not have contact number");
                    obj.setRowIndex(rowIndex);
                    invalidList.add(obj);
                }
                else {
                    if (!StringUtils.isBlank(list.get(0).getSoGiamDoc()))
                        messagDts.add(addMessageDt(noThue, list.get(0).getSoGiamDoc(), id, smsTemp));
                    if (!StringUtils.isBlank(list.get(0).getSoKeToan()))
                        messagDts.add(addMessageDt(noThue, list.get(0).getSoKeToan(), id, smsTemp));
                }
            } else {
                BaseThongBaoNoThue obj = new BaseThongBaoNoThue();
                obj.setError("Corporation \"" + noThue.getNguoiNopThue() + "\" does not have contact number");
                obj.setRowIndex(rowIndex);
                invalidList.add(obj);
            }
            rowIndex++;
        }
        return messagDts;
    }


    private SysMessagDt addMessageDt(BaseThongBaoNoThue noThue, String phone, long id, String smsTemp, String pdfFile, boolean createShortLink) {
        SysMessagDt dt = new SysMessagDt();
        dt.setTaxCode(noThue.getMaSoThue());
        dt.setCorName(noThue.getNguoiNopThue());
        dt.setCalledNumber(formatCalledNumber(phone));
        String sms = smsTemp;
        sms = sms.replace("{so_thong_bao}", noThue.getSoThongBao() == null ? "" : noThue.getSoThongBao());
        sms = sms.replace("{ngay_ban_hanh}", noThue.getNgayBanHanh() == null ? "" : noThue.getNgayBanHanh());
        sms = sms.replace("{nguoi_nop_thue}", noThue.getNguoiNopThue() == null ? "" : noThue.getNguoiNopThue());
        sms = sms.replace("{ma_so_thue}", noThue.getMaSoThue() == null ? "" : noThue.getMaSoThue());
        sms = sms.replace("{so_quyet_dinh}", noThue.getSoQuyetDinh() == null ? "" : noThue.getSoQuyetDinh());
        sms = sms.replace("{tu_ngay}", noThue.getTuNgay() == null ? "" : noThue.getTuNgay());
        sms = sms.replace("{den_ngay}", noThue.getDenNgay() == null ? "" : noThue.getDenNgay());
        sms = sms.replace("{so_tien_chua_nop}", StringUtils.thousandSeparator(noThue.getSoTienChuaNop(), ".") + " đồng");
        sms = sms.replace("{so_tien_qua_han}", StringUtils.thousandSeparator(noThue.getSoTienQuaHan(), ".") + " đồng");
        String token = JwtTokenForPdfFile.generateToken(id, dt.getCalledNumber(), pdfFile);
        if (createShortLink) {
            String url = StringUtils.getTinyUrl(Constants.IMAGE_URL_PATH + "?p=" + token);
            if(!url.equals(""))
                sms += (" See detail at: " + url);
            else
                sms += (" See detail at: " + Constants.IMAGE_URL_PATH + "?p=" + token);
        }
        else
            sms += (" See detail at: " + Constants.IMAGE_URL_PATH + "?p=" + token);

        dt.setSmsContent(sms);
        dt.setMessHdId(id);

        return dt;
    }

    private SysMessagDt addMessageDt(BaseThongBaoNoThue noThue, String phone, long id, String smsTemp) {
        SysMessagDt dt = new SysMessagDt();
        dt.setTaxCode(noThue.getMaSoThue());
        dt.setCorName(noThue.getNguoiNopThue());
        dt.setCalledNumber(formatCalledNumber(phone));
        String sms = smsTemp;
        sms = sms.replace("{so_thong_bao}", noThue.getSoThongBao() == null ? "" : noThue.getSoThongBao());
        sms = sms.replace("{ngay_ban_hanh}", noThue.getNgayBanHanh() == null ? "" : noThue.getNgayBanHanh());
        sms = sms.replace("{nguoi_nop_thue}", noThue.getNguoiNopThue() == null ? "" : noThue.getNguoiNopThue());
        sms = sms.replace("{ma_so_thue}", noThue.getMaSoThue() == null ? "" : noThue.getMaSoThue());
        sms = sms.replace("{so_quyet_dinh}", noThue.getSoQuyetDinh() == null ? "" : noThue.getSoQuyetDinh());
        sms = sms.replace("{tu_ngay}", noThue.getTuNgay() == null ? "" : noThue.getTuNgay());
        sms = sms.replace("{den_ngay}", noThue.getDenNgay() == null ? "" : noThue.getDenNgay());
        sms = sms.replace("{so_tien_chua_nop}", StringUtils.thousandSeparator(noThue.getSoTienChuaNop(), ".") + " đồng");
        sms = sms.replace("{so_tien_qua_han}", StringUtils.thousandSeparator(noThue.getSoTienQuaHan(), ".") + " đồng");

        dt.setSmsContent(sms);
        dt.setMessHdId(id);

        return dt;
    }

    private String formatCalledNumber(String phone) {
        phone = phone.replaceAll(" ", "");
        return phone.startsWith("0") ? phone.substring(1) : phone;
    }

    public String storeFile(MultipartFile file, String filename) throws Exception {
        InputStream inputStream = null;
        String fileName = filename + "_" + System.currentTimeMillis() + "." + FilenameUtils.getExtension(file.getOriginalFilename()).toLowerCase();
        try {
            String folder = DateTimeUtils.convertDateToString(new Date(), "MMyyyy");
            String filePath = Constants.FILE_PATH + folder;
            File filez = new File(filePath);
            if (!filez.exists())
                filez.mkdir();

            inputStream = file.getInputStream();
            Files.copy(inputStream, Paths.get(filePath).resolve(fileName), StandardCopyOption.REPLACE_EXISTING);

            fileName = "/" + folder + "/" + fileName;

        } catch (Exception ex) {
            ex.printStackTrace();
            fileName = "";
        } finally {
            if (inputStream != null)
                inputStream.close();
        }
        return fileName;
    }

    private Map<String, String> readPdfFile(MultipartFile[] files) {
        Map<String, String> map = new HashMap<>();
        try {
            //Tạo folder từng tháng lưu file pdf
            String folder = DateTimeUtils.convertDateToString(new Date(), "MMyyyy");
            String filePath = Constants.FILE_PDF_PATH + folder;
            File filez = new File(filePath);
            if (!filez.exists())
                filez.mkdir();

            //Đọc từng file pdf -> tách thành các file thông báo theo DN -> lưu vào hệ thống
            for (MultipartFile file : files) {
                InputStream input = null;
                PDDocument document = null;
                try {
                    input = file.getInputStream();
                    PdfDocument pdfDocument = new PdfDocument(new PdfReader(input));

                    document = PDDocument.load(file.getInputStream(), MemoryUsageSetting.setupTempFileOnly());
                    PDFRenderer pdfRenderer = new PDFRenderer(document);

                    int startPage = 1;
                    String taxCode = "";

                    for (int i = 1; i <= pdfDocument.getNumberOfPages(); i++) {
                        String text = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(i), new SimpleTextExtractionStrategy());
                        text = text.replace("\n", " ");
                        int index = text.indexOf("Tax number:");
                        if (index > 0) {
                            if (i > 1) {
//                                String fileName = taxCode + "_" + System.currentTimeMillis() + ".pdf";
//                                cropPdfFile( pdfDocument, filePath + "\\" + fileName, startPage, i - 1);
                                String fileName = taxCode + "_" + System.currentTimeMillis() + ".jpg";
                                generateImageFromPDF(pdfRenderer, startPage - 1, i - 1, filePath + "\\" + fileName);

                                map.put(taxCode, folder + "_" + fileName);
                            }
                            startPage = i;
                            text = text.substring(index + 12);
                            index = text.indexOf(" ");
                            taxCode = text.substring(0, index);
                        }
                    }

//                    String fileName = taxCode + "_" + System.currentTimeMillis() + ".pdf";
//                    cropPdfFile( pdfDocument, filePath + "\\" + fileName, startPage, pdfDocument.getNumberOfPages());
                    String fileName = taxCode + "_" + System.currentTimeMillis() + ".jpg";
                    generateImageFromPDF(pdfRenderer, startPage - 1, pdfDocument.getNumberOfPages(), filePath + "\\" + fileName);

                    map.put(taxCode, folder + "_" + fileName);

                    pdfDocument.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    if (input != null)
                        input.close();
                    if (document != null)
                        document.close();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    private String zipMultipleFiles(MultipartFile[] files, String filename) {
        String fileName = filename + "_" + System.currentTimeMillis() + ".zip";
        FileOutputStream fos = null;
        InputStream fis = null;
        try {
            String folder = DateTimeUtils.convertDateToString(new Date(), "MMyyyy");
            String filePath = Constants.FILE_PATH + folder;
            File filez = new File(filePath);
            if (!filez.exists())
                filez.mkdir();

            fos = new FileOutputStream(filePath + "\\" + fileName);
            ZipOutputStream zipOut = new ZipOutputStream(fos);
            for (MultipartFile file : files) {
                fis = file.getInputStream();
                ZipEntry zipEntry = new ZipEntry(file.getOriginalFilename());
                zipOut.putNextEntry(zipEntry);

                byte[] bytes = new byte[1024];
                int length;
                while ((length = fis.read(bytes)) >= 0) {
                    zipOut.write(bytes, 0, length);
                }
                fis.close();
            }
            zipOut.close();
            return ("/" + folder + "/" + fileName);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (fos != null)
                    fos.close();
                if (fis != null)
                    fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    private void generateImageFromPDF(PDFRenderer pdfRenderer, int startPage, int endPage, String pngName) throws IOException {
        BufferedImage joinBufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
//        page bắt đầu từ 0
        for (int page = startPage; page < endPage; ++page) {
            BufferedImage bim = pdfRenderer.renderImageWithDPI(page, IMAGE_DPI, ImageType.RGB);   //dpi:300,150,72
            joinBufferedImage = joinBufferedImage(joinBufferedImage, bim);
        }
        ImageIOUtil.writeImage(joinBufferedImage, pngName, IMAGE_DPI);
    }

    public BufferedImage joinBufferedImage(BufferedImage img1, BufferedImage img2) {
        //do some calculate first
        int offset = 5;
        int wid = Math.max(img1.getWidth(), img2.getWidth());
        int height = img1.getHeight() + img2.getHeight() + offset;
        //create a new buffer and draw two image into the new image
        BufferedImage newImage = new BufferedImage(wid, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = newImage.createGraphics();
        Color oldColor = g2.getColor();
        //fill background
        g2.setPaint(Color.GRAY);
        g2.fillRect(0, 0, wid, height);
        //draw image
        g2.setColor(oldColor);
        g2.drawImage(img1, null, 0, 0);
        g2.drawImage(img2, null, 0, img1.getHeight() + offset);
        g2.dispose();
        return newImage;
    }
}
