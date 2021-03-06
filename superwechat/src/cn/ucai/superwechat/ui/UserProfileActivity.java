package cn.ucai.superwechat.ui;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.utils.EaseImageUtils;
import com.hyphenate.easeui.utils.EaseUserUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatHelper;
import cn.ucai.superwechat.domain.Result;
import cn.ucai.superwechat.net.NetDao;
import cn.ucai.superwechat.net.OnCompleteListener;
import cn.ucai.superwechat.utils.CommonUtils;
import cn.ucai.superwechat.utils.L;
import cn.ucai.superwechat.utils.MFGT;
import cn.ucai.superwechat.utils.OkHttpUtils;
import cn.ucai.superwechat.utils.PreferenceManager;
import cn.ucai.superwechat.utils.ResultUtils;

public class UserProfileActivity extends BaseActivity implements OnClickListener {
    private static final String TAG = UserProfileActivity.class.getSimpleName();

    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.Avatar)
    RelativeLayout Avatar;
    @BindView(R.id.tvNickName)
    TextView tvNickName;
    @BindView(R.id.tvUsername)
    TextView tvUsername;
    @BindView(R.id.ivQrcode)
    ImageView ivQrcode;
    @BindView(R.id.tvAddress)
    TextView tvAddress;
    @BindView(R.id.tvGender)
    TextView tvGender;
    @BindView(R.id.tvArea)
    TextView tvArea;
    @BindView(R.id.tvLogo)
    TextView tvLogo;
    @BindView(R.id.user_head_avatar)
    ImageView userHeadAvatar;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.em_activity_user_profile);
        ButterKnife.bind(this);
        initView();
        initListener();
    }

    private void initView() {
        imgBack.setVisibility(View.VISIBLE);
        txtTitle.setVisibility(View.VISIBLE);
        txtTitle.setText(R.string.title_user_profile);
    }

    private void initListener() {
        String username = EMClient.getInstance().getCurrentUser();
        tvUsername.setText(username);
        EaseUserUtils.setUserNick(username, tvNickName);
        EaseUserUtils.setUserAvatar(this, username, userHeadAvatar);
        asyncFetchUserInfo(username);
    }

    public void asyncFetchUserInfo(String username) {
        NetDao.getUserInfoByUsername(this, username, new OnCompleteListener<String>() {
            @Override
            public void onSuccess(String s) {
                if (s != null) {
                    Result result = ResultUtils.getResultFromJson(s, User.class);
                    if (result != null) {
                        if (result.isRetMsg()) {
                            L.e("UserProfileManager", "asyncGetCurrentUserInfo,result=" + result);
                            User user = (User) result.getRetData();
                            L.e(TAG, "user=" + user);
                            if (user != null) {
                                // save user info to db
                                SuperWeChatHelper.getInstance().saveAppContact(user);
                                tvNickName.setText(user.getMUserNick());
                                if (!TextUtils.isEmpty(user.getAvatar())) {
                                    Glide.with(UserProfileActivity.this).load(user.getAvatar()).placeholder(R.drawable.em_default_avatar).into(userHeadAvatar);
                                } else {
                                    Glide.with(UserProfileActivity.this).load(R.drawable.em_default_avatar).into(userHeadAvatar);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }


    private void uploadHeadPhoto() {
        Builder builder = new Builder(this);
        builder.setTitle(R.string.dl_title_upload_photo);
        builder.setItems(new String[]{getString(R.string.dl_msg_take_photo), getString(R.string.dl_msg_local_upload)},
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        switch (which) {
                            case 0:
                                Toast.makeText(UserProfileActivity.this, getString(R.string.toast_no_support),
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                                pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                                startActivityForResult(pickIntent, I.REQUESTCODE_PICK);
                                break;
                            default:
                                break;
                        }
                    }
                });
        builder.create().show();
    }

    private void updateRemoteNick(final String nickName) {
        dialog = ProgressDialog.show(this, getString(R.string.dl_update_nick), getString(R.string.dl_waiting));
        NetDao.updateUserNick(this, EMClient.getInstance().getCurrentUser(), nickName,
                new OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String res) {
                        dialog.dismiss();
                        if (res != null) {
                            Result result = ResultUtils.getResultFromJson(res, User.class);
                            if (result != null) {
                                if (result.isRetMsg()) {
                                    User user = (User) result.getRetData();
                                    if (user != null) {
                                        L.e(TAG, "user=" + user);
                                        PreferenceManager.getInstance().setCurrentUserNick(nickName);
                                        SuperWeChatHelper.getInstance().saveAppContact(user);
                                        tvNickName.setText(nickName);
                                        CommonUtils.showShortToast(R.string.toast_updatenick_success);
                                    }
                                } else {
                                    if (result.getRetCode() == I.MSG_USER_SAME_NICK) {
                                        CommonUtils.showShortToast("昵称未修改");
                                    } else {
                                        CommonUtils.showShortToast(R.string.toast_updatenick_fail);
                                    }
                                }
                            } else {
                                CommonUtils.showShortToast(R.string.toast_updatenick_fail);
                            }
                        } else {
                            CommonUtils.showShortToast(R.string.toast_updatenick_fail);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        L.e(TAG, "error=" + error);
                        dialog.dismiss();
                        CommonUtils.showShortToast(R.string.toast_updatenick_fail);
                    }
                });
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                boolean updatenick = SuperWeChatHelper.getInstance().getUserProfileManager().updateCurrentUserNickName(nickName);
//                if (UserProfileActivity.this.isFinishing()) {
//                    return;
//                }
//                if (!updatenick) {
//                    runOnUiThread(new Runnable() {
//                        public void run() {
//                            Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatenick_fail), Toast.LENGTH_SHORT)
//                                    .show();
//                            dialog.dismiss();
//                        }
//                    });
//                } else {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            dialog.dismiss();
//                            Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatenick_success), Toast.LENGTH_SHORT)
//                                    .show();
//                            tvNickName.setText(nickName);
//                        }
//                    });
//                }
//            }
//        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case I.REQUESTCODE_PICK:
                if (data == null || data.getData() == null) {
                    return;
                }
                startPhotoZoom(data.getData());
                break;
            case I.REQUESTCODE_CUTTING:
                if (data != null) {
                    uploadAppUserAvatar(data);
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, I.REQUESTCODE_CUTTING);
    }

    /**
     * save the picture data
     *
     * @param picdata
     */
    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(getResources(), photo);
            userHeadAvatar.setImageDrawable(drawable);
            uploadUserAvatar(Bitmap2Bytes(photo));
        }

    }

    private void uploadAppUserAvatar(Intent picdata) {
        File file = saveBitmapFile(picdata);
        L.e(TAG, "file=" + file);
        if (file == null) {
            return;
        }
        L.e(TAG, "file=" + file.getAbsolutePath());
        dialog = ProgressDialog.show(this, getString(R.string.dl_update_photo), getString(R.string.dl_waiting));
        NetDao.uploadUserAvatar(this, EMClient.getInstance().getCurrentUser(), file, new OnCompleteListener<String>() {
            @Override
            public void onSuccess(String res) {
                L.e(TAG, "s=" + res);
                if (res != null) {
                    Result result = ResultUtils.getListResultFromJson(res, User.class);
                    if (result != null) {
                        if (result.isRetMsg()) {
                            User user = (User) result.getRetData();
                            if (user != null) {
                                PreferenceManager.getInstance().setCurrentUserAvatar(user.getAvatar());
                                SuperWeChatHelper.getInstance().saveAppContact(user);
                                EaseUserUtils.setAppUserAvatar(UserProfileActivity.this,
                                        user.getMUserName(), userHeadAvatar);
                                CommonUtils.showShortToast(R.string.toast_updatephoto_success);
                            }
                        }
                    }
                }
                dialog.dismiss();
            }

            @Override
            public void onError(String error) {
                L.e(TAG, "error=" + error);
                dialog.dismiss();
                CommonUtils.showShortToast(R.string.toast_updatenick_fail);
            }
        });

    }

    private File saveBitmapFile(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            String imagePath = EaseImageUtils.getImagePath(EMClient.getInstance().getCurrentUser() + I.AVATAR_SUFFIX_JPG);
            File file = new File(imagePath);
            L.e("file path=" + file.getAbsolutePath());
            try {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                photo.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                bos.flush();
                bos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return file;
        }
        return null;
    }


    private void uploadUserAvatar(final byte[] data) {
        dialog = ProgressDialog.show(this, getString(R.string.dl_update_photo), getString(R.string.dl_waiting));
        new Thread(new Runnable() {

            @Override
            public void run() {
                final String avatarUrl = SuperWeChatHelper.getInstance().getUserProfileManager().uploadUserAvatar(data);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        if (avatarUrl != null) {
                            Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatephoto_success),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatephoto_fail),
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        }).start();

        dialog.show();
    }

    public byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    @OnClick({R.id.img_back, R.id.Avatar, R.id.tvNickName, R.id.tvUsername, R.id.ivQrcode, R.id.tvAddress, R.id.tvGender, R.id.tvArea})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                MFGT.finishActivity(this);
                break;
            case R.id.Avatar:
                uploadHeadPhoto();
                break;
            case R.id.tvNickName:
                final EditText editText = new EditText(this);
                new Builder(this).setTitle(R.string.setting_nickname).setIcon(android.R.drawable.ic_dialog_info).setView(editText)
                        .setPositiveButton(R.string.dl_ok, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String nickString = editText.getText().toString();
                                if (TextUtils.isEmpty(nickString)) {
                                    Toast.makeText(UserProfileActivity.this, getString(R.string.toast_nick_not_isnull), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                updateRemoteNick(nickString);
                            }
                        }).setNegativeButton(R.string.dl_cancel, null).show();
                break;
            case R.id.tvUsername:
                break;
            case R.id.ivQrcode:
                break;
            case R.id.tvAddress:
                break;
            case R.id.tvGender:
                break;
            case R.id.tvArea:
                break;
        }
    }
}
