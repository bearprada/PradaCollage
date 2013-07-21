package lab.prada.collage;

import com.androidquery.AQuery;

import afzkl.development.mColorPicker.views.ColorPickerView;
import afzkl.development.mColorPicker.views.ColorPickerView.OnColorChangedListener;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

public class TextEditorActivity extends Activity {
	
	private AQuery aq;
	private ColorPickerView cp;
	
	public final static int TYPE_NEW = 0;
	public final static int TYPE_UPDATE = 1;
	public final static String EXTRA_EDITOR_TYPE = "type";
	public final static String EXTRA_EDITOR_TEXT = "text";
	public final static String EXTRA_EDITOR_COLOR = "color";
	public final static String EXTRA_EDITOR_BORDER = "border";
	//public final static String EXTRA_EDITOR_FONT = "font"; //TODO set the font type
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.text_editor);
		aq = new AQuery(this);
		cp = (ColorPickerView) aq.find(R.id.colorPickerView1).getView();
		aq.find(R.id.btnFinish).clicked(this, "clickFinish");
		switch(getIntent().getIntExtra(EXTRA_EDITOR_TYPE, TYPE_NEW)){
		case TYPE_NEW:
			// do nothing
			break;
		case TYPE_UPDATE:
			aq.find(R.id.editText1).text(getIntent().getStringExtra(EXTRA_EDITOR_TEXT));
			cp.setColor(getIntent().getIntExtra(EXTRA_EDITOR_COLOR,Color.BLACK));
			aq.find(R.id.checkBoxHasStroke).checked(getIntent().getBooleanExtra(EXTRA_EDITOR_BORDER, false));
			break;
		}
	}
	
	public void clickFinish(View button){
	    Bundle bundle = new Bundle();  
	    bundle.putString(EXTRA_EDITOR_TEXT, aq.find(R.id.editText1).getEditText().getText().toString());  
	    bundle.putInt(EXTRA_EDITOR_COLOR, cp.getColor());
	    bundle.putBoolean(EXTRA_EDITOR_BORDER, aq.find(R.id.checkBoxHasStroke).isChecked());
	    Intent intent = new Intent();  
	    intent.putExtras(bundle);
	    setResult(RESULT_OK, intent);
		finish();
	}
}
