package com.Util;

import java.io.*;
import java.util.List;

public class FileHelper {
    private FileReadLineCallback mFileReadLineCallback = null;

    public interface FileReadLineCallback {
        public void readSVNFilerCallback(String line);

        public void readSVNLogCallback(String line);
    }

    public enum ReadType {
        svnFilter,
        svnLog
    }

    public FileReadLineCallback getFileReadLineCallback() {
        return mFileReadLineCallback;
    }

    public void setFileReadLineCallback(FileReadLineCallback mFileReadLineCallback) {
        this.mFileReadLineCallback = mFileReadLineCallback;
    }

    /**
     * 随机读取文件内容
     *
     * @param fileName
     */
    public void readFileByRandomAccess(String fileName) {
        RandomAccessFile randomFile = null;
        try {
            Log.log("随机读取一段文件内容：");
            // 打开一个随机访问文件流，按只读方式
            randomFile = new RandomAccessFile(fileName, "r");
            // 文件长度，字节数
            long fileLength = randomFile.length();
            // 读文件的起始位置
            int beginIndex = (fileLength > 4) ? 0 : 0;
            // 将读文件的开始位置移到beginIndex位置。
            randomFile.seek(beginIndex);
            byte[] bytes = new byte[10];
            int byteread = 0;
            // 一次读10个字节，如果文件内容不足10个字节，则读剩下的字节。
            // 将一次读取的字节数赋给byteread
            while ((byteread = randomFile.read(bytes)) != -1) {
                System.out.write(bytes, 0, byteread);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (randomFile != null) {
                try {
                    randomFile.close();
                } catch (IOException e1) {
                }
            }
        }
    }

    /**
     * 以行为单位读取文件，常用于读面向行的格式化文件
     *
     * @param fileName
     * @param type
     */
    public void readFileByLines(String fileName, ReadType type) {
        File file = new File(fileName);
        if (file == null || !file.exists()) {
            Log.log("readFileByLines, 文件:" + fileName + ",不存在。");
            return;
        }
        BufferedReader reader = null;
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
                if (type == ReadType.svnFilter) {
                    if (mFileReadLineCallback != null) {
                        mFileReadLineCallback.readSVNFilerCallback(tempString);
                    }
                } else if (type == ReadType.svnLog) {
                    if (mFileReadLineCallback != null) {
                        mFileReadLineCallback.readSVNLogCallback(tempString);
                    }
                }
                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e1) {
                }
            }
        }
    }

    public String readFileByLines(File file) {
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                builder.append(tempString).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }

            return builder.toString().trim();
        }
    }

    public String readFileByLines(File file, List<String> keysContain, List<String> keysNotContain) {
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
//                Log.log("line " + line + ": " + tempString);
                line++;
                if (keysContain != null && keysContain.size() > 0) {
                    boolean isContainsKey = false;
                    boolean isNotContainsKey = false;
                    for (String keyItem : keysContain) {
                        if (tempString.contains(keyItem)) {
                            isContainsKey = true;
                            break;
                        }
                    }
                    if (keysNotContain != null && keysNotContain.size() > 0) {
                        for (String keyItem : keysNotContain) {
                            if (tempString.contains(keyItem)) {
                                isNotContainsKey = true;
                                break;
                            }
                        }
                    }

                    if (isContainsKey && !isNotContainsKey) {
                        if (tempString.contains("I/am_pss")) {
                            String[] buffStr = tempString
                                    .substring(tempString.indexOf("[") + 1, tempString.length() - 1)
                                    .split(",");

                            String Pid = buffStr[0];
                            String Uid = buffStr[1];
                            String pgName = buffStr[2];
                            String Pss = buffStr[3];
                            String Uss = buffStr[4];
                            String SwapPss = buffStr[5];
                            String buff = "Pid=" + Pid + " , "
                                    + "Uid=" + Uid + " , "
                                    + "pgName=" + pgName + " , "
                                    + "Pss=" + Pss + " , "
                                    + "Uss=" + Uss + " , "
                                    + "SwapPss=" + SwapPss + " , "
                                    + "Remain=" + (Long.parseLong(Pss) - Long.parseLong(Uss));
                            tempString += "\n\t\t\t" + buff;
                        }
                        builder.append(tempString).append("\n");
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }

            return builder.toString();
        }
    }

    /**
     * 以字符为单位读取文件，常用于读文本，数字等类型的文件
     * @param fileName
     */
    public void readFileByChars(String fileName) {
        File file = new File(fileName);
        Reader reader = null;
        try {
            Log.log("以字符为单位读取文件内容，一次读一个字节：");
            // 一次读一个字符
            reader = new InputStreamReader(new FileInputStream(file));
            int tempchar;
            while ((tempchar = reader.read()) != -1) {
                // 对于windows下，\r\n这两个字符在一起时，表示一个换行。
                // 但如果这两个字符分开显示时，会换两次行。
                // 因此，屏蔽掉\r，或者屏蔽\n。否则，将会多出很多空行。
                if (((char) tempchar) != '\r') {
                    System.out.print((char) tempchar);
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Log.log("\n以字符为单位读取文件内容，一次读多个字节：");
            // 一次读多个字符
            char[] tempchars = new char[30];
            int charread = 0;
            reader = new InputStreamReader(new FileInputStream(fileName));
            // 读入多个字符到字符数组中，charread为一次读取字符数
            while ((charread = reader.read(tempchars)) != -1) {
                // 同样屏蔽掉\r不显示
                if ((charread == tempchars.length)
                        && (tempchars[tempchars.length - 1] != '\r')) {
                    System.out.print(tempchars);
                } else {
                    for (int i = 0; i < charread; i++) {
                        if (tempchars[i] == '\r') {
                            continue;
                        } else {
                            System.out.print(tempchars[i]);
                        }
                    }
                }
            }

        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }

    /**
     * 以字节为单位读取文件，常用于读二进制文件，如图片、声音、影像等文件。
     * @param fileName
     */
    public void readFileByBytes(String fileName) {
        File file = new File(fileName);
        InputStream in = null;
        try {
            // 一次读一个字节
            in = new FileInputStream(file);
            int tempbyte;
            while ((tempbyte = in.read()) != -1) {
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        try {
            // 一次读多个字节
            byte[] tempbytes = new byte[100];
            int byteread = 0;
            in = new FileInputStream(fileName);
            while ((byteread = in.read(tempbytes)) != -1) {
                System.out.write(tempbytes, 0, byteread);//好方法，第一个参数是数组，第二个参数是开始位置，第三个参数是长度
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e1) {
                }
            }
        }
    }

    /**
     * 把文本写入到文件中
     * @param file
     * @param txt
     */
    public void writeToFile(File file, String txt) {
        try {
            if (!file.exists()) {
                File fileParent = file.getParentFile();
                if (!fileParent.exists()) {
                    fileParent.mkdirs();
                }
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file, true);
            BufferedWriter oWriter = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
            oWriter.write(txt);
            oWriter.flush();
            oWriter.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
