package com.gever.jdbc.sqlhelper;

import java.beans.*;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ��������</p>
 * @author Hu.Walker
 * @version 1.0
 */

public class SQLHelperUtil {
    public SQLHelperUtil() {
    }
    private static String dbtype = "db2";
    
    public static boolean isOracle (){
		boolean bRet = false;
		if ("oracle".equalsIgnoreCase(dbtype)){
			bRet = true;
		}
		return bRet;
	}
	public boolean isDB2 (){
		boolean bRet = false;
		if ("db2".equalsIgnoreCase(dbtype)){
			bRet = true;
		}
		return bRet;
	}
	/**
	 * @return ���� dbtype��
	 */
	public static String getDbtype() {
		return dbtype;
	}
	/**
	 * @param dbtype Ҫ���õ� dbtype��
	 */
	public static void setDbtype(String dbtype) {
		SQLHelperUtil.dbtype = dbtype;
	}
    public static int getDebug() {
        return debug;
    }

    public static void setDebug(int newDebug) {
        debug = newDebug;
    }

    public static Map describe(Object bean) throws IllegalAccessException,
        InvocationTargetException, NoSuchMethodException {
        if (bean == null)
            throw new IllegalArgumentException("No bean specified");
        PropertyDescriptor descriptors[] = getPropertyDescriptors(bean);
        Map description = new HashMap(descriptors.length);
        for (int i = 0; i < descriptors.length; i++) {
            String name = descriptors[i].getName();
            if (descriptors[i].getReadMethod() != null)
                description.put(name, getProperty(bean, name));
        }

        return description;
    }

    public static Object getIndexedProperty(Object bean, String name) throws
        IllegalAccessException, InvocationTargetException,
        NoSuchMethodException {
        if (bean == null)
            throw new IllegalArgumentException("No bean specified");
        if (name == null)
            throw new IllegalArgumentException("No name specified");
        int delim = name.indexOf('[');
        int delim2 = name.indexOf(']');
        if (delim < 0 || delim2 <= delim)
            throw new IllegalArgumentException("Invalid indexed property '" +
                                               name + "'");
        int index = -1;
        try {
            String subscript = name.substring(delim + 1, delim2);
            index = Integer.parseInt(subscript);
        } catch (NumberFormatException numberformatexception) {
            throw new IllegalArgumentException("Invalid indexed property '" +
                                               name + "'");
        }
        name = name.substring(0, delim);
        return getIndexedProperty(bean, name, index);
    }

    public static Object getIndexedProperty(Object bean, String name, int index) throws
        IllegalAccessException, InvocationTargetException,
        NoSuchMethodException {
        if (bean == null)
            throw new IllegalArgumentException("No bean specified");
        if (name == null)
            throw new IllegalArgumentException("No name specified");
        PropertyDescriptor descriptor = getPropertyDescriptor(bean, name);
        if (descriptor == null)
            throw new NoSuchMethodException("Unknown property '" + name + "'");
        Method readMethod;
        if (descriptor instanceof IndexedPropertyDescriptor) {
            readMethod = ( (IndexedPropertyDescriptor) descriptor).
                getIndexedReadMethod();
            if (readMethod != null) {
                Object subscript[] = new Object[1];
                subscript[0] = new Integer(index);
                return readMethod.invoke(bean, subscript);
            }
        }
        readMethod = getReadMethod(descriptor);
        if (readMethod == null)
            throw new NoSuchMethodException("Property '" + name +
                                            "' has no getter method");
        Object value = readMethod.invoke(bean, new Object[0]);
        if (!value.getClass().isArray())
            throw new IllegalArgumentException("Property '" + name +
                                               "' is not indexed");
        else
            return Array.get(value, index);
    }

    public static Object getNestedProperty(Object bean, String name) throws
        IllegalAccessException, InvocationTargetException,
        NoSuchMethodException {
    //  System.out.println("----start---1--");
        if (bean == null)
            throw new IllegalArgumentException("No bean specified");
        if (name == null)
            throw new IllegalArgumentException("No name specified");

        do {
            int delim = name.indexOf('.');
            if (delim < 0)
                break;
            String next = name.substring(0, delim);

            if (next.indexOf('[') >= 0)
                bean = getIndexedProperty(bean, next);
            else
                bean = getSimpleProperty(bean, next);
            if (bean == null)
                throw new IllegalArgumentException("Null property value for '" +
                    name.substring(0, delim) + "'");
            name = name.substring(delim + 1);
        } while (true);
        if (name.indexOf('[') >= 0)
            return getIndexedProperty(bean, name);
        else
            return getSimpleProperty(bean, name);
    }

    public static Object getProperty(Object bean, String name) throws
        IllegalAccessException, InvocationTargetException,
        NoSuchMethodException {
      //System.out.println("----------" + bean.getClass().getName() + " name=" + name);
        return getNestedProperty(bean, name);
    }

