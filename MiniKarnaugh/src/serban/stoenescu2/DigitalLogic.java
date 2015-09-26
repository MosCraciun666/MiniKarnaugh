package serban.stoenescu2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import atomi.Atom;
import atomi.ConsecutiveOperatorsException;
import com.google.analytics.tracking.android.EasyTracker;
import com.purplebrain.adbuddiz.sdk.AdBuddiz;
import exceptii.ExceededLengthException;
import exceptii.IllegalEndException;
import exceptii.IllegalStartException;
import exceptii.InvalidCharacterException;
import exceptii.InvalidParanthesesException;
import exceptii.NoExpressionException;
import exceptii.TooManyVariablesException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Vector;
import minimizerInterface.MinimizerInterface;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import serban.stoenescu2.R;

/**
 * exceptii:
 * 
 * OK ExceededLengthException
 * OK IllegalEndException
 * OK IllegalStartException
 * OK InvalidCharacterException
 * OK InvalidParanthesisException
 * OK NoExpressionException
 * TooManyVariablesException
 */

public class DigitalLogic extends Activity implements OnItemSelectedListener
{
    /** Called when the activity is first created. */
    private Button tableButton;
    private Button minimizeFromFormulaButton,minimizeFromMapButton;
    private Button allZeroButton,allOneButton;
    private Button[] buttons;
    private TextView[] textViews,addressTextViews,binaryTextViews,binaryTextViews2;
    private TextView formulaTextView;//=(TextView)findViewById(R.id.resultformula);
    private TextView numberOfVariablesTextView,vkTextView;
    private DigitalLogic ceISus;
    private TableLayout tl,ttl/*TruthTableLayout*/;
    private LinearLayout layout;
    private boolean karnaughActive=true,showCellAddresses=true,dumbass=false;
    private static boolean autoCalc = false;
    public static boolean getAutoCalc(){return autoCalc;}
    private int numberOfVariables=3;
    private int[] buttonContents;
    private int[] experimental={0,1,3,2, 4,5,7,6, 12,13,15,14, 8,9,11,10};
    private final int[] smallOrder={0,1,3,2};
    private EditText formulaEditText;//=(EditText)findViewById(R.id.func);
    private Vector<Atom> tabelaSimboluri;
    private boolean fromFormula;
    private DigitalLogic ceIsus;
    protected WaitTask waitTask;
    private Button faceBookButton;
    private HorizontalScrollView sv;

    private String[][] binaryCombinations=
    {
        {" 0"},
        {" 0", " 1"},
        {" 00"," 01"," 11"," 10"},
        {"000","001","011","010","100","101","111","110"},
        {"0000","0001","0011","0010","0100","0101","0111","0110","1000","1001","1011","1010","1100","1101","1111","1110"},
    };

    private void resetButtons(int n)
    {
        int i;
        for(i=0;i<n;i++)
            buttonContents[i]=0;
    }
    private void setButtons(int n)
    {
        int i;
        for(i=0;i<n;i++)
            buttonContents[i]=1;
    }
    private int karnaughOrder(int n)
    {
        if(n>=16) return n;//faci ca jos, cu numberofvariables si pondere xl,xh,yl,yh etc.
        return experimental[n];
    }
    private int karnaughOrder(int y,int x)
    {
        int xh,xl,yh,yl;
        int pondereYh,pondereYl;
        xl=smallOrder[x%4];
        yl=smallOrder[y%4];
        xh=(x&0xC)>>2;
        yh=(y&0xC)>>2;
        if(numberOfVariables<=6) pondereYh=32;
        else pondereYh=64;
        pondereYl=4;
        if(numberOfVariables<=2) pondereYl=2;
        if(numberOfVariables==1) pondereYl=1;
        return xl+yl*pondereYl+xh*16+yh*pondereYh;
    }



