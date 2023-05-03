package lazar.jovanovic.shoppinglist;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TaskElementAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<TaskElement> mTaskElements;

    public TaskElementAdapter(Context context) {
        this.mContext = context;
        mTaskElements = new ArrayList<TaskElement>();
    }

    @Override
    public int getCount() {
        return mTaskElements.size();
    }

    @Override
    public Object getItem(int i) {
        Object rv = null;
        try{
            rv = mTaskElements.get(i);
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }

        return rv;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void updateTasks(List<TaskElement> itemsList) {
        mTaskElements.clear();
        mTaskElements.addAll(itemsList);
        notifyDataSetChanged();
    }

    private class ViewHolder{
        public TextView vhNaslov;
        public CheckBox vhChecked;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder vh;
        if(view == null){
            LayoutInflater inflater = (LayoutInflater)
                    mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.task_list_layout, null);

            vh = new ViewHolder();
            vh.vhNaslov = view.findViewById(R.id.task_tekst_view);
            vh.vhChecked = view.findViewById(R.id.task_check_box);
            view.setTag(vh);
        }else{
            vh = (ViewHolder) view.getTag();
        }

        TaskElement te = (TaskElement) getItem(i);


        vh.vhChecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //ShowListActivity slA = new ShowListActivity();
                //String listName = slA.getItemListName();
                DbHelper dbHelper = new DbHelper(mContext, "database.db", null, 1);
                dbHelper.updateChecked(te, b);
                te.setmChecked(b);

                if(b)
                    vh.vhNaslov.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                else
                    vh.vhNaslov.setPaintFlags(0);

                notifyDataSetChanged();
            }
        });

        vh.vhChecked.setChecked(te.getmChecked());
        vh.vhNaslov.setText(te.getmNaslov());

        return view;
    }

    public void addTaskElements(TaskElement te){
        mTaskElements.add(te);
        notifyDataSetChanged();
    }

    public void removeTaskElements(TaskElement te){
        mTaskElements.remove(te);
        notifyDataSetChanged();
    }
}
