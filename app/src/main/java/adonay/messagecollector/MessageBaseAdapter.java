package adonay.messagecollector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class MessageBaseAdapter extends BaseAdapter {

    private ArrayList<String> messages;
    private LayoutInflater inflater;
    private MainActivity mainActivity;

    MessageBaseAdapter(Context context, ArrayList<String> messages) {
        this.messages = messages;
        mainActivity = (MainActivity) context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public String getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        String currentMessage = messages.get(position);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.message_item, parent, false);
            holder = new Holder(this.mainActivity);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        holder.position = position;

        holder.contactNameTextView = (TextView) convertView.findViewById(R.id.contactNameTextView);

        String patternString1 = "address:";
        String patternString2 = "person:";

        String regexString = patternString1 + "(?s)(.*?)" + patternString2;

        Pattern pattern = Pattern.compile(regexString);
        Matcher matcher = pattern.matcher(currentMessage);
        if(matcher.find()) {
            String match = matcher.group();
            holder.contactNameTextView.setText(match.substring(patternString1.length(), match.length() - (patternString2.length() + 2)));
        }

        holder.messageBodyTextView = (TextView) convertView.findViewById(R.id.messageBodyTextView);

        String message = currentMessage;
        holder.messageBodyTextView.setText(message);

        return convertView;
    }

    private class Holder{
        TextView contactNameTextView;
        TextView messageBodyTextView;
        MainActivity mainActivity;
        int position;

        Holder(MainActivity mainActivity) {
            this.mainActivity = mainActivity;
        }
    }
}