    private void makeKarnaughButtons(int lines,int cols)
    {
        TableRow tr=new TableRow(this);
        int i,j,notvx=cols-1,notvy=cols-1;//notv=number of text views = a mother fucking shit
        tl.removeAllViews();
        TextView dumbShit;

        notvx=(int)(Math.log(cols)/Math.log(2));
        tr=new TableRow(this);
        dumbShit=new TextView(this);
        dumbShit.setText(" ");
        tr.addView(dumbShit);
        for(i=0;i<cols;i++)
        {
            System.out.println("x="+notvx+" i="+i);
            binaryTextViews[i]=new TextView(this);
            binaryTextViews[i].setText(binaryCombinations[notvx][i]);
            binaryTextViews[i].setTextColor(Color.WHITE);
            tr.addView(binaryTextViews[i]);
            dumbShit=new TextView(this);
            dumbShit.setText(" ");
            tr.addView(dumbShit);
        }
        tl.addView(tr);

        notvy=(int)(Math.log(lines)/Math.log(2));
        for(i=0;i<lines;i++)
        {
            System.out.println("NOTVy="+notvy+" i="+i);
            binaryTextViews2[i]=new TextView(this);
            binaryTextViews2[i].setText(binaryCombinations[notvy][i]);
            binaryTextViews2[i].setTextColor(Color.WHITE);
            //esti un muist si un cacat tr.addView(binaryTextViews2[i]);
            dumbShit=new TextView(this);
            dumbShit.setText(" ");
            tr.addView(dumbShit);
        }
        //-new

        vkTextView.setText("");
        vkTextView.setTextColor(Color.WHITE);
        for(i=0;i<Math.log(lines)/Math.log(2);i++)//asta e log in baza 2
        {
            if(dumbass) vkTextView.append(getDumbassVariableName(i)+" ");
            else vkTextView.append(getVariableName(i) + " ");
        }
        j=i;
        vkTextView.append("\\");
        for(i=0;i<Math.log(cols)/Math.log(2);i++)//asta e log in baza 2
        {
            if(dumbass)  vkTextView.append(getDumbassVariableName(i+j)+" ");
            else vkTextView.append(getVariableName(i + j) + " ");
        }
        int order;
        
            for(i=0;i<lines;i++)
            {
             tr=new TableRow(this);
             //+new
             tr.addView(binaryTextViews2[i]);
             //new
                for(j=0;j<cols;j++)
                {
               // order=karnaughOrder(i*cols+j);
                order=karnaughOrder(i,j);
                addressTextViews[order]=new TextView(this);
                addressTextViews[order].setText(""+order);
                addressTextViews[order].setTextSize(11);
                addressTextViews[order].setGravity(Gravity.BOTTOM);
                addressTextViews[order].setTextColor(Color.WHITE);
                //sa nu mai incerci treaba cu if(butonul==null) butonul=new Button(this);
                //pentru ca risti sa faci add dublu cu acelasi obiect
                //si o sa crape
                buttons[order]=new Button(this);
                buttons[order].setOnClickListener(new MyOnClickListener(buttons[order],order,this,minimizeFromMapButton));
                if(buttonContents[order]==2)
                    buttons[order].setText("X");
                else
                    buttons[order].setText(""+buttonContents[order]);
               // buttons[order].setText("" + order);
                tr.addView(buttons[order]);//era (...,10,10);
                tr.addView(addressTextViews[order]);
                if(showCellAddresses) addressTextViews[order].setVisibility(View.VISIBLE);
                else addressTextViews[order].setVisibility(View.INVISIBLE);
                }
             tl.addView(tr);
            }
    }

    public void showException(String crap)
    {
    TextView tv=new TextView(this);
    tv.setText(crap);
    setContentView(tv);
    }

