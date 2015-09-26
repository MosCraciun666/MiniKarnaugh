/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package serban.stoenescu2;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import com.google.analytics.tracking.android.EasyTracker;
import serban.stoenescu2.R;

/**
 *
 * @author Serban
 */
public class FacebookActivity extends Activity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.facebook);
        WebView w=(WebView)findViewById(R.id.webview);
        //test page: w.loadUrl("http://www.facebook.com/serban6679");
        w.loadUrl("http://www.facebook.com/pages/Minikarnaugh/549776538385514");
      
    }

  @Override
  public void onStart() {
    super.onStart();
    // The rest of your onStart() code.
    EasyTracker.getInstance().activityStart(this); // Add this method.
  }

  @Override
  public void onStop() {
    super.onStop();
    // The rest of your onStop() code.
    EasyTracker.getInstance().activityStop(this); // Add this method.
  }

}
