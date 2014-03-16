package yakushimalife.yakushimanextbus;

import com.yakushimalife.yakushimanextbus.R;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class NumbersActivity extends Activity {

	private Button buttonkankoukyoukaipdfs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_numbers);
		buttonkankoukyoukaipdfs = (Button) findViewById(R.id.buttonkankoukyoukaipdfs);
		buttonkankoukyoukaipdfs.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri
						.parse("http://www1.ocn.ne.jp/~yakukan/download.htm")));
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_search:
			Intent mainintent = new Intent(this, MainActivity.class);
			startActivity(mainintent);
			return true;
		case R.id.action_stoplist:
			Intent stoplistintent = new Intent(this, StopListActivity.class);
			startActivity(stoplistintent);
			return true;
		case R.id.action_about:
			Intent aboutintent = new Intent(this, AboutActivity.class);
			startActivity(aboutintent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
