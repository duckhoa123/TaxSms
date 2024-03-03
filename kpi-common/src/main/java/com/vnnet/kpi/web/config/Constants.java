package com.vnnet.kpi.web.config;

import java.util.Arrays;
import java.util.List;

public class Constants {

    public static final int PROTOCOL_VERSION = 1;

    public static final String MASTER_VERSION = "1.0.0"; // master data version
    public static final int MASTER_EXPIRED_TIME = 1000 * 60 * 60 * 24 * 5; // master data expired timeout (in milliseconds)

    public static final long USER_TYPE_ADMIN = 1;
    public static final long USER_TYPE_QUAN_LY_NO = 4;
    public static final long USER_TYPE_KE_KHAI = 5;
    public static final long USER_TYPE_TUYEN_TRUYEN = 6;


    public static final long ROLE_QTV = 1;
    public static final long ROLE_LANH_DAO_QLN = 3;
    public static final long ROLE_NHAN_VIEN_QLN = 4;
    public static final long ROLE_LANH_DAO_KE_KHAI = 5;
    public static final long ROLE_NHAN_VIEN_KE_KHAI = 6;
    public static final long ROLE_LANH_DAO_TUYEN_TRUYEN = 7;
    public static final long ROLE_NHAN_VIEN_TUYEN_TRUYEN = 8;
    public static final long ROLE_LANH_DAO_PHONG_HO = 9;
    public static final long ROLE_NHAN_VIEN_PHONG_HO = 10;
    public static final long ROLE_LANH_DAO_VAN_PHONG = 11;
    public static final long ROLE_NHAN_VIEN_VAN_PHONG = 12;


    public static final byte DISABLE = 0;
    public static final byte ENABLE = 1;

    public static final int PERMISSION_CANCEL = 1;
    public static final int PERMISSION_EDIT = 2;
    public static final int PERMISSION_DELETE = 3;
    public static final int PERMISSION_CONFIRM = 4;

    public static final List<Long> AM_KAM_LONG = Arrays.asList(1L, 2L, 3L, 4L);

    public static final String ADMIN = "admin";

//    public static final String FILE_PDF_PATH = "D:\\NodeJs\\FileUpload\\Pdf\\";
//    public static final String FILE_PATH = "D:\\NodeJs\\FileUpload\\";
//    public static final String PDF_URL_PATH = "http://10.2.64.234:8080/readpdf/view/";
//    public static final String IMAGE_URL_PATH = "http://10.2.64.234:8080/readpdf/view/";
//    public static final String TESSERACT_FILE_DATA_PATH = "D:\\NodeJs\\FileUpload\\tessdata\\";

    public static final String FILE_PDF_PATH = "E:\\Project\\Mbf5_CucThue\\File\\Pdf\\";
    public static final String FILE_PATH = "C:\\Users\\61451\\Downloads\\Mbf5_CucThue\\Mbf5_CucThue\\File";
    public static final String PDF_URL_PATH = "http://10.40.55.26:6006/view/";
    public static final String IMAGE_URL_PATH = "http://10.40.55.26:6006/view/";
    public static final String TESSERACT_FILE_DATA_PATH = "C:\\Users\\61451\\Downloads\\Mbf5_CucThue\\Mbf5_CucThue\\File\\tessdata";

}
