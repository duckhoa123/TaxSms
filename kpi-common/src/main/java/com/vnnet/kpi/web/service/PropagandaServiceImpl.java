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
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
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

import javax.imageio.ImageIO;
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
public class PropagandaServiceImpl implements PropagandaService {

    private static final Logger logger = LoggerFactory.getLogger(PropagandaServiceImpl.class);

    private static int IMAGE_DPI = 150; //dpi: 300, 150, 76

    @Autowired
    private SysMessagHdMapper sysMessagHdMapper;
    @Autowired
    private SysMessagDtMapper sysMessagDtMapper;


    @Override
    public HttpResult save(MultipartFile file1, MultipartFile file2, MultipartFile[] file3, SysMessagHd sysMessagHd) {
        try {
            List<BaseThongBaoTuyenTruyen> validList1 = new ArrayList<>();
            List<BaseThongBaoTuyenTruyen> validList2 = new ArrayList<>();
            List<BaseThongBaoTuyenTruyen> invalidList1 = new ArrayList<>();
            List<BaseThongBaoTuyenTruyen> invalidList2 = new ArrayList<>();

            //Đọc file excel
            String str1 = loadDataFromExcel1(file1, validList1, invalidList1);
            String str2 = loadDataFromExcel2(file2, validList2, invalidList2);

            //Đọc file pdf
            Map<String, String> pdfList = readPdfFile(file3);

            if (!StringUtils.isBlank(str1))
                return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, str1);
            if (!StringUtils.isBlank(str2))
                return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, str2);
            if (pdfList.isEmpty())
                return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Error reading pdf Notification file ");

            if (invalidList1.size() > 0)
                return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Notification file error (" + file1.getOriginalFilename() + "). Vui lòng xem bảng dưới để biết thêm chi tiết", invalidList1);
            if (invalidList2.size() > 0)
                return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Address book file error (" + file2.getOriginalFilename() + "). Vui lòng xem bảng dưới để biết thêm chi tiết", invalidList2);

            //Lưu file excel và pdf
            String fileName1 = storeFile(file1, "tuyen_truyen_chi_tiet");
            String fileName2 = storeFile(file2, "tuyen_truyen_danh_ba");
            String fileName3 = zipMultipleFiles(file3, "tuyen_truyen_thong_bao");

            if (fileName1.equals("") || fileName2.equals("") || fileName3.equals(""))
                return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Error saving uploaded file. Please try again later");

            sysMessagHd.setDetailFile(fileName1);
            sysMessagHd.setContactFile(fileName2);
            sysMessagHd.setPdfFile(fileName3);

