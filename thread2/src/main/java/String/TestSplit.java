package String;

public class TestSplit {
    public static void main(String[] args) {
        String str="insert overwrite table ${DBNAME}.t_cb_grbdb partition(org_no,load_date)\n" +
                "select \n" +
                " a.lsh\n" +
                " ,a.bxjgdm\n" +
                " ,a.bxjgmc\n" +
                " ,a.ttbdh\n" +
                " ,a.grbdh\n" +
                " ,a.bdtgxz\n" +
                " ,a.jtdbz\n" +
                " ,a.gljgdm\n" +
                " ,a.gljgmc\n" +
                " ,a.jgxqdm\n" +
                " ,a.cbdq\n" +
                " ,a.xsqd\n" +
                " ,a.dljgbm\n" +
                " ,a.dljgmc\n" +
                " ,a.tbrkhbh\n" +
                " ,a.jfjg\n" +
                " ,a.jffs\n" +
                " ,a.qdrq\n" +
                " ,a.hbdm\n" +
                " ,a.bf\n" +
                " ,a.be\n" +
                " ,a.ljbf\n" +
                " ,a.jzrq\n" +
                " ,a.sqjfrq\n" +
                " ,a.bdsxrq\n" +
                " ,a.hblx\n" +
                " ,a.tbdsqrq\n" +
                " ,a.bdzt\n" +
                " ,a.bdxs\n" +
                " ,a.bdmqrq\n" +
                " ,a.bdzzrq1\n" +
                " ,a.bdzzrq2\n" +
                " ,a.bdxlhfrq\n" +
                " ,a.bdzzyy\n" +
                " ,a.hlwbxywbz\n" +
                " ,a.smrztgbz\n" +
                " ,a.smrzfs\n" +
                " ,a.fzctbbz\n" +
                " ,a.fzcgfbz\n" +
                " ,a.jbywbz\n" +
                " ,a.jbglf\n" +
                " ,a.ybgrzhgmbz\n" +
                " ,a.org_no\n" +
                " ,'${loadDate}' as load_date\n" +
                "  from\n" +
                "(select *,row_number() over (partition by bxjgdm,grbdh order by load_date desc,lsh desc) num from (\n" +
                "  select  \n" +
                "  *\n" +
                "  from ${DBNAME}.t_cb_grbdb where org_no = '${org_no}' and load_date = '${BloadDate}'\n" +
                "  union all\n" +
                "  select  \n" +
                "  *\n" +
                "  from ${DBNAME}.t_cb_grbdb_mid where org_no = '${org_no}' and load_date = '${loadDate}') b ) a \n" +
                " where a.num=1";
        String[] strings = str.split("\\$\\{DBNAME\\}");
        System.out.println(strings[0]);
        System.out.println(strings[1]);
        System.out.println(strings[2]);
        System.out.println(strings[3]);


    }
}
