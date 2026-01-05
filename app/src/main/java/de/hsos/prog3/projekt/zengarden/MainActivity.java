package de.hsos.prog3.projekt.zengarden;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import de.hsos.prog3.projekt.zengarden.viewmodel.GartenViewModel;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.root);

        GartenViewModel viewModel = new ViewModelProvider(this).get(GartenViewModel.class);

        initialisiereViews();
    }

    private void initialisiereViews(){}
}