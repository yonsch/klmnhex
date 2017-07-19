import java.util.regex.*;

public class Parser {
    public static void parse(String s, Byte[] bytes){
        String[] parts = s.split(",");
        int index = 0;
        for(String str:parts){
            String[] parts2 = str.split(":");
            System.out.println(parts2[0]);
            Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(parts2[1]);
            int count=0;
            if(m.find()) {
                count = Integer.parseInt(m.group(1), 10);
            }
            if (parts2[1].startsWith("asc")){
                for (int i = 0; i < count; i++) {
                    char c = (char) (bytes[index] & 0xFF);
                    //String val=Byte.toString(bytes[index]);
                    System.out.print(c);
                    index++;
                }
            }
            if (parts2[1].startsWith("int")){
                byte[] toint = {bytes[index],bytes[index+1],bytes[index+2],bytes[index+3]};
                System.out.print(ByteTools.bytesToInt(toint,true));
                index += 4;
            }
        }


    }
}
