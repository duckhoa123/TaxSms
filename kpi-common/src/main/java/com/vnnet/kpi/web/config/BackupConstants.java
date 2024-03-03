package com.vnnet.kpi.web.config;

import java.io.File;


public interface BackupConstants {

    /**
     * backup directory name
     */
    public static final String BACKUP_FOLDER_NAME = "_iot_backup";
    /**
     * backup directory
     */
    public static final String BACKUP_FOLDER = System.getProperty("user.home") + File.separator + BACKUP_FOLDER_NAME + File.separator;
    /**
     * Restore directory, default is backup directory
     */
    public static final String RESTORE_FOLDER = BACKUP_FOLDER;
    /**
     * Date format
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd_HHmmss";
    /**
     * SQL extension
     */
    public static final String SQL_EXT = ".sql";
    /**
     * default backup file name
     */
    public static final String BACKUP_FILE_NAME = "iot" + SQL_EXT;
    /**
     * Default backup restore directory name
     */
    public static final String DEFAULT_BACKUP_NAME = "backup";
    /**
     * Restore files by default backup
     */
    public static final String DEFAULT_RESTORE_FILE = BACKUP_FOLDER + DEFAULT_BACKUP_NAME + File.separator + BACKUP_FILE_NAME;

}
