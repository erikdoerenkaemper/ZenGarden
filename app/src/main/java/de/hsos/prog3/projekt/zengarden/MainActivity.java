package de.hsos.prog3.projekt.zengarden;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.GridLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import de.hsos.prog3.projekt.zengarden.view.GartenView;
import de.hsos.prog3.projekt.zengarden.viewmodel.GartenViewModel;

public class MainActivity extends AppCompatActivity {

    private GartenViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.root);

        GartenViewModel viewModel = new ViewModelProvider(this).get(GartenViewModel.class);

        initialisiereViews();
    }

    private void initialisiereViews(){
        GartenView gartenView = new GartenView(viewModel);

        GridLayout gartengrid = findViewById(R.id.gartengrid);

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 3; j++) {
                LayoutInflater.from(this).inflate(R.layout.topf_mit_pflanze, gartengrid, true);
            }
        }
    }
}