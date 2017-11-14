package com.ibg.datasource;

import org.javalite.activejdbc.Base;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

public class ActiveJdbcConClose implements ApplicationListener<ContextClosedEvent> {

	@Override
	public void onApplicationEvent(ContextClosedEvent event) {
		if (Base.hasConnection()) {
			Base.close();
		}

	}

}
