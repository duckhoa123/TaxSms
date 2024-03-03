package com.vnnet.kpi.web.service;

import com.vnnet.kpi.web.bean.HttpResult;
import com.vnnet.kpi.web.bean.HttpStatus;
import com.vnnet.kpi.web.config.Constants;
import com.vnnet.kpi.web.model.BaseThongBaoKeKhai;
import com.vnnet.kpi.web.model.SysMessagDt;
import com.vnnet.kpi.web.model.SysMessagHd;
import com.vnnet.kpi.web.persistence.SysMessagDtMapper;
import com.vnnet.kpi.web.persistence.SysMessagHdMapper;
import com.vnnet.kpi.web.utils.DateTimeUtils;
import com.vnnet.kpi.web.utils.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FeeServiceImpl implements FeeService {

    private static final Logger logger = LoggerFactory.getLogger(FeeServiceImpl.class);

    private static String EXTENTION = ".xlsx";

    @Autowired
    private SysMessagHdMapper sysMessagHdMapper;
    @Autowired
    private SysMessagDtMapper sysMessagDtMapper;

    @Override
    public HttpResult save(MultipartFile file1, MultipartFile file2, SysMessagHd sysMessagHd) {
        try {
            List<BaseThongBaoKeKhai> validList1 = new ArrayList<>();
            List<BaseThongBaoKeKhai> validList2 = new ArrayList<>();
            List<BaseThongBaoKeKhai> invalidList1 = new ArrayList<>();
            List<BaseThongBaoKeKhai> invalidList2 = new ArrayList<>();

            String str1 = loadDataFromExcel1(file1, validList1, invalidList1);
            String str2 = loadDataFromExcel2(file2, validList2, invalidList2);

            if (!StringUtils.isBlank(str1))
                return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, str1);
            if (!StringUtils.isBlank(str2))
                return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, str2);

            if (invalidList1.size() > 0)
                return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Detail file error (" + file1.getOriginalFilename() + "). Please see the table below for more details", invalidList1);
            if (invalidList2.size() > 0)
                return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Contact file error (" + file2.getOriginalFilename() + "). Please see the table below for more details", invalidList2);

            String fileName1 = storeFile(file1, "le_phi_chi_tiet");
            String fileName2 = storeFile(file2, "le_phi_danh_ba");

            if (fileName1.equals("") || fileName2.equals(""))
                return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Error saving uploaded file. Please try again later");

            sysMessagHd.setDetailFile(fileName1);
            sysMessagHd.setContactFile(fileName2);

            sysMessagHd.setDelFlag((byte) 0);
            int result = sysMessagHdMapper.insertSelective(sysMessagHd);
            if (result >= 1) {
                List<BaseThongBaoKeKhai> invalidList3 = new ArrayList<>();
                List<SysMessagDt> sysMessagDts = loadMessages(validList1, validList2, sysMessagHd.getMessHdId(), sysMessagHd.getSmsTemplate(), invalidList3);
                if (invalidList3.size() > 0) {
                    sysMessagHdMapper.deleteByPrimaryKey(sysMessagHd.getMessHdId());
                    return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Error retrieving corporation phone number information.  Please see the table below for more details", invalidList3);
                }
                for (SysMessagDt messagDt : sysMessagDts) {
                    sysMessagDtMapper.insertSelective(messagDt);
                }
                return HttpResult.ok("Message schedule created successfully. Total " + sysMessagDts.size() + " message");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
        }
        return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "An error occurred. Please try again later.");
    }

    private String loadDataFromExcel1(MultipartFile file, List<BaseThongBaoKeKhai> validList, List<BaseThongBaoKeKhai> invalidList) {
        try {
            InputStream inputStream = file.getInputStream();
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            workbook.setMissingCellPolicy(Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
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
                        title = checkHeaderExcel1(_col, cell);
                        if (!title.equals(""))
                            break;
                        _col++;
                    }
                    if (!title.equals(""))
                        return "Detail file (" + file.getOriginalFilename() + ") missing column \"" + title + "\". Please select the file again";
                } else {
                    Iterator<Cell> cellIterator = row.cellIterator();
                    _col = 1;
                    BaseThongBaoKeKhai hd = new BaseThongBaoKeKhai();
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        Object cellValue = getCellValue(cell);
                        if (_col == 1 && StringUtils.isBlank(String.valueOf(cellValue))) {
                            break;
                        }
                        switch (_col) {
                            case 3:
                                if (StringUtils.isBlank(String.valueOf(cellValue)))
                                    hd.setError("Missing tax number");
                                else
                                    hd.setMaSoThue(String.valueOf(cellValue));
                                break;
                            case 4:
                                if (StringUtils.isBlank(String.valueOf(cellValue)))
                                    hd.setError("Missing corporation information");
                                else
                                    hd.setNguoiNopThue(String.valueOf(cellValue));
                                break;
                            case 5:
                                if (StringUtils.isBlank(String.valueOf(cellValue)))
                                    hd.setError("Missing subsection");
                                else
                                    hd.setTieuMuc(String.valueOf(cellValue));
                                break;
                            case 6:
                                try {
                                    if (StringUtils.isBlank(String.valueOf(cellValue)))
                                        hd.setError("Missing amount");
                                    else
                                        hd.setSoTien((long) cell.getNumericCellValue());
                                } catch (Exception ex) {
                                    hd.setError("Amount column is not numerical");
                                }
                                break;
                            case 9:
                                try {
                                    if (StringUtils.isBlank(String.valueOf(cellValue)))
                                        hd.setError("Missing tax filling date");
                                    else
                                        hd.setNgayNop(convertToJavaDate(cellValue));
                                } catch (Exception ex) {
                                    hd.setError("Tax filling date column incorrect");
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
                    else if (!StringUtils.isBlank(hd.getMaSoThue()))
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
                        return "Contact file (" + file.getOriginalFilename() + ") missing column \"" + title + "\". Please select file again";
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

    private String checkHeaderExcel1(int col, Cell cell) {
        if (col == 0 && !getCellValue(cell).toString().equals("Số lô chứng từ"))
            return "Số lô chứng từ";
        else if (col == 1 && !getCellValue(cell).toString().equals("Trạng thái lô chứng từ"))
            return "Trạng thái lô chứng từ";
        else if (col == 2 && !getCellValue(cell).toString().equals("Mã số thuế"))
            return "Mã số thuế";
        else if (col == 3 && !getCellValue(cell).toString().contains("Tên NNT"))
            return "Tên NNT";
        else if (col == 4 && !getCellValue(cell).toString().contains("Tiểu mục"))
            return "Tiểu mục";
        else if (col == 5 && !getCellValue(cell).toString().equals("Số tiền"))
            return "Số tiền";
        else if (col == 6 && !getCellValue(cell).toString().equals("Hủy chứng từ"))
            return "Hủy chứng từ";
        else if (col == 7 && !getCellValue(cell).toString().equals("Ký hiệu giao dịch"))
            return "Ký hiệu giao dịch";
        else if (col == 8 && !getCellValue(cell).toString().equals("Ngày nộp thuế"))
            return "Ngày nộp thuế";

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

    private String convertToJavaDate(Object date) {
        Date javaDate = DateUtil.getJavaDate(Double.valueOf(date.toString()));
        return new SimpleDateFormat("dd/MM/yyyy").format(javaDate);
    }

    private Object getCellValue(Cell cell) {
        if (cell != null) {
            CellType cellType = cell.getCellType();
            if (CellType.STRING.equals(cellType))
                return cell.getStringCellValue();
            else if (CellType.NUMERIC.equals(cellType))
                return String.valueOf(cell.getNumericCellValue());
            else if (CellType.BOOLEAN.equals(cellType))
                return cell.getBooleanCellValue();
        }
        return "";
    }

    private List<SysMessagDt> loadMessages(List<BaseThongBaoKeKhai> smsList, List<BaseThongBaoKeKhai> phoneList, long id, String smsTemp, List<BaseThongBaoKeKhai> invalidList) {
        List<SysMessagDt> messagDts = new ArrayList<>();
        int rowIndex = 2;
        for (BaseThongBaoKeKhai noThue : smsList) {
            List<BaseThongBaoKeKhai> list = phoneList.stream().filter(it -> it.getMaSoThue() != null && it.getMaSoThue().equals(noThue.getMaSoThue())).collect(Collectors.toList());
            if (list != null && list.size() > 0) {
                if (StringUtils.isBlank(list.get(0).getSoGiamDoc()) && StringUtils.isBlank(list.get(0).getSoKeToan())) {
                    BaseThongBaoKeKhai obj = new BaseThongBaoKeKhai();
                    obj.setError("Corporation missing contact number");
                    obj.setRowIndex(rowIndex);
                    invalidList.add(obj);
                } else {
                    if (!StringUtils.isBlank(list.get(0).getSoGiamDoc()))
                        messagDts.add(addMessageDt(noThue, list.get(0).getSoGiamDoc(), id, smsTemp));
                    if (!StringUtils.isBlank(list.get(0).getSoKeToan()))
                        messagDts.add(addMessageDt(noThue, list.get(0).getSoKeToan(), id, smsTemp));
                }
            } else {
                BaseThongBaoKeKhai obj = new BaseThongBaoKeKhai();
                obj.setError("Corporation missing contact number");
                obj.setRowIndex(rowIndex);
                invalidList.add(obj);
            }
            rowIndex++;
        }
        return messagDts;
    }

    private SysMessagDt addMessageDt(BaseThongBaoKeKhai noThue, String phone, long id, String smsTemp) {
        SysMessagDt dt = new SysMessagDt();
        dt.setTaxCode(noThue.getMaSoThue());
        dt.setCorName(noThue.getNguoiNopThue());
        dt.setCalledNumber(formatCalledNumber(phone));
        String sms = smsTemp;
        sms = sms.replace("{nguoi_nop_thue}", noThue.getNguoiNopThue());
        sms = sms.replace("{ma_so_thue}", noThue.getMaSoThue());
        sms = sms.replace("{so_tien}", StringUtils.thousandSeparator(noThue.getSoTien(), ".") + " đồng");
        sms = sms.replace("{tieu_muc}", noThue.getTieuMuc());
        sms = sms.replace("{ngay_nop}", String.valueOf(noThue.getNgayNop()));
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
        String fileName = filename + "_" + System.currentTimeMillis() + EXTENTION;
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
}
