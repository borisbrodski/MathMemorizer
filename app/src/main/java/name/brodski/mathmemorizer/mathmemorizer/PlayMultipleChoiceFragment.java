package name.brodski.mathmemorizer.mathmemorizer;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
    private static final String ARG_ANSWER = "answer";
    private Handler mHandler = new Handler();
    private CharSequence[] mChoices;
    private int mAnswer;

    private OnFragmentInteractionListener mListener;
    private Button mAnswerButton;

    public PlayMultipleChoiceFragment() {
        // Required empty public constructor
    }

    public static PlayMultipleChoiceFragment newInstance(CharSequence[] choices, int answer) {
        PlayMultipleChoiceFragment fragment = new PlayMultipleChoiceFragment();
        Bundle args = new Bundle();
        args.putCharSequenceArray(ARG_CHOICES, choices);
        args.putInt(ARG_ANSWER, answer);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mChoices = getArguments().getCharSequenceArray(ARG_CHOICES);
            mAnswer = getArguments().getInt(ARG_ANSWER);
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
                final int index = column + row * columns;
                button.setText(mChoices[index]);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (index == mAnswer) {
                            correctAnswer();
                        } else {
                            wrongAnswer(v);
                        }
                    }
                });
                if (index == mAnswer) {
                    mAnswerButton = button;
                }
                tableRow.addView(button, layoutParams);
            }
            layout.addView(tableRow);
        }
    }

    private void correctAnswer() {
        Sound.OK.play(getActivity());
        mAnswerButton.setBackgroundResource(R.color.correctAnswerButton);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isRemoving()) {
                    return;
                }
                if (mListener != null) {
                    mListener.onAnswer(true);
                }
            }
        }, 500);
        if (mListener != null) {
            mListener.onStopTimer(true);
        }
    }
    void wrongAnswer(View view) {
        Sound.ERROR.play(getActivity());
        mAnswerButton.setBackgroundResource(R.color.correctAnswerButton);
        if (view != null) {
            view.setEnabled(false);
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isRemoving()) {
                    return;
                }
                if (mListener != null) {
                    mListener.onAnswer(false);
                }
            }
        }, 6000);
        if (mListener != null) {
            mListener.onStopTimer(false);
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

    public interface OnFragmentInteractionListener {
        void onAnswer(boolean correct);
        void onStopTimer(boolean correct);
    }
}
