package tsi.ailton.android.jokenpo.ui.ranking;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tsi.ailton.android.jokenpo.MainActivity;
import tsi.ailton.android.jokenpo.databinding.FragmentRankingBinding;
import tsi.ailton.android.jokenpo.models.RankingItem;
import tsi.ailton.android.jokenpo.ui.jokenpo.JoKenPoViewModel;

public class RankingFragment extends Fragment {

    public static RankingFragment newInstance() {
        return new RankingFragment();
    }

    private FragmentRankingBinding binding;
    private MainActivity mainActivity;
    private FirebaseFirestore firestore;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        JoKenPoViewModel homeViewModel =
                new ViewModelProvider(this).get(JoKenPoViewModel.class);

        binding = FragmentRankingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mainActivity = (MainActivity)getActivity();

        firestore = FirebaseFirestore.getInstance();

        binding.clearRankingButton.setOnClickListener(view -> clearRanking());

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateRankingGui();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void clearRanking(){
        firestore.collection("ranking")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            document.getReference().delete();
                        }
                        updateRankingGui();
                    }
                });
    }

    public void updateRankingGui(){
        List<RankingItem> ranking = new ArrayList<>();

        firestore.collection("ranking")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            ranking.add(document.toObject(RankingItem.class));
                        }

                        Collections.sort(ranking);

                        List<String> items = new ArrayList<>();
                        for(RankingItem rankingItem : ranking){
                            items.add(rankingItem.toString());
                        }

                        ListView rankingListView = binding.rankingListView;

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                mainActivity, android.R.layout.simple_list_item_1, items
                        );

                        rankingListView.setAdapter(adapter);

                        TextView textView = binding.emptyRankingTextView;
                        ConstraintLayout rankingConstraintLayout = binding.rankingConstraintLayout;
                        if(adapter.getCount() == 0){
                            textView.setVisibility(View.VISIBLE);
                            rankingConstraintLayout.setVisibility(View.GONE);
                        } else {
                            rankingConstraintLayout.setVisibility(View.VISIBLE);
                            textView.setVisibility(View.GONE);
                        }
                    }
                });
    }

}