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
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.humsat.demo.gssw.sensorlocator.csv.CSVHelper;
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
            = FileHelper.makeOutputFile(CSVHelper.getCSVIntermediateFilename
                                            (inputFile.getName()), true);
        
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
        
        List<SensorData> s_data = CSVHelper.readSensorData(this.inputFile);
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
            List<SensorData> sensors = CSVHelper.readSensorData(sl.inputFile);
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
