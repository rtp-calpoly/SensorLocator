/**
 * @file KMLNode.java
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
package org.humsat.demo.gssw.sensorlocator.kml;

import java.util.ArrayList;
import java.util.List;
import org.humsat.demo.gssw.sensorlocator.data.DataField;
import org.humsat.demo.gssw.sensorlocator.data.DataFieldConstants;
import static org.humsat.demo.gssw.sensorlocator.data.DataFieldConstants.POSITION_F_CODE;
import org.humsat.demo.gssw.sensorlocator.data.SensorData;

/**
 * Data field object that contains the coordinates (latitude and longitude) of
 * a sensor.
 *
 * @author Ricardo Tubío (rtpardavila[at]gmail.com)
 */
public class KMLNode
    extends SensorData
    implements DataFieldConstants
{
    
    /**
     * Class for handling a node's position.
     *
     * @author Ricardo Tubío (rtpardavila[at]gmail.com)
     */
    public class Position
    {
        
        /** Latitude in degrees. */
        public float latitude   = (float) 0.0;
        /** Longitude in degrees. */
        public float longitude  = (float) 0.0;
        
        /** Default constructor is hidden. */
        protected Position() {};
        
        /**
         * Main constructor, it initializes the object fields from the given
         * DataField object information.
         * 
         * @param position DataField object from where to take the required
         *                  position information.
         */
        public Position(DataField position)
        {
            this.latitude   = position.values .get(LATITUDE_POSITION_INDEX)
                                                .get(POSITION_VALUE_UNITS);
            this.longitude  = position.values .get(LONGITUDE_POSITION_INDEX)
                                                .get(POSITION_VALUE_UNITS);
        }
        
        /** Units considered for the pair (lat,long). */
        public final static String POSITION_UNITS =
                "(" + POSITION_VALUE_UNITS + ","+ POSITION_VALUE_UNITS +")";
        
        /**
         * Creates an string representing this position compatible with the
         * KML 2.2 specification by Google.
         * 
         * @return The KML position in String format.
         */
        public String getKMLPosition()
            { return("" + this.latitude + "," + this.longitude); }
        
        @Override
        public String toString()
        {
            String buffer = "";
            buffer  += "(latitude,longitude) = "
                    + "(" + this.latitude + "," + this.longitude + ")"
                    + POSITION_UNITS;
            return(buffer);
        }
        
    }
    
    /** Name of this KML node. */
    protected String name = "";
    /** DataField with the position of the sensor. */
    protected Position position = null;
    /** List of DataField objects with information non-relative to position. */
    protected List<DataField> information = null;

    /** Parent sensor data from where a KML node gets its information. */
    protected SensorData parent = null;
        
    /** Default constructor is hidden. */
    protected KMLNode() {}
    
    /**
     * Main KMLNode constructor to be used by the factory method.
     * 
     * @param parent Parent sensor data object.
     * @param position DataField with the position of the sensor.
     */
    protected KMLNode(final SensorData parent, final DataField position)
    {
        this.parent = parent;
        
        this.name = getName(parent);
        this.position = new Position(position);
        this.information = getInformation(parent);
    }
    
    @Override
    public String toString()
    {
        String buffer = "";
        
        buffer += ">>>>>> KMLNode ";
        buffer += "@" + this.position + "\n";
        
        for ( DataField df_i : this.information )
            { buffer += "* " + df_i + "\n"; }
        
        return(buffer);
    }
    
    /**
     * Getter for the name property.
     * 
     * @return The name of the KML node.
     */
    public String getName()
        { return(this.name); }
    
    /**
     * Generates a String containing the information of the DataFields that are
     * in the information list.
     * 
     * @return String with the description of this KML node.
     */
    public String getDescription()
    {
        String buffer = "";
        for ( DataField df_i : this.information )
            { buffer += "<li>" + df_i + "</li>\n"; }
        return(buffer);
    }
    
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> factory
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    
    /**
     * Factory method that creates a KML node with the information that is
     * contained in the given parent SensorData object.
     * 
     * @param parent SensorData object.
     * @return KML generated node.
     * @throws IllegalArgumentException In case parent node does not contain the
     *                                  mandatory position data field.
     */
    public static KMLNode createKMLNode(final SensorData parent)
            throws IllegalArgumentException
        { return(new KMLNode(parent, getPosition(parent))); }
    
    /**
     * Finds the position field of the given SensorData object. In case it
     * does not find it, an exception is thrown.
     * 
     * @param sensorData The SensorData object whose position DataField is
     *                          requested.
     * @return The Datafield object found.
     * @throws IllegalArgumentException In case parent node does not contain the
     *                                  mandatory position data field.
     */
    public static DataField getPosition(final SensorData sensorData)
    {
        
        DataField position = null;
        
        for ( DataField df_i : sensorData.getDataFields() )
        {
            if ( df_i.type.equalsIgnoreCase(POSITION_F_CODE) == true )
                { position = df_i; break; }
        }
        
        if ( position == null )
            { throw(new IllegalArgumentException
                    ("<sensorData> has no position field.")); }  
        
        return(position);
        
    }
    
    /**
     * Gets the information nodes of this sensor data; i.e., all data fields
     * but the one relative to the position of the sensor.
     * 
     * @param sensorData Object containing all sensor's data.
     * @return List with the information data fields.
     */
    public static List<DataField> getInformation(final SensorData sensorData)
    {
    
        List<DataField> info = new ArrayList<DataField>();
        
        for ( DataField df_i : sensorData.getDataFields() )
        {
            if ( df_i.type.equalsIgnoreCase(POSITION_F_CODE) == true )
                { continue; }
            info.add(df_i);
        }
        
        return(info);
        
    }
    
    /**
     * Static method that generates a KML node name from the information of the
     * given sensor.
     * 
     * @param sensor The sensor whose information is to be used.
     * @return The name for the associated KML node.
     */
    public static String getName(final SensorData sensor)
        { return("SensorID = " + sensor.getSensorId() 
                    + ", timestamp = " + sensor.getTimestamp()); }
    
}
