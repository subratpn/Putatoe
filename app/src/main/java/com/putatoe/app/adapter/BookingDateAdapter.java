package com.putatoe.app.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.putatoe.app.R;
import com.putatoe.app.pojo.BookingDate;

import java.util.List;

public class BookingDateAdapter extends RecyclerView.Adapter<BookingDateAdapter.ViewHolder> {

    private List<BookingDate> bookingDates;
    private Context context;
    private static BookingDate selectedBookingDate;


    public BookingDateAdapter(List<BookingDate> bookingDates, Context context) {
        this.bookingDates = bookingDates;
        this.context = context;
    }

    @NonNull
    @Override
    public BookingDateAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View listItem= layoutInflater.inflate(R.layout.booking_date_layout, viewGroup, false);
        BookingDateAdapter.ViewHolder viewHolder = new BookingDateAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BookingDateAdapter.ViewHolder viewHolder, int i) {

        BookingDate bookingDate = bookingDates.get(i);
        viewHolder.bookingDateButton.setText(bookingDate.getDate());

        if(bookingDate.getSelected()){
            viewHolder.bookingDateButton.setBackground(ContextCompat.getDrawable(context,R.drawable.custom_accent_selected_button));
            viewHolder.bookingDateButton.setTextColor(ContextCompat.getColor(context,R.color.colorWhite));
        }else{
            viewHolder.bookingDateButton.setBackground(ContextCompat.getDrawable(context,R.drawable.custom_accent_button));
            viewHolder.bookingDateButton.setTextColor(ContextCompat.getColor(context,R.color.colorAccent));
        }

    }

    @Override
    public int getItemCount() {
        return bookingDates.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {

        public Button bookingDateButton;
        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            bookingDateButton = (Button) itemView.findViewById(R.id.booking_date_button);
            bookingDateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int itemPosition = getAdapterPosition();
                    BookingDate bookingDate = bookingDates.get(itemPosition);

                    if(bookingDate.getSelected()){
                        if(selectedBookingDate!=null)
                            selectedBookingDate.setSelected(true);
                        bookingDate.setSelected(false);
                    }else {
                        if(selectedBookingDate!=null)
                            selectedBookingDate.setSelected(false);
                        bookingDate.setSelected(true);
                    }

                    selectedBookingDate = bookingDate;
                    notifyDataSetChanged();
                }
            });

        }
    }
}
