package cn.ucai.superwechat.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.utils.EaseUserUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatHelper;
import cn.ucai.superwechat.domain.Result;
import cn.ucai.superwechat.net.NetDao;
import cn.ucai.superwechat.net.OnCompleteListener;
import cn.ucai.superwechat.utils.L;
import cn.ucai.superwechat.utils.MFGT;
import cn.ucai.superwechat.utils.ResultUtils;

public class FriendProfileActivity extends BaseActivity {

    @BindView(R.id.img_back)
    ImageView mImgBack;
    @BindView(R.id.txt_title)
    TextView mTxtTitle;
    @BindView(R.id.profile_image)
    ImageView mProfileImage;
    @BindView(R.id.tv_userinfo_nick)
    TextView mTvUserinfoNick;
    @BindView(R.id.tv_userinfo_name)
    TextView mTvUserinfoName;
    @BindView(R.id.btn_add_contact)
    Button mBtnAddContact;
    @BindView(R.id.btn_send_msg)
    Button mBtnSendMsg;
    @BindView(R.id.btn_send_video)
    Button mBtnSendVideo;
    User user = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        mImgBack.setVisibility(View.VISIBLE);
        mTxtTitle.setVisibility(View.VISIBLE);
        mTxtTitle.setText(R.string.userinfo_txt_profile);
        user = (User) getIntent().getSerializableExtra(I.User.TABLE_NAME);
        L.e("FriendPro", "user=" + user);
        if (user != null) {
            showUserInfo();
        } else {
            String username = getIntent().getStringExtra(I.User.USER_NAME);
            if (username == null) {
                MFGT.finishActivity(this);
            } else {
                syncUserInfo(username);
            }
        }
    }

    private void syncUserInfo(String username) {
        NetDao.getUserInfoByUsername(this, username, new OnCompleteListener<String>() {
            @Override
            public void onSuccess(String res) {
                if (res != null) {
                    Result result = ResultUtils.getResultFromJson(res, User.class);
                    if (result != null) {
                        if (result.isRetMsg()) {
                            User u = (User) result.getRetData();
                            if (u != null) {
                                user = u;
                                showUserInfo();
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

    private void showUserInfo() {
        L.e("FriendPro", "showUserInfo" + user);
        mTvUserinfoNick.setText(user.getMUserNick());
        EaseUserUtils.setAppUserAvatarByPath(this, user.getAvatar(), mProfileImage);
        mTvUserinfoName.setText("微信号：" + user.getMUserName());
        if (isFriend()) {
            mBtnSendMsg.setVisibility(View.VISIBLE);
            mBtnSendVideo.setVisibility(View.VISIBLE);
        } else {
            mBtnAddContact.setVisibility(View.VISIBLE);
        }
    }

    private boolean isFriend() {
        User us = SuperWeChatHelper.getInstance().getAppContactList().get(user.getMUserName());
        L.e("FriendPro", "us=" + us);
        if (us == null) {
            return false;
        } else {
            SuperWeChatHelper.getInstance().saveAppContact(user);
            return true;
        }
    }


    @OnClick(R.id.img_back)
    public void imgBack() {
        finish();
    }

    @OnClick(R.id.btn_add_contact)
    public void sendAddContactMsg() {
        MFGT.gotoAddFriend(this, user.getMUserName());
    }

    @OnClick(R.id.btn_send_msg)
    public void sendMsg(View view) {
        MFGT.gotoChat(this, user.getMUserName());
    }
}
