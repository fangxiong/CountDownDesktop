package com.fax.showdt.utils;

import android.util.ArrayMap;

import com.fax.showdt.BuildConfig;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

public class ReflectUtils {

    private static final int INT_BOOL_FALSE = 0;
    private static final int INT_BOOL_TRUE = 1;
    private static final int INT_BOOL_THIRD_STATE = 10000000;

    private static final Map<String, String> SIGNATURE_TABLE;

    static {
        final Map<String, String> table = new ArrayMap<>(9);
        table.put("byte", "B");
        table.put("char", "C");
        table.put("short", "S");
        table.put("int", "I");
        table.put("long", "J");
        table.put("float", "F");
        table.put("double", "D");
        table.put("void", "V");
        table.put("boolean", "Z");
        SIGNATURE_TABLE = Collections.unmodifiableMap(table);
    }

    public static String getSignature(Field field) {
        return getFieldSignature(field.getName(), field.getType());
    }

    public static String getSignature(Method method) {
        return getMethodSignature(method.getName(), method.getReturnType(), method.getParameterTypes());
    }

    public static String getFieldSignature(String name, Class<?> type) {
        StringBuilder builder = new StringBuilder(name).append(':');
        getSignature(type, builder);
        return builder.toString();
    }

    public static String getMethodSignature(String name, Class<?> returnType, Class<?>... paramTypes) {
        StringBuilder builder = new StringBuilder(name).append('(');

        if (paramTypes != null) {
            for (Class<?> type : paramTypes) {
                getSignature(type, builder);
            }
        }
        builder.append(')');
        getSignature(returnType, builder);
        return builder.toString();
    }

    public static String getSignature(Class<?> clazz) {
        return getSignature(clazz, null);
    }

    public static void setAccessible(final AccessibleObject accessible, final boolean isAccessible) {
        if (accessible == null) {
            return;
        }
        if (accessible.isAccessible() != isAccessible) {
            accessible.setAccessible(isAccessible);
        }
    }

    private static String getSignature(Class<?> clazz, StringBuilder builder) {
        final boolean isRoot = builder == null;
        if (isRoot) {
            builder = new StringBuilder();
        }
        final String name = clazz.getName();
        if (SIGNATURE_TABLE.containsKey(name)) {
            builder.append(SIGNATURE_TABLE.get(name));
        } else {
            if (clazz.isArray()) {
                builder.append('[');
                getSignature(clazz.getComponentType(), builder);
            } else {
                builder.append('L').append(name).append(';');
            }
        }
        return isRoot ? builder.toString() : null;
    }

    public static Class<?> classForName(String className) {
        try {
            return Class.forName(className);
        } catch (Throwable ignored) {
        }
        return null;
    }

    public static boolean setFieldValue(Object object, String fieldName, Object value) {
        final Class<?> clazz = object instanceof Class ? (Class) object : object.getClass();
        final Field field = getField(clazz, fieldName);

        if (field == null) {
            return false;
        }

        int isAccessible = INT_BOOL_THIRD_STATE;
        try {
            isAccessible = boolToInt(field.isAccessible());
            if (isAccessible == INT_BOOL_FALSE) {
                field.setAccessible(true);
            }
            field.set(object, value);
            return true;
        } catch (Exception ex) {
        } finally {
            if (isAccessible == INT_BOOL_FALSE) {
                field.setAccessible(false);
            }
        }
        return false;
    }

    public static <T> T getFieldValue(Object object, String fieldName) {
        final Class<?> clazz;
        if (object instanceof Class) {
            clazz = (Class<?>) object;
        } else {
            clazz = object.getClass();
        }

        Field field = getField(clazz, fieldName);

        if (field == null) {
            return null;
        }

        int isAccessible = INT_BOOL_THIRD_STATE;
        try {
            isAccessible = boolToInt(field.isAccessible());
            if (isAccessible == INT_BOOL_FALSE) {
                field.setAccessible(true);
            }
            return (T) field.get(object);
        } catch (Exception ex) {
        } finally {
            if (field != null && isAccessible == INT_BOOL_FALSE) {
                field.setAccessible(false);
            }
        }
        return null;
    }

    public static <T> T invokeVerySafe(Object obj, String methodName, Object... params) {
        try {
            return invoke(obj, methodName, params);
        } catch (Throwable tr) {
            if (BuildConfig.DEBUG) {
            }
        }
        return null;
    }

