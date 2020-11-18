package must.example.com.myapplication;

/**
 * Created by Lenovo on 2017/4/10.
 */

public class OffReceive {
    private static String str;
    public OffReceive(String str)
    {
        this.str=str;
    }
    public static String Get()
    {
        return str;
    }

}
