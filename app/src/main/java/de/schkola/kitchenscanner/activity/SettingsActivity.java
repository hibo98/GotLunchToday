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
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.schkola.kitchenscanner.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import androidx.appcompat.app.AppCompatActivity;
import de.schkola.kitchenscanner.R;
import de.schkola.kitchenscanner.task.JsonScanTask;
import de.schkola.kitchenscanner.util.Person;

public class SettingsActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_DAY = 42;
    private static final int REQUEST_CODE_ALLERGY = 43;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set Content
        setContentView(R.layout.activity_settings);
        getFragmentManager().beginTransaction().replace(R.id.content_frame, new SettingsFragment()).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_CSV_copy:
                //Start copy CSV-Files
                new AlertDialog.Builder(this)
                        .setTitle(R.string.csv_import)
                        .setMessage(R.string.copy_request_day)
                        .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                            deleteFiles();
                            startCopy(false);
                        })
                        .setNegativeButton(R.string.no, (dialog, witch) -> startCopy(false))
                        .setNeutralButton(R.string.canel, null)
                        .create().show();
                return true;
            case R.id.action_CSV_copy_allergy:
                //Start copy CSV-Files
                new AlertDialog.Builder(this)
                        .setTitle(R.string.csv_import)
                        .setMessage(R.string.copy_request_allergy)
                        .setPositiveButton(R.string.yes, (dialogInterface, i) -> startCopy(true))
                        .setNegativeButton(R.string.no, null)
                        .create().show();
                return true;
            case R.id.action_delete_data:
                //Delete Cache
                new AlertDialog.Builder(this)
                        .setTitle(R.string.delete)
                        .setMessage(R.string.delete_request)
                        .setPositiveButton(R.string.yes, (dialogInterface, i) -> deleteFiles())
                        .setNegativeButton(R.string.no, null)
                        .create().show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startCopy(boolean allergy) {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setTitle(getString(R.string.csv_import));
        dialog.setMessage(getString(R.string.csv_import_ongoing));
        int text = allergy ? R.string.choose_allergy : R.string.choose_day;
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        String[] mimetypes = {"text/csv", "text/comma-separated-values"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        startActivityForResult(intent, allergy ? REQUEST_CODE_ALLERGY : REQUEST_CODE_DAY);
    }

    private void deleteFiles() {
        for (File f : getDir("Lunch", MainActivity.MODE_PRIVATE).listFiles()) {
            f.delete();
        }
        SparseArray<Person> array = Person.getPersons();
        for (int j = 0; j < array.size(); j++) {
            Person p = array.get(array.keyAt(j));
            p.resetGotLunch();
        }
    }

    private JsonScanTask createScanTask(boolean allergy) {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setTitle(getString(R.string.csv_import));
        dialog.setMessage(getString(R.string.csv_import_ongoing));
        return new JsonScanTask(dialog, getDir("JSON", Activity.MODE_PRIVATE), allergy);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (resultData != null && resultCode == Activity.RESULT_OK) {
            Uri data = resultData.getData();
            if (data != null) {
                InputStream inputStream;
                try {
                    inputStream = getContentResolver().openInputStream(data);
                } catch (FileNotFoundException ignored) {
                    return;
                }
                createScanTask(requestCode == REQUEST_CODE_ALLERGY).execute(inputStream);
            }
        }
    }
}
