package yakushimalife.yakushimanextbus;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.yakushimalife.yakushimanextbus.R;

public class AboutActivity extends NextBusActivity {
	public static final String YAKUSHIMA_LIFE_URL = "http://www.yakushimalife.com";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		Button buttonYakushimaLife = (Button) findViewById(R.id.buttonyakushimalife);
		buttonYakushimaLife.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri
						.parse(YAKUSHIMA_LIFE_URL)));
			}
		});
	}
}
