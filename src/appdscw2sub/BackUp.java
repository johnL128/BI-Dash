package appdscw2sub;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class BackUp {
    private String backUp;

    public BackUp(String p) {
        this.backUp = p;
    }

    public void backUp(String s) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(backUp))) {
            bw.write(s);
            bw.flush();
        }
        catch (Exception e) {
            // e.printStackTrace();
        }
    }

    public Boolean exists() {
        return new File(backUp).isFile();
    }

    public String restore() {
        String s = "";

        try (BufferedReader br = new BufferedReader(new FileReader(backUp))){
            s = br.readLine();
        }
        catch (Exception e) {
            // e.printStackTrace();
        }

        return s;
    }
}
