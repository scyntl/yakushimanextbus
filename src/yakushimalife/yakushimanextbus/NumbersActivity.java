package yakushimalife.yakushimanextbus;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.yakushimalife.yakushimanextbus.R;

public class NumbersActivity extends NextBusActivity {
	public static final String PDF_DOWNLOAD_URL = "http://www1.ocn.ne.jp/~yakukan/download.htm";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_numbers);
		
		findViewById(R.id.buttonkankoukyoukaipdfs).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri
						.parse(PDF_DOWNLOAD_URL)));
			}
		});
	}
}
