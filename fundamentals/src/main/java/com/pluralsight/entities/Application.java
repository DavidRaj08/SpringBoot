/*package com.pluralsight.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Application {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "app_id")
	private Integer id;

	@Column(name = "app_name", nullable = false)
	private String name;

	@Column(length = 2000)
	private String description;
	private String owner;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	@Override
	public String toString() {
		return "Application [id=" + id + ", name=" + name + ", description=" + description + ", owner=" + owner + "]";
	}

	public Application() {
	}

	public Application(String name, String description, String owner) {
		this.name = name;
		this.description = description;
		this.owner = owner;
	}
}
*/