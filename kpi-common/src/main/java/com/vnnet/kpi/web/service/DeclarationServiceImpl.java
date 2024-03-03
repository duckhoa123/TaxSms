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
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Service
@Transactional
public class DeclarationServiceImpl implements DeclarationService {

    private static final Logger logger = LoggerFactory.getLogger(DeclarationServiceImpl.class);

    private static int THONG_BAO_SO_NOP = 2;
    private static int THONG_BAO_NHAC_NOP = 3;
    private static int THONG_BAO_YEU_CAU_NOP = 4;

    private static int IMAGE_DPI = 76; //dpi: 300, 150, 76

    @Autowired
    private SysMessagHdMapper sysMessagHdMapper;
    @Autowired
    private SysMessagDtMapper sysMessagDtMapper;


    @Override
    public HttpResult save(MultipartFile file1, MultipartFile file2, MultipartFile file3, SysMessagHd sysMessagHd) {
        if (sysMessagHd.getProgramId() == THONG_BAO_SO_NOP)
            return savePaymentNumber(file1, file2, sysMessagHd);
        else if (sysMessagHd.getProgramId() == THONG_BAO_NHAC_NOP || sysMessagHd.getProgramId() == THONG_BAO_YEU_CAU_NOP)
            return savePaymentLateOrPaymentReminber(file1, file2, file3, sysMessagHd);
        else
            return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, " Couldn't find  valid program.");
    }

    private HttpResult savePaymentNumber(MultipartFile file1, MultipartFile file2, SysMessagHd sysMessagHd) {
        try {
            List<BaseThongBaoKeKhai> validList1 = new ArrayList<>();
            List<BaseThongBaoKeKhai> validList2 = new ArrayList<>();
            List<BaseThongBaoKeKhai> invalidList1 = new ArrayList<>();
            List<BaseThongBaoKeKhai> invalidList2 = new ArrayList<>();

            //Đọc file excel
            String str1 = loadDataFromExcelPaymentNumber(file1, validList1, invalidList1);
            String str2 = loadDataFromExcel2(file2, validList2, invalidList2);


            if (!StringUtils.isBlank(str1))
                return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, str1);
            if (!StringUtils.isBlank(str2))
                return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, str2);

            if (invalidList1.size() > 0)
                return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Notification file  error . (" + file1.getOriginalFilename() + "). Please see the table below for more details.", invalidList1);
            if (invalidList2.size() > 0)
                return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Address book file error (" + file2.getOriginalFilename() + "). Please see the table below for more details.", invalidList2);

            //Lưu file excel và pdf
            String fileName1 = storeFile(file1, "ke_khai_chi_tiet");
            String fileName2 = storeFile(file2, "ke_khai_danh_ba");

            if (fileName1.equals("") || fileName2.equals(""))
                return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Error saving uploaded file. Please try again later");

            sysMessagHd.setDetailFile(fileName1);
            sysMessagHd.setContactFile(fileName2);

            sysMessagHd.setDelFlag((byte) 0);
            int result = sysMessagHdMapper.insertSelective(sysMessagHd);
            if (result >= 1) {
                List<BaseThongBaoKeKhai> invalidList3 = new ArrayList<>();
                List<SysMessagDt> sysMessagDts = loadMessages(validList1, validList2, sysMessagHd.getMessHdId(), sysMessagHd.getSmsTemplate(), invalidList3);
//                if(invalidList3.size() > 0) {
//                    sysMessagHdMapper.deleteByPrimaryKey(sysMessagHd.getMessHdId());
//                    return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Lỗi lấy thông tin SĐT doanh nghiệp. Vui lòng xem bảng dưới để biết thêm chi tiết", invalidList3);
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

    private HttpResult savePaymentLateOrPaymentReminber(MultipartFile file1, MultipartFile file2, MultipartFile file3, SysMessagHd sysMessagHd) {
        try {
            List<BaseThongBaoKeKhai> validList1 = new ArrayList<>();
            List<BaseThongBaoKeKhai> validList2 = new ArrayList<>();
            List<BaseThongBaoKeKhai> invalidList1 = new ArrayList<>();
            List<BaseThongBaoKeKhai> invalidList2 = new ArrayList<>();

            //Đọc file excel
            String str1 = "";
            if (sysMessagHd.getProgramId() == THONG_BAO_NHAC_NOP)
                str1 = loadDataFromExcelPaymentReminder(file1, validList1, invalidList1);
            else if (sysMessagHd.getProgramId() == THONG_BAO_YEU_CAU_NOP)
                str1 = loadDataFromExcelPaymentLate(file1, validList1, invalidList1);
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
                return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Error reading Zip notification file ");

            if (invalidList1.size() > 0)
                return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Notification file error (" + file1.getOriginalFilename() + "). Please see the table below for more details", invalidList1);
            if (invalidList2.size() > 0)
                return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Address book file error (" + file2.getOriginalFilename() + "). Please see the table below for more details", invalidList2);

            //Lưu file excel và pdf
            String fileName1 = storeFile(file1, "ke_khai_chi_tiet");
            String fileName2 = storeFile(file2, "ke_khai_danh_ba");
            String fileName3 = storeFile(file3, "ke_khai_thong_bao");

            if (fileName1.equals("") || fileName2.equals("") || fileName3.equals(""))
                return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Error saving uploaded file. Please try again later");

            sysMessagHd.setDetailFile(fileName1);
            sysMessagHd.setContactFile(fileName2);
            sysMessagHd.setPdfFile(fileName3);

            sysMessagHd.setDelFlag((byte) 0);
            int result = sysMessagHdMapper.insertSelective(sysMessagHd);
            if (result >= 1) {
                List<BaseThongBaoKeKhai> invalidList3 = new ArrayList<>();
                List<SysMessagDt> sysMessagDts = loadMessages(validList1, validList2, sysMessagHd.getMessHdId(), sysMessagHd.getSmsTemplate(), invalidList3, pdfList, sysMessagHd.getCreateShortLink());
//                if(invalidList3.size() > 0) {
//                    sysMessagHdMapper.deleteByPrimaryKey(sysMessagHd.getMessHdId());
//                    return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Lỗi lấy thông tin SĐT doanh nghiệp. Vui lòng xem bảng dưới để biết thêm chi tiết", invalidList3);
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
        return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "An error occurred. Please try again later.");
    }

    private String loadDataFromExcelPaymentNumber(MultipartFile file, List<BaseThongBaoKeKhai> validList, List<BaseThongBaoKeKhai> invalidList) {
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
                if (_row == 0) {
                    Iterator<Cell> cellIterator = row.cellIterator();
                    String title = "";
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        title = checkHeaderExcelPaymentNumber(_col, cell);
                        if (!title.equals(""))
                            break;
                        _col++;
                    }
                    if (!title.equals(""))
                        return "Detailed file (" + file.getOriginalFilename() + ") missing column \"" + title + "\". Please select the file again";
                }
                else {
                    Iterator<Cell> cellIterator = row.cellIterator();
                    _col = 0;
                    BaseThongBaoKeKhai hd = new BaseThongBaoKeKhai();
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        Object cellValue = getCellValue(cell);
                        switch (_col) {
                            case 0:
                                hd.setMaSoThue(String.valueOf(cellValue));
                                break;
                            case 1:
                                hd.setNguoiNopThue(String.valueOf(cellValue));
                                break;
                            case 3:
                                hd.setTieuMuc(String.valueOf(cellValue));
                                break;
                            case 4:
                                try {
                                    if (StringUtils.isBlank(String.valueOf(cellValue)))
                                        hd.setError("Missing amount information");
                                    else
                                        hd.setSoTien((long) cell.getNumericCellValue());
                                } catch (Exception ex) {
                                    hd.setError("The amount value is not a numerical");
                                }
                                break;
                            case 5:
                                if (StringUtils.isBlank(String.valueOf(cellValue)))
                                    hd.setNgayNop("");
                                else if (cellValue instanceof Long && DateUtil.isCellDateFormatted(cell))
                                    hd.setNgayNop(DateTimeUtils.convertDateToString(cell.getDateCellValue(), "dd/MM/yyyy"));
                                else if (DateTimeUtils.isValid(String.valueOf(cellValue), "dd/MM/yyyy"))
                                    hd.setNgayNop(String.valueOf(cellValue));
                                else
                                    hd.setError("The tax filing date information is invalid or does not use the format DD/MM/YYYY");
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

    private String loadDataFromExcelPaymentReminder(MultipartFile file, List<BaseThongBaoKeKhai> validList, List<BaseThongBaoKeKhai> invalidList) {
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

                if (_row == 0) {
                    Iterator<Cell> cellIterator = row.cellIterator();
                    String title = "";
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        title = checkHeaderExcelPaymentReminder(_col, cell);
                        if (!title.equals(""))
                            break;
                        _col++;
                    }
                    if (!title.equals(""))
                        return "Detail file (" + file.getOriginalFilename() + ") missing column \"" + title + "\". Please select the file again";
                } else {
                    Iterator<Cell> cellIterator = row.cellIterator();
                    _col = 1;
                    Cell index = cellIterator.next();
                    if (StringUtils.isBlank(getCellValue(index).toString())) {
                        _row++;
                        break;
                    }
                    BaseThongBaoKeKhai hd = new BaseThongBaoKeKhai();
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
                            case 8:
                                if (StringUtils.isBlank(String.valueOf(cellValue)))
                                    hd.setError("Missing corporation information");
                                else
                                    hd.setNguoiNopThue(String.valueOf(cellValue));
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

    private String loadDataFromExcelPaymentLate(MultipartFile file, List<BaseThongBaoKeKhai> validList, List<BaseThongBaoKeKhai> invalidList) {
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

                if (_row == 0) {
                    Iterator<Cell> cellIterator = row.cellIterator();
                    String title = "";
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        title = checkHeaderExcelPaymentLate(_col, cell);
                        if (!title.equals(""))
                            break;
                        _col++;
                    }
                    if (!title.equals(""))
                        return "Detail file (" + file.getOriginalFilename() + ") missing column \"" + title + "\". Please select the file again";
                } else {
                    Iterator<Cell> cellIterator = row.cellIterator();
                    _col = 1;
                    Cell index = cellIterator.next();
                    if (StringUtils.isBlank(getCellValue(index).toString())) {
                        _row++;
                        break;
                    }
                    BaseThongBaoKeKhai hd = new BaseThongBaoKeKhai();
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
                            case 8:
                                if (StringUtils.isBlank(String.valueOf(cellValue)))
                                    hd.setError("Missing corporation information");
                                else
                                    hd.setNguoiNopThue(String.valueOf(cellValue));
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

    private String loadDataFromExcel2(MultipartFile file, List<BaseThongBaoKeKhai> validList, List<BaseThongBaoKeKhai> invalidList) {
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
                    BaseThongBaoKeKhai hd = new BaseThongBaoKeKhai();
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

    private String checkHeaderExcelPaymentNumber(int col, Cell cell) {
        if (col == 0 && !getCellValue(cell).toString().equals("Mã số thuế"))
            return "Mã số thuế";//tax number
        else if (col == 1 && !getCellValue(cell).toString().equals("Tên NNT"))
            return "Tên NNT";//name of sender
        else if (col == 2 && !getCellValue(cell).toString().equals("Chương"))
            return "Chương";//page
        else if (col == 3 && !getCellValue(cell).toString().contains("Tiểu mục"))
            return "Tiểu mục";//subsection
        else if (col == 4 && !getCellValue(cell).toString().contains("Số tiền"))
            return "Số tiền";//amount
        else if (col == 5 && !getCellValue(cell).toString().equals("Ngày nộp thuế"))
            return "Ngày nộp thuế";//tax filing date

        return "";
    }

    private String checkHeaderExcelPaymentReminder(int col, Cell cell) {
        if (col == 0 && !getCellValue(cell).toString().contains("STT"))
            return "STT";//order number
        else if (col == 1 && !getCellValue(cell).toString().contains("Mã số thuế"))
            return "Mã số thuế";//tax number
        else if (col == 2 && !getCellValue(cell).toString().contains("Tên hồ sơ"))
            return "Tên hồ sơ";//case name
        else if (col == 8 && !getCellValue(cell).toString().contains("Tên NNT"))
            return "Tên NNT";//taxpayer name

        return "";
    }

    private String checkHeaderExcelPaymentLate(int col, Cell cell) {
        if (col == 0 && !getCellValue(cell).toString().contains("STT"))
            return "STT";
        else if (col == 1 && !getCellValue(cell).toString().contains("Mã số thuế"))
            return "Mã số thuế";
        else if (col == 2 && !getCellValue(cell).toString().contains("Tên hồ sơ"))
            return "Tên hồ sơ";
        else if (col == 8 && !getCellValue(cell).toString().contains("Tên NNT"))
            return "Tên NNT";

        return "";
    }

    private String checkHeaderExcel2(int col, Cell cell) {
        if (col == 1 && !getCellValue(cell).toString().equals("Mã số thuế"))
            return "Mã số thuế";
        else if (col == 2 && !getCellValue(cell).toString().equals("Tên NNT"))
            return "Tên NNT";
        else if (col == 3 && !getCellValue(cell).toString().equals("Điện thoại giám đốc"))
            return "Điện thoại giám đốc";//phone number of director
        else if (col == 4 && !getCellValue(cell).toString().equals("Điện thoại kế toán trưởng"))
            return "Điện thoại kế toán trưởng";//phone number of chief accountant

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

    private List<SysMessagDt> loadMessages(List<BaseThongBaoKeKhai> smsList, List<BaseThongBaoKeKhai> phoneList, long id, String smsTemp, List<BaseThongBaoKeKhai> invalidList, Map<String, String> pdfList, boolean createShortLink) {
        List<SysMessagDt> messagDts = new ArrayList<>();
        int rowIndex = 13;
        for (BaseThongBaoKeKhai noThue : smsList) {
            List<BaseThongBaoKeKhai> list = phoneList.stream().filter(it -> it.getMaSoThue() != null && it.getMaSoThue().equals(noThue.getMaSoThue())).collect(Collectors.toList());
            String pdfFile = pdfList.get(noThue.getMaSoThue());
            if (list != null && list.size() > 0) {
                if (StringUtils.isBlank(list.get(0).getSoGiamDoc()) && StringUtils.isBlank(list.get(0).getSoKeToan())) {
                    BaseThongBaoKeKhai obj = new BaseThongBaoKeKhai();
                    obj.setError("Corporation \"" + noThue.getNguoiNopThue() + "\" don't have phone number information");
                    obj.setRowIndex(rowIndex);
                    invalidList.add(obj);
                } else if (StringUtils.isBlank(pdfFile)) {
                    BaseThongBaoKeKhai obj = new BaseThongBaoKeKhai();
                    obj.setError("Corporation \"" + noThue.getNguoiNopThue() + "\" don't have notify pdf file");
                    obj.setRowIndex(rowIndex);
                    invalidList.add(obj);
                } else {
                    if (!StringUtils.isBlank(list.get(0).getSoGiamDoc()))
                        messagDts.add(addMessageDt(noThue, list.get(0).getSoGiamDoc(), id, smsTemp, pdfFile, createShortLink));
                    if (!StringUtils.isBlank(list.get(0).getSoKeToan()))
                        messagDts.add(addMessageDt(noThue, list.get(0).getSoKeToan(), id, smsTemp, pdfFile, createShortLink));
                }
            } else {
                BaseThongBaoKeKhai obj = new BaseThongBaoKeKhai();
                obj.setError("Corporation \"" + noThue.getNguoiNopThue() + "\" don't have phone number information");
                obj.setRowIndex(rowIndex);
                invalidList.add(obj);
            }
            rowIndex++;
        }
        return messagDts;
    }

    private List<SysMessagDt> loadMessages(List<BaseThongBaoKeKhai> smsList, List<BaseThongBaoKeKhai> phoneList, long id, String smsTemp, List<BaseThongBaoKeKhai> invalidList) {
        List<SysMessagDt> messagDts = new ArrayList<>();
        int rowIndex = 2;

        BaseThongBaoKeKhai ett = new BaseThongBaoKeKhai();
        StringJoiner smsDetail = new StringJoiner(",");
        for (BaseThongBaoKeKhai keKhai : smsList) {
            if (StringUtils.isBlank(keKhai.getMaSoThue()))
                break;
            else if (!StringUtils.isBlank(keKhai.getNguoiNopThue()) && (ett.getMaSoThue() == null || keKhai.getMaSoThue().equals(ett.getMaSoThue()))) {
                ett = keKhai;
                smsDetail.add("TM " + keKhai.getTieuMuc() + " " + StringUtils.thousandSeparator(keKhai.getSoTien(), ".") + " đồng");
            } else {
                List<BaseThongBaoKeKhai> list = phoneList.stream().filter(it -> it.getMaSoThue() != null && it.getMaSoThue().equals(keKhai.getMaSoThue())).collect(Collectors.toList());
                if (list != null && list.size() > 0) {
                    if (StringUtils.isBlank(list.get(0).getSoGiamDoc()) && StringUtils.isBlank(list.get(0).getSoKeToan())) {
                        BaseThongBaoKeKhai obj = new BaseThongBaoKeKhai();
                        obj.setError("Corporation \"" + ett.getNguoiNopThue() + "\" don't have phone number information");
                        obj.setRowIndex(rowIndex);
                        invalidList.add(obj);
                    } else {
                        if (!StringUtils.isBlank(list.get(0).getSoGiamDoc()))
                            messagDts.add(addMessageDt(ett, list.get(0).getSoGiamDoc(), id, smsTemp, keKhai.getSoTien(), smsDetail.toString()));
                        if (!StringUtils.isBlank(list.get(0).getSoKeToan()))
                            messagDts.add(addMessageDt(ett, list.get(0).getSoKeToan(), id, smsTemp, keKhai.getSoTien(), smsDetail.toString()));
                    }
                } else {
                    BaseThongBaoKeKhai obj = new BaseThongBaoKeKhai();
                    obj.setError("Corporation \"" + ett.getNguoiNopThue() + "\" don't have phone number information");
                    obj.setRowIndex(rowIndex);
                    invalidList.add(obj);
                }
                smsDetail = new StringJoiner(",");
                ett = new BaseThongBaoKeKhai();
            }
            rowIndex++;
        }
        return messagDts;
    }


    private SysMessagDt addMessageDt(BaseThongBaoKeKhai keKhai, String phone, long id, String smsTemp, String pdfFile, boolean createShortLink) {
        SysMessagDt dt = new SysMessagDt();
        dt.setTaxCode(keKhai.getMaSoThue());
        dt.setCorName(keKhai.getNguoiNopThue());
        dt.setCalledNumber(formatCalledNumber(phone));
        String sms = smsTemp;
        sms = sms.replace("{ma_so_thue}", keKhai.getMaSoThue() == null ? "" : keKhai.getMaSoThue());
        sms = sms.replace("{nguoi_nop_thue}", keKhai.getNguoiNopThue() == null ? "" : keKhai.getNguoiNopThue());
        String token = JwtTokenForPdfFile.generateToken(id, dt.getCalledNumber(), pdfFile);
        if (createShortLink) {
            String url = StringUtils.getTinyUrl(Constants.PDF_URL_PATH + "?p=" + token);
            if(!url.equals(""))
                sms += (" See details at: " + url);
            else
                sms += (" See details at: " + Constants.PDF_URL_PATH + "?p=" + token);
        }
        else
            sms += (" See details at: " + Constants.PDF_URL_PATH + "?p=" + token);

        dt.setSmsContent(sms);
        dt.setMessHdId(id);

        return dt;
    }

    private SysMessagDt addMessageDt(BaseThongBaoKeKhai keKhai, String phone, long id, String smsTemp, long total, String detail) {
        SysMessagDt dt = new SysMessagDt();
        dt.setTaxCode(keKhai.getMaSoThue());
        dt.setCorName(keKhai.getNguoiNopThue());
        dt.setCalledNumber(formatCalledNumber(phone));
        String sms = smsTemp;
        sms = sms.replace("{ngay_nop}", keKhai.getNgayNop() == null ? "" : keKhai.getNgayNop());
        sms = sms.replace("{nguoi_nop_thue}", keKhai.getNguoiNopThue() == null ? "" : keKhai.getNguoiNopThue());
        sms = sms.replace("{ma_so_thue}", keKhai.getMaSoThue() == null ? "" : keKhai.getMaSoThue());
        sms = sms.replace("{so_tien}", StringUtils.thousandSeparator(total, ".") + " đồng");
        sms = sms.replace("{chi_tiet}", detail);
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

    private Map<String, String> readPdfFile(MultipartFile file) {
        Map<String, String> map = new HashMap<>();
        ZipInputStream zis = null;
        FileOutputStream fileOutputStream = null;
        byte[] buffer = new byte[1024];
        try {
            //Tạo folder từng tháng lưu file pdf
            String folder = DateTimeUtils.convertDateToString(new Date(), "MMyyyy");
            String filePath = Constants.FILE_PDF_PATH + folder;
            File filez = new File(filePath);
            if (!filez.exists())
                filez.mkdir();

            zis = new ZipInputStream(file.getInputStream());

            String taxCode = "";
            ZipEntry entry;
            while((entry = zis.getNextEntry()) != null) {
                String fileName = entry.getName();
                String[] split = fileName.split("-");
                if(split.length == 5)
                    taxCode = split[0];
                else if(split.length == 6)
                    taxCode = split[0] + "-" + split[1];
                else
                    taxCode = "";

                String pdf = taxCode + "_" + System.currentTimeMillis() + ".pdf";

                fileOutputStream = new FileOutputStream(filePath + "/" + pdf);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fileOutputStream.write(buffer, 0, len);
                }

                fileOutputStream.close();
                zis.closeEntry();
                map.put(taxCode, "/" + folder + "/" + pdf);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {
            if(zis != null) {
                try {
                    zis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return map;
    }

}
