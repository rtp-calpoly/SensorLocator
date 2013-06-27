/**
 * @file SensorLocator.java
 * @author Ricardo Tubío (rtpardavila[at]gmail.com)
 * @version 0.1
 *
 * @section LICENSE
 *
 * This file is part of SensorLocator.
 * SensorLocator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SensorLocator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SensorLocator.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.humsat.demo.gssw.sensorlocator;

import cx.ath.rtubio.javalib.pojos.FileHelper;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.humsat.demo.gssw.sensorlocator.kml.KMLNode;
import org.humsat.demo.gssw.sensorlocator.data.SensorData;
import org.humsat.demo.gssw.sensorlocator.kml.SimpleKMLWriter;

/**
 * Main application class.
 * 
 * @author Ricardo Tubío (rtpardavila[at]gmail.com)
 */
public class SensorLocator 
{
    /** Separator for the data of the hex string. */
    public static final String HEX_SEPARATOR = ":";
    /** Initial and final character of the data field. */
    public static final String DATA_FIELD_SEPARATOR = "\"";
    
    /** Input CSV file. */
    protected File inputFile = null;
    /** Output CSV file. */
    protected File outputFile = null;
    /** Intermediate CSV file generated with the required columns. */
    protected File intFile = null;
    
    /**
     * Main class constructor. This class must be instantiated by either of the
     * running methods available (either from the command line "main()" or 
     * from other methods like, for example, a launcher method for a servlet).
     */
    public SensorLocator(File inputFile, File outputFile) throws IOException
    {
    
        this.inputFile = inputFile;
        this.outputFile = outputFile;
        
        this.intFile
            = FileHelper.makeOutputFile(getCSVIntermediateFilename
                                            (inputFile.getName()), true);
        
    }

    /** Separator of the fields of the CSV file. */
    public final static String CSV_FIELD_SEPARATOR = ",";
    /** String for filtering each line of the CSV input file. */
    public final static String LINE_FILTER = "Event-A";
    /** Fields to be selected. */
    public int[] FIELDS = {38, 43, 44, 45};
    /** Number of fields required for each line of the CSV input file. */
    public final static int FIELDS_REQUIRED = 46;
    
    /**
     * Method that returns the name of the intermediate CSV file generated from
     * the original name of the input file.
     * 
     * @param csvFilename The name of the original CSV input file.
     * @return  The just-generated name.
     */
    protected static String getCSVIntermediateFilename(String csvFilename)
        { return(csvFilename + ".int"); }
    
    /**
     * This method filters the input file as configured for this object and
     * writes the results in the given file as CSV text.
     * 
     * @return List with the SensorData objects that contain the information
     *          related with the sensors.
     * @throws IOException In case any problem occurs while reading, filtering
     *                      or writing.
     */
    @Deprecated
    protected List<SensorData> filterSensorData()
        throws IOException
    {
        
        List<SensorData> s_data = this.readSensorData();
        Logger.getLogger(SensorLocator.class.getName())
                .log(Level.INFO, "Step 1/3: filterCSVLines() = {0}", s_data);
        
        List<String> lines = getLines(s_data);
        Logger.getLogger(SensorLocator.class.getName())
                .log(Level.INFO, "Step 2/3: getLines() = {0}", lines);
        
        SensorLocator.writeTextFile(this.intFile, lines);
        Logger.getLogger(SensorLocator.class.getName())
                .log(Level.INFO, "Step 3/3: intermediate file written, f = {0}"
                                    , this.intFile.getAbsolutePath());
     
        return(s_data);
        
    }
    
    /**
     * Transforms a list of SensorData objects into a list of lines, each line
     * containing the data of the SensorData objects.
     * 
     * @param list Input list to be transformed.
     * @return List with the text lines.
     */
    protected static List<String> getLines(final List<SensorData> list)
    {
    
        List<String> lines = new ArrayList<String>();
        for ( SensorData sd_i : list )
            { lines.add(sd_i.toString()); }
        return(lines);
        
    }
    
