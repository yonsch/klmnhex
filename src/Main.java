import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by Michael on 7/18/2017.
 */
public class Main
{
    public static void main(String[] args) {
        HexFile f = new HexFile();
        f.open("C:\\Users\\Yonch\\Desktop\\MyStereogram.jpg");
        //System.out.println(f);
        f.saveAs("C:\\Users\\Yonch\\Desktop\\MyStereogram3.jpg");
    }
}
