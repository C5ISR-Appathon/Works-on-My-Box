package com.geocent.codeathon.teamlocator;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class TeamOverlay extends ItemizedOverlay
{

    private ArrayList<OverlayItem> overlays = new ArrayList<OverlayItem>();
    private Context context;

    public TeamOverlay( Drawable defaultMarker ) {
        super( boundCenterBottom(defaultMarker) );
    }

    public TeamOverlay( Drawable defaultMarker, Context context ) {
        super( boundCenterBottom(defaultMarker) );
        this.context = context;
    }

    public void addOverlay( OverlayItem overlay ) {
        overlays.add( overlay );
        populate();
    }
    @Override
    protected OverlayItem createItem( int i ) {
        return overlays.get( i );
    }

    @Override
    public int size() {
        return overlays.size();
    }

}