    private void makeTruthTable(int rows,int variables)
    {
        int i,j,offset=0;
        ttl.removeAllViews();
        TableRow tr=new TableRow(this);
        
        for(j=0;j<variables+1;j++)
        {
            System.out.println("MUIE MUIE MUIE MUIE MUIE MUIE MUIE MUIE MUIE ");
            textViews[j]=new TextView(this);
            if(j==0) textViews[j].setText(" ");
            else 
            {
                if(dumbass)textViews[j].setText(getDumbassVariableName(j - 1) + " ");
                else
                    textViews[j].setText(getVariableName(j - 1) + " ");
            }
            textViews[j].setTextColor(Color.WHITE);
            offset++;
            tr.addView(textViews[j]);
        }
        ttl.addView(tr);
        for(i=0;i<rows;i++)
        {
            tr=new TableRow(this);
            for(j=0;j<variables+1;j++)
            {
                textViews[i*variables+j+offset]=new TextView(this);
                if(j==0)
                {
                textViews[i*variables+j+offset].setText(i+" ");
                textViews[i*variables+j+offset].setTextColor(Color.WHITE);
                }
                else
                {
                textViews[i*variables+j+offset].setText(""+((i>>(variables-1-j+1))&1));
                textViews[i*variables+j+offset].setTextColor(Color.BLACK);
                }
                tr.addView(textViews[i*variables+j+offset]);
            }
            buttons[i]=new Button(this);
            if(buttonContents[i]==2)
                buttons[i].setText("X");
            else
                buttons[i].setText(""+buttonContents[i]);
            //buttons[i].setText(""+i);
            buttons[i].setOnClickListener(new MyOnClickListener(buttons[i],i,this,minimizeFromMapButton));
            tr.addView(buttons[i]);

            ttl.addView(tr);
        }
    }

    public void setButton(int n) {buttonContents[n]=1;}
    public void resetButton(int n) {buttonContents[n]=0;}
    public void setButtonToDontCare(int n) {buttonContents[n]=2;}//2 = 'x'
    private int getButtonValue(int n) {return buttonContents[n];}
    private String getDumbassVariableName(int n)
    {
        return "" +(char)('A' +n);
    }
    private String getVariableName(int n)
    {
        if(tabelaSimboluri!=null &&  tabelaSimboluri.size()>n)
        {
            System.out.println("sa-ti dau la muie");
            return tabelaSimboluri.get(n).getName();
        }
        return "" +(char)('A' +n);
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


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        try
        {
        setContentView(R.layout.main);//sa mori tu?
               
        ceIsus=this;
        final DigitalLogic upperthis=this;

        

        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.default_background);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(bmp);
        bitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);

        Button helpButton=(Button)findViewById(R.id.help);
        helpButton.setOnClickListener(new OnClickListener(){

                public void onClick(View arg0) {
                    Intent intent=new Intent(upperthis,HelpActivity.class);
                    startActivity(intent);
                }
            });

        faceBookButton=(Button)findViewById(R.id.facebook);
        faceBookButton.setOnClickListener(new OnClickListener(){

                public void onClick(View arg0) {
                    Intent intent = new Intent(upperthis,FacebookActivity.class);
                    startActivity(intent);
                }
            });
        
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
        R.array.variables_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener((OnItemSelectedListener) this);

        numberOfVariablesTextView=(TextView)findViewById(R.id.numberofvariables);
        numberOfVariablesTextView.setText( "Number of variables: "+numberOfVariables);

        minimizeFromFormulaButton=(Button)findViewById(R.id.minimizeformula);
        minimizeFromMapButton=(Button)findViewById(R.id.minimizemaptable);
        vkTextView=(TextView)findViewById(R.id.vktextview);
        formulaTextView=(TextView)findViewById(R.id.resultformula);
        formulaEditText=(EditText)findViewById(R.id.func);

        

        allZeroButton=(Button)findViewById(R.id.allzero);
        allOneButton=(Button)findViewById(R.id.allone);

        
        tl=new TableLayout(this);
        tl.setStretchAllColumns(true);
        //tl.setShrinkAllColumns(true);
        tl.setPadding(15,15,15,15);

