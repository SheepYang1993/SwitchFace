package me.sheepyang.switchface.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.yalantis.ucrop.UCrop;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.sheepyang.switchface.R;
import me.sheepyang.switchface.utils.CropUtil;

import static me.sheepyang.switchface.activity.EditFrontActivity.INTENT_URI;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final int REQUEST_SELECT_FRONT_PICTURE = 0x01;
    private static final int REQUEST_SELECT_BACK_PICTURE = 0x02;
    private static final int REQUEST_CROP_FRONT_PICTURE = 0x03;
    private static final int REQUEST_CROP_BACK_PICTURE = 0x04;
    private static final int REQUEST_TO_EDIT_FRONT = 0x05;
    private static final int REQUEST_TO_EDIT_BACK = 0x06;
    private static final int REQUEST_TO_MERGE = 0x07;
    @BindView(R.id.iv_front)
    ImageView mIvFront;
    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.iv_merge)
    ImageView mIvMerge;
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
        mIvFront.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mFrontUri != null) {
                    showAlertDialog("图片裁剪", "是否要对前景图片进行裁剪？",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    CropUtil.startCropActivity((Activity) mContext, mFrontUri, REQUEST_CROP_FRONT_PICTURE, System.currentTimeMillis() + "_front.jpg");
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
        mIvBack.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mBackUri != null) {
                    showAlertDialog("图片裁剪", "是否要对背景图片进行裁剪？",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    CropUtil.startCropActivity((Activity) mContext, mBackUri, REQUEST_CROP_BACK_PICTURE, System.currentTimeMillis() + "_back.jpg");
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
//                        showAlertDialog("图片裁剪", "是否要对前景图片进行裁剪？",
//                                new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
                        CropUtil.startCropActivity((Activity) mContext, selectedUri, REQUEST_CROP_FRONT_PICTURE, System.currentTimeMillis() + "_front.jpg");
//                                    }
//                                }, "裁剪",
//                                new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        showPicture(mIvFront, selectedUri);
//                                    }
//                                }, "取消");
                    } else {
                        showToast("获取图片失败");
                    }
                }
                break;
            case REQUEST_SELECT_BACK_PICTURE:
                if (resultCode == RESULT_OK) {
                    final Uri selectedUri = data.getData();
                    mBackUri = selectedUri;
                    if (selectedUri != null) {
//                        showAlertDialog("图片裁剪", "是否要对背景图片进行裁剪？",
//                                new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
                        CropUtil.startCropActivity((Activity) mContext, selectedUri, REQUEST_CROP_BACK_PICTURE, System.currentTimeMillis() + "_back.jpg");
//                                    }
//                                }, "裁剪",
//                                new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        showPicture(mIvBack, selectedUri);
//                                    }
//                                }, "取消");
                    } else {
                        showToast("获取图片失败");
                    }
                }
                break;
            case REQUEST_CROP_FRONT_PICTURE:
                if (resultCode == RESULT_OK) {
                    final Uri resultUri = UCrop.getOutput(data);
                    mFrontUri = resultUri;
                    if (resultUri != null) {
                        showPicture(mIvFront, resultUri);
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
                        showPicture(mIvBack, resultUri);
                    }
                } else if (resultCode == UCrop.RESULT_ERROR) {
                    handleCropError(data);
                }
                break;
            case REQUEST_TO_EDIT_FRONT://编辑前景图片后返回
                if (resultCode == RESULT_OK && data != null) {
                    final Uri resultUri = data.getParcelableExtra(EditFrontActivity.INTENT_URI);
                    mFrontUri = resultUri;
                    if (resultUri != null) {
                        showPicture(mIvFront, resultUri);
                    }
                }
                break;
            case REQUEST_TO_MERGE://融合后返回
                if (resultCode == RESULT_OK && data != null) {

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
                    intent.putExtra(INTENT_URI, mFrontUri);
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
                if (mFrontUri == null) {
                    showToast("请选择前景图片");
                    return;
                }
                if (mBackUri == null) {
                    showToast("请选择背景图片");
                    return;
                }
                Intent intent = new Intent(this, MergeActivity.class);
                intent.putExtra(MergeActivity.INTENT_FRONT_URI, mFrontUri);
                intent.putExtra(MergeActivity.INTENT_BACK_URI, mBackUri);
                startActivityForResult(intent, REQUEST_TO_MERGE);
                break;
            case R.id.iv_merge:
                showToast("融合的图片");
                break;
            case R.id.btn_share:
                showToast("分享图片暂未开放~");
                break;
            default:
                break;
        }
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    private void handleCropError(@NonNull Intent result) {
        final Throwable cropError = UCrop.getError(result);
        if (cropError != null) {
            Log.e("SheepYang", "handleCropError: ", cropError);
            showToast(cropError.getMessage());
        } else {
            showToast("图片裁剪异常");
        }
    }
}
