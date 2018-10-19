/*
 */
package io.xml;

import io.xml.annotations.Alias;
import io.xml.annotations.Items;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author migo
 */
public class XmlMapper {

    private static final int INDENT_SPACES = 2;
    private static String simpleDateFormat = "dd-MM-yyyy HH:mm:ss";
    private static String EncodingProfile = "UTF-8";
    private static boolean LowerCaseFields = false;
    private static boolean PrettyPrintXml = true;
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(simpleDateFormat);

    /**
     * Set simpleDateFormat
     *
     * @return
     */
    public static String getSimpleDateFormat() {
        return simpleDateFormat;
    }

    /**
     * Get dateformat
     *
     * @param simpleDateFormat
     */
    public static void setSimpleDateFormat(String simpleDateFormat) {
        XmlMapper.simpleDateFormat = simpleDateFormat;
    }

    /**
     * is pretty xml
     *
     * @return
     */
    public static boolean isPrettyPrintXml() {
        return PrettyPrintXml;

    }

    /**
     * set pretty print
     *
     * @param PrettyPrintXml
     */
    public static void setPrettyPrintXml(boolean PrettyPrintXml) {
        XmlMapper.PrettyPrintXml = PrettyPrintXml;
    }

    /**
     * is lowercase names
     *
     * @return
     */
    public static boolean isLowerCaseFields() {
        return LowerCaseFields;
    }

    /**
     * set lowercase names
     *
     * @param LowerCaseFields
     */
    public static void setLowerCaseFields(boolean LowerCaseFields) {
        XmlMapper.LowerCaseFields = LowerCaseFields;
    }

    /**
     * Get encoding
     *
     * @return
     */
    public static String getEncodingProfile() {
        return EncodingProfile;
    }

    /**
     * Set encoding
     *
     * @param EncodingProfile
     */
    public static void setEncodingProfile(String EncodingProfile) {
        XmlMapper.EncodingProfile = EncodingProfile;
    }

    private static Field[] getFieldsFromClass(Object o) {
        List<Field> fields = new LinkedList<>();

        for (Field f : o.getClass().getDeclaredFields()) {
            fields.add(f);
        }

        for (Class<?> c = o.getClass(); c != null; c = c.getSuperclass()) {
            for (Field f : c.getDeclaredFields()) {
                if (Modifier.isPublic(f.getModifiers()) || Modifier.isProtected(f.getModifiers())) {
                    fields.add(f);
                }
            }
        }

        Field[] foundFields = new Field[fields.size()];
        foundFields = fields.toArray(foundFields);
        return foundFields;
    }

    public XmlMapper() {
    }

