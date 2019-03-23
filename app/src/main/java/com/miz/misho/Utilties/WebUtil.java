package com.miz.misho.Utilties;
import com.miz.misho.Objects.DEntry;
import com.miz.misho.Objects.ESense;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.URL;

import java.nio.charset.Charset;
import java.util.ArrayList;


import org.json.*;
import org.apache.commons.io.IOUtils;


/**
 * Helper class to get results from Jisho.org
 */
public class WebUtil {

    final private String base = "https://jisho.org/api/v1/search/words?keyword=";


  private ArrayList<DEntry> rez;

  public void WebUtil() {
      rez = null;
  }

    public ArrayList<DEntry> search(final String keyword) throws JSONException, IOException {
        rez = new ArrayList<>();

                InputStream fetch;
                fetch = new URL(base + keyword).openStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(fetch, Charset.forName("UTF-8")));
                String js;
                //gets br to string
                    js = IOUtils.toString(br);
                // string to json
                JSONObject jst;
                jst = new JSONObject(js);

                //go into data key
                // each json obj in data array is an entry, convert array into dentry obj
                JSONArray vals;
                vals = jst.getJSONArray("data");

            for(int i = 0; i < vals.length(); i++) {
                JSONObject temp = vals.getJSONObject(i);
                DEntry etemp = new DEntry();
                JSONArray jreadings;
                JSONArray jsenses;
                jreadings = temp.getJSONArray("japanese");
                jsenses = temp.getJSONArray("senses");
                for (int z = 0; z < jreadings.length(); z++) {
                    JSONObject jr = jreadings.getJSONObject(z);
                    try {
                        etemp.kreading.add(jr.getString("word"));
                    } catch (JSONException je) {
                        etemp.kreading.add("");
                    }
                    try {
                        etemp.reading.add(jr.getString("reading"));
                    } catch (JSONException jee) {
                        etemp.reading.add("");
                    }
                }
                for (int z = 0; z < jsenses.length(); z++) {
                        ESense stemp = new ESense();
                        JSONArray jpos = jsenses.getJSONObject(z).getJSONArray("parts_of_speech");
                        JSONArray jdefs = jsenses.getJSONObject(z).getJSONArray("english_definitions");
                    JSONArray jrest = jsenses.getJSONObject(z).getJSONArray("restrictions");
                    JSONArray jsa = jsenses.getJSONObject(z).getJSONArray("see_also");
                    JSONArray jant = jsenses.getJSONObject(z).getJSONArray("antonyms");
                    JSONArray jsource = jsenses.getJSONObject(z).getJSONArray("source");
                    JSONArray jinfo = jsenses.getJSONObject(z).getJSONArray("info");
                    JSONArray jtags = jsenses.getJSONObject(z).getJSONArray("tags");
                    for (int w = 0; w < jpos.length(); w++) {
                        stemp.addToPos(jpos.getString(w));
                    }
                    for(int x = 0; x < jdefs.length(); x++) {
                        stemp.addToGloss(jdefs.getString(x));
                    }
                    for(int x = 0; x < jrest.length(); x++) {
                        stemp.addToStagk(jrest.getString(x));
                    }
                    for(int x = 0; x < jsa.length(); x++) {
                        stemp.addToXref(jsa.getString(x));
                    }
                    for(int x = 0; x < jant.length(); x++) {
                        stemp.addToAnt(jant.getString(x));
                    }
                    for(int x = 0; x < jsource.length(); x++) {
                        stemp.addToLsource(jsource.getString(x));
                    }
                    for(int x = 0; x < jinfo.length(); x++) {
                        stemp.addToS_inf(jinfo.getString(x));
                    }

                    for(int x = 0; x < jtags.length(); x++) {
                        stemp.addToMisc(jtags.getString(x));
                    }
                    etemp.senses.add(stemp);
                }
                rez.add(etemp);
            }
        return rez;
    }

}
