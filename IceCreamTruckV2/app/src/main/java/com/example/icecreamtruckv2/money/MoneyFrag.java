package com.example.icecreamtruckv2.money;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.icecreamtruckv2.R;
import com.example.icecreamtruckv2.utils.Constants;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.icecreamtruckv2.R.drawable.rounded_bubble_orange_box;
import static com.example.icecreamtruckv2.R.drawable.rounded_bubble_purple_box;
import static com.example.icecreamtruckv2.R.drawable.toggle_bg;

public class MoneyFrag extends Fragment {
    private List<MoneyItem> income = new ArrayList<>();
    private List<MoneyItem> expenses = new ArrayList<>();

    private DatabaseReference rootE, rootI;
    private FirebaseDatabase db;
    private SharedPreferences sharedPreferences;
    private String userRole;

    private TableLayout table;
    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        userRole = sharedPreferences.getString("userRole", "null");
        // Defines the xml file for the fragment
        db = FirebaseDatabase.getInstance();
        return inflater.inflate(R.layout.money_frag, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);
        table = getView().findViewById(R.id.accounts_table);

        Button btnI = view.findViewById(R.id.add_income_entry);
        btnI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildAlert(true);
            }
        });
        Button btnE = view.findViewById(R.id.add_expenses_entry);
        btnE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildAlert(false);
            }
        });

        readData();
        getResults();
    }

    private void editAlert(View v, boolean isIncome) {
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(getContext());
        View promptsView = li.inflate(R.layout.money_alert, null);

        final EditText titleInput = promptsView.findViewById(R.id.input_title);
        TextView prevTV = (TextView) ((LinearLayout) v.getParent()).getChildAt(0);
        titleInput.setText(prevTV.getText());

        final EditText costInput = promptsView.findViewById(R.id.input_cost);
        CheckBox prevCB = (CheckBox) v;
        costInput.setText(prevCB.getText());

        Button btnYes = promptsView.findViewById(R.id.OKbtn);
        Button btnNo = promptsView.findViewById(R.id.CANCELbtn);

        final RadioGroup radioEntry = promptsView.findViewById(R.id.radioEntry);
        RadioButton radioIncome = promptsView.findViewById(R.id.radioIncome);
        RadioButton radioExpense = promptsView.findViewById(R.id.radioExpense);

        if (isIncome)
            radioIncome.setChecked(true);
        else
            radioExpense.setChecked(true);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setView(promptsView)
                .setCancelable(false);


        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = radioEntry.getCheckedRadioButtonId();

                if (selectedId == R.id.radioIncome) {
                    rootI = db.getReference(userRole + "/" + Constants.INCOME_DB);
                    Log.e("Added", "Income");
                    Long ts = new Date().getTime();
                    MoneyItem data = new MoneyItem(titleInput.getText().toString(), Integer.parseInt(costInput.getText().toString()), false, ts);
                    rootI.child(String.valueOf(ts)).setValue(data);
                } else {
                    rootE = db.getReference(userRole + "/" + Constants.EXPENSES_DB);
                    Log.e("Added", "Expenses");
                    Long ts = new Date().getTime();
                    MoneyItem data = new MoneyItem(titleInput.getText().toString(), Integer.parseInt(costInput.getText().toString()), false, ts);
                    rootE.child(String.valueOf(ts)).setValue(data);
                }
                alertDialog.dismiss();
            }
        });
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }


    private void buildAlert(boolean isIncome) {
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(getContext());
        View promptsView = li.inflate(R.layout.money_alert, null);

        final EditText titleInput = promptsView.findViewById(R.id.input_title);
        final EditText costInput = promptsView.findViewById(R.id.input_cost);

        Button btnYes = promptsView.findViewById(R.id.OKbtn);
        Button btnNo = promptsView.findViewById(R.id.CANCELbtn);

        final RadioGroup radioEntry = promptsView.findViewById(R.id.radioEntry);
        RadioButton radioIncome = promptsView.findViewById(R.id.radioIncome);
        RadioButton radioExpense = promptsView.findViewById(R.id.radioExpense);

        if (isIncome)
            radioIncome.setChecked(true);
        else
            radioExpense.setChecked(true);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setView(promptsView)
                .setCancelable(false);


        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = radioEntry.getCheckedRadioButtonId();

                if (selectedId == R.id.radioIncome) {
                    rootI = db.getReference(userRole + "/" + Constants.INCOME_DB);
                    Log.e("Added", "Income");

                    Long ts = new Date().getTime();
                    MoneyItem data = new MoneyItem(titleInput.getText().toString(), Integer.parseInt(costInput.getText().toString()), false, ts);
                    rootI.child(String.valueOf(ts)).setValue(data);
                } else {
                    rootE = db.getReference(userRole + "/" + Constants.EXPENSES_DB);
                    Log.e("Added", "Expenses");

                    Long ts = new Date().getTime();
                    MoneyItem data = new MoneyItem(titleInput.getText().toString(), Integer.parseInt(costInput.getText().toString()), false, ts);
                    rootE.child(String.valueOf(ts)).setValue(data);
                }
                alertDialog.dismiss();
            }
        });
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }

    // Read data from firebase
    private void readData() {
        try {
            rootI = db.getReference(userRole + "/" + Constants.INCOME_DB);
            rootI.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    final MoneyItem data = dataSnapshot.getValue(MoneyItem.class);

                    int nextIDX = income.size() + 2;
                    Log.e("BRForeidx", Integer.toString(nextIDX));
                    income.add(data);
                    Log.e("count", Integer.toString(table.getChildCount()));
                    Log.e("idx", Integer.toString(nextIDX));
                    if (nextIDX >= table.getChildCount()) {
                        addRow(data, true);
                    } else {
                        TableRow row = (TableRow) table.getChildAt(nextIDX);
                        LinearLayout ll = (LinearLayout) row.getChildAt(0);
                        ll.setVisibility(View.VISIBLE);

                        TextView tv = (TextView) ll.getChildAt(0);
                        final CheckBox cb = (CheckBox) ll.getChildAt(1);

                        tv.setText(data.getTitle());
                        cb.setText(Integer.toString(data.getCost()));

                        cb.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Log.e("Income", Integer.toString(data.getCost()));
                                int newCost = data.getCost();
                                TextView tvResult = getView().findViewById(R.id.result);
                                int cost = Integer.parseInt(tvResult.getText().toString());
                                if (cb.isChecked()) {
                                    cost += newCost;
                                } else {
                                    cost -= newCost;
                                }
                                tvResult.setTextColor(cost > 0 ? Color.GREEN : Color.RED);
                                tvResult.setText(Integer.toString(cost));
                            }
                        });

                        cb.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                editAlert(v, true);
                                return false;
                            }
                        });
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    income.removeIf(m -> dataSnapshot.getValue(MoneyItem.class).getTimestamp() == m.getTimestamp());
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } catch (Exception e) {
        }
        try {
            rootE = db.getReference(userRole + "/" + Constants.EXPENSES_DB);
            rootE.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    final MoneyItem data = dataSnapshot.getValue(MoneyItem.class);

                    int nextIDX = expenses.size() + 2;
                    expenses.add(data);
                    Log.e("count", Integer.toString(table.getChildCount()));
                    Log.e("idx", Integer.toString(nextIDX));
                    if (nextIDX >= table.getChildCount()) {
                        addRow(data, false);
                    } else {
                        TableRow row = (TableRow) table.getChildAt(nextIDX);
                        LinearLayout ll = (LinearLayout) row.getChildAt(1);
                        ll.setVisibility(View.VISIBLE);

                        TextView tv = (TextView) ll.getChildAt(0);
                        final CheckBox cb = (CheckBox) ll.getChildAt(1);

                        tv.setText(data.getTitle());
                        cb.setText(Integer.toString(data.getCost()));

                        cb.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Log.e("Expense", Integer.toString(data.getCost()));
                                int newCost = data.getCost();
                                TextView tvResult = getView().findViewById(R.id.result);
                                Log.e("Expense", tvResult.getText().toString());
                                int cost = Integer.parseInt(tvResult.getText().toString());
                                if (cb.isChecked()) {
                                    cost -= newCost;
                                } else {
                                    cost += newCost;
                                }
                                tvResult.setTextColor(cost > 0 ? Color.GREEN : Color.RED);
                                tvResult.setText(Integer.toString(cost));
                            }
                        });

                        cb.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                editAlert(v, false);
                                return false;
                            }
                        });
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    expenses.removeIf(m -> dataSnapshot.getValue(MoneyItem.class).getTimestamp() == m.getTimestamp());
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } catch (Exception e) {
        }
    }

    private void addRow(final MoneyItem data, boolean isIncome) {
        TableRow.LayoutParams lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);

        TableRow row = new TableRow(getContext());
        row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT));
        row.setWeightSum(2);

        LinearLayout ll1 = new LinearLayout(getContext());
        ll1.setOrientation(LinearLayout.VERTICAL);
        ll1.setPadding(3, 3, 3, 3);
        ll1.setLayoutParams(lp);
        ll1.setBackgroundResource(rounded_bubble_purple_box);

        TextView tv1 = new TextView(getContext());
        tv1.setText(isIncome ? data.getTitle() : "");
        tv1.setTextColor(Color.WHITE);
        tv1.setLayoutParams(p);
        ll1.addView(tv1);

        final CheckBox cb1 = new CheckBox(getContext());
        cb1.setText(isIncome ? Integer.toString(data.getCost()) : "");
        cb1.setTextColor(Color.WHITE);
        cb1.setBackgroundResource(toggle_bg);
        cb1.setLayoutParams(p);

        ll1.addView(cb1);
        row.addView(ll1);

        ll1.setVisibility(isIncome ? View.VISIBLE : View.INVISIBLE);

        LinearLayout ll2 = new LinearLayout(getContext());
        ll2.setOrientation(LinearLayout.VERTICAL);
        ll2.setPadding(3, 3, 3, 3);
        ll2.setLayoutParams(lp);
        ll2.setBackgroundResource(rounded_bubble_orange_box);

        TextView tv2 = new TextView(getContext());
        tv2.setText(isIncome ? "" : data.getTitle());
        tv2.setTextColor(Color.WHITE);
        tv2.setLayoutParams(p);
        tv2.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        ll2.addView(tv2);

        final CheckBox cb2 = new CheckBox(getContext());
        cb2.setText(isIncome ? "" : Integer.toString(data.getCost()));
        cb2.setTextColor(Color.WHITE);
        cb2.setBackgroundResource(toggle_bg);
        cb2.setLayoutParams(p);
        cb2.setButtonDrawable(android.R.color.transparent);
        cb2.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);

        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.listChoiceIndicatorMultiple, typedValue, true);

        // it's probably a good idea to check if the color wasn't specified as a resource
        if (typedValue.resourceId != 0) {
            cb2.setCompoundDrawablesWithIntrinsicBounds(0, 0, typedValue.resourceId, 0);
        } else {
            // this should work whether there was a resource id or not
            cb2.setCompoundDrawablesWithIntrinsicBounds(0, 0, typedValue.data, 0);
        }

        if (isIncome) {
            cb1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.e("Income", Integer.toString(data.getCost()));
                    int newCost = data.getCost();
                    TextView tvResult = getView().findViewById(R.id.result);
                    int cost = Integer.parseInt(tvResult.getText().toString());
                    if (cb1.isChecked()) {
                        cost += newCost;
                    } else {
                        cost -= newCost;
                    }
                    tvResult.setTextColor(cost > 0 ? Color.GREEN : Color.RED);
                    tvResult.setText(Integer.toString(cost));
                }
            });
        } else {
            cb2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.e("Expense", Integer.toString(data.getCost()));
                    int newCost = data.getCost();
                    TextView tvResult = getView().findViewById(R.id.result);
                    Log.e("Expense", tvResult.getText().toString());
                    int cost = Integer.parseInt(tvResult.getText().toString());
                    if (cb2.isChecked()) {
                        cost -= newCost;
                    } else {
                        cost += newCost;
                    }
                    tvResult.setTextColor(cost > 0 ? Color.GREEN : Color.RED);
                    tvResult.setText(Integer.toString(cost));
                }
            });
        }
        ll2.addView(cb2);
        row.addView(ll2);

        ll2.setVisibility(isIncome ? View.INVISIBLE : View.VISIBLE);

        table.addView(row);
    }

    private void getResults() {
        int cost = 0;
        for (MoneyItem i : income) {
            if (i.isDone()) {
                cost += i.getCost();
            }
        }

        for (MoneyItem i : expenses) {
            if (i.isDone()) {
                cost -= i.getCost();
            }
        }
        TextView tvResult = getView().findViewById(R.id.result);
        tvResult.setTextColor(cost > 0 ? Color.GREEN : Color.RED);
        tvResult.setText(Integer.toString(cost));
    }
}
