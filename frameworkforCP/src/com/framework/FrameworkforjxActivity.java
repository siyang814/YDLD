package com.framework;

import java.util.ArrayList;
import java.util.List;




import com.framework.ImageFileLoader.FileLoader;





import android.app.Activity;

import android.os.Bundle;
import android.widget.ImageView;

public class FrameworkforjxActivity extends Activity {
    /** Called when the activity is first created. */
	
	  ImageView imageView ;

		 String uri = "http://image.ihaomen.com/wallpapers/240x200/_WAGs_1600_1200_210.jpg";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
         //this.doLogin("pppp@11.com", "123", "举贤猎才互动移动客户端android版", "1.2");
//        findView(); 
        
        FileLoader.getInstance().load(uri, imageView, true);
       // getHunters();
    }
    private void findView(){
    	imageView = (ImageView) this.findViewById(R.id.imageViewId);
    			
    }
    
 
    
    
    
}