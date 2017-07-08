package com.natalieryan.android.superaudiobookplayer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//launch tag reader
		Button tagButton = (Button) findViewById(R.id.tag_reader_btn);
		tagButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				launchTagReader();
			}
		});

		//launch folder browser
		Button folderButton = (Button) findViewById(R.id.folder_button);
		folderButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				launchFolderBrowser();
			}
		});
	}

	public void launchTagReader()
	{
		Intent intent = new Intent(this, TagReader.class);
		startActivity(intent);

	}

	public void launchFolderBrowser()
	{
		Intent intent = new Intent(this, FolderBrowserActivity.class);
		startActivity(intent);

	}
}
