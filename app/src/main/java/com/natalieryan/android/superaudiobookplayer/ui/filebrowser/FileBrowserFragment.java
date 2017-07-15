package com.natalieryan.android.superaudiobookplayer.ui.filebrowser;


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
import com.natalieryan.android.superaudiobookplayer.databinding.FragmentFileBrowserBinding;
import com.natalieryan.android.superaudiobookplayer.model.FileItem;
import com.natalieryan.android.superaudiobookplayer.utils.PathUtils;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;

@SuppressWarnings("unused")
public class FileBrowserFragment extends Fragment implements FileItemAdapter.FileClickListener
{
	public static final String TAG = FileBrowserFragment.class.getSimpleName();

	public static final String SHOW_FOLDERS_ONLY = "show_folders_only";
	public static final String EXTRA_FILE_PATH = "file_path";
	public static final String EXTRA_FILE_IS_ON_SD_CARD = "file_is_on_sd_card";

	private static final String CURRENT_PATH = "current_path";
	private static final String PARENT_PATH = "parent_path";
	private static final String DEVICE_ROOT_PATH = "device_root_path";
	private static final String SD_CARD_ROOT_PATH = "sd_card_root_path";
	private static final String FILES = "files";
	private static final String SELECTED_ITEM = "selected_item";
	private static final String ROOT_ITEM = "root_item";
	private static final String SELECTED_FILE = "selected_file";

	private FragmentFileBrowserBinding mBinder;
	private String mDeviceRootPath;
	private String mSdCardRootPath;
	private FileItem mSelectedItem;
	private FileItem mSessionRootItem;
	private String mCurrentPath;
	private String mParentPath;
	private ArrayList<FileItem> mFiles = new ArrayList<>();
	private boolean mShowOnlyFolders = false;
	private FileItemAdapter mFileItemAdapter;


	//default constructor
	public FileBrowserFragment() {}

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

			if(savedInstanceState.containsKey(FILES))
			{
				mFiles = savedInstanceState.getParcelableArrayList(FILES);
			}

			if(savedInstanceState.containsKey(DEVICE_ROOT_PATH))
			{
				mDeviceRootPath = savedInstanceState.getString(DEVICE_ROOT_PATH);
			}

			if(savedInstanceState.containsKey(SD_CARD_ROOT_PATH))
			{
				mSdCardRootPath = savedInstanceState.getString(SD_CARD_ROOT_PATH);
			}

			if(savedInstanceState.containsKey(SHOW_FOLDERS_ONLY))
			{
				mShowOnlyFolders = savedInstanceState.getInt(SHOW_FOLDERS_ONLY) == 1;
			}

			if(savedInstanceState.containsKey(SELECTED_ITEM))
			{
				mSelectedItem = savedInstanceState.getParcelable(SELECTED_ITEM);
			}

