package Shared.data;

import Constant.ServerConstant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    public static String getServerNameByMovieID(String movieID) {
        String serverPrefix = movieID.substring(0,3).toUpperCase();
        return switch (serverPrefix) {
            case ServerConstant.SERVER_ATWATER_PREFIX -> ServerConstant.SERVER_ATWATER;
            case ServerConstant.SERVER_VERDUN_PREFIX -> ServerConstant.SERVER_VERDUN;
            case ServerConstant.SERVER_OUTREMONT_PREFIX -> ServerConstant.SERVER_OUTREMONT;
            default -> null;
        };
    }

    public static String getServerPrefixByMovieID(String movieID) {
        String serverPrefix = movieID.substring(0,3).toUpperCase();
        return switch (serverPrefix) {
            case ServerConstant.SERVER_ATWATER_PREFIX -> ServerConstant.SERVER_ATWATER_PREFIX;
            case ServerConstant.SERVER_VERDUN_PREFIX -> ServerConstant.SERVER_VERDUN_PREFIX;
            case ServerConstant.SERVER_OUTREMONT_PREFIX -> ServerConstant.SERVER_OUTREMONT_PREFIX;
            default -> null;
        };
    }

    public static String getServerNameByServerPrefix(String serverPrefix) {
        return switch (serverPrefix) {
            case ServerConstant.SERVER_ATWATER_PREFIX -> ServerConstant.SERVER_ATWATER;
            case ServerConstant.SERVER_VERDUN_PREFIX -> ServerConstant.SERVER_VERDUN;
            case ServerConstant.SERVER_OUTREMONT_PREFIX -> ServerConstant.SERVER_OUTREMONT;
            default -> null;
        };
    }

    public static String getSlotByMovieID(String movieID) {
        String slot = movieID.substring(3,4).toUpperCase();
        return switch (slot) {
            case "A" -> Movie.Slots.Afternoon.toString();
            case "M" -> Movie.Slots.Morning.toString();
            case "E" -> Movie.Slots.Evening.toString();
            default -> null;
        };
    }

    public static Date getSlotDateByMovieID(String movieID) {
        String dateString = movieID.substring(4,10);
        try {
            return new SimpleDateFormat("ddMMyy").parse(dateString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
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
        return new ArrayList<Integer>(map.values());
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

    public static List<MovieState> sortMovieBySlots(List<MovieState> movieObj) {
        movieObj.sort(new Comparator<MovieState>() {
            @Override
            public int compare(MovieState o1, MovieState o2) {
                Integer movieSlotFirst = switch (o1.getMovieID().substring(3, 4).toUpperCase()) {
                    case "M" -> 1;
                    case "A" -> 2;
                    case "E" -> 3;
                    default -> 0;
                };
                int movieSlotSecond = switch (o2.getMovieID().substring(3, 4).toUpperCase()) {
                    case "M" -> 1;
                    case "A" -> 2;
                    case "E" -> 3;
                    default -> 0;
                };
                int dateCompare = o1.getMovieDate().compareTo(o2.getMovieDate());
                int slotCompare = movieSlotFirst.compareTo(movieSlotSecond);
                if (dateCompare == 0) {
                    return ((slotCompare == 0) ? dateCompare : slotCompare);
                } else {
                    return dateCompare;
                }
            }
        });
        return movieObj;
    }

    public static List<MovieState> sortMovieByDates(List<MovieState> movieInfo) {
        movieInfo.sort(new Comparator<MovieState>() {
            public int compare(MovieState movieA, MovieState movieB) {
                return movieA.getMovieDate().compareTo(movieB.getMovieDate());
            }
        });
        return movieInfo;
    }

    public static int getWeekOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.WEEK_OF_MONTH);
    }

    public static int getMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH);
    }

    public static String createLogMsg(String customerID, String movieId, String movieName, int numberOfTickets, String serverResponse) {
       StringBuilder sb = new StringBuilder();
        if(customerID!=null&&!customerID.isEmpty() && movieId!=null&&!movieId.isEmpty() && movieName!=null&&!movieName.isEmpty() && numberOfTickets!=-1 && serverResponse!=null&&!serverResponse.isEmpty())
            sb.append("RequestParameters: \n");
        if(customerID!=null&&!customerID.isEmpty()) sb.append("CustomerID: " + customerID+ " |");
        if(movieId!=null&&!movieId.isEmpty()) sb.append(" MovieID: " + movieId+ " |");
        if(movieName!=null&&!movieName.isEmpty()) sb.append(" MovieName: " + movieName + " |");
        if(numberOfTickets!=-1) sb.append(" Number Of Tickets: " + numberOfTickets + " |");
        if(serverResponse!=null&&!serverResponse.isEmpty()) sb.append(" ServerResponse: " + serverResponse);
        return  sb.toString();
    }
}
