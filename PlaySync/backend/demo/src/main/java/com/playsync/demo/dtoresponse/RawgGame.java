package com.playsync.demo.dtoresponse;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RawgGame {

	@JsonProperty("id")
	private Long id;

	@JsonProperty("slug")
	private String slug;

	@JsonProperty("name")
	private String name;

	@JsonProperty("released")
	private String released;

	@JsonProperty("background_image")
	private String backgroundImage;

	@JsonProperty("rating")
	private Double rating;

	@JsonProperty("rating_top")
	private Integer ratingTop;

	@JsonProperty("ratings_count")
	private Integer ratingsCount;

	@JsonProperty("metacritic")
	private Integer metacritic;

	@JsonProperty("playtime")
	private Integer playtime;

	@JsonProperty("genres")
	private List<RawgGenre> genres;

	@JsonProperty("platforms")
	private List<RawgPlatformWrapper> platforms;

	@JsonProperty("short_screenshots")
	private List<RawgScreenshot> shortScreenshots;

	// Campos calculados/adaptados
	private String description;
	private String descriptionRaw;
	private Integer gameId;
	private String img;
	private Double precoInicial;
	private Double precoFinal;
}
