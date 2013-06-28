/**
 * @file SimpleKMLWriter.java
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 * Simple KML writer whose input are KMLNodes.
 *
 * @author Ricardo Tubío (rtpardavila[at]gmail.com)
 */
public class SimpleKMLWriter
{
    
    /** URL for the icons of the placemarks. */
    public final static String PLACEMARK_ICON_URL =
        "http://www.clker.com/cliparts/O/5/U/b/h/Q/radio-waves-3-hpg-hi.png";
        //"http://www.cs.mun.ca/~hoeber/teaching/cs4767/notes/kml/circle.png";
    
    /** Google Earth namespace for KML 2.2. */
    //public final static String KML_2_2_NS = "http://earth.google.com/kml/2.2";
    public final static String KML_2_2_NS = "http://www.opengis.net/kml/2.2";
    /** KML 2.2 namespace. */
    protected Namespace ns = Namespace.getNamespace("", KML_2_2_NS);
    
    /** Base document to which the writer adds KML nodes. */
    protected Document kmlDocument = null;
    /** Root of the document where nested elements must be added. */
    protected Element root = null;
    
    /** Default constructor. */
    public SimpleKMLWriter()
    {
        this.createKMLStubDocument();
    }
    
    /**
     * Adds a new KML node to the KML base document.
     * 
     * @param node The KML node to be added.
     */
    public void addKMLNode(KMLNode node)
    {
						
	// Placemark
        Element placemark = new Element("Placemark", ns);
        this.root.addContent(placemark);
	
        // name
	Element pmName = new Element("name", ns);
	pmName.setText(node.getName().trim());
	placemark.addContent(pmName);
						
	// description
        Element pmDescription = new Element("description", ns);
	pmDescription.setText(node.getDescription().trim());
	placemark.addContent(pmDescription);

	// styleUrl
	Element pmStyleUrl = new Element("styleUrl", ns);
	pmStyleUrl.setText("#redIcon");
	placemark.addContent(pmStyleUrl);
						
	// Point
	Element pmPoint = new Element("Point", ns);
	placemark.addContent(pmPoint);

	// coordinates
	Element pmCoordinates = new Element("coordinates", ns);
	pmCoordinates.setText(node.position.getKMLPosition().trim());
	pmPoint.addContent(pmCoordinates);

    }
    
    /**
     * Adds all the nodes of the input list to this writer.
     * 
     * @param nodes List of nodes to be added to the writer.
     */
    public void addKMLNodes(List<KMLNode> nodes)
    {
        for ( KMLNode k_i : nodes )
            { this.addKMLNode(k_i); }
    }
    
    @Override
    public String toString()
    {
        String buffer = "";
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            this.writeXML(baos);
            buffer = baos.toString();
        }
        catch (IOException ex)
        {
            buffer = "Could not write XML document.";
            Logger.getLogger(SimpleKMLWriter.class.getName())
                    .log(Level.SEVERE, buffer, ex);
        }
        return(buffer);
    }
    
    /**
     * Method that creates a simple empty KML document with style information 
     * but without contents. It is useful for using it as a base to which add 
     * new KML nodes.
     */
    private void createKMLStubDocument()
    {
        
	Element kml = new Element("kml", ns);
	this.kmlDocument = new Document(kml);

	// Document
	this.root = new Element("Document", ns);
	kml.addContent(this.root);
		
	// name
	Element name = new Element("name", ns);
	name.setText("HumSAT-D sensors");
	this.root.addContent(name);
		
	// Style
	Element style = new Element("Style", ns);
	style.setAttribute("id", "redIcon");
	this.root.addContent(style);
		
	// IconStyle
	Element iconStyle = new Element("IconStyle", ns);
	style.addContent(iconStyle);
		
	// color
	Element color = new Element("color", ns);
	color.setText("990000ff");
	iconStyle.addContent(color);
		
	// Icon
	Element icon = new Element("Icon", ns);
	iconStyle.addContent(icon);
		
	// href
	Element href = new Element("href", ns);
	href.setText(PLACEMARK_ICON_URL);
	icon.addContent(href);
	
    }
    
    /**
     * Writes the current KML tree to the given file.
     * 
     * @param output The file where to write the current KML tree.
     * @throws IOException In case an IO error occurs.
     */
    public void writeXML(File output)
        throws IOException
    {
        FileOutputStream fos = new FileOutputStream(output);
        this.writeXML(fos);
        fos.close();
    }
    
    /**
     * Writes the current KML tree to the given output stream.
     * 
     * @param os The output stream where the KML tree is to be written.
     * @throws IOException In case an IO error occurs.
     */
    public void writeXML(OutputStream os)
        throws IOException
    {
	XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
	outputter.output(this.kmlDocument, os);
    }
    
}
