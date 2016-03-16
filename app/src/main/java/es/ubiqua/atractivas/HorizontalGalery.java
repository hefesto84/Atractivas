package es.ubiqua.atractivas;

import android.app.Activity;

public class HorizontalGalery extends Activity {
/*
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.fragment_article_page);
		
		HorizontalListView listview = (HorizontalListView) findViewById(R.id.listview);
		listview.setAdapter(mAdapter);
		
	}
	
	private static String[] dataObjects = new String[]{ "Text #1",
		"Text #2",
		"Text #3" }; 
	
	private BaseAdapter mAdapter = new BaseAdapter() {

		private OnClickListener mOnButtonClicked = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(HorizontalGalery.this);
				builder.setMessage("hello from " + v);
				builder.setPositiveButton("Cool", null);
				builder.show();
				
			}
		};

		@Override
		public int getCount() {
			return dataObjects.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View retval = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_articles_galery_list_item, null);
			TextView title = (TextView) retval.findViewById(R.id.title);
			Button button = (Button) retval.findViewById(R.id.horizontallistview);
			button.setOnClickListener(mOnButtonClicked);
			title.setText(dataObjects[position]);
			
			return retval;
		}
		
	};
*/
}
