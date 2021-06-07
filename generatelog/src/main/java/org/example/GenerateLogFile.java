package org.example;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.util.Date;

public class GenerateLogFile {
    public static void main(String[] args) throws IOException {
        String path=args[0];
        File file = new File(path);
        String[] fileNames = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".txt");
            }
        });

        /**
         * 文件名
         * 文件大小（字节）
         * 文件创建完成时间（YYYY-MM-DD HH:MM:SS）
         * 文件是否正常生成（Y或N）
         * 文件记录数（行数）
         * 数据文件的MD5值
         */
        for (String fileName:fileNames){
            System.out.println("###"+fileName+"####");
            File txtFile = new File(path + "/"+fileName);
            File logFile = new File(path + "/"+fileName.substring(0,fileName.lastIndexOf(".txt"))+".log");
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(logFile));
            bufferedWriter.write(fileName);
            bufferedWriter.newLine();
            bufferedWriter.write(String.valueOf(txtFile.length()));
            bufferedWriter.newLine();
            bufferedWriter.write(String.valueOf(new Date()));
            bufferedWriter.newLine();
            bufferedWriter.write("Y");
            bufferedWriter.newLine();
            bufferedWriter.write((String.valueOf(getFileLines(path + "/"+fileName))));
            bufferedWriter.newLine();
            bufferedWriter.write(getMD5(path + "/"+fileName));
            bufferedWriter.flush();
            bufferedWriter.close();
        }

    }

    public static long getFileLines(String path) throws IOException {
        FileReader fileReader = new FileReader(path);
        LineNumberReader lineNumberReader = new LineNumberReader(fileReader);
        lineNumberReader.skip(Long.MAX_VALUE);
        long number = lineNumberReader.getLineNumber();
        fileReader.close();
        lineNumberReader.close();
        return number;
    }

    public static String getMD5(String path) throws IOException {
        return DigestUtils.md5Hex(new FileInputStream(path));
    }
}
