package Shared.data;

public interface IMovie {
    public void moviesPrompt(String heading);
    public void slotsPrompt(String heading);
    public void bookingCapacityPrompt(String heading);
    public String validateMovieID(String movieID);
    public String getMovieName(int movieIndex);
    public String grepServerPrefixByMovieID(String movieID);
}
