package com.promptoven.profileservice.adaptor.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(indexes = {
	@Index(name = "idx_uuid", columnList = "memberUUID")
})
public class ProfileStatisticsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long Id;

	@Column(unique = true)
	private String memberUUID;

	private Long viewer;

	private Long sales;

	@Column(name = "profile_rank")
	private Long rank;

	private Double rating;

	public void setId(Long id) {
		this.Id = id;
	}

}
