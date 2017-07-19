import java.util.regex.*;

public class Parser {
    public static void parse(String s, Byte[] bytes){
        String[] parts = s.split(",");
        for(String str:parts){
            String[] parts2 = str.split(":");
            System.out.println(parts2[0]);
            Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(parts2[1]);
            if(m.find()) {
                System.out.println(m.group(1));
            }
        }


    }
}
