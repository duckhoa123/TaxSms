package com.vnnet.kpi.web.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.SimpleTextExtractionStrategy;
import com.vnnet.kpi.web.bean.HttpResult;
import com.vnnet.kpi.web.bean.HttpStatus;
import com.vnnet.kpi.web.bean.PageRequest;
import com.vnnet.kpi.web.bean.PageResult;
import com.vnnet.kpi.web.config.Constants;
import com.vnnet.kpi.web.model.*;
import com.vnnet.kpi.web.persistence.SysConfigMapper;
import com.vnnet.kpi.web.persistence.SysMessagDtMapper;
import com.vnnet.kpi.web.persistence.SysMessagExMapper;
import com.vnnet.kpi.web.persistence.SysMessagHdMapper;
import com.vnnet.kpi.web.utils.DateTimeUtils;
import com.vnnet.kpi.web.utils.ExcelUtil;
import com.vnnet.kpi.web.utils.MybatisPageHelper;
import com.vnnet.kpi.web.utils.StringUtils;
import org.apache.commons.compress.compressors.FileNameUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.time.Duration;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Transactional
public class SysMessageServiceImpl implements SysMessageService {

    private static final Logger logger = LoggerFactory.getLogger(SysMessageServiceImpl.class);

    @Autowired
    private SysMessagHdMapper sysMessagHdMapper;
    @Autowired
    private SysMessagDtMapper sysMessagDtMapper;
    @Autowired
    private SysMessagExMapper sysMessagExMapper;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysLogInfoService sysLogInfoService;
    @Autowired
    private SysRoleService sysRoleService;


