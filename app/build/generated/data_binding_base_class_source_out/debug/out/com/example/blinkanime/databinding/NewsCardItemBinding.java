// Generated by view binder compiler. Do not edit!
package com.example.blinkanime.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.media3.ui.PlayerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.blinkanime.R;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class NewsCardItemBinding implements ViewBinding {
  @NonNull
  private final CardView rootView;

  @NonNull
  public final LinearLayout cardHeader;

  @NonNull
  public final CircularProgressIndicator circleProgress;

  @NonNull
  public final ConstraintLayout constraintLayout;

  @NonNull
  public final TextView createdAt;

  @NonNull
  public final TextView descriptionTextView;

  @NonNull
  public final LinearLayout imageContainer;

  @NonNull
  public final ImageView mainImageView;

  @NonNull
  public final ImageButton muteButton;

  @NonNull
  public final ImageView profileLogo;

  @NonNull
  public final TextView showLessTextView;

  @NonNull
  public final TextView showMoreTextView;

  @NonNull
  public final TextView titleTextView;

  @NonNull
  public final FrameLayout videoContainer;

  @NonNull
  public final PlayerView videoView;

  private NewsCardItemBinding(@NonNull CardView rootView, @NonNull LinearLayout cardHeader,
      @NonNull CircularProgressIndicator circleProgress, @NonNull ConstraintLayout constraintLayout,
      @NonNull TextView createdAt, @NonNull TextView descriptionTextView,
      @NonNull LinearLayout imageContainer, @NonNull ImageView mainImageView,
      @NonNull ImageButton muteButton, @NonNull ImageView profileLogo,
      @NonNull TextView showLessTextView, @NonNull TextView showMoreTextView,
      @NonNull TextView titleTextView, @NonNull FrameLayout videoContainer,
      @NonNull PlayerView videoView) {
    this.rootView = rootView;
    this.cardHeader = cardHeader;
    this.circleProgress = circleProgress;
    this.constraintLayout = constraintLayout;
    this.createdAt = createdAt;
    this.descriptionTextView = descriptionTextView;
    this.imageContainer = imageContainer;
    this.mainImageView = mainImageView;
    this.muteButton = muteButton;
    this.profileLogo = profileLogo;
    this.showLessTextView = showLessTextView;
    this.showMoreTextView = showMoreTextView;
    this.titleTextView = titleTextView;
    this.videoContainer = videoContainer;
    this.videoView = videoView;
  }

  @Override
  @NonNull
  public CardView getRoot() {
    return rootView;
  }

  @NonNull
  public static NewsCardItemBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static NewsCardItemBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.news_card_item, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static NewsCardItemBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.cardHeader;
      LinearLayout cardHeader = ViewBindings.findChildViewById(rootView, id);
      if (cardHeader == null) {
        break missingId;
      }

      id = R.id.circle_progress;
      CircularProgressIndicator circleProgress = ViewBindings.findChildViewById(rootView, id);
      if (circleProgress == null) {
        break missingId;
      }

      id = R.id.constraintLayout;
      ConstraintLayout constraintLayout = ViewBindings.findChildViewById(rootView, id);
      if (constraintLayout == null) {
        break missingId;
      }

      id = R.id.createdAt;
      TextView createdAt = ViewBindings.findChildViewById(rootView, id);
      if (createdAt == null) {
        break missingId;
      }

      id = R.id.descriptionTextView;
      TextView descriptionTextView = ViewBindings.findChildViewById(rootView, id);
      if (descriptionTextView == null) {
        break missingId;
      }

      id = R.id.imageContainer;
      LinearLayout imageContainer = ViewBindings.findChildViewById(rootView, id);
      if (imageContainer == null) {
        break missingId;
      }

      id = R.id.mainImageView;
      ImageView mainImageView = ViewBindings.findChildViewById(rootView, id);
      if (mainImageView == null) {
        break missingId;
      }

      id = R.id.mute_button;
      ImageButton muteButton = ViewBindings.findChildViewById(rootView, id);
      if (muteButton == null) {
        break missingId;
      }

      id = R.id.profileLogo;
      ImageView profileLogo = ViewBindings.findChildViewById(rootView, id);
      if (profileLogo == null) {
        break missingId;
      }

      id = R.id.showLessTextView;
      TextView showLessTextView = ViewBindings.findChildViewById(rootView, id);
      if (showLessTextView == null) {
        break missingId;
      }

      id = R.id.showMoreTextView;
      TextView showMoreTextView = ViewBindings.findChildViewById(rootView, id);
      if (showMoreTextView == null) {
        break missingId;
      }

      id = R.id.titleTextView;
      TextView titleTextView = ViewBindings.findChildViewById(rootView, id);
      if (titleTextView == null) {
        break missingId;
      }

      id = R.id.videoContainer;
      FrameLayout videoContainer = ViewBindings.findChildViewById(rootView, id);
      if (videoContainer == null) {
        break missingId;
      }

      id = R.id.videoView;
      PlayerView videoView = ViewBindings.findChildViewById(rootView, id);
      if (videoView == null) {
        break missingId;
      }

      return new NewsCardItemBinding((CardView) rootView, cardHeader, circleProgress,
          constraintLayout, createdAt, descriptionTextView, imageContainer, mainImageView,
          muteButton, profileLogo, showLessTextView, showMoreTextView, titleTextView,
          videoContainer, videoView);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
