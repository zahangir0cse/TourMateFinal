package com.android.zsm.tourmatefinal.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import com.android.zsm.tourmatefinal.ExpenditureList;
import com.android.zsm.tourmatefinal.R;
import com.android.zsm.tourmatefinal.model.Expenditure;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import java.util.ArrayList;

public class ExpenditureAdapter extends RecyclerView.Adapter<ExpenditureAdapter.ExpenseViewHolder> {
    private Context context;
    private ArrayList<Expenditure> expenses;
    private int count = 0;

    public ExpenditureAdapter(Context context, ArrayList<Expenditure> expenses) {
        this.context = context;
        this.expenses = expenses;

    }

    @Override
    public ExpenseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.single_expenditure_row, parent, false);
        return new ExpenseViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ExpenseViewHolder holder, int position) {
        final Expenditure expens = expenses.get(position);
        holder.extitle.setText(expenses.get(position).getDescription());
        holder.excost.setText("Tk. " + String.valueOf(expenses.get(position).getExpense()) + "0");
        holder.createDate.setText("On: " + expenses.get(position).getCreatedate());

        holder.optionDigit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onClickListenerUtil(v, holder, expens);
            }
        });

        holder.excost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onClickListenerUtil(v, holder, expens);
            }
        });

        holder.createDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onClickListenerUtil(v, holder, expens);
            }
        });

        holder.extitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onClickListenerUtil(v, holder, expens);
            }
        });
    }

    private void onClickListenerUtil(final View v, ExpenseViewHolder holder, final Expenditure expens){
        PopupMenu popupMenu = new PopupMenu(context, holder.optionDigit);
        popupMenu.inflate(R.menu.option_menu2);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.edit:
                        try {
                            ((ExpenditureList) v.getContext()).editExpenseDialog(expens);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case R.id.delete:
                        try {
                            ((ExpenditureList) v.getContext()).deleteRecord(expens);
                        } catch (Exception e) {
                           e.printStackTrace();
                        }
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    public class ExpenseViewHolder extends RecyclerView.ViewHolder {
        DatabaseReference root;
        private EventAdapter eventAdapter;
        FirebaseUser user;
        private FirebaseAuth auth;
        TextView extitle;
        TextView excost;
        TextView optionDigit;
        TextView createDate;

        public ExpenseViewHolder(View itemView) {
            super(itemView);
            extitle = itemView.findViewById(R.id.roweventname);
            excost = itemView.findViewById(R.id.budgetrow);
            createDate = itemView.findViewById(R.id.createtDaterow);
            optionDigit = itemView.findViewById(R.id.optionDigit);


        }

    }
}
