package yakushimalife.yakushimanextbus;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.yakushimalife.yakushimanextbus.R;

public abstract class NextBusActivity extends Activity {
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
		case R.id.action_numbers:
			Intent numbersintent = new Intent(this, NumbersActivity.class);
			startActivity(numbersintent);
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
