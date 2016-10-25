package lab.prada.collage;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

public class MultipleImagePickerActivity extends BaseActivity {

	protected static final String EXTRA_IMAGE_PICKER_IMAGE_PATH = "image_path";
	private DisplayImageOptions options;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private ImageAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_multiple_image_picker);
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		initImageLoader(this);
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.ic_stub)
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error).cacheInMemory(true)
				.cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565).build();
		GridView listView = (GridView) findViewById(R.id.gridView);

		adapter = new ImageAdapter(getCameraImages(this));
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				adapter.selected(position);
			}
		});
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		adapter.dispose();
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
		String[] projection = new String[] { MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA };
		Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		Cursor cur = new CursorLoader(this,images,projection,"",null,"").loadInBackground();
		Log.i("ListingImages", " query count=" + cur.getCount());
		ArrayList<String> imagePaths = new ArrayList<String>(cur.getCount());
		int rawCol = cur.getColumnIndex(MediaStore.Images.Media.DATA);
		if (cur.moveToFirst()) {
			do {
				imagePaths.add(cur.getString(rawCol));
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
			if(selectionTable.size()>Constants.SUPPORTED_FRAME_NUMBER){
				Toast.makeText(MultipleImagePickerActivity.this, R.string.over_num_of_images, Toast.LENGTH_LONG).show();
			}else{
				if(selectionTable.containsKey(position)){
					selectionTable.remove(position);
				}else{
					selectionTable.put(position, true);
				}
				notifyDataSetChanged();
			}
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
		
		public void dispose(){
			datasource.clear();
			datasource = null;
			selectionTable.clear();
			selectionTable = null;
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
		getMenuInflater().inflate(R.menu.multiple_image_picker, menu);
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
				bundle.putStringArray(EXTRA_IMAGE_PICKER_IMAGE_PATH, adapter.getSelectedItems());
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
