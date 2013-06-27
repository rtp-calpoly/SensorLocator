/**
 * @file SensorLocatorTest.java
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

import org.junit.*;
import java.util.logging.Logger;
import java.io.File;
import java.util.logging.Level;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.humsat.demo.gssw.sensorlocator.data.DataField;
import org.humsat.demo.gssw.sensorlocator.kml.KMLNode;
import org.humsat.demo.gssw.sensorlocator.data.SensorData;

/**
 * JUNIT test class for the SensorLocator class.
 *
 * @author Ricardo Tubío (rtpardavila[at]gmail.com)
 */
public class SensorLocatorTest
    extends TestCase
{
    
    /**
     * Static method that activates all logging levels for all classes.
     */
    @BeforeClass
    public static void logAllLevels()
    {
        System.out.println("**************** logAllLevels");
        Logger.getLogger(SensorLocator.class.getName()).setLevel(Level.ALL);
        Logger.getLogger(SensorData.class.getName()).setLevel(Level.ALL);
        Logger.getLogger(DataField.class.getName()).setLevel(Level.ALL);
        Logger.getLogger(KMLNode.class.getName()).setLevel(Level.ALL);
    }
    
    /** Path to test files. */
    public final static String TEST_FILES_PATH =    "src"   + File.separator +
                                                    "test"  + File.separator +
                                                    "java"  + File.separator;
    
    /** Test file. */
    public final static String CSV_TEST_FILE_1 = TEST_FILES_PATH 
                                                    + "input-1-example.csv";
    /** Test file. */
    public final static String CSV_TEST_FILE_2 = TEST_FILES_PATH
                                                    + "input-2-example.csv";
    /** Test file. */
    public final static String CSV_TEST_FILE_3 = TEST_FILES_PATH
                                                    + "input-3-real.csv";
    
    /** CLI arguments for test 1. */
    public final static String[] ARGS_TEST_1 =
    {
        CSV_TEST_FILE_3, TEST_FILES_PATH + "output-1.kml"
    };
    
    /**
     * Test of main method, of class SensorLocator.
     */
    @Test
    public void test__realCSV()
    {
        
        System.out.println(">>>>>>>>>> test__realCSV <<<<<<<<<");
        SensorLocator.main(ARGS_TEST_1);
        
    }

    /**
     * Test for verifying that an integer can be read with the 
     * "Float.parseFloat()" method.
     */
    @Test
    public void test__floatInteger()
    {
    
        System.out.println(">>>>>>>>>> test__floatInteger <<<<<<<<<");
        
        String float_test = "5";
        Float result = new Float(5.0);
        Float current = Float.parseFloat(float_test);        
        Assert.assertEquals(current, result, 0);
        
    }
    
}