    /**
     * Method that applies a series of filters to the input CSV file in order
     * to get a more simple CSV input file. It erases all columns that are not
     * to be used through subsequent processing stages.
     * 
     * @return List of the lines selected without the columns.
     */
    protected List<SensorData> readSensorData()
        throws FileNotFoundException, IOException
    {
    
        BufferedReader in = new BufferedReader(new FileReader(this.inputFile));
        String line;
        List<SensorData> lines = new ArrayList<SensorData>();
        
        while ( ( line = in.readLine() ) != null )
        {
            
            if ( line.contains(LINE_FILTER) == false )
            {
                Logger.getLogger(SensorLocator.class.getName())
                        .log(   Level.FINE,
                                "No {0} data, skipping line = {1}"
                                    , new Object[]{LINE_FILTER, line}   );
                continue;
            }
            
            String[] fields = line.split(CSV_FIELD_SEPARATOR);
            
            if ( ( fields == null ) || ( fields.length == 0 ) )
            {
                Logger.getLogger(SensorLocator.class.getName())
                        .log(Level.FINE, "Empty line! Skipping...");
                continue;
            }
            
            if ( fields.length < FIELDS_REQUIRED )
            {
                Logger.getLogger(SensorLocator.class.getName())
                        .log(   Level.FINE,
                                "Wrong line, fields = {0} < required = {1}. "
                                    + "Skipping..."
                                    , new Object[]
                                        {fields.length, FIELDS_REQUIRED}    );
                continue;
            }
            
            Logger.getLogger(SensorLocator.class.getName())
                    .log(   Level.FINE,
                            "fields#{0} >>> (selected) :: {1}/{2}/{3}/{4}\n"
                                ,  new Object[]
                                    {  
                                        fields.length, fields[38], fields[43],
                                        fields[44], fields[45]
                                    }) ;
            
            List<String> line_i = selectFields(fields, FIELDS);
            
            try
            {
                Logger.getLogger(SensorLocator.class.getName())
                        .log(Level.INFO, "Parsing line = {0}", line_i);
                SensorData sdi = new SensorData
                                        (   line_i.get(0), line_i.get(1), 
                                            line_i.get(2), line_i.get(3)    );
                lines.add(sdi);   
            }
            catch(IllegalArgumentException ex)
            {
                Logger.getLogger(SensorLocator.class.getName())
                        .log(Level.WARNING, "Wrong format, "
                            + "skipping line = {0}", line_i);
                Logger.getLogger(SensorLocator.class.getName())
                        .log(Level.WARNING, ex.getMessage(), ex);
                continue;
            }
            
        }
        
        in.close();
        return(lines);
        
    }
    
    /**
     * Gets the line formatted for a CSV file using the set of elements from 
     * among the "fields" array indicated by the elements of the selection
     * array.
     * 
     * @param fields All the fields from which to make the selection.
     * @param separator CSV fields separator.
     * @param selection List of fields to be selected.
     * @return Line as per a CSV file.
     */
    protected static String getLine
        (String[] fields, String separator, int[] selection)
    {
    
        String buffer = "";
        int s = selection.length;
        
        for ( int i = 0; i < s; i++ )
        {
            buffer += fields[selection[i]];
            if ( i < ( s - 1 ) ) { buffer += separator; }
        }
        
        return(buffer);
        
    }
    
    /**
     * Static method that returns a set of elements of the input list, using 
     * the indexes contained in the given selection array.
     * 
     * @param fields Input list with all available fields.
     * @param selection Set of fields to be selected.
     * @return List containing the selected fields.
     */
    protected static List<String> selectFields
        (final String[] fields, final int[] selection)
    {
    
        List<String> buffer = new ArrayList<String>();
        
        for ( int i = 0; i < selection.length; i++ )
            { buffer.add(fields[selection[i]]); }
        
        return(buffer);
        
    }
    
