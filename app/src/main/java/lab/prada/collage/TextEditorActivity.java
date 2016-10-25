package lab.prada.collage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import afzkl.development.mColorPicker.views.ColorPickerView;

public class TextEditorActivity extends Activity {
	
	private ColorPickerView cp;
	
	public final static int TYPE_NEW = 0;
	public final static int TYPE_UPDATE = 1;
	public final static String EXTRA_EDITOR_TYPE = "type";
	public final static String EXTRA_EDITOR_TEXT = "text";
	public final static String EXTRA_EDITOR_COLOR = "color";
	public final static String EXTRA_EDITOR_BORDER = "border";
	private EditText mEditText;
	//public final static String EXTRA_EDITOR_FONT = "font"; //TODO set the font type
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.text_editor);
		cp = (ColorPickerView) findViewById(R.id.colorPickerView1);
		mEditText = (EditText) findViewById(R.id.editText1);
		final CheckBox mHasStroke = (CheckBox) findViewById(R.id.checkBoxHasStroke);
		findViewById(R.id.btnFinish).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString(EXTRA_EDITOR_TEXT, mEditText.getText().toString());
				bundle.putInt(EXTRA_EDITOR_COLOR, cp.getColor());
				bundle.putBoolean(EXTRA_EDITOR_BORDER, mHasStroke.isChecked());
				Intent intent = new Intent();
				intent.putExtras(bundle);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
		switch(getIntent().getIntExtra(EXTRA_EDITOR_TYPE, TYPE_NEW)){
		case TYPE_NEW:
			// do nothing
			break;
		case TYPE_UPDATE:
			mEditText.setText(getIntent().getStringExtra(EXTRA_EDITOR_TEXT));
			cp.setColor(getIntent().getIntExtra(EXTRA_EDITOR_COLOR,Color.BLACK));
			mHasStroke.setChecked(getIntent().getBooleanExtra(EXTRA_EDITOR_BORDER, false));
			break;
		}
	}
}
