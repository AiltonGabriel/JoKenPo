package tsi.ailton.android.jokenpo.ui.jokenpo;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tsi.ailton.android.jokenpo.MainActivity;
import tsi.ailton.android.jokenpo.databinding.FragmentJoKenPoBinding;

public class JoKenPoFragment extends Fragment {

    private FragmentJoKenPoBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        JoKenPoViewModel homeViewModel =
                new ViewModelProvider(this).get(JoKenPoViewModel.class);

        binding = FragmentJoKenPoBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity mainActivity = (MainActivity)getActivity();
        if (mainActivity != null) {
//            if (mainActivity.isGameFinished())
//                mainActivity.resetScoreboard(binding.getRoot());
//            else
//                mainActivity.updateScoreboardGui(binding.getRoot());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}