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
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).updateJoKenPoGuiInfo();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}