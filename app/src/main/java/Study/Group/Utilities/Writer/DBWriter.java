package Study.Group.Utilities.Writer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class DBWriter {
    public DBWriter(String filePath){
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = br.readLine()) != null) {
                StringBuilder sb = new StringBuilder(line);
                sb.deleteCharAt(0);
                sb.deleteCharAt(sb.length()-1);
                sb.deleteCharAt(sb.length()-1);

                String name = null, faculty = null, id = null;
                String arr[] = sb.toString().split(",");
                name = arr[0].substring(arr[0].indexOf(":")+2,arr[0].lastIndexOf("\""));
                id = arr[1].substring(arr[1].indexOf(":")+2,arr[1].lastIndexOf("\""));
                faculty = arr[2].substring(arr[2].indexOf(":")+2,arr[2].lastIndexOf("\""));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
