package com.example.diane.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private SQLiteDatabase mDatabase;
    private GroceryAdapter mAdapter;
    private EditText mEditTextName;
    private TextView mTextViewAmount;
    private int mAmount = 0;
    Context context = this;
    private Button button;
    private  EditText editTextList;
    public static final String EXTRA_TEXT="com.example.diane.myapplication.EXTRA_TEXT";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GroceryDBHelper dbHelper = new GroceryDBHelper(context);
        mDatabase = dbHelper.getWritableDatabase();

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter= new GroceryAdapter(context, getAllItems());
        recyclerView.setAdapter(mAdapter);

        // pour supprimer les éléments en swipant
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                removeItem((long) viewHolder.itemView.getTag());
            }
        }).attachToRecyclerView(recyclerView);

        mEditTextName = findViewById(R.id.edittext_name);
        mTextViewAmount = findViewById(R.id.textview_amount);

        Button buttonIncrease = findViewById(R.id.button_increase);
        Button buttonDecrease = findViewById(R.id.button_decrease);
        Button buttonAdd = findViewById(R.id.button_add);

        buttonIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increase();
            }
        });

        buttonDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrease();

            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });

        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity2();
            }
        });



        editTextList = findViewById(R.id.edittext_list);
        editTextList.addTextChangedListener(typeTextWatcher);

    }

    private TextWatcher typeTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { // permet de changer l'apparence du bouton lorqu'on écrit dans le champ
            String namelistInput = editTextList.getText().toString().trim(); // enlève les éventuels espaces

            // s'il y a du texte dans le champ alors le bouton "suivant" pour passer à l'activité 2 est actif, sinon il est inactif
            button.setEnabled(!namelistInput.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    private void increase(){
        mAmount++;
        mTextViewAmount.setText(String.valueOf(mAmount));
    }
    private void decrease() {
        if (mAmount > 0) {
            mAmount--;
            mTextViewAmount.setText(String.valueOf(mAmount));
        }
    }
    private void addItem(){

        if (mEditTextName.getText().toString().trim().length() ==0 || mAmount == 0){ // trim enlève l'espace au début et à la fin de l'édit text
            return;
        }

        String name = mEditTextName.getText().toString();
        ContentValues cv = new ContentValues();
        cv.put(GroceryContract.GroceryEntry.COLUMN_NAME, name);
        cv.put(GroceryContract.GroceryEntry.COLUMN_AMOUNT, mAmount);
        // pour colonnes et timestamp c'est fait automatiquement

        mDatabase.insert(GroceryContract.GroceryEntry.TABLE_NAME, null, cv);
        mAdapter.swapCursor(getAllItems());

        mEditTextName.getText().clear();
    }

    private void removeItem(long id){
        mDatabase.delete(GroceryContract.GroceryEntry.TABLE_NAME,
                GroceryContract.GroceryEntry._ID + "=" + id, null);
        mAdapter.swapCursor(getAllItems());
    }

    // méthode qui retourne le curseur

    private Cursor getAllItems(){
        return mDatabase.query(
                GroceryContract.GroceryEntry.TABLE_NAME,
                null, // 5 arguments passés en null parce qu'on n'en a pas besoin ici
                null,
                null,
                null,
                null,
                GroceryContract.GroceryEntry.COLUMN_TIMESTAMP + " DESC" // éléments nouvellement ajoutés sont en haut de la liste
        );
    }

    public void openActivity2() { // méthode qui permet de passer à la page 2
        EditText editTextList = (EditText)findViewById(R.id.edittext_list);
        String text = editTextList.getText().toString();


        Intent intent = new Intent(this, Activity2.class);
        intent.putExtra(EXTRA_TEXT, text); // on passe la variable nom de la liste à l'activité 2
        startActivity(intent);
    }
}
