// Generated by view binder compiler. Do not edit!
package com.example.blinkanime.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.blinkanime.R;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class AnimeDetailsEpBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final RecyclerView episodesListView;

  @NonNull
  public final CircularProgressIndicator progressCircular;

  private AnimeDetailsEpBinding(@NonNull ConstraintLayout rootView,
      @NonNull RecyclerView episodesListView, @NonNull CircularProgressIndicator progressCircular) {
    this.rootView = rootView;
    this.episodesListView = episodesListView;
    this.progressCircular = progressCircular;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static AnimeDetailsEpBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static AnimeDetailsEpBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.anime_details_ep, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static AnimeDetailsEpBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.episodes_list_view;
      RecyclerView episodesListView = ViewBindings.findChildViewById(rootView, id);
      if (episodesListView == null) {
        break missingId;
      }

      id = R.id.progress_circular;
      CircularProgressIndicator progressCircular = ViewBindings.findChildViewById(rootView, id);
      if (progressCircular == null) {
        break missingId;
      }

      return new AnimeDetailsEpBinding((ConstraintLayout) rootView, episodesListView,
          progressCircular);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
