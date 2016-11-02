/*
 * MIT License
 *
 * Copyright 2016 Niklas Merkelt
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
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.schkola.kitchenscanner.task;

import android.os.AsyncTask;
import android.preference.PreferenceManager;

import de.schkola.kitchenscanner.activity.DisplayActivity;
import de.schkola.kitchenscanner.activity.MainActivity;

/**
 * Dieser Task wird ausgeführt, wenn das Essen angezeigt wird und in ... Sekunden neugescannt wird.
 */
public class RescanTask extends AsyncTask<Void, Void, Void> {

    @Override
    protected Void doInBackground(Void... Void) {
        try {
            Thread.sleep(Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(MainActivity.getInstance()).getString("rescan", "2")) * 1000);
        } catch (InterruptedException ignored) {
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        DisplayActivity.getInstance().finish();
        MainActivity.startScan();
    }
}