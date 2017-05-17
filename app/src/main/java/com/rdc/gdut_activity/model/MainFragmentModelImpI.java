package com.rdc.gdut_activity.model;

import android.util.Log;

import com.rdc.gdut_activity.bean.ActivityInfoBean;
import com.rdc.gdut_activity.contract.MainFragmentContract;

import java.util.Arrays;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by zjz on 2017/5/17.
 */

public class MainFragmentModelImpI implements MainFragmentContract.Model {
    private static final String TAG = "MainFragmentModelImpl";
    private int mPage = 0;  //当前页数
    private int mOnePageSum = 10; //一页的数据条数
    private MainFragmentContract.Presenter mPresenter;

    public MainFragmentModelImpI(MainFragmentContract.Presenter presenter){
        mPresenter = presenter;
    }

    //刷新数据
    @Override
    public void refreshData(String type) {
        mPage = 0;
        BmobQuery<ActivityInfoBean> query = new BmobQuery<>();
        query.addWhereEqualTo("mCheckStatus","审核通过");

//    if (type.equals("未审核")){
//        }else {
//            String[] types = {"审核通过","审核不通过"};
//            query.addWhereContainedIn("mCheckStatus", Arrays.asList(types));
//        }

        query.order("-createdAt");
        query.setLimit(mOnePageSum);
        query.findObjects(new FindListener<ActivityInfoBean>() {
            @Override
            public void done(List<ActivityInfoBean> list, BmobException e) {
                if (e == null){
                    mPresenter.refreshDataSuccess(list);
                    Log.e(TAG, "done: "+list.get(1).getObjectId());
//                    Log.e(TAG, "done: " + list.get(0).getPublisher().getUsername());
                }else {
                    mPresenter.refreshDataError(e.getMessage());
                }
            }
        });
    }

    //加载更多数据
    @Override
    public void loadMoreData(String type) {
        ++ mPage;
        BmobQuery<ActivityInfoBean> query = new BmobQuery<>();

            query.addWhereEqualTo("mCheckStatus","审核通过");
//        if (type.equals("未审核")){
//        }else {
//            String[] types = {"审核通过，审核不通过"};
//            query.addWhereContainedIn("mCheckStatus", Arrays.asList(types));
//        }
        query.order("-createdAt");
        query.setLimit(mOnePageSum);
        query.setSkip(mOnePageSum * mPage);
        query.findObjects(new FindListener<ActivityInfoBean>() {
            @Override
            public void done(List<ActivityInfoBean> list, BmobException e) {
                if (e == null){
                    mPresenter.loadMoreDataSuccess(list);
                }else {
                    mPresenter.loadMoreDataErroe(e.getMessage());
                }
            }
        });
    }

    //审核通过上传
    @Override
    public void mainFragmentPass(String objectId) {
        ActivityInfoBean bean = new ActivityInfoBean();
        bean.setCheckStatus("审核通过");
        bean.update(objectId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null){
                    mPresenter.mainFragmentSuccess();
                }else {
                    mPresenter.mainFragmentError(e.getMessage());
                }
            }
        });
    }

    //审核失败上传
    @Override
    public void mainFragmentFailure(String objectId, String reason) {
        ActivityInfoBean bean = new ActivityInfoBean();
        bean.setCheckStatus("审核不通过");
        bean.setCheckReason(reason);
        bean.update(objectId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null){
                    mPresenter.mainFragmentSuccess();
                }else {
                    mPresenter.mainFragmentError(e.getMessage());
                }
            }
        });
    }
}