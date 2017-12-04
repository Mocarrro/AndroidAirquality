package tim.lab7;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.OkHttpClient;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import tim.lab7.rest.RestClient;
import tim.lab7.rest.model.Address;
import tim.lab7.rest.model.CommentPOJO;
import tim.lab7.rest.model.EntryPOJO;
import tim.lab7.rest.model.LiterarySubstancePOJO;
import tim.lab7.rest.model.StationAdapter;
import tim.lab7.rest.model.StationPOJO;
import tim.lab7.rest.model.SubstanceAll;
import tim.lab7.rest.model.SubstancePOJO;
import tim.lab7.rest.service.ExampleApiService;

import static tim.lab7.R.layout.station_row;

@EActivity(R.layout.activity_station)
public class EntryActivity extends AppCompatActivity {

    private String choosenStationId;

    StationPOJO stationPOJO = new StationPOJO();
    List<LiterarySubstancePOJO> literarySubstances = new ArrayList<LiterarySubstancePOJO>();
    List<SubstanceAll> substanceAllList;

    RestAdapter restAdapter;
    static final String API_URL = "http://10.0.2.2:8282/lab6_v2Web/";

    @ViewById
    TextView stationId;
    @ViewById
    TextView city;

    @ViewById
    TextView street;

    @ViewById
    ListView substanceList;

    StationAdapter adapter;
    public ArrayList<SubstancePOJO> CustomListViewValuesArr = new ArrayList<SubstancePOJO>();

//    @ViewById
//    EditText urlText;
//
//    @ViewById
//    EditText idText;
//    @ViewById
//    EditText dateText;
//    @ViewById
//    EditText subjectText;
//    @ViewById
//    EditText contentText;
//    @ViewById
//    EditText commentsText;
//
//    @ViewById
//    EditText getEntryIdText;
//    @ViewById
//    EditText delEntryIdText;
//    @ViewById
//    EditText putEntryIdText;
//
//    @Click
//    void addEntryButton(){
//        addEntry();
//    }

    //    @Click
//    protected void delEntryButton(){
//        delEntry();
//    }
//    @Click
//    protected void putEntryButton(){
//        putEntry();
//    }
//    @Click
//    protected void delAllEntryButton(){
//        delAllEntry();
//    }
    @Click
    void GetStationInfoByIdButton() {
        getStationInfoById(choosenStationId);
    }

    @Background
    void getStationInfoById(String stationId) {
//        Toast.makeText(getApplicationContext(),
//                "REST: ", Toast.LENGTH_SHORT)
//                .show();
        //TODO tu bedzie RESTowa komunikacja

        OkHttpClient mOkHttpClient = new OkHttpClient();
        mOkHttpClient.setConnectTimeout(15000, TimeUnit.MILLISECONDS);
        mOkHttpClient.setReadTimeout(15000, TimeUnit.MILLISECONDS);

        restAdapter = new RestAdapter.Builder()
                .setEndpoint(API_URL)
                .setClient(new OkClient(mOkHttpClient))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        ExampleApiService methods = restAdapter.create(ExampleApiService.class);


        Callback<StationPOJO> cb = new Callback<StationPOJO>() {

            @Override
            public void success(StationPOJO questions, retrofit.client.Response response) {

                stationPOJO.setStationId(questions.getStationId());
                Address address = new Address();
                address.setCity(questions.getStationAddress().getCity());
                address.setStreet(questions.getStationAddress().getStreet());
                stationPOJO.setStationAddress(address);
                SubstancePOJO substancePOJO = new SubstancePOJO();
                List<SubstancePOJO> substancePOJOS = new ArrayList<>();

                for (int i = 0; i < questions.getSubstances().size(); i++) {
                    substancePOJO.setSubstanceName(questions.getSubstances().get(i).getSubstanceName());
                    substancePOJO.setType(questions.getSubstances().get(i).getType());
                    substancePOJO.setValue(questions.getSubstances().get(i).getValue());
                    substancePOJOS.add(substancePOJO);
                }
                stationPOJO.setSubstances(substancePOJOS);
            }

            @Override
            public void failure(RetrofitError retrofitError) {

            }


        };
        methods.getStationInfo(choosenStationId, cb);

    }

