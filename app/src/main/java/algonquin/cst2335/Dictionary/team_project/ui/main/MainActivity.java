package algonquin.cst2335.Dictionary.team_project.ui.main;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import algonquin.cst2335.Dictionary.team_project.ui.adapter.FavouritesAdapter;
import algonquin.cst2335.Dictionary.team_project.util.WordResponseListener;
import algonquin.cst2335.Dictionary.team_project.viewmodel.MainViewModel;
import algonquin.cst2335.Dictionary.team_project.wordsapi.Word;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import algonquin.cst2335.Dictionary.team_project.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private static final String TAG = "MainActivity";
    private MainViewModel mViewModel;
    private FavouritesAdapter mFavAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View viewRoot = binding.getRoot();
        setContentView(viewRoot);
        try {
            initValues();
        } catch(Exception e){
            Log.e(TAG,e.toString());
        }
    }
    private void initValues(){
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mViewModel.init(getApplication());
        createSearchBarEvent();
        createFavouritesRV();
        binding.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Word> listW = mFavAdapter.getSelectedWords();
                if (listW.size() > 0){
                    mViewModel.removeListofWords(listW, new OnRemoveSelectedWords() {
                        @Override
                        public void clear(boolean result) {
                            if(result){
                                mFavAdapter.clearSelectedWords();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(),"Please select word you want to remove", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    void createFavouritesRV(){
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getApplicationContext());
        binding.rvFavourites.setLayoutManager(layoutManager);
        mFavAdapter = new FavouritesAdapter(getApplicationContext(),
                new FavouritesAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Word item) {
                        openDetailActivity(item, true);
                    }
                });
        binding.rvFavourites.setAdapter(mFavAdapter);
        mViewModel.getFavWords().observe(this, new Observer<List<Word>>() {
            @Override
            public void onChanged(List<Word> words) {
                if(words != null){
                    mFavAdapter.setFavWordsList(words);
                } else {
                    Log.d(TAG, "words == null");
                }
            }
        });
    }
    void createSearchBarEvent(){
        binding.svSearchbar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Single<Word> w = mViewModel.findWord(query);
                w.subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<Word>() {
                            @Override
                            public void onSuccess(@NonNull Word word) {
                                if (word != null){
                                    openDetailActivity(word, true);
                                } else {
                                    Log.e(TAG, "onSuccess " + query);
                                }
                            }
                            @Override
                            public void onError(@NonNull Throwable e) {
                                if (isConnected()){
                                    getWordFromApi(query);
                                } else {
                                    Toast.makeText(getApplicationContext(),"Please check your internet connection and try again", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                mFavAdapter.filter(newText);
                return true;
            }
        });
    }
    void openDetailActivity(Word word, Boolean isFav){
        Gson gson = new Gson();
        String myJson = gson.toJson(word);
        Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
        intent.putExtra("wordItem", myJson);
        if (isFav){
            intent.putExtra("favourite", true);
        }
        startActivity(intent);
    }
    void getWordFromApi(String text){
        mViewModel.getWord(text, new WordResponseListener(){
            @Override
            public void onSuccess(Word word) {
                if (word.getResults() != null){
                    try {
                        openDetailActivity(word, false);
                    } catch(Exception e){
                        Log.e(TAG, e.toString());
                    }
                } else {
                    Toast.makeText(getApplicationContext(),"Word not found", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFail(String s) {
                Log.d(TAG,s);
                Toast.makeText(getApplicationContext(),"Word not found", Toast.LENGTH_LONG).show();
            }
            @Override
            public void onError(Throwable t) {
                Log.e(TAG,t.getMessage());
                Toast.makeText(getApplicationContext(),"Error " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    //    Check for internet connection
    public boolean isConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public interface OnRemoveSelectedWords{
        public void clear(boolean result);
    }

    }

}