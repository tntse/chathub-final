package edu.sfsu.csc780.chathub.ui;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;

import edu.sfsu.csc780.chathub.R;

/**
 * Created by thomastse on 11/2/16.
 */

public class ImageDialogFragment extends DialogFragment {

    public static final String PHOTO_BITMAP = "Photo";
    public static final int SIDE_MARGIN = 0;
    private Bitmap mPhotoBitmap;
    private int mScaledWidth;
    private int mScaledHeight;

    static ImageDialogFragment newInstance(Bitmap bitmap) {
        ImageDialogFragment f = new ImageDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(PHOTO_BITMAP, bitmap);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPhotoBitmap = getArguments().getParcelable(PHOTO_BITMAP);
        int imageWidth = mPhotoBitmap.getWidth();
        int imageHeight = mPhotoBitmap.getHeight();
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        mScaledWidth = (int)((double)width - SIDE_MARGIN);
        mScaledHeight = (int)((double)imageHeight / (double)imageWidth * mScaledWidth);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.image_dialog_layout, null);
        //TODO: 1.get the photoImageView from the root view
        ImageView mPhotoImageView = (ImageView)view.findViewById(R.id.photoImageView);

        //TODO: 2.set layout parameters of photoImageView to the dimensions that were set in...
        //TODO: ...the constructor

        mPhotoImageView.setMaxWidth(mScaledWidth);
        mPhotoImageView.setMaxHeight(mScaledHeight);

        builder.setView(view);
        //TODO: 3. set ImageBitmap of photoImageView to the bitmap from the constructor argument

        mPhotoImageView.setImageBitmap(mPhotoBitmap);

        final Dialog dialog = builder.create(); dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
        return dialog;
    }


}
