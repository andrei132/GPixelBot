package GPixelBot;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Debug {
    BufferedWriter bufferedWriter;

    public Debug () {
        try {
            this.bufferedWriter = new BufferedWriter(new FileWriter("debug.log",true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeInLogFile(String text){
        try {
            bufferedWriter.write(""+text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeDebug(){
        try {
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
