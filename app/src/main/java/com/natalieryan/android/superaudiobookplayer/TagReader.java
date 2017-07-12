package com.natalieryan.android.superaudiobookplayer;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class TagReader extends AppCompatActivity
{


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tag_reader);
		overridePendingTransition(R.anim.swap_in_bottom, R.anim.swap_out_bottom);
		ActionBar actionBar = this.getSupportActionBar();

		if (actionBar != null)
		{
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public void onBackPressed()
	{
			super.onBackPressed();
			overridePendingTransition(R.anim.swap_in_bottom, R.anim.swap_out_bottom);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int id = item.getItemId();
		if (id == android.R.id.home) {
			super.onBackPressed();
			overridePendingTransition(R.anim.swap_in_bottom, R.anim.swap_out_bottom);
		}
		return super.onOptionsItemSelected(item);
	}
}
