import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class CSVConverter {
    private CellProcessor[] cellProcessors;
    private ICsvBeanWriter beanWriter;

    public CSVConverter() throws IOException {
        this.cellProcessors = new CellProcessor[]{
                new NotNull(), // Name
                new NotNull(), // Description
                new NotNull(), // image path
        };

        this.beanWriter = new CsvBeanWriter(new FileWriter("ABSOLUTEPATH/result.csv"),
                CsvPreference.STANDARD_PREFERENCE);
    }


    public void OutputCSV(ArrayList<Item> items) throws IOException {
        String[] header = {"name", "description", "image"};
        this.beanWriter.writeHeader(header);

        for (Item i : items) {
            beanWriter.write(i, header, this.cellProcessors);
        }
    }

    public void writeCSVFile( ArrayList<Item> items) {
        try {
            this.OutputCSV(items);

        } catch (IOException ex) {
            System.err.println("Error writing the CSV file: " + ex);
        } finally {
            if (beanWriter != null) {
                try {
                    beanWriter.close();
                } catch (IOException ex) {
                    System.err.println("Error closing the writer: " + ex);
                }
            }
        }
    }

}
