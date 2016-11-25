package name.brodski.mathmemorizer.mathmemorizer.preferences;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.reflect.Method;

/**
 * Created by boris on 15.10.16.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public abstract class MyEditingPreferenceFragment extends MyPreferenceFragment {
    protected abstract Object getObject();
    protected abstract boolean validate(Pref pref, Preference preference, Object newValue);
    protected abstract void objectUpdated();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Object objectToEdit = getObject();
        fillPreferencesFromObject(objectToEdit);
        super.onCreate(savedInstanceState);

    }

    protected void fillPreferencesFromObject(Object objectToEdit) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = preferences.edit();
        for (Pref pref : getPrefs()) {
            if (objectToEdit != null) {
                Method getter = getGetter(objectToEdit, pref);
                Object value = null;
                try {
                    value = getter.invoke(objectToEdit);
                } catch (Exception e) {
                    throw new RuntimeException("Can't invoke getter method", e);
                }
                setToPreferences(editor, pref, value);
            } else {
                editor.remove(pref.getName());
            }
        }
        editor.commit();
    }

    @Override
    public void onPause() {
        super.onPause();
        Object object = getObject();
        if (object != null) {
            fillFromPreferences(object);
            objectUpdated();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        for (final Pref pref : getPrefs()) {
            Preference preference = findPreference(pref.getName());
            final Preference.OnPreferenceChangeListener oldPreferenceChangeListener = preference.getOnPreferenceChangeListener();
            // getPreferenceManager().
            preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (validate(pref, preference, newValue)) {
                        if (oldPreferenceChangeListener != null) {
                            return oldPreferenceChangeListener.onPreferenceChange(preference, newValue);
                        }
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    private void fillFromPreferences(Object object) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        for (Pref pref : getPrefs()) {
            Method setter = getSetter(object, pref);
            Class<?> type = setter.getParameterTypes()[0];

            Object value = getFromPreferences(preferences, pref, type);
            try {
                setter.invoke(object, value);
            } catch (Exception e) {
                throw new RuntimeException("Can't invoke setter method", e);
            }
        }
    }

    private Object getFromPreferences(SharedPreferences preferences, Pref pref, Class<?> type) {
        if (!preferences.contains(pref.getName())) {
            throw new RuntimeException("Preference " + pref.getName() + " not set");
        }

        switch (pref.getType()) {
            case STRING:
            case RINGTONE:
                return preferences.getString(pref.getName(), "").trim();

            case INT:
            case SECONDS:
                String value = getStringFromPreferences(preferences, pref, type);
                if (value == null) {
                    return null;
                }
                if (type == Integer.class || type == int.class) {
                    return Integer.valueOf(value);
                }  else if (type == Long.class || type == long.class) {
                    return Long.valueOf(value);
                }
                throw new RuntimeException("Unsupported type: " + value.getClass());

            case SWITCH:
                if (type == Boolean.class || type == boolean.class) {
                    return preferences.getBoolean(pref.getName(), false);
                }
                throw new RuntimeException("Unsupported type: " + type);
            default:
                throw new RuntimeException("Unknown enum: " + pref.getType());
        }
    }

    @Nullable
    private String getStringFromPreferences(SharedPreferences preferences, Pref pref, Class<?> type) {
        String value = preferences.getString(pref.getName(), "").trim();
        if (value.length() == 0) {
            if (type.isPrimitive()) {
                throw new RuntimeException("Value required for " + pref.getName());
            }
            return null;
        }
        return value;
    }

    protected void setToPreferences(SharedPreferences.Editor editor, Pref pref, Object value) {
        switch (pref.getType()) {
            case STRING:
            case RINGTONE:
                editor.putString(pref.getName(), value.toString());
                break;

            case INT:
            case SECONDS:
                if (value.getClass() == Integer.class) {
                    editor.putString(pref.getName(), ((Integer) value).toString());
                } else if (value.getClass() == Long.class) {
                    editor.putString(pref.getName(), ((Long) value).toString());
                } else {
                    throw new RuntimeException("Unsupported type: " + value.getClass());
                }
                break;

            case SWITCH:
                if (value.getClass() == Boolean.class) {
                    editor.putBoolean(pref.getName(), (Boolean) value);
                } else {
                    throw new RuntimeException("Unsupported type: " + value.getClass());
                }
                break;
        }
    }

    private Method getGetter(Object objectToEdit, Pref pref) {
        String firstUpperName = toFirstUpper(pref.getField());
        try {
            return objectToEdit.getClass().getMethod("get" + firstUpperName);
        } catch (NoSuchMethodException e) {
            try {
                return objectToEdit.getClass().getMethod("is" + firstUpperName);
            } catch (NoSuchMethodException e1) {
                throw new RuntimeException("No 'get-'/'is-' getter method found on " + objectToEdit.getClass() + " for pref " + pref.getName(), e1);
            }
        }
    }

    @NonNull
    private String toFirstUpper(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    private Method getSetter(Object objectToEdit, Pref pref) {
        Method getter = getGetter(objectToEdit, pref);
        String firstUpperName = toFirstUpper(pref.getField());
        try {
            return objectToEdit.getClass().getMethod("set" + firstUpperName, new Class<?>[] {getter.getReturnType()});
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("No setter method found on " + objectToEdit.getClass() + " for pref " + pref.getName() + ". (Getter: " + getter + ")", e);
        }
    }

}
