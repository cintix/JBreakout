/*
 */
package io.xml;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import io.xml.annotations.Alias;
import io.xml.annotations.DateFormat;
import io.xml.annotations.InstanceOf;
import io.xml.annotations.RootNodeValue;
import io.xml.annotations.UnixTimestamp;

/**
 *
 * @author migo
 */
public class XMLReader {

    /**
     *
     * @param <T>
     * @param model
     * @param xml
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T getModelFromXML(Class<T> model, String xml) {
        try {
            T instance = model.newInstance();
            Map<String, List> children = new HashMap<>();
            XMLValidator xmlv = new XMLValidator(xml);

            if (!xmlv.isVerified()) {
                return null;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource inputSource = new InputSource(new StringReader(xmlv.getXML()));

            Document document = builder.parse(inputSource);
            document.getDocumentElement().normalize();

            NodeList nodeList = document.getDocumentElement().getChildNodes();
            NamedNodeMap attributes = document.getDocumentElement().getAttributes();

            if (model.getDeclaredAnnotation(RootNodeValue.class) != null) {
                RootNodeValue nv = model.getAnnotation(RootNodeValue.class);
                Field field = getField(model, nv.name(), nv.name());

                String fieldValue = nodeList.item(0).getNodeValue();

                if (field != null) {
                    field.setAccessible(true);
                    setFieldValue(field, instance, fieldValue);
                }
            }

            for (int ai = 0; ai < attributes.getLength(); ai++) {
                String fieldName = getFieldName(attributes.item(ai).getNodeName());

                String fieldValue = attributes.item(ai).getNodeValue();
                Field field = getField(model, fieldName, attributes.item(ai).getNodeName());

                if (field != null) {
                    field.setAccessible(true);
                    setFieldValue(field, instance, fieldValue);
                }

            }

            Field field = getField(model, "_nodeXml", "_nodeXml", true);
            if (field != null) {
                field.setAccessible(true);
                field.set(instance, xml);
            }

            for (int index = 0; index < nodeList.getLength(); index++) {
                Node node = nodeList.item(index);
                if (node.getNodeName().equals("#text")) {
                    continue;
                }
                field = getField(model, getFieldName(node.getNodeName()), node.getNodeName());
                if (field != null) {
                    field.setAccessible(true);
                    Class<?> fieldType = field.getType();
                    if (fieldType.getPackage() != null && fieldType.getPackage().getName().equals(model.getPackage().getName())) {
                        try {
                            Object childInstance = getClassFromNode(field.getType().getName()).newInstance();
                            childInstance = getModelFromXML(childInstance.getClass(), nodeToXML(node));
                            field.set(instance, childInstance);
                        } catch (ClassNotFoundException ex) {
                            Logger.getLogger(XMLReader.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else if (fieldType.getPackage() != null && fieldType.getName().equals("java.util.List")) {
                        try {
                            List childrenList = children.get(node.getNodeName());
                            if (childrenList == null) {

                                if (field.getAnnotation(InstanceOf.class) != null) {
                                    InstanceOf listInstance = field.getAnnotation(InstanceOf.class);
                                    childrenList = (List) listInstance.type().newInstance();
                                } else {
                                    childrenList = new LinkedList();
                                }
                            }

                            String instanceName = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0].getTypeName();
                            Object childInstance = getClassFromNode(instanceName).newInstance();
                            NodeList nchildrenList = node.getChildNodes();

                            for (int xIndex = 0; xIndex < nchildrenList.getLength(); xIndex++) {
                                Node xChildNode = nchildrenList.item(xIndex);
                                if (xChildNode.getNodeName().equals("#text")) {
                                    continue;
                                }

                                childInstance = getModelFromXML(childInstance.getClass(), nodeToXML(xChildNode));
                                childrenList.add(childInstance);
                                children.put(node.getNodeName(), childrenList);
                            }

                            field.set(instance, children.get(node.getNodeName()));

                        } catch (ClassNotFoundException ex) {
                            Logger.getLogger(XMLReader.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {

                        String value;
                        if (node.hasChildNodes()) {
                            value = node.getChildNodes().item(0).getNodeValue();
                        } else {
                            value = node.getNodeValue();
                        }

                        setFieldValue(field, instance, value);

                    }
                }
            }
            return instance;
        } catch (ParserConfigurationException | IOException | SAXException | InstantiationException | IllegalAccessException exception) {
            exception.printStackTrace();

            return null;
        }
    }

    private static void setFieldValue(Field field, Object instance, String value) {
        try {
            if (field.getType().equals(Integer.TYPE) || field.getType().equals(int.class)) {
                field.set(instance, Integer.parseInt(value));
            } else if (field.getType().equals(Long.TYPE) || field.getType().equals(long.class)) {
                field.set(instance, Long.parseLong(value));
            } else if (field.getType().equals(Boolean.TYPE) || field.getType().equals(boolean.class)) {
                field.set(instance, Boolean.parseBoolean(value));
            } else if (field.getType().equals(Double.TYPE) || field.getType().equals(double.class)) {
                field.set(instance, Double.parseDouble(value));
            } else if (field.getType().equals(Byte.TYPE) || field.getType().equals(byte.class)) {
                field.set(instance, Byte.parseByte(value));
            } else if (field.getType().equals(Float.TYPE) || field.getType().equals(float.class)) {
                field.set(instance, Float.parseFloat(value));
            } else if (field.getType().equals(Date.class)) {

                if (field.getAnnotation(DateFormat.class) != null && value != null && !value.isEmpty()) {
                    DateFormat dateFormat = field.getAnnotation(DateFormat.class);
                    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat.format());
                    sdf.setTimeZone(TimeZone.getDefault());
                    field.set(instance, sdf.parse(value));
                }

                if (field.getAnnotation(UnixTimestamp.class) != null) {
                    long timestamp = Long.parseLong(value);
                    field.set(instance, new Date(timestamp));
                }

            } else {
                field.set(instance, value);
            }
        } catch (ParseException | IllegalArgumentException | IllegalAccessException parseException) {
        }

    }

    /**
     *
     * @param nodeName
     *
     * @return
     *
     * @throws ClassNotFoundException
     */
    private static Class getClassFromNode(String classPath) throws ClassNotFoundException {
        return Class.forName(classPath);
    }

