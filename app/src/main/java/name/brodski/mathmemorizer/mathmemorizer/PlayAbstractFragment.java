package name.brodski.mathmemorizer.mathmemorizer;

import android.support.v4.app.Fragment;

/**
 * Created by boris on 21.11.16.
 */

public abstract class PlayAbstractFragment extends Fragment {
    public interface OnFragmentInteractionListener {
        void onAnswer(boolean correct);
    }

    public abstract void timeout();
}
