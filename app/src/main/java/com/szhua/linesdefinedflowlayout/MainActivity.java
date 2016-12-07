package com.szhua.linesdefinedflowlayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.szhua.linesdefinedflowlayout.flowlayout.FlowLayout;
import com.szhua.linesdefinedflowlayout.flowlayout.OnLinesChangeListener;
import com.szhua.linesdefinedflowlayout.flowlayout.OnLinesUpToMaxListener;
import com.szhua.linesdefinedflowlayout.flowlayout.TagAdapter;
import com.szhua.linesdefinedflowlayout.flowlayout.TagFlowLayout;

import java.lang.reflect.Array;

public class MainActivity extends AppCompatActivity {


    private TagFlowLayout tagFlowLayout  ;
    private TextView more_tag_bt;
    private boolean  isExpanded=true;
    private String [] tags =new String []{
            "酸奶" ,"西瓜" ,"连衣裙" ,"西餐" ,"劲道的面条" ,"长头发" ,"小孩子","无微不至的关爱","别人的夸奖","海边" ,"冰红茶" ,"一个美好的爱情",
            "Kitty" ,"粉红色" ,"砰砰的心跳" ,"一个美人能够打扰的自然醒的觉" ,"米线" ,"鸭血","粉条" ,"鸡公煲"
    } ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tagFlowLayout = (TagFlowLayout) findViewById(R.id.tag_flow_layout);
        more_tag_bt = (TextView) findViewById(R.id.more_tag_bt);
        //为tagview设置行数和显示情况；
       tagFlowLayout.setAdapter(new TagAdapter(tags) {
           @Override
           public View getView(FlowLayout parent, int position, Object o) {
               TextView tv = (TextView) LayoutInflater.from(MainActivity.this).inflate(R.layout.item_tag,
                       tagFlowLayout, false);
               tv.setText(tags[position]);
               return tv;
           }
       });

        tagFlowLayout.setOnLinesChangeListener(new OnLinesChangeListener() {
            @Override
            public void onlinesChanged(int lines) {
                Log.i("szhua", "Listen+lines" + lines);
            }
        });
        tagFlowLayout.setOnLinesUpToMaxListener(new OnLinesUpToMaxListener() {
            @Override
            public void onlinesUptoMax(int maxLines) {
                Log.i("szhua", "listen+max" + maxLines);

            }

            @Override
            public void onlinesGreaterThanMaxFisrt(int lines) {
                Log.i("szhua", "listen+max" + lines);
                    more_tag_bt.setVisibility(View.VISIBLE);
            }
        });

        more_tag_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isExpanded) {
                    tagFlowLayout.setMinLines(Integer.MAX_VALUE);
                    tagFlowLayout.onChanged();
                    isExpanded = false;
                    more_tag_bt.setText("收起");
                } else {
                    tagFlowLayout.setMinLines(3);
                    tagFlowLayout.onChanged();
                    isExpanded = true;
                   more_tag_bt.setText("展开");
                }
            }
        });
    }
}
