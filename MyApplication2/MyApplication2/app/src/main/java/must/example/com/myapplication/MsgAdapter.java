package must.example.com.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Lenovo on 2017/4/10.
 */
public class MsgAdapter extends ArrayAdapter<Msg>
{
    private int resourceId;

    public MsgAdapter(Context context, int textViewResourceId, List<Msg> object)
    {
        super(context,textViewResourceId,object);
        resourceId=textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Msg msg=getItem(position);
        View view;
        ViewHolder viewHolder;

        if(convertView==null)
        {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.leftLayout = (LinearLayout) view.findViewById(R.id.left_layout);
            viewHolder.rightLayout = (LinearLayout) view.findViewById(R.id.right_layout);
            viewHolder.leftMsg = (TextView) view.findViewById(R.id.left_msg);
            viewHolder.rightMsg = (TextView) view.findViewById(R.id.right_msg);
            viewHolder.manager_l = (ImageView) view.findViewById(R.id.manager_l);
            viewHolder.manager_r = (ImageView) view.findViewById(R.id.manager_r);
            view.setTag(viewHolder);
        }
        else
        {
            view=convertView;
            viewHolder=(ViewHolder)view.getTag();
        }
        if(msg.getType()==Msg.TYPE_RECEIVED)
        {
            viewHolder.leftLayout.setVisibility(View.VISIBLE);
            viewHolder.manager_l.setVisibility(View.VISIBLE);
            viewHolder.rightLayout.setVisibility(View.GONE);
            viewHolder.manager_r.setVisibility(View.GONE);
            viewHolder.leftMsg.setText(msg.getContent());
        }
        else if(msg.getType()==Msg.TYPE_SENT)
        {
            viewHolder.rightLayout.setVisibility(View.VISIBLE);
            viewHolder.manager_r.setVisibility(View.VISIBLE);
            viewHolder.leftLayout.setVisibility(View.GONE);
            viewHolder.manager_l.setVisibility(View.GONE);
            viewHolder.rightMsg.setText(msg.getContent());
        }
        return view;
    }
    class ViewHolder
    {
        LinearLayout leftLayout;

        LinearLayout rightLayout;

        TextView leftMsg;

        TextView rightMsg;

        ImageView manager_l;

        ImageView manager_r;
    }


}
