package com.natalieryan.android.superaudiobookplayer.ui.filebrowser;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.natalieryan.android.superaudiobookplayer.R;
import com.natalieryan.android.superaudiobookplayer.databinding.FragmentFileBrowserBinding;
import com.natalieryan.android.superaudiobookplayer.model.FileItem;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;

@SuppressWarnings("unused")
public class FileBrowserFragment extends Fragment implements FileItemAdapter.FileClickListener
{
	public static final String TAG = FileBrowserFragment.class.getSimpleName();

	private static final String CURRENT_PATH = "current_path";
	private static final String PARENT_PATH = "parent_path";
	private static final String DEVICE_ROOT_PATH = "device_root_path";
	private static final String SD_CARD_ROOT_PATH = "sd_card_root_path";
	private static final String FILES = "files";
	private static final String SHOW_ONLY_FOLDERS = "show_only_folders";
	private static final String SELECTED_ITEM = "selected_item";
	private static final String ROOT_ITEM = "root_item";

	private FragmentFileBrowserBinding mBinder;
	private String mDeviceRootPath;
	private String mSdCardRootPath;
	private FileItem mSelectedItem;
	private FileItem mSessionRootItem;

	// new sauce
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

			if(savedInstanceState.containsKey(SHOW_ONLY_FOLDERS))
			{
				mShowOnlyFolders = savedInstanceState.getInt(SHOW_ONLY_FOLDERS) == 1;
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
			mDeviceRootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
			mSdCardRootPath = getSdCardPath(mDeviceRootPath);
			mCurrentPath = mDeviceRootPath;
			mSessionRootItem = createRootLevelFileItem(mCurrentPath, false);
			mSelectedItem = mSessionRootItem;
		}

		mBinder = DataBindingUtil.inflate(inflater, R.layout.fragment_file_browser, container, false);

		View rootView = mBinder.getRoot();

		mBinder.backArrowImageView.setOnClickListener(new View.OnClickListener() {
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

		mBinder.selectedFileNameTv.setText(mSelectedItem.getName());
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

		outState.putInt(SHOW_ONLY_FOLDERS, mShowOnlyFolders ? 1 : 0);
	}

	private void loadFileList(String currentLocation)
	{
		if(currentLocation != null && !currentLocation.isEmpty())
		{
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

		if(mParentPath !=null)
		{
			mCurrentPath = mParentPath;
			mParentPath = getParentFilePath(mCurrentPath);
			loadFileList(mCurrentPath);
			mSelectedItem = mFiles.get(0);
		}
		else
		{
			mCurrentPath = mSessionRootItem.getPath();
			loadFileList(mCurrentPath);
			mSelectedItem = mSessionRootItem;
			mBinder.backArrowImageView.setImageResource(mSelectedItem.getIcon());
		}
		mBinder.selectedFileNameTv.setText(mSelectedItem.getName());
	}

	public boolean isAtTopLevel(){
		return mSelectedItem.equals(mSessionRootItem);
	}

	@Override
	public void onFileClick (View view, int position)
	{
		final FileItem fileItem = mFileItemAdapter.getItem(position);
		if(fileItem != null)
		{
			mSelectedItem = fileItem;
			mBinder.selectedFileNameTv.setText(mSelectedItem.getName());
			mBinder.backArrowImageView.setImageResource(R.drawable.ic_arrow_back_black_24dp);
			if(fileItem.getHasChildren())
			{
				mParentPath = fileItem.getParentPath();
				mCurrentPath = fileItem.getPath();
				loadFileList(mCurrentPath);
			}
		}
	}

	private String getSdCardPath(String baseStoragePath)
	{
		File storageRoots[] = new File("/storage/").listFiles();
		ArrayList<String> sdCardPaths = new ArrayList<>();

		for(File singleRoot : storageRoots)
		{
			if(!singleRoot.getAbsolutePath().equalsIgnoreCase(baseStoragePath)
					&& singleRoot.isDirectory()
					&& singleRoot.canRead())
			{
				sdCardPaths.add(singleRoot.getAbsolutePath());
			}
		}

		if(sdCardPaths.isEmpty())
		{
			return null;
		}
		else
		{
			return sdCardPaths.get(0);
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
		String displayName;
		FileItem fileItem = new FileItem();
		File file = new File(rootPath);
		if(isSDCard)
		{
			fileItem.setName(getString(R.string.sd_root_folder));
			fileItem.setIcon(R.drawable.ic_phone_android_black_24dp);
		}
		else
		{
			fileItem.setName(getString(R.string.device_root_folder));
			fileItem.setIcon(R.drawable.ic_phone_android_black_24dp);
		}
		fileItem.setPath(file.getPath());
		fileItem.setIsDirectory(true);
		fileItem.setSize(-1);
		fileItem.setHasChildren(file.listFiles().length > 0);
		fileItem.setIsTopLevel(isTopLevelFolder(file.getAbsolutePath()));
		fileItem.setParentPath(null);
		return fileItem;
	}
}