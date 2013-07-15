package com.cloudpoint.activitis;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;


public class MainActivity extends TabActivity {

	private TabHost tabHost;
	private TabWidget tabWidget;
	
	//聚焦 ， 限免， 分类 ， 专题 , 更多。
	public static final String TAB_Focus = "TAB_Focus";
	public static final String TAB_Limitfree = "TAB_Limitfree";
	public static final String TAB_Category = "TAB_Category";
	public static final String TAB_Specialized = "TAB_Specialized";
	public static final String TAB_More = "TAB_More";
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		 setContentView(R.layout.main); 
		 
		   tabHost = getTabHost();
		   tabWidget = tabHost.getTabWidget();
           setTabs();
           
           tabHost.setCurrentTab(0);
           
         
 	   		
	}
	
	@Override
	protected void onResume() {
	   
	   		
		super.onResume();
	};
	
	 private void setTabs(){
     	addTab(TAB_Focus, "聚焦", R.drawable.ic_launcher,TAB_FocusActivity.class);
     	addTab(TAB_Limitfree, "限免", R.drawable.ic_launcher, TAB_LimitfreeActivity.class);
     	addTab(TAB_Category, "分类", R.drawable.ic_launcher, TAB_CategoryActivity.class);
     	addTab(TAB_Specialized, "专题", R.drawable.ic_launcher, TAB_SpecializedActivity.class);
     	addTab(TAB_More, "更多", R.drawable.ic_launcher, TAB_MoreActivity.class);
     
     }
	 private void addTab(String tabId, String label, int drawableId, Class<?> c) {
 		Intent intent = new Intent(this, c);
 		TabHost.TabSpec spec = tabHost.newTabSpec(tabId);	
 		
 		View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tab_indicator, getTabWidget(), false);
 		TextView title = (TextView) tabIndicator.findViewById(R.id.tab_title);
 		title.setText(label);
 		title.setTextColor(Color.WHITE);
 		
 		ImageView icon = (ImageView) tabIndicator.findViewById(R.id.tab_icon);
 		icon.setImageResource(drawableId);
 		
 		
 		
 		spec.setIndicator(tabIndicator);
 		spec.setContent(intent);
 		tabHost.addTab(spec);		
 		//tabIndicator.setBackgroundResource(R.drawable.bottombg);
 	}
	
}
