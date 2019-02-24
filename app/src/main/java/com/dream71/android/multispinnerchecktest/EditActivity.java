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
import android.view.ViewGroup;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditActivity extends AppCompatActivity {

    ArrayList<View> allViewInstance = new ArrayList<View>();
    JSONObject jsonObject = new JSONObject();

    List<String> selectedCheckList;

    private JSONObject optionsObj;


    @BindView(R.id.customOptionLL)
    LinearLayout viewProductLayout;

    @BindView(R.id.button_product_upload)
    Button productUpload;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    Map<String, List<String>> checkMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        selectedCheckList = new ArrayList<>();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewProductLayout.removeAllViews();
                new dynamicFieldCall().execute("http://o1bazaar.com/api/product/edit?id=249");
            }
        });


        viewProductLayout.removeAllViews();
        new dynamicFieldCall().execute("http://o1bazaar.com/api/product/edit?id=249");

        productUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataFromDynamicViews();
            }
        });


    }


    public JSONObject getDataFromDynamicViews() {

        String outputData = null;
        try {
            JSONArray customOptnList = jsonObject.getJSONArray(Constant.FIELDS);
            optionsObj = new JSONObject();


            for (int noOfViews = 0; noOfViews < customOptnList.length(); noOfViews++) {
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

                    optionsObj.put(eachData.getString(Constant.OPTION_NAME),
                            "" + selectedRadioBtn.getTag().toString());
                }

                if (eachData.getString(Constant.TYPE).equals(Constant.CHECKBOX)) {
                    CheckBox tempChkBox = (CheckBox) allViewInstance.get(noOfViews);

                    //if (tempChkBox.isChecked()) {
                    //optionsObj.put(eachData.getString(Constant.OPTION_NAME), tempChkBox.getTag().toString());
                    //}

                    JSONArray jsonArrayCheckList = new JSONArray();
                    for (String value : selectedCheckList) {
                        jsonArrayCheckList.put(value);
                    }

                    Log.d("CHECK_MAP", "" + checkMap.toString());

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

            outputData = (optionsObj + "").replace(",", "\n");
            outputData = outputData.replaceAll("[{}]", "");
            //((TextView) getActivity().findViewById(R.id.showData)).setText(outputData);
            Log.d("optionsObj", optionsObj + "");


        } catch (Exception e) {
            e.printStackTrace();
        }

        return optionsObj;
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


                if (eachData.getString(Constant.TYPE).equals(Constant.SPINNER)) {
                    final JSONArray dropDownJSONOpt = eachData.getJSONArray(Constant.VALUES);
                    ArrayList<String> SpinnerOptions = new ArrayList<String>();

                    String selectedValue = eachData.getString(Constant.SELECTED_VALUE);
                    int position = 0;

                    for (int j = 0; j < dropDownJSONOpt.length(); j++) {
                        if (dropDownJSONOpt.getJSONObject(j).getString(Constant.NAME).contains(selectedValue)) {
                            position = j;
                        }
                    }


                    for (int j = 0; j < dropDownJSONOpt.length(); j++) {
                        String optionString = dropDownJSONOpt.getJSONObject(j).getString(Constant.NAME);
                        SpinnerOptions.add(optionString);
                    }

                    ArrayAdapter<String> spinnerArrayAdapter = null;
                    spinnerArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spiner_row, SpinnerOptions);
                    Spinner spinner = new Spinner(getApplicationContext());
                    allViewInstance.add(spinner);


                    spinner.setAdapter(spinnerArrayAdapter);
                    spinner.setSelection(position, false);
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                            try {
                                String variant_name = dropDownJSONOpt.getJSONObject(position).getString(Constant.NAME);
                                //Toast.makeText(getActivity(), variant_name + "", Toast.LENGTH_LONG).show();
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

                    String selectedValue = eachData.getString(Constant.SELECTED_VALUE);
                    int position = 0;


                    for (int j = 0; j < radioButtonJSONOpt.length(); j++) {
                        if (radioButtonJSONOpt.getJSONObject(j).getString(Constant.NAME).contains(selectedValue)) {
                            position = j;
                        }
                    }

                    for (int j = 0; j < radioButtonJSONOpt.length(); j++) {

                        RadioButton rb = new RadioButton(getApplicationContext());
                        rg.addView(rb, params);


                        if (j == position)
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
                                //String variant_name = radioButton.getTag().toString();
                                //Toast.makeText(getApplicationContext(), variant_name + "", Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                    viewProductLayout.addView(rg, params);
                }

//                    /***********************************CheckBox ***********************************************/

                if (eachData.getString(Constant.TYPE).equals(Constant.CHECKBOX)) {
                    JSONArray checkBoxJSONOpt = eachData.getJSONArray(Constant.VALUES);

                    CheckBox chk = null;

                    for (int j = 0; j < checkBoxJSONOpt.length(); j++) {

                        chk = new CheckBox(getApplicationContext());
                        chk.setBackgroundColor(Color.parseColor("#FFFFFF"));

                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.topMargin = 3;
                        params.bottomMargin = 3;
                        final String optionString = checkBoxJSONOpt.getJSONObject(j).getString(Constant.NAME);

                        if (checkBoxJSONOpt.getJSONObject(j).getString(Constant.CHECKED).contains("true")) {
                            String options = checkBoxJSONOpt.getJSONObject(j).getString(Constant.NAME);
                            selectedCheckList.add(options);
                            chk.setChecked(true);
                        }

                        final int k = j;
                        final CheckBox finalChk = chk;
                        chk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (finalChk.isChecked()) {
                                    String variant_name = v.getTag().toString();
                                    selectedCheckList.add(variant_name);
                                } else {
                                    String variant_name = v.getTag().toString();
                                    for (int i = 0; i < selectedCheckList.size(); i++) {
                                        String value = selectedCheckList.get(i);
                                        if (value.equals(variant_name)) {
                                            selectedCheckList.remove(value);
                                        }
                                    }
                                }
                                checkMap.put(optionString, selectedCheckList);
                            }
                        });

                        chk.setText(optionString);
                        viewProductLayout.addView(chk, params);
                    }

                    allViewInstance.add(chk);
                    //}
                }

                if (eachData.getString(Constant.TYPE).equals(Constant.EDITTEXT)) {
                    String selectedValue = eachData.getString(Constant.SELECTED_VALUE);
                    EditText et = new EditText(getApplicationContext());
                    et.setHint("" + eachData.getString(Constant.LABEL));
                    et.setPadding(20, 40, 0, 40);

                    if (selectedValue != null) {
                        et.setText(selectedValue);
                    } else {
                        et.setText("");
                    }

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
                    String selectedValue = eachData.getString(Constant.SELECTED_VALUE);
                    et.setHint("" + eachData.getString(Constant.LABEL));
                    if (selectedValue != null) {
                        et.setText(selectedValue);
                    } else {
                        et.setText("");
                    }


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

}
