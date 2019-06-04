package com.fastdfs.test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;

import org.csource.common.NameValuePair;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.FileInfo;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.Test;

public class TestFastDfs {

	// fdfs_client 核心配置文件

	public String conf_filename = "src/main/resources/config2.properties";
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Test
	public void testUpload() { // 上传文件
		TrackerServer trackerServer = null;
		StorageServer storageServer = null;

		try {
			ClientGlobal.init(conf_filename);
			TrackerClient tracker = new TrackerClient();
			trackerServer = tracker.getConnection();
			StorageClient1 client = new StorageClient1(trackerServer, storageServer);

			// 要上传的文件路径
			String local_filename = "/home/yongmaow/图片/2019-04-09 18-30-33 的屏幕截图.png";
//            这个参数可以指定，也可以不指定，如果指定了，可以根据 testGetFileMate()方法来获取到这里面的值
			NameValuePair nvp[] = new NameValuePair[] { new NameValuePair("local_filename", local_filename), new NameValuePair("sex", "male") };

			StorageClient storageClient = new StorageClient(trackerServer, storageServer);
			String fileIds[] = storageClient.upload_file(local_filename, "png", nvp);
//            String fileIds[] = storageClient.upload_file(local_filename, "png", null);

			System.out.println(fileIds.length);
			System.out.println("组名：" + fileIds[0]);
			System.out.println("路径: " + fileIds[1]);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != storageServer)
					storageServer.close();
				if (null != trackerServer)
					trackerServer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test
	public void testDownload() { // 下载文件
		TrackerServer trackerServer = null;
		StorageServer storageServer = null;

		try {
			String groupName = "group1";
			String filePath = "M00/00/00/rBBZRVzEMfeAZ2uXAAL5_bzafj4387.png";
			ClientGlobal.init(conf_filename);

			TrackerClient tracker = new TrackerClient();
			trackerServer = tracker.getConnection();

			StorageClient storageClient = new StorageClient(trackerServer, storageServer);
			byte[] bytes = storageClient.download_file(groupName, filePath);

			String storePath = "/home/yongmaow/图片/fastdfs/aa.png";
			OutputStream out = new FileOutputStream(storePath);
			out.write(bytes);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != storageServer)
					storageServer.close();
				if (null != trackerServer)
					trackerServer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test
	public void testGetFileInfo() { // 获取文件信息
		TrackerServer trackerServer = null;
		StorageServer storageServer = null;

		try {
			String groupName = "group1";
			String filePath = "M00/00/00/rBBZRVzEaW2AMHmZAAL5_bzafj4239.png";
			ClientGlobal.init(conf_filename);

			TrackerClient tracker = new TrackerClient();
			trackerServer = tracker.getConnection();

			StorageClient storageClient = new StorageClient(trackerServer, storageServer);
			FileInfo file = storageClient.get_file_info(groupName, filePath);
			System.out.println("ip--->" + file.getSourceIpAddr());
			System.out.println("文件大小--->" + file.getFileSize());

			System.out.println("文件上传时间--->" + sdf.format(file.getCreateTimestamp().getTime()));
			System.out.println(file.getCrc32());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != storageServer)
					storageServer.close();
				if (null != trackerServer)
					trackerServer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test
	public void testGetFileMate() { // 获取文件的原数据类型
		TrackerServer trackerServer = null;
		StorageServer storageServer = null;

		try {
			String groupName = "group1";
			String filePath = "M00/00/00/rBBZRVzEaW2AMHmZAAL5_bzafj4239.png";
			ClientGlobal.init(conf_filename);

			TrackerClient tracker = new TrackerClient();
			trackerServer = tracker.getConnection();

			StorageClient storageClient = new StorageClient(trackerServer, storageServer);

			// 这个值是上传的时候指定的NameValuePair
			NameValuePair nvps[] = storageClient.get_metadata(groupName, filePath);
			if (null != nvps && nvps.length > 0) {
				for (NameValuePair nvp : nvps) {
					System.out.println(nvp.getName() + ":" + nvp.getValue());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != storageServer)
					storageServer.close();
				if (null != trackerServer)
					trackerServer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test
	public void testDelete() { // 删除文件
		TrackerServer trackerServer = null;
		StorageServer storageServer = null;

		try {
			String groupName = "group1";
			String filePath = "M00/00/00/ZGIW_lpujW-ADvpRAAblmT4ACuo125.png";
			ClientGlobal.init(conf_filename);

			TrackerClient tracker = new TrackerClient();
			trackerServer = tracker.getConnection();

			StorageClient storageClient = new StorageClient(trackerServer, storageServer);
			int i = storageClient.delete_file(groupName, filePath);
			System.out.println(i == 0 ? "删除成功" : "删除失败:" + i);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != storageServer)
					storageServer.close();
				if (null != trackerServer)
					trackerServer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}