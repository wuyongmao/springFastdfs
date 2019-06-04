package com.fastdfs.controller;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.FileInfo;
import org.csource.fastdfs.StorageClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class FileUploadController {

	public static final String SEPARATOR = "/";
	/**
	 * Point
	 */
	public static final String POINT = ".";

	public FileUploadController() {
		System.out.println("FileUploadController ....init ");
	}

//	@Autowired
//	private ServletContext servletContext;

	@Autowired
	private StorageClient storageClient;

	/*
	 * @ResponseBody
	 * 
	 * @PostMapping(value = "/fileUpload") public String fileUpload(
	 * 
	 * @RequestParam("name") String name,
	 * 
	 * @RequestParam("file") MultipartFile file) throws Exception{ if
	 * (!file.isEmpty()) { ClientGlobal.init("src/fastdfs_conf.conf"); TrackerClient
	 * client = new TrackerClient(); TrackerServer tracker = client.getConnection();
	 * StorageClient storageClient = new StorageClient(tracker,null);
	 * storageClient.upload_file() String path =
	 * this.servletContext.getRealPath("/upload"); String filename =
	 * file.getOriginalFilename();
	 * System.out.println("OriginalFilename :"+filename); if (name != null &&
	 * !name.equals("")){ String fileType =
	 * filename.substring(filename.lastIndexOf(".")); filename = name + fileType; }
	 * System.out.println("after filename : "+filename);
	 * System.out.println(path+"/"+filename); File newFile = new File(path + "/" +
	 * filename); System.out.println("path : "+path); try { InputStream inputStream
	 * = file.getInputStream(); FileOutputStream outputStream = new
	 * FileOutputStream(newFile); byte[] arr = new byte[1024]; int len; while ((len
	 * = inputStream.read(arr))!=-1){ outputStream.write(arr,0,len); }
	 * outputStream.close(); inputStream.close(); } catch (Exception e) {
	 * e.printStackTrace(); } return "FileUpload Successful"; } return
	 * "FileUpload Fail"; }
	 */

	/**
	 * http://localhost:8080/springFastdfs/index.jsp
	 * 
	 * @param files
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/fileUpload", method = RequestMethod.POST)
	public String fileUpload(
			/* @RequestParam("name") String name, */
			@RequestParam("file") MultipartFile[] files) throws Exception {
		for (MultipartFile file : files) {
			if (!file.isEmpty()) {
				String filename = file.getOriginalFilename();
				String fileType = filename.substring(filename.lastIndexOf(".") + 1);
				System.out.println("fileType" + fileType);

				NameValuePair[] meta_list = { new NameValuePair("fileName", filename) };
				String[] strings = storageClient.upload_file(file.getBytes(), fileType, meta_list);
				StringBuffer storagePath = new StringBuffer();
				for (String s : strings) {
					storagePath.append(s);
				}
				String fileLocation = storagePath.toString().substring(6);
				String storageGroup = storagePath.substring(0, 6);
				System.out.println("fileAccessLocation : " + fileLocation);
				System.out.println("storageGroup : " + storageGroup);
				System.out.println(storagePath.toString());
			}
		}
		return "FileUpload success";
	}

	
	/**
	 * http://localhost:8080/springFastdfs/download?group=group1&filename=M00/00/00/rBBZRVz2N_WAdUXGAAs_pBa_IUU423.png
	 * @param group
	 * @param filename
	 * @param r
	 * @return
	 */
	@RequestMapping("/download")
	@ResponseBody

	public String fileDownLoad(String group, String filename, HttpServletRequest r) {
		System.out.println("downLoad............");
		System.out.println("group : " + group);
		System.out.println("filename ： " + filename);

		try {
			byte[] bytes = storageClient.download_file(group, filename);
			FileInfo fi = storageClient.get_file_info(group, filename);
			System.out.println("文件信息:" + fi);

			NameValuePair[] nps= storageClient.get_metadata(group, filename);
			for (NameValuePair aa : nps) {
				System.out.println(aa.getName()+":"+aa.getValue()+"------");
			}

			
			
			System.out.println(bytes == null);
			ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			int i = 0;
			FileOutputStream os = new FileOutputStream("/home/yongmaow");
			while ((i = bais.read()) != -1) {
				os.write(i);
			}
			os.flush();
			os.close();
			bais.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (MyException e) {
			e.printStackTrace();
		}
		return "downLoadsuccessful";
	}

	public static String getFilenameSuffix(String filename) {
		String suffix = null;
		if (StringUtils.isNotBlank(filename)) {
			if (filename.contains(SEPARATOR)) {
				filename = filename.substring(filename.lastIndexOf(SEPARATOR) + 1);
			}
			if (filename.contains(POINT)) {
				suffix = filename.substring(filename.lastIndexOf(POINT) + 1);
			} else {

			}
		}
		return suffix;
	}

	@RequestMapping("/fileDelete")
	@ResponseBody
	public Msg fileDelete(HttpServletRequest r, String group, String filename, OutputStream os)
			throws IOException, MyException {
		if (storageClient.delete_file(group, filename) > 0) {
			return Msg.success("删除成功");

		} else {
			return Msg.fail("删除失败");
		}
	}

	/**
	 * http://localhost:4040/lyyzoo-fastdfs-java/download2?group=group1&filename=M00/00/00/rBBZRVzEMfeAZ2uXAAL5_bzafj4387.png
	 * 
	 * @param group
	 * @param filename
	 * @param os
	 * @param r
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("/download2")

	public void fileDownLoad2(String group, String filename, OutputStream os, HttpServletRequest r,
			HttpServletResponse response) throws Exception {
		System.out.println("downLoad............");
		System.out.println("group : " + group);
		System.out.println("filename ： " + filename);
		Map<String, String> EXT_MAPS = new HashMap<String, String>();
		try {
			String contentType = EXT_MAPS.get(getFilenameSuffix(filename));
			// 下载
			byte[] fileByte = storageClient.download_file(group, filename);

			if (response != null) {
				os = response.getOutputStream();

				// 设置响应头
				if (StringUtils.isNotBlank(contentType)) {
					// 文件编码 处理文件名中的 '+'、' ' 特殊字符
					String encoderName = URLEncoder.encode(filename, "UTF-8").replace("+", "%20").replace("%2B", "+");
					response.setHeader("Content-Disposition", "attachment;filename=\"" + encoderName + "\"");
					response.setContentType(contentType + ";charset=UTF-8");
					response.setHeader("Accept-Ranges", "bytes");
				}
			}

			ByteArrayInputStream is = new ByteArrayInputStream(fileByte);
			byte[] buffer = new byte[1024 * 5];
			int len = 0;
			while ((len = is.read(buffer)) > 0) {
				os.write(buffer, 0, len);
			}
			os.flush();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (MyException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 调用下载弹窗
	 * http://localhost:4040/lyyzoo-fastdfs-java/download3?group=group1&filename=M00/00/00/rBBZRVzEdbKAYzuWAAsQA-hBNJU206.jpg
	 * 
	 * @param r
	 * @param group
	 * @param filename
	 * @param os
	 * @return
	 * @throws IOException
	 * @throws MyException
	 */
	@RequestMapping(value = "/download3", method = RequestMethod.GET) // 匹配的是href中的download请求
	public ResponseEntity<byte[]> download(HttpServletRequest r, String group, String filename, OutputStream os)
			throws IOException, MyException {

		byte[] bytes = storageClient.download_file(group, filename);
		FileInfo fi = storageClient.get_file_info(group, filename);
		System.out.println("文件信息:" + fi);

		NameValuePair[] nps= storageClient.get_metadata(group, filename);
		for (NameValuePair aa : nps) {
			System.out.println(aa.getName()+":"+aa.getValue()+"------");
		}

		
		
		
		HttpHeaders headers = new HttpHeaders();// http头信息

		String downloadFileName = new String(filename.getBytes("UTF-8"), "iso-8859-1");// 设置编码
		headers.setContentDispositionFormData("attachment", downloadFileName);
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

		// MediaType:互联网媒介类型 contentType：具体请求中的媒体类型信息
		return new ResponseEntity<byte[]>(bytes, headers, HttpStatus.CREATED);

	}

}
