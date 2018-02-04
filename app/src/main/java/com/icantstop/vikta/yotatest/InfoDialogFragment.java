package com.icantstop.vikta.yotatest;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

public class InfoDialogFragment extends DialogFragment {

    private static final String TAG_VIDEO_LINK = "video";
    private static final String TAG_ARTICLE_LINK = "article";

    private ImageView mImageViewVideo;
    private ImageView mImageViewArticle;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_on_item_click, null);

        Bundle mArgs = getArguments();
        final String mVideoLink = mArgs.getString(TAG_VIDEO_LINK);
        final String mArticleLink = mArgs.getString(TAG_ARTICLE_LINK);

        mImageViewVideo = (ImageView) v.findViewById(R.id.image_view_video);
        mImageViewVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usingLinksByImplicitIntent(mVideoLink);
            }
        });

        mImageViewArticle = (ImageView) v.findViewById(R.id.image_video_article);
        mImageViewArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usingLinksByImplicitIntent(mArticleLink);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setView(v)
                .setTitle(R.string.info_dialog);
        return builder.create();
    }

    private void usingLinksByImplicitIntent(String link) {
        Uri  parsedLink= Uri.parse(link);
        Intent i = new Intent(Intent.ACTION_VIEW, parsedLink);
        startActivity(i);
    }
}
