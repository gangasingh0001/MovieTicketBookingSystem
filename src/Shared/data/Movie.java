package Shared.data;

import Constant.ServerConstant;

public class Movie implements IMovie{
    public enum Movies {
        Avatar,
        Avengers,
        Titanic,
        None;

        public static Movies fromInt(int movieID) {
            switch (movieID) {
                case 1 : return Avatar;
                case 2 : return Avengers;
                case 3 : return Titanic;
                default : return None;
            }
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
             switch (slotID) {
                case 1 : return Morning;
                case 2 : return Afternoon;
                case 3 : return Evening;
                default : return None;
            }
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
             switch (theaterID) {
                case 1 : return Atwater;
                case 2 : return Verdun;
                case 3 : return Outremont;
                default : return None;
            }
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

    public String validateMovieID(String movieID) {
        String serverPrefix = movieID.substring(0, 3);
        String slot = movieID.substring(3,4);
        String date = movieID.substring(4,10);
        if (movieID.length() == 10) {
            if (serverPrefix.equals(ServerConstant.SERVER_OUTREMONT_PREFIX) ||
                    serverPrefix.equals(ServerConstant.SERVER_ATWATER_PREFIX) ||
                    serverPrefix.equals(ServerConstant.SERVER_VERDUN_PREFIX)) {
                if (slot.equals("M") ||
                        slot.equals("A")||
                        slot.equals("E")) {
                    return movieID;
                }
            }
        }
        return null;
    }

    public boolean validateUserID(String userID) {
        String serverPrefix = userID.substring(0, 3);
        String slot = userID.substring(3,4);
        String number = userID.substring(4,8);
        if (userID.length() == 8) {
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
