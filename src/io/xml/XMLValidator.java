/*
 */
package io.xml;

/**
 *
 * @author migo
 */
public class XMLValidator {

    private String xml;

    /**
     * 
     * @param xml 
     */
    public XMLValidator(String xml) {
        this.xml = xml;
    }

    /**
     * Validtae the xml 
     * @return 
     */
    public boolean isVerified() {
        return !(xml == null || xml.isEmpty());
    }

    /**
     * Return xml
     * 
     * @return 
     */
    public String getXML() {

        while (!xml.startsWith("<")) {
            xml = xml.substring(1);
        }

        return xml;
    }

}
