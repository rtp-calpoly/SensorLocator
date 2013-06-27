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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
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
    
    /** HUMPL Time column name. */
    public final static String HUMPL_TIME_CN = "HUMPL Time";
    /** Sensor ID column name. */
    public final static String SENSOR_ID_CN = "Sensor ID";
    /** Sensor Length column name. */
    public final static String SENSOR_LENGTH_CN = "Length";
    /** Sensor Data column name. */
    public final static String SENSOR_DATA_CN = "Data";

    /** All fields containing sensor information. */
    public final static List<String> SENSOR_CN = new ArrayList<String>()
    {
        {
            this.add(HUMPL_TIME_CN);
            this.add(SENSOR_ID_CN);
            this.add(SENSOR_LENGTH_CN);
            this.add(SENSOR_DATA_CN);  
        }
    };
    
    /** Separator of the fields of the CSV file. */
    public final static String CSV_FIELD_SEPARATOR = ",";
    /** String for filtering each line of the CSV input file. */
    public final static String LINE_FILTER = "Event-A";
    
    /**
     * Method that applies a series of filters to the input CSV file in order
     * to get a more simple CSV input file. It erases all columns that are not
     * to be used through subsequent processing stages.
     * 
     * @return List of the lines selected without the columns.
     */
    public static List<SensorData> readSensorData(final File inputFile)
        throws FileNotFoundException, IOException
    {
        
        BufferedReader in = new BufferedReader(new FileReader(inputFile));
        String line = "";
        List<SensorData> lines = new ArrayList<SensorData>();
        Map<String, Integer> indexes = readSensorDataIndexes(inputFile);
        int fields_required = getFieldsRequired(indexes);
        
        System.out.println("req = " + fields_required 
                            + ", indexes = " + indexes);
        
        while ( ( line = in.readLine() ) != null )
        {
            
            line = line.replaceAll("[0-9]{2}:[0-9]{2}:[0-9]{2},[0-9]{2}", "");
            //line = line.replaceAll("\"[0-9]{2}:[0-9]{2}:[0-9]{2},[0-9]{2}\"", "");
            
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
            
            if ( fields.length < fields_required )
            {
                Logger.getLogger(SensorLocator.class.getName())
                        .log(   Level.FINE,
                                "Wrong line, fields = {0} < required = {1}. "
                                    + "Skipping, line = {2}"
                                    , new Object[]
                                        {fields.length, fields_required, line});
                continue;
            }

            List<String> line_i = selectFields(fields, indexes);

            Logger.getLogger(SensorLocator.class.getName())
                    .log(   Level.INFO,
                            "fields#{0} >>> (selected) = {1}\n"
                                , new Object[]{fields.length, line_i}   );
            
            try
            {
                Logger.getLogger(SensorLocator.class.getName())
                        .log(Level.FINE, "Parsing line = {0}", line_i);
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
        (final String[] fields, final String separator, final int[] selection)
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
     * Gets the total ammount of fields required.
     * 
     * @param indexes Set of indexes to be read.
     * @return Total ammount of fields that must be available.
     */
    public static int getFieldsRequired(final Map<String, Integer> indexes)
    {
    
        int compare = 0;
        Collection<Integer> vs = indexes.values();
        Iterator<Integer> it = vs.iterator();
        
        while ( it.hasNext() == true )
        {
            Integer i = it.next();
            if ( compare < i.intValue() )
                { compare = i.intValue(); }
        }
        
        return(compare + 1);
        
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
        (final String[] fields, final Map<String, Integer> selection)
    {
    
        System.out.println("selectFields, m =\n" + selection);
        
        int[] s_a = new int[selection.size()];
            
        s_a[0] = selection.get(HUMPL_TIME_CN);
        s_a[1] = selection.get(SENSOR_ID_CN);
        s_a[2] = selection.get(SENSOR_LENGTH_CN);
        s_a[3] = selection.get(SENSOR_DATA_CN);
        
        return(selectFields(fields, s_a));
        
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
    
        for ( int i = 0; i < selection.length; i++ )
        {
            System.out.println("selection[" + i + "] = " + selection[i]);
        }
        
        System.out.println("### fields = " + Arrays.asList(fields));
        
        List<String> buffer = new ArrayList<String>();
        
        for ( int i = 0; i < selection.length; i++ )
        {
            int s_i = selection[i];
            System.out.println("s[" + i + "] = " + s_i);
            String f_i = fields[s_i];
            System.out.println("f[" + i + "] = " + f_i);
            buffer.add(f_i);
        }
        
        return(buffer);
        
    }
    
    /**
     * Gets the headers of the first line of a CSV file.
     * 
     * @param line First line of the given CSV file.
     * @return List with the headers found.
     * @throws FileNotFoundException In case the given file does not exist.
     * @throws IOException In case any IO problem occurs.
     */
    public static List<String> readHeaders(final File input)
            throws FileNotFoundException, IOException
    {
            
        BufferedReader in = new BufferedReader(new FileReader(input));
        String line = in.readLine();
        in.close();
        
        if ( ( line == null ) || ( line.length() == 0 ) )
            { throw(new IllegalArgumentException("<input> FILE is empty.")); }
    
        String[] splits = line.split(CSV_FIELD_SEPARATOR);
        if ( ( splits == null ) || ( splits.length == 0 ) )
        {
            throw(new IllegalArgumentException("No headers found, line = " 
                                                + line));
        }
        
        return(Arrays.asList(splits));
        
    }
    
    /**
     * Static method that gets the indexes of the required columns.
     * 
     * @param input The input file from where to read the data.
     * @return A map with each column name linked to its index.
     * @throws FileNotFoundException In case the given file does not exist.
     * @throws IOException In case any IO problem occurs.
     */
    public static Map<String, Integer> readSensorDataIndexes(final File input)
        throws FileNotFoundException, IOException
    {
    
        List<String> headers = readHeaders(input);
        Map<String, Integer> indexes = getSensorDataIndexes(headers);
        return(indexes);
        
    }
    
    /**
     * Static method that extracts the indexes that contain the information of
     * the sensors.
     * 
     * @param headers All CSV file headers.
     * @return Indexes of all data required.
     */
    public static Map<String, Integer> getSensorDataIndexes
            (final List<String> headers)
    {
        
        if ( ( headers == null ) || ( headers.isEmpty() == true ) )
            { throw(new IllegalArgumentException("<indexes> is empty.")); }
        
        Integer h_t_index = - 1;
        Integer s_id_index = -1;
        
        h_t_index = headers.indexOf(HUMPL_TIME_CN);
        s_id_index = headers.indexOf(SENSOR_ID_CN);
        
        if ( h_t_index == - 1)
            { h_t_index = headers.indexOf("\"" + HUMPL_TIME_CN + "\""); }
        if ( s_id_index == - 1)
            { s_id_index = headers.indexOf("\"" + SENSOR_ID_CN + "\""); }
        
        Integer s_l_index = s_id_index + 1;
        Integer s_d_index = s_id_index + 2;
        
        Map<String, Integer> indexes = new HashMap<String, Integer>();
        
        indexes.put(HUMPL_TIME_CN, h_t_index);
        indexes.put(SENSOR_ID_CN, s_id_index);
        indexes.put(SENSOR_LENGTH_CN, s_l_index);
        indexes.put(SENSOR_DATA_CN, s_d_index);
        
        return(indexes);
        
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
