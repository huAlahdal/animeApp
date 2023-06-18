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
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.blinkanime.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class WidgetViewItemBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final ImageView animeImage;

  @NonNull
  public final TextView episodeNumber;

  @NonNull
  public final TextView episodeTitle;

  private WidgetViewItemBinding(@NonNull LinearLayout rootView, @NonNull ImageView animeImage,
      @NonNull TextView episodeNumber, @NonNull TextView episodeTitle) {
    this.rootView = rootView;
    this.animeImage = animeImage;
    this.episodeNumber = episodeNumber;
    this.episodeTitle = episodeTitle;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static WidgetViewItemBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static WidgetViewItemBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.widget_view_item, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static WidgetViewItemBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.anime_image;
      ImageView animeImage = ViewBindings.findChildViewById(rootView, id);
      if (animeImage == null) {
        break missingId;
      }

      id = R.id.episode_number;
      TextView episodeNumber = ViewBindings.findChildViewById(rootView, id);
      if (episodeNumber == null) {
        break missingId;
      }

      id = R.id.episode_title;
      TextView episodeTitle = ViewBindings.findChildViewById(rootView, id);
      if (episodeTitle == null) {
        break missingId;
      }

      return new WidgetViewItemBinding((LinearLayout) rootView, animeImage, episodeNumber,
          episodeTitle);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
