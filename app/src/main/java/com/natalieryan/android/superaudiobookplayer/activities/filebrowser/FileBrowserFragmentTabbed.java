package com.natalieryan.android.superaudiobookplayer.activities.filebrowser;


import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.natalieryan.android.superaudiobookplayer.R;
import com.natalieryan.android.superaudiobookplayer.databinding.FragmentFileBrowserTabbedBinding;
import com.natalieryan.android.superaudiobookplayer.model.FileItem;
import com.natalieryan.android.superaudiobookplayer.utils.filesystem.FileUtils;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;

@SuppressWarnings("unused")
public class FileBrowserFragmentTabbed extends Fragment implements FileItemAdapter.FileClickListener
{
	//TODO: Extend to allow showing files but NOT allowing selection
	public static final String TAG = FileBrowserFragmentTabbed.class.getSimpleName();

	//public options for the fragment
	public static final String SHOW_FOLDERS_ONLY = "show_folders_only";
	public static final String ALLOW_FILE_SELECTION = "allow_file_selection";
	public static final String BROWSER_ROOT_PATH = "browser_root_path";

	//extras for the return intent
	public static final String EXTRA_FILE_PATH = "file_path";
	public static final String EXTRA_FILE_IS_ON_SD_CARD = "file_is_on_sd_card";

	//additional keys for saved instance state
	private static final String CURRENT_PATH = "current_path";
	private static final String PARENT_PATH = "parent_path";
	private static final String DEVICE_ROOT_PATH = "device_root_path";
	private static final String FILES = "files";
	private static final String SELECTED_ITEM = "selected_item";
	private static final String ROOT_ITEM = "root_item";
	private static final String ROOT_PATH_IS_ON_SD = "root_path_is_on_sd";
	private static final String SELECTED_FILE = "selected_file";

	private FragmentFileBrowserTabbedBinding mBinder;
	private FileItem mSelectedItem;
	private FileItem mSessionRootItem;
	private String mCurrentPath;
	private String mParentPath;
	private ArrayList<FileItem> mFiles = new ArrayList<>();
	private FileItemAdapter mFileItemAdapter;
	private boolean mRootPathIsOnSDCard;

	/*
		these can be overriden by passing in args to the fragment after construction
		by default, the browser will show files and folder, allow files and folders to be
		selected, and start off at the root of the device's internal emulated storage
	 */
	private boolean mShowOnlyFolders = false;
	private Boolean mAllowFileSelection = true;
	private String mBrowserRootPath = FileUtils.getDeviceRootStoragePath();


	//default constructor
	public FileBrowserFragmentTabbed() {}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		if(savedInstanceState != null)
		{
			if(savedInstanceState.containsKey(CURRENT_PATH))
			{
				mCurrentPath = savedInstanceState.getString(CURRENT_PATH);
			}

			if(savedInstanceState.containsKey(PARENT_PATH))
			{
				mParentPath = savedInstanceState.getString(PARENT_PATH);
			}

			if(savedInstanceState.containsKey(BROWSER_ROOT_PATH))
			{
				mBrowserRootPath = savedInstanceState.getString(BROWSER_ROOT_PATH);
			}

			if(savedInstanceState.containsKey(SELECTED_ITEM))
			{
				mSelectedItem = savedInstanceState.getParcelable(SELECTED_ITEM);
			}

			if(savedInstanceState.containsKey(ROOT_ITEM))
			{
				mSessionRootItem = savedInstanceState.getParcelable(ROOT_ITEM);
			}

			if(savedInstanceState.containsKey(FILES))
			{
				mFiles = savedInstanceState.getParcelableArrayList(FILES);
			}

			if(savedInstanceState.containsKey(SHOW_FOLDERS_ONLY))
			{
				mShowOnlyFolders = savedInstanceState.getBoolean(SHOW_FOLDERS_ONLY);
			}

			if(savedInstanceState.containsKey(ALLOW_FILE_SELECTION))
			{
				mAllowFileSelection = savedInstanceState.getBoolean(ALLOW_FILE_SELECTION);
			}

			if(savedInstanceState.containsKey(ROOT_PATH_IS_ON_SD))
			{
				mRootPathIsOnSDCard = savedInstanceState.getBoolean(ROOT_PATH_IS_ON_SD);
			}
		}
		else
		{
			Bundle args=getArguments();
			if (args!=null)
			{
				if (args.containsKey(SHOW_FOLDERS_ONLY))
				{
					mShowOnlyFolders = getArguments().getBoolean(SHOW_FOLDERS_ONLY);
					mAllowFileSelection = getArguments().getBoolean(ALLOW_FILE_SELECTION);
					mBrowserRootPath = getArguments().getString(BROWSER_ROOT_PATH);
				}
			}
			mCurrentPath = mBrowserRootPath;
			mRootPathIsOnSDCard = FileUtils.fileIsOnMountedSdCard(mCurrentPath);
			mSessionRootItem = createRootLevelFileItem(mCurrentPath, mRootPathIsOnSDCard);
			mSelectedItem = mSessionRootItem;
		}


