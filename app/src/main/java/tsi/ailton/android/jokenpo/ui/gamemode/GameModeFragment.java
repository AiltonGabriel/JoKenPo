package tsi.ailton.android.jokenpo.ui.gamemode;

import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tsi.ailton.android.jokenpo.MainActivity;
import tsi.ailton.android.jokenpo.R;
import tsi.ailton.android.jokenpo.databinding.FragmentGameModeBinding;

public class GameModeFragment extends Fragment {

    public static GameModeFragment newInstance() {
        return new GameModeFragment();
    }

    private FragmentGameModeBinding binding;
    private MainActivity mainActivity;

    @SuppressLint("NonConstantResourceId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        GameModeViewModel homeViewModel =
                new ViewModelProvider(this).get(GameModeViewModel.class);

        binding = FragmentGameModeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mainActivity = (MainActivity)getActivity();

        binding.gameModeRadioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (i){
                case R.id.random_game_mode_radioButton:
                    mainActivity.setGame_mode(MainActivity.GAME_MODE.RANDOM);
                    break;
                case R.id.only_rock_game_mode_radioButton:
                    mainActivity.setGame_mode(MainActivity.GAME_MODE.ONLY_ROCK);
                    break;
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(mainActivity.getGame_mode() == MainActivity.GAME_MODE.RANDOM){
            binding.randomGameModeRadioButton.setChecked(true);
        } else if(mainActivity.getGame_mode() == MainActivity.GAME_MODE.ONLY_ROCK){
            binding.onlyRockGameModeRadioButton.setChecked(true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}