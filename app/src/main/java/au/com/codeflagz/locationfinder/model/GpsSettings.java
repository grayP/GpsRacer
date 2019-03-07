package au.com.codeflagz.locationfinder.model;
import java.io.Serializable;

public class GpsSettings implements Serializable {
    public int FastSog;
    public int SlowSog;
    public int FastCog;
    public int SlowCog;
    public int NumSeconds;

    public GpsSettings(){
        FastCog=5;
        SlowCog=12;
        FastSog=5;
        SlowSog=10;
        NumSeconds=60;
    }
}
