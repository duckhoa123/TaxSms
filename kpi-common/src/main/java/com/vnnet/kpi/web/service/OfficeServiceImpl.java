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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Service
@Transactional
public class OfficeServiceImpl implements OfficeService {

    private static String EXTENTION = ".xlsx";

    @Autowired
    private SysMessagHdMapper sysMessagHdMapper;
    @Autowired
    private SysMessagDtMapper sysMessagDtMapper;

    @Override
    public HttpResult save(MultipartFile file1, SysMessagHd sysMessagHd) {
        try {
            List<BaseThongBaoKeKhai> validList2 = new ArrayList<>();
            List<BaseThongBaoKeKhai> invalidList2 = new ArrayList<>();

            String str2 = loadDataFromExcel2(file1, validList2, invalidList2);

            if (!StringUtils.isBlank(str2))
                return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, str2);

            if (invalidList2.size() > 0)
                return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Contact file error (" + file1.getOriginalFilename() + "). Vui lòng xem bảng dưới để biết thêm chi tiết", invalidList2);

            String fileName1 = storeFile(file1, "van_phong_danh_ba");

            if (fileName1.equals(""))
                return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Error saving uploaded file. please Try again later");

            sysMessagHd.setContactFile(fileName1);

            sysMessagHd.setDelFlag((byte) 0);
            int result = sysMessagHdMapper.insertSelective(sysMessagHd);
            if (result >= 1) {
                List<BaseThongBaoKeKhai> invalidList3 = new ArrayList<>();
                List<SysMessagDt> sysMessagDts = loadMessages(validList2, sysMessagHd.getMessHdId(), sysMessagHd.getSmsTemplate(), invalidList3);
                if (invalidList3.size() > 0) {
                    sysMessagHdMapper.deleteByPrimaryKey(sysMessagHd.getMessHdId());
                    return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Error retrieving phone number INFORMATION. Please see the table below for more details", invalidList3);
                }
                for (SysMessagDt messagDt : sysMessagDts) {
                    sysMessagDtMapper.insertSelective(messagDt);
                }
                return HttpResult.ok("Message schedule created successfully. total " + sysMessagDts.size() + " message");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
        }
        return HttpResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "An error occurred. please Try again later.");
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
                        return "Contact file (" + file.getOriginalFilename() + ") missing column \"" + title + "\".  Please select file again";
                    _row++;
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

    private String checkHeaderExcel2(int col, Cell cell) {
        if (col == 1 && !getCellValue(cell).toString().equals("Họ tên"))
            return "Họ tên";
        else if (col == 2 && !getCellValue(cell).toString().equals("Chức vụ"))
            return "Chức vụ";
        else if (col == 3 && !getCellValue(cell).toString().equals("Số điện thoại"))
            return "Số điện thoại";

        return "";
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

    private List<SysMessagDt> loadMessages(List<BaseThongBaoKeKhai> phoneList, long id, String smsTemp, List<BaseThongBaoKeKhai> invalidList) {
        List<SysMessagDt> messagDts = new ArrayList<>();
        int rowIndex = 2;
        for (BaseThongBaoKeKhai item : phoneList) {
            if (StringUtils.isBlank(item.getSoGiamDoc())) {
                BaseThongBaoKeKhai obj = new BaseThongBaoKeKhai();
                obj.setError("Missing phone number");
                obj.setRowIndex(rowIndex);
                invalidList.add(obj);
            } else {
                messagDts.add(addMessageDt(item, item.getSoGiamDoc(), id, smsTemp));
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
        dt.setSmsContent(smsTemp);
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
