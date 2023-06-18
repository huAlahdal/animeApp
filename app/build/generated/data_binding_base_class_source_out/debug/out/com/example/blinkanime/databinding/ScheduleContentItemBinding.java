// Generated by view binder compiler. Do not edit!
package com.example.blinkanime.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.blinkanime.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ScheduleContentItemBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final TextView animeDescription;

  @NonNull
  public final ImageView animeImage;

  @NonNull
  public final TextView animeTitle;

  @NonNull
  public final LinearLayout animeTitleLayout;

  @NonNull
  public final CardView cardView;

  private ScheduleContentItemBinding(@NonNull ConstraintLayout rootView,
      @NonNull TextView animeDescription, @NonNull ImageView animeImage,
      @NonNull TextView animeTitle, @NonNull LinearLayout animeTitleLayout,
      @NonNull CardView cardView) {
    this.rootView = rootView;
    this.animeDescription = animeDescription;
    this.animeImage = animeImage;
    this.animeTitle = animeTitle;
    this.animeTitleLayout = animeTitleLayout;
    this.cardView = cardView;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ScheduleContentItemBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ScheduleContentItemBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.schedule_content_item, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ScheduleContentItemBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.anime_description;
      TextView animeDescription = ViewBindings.findChildViewById(rootView, id);
      if (animeDescription == null) {
        break missingId;
      }

      id = R.id.animeImage;
      ImageView animeImage = ViewBindings.findChildViewById(rootView, id);
      if (animeImage == null) {
        break missingId;
      }

      id = R.id.anime_Title;
      TextView animeTitle = ViewBindings.findChildViewById(rootView, id);
      if (animeTitle == null) {
        break missingId;
      }

      id = R.id.animeTitleLayout;
      LinearLayout animeTitleLayout = ViewBindings.findChildViewById(rootView, id);
      if (animeTitleLayout == null) {
        break missingId;
      }

      id = R.id.cardView;
      CardView cardView = ViewBindings.findChildViewById(rootView, id);
      if (cardView == null) {
        break missingId;
      }

      return new ScheduleContentItemBinding((ConstraintLayout) rootView, animeDescription,
          animeImage, animeTitle, animeTitleLayout, cardView);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
