package name.brodski.mathmemorizer.mathmemorizer;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
// import android.support.v7.view.GridLayoutManager;
import android.widget.GridLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PlayMultipleChoiceFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PlayMultipleChoiceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayMultipleChoiceFragment extends Fragment {
    private static final String ARG_CHOICES = "choices";

    private CharSequence[] mChoices;

    private OnFragmentInteractionListener mListener;

    public PlayMultipleChoiceFragment() {
        // Required empty public constructor
    }

    public static PlayMultipleChoiceFragment newInstance(CharSequence[] choices) {
        PlayMultipleChoiceFragment fragment = new PlayMultipleChoiceFragment();
        Bundle args = new Bundle();
        args.putCharSequenceArray(ARG_CHOICES, choices);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mChoices = getArguments().getCharSequenceArray(ARG_CHOICES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play_multiple_choice, container, false);
        createButtons(view);
        return view;
    }

    public void createButtons(View view) {
        int rows;
        int columns;
        switch (mChoices.length) {
            case 4:
                rows = 2;
                columns = 2;
                break;
            case 6:
                rows = 3;
                columns = 2;
                break;
            case 9:
                rows = 3;
                columns = 3;
                break;
            default:
                throw new RuntimeException("Unsupported amount of choices: " + mChoices.length + " (" + Arrays.toString(mChoices) + ")");
        }

        TableLayout layout = (TableLayout) view.findViewById(R.id.play_multiple_choice_tablelayout);

        final float scale = getContext().getResources().getDisplayMetrics().density;
        int marginPx = (int) (15 * scale + 0.5f);


        // layout.removeAllViews();
        for (int row = 0; row < rows; row++) {
            TableRow tableRow = new TableRow(getActivity());
            TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
            tableParams.weight = 1.0f;
            tableRow.setLayoutParams(tableParams);
            for (int column = 0; column < columns; column++) {
                Button button = new Button(getActivity());
                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                layoutParams.setMargins(marginPx, marginPx, marginPx, marginPx);
                button.setText(mChoices[column + row * columns]);
                tableRow.addView(button, layoutParams);
            }
            layout.addView(tableRow);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
