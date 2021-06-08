package com.example;

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

public class ImportExcel {

    Logger logger = LoggerFactory.getLogger(ImportExcel.class);

    public static void main(String[] args) {
        ImportExcel importExcel = new ImportExcel();
        importExcel.importHighVersion();
    }

    public boolean importHighVersion(){
        boolean result = true;
        //一共18列，第一行title
        ImportExcel importExcel = new ImportExcel();
        try {
            XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(new File("/Users/zqq/Downloads/zhong/建表规则元数据脚本/规则组hive+tidb+oracle-update.xlsx")));
            Sheet sheet = wb.getSheetAt(0);
            List<Object> results ;
            Map<Integer,String> beanpros = new HashMap<Integer, String>() ;
            beanpros.put(0, "ruleSeq");
            beanpros.put(1, "ruleDesc");
            beanpros.put(2, "topCategoryCode");
            beanpros.put(3, "bmCn");
            beanpros.put(4, "bmEn");
            beanpros.put(5, "fieldEn");
            beanpros.put(6, "fieldCn");
            beanpros.put(7, "status");
            beanpros.put(8, "diySql");
            beanpros.put(9, "tidbSql");
            beanpros.put(10, "oracleSql");
            beanpros.put(11, "reviewSysCode");
            beanpros.put(13, "levelCode");
            beanpros.put(16, "ruleTypeCode");
            beanpros.put(17, "ownershipCode");
            results = importExcel.getDatasByrc(0, beanpros, TSysRule.class, sheet);
            logger.info("size,{}",results.size());
            int i =0;
            for(Object obj:results){
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
        ImportExcel importExcel = new ImportExcel();
        try {
            HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(new File("/Users/zqq/Downloads/zhong/建表规则元数据脚本/规则组hive+tidb+oracle-update.xlsx")));
            Sheet sheet = wb.getSheetAt(0);
            List<Object> results;
            Map<Integer,String> beanpros = new HashMap<Integer, String>() ;
            beanpros.put(0, "ruleSeq");
//            beanpros.put(1, "ruleDesc");
//            beanpros.put(2, "topCategoryCode");
//            beanpros.put(3, "bmCn");
//            beanpros.put(4, "bmEn");
//            beanpros.put(5, "fieldEn");
//            beanpros.put(6, "fieldCn");
            beanpros.put(7, "status");
//            beanpros.put(8, "diySql");
//            beanpros.put(9, "tidbSql");
//            beanpros.put(10, "oracleSql");
//            beanpros.put(11, "reviewSysCode");
//            beanpros.put(13, "levelCode");
//            beanpros.put(16, "ruleTypeCode");
//            beanpros.put(17, "ownershipCode");
            results = importExcel.getDatasByrc(0, beanpros, TSysRule.class, sheet);
            for(Object obj:results){
                TSysRule bean = (TSysRule) obj ;
                logger.info(bean.getRuleSeq()+"---"+bean.getBmCn()+"---"+bean.getStatus());
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

    public List<Object> getDatasByrc(int beginRow, Map<Integer,String> beanpros, Class classPathName, Sheet sheet){
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
                    if (null!=type && type.getTypeName().equals("java.lang.Integer")){
                        Integer tmp = Integer.getInteger(value);
                        wM.invoke(obj, tmp);
                    } else {
                        wM.invoke(obj, value);
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
