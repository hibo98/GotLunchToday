/*
 * MIT License
 *
 * Copyright 2017 Niklas Merkelt
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.schkola.kitchenscanner.task;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.SparseArray;

import androidx.core.util.Consumer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import de.schkola.kitchenscanner.util.Person;

public class StatTask extends AsyncTask<Void, Void, JSONObject> {

    private final ProgressDialog dialog;
    private final Consumer<JSONObject> runnable;

    public StatTask(ProgressDialog dialog, Consumer<JSONObject> runnable) {
        this.runnable = runnable;
        this.dialog = dialog;
    }

    @Override
    protected void onPreExecute() {
        this.dialog.show();
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        int lunchA = 0;
        int lunchB = 0;
        int lunchS = 0;
        int gotLunchA = 0;
        int gotLunchB = 0;
        int gotLunchS = 0;
        ArrayList<String> getLunchA = new ArrayList<>();
        ArrayList<String> getLunchB = new ArrayList<>();
        ArrayList<String> getLunchS = new ArrayList<>();
        SparseArray<Person> array = Person.getPersons();
        for(int i = 0; i < array.size(); i++) {
            Person p = array.get(array.keyAt(i));
            switch (p.getRawLunch()) {
                case 1:
                    lunchA++;
                    if (p.getGotLunch() > 0) {
                        gotLunchA++;
                    } else {
                        getLunchA.add(p.getPersonName());
                    }
                    break;
                case 2:
                    lunchB++;
                    if (p.getGotLunch() > 0) {
                        gotLunchB++;
                    } else {
                        getLunchB.add(p.getPersonName());
                    }
                    break;
                case 3:
                    lunchS++;
                    if (p.getGotLunch() > 0) {
                        gotLunchS++;
                    } else {
                        getLunchS.add(p.getPersonName());
                    }
                    break;
            }
        }
        JSONObject jsonObject = new JSONObject();
        Collections.sort(getLunchA);
        Collections.sort(getLunchB);
        Collections.sort(getLunchS);
        try {
            jsonObject.put("lunchA", lunchA);
            jsonObject.put("lunchB", lunchB);
            jsonObject.put("lunchS", lunchS);
            jsonObject.put("gotLunchA", gotLunchA);
            jsonObject.put("gotLunchB", gotLunchB);
            jsonObject.put("gotLunchS", gotLunchS);
            jsonObject.put("getLunchA", new JSONArray(getLunchA));
            jsonObject.put("getLunchB", new JSONArray(getLunchB));
            jsonObject.put("getLunchS", new JSONArray(getLunchS));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        dialog.dismiss();
        dialog.cancel();
        runnable.accept(jsonObject);
    }
}
