package MovieTicketApp;

/**
* MovieTicketApp/IMovieTicketHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from MovieTicket.idl
* Monday, 6 February, 2023 8:01:20 PM EST
*/

public final class IMovieTicketHolder implements org.omg.CORBA.portable.Streamable
{
  public MovieTicketApp.IMovieTicket value = null;

  public IMovieTicketHolder ()
  {
  }

  public IMovieTicketHolder (MovieTicketApp.IMovieTicket initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = MovieTicketApp.IMovieTicketHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    MovieTicketApp.IMovieTicketHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return MovieTicketApp.IMovieTicketHelper.type ();
  }

}
