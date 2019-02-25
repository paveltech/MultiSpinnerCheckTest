package com.dream71.android.multispinnerchecktest;

import android.annotation.SuppressLint;
import android.content.Context;
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


    private JSONObject optionsObj;


    @BindView(R.id.customOptionLL)
    LinearLayout viewProductLayout;

    @BindView(R.id.button_product_upload)
    Button productUpload;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    Map<String, String> checkMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


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

                    /*
                    JSONArray jsonArrayCheckList = new JSONArray();
                    for (String value : selectedCheckList) {
                        jsonArrayCheckList.put(value);
                    }
                    */

                    Log.d("CHECK_MAP", "" + checkMap.toString());

                    //optionsObj.put("" + eachData.getString(Constant.OPTION_NAME), jsonArrayCheckList);

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


                //***********************************CheckBox ***********************************************/

                if (eachData.getString(Constant.TYPE).equals(Constant.CHECKBOX)) {
                    JSONArray checkBoxJSONOpt = eachData.getJSONArray(Constant.VALUES);
                    final String data = eachData.getString(Constant.NAME);
                    CheckBox chk = null;

                    for (int j = 0; j < checkBoxJSONOpt.length(); j++) {

                        chk = new CheckBox(getApplicationContext());
                        chk.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        chk.setTag(checkBoxJSONOpt.getJSONObject(j).getString(Constant.NAME));

                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.topMargin = 3;
                        params.bottomMargin = 3;
                        final String optionString = checkBoxJSONOpt.getJSONObject(j).getString(Constant.NAME);

                        //Log.d("CHECK_MAP", "" + data);

                        //selectedCheckList = new ArrayList<>();

                        if (checkBoxJSONOpt.getJSONObject(j).getString(Constant.CHECKED).contains("true")) {
                            String options = checkBoxJSONOpt.getJSONObject(j).getString(Constant.NAME);
                            checkMap.put(data, options);
                            chk.setChecked(true);
                        }

                        final int k = j;
                        final CheckBox finalChk = chk;
                        chk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (finalChk.isChecked()) {
                                    String variant_name = v.getTag().toString();
                                    checkMap.put(data, variant_name);
                                } else {
                                    String variant_name = v.getTag().toString();

                                    for (int i = 0; i < checkMap.size(); i++) {

                                        String value = checkMap.toString();

                                        if (value.equals(variant_name)) {

                                            checkMap.remove(value);
                                        }
                                    }
                                }
                                //checkMap.put(data, selectedCheckList);
                            }
                        });

                        chk.setText(optionString);
                        viewProductLayout.addView(chk, params);
                    }

                    allViewInstance.add(chk);
                    //}
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