    /**
     * Static method that transforms a list of SensorData objects into a list
     * of KML nodes.
     * 
     * @param sensors List with the SensorData objects to be transformed.
     * @return List with the created KML nodes, each of them associated with 
     *          one of the given SensorData objects.
     */
    protected static List<KMLNode> createKMLNodes
            (final List<SensorData> sensors)
    {
        
        List<KMLNode> l = new ArrayList<KMLNode>(sensors.size());
        
        for ( SensorData sd_i : sensors )
        {
            try
                { l.add(KMLNode.createKMLNode(sd_i)); }
            catch(Exception ex)
            {
                Logger.getLogger(SensorLocator.class.getName())
                        .log(Level.WARNING, "Could not create KML node for, " 
                                            + "sensor data object = {0}"
                                                , sd_i);
                Logger.getLogger(SensorLocator.class.getName())
                        .log(Level.WARNING, ex.getMessage(), ex);
                continue;
            }
        }
        
        return(l);
        
    }
    
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> factory
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    /** 2 arguments are required for this application. */
    public final static int __ARGS_LEN = 2;
    
    /** Wrong arguments exception message. */
    public final static String __WRONG_ARGS_EX
            = "Wrong arguments, usage: SensorLocator.jar input.csv output.kml";
    
    /**
     * Static method that creates a SensorLocator object with the data provided
     * in the CLI arguments.
     * 
     * @param args CLI arguments for the configuration of the object.
     * @return The created SensorLocator object.
     */
    public static SensorLocator createSensorLocator(String[] args)
            throws Exception
    {
        
        if ( ( args == null ) || ( args.length != __ARGS_LEN ) )
            { throw(new Exception(__WRONG_ARGS_EX)); }
        
        File in_f = cx.ath.rtubio.javalib.pojos.FileHelper
                        .checkInputFile(args[0]);
        File out_f = cx.ath.rtubio.javalib.pojos.FileHelper
                        .makeOutputFile(args[1], true);
        
        return(new SensorLocator(in_f, out_f));
    
    }
    
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> main ()
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    
    /**
     * Method for running the application from the command line.
     * 
     * @param args Set of args given from the command line.
     */
    public static void main(String[] args)
    {
        
        try
        {
            
            Logger.getLogger(SensorLocator.class.getName())
                                .log(Level.INFO, "Checking arguments...");
            SensorLocator sl = SensorLocator.createSensorLocator(args);
            
            Logger.getLogger(SensorLocator.class.getName())
                                .log(Level.INFO, "Reading sensor data...");
            List<SensorData> sensors = sl.readSensorData();
            List<KMLNode> k_nodes = SensorLocator.createKMLNodes(sensors);
            
            Logger.getLogger(SensorLocator.class.getName())
                                .log(Level.INFO, ">>>>> KML nodes\n{0}\n<<<<<"
                                                    , k_nodes);
            
            SimpleKMLWriter skw = new SimpleKMLWriter();
            skw.addKMLNodes(k_nodes);
            
            Logger.getLogger(SensorLocator.class.getName())
                                .log(Level.INFO, ">>>>> SKWriter \n{0}\n<<<<<"
                                                    , skw);            
            
            skw.writeXML(sl.outputFile);
            
            Logger.getLogger(SensorLocator.class.getName())
                                .log(Level.INFO, "Output written to {0}"
                                                    , sl.outputFile);
            
        }
        catch (Exception ex)
        {
            Logger.getLogger(SensorLocator.class.getName())
                                .log(Level.SEVERE, ex.getMessage(), ex);
            System.exit(-1);
        }
        
        System.exit(0);
        
    }
    
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> move
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    
    /**
     * Static method that writes the given list of text lines in a text file.
     * TODO Move this method to the "common-tools-library".
     * 
     * @param file The file where the lines will be written.
     * @param lines The lines to write.
     * @throws IOException In case any problem occurs while writing.
     */
    public static void writeTextFile(File file, final List<String> lines)
        throws IOException
    {
    
        if ( file == null )
            { throw(new NullPointerException("<file> is null.")); }
        
        if ( lines == null )
            { throw(new NullPointerException("<lines> is null.")); }

        BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
       
        for ( String l_i : lines )
            { bw.write(l_i); bw.newLine(); }
        
        bw.close();
        
    }
    
}
