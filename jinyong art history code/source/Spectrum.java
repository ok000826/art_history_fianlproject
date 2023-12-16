import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Map;


public class Spectrum {
    public static final String sourceImage = "huskyy.jpg"; // input source image file
    public static final String sourceData = "annual temperature.txt"; // input source data file
    public static final Color lowEnd = new Color(255, 255, 255); // white
    public static final Color highEnd = new Color(0, 255, 0); // deep blue
    public static double maxTemp;
    public static double minTemp;
    public static int maxYear;
    public static int minYear;
    public static void main(String[] args) throws Exception {
        Picture pb = new Picture(sourceImage);
        Color[][] pixels = pb.getPixels();
        Picture modifiedPB = new Picture(pixels[0].length, pixels.length);
        
        
        paintPixels(pixels);

        

        modifiedPB.setPixels(pixels);

        //pb.show();
        modifiedPB.show();
        modifiedPB.save("spectral bear.jpg");
    }
    
    public static Map<Integer, Double> parseData() throws FileNotFoundException {
        Map<Integer, Double> records = new HashMap<Integer, Double>();
        minTemp = Integer.MAX_VALUE;
        maxTemp = Integer.MIN_VALUE;
        minYear = Integer.MAX_VALUE;
        maxYear = Integer.MIN_VALUE;
        try (Scanner scanner = new Scanner(new File(sourceData))) {
            while (scanner.hasNextLine()) {
                String[] parsed = scanner.nextLine().split(",");
                if (parsed[0].equals("GCAG")) {
                    records.put(Integer.parseInt(parsed[1]), Double.parseDouble((parsed[2])));
                    minTemp = Math.min(minTemp, Double.parseDouble(parsed[2]));
                    maxTemp = Math.max(maxTemp, Double.parseDouble(parsed[2]));
                    minYear = Math.min(minYear, Integer.parseInt(parsed[1]));
                    maxYear = Math.max(maxYear, Integer.parseInt(parsed[1]));
                }
            }
        }
        return records;
    }


    public static void paintPixels(Color[][] pixels) throws FileNotFoundException {
        Map<Integer, Double> records = parseData();

        for (int i = 0; i < pixels[0].length; i++) {
            Color columnColor = getColor(i, records, pixels);
            for (int j = 0; j < pixels.length; j++) {
                if (isNearBlack(pixels[j][i])) pixels[j][i] = columnColor;
            }
        }
    }

    public static Color getColor(int col, Map<Integer, Double> records, Color[][] pixels) {
        int calculatedYearOfColumn = (int)(minYear +  (col * 1.0 / (pixels[0].length) * (maxYear - minYear)));
        double temperature = records.get(calculatedYearOfColumn);
        double resultRed = lowEnd.getRed() + (highEnd.getRed() - lowEnd.getRed()) * ((temperature - minTemp) / (maxTemp - minTemp));
        double resultBlue = lowEnd.getBlue() + (highEnd.getBlue() - lowEnd.getBlue()) * ((temperature - minTemp) / (maxTemp - minTemp));
        double resultGreen = lowEnd.getGreen() + (highEnd.getGreen() - lowEnd.getGreen()) * ((temperature - minTemp) / (maxTemp - minTemp));
        return new Color((int)resultRed, (int)resultBlue, (int) resultGreen);
    }

    public static boolean isNearBlack(Color c) {
        return (c.getRed() < 15 && c.getBlue() < 15 && c.getGreen() < 15);
    }
}
