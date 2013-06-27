/**
 * @file SensorData.java
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.humsat.demo.gssw.sensorlocator.SensorLocator;
import org.humsat.demo.gssw.sensorlocator.csv.CSVHelper;

/**
 * Class for handing sensor related data.
 *
 * @author Ricardo Tubío (rtpardavila[at]gmail.com)
 */
public class SensorData
{
    /** Separator for the data of the hex string. */
    public static final String HEX_SEPARATOR = ":";
    
    /** Timestamp associated with this sensor. */
    protected int timestamp = -1;
    /** Identifier of the sensor. */
    protected int sensorId = -1;
    /** Length of the raw data obtained from the sensor. */
    protected int dataLen = -1;
    /** Buffer with the data obtained from the sensor. */
    protected List<DataField> data = null;
    
    /** Default constructor is hidden. */
    protected SensorData() {}
    
    /**
     * Main constructor. Initializes an object for handling sensor's data with 
     * the given parameters.
     * 
     * @param timestamp Timestamp for this data.
     * @param sensorId Identifier of the sensor.
     * @param dataLen Length of the raw data obtained.
     * @param data Raw data obtained.
     */
    public SensorData
            (String timestamp, String sensorId, String dataLen, String rawData)
        throws IOException
    {
        
        if ( timestamp == null )
            { throw(new NullPointerException("<timestamp> is null.")); }
        if ( timestamp.isEmpty() == true )
            { throw(new IllegalArgumentException("<timestamp> is empty.")); }
        
        if ( sensorId == null )
            { throw(new NullPointerException("<sensorId> is null.")); }
        if ( sensorId.isEmpty() == true )
            { throw(new IllegalArgumentException("<sensorId> is empty.")); }
        
        if ( dataLen == null )
            { throw(new NullPointerException("<dataLen> is null.")); }
        if ( dataLen.isEmpty() == true )
            { throw(new IllegalArgumentException("<dataLen> is empty.")); }
        
        if ( rawData == null )
            { throw(new NullPointerException("<rawData> is null.")); }
        if ( rawData.isEmpty() == true )
            { throw(new IllegalArgumentException("<rawData> is empty.")); }
        
        int __timestamp = Integer.parseInt(timestamp);
        int __sensorId = Integer.parseInt(sensorId);
        int __dataLen = Integer.parseInt(dataLen);
        
        String filtered_raw_data = rawData
                .replaceAll(HEX_SEPARATOR, "")
                .replaceAll("\"", "");
        
        Logger.getLogger(SensorLocator.class.getName())
                .log(Level.FINE, ">> filtered_rd = {0}", filtered_raw_data);
        
        byte[] byte_data = getByteArray(__dataLen, filtered_raw_data);
        String string_data = toString(byte_data);
        
        Logger.getLogger(SensorLocator.class.getName())
                .log(Level.FINE, ">> string_data = {0}", string_data);
        
        List<DataField> fields = SensorData.readDataFields(string_data);
        
        this.initialize(__timestamp, __sensorId, __dataLen, fields);
        
    }
    
    /**
     * Sets the data for this object.
     * 
     * @param timestamp Timestamp for this data.
     * @param sensorId Identifier of the sensor.
     * @param dataLen Length of the raw data obtained.
     * @param data Raw data obtained.
     */
    public final void initialize
                (int timestamp, int sensorId, int dataLen, List<DataField> data)
    {
        this.timestamp = timestamp;
        this.sensorId = sensorId;
        this.dataLen = dataLen;
        this.data = data;
    }
    
    /**
     * Getter for the list object containing all associated data fields.
     * 
     * @return List with the data fields.
     */
    public List<DataField> getDataFields()
        { return(this.data); }
    
    /**
     * Getter for the sensorID property.
     * 
     * @return The sensor identifier.
     */
    public int getSensorId()
        { return(this.sensorId); }
    
