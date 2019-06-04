### FastDFS Client Java API

这是基于[fastdfs-client-java]
开发、使用commons-pool2连接池封装的工具类，提供简易的上传下载功能.

* FastDFSClient 主要的工具类
* ErrorCode 异常编码
* FastDFSException 异常类
* FileResponseData 文件返回信息
* TrackerServerFactory TrackerServer Factory
* TrackerServerPool TrackerServer Pool
  


### docker 部署 fastdfs
~~~

  docker  search fastdfs
  docker pull qbanxiaoli/fastdfs
  docker images
  mkdir -p /var/fdfs
  
  docker run -d --restart=always --privileged=true --net=host --name=fastdfs -e IP=172.16.89.69 -e WEB_PORT=888 -v /var/fdfs:/var/local/fdfs qbanxiaoli/fastdfs
  docker exec -it fastdfs /bin/bash
  
  echo "Hello FastDFS!" index.html
  fdfs_test /etc/fdfs/client.conf upload index.html

  http://172.16.89.69:888/group1/M00/00/00/rBBZRVz2LECAcf1yAAAADxjm17c12.html
