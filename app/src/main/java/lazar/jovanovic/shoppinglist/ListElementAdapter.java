package lazar.jovanovic.shoppinglist;

import android.content.Context;
import android.view.ContentInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

public class ListElementAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<ListElement> mListElements;
    public ListElementAdapter(Context context){
        mContext = context;
        mListElements = new ArrayList<ListElement>();
    }

    @Override
    public int getCount() {
        return mListElements.size();
    }

    @Override
    public Object getItem(int i) {
        Object rv = null;
        try {
            rv = mListElements.get(i);
        } catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }

        return rv;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private class ViewHolder {
        TextView vhNaslov, vhShared;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder vh;
        if(view == null){
            LayoutInflater inflater = (LayoutInflater)
                    mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_layout, null);
            vh = new ViewHolder();
            vh.vhNaslov = view.findViewById(R.id.list_naslov);
            vh.vhShared = view.findViewById(R.id.list_shared);
            view.setTag(vh);
        }else{
            vh = (ViewHolder) view.getTag();
        }

        ListElement le = (ListElement) getItem(i);
        vh.vhNaslov.setText(le.getmNaslov());
        vh.vhShared.setText(String.valueOf(le.getmShared()));

        return view;
    }

    public void addListElement(ListElement le){
        mListElements.add(le);
        notifyDataSetChanged();
    }

    public void removeListElement(ListElement le){
        mListElements.remove(le);
        notifyDataSetChanged();
    }
}
