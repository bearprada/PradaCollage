package com.example.pradacollage;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import com.androidquery.AQuery;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;

public class MultipleImagePickerActivity extends Activity implements OnItemClickListener {

	protected static final String EXTRA_IMAGE_PICKER_IMAGE_PATH = "image_path";
	private DisplayImageOptions options;
	private GridView listView;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private AQuery aq;
	private ImageAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_multiple_image_picker);
		initImageLoader(this);
		aq = new AQuery(this);
		aq.find(R.id.btnOk).clicked(this, "clickOk");
		aq.find(R.id.btnCancel).clicked(this, "clickCancel");
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.ic_stub)
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error).cacheInMemory(true)
				.cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565).build();
		listView = (GridView) findViewById(R.id.gridView);
		
		adapter = new ImageAdapter(getCameraImages(this));
		((GridView) listView).setAdapter(adapter);
		listView.setOnItemClickListener(this);
	}
	
	public void clickOk(View button){
		Bundle bundle = new Bundle();  
	    bundle.putStringArray(EXTRA_IMAGE_PICKER_IMAGE_PATH, adapter.getSelectedItems());
	    Intent intent = new Intent();  
	    intent.putExtras(bundle);  
	    setResult(RESULT_OK, intent);
	    finish();
	}
	
	public void clickCancel(View button){
		finish();
	}

	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}

	public List<String> getCameraImages(Context context) {
		// which image properties are we querying
		String[] projection = new String[] { MediaStore.Images.Media._ID,
				MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
				MediaStore.Images.Media.DATE_TAKEN,
				MediaStore.Images.Media.DATA };

		// Get the base URI for the People table in the Contacts content
		// provider.
		Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		// Make the query.
		Cursor cur = managedQuery(images, projection, // Which columns to return
				"", // Which rows to return (all rows)
				null, // Selection arguments (none)
				"" // Ordering
		);

		Log.i("ListingImages", " query count=" + cur.getCount());
		ArrayList<String> imagePaths = new ArrayList<String>(cur.getCount());
		if (cur.moveToFirst()) {
			String bucket;
			String token;
			String data;
			int bucketColumn = cur
					.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

			int dateColumn = cur
					.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);
			int rawCol = cur.getColumnIndex(MediaStore.Images.Media.DATA);

			do {
				// Get the field values
				bucket = cur.getString(bucketColumn);
				token = cur.getString(dateColumn);
				data = cur.getString(rawCol);
				imagePaths.add(data);
				// Do something with the values.
				Log.i("ListingImages", " bucket=" + bucket + "  date_taken="
						+ token + " data = " + data);
			} while (cur.moveToNext());

		}
		return imagePaths;
	}

	public class ImageAdapter extends BaseAdapter {
		private List<String> datasource;
		private Hashtable<Integer,Boolean> selectionTable = new Hashtable<Integer,Boolean>();

		public ImageAdapter(List<String> cameraImages) {
			datasource = cameraImages;
		}
		
		private void selected(int position){
			// TODO add the ui effect
			if(selectionTable.containsKey(position)){
				selectionTable.remove(position);
			}else{
				selectionTable.put(position, true);
			}
			Log.d("TEST","----- selected : "+ position+" " +isSelected(position));
			notifyDataSetChanged();
		}
		
		public String[] getSelectedItems(){
			String[] items = new String[selectionTable.size()];
			Enumeration<Integer> ks = selectionTable.keys();
			int count = 0;
			while(ks.hasMoreElements()){
				items[count++] = datasource.get(ks.nextElement());
			}
			return items;
		}
		
		private boolean isSelected(int position){
			return selectionTable.containsKey(position);
		}

		@Override
		public int getCount() {
			return datasource.size();
		}

		@Override
		public Object getItem(int position) {
			return datasource.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final View view;
			if (convertView == null) {
				view = getLayoutInflater().inflate(R.layout.item_grid_image, parent, false);
			} else {
				view = convertView;
			}
			
			Log.d("TEST","@@@@ "+position + " " +isSelected(position));
			view.findViewById(R.id.imageView1).setVisibility((isSelected(position)?View.VISIBLE:View.INVISIBLE));
			imageLoader.displayImage("file://"+datasource.get(position), (ImageView) view.findViewById(R.id.image),
					options);
			return view;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.multiple_image_picker, menu);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		adapter.selected(position);
	}

}
