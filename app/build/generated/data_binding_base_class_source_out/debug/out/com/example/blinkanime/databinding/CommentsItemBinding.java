// Generated by view binder compiler. Do not edit!
package com.example.blinkanime.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.blinkanime.R;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class CommentsItemBinding implements ViewBinding {
  @NonNull
  private final CardView rootView;

  @NonNull
  public final TextView episdoeComment;

  @NonNull
  public final TextView episdoeCommentDate;

  @NonNull
  public final TextView episodeCommentUsername;

  @NonNull
  public final CircularProgressIndicator progressCircular;

  @NonNull
  public final ImageView userImage;

  private CommentsItemBinding(@NonNull CardView rootView, @NonNull TextView episdoeComment,
      @NonNull TextView episdoeCommentDate, @NonNull TextView episodeCommentUsername,
      @NonNull CircularProgressIndicator progressCircular, @NonNull ImageView userImage) {
    this.rootView = rootView;
    this.episdoeComment = episdoeComment;
    this.episdoeCommentDate = episdoeCommentDate;
    this.episodeCommentUsername = episodeCommentUsername;
    this.progressCircular = progressCircular;
    this.userImage = userImage;
  }

  @Override
  @NonNull
  public CardView getRoot() {
    return rootView;
  }

  @NonNull
  public static CommentsItemBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static CommentsItemBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.comments_item, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static CommentsItemBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.episdoe_comment;
      TextView episdoeComment = ViewBindings.findChildViewById(rootView, id);
      if (episdoeComment == null) {
        break missingId;
      }

      id = R.id.episdoe_comment_date;
      TextView episdoeCommentDate = ViewBindings.findChildViewById(rootView, id);
      if (episdoeCommentDate == null) {
        break missingId;
      }

      id = R.id.episode_comment_username;
      TextView episodeCommentUsername = ViewBindings.findChildViewById(rootView, id);
      if (episodeCommentUsername == null) {
        break missingId;
      }

      id = R.id.progress_circular;
      CircularProgressIndicator progressCircular = ViewBindings.findChildViewById(rootView, id);
      if (progressCircular == null) {
        break missingId;
      }

      id = R.id.user_image;
      ImageView userImage = ViewBindings.findChildViewById(rootView, id);
      if (userImage == null) {
        break missingId;
      }

      return new CommentsItemBinding((CardView) rootView, episdoeComment, episdoeCommentDate,
          episodeCommentUsername, progressCircular, userImage);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
