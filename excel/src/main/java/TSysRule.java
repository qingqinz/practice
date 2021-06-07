import lombok.Data;

import java.util.Date;


@Data
public class TSysRule {
    private Long id;
    //规则编号
    private String ruleSeq;

    private String ruleTypeCode;

    private String ownershipCode;

    private String ruleDesc;

    private String levelCode;

    private String reviewSysCode;

    private Double threshold;

    private String bmEn;

    private String bmCn;

    private String fieldEn;

    private String fieldCn;

    private String topCategoryCode;

    private String subCategoryCode;

    private String diySql;

    private String  tidbSql;

    private String  oracleSql;

    private  String  mySql;

    private String founder;

    private Date founderDate;

    private String modiry;

    private Date modiryDate;

    private Integer status;

    private  String errorMsg;

    private  String yjflmc;

    private String  secondName;

    private  String  ownershipName;

    private  String  systemName;

    private  String  typeName;

    private  String  levelName;

    private  String    xtbm;




}