    /**
     * Convert model to XML
     *
     * @param o
     * @return
     */
    public static String toXML(Object o) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<?xml version=\"1.0\" encoding=\"").append(EncodingProfile).append("\"?>\n");
        if (o == null) {
            return stringBuilder.toString();
        }

        String name = o.getClass().getSimpleName();
        if (o.getClass().isAnnotationPresent(Alias.class)) {
            name = o.getClass().getAnnotation(Alias.class).name();
        }
        if (LowerCaseFields) {
            name = name.toLowerCase();
        }
        stringBuilder.append(write(o, name, null, 0));
        String xml = stringBuilder.toString();
        if (!PrettyPrintXml) {
            xml = stringBuilder.toString().replaceAll("\n", "");
        }
        return xml;
    }

    /**
     * write method
     *
     * @param o
     * @return
     */
    private static String write(Object o, String name, String itemName, int indent) {
        boolean isEnum = false;
        
        if (o != null && o.getClass().getEnumConstants() != null && o.getClass().getEnumConstants().length > 0) {
            isEnum = true;
        }

        if (o == null || o.getClass().isPrimitive() || o.getClass().getName().contains("java.lang")) {
            if (o != null && o.getClass().isArray()) {
                return writeFields(o, name, itemName, indent);
            } else {
                return writeField(o, name, indent);
            }
        } else if (isEnum) {
            return writeEnum(o, name, indent);
        } else if ((o.getClass().getName().contains("java.util.") && o.getClass().getName().contains("Date"))) {
            return writeDate(o, name, indent);
        } else if (o.getClass().getName().contains("java.util.") && o.getClass().getName().contains("List")) {
            return writeList(o, name, itemName, indent);
        } else if (o.getClass().getName().contains("java.util.") && o.getClass().getName().contains("Map")) {
            return writeMap(o, name, indent);
        } else {
            return writeObject(o, name, indent);
        }
    }

    /**
     * write object (class)
     *
     * @param o
     * @param indent
     * @return
     */
    private static String writeObject(Object o, String name, int indent) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(getIdent(indent)).append("<").append(name).append(">\n");
        Field[] fields = getFieldsFromClass(o);
        if (fields != null) {
            indent++;
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    Object fieldObject = field.get(o);
                    String fieldName = field.getName();
                    String itemName = null;
                    if (field.isAnnotationPresent(Alias.class)) {
                        fieldName = field.getAnnotation(Alias.class).name();
                    }
                    if (field.getType().isArray()) {
                        itemName = "item";
                        if (field.isAnnotationPresent(Items.class)) {
                            itemName = field.getAnnotation(Items.class).name();
                        }
                    }
                    if (LowerCaseFields) {
                        fieldName = fieldName.toLowerCase();
                        if (itemName != null) {
                            itemName = itemName.toLowerCase();
                        }
                    }
                    stringBuilder.append(write(fieldObject, fieldName, itemName, indent));
                } catch (IllegalArgumentException | IllegalAccessException ex) {
                    Logger.getLogger(XmlMapper.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            indent--;
        }
        stringBuilder.append(getIdent(indent)).append("</").append(name).append(">\n");
        return stringBuilder.toString();
    }

    /**
     * Write List
     *
     * @param o
     * @param name
     * @param indent
     * @return
     */
    private static String writeList(Object o, String name, String itemName, int indent) {
        StringBuilder stringBuilder = new StringBuilder();
        List<Object> items = (List) o;
        stringBuilder.append(getIdent(indent)).append("<").append(name).append(">\n");
        if (items != null) {
            indent++;
            for (Object fieldObject : items) {
                try {
                    if (itemName == null) {
                        if (fieldObject.getClass().isPrimitive() || fieldObject.getClass().getName().startsWith("java.lang")
                                || (fieldObject.getClass().getName().contains("java.util.") && fieldObject.getClass().getName().contains("Date"))) {
                            itemName = "item";
                        } else {
                            itemName = fieldObject.getClass().getSimpleName();
                            if (fieldObject.getClass().isAnnotationPresent(Alias.class)) {
                                itemName = fieldObject.getClass().getAnnotation(Alias.class).name();
                            }
                        }
                    }

                    if (LowerCaseFields) {
                        if (itemName != null) {
                            itemName = itemName.toLowerCase();
                        }
                    }

                    stringBuilder.append(write(fieldObject, itemName, null, indent));
                } catch (IllegalArgumentException ex) {
                    Logger.getLogger(XmlMapper.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            indent--;
        }
        stringBuilder.append(getIdent(indent)).append("</").append(name).append(">\n");
        return stringBuilder.toString();
    }

    /**
     * Write Maps
     *
     * @param o
     * @param name
     * @param indent
     * @return
     */
    private static String writeMap(Object o, String name, int indent) {
        StringBuilder stringBuilder = new StringBuilder();
        Map<Object, Object> items = (Map<Object, Object>) o;
        stringBuilder.append(getIdent(indent)).append("<").append(name).append(">\n");
        if (items != null) {
            indent++;
            for (Object fieldKey : items.keySet()) {
                try {
                    Object fieldObject = items.get(fieldKey);
                    stringBuilder.append(write(fieldObject, (LowerCaseFields) ? fieldKey.toString().toLowerCase() : fieldKey.toString(), null, indent));
                } catch (IllegalArgumentException ex) {
                    Logger.getLogger(XmlMapper.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            indent--;
        }
        stringBuilder.append(getIdent(indent)).append("</").append(name).append(">\n");
        return stringBuilder.toString();
    }

    /**
     * write field value
     *
     * @param o
     * @param name
     * @param indent
     * @return
     */
    private static String writeField(Object o, String name, int indent) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getIdent(indent)).append("<").append(name).append(">");
        if (o != null) {
            stringBuilder.append(o.toString());
        }
        stringBuilder.append("</").append(name).append(">\n");
        return stringBuilder.toString();
    }

    /**
     * write enum value
     *
     * @param o
     * @param name
     * @param indent
     * @return
     */
    private static String writeEnum(Object o, String name, int indent) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getIdent(indent)).append("<").append(name).append(">");
        Enum e = (Enum) o;
        if (o != null) {
            stringBuilder.append(e.name());
        }
        stringBuilder.append("</").append(name).append(">\n");
        return stringBuilder.toString();
    }

    /**
     * write date value
     *
     * @param o
     * @param name
     * @param indent
     * @return
     */
    private static String writeDate(Object o, String name, int indent) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getIdent(indent)).append("<").append(name).append(">");
        Date date = (Date) o;
        if (o != null) {
            stringBuilder.append(SIMPLE_DATE_FORMAT.format(o));
        }
        stringBuilder.append("</").append(name).append(">\n");
        return stringBuilder.toString();
    }

    /**
     * write fields (Array)
     *
     * @param o
     * @param name
     * @param indent
     * @return
     */
    private static String writeFields(Object o, String name, String itemName, int indent) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getIdent(indent)).append("<").append(name).append(">\n");
        Object[] objects = (Object[]) o;
        if (objects != null) {
            indent++;
            for (Object obj : objects) {
                stringBuilder.append(getIdent(indent)).append("<").append(itemName).append(">");
                stringBuilder.append(obj.toString());
                stringBuilder.append("</").append(itemName).append(">\n");
            }
            indent--;
        }
        stringBuilder.append(getIdent(indent)).append("</").append(name).append(">\n");
        return stringBuilder.toString();
    }

    /**
     * Get indent spaces
     *
     * @param count
     * @return
     */
    private static String getIdent(int count) {
        int ident = 0;
        int total = count * INDENT_SPACES;
        StringBuilder stringBuilder = new StringBuilder();
        if (!PrettyPrintXml) {
            total = 0;
        }
        while (ident < total) {
            ident++;
            stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }

}
