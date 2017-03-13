package me.sheepyang.switchface;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_SELECT_FRONT_PICTURE = 0x01;
    private static final int REQUEST_SELECT_BACK_PICTURE = 0x02;
    private static final int REQUEST_CROP_FRONT_PICTURE = 0x03;
    private static final int REQUEST_CROP_BACK_PICTURE = 0x04;
    private static final int REQUEST_TO_EDIT_FRONT = 0x05;
    private static final int REQUEST_TO_EDIT_BACK = 0x06;
    @BindView(R.id.iv_front)
    ImageView ivFront;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_merge)
    ImageView ivMerge;
    private int tempRequestCode;
    private Uri mFrontUri;
    private Uri mBackUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initListener();
    }

    private void initListener() {
        ivFront.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mFrontUri != null) {
                    showAlertDialog("图片裁剪", "是否要对前景图片进行裁剪？",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startCropActivity(mFrontUri, REQUEST_CROP_FRONT_PICTURE, System.currentTimeMillis() + "_front.jpg");
                                }
                            }, "裁剪",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }, "取消");
                }
                return true;
            }
        });
        ivBack.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mBackUri != null) {
                    showAlertDialog("图片裁剪", "是否要对背景图片进行裁剪？",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startCropActivity(mBackUri, REQUEST_CROP_BACK_PICTURE, System.currentTimeMillis() + "_back.jpg");
                                }
                            }, "裁剪",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }, "取消");
                }
                return true;
            }
        });
    }

    private void pickFromGallery(int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            tempRequestCode = requestCode;
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                    "读取存储内容，获取照片",
                    REQUEST_STORAGE_READ_ACCESS_PERMISSION);
        } else {
            String msg = "";
            switch (requestCode) {
                case REQUEST_SELECT_FRONT_PICTURE:
                    msg = "前景";
                    break;
                case REQUEST_SELECT_BACK_PICTURE:
                    msg = "背景";
                    break;
                default:
                    break;
            }
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_PICK);
//            intent.setAction(Intent.ACTION_GET_CONTENT);
//            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, "选择" + msg + "照片"), requestCode);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE_READ_ACCESS_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickFromGallery(tempRequestCode);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_SELECT_FRONT_PICTURE:
                if (resultCode == RESULT_OK) {
                    final Uri selectedUri = data.getData();
                    mFrontUri = selectedUri;
                    if (selectedUri != null) {
                        showAlertDialog("图片裁剪", "是否要对前景图片进行裁剪？",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startCropActivity(selectedUri, REQUEST_CROP_FRONT_PICTURE, System.currentTimeMillis() + "_front.jpg");
                                    }
                                }, "裁剪",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        showPicture(ivFront, selectedUri);
                                    }
                                }, "取消");
                    } else {
                        Toast.makeText(MainActivity.this, "获取图片失败", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case REQUEST_SELECT_BACK_PICTURE:
                if (resultCode == RESULT_OK) {
                    final Uri selectedUri = data.getData();
                    mBackUri = selectedUri;
                    if (selectedUri != null) {
                        showAlertDialog("图片裁剪", "是否要对背景图片进行裁剪？",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startCropActivity(selectedUri, REQUEST_CROP_BACK_PICTURE, System.currentTimeMillis() + "_back.jpg");
                                    }
                                }, "裁剪",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        showPicture(ivBack, selectedUri);
                                    }
                                }, "取消");
                    } else {
                        Toast.makeText(MainActivity.this, "获取图片失败", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case REQUEST_CROP_FRONT_PICTURE:
                if (resultCode == RESULT_OK) {
                    final Uri resultUri = UCrop.getOutput(data);
                    mFrontUri = resultUri;
                    if (resultUri != null) {
                        showPicture(ivFront, resultUri);
                    }
                } else if (resultCode == UCrop.RESULT_ERROR) {
                    handleCropError(data);
                }
                break;
            case REQUEST_CROP_BACK_PICTURE:
                if (resultCode == RESULT_OK) {
                    final Uri resultUri = UCrop.getOutput(data);
                    mBackUri = resultUri;
                    if (resultUri != null) {
                        showPicture(ivBack, resultUri);
                    }
                } else if (resultCode == UCrop.RESULT_ERROR) {
                    handleCropError(data);
                }
                break;
            default:
                break;
        }
    }

    private void showPicture(ImageView view, Uri uri) {
        Glide.with(this)
                .load(uri)
                .centerCrop()
                .placeholder(R.drawable.select_image)
                .into(view);
    }

    private void startCropActivity(@NonNull Uri uri, int resquestCode, String fileName) {
        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), fileName)));

        //使用图片纵横比
        uCrop = uCrop.useSourceImageAspectRatio();
        uCrop = advancedConfig(uCrop);

        uCrop.start(MainActivity.this, resquestCode);
    }

    /**
     * Sometimes you want to adjust more options, it's done via {@link com.yalantis.ucrop.UCrop.Options} class.
     *
     * @param uCrop - ucrop builder instance
     * @return - ucrop builder instance
     */
    private UCrop advancedConfig(@NonNull UCrop uCrop) {
        UCrop.Options options = new UCrop.Options();
        //图片格式
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        //图片压缩质量
        options.setCompressionQuality(100);
        options.setFreeStyleCropEnabled(true);

//        /*
//        If you want to configure how gestures work for all UCropActivity tabs

        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL);
