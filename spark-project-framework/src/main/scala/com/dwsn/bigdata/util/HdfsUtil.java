package com.dwsn.bigdata.util;

import com.dwsn.bigdata.constants.ConfConstants;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Dawson
 * @date 2022-9-30 15:58:43
 */
public class HdfsUtil {

    private HdfsUtil() {}

    /**
     * 通过设置HDFS及用户使用文件系统
     * @param url
     * @param user
     * @throws URISyntaxException
     * @throws IOException
     * @throws InterruptedException
     */
    public static HdfsSession initialize(String url, Configuration conf, String user)
            throws URISyntaxException, IOException, InterruptedException {
        FileSystem fs = FileSystem.get(new URI(url), conf, user);
        return new HdfsSession(fs);
    }

    /**
     * 通过传入conf初始化
     * @param conf
     * @param user
     * @return
     */
    public static HdfsSession initialize(Configuration conf, String user)
            throws URISyntaxException, IOException, InterruptedException {
        String url = conf.get(ConfConstants.HDFS_DEFAULT_FS());
        return initialize(url, conf, user);
    }

    static class HdfsSession {
        private FileSystem fs;

        private HdfsSession(FileSystem fs) {
            this.fs = fs;
        }

        /**
         * 获取当前Session的FileSystem对象
         * @return
         */
        public FileSystem getFileSystem() {
            return fs;
        }

        /**
         * 新建文件
         * @param filePath
         * @param data
         * @throws IOException
         */
        public void createFile(String filePath, byte[] data) throws IOException {
            FSDataOutputStream outputStream = fs.create(new Path(filePath));
            outputStream.write(data);
            outputStream.close();
            fs.close();
        }

        /**
         * 新建文件
         * @param filePath
         * @param data
         * @throws IOException
         */
        public void createFile(String filePath, String data) throws IOException {
            createFile(filePath, data.getBytes());
        }

        /**
         * 从本地上传到HDFS
         * @param localPath 本地文件路径
         * @param remotePath HDF文件S路径
         * @throws IllegalArgumentException
         * @throws IOException
         */
        public void copyFileFromLocal(String localPath, String remotePath)
                throws IllegalArgumentException, IOException {
            fs.copyFromLocalFile(new Path(localPath), new Path(remotePath));
        }

        /**
         * 从HDFS下载到本地
         * @param remotePath HDF文件S路径
         * @param localPath 本地文件路径
         * @throws IOException
         */
        public void getFileFromHDFS(String remotePath, String localPath) throws IOException {
            //获取输入流
            FSDataInputStream fis = fs.open(new Path(remotePath));
            //获取输出流
            FileOutputStream fos = new FileOutputStream(new File(localPath));
            //流的拷贝
            IOUtils.copyBytes(fis, fos, fs.getConf());
            //关闭资源
            IOUtils.closeStream(fis);
            IOUtils.closeStream(fos);
            fs.close();
        }

        /**
         * 非递归删除文件
         * @param filePath
         * @return
         * @throws IllegalArgumentException
         * @throws IOException
         */
        public boolean deleteFile(String filePath) throws IllegalArgumentException, IOException {
            return deleteFile(filePath, false);
        }

        /**
         * 删除文件
         * @param filePath
         * @param recursive 是否递归删除
         * @return
         * @throws IllegalArgumentException
         * @throws IOException
         */
        private boolean deleteFile(String filePath, boolean recursive) throws IllegalArgumentException, IOException {
            return fs.delete(new Path(filePath), recursive);
        }

        /**
         * 创建文件夹
         * @param dirPath
         * @return
         * @throws IllegalArgumentException
         * @throws IOException
         */
        public boolean mkdir(String dirPath) throws IllegalArgumentException, IOException {
            return fs.mkdirs(new Path(dirPath));
        }

        /**
         * 读取文件内容
         * @param filePath
         * @return
         * @throws IOException
         */
        public String readFile(String filePath) throws IOException {
            String res = null;
            FSDataInputStream inputStream = null;
            ByteArrayOutputStream outputStream = null;
            try {
                inputStream = fs.open(new Path(filePath));
                outputStream = new ByteArrayOutputStream(inputStream.available());
                IOUtils.copyBytes(inputStream, outputStream, fs.getConf());
                res = outputStream.toString();
            }catch (Exception e) {
                throw e;
            }finally {
                if (inputStream != null) {
                    IOUtils.closeStream(inputStream);
                }
                if (outputStream != null) {
                    IOUtils.closeStream(outputStream);
                }
            }
            return res;
        }

        /**
         * 判断路径在HDFS上是否存在
         * @param path 路径
         * @return
         * @throws IOException
         */
        public boolean exits(String path) throws IOException {
            return fs.exists(new Path(path));
        }
    }

}