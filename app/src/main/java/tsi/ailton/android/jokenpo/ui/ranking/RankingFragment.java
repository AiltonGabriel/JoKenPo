package tsi.ailton.android.jokenpo.ui.ranking;

import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import tsi.ailton.android.jokenpo.MainActivity;
import tsi.ailton.android.jokenpo.databinding.FragmentRankingBinding;
import tsi.ailton.android.jokenpo.models.Player;
import tsi.ailton.android.jokenpo.ui.jokenpo.JoKenPoViewModel;

public class RankingFragment extends Fragment {

    public static RankingFragment newInstance() {
        return new RankingFragment();
    }

    private FragmentRankingBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        JoKenPoViewModel homeViewModel =
                new ViewModelProvider(this).get(JoKenPoViewModel.class);

        binding = FragmentRankingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity mainActivity =  (MainActivity)getActivity();

        mainActivity.updateRanking();
//        Player player = mainActivity.getPlayer();
//
//        if(player != null){
//            AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity.getApplicationContext());
//            builder.setTitle("Digite seu nome para adicionar seu resultado ao ranking:");
//
//            final EditText input = new EditText(getActivity().getApplicationContext());
//            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
//            builder.setView(input);
//
//            builder.setPositiveButton("Adicionar", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    player.setNome(input.getText().toString());
//                    mainActivity.addPlayerToRanking(player);
//                }
//            });
//            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.cancel();
//                }
//            });
//
//            builder.show();
//        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}