package name.brodski.mathmemorizer.mathmemorizer;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.Arrays;

// import android.support.v7.view.GridLayoutManager;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PlayMultipleChoiceFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PlayMultipleChoiceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayMultipleChoiceFragment extends PlayAbstractFragment {
    private CharSequence[] mChoices;
    private int mAnswer;
    private boolean waitForAnswer;

    private Button mAnswerButton;

    public PlayMultipleChoiceFragment() {
        // Required empty public constructor
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
                button.setSoundEffectsEnabled(false);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!waitForAnswer) {
                            return;
                        }
                        Log.i("DEBUG", "if (index == mAnswer) -- I: " + index + ", mA: " + mAnswer);
                        if (index == mAnswer) {
                            answer(true, null);
                        } else {
                            answer(false, v);
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

        waitForAnswer = true;
    }

    private void answer(boolean correct, View wrongButton) {
        waitForAnswer = false;
        mAnswerButton.setBackgroundResource(R.color.correctAnswerButton);

        if (!correct) {
            if (wrongButton != null) {
                wrongButton.setEnabled(false);
            }
        }

        getListener().onAnswer(correct);

//        Sound.OK.play(getActivity());
//        mHandler.removeCallbacksAndMessages(null);
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (isRemoving()) {
//                    return;
//                }
//                if (mListener != null) {
//                    mListener.onAnswer(true);
//                }
//            }
//        }, mListener.getCorrectAnswerPauseMillis());
//        if (mListener != null) {
//            mListener.onStopTimer(true);
//        }
    }
    public void timeout() {
        answer(false, null);
    }

//    void wrongAnswer(View view) {
//        waitForAnswer = false;
//        Sound.ERROR.play(getActivity(), new Runnable() {
//            @Override
//            public void run() {
//                if (mListener != null) {
//                    mListener.speakAnswer();
//                }
//            }
//        });
//        mAnswerButton.setBackgroundResource(R.color.correctAnswerButton);
//        mHandler.removeCallbacksAndMessages(null);
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (isRemoving()) {
//                    return;
//                }
//                if (mListener != null) {
//                    mListener.onAnswer(false);
//                }
//            }
//        }, mListener.getWrongAnswerPauseMillis());
//        if (mListener != null) {
//            mListener.onStopTimer(false);
//        }
//    }


}
