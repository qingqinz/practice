package org.example;

import java.io.*;
import java.util.Date;

public class Specialchar {
    public static void main(String[] args) throws IOException {
        //参数为目录
//        String path=args[0];
        String path="/Users/zqq/Downloads/bankfilepath/scan_aud/titic20200731001/20210101bak";
        File file = new File(path);
        String[] fileNames = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".txt");
            }
        });
        //添加^A
        char a = (char)1;
        BufferedReader bufferedReader=null;
        BufferedWriter bufferedWriter=null;
        String[] chars= {"U","A","D"};
        for (String fileName:fileNames){
            System.out.println("###"+fileName+"####");
            File txtFile = new File(path + "/"+fileName);
            String fileNamebak = fileName+"bak";
            File txtFilebak = new File(path + "/"+fileNamebak);
            try {

                bufferedReader = new BufferedReader(new FileReader(txtFile));
                bufferedWriter = new BufferedWriter(new FileWriter(txtFilebak));
                String line;
                int i =0;
                while (null!=(line=bufferedReader.readLine())){

                    bufferedWriter.write(line);
                    bufferedWriter.write(a);
                    bufferedWriter.write(chars[(i++)%3]);
                    bufferedWriter.write('\n');
                }
            }catch (Exception e){
                e.printStackTrace();
            } finally {
                if (null!=bufferedWriter){
                    bufferedWriter.flush();
                    bufferedWriter.close();
                }
                if (null!=bufferedReader){
                    bufferedReader.close();
                }
            }
            //bak 文件重命名

            txtFilebak.renameTo(txtFile);
        }
    }
}
