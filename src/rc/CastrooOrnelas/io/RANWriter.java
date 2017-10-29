package rc.CastrooOrnelas.io;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;

/**
 * Created by raque on 10/1/2017.
 */
public class RANWriter<type> {

    File file;
    String url;

    public interface RANRowWriter<k>{
        public String[] write(k obj);
    }


    RANRowWriter<type> rowWriter;
    public RANWriter(String url, RANRowWriter<type> rowWriter) throws IOException {
        file = new File(url);
        if(!file.exists()){
            file.createNewFile();
        }else{
            if(!file.canWrite()){
                throw new IOException("RANWRITER: FILE CHOSEN CANT BE EDITED");
            }
        }
        this.rowWriter=rowWriter;
        this.url=url;
    }

    public void write(ArrayList<type> objects){

        String urlA = url.replaceAll("\\.java","");
        String urlSplit[] = urlA.split("\\\\");
        String className = urlSplit[urlSplit.length-1];
        String code=
                "import java.util.ArrayList;" + "\n\n" +
                        "public class " + className + "{"+ "\n" +
                        "\tpublic static Object[][] objects = new Object[][]{\n";


        String codeLine;
        int i=1;
        //write down all the individual variables as strings provided by the write function
        for(type obj:objects){
            codeLine = "\t\t{";
            String[] vars = rowWriter.write(obj);
            int j=1;
            for(String var: vars){
                codeLine = codeLine.concat(var);
                if(j++ !=vars.length){
                    codeLine = codeLine.concat(" , ");
                }else{
                    codeLine = codeLine.concat("}");
                }
            }
            if(i++ !=objects.size()){
                codeLine = codeLine.concat(",");
            }
            code = code.concat(codeLine + "\n");
        }
        //end object array, and then end class
        code = code.concat("\t};\n" +
                "}");
        System.out.print("\n" +code);
        System.out.println("\nDONE");

        try {
            new PrintWriter(file.getPath().toString()).close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))){
            bw.append(code);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
