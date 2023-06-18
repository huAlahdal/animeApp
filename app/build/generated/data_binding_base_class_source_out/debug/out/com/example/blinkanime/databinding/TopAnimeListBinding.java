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
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.blinkanime.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class TopAnimeListBinding implements ViewBinding {
  @NonNull
  private final CardView rootView;

  @NonNull
  public final ImageView animeImage;

  @NonNull
  public final TextView animeLength;

  @NonNull
  public final TextView animeName;

  @NonNull
  public final TextView animePopularity;

  @NonNull
  public final TextView animeRank;

  @NonNull
  public final TextView animeScore;

  @NonNull
  public final TextView animeSource;

  @NonNull
  public final TextView animeStatus;

  @NonNull
  public final TextView animeType;

  @NonNull
  public final LinearLayout topAnimeList;

  private TopAnimeListBinding(@NonNull CardView rootView, @NonNull ImageView animeImage,
      @NonNull TextView animeLength, @NonNull TextView animeName, @NonNull TextView animePopularity,
      @NonNull TextView animeRank, @NonNull TextView animeScore, @NonNull TextView animeSource,
      @NonNull TextView animeStatus, @NonNull TextView animeType,
      @NonNull LinearLayout topAnimeList) {
    this.rootView = rootView;
    this.animeImage = animeImage;
    this.animeLength = animeLength;
    this.animeName = animeName;
    this.animePopularity = animePopularity;
    this.animeRank = animeRank;
    this.animeScore = animeScore;
    this.animeSource = animeSource;
    this.animeStatus = animeStatus;
    this.animeType = animeType;
    this.topAnimeList = topAnimeList;
  }

  @Override
  @NonNull
  public CardView getRoot() {
    return rootView;
  }

  @NonNull
  public static TopAnimeListBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static TopAnimeListBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.top_anime_list, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static TopAnimeListBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.anime_image;
      ImageView animeImage = ViewBindings.findChildViewById(rootView, id);
      if (animeImage == null) {
        break missingId;
      }

      id = R.id.anime_length;
      TextView animeLength = ViewBindings.findChildViewById(rootView, id);
      if (animeLength == null) {
        break missingId;
      }

      id = R.id.anime_name;
      TextView animeName = ViewBindings.findChildViewById(rootView, id);
      if (animeName == null) {
        break missingId;
      }

      id = R.id.anime_popularity;
      TextView animePopularity = ViewBindings.findChildViewById(rootView, id);
      if (animePopularity == null) {
        break missingId;
      }

      id = R.id.anime_rank;
      TextView animeRank = ViewBindings.findChildViewById(rootView, id);
      if (animeRank == null) {
        break missingId;
      }

      id = R.id.anime_score;
      TextView animeScore = ViewBindings.findChildViewById(rootView, id);
      if (animeScore == null) {
        break missingId;
      }

      id = R.id.anime_source;
      TextView animeSource = ViewBindings.findChildViewById(rootView, id);
      if (animeSource == null) {
        break missingId;
      }

      id = R.id.anime_status;
      TextView animeStatus = ViewBindings.findChildViewById(rootView, id);
      if (animeStatus == null) {
        break missingId;
      }

      id = R.id.anime_type;
      TextView animeType = ViewBindings.findChildViewById(rootView, id);
      if (animeType == null) {
        break missingId;
      }

      id = R.id.top_anime_list;
      LinearLayout topAnimeList = ViewBindings.findChildViewById(rootView, id);
      if (topAnimeList == null) {
        break missingId;
      }

      return new TopAnimeListBinding((CardView) rootView, animeImage, animeLength, animeName,
          animePopularity, animeRank, animeScore, animeSource, animeStatus, animeType,
          topAnimeList);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
