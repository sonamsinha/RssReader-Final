package sonamsinha.rss_reader;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by sonamsinha on 3/18/16.
 */
public class RssItemAdapter extends ArrayAdapter<RssItemData> {
    private Activity myContext;
    private RssItemData[] datas;

    public RssItemAdapter(Context context, int textViewResourceId, RssItemData[] objs){
        super(context, textViewResourceId, objs);
        myContext = (Activity)context;
        datas = objs;
    }

    public View getView(int pos, View convertView, ViewGroup parent){
        View cellView;
        if(convertView == null) {
            LayoutInflater inflater = myContext.getLayoutInflater();
            convertView = inflater.inflate(R.layout.list_item_layout,null);
        }
        cellView = convertView;
         //cellView = inflater.inflate(R.layout.list_item_layout, null);
        TextView postTitleView = (TextView)cellView.findViewById(R.id.descriptionView);
        postTitleView.setText(datas[pos].title);
        return cellView;
    }
}
