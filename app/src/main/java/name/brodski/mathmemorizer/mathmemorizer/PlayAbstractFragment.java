package name.brodski.mathmemorizer.mathmemorizer;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by boris on 21.11.16.
 */

public abstract class PlayAbstractFragment extends Fragment {
    public interface OnFragmentInteractionListener {
        void onAnswer(boolean correct);
        void updatePartialAnswer(String partialAnswer);
    }

    public static final String ARG_CHOICES = "choices";
    public static final String ARG_ANSWER = "answer";

    private OnFragmentInteractionListener mListener;

    public abstract void timeout();

    public static PlayAbstractFragment newInstance(PlayAbstractFragment fragment, CharSequence[] choices, int answer) {
        Bundle args = new Bundle();
        args.putCharSequenceArray(ARG_CHOICES, choices);
        args.putInt(ARG_ANSWER, answer);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    protected OnFragmentInteractionListener getListener() {
        return mListener;
    }
}
