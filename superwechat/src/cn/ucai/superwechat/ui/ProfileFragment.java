/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.ucai.superwechat.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.redpacketui.utils.RedPacketUtil;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.utils.EaseUserUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.superwechat.Constant;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatHelper;
import cn.ucai.superwechat.utils.MFGT;

/**
 * settings screen
 *
 *
 */
@SuppressWarnings({"FieldCanBeLocal"})
public class ProfileFragment extends Fragment {

    @BindView(R.id.iv_profile_avatar)
    ImageView ivProfileAvatar;
    @BindView(R.id.iv_profile_nickname)
    TextView ivProfileNickname;
    @BindView(R.id.tv_profile_username)
    TextView tvProfileUsername;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false))
            return;
        initData();
    }

    private void initData() {
        String username = EMClient.getInstance().getCurrentUser();
        tvProfileUsername.setText("微信号"+username);
        EaseUserUtils.setAppUserNick(username,ivProfileNickname);
        EaseUserUtils.setAppUserAvatar(getContext(),username,ivProfileAvatar);

    }


//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//			//red packet code : 进入零钱页面
//			case R.id.ll_change:
//				RedPacketUtil.startChangeActivity(getActivity());
//				break;
//			//end of red packet code
//			case R.id.rl_switch_notification:
//				if (notifySwitch.isSwitchOpen()) {
//					notifySwitch.closeSwitch();
//					rl_switch_sound.setVisibility(View.GONE);
//					rl_switch_vibrate.setVisibility(View.GONE);
//					textview1.setVisibility(View.GONE);
//					textview2.setVisibility(View.GONE);
//					settingsModel.setSettingMsgNotification(false);
//				} else {
//					notifySwitch.openSwitch();
//					rl_switch_sound.setVisibility(View.VISIBLE);
//					rl_switch_vibrate.setVisibility(View.VISIBLE);
//					textview1.setVisibility(View.VISIBLE);
//					textview2.setVisibility(View.VISIBLE);
//					settingsModel.setSettingMsgNotification(true);
//				}
//				break;
//			case R.id.rl_switch_sound:
//				if (soundSwitch.isSwitchOpen()) {
//					soundSwitch.closeSwitch();
//					settingsModel.setSettingMsgSound(false);
//				} else {
//					soundSwitch.openSwitch();
//					settingsModel.setSettingMsgSound(true);
//				}
//				break;
//			case R.id.rl_switch_vibrate:
//				if (vibrateSwitch.isSwitchOpen()) {
//					vibrateSwitch.closeSwitch();
//					settingsModel.setSettingMsgVibrate(false);
//				} else {
//					vibrateSwitch.openSwitch();
//					settingsModel.setSettingMsgVibrate(true);
//				}
//				break;
//			case R.id.rl_switch_speaker:
//				if (speakerSwitch.isSwitchOpen()) {
//					speakerSwitch.closeSwitch();
//					settingsModel.setSettingMsgSpeaker(false);
//				} else {
//					speakerSwitch.openSwitch();
//					settingsModel.setSettingMsgVibrate(true);
//				}
//				break;
//			case R.id.rl_switch_chatroom_owner_leave:
//				if(ownerLeaveSwitch.isSwitchOpen()){
//					ownerLeaveSwitch.closeSwitch();
//					settingsModel.allowChatroomOwnerLeave(false);
//					chatOptions.allowChatroomOwnerLeave(false);
//				}else{
//					ownerLeaveSwitch.openSwitch();
//					settingsModel.allowChatroomOwnerLeave(true);
//					chatOptions.allowChatroomOwnerLeave(true);
//				}
//				break;
//			case R.id.rl_switch_delete_msg_when_exit_group:
//				if(switch_delete_msg_when_exit_group.isSwitchOpen()){
//					switch_delete_msg_when_exit_group.closeSwitch();
//					settingsModel.setDeleteMessagesAsExitGroup(false);
//					chatOptions.setDeleteMessagesAsExitGroup(false);
//				}else{
//					switch_delete_msg_when_exit_group.openSwitch();
//					settingsModel.setDeleteMessagesAsExitGroup(true);
//					chatOptions.setDeleteMessagesAsExitGroup(true);
//				}
//				break;
//			case R.id.rl_switch_auto_accept_group_invitation:
//				if(switch_auto_accept_group_invitation.isSwitchOpen()){
//					switch_auto_accept_group_invitation.closeSwitch();
//					settingsModel.setAutoAcceptGroupInvitation(false);
//					chatOptions.setAutoAcceptGroupInvitation(false);
//				}else{
//					switch_auto_accept_group_invitation.openSwitch();
//					settingsModel.setAutoAcceptGroupInvitation(true);
//					chatOptions.setAutoAcceptGroupInvitation(true);
//				}
//				break;
//			case R.id.rl_switch_adaptive_video_encode:
//				EMLog.d("switch", "" + !switch_adaptive_video_encode.isSwitchOpen());
//				if (switch_adaptive_video_encode.isSwitchOpen()){
//					switch_adaptive_video_encode.closeSwitch();
//					settingsModel.setAdaptiveVideoEncode(false);
//					EMClient.getInstance().callManager().getCallOptions().enableFixedVideoResolution(true);
//
//				}else{
//					switch_adaptive_video_encode.openSwitch();
//					settingsModel.setAdaptiveVideoEncode(true);
//					EMClient.getInstance().callManager().getCallOptions().enableFixedVideoResolution(false);
//				}
//				break;
//			case R.id.btn_logout:
//				logout();
//				break;
//			case R.id.ll_black_list:
//				startActivity(new Intent(getActivity(), BlacklistActivity.class));
//				break;
//			case R.id.ll_diagnose:
//				startActivity(new Intent(getActivity(), DiagnoseActivity.class));
//				break;
//			case R.id.ll_set_push_nick:
//				startActivity(new Intent(getActivity(), OfflinePushNickActivity.class));
//				break;
//			case R.id.ll_call_option:
//				startActivity(new Intent(getActivity(), CallOptionActivity.class));
//				break;
//			case R.id.ll_user_profile:
//				startActivity(new Intent(getActivity(), UserProfileActivity.class).putExtra("setting", true)
//						.putExtra("username", EMClient.getInstance().getCurrentUser()));
//				break;
//			case R.id.switch_custom_server:
//				if(customServerSwitch.isSwitchOpen()){
//					customServerSwitch.closeSwitch();
//					settingsModel.enableCustomServer(false);
//				}else{
//					customServerSwitch.openSwitch();
//					settingsModel.enableCustomServer(true);
//				}
//				break;
//			case R.id.switch_custom_appkey:
//				if(customAppkeySwitch.isSwitchOpen()){
//					customAppkeySwitch.closeSwitch();
//					settingsModel.enableCustomAppkey(false);
//				}else{
//					customAppkeySwitch.openSwitch();
//					settingsModel.enableCustomAppkey(true);
//				}
//				edit_custom_appkey.setEnabled(customAppkeySwitch.isSwitchOpen());
//				break;
//			case R.id.rl_custom_server:
//				startActivity(new Intent(getActivity(), SetServersActivity.class));
//				break;
//			case R.id.rl_push_settings:
//				startActivity(new Intent(getActivity(), OfflinePushSettingsActivity.class));
//				break;
//			default:
//				break;
//		}
//
//	}

    void logout() {
        final ProgressDialog pd = new ProgressDialog(getActivity());
        String st = getResources().getString(R.string.Are_logged_out);
        pd.setMessage(st);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        SuperWeChatHelper.getInstance().logout(false, new EMCallBack() {

            @Override
            public void onSuccess() {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        pd.dismiss();
                        // show login screen
                        ((MainActivity) getActivity()).finish();
                        startActivity(new Intent(getActivity(), LoginActivity.class));

                    }
                });
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        pd.dismiss();
                        Toast.makeText(getActivity(), "unbind devicetokens failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (((MainActivity) getActivity()).isConflict) {
            outState.putBoolean("isConflict", true);
        } else if (((MainActivity) getActivity()).getCurrentAccountRemoved()) {
            outState.putBoolean(Constant.ACCOUNT_REMOVED, true);
        }
    }

    @OnClick({R.id.layout_profile_view, R.id.tv_profile_money, R.id.iv_profile_settings})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_profile_view:
                break;
            //red packet code : 进入零钱页面
            case R.id.tv_profile_money:
                RedPacketUtil.startChangeActivity(getActivity());
                break;
            case R.id.iv_profile_settings:
                MFGT.gotoSettings(getActivity());
                break;
        }
    }
}