//        * */

        /*
        This sets max size for bitmap that will be decoded from source Uri.
        More size - more memory allocation, default implementation uses screen diagonal.

        options.setMaxBitmapSize(640);
        * */


       /*

        Tune everything (ﾉ◕ヮ◕)ﾉ*:･ﾟ✧

        options.setMaxScaleMultiplier(5);
        options.setImageToCropBoundsAnimDuration(666);
        options.setDimmedLayerColor(Color.CYAN);
        options.setCircleDimmedLayer(true);
        options.setShowCropFrame(false);
        options.setCropGridStrokeWidth(20);
        options.setCropGridColor(Color.GREEN);
        options.setCropGridColumnCount(2);
        options.setCropGridRowCount(1);
        options.setToolbarCropDrawable(R.drawable.your_crop_icon);
        options.setToolbarCancelDrawable(R.drawable.your_cancel_icon);

        // Color palette
        options.setToolbarColor(ContextCompat.getColor(this, R.color.your_color_res));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.your_color_res));
        options.setActiveWidgetColor(ContextCompat.getColor(this, R.color.your_color_res));
        options.setToolbarWidgetColor(ContextCompat.getColor(this, R.color.your_color_res));
        options.setRootViewBackgroundColor(ContextCompat.getColor(this, R.color.your_color_res));

        // Aspect ratio options
        options.setAspectRatioOptions(1,
            new AspectRatio("WOW", 1, 2),
            new AspectRatio("MUCH", 3, 4),
            new AspectRatio("RATIO", CropImageView.DEFAULT_ASPECT_RATIO, CropImageView.DEFAULT_ASPECT_RATIO),
            new AspectRatio("SO", 16, 9),
            new AspectRatio("ASPECT", 1, 1));

       */

        return uCrop.withOptions(options);
    }

    @Override
    @OnClick({R.id.btn_select_front, R.id.btn_select_back, R.id.iv_front, R.id.iv_back, R.id.btn_merge, R.id.iv_merge, R.id.btn_share})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_select_front:
                pickFromGallery(REQUEST_SELECT_FRONT_PICTURE);
                break;
            case R.id.btn_select_back:
                pickFromGallery(REQUEST_SELECT_BACK_PICTURE);
                break;
            case R.id.iv_front:
                if (mFrontUri == null) {
                    pickFromGallery(REQUEST_SELECT_FRONT_PICTURE);
                } else {
                    Intent intent = new Intent(this, EditFrontActivity.class);
                    intent.putExtra(EditFrontActivity.INTENT_URI, mFrontUri);
                    startActivityForResult(intent, REQUEST_TO_EDIT_FRONT);
                }
                break;
            case R.id.iv_back:
                if (mBackUri == null) {
                    pickFromGallery(REQUEST_SELECT_BACK_PICTURE);
                } else {
                    Intent intent = new Intent(this, EditBackActivity.class);
                    intent.putExtra(EditBackActivity.INTENT_URI, mFrontUri);
                    startActivityForResult(intent, REQUEST_TO_EDIT_BACK);
                }
                break;
            case R.id.btn_merge:
                Toast.makeText(MainActivity.this, "进行融合,暂未开放~", Toast.LENGTH_SHORT).show();
                break;
            case R.id.iv_merge:
                Toast.makeText(MainActivity.this, "融合的图片", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_share:
                Toast.makeText(MainActivity.this, "分享图片暂未开放~", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    private void handleCropError(@NonNull Intent result) {
        final Throwable cropError = UCrop.getError(result);
        if (cropError != null) {
            Log.e(TAG, "handleCropError: ", cropError);
            Toast.makeText(MainActivity.this, cropError.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this, "图片裁剪异常", Toast.LENGTH_SHORT).show();
        }
    }
}
