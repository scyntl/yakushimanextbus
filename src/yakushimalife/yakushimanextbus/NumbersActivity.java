package yakushimalife.yakushimanextbus;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.yakushimalife.yakushimanextbus.R;

public class NumbersActivity extends NextBusActivity {

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
}
