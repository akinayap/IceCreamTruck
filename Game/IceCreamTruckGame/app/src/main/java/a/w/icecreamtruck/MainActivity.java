package a.w.icecreamtruck;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

	ConstraintLayout ic1, ic2, ic3;
	ImageButton c1, c2, c3;
	AppCompatImageView iv1, iv2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ic1 = findViewById(R.id.ic_1);
		ic2 = findViewById(R.id.ic_2);
		ic3 = findViewById(R.id.ic_3);

		c1 = findViewById(R.id.cone_1);
		c2 = findViewById(R.id.cone_2);
		c3 = findViewById(R.id.cone_3);

		//iv1 = findViewById(R.id.cream_1_1);
		//iv2 = findViewById(R.id.cream_1_2);

		c1.setOnClickListener(v->{
			addScoop(ic1);
		});
		c2.setOnClickListener(v->{
			addScoop(ic2);
		});
		c3.setOnClickListener(v->{
			addScoop(ic3);
		});

	}

	private void addScoop(ConstraintLayout container) {
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.item_cream, null);
		container.getChildAt(container.getChildCount()-1);
		container.addView(view);
/*		View bottom = container;
		if(container.getChildCount() > 0){
			bottom = container.getChildAt(container.getChildCount() - 1);
		}

		int cream_size = (int) (getResources().getDimension(R.dimen.cream_size) / getResources().getDisplayMetrics().density);
		int cream_dist = (int) (getResources().getDimension(R.dimen.cream_dist) / getResources().getDisplayMetrics().density);

		AppCompatImageView newCream = new AppCompatImageView(this);
		container.addView(newCream);

		ConstraintLayout.LayoutParams containerParam = (ConstraintLayout.LayoutParams) container.getLayoutParams();
		ConstraintLayout.LayoutParams bottomParam = (ConstraintLayout.LayoutParams) bottom.getLayoutParams();
		ConstraintLayout.LayoutParams newCreamParam = new ConstraintLayout.LayoutParams(cream_size, cream_size);

		ConstraintLayout.LayoutParams mostBotParam =(ConstraintLayout.LayoutParams)  ic1.getLayoutParams();
		ConstraintLayout.LayoutParams otherParam =(ConstraintLayout.LayoutParams)  ic2.getLayoutParams();
		Log.e("Width", String.valueOf(mostBotParam.width));
		Log.e("Height", String.valueOf(mostBotParam.height));
		Log.e("bottomMargin", String.valueOf(mostBotParam.bottomMargin));
		Log.e("bottomToBottom", String.valueOf(mostBotParam.bottomToBottom));
		Log.e("endToEnd", String.valueOf(mostBotParam.endToEnd));
		Log.e("startToStart", String.valueOf(mostBotParam.startToStart));

		Log.e("otherParam Width", String.valueOf(otherParam.width));
		Log.e("otherParam Height", String.valueOf(otherParam.height));
		Log.e("otherParam bottomMargin", String.valueOf(otherParam.bottomMargin));
		Log.e("otherParam botToBottom", String.valueOf(otherParam.bottomToBottom));
		Log.e("otherParam endToEnd", String.valueOf(otherParam.endToEnd));
		Log.e("otherParam startToStart", String.valueOf(otherParam.startToStart));

		Log.e("containerParam Width", String.valueOf(containerParam.width));
		Log.e("containerParam Height", String.valueOf(containerParam.height));
		Log.e("conParam botMargin", String.valueOf(containerParam.bottomMargin));
		Log.e("containerParam botToBot", String.valueOf(containerParam.bottomToBottom));
		Log.e("containerParam endToEnd", String.valueOf(containerParam.endToEnd));
		Log.e("conParam startToStart", String.valueOf(containerParam.startToStart));


		newCreamParam.bottomMargin = cream_dist;
		newCreamParam.bottomToBottom = bottomParam.bottomMargin;
		newCreamParam.endToEnd = containerParam.getMarginEnd();
		newCreamParam.startToStart = containerParam.getMarginStart();

		Log.e("newParam Width", String.valueOf(newCreamParam.width));
		Log.e("newParam Height", String.valueOf(newCreamParam.height));
		Log.e("newParam botMargin", String.valueOf(newCreamParam.bottomMargin));
		Log.e("newParam botToBot", String.valueOf(newCreamParam.bottomToBottom));
		Log.e("newParam endToEnd", String.valueOf(newCreamParam.endToEnd));
		Log.e("newParam startToStart", String.valueOf(newCreamParam.startToStart));
		newCream.setLayoutParams(newCreamParam);
		newCream.setImageResource(R.drawable.ph_ic_cream1);*/
	}
}