		mBinder = DataBindingUtil.inflate(inflater, R.layout.fragment_file_browser_tabbed, container, false);
		View rootView = mBinder.getRoot();

		LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());
		mFileItemAdapter = new FileItemAdapter();
		mFileItemAdapter.setClickListener(this);
		mBinder.fileListRv.setAdapter(mFileItemAdapter);
		mBinder.fileListRv.setLayoutManager(layoutManager);

		if(mFiles !=null && !mFiles.isEmpty())
		{
			mFileItemAdapter.setFileList(mFiles);
		}
		else
		{
			loadFileList(mCurrentPath);
		}


		//set the handlers for our select/cancel/back buttons
		mBinder.browserSelectButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				Activity callingActivity = getActivity();
				Intent returnIntent = new Intent();
				returnIntent.putExtra(EXTRA_FILE_PATH, mSelectedItem.getPath());
				returnIntent.putExtra(EXTRA_FILE_IS_ON_SD_CARD, mRootPathIsOnSDCard);
				callingActivity.setResult(Activity.RESULT_OK, returnIntent);
				callingActivity.finish();
			}
		});

		mBinder.browserCancelButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				Activity callingActivity = getActivity();
				callingActivity.setResult(Activity.RESULT_CANCELED);
				callingActivity.finish();
			}
		});

		mBinder.backArrowImageView.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v) {
				navigateBack();
			}
		});

		//set the icon foe the selected file/folder
		mBinder.selectedFileNameTv.setText(getString(R.string.selected_folder, mSelectedItem.getName()));
		if(mSelectedItem.equals(mSessionRootItem))
		{
			mBinder.backArrowImageView.setImageResource(mSelectedItem.getIcon());
		}

		return rootView;
	}


	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		if (mCurrentPath!=null)
		{
			outState.putString(CURRENT_PATH, mCurrentPath);
		}

		if (mParentPath != null)
		{
			outState.putString(PARENT_PATH, mParentPath);
		}

		if(mFiles != null && !mFiles.isEmpty())
		{
			outState.putParcelableArrayList(FILES, mFiles);
		}

		if(mBrowserRootPath != null)
		{
			outState.putString(BROWSER_ROOT_PATH, mBrowserRootPath);
		}

		if(mSelectedItem != null)
		{
			outState.putParcelable(SELECTED_ITEM, mSelectedItem);
		}

		if(mSessionRootItem != null)
		{
			outState.putParcelable(ROOT_ITEM, mSessionRootItem);
		}

		outState.putBoolean(SHOW_FOLDERS_ONLY, mShowOnlyFolders);
		outState.putBoolean(ALLOW_FILE_SELECTION, mAllowFileSelection);
		outState.putBoolean(ROOT_PATH_IS_ON_SD, mRootPathIsOnSDCard);
	}

	private void loadFileList(String currentLocation)
	{
		if(currentLocation != null && !currentLocation.isEmpty())
		{
			//check to make sure the SD card is still mounted if it's in play
//TODO - sd card check here
			mFiles = getFileItems(currentLocation, mShowOnlyFolders);
			mFileItemAdapter.setFileList(mFiles);
		}
	}


	@Nullable
	private String getParentFilePath(String currentFilePath)
	{
		File currentFile = new File(currentFilePath);

		if(!currentFilePath.equalsIgnoreCase(mBrowserRootPath))
		{
			return currentFile.getParentFile().getAbsolutePath();
		}
		else
		{
			return null;
		}
	}

	public void navigateBack() {

		if(mRootPathIsOnSDCard && !FileUtils.sdCardIsMounted())
		{
			handleSdCardNotPresent();
			return;
		}

		if(mParentPath !=null)
		{
			mCurrentPath = mParentPath;
			mParentPath = getParentFilePath(mCurrentPath);
			loadFileList(mCurrentPath);
			mSelectedItem = CreateParentFileItem(mCurrentPath);
			if(mSelectedItem.getPath().equalsIgnoreCase(mSessionRootItem.getPath())){
				mSelectedItem = mSessionRootItem;
				mBinder.backArrowImageView.setImageResource(mSelectedItem.getIcon());
			}
		}
		else
		{
			mCurrentPath = mSessionRootItem.getPath();
			loadFileList(mCurrentPath);
			mSelectedItem = mSessionRootItem;
			mBinder.backArrowImageView.setImageResource(mSelectedItem.getIcon());
		}
		mBinder.selectedFileNameTv.setText(getString(R.string.selected_folder, mSelectedItem.getName()));
	}

	public boolean isAtTopLevel(){
		return mSelectedItem.equals(mSessionRootItem);
	}

	@Override
	public void onFileClick (View view, int position)
	{
		if(mRootPathIsOnSDCard && !FileUtils.sdCardIsMounted()){
			handleSdCardNotPresent();
			return;
		}
		final FileItem fileItem = mFileItemAdapter.getItem(position);
		if(fileItem != null)
		{
			mSelectedItem = fileItem;
			mBinder.selectedFileNameTv.setText(getString(R.string.selected_folder, mSelectedItem.getName()));
			mBinder.backArrowImageView.setImageResource(R.drawable.ic_arrow_back_black_24dp);
			if(fileItem.getIsDirectory())
			{
				mParentPath = fileItem.getParentPath();
				mCurrentPath = fileItem.getPath();
				loadFileList(mCurrentPath);
			}
		}
	}

	private ArrayList<FileItem> getFileItems(String filePath, boolean fetchFoldersOnly)
	{
		ArrayList<FileItem> fileAndFolderItems = new ArrayList<>();

		File currentLocation = new File(filePath);

		File[] folders = currentLocation.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});

		if(folders.length > 0)
		{
			for(File file : folders)
			{
				FileItem fileItem = new FileItem();
				fileItem.setName(file.getName());
				fileItem.setPath(file.getPath());
				fileItem.setIsDirectory(true);
				fileItem.setIcon(R.drawable.ic_folder_black_24dp);
				fileItem.setSize(-1);
				fileItem.setHasChildren(file.listFiles().length > 0);
				fileItem.setIsTopLevel(isTopLevelFolder(file.getAbsolutePath()));
				fileItem.setParentPath(filePath);
				fileAndFolderItems.add(fileItem);
			}
			Collections.sort(fileAndFolderItems);
		}

		if(!fetchFoldersOnly)
		{
			ArrayList<FileItem> fileItems = new ArrayList<>();
			File[] files = currentLocation.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return !pathname.isDirectory();
				}
			});
			if(files.length > 0)
			{
				for(File file : files)
				{
					FileItem fileItem = new FileItem();
					fileItem.setName(file.getName());
					fileItem.setPath(file.getPath());
					fileItem.setIsDirectory(false);
					fileItem.setIcon(FileUtils.getIconIdForFile(file.getPath()));
					fileItem.setSize(file.length());
					fileItem.setHasChildren(false);
					fileItem.setParentPath(filePath);
					fileItem.setIsTopLevel(false);
					fileItems.add(fileItem);
				}
				Collections.sort(fileItems);
				fileAndFolderItems.addAll(fileItems);
			}
		}
		return fileAndFolderItems;
	}

	private boolean isTopLevelFolder(String folderPath)
	{
		return folderPath.equalsIgnoreCase(mBrowserRootPath);
	}

	private FileItem createRootLevelFileItem(String rootPath, boolean isSDCard)
	{
		FileItem fileItem = new FileItem();
		File file = new File(rootPath);

		if(isSDCard)
		{
			fileItem.setName(getString(R.string.sd_root_folder));
		}
		else
		{
			fileItem.setName(getString(R.string.device_root_folder));
		}
		fileItem.setIcon(R.drawable.ic_home_black_24dp);
		fileItem.setPath(file.getPath());
		fileItem.setIsDirectory(true);
		fileItem.setSize(-1);
		fileItem.setHasChildren(file.listFiles().length > 0);
		fileItem.setIsTopLevel(true);
		fileItem.setParentPath(null);
		return fileItem;
	}

	private FileItem CreateParentFileItem(String path)
	{

		FileItem fileItem = new FileItem();
		File file = new File(path);
		fileItem.setName(file.getName());
		fileItem.setIcon(R.drawable.ic_folder_black_24dp);
		fileItem.setPath(file.getPath());
		fileItem.setIsDirectory(true);
		fileItem.setSize(-1);
		fileItem.setHasChildren(true);
		boolean isTopLevel = isTopLevelFolder(file.getAbsolutePath());
		if(!isTopLevel)
		{
			File parentFile = new File(file.getParent());
			fileItem.setParentPath(parentFile.getAbsolutePath());
		}
		return fileItem;
	}

	private void handleSdCardNotPresent()
	{
		Toast.makeText(getContext(), R.string.sd_card_unmounted, Toast.LENGTH_LONG).show();
		//TODO - listener callback here
	}
}