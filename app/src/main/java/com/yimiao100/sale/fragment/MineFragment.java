package com.yimiao100.sale.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.header.progresslayout.ProgressLayout;
import com.squareup.picasso.Picasso;
import com.yimiao100.sale.R;
import com.yimiao100.sale.activity.BindPromotionActivity;
import com.yimiao100.sale.activity.CollectionActivity;
import com.yimiao100.sale.activity.IntegralActivity;
import com.yimiao100.sale.activity.IntegralShopActivity;
import com.yimiao100.sale.activity.LoginActivity;
import com.yimiao100.sale.activity.NoticeActivity;
import com.yimiao100.sale.activity.OrderActivity;
import com.yimiao100.sale.activity.PersonalSettingActivity;
import com.yimiao100.sale.activity.RichesActivity;
import com.yimiao100.sale.activity.StudyTaskActivity;
import com.yimiao100.sale.activity.VendorListActivity;
import com.yimiao100.sale.base.ActivityCollector;
import com.yimiao100.sale.base.BaseFragment;
import com.yimiao100.sale.bean.ErrorBean;
import com.yimiao100.sale.bean.ImageBean;
import com.yimiao100.sale.bean.UserAccountBean;
import com.yimiao100.sale.ext.JSON;
import com.yimiao100.sale.service.AliasService;
import com.yimiao100.sale.utils.AppUtil;
import com.yimiao100.sale.utils.BitmapUtil;
import com.yimiao100.sale.utils.Constant;
import com.yimiao100.sale.utils.DataUtil;
import com.yimiao100.sale.utils.FormatUtils;
import com.yimiao100.sale.utils.LogUtil;
import com.yimiao100.sale.utils.SharePreferenceUtil;
import com.yimiao100.sale.utils.ToastUtil;
import com.yimiao100.sale.utils.Util;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.lang.reflect.Field;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;


/**
 * 我的界面
 * Created by 亿苗通 on 2016/8/1.
 */
public class MineFragment extends BaseFragment implements View.OnClickListener {
    private View mView;
    private ImageView mMine_setting;
    private TextView mPromotion;
    private LinearLayout mLl_mine_notice;
    private LinearLayout mLl_mine_exit;

    private final String URL_LOGOUT = Constant.BASE_URL + "/api/user/logout";
    private final String BALANCE_ORDER = "balance_order";                   //对账订单
    private CircleImageView mMinePhoto;
    private String mUserIconUrl;
    private LinearLayout mLlMineCollection;
    private LinearLayout mLl_mine_order;
    private TextView mReconciliation;
    private TextView mMineAmount;
    private TextView mBankCount;
    private TextView mShop;
    private TextView mRiches;
    private LinearLayout mLlStudy;
    private LinearLayout mLlMore;
    private TextView mIntegral;
    private String mAccessToken;

    private File tempFile;
    private AlertDialog mDialog;
    /* 头像名称 */
    private static final String PHOTO_FILE_NAME = "temp_photo.jpg";
    private Uri mUri;
    private static final int PHOTO_REQUEST_CAMERA = 1;          // 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;         // 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;             // 结果
    private TwinklingRefreshLayout mRefreshLayout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        mView = View.inflate(getContext(), R.layout.fragment_mine, null);
        mAccessToken = (String) SharePreferenceUtil.get(getContext(), Constant.ACCESSTOKEN, "");

        initView();