			if(savedInstanceState.containsKey(ROOT_ITEM))
			{
				mSessionRootItem = savedInstanceState.getParcelable(ROOT_ITEM);
			}
		}
		else
		{
			Bundle args=getArguments();
			if (args!=null)
			{
				if (args.containsKey(SHOW_FOLDERS_ONLY))
				{
					mShowOnlyFolders = getArguments().getInt(SHOW_FOLDERS_ONLY) == 1;
				}
			}
			mDeviceRootPath = PathUtils.getDeviceRootStoragePath();
			mSdCardRootPath = PathUtils.getSdCardPath();
			mCurrentPath = mDeviceRootPath;
			mSessionRootItem = createRootLevelFileItem(mCurrentPath, false);
			mSelectedItem = mSessionRootItem;
		}

		mBinder = DataBindingUtil.inflate(inflater, R.layout.fragment_file_browser, container, false);

		View rootView = mBinder.getRoot();

		mBinder.backArrowImageView.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v) {
				navigateBack();
			}
		});

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

		//show device/sd card toggle buttons if SD card present
		if(PathUtils.sdCardIsMounted())
		{
			mBinder.sdCardButton.setVisibility(View.VISIBLE);
			mBinder.deviceButton.setVisibility(View.VISIBLE);
			mBinder.fileBrowserButtonBottom.setVisibility(View.VISIBLE);

			mBinder.sdCardButton.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View v)
				{
				if(!isUsingSdCard())
				{
					if(PathUtils.sdCardIsMounted())
					{
						swapRoot(mSdCardRootPath);
					}
					else
					{
						handleSdCardNotPresent();
					}

				}
				}
			});

			mBinder.deviceButton.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View v)
				{
				if(!mSessionRootItem.getPath().equalsIgnoreCase(mDeviceRootPath))
				{
					swapRoot(mDeviceRootPath);
				}
				}
			});
		}

		//set the handlers for our select/cancel buttons
		mBinder.browserSelectButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				Activity callingActivity = getActivity();
				Intent returnIntent = new Intent();
				returnIntent.putExtra(EXTRA_FILE_PATH, mSelectedItem.getPath());
				returnIntent.putExtra(EXTRA_FILE_IS_ON_SD_CARD, isUsingSdCard());
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

		if(mDeviceRootPath != null)
		{
			outState.putString(DEVICE_ROOT_PATH, mDeviceRootPath);
		}

		if(mSdCardRootPath != null)
		{
			outState.putString(SD_CARD_ROOT_PATH, mSdCardRootPath);
		}

		if(mSelectedItem != null)
		{
			outState.putParcelable(SELECTED_ITEM, mSelectedItem);
		}

		if(mSessionRootItem != null)
		{
			outState.putParcelable(ROOT_ITEM, mSessionRootItem);
		}

		outState.putInt(SHOW_FOLDERS_ONLY, mShowOnlyFolders ? 1 : 0);
	}

	private void loadFileList(String currentLocation)
	{
		if(currentLocation != null && !currentLocation.isEmpty())
		{
			//check to make sure the SD card is still mounted if it's in play

			mFiles = getFileItems(currentLocation, mShowOnlyFolders);
			mFileItemAdapter.setFileList(mFiles);
		}
	}


	@Nullable
	private String getParentFilePath(String currentFilePath)
	{
		File currentFile = new File(currentFilePath);

		if(!currentFilePath.equalsIgnoreCase(mDeviceRootPath) && !currentFilePath.equalsIgnoreCase(mSdCardRootPath))
		{
			return currentFile.getParentFile().getAbsolutePath();
		}
		else
		{
			return null;
		}
	}

	public void navigateBack() {

		if(isUsingSdCard() && !PathUtils.sdCardIsMounted())
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
		if(isUsingSdCard() && !PathUtils.sdCardIsMounted()){
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
					fileItem.setIcon(R.drawable.ic_insert_drive_file_black_24dp);
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
		boolean isTopLevel = false;

		if(folderPath.equalsIgnoreCase(mDeviceRootPath))
		{
			isTopLevel = true;
		}
		else if (mSdCardRootPath != null && folderPath.equalsIgnoreCase(mSdCardRootPath))
		{
			isTopLevel = true;
		}

		return isTopLevel;
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
		fileItem.setIsTopLevel(isTopLevelFolder(file.getAbsolutePath()));
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

	private void swapRoot(String rootPath)
	{
		boolean isSDCard = false;

		if(rootPath.equals(mSdCardRootPath))
		{
			isSDCard = true;
		}
		mSessionRootItem = createRootLevelFileItem(rootPath,isSDCard);
		mSelectedItem = mSessionRootItem;
		mBinder.selectedFileNameTv.setText(getString(R.string.selected_folder, mSelectedItem.getName()));
		mBinder.backArrowImageView.setImageResource(mSelectedItem.getIcon());
		mCurrentPath = mSelectedItem.getPath();
		mParentPath = null;
		loadFileList(mCurrentPath);
	}

	private boolean isUsingSdCard()
	{
		return mSessionRootItem.getPath().equalsIgnoreCase(mSdCardRootPath);
	}

	private void handleSdCardNotPresent()
	{
		Toast.makeText(getContext(), R.string.sd_card_unmounted, Toast.LENGTH_LONG).show();
		swapRoot(mDeviceRootPath);
		mBinder.sdCardButton.setEnabled(false);
	}
}