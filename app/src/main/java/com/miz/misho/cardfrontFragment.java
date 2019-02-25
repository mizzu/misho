package com.miz.misho;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.miz.misho.Objects.DEntry;
import com.miz.misho.Objects.KEntry;

public class cardfrontFragment extends Fragment {

    TextView text_front;
    DEntry entry;
    KEntry kentry;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_cardfront, container, false);
        text_front = view.findViewById(R.id.card_text_front);
            entry = (DEntry) this.getArguments().getSerializable("ENTRY");
            kentry = (KEntry) this.getArguments().getSerializable("KENTRY");
        if(entry != null) {
            if(!entry.kreading.get(0).isEmpty())
                text_front.setText( entry.kreading.get(0));
            else
                text_front.setText( entry.reading.get(0));
        } else
            text_front.setText(kentry.getKanji());
        return view;
    }
}
