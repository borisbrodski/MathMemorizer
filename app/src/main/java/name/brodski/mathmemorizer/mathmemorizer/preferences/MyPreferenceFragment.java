package name.brodski.mathmemorizer.mathmemorizer.preferences;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.preference.PreferenceFragment;
import android.view.MenuItem;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by boris on 15.10.16.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public abstract class MyPreferenceFragment extends PreferenceFragment {
    protected static final BindPreferenceSummaryToValueListener BIND_PREFERENCE_SUMMARY_TO_VALUE_LISTENER = new BindPreferenceSummaryToValueListener();

    protected abstract Pref[] getPrefs();

    public void initFromResource(int preferencesResId) {
        addPreferencesFromResource(preferencesResId);

        for (Pref pref : getPrefs()) {
            if (pref.isSummaryUpdateRequired()) {
                BIND_PREFERENCE_SUMMARY_TO_VALUE_LISTENER.bind(findPreference(pref.getName()));
            }
        }
        setHasOptionsMenu(true);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