            sysMessagHd.setDelFlag((byte) 0);
            int result = sysMessagHdMapper.insertSelective(sysMessagHd);
            if (result >= 1) {
                List<BaseThongBaoTuyenTruyen> invalidList3 = new ArrayList<>();
                List<SysMessagDt> sysMessagDts = loadMessages(validList1, validList2, sysMessagHd.getMessHdId(), sysMessagHd.getSmsTemplate(), invalidList3, pdfList, sysMessagHd.getCreateShortLink());
//                if(invalidList3.size() > 0) {
//                    sysMessagHdMapper.deleteByPrimaryKey(sysMessagHd.getMessHdId());
//                    return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Lỗi lấy thông tin SĐT doanh nghiệp. Vui lòng xem bảng dưới để biết thêm chi tiết", invalidList3);
//                }
                for (SysMessagDt messagDt : sysMessagDts) {
                    sysMessagDtMapper.insertSelective(messagDt);
                }
                return HttpResult.ok("Message schedule created successfully. Total " + sysMessagDts.size() + " tin nhắn", invalidList3);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
        }
        return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "An error occurred. please Try again later.");
    }

    private String loadDataFromExcel1(MultipartFile file, List<BaseThongBaoTuyenTruyen> validList, List<BaseThongBaoTuyenTruyen> invalidList) {
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
                        title = checkHeaderExcel1(_col, cell);
                        if (!title.equals(""))
                            break;
                        _col++;
                    }
                    if (!title.equals(""))
                        return "Detail file (" + file.getOriginalFilename() + ") missing column \"" + title + "\". Please select file again";
                } else if (_row >= 9) {
                    Iterator<Cell> cellIterator = row.cellIterator();
                    _col = 1;
                    Cell index = cellIterator.next();
                    if (StringUtils.isBlank(getCellValue(index).toString())) {
                        _row++;
                        break;
                    }
                    BaseThongBaoTuyenTruyen hd = new BaseThongBaoTuyenTruyen();
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        Object cellValue = getCellValue(cell);
                        switch (_col) {
                            case 1:
                                if (StringUtils.isBlank(String.valueOf(cellValue)))
                                    hd.setError("Thiếu thông tin người nộp thuế");
                                else
                                    hd.setNguoiNopThue(String.valueOf(cellValue));
                                break;
                            case 2:
                                if (StringUtils.isBlank(String.valueOf(cellValue)))
                                    hd.setError("Thiếu thông tin mã số thuế");
                                else
                                    hd.setMaSoThue(String.valueOf(cellValue));
                                break;
                            case 3:
                                if (StringUtils.isBlank(String.valueOf(cellValue)))
                                    hd.setError("Thiếu thông tin số CV hỏi");
                                else
                                    hd.setSoCVHoi(String.valueOf(cellValue));
                                break;
                            case 4:
                                if (StringUtils.isBlank(String.valueOf(cellValue)))
                                    hd.setError("Thiếu thông tin Ngày CV hỏi");
                                else {
                                    if (cellValue instanceof Long && DateUtil.isCellDateFormatted(cell))
                                        hd.setNgayHoi(DateTimeUtils.convertDateToString(cell.getDateCellValue(), "dd/MM/yyyy"));
                                    else if (DateTimeUtils.isValid(String.valueOf(cellValue), "dd/MM/yyyy"))
                                        hd.setNgayHoi(String.valueOf(cellValue));
                                    else
                                        hd.setError("Thông tin Ngày CV hỏi không hợp lệ hoặc không sử dụng định dạng DD/MM/YYYY");
                                }
                                break;
                            case 5:
                                if (StringUtils.isBlank(String.valueOf(cellValue)))
                                    hd.setError("Thiếu thông tin Số CV trả lời");
                                else
                                    hd.setSoCVTraLoi(String.valueOf(cellValue));
                                break;
                            case 6:
                                if (StringUtils.isBlank(String.valueOf(cellValue)))
                                    hd.setError("Thiếu thông tin Ngày CV trả lời");
                                else {
                                    if (cellValue instanceof Long && DateUtil.isCellDateFormatted(cell))
                                        hd.setNgayTraLoi(DateTimeUtils.convertDateToString(cell.getDateCellValue(), "dd/MM/yyyy"));
                                    else if (DateTimeUtils.isValid(String.valueOf(cellValue), "dd/MM/yyyy"))
                                        hd.setNgayTraLoi(String.valueOf(cellValue));
                                    else
                                        hd.setError("Thông tin Ngày CV trả lời không hợp lệ hoặc không sử dụng định dạng DD/MM/YYYY");
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

    private String loadDataFromExcel2(MultipartFile file, List<BaseThongBaoTuyenTruyen> validList, List<BaseThongBaoTuyenTruyen> invalidList) {
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
                        return "File danh bạ (" + file.getOriginalFilename() + ") missing column \"" + title + "\". Please select file again";
                    _row++;
                } else if (_row == 1) {
                    //Bỏ qua dòng đầu tiên dữ liệu mẫu
                    _row++;
                    continue;
                } else {
                    _col = 0;
                    BaseThongBaoTuyenTruyen hd = new BaseThongBaoTuyenTruyen();
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

    private String checkHeaderExcel1(int col, Cell cell) {
        if (col == 0 && !getCellValue(cell).toString().contains("STT"))
            return "STT";
        else if (col == 1 && !getCellValue(cell).toString().contains("Tên công ty"))
            return "Tên công ty";
        else if (col == 2 && !getCellValue(cell).toString().contains("Mã số thuế"))
            return "Mã số thuế";
        else if (col == 3 && !getCellValue(cell).toString().contains("Số CV hỏi"))
            return "Số CV hỏi";
        else if (col == 4 && !getCellValue(cell).toString().contains("Ngày CV hỏi"))
            return "Ngày CV hỏi";
        else if (col == 5 && !getCellValue(cell).toString().contains("Số CV trả lời"))
            return "Số CV trả lời";
        else if (col == 6 && !getCellValue(cell).toString().contains("Ngày CV trả lời"))
            return "Ngày CV trả lời";

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

    private List<SysMessagDt> loadMessages(List<BaseThongBaoTuyenTruyen> smsList, List<BaseThongBaoTuyenTruyen> phoneList, long id, String smsTemp, List<BaseThongBaoTuyenTruyen> invalidList, Map<String, String> pdfList, boolean createShortLink) {
        List<SysMessagDt> messagDts = new ArrayList<>();
        int rowIndex = 13;
        for (BaseThongBaoTuyenTruyen noThue : smsList) {
            List<BaseThongBaoTuyenTruyen> list = phoneList.stream().filter(it -> it.getMaSoThue() != null && it.getMaSoThue().equals(noThue.getMaSoThue())).collect(Collectors.toList());
            String pdfFile = pdfList.get(noThue.getMaSoThue());
            if (list != null && list.size() > 0) {
                if (StringUtils.isBlank(list.get(0).getSoGiamDoc()) && StringUtils.isBlank(list.get(0).getSoKeToan())) {
                    BaseThongBaoTuyenTruyen obj = new BaseThongBaoTuyenTruyen();
                    obj.setError("Doanh nghiệp \"" + noThue.getNguoiNopThue() + "\" không có thông tin điện thoại");
                    obj.setRowIndex(rowIndex);
                    invalidList.add(obj);
                } else if (StringUtils.isBlank(pdfFile)) {
                    BaseThongBaoTuyenTruyen obj = new BaseThongBaoTuyenTruyen();
                    obj.setError("Doanh nghiệp \"" + noThue.getNguoiNopThue() + "\" không có file pdf thông báo");
                    obj.setRowIndex(rowIndex);
                    invalidList.add(obj);
                } else {
                    if (!StringUtils.isBlank(list.get(0).getSoGiamDoc()))
                        messagDts.add(addMessageDt(noThue, list.get(0).getSoGiamDoc(), id, smsTemp, pdfFile, createShortLink));
                    if (!StringUtils.isBlank(list.get(0).getSoKeToan()))
                        messagDts.add(addMessageDt(noThue, list.get(0).getSoKeToan(), id, smsTemp, pdfFile, createShortLink));
                }
            } else {
                BaseThongBaoTuyenTruyen obj = new BaseThongBaoTuyenTruyen();
                obj.setError("Doanh nghiệp \"" + noThue.getNguoiNopThue() + "\" không có thông tin điện thoại");
                obj.setRowIndex(rowIndex);
                invalidList.add(obj);
            }
            rowIndex++;
        }
        return messagDts;
    }

    private SysMessagDt addMessageDt(BaseThongBaoTuyenTruyen tuyenTruyen, String phone, long id, String smsTemp, String pdfFile, boolean createShortLink) {
        SysMessagDt dt = new SysMessagDt();
        dt.setTaxCode(tuyenTruyen.getMaSoThue());
        dt.setCorName(tuyenTruyen.getNguoiNopThue());
        dt.setCalledNumber(formatCalledNumber(phone));
        String sms = smsTemp;
        sms = sms.replace("{nguoi_nop_thue}", tuyenTruyen.getNguoiNopThue() == null ? "" : tuyenTruyen.getNguoiNopThue());
        sms = sms.replace("{ma_so_thue}", tuyenTruyen.getMaSoThue() == null ? "" : tuyenTruyen.getMaSoThue());
        sms = sms.replace("{so_cong_van_tra_loi}", tuyenTruyen.getSoCVTraLoi() == null ? "" : tuyenTruyen.getSoCVTraLoi());
        sms = sms.replace("{ngay_cong_van_tra_loi}", tuyenTruyen.getNgayTraLoi() == null ? "" : tuyenTruyen.getNgayTraLoi());
        sms = sms.replace("{so_cong_van_hoi}", tuyenTruyen.getSoCVHoi() == null ? "" : tuyenTruyen.getSoCVHoi());
        sms = sms.replace("{ngay_cong_van_hoi}", tuyenTruyen.getNgayHoi() == null ? "" : tuyenTruyen.getNgayHoi());
        String token = JwtTokenForPdfFile.generateToken(id, dt.getCalledNumber(), pdfFile);
        if (createShortLink) {
            String url = StringUtils.getTinyUrl(Constants.PDF_URL_PATH + "?p=" + token);
            if(!url.equals(""))
                sms += (" Chi tiết xem tại: " + url);
            else
                sms += (" Chi tiết xem tại: " + Constants.PDF_URL_PATH + "?p=" + token);
        }
        else
            sms += (" Chi tiết xem tại: " + Constants.PDF_URL_PATH + "?p=" + token);

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

            //Đọc từng file pdf
            for (MultipartFile file : files) {
                InputStream input = null;
                PDDocument document = null;
                try {
                    input = file.getInputStream();
                    document = PDDocument.load(file.getInputStream(), MemoryUsageSetting.setupTempFileOnly());
                    PDFRenderer pdfRenderer = new PDFRenderer(document);

                    BufferedImage bim = pdfRenderer.renderImageWithDPI(0, IMAGE_DPI, ImageType.RGB);
                    String taxCode = extractTextFromScannedDocument(bim);
                    if (!StringUtils.isBlank(taxCode)) {
                        // Lưu file pdf vào hệ thống
                        String fileName = taxCode + "_" + System.currentTimeMillis() + "." + FilenameUtils.getExtension(file.getOriginalFilename());
                        Files.copy(input, Paths.get(filePath).resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                        map.put(taxCode, folder + "_" + fileName);
                    }
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

    private String extractTextFromScannedDocument(BufferedImage bim) {
        File temp = null;
        try {
            // Khởi tạo trình đọc OCR
            ITesseract _tesseract = new Tesseract();
            _tesseract.setDatapath(Constants.TESSERACT_FILE_DATA_PATH);
            _tesseract.setLanguage("vie");

            temp = File.createTempFile(String.valueOf(System.currentTimeMillis()), ".png");
            ImageIO.write(bim, "png", temp);

            //Lấy thông tin MST từ file pdf
            String data = _tesseract.doOCR(temp);
            data = data.replace("\n", " ");
            int index = data.indexOf("Mã số thuế: ");

            if (index > 0) {
                data = data.substring(index + 12);
                index = data.indexOf(" ");
                return data.substring(0, index);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (temp != null)
                temp.delete();
        }
        return "";
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

}
