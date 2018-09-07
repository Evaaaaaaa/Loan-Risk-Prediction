import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Created by 352videoquiz on 2018/3/12.
 */
public class test {

    public static void main (String [] args) {
        String outputFile = args[0];
        String exampleOutput = args[1];

        BufferedReader in1;
        BufferedReader in2;

        try {
            in1 = new BufferedReader(new FileReader(outputFile));
            in2 =  new BufferedReader(new FileReader(exampleOutput));

            ArrayList<String> outputLine = new ArrayList<>();
            ArrayList<String>exampleLine = new ArrayList<>();
            while (in1.ready()) {
                String line = in1.readLine();
                outputLine.add(line);

            }
            in1.close();

            while (in2.ready()) {
                String line = in2.readLine();
                exampleLine.add(line);

            }
            in2.close();

//            if (outputLine.size()!=exampleLine.size()) {
//                System.out.println("line count doesn't match");
//            } else {

                for (int i = 0; i<exampleLine.size();i++) {
                    if (!outputLine.get(i).equals(exampleLine.get(i))) {
                        System.out.println("line " + i + " doesn't match ******" );
                    }


                }


           // }


        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }



    }
}
