package com.example.demo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
@Entity // This tells Hibernate to make a table out of this class
@Table(name="interest")
public class Interest {
	@Id
	@Column
	private int id;

	@Column
	private String subtitle;

	@Column
	private String maintitle;

	@Column
	private String url;

	@Column
	private String description;

	public Interest() {
		super();
	}



	public Interest(int id, String subtitle, String maintitle, String url, String description) {
		super();
		this.id = id;
		this.subtitle = subtitle;
		this.maintitle = maintitle;
		this.url = url;
		this.description = description;
	}



	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public String getMaintitle() {
		return maintitle;
	}

	public void setMaintitle(String maintitle) {
		this.maintitle = maintitle;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "Interest{" +
				"id=" + id +
				", subtitle='" + subtitle + '\'' +
				", maintitle='" + maintitle + '\'' +
				", url='" + url + '\'' +
				", description='" + description + '\'' +
				'}';
	}
}