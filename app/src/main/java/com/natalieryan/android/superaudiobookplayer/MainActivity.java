package com.natalieryan.android.superaudiobookplayer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.natalieryan.android.superaudiobookplayer.ui.folderbrowser.FolderBrowserActivity;

public class MainActivity extends AppCompatActivity
{

	private static final int PERMISSION_REQUEST_CODE = 101;

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

	private void launchTagReader()
	{
		Intent intent = new Intent(this, TagReader.class);
		startActivity(intent);

	}

	private void launchFolderBrowser()
	{
		int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
		if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this,
					new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
		} else {
			Intent intent = new Intent(this, FolderBrowserActivity.class);
			startActivity(intent);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
										   @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == PERMISSION_REQUEST_CODE) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				Intent intent = new Intent(this, FolderBrowserActivity.class);
				startActivity(intent);
			}
		}
	}
}
