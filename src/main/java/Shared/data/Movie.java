package Shared.data;

import Constant.ServerConstant;

public class Movie implements IMovie{
    public enum Movies {
        Avatar,
        Avengers,
        Titanic,
        None;

        public static Movies fromInt(int movieID) {
            return switch (movieID) {
                case 1 -> Avatar;
                case 2 -> Avengers;
                case 3 -> Titanic;
                default -> None;
            };
        }

        public static Movies[] getMovies() {
            return new Movies[]{
                    Movies.Avatar,
                    Movies.Avengers,
                    Movies.Titanic
            };
        }
    }

    public enum Slots {
        Morning,
        Afternoon,
        Evening,
        None;

        public static Slots fromInt(int slotID) {
            return switch (slotID) {
                case 1 -> Morning;
                case 2 -> Afternoon;
                case 3 -> Evening;
                default -> None;
            };
        }

        public static Slots[] getSlots() {
            return new Slots[]{
                    Slots.Morning,
                    Slots.Afternoon,
                    Slots.Evening
            };
        }
    }

    public enum Theaters {
        Atwater,
        Verdun,
        Outremont,
        None;

        public static Theaters fromInt(int theaterID) {
            return switch (theaterID) {
                case 1 -> Atwater;
                case 2 -> Verdun;
                case 3 -> Outremont;
                default -> None;
            };
        }

        public static Theaters[] getTheaters() {
            return new Theaters[]{
                    Theaters.Atwater,
                    Theaters.Verdun,
                    Theaters.Outremont
            };
        }
    }

    private String movieID = null;

    public void moviesPrompt(String heading) {
        System.out.println(heading);
        int i=1;
        for(Movies movie : Movies.getMovies()) {
            System.out.println(i++ +". "+movie);
        }
    }

    public void theaterPrompt(String heading) {
        System.out.println(heading);
        int i=1;
        for(Theaters theater : Theaters.getTheaters()) {
            System.out.println(i++ +". "+theater);
        }
    }

    public void slotsPrompt(String heading) {
        System.out.println(heading);
        for(Slots slot : Slots.getSlots()) {
            System.out.println(slot);
        }
    }

    public void bookingCapacityPrompt(String heading) {
        System.out.println(heading);
    }

    public boolean validateMovieID(String movieID) {
        if (movieID.length() == 10) {
            String serverPrefix = movieID.substring(0, 3);
            String slot = movieID.substring(3,4);
            String date = movieID.substring(4,10);
            if (serverPrefix.equals(ServerConstant.SERVER_OUTREMONT_PREFIX) ||
                    serverPrefix.equals(ServerConstant.SERVER_ATWATER_PREFIX) ||
                    serverPrefix.equals(ServerConstant.SERVER_VERDUN_PREFIX)) {
                if (slot.equals("M") ||
                        slot.equals("A")||
                        slot.equals("E")) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean validateUserID(String userID) {
        if (userID.length() == 8) {
            String serverPrefix = userID.substring(0, 3);
            String slot = userID.substring(3,4);
            String number = userID.substring(4,8);
            if (serverPrefix.equals(ServerConstant.SERVER_OUTREMONT_PREFIX) ||
                    serverPrefix.equals(ServerConstant.SERVER_ATWATER_PREFIX) ||
                    serverPrefix.equals(ServerConstant.SERVER_VERDUN_PREFIX)) {
                if (slot.equals("M") || slot.equals("A")) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getMovieName(int movieIndex) {
        return String.valueOf(Movies.fromInt(movieIndex));
    }

    public String getTheaterName(int theaterIndex) {
        return String.valueOf(Theaters.fromInt(theaterIndex));
    }

    public String grepServerPrefixByMovieID(String movieID) {
        return String.valueOf(movieID.substring(0,3));
    }
}
