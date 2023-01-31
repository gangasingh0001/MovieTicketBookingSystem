package Shared.data;

import Constant.ServerConstant;

import java.util.*;

public class Util {
    public static String getServerPrefixNameByCustomerID(String customerID) {
        String serverPrefix = customerID.substring(0,3).toUpperCase();
        return switch (serverPrefix) {
            case ServerConstant.SERVER_ATWATER_PREFIX -> ServerConstant.SERVER_ATWATER_PREFIX;
            case ServerConstant.SERVER_VERDUN_PREFIX -> ServerConstant.SERVER_VERDUN_PREFIX;
            case ServerConstant.SERVER_OUTREMONT_PREFIX -> ServerConstant.SERVER_OUTREMONT_PREFIX;
            default -> null;
        };
    }

    public static String getServerFullNameByCustomerID(String customerID) {
        String serverPrefix = customerID.substring(0,3).toUpperCase();
        System.out.println("Server prefix name: "+ serverPrefix);
        return switch (serverPrefix) {
            case ServerConstant.SERVER_ATWATER_PREFIX -> ServerConstant.SERVER_ATWATER;
            case ServerConstant.SERVER_VERDUN_PREFIX -> ServerConstant.SERVER_VERDUN;
            case ServerConstant.SERVER_OUTREMONT_PREFIX -> ServerConstant.SERVER_OUTREMONT;
            default -> null;
        };
    }

    public static int getServerPortByCustomerID(String customerID) {
        String serverPrefix = customerID.substring(0,3).toUpperCase();
        return switch (serverPrefix) {
            case ServerConstant.SERVER_ATWATER_PREFIX -> ServerConstant.SERVER_ATWATER_PORT;
            case ServerConstant.SERVER_VERDUN_PREFIX -> ServerConstant.SERVER_VERDUN_PORT;
            case ServerConstant.SERVER_OUTREMONT_PREFIX -> ServerConstant.SERVER_OUTREMONT_PORT;
            default -> -1;
        };
    }

    public static String createStringFromHashMapEntries(Map<String,Integer> map) {
        StringBuilder builder = new StringBuilder();
        for (Integer value :
                map.values()) {
            builder.append(value.toString() + " || ");
        }
        return builder.toString();
    }

    public static List<String> getKeyListByHashMap(Map<String,Integer> map) {
        List<String> keyList = new ArrayList<String>();
        for (String key :
                map.keySet()) {
            keyList.add(key);
        }
        return keyList;
    }

    public static List<Integer> getValueListByHashMap(Map<String,Integer> map) {
        List<Integer> keyList = new ArrayList<Integer>();
        for (Integer value :
                map.values()) {
            keyList.add(value);
        }
        return keyList;
    }

    public static boolean isDateEqual(Date firstDate,Date secondDate){
        Calendar firstCalendar = Calendar.getInstance();
        Calendar secondCalendar = Calendar.getInstance();
        firstCalendar.setTime(firstDate);
        secondCalendar.setTime(secondDate);
        return firstCalendar.get(Calendar.DAY_OF_YEAR) == secondCalendar.get(Calendar.DAY_OF_YEAR) &&
                firstCalendar.get(Calendar.YEAR) == secondCalendar.get(Calendar.YEAR) &&
                firstCalendar.get(Calendar.MONTH) == secondCalendar.get(Calendar.MONTH);
    }

//    public static List<Object> getListOfKeyPairByHashMap(Map<String,Object> map) {
//        List<Object> keyPair = new ArrayList<Object>();
//        for (Map.Entry<String,Object> entry : map.entrySet()) {
//            keyPair.add(entry.getValue()., entry.getKey());
//        }
//        return keyPair;
//    }
}
