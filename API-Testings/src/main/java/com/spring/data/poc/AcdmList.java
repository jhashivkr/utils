package com.spring.data.poc;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class AcdmList {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long list_id;
	
}
