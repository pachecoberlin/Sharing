package de.pacheco.sharing;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class RecommendationDialog extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recommendation_dialog, container, false);
//        ((TextView) view.findViewById(R.id.dialog_title)).setText("You got a Link Recommendation");
        ((TextView) view.findViewById(R.id.dialog_title)).setText("Driver recommends to watch Big Buck Bunny");
        ImageButton positiveButton = view.findViewById(R.id.positive_button);
        ImageButton negativeButton = view.findViewById(R.id.negative_button);

        positiveButton.setOnClickListener(v -> {
            String rrPackageName = "com.example.android.cars.roadreels";
            Intent intent = new Intent(Intent.ACTION_VIEW)
                    .setClassName(rrPackageName, rrPackageName + ".MainActivity")
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    .putExtra("detail", true);
            startActivity(intent);
            dismiss();
        });

        negativeButton.setOnClickListener(v -> {
            // Negative Button Clicked
            dismiss();
        });

        return view;
    }
}
