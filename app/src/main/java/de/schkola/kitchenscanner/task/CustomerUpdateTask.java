package de.schkola.kitchenscanner.task;

import android.os.AsyncTask;
import androidx.annotation.NonNull;
import de.schkola.kitchenscanner.database.Customer;
import de.schkola.kitchenscanner.database.LunchDatabase;

public class CustomerUpdateTask extends AsyncTask<Customer, Void, Void> {

    private LunchDatabase database;

    public CustomerUpdateTask(LunchDatabase database) {
        this.database = database;
    }

    @Override
    protected Void doInBackground(@NonNull Customer... customers) {
        for (Customer c : customers) {
            database.customerDao().updateCustomer(c);
        }
        return null;
    }
}
