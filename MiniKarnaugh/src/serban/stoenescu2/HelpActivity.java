/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package serban.stoenescu2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.LinearLayout;
import com.google.analytics.tracking.android.EasyTracker;
import serban.stoenescu2.R;

/**
 *
 * @author Serban
 */
public class HelpActivity extends Activity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.help);

        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.default_background);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(bmp);
        bitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);

        LinearLayout  layout = (LinearLayout)findViewById(R.id.helplinearlayout);
        layout.setBackgroundDrawable(bitmapDrawable);

        // ToDo add your GUI initialization code here        
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
