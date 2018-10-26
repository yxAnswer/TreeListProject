package com.example.google.treelistproject.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.google.treelistproject.R;
import com.example.google.treelistproject.adapter.SimpleTreeRecyclerAdapter;
import com.example.google.treelistproject.base.BaseActivity;
import com.example.google.treelistproject.bean.Persons;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.multilevel.treelist.Node;
import com.multilevel.treelist.TreeRecyclerAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * * Created by Google on 2018-10-26.
 */
public class RecyclerViewActivity extends BaseActivity {

    private TreeRecyclerAdapter mAdapter;
    List<Node> datas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);


        RecyclerView mTree = (RecyclerView) findViewById(R.id.recyclerview);
        mTree.setLayoutManager(new LinearLayoutManager(this));
        //第一个参数  RecyclerView
        //第二个参数  上下文
        //第三个参数  数据集
        //第四个参数  默认展开层级数 0为不展开
        //第五个参数  展开的图标
        //第六个参数  闭合的图标

        mAdapter = new SimpleTreeRecyclerAdapter(mTree, RecyclerViewActivity.this,
                datas, 1, R.mipmap.tree_ex, R.mipmap.tree_ec);

        mTree.setAdapter(mAdapter);
        initData();

    }


    private void initData() {
        OkGo.<String>get("http://192.168.1.93:9000/system/group/groupUsers?appkey=tjtl20180808-1f9a02c57a7649be87a2be0251c4aa4a")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        try {
                            JSONObject jb = new JSONObject(body);
                            String code = jb.getString("code");
                            if ("200".endsWith(code)) {
                                String data = jb.getString("data");
                                List<Persons> persons = new Gson().fromJson(data, new TypeToken<List<Persons>>() {
                                }.getType());
                                List<Node> list = new ArrayList<>();
                                for (Persons p : persons) {
                                    list.add(new Node(p.getId(), p.getPId(), p.getName(), p));
                                }
                                if (datas.size() > 0) {
                                    datas.clear();
                                }
                                datas.addAll(list);
                                mAdapter.addDataAll(datas, 100);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }
                });
    }


    /**
     * 显示选中数据
     */
    public void clickShow(View v) {
        StringBuilder sb = new StringBuilder();
        final List<Node> allNodes = mAdapter.getAllNodes();
        for (int i = 0; i < allNodes.size(); i++) {
            if (allNodes.get(i).isChecked()) {
                sb.append(allNodes.get(i).getName() + ",");
            }
        }
        String strNodesName = sb.toString();
        if (!TextUtils.isEmpty(strNodesName)) {
            Toast.makeText(this, strNodesName.substring(0, strNodesName.length() - 1), Toast.LENGTH_SHORT).show();
        }

    }
}
