package name.brodski.mathmemorizer.mathmemorizer.preferences;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by boris on 15.10.16.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public abstract class MyEditingPreferenceFragment extends MyPreferenceFragment {
    protected abstract Object geObjectToEdit();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Object objectToEdit = geObjectToEdit();
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
        super.onCreate(savedInstanceState);
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
                } if (value.getClass() == Long.class) {
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
        String name = pref.getField();
        String firstUpperName = name.substring(0, 1).toUpperCase() + name.substring(1);
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

}
