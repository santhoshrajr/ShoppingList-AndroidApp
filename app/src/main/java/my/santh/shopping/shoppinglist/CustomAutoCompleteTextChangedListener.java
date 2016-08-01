package my.santh.shopping.shoppinglist;


import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
/**
 * CustomAutoCompleteTextChangedListener is used to get the Products from DB when a character has been changed by user.
 */

public class CustomAutoCompleteTextChangedListener implements TextWatcher{

    public static final String TAG = "CustomAutoCompleteTextChangedListener.java";
    Context context;

    public CustomAutoCompleteTextChangedListener(Context context){
        this.context = context;
    }

    @Override
    public void afterTextChanged(Editable s) {
        // TODO Auto-generated method stub

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
        // TODO Auto-generated method stub

    }

    /**
     * Text Changed Method is called whenever user enters a new character in autocomplete field
     * @param userInput
     * @param start
     * @param before
     * @param count
     */
    @Override
    public void onTextChanged(CharSequence userInput, int start, int before, int count) {

        // if you want to see in the logcat what the user types
       // Log.e(TAG, "User input: " + userInput);

        ItemActivity mainActivity = ((ItemActivity) context);

        // query the database based on the user input
       /* if(userInput.toString().contains("'"))
        {
            userInput=userInput.toString().replaceAll("[']", "\'");
        }*/
        mainActivity.item = mainActivity.getItemsFromDb(userInput.toString().replaceAll("[']","''"));

        // update the adapater
        mainActivity.myAdapter.notifyDataSetChanged();
        mainActivity.myAdapter = new ArrayAdapter<String>(mainActivity, android.R.layout.simple_dropdown_item_1line, mainActivity.item);
        mainActivity.myAutoComplete.setAdapter(mainActivity.myAdapter);

    }

}
