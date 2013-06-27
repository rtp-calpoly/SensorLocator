/**
 * @file DataFieldConstants.java
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
import java.util.List;
import java.util.Map;

/**
 * Constants that define the data types for the different data fields.
 *
 * @author Ricardo Tubío (rtpardavila[at]gmail.com)
 */
public interface DataFieldConstants
{
    
    public final static String UNKNOWN_F_CODE           = "__unknown";
    public final static String POSITION_F_CODE          = "P";
    public final static String RIVER_VOLUME_F_CODE      = "L";
    public final static String RIVER_LEVEL_F_CODE       = "F";
    public final static String RIVER_PH_F_CODE          = "H";
    public final static String RIVER_O2_F_CODE          = "O";
    public final static String STREAM_F_CODE            = "C";
    public final static String SEA_SALINITY_F_CODE      = "S";
    public final static String SWELL_F_CODE             = "V";
    public final static String TEMPERATURE_F_CODE       = "T";
    public final static String HUMIDITY_F_CODE          = "U";
    public final static String WIND_F_CODE              = "W";
    public final static String RAIN_F_CODE              = "R";
    
    public final static int LATITUDE_POSITION_INDEX = 0;
    public final static int LONGITUDE_POSITION_INDEX = 1;
    
    public final static String POSITION_VALUE_UNITS = "degrees";
    
    public final static List<String> POSITION_VALUES 
            = new ArrayList<String>()
        { { this.add("degrees"); this.add("degrees"); } };
    public final static List<String> RIVER_LEVEL_VALUES 
            = new ArrayList<String>()
        { { this.add("meters"); } };
    public final static List<String> RIVER_VOLUME_VALUES 
            = new ArrayList<String>()
        { { this.add("m^3/s"); } };
    public final static List<String> RIVER_PH_VALUES 
            = new ArrayList<String>()
        { { this.add("u.pH"); } };
    public final static List<String> RIVER_O2_VALUES 
            = new ArrayList<String>()
        { { this.add("mg/L"); } };
    public final static List<String> STREAM_VALUES 
            = new ArrayList<String>()
        { { this.add("cm/s"); this.add("degrees"); } };
    public final static List<String> SEA_SALINITY_VALUES 
            = new ArrayList<String>()
        { { this.add("psu"); } };
    public final static List<String> SWELL_VALUES 
            = new ArrayList<String>()
        { { this.add("m"); this.add("degrees"); } };
    public final static List<String> TEMPERATURE_VALUES 
            = new ArrayList<String>()
        { { this.add("centigrades"); } };
    public final static List<String> HUMIDITY_VALUES 
            = new ArrayList<String>()
        { { this.add("%"); } };
    public final static List<String> WIND_VALUES 
            = new ArrayList<String>()
        { { this.add("m/s"); this.add("degrees"); } };
    public final static List<String> RAIN_VALUES 
            = new ArrayList<String>()
        { { this.add("l/m^2"); } };
    
    /** Map with the units that each data field contains. */
    public final static Map<String, List<String>> VALUES_PER_CODE 
            = new HashMap<String, List<String>>()
    {
        {
            this.put(UNKNOWN_F_CODE,        new ArrayList<String>());
            this.put(POSITION_F_CODE,       POSITION_VALUES);
            this.put(RIVER_VOLUME_F_CODE,   RIVER_VOLUME_VALUES);
            this.put(RIVER_LEVEL_F_CODE,    RIVER_LEVEL_VALUES);
            this.put(RIVER_PH_F_CODE,       RIVER_PH_VALUES);
            this.put(RIVER_O2_F_CODE,       RIVER_O2_VALUES);
            this.put(STREAM_F_CODE,         STREAM_VALUES);
            this.put(SEA_SALINITY_F_CODE,   SEA_SALINITY_VALUES);
            this.put(SWELL_F_CODE,          SWELL_VALUES);
            this.put(TEMPERATURE_F_CODE,    TEMPERATURE_VALUES);
            this.put(HUMIDITY_F_CODE,       HUMIDITY_VALUES);
            this.put(WIND_F_CODE,           WIND_VALUES);
            this.put(RAIN_F_CODE,           RAIN_VALUES);
        }
    };
    
    /** Map with the names for each data field. */
    public final static Map<String, String> NAMES_PER_CODE 
            = new HashMap<String, String>()
    {
        {
            this.put(UNKNOWN_F_CODE,        "Unknown data");
            this.put(POSITION_F_CODE,       "Position");
            this.put(RIVER_VOLUME_F_CODE,   "River volume");
            this.put(RIVER_LEVEL_F_CODE,    "River level");
            this.put(RIVER_PH_F_CODE,       "River PH");
            this.put(RIVER_O2_F_CODE,       "River Oxigen");
            this.put(STREAM_F_CODE,         "Stream");
            this.put(SEA_SALINITY_F_CODE,   "Sea salinity");
            this.put(SWELL_F_CODE,          "Swell");
            this.put(TEMPERATURE_F_CODE,    "Temperature");
            this.put(HUMIDITY_F_CODE,       "Relative Humidity");
            this.put(WIND_F_CODE,           "Wind");
            this.put(RAIN_F_CODE,           "Rain");
        }
    };
    
}
