package common;

import entity.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @author: yangx
 * @date: 2018/1/22
 * @description: 负责数据的载入
 */
public class DataLoader {
    private static long iptvCount = 0;
    private static long onuOltCount = 0;
    private static long oltHjswCount = 0;
    private static long brasCrCount = 0;

    public static List<SimpleIptv> loadSimpleIptv(){
        List<SimpleIptv> res = new ArrayList<SimpleIptv>();
        String[] customerIds = new String[]{"customer-1", "customer-2"};
        Double vmos = 4D;
        for (int i = 0; i < 2; i++){
            vmos += Math.random();
            SimpleIptv temp = new SimpleIptv(iptvCount, customerIds[i], vmos, new Date(System.currentTimeMillis()));
            res.add(temp);
            iptvCount++;
        }
        return res;
    }
    public static List<SimpleOnuOlt> loadSimpleOnuOlt(){
        List<SimpleOnuOlt> res = new ArrayList<SimpleOnuOlt>();
        String[] oltDownIds = new String[]{"oltdown-1", "oltdown-2"};
        Double[] upRatios = new Double[]{11D, 12D};
        for (int i = 0; i < 2; i++){
            SimpleOnuOlt temp = new SimpleOnuOlt(onuOltCount, oltDownIds[i], new Date(System.currentTimeMillis()), upRatios[i]);
            res.add(temp);
            onuOltCount++;
        }
        return res;
    }
    public static List<SimpleOltHjsw> loadSimpleOltHjsw(){
        List<SimpleOltHjsw> res = new ArrayList<SimpleOltHjsw>();
        String[] oltUpIds = new String[]{"oltup-1", "oltup-2"};
        Double[] upRatios = new Double[]{21D, 22D};
        for (int i = 0; i < 2; i++){
            SimpleOltHjsw temp = new SimpleOltHjsw(oltHjswCount, oltUpIds[i], new Date(System.currentTimeMillis()), upRatios[i]);
            res.add(temp);
            oltHjswCount++;
        }
        return res;
    }
    public static List<SimpleBrasCr> loadSimpleBrasCr(){
        List<SimpleBrasCr> res = new ArrayList<SimpleBrasCr>();
        String[] linkIds = new String[]{"linkid-1", "linkid-2"};
        Double[] upRatios = new Double[]{31D, 32D};
        for (int i = 0; i < 2; i++){
            SimpleBrasCr temp = new SimpleBrasCr(brasCrCount, linkIds[i], new Date(System.currentTimeMillis()), upRatios[i]);
            res.add(temp);
            brasCrCount++;
        }
        return res;
    }

    public static List<SimpleTopo> loadSimpleTopo(){
        List<SimpleTopo> res = new ArrayList<>();
        SimpleTopo temp = new SimpleTopo("customer-1", "oltdown-2", "oltup-1", "linkid-2");
        SimpleTopo temp4 = new SimpleTopo("customer-1", "oltdown-2", "oltup-1", "linkid-3");
        SimpleTopo temp2 = new SimpleTopo("customer-1", "oltdown-2", "oltup-2", "linkid-2");
        SimpleTopo temp5 = new SimpleTopo("customer-1", "oltdown-2", "oltup-2", "linkid-1");
        SimpleTopo temp3 = new SimpleTopo("customer-1", "oltdown-1", "oltup-1", "linkid-2");
        res.add(temp);
        res.add(temp2);
        res.add(temp3);
        res.add(temp4);
        res.add(temp5);
        return res;
    }
}
