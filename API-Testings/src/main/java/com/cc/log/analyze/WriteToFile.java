package com.cc.log.analyze;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;

import com.cc.log.obj.LogMqData;
import com.spring.jms.LogTiebacks;

public class WriteToFile {

	
	public static void writeFileBytesBuffered(String filename, Map<String, Map<String, String>> lines) {

		try (BufferedWriter writer = Files.newBufferedWriter(
				FileSystems.getDefault().getPath("D:\\ingram\\1023-logs\\mqdata\\", filename),
				Charset.forName("US-ASCII"), StandardOpenOption.CREATE)) {

			String content = "";
			for (String key : lines.keySet()) {
				content = key + " => " + lines.get(key) + '\n';
				writer.write(content, 0, content.length());
			}
			writer.close();

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public static void writeToFile(String filename, List<LogTiebacks> lines) {
		try (BufferedWriter writer = Files.newBufferedWriter(
				FileSystems.getDefault().getPath("D:\\ipage-workspace\\mq-logs\\", filename),
				Charset.forName("US-ASCII"), StandardOpenOption.CREATE)) {

			String content = "";
			for (LogTiebacks data : lines) {
				content = data.getTimeStamp() + '|' + data.getTieBack() + '|' + data.getTranType() + '\n';
				writer.write(content, 0, content.length());
			}
			writer.close();

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public static void writeToLogFile(String filename, List<LogMqData> lines) {
		try (BufferedWriter writer = Files.newBufferedWriter(
				FileSystems.getDefault().getPath("D:\\ipage-workspace\\mq-logs\\", filename),
				Charset.forName("US-ASCII"), StandardOpenOption.CREATE)) {

			String content = "";
			for (LogMqData data : lines) {
				content = data.toString() + '\n';
				writer.write(content, 0, content.length());
			}
			writer.close();

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public static void writeTiebacksToFile(String filename) {
		try (BufferedWriter writer = Files.newBufferedWriter(
				FileSystems.getDefault().getPath("D:\\ipage-workspace\\mq-logs\\", filename),
				Charset.forName("US-ASCII"), StandardOpenOption.CREATE)) {

			String content = "";
			for (String tieback : Constants.keyTiebacks.keySet()) {
				content = tieback + "," + Constants.keyTiebacks.get(tieback) + '\n';
				writer.write(content, 0, content.length());
			}
			writer.close();

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public static void writeLogDataTiebacksToFile(String filename,Map<Object, LogMqData> logData) {
		try (BufferedWriter writer = Files.newBufferedWriter(
				FileSystems.getDefault().getPath("D:\\ipage-workspace\\mq-logs\\", filename),
				Charset.forName("US-ASCII"), StandardOpenOption.CREATE)) {

			String content = "";
			for (Object key : logData.keySet()) {
				content = key + "," + logData.get(key).getTieBack() + '\n';
				writer.write(content, 0, content.length());
			}
			writer.close();

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