    @Override
    public int update(SysMessagHd sysMessagHd, String userName) {
        try {
            SysMessagHd sysMessagHd1 = sysMessagHdMapper.selectByPrimaryKey(sysMessagHd.getMessHdId());
            if (sysMessagHd1 != null) {
                if (sysMessagHd1.getDelFlag() == 2)
                    return 3;

//            long hours = (sysMessagHd.getSchedulerDateTime().getTime() - new Date().getTime()) / (60*60*1000);
                Date d1 = new Date();
                Date d2 = sysMessagHd.getSchedulerDateTime();
                if (d1.before(d2)) {
                    String title = "";
                    String date = "";
                    String titleOld = sysMessagHd1.getTitle();
                    if (!sysMessagHd.getTitle().equals(sysMessagHd1.getTitle()))
                        title = sysMessagHd1.getTitle() + " -> " + sysMessagHd.getTitle();
                    if (sysMessagHd.getSchedulerDateTime() != sysMessagHd1.getSchedulerDateTime())
                        date = DateTimeUtils.convertDateToString(sysMessagHd1.getSchedulerDateTime(), "dd/MM/yyyy HH:mm:ss") + " -> " + DateTimeUtils.convertDateToString(sysMessagHd.getSchedulerDateTime(), "dd/MM/yyyy HH:mm:ss");
                    sysMessagHd1.setTitle(sysMessagHd.getTitle());
                    sysMessagHd1.setSchedulerDateTime(sysMessagHd.getSchedulerDateTime());

                    SysMessagHdExample example = new SysMessagHdExample();
                    example.createCriteria().andMessHdIdEqualTo(sysMessagHd1.getMessHdId()).andDelFlagNotEqualTo((byte) 2);
                    sysMessagHd1.setMessHdId(null);
                    int result = sysMessagHdMapper.updateByExampleSelective(sysMessagHd1, example);
                    if (result >= 1)
                        sysLogInfoService.save("Message schedule updated successfully. Title: " + titleOld + ". Tên lịch: " + title + ". Ngày nhắn: " + date, userName);
                    return result;
                } else {
                    sysLogInfoService.save("Failed to update message schedule. Title: " + sysMessagHd.getTitle() + ". Error: Lịch nhắn tin nhỏ hơn thời gian hiện tại", userName);
                    return 2;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    @Override
    public int delete(SysMessagHd hd, String userName) {
        try {
            SysMessagHd sysMessagHd = sysMessagHdMapper.selectByPrimaryKey(hd.getMessHdId());
            if (sysMessagHd != null) {
                Date d1 = new Date();
                Date d2 = sysMessagHd.getSchedulerDateTime();
                if (d1.before(d2)) {
                    sysMessagHd.setDelFlag((byte) 2);
                    sysMessagHd.setDelReason(hd.getDelReason());
                    SysMessagHdExample example = new SysMessagHdExample();
                    example.createCriteria().andMessHdIdEqualTo(sysMessagHd.getMessHdId()).andDelFlagNotEqualTo((byte) 2);
                    sysMessagHd.setMessHdId(null);
                    int res = sysMessagHdMapper.updateByExampleSelective(sysMessagHd, example);
                    if (res >= 1)
                        sysLogInfoService.save("Message schedule canceled successfully. Title: " + sysMessagHd.getTitle(), userName);

                    return res;
                } else {
                    sysLogInfoService.save("Failed to cancel scheduled message. Title: " + sysMessagHd.getTitle() + ". Error: Lịch nhắn tin đã được thực hiện nên không thể hủy", userName);
                    return 2;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    @Override
    public int approve(SysMessagHd hd, String userName) {
        try {
            SysMessagHd sysMessagHd = sysMessagHdMapper.selectByPrimaryKey(hd.getMessHdId());
            if (sysMessagHd != null) {
                Date d1 = new Date();
                Date d2 = sysMessagHd.getSchedulerDateTime();
                if (d1.before(d2)) {
                    sysMessagHd.setDelFlag((byte) 1);
                    SysMessagHdExample example = new SysMessagHdExample();
                    example.createCriteria().andMessHdIdEqualTo(sysMessagHd.getMessHdId()).andDelFlagEqualTo((byte) 0);
                    sysMessagHd.setMessHdId(null);
                    int res = sysMessagHdMapper.updateByExampleSelective(sysMessagHd, example);
                    if (res >= 1)
                        sysLogInfoService.save("Message schedule approved successfully. Title: " + sysMessagHd.getTitle(), userName);

                    return res;
                } else {
                    sysLogInfoService.save("Failed to approve message schedule. Title: " + sysMessagHd.getTitle() + ". Error: Lịch nhắn tin đã được thực hiện nên không thể phê duyệt", userName);
                    return 2;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    @Override
    public ByteArrayInputStream download(PageRequest pageRequest, File file) {
        try {
            pageRequest.setPageNum(1);
            pageRequest.setPageSize(100000);
            List<SysMessagDt> list = (List<SysMessagDt>) (findPageDT(pageRequest).getContent());
            List<SysMessagDt> messagDts = list.stream().collect(Collectors.toCollection(LinkedList::new));

            if (messagDts != null && messagDts.size() > 0) {
                FileInputStream inputStream = new FileInputStream(file);
                XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
                inputStream.close();

                SXSSFWorkbook wb = new SXSSFWorkbook(workbook, 100); // keep 100 rows in memory, exceeding rows will be flushed to disk
                Sheet sheet = wb.getSheetAt(0);

                Font font = wb.createFont();
                font.setBold(false);
                font.setFontName("Arial");

                CellStyle style = wb.createCellStyle();
                style.setFont(font);
                style.setAlignment(HorizontalAlignment.LEFT);
                style.setVerticalAlignment(VerticalAlignment.CENTER);
                style.setBorderBottom(BorderStyle.THIN);
                style.setBorderTop(BorderStyle.THIN);
                style.setBorderRight(BorderStyle.THIN);
                style.setBorderLeft(BorderStyle.THIN);
                style.setWrapText(true);

                int rowIdx = 1;

                for (SysMessagDt dt : messagDts) {
                    Row row = sheet.createRow(rowIdx++);
                    int i = 0;
                    ExcelUtil.createCell(row, i++, dt.getTaxCode(), (XSSFCellStyle) style);
                    ExcelUtil.createCell(row, i++, dt.getCorName(), (XSSFCellStyle) style);
                    ExcelUtil.createCell(row, i++, dt.getCalledNumber(), (XSSFCellStyle) style);
                    ExcelUtil.createCell(row, i++, dt.getSmsContent(), (XSSFCellStyle) style);
                    ExcelUtil.createCell(row, i++, getStatus(dt.getStatus()), (XSSFCellStyle) style);
                }
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                wb.write(out);
                wb.dispose();
                workbook.close();
                return new ByteArrayInputStream(out.toByteArray());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public PageResult findPage(PageRequest pageRequest, String userName) {
        try {
            List<SysUserRole> sysUserRoles = sysRoleService.findByUserName(userName);
            if (sysUserRoles == null || sysUserRoles.size() == 0)

                return null;


           String fromDate = MybatisPageHelper.getColumnFilterValue(pageRequest, "fromDate");
            String toDate = MybatisPageHelper.getColumnFilterValue(pageRequest, "toDate");
            String title = MybatisPageHelper.getColumnFilterValue(pageRequest, "title");
            String programId = MybatisPageHelper.getColumnFilterValue(pageRequest, "programId");
            title = title == null ? "" : title;

            int pageNum = pageRequest.getPageNum();
            int pageSize = pageRequest.getPageSize();
            if (pageNum < 1)
                pageNum = 1;
            if (pageSize < 0)
                pageSize = 10;

            String groupCode = new CommonService().getGroupProgramWithRole(sysUserRoles.get(0).getRoleId());
            userName = new CommonService().getUerNameWithRole(sysUserRoles.get(0).getRoleId(), userName);

            if (!StringUtils.isBlank(fromDate) && !StringUtils.isBlank(toDate)) {
                Date from_date = DateTimeUtils.convertStringToDate(fromDate, "dd/MM/yyyy");
                Date to_date = DateTimeUtils.addDay(DateTimeUtils.convertStringToDate(toDate, "dd/MM/yyyy"), 1);

                PageHelper.startPage(pageNum, pageSize);
                List<SysMessagHd> sysMessagHds = sysMessagExMapper.findPage(from_date, to_date, new Date(), title, userName, groupCode, programId);

                return MybatisPageHelper.getPageResult(pageRequest, new PageInfo(sysMessagHds));
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex.fillInStackTrace());
        }
        return null;
    }

    @Override
    public PageResult findPageDT(PageRequest pageRequest) {
        try {
            String messageId = MybatisPageHelper.getColumnFilterValue(pageRequest, "messageId");
            String status = MybatisPageHelper.getColumnFilterValue(pageRequest, "status");
            int pageNum = pageRequest.getPageNum();
            int pageSize = pageRequest.getPageSize();
            if (pageNum < 1)
                pageNum = 1;
            if (pageSize < 0)
                pageSize = 10;

            PageHelper.startPage(pageNum, pageSize);
            SysMessagDtExample example = new SysMessagDtExample();
            if (status.equals("0"))
                example.createCriteria().andMessHdIdEqualTo(Long.valueOf(messageId));
            else if (status.equals("1"))
                example.createCriteria().andMessHdIdEqualTo(Long.valueOf(messageId)).andStatusEqualTo((byte) 1);
            else if (status.equals("2"))
                example.createCriteria().andMessHdIdEqualTo(Long.valueOf(messageId)).andStatusEqualTo((byte) 2);
            List<SysMessagDt> sysMessagDts = sysMessagDtMapper.selectByExample(example);
            return MybatisPageHelper.getPageResult(pageRequest, new PageInfo(sysMessagDts));
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex.fillInStackTrace());
        }
        return null;
    }
    @Override
    public SysMessagHd findTotalMessFromLoadDetailReport(PageRequest pageRequest, String userName) {
        SysMessagHd sysMessagHd = new SysMessagHd();
        try {
            List<SysUserRole> sysUserRoles = sysRoleService.findByUserName(userName);
            if (sysUserRoles == null || sysUserRoles.size() == 0)
                return sysMessagHd;
            String fromDate = MybatisPageHelper.getColumnFilterValue(pageRequest, "fromDate");
            String toDate = MybatisPageHelper.getColumnFilterValue(pageRequest, "toDate");
            String groupCode = MybatisPageHelper.getColumnFilterValue(pageRequest, "groupCode");
            String programId=MybatisPageHelper.getColumnFilterValue(pageRequest,"programId");

            if (!StringUtils.isBlank(fromDate) && !StringUtils.isBlank(toDate)) {
                Date from_date = DateTimeUtils.convertStringToDate(fromDate, "dd/MM/yyyy");
                Date to_date = DateTimeUtils.addDay(DateTimeUtils.convertStringToDate(toDate, "dd/MM/yyyy"), 1);
                List<SysMessagHd> sysMessagHds = sysMessagExMapper.findTotalMessFromLoadDetailReport(from_date, to_date, groupCode ,programId);
                if (sysMessagHds != null && sysMessagHds.size() > 0)
                    sysMessagHd = sysMessagHds.get(0);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex.fillInStackTrace());
        }
        return sysMessagHd;
    }

    @Override
    public SysMessagHd findTotalMessByDate(PageRequest pageRequest, String userName) {
        SysMessagHd sysMessagHd = new SysMessagHd();
        try {
            List<SysUserRole> sysUserRoles = sysRoleService.findByUserName(userName);
            if (sysUserRoles == null || sysUserRoles.size() == 0)
                return sysMessagHd;
            String groupProgram = new CommonService().getGroupProgramWithRole(sysUserRoles.get(0).getRoleId());

            String fromDate = MybatisPageHelper.getColumnFilterValue(pageRequest, "fromDate");
            String toDate = MybatisPageHelper.getColumnFilterValue(pageRequest, "toDate");

            if (!StringUtils.isBlank(fromDate) && !StringUtils.isBlank(toDate)) {
                Date from_date = DateTimeUtils.convertStringToDate(fromDate, "dd/MM/yyyy");
                Date to_date = DateTimeUtils.addDay(DateTimeUtils.convertStringToDate(toDate, "dd/MM/yyyy"), 1);
                List<SysMessagHd> sysMessagHds = sysMessagExMapper.findTotalMessByDate(from_date, to_date, groupProgram);
                if (sysMessagHds != null && sysMessagHds.size() > 0)
                    sysMessagHd = sysMessagHds.get(0);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex.fillInStackTrace());
        }
        return sysMessagHd;
    }

    @Override
    public HashMap<String, List<Object>> findTop5Scheduler(PageRequest pageRequest, String userName) {
        HashMap<String, List<Object>> hashMap = new HashMap<>();
        try {
            List<SysUserRole> sysUserRoles = sysRoleService.findByUserName(userName);
            if (sysUserRoles == null || sysUserRoles.size() == 0)
                return hashMap;
            String groupProgram = new CommonService().getGroupProgramWithRole(sysUserRoles.get(0).getRoleId());
            String fromDate = MybatisPageHelper.getColumnFilterValue(pageRequest, "fromDate");
            String toDate = MybatisPageHelper.getColumnFilterValue(pageRequest, "toDate");

            if (!StringUtils.isBlank(fromDate) && !StringUtils.isBlank(toDate)) {
                Date from_date = DateTimeUtils.convertStringToDate(fromDate, "dd/MM/yyyy");
                Date to_date = DateTimeUtils.addDay(DateTimeUtils.convertStringToDate(toDate, "dd/MM/yyyy"), 1);
                List<SysMessagHd> sysMessagHds = sysMessagExMapper.findTop5Scheduler(from_date, to_date, groupProgram);
                if (sysMessagHds != null && sysMessagHds.size() > 0)
                    hashMap = convertToChartData(sysMessagHds);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex.fillInStackTrace());
        }
        return hashMap;
    }

    @Override
    public HashMap<String, List<Object>> findListScheduler(PageRequest pageRequest, String userName) {
        HashMap<String, List<Object>> hashMap = new HashMap<>();
        try {
            List<SysUserRole> sysUserRoles = sysRoleService.findByUserName(userName);
            if (sysUserRoles == null || sysUserRoles.size() == 0)
                return hashMap;
            String groupProgram = new CommonService().getGroupProgramWithRole(sysUserRoles.get(0).getRoleId());
            String fromDate = MybatisPageHelper.getColumnFilterValue(pageRequest, "fromDate");
            String toDate = MybatisPageHelper.getColumnFilterValue(pageRequest, "toDate");

            if (!StringUtils.isBlank(fromDate) && !StringUtils.isBlank(toDate)) {
                Date from_date = DateTimeUtils.convertStringToDate(fromDate, "dd/MM/yyyy");
                Date to_date = DateTimeUtils.addDay(DateTimeUtils.convertStringToDate(toDate, "dd/MM/yyyy"), 1);
                List<SysMessagHd> sysMessagHds = sysMessagExMapper.findListScheduler(from_date, to_date, groupProgram);
                if (sysMessagHds != null && sysMessagHds.size() > 0)
                    hashMap = convertToChartData1(sysMessagHds);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex.fillInStackTrace());
        }
        return hashMap;
    }

    @Override
    public PageResult findListHistory(PageRequest pageRequest, String userName) {
        try {
            List<SysUserRole> sysUserRoles = sysRoleService.findByUserName(userName);
            if (sysUserRoles == null || sysUserRoles.size() == 0)
                return null;
            String groupProgram = new CommonService().getGroupProgramWithRole(sysUserRoles.get(0).getRoleId());
            userName = new CommonService().getUerNameWithRole(sysUserRoles.get(0).getRoleId(), userName);

            String fromDate = MybatisPageHelper.getColumnFilterValue(pageRequest, "fromDate");
            String toDate = MybatisPageHelper.getColumnFilterValue(pageRequest, "toDate");
            String title = MybatisPageHelper.getColumnFilterValue(pageRequest, "title");
            String taxCode = MybatisPageHelper.getColumnFilterValue(pageRequest, "taxCode");
            String corName = MybatisPageHelper.getColumnFilterValue(pageRequest, "corName");
            String status = MybatisPageHelper.getColumnFilterValue(pageRequest, "status");
            String isdn = MybatisPageHelper.getColumnFilterValue(pageRequest, "isdn");
            String userTypeCode = MybatisPageHelper.getColumnFilterValue(pageRequest, "userTypeCode");
            String programId = MybatisPageHelper.getColumnFilterValue(pageRequest, "programId");
            title = title == null ? "" : title;
            taxCode = taxCode == null ? "" : taxCode;
            corName = corName == null ? "" : corName;
            isdn = isdn == null ? "" : isdn;
            isdn = isdn.startsWith("0") ? isdn.substring(1) : isdn;
            userTypeCode = userTypeCode == null ? "" : userTypeCode;
            programId = programId == null ? "" : programId;

            int pageNum = pageRequest.getPageNum();
            int pageSize = pageRequest.getPageSize();
            if (pageNum < 1)
                pageNum = 1;
            if (pageSize < 0)
                pageSize = 10;

            if (!StringUtils.isBlank(fromDate) && !StringUtils.isBlank(toDate)) {
                Date from_date = DateTimeUtils.convertStringToDate(fromDate, "dd/MM/yyyy");
                Date to_date = DateTimeUtils.addDay(DateTimeUtils.convertStringToDate(toDate, "dd/MM/yyyy"), 1);
                PageHelper.startPage(pageNum, pageSize);
                System.out.println(userName + " - " + userTypeCode + " - " + programId);
                List<SysMessagHd> sysMessagHds = sysMessagExMapper.findListHistory(from_date, to_date, new Date(), title, taxCode, corName, status, isdn, userName, userTypeCode, programId);
                return MybatisPageHelper.getPageResult(pageRequest, new PageInfo(sysMessagHds));
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex.fillInStackTrace());
        }
        return null;
    }

    @Override
    public ByteArrayInputStream downloadHis(PageRequest pageRequest, String userName, File file) {
        try {
            pageRequest.setPageNum(1);
            pageRequest.setPageSize(100000);
            List<SysMessagDt> list = (List<SysMessagDt>) (findListHistory(pageRequest, userName).getContent());
            List<SysMessagDt> messagDts = list.stream().collect(Collectors.toCollection(LinkedList::new));

            if (messagDts != null && messagDts.size() > 0) {
                FileInputStream inputStream = new FileInputStream(file);
                XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
                inputStream.close();

                SXSSFWorkbook wb = new SXSSFWorkbook(workbook, 100); // keep 100 rows in memory, exceeding rows will be flushed to disk
                Sheet sheet = wb.getSheetAt(0);

                Font font = wb.createFont();
                font.setBold(false);
                font.setFontName("Arial");

                CellStyle style = wb.createCellStyle();
                style.setFont(font);
                style.setAlignment(HorizontalAlignment.LEFT);
                style.setVerticalAlignment(VerticalAlignment.CENTER);
                style.setBorderBottom(BorderStyle.THIN);
                style.setBorderTop(BorderStyle.THIN);
                style.setBorderRight(BorderStyle.THIN);
                style.setBorderLeft(BorderStyle.THIN);
                style.setWrapText(true);

                int rowIdx = 1;

                for (SysMessagDt dt : messagDts) {
                    Row row = sheet.createRow(rowIdx++);
                    int i = 0;
                    ExcelUtil.createCell(row, i++, dt.getTaxCode(), (XSSFCellStyle) style);
                    ExcelUtil.createCell(row, i++, dt.getCorName(), (XSSFCellStyle) style);
                    ExcelUtil.createCell(row, i++, dt.getCalledNumber(), (XSSFCellStyle) style);
                    ExcelUtil.createCell(row, i++, DateTimeUtils.convertDateToString(dt.getSchedulerDateTime(), "dd/MM/yyyy HH:mm:ss"), (XSSFCellStyle) style);
                    ExcelUtil.createCell(row, i++, dt.getSmsContent(), (XSSFCellStyle) style);
                    ExcelUtil.createCell(row, i++, getStatus(dt.getStatus()), (XSSFCellStyle) style);
                    ExcelUtil.createCell(row, i++, dt.getCreateBy(), (XSSFCellStyle) style);
                    ExcelUtil.createCell(row, i++, DateTimeUtils.convertDateToString(dt.getCreateTime(), "dd/MM/yyyy"), (XSSFCellStyle) style);
                }
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                wb.write(out);
                wb.dispose();
                workbook.close();
                return new ByteArrayInputStream(out.toByteArray());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public ByteArrayInputStream reportDownload(PageRequest pageRequest, File file, String userName) {
        try {
            pageRequest.setPageNum(1);
            pageRequest.setPageSize(100000);
            List<SysMessagHd> list = (List<SysMessagHd>) (findPage(pageRequest, userName).getContent());
            List<SysMessagHd> messagDts = list.stream().collect(Collectors.toCollection(LinkedList::new));

            if (messagDts != null && messagDts.size() > 0) {
                FileInputStream inputStream = new FileInputStream(file);
                XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
                inputStream.close();

                SXSSFWorkbook wb = new SXSSFWorkbook(workbook, 100); // keep 100 rows in memory, exceeding rows will be flushed to disk
                Sheet sheet = wb.getSheetAt(0);

                Font font = wb.createFont();
                font.setBold(false);
                font.setFontName("Arial");

                CellStyle style = wb.createCellStyle();
                style.setFont(font);
                style.setAlignment(HorizontalAlignment.LEFT);
                style.setVerticalAlignment(VerticalAlignment.CENTER);
                style.setBorderBottom(BorderStyle.THIN);
                style.setBorderTop(BorderStyle.THIN);
                style.setBorderRight(BorderStyle.THIN);
                style.setBorderLeft(BorderStyle.THIN);
                style.setWrapText(true);

                int rowIdx = 1;

                long corTotal = 0;
                long smsTotal = 0;
                long sendSuccess = 0;
                for (SysMessagHd dt : messagDts) {
                    Row row = sheet.createRow(rowIdx++);
                    int i = 0;
                    ExcelUtil.createCell(row, i++, dt.getTitle(), (XSSFCellStyle) style);
                    ExcelUtil.createCell(row, i++, DateTimeUtils.convertDateToString(dt.getSchedulerDateTime(), "dd/MM/yyyy HH:mm:ss"), (XSSFCellStyle) style);
                    ExcelUtil.createCell(row, i++, DateTimeUtils.convertDateToString(dt.getCreateTime(), "dd/MM/yyyy"), (XSSFCellStyle) style);
                    ExcelUtil.createCell(row, i++, dt.getCreateBy(), (XSSFCellStyle) style);
                    ExcelUtil.createCell(row, i++, dt.getCorTotal(), (XSSFCellStyle) style);
                    ExcelUtil.createCell(row, i++, dt.getSmsTotal(), (XSSFCellStyle) style);
                    ExcelUtil.createCell(row, i++, dt.getSendSuccess(), (XSSFCellStyle) style);
                    corTotal += dt.getCorTotal();
                    smsTotal += dt.getSmsTotal();
                    sendSuccess += dt.getSendSuccess();
                }

                //Bổ sung dòng tổng
                Font font1 = wb.createFont();
                font1.setBold(true);
                font1.setFontName("Arial");

                CellStyle style1 = wb.createCellStyle();
                style1.setFont(font1);
                style1.setAlignment(HorizontalAlignment.LEFT);
                style1.setVerticalAlignment(VerticalAlignment.CENTER);
                style1.setBorderBottom(BorderStyle.THIN);
                style1.setBorderTop(BorderStyle.THIN);
                style1.setBorderRight(BorderStyle.THIN);
                style1.setBorderLeft(BorderStyle.THIN);
                style1.setWrapText(true);
                Row row = sheet.createRow(rowIdx++);
                int i = 0;
                ExcelUtil.createCell(row, i++, "Total", (XSSFCellStyle) style1);
                ExcelUtil.createCell(row, i++, "", (XSSFCellStyle) style1);
                ExcelUtil.createCell(row, i++, "", (XSSFCellStyle) style1);
                ExcelUtil.createCell(row, i++, "", (XSSFCellStyle) style1);
                ExcelUtil.createCell(row, i++, corTotal, (XSSFCellStyle) style1);
                ExcelUtil.createCell(row, i++, smsTotal, (XSSFCellStyle) style1);
                ExcelUtil.createCell(row, i++, sendSuccess, (XSSFCellStyle) style1);

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                wb.write(out);
                wb.dispose();
                workbook.close();
                return new ByteArrayInputStream(out.toByteArray());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public PageResult loadReport(PageRequest pageRequest, String userName) {
        try {
//            List<SysUserRole> sysUserRoles = sysRoleService.findByUserName(userName);
//            if (sysUserRoles == null || sysUserRoles.size() == 0)
//                return null;
//            String groupCode = new CommonService().getGroupProgramWithRole(sysUserRoles.get(0).getRoleId());
//            userName = new CommonService().getUerNameWithRole(sysUserRoles.get(0).getRoleId(), userName);

            String fromDate = MybatisPageHelper.getColumnFilterValue(pageRequest, "fromDate");
            String toDate = MybatisPageHelper.getColumnFilterValue(pageRequest, "toDate");
            String status = MybatisPageHelper.getColumnFilterValue(pageRequest, "status");

            int pageNum = pageRequest.getPageNum();
            int pageSize = pageRequest.getPageSize();
            if (pageNum < 1)
                pageNum = 1;
            if (pageSize < 0)
                pageSize = 10;

            if (!StringUtils.isBlank(fromDate) && !StringUtils.isBlank(toDate)) {
                Date from_date = DateTimeUtils.convertStringToDate(fromDate, "dd/MM/yyyy");
                Date to_date = DateTimeUtils.addDay(DateTimeUtils.convertStringToDate(toDate, "dd/MM/yyyy"), 1);
                PageHelper.startPage(pageNum, pageSize);
                List<SysMessagHd> sysMessagHds = sysMessagExMapper.loadReport(from_date, to_date, status);
                return MybatisPageHelper.getPageResult(pageRequest, new PageInfo(sysMessagHds));
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex.fillInStackTrace());
        }
        return null;
    }
    @Override
    public PageResult loadDetailReport(PageRequest pageRequest, String userName) {

        try {
            String fromDate = MybatisPageHelper.getColumnFilterValue(pageRequest, "fromDate");
            String toDate = MybatisPageHelper.getColumnFilterValue(pageRequest, "toDate");
           String status = MybatisPageHelper.getColumnFilterValue(pageRequest, "status");
            String groupCode = MybatisPageHelper.getColumnFilterValue(pageRequest, "groupCode");
            String programId=MybatisPageHelper.getColumnFilterValue(pageRequest,"programId");


            int pageNum = pageRequest.getPageNum();
            int pageSize = pageRequest.getPageSize();
            if (pageNum < 1)
                pageNum = 1;
            if (pageSize < 0)
                pageSize = 10;

            //  String groupCode = new CommonService().getGroupProgramWithRole(sysUserRoles.get(0).getRoleId());

            if (!StringUtils.isBlank(fromDate) && !StringUtils.isBlank(toDate)) {
                Date from_date = DateTimeUtils.convertStringToDate(fromDate, "dd/MM/yyyy");
                Date to_date = DateTimeUtils.addDay(DateTimeUtils.convertStringToDate(toDate, "dd/MM/yyyy"), 1);
                PageHelper.startPage(pageNum, pageSize);


                //List<SysMessagHd> sysMessagHds = sysMessagExMapper.loadDetailReport(from_date, to_date,groupCode,status);
                List<SysMessagHd> sysMessagHds = sysMessagExMapper.loadDetailReport(from_date, to_date,groupCode,status,programId);
                //List<SysMessagHd> sysMessagHds = sysMessagExMapper.loadDetailReport(from_date, to_date,);
                return MybatisPageHelper.getPageResult(pageRequest, new PageInfo(sysMessagHds));

            }

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex.fillInStackTrace());
        }
        return null;
    }
    @Override
    public ByteArrayInputStream reportDetailDownload(PageRequest pageRequest, File file, String userName) {
        try {
            pageRequest.setPageNum(1);
            pageRequest.setPageSize(100000);
         List<SysMessagHd> list = (List<SysMessagHd>) (loadDetailReport(pageRequest, userName).getContent());
            //List<SysMessagHd> list = (List<SysMessagHd>) (findPage(pageRequest, userName).getContent());
            //List<SysMessagHd> list = (List<SysMessagHd>) (loadReport(pageRequest, userName).getContent());

            List<SysMessagHd> messagDts = list.stream().collect(Collectors.toCollection(LinkedList::new));

            if (messagDts != null && messagDts.size() > 0) {

                FileInputStream inputStream = new FileInputStream(file);
                XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
                inputStream.close();

                SXSSFWorkbook wb = new SXSSFWorkbook(workbook, 100); // keep 100 rows in memory, exceeding rows will be flushed to disk
                Sheet sheet = wb.getSheetAt(0);

                Font font = wb.createFont();
                font.setBold(false);
                font.setFontName("Arial");

                CellStyle style = wb.createCellStyle();
                style.setFont(font);
                style.setAlignment(HorizontalAlignment.LEFT);
                style.setVerticalAlignment(VerticalAlignment.CENTER);
                style.setBorderBottom(BorderStyle.THIN);
                style.setBorderTop(BorderStyle.THIN);
                style.setBorderRight(BorderStyle.THIN);
                style.setBorderLeft(BorderStyle.THIN);
                style.setWrapText(true);
                int rowIdx = 1;
                for (SysMessagHd dt : messagDts) {
                    Row row = sheet.createRow(rowIdx++);
                    int i = 0;
                    ExcelUtil.createCell(row, i++, dt.getstt(), (XSSFCellStyle) style);
                    ExcelUtil.createCell(row, i++, dt.getTaxcode(), (XSSFCellStyle) style);
                    ExcelUtil.createCell(row, i++, dt.getCorname(), (XSSFCellStyle) style);
                    ExcelUtil.createCell(row, i++, dt.getStatus(), (XSSFCellStyle) style);
                    ExcelUtil.createCell(row, i++, dt.getsdt1(), (XSSFCellStyle) style);
                    ExcelUtil.createCell(row, i++, dt.getsdt2(), (XSSFCellStyle) style);
                    ExcelUtil.createCell(row, i++, DateTimeUtils.convertDateToString(dt.getSchedulerDetailDateTime(), "dd/MM/yyyy HH:mm:ss"), (XSSFCellStyle) style);

                }
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                wb.write(out);
                wb.dispose();
                workbook.close();
                return new ByteArrayInputStream(out.toByteArray());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }



        return null;
    /*    try {
            pageRequest.setPageNum(1);
            pageRequest.setPageSize(100000);
            List<SysMessagHd> list = (List<SysMessagHd>) (loadDetailReport(pageRequest, userName).getContent());
            List<SysMessagHd> messagDts = list.stream().collect(Collectors.toCollection(LinkedList::new));

            FileInputStream inputStream = null;
            XSSFWorkbook workbook = null;
            SXSSFWorkbook wb = null;
            try {
                if (messagDts != null && messagDts.size() > 0) {
                    inputStream = new FileInputStream(file);
                    workbook = new XSSFWorkbook(inputStream);

                    wb = new SXSSFWorkbook(workbook, 100);
                    Sheet sheet = wb.getSheetAt(0);

                    Font font = wb.createFont();
                    font.setBold(false);
                    font.setFontName("Arial");

                    CellStyle style = wb.createCellStyle();
                    style.setFont(font);
                    style.setAlignment(HorizontalAlignment.LEFT);
                    style.setVerticalAlignment(VerticalAlignment.CENTER);
                    style.setBorderBottom(BorderStyle.THIN);
                    style.setBorderTop(BorderStyle.THIN);
                    style.setBorderRight(BorderStyle.THIN);
                    style.setBorderLeft(BorderStyle.THIN);
                    style.setWrapText(true);

                    int rowIdx = 1;

                    for (SysMessagHd dt : messagDts) {
                        Row row = sheet.createRow(rowIdx++);
                        int i = 0;
                        ExcelUtil.createCell(row, i++, dt.getstt(), (XSSFCellStyle) style);
                        ExcelUtil.createCell(row, i++, dt.getTaxcode(), (XSSFCellStyle) style);
                        ExcelUtil.createCell(row, i++, dt.getCorname(), (XSSFCellStyle) style);
                        ExcelUtil.createCell(row, i++, dt.getStatus(), (XSSFCellStyle) style);
                        ExcelUtil.createCell(row, i++, dt.getsdt1(), (XSSFCellStyle) style);
                        ExcelUtil.createCell(row, i++, dt.getsdt2(), (XSSFCellStyle) style);
                        ExcelUtil.createCell(row, i++, DateTimeUtils.convertDateToString(dt.getSchedulerDetailDateTime(), "dd/MM/yyyy HH:mm:ss"), (XSSFCellStyle) style);
                    }

                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    wb.write(out);
                    return new ByteArrayInputStream(out.toByteArray());
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            finally {
                if(inputStream != null)
                    inputStream.close();
                if(wb != null)
                    wb.close();
                if(workbook != null)
                    workbook.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;*/

    }

    @Override
    public ByteArrayInputStream reportGenDownload(PageRequest pageRequest, File file, String userName) {

     try {
            pageRequest.setPageNum(1);
            pageRequest.setPageSize(100000);
            List<SysMessagHd> list = (List<SysMessagHd>) (loadReport(pageRequest, userName).getContent());
            List<SysMessagHd> messagDts = list.stream().collect(Collectors.toCollection(LinkedList::new));

            FileInputStream inputStream = null;
            XSSFWorkbook workbook = null;
            SXSSFWorkbook wb = null;
            try {
                if (messagDts != null && messagDts.size() > 0) {
                    inputStream = new FileInputStream(file);
                    workbook = new XSSFWorkbook(inputStream);

                    wb = new SXSSFWorkbook(workbook, 100);
                    Sheet sheet = wb.getSheetAt(0);

                    Font font = wb.createFont();
                    font.setBold(false);
                    font.setFontName("Arial");

                    CellStyle style = wb.createCellStyle();
                    style.setFont(font);
                    style.setAlignment(HorizontalAlignment.LEFT);
                    style.setVerticalAlignment(VerticalAlignment.CENTER);
                    style.setBorderBottom(BorderStyle.THIN);
                    style.setBorderTop(BorderStyle.THIN);
                    style.setBorderRight(BorderStyle.THIN);
                    style.setBorderLeft(BorderStyle.THIN);
                    style.setWrapText(true);

                    int rowIdx = 1;

                    for (SysMessagHd dt : messagDts) {
                        Row row = sheet.createRow(rowIdx++);
                        int i = 0;
                        ExcelUtil.createCell(row, i++, dt.getTitle(), (XSSFCellStyle) style);
                        ExcelUtil.createCell(row, i++, dt.getSmsTotal(), (XSSFCellStyle) style);
                        ExcelUtil.createCell(row, i++, dt.getSendSuccess(), (XSSFCellStyle) style);
                        ExcelUtil.createCell(row, i++, dt.getSendFail(), (XSSFCellStyle) style);
                    }

                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    wb.write(out);
                    return new ByteArrayInputStream(out.toByteArray());
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            finally {
                if(inputStream != null)
                    inputStream.close();
                if(wb != null)
                    wb.close();
                if(workbook != null)
                    workbook.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }




    private HashMap<String, List<Object>> convertToChartData(List<SysMessagHd> sysMessagHds) {
        List<Object> timeList = new ArrayList<>();
        List<Object> totalList = new ArrayList<>();
        List<Object> successList = new ArrayList<>();
        List<Object> failList = new ArrayList<>();

        sysMessagHds.sort((o1, o2) -> o1.getSchedulerDateTime().compareTo(o2.getSchedulerDateTime()));

        for (SysMessagHd hd : sysMessagHds) {
            timeList.add(DateTimeUtils.convertDateToString(hd.getSchedulerDateTime(), "dd/MM HH:mm:ss"));
            totalList.add(hd.getSmsTotal());
            successList.add(hd.getSendSuccess());
            failList.add(hd.getSendFail());
        }

        HashMap<String, List<Object>> listHashMap = new HashMap<>();

        listHashMap.put("timeList", timeList);
        listHashMap.put("totalList", totalList);
        listHashMap.put("successList", successList);
        listHashMap.put("failList", failList);
        return listHashMap;
    }

    private HashMap<String, List<Object>> convertToChartData1(List<SysMessagHd> sysMessagHds) {
        List<Object> timeList = new ArrayList<>();
        List<Object> corList = new ArrayList<>();

        for (SysMessagHd hd : sysMessagHds) {
            timeList.add(DateTimeUtils.convertDateToString(hd.getSchedulerDateTime(), "dd/MM HH:mm:ss"));
            corList.add(hd.getCorTotal());
        }

        HashMap<String, List<Object>> listHashMap = new HashMap<>();

        listHashMap.put("timeList", timeList);
        listHashMap.put("corList", corList);
        return listHashMap;
    }

    private String getStatus(byte status) {
        switch (status) {
            case (byte) 0:
                return "Pending";
            case (byte) 1:
                return "Sent successfully";
            case (byte) 2:
                return "Failed to send";
            default:
                return "";
        }
    }


}