        ttl=new TableLayout(this);
        ttl.setStretchAllColumns(true);
       // ttl.setShrinkAllColumns(true);
        ttl.setPadding(15,15,15,15);

        buttons=new Button[257];//imi ajung 256 de fapt
        buttonContents=new int[257];
        textViews=new TextView[2314];//de fapt imi ajung 2314
        addressTextViews=new TextView[257];//de fapt imi ajung exact 256, dar sa fiu sigur
        binaryTextViews=new TextView[80];//de fapt 16 ajung
        binaryTextViews2=new TextView[80];//pula mea
       // final ScrollView sv=new ScrollView(this);
       sv=(HorizontalScrollView)findViewById(R.id.scrollview1);

        resetButtons(257);
        ceISus=this;
       
        makeKarnaughButtons(2,4);

        layout = (LinearLayout)findViewById(R.id.linearlayout2);
        layout.setBackgroundDrawable(bitmapDrawable);


        tableButton=(Button)findViewById(R.id.tablebutton);
        tableButton.setOnClickListener(new OnClickListener()
        {
                public void onClick(View arg0) 
                {
                    if(karnaughActive)//make truth table
                    {
                        remakeScrollView(sv,false);//not karnaugh map
                        vkTextView.setVisibility(View.INVISIBLE);
                        tableButton.setText("Show Karnaugh map");
                        karnaughActive=false;
                    }
                    else//make Veitch-Karnaugh map
                    {
                        int x,y;
                        remakeScrollView(sv,true);//karnaugh map
                        vkTextView.setVisibility(View.VISIBLE);
                        karnaughActive=true;
                        tableButton.setText("Show truth table");
                    }
                }
        });

        sv.addView(tl);

        final DigitalLogic _this = this;

        minimizeFromFormulaButton.setOnClickListener(new OnClickListener()
            {
                public void onClick(View arg0) 
                {
                    try
                    {
                        String result;
                        int[] kmap;
                        int i;

                        if(formulaEditText.getText().toString().length()==0)
                        {
                            throw new NoExpressionException();
                        }
                        ProgressDialog pd=ProgressDialog.show(_this, "Thinking...","Do not disturb");

                        waitTask=new WaitTask();
                        waitTask.setCrap(_this);
                        waitTask.setPd(pd);
                        waitTask.setFormula(formulaEditText.getText().toString());
                        waitTask.setFTW(formulaTextView);
                        waitTask.setFromMap(false);
                        waitTask.execute(_this);

                        dumbass=false;
                    }
                    catch(NoExpressionException e)
                    {
                       Toast t = Toast.makeText(ceIsus,"Please provide an expression in the text field",Toast.LENGTH_LONG);
                       t.show();
                    }
                }
            });
        minimizeFromMapButton.setOnClickListener(new OnClickListener()
            {
                public void onClick(View arg0)
                {
                    System.out.println("Imi bei pula");

                    final int[] kmap=new int[(int)Math.pow(2,numberOfVariables)];
                    int i,j;
                    dumbass=true;

                    for(i=0;i<kmap.length;i++)
                        {
                            kmap[i]=buttonContents[karnaughOrder(i)];
                            System.out.println(buttonContents[karnaughOrder(i)]+"(+"+i+") ");
                        }
                    int lines=0,cols=0;
                    switch(numberOfVariables)
                    {
                        case 1: lines=1; cols=0; break;
                        case 2: lines=1; cols=1; break;
                        case 3: lines=1; cols=2; break;
                        case 4: lines=2; cols=2; break;
                        case 5: lines=2; cols=3; break;
                        case 6: lines=3; cols=3; break;
                        case 7: lines=3; cols=4; break;
                        case 8: lines=4; cols=4; break;
                    }
                    //remake Karnaugh map header
                    vkTextView.setText("");
                    vkTextView.setTextColor(Color.WHITE);
                    for(i=0;i<lines;i++)//asta e log in baza 2
                    {
                        vkTextView.append(getDumbassVariableName(i)+" ");
                    }
                    j=i;
                    vkTextView.append("\\");
                    for(i=0;i<cols;i++)//asta e log in baza 2
                    {
                        vkTextView.append(getDumbassVariableName(i+j)+" ");
                    }
                    //remake truth table header
                    for(j=0;j<numberOfVariables+1;j++)
                    {
                    if(textViews[j]==null)
                        textViews[j]=new TextView(upperthis);
                    textViews[j].setText("");
                    if(j==0) textViews[j].setText(" ");
                    else textViews[j].setText(getDumbassVariableName(j-1) + " ");
                    }
                    String result;
                    ProgressDialog pd=ProgressDialog.show(_this, "Thinking...","Do not disturb");
                   
                    String result2;
                    waitTask=new WaitTask();
                    waitTask.setPd(pd);
                    waitTask.setKmap(kmap);
                    waitTask.setFTW(formulaTextView);
                    waitTask.setFromMap(true);
                    waitTask.execute(_this);

                }
            });
            allZeroButton.setOnClickListener(new OnClickListener()
            {

                public void onClick(View arg0) {
                    int i;
                    for(i=0;i<Math.pow(2,numberOfVariables);i++)
                    {
                        resetButton(i);
                        buttons[i].setText("0");
                    }
                }

            });
            allOneButton.setOnClickListener(new OnClickListener()
            {

                public void onClick(View arg0) {
                    int i;
                    for(i=0;i<Math.pow(2,numberOfVariables);i++)
                    {
                        setButton(i);
                        buttons[i].setText("1");
                    }
                }

            });

