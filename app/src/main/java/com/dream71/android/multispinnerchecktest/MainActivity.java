package com.dream71.android.multispinnerchecktest;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    ArrayList<View> allViewInstance = new ArrayList<View>();
    JSONObject jsonObject = new JSONObject();

    private JSONObject optionsObj;

    JSONArray jsonArrayCheckList = new JSONArray();


    @BindView(R.id.customOptionLL)
    LinearLayout viewProductLayout;


    @BindView(R.id.button_product_upload)
    Button productUpload;


    @BindView(R.id.fab)
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewProductLayout.removeAllViews();
                new dynamicFieldCall().execute("http://o1bazaar.com/api/form/additionalFields?category_id=46");
            }
        });
        viewProductLayout.removeAllViews();
        new dynamicFieldCall().execute("http://o1bazaar.com/api/form/additionalFields?category_id=46");

        productUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataFromDynamicViews();
            }
        });
    }


    @SuppressLint("NewApi")
    private void loadJSONDate(JSONObject json) {
        jsonObject = json;

        try {

            JSONArray customOptnList = json.getJSONArray(Constant.FIELDS);

            for (int noOfCustomOpt = 0; noOfCustomOpt < customOptnList.length(); noOfCustomOpt++) {
                JSONObject eachData = customOptnList.getJSONObject(noOfCustomOpt);
                TextView customOptionsName = new TextView(getApplicationContext());
                customOptionsName.setTextSize(18);
                customOptionsName.setPadding(0, 15, 0, 15);
                customOptionsName.setText(eachData.getString(Constant.LABEL));
                viewProductLayout.addView(customOptionsName);


                Log.d("CHECK_DOWN", "spinner: " + eachData.getString(Constant.TYPE).equals(Constant.SPINNER));


                if (eachData.getString(Constant.TYPE).equals(Constant.CHECKBOX)) {
                    JSONArray checkBoxJSONOpt = eachData.getJSONArray(Constant.VALUES);

                    for (int j = 0; j < checkBoxJSONOpt.length(); j++) {
                        CheckBox chk = new CheckBox(MainActivity.this);

                        chk.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        allViewInstance.add(chk);

                        //Log.d("CHECK_DOWN", "check views number: " + j);
                        //Log.d("CHECK_DOWN", "check views: " + allViewInstance.get(j));


                        chk.setTag(checkBoxJSONOpt.getJSONObject(j).getString(Constant.NAME));
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.topMargin = 3;
                        params.bottomMargin = 3;
                        String optionString = checkBoxJSONOpt.getJSONObject(j).getString(Constant.NAME);

                        chk.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                String variant_name = v.getTag().toString();
                                jsonArrayCheckList.put(variant_name);
                            }
                        });

                        chk.setText(optionString);
                        viewProductLayout.addView(chk, params);
                    }

                }


                if (eachData.getString(Constant.TYPE).equals(Constant.SPINNER)) {
                    final JSONArray dropDownJSONOpt = eachData.getJSONArray(Constant.VALUES);
                    ArrayList<String> SpinnerOptions = new ArrayList<String>();

                    for (int j = 0; j < dropDownJSONOpt.length(); j++) {
                        String optionString = dropDownJSONOpt.getJSONObject(j).getString(Constant.NAME);
                        SpinnerOptions.add(optionString);
                    }

                    ArrayAdapter<String> spinnerArrayAdapter = null;
                    spinnerArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spiner_row, SpinnerOptions);

                    Spinner spinner = new Spinner(MainActivity.this);
                    allViewInstance.add(spinner);

                    spinner.setAdapter(spinnerArrayAdapter);
                    spinner.setSelection(0, false);
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                            try {
                                String variant_name = dropDownJSONOpt.getJSONObject(position).getString(Constant.NAME);
                                Toast.makeText(getApplicationContext(), variant_name + "", Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                        }

                    });
                    viewProductLayout.addView(spinner);
                }