    public static PropertyDescriptor getPropertyDescriptor(Object bean,
        String name) throws IllegalAccessException, InvocationTargetException,
        NoSuchMethodException {

        if (bean == null)
            throw new IllegalArgumentException("No bean specified");
        if (name == null)
            throw new IllegalArgumentException("No name specified");

        do {
            int period = name.indexOf('.');
            if (period < 0)
                break;
            String next = name.substring(0, period);
            if (next.indexOf('[') >= 0)
                bean = getIndexedProperty(bean, next);
            else
                bean = getSimpleProperty(bean, next);
            if (bean == null)
                throw new IllegalArgumentException("Null property value for '" +
                    name.substring(0, period) + "'");
            name = name.substring(period + 1);
        } while (true);
         //     System.out.println("----start 13--");
        int left = name.indexOf('[');
        if (left >= 0)
            name = name.substring(0, left);
        if (bean == null || name == null)
            return null;

        PropertyDescriptor descriptors[] = getPropertyDescriptors(bean);
        if (descriptors == null)
            return null;
        for (int i = 0; i < descriptors.length; i++)
            if (name.equals(descriptors[i].getName()))
                return descriptors[i];

        return null;
    }

    public static PropertyDescriptor[] getPropertyDescriptors(Object bean) {
        if (bean == null)
            throw new IllegalArgumentException("No bean specified");
        String beanClassName = bean.getClass().getName();

        PropertyDescriptor descriptors[] = null;
        descriptors = (PropertyDescriptor[]) descriptorsCache.get(beanClassName);
        if (descriptors != null)
            return descriptors;
        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(bean.getClass());
        } catch (IntrospectionException introspectionexception) {
            return new PropertyDescriptor[0];
        }
        descriptors = beanInfo.getPropertyDescriptors();
        if (descriptors == null)
            descriptors = new PropertyDescriptor[0];
        descriptorsCache.put(beanClassName, descriptors);
        return descriptors;
    }

    public static Class getPropertyEditorClass(Object bean, String name) throws
        IllegalAccessException, InvocationTargetException,
        NoSuchMethodException {
        if (bean == null)
            throw new IllegalArgumentException("No bean specified");
        if (name == null)
            throw new IllegalArgumentException("No name specified");
        PropertyDescriptor descriptor = getPropertyDescriptor(bean, name);
        if (descriptor != null)
            return descriptor.getPropertyEditorClass();
        else
            return null;
    }

    public static Class getPropertyType(Object bean, String name) throws
        IllegalAccessException, InvocationTargetException,
        NoSuchMethodException {
        if (bean == null)
            throw new IllegalArgumentException("No bean specified");
        if (name == null)
            throw new IllegalArgumentException("No name specified");
        PropertyDescriptor descriptor = getPropertyDescriptor(bean, name);
        if (descriptor == null)
            return null;
        if (descriptor instanceof IndexedPropertyDescriptor)
            return ( (IndexedPropertyDescriptor) descriptor).
                getIndexedPropertyType();
        else
            return descriptor.getPropertyType();
    }

    public static Method getReadMethod(PropertyDescriptor descriptor) {
        return getAccessibleMethod(descriptor.getReadMethod());
    }

    public static Object getSimpleProperty(Object bean, String name) throws
        IllegalAccessException, InvocationTargetException,
        NoSuchMethodException {

        if (bean == null)
            throw new IllegalArgumentException("No bean specified");
        if (name == null)
            throw new IllegalArgumentException("No name specified");
        if (name.indexOf('.') >= 0)
            throw new IllegalArgumentException(
                "Nested property names are not allowed");
        if (name.indexOf('[') >= 0)
            throw new IllegalArgumentException(
                "Indexed property names are not allowed");

        PropertyDescriptor descriptor = getPropertyDescriptor(bean, name);


        if (descriptor == null)
            throw new NoSuchMethodException("Unknown property '" + name + "'");
        Method readMethod = getReadMethod(descriptor);
        if (readMethod == null) {
            throw new NoSuchMethodException("Property '" + name +
                                            "' has no getter method");
        } else {
            Object value = readMethod.invoke(bean, new Object[0]);
            return value;
        }
    }

    public static Method getWriteMethod(PropertyDescriptor descriptor) {
        return getAccessibleMethod(descriptor.getWriteMethod());
    }

    public static void setIndexedProperty(Object bean, String name,
                                          Object value) throws
        IllegalAccessException, InvocationTargetException,
        NoSuchMethodException {
        if (bean == null)
            throw new IllegalArgumentException("No bean specified");
        if (name == null)
            throw new IllegalArgumentException("No name specified");
        int delim = name.indexOf('[');
        int delim2 = name.indexOf(']');
        if (delim < 0 || delim2 <= delim)
            throw new IllegalArgumentException("Invalid indexed property '" +
                                               name + "'");
        int index = -1;
        try {
            String subscript = name.substring(delim + 1, delim2);
            index = Integer.parseInt(subscript);
        } catch (NumberFormatException numberformatexception) {
            throw new IllegalArgumentException("Invalid indexed property '" +
                                               name + "'");
        }
        name = name.substring(0, delim);
        setIndexedProperty(bean, name, index, value);
    }

    public static void setIndexedProperty(Object bean, String name, int index,
                                          Object value) throws
        IllegalAccessException, InvocationTargetException,
        NoSuchMethodException {
        if (bean == null)
            throw new IllegalArgumentException("No bean specified");
        if (name == null)
            throw new IllegalArgumentException("No name specified");
        PropertyDescriptor descriptor = getPropertyDescriptor(bean, name);
        if (descriptor == null)
            throw new NoSuchMethodException("Unknown property '" + name + "'");
        if (descriptor instanceof IndexedPropertyDescriptor) {
            Method writeMethod = ( (IndexedPropertyDescriptor) descriptor).
                getIndexedWriteMethod();
            if (writeMethod != null) {
                Object subscript[] = new Object[2];
                subscript[0] = new Integer(index);
                subscript[1] = value;
                writeMethod.invoke(bean, subscript);
                return;
            }
        }
        Method readMethod = descriptor.getReadMethod();
        if (readMethod == null)
            throw new NoSuchMethodException("Property '" + name +
                                            "' has no getter method");
        Object array = readMethod.invoke(bean, new Object[0]);
        if (!array.getClass().isArray()) {
            throw new IllegalArgumentException("Property '" + name +
                                               "' is not indexed");
        } else {
            Array.set(array, index, value);
            return;
        }
    }

    public static void setNestedProperty(Object bean, String name, Object value) throws
        IllegalAccessException, InvocationTargetException,
        NoSuchMethodException {
        if (bean == null)
            throw new IllegalArgumentException("No bean specified");
        if (name == null)
            throw new IllegalArgumentException("No name specified");
        do {
            int delim = name.indexOf('.');
            if (delim < 0)
                break;
            String next = name.substring(0, delim);
            if (next.indexOf('[') >= 0)
                bean = getIndexedProperty(bean, next);
            else
                bean = getSimpleProperty(bean, next);
            if (bean == null)
                throw new IllegalArgumentException("Null property value for '" +
                    name.substring(0, delim) + "'");
            name = name.substring(delim + 1);
        } while (true);
        if (name.indexOf('[') >= 0)
            setIndexedProperty(bean, name, value);
        else
            setSimpleProperty(bean, name, value);
    }

    public static void setProperty(Object bean, String name, Object value) throws
        IllegalAccessException, InvocationTargetException,
        NoSuchMethodException {
        setNestedProperty(bean, name, value);
    }

    public static void setSimpleProperty(Object bean, String name, Object value) throws
        IllegalAccessException, InvocationTargetException,
        NoSuchMethodException {
        if (bean == null)
            throw new IllegalArgumentException("No bean specified");
        if (name == null)
            throw new IllegalArgumentException("No name specified");
        if (name.indexOf('.') >= 0)
            throw new IllegalArgumentException(
                "Nested property names are not allowed");
        if (name.indexOf('[') >= 0)
            throw new IllegalArgumentException(
                "Indexed property names are not allowed");
        PropertyDescriptor descriptor = getPropertyDescriptor(bean, name);
        if (descriptor == null)
            throw new NoSuchMethodException("Unknown property '" + name + "'");
        Method writeMethod = getWriteMethod(descriptor);
        if (writeMethod == null) {
            throw new NoSuchMethodException("Property '" + name +
                                            "' has no setter method");
        } else {
            Object values[] = new Object[1];
            values[0] = value;
            writeMethod.invoke(bean, values);
            return;
        }
    }

    private static Method getAccessibleMethod(Method method) {
        if (method == null)
            return null;
        if (!Modifier.isPublic(method.getModifiers()))
            return null;
        Class clazz = method.getDeclaringClass();
        if (Modifier.isPublic(clazz.getModifiers())) {
            return method;
        } else {
            String methodName = method.getName();
            Class parameterTypes[] = method.getParameterTypes();
            method = getAccessibleMethodFromInterfaceNest(clazz, method.getName(),
                method.getParameterTypes());
            return method;
        }
    }

    private static Method getAccessibleMethodFromInterfaceNest(Class clazz,
        String methodName, Class parameterTypes[]) {
        Method method = null;
        Class interfaces[] = clazz.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            if (!Modifier.isPublic(interfaces[i].getModifiers()))
                continue;
            try {
                method = interfaces[i].getDeclaredMethod(methodName,
                    parameterTypes);
            } catch (NoSuchMethodException nosuchmethodexception) {}
            if (method != null)
                break;
            method = getAccessibleMethodFromInterfaceNest(interfaces[i],
                methodName, parameterTypes);
            if (method != null)
                break;
        }

        return method;
    }

    public static final char INDEXED_DELIM = 91;
    public static final char INDEXED_DELIM2 = 93;
    public static final char NESTED_DELIM = 46;
    private static int debug = 0;
    private static HashMap descriptorsCache;
    static{
      descriptorsCache = new HashMap();

    }
}