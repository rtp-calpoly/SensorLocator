/**
 * @file CSVHelper.java
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
package org.humsat.demo.gssw.sensorlocator.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.humsat.demo.gssw.sensorlocator.SensorLocator;
import org.humsat.demo.gssw.sensorlocator.data.SensorData;

/**
 * Class with static methods for helping in processing CSV files.
 *
 * @author Ricardo Tubío (rtpardavila[at]gmail.com)
 */
public class CSVHelper
{
    

    /** Separator of the fields of the CSV file. */
    public final static String CSV_FIELD_SEPARATOR = ",";
    /** String for filtering each line of the CSV input file. */
    public final static String LINE_FILTER = "Event-A";
    /** Fields to be selected. */
    public final static int[] FIELDS = {38, 43, 44, 45};
    /** Number of fields required for each line of the CSV input file. */
    public final static int FIELDS_REQUIRED = 46;
    
    /**
     * Method that applies a series of filters to the input CSV file in order
     * to get a more simple CSV input file. It erases all columns that are not
     * to be used through subsequent processing stages.
     * 
     * @return List of the lines selected without the columns.
     */
    public static List<SensorData> readSensorData(File inputFile)
        throws FileNotFoundException, IOException
    {
        
        BufferedReader in = new BufferedReader(new FileReader(inputFile));
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
     * Gets the headers of the first line of a CSV file.
     * 
     * @param line First line of the given CSV file.
     * @return List with the headers found.
     */
    public static List<String> getHeaders(final String line)
    {
            
        if ( ( line == null ) || ( line.length() == 0 ) )
            { throw(new IllegalArgumentException("<line> is empty.")); }
    
        String[] splits = line.split(CSV_FIELD_SEPARATOR);
        if ( ( splits == null ) || ( splits.length == 0 ) )
        {
            throw(new IllegalArgumentException("No headers found, line = " 
                                                + line));
        }
            
        return(Arrays.asList(splits));
        
    }
    
    /**
     * Static method that gets the indexes of the columns that match the given
     * names.
     * 
     * @param headers The list of the headers of the CSV file.
     * @param names Names of the columns whose index is requested.
     * @param all Flag that indicates whether an exception must be thrown in
     *              case any of the requested names is not found.
     * @return A map containing for each column name, its index.
     */
    public static Map<String, Integer> getColumnIndexes
            (   final List<String> headers, final List<String> names,
                final boolean all   )
    {
        
        Map<String, Integer> indexes = new HashMap<String, Integer>();
        
        for ( String n_i : names )
        {
            
            Integer index = headers.indexOf(n_i);
            if ( index < 0 )
            {
                if ( all == true )
                {
                    throw(new IllegalArgumentException("<headers> does not "
                                            + "contain the column = " + n_i));
                }
                continue;
            }
            
            indexes.put(n_i, index);
        }
        
        return(indexes);
    
    }

    /** File extension utilized for generating the intermediate file. */
    public final static String INTERMEDIATE_FILE_EXTENSION = ".int";
    
    /**
     * Method that returns the name of the intermediate CSV file generated from
     * the original name of the input file.
     *
     * @param csvFilename The name of the original CSV input file.
     * @return  The just-generated name.
     */
    public static String getCSVIntermediateFilename(String csvFilename)
        { return csvFilename + INTERMEDIATE_FILE_EXTENSION; }
    
}