    /**
     * Getter for the timestamp property.
     * 
     * @return The sensor timestamp.
     */
    public int getTimestamp()
        { return(this.timestamp); }
    
    @Override
    public String toString()
    {
        String buffer = "";
        buffer = this.timestamp + CSVHelper.CSV_FIELD_SEPARATOR
                    + this.sensorId + CSVHelper.CSV_FIELD_SEPARATOR
                    + this.dataLen + CSVHelper.CSV_FIELD_SEPARATOR
                    + this.data;
        return(buffer);
    }
    
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> factory
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    
    /** Separator for the data fields. */
    public final static String DATA_FIELDS_SEPARATOR = ";";
    
    /**
     * This static method reads all the data fields contained within the given
     * data string. This data string is expected to be reconstructred from
     * within the hexadecimal string gathered from HUMSAT sensors.
     * 
     * @param data The data string as generated by a HUMSAT sensor.
     * @return A list with all the data fields contained within the given 
     *          string.
     */
    public static List<DataField> readDataFields(final String data)
    {
        
        List<DataField> list = new ArrayList<DataField>();
        
        String[] fields = data.split(DATA_FIELDS_SEPARATOR);
        
        if ( ( fields == null ) || ( fields.length == 0 ) ) 
            { throw(new IllegalArgumentException
                            ("No fields result from spliting <rawData> "
                                + "using as separator = " 
                                + DATA_FIELDS_SEPARATOR)); }
        
        for ( String f_i : fields )
        {
        
            DataField d_i = DataField.readDataField(f_i);
            list.add(d_i);
            
        }
        
        return(list);
        
    }
    
    /**
     * Gets an array of bytes using by transforming characters 2-by-2 into its
     * corresponding short integer value.
     * 
     * @param length The total ammount of bytes to read.
     * @param hexString The input string with hexadecimal characters.
     * @return The read byte array containing "dataLen" bytes.
     * @throws IOException In case any I/O problem occurs.
     */
    public static byte[] getByteArray
            (final int length, final String hexString)
        throws IOException
    {
        
        if ( hexString == null )
            { throw(new NullPointerException("<hexString> is null.")); }    
        if ( hexString.isEmpty() == true )
            { throw(new IllegalArgumentException("<hexString> is empty.")); }
        
        int h_s_len = hexString.length();
                
        if ( ( hexString.length() % 2 ) != 0 )
            { throw(new IllegalArgumentException("<hexString>.length() "
                    + "shall be even, current = " + hexString.length() )); }
                
        if ( length <= 0 )
            { throw(new IllegalArgumentException("<length> = " + length 
                    + ", value not permitted. Must be bigger than 0.")); }

        int bytes_2_read = h_s_len / 2;
        if ( length > bytes_2_read )
            { throw(new IllegalArgumentException("Not enough bytes to read = " 
                    + bytes_2_read + ", required = " + length)); }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        
        int read_bytes = 0;
        
        for (int i = 0; i < (hexString.length() - 2); i = i + 2)
        {
            String ss_i = hexString.substring(i, i + 2);
            int s_ss_i = Integer.parseInt(ss_i, 16);
            dos.writeByte(s_ss_i);
            if ( read_bytes++ == length ) { break; }
        }
        
        return baos.toByteArray();
        
    }
    
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> move
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    
    /**
     * Static method that provides a very useful ASCII utility for reading the
     * ASCII characters included in the given byte array.
     * 
     * TODO Move to CommonTools library.
     * 
     * @param inputBytes The input byte array.
     * @return The read ASCII string.
     */
    public static String toString(final byte[] inputBytes)
    {
        
        ByteArrayInputStream bais = new ByteArrayInputStream(inputBytes);
        
        int size = bais.available();
        char[] theChars = new char[size];
        byte[] bytes = new byte[size];

        bais.read(bytes, 0, size);
        for (int i = 0; i < size;)
            theChars[i] = (char)(bytes[i++]&0xff);
    
        return new String(theChars);
        
    }
    
}
