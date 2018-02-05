package common;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PositionLoader {
    public static Map<String, Integer> loader(){
        Map<String, Integer> posMap = new HashMap<>();
        List<String> lines = null;
        try {
            lines = Files.readAllLines(Paths.get("/home/yangx/tmp/ctb/fieldPosition"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (String line :
                lines) {
            String[] split = line.split(",");
            posMap.put(split[0], new Integer(split[1]));
        }
        return posMap;
    }

    public static void main(String[] args){
        Map<String, Integer> pos = loader();
        for (Map.Entry<String, Integer> entry: pos.entrySet()
             ) {
            System.out.println(entry.getKey() +"\t"+ entry.getValue());
        }
    }
}