    /**
     *
     * @param s
     * @param delimitor
     *
     * @return
     */
    private static String toCamelCase(String s, String delimitor) {
        String[] parts = s.split(delimitor);
        String camelCaseString = "";
        for (String part : parts) {
            camelCaseString = camelCaseString + toProperCase(part);
        }
        return camelCaseString;
    }

    /**
     *
     * @param s
     *
     * @return
     */
    private static String toProperCase(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    /**
     *
     * @param obj
     * @param name
     *
     * @return
     */
    private static Field getField(Class obj, String name, String orginalName) {
        return getField(obj, name, orginalName, true);
    }

    /**
     *
     * @param obj
     * @param name
     *
     * @return
     */
    private static Field getField(Class obj, String name, String originalName, boolean checkSuper) {
        try {
            for (Field field : obj.getDeclaredFields()) {

                if (field.getDeclaredAnnotation(Alias.class) != null) {
                    Alias fn = field.getAnnotation(Alias.class);
                    if (fn.name().equalsIgnoreCase(name) || fn.name().equalsIgnoreCase(originalName)) {
                        return field;
                    }
                }

                if (field.getName().equalsIgnoreCase(name) || field.getName().equalsIgnoreCase(originalName)) {
                    return field;
                }
            }
            if (checkSuper) {
                for (Field field : obj.getSuperclass().getDeclaredFields()) {
                    if (field.getDeclaredAnnotation(Alias.class) != null) {
                        Alias fn = field.getAnnotation(Alias.class);
                        if (fn.name().equalsIgnoreCase(name) || fn.name().equalsIgnoreCase(originalName)) {
                            return field;
                        }
                    }
                    if (field.getName().equalsIgnoreCase(name)) {
                        return field;
                    }
                }
            }
        } catch (SecurityException ex) {
        }
        return null;
    }

    /**
     *
     * @param node
     *
     * @return
     */
    private static String nodeToXML(Node node) {
        StringWriter sw = new StringWriter();
        try {
            Transformer t = TransformerFactory.newInstance().newTransformer();
            //t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            t.transform(new DOMSource(node), new StreamResult(sw));
        } catch (TransformerException te) {
        }
        return sw.toString();
    }

    /**
     *
     * @param name
     *
     * @return
     */
    private static String getFieldName(String name) {
        String tmpName;
        if (name.contains("_")) {
            tmpName = toCamelCase(name, "_");
        } else if (name.contains("-")) {
            tmpName = toCamelCase(name, "-");
        } else {
            tmpName = name;
        }

        return tmpName.substring(0, 1).toLowerCase() + tmpName.substring(1);
    }

    /**
     *
     */
    public XMLReader() {
    }
}