    @AfterViews
    void initStation() {
        LiterarySubstancePOJO literarySubstancePOJO = new LiterarySubstancePOJO();
        literarySubstancePOJO.setSubstanceId("1");
        literarySubstancePOJO.setSubstanceName("CO");
        literarySubstancePOJO.setTreshold(20.0);
        literarySubstancePOJO.setUnit("g/m3");
        literarySubstances.add(literarySubstancePOJO);
        choosenStationId = "0";
        choosenStationId = getIntent().getStringExtra("choosenStationId");
        stationId.setText("ID: " + choosenStationId);
        stationPOJO = new StationPOJO();
        getStationInfoById(choosenStationId);
    }

    public double findSubstanceValue(LiterarySubstancePOJO literarySubstancePOJO, List<SubstancePOJO> substancePOJOS) {

        for (SubstancePOJO substancePOJO : substancePOJOS) {
            if (substancePOJO.getType().equals(literarySubstancePOJO.getSubstanceId())) {
                return literarySubstancePOJO.getTreshold();
            }
        }
        return 0d;
    }

    private List<SubstanceAll> mergeSubstancesLists(List<SubstancePOJO> substancePOJOS, List<LiterarySubstancePOJO> literarySubstancePOJOS) {
        substanceAllList = new ArrayList<>();
        SubstanceAll substanceAll = new SubstanceAll();
        for (LiterarySubstancePOJO literarySubstancePOJO : literarySubstancePOJOS) {
            substanceAll.setSubstanceId(literarySubstancePOJO.getSubstanceId());
            substanceAll.setSubstanceName(literarySubstancePOJO.getSubstanceName());
            substanceAll.setTreshold(literarySubstancePOJO.getTreshold());
            substanceAll.setUnit(literarySubstancePOJO.getUnit());
            substanceAll.setValue(findSubstanceValue(literarySubstancePOJO, substancePOJOS));
            substanceAllList.add(substanceAll);
        }
        return substanceAllList;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            city.setText(stationPOJO.getStationAddress().getCity());
            street.setText(stationPOJO.getStationAddress().getStreet());
            substanceAllList = mergeSubstancesLists(stationPOJO.getSubstances(), literarySubstances);

            StationAdapter stationAdapter = new StationAdapter(this, R.layout.station_row, substanceAllList);
            substanceList.setAdapter(stationAdapter);
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        setContentView(R.layout.activity_station);
        getStationInfoById(choosenStationId);


//
//    @Background
//    void addEntry(){
//        RestClient client = new RestClient(urlText.getText().toString());
//        List<CommentPOJO> list = new ArrayList<>();
//        final EntryPOJO entryPOJO = new EntryPOJO(Integer.parseInt(idText.getText().toString()),dateText.getText().toString(),subjectText.getText().toString(),contentText.getText().toString(), list);
//        client.addEntry(entryPOJO);
//    }
//
//    @Background
//    void getEntry(){
//        try {
//            RestClient client = new RestClient(urlText.getText().toString());
//            final EntryPOJO entryPOJO = client.getEntryById(getEntryIdText.getText().toString());
//            EntryActivity.this.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    System.out.println(entryPOJO.toString());
//                    idText.setText(Integer.toString(entryPOJO.getId()));
//                    dateText.setText(entryPOJO.getDate());
//                    subjectText.setText(entryPOJO.getSubject());
//                    contentText.setText(entryPOJO.getContent());
//                    commentsText.setText(entryPOJO.getComments().toString());
//                }
//            });
//        }
//        catch (Exception e){
//            Toast.makeText(getApplicationContext(), "Wrong request parameters!",Toast.LENGTH_SHORT).show();
//        }
//
//
//    }
//
//    @Background
//    void delEntry(){
//        RestClient client = new RestClient(urlText.getText().toString());
//        client.delEntry(delEntryIdText.getText().toString());
//    }
//
//    @Background
//    void putEntry(){
//        try {
//
//
//            RestClient client = new RestClient(urlText.getText().toString());
//            List<CommentPOJO> list = client.getEntryById(putEntryIdText.getText().toString()).getComments();
//            client = new RestClient(urlText.getText().toString());
//            final EntryPOJO entryPOJO = new EntryPOJO(Integer.parseInt(idText.getText().toString()), dateText.getText().toString(), subjectText.getText().toString(), contentText.getText().toString(), list);
//            client.putEntry(entryPOJO, putEntryIdText.getText().toString());
//        }
//        catch (Exception e){
//            Toast.makeText(getApplicationContext(), "Wrong request parameters!",Toast.LENGTH_SHORT).show();
//
//        }
//    }
//
//    @Background
//    void delAllEntry(){
//        RestClient client = new RestClient(urlText.getText().toString());
//        client.delAllEntry();
//    }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}