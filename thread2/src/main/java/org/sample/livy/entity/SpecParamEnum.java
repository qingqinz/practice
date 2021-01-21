package org.sample.livy.entity;

public enum SpecParamEnum {
    LOAD_DATE("loadDate", "当前批次日期"),
    ORG_NO("orgNo", "任务的机构编码"),
    SCAN_LAST_TIME("scanLastTime", "上次扫描的文件最后修改时间"),
    H2H_SCAN_LAST_TIME("h2hScanLastTime", "上次扫描的文件最后修改时间"),
    LOAD_FILE_PATH("filePath", "加载时的文件路径"),
    LOAD_PARENT_ORG_NO("parentOrgNo", "加载时银行的父机构编码，从etl_file表获取"),
    LOAD_DATABASE_NAME("loadDatabaseName", "加载时数据库名，从etl_file表获取"),
    LOAD_DATA_TABLE_NAME("loadDataTableName", "加载时数据表名，从etl_file表获取"),
    LOAD_FINANCE_NO("loadFinanceNo", "加载时银行的金融许可证号，从etl_file表获取"),
    LOAD_LOC_ID("loadLocId", "加载时银行地区，从etl_file表获取"),
    CLEAN_START_DATE("cleanStartDate", "清洗的开始日期，优先取全量日期，如果没有，就用loadDate"),
    CLEAN_END_DATE("cleanEndDate", "清洗的结束日期， 默认loadDate"),
    CLEAN_DATABASE_NAME("cleanDatabaseName", "清洗任务的数据库名"),
    CLEAN_DATA_TABLE_NAME("cleanDataTableName", "清洗任务数据表名"),
    DQ_ORG_NO_NAME("dq_org_no_name", "检核的机构编码参数的名称"),
    DQ_LOAD_DATE_NAME("dq_load_date_name", "检核的批次日期参数的名称"),
    ;

    private String key;
    private String desc;

    SpecParamEnum(String key, String desc) {
        this.key = key;
        this.desc = desc;
    }

    public String getKey() {
        return key;
    }

    public String getDesc() {
        return desc;
    }
}
