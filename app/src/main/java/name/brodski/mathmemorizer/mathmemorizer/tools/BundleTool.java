package name.brodski.mathmemorizer.mathmemorizer.tools;

import android.os.Bundle;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by boris on 29.11.16.
 */
public class BundleTool {
    public static void save(Object o, Bundle bundle, String prefix) {
        if (o == null) {
            throw new RuntimeException("null not supported yet");
        }
        Class<?> c = o.getClass();
        while (c != Object.class) {
            for (Field f : c.getDeclaredFields()) {
                if (f.getAnnotation(Save.class) != null) {
                    f.setAccessible(true);
                    saveField(o, f, bundle, prefix);
                }
            }
            c = c.getSuperclass();
        }
    }

    public static void load(Object o, Bundle bundle, String prefix) {
        if (o == null) {
            throw new RuntimeException("null not supported yet");
        }
        Class<?> c = o.getClass();
        while (c != Object.class) {
            for (Field f : c.getDeclaredFields()) {
                if (f.getAnnotation(Save.class) != null) {
                    f.setAccessible(true);
                    loadField(o, f, bundle, prefix);
                }
            }
            c = c.getSuperclass();
        }
    }

    private static void saveField(Object o, Field f, Bundle bundle, String prefix) {
        Object value;
        try {
            value = f.get(o);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Can't get value of the field: " + f, e);
        }
        String key = prefix + f.getName();
        if (value == null) {
            bundle.putSerializable(key, null);
        } else if (f.getType() == String.class) {
            bundle.putString(key, (String)value);
        } else if (f.getType() == Long.class || f.getType() == long.class) {
            bundle.putLong(key, (Long)value);
        } else if (f.getType() == Integer.class || f.getType() == int.class) {
            bundle.putInt(key, (Integer) value);
        } else if (f.getType() == Boolean.class || f.getType() == boolean.class) {
            bundle.putBoolean(key, (Boolean) value);
        } else if (value instanceof Serializable) {
            bundle.putSerializable(key, (Serializable) value);
        } else {
            throw new RuntimeException("Unknown type: " + f.getType() + " (" + f + ")");
        }
    }

    private static void loadField(Object o, Field f, Bundle bundle, String prefix) {
        Object value;
        String key = prefix + f.getName();

        if (f.getType() == String.class) {
            value = bundle.getString(key);
        } else if (f.getType() == Long.class || f.getType() == long.class) {
            value = bundle.getLong(key);
        } else if (f.getType() == Integer.class || f.getType() == int.class) {
            value = bundle.getInt(key);
        } else if (f.getType() == Boolean.class || f.getType() == boolean.class) {
            value = bundle.getBoolean(key);
        } else {
            value = bundle.getSerializable(key);
        }
        try {
            f.set(o, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Can't set value to the field: " + value.getClass() + " => " + f);
        }
    }
}