    /**
     * 反射调用方法, params是参数-值表, 结构是:<br/>
     * <b> class1: value1 <br/>
     * class2: value2 <br/>
     * class3: value3 <br/>
     * ....<br />
     * </b>
     *
     * @param obj
     * @param methodName
     * @param params
     * @return
     */
    public static <T> T invoke(Object obj, String methodName, Object... params) {
        Method method = null;
        int isAccessible = INT_BOOL_THIRD_STATE;
        T result = null;

        final CallTypesAndParams data = parseCallTypesAndParams(params);
        try {
            Class<?> clazz;
            // 如果是静态方法, 传进来的obj就是class本身
            if (obj instanceof Class) {
                clazz = (Class<?>) obj;
            } else {
                clazz = obj.getClass();
            }
            if (data.hasParams) {
                method = getMethod(clazz, methodName, data.types);
            } else {
                method = getMethod(clazz, methodName);
            }
            isAccessible = boolToInt(method.isAccessible());
            if (isAccessible == INT_BOOL_FALSE) {
                method.setAccessible(true);
            }

            if (data.hasParams) {
                result = (T) method.invoke(obj, data.params);
            } else {
                result = (T) method.invoke(obj);
            }

        } catch (InvocationTargetException e) {
            Throwable targetException = e.getTargetException();
            if (targetException == null) {
                targetException = e.getCause();
            }
            if (targetException == null) {
                targetException = e;
            }
            throw new RuntimeException(targetException);
        } catch (Exception e) {
        } finally {
            if (method != null && isAccessible == INT_BOOL_FALSE) {
                method.setAccessible(false);
            }
        }
        return result;
    }


    public static <T> T newInstance(Class<T> clazz, Object... params) {
        T result = null;
        Constructor<T> constructor = null;
        int isAccessible = INT_BOOL_THIRD_STATE;

        final CallTypesAndParams data = parseCallTypesAndParams(params);

        try {
            if (data.hasParams) {
                constructor = clazz.getConstructor(data.types);
            } else {
                constructor = clazz.getConstructor();
            }

            isAccessible = boolToInt(constructor.isAccessible());
            if (isAccessible == INT_BOOL_FALSE) {
                constructor.setAccessible(true);
            }
            if (data.hasParams) {
                result = constructor.newInstance(data.params);
            } else {
                result = constructor.newInstance();
            }
        } catch (InvocationTargetException e) {
            Throwable targetException = e.getTargetException();
            if (targetException == null) {
                targetException = e.getCause();
            }
            if (targetException == null) {
                targetException = e;
            }
            throw new RuntimeException(targetException);
        } catch (Exception ex) {
        } finally {
            if (constructor != null && isAccessible == INT_BOOL_FALSE) {
                constructor.setAccessible(false);
            }
        }
        return result;
    }

    public static <T> T newInstance(String className, Object... params) {
        if (className == null) {
            return null;
        }
        Class<T> clazz = null;
        try {
            clazz = (Class<T>) Class.forName(className);
        } catch (Exception e) {
        }

        if (clazz == null) {
            return null;
        }

        return newInstance(clazz, params);
    }

    private static Field getField(Class<?> clazz, String fieldName) {
        Field field = null;
        try {
            field = clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            try {
                field = clazz.getField(fieldName);
            } catch (NoSuchFieldException e1) {
                if (clazz.getSuperclass() != null) {
                    field = getField(clazz.getSuperclass(), fieldName);
                }
            }
        }
        return field;
    }

    private static Method getMethod(Class<?> clazz, String methodName, Class<?>... classes) {
        Method method = null;
        try {
            method = clazz.getDeclaredMethod(methodName, classes);
        } catch (NoSuchMethodException e) {
            if ((clazz = clazz.getSuperclass()) != null) {
                method = getMethod(clazz, methodName, classes);
            }
        }
        return method;
    }

    public static Method getMethodDeep(Class<?> clazz, String methodName, Class<?>... classes) {
        return getMethod(clazz, methodName, classes);
    }

    private static int boolToInt(boolean bool) {
        return bool ? INT_BOOL_TRUE : INT_BOOL_FALSE;
    }

    private static CallTypesAndParams parseCallTypesAndParams(Object... params) {
        Class<?>[] classes = null;
        Object[] objects = null;
        int len = 0;
        boolean hasParams = params != null && (len = params.length) > 0;
        if (hasParams) {
            if ((len % 2) == 0) {
                int count = len >> 1;
                classes = new Class<?>[count];
                objects = new Object[count];
                for (int i = 0; i < len; i++) {
                    if (i % 2 == 0) {
                        if (params[i] instanceof Class<?>) {
                            classes[i >> 1] = (Class<?>) params[i];
                        } else {
                            throw new RuntimeException(
                                    "Params wrong!!! This index of params mast be a Class<?> index: "
                                            + i
                            );
                        }
                    } else {
                        objects[i >> 1] = params[i];
                    }
                }
            } else {
                throw new RuntimeException("Params count not correct!");
            }
        }
        return new CallTypesAndParams(hasParams, classes, objects);
    }

    private static class CallTypesAndParams {
        final boolean hasParams;
        final Class<?>[] types;
        final Object[] params;

        private CallTypesAndParams(boolean hasParams, Class<?>[] types, Object[] params) {
            this.hasParams = hasParams;
            this.types = types;
            this.params = params;
        }
    }

}
