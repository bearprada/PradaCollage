package lab.prada.collage;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import afzkl.development.mColorPicker.views.ColorPickerView;

public class TextEditorActivity extends BaseActivity {
	
	private ColorPickerView cp;
	
	public final static int TYPE_NEW = 0;
	public final static int TYPE_UPDATE = 1;
	public final static String EXTRA_EDITOR_TYPE = "type";
	public final static String EXTRA_EDITOR_TEXT = "text";
	public final static String EXTRA_EDITOR_COLOR = "color";
	public final static String EXTRA_EDITOR_BORDER = "border";
	private EditText mEditText;
    private CheckBox mHasStroke;
    //public final static String EXTRA_EDITOR_FONT = "font"; //TODO set the font type
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_text);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		cp = (ColorPickerView) findViewById(R.id.colorPickerView1);
		mEditText = (EditText) findViewById(R.id.editText1);
		mHasStroke = (CheckBox) findViewById(R.id.checkBoxHasStroke);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.text, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_choose:
				Bundle bundle = new Bundle();
				bundle.putString(EXTRA_EDITOR_TEXT, mEditText.getText().toString());
				bundle.putInt(EXTRA_EDITOR_COLOR, cp.getColor());
				bundle.putBoolean(EXTRA_EDITOR_BORDER, mHasStroke.isChecked());
				Intent intent = new Intent();
				intent.putExtras(bundle);
				setResult(RESULT_OK, intent);
				finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