            //+web page text download
            AnnouncementTask announcementTask = new AnnouncementTask();
            announcementTask.setCrap(this);
            announcementTask.execute(this);
            //-web page text download

            //+adbuddiz
            System.out.println("\n\n\nBa boule, ar trebui sa fiu la AdBuddiz 1\n\n\n");
            AdBuddiz.cacheAds(this); // this = current Activity
            System.out.println("\n\n\nBa boule, ar trebui sa fiu la AdBuddiz 2\n\n\n");
            AdBuddiz.showAd(this); // this = current Activity
            System.out.println("\n\n\nBa boule, ar trebui sa fiu la AdBuddiz 3\n\n\n");
            //-adbuddiz
        }
        catch(Exception e)
        {
            TextView tv=new TextView(this);
            tv.setText("WTF " +e.getClass().getName());
            tv.append("\nMessage: "+e.getMessage());
            e.printStackTrace();
            if(layout==null) tv.append("\nEste layout");
            setContentView(tv);
        }

      
    }

    /*package*/ void remakeScrollView(HorizontalScrollView sv,boolean makeKarnaugh)
    {
        if(!makeKarnaugh)//make truth table
                    {
                        vkTextView.setVisibility(View.INVISIBLE);
                        makeTruthTable((int)Math.pow(2,numberOfVariables),numberOfVariables);
                        sv.removeAllViews();
                        sv.addView(ttl);
                    }
                    else//make Veitch-Karnaugh map
                    {
                        int x,y;
                        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
                        vkTextView.setVisibility(View.VISIBLE);
                        switch(numberOfVariables)
                        {
                            case 1: x=1;y=2; break;
                            default:
                                if(numberOfVariables%2==0)
                                {
                                    x=(int) Math.pow(2,numberOfVariables/2);
                                    y=x;
                                }
                                else//prostie
                                {
                                  x=(int) Math.pow(2,numberOfVariables/2+1);
                                  y=(int) Math.pow(2,numberOfVariables/2);
                                }
                            break;
                        }
                        makeKarnaughButtons(y,x);
                        sv.removeAllViews();
                        sv.addView(tl);
                    }
    }

    public void onCheckboxClicked(View view)
    {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();
        autoCalc=checked;
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        String crap;
        crap=(String)(parent.getItemAtPosition(pos));
        System.out.println("S-a selectat: "+crap+ " pozitia:"+pos);

        numberOfVariables=pos+1;
        numberOfVariablesTextView.setText("Number of variables: " +numberOfVariables);
        setFromFormula(false);
        remakeScrollView(sv,karnaughActive);
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    void setFromFormula(boolean b) {
        this.fromFormula=b;
    }

    boolean getKarnaughActive() {
        return karnaughActive;
    }

    HorizontalScrollView getsv() {
        return sv;
    }

    TextView getNumberOfVariablesTextView() {
        return numberOfVariablesTextView;
    }

    void setTabelaSimboluri(Vector<Atom> tabelaSimboluri) {
        this.tabelaSimboluri=tabelaSimboluri;
    }

    void setNumberOfVariables(int numberOfVariables) {
        this.numberOfVariables=numberOfVariables;
    }

    void updateUrlString(String stringFromUrl) {
        TextView tv = (TextView)findViewById(R.id.announcementtextview);
        if(stringFromUrl == null)
            tv.setText("MiniKarnaugh by Serban Stoenescu\nsherbanmobile.appspot.com");
        else
            tv.setText(stringFromUrl);
    }
}


