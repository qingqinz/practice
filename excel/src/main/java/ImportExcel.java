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
import java.lang.reflect.Method;
import java.util.*;

public class ImportExcel {

    Logger logger = LoggerFactory.getLogger(ImportExcel.class);

    public static void main(String[] args) {
        ImportExcel importExcel = new ImportExcel();
        importExcel.importHighVersion();
    }

    public boolean importHighVersion(){
        boolean result = true;
        //一共21列，第一行title
        ImportExcel importExcel = new ImportExcel();
        try {
            XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(new File("/Users/zqq/Downloads/zhong/建表规则元数据脚本/规则组hive+tidb+oracle-update.xlsx")));
            Sheet sheet = wb.getSheetAt(0);
            List<Object> results ;
            Map<Integer,String> beanpros = new HashMap<Integer, String>() ;
            beanpros.put(0, "rule_seq");
            beanpros.put(1, "rule_desc");
            beanpros.put(2, "top_category_code");
            beanpros.put(3, "bm_cn");
            beanpros.put(4, "bm_en");
            beanpros.put(5, "field_en");
            beanpros.put(6, "field_cn");
            beanpros.put(7, "status");
            beanpros.put(8, "diy_sql");
            beanpros.put(9, "tidb_sql");
            beanpros.put(10, "oracle_sql");
            beanpros.put(11, "review_sys_code");
            beanpros.put(12, "level_code");
            beanpros.put(13, "rule_type_code");
            beanpros.put(14, "ownership_code");
            results = importExcel.getDatasByrc(0, beanpros, "SysUser", sheet);
            for(Object obj:results){
                TSysRule bean = (TSysRule) obj ;
                logger.info(bean.getRuleSeq()+"---"+bean.getBmCn());
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
            beanpros.put(0, "username");
            beanpros.put(1, "password");
            results = importExcel.getDatasByrc(0, beanpros, "SysUser", sheet);
            for(Object obj:results){
                TSysRule bean = (TSysRule) obj ;
                logger.info(bean.getRuleSeq()+"---"+bean.getBmCn());
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

    public List<Object> getDatasByrc(int beginRow, Map<Integer,String> beanpros, String classPathName, Sheet sheet){
        List<Object> results = new ArrayList<>();
        try {
            Class clazz = Class.forName(classPathName);
            Set<Integer> set = beanpros.keySet() ;
            Row row = sheet.getRow(beginRow);
            while(row!=null) {
                Object obj = clazz.newInstance() ;
                for(Integer key:set){
                    String value = "" ;
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
                    wM.invoke(obj, value);
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
