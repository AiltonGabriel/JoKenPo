package tsi.ailton.android.jokenpo.ui.ranking;

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

import java.util.ArrayList;
import java.util.List;

import tsi.ailton.android.jokenpo.MainActivity;
import tsi.ailton.android.jokenpo.R;
import tsi.ailton.android.jokenpo.databinding.FragmentRankingBinding;
import tsi.ailton.android.jokenpo.models.Player;
import tsi.ailton.android.jokenpo.ui.jokenpo.JoKenPoViewModel;

public class RankingFragment extends Fragment {

    public static RankingFragment newInstance() {
        return new RankingFragment();
    }

    private FragmentRankingBinding binding;
    private MainActivity mainActivity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        JoKenPoViewModel homeViewModel =
                new ViewModelProvider(this).get(JoKenPoViewModel.class);

        binding = FragmentRankingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mainActivity = (MainActivity)getActivity();

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

    public void updateRankingGui(){

        List<String> items = new ArrayList<>();
        for(Player player : mainActivity.getRanking()){
            items.add(player.toString());
        }

        ListView rankingListView = binding.rankingListView;

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                mainActivity, android.R.layout.simple_list_item_1, items
        );

        rankingListView.setAdapter(adapter);

        TextView textView = binding.emptyRankingTextView;
        if(adapter.getCount() == 0){
            textView.setVisibility(View.VISIBLE);
            rankingListView.setVisibility(View.GONE);
        } else {
            rankingListView.setVisibility(View.VISIBLE);
            textView.setVisibility(View.GONE);
        }
    }

}