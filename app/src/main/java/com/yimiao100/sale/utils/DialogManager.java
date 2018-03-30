package com.yimiao100.sale.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import com.blankj.utilcode.util.ToastUtils;
import com.yimiao100.sale.R;
import com.yimiao100.sale.base.ActivityCollector;
import com.yimiao100.sale.base.BaseActivity;
import com.yimiao100.sale.base.BaseFragment;
import com.yimiao100.sale.base.FragmentCollector;
import pub.devrel.easypermissions.EasyPermissions;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by michel on 2017/10/16.
 */
public class DialogManager implements EasyPermissions.PermissionCallbacks{

    private static volatile DialogManager INSTANCE;
    private File tempFile;
    private Uri uri;
    private final int PHOTO_REQUEST_CAMERA = 201;
    private final int PHOTO_REQUEST_GALLERY = 202;
    private final int PHOTO_REQUEST_CUT = 203;
    private final int RC_CAMERA = 301;
    private onPicCropListener listener;
    private View view;

    private DialogManager(){}

    public static DialogManager getInstance() {
        if (INSTANCE == null) {
            synchronized (DialogManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DialogManager();
                }
            }
        }
        return INSTANCE;
    }

    /*
    * 首先先调用showPicDialog()
    * 然后链式调用setOnPicCropListener()
    *
    * 然后重写Activity或者Fragment的onActivityResult(),调用onPicActivityResult()
    * 最后重写Activity或者Fragment的onRequestPermissionsResult(),调用自身的onRequestPermissionsResult()
    * */
    public DialogManager showPicDialog(View view) {
        this.view = view;
        final BaseActivity activity = ActivityCollector.getTopActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        CharSequence[] items = {activity.getString(R.string.open_camera), activity.getString(R.string.open_DCIM)};
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        openCamera(activity);
                        break;
                    case 1:
                        openGallery(activity);
                        break;
                }
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        return this;
    }

    /**
     * 打开相机
     * @param activity
     */
    private void openCamera(BaseActivity activity) {
        if (!EasyPermissions.hasPermissions(activity, Manifest.permission.CAMERA)) {
            if (activity.getSupportFragmentManager().getFragments() != null) {
                EasyPermissions.requestPermissions(FragmentCollector.getTopFragment(), activity.getString(R.string.rationale_camera), RC_CAMERA, Manifest.permission.CAMERA);
                return;
            } else {
                EasyPermissions.requestPermissions(activity, activity.getString(R.string.rationale_camera), 0, Manifest.permission.CAMERA);
                return;
            }
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String tempFileName = "tempFile" + System.currentTimeMillis() + ".jpg";
        tempFile = Util.createFile(tempFileName);
        // 从文件中创建uri
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ContentValues contentValues = new ContentValues(1);
            contentValues.put(MediaStore.Images.Media.DATA, tempFile.getAbsolutePath());
            uri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        } else {
            uri = Uri.fromFile(tempFile);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        if (activity.getSupportFragmentManager().getFragments() != null) {
            FragmentCollector.getTopFragment().startActivityForResult(intent, PHOTO_REQUEST_CAMERA);
        } else {
            activity.startActivityForResult(intent, PHOTO_REQUEST_CAMERA);
        }
    }


    /**
     * 打开相册
     * @param activity
     */
    private void openGallery(BaseActivity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // 7.0以上使用Provider
            String tempFileName = "tempFile" + System.currentTimeMillis() + ".jpg";
            tempFile = Util.createFile(tempFileName);
            Uri uriForFile = FileProvider.getUriForFile(activity, "com.yimiao100.sale.fileprovider", tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        if (activity.getSupportFragmentManager().getFragments() != null) {
            FragmentCollector.getTopFragment().startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
        } else {
            activity.startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
        }
    }

    public void onPicActivityResult(int requestCode, int resultCode, Intent data) {
        BaseActivity activity = ActivityCollector.getTopActivity();
        switch (requestCode) {
            case PHOTO_REQUEST_CAMERA:
                // 从相机返回
                LogUtil.d("result from camera");
                if (resultCode == Activity.RESULT_OK) {
                    crop(activity, uri);
                }
                break;
            case PHOTO_REQUEST_GALLERY:
                LogUtil.d("result from gallery");
                if (data != null) {
                    Uri uri;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        File imgUri = new File(GetImagePath.getPath(activity, data.getData()));
                        uri = FileProvider.getUriForFile(activity, "com.yimiao100.sale.fileprovider", imgUri);
                    } else {
                        uri = data.getData();
                    }
                    crop(activity, uri);
                }
                break;
            case PHOTO_REQUEST_CUT:
                // 从Crop返回
                LogUtil.d("result from crop");
                if (data != null && resultCode == Activity.RESULT_OK) {
                    Bitmap bitmap = null;
                    if (data.getData() != null) {
                        try {
                            bitmap = BitmapUtil.getBitmapFormUri(activity, data.getData());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        bitmap = data.getParcelableExtra("data");
                    }

                    if (bitmap == null) {
                        ToastUtils.showShort("unknown error");
                    } else {
                        if (listener != null) {
                            listener.handleBitmap(view, bitmap);
                        }
                    }
                }
                break;
        }
    }

    private void crop(BaseActivity activity, Uri uri) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setDataAndType(uri, "image/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                String url = GetImagePath.getPath(activity, uri);
                intent.setDataAndType(Uri.fromFile(new File(url)), "image/*");
            } else {
                intent.setDataAndType(uri, "image/*");
            }
        }

        Uri outPutUri = Uri.fromFile(tempFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutUri);

        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);

        intent.putExtra("outputFormat", "JPEG");// 图片格式
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);

        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CUT
        if (activity.getSupportFragmentManager().getFragments() != null) {
            FragmentCollector.getTopFragment().startActivityForResult(intent, PHOTO_REQUEST_CUT);
        } else {
            activity.startActivityForResult(intent, PHOTO_REQUEST_CUT);
        }
    }

    public void setOnPicCropListener(onPicCropListener listener) {
        this.listener = listener;
    }

    public interface onPicCropListener {
        void handleBitmap(View view, Bitmap bitmap);
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        LogUtil.e("onPermissionsGranted");
        openCamera(ActivityCollector.getTopActivity());
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        LogUtil.e("onPermissionsDenied");
        BaseActivity activity = ActivityCollector.getTopActivity();

        if (activity.getSupportFragmentManager().getFragments() != null) {
            BaseFragment fragment = FragmentCollector.getTopFragment();
            if (EasyPermissions.somePermissionPermanentlyDenied(fragment, perms)) {
                // 如果用户选择了不再询问，则要去设置界面打开权限
                LogUtil.d("用户选择了不再询问-fragment");
                fragment.showSettingDialog();
            }
        } else {
            if (EasyPermissions.somePermissionPermanentlyDenied(activity, perms)) {
                // 如果用户选择了不再询问，则要去设置界面打开权限
                LogUtil.d("用户选择了不再询问-activity");
                activity.showSettingDialog();
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}
