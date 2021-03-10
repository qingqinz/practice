import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class TestFile {

    public static void main(String[] args) throws FileNotFoundException {
        File file = new File("/Users/zqq/Downloads/bankfilepath/scan_insu/000001234567/20210228//1.aaa");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
    }
}