//                    /***************************Radio*****************************************************/


                if (eachData.getString(Constant.TYPE).equals(Constant.RADIOBUTTON)) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.topMargin = 3;
                    params.bottomMargin = 3;

                    final JSONArray radioButtonJSONOpt = eachData.getJSONArray(Constant.VALUES);
                    RadioGroup rg = new RadioGroup(getApplicationContext()); //create the RadioGroup
                    allViewInstance.add(rg);


                    for (int j = 0; j < radioButtonJSONOpt.length(); j++) {

                        RadioButton rb = new RadioButton(getApplicationContext());
                        rg.addView(rb, params);
                        if (j == 0)
                            rb.setChecked(true);
                        rb.setLayoutParams(params);
                        rb.setTag(radioButtonJSONOpt.getJSONObject(j).getString(Constant.NAME));

                        rb.setBackgroundColor(Color.parseColor("#FFFFFF"));

                        String optionString = radioButtonJSONOpt.getJSONObject(j).getString(Constant.NAME);
                        rb.setText(optionString);


                        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {

                                View radioButton = group.findViewById(checkedId);
                                String variant_name = radioButton.getTag().toString();
                                Toast.makeText(getApplicationContext(), variant_name + "", Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                    viewProductLayout.addView(rg, params);
                }

//                    /***********************************CheckBox ***********************************************/


                if (eachData.getString(Constant.TYPE).equals(Constant.EDITTEXT)) {
                    //TextInputLayout til = new TextInputLayout(getActivity());
                    //til.setHint(""+eachData.getString(Constant.LABEL));
                    EditText et = new EditText(getApplicationContext());
                    et.setHint("" + eachData.getString(Constant.LABEL));
                    et.setPadding(20, 30, 0, 30);
                    et.setText("" + eachData.getString(Constant.VALUES));

                    // Initialize a new GradientDrawable instance
                    GradientDrawable gd = new GradientDrawable();
                    gd.setColor(Color.parseColor("#ffffff"));
                    gd.setCornerRadius(3);

                    gd.setStroke(2, getResources().getColor(R.color.colorAccent));
                    et.setBackground(gd);


                    //til.addView(et);
                    allViewInstance.add(et);
                    viewProductLayout.addView(et);
                }

                if (eachData.getString(Constant.TYPE).equals(Constant.TEXT_AREA)) {
                    //TextInputLayout til = new TextInputLayout(getActivity());
                    //til.setHint(""+eachData.getString(Constant.LABEL));
                    EditText et = new EditText(getApplicationContext());
                    et.setHint("" + eachData.getString(Constant.LABEL));
                    et.setText("" + eachData.getString(Constant.VALUES));
                    et.setPadding(20, 40, 0, 40);
                    //til.addView(et);

                    // Initialize a new GradientDrawable instance
                    GradientDrawable gd = new GradientDrawable();
                    gd.setColor(Color.parseColor("#ffffff"));
                    gd.setCornerRadius(3);

                    gd.setStroke(2, getResources().getColor(R.color.colorAccent));
                    et.setBackground(gd);


                    allViewInstance.add(et);
                    viewProductLayout.addView(et);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getDataFromDynamicViews() {

        String outputData = null;

        try {
            JSONArray customOptnList = jsonObject.getJSONArray(Constant.FIELDS);
            optionsObj = new JSONObject();

            int total = allViewInstance.size();

            Log.d("CHECK_DOWN", "child count : " + total);


            for (int i =0 ;i<=4;i++){
                Log.d("CHECK_DOWN", "single view: " + i);
                Log.d("CHECK_DOWN", "single view: " + allViewInstance.get(i).toString());

                JSONObject eachData = customOptnList.getJSONObject(i);
            }
            /*
            for (int noOfViews = 0; noOfViews < allViewInstance.size(); noOfViews++) {

                //Log.d("CHECK_DOWN", "view count: " + noOfViews);
                //Log.d("CHECK_DOWN", "single view: " + allViewInstance.get(0).toString());



                JSONObject eachData = customOptnList.getJSONObject(noOfViews);

                if (eachData.getString(Constant.TYPE).equals(Constant.SPINNER)) {
                    Spinner spinner = (Spinner) allViewInstance.get(noOfViews);
                    JSONArray dropDownJSONOpt = eachData.getJSONArray(Constant.VALUES);

                    String variant_name = dropDownJSONOpt.getJSONObject(spinner.getSelectedItemPosition()).getString(Constant.NAME);

                    Log.d(Constant.NAME, variant_name + "");
                    optionsObj.put(eachData.getString(Constant.OPTION_NAME),
                            "" + variant_name);
                }

                if (eachData.getString(Constant.TYPE).equals(Constant.RADIOBUTTON)) {
                    RadioGroup radioGroup = (RadioGroup) allViewInstance.get(noOfViews);
                    RadioButton selectedRadioBtn = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
                    Log.d(Constant.NAME, selectedRadioBtn.getTag().toString() + "");
                    optionsObj.put(eachData.getString(Constant.OPTION_NAME),
                            "" + selectedRadioBtn.getTag().toString());
                }

                if (eachData.getString(Constant.TYPE).equals(Constant.CHECKBOX)) {
                    CheckBox tempChkBox = (CheckBox) allViewInstance.get(noOfViews);

                    //if (tempChkBox.isChecked()) {
                    //optionsObj.put(eachData.getString(Constant.OPTION_NAME), tempChkBox.getTag().toString());
                    //}


                    optionsObj.put("" + eachData.getString(Constant.OPTION_NAME), jsonArrayCheckList);

                    Log.d(Constant.NAME, tempChkBox.getTag().toString() + "");
                }


                if (eachData.getString(Constant.TYPE).equals(Constant.EDITTEXT)) {
                    TextView textView = (TextView) allViewInstance.get(noOfViews);
                    if (!textView.getText().toString().equalsIgnoreCase(""))
                        optionsObj.put(eachData.getString(Constant.OPTION_NAME), textView.getText().toString());
                    else
                        optionsObj.put(eachData.getString(Constant.OPTION_NAME), textView.getText().toString());
                    Log.d(Constant.NAME, textView.getText().toString() + "");
                }


                if (eachData.getString(Constant.TYPE).equals(Constant.TEXT_AREA)) {
                    TextView textView = (TextView) allViewInstance.get(noOfViews);
                    if (!textView.getText().toString().equalsIgnoreCase(""))
                        optionsObj.put(eachData.getString(Constant.OPTION_NAME), textView.getText().toString());
                    else
                        optionsObj.put(eachData.getString(Constant.OPTION_NAME), textView.getText().toString());
                    Log.d(Constant.NAME, textView.getText().toString() + "");
                }


            }
            */


            outputData = (optionsObj + "").replace(",", "\n");
            outputData = outputData.replaceAll("[{}]", "");
            //((TextView) getActivity().findViewById(R.id.showData)).setText(outputData);
            Log.d("optionsObj", optionsObj + "");


            hideSoftKeyboard(findViewById(R.id.layout));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return optionsObj;
    }

    public void hideSoftKeyboard(View v) {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public class dynamicFieldCall extends AsyncTask<String, JSONObject, JSONObject> {

        JSONObject jsonObj;

        @Override
        protected JSONObject doInBackground(String... strings) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = strings[0];
            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {
                try {
                    jsonObj = new JSONObject(jsonStr);

                } catch (final JSONException e) {
                }

            }
            return jsonObj;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            Log.d("JSON_RESPONSE", "" + jsonObject.toString());
            loadJSONDate(jsonObject);
        }
    }
}
