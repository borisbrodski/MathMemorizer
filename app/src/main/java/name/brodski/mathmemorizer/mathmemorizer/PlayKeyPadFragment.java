package name.brodski.mathmemorizer.mathmemorizer;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.Arrays;

/**
 * Created by boris on 22.11.16.
 */
public class PlayKeyPadFragment extends PlayAbstractFragment {
    private CharSequence mAnswer;
    private String mPartialAnswer;
    private boolean mWaitForAnswer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            CharSequence[] choices = getArguments().getCharSequenceArray(ARG_CHOICES);
            int answerIndex = getArguments().getInt(ARG_ANSWER);
            mAnswer = choices[answerIndex];
            mPartialAnswer = "";
            if (mAnswer.length() == 0) {
                throw new RuntimeException("answer is empty");
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play_keypad, container, false);
        createButtons(view);
        return view;
    }


    public void createButtons(View view) {
        TableLayout layout = (TableLayout) view.findViewById(R.id.play_keypad_tablelayout);

        final float scale = getContext().getResources().getDisplayMetrics().density;
        int marginPx = (int) (15 * scale + 0.5f);


        boolean zeroBottom = getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE;

        // layout.removeAllViews();
        for (int row = 0; row < 4; row++) {
            TableRow tableRow = new TableRow(getActivity());
            TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
            tableParams.weight = 1.0f;
            tableRow.setLayoutParams(tableParams);
            if (row < 3) {
                for (int column = 0; column < 3; column++) {
                    String text = "" + (1 + column + (2 - row) * 3);
                    createButton(marginPx, tableRow, text);
                }
            } else {
                if (zeroBottom) {
                    Button emptyButton = createButton(marginPx, tableRow, "");
                    emptyButton.setVisibility(View.INVISIBLE);
                    createButton(marginPx, tableRow, "0");
                }
            }
            if (row == 0 && !zeroBottom) {
                createButton(marginPx, tableRow, "0");
            }
            layout.addView(tableRow);
        }

        mWaitForAnswer = true;
    }

    private Button createButton(int marginPx, TableRow tableRow, final String text) {
        Button button = new Button(getActivity());
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(marginPx, marginPx, marginPx, marginPx);
        button.setText(text);
        button.setSoundEffectsEnabled(false);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mWaitForAnswer) {
                    return;
                }
                buttonPressed(text);
            }
        });
        tableRow.addView(button, layoutParams);
        return button;
    }

    public void buttonPressed(String text) {
        char expectedChar = mAnswer.charAt(mPartialAnswer.length());
        if (expectedChar != text.charAt(0)) {
            getListener().onAnswer(false);
            return;
        }
        mPartialAnswer += text;
        getListener().updatePartialAnswer(mPartialAnswer);
        if (mAnswer.length() <= mPartialAnswer.length()) {
            getListener().onAnswer(true);
        }
    }

    @Override
    public void timeout() {
        mWaitForAnswer = false;
    }
}
