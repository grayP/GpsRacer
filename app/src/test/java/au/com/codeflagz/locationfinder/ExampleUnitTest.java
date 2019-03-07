package au.com.codeflagz.locationfinder;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        double slowValue=23.2;

        double result= Math.floor(slowValue/10)*10-20;

        assertEquals(0.0d, result,.01);


        slowValue=16.7;
        result= Math.floor(slowValue/10)*10-20;
        assertEquals(-10.0d, result,.01);
        slowValue=143.7;
        result= Math.floor(slowValue/10)*10-20;
        assertEquals(120.0d, result,.01);

    }
}