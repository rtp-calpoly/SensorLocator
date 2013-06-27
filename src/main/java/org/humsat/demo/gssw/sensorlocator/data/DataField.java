/**
 * @file DataField.java
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
package org.humsat.demo.gssw.sensorlocator.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Class for storing data obtained from sensors.
 *
 * @author Ricardo Tubío (rtpardavila[at]gmail.com)
 */
public class DataField
    implements DataFieldConstants
{
    
    /** Type of the data field. */
    public String type = "";
    /** Raw value as read from the data input file. */
    public String value = "";
    
    /** Map with all the values. */
    public List<Map<String, Float>> values = null;
    
    /** Minimum number of characters for the full value string. */
    public final static int MIN_NO_CHARS = 2;
    
    /** Default constructor is hidden. */
    protected DataField() {}
    
    @Override
    public String toString()
    {
        
        String buffer = NAMES_PER_CODE.get(this.type);
        buffer += " = ";
        
        for ( int i = 0; i < this.values.size(); i++ )
        {
            
            Map<String, Float> m_i = this.values.get(i);
            
            Set<Entry<String, Float>> s_i = m_i.entrySet();
            Iterator<Entry<String, Float>> it_s_i = s_i.iterator();
            while ( it_s_i.hasNext() == true )
            {
                Entry<String, Float> e_i = it_s_i.next();
                String k_i = e_i.getKey();
                Float v_i = e_i.getValue();
                buffer += "" + v_i + " (" + k_i + ")";
            }
            
            if ( i != ( this.values.size() - 1 ) )
                { buffer += ", "; }
            
        }
        
        return(buffer);
        
    }
    
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> factory
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        
    /**
     * Static factory method that creates a data field object from the data 
     * contained in the given data string.
     * 
     * @param data The string that contains the information for the data field.
     * @return The data field object.
     */
    public static DataField readDataField(String data)
    {

        if ( data.length() < MIN_NO_CHARS )
            { throw(new IllegalArgumentException("<data>.length must be " 
                                                    + MIN_NO_CHARS 
                                                    + " at least.")); }
        
        String type = data.substring(0, 1);
        String value = data.substring(1);
        
        return(decodeDataField(type, value));
        
    }
    
    /** Separator of the data fields to be found. */
    public final static String DATA_FIELD_SEPARATOR = ",";
    
    /**
     * Static method that decodes a data field in function of its type and 
     * associated data.
     * 
     * @param type The type of data field detected.
     * @param value The value containing the information for this data field.
     * @return An initialized data field object.
     */
    protected static DataField decodeDataField
                                (final String type, final String value)
    {
    
        DataField df = new DataField();
        
        String[] fields = value.split(DATA_FIELD_SEPARATOR);
        
        if ( ( fields == null ) || ( fields.length == 0 ) )
            { throw(new IllegalArgumentException("<data>.length must be " 
                                                    + MIN_NO_CHARS 
                                                    + " at least.")); }
        
        List<Float> f_list = new ArrayList<Float>();
        
        for ( int i = 0; i < fields.length; i++ )
        {
            Float v_i = Float.parseFloat(fields[i]);
            f_list.add(v_i);
        }
        
        df.type = type;
        df.value = value;
        df.values = decodeFieldsList(type, f_list);
        
        return(df);
        
    }
    
    /**
     * This method decodes the read data values and associates them with its
     * corresponding units.
     * 
     * @param type The type of data that is currently being decoded.
     * @param list The list of the values associated with the data.
     * @return The list of values associated with their units.
     */
    protected static List<Map<String, Float>> decodeFieldsList
            (final String type, final List<Float> list)
    {
    
        List<Map<String, Float>> result = new ArrayList<Map<String, Float>>();
        List<String> values = DataField.VALUES_PER_CODE.get(type);
    
        if ( ( values == null ) || ( values.isEmpty() == true ) )
            { throw(new IllegalArgumentException
                            ("Unsupported type = " + type)); }
        
        if ( values.size() != list.size() )
            { throw(new IllegalArgumentException
                            ("Read type = " + type 
                                + " list = " + list + ", does not match "
                                + "expected values = " + values)); }
        
        for ( int i = 0; i < values.size(); i++ )
        {
            Map<String, Float> m_i = new HashMap<String, Float>();
            m_i.put(values.get(i), list.get(i));
            result.add(m_i);
        }
        
        return(result);
        
    }
    
}