class MyOnClickListener implements OnClickListener
{
    private Button button;
    private int buttonNumber;
    private DigitalLogic dl;
    private Button artificialButton;
    public MyOnClickListener(Button b,int bn,DigitalLogic dl,Button ab)
    {
        button=b;
        artificialButton=ab;
        buttonNumber=bn;
        this.dl=dl;
    }
    public void onClick(View arg0) {
        try
        {
        if(button.getText().equals("0"))//0->1
            {
             button.setText("1");
             dl.setButton(buttonNumber);//1->x
            }
        else if(button.getText().equals("1"))
            {
             button.setText("X");
             dl.setButtonToDontCare(buttonNumber);
            }
        else //x->0
            {
             button.setText("0");
             dl.resetButton(buttonNumber);
            }
        }
        catch(Exception e) 
        {
            dl.showException("" +e.getClass().toString());
            if(button==null)
                dl.showException("butonul meu este null");
        }
        if(DigitalLogic.getAutoCalc())
        {
            artificialButton.performClick();
        }
    }

    
}


class AnnouncementTask extends AsyncTask<Context,Integer,String>
{
    private String stringFromUrl;
    private DigitalLogic crap;
    public void setCrap(DigitalLogic dt){crap=dt;}
    protected String doInBackground(Context... arg0)
    {
        stringFromUrl = "";
        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet("http://sherbanmobile.appspot.com/announcements.txt");
        try
        {
            HttpResponse execute = client.execute(httpGet);
            InputStream content = execute.getEntity().getContent();

            BufferedReader buffer = new BufferedReader(
                            new InputStreamReader(content));
            String s = "";
            while ((s = buffer.readLine()) != null)
            {
                stringFromUrl += s;
            }
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return stringFromUrl;
    }

    @Override
    protected void onPostExecute( String result )
    {
       super.onPostExecute(result);
       crap.updateUrlString(stringFromUrl);
    }
    @Override
    protected void onCancelled()
    {
       super.onCancelled();
    }
}

class WaitTask extends AsyncTask<Context,Integer,String>
{
    private ProgressDialog pd;
    private int[] kmap;
    private String result;
    private String formula;
    private TextView formulaTextView;
    private DigitalLogic crap;
    private boolean fromMap;
    private Vector<Atom> tabelaSimboluri;
    public void setFromMap(boolean fm){fromMap=fm;}
    public void setPd(ProgressDialog pd)
    {
        this.pd=pd;
    }
    public void setFTW(TextView tv){formulaTextView=tv;}
    public void setKmap(int[] kmap) {this.kmap=kmap;}
    public String getResult(){return result;}
    public void setCrap(DigitalLogic dt){crap=dt;}
    public void setFormula(String formula)
    {
        this.formula=formula;
    }
    
