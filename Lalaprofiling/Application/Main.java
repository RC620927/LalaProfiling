package Lalaprofiling.Application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 1200, 850));
        primaryStage.setMinHeight(850);
        primaryStage.setMinWidth(1200);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);


/*
        try {
            RSimpleTXTIO io = new RSimpleTXTIO("C:\\Users\\raque\\Pictures\\testingreader.txt",true);

         //   io.write("hey", "apple");
            io.remove("hey", "pear");

            HashMap<String, String[]> values = io.getValues();
            for(String key:values.keySet()){
                String keyvalues[] = values.get(key);
                for(String s: keyvalues){
                    System.out.println(key + "-:-"+s+"END");
                }
            }


        } catch (FileNotFoundException e) {
            System.out.print("WRA");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.print("WRB");
            e.printStackTrace();
        }
*/


/*        *//*try {
            RANWriter<String> ranWriter = new RANWriter<>("C:\\Users\\raque\\Music\\test\\testing.java", (obj)->{
                String[] strings = {obj, "FFF"};
                return strings;
            });
            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add("HELLO");
            arrayList.add("YO");
            arrayList.add("NICE");
            ranWriter.write(arrayList);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
*//*
        RANReader<String> ranReader = new RANReader<>((objects)->{
            return (String) objects[0];
        });
        ArrayList<String> strings = ranReader.read(testing.objects);
        strings.stream().forEach((str) ->{
            System.out.println(str);
        } );*/
    }
}
