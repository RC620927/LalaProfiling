package sample;

import rc.CastrooOrnelas.io.RSimpleTXTReader;

import java.io.FileNotFoundException;

/**
 * Created by raque on 10/13/2017.
 */
public class Loader {


    public static String getWorkspaceURL() throws FileNotFoundException {
        RSimpleTXTReader reader = new RSimpleTXTReader("LOADME.txt");
        return reader.getValues().get("WORKSPACEURL")[0];
    }

    public static String getLastAuto() throws FileNotFoundException {
        RSimpleTXTReader reader = new RSimpleTXTReader("LOADME.txt");
        return reader.getValues().get("LASTAUTO")[0];
    }


}