    @Override
    protected String doInBackground(Context... arg0) 
    {
        if(fromMap)
        {
        MinimizerInterface.minimizeFromKarnaughMap(kmap);//formulaEditText
        result=MinimizerInterface.getResultAsString();
        System.out.println("REZULTATUL PULII TALE="+result);
        }
        else
        {
            try
            {
                MinimizerInterface.minimizeFromExpression(formula);
                result=MinimizerInterface.getResultAsString();
                
            }
            catch (NoExpressionException e)
            {
                pd.dismiss();
                Toast t = Toast.makeText(crap,"Please provide an expression in the text field",Toast.LENGTH_LONG);
                t.show();
            }
            catch(InvalidCharacterException e)
            {
                pd.dismiss();
                Toast t = Toast.makeText(crap,e.getMessage(),Toast.LENGTH_LONG);
                t.show();
            }
            catch(ExceededLengthException e)
            {
                pd.dismiss();
                Toast t = Toast.makeText(crap,"Error: variable name is too long",Toast.LENGTH_LONG);
                t.show();
            }
            catch(IllegalStartException e)
            {
                pd.dismiss();
                Toast t = Toast.makeText(crap,e.getMessage(),Toast.LENGTH_LONG);
                t.show();
            }
            catch(ConsecutiveOperatorsException e)
            {
                pd.dismiss();
                Toast t = Toast.makeText(crap,"Error: can\'t have two consecutive operators",Toast.LENGTH_LONG);
                t.show();
            }
            catch(IllegalEndException e)
            {
                pd.dismiss();
                Toast t = Toast.makeText(crap,e.getMessage(),Toast.LENGTH_LONG);
                t.show();
            }
            catch(InvalidParanthesesException e)
            {
                pd.dismiss();
                Toast t = Toast.makeText(crap,"Paranthese error",Toast.LENGTH_LONG);
                t.show();
            }
            catch(TooManyVariablesException e)
            {
                pd.dismiss();
                Toast t = Toast.makeText(crap,"Too many variables. Maximum allowed is 8."+e.getMessage(),Toast.LENGTH_LONG);
                t.show();
            }
        }//end else (formula case)
        return "M-AM CACAT";
    }

    @Override
    protected void onCancelled()
    {
       super.onCancelled();
    }
    
    @Override
    protected void onPostExecute( String result )
    {
        if(fromMap)
        {
         formulaTextView.setText("Minimized function: "+this.result);
         super.onPostExecute(result);
         pd.dismiss();
         result=MinimizerInterface.getResultAsString();
         
         //+cacat
         /*
          if(crap.getKarnaughActive())
              crap.remakeScrollView(crap.getsv(),true);
          else crap.remakeScrollView(crap.getsv(),false);*/
         //-cacat
        }
        else
        {
         int i;
         int [] kmap=MinimizerInterface.getResultAsTruthTable();
         result=MinimizerInterface.getResultAsString();
         //set table content:
         for(i=0;i<kmap.length;i++)
            if(kmap[i]==1)
             crap.setButton(i);
            else crap.resetButton(i);

         
          int numberOfVariables=MinimizerInterface.getNumberOfSymbols();
          crap.setNumberOfVariables(numberOfVariables);
          tabelaSimboluri=MinimizerInterface.getSymbols();
          crap.setTabelaSimboluri(tabelaSimboluri);
          Collections.reverse(tabelaSimboluri);
          //fromFormula=true;
          crap.setFromFormula(true);

          
          if(crap.getKarnaughActive())
              crap.remakeScrollView(crap.getsv(),true);
          else crap.remakeScrollView(crap.getsv(),false);

          crap.getNumberOfVariablesTextView().setText( "Number of variables: "+numberOfVariables);
          formulaTextView.setText("Minimized function: "+this.result);
          pd.dismiss();
        }
    }


    
}