        LogUtil.d("MineFragment:onCreateView");
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtil.d("MineFragment:onStart");
        //获取用户头像URL
        mUserIconUrl = (String) SharePreferenceUtil.get(getContext(), Constant.PROFILEIMAGEURL, "");
        //设置个人头像
        if (mUserIconUrl != null && !mUserIconUrl.isEmpty()) {
            Picasso.with(getContext()).load(mUserIconUrl).placeholder(R.mipmap
                    .ico_my_default_avatar).into(mMinePhoto);
        }
        initData();
    }

    private void initView() {
        mRefreshLayout = (TwinklingRefreshLayout) mView.findViewById(R.id.mine_refresh_layout);
        initRefreshLayout();
        //个人设置
        mMine_setting = (ImageView) mView.findViewById(R.id.mine_setting);
        //个人头像
        mMinePhoto = (CircleImageView) mView.findViewById(R.id.mine_photo);
        mMinePhoto.setOnClickListener(this);
        //三大跳转
        mView.findViewById(R.id.mine_ll_riches).setOnClickListener(this);
        mView.findViewById(R.id.mine_ll_promotion).setOnClickListener(this);
        mView.findViewById(R.id.mine_ll_integral).setOnClickListener(this);
        //账户余额
        mMineAmount = (TextView) mView.findViewById(R.id.mine_money);
        //银行卡数
        mBankCount = (TextView) mView.findViewById(R.id.mine_bank_card);
        //我的积分
        mIntegral = (TextView) mView.findViewById(R.id.mine_integral);
        //推广主体
        mPromotion = (TextView) mView.findViewById(R.id.promotion);
        //积分商城
        mShop = (TextView) mView.findViewById(R.id.shop);
        mShop.setOnClickListener(this);
        //财富查看
        mRiches = (TextView) mView.findViewById(R.id.riches);
        mRiches.setOnClickListener(this);
        //业务对账
        mReconciliation = (TextView) mView.findViewById(R.id.mine_reconciliation);

        //我的通知
        mLl_mine_notice = (LinearLayout) mView.findViewById(R.id.ll_mine_notice);
        //我的学习
        mLlStudy = (LinearLayout) mView.findViewById(R.id.ll_study);
        mLlStudy.setOnClickListener(this);
        //我的业务
        mLl_mine_order = (LinearLayout) mView.findViewById(R.id.ll_mine_order);
        //我的收藏
        mLlMineCollection = (LinearLayout) mView.findViewById(R.id.ll_mine_collection);
        //更多
        mLlMore = (LinearLayout) mView.findViewById(R.id.ll_more);
        mLlMore.setOnClickListener(this);
        mLlMore.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //获取当前版本号
                String versionName = AppUtil.getVersionName(getContext());
                ToastUtil.showShort(getContext(), "当前应用版本：V" + versionName);
                return false;
            }
        });
        //退出
        mLl_mine_exit = (LinearLayout) mView.findViewById(R.id.ll_mine_exit);
        mMine_setting.setOnClickListener(this);
        mPromotion.setOnClickListener(this);
        mLl_mine_notice.setOnClickListener(this);
        mLl_mine_exit.setOnClickListener(this);
        mLlMineCollection.setOnClickListener(this);
        mLl_mine_order.setOnClickListener(this);

        mReconciliation.setOnClickListener(this);
    }

    private void initRefreshLayout() {
        ProgressLayout header = new ProgressLayout(getActivity());
        header.setColorSchemeResources(
                android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        mRefreshLayout.setHeaderView(header);
        mRefreshLayout.setFloatRefresh(true);
        mRefreshLayout.setOverScrollRefreshShow(false);
        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);
                initData();
            }
        });
    }

    private void initData() {
        initAccountData();
        //显示账户总额
        double total_amount = Double.valueOf((String) SharePreferenceUtil.get(getContext(), Constant
                .TOTAL_AMOUNT, "0"));
        mMineAmount.setText("￥" + FormatUtils.MoneyFormat(total_amount));

        //显示积分
        int integral = (int) SharePreferenceUtil.get(getContext(), Constant.INTEGRAL, 0);
        mIntegral.setText(FormatUtils.NumberFormat(integral));
    }

    private void initAccountData() {//本地显示推广主体
        int promotionCount = 0;
        boolean corporateExit = (boolean) SharePreferenceUtil.get(getContext(), Constant
                .CORPORATE_EXIT, false);
        boolean personalExit = (boolean) SharePreferenceUtil.get(getContext(), Constant
                .PERSONAL_EXIT, false);
        if (corporateExit) {
            promotionCount += 1;
        }
        if (personalExit) {
            promotionCount += 1;
        }
        mBankCount.setText(promotionCount + "");
        // 联网刷新数据
        DataUtil.updateUserAccount(mAccessToken, mRefreshLayout, new DataUtil.onSuccessListener() {
            @Override
            public void echoData(UserAccountBean userAccount) {
                int promotionCount = 0;
                if (userAccount.getCorporate() != null) {
                    promotionCount += 1;
                }
                if (userAccount.getPersonal() != null) {
                    promotionCount += 1;
                }
                mBankCount.setText(promotionCount + "");
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mine_setting:
                //设置
                startActivity(new Intent(getContext(), PersonalSettingActivity.class));
                break;
            case R.id.mine_photo:
                //设置个人头像
                showImageDialog();
                break;
            case R.id.mine_ll_promotion:
            case R.id.promotion:
                //推广主体
                startActivity(new Intent(getContext(), BindPromotionActivity.class));
                break;
            case R.id.mine_ll_integral:
                //我的积分
                startActivity(new Intent(getContext(), IntegralActivity.class));
                break;
            case R.id.shop:
                //积分商城
                startActivity(new Intent(getContext(), IntegralShopActivity.class));
                break;
            case R.id.mine_ll_riches:
            case R.id.riches:
                //财富查看
                startActivity(new Intent(getContext(), RichesActivity.class));
                break;
            case R.id.mine_reconciliation:
                //业务对账
                Intent reconciliationIntent = new Intent(getContext(), VendorListActivity.class);
                reconciliationIntent.putExtra("moduleType", BALANCE_ORDER);
                startActivity(reconciliationIntent);
                break;
            case R.id.ll_mine_notice:
                //我的通知
                startActivity(new Intent(getContext(), NoticeActivity.class));
                break;
            case R.id.ll_study:
                //我的学习
                startActivity(new Intent(getContext(), StudyTaskActivity.class));
                break;
            case R.id.ll_mine_order:
                //我的业务
                startActivity(new Intent(getContext(), OrderActivity.class));
                break;
            case R.id.ll_mine_collection:
                //我的收藏
                startActivity(new Intent(getContext(), CollectionActivity.class));
                break;
            case R.id.ll_more:
                //更多
                ToastUtil.showShort(getContext(), "敬请期待");
                break;
            case R.id.ll_mine_exit:
                mLl_mine_exit.setEnabled(false);
                //退出
                LogOut();
                break;
        }
    }

    /**
     * 点击换头像
     */
    private void showImageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        CharSequence[] items = {"拍照", "从相册选择"};
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        //打开相机拍照,激活相机
                        Intent intentCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        tempFile = new File(Environment.getExternalStorageDirectory(),
                                PHOTO_FILE_NAME);
                        // 从文件中创建uri
                        mUri = Uri.fromFile(tempFile);
                        intentCapture.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
                        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CAMERA
                        startActivityForResult(intentCapture, PHOTO_REQUEST_CAMERA);
                        mDialog.dismiss();
                        break;
                    case 1:
                        //打开相册,激活系统图库，选择一张图片
                        Intent intentPick = new Intent(Intent.ACTION_PICK);
                        intentPick.setType("image/*");
                        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
                        startActivityForResult(intentPick, PHOTO_REQUEST_GALLERY);
                        mDialog.dismiss();
                        break;
                }
            }
        });
        mDialog = builder.create();
        mDialog.show();
    }

    /**
     * 剪切图片
     *
     * @param uri
     */
    private void crop(Uri uri) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
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
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PHOTO_REQUEST_GALLERY:// 从相册返回的数据
                if (data != null && resultCode == -1) {
                    Uri uri = data.getData();
                    crop(uri);
                }
                break;
            case PHOTO_REQUEST_CAMERA:
                // 从相机返回的数据
                if (resultCode == -1) {
                    //如果是选择图片返回数据，才进行图片剪切
                    crop(mUri);
                }
                break;
            case PHOTO_REQUEST_CUT:
                // 从剪切图片返回的数据
                if (data != null && resultCode == -1) {
                    Bitmap bitmap = data.getParcelableExtra("data");
                    //显示在本地
                    mMinePhoto.setImageBitmap(bitmap);
                    //保存在在SD卡中
                    File file = BitmapUtil.setPicToView(bitmap, "head.jpg");
                    /**
                     * 更新头像
                     */
                    String UPLOAD_PROFILE_IMAGE = "/api/user/upload_profile_image";
                    String url = Constant.BASE_URL + UPLOAD_PROFILE_IMAGE;
                    OkHttpUtils.post().url(url)
                            .addHeader(ACCESS_TOKEN, accessToken)
                            .addFile("profileImage", "head.jpg", file)
                            .build().execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            Util.showTimeOutNotice(getActivity());
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            LogUtil.d("更新头像" + response);
                            ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                            switch (errorBean.getStatus()) {
                                case "success":
                                    ImageBean imageBean = JSON.parseObject(response,
                                            ImageBean.class);
                                    //拿到头像的URL地址，更新本地数据
                                    String profileImageUrl = imageBean.getProfileImageUrl();
                                    Picasso.with(getContext()).load(profileImageUrl).placeholder
                                            (R.mipmap.ico_my_default_avatar).into(mMinePhoto);
                                    SharePreferenceUtil.put(getActivity(), Constant
                                            .PROFILEIMAGEURL, profileImageUrl);
                                    break;
                                case "failure":
                                    Util.showError(getActivity(), errorBean.getReason());
                                    break;
                            }
                        }
                    });
                    try {
                        // 将临时文件删除
                        tempFile.delete();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 退出登录
     */
    private void LogOut() {
        //获取网络状态
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null
                && connectivityManager.getActiveNetworkInfo() != null
                && connectivityManager.getActiveNetworkInfo().isAvailable()) {
            //如果联网，登出系统
            OkHttpUtils.post().url(URL_LOGOUT).addHeader(ACCESS_TOKEN, accessToken)
                    .build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    e.printStackTrace();
                    // 服务器异常时，照样退出
                    signOut();
                }

                @Override
                public void onResponse(String response, int id) {
                    LogUtil.d("退出" + response);
                    ErrorBean errorBean = JSON.parseObject(response, ErrorBean.class);
                    switch (errorBean.getStatus()) {
                        case "success":
                            // 成功退出
                            signOut();
                            break;
                        case "failure":
                            //显示错误信息
                            Util.showError(getActivity(), errorBean.getReason());
                            signOut();
                            break;
                    }
                }
            });
        } else {
            signOut();
        }

    }

    private void signOut() {
        mLl_mine_exit.setEnabled(true);
        // step1:清空本地数据
        SharePreferenceUtil.clear(getContext());
        // step2:启动服务，设置别名
        getActivity().startService(new Intent(getActivity(), AliasService.class));
        getActivity().finish();
        ActivityCollector.finishAll();
        // step3:跳回到登录界面
        startActivity(new Intent(getActivity(), LoginActivity.class));
    }

    /**
     * 解决如下问题
     * java.lang.IllegalStateException: No host
     */
    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
