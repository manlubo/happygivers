package util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.sql.DataSource;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;


public class PropsLoaderUtil {
	public static Properties getProperties(String file) {
		Properties props = new Properties();
		// 현재 실행중인 스레드의 컨텍스트 클래스로더의 위치에서 resource를 stream형태로 가져오기
		try(InputStream is = Thread.currentThread()
							.getContextClassLoader()
							.getResourceAsStream(file)) {
			if(is == null) {
				throw new FileNotFoundException("Cannot find db.properties in classpath");
			}
			props.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return props;
	}
		
		
}