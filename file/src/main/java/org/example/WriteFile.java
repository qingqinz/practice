package org.example;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

public class WriteFile {
    public static void main(String[] args) throws IOException {
        File file = new File("test.txt");
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write("first");
        fileWriter.flush();
        fileWriter.close();
    }
}
