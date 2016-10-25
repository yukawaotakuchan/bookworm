package com.shira.ayelethashahar.bookWorm;

/**
 * Created by Shira on 08/12/2015.
 */
import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class BookResultAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final List<String> itemname;
    private final List<String> itemauthor;
    private final List<String> itemprice;
    private final List<String> itemvendor;
    private final List<Bitmap> itemcover;

    public BookResultAdapter(Activity context, List<String> itemname, List<String> itemauthor, List<String> itemprice, List<Bitmap> imgid, List<String> itemvendor) {
        super(context, R.layout.row_book_search_result, itemname);

        this.context = context;
        this.itemname = itemname;
        this.itemcover = imgid;
        this.itemauthor = itemauthor;
        this.itemprice = itemprice;
        this.itemvendor = itemvendor;
    }


    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.row_book_search_result, null, true);

        LinearLayout book = (LinearLayout) rowView.findViewById(R.id.book);
        TextView resultTitle = (TextView) rowView.findViewById(R.id.textViewResultName);
        TextView resultAuthor = (TextView) rowView.findViewById(R.id.textViewResultAuthor);
        TextView resultPrice = (TextView) rowView.findViewById(R.id.textViewResultPrice);
        TextView resultSeller = (TextView) rowView.findViewById(R.id.textViewResultSeller);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.imageViewResult);
        resultTitle.setText(itemname.get(position));
        resultAuthor.setText(itemauthor.get(position));
        resultPrice.setText(itemprice.get(position));
        resultSeller.setText(itemvendor.get(position));
        imageView.setImageBitmap(itemcover.get(position));
        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ClientSystemActivity) context).goZoom(itemname.get(position), itemvendor.get(position));
                context.finish();
            }
        });

        return rowView;
    }
}