package com.example;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

public class ExcelImporter {

    Logger logger = LoggerFactory.getLogger(ExcelImporter.class);

    public static void main(String[] args) {
        ExcelImporter excelImporter = new ExcelImporter();
//        importExcel.importHighVersion();
        excelImporter.importLowVersion();
    }

    public boolean importHighVersion(){
        boolean result = true;
        //一共16列，第一二三行title
        ExcelImporter excelImporter = new ExcelImporter();
        try {
//            XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(new File("/Users/zqq/Downloads/zhong/建表规则元数据脚本/规则组hive+tidb+oracle-update.xlsx")));
            XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(new File("/Users/zqq/Downloads/zhong/建表规则元数据脚本/规则.xls")));
            Sheet sheet = wb.getSheetAt(0);
            List<Object> object ;
            Map<Integer, String> beanpros = getProsMap();
            //正式内容从第四行开始
            object = excelImporter.getObjectFromExcel(3, beanpros, TSysRule.class, sheet);
            logger.info("size,{}",object.size());
            int i =0;
            for(Object obj:object){
                i++;
                TSysRule bean = (TSysRule) obj ;
                logger.info("i:" + i + "---" + bean.getRuleSeq() + "---" + bean.getBmCn());
            }
        } catch (FileNotFoundException e) {
            logger.error("",e);
        } catch (IOException e) {
            logger.error("",e);
        }
        return result;
    }

    public boolean importLowVersion(){
        boolean result=true;
        ExcelImporter excelImporter = new ExcelImporter();
        try {
            HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(new File("/Users/zqq/Downloads/zhong/建表规则元数据脚本/规则.xls")));
            Sheet sheet = wb.getSheetAt(0);
            List<Object> results;
            Map<Integer, String> beanpros = getProsMap();
            //正式内容从第四行开始
            results = excelImporter.getObjectFromExcel(3, beanpros, TSysRule.class, sheet);
            for(Object obj:results){
                TSysRule bean = (TSysRule) obj ;
                logger.info(bean.toString());
            }
        } catch (FileNotFoundException e) {
            logger.error("",e);
            result = false;
        } catch (IOException e) {
            logger.error("",e);
            result = false;
        }
        return result;
    }

    private Map<Integer, String> getProsMap() {
        Map<Integer, String> beanpros = new HashMap<Integer, String>();
        beanpros.put(0, "ruleSeq");
        beanpros.put(1, "ruleTypeCode");
        beanpros.put(2, "ownershipCode");
        beanpros.put(3, "ruleDesc");
        beanpros.put(4, "levelCode");
        beanpros.put(5, "reviewSysCode");
        beanpros.put(6, "threshold");
        beanpros.put(7, "bmEn");
        beanpros.put(8, "fieldEn");
        beanpros.put(9, "topCategoryCode");
        beanpros.put(10, "subCategoryCode");
        beanpros.put(12, "status");
        beanpros.put(13, "diySql");
        beanpros.put(14, "tidbSql");
        beanpros.put(15, "oracleSql");
        return beanpros;
    }

    public List<Object> getObjectFromExcel(int beginRow, Map<Integer,String> beanpros, Class classPathName, Sheet sheet){
        List<Object> results = new ArrayList<>();
        try {
            Class clazz = classPathName;
            Set<Integer> set = beanpros.keySet() ;
            Row row = sheet.getRow(beginRow);
            while(row!=null) {
                Object obj = clazz.newInstance() ;
                for(Integer key:set){
                    String value = "";
                    if(row!=null){
                        Cell cell = row.getCell(key) ;
                        if(cell!=null){
                            int type = cell.getCellType() ;
                            if(type == Cell.CELL_TYPE_STRING){
                                value = cell.getStringCellValue() ;
                            }else if(type==Cell.CELL_TYPE_NUMERIC||type==Cell.CELL_TYPE_FORMULA){
                                value = String.valueOf(cell.getNumericCellValue());
                            }else if(type==Cell.CELL_TYPE_BOOLEAN){
                                value = String.valueOf(cell.getBooleanCellValue()) ;
                            }
                        }
                    }
                    if (StringUtils.isEmpty(value)){
                        continue;
                    } else {
                        PropertyDescriptor pd = new PropertyDescriptor(beanpros.get(key),clazz);
                        Method wM = pd.getWriteMethod();
                        Field[] f =TSysRule.class.getDeclaredFields();
                        Type type = null;
                        for(Field ff:f){
                            if (ff.getName().equals(beanpros.get(key))){
                                type = ff.getGenericType();
                                break;
                            }
                        }
                        if (null != type) {
                            if (type.getTypeName().equals("java.lang.Integer")){
                                Integer tmp = Integer.getInteger(value);
                                wM.invoke(obj, tmp);
                            } else if (type.getTypeName().equals("java.lang.Double")) {
                                Double tmp = Double.parseDouble(value);
                                wM.invoke(obj, tmp);
                            } else {
                                wM.invoke(obj, value);
                            }
                        } else {
                            return null;
                        }
                    }
                }
                results.add(obj);
                row = sheet.getRow(++beginRow);
            }
        } catch (Exception e) {
            logger.error("",e);
        }
        return results ;
    }

}